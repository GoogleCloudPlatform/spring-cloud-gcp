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
import com.google.cloud.spanner.r2dbc.statement.StatementParser;
import com.google.cloud.spanner.r2dbc.statement.StatementType;
import com.google.protobuf.ByteString;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.Transaction;
import com.google.spanner.v1.TransactionOptions;
import com.google.spanner.v1.TransactionOptions.ReadWrite;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.IsolationLevel;
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

  private final Session session;

  private Transaction transaction;

  private TransactionOptions transactionOptions;

  private AtomicLong seqNum = new AtomicLong(0);

  private final SpannerConnectionConfiguration config;

  /**
   * Instantiates a Spanner session with given configuration.
   * @param client client controlling low-level Spanner operations
   * @param session Spanner session to use for all interactions on this connection.
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
   */
  public Mono<Void> beginTransaction(TransactionOptions transactionOptions) {
    return this.client.beginTransaction(this.getSessionName(), transactionOptions)
        .doOnNext(transaction -> this.setTransaction(transaction, transactionOptions))
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
    return commitTransaction(false).then(this.client.deleteSession(this.getSessionName()));
  }

  @Override
  public Batch createBatch() {
    return null;
  }

  @Override
  public Publisher<Void> createSavepoint(String s) {
    return null;
  }

  @Override
  public SpannerStatement createStatement(String sql) {
    return StatementParser.getStatementType(sql) == StatementType.DML
        && !isTransactionPartitionedDml()
        ? new AutoCommitSpannerStatement(this.client, sql, this.config, this)
        : new SpannerStatement(this.client, this, sql, this.config);
  }

  @Override
  public Publisher<Void> releaseSavepoint(String s) {
    return null;
  }

  @Override
  public Publisher<Void> rollbackTransactionToSavepoint(String s) {
    return null;
  }

  @Override
  public Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
    return null;
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

}
