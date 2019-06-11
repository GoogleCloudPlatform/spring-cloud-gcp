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
import com.google.cloud.spanner.r2dbc.SpannerTransactionContext;
import com.google.cloud.spanner.r2dbc.util.ObservableReactiveUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Empty;
import com.google.protobuf.Struct;
import com.google.spanner.v1.BeginTransactionRequest;
import com.google.spanner.v1.CommitRequest;
import com.google.spanner.v1.CommitResponse;
import com.google.spanner.v1.CreateSessionRequest;
import com.google.spanner.v1.DeleteSessionRequest;
import com.google.spanner.v1.ExecuteBatchDmlRequest;
import com.google.spanner.v1.ExecuteBatchDmlResponse;
import com.google.spanner.v1.ExecuteSqlRequest;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.RollbackRequest;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.SpannerGrpc;
import com.google.spanner.v1.SpannerGrpc.SpannerStub;
import com.google.spanner.v1.Transaction;
import com.google.spanner.v1.TransactionOptions;
import com.google.spanner.v1.TransactionOptions.ReadWrite;
import com.google.spanner.v1.TransactionSelector;
import com.google.spanner.v1.Type;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.MoreCallCredentials;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * gRPC-based {@link Client} implementation.
 */
public class GrpcClient implements Client {

  public static final String HOST = "spanner.googleapis.com";

  public static final String PACKAGE_VERSION = GrpcClient.class.getPackage()
      .getImplementationVersion();

  public static final String USER_AGENT_LIBRARY_NAME = "cloud-spanner-r2dbc";

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
        .userAgent(USER_AGENT_LIBRARY_NAME + "/" + PACKAGE_VERSION)
        .build();

    // Create the asynchronous stub for Cloud Spanner
    this.spanner = SpannerGrpc.newStub(this.channel)
        .withCallCredentials(callCredentials);
  }

  @VisibleForTesting
  GrpcClient(SpannerStub spanner) {
    this.spanner = spanner;
    this.channel = null;
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
          (observer) -> this.spanner.deleteSession(deleteSessionRequest, observer))
          .then();
    });
  }

  @Override
  public Mono<ExecuteBatchDmlResponse> executeBatchDml(Session session,
      @Nullable SpannerTransactionContext transactionContext, String sql,
      List<Struct> params, Map<String, Type> types) {

    ExecuteBatchDmlRequest.Builder request = ExecuteBatchDmlRequest.newBuilder()
        .setSession(session.getName());
    if (transactionContext != null && transactionContext.getTransaction() != null) {
      request.setTransaction(
          TransactionSelector.newBuilder().setId(transactionContext.getTransaction().getId())
              .build())
          .setSeqno(transactionContext.nextSeqNum());

    }
    for (Struct paramsStruct : params) {
      ExecuteBatchDmlRequest.Statement statement = ExecuteBatchDmlRequest.Statement.newBuilder()
          .setSql(sql).setParams(paramsStruct).putAllParamTypes(types)
          .build();
      request.addStatements(statement);
    }

    return ObservableReactiveUtil
        .unaryCall(obs -> this.spanner.executeBatchDml(request.build(), obs));
  }

  @Override
  public Flux<PartialResultSet> executeStreamingSql(
      Session session, @Nullable SpannerTransactionContext transactionContext, String sql,
      Struct params, Map<String, Type> types) {

    return Flux.defer(() -> {
      ExecuteSqlRequest.Builder executeSqlRequest =
          ExecuteSqlRequest.newBuilder()
              .setSql(sql)
              .setSession(session.getName());
      if (params != null) {
        executeSqlRequest
            .setParams(params)
            .putAllParamTypes(types);
      }

      if (transactionContext != null && transactionContext.getTransaction() != null) {
        executeSqlRequest.setTransaction(
            TransactionSelector.newBuilder().setId(transactionContext.getTransaction().getId())
                .build());
        executeSqlRequest.setSeqno(transactionContext.nextSeqNum());
      }

      return ObservableReactiveUtil.streamingCall(
          obs -> this.spanner.executeStreamingSql(executeSqlRequest.build(), obs));
    });
  }

  @Override
  public Mono<Void> close() {
    return Mono.fromRunnable(() -> {
      if (this.channel != null) {
        this.channel.shutdownNow();
      }
    });
  }

  @VisibleForTesting
  public SpannerStub getSpanner() {
    return this.spanner;
  }
}
