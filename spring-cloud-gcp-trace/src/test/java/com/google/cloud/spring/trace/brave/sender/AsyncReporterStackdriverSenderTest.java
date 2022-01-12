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

import static com.google.cloud.spring.trace.brave.sender.TestConstants.CLIENT_SPAN;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.cloud.spring.trace.brave.translation.SpanTranslator;
import com.google.devtools.cloudtrace.v2.BatchWriteSpansRequest;
import com.google.devtools.cloudtrace.v2.TraceServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;

/** Same as StackdriverSpanConsumerTest: tests everything wired together */
public class AsyncReporterStackdriverSenderTest {
  @Rule public final GrpcServerRule server = new GrpcServerRule().directExecutor();
  TestTraceService traceService = spy(new TestTraceService());
  String projectId = "test-project";
  AsyncReporter<Span> reporter;

  @Before
  public void setUp() {
    server.getServiceRegistry().addService(traceService);
    reporter =
        AsyncReporter.builder(
                StackdriverSender.newBuilder(server.getChannel()).projectId(projectId).build())
            .messageTimeout(0, TimeUnit.MILLISECONDS) // don't spawn a thread
            .build(StackdriverEncoder.V2);
  }

  @Test
  public void sendSpans_empty() {
    reporter.flush();

    verify(traceService, never()).batchWriteSpans(any(), any());
  }

  @Test
  public void sendSpans() {
    onClientCall(
        observer -> {
          observer.onNext(Empty.getDefaultInstance());
          observer.onCompleted();
        });

    reporter.report(CLIENT_SPAN);
    reporter.flush();

    ArgumentCaptor<BatchWriteSpansRequest> requestCaptor =
        ArgumentCaptor.forClass(BatchWriteSpansRequest.class);

    verify(traceService).batchWriteSpans(requestCaptor.capture(), any());

    BatchWriteSpansRequest request = requestCaptor.getValue();
    assertThat(request.getName()).isEqualTo("projects/" + projectId);

    assertThat(request.getSpansList()).containsExactlyElementsOf(
        SpanTranslator.translate(projectId, asList(CLIENT_SPAN)));
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

  static class TestTraceService extends TraceServiceGrpc.TraceServiceImplBase {}
}
