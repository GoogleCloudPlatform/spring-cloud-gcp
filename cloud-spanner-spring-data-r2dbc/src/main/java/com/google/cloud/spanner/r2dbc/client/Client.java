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

import com.google.cloud.spanner.r2dbc.StatementExecutionContext;
import com.google.longrunning.Operation;
import com.google.protobuf.Struct;
import com.google.spanner.v1.CommitResponse;
import com.google.spanner.v1.ExecuteBatchDmlRequest.Statement;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.ResultSet;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.Transaction;
import com.google.spanner.v1.TransactionOptions;
import com.google.spanner.v1.Type;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * An abstraction that wraps interaction with the Cloud Spanner Database APIs.
 */
public interface Client {

  /**
   * Create a Spanner session to be used in subsequent interactions with the database.
   *
   * @param databaseName Fully qualified Spanner database name in the format {@code
   * projects/[PROJECT_ID]/instances/[INSTANCE]/databases/[DATABASE]}
   * @return {@link Mono} of the generated session.
   */
  Mono<Session> createSession(String databaseName);

  /**
   * Deletes a Spanner session that is used to call Spanner APIs.
   *
   * @param sessionName name of existing Cloud Spanner session to be closed.
   * @return {@link Mono} indicating completion closing the session.
   */
  Mono<Void> deleteSession(String sessionName);

  /**
   * Begins a new Spanner {@link Transaction} within the provided {@link Session}.
   *
   * @param sessionName name of existing Cloud Spanner session.
   * @param transactionOptions properties determining the type of transaction to create
   * @return {@link Mono} of the transaction that was started.
   */
  Mono<Transaction> beginTransaction(String sessionName, TransactionOptions transactionOptions);

  /**
   * Commits a Spanner {@link Transaction} within the provided {@link Session}.
   *
   * @param sessionName name of existing Cloud Spanner session
   * @param transaction The transaction that you want to commit.
   * @return {@link CommitResponse} describing the timestamp at which the transaction committed.
   */
  Mono<CommitResponse> commitTransaction(String sessionName, Transaction transaction);


  /**
   * Performs a rollback on a Spanner {@link Transaction} within the provided {@link Session}.
   *
   * @param sessionName name of existing Cloud Spanner session.
   * @param transaction The transaction that you want to rollback.
   * @return {@link Mono} indicating completion of the rollback.
   */
  Mono<Void> rollbackTransaction(String sessionName, Transaction transaction);

  /**
   * Execute a streaming query and get partial results.
   *
   * @param ctx connection-specific state.
   * @param sql select or DML query to execute
   * @param params parameter values
   * @param types parameter types
   * @return the {@link Flux} of partial result sets from the query.
   */
  Flux<PartialResultSet> executeStreamingSql(
      StatementExecutionContext ctx,
      String sql,
      Struct params,
      Map<String, Type> types);

  /**
   * Execute a streaming query without any parameters.
   *
   * @param ctx connection-specific state.
   * @param sql select or DML query to execute
   * @return the {@link Flux} of partial result sets from the query.
   */
  default Flux<PartialResultSet> executeStreamingSql(StatementExecutionContext ctx, String sql) {
    return executeStreamingSql(ctx, sql, null, null);
  }

  /**
   * Execute DML batch.
   *
   * @param ctx connection-specific state.
   * @param statements list of DML statements to execute.
   * @return the {@link ResultSet}s returned after executing the query
   */
  Flux<ResultSet> executeBatchDml(StatementExecutionContext ctx, List<Statement> statements);

  /**
   * Execute a DDL query.
   *
   * <p>The underlying API is a long-running operation that has to be polled for status.
   *
   * @param fullyQualifiedDatabaseName database name, including project ID and instance name.
   * @param ddlStatement statement to execute (CREATE/DROP etc.).
   * @param ddlOperationTimeout how long to poll for the operation results until giving up.
   * @param ddlPollInterval how frequently to poll for the operation results.
   * @return a {@link Mono} containing the operation metadata for tracking status of operation.
   */
  Mono<Operation> executeDdl(
      String fullyQualifiedDatabaseName,
      List<String> ddlStatement,
      Duration ddlOperationTimeout,
      Duration ddlPollInterval);

  /**
   * Release any resources held by the {@link Client}.
   *
   * @return a {@link Mono} that indicates that a client has been closed
   */
  Mono<Void> close();

  /**
   * Validates session associated with the passed in {@link StatementExecutionContext}.
   * @return {@link Mono} of whether the connection is working.
   */
  Mono<Boolean> healthcheck(StatementExecutionContext ctx);
}
