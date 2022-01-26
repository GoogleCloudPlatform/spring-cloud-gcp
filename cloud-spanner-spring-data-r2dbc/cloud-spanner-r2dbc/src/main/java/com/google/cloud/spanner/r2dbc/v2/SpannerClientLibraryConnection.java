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

import com.google.cloud.spanner.TimestampBound;
import com.google.cloud.spanner.r2dbc.api.SpannerConnection;
import com.google.cloud.spanner.r2dbc.statement.StatementParser;
import com.google.cloud.spanner.r2dbc.statement.StatementType;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionMetadata;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.TransactionDefinition;
import io.r2dbc.spi.ValidationDepth;
import java.time.Duration;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

class SpannerClientLibraryConnection implements Connection, SpannerConnection {

  private final DatabaseClientReactiveAdapter clientLibraryAdapter;

  /**
   * Cloud Spanner implementation of R2DBC Connection SPI.
   *
   * @param clientLibraryAdapter adapter to Cloud Spanner database client
   */
  public SpannerClientLibraryConnection(DatabaseClientReactiveAdapter clientLibraryAdapter) {
    this.clientLibraryAdapter = clientLibraryAdapter;
  }

  @Override
  public Publisher<Void> beginTransaction() {
    return this.clientLibraryAdapter.beginTransaction();
  }

  @Override
  public Publisher<Void> beginTransaction(TransactionDefinition definition) {
    return Mono.error(new UnsupportedOperationException());
  }

  @Override
  public Publisher<Void> setLockWaitTimeout(Duration timeout) {
    return Mono.error(new UnsupportedOperationException());
  }

  @Override
  public Publisher<Void> setStatementTimeout(Duration timeout) {
    return Mono.error(new UnsupportedOperationException());
  }

  @Override
  public Mono<Void> beginReadonlyTransaction(TimestampBound timestampBound) {
    return this.clientLibraryAdapter.beginReadonlyTransaction(timestampBound);
  }

  @Override
  public Mono<Void> beginReadonlyTransaction() {
    return this.clientLibraryAdapter.beginReadonlyTransaction(TimestampBound.strong());
  }

  @Override
  public Publisher<Void> commitTransaction() {
    return this.clientLibraryAdapter.commitTransaction();
  }

  @Override
  public Batch createBatch() {
    return new SpannerBatch(this.clientLibraryAdapter);
  }

  @Override
  public Publisher<Void> createSavepoint(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement createStatement(String query) {
    if (query == null) {
      throw new IllegalArgumentException("Invalid null query.");
    }
    StatementType type = StatementParser.getStatementType(query);
    if (type == StatementType.DDL) {
      return new SpannerClientLibraryDdlStatement(query, this.clientLibraryAdapter);
    } else if (type == StatementType.DML) {
      return new SpannerClientLibraryDmlStatement(this.clientLibraryAdapter, query);
    }
    return new SpannerClientLibraryStatement(this.clientLibraryAdapter, query);
  }

  @Override
  public boolean isAutoCommit() {
    return this.clientLibraryAdapter.isAutoCommit();
  }

  @Override
  public ConnectionMetadata getMetadata() {
    throw new UnsupportedOperationException();
  }

  @Override
  public IsolationLevel getTransactionIsolationLevel() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Publisher<Void> releaseSavepoint(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Publisher<Void> rollbackTransaction() {
    return this.clientLibraryAdapter.rollback();
  }

  @Override
  public Publisher<Void> rollbackTransactionToSavepoint(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Publisher<Void> setAutoCommit(boolean autoCommit) {
    return this.clientLibraryAdapter.setAutoCommit(autoCommit);
  }

  @Override
  public Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Publisher<Boolean> validate(ValidationDepth depth) {
    if (depth == ValidationDepth.LOCAL) {
      return this.clientLibraryAdapter.localHealthcheck();
    } else {
      return this.clientLibraryAdapter.healthCheck();
    }
  }

  @Override
  public Publisher<Void> close() {
    return this.clientLibraryAdapter.close();
  }

  public boolean isInReadonlyTransaction() {
    return this.clientLibraryAdapter.isInReadonlyTransaction();
  }
}
