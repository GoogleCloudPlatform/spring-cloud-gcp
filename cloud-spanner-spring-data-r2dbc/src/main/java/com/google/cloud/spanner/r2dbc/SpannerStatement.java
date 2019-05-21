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
import com.google.cloud.spanner.r2dbc.result.PartialResultRowExtractor;
import com.google.cloud.spanner.r2dbc.util.ConvertingFluxAdapter;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.Transaction;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link Statement} implementation for Cloud Spanner.
 */
public class SpannerStatement implements Statement {

  private Client client;

  private Session session;

  private Mono<Transaction> transaction;

  private String sql;

  /**
   * Creates a Spanner statement for a given SQL statement.
   *
   * <p>If no transaction is present, a temporary strongly consistent readonly transaction will be
   * used.
   *
   * @param client cloud spanner client to use for performing the query operation
   * @param session current cloud spanner session
   * @param transaction current cloud spanner transaction, or empty if no transaction is started
   * @param sql the query to execute
   */
  public SpannerStatement(
      Client client, Session session, Mono<Transaction> transaction, String sql) {
    this.client = client;
    this.session = session;
    this.transaction = transaction;
    this.sql = sql;
  }

  @Override
  public Statement add() {
    return null;
  }

  @Override
  public Statement bind(Object o, Object o1) {
    return null;
  }

  @Override
  public Statement bind(int i, Object o) {
    return null;
  }

  @Override
  public Statement bindNull(Object o, Class<?> type) {
    return null;
  }

  @Override
  public Statement bindNull(int i, Class<?> type) {
    return null;
  }

  @Override
  public Publisher<? extends Result> execute() {
    Flux<PartialResultSet> result
        = client.executeStreamingSql(this.session, this.transaction, this.sql);

    // Then use a different SpannerResult constructor (this might be more difficult because the
    // update row stats come as the last PartialResultSet, in which case SpannerResult might need
    // only a single constructor that takes a flux of rows and a mono of the # rows updated).
    return Mono
        .just(new SpannerResult(
            Flux.create(sink -> result
                .subscribe(new ConvertingFluxAdapter(sink, new PartialResultRowExtractor())))));
  }
}
