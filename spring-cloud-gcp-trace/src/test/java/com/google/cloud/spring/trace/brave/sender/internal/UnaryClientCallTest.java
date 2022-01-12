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
package com.google.cloud.spring.trace.brave.sender.internal;

import static com.google.cloud.spring.trace.brave.sender.internal.UnaryClientCall.DEFAULT_SERVER_TIMEOUT_MS;
import static io.grpc.CallOptions.DEFAULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.devtools.cloudtrace.v2.BatchWriteSpansRequest;
import com.google.devtools.cloudtrace.v2.TraceServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import zipkin2.Callback;

public class UnaryClientCallTest {
  @Rule public final GrpcServerRule server = new GrpcServerRule().directExecutor();
  final TestTraceService traceService = spy(new TestTraceService());

  static class BatchWriteSpansCall extends UnaryClientCall<BatchWriteSpansRequest, Empty> {
    final Channel channel;

    BatchWriteSpansCall(
        Channel channel, BatchWriteSpansRequest request, long serverResponseTimeout) {
      super(
          channel,
          TraceServiceGrpc.getBatchWriteSpansMethod(),
          DEFAULT,
          request,
          serverResponseTimeout);
      this.channel = channel;
    }

    @Override
    public BatchWriteSpansCall clone() {
      return new BatchWriteSpansCall(channel, request(), DEFAULT_SERVER_TIMEOUT_MS);
    }
  }

  BatchWriteSpansCall call;

  @Before
  public void setUp() {
    server.getServiceRegistry().addService(traceService);
    call =
        new BatchWriteSpansCall(
            server.getChannel(),
            BatchWriteSpansRequest.newBuilder().build(),
            DEFAULT_SERVER_TIMEOUT_MS);
  }

  @Test
  public void execute_success() throws Throwable {
    onClientCall(
        observer -> {
          observer.onNext(Empty.getDefaultInstance());
          observer.onCompleted();
        });

    call.execute();

    verifyPatchRequestSent();
  }

  @Test
  public void enqueue_success() throws Throwable {
    onClientCall(
        observer -> {
          observer.onNext(Empty.getDefaultInstance());
          observer.onCompleted();
        });

    awaitCallbackResult();

    verifyPatchRequestSent();
  }

  void verifyPatchRequestSent() {
    ArgumentCaptor<BatchWriteSpansRequest> requestCaptor =
        ArgumentCaptor.forClass(BatchWriteSpansRequest.class);

    verify(traceService).batchWriteSpans(requestCaptor.capture(), any());

    BatchWriteSpansRequest request = requestCaptor.getValue();
    assertThat(request).isEqualTo(BatchWriteSpansRequest.getDefaultInstance());
  }

  @Test(expected = StatusRuntimeException.class)
  public void accept_execute_serverError() throws Throwable {
    onClientCall(observer -> observer.onError(new IllegalStateException()));

    call.execute();
  }

  @Test(expected = StatusRuntimeException.class)
  public void accept_enqueue_serverError() throws Throwable {
    onClientCall(observer -> observer.onError(new IllegalStateException()));

    awaitCallbackResult();
  }

  @Test(expected = IllegalStateException.class)
  public void execute_timeout() throws Throwable {
    long overriddenTimeout = 50;
    call =
        new BatchWriteSpansCall(
            server.getChannel(), BatchWriteSpansRequest.newBuilder().build(), overriddenTimeout);
    onClientCall(
        observer ->
            Executors.newSingleThreadExecutor()
                .submit(
                    () -> {
                      try {
                        Thread.sleep(overriddenTimeout + 10);
                      } catch (InterruptedException e) {
                      }
                      observer.onCompleted();
                    }));

    call.execute();
  }

  static class TestTraceService extends TraceServiceGrpc.TraceServiceImplBase {}

  void awaitCallbackResult() throws Throwable {
    AtomicReference<Throwable> ref = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);
    call.enqueue(
        new Callback<Empty>() {
          @Override
          public void onSuccess(Empty empty) {
            latch.countDown();
          }

          @Override
          public void onError(Throwable throwable) {
            ref.set(throwable);
            latch.countDown();
          }
        });
    latch.await(10, TimeUnit.MILLISECONDS);
    if (ref.get() != null) throw ref.get();
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
}
