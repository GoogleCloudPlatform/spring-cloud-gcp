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

import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import io.grpc.stub.StreamObserver;
import java.util.function.Consumer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

/**
 * Converter from a gRPC async calls to Reactor primitives ({@link Mono}).
 */
public class ObservableReactiveUtil {

  /**
   * Invokes a lambda that in turn issues a remote call, directing the response to a {@link Mono}
   * stream.
   * @param remoteCall lambda capable of invoking the correct remote call, making use of the
   * {@link Mono}-converting {@link StreamObserver} implementation.
   * @param <ResponseT> type of remote call response
   * @return {@link Mono} containing the response of the unary call.
   */
  public static <ResponseT> Mono<ResponseT> unaryCall(
      Consumer<StreamObserver<ResponseT>> remoteCall) {
    return Mono.create(sink -> remoteCall.accept(new UnaryStreamObserver(sink)));
  }

  /**
   * Invokes a lambda that issues a streaming call and directs the response to a {@link Flux}
   * stream.
   *
   * @param remoteCall call to make
   * @param <RequestT> request type
   * @param <ResponseT> response type
   * @return {@link Flux} of response objects resulting from the streaming call.
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
      this.sink.next(value);
    }

    @Override
    public void onError(Throwable throwable) {
      this.sink.error(SpannerExceptionUtil.createR2dbcException(throwable));
    }

    @Override
    public void onCompleted() {
      this.sink.complete();
    }

    @Override
    public void beforeStart(ClientCallStreamObserver<RequestT> requestStream) {
      this.rsObserver = requestStream;
      requestStream.disableAutoInboundFlowControl();
      this.sink.onCancel(() -> requestStream.cancel("Flux requested cancel.", null));
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
      this.sink.error(SpannerExceptionUtil.createR2dbcException(throwable));
    }

    @Override
    public void onCompleted() {
      if (!this.terminalEventReceived) {
        this.sink.error(
            new RuntimeException("Unary gRPC call completed without yielding a value or an error"));
      }
    }
  }
}
