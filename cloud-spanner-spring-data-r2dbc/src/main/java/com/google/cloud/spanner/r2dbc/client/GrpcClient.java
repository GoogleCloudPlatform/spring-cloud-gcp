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

package com.google.cloud.spanner.r2dbc.client;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.spanner.v1.ExecuteSqlRequest;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.SpannerGrpc;
import com.google.spanner.v1.SpannerGrpc.SpannerStub;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import java.io.IOException;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;

/**
 * gRPC-based {@link Client} implementation.
 */
public class GrpcClient implements Client {

  public static final int PORT = 443;

  private final SpannerStub spanner;

  /**
   * Initializes the Cloud Spanner gRPC async stub.
   */
  public GrpcClient() throws IOException {
    // Create a channel
    ManagedChannel channel = ManagedChannelBuilder
        .forAddress("spanner.googleapis.com", PORT)
        .build();

    // Create blocking and async stubs using the channel
    CallCredentials callCredentials = MoreCallCredentials
        .from(GoogleCredentials.getApplicationDefault());

    // Create the asynchronous stub for Cloud Spanner
    this.spanner = SpannerGrpc.newStub(channel)
        .withCallCredentials(callCredentials);
  }

  @Override
  public Mono<Void> close() {
    return null;
  }

  @Override
  public Publisher<PartialResultSet> executeStreamingSql(ExecuteSqlRequest request) {
    return subscriber -> spanner.executeStreamingSql(request,
        new ClientResponseObserver<ExecuteSqlRequest, PartialResultSet>() {
          @Override
          public void beforeStart(
              ClientCallStreamObserver<ExecuteSqlRequest> clientCallStreamObserver) {

            clientCallStreamObserver.disableAutoInboundFlowControl();

            subscriber.onSubscribe(new Subscription() {
              @Override
              public void request(long l) {
                clientCallStreamObserver.request((int) l);
              }

              @Override
              public void cancel() {
                clientCallStreamObserver.cancel(null, null);
              }
            });
          }

          @Override
          public void onNext(PartialResultSet partialResultSet) {
            subscriber.onNext(partialResultSet);
          }

          @Override
          public void onError(Throwable throwable) {
            subscriber.onError(throwable);
          }

          @Override
          public void onCompleted() {
            subscriber.onComplete();
          }
        });

  }
}
