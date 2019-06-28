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
import com.google.cloud.spanner.r2dbc.StatementExecutionContext;
import com.google.cloud.spanner.r2dbc.util.Assert;
import com.google.cloud.spanner.r2dbc.util.ObservableReactiveUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.longrunning.GetOperationRequest;
import com.google.longrunning.Operation;
import com.google.longrunning.OperationsGrpc;
import com.google.longrunning.OperationsGrpc.OperationsStub;
import com.google.protobuf.Empty;
import com.google.protobuf.Struct;
import com.google.spanner.admin.database.v1.DatabaseAdminGrpc;
import com.google.spanner.admin.database.v1.DatabaseAdminGrpc.DatabaseAdminStub;
import com.google.spanner.admin.database.v1.UpdateDatabaseDdlRequest;
import com.google.spanner.v1.BeginTransactionRequest;
import com.google.spanner.v1.CommitRequest;
import com.google.spanner.v1.CommitResponse;
import com.google.spanner.v1.CreateSessionRequest;
import com.google.spanner.v1.DeleteSessionRequest;
import com.google.spanner.v1.ExecuteBatchDmlRequest;
import com.google.spanner.v1.ExecuteBatchDmlRequest.Statement;
import com.google.spanner.v1.ExecuteBatchDmlResponse;
import com.google.spanner.v1.ExecuteSqlRequest;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.ResultSet;
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
import io.grpc.Status;
import io.grpc.auth.MoreCallCredentials;
import io.r2dbc.spi.R2dbcNonTransientResourceException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * gRPC-based {@link Client} implementation.
 */
public class GrpcClient implements Client {

  private static final TransactionOptions READ_WRITE_TRANSACTION =
      TransactionOptions.newBuilder()
          .setReadWrite(ReadWrite.getDefaultInstance())
          .build();

  private static final String HOST = "spanner.googleapis.com";

  private static final String PACKAGE_VERSION =
      GrpcClient.class.getPackage().getImplementationVersion();

  private static final String USER_AGENT_LIBRARY_NAME = "cloud-spanner-r2dbc";

  private static final int PORT = 443;

  private final ManagedChannel channel;
  private final SpannerStub spanner;
  private final DatabaseAdminStub databaseAdmin;
  private final OperationsStub operations;

  /**
   * Initializes the Cloud Spanner gRPC async stub.
   *
   * @param credentials the Google Cloud Platform credentials used to authenticate with Spanner.
   */
  public GrpcClient(GoogleCredentials credentials) {
    // Create blocking and async stubs using the channel
    CallCredentials callCredentials = MoreCallCredentials.from(credentials);

    // Create a channel
    this.channel = ManagedChannelBuilder
        .forAddress(HOST, PORT)
        .userAgent(USER_AGENT_LIBRARY_NAME + "/" + PACKAGE_VERSION)
        .build();

    // Async stub for general Spanner SQL queries
    this.spanner = SpannerGrpc.newStub(this.channel)
        .withCallCredentials(callCredentials);

    // Async stub for DDL queries
    this.databaseAdmin = DatabaseAdminGrpc.newStub(this.channel)
        .withCallCredentials(callCredentials);

    this.operations = OperationsGrpc.newStub(this.channel).withCallCredentials(callCredentials);
  }

  @VisibleForTesting
  GrpcClient(SpannerStub spanner, DatabaseAdminStub databaseAdmin, OperationsStub operations) {
    this.spanner = spanner;
    this.databaseAdmin = databaseAdmin;
    this.operations = operations;
    this.channel = null;
  }

  @Override
  public Mono<Transaction> beginTransaction(
      String sessionName, TransactionOptions transactionOptions) {

    return Mono.defer(() -> {
      Assert.requireNonNull(sessionName, "Session name must not be null");
      BeginTransactionRequest beginTransactionRequest =
          BeginTransactionRequest.newBuilder()
              .setSession(sessionName)
              .setOptions(transactionOptions)
              .build();

      return ObservableReactiveUtil.unaryCall(
          (obs) -> this.spanner.beginTransaction(beginTransactionRequest, obs));
    });
  }

  @Override
  public Mono<CommitResponse> commitTransaction(String sessionName, Transaction transaction) {
    return Mono.defer(() -> {
      Assert.requireNonNull(sessionName, "Session name must not be null");
      Assert.requireNonEmpty(transaction.getId(), "Transaction ID must not be empty");

      CommitRequest commitRequest =
          CommitRequest.newBuilder()
              .setSession(sessionName)
              .setTransactionId(transaction.getId())
              .build();

      return ObservableReactiveUtil.unaryCall(
          (obs) -> this.spanner.commit(commitRequest, obs));
    });
  }

