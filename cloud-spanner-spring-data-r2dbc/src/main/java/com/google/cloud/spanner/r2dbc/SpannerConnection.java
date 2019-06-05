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
import com.google.spanner.v1.Session;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.IsolationLevel;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * {@link Connection} implementation for Cloud Spanner.
 */
public class SpannerConnection implements Connection {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final Client client;

  private final Session session;

  private volatile SpannerTransactionContext transactionContext;

  private Integer partialResultSetFetchSize;

  /**
   * Instantiates a Spanner session with given configuration.
   * @param client client controlling low-level Spanner operations
   * @param session Spanner session to use for all interactions on this connection.
   */
  public SpannerConnection(Client client, Session session) {
    this.client = client;
    this.session = session;
    this.transactionContext = null;
  }

  @Override
  public Publisher<Void> beginTransaction() {
    return this.client.beginTransaction(this.session)
        .doOnNext(
            transaction -> this.transactionContext = SpannerTransactionContext.from(transaction))
        .then();
  }

  @Override
  public Publisher<Void> commitTransaction() {
    return Mono.defer(() -> {
      if (this.transactionContext == null) {
        this.logger.warn("commitTransaction() is a no-op; called with no transaction active.");
        return Mono.empty();
      } else {
        return this.client.commitTransaction(this.session, this.transactionContext.getTransaction())
            .then();
      }
    });
  }

  @Override
  public Publisher<Void> rollbackTransaction() {
    return Mono.defer(() -> {
      if (this.transactionContext == null) {
        this.logger.warn("rollbackTransaction() is a no-op; called with no transaction active.");
        return Mono.empty();
      } else {
        return this.client
            .rollbackTransaction(this.session, this.transactionContext.getTransaction());
      }
    });
  }

  @Override
  public Publisher<Void> close() {
    return this.client.deleteSession(this.session);
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
    SpannerStatement statement
        = new SpannerStatement(this.client, this.session, this.transactionContext, sql);

    statement.setPartialResultSetFetchSize(this.partialResultSetFetchSize);

    return statement;
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

  /**
   * Returns the Spanner session associated with the current {@link Connection}.
   * @return spanner session proto
   */
  public Session getSession() {
    return this.session;
  }

  public void setPartialResultSetFetchSize(Integer fetchSize) {
    this.partialResultSetFetchSize = fetchSize;
  }

}
