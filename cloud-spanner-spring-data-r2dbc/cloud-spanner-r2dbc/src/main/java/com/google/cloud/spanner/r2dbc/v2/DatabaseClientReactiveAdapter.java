/*
 * Copyright 2019-2020 Google LLC
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

package com.google.cloud.spanner.r2dbc.v2;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.spanner.AsyncResultSet;
import com.google.cloud.spanner.AsyncResultSet.CallbackResponse;
import com.google.cloud.spanner.AsyncTransactionManager;
import com.google.cloud.spanner.AsyncTransactionManager.AsyncTransactionStep;
import com.google.cloud.spanner.AsyncTransactionManager.TransactionContextFuture;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.ReadContext;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.r2dbc.SpannerConnectionConfiguration;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

/**
 * Converts gRPC/Cloud Spanner client library asyncronous abstractions into reactive ones.
 * Encapsulates useful per-connection state.
 */
class DatabaseClientReactiveAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseClientReactiveAdapter.class);

  // used for DDL operations
  private final SpannerConnectionConfiguration config;

  private final DatabaseClient dbClient;

  private final DatabaseAdminClient dbAdminClient;

  private AsyncTransactionManager transactionManager;

  private ExecutorService executorService;

  private TransactionContextFuture txnContext;

  private AsyncTransactionStep<?, ? extends Object> lastStep;

  /**
   * Instantiates the adapter with given client library {@code DatabaseClient} and executor.
   *
   * @param spannerClient Cloud Spanner client used to run queries and manage transactions.
   * @param executorService executor to be used for running callbacks.
   */
  public DatabaseClientReactiveAdapter(Spanner spannerClient, ExecutorService executorService,
      SpannerConnectionConfiguration config) {
    this.dbClient = spannerClient.getDatabaseClient(
        DatabaseId.of(config.getProjectId(), config.getInstanceName(), config.getDatabaseName()));
    this.dbAdminClient = spannerClient.getDatabaseAdminClient();

    this.executorService = executorService;
    this.config = config;
  }

  private boolean isInTransaction() {
    return this.txnContext != null;
  }

  /**
   * Allows starting a Cloud Spanner transaction.
   *
   * @return reactive pipeline for starting a transaction
   */
  public Mono<Void> beginTransaction() {
    return convertFutureToMono(() -> {
      LOGGER.debug("begin transaction");
      this.transactionManager = this.dbClient.transactionManagerAsync();
      this.txnContext = this.transactionManager.beginAsync();
      return this.txnContext;
    }).then();
  }

  /**
   * Allows committing a Cloud Spanner transaction.
   *
   * @return reactive pipeline for committing a transaction
   */
  public Publisher<Void> commitTransaction() {

    return convertFutureToMono(() -> {
      LOGGER.debug("commit transaction");
      if (this.lastStep == null) {
        // TODO: replace by a better non-retryable;
        //  consider not throwing at all and no-oping with warning.
        throw new RuntimeException("Nothing was executed in this transaction");
      }
      return this.lastStep.commitAsync();

    }).doOnTerminate(this::clearTransactionManager).then();
  }

  private void clearTransactionManager() {
    LOGGER.debug("close transaction manager");
    this.txnContext = null;
    this.lastStep = null;
    this.transactionManager.close();
  }

  /**
   * Allows rolling back a Cloud Spanner transaction.
   *
   * @return reactive pipeline for rolling back a transaction
   */
  public Publisher<Void> rollback() {
    return convertFutureToMono(() -> {
      LOGGER.debug("roll back");
      if (this.lastStep == null) {
        // TODO: replace by a better non-retryable;
        //  consider not throwing at all and no-oping with warning.
        throw new RuntimeException(
            "No statements were executed in this transaction; no-op rollback");
      }
      return this.transactionManager.rollbackAsync();

    }).doOnTerminate(this::clearTransactionManager);
  }

  /**
   * Allows cleaning up resources.
   *
   * <p>Closes client library objects.
   *
   * @return reactive pipeline for closing the connection.
   */
  public Mono<Void> close() {
    // TODO: if txn is committed/rolled back and then connection closed, clearTransactionManager
    // will run twice, causing trace span to be closed twice. Introduce `closed` field.
    return Mono.<Void>fromRunnable(this::clearTransactionManager);
  }

  /**
   * Allows running a DML statement.
   *
   * <p>If no transaction is active, a single-use transaction will be used.
   *
   * @return reactive pipeline for running a DML statement
   */
  public Mono<Long> runDmlStatement(com.google.cloud.spanner.Statement statement) {
    return convertFutureToMono(() -> {
      if (this.isInTransaction()) {
        LOGGER.debug("  chaining DML statement in transaction: " + statement.getSql());

        // The first statement in a transaction has no input, hence Void input type.
        // The subsequent statements take the previous statements' return (affected row count)
        // as input.
        AsyncTransactionStep<? extends Object, Long> updateStatementFuture =
            this.lastStep == null
                ? this.txnContext.then(
                    (ctx, unusedVoid) -> ctx.executeUpdateAsync(statement),
                    this.executorService)
                : this.lastStep.then(
                    (ctx, unusedPreviousResult) -> ctx.executeUpdateAsync(statement),
                    this.executorService);

        this.lastStep = updateStatementFuture;
        return updateStatementFuture;

      } else {
        LOGGER.debug("  running standalone DML statement: " + statement.getSql());
        ApiFuture<Long> rowCountFuture =
            this.dbClient
                .runAsync()
                .runAsync(txn -> txn.executeUpdateAsync(statement), this.executorService);
        return rowCountFuture;
      }
    });
  }

  public Flux<SpannerClientLibraryRow> runSelectStatement(
      com.google.cloud.spanner.Statement statement) {
    return Flux.create(
        sink -> {
          if (this.isInTransaction()) {

            LOGGER.debug("  chaining SELECT statement in transaction: " + statement.getSql());
            // The first statement in a transaction has no input, hence Void input type.
            // The subsequent statements take the previous statement's return value as input.
            this.lastStep =
                this.lastStep == null
                    ? this.txnContext.then(
                        (ctx, unusedVoid) -> runSelectStatementAsFlux(() -> ctx, statement, sink),
                        this.executorService)
                    : this.lastStep.then(
                        (ctx, unusedPreviousResult) ->
                            runSelectStatementAsFlux(() -> ctx, statement, sink),
                        this.executorService);

          } else {
            LOGGER.debug("  running standalone SELECT statement: " + statement.getSql());
            runSelectStatementAsFlux(() -> this.dbClient.singleUse(), statement, sink);
          }
        });
  }

  public Mono<Void> runDdlStatement(String query) {

    return convertFutureToMono(() -> this.dbAdminClient.updateDatabaseDdl(
        this.config.getInstanceName(),
        this.config.getDatabaseName(),
        Collections.singletonList(query),
        null));
  }

  /**
   * Runs SELECT query, adapting its output from {@link AsyncResultSet} to {@link FluxSink}.
   *
   * <p>Make sure that if run from a transactional context, this method is called after {@link
   * ApiFuture} returned by `transactionManager.beginAsync()` resolves. In practice, this means
   * always invoking this method in a chained `.then()` lambda * when running in transaction.
   *
   * @param ctxSupplier Non-blocking supplier of read context (transactional or not).
   * @param statement query to run
   * @param sink output Flux sink
   * @return future suitable for transactional step chaining
   */
  private ApiFuture<Void> runSelectStatementAsFlux(
      Supplier<ReadContext> ctxSupplier,
      com.google.cloud.spanner.Statement statement,
      FluxSink<SpannerClientLibraryRow> sink) {
    AsyncResultSet ars = ctxSupplier.get().executeQueryAsync(statement);
    sink.onCancel(ars::cancel);

    return ars.setCallback(
        this.executorService,
        resultSet -> {
          // TODO: handle backpressure by asking callback to signal CallbackResponse.PAUSE
          try {
            switch (resultSet.tryNext()) {
              case DONE:
                sink.complete();
                return CallbackResponse.DONE;
              case NOT_READY:
              default:
                return CallbackResponse.CONTINUE;
              case OK:
                sink.next(new SpannerClientLibraryRow(resultSet.getCurrentRowAsStruct()));
                return CallbackResponse.CONTINUE;
            }
          } catch (Throwable t) {
            sink.error(t);
            return CallbackResponse.DONE;
          }
        });
  }

  private <T> Mono<T> convertFutureToMono(Supplier<ApiFuture<T>> futureSupplier) {
    return Mono.create(
        sink -> {
          ApiFuture future = futureSupplier.get();
          sink.onCancel(() -> future.cancel(true));

          ApiFutures.addCallback(future,
              new ApiFutureCallback<T>() {
                @Override
                public void onFailure(Throwable t) {
                  sink.error(t);
                }

                @Override
                public void onSuccess(T result) {
                  sink.success(result);
                }
              },
              this.executorService);
        });
  }

}
