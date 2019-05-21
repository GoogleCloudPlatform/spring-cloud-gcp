/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spanner.r2dbc.util;

import static com.google.cloud.spanner.r2dbc.util.SpannerExceptionUtil.isRetryable;

import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import io.grpc.stub.StreamObserver;
import java.time.Duration;
import java.util.function.Consumer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.retry.Retry;

/**
 * Converter from a gRPC async calls to Reactor primitives ({@link Mono}).
 */
public class ObservableReactiveUtil {

  // Retry settings inspired from:
  // https://github.com/googleapis/googleapis/blob/master/google/spanner/v1/spanner_gapic.yaml#L48
  private static final Retry retryStrategy =
      Retry.onlyIf(retryContext -> isRetryable(retryContext.exception()))
          .exponentialBackoffWithJitter(Duration.ofSeconds(1), Duration.ofSeconds(32))
          .timeout(Duration.ofSeconds(60));

  /**
   * Invokes a lambda that in turn issues a remote call, directing the response to a {@link Mono}
   * stream.
   * @param remoteCall lambda capable of invoking the correct remote call, making use of the
   * {@link Mono}-converting {@link StreamObserver} implementation.
   * @param <ResponseT> type of remote call response
   * @return
   */
  public static <ResponseT> Mono<ResponseT> unaryCall(
      Consumer<StreamObserver<ResponseT>> remoteCall) {
    return Mono.create(sink -> remoteCall.accept(new UnaryStreamObserver(sink)))
        .retryWhen(retryStrategy);
  }

  /**
   * This will go away in favor of Mike's implementation.
   * @param remoteCall call to make
   * @param <RequestT> request type
   * @param <ResponseT> response type
   * @return
   */
  public static <RequestT, ResponseT> Flux<ResponseT> streamingCall(
      Consumer<StreamObserver<ResponseT>> remoteCall) {

    return Flux.create(sink -> {
      StreamingObserver observer = new StreamingObserver(sink);
      remoteCall.accept(observer);
      sink.onRequest(demand -> observer.request(demand));
    });
  }

  static class StreamingObserver<RequestT, ResponseT>
      implements ClientResponseObserver<RequestT, ResponseT>  {
    ClientCallStreamObserver<RequestT> rsObserver;
    FluxSink<ResponseT> sink;

    public StreamingObserver(FluxSink<ResponseT> sink) {
      this.sink = sink;
    }

    @Override
    public void onNext(ResponseT value) {
      sink.next(value);
    }

    @Override
    public void onError(Throwable t) {
      sink.error(t);
    }

    @Override
    public void onCompleted() {
      sink.complete();
    }

    @Override
    public void beforeStart(ClientCallStreamObserver<RequestT> requestStream) {
      this.rsObserver = requestStream;
      requestStream.disableAutoInboundFlowControl();
      sink.onCancel(() -> requestStream.cancel(null, null));
    }

    public void request(long n) {
      this.rsObserver.request(n > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)n);
    }
  }

  /**
   * Forwards the result of a unary gRPC call to a {@link MonoSink}.
   *
   * <p>Unary gRPC calls expect a single response or an error, so completion of the call without an
   * emitted value is an error condition.
   *
   * @param <ResponseT> type of expected gRPC call response value.
   */
  private static class UnaryStreamObserver<ResponseT> implements StreamObserver<ResponseT> {

    private boolean terminalEventReceived;

    private final MonoSink sink;

    public UnaryStreamObserver(MonoSink sink) {
      this.sink = sink;
    }

    @Override
    public void onNext(ResponseT response) {
      this.terminalEventReceived = true;
      this.sink.success(response);
    }

    @Override
    public void onError(Throwable throwable) {
      this.terminalEventReceived = true;
      this.sink.error(throwable);
    }

    @Override
    public void onCompleted() {
      if (!terminalEventReceived) {
        this.sink.error(
            new RuntimeException("Unary gRPC call completed without yielding a value or an error"));
      }
    }
  }
}
