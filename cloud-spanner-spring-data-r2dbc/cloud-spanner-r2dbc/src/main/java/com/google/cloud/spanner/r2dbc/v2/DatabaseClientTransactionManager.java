/*
 * Copyright 2020-2020 Google LLC
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

import static com.google.cloud.spanner.r2dbc.v2.DatabaseClientReactiveAdapter.REACTOR_EXECUTOR;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.AsyncTransactionManager;
import com.google.cloud.spanner.AsyncTransactionManager.AsyncTransactionStep;
import com.google.cloud.spanner.AsyncTransactionManager.TransactionContextFuture;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ReadContext;
import com.google.cloud.spanner.ReadOnlyTransaction;
import com.google.cloud.spanner.TimestampBound;
import com.google.cloud.spanner.TransactionContext;
import com.google.cloud.spanner.r2dbc.TransactionInProgressException;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class encapsulating read/write and readonly transaction management for client library
 * based interaction with Cloud Spanner.
 * Partitioned DML is out of scope for transaction handling, as they are not atomic.
 */
class DatabaseClientTransactionManager {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DatabaseClientTransactionManager.class);

  private final DatabaseClient dbClient;

  private AsyncTransactionManager transactionManager;

  private TransactionContextFuture txnContextFuture;

  private ReadOnlyTransaction readOnlyTransaction;

  private AsyncTransactionStep<?, ? extends Object> lastStep;

  public DatabaseClientTransactionManager(DatabaseClient dbClient) {
    this.dbClient = dbClient;
  }

  boolean isInReadWriteTransaction() {
    return this.txnContextFuture != null;
  }

  boolean isInReadonlyTransaction() {
    return this.readOnlyTransaction != null;
  }

  boolean isInTransaction() {
    return isInReadWriteTransaction() || isInReadonlyTransaction();
  }

  /**
   * Starts a Cloud Spanner Read/Write transaction.
   *
   * @return chainable {@link TransactionContextFuture} for the current transaction.
   */
  TransactionContextFuture beginTransaction() {
    if (this.isInTransaction()) {
      throw new TransactionInProgressException(this.isInReadWriteTransaction());
    }
    this.transactionManager = this.dbClient.transactionManagerAsync();
    this.txnContextFuture = this.transactionManager.beginAsync();
    return this.txnContextFuture;
  }

  /**
   * Starts a Cloud Spanner Read-only transaction with specified staleness.
   *
   * @param timestampBound staleness settings
   *
   * @return chainable {@link TransactionContextFuture} for the current transaction.
   */
  void beginReadonlyTransaction(TimestampBound timestampBound) {
    if (this.isInTransaction()) {
      throw new TransactionInProgressException(this.isInReadWriteTransaction());
    }

    this.readOnlyTransaction = this.dbClient.readOnlyTransaction(timestampBound);
  }

  /**
   * Closes the read/write transaction manager and clears its state.
   */
  ApiFuture<Void> clearTransactionManager() {
    this.txnContextFuture = null;
    this.lastStep = null;
    ApiFuture<Void> returnFuture = ApiFutures.immediateFuture(null);

    if (this.transactionManager != null) {
      returnFuture = this.transactionManager.closeAsync();
      this.transactionManager = null;
    }

    if (isInReadonlyTransaction()) {
      closeReadOnlyTransaction();
    }

    return returnFuture;
  }

  /**
   * Commits the current transaction (if read/write) or closes it (if read-only).
   *
   * @return chainable {@link ApiFuture} for commit status.
   */
  ApiFuture<Timestamp> commitTransaction() {
    if (isInReadWriteTransaction()) {
      if (this.lastStep == null) {
        LOGGER.warn("Read/Write transaction committing without any statements.");
        return ApiFutures.immediateFuture(null);
      }
      return this.lastStep.commitAsync();

    } else if (isInReadonlyTransaction()) {
      closeReadOnlyTransaction();
    } else {
      LOGGER.warn("Commit called outside of an active transaction.");
    }
    return ApiFutures.immediateFuture(null);
  }

  private void closeReadOnlyTransaction() {
    this.readOnlyTransaction.close();
    this.readOnlyTransaction = null;
  }

  /**
   * Rolls back the current read/write transaction.
   *
   * @return chainable {@link ApiFuture} for rollback status.
   */
  ApiFuture<Void> rollbackTransaction() {

    if (isInReadWriteTransaction()) {
      if (this.lastStep == null) {
        LOGGER.warn("Read/Write transaction rolling back without any statements.");
      }
      return this.transactionManager.rollbackAsync();
    }
    LOGGER.warn("Rollback called outside of an active read/write transaction.");
    return ApiFutures.immediateFuture(null);
  }

  /**
   * Runs provided operation, managing the client library transactional future chaining.
   *
   * @param operation a function executing either streaming SQL or DML.
   *     The function accepts ReadContext for SELECT queries, and TransactionContext for DML.
   * @param <T> Type of object wrapped by the {@link ApiFuture} returned by the operation
   *
   * @return {@link ApiFuture} result of the provided operation
   */
  <T> ApiFuture<T> runInTransaction(Function<? super TransactionContext, ApiFuture<T>> operation) {

    // The first statement in a transaction has no input, hence Void input type.
    // The subsequent statements take the previous statements' return (affected row count)
    // as input.
    AsyncTransactionStep<? extends Object, T> updateStatementFuture =
        this.lastStep == null
            ? this.txnContextFuture.then(
                (ctx, unusedVoid) -> operation.apply(ctx), REACTOR_EXECUTOR)
            : this.lastStep.then(
                (ctx, unusedPreviousResult) -> operation.apply(ctx), REACTOR_EXECUTOR);

    this.lastStep = updateStatementFuture;
    return updateStatementFuture;
  }

  ReadContext getReadContext() {
    return this.readOnlyTransaction == null ? this.dbClient.singleUse() : this.readOnlyTransaction;
  }

}
