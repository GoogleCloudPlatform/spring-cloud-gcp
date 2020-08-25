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
import com.google.cloud.spanner.AsyncTransactionManager;
import com.google.cloud.spanner.AsyncTransactionManager.AsyncTransactionStep;
import com.google.cloud.spanner.AsyncTransactionManager.TransactionContextFuture;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Statement;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * Converts gRPC/Cloud Spanner client library asyncronous abstractions into reactive ones.
 * Encapsulates useful per-connection state.
 */
class DatabaseClientReactiveAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseClientReactiveAdapter.class);

  private final DatabaseClient dbClient;

  private AsyncTransactionManager transactionManager;

  private ExecutorService executorService;

  private TransactionContextFuture currentTransactionFuture;

  private AsyncTransactionStep<?,Long> asyncTransactionLastStep;

  /**
   * Instantiates the adapter with given client library {@code DatabaseClient} and executor.
   * @param dbClient Cloud Spanner DatabaseClient used to run queries and manage transactions.
   * @param executorService executor to be used for future callbacks.
   */
  public DatabaseClientReactiveAdapter(DatabaseClient dbClient, ExecutorService executorService) {
    this.dbClient = dbClient;
    this.executorService = executorService;
  }

  private boolean isInTransaction() {
    return this.currentTransactionFuture != null;
  }

  /**
   * Allows starting a Cloud Spanner transaction.
   *
   * @return reactive pipeline for starting a transaction
   */
  public Mono<Void> beginTransaction() {
    return convertFutureToMono(() -> {
      LOGGER.debug("  STARTING TRANSACTION");
      this.transactionManager = this.dbClient.transactionManagerAsync();
      this.currentTransactionFuture = this.transactionManager.beginAsync();
      return this.currentTransactionFuture;
    }).then();
  }

  /**
   * Allows committing a Cloud Spanner transaction.
   *
   * @return reactive pipeline for committing a transaction
   */
  public Publisher<Void> commitTransaction() {

    return convertFutureToMono(() -> {
      LOGGER.debug("  COMMITTING");
      if (this.asyncTransactionLastStep == null) {
        // TODO: replace by a better non-retryable;
        //  consider not throwing at all and no-oping with warning.
        throw new RuntimeException("Nothing was executed in this transaction");
      }
      return this.asyncTransactionLastStep.commitAsync();

    }).doOnTerminate(this::clearTransactionManager).then();

  }

  private void clearTransactionManager() {
    LOGGER.debug("  closing transaction manager");
    this.currentTransactionFuture = null;
    this.transactionManager.close();
  }

  /**
   * Allows rolling back a Cloud Spanner transaction.
   *
   * @return reactive pipeline for rolling back a transaction
   */
  public Publisher<Void> rollback() {
    return convertFutureToMono(() -> {
      LOGGER.debug("  ROLLING BACK");
      if (this.asyncTransactionLastStep == null) {
        // TODO: replace by a better non-retryable;
        //  consider not throwing at all and no-oping with warning.
        throw new RuntimeException(
            "No statements were executed in this transaction; no-op rollback");
      }
      return this.transactionManager.rollbackAsync();
    }).doOnTerminate(this::clearTransactionManager);
  }

  /**
   * Allows cleaning up used resources.
   *
   * <p>Closes client library objects.
   *
   * @return reactive pipeline for starting a transaction
   */
  public Mono<Void> close() {
    return Mono.<Void>fromSupplier(() -> {
      this.transactionManager.close();
      return null;
    });
  }

  // TODO (elfel): extend to Query statements
  /**
   * Allows running a DML statement.
   *
   * @return reactive pipeline for starting a transaction
   */
  public Mono<Long> runDmlStatement(com.google.cloud.spanner.Statement statement) {

    return convertFutureToMono(() -> this.isInTransaction()
          ? this.chainStatementInTransaction(statement)
          : this.dbClient.runAsync()
              .runAsync(txn -> txn.executeUpdateAsync(statement), this.executorService));

  }

  // TODO: spanner allows read queries within the transaction. Right now, only update queries
  //  get passed here
  private AsyncTransactionStep chainStatementInTransaction(Statement statement) {

    LOGGER.debug("  CHAINING STEP: " + statement.getSql());

    // The first statement in a transaction has no input, hence Void input type.
    // The subsequent statements take the previous statements' return (affected row count) as input.
    this.asyncTransactionLastStep = this.asyncTransactionLastStep == null
        ? this.currentTransactionFuture.<Long>then(
          (ctx, unusedVoid) -> ctx.executeUpdateAsync(statement), this.executorService)
        : this.asyncTransactionLastStep.<Long>then(
          (ctx, previousRowCount) -> ctx.executeUpdateAsync(statement), this.executorService);

    return this.asyncTransactionLastStep;
  }

  private <T> Mono<T> convertFutureToMono(Supplier<ApiFuture<T>> futureSupplier) {
    return Mono.create(sink -> {
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
          }, this.executorService);
    });
  }

}
