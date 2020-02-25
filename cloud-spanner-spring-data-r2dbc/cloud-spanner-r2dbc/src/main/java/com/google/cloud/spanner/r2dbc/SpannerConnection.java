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

package com.google.cloud.spanner.r2dbc;

import com.google.cloud.spanner.r2dbc.client.Client;
import com.google.protobuf.ByteString;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.Transaction;
import com.google.spanner.v1.TransactionOptions;
import com.google.spanner.v1.TransactionOptions.ReadWrite;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.ValidationDepth;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * {@link Connection} implementation for Cloud Spanner.
 */
public class SpannerConnection implements Connection, StatementExecutionContext {

  private static final TransactionOptions READ_WRITE_TRANSACTION =
      TransactionOptions.newBuilder()
          .setReadWrite(ReadWrite.getDefaultInstance())
          .build();

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final Client client;

  private Session session;

  private Transaction transaction;

  private TransactionOptions transactionOptions;

  private AtomicLong seqNum = new AtomicLong(0);

  private final SpannerConnectionConfiguration config;

  private boolean autoCommit = true;

  /**
   * Instantiates a Spanner session with given configuration.
   * @param client client controlling low-level Spanner operations.
   * @param session Spanner session to use for all interactions on this connection.
   * @param config configuration to customize the Spanner Connection properties.
   */
  public SpannerConnection(Client client, Session session, SpannerConnectionConfiguration config) {
    this.client = client;
    this.session = session;
    this.config = config;
  }

  @Override
  public Mono<Void> beginTransaction() {
    return this.beginTransaction(READ_WRITE_TRANSACTION);
  }

  /**
   * Begins a new transaction with the specified {@link TransactionOptions}.
   *
   * @param transactionOptions custom options to modify the transaction to start.
   * @return {@link Mono} indicating completion of the operation.
   */
  public Mono<Void> beginTransaction(TransactionOptions transactionOptions) {
    return this.client.beginTransaction(this.getSessionName(), transactionOptions)
        .doOnNext(transaction -> {
          this.setTransaction(transaction, transactionOptions);
          this.autoCommit = false;
        })
        .then();
  }

  @Override
  public Mono<Void> commitTransaction() {
    return commitTransaction(true);
  }

  private Mono<Void> commitTransaction(boolean logMessage) {
    return Mono.defer(() -> {
      if (this.getTransactionId() != null && this.isTransactionReadWrite()) {
        return this.client.commitTransaction(
            this.getSessionName(), this.transaction)
            .doOnNext(response -> this.setTransaction(null, null))
            .then();
      }

      if (logMessage) {
        if (this.getTransactionId() == null) {
          this.logger.debug("commitTransaction() is a no-op; called with no transaction active.");
        } else if (!this.isTransactionReadWrite()) {
          this.logger.debug("commitTransaction() is a no-op; "
              + "called outside of a read-write transaction.");
        }
      }
      return Mono.empty();
    });
  }

  @Override
  public Mono<Void> rollbackTransaction() {
    return Mono.defer(() -> {
      if (this.getTransactionId() == null) {
        this.logger.warn("rollbackTransaction() is a no-op; called with no transaction active.");
        return Mono.empty();
      }

      return this.client
            .rollbackTransaction(this.getSessionName(), this.transaction)
            .doOnSuccess(response -> this.setTransaction(null, null));
    });
  }

  @Override
  public Mono<Void> close() {
    return commitTransaction(false)
        .then(this.client.deleteSession(this.getSessionName()).doOnSuccess(none -> {
          this.session = null;
        }));
  }

  @Override
  public Batch createBatch() {
    return new SpannerBatch(this.client, this);
  }

  @Override
  public Publisher<Void> createSavepoint(String s) {
    throw new UnsupportedOperationException("Savepoints are not supported.");
  }

  @Override
  public SpannerStatement createStatement(String sql) {
    return new SpannerStatement(this.client, this, sql, this.config);
  }

  @Override
  public Publisher<Void> releaseSavepoint(String s) {
    throw new UnsupportedOperationException("Savepoints are not supported.");
  }

  @Override
  public Publisher<Void> rollbackTransactionToSavepoint(String s) {
    return null;
  }

  @Override
  public Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
    return Mono.error(
        new UnsupportedOperationException("Changing isolation level is not supported."));
  }

  @Override
  public IsolationLevel getTransactionIsolationLevel() {
    // This is an approximation.
    // Cloud Spanner's isolation guarantees are stronger than traditional RDBMS.
    return IsolationLevel.SERIALIZABLE;
  }

  @Override
  public ByteString getTransactionId() {
    return this.transaction == null ? null : this.transaction.getId();
  }

  @Override
  public String getSessionName() {
    return this.session == null ? null : this.session.getName();
  }

  @Override
  public long nextSeqNum() {
    return this.seqNum.getAndIncrement();
  }

  public boolean isTransactionReadWrite() {
    return this.transactionOptions == null ? false : this.transactionOptions.hasReadWrite();
  }

  public boolean isTransactionPartitionedDml() {
    return this.transactionOptions == null ? false : this.transactionOptions.hasPartitionedDml();
  }

  /**
   * Sets a new transaction or unsets the current one if {@code null} is passed in.
   * Transactions are mutable in the execution context.
   * @param transaction the newly opened transaction
   */
  private void setTransaction(
      @Nullable Transaction transaction, @Nullable TransactionOptions transactionOptions) {
    this.transaction = transaction;
    this.transactionOptions = transactionOptions;
  }

  @Override
  public Publisher<Boolean> validate(ValidationDepth validationDepth) {
    if (validationDepth == ValidationDepth.LOCAL) {
      return Mono.fromSupplier(() -> this.getSessionName() != null);
    }

    return this.client.healthcheck(this);
  }


  /**
   * Changes the autocommit mode of the current connection. No-op if the value is unchanged.
   *
   * <p>If autocommit was previously off and a read/write transaction is in progress, the
   * transaction is committed first.
   *
   * @param newAutoCommit whether autocommit should be on or off in the future.
   * @return {@link Mono} of the transaction commit operation, if applicable; empty mono otherwise.
   */
  @Override
  public Publisher<Void> setAutoCommit(boolean newAutoCommit) {
    return Mono.defer(
        () -> {
          boolean commitNeeded = newAutoCommit && !this.autoCommit && this.transaction != null;
          return (commitNeeded ? this.commitTransaction(false) : Mono.<Void>empty())
              .doOnSuccess(none -> {
                this.autoCommit = newAutoCommit;
              });
        });
  }

  /**
   * Determines current autocommit state of the connection (default is autocommit-on).
   * Autocommit applies to DML queries only.
   *
   * <p>When autocommit is on, each standalone DML query will be executed in its own Read/Write
   * transaction.
   *
   * <p>For batching multiple DML queries, see {@link #createBatch()}.
   *
   * @return whether autocommit mode is on.
   */
  @Override
  public boolean isAutoCommit() {
    return this.autoCommit;
  }

  @Override
  public SpannerConnectionMetadata getMetadata() {
    return SpannerConnectionMetadata.INSTANCE;
  }
}
