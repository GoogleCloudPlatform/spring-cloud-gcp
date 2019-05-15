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
import com.google.cloud.spanner.r2dbc.util.ObservableReactiveUtil;
import com.google.protobuf.Empty;
import com.google.spanner.v1.BeginTransactionRequest;
import com.google.spanner.v1.CommitRequest;
import com.google.spanner.v1.CommitResponse;
import com.google.spanner.v1.CreateSessionRequest;
import com.google.spanner.v1.DeleteSessionRequest;
import com.google.spanner.v1.ExecuteSqlRequest;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.RollbackRequest;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.SpannerGrpc;
import com.google.spanner.v1.SpannerGrpc.SpannerStub;
import com.google.spanner.v1.Transaction;
import com.google.spanner.v1.TransactionOptions;
import com.google.spanner.v1.TransactionOptions.ReadWrite;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * gRPC-based {@link Client} implementation.
 */
public class GrpcClient implements Client {

  public static final String HOST = "spanner.googleapis.com";
  public static final int PORT = 443;

  private final ManagedChannel channel;
  private final SpannerStub spanner;

  /**
   * Initializes the Cloud Spanner gRPC async stub.
   */
  public GrpcClient(GoogleCredentials credentials) {
    // Create blocking and async stubs using the channel
    CallCredentials callCredentials = MoreCallCredentials.from(credentials);

    // Create a channel
    this.channel = ManagedChannelBuilder
        .forAddress(HOST, PORT)
        .build();

    // Create the asynchronous stub for Cloud Spanner
    this.spanner = SpannerGrpc.newStub(this.channel)
        .withCallCredentials(callCredentials);
  }

  @Override
  public Mono<Transaction> beginTransaction(Session session) {
    return Mono.defer(() -> {
      BeginTransactionRequest beginTransactionRequest =
          BeginTransactionRequest.newBuilder()
              .setSession(session.getName())
              .setOptions(
                  TransactionOptions
                      .newBuilder()
                      .setReadWrite(ReadWrite.getDefaultInstance()))
              .build();

      return ObservableReactiveUtil.unaryCall(
          (obs) -> this.spanner.beginTransaction(beginTransactionRequest, obs));
    });
  }

  @Override
  public Mono<CommitResponse> commitTransaction(Session session, Transaction transaction) {
    return Mono.defer(() -> {
      CommitRequest commitRequest =
          CommitRequest.newBuilder()
              .setSession(session.getName())
              .setTransactionId(transaction.getId())
              .build();

      return ObservableReactiveUtil.unaryCall(
          (obs) -> this.spanner.commit(commitRequest, obs));
    });
  }

  @Override
  public Mono<Void> rollbackTransaction(Session session, Transaction transaction) {
    return Mono.defer(() -> {
      RollbackRequest rollbackRequest =
          RollbackRequest.newBuilder()
              .setSession(session.getName())
              .setTransactionId(transaction.getId())
              .build();

      return ObservableReactiveUtil.<Empty>unaryCall(
          (obs) -> this.spanner.rollback(rollbackRequest, obs))
          .then();
    });
  }

  @Override
  public Mono<Session> createSession(String databaseName) {
    return Mono.defer(() -> {
      CreateSessionRequest request = CreateSessionRequest.newBuilder()
          .setDatabase(databaseName)
          .build();

      return ObservableReactiveUtil.unaryCall((obs) -> this.spanner.createSession(request, obs));
    });
  }

  @Override
  public Mono<Void> deleteSession(Session session) {
    return Mono.defer(() -> {
      DeleteSessionRequest deleteSessionRequest =
          DeleteSessionRequest.newBuilder()
              .setName(session.getName())
              .build();

      return ObservableReactiveUtil.<Empty>unaryCall(
          (obs) -> this.spanner.deleteSession(deleteSessionRequest, obs))
          .then();
    });
  }

  @Override
  public Publisher<PartialResultSet> executeStreamingSql(ExecuteSqlRequest request) {
    return Flux.create(sink -> {
      ClientResponseObserver<ExecuteSqlRequest, PartialResultSet> clientResponseObserver =
          new ClientResponseObserver<ExecuteSqlRequest, PartialResultSet>() {
            @Override
            public void onNext(PartialResultSet value) {
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
            public void beforeStart(ClientCallStreamObserver<ExecuteSqlRequest> requestStream) {
              requestStream.disableAutoInboundFlowControl();
              sink.onRequest(demand -> requestStream.request((int) demand));
              sink.onCancel(() -> requestStream.cancel(null, null));
            }
          };
      this.spanner.executeStreamingSql(request, clientResponseObserver);
    });
  }

  @Override
  public Mono<Void> close() {
    return Mono.fromRunnable(() -> this.channel.shutdownNow());
  }
}