  @Override
  public Mono<Void> rollbackTransaction(String sessionName, Transaction transaction) {
    return Mono.defer(() -> {
      Assert.requireNonNull(sessionName, "Session name must not be null");
      Assert.requireNonEmpty(transaction.getId(), "Transaction ID must not be empty");

      RollbackRequest rollbackRequest =
          RollbackRequest.newBuilder()
              .setSession(sessionName)
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
      Assert.requireNonEmpty(databaseName, "Database name must not be empty");

      CreateSessionRequest request = CreateSessionRequest.newBuilder()
          .setDatabase(databaseName)
          .build();

      return ObservableReactiveUtil.unaryCall((obs) -> this.spanner.createSession(request, obs));
    });
  }

  @Override
  public Mono<Void> deleteSession(String sessionName) {
    return Mono.defer(() -> {
      Assert.requireNonNull(sessionName, "Session name must not be null");

      DeleteSessionRequest deleteSessionRequest =
          DeleteSessionRequest.newBuilder()
              .setName(sessionName)
              .build();

      return ObservableReactiveUtil.<Empty>unaryCall(
          (observer) -> this.spanner.deleteSession(deleteSessionRequest, observer))
          .then();
    });
  }

  @Override
  public Flux<ResultSet> executeBatchDml(
      StatementExecutionContext ctx,
      List<Statement> statements) {

    return Mono.defer(() -> {
      ExecuteBatchDmlRequest.Builder request =
          ExecuteBatchDmlRequest.newBuilder()
              .setSession(ctx.getSessionName())
              .addAllStatements(statements)
              .setSeqno(ctx.nextSeqNum());

      if (ctx.getTransactionId() != null) {
        request.setTransaction(TransactionSelector.newBuilder().setId(ctx.getTransactionId()));
        return ObservableReactiveUtil.unaryCall(
            obs -> this.spanner.executeBatchDml(request.build(), obs));
      } else {
        request.setTransaction(TransactionSelector.newBuilder().setBegin(READ_WRITE_TRANSACTION));
        return ObservableReactiveUtil
            .<ExecuteBatchDmlResponse>unaryCall(
                obs -> this.spanner.executeBatchDml(request.build(), obs))
            .delayUntil(response -> {
              if (response.getResultSetsList().size() > 0) {
                Transaction transaction =
                    response.getResultSets(0)
                        .getMetadata()
                        .getTransaction();
                return commitTransaction(ctx.getSessionName(), transaction);
              } else {
                return Mono.empty();
              }
            });
      }
    })
    .flatMapMany(response -> {
      Flux<ResultSet> results = Flux.fromIterable(response.getResultSetsList());
      if (response.hasStatus() && response.getStatus().getCode() != Status.Code.OK.value()) {
        results = results.concatWith(
            Mono.error(
                new R2dbcNonTransientResourceException(response.getStatus().getMessage())));
      }
      return results;
    });
  }

  @Override
  public Flux<PartialResultSet> executeStreamingSql(
      StatementExecutionContext ctx,
      String sql,
      Struct params,
      Map<String, Type> types) {

    return Flux.defer(() -> {
      Assert.requireNonNull(ctx.getSessionName(), "Session name must not be null");

      ExecuteSqlRequest.Builder executeSqlRequest =
          ExecuteSqlRequest.newBuilder()
              .setSql(sql)
              .setSession(ctx.getSessionName());

      if (params != null) {
        executeSqlRequest
            .setParams(params)
            .putAllParamTypes(types);
      }

      if (ctx.getTransactionId() != null) {
        executeSqlRequest.setTransaction(
            TransactionSelector.newBuilder().setId(ctx.getTransactionId())
                .build());
        executeSqlRequest.setSeqno(ctx.nextSeqNum());
      }

      return ObservableReactiveUtil.streamingCall(
          obs -> this.spanner.executeStreamingSql(executeSqlRequest.build(), obs));
    });
  }

  @Override
  public Mono<Operation> executeDdl(
      String fullyQualifiedDatabaseName,
      List<String> ddlStatements,
      Duration ddlOperationTimeout,
      Duration ddlPollInterval) {

    UpdateDatabaseDdlRequest ddlRequest =
        UpdateDatabaseDdlRequest.newBuilder()
            .setDatabase(fullyQualifiedDatabaseName)
            .addAllStatements(ddlStatements)
            .build();

    Mono<Operation> ddlResponse =
        ObservableReactiveUtil.unaryCall(
            obs -> this.databaseAdmin.updateDatabaseDdl(ddlRequest, obs));

    return ddlResponse.flatMap(ddlOperation -> {
      GetOperationRequest getRequest =
          GetOperationRequest.newBuilder()
              .setName(ddlOperation.getName())
              .build();

      return ObservableReactiveUtil
          .<Operation>unaryCall(obs -> this.operations.getOperation(getRequest, obs))
          .repeatWhen(completed -> completed.delayElements(ddlPollInterval))
          .takeUntil(Operation::getDone)
          .last()
          .timeout(ddlOperationTimeout)
          .handle((operation, sink) -> {
            if (operation.hasError()) {
              sink.error(new R2dbcNonTransientResourceException(operation.getError().getMessage()));
            } else {
              sink.next(operation);
            }
          });
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
