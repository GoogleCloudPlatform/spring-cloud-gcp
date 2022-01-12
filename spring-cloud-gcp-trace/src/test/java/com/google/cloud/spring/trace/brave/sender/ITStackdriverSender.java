/*
 * Copyright 2016-2020 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.spring.trace.brave.sender;

import static com.google.cloud.spring.trace.brave.sender.TestConstants.BACKEND;
import static com.google.cloud.spring.trace.brave.sender.TestConstants.FRONTEND;
import static com.google.cloud.spring.trace.brave.sender.TestConstants.TODAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.Assumptions.assumeThatCode;
import static org.awaitility.Awaitility.await;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.devtools.cloudtrace.v1.GetTraceRequest;
import com.google.devtools.cloudtrace.v1.Trace;
import com.google.devtools.cloudtrace.v1.TraceServiceGrpc;
import io.grpc.CallOptions;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.auth.MoreCallCredentials;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zipkin2.CheckResult;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;

/** Integration test against Stackdriver Trace on a real GCP project */
public class ITStackdriverSender {

  GoogleCredentials credentials;
  StackdriverSender sender;
  StackdriverSender senderNoPermission;
  AsyncReporter<Span> reporter;
  AsyncReporter<Span> reporterNoPermission;
  TraceServiceGrpc.TraceServiceBlockingStub traceServiceGrpcV1;

  @BeforeEach
  public void setUp() throws IOException {
    // Application Default credential is configured using the GOOGLE_APPLICATION_CREDENTIALS env var
    // See: https://cloud.google.com/docs/authentication/production#providing_credentials_to_your_application

    String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
    assumeThat(credentialsPath).isNotBlank();
    assumeThat(new File(credentialsPath)).exists();
    assumeThatCode(GoogleCredentials::getApplicationDefault).doesNotThrowAnyException();

    credentials = GoogleCredentials.getApplicationDefault()
            .createScoped(Collections.singletonList("https://www.googleapis.com/auth/trace.append"));

    // Setup the sender to authenticate the Google Stackdriver service
    sender = StackdriverSender.newBuilder()
            .callOptions(CallOptions.DEFAULT.withCallCredentials(MoreCallCredentials.from(credentials)))
            .build();

    reporter =
        AsyncReporter.builder(sender)
            .messageTimeout(0, TimeUnit.MILLISECONDS) // don't spawn a thread
            .build(StackdriverEncoder.V2);

    traceServiceGrpcV1 = TraceServiceGrpc.newBlockingStub(sender.channel)
            .withCallCredentials(MoreCallCredentials.from(credentials.createScoped("https://www.googleapis.com/auth/cloud-platform")));

    senderNoPermission = StackdriverSender.newBuilder()
            .build();

    reporterNoPermission =
            AsyncReporter.builder(senderNoPermission)
                    .messageTimeout(0, TimeUnit.MILLISECONDS)
                    .build(StackdriverEncoder.V2);
  }

  @AfterEach
  public void tearDown() {
    if (reporter != null) {
      reporter.close();
    }
    if (reporterNoPermission != null) {
      reporterNoPermission.close();
    }
  }

  @Test
  public void healthcheck() {
    assertThat(reporter.check().ok()).isTrue();
  }

  @Test
  public void sendSpans() {
    Random random = new Random();
    Span span = Span.newBuilder()
            .traceId(random.nextLong(), random.nextLong())
            .parentId("1")
            .id("2")
            .name("get")
            .kind(Span.Kind.CLIENT)
            .localEndpoint(FRONTEND)
            .remoteEndpoint(BACKEND)
            .timestamp((TODAY + 50L) * 1000L)
            .duration(200000L)
            .addAnnotation((TODAY + 100L) * 1000L, "foo")
            .putTag("http.path", "/api")
            .putTag("clnt/finagle.version", "6.45.0")
            .build();

    reporter.report(span);
    reporter.flush();

    Trace trace = await()
            .atLeast(1, TimeUnit.SECONDS)
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(1, TimeUnit.SECONDS)
            .ignoreExceptionsMatching(e ->
                    e instanceof StatusRuntimeException &&
                            ((StatusRuntimeException) e).getStatus().getCode() == Status.Code.NOT_FOUND
            )
            .until(() -> traceServiceGrpcV1.getTrace(GetTraceRequest.newBuilder()
                    .setTraceId(span.traceId())
                    .build()), t -> t.getSpansCount() == 1);

    assertThat(trace.getSpans(0).getSpanId()).isEqualTo(2);
    assertThat(trace.getSpans(0).getParentSpanId()).isEqualTo(1);
  }

  @Test
  public void sendSpanEmptyName() {
    Random random = new Random();
    Span span = Span.newBuilder()
            .traceId(random.nextLong(), random.nextLong())
            .parentId("1")
            .id("2")
            .kind(Span.Kind.CLIENT)
            .localEndpoint(FRONTEND)
            .remoteEndpoint(BACKEND)
            .timestamp((TODAY + 50L) * 1000L)
            .duration(200000L)
            .addAnnotation((TODAY + 100L) * 1000L, "foo")
            .putTag("http.path", "/api")
            .putTag("clnt/finagle.version", "6.45.0")
            .build();

    reporter.report(span);
    reporter.flush();

    Trace trace = await()
            .atLeast(1, TimeUnit.SECONDS)
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(1, TimeUnit.SECONDS)
            .ignoreExceptionsMatching(e ->
                    e instanceof StatusRuntimeException &&
                            ((StatusRuntimeException) e).getStatus().getCode() == Status.Code.NOT_FOUND
            )
            .until(() -> traceServiceGrpcV1.getTrace(GetTraceRequest.newBuilder()
                    .setTraceId(span.traceId())
                    .build()), t -> t.getSpansCount() == 1);

    // In Stackdriver Trace v2 API, Zipkin Span "name" is sent as Stackdriver Span "displayName"
    // However, in Stackdriver Trace v1 API, to read this back, it's Stackdriver TraceSpan's "name"
    assertThat(trace.getSpans(0).getName()).isEqualTo("unknown");
    assertThat(trace.getSpans(0).getSpanId()).isEqualTo(2);
    assertThat(trace.getSpans(0).getParentSpanId()).isEqualTo(1);
  }

  @Test
  public void healthcheckFailNoPermission() {
    CheckResult result = reporterNoPermission.check();
    assertThat(result.ok()).isFalse();
    assertThat(result.error()).isNotNull();
    assertThat(result.error()).isInstanceOfSatisfying(StatusRuntimeException.class,
            sre -> assertThat(sre.getStatus().getCode()).isEqualTo(Status.Code.PERMISSION_DENIED));
  }

}
