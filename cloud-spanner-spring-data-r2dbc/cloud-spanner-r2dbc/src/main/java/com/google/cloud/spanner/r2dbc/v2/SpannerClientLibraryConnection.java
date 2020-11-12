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
import com.google.cloud.spanner.r2dbc.SpannerConnectionConfiguration;
import com.google.cloud.spanner.r2dbc.statement.StatementParser;
import com.google.cloud.spanner.r2dbc.statement.StatementType;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionMetadata;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.ValidationDepth;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class SpannerClientLibraryConnection implements Connection {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SpannerClientLibraryConnection.class);

  private final DatabaseClientReactiveAdapter clientLibraryAdapter;



  /**
   * Cloud Spanner implementation of R2DBC Connection SPI.
   * @param clientLibraryAdapter adapter to Cloud Spanner database client
   * @param config driver configuration extracted from URL or passed directly to connection factory.
   */
  public SpannerClientLibraryConnection(DatabaseClientReactiveAdapter clientLibraryAdapter,
      SpannerConnectionConfiguration config) {
    this.clientLibraryAdapter = clientLibraryAdapter;
  }

  @Override
  public Publisher<Void> beginTransaction() {
    return this.clientLibraryAdapter.beginTransaction();
  }

  /**
   * Allows starting a readonly Cloud Spanner transaction with given staleness settings.
   * @param timestampBound staleness settings
   * @return {@link Mono} signaling readonly transaction is ready for use
   */
  public Mono<Void> beginReadonlyTransaction(TimestampBound timestampBound) {
    return this.clientLibraryAdapter.beginReadonlyTransaction(timestampBound);
  }

  /**
   * Allows starting a readonly Cloud Spanner transaction with strong consistency.
   * @return {@link Mono} signaling readonly transaction is ready for use
   */
  public Mono<Void> beginReadonlyTransaction() {
    return this.clientLibraryAdapter.beginReadonlyTransaction(TimestampBound.strong());
  }

  @Override
  public Publisher<Void> commitTransaction() {
    return this.clientLibraryAdapter.commitTransaction();
  }

  @Override
  public Batch createBatch() {
    throw new UnsupportedOperationException();
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
      LOGGER.debug("DDL statement detected: " + query);
      return new SpannerClientLibraryDdlStatement(query, this.clientLibraryAdapter);
    } else if (type == StatementType.DML) {
      LOGGER.debug("DML statement detected: " + query);
      return new SpannerClientLibraryDmlStatement(this.clientLibraryAdapter, query);
    }
    return new SpannerClientLibraryStatement(this.clientLibraryAdapter, query);
  }

  @Override
  public boolean isAutoCommit() {
    return false;
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
    throw new UnsupportedOperationException();
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
}
