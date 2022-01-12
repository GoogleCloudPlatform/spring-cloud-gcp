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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.cloud.spring.trace.brave.translation.SpanTranslator;
import com.google.common.collect.ImmutableList;
import com.google.devtools.cloudtrace.v2.BatchWriteSpansRequest;
import com.google.devtools.cloudtrace.v2.TraceServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import zipkin2.CheckResult;
import zipkin2.Endpoint;
import zipkin2.Span;

public class StackdriverSenderTest {

  public static final Endpoint FRONTEND =
      Endpoint.newBuilder().serviceName("frontend").ip("127.0.0.1").build();

  @Rule public final GrpcServerRule server = new GrpcServerRule().directExecutor();
  TestTraceService traceService = spy(new TestTraceService());
  String projectId = "test-project";
  StackdriverSender sender;

  Span span = Span.newBuilder().traceId("1").id("a").name("get").localEndpoint(FRONTEND).build();

  @Before
  public void setUp() {
    server.getServiceRegistry().addService(traceService);
    sender = StackdriverSender.newBuilder(server.getChannel()).projectId(projectId).build();
  }

  @Test
  public void verifyRequestSent_single() throws IOException {
    byte[] oneTrace = StackdriverEncoder.V2.encode(span);
    List<byte[]> encodedSpans = ImmutableList.of(oneTrace);

    onClientCall(
        observer -> {
          observer.onNext(Empty.getDefaultInstance());
          observer.onCompleted();
        });

    sender.sendSpans(encodedSpans).execute();

    // verify our estimate is correct
    int actualSize = takeRequest().getSerializedSize();
    assertThat(sender.messageSizeInBytes(oneTrace.length)).isEqualTo(actualSize);
  }

  @Test
  public void verifyRequestSent_multipleTraces() throws IOException {
    // intentionally change only the boundaries to help break any offset-based logic
    List<Span> spans =
        ImmutableList.of(
            span.toBuilder().traceId("10000000000000000000000000000002").build(),
            span.toBuilder().traceId("10000000000000000000000000000001").build(),
            span.toBuilder().traceId("20000000000000000000000000000001").build(),
            span.toBuilder().traceId("20000000000000000000000000000002").build());

    verifyRequestSent(spans);
  }

  @Test
  public void verifyRequestSent_multipleSpans() throws IOException {
    // intentionally change only the boundaries to help break any offset-based logic
    List<Span> spans =
        ImmutableList.of(
            span.toBuilder().traceId("10000000000000000000000000000002").build(),
            span.toBuilder().traceId("10000000000000000000000000000001").build(),
            // intentionally out-of-order
            span.toBuilder().traceId("10000000000000000000000000000002").id("b").build(),
            span.toBuilder().traceId("10000000000000000000000000000001").id("c").build());

    verifyRequestSent(spans);
  }

  void verifyRequestSent(List<Span> spans) throws IOException {
    onClientCall(
        observer -> {
          observer.onNext(Empty.getDefaultInstance());
          observer.onCompleted();
        });

    List<byte[]> encodedSpans =
        spans.stream().map(StackdriverEncoder.V2::encode).collect(Collectors.toList());

    sender.sendSpans(encodedSpans).execute();

    BatchWriteSpansRequest request = takeRequest();

    List<com.google.devtools.cloudtrace.v2.Span> translated =
        SpanTranslator.translate(projectId, spans);

    // sanity check the data
    assertThat(request.getSpansList()).containsExactlyElementsOf(translated);

    // verify our estimate is correct
    int actualSize = request.getSerializedSize();
    assertThat(sender.messageSizeInBytes(encodedSpans)).isEqualTo(actualSize);
  }

  @Test
  public void verifyCheckReturnsFailureWhenServiceFailsWithKnownGrpcFailure() {
    onClientCall(observer -> {
      observer.onError(new StatusRuntimeException(Status.RESOURCE_EXHAUSTED));
    });
    CheckResult result = sender.check();
    assertThat(result.ok()).isFalse();
    assertThat(result.error())
        .isInstanceOf(StatusRuntimeException.class)
        .hasMessageContaining("RESOURCE_EXHAUSTED");
  }

  @Test
  public void verifyCheckReturnsFailureWhenServiceFailsForUnknownReason() {
    onClientCall(observer -> {
      observer.onError(new RuntimeException("oh no"));
    });
    CheckResult result = sender.check();
    assertThat(result.ok()).isFalse();
    assertThat(result.error())
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("UNKNOWN");
  }

  @Test
  public void verifyCheckReturnsOkWhenExpectedValidationFailure() {
    onClientCall(observer -> {
      observer.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT));
    });
    assertThat(sender.check()).isSameAs(CheckResult.OK);
  }

  @Test
  public void verifyCheckReturnsOkWhenServiceSucceeds() {
    onClientCall(observer -> {
      observer.onNext(Empty.getDefaultInstance());
      observer.onCompleted();
    });
    assertThat(sender.check()).isSameAs(CheckResult.OK);
  }

  void onClientCall(Consumer<StreamObserver<Empty>> onClientCall) {
    doAnswer(
            (Answer<Void>)
                invocationOnMock -> {
                  StreamObserver<Empty> observer =
                      ((StreamObserver) invocationOnMock.getArguments()[1]);
                  onClientCall.accept(observer);
                  return null;
                })
        .when(traceService)
        .batchWriteSpans(any(BatchWriteSpansRequest.class), any(StreamObserver.class));
  }

  BatchWriteSpansRequest takeRequest() {
    ArgumentCaptor<BatchWriteSpansRequest> requestCaptor =
        ArgumentCaptor.forClass(BatchWriteSpansRequest.class);

    verify(traceService).batchWriteSpans(requestCaptor.capture(), any());

    return requestCaptor.getValue();
  }

  static class TestTraceService extends TraceServiceGrpc.TraceServiceImplBase {}
}
