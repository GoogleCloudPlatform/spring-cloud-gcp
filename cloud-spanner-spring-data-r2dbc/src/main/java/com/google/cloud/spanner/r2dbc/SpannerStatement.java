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
import com.google.cloud.spanner.r2dbc.statement.StatementBindings;
import com.google.cloud.spanner.r2dbc.statement.StatementParser;
import com.google.cloud.spanner.r2dbc.statement.StatementType;
import com.google.cloud.spanner.r2dbc.statement.TypedNull;
import com.google.cloud.spanner.r2dbc.util.Assert;
import com.google.protobuf.Struct;
import com.google.spanner.v1.ExecuteBatchDmlResponse;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.Session;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import java.util.Collections;
import javax.annotation.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link Statement} implementation for Cloud Spanner.
 */
public class SpannerStatement implements Statement {

  private Client client;

  private Session session;

  private SpannerTransactionContext transaction;

  private String sql;

  private SpannerConnectionConfiguration config;

  private StatementBindings statementBindings;

  private StatementType statementType;

  /**
   * Creates a Spanner statement for a given SQL statement.
   *
   * <p>If no transaction is present, a temporary strongly consistent readonly transaction will be
   * used.
   * @param client cloud spanner client to use for performing the query operation
   * @param session current cloud spanner session
   * @param transaction current cloud spanner transaction, or empty if no transaction is started
   * @param sql the query to execute
   */
  public SpannerStatement(
      Client client,
      Session session,
      @Nullable SpannerTransactionContext transaction,
      String sql,
      SpannerConnectionConfiguration config) {

    this.client = client;
    this.session = session;
    this.transaction = transaction;
    this.sql = Assert.requireNonNull(sql, "SQL string can not be null");
    this.config = config;
    this.statementBindings = new StatementBindings();
    this.statementType = StatementParser.getStatementType(this.sql);
  }

  @Override
  public Statement add() {
    this.statementBindings.completeBinding();
    return this;
  }

  @Override
  public Statement bind(Object identifier, Object value) {
    if (!(identifier instanceof String)) {
      throw new IllegalArgumentException("Only String identifiers are supported");
    }

    this.statementBindings.createBind((String) identifier, value);
    return this;
  }

  @Override
  public Statement bind(int i, Object o) {
    throw new UnsupportedOperationException("Only named parameters are supported");
  }

  @Override
  public Statement bindNull(Object identifier, Class<?> type) {
    return bind(identifier, new TypedNull(type));
  }

  @Override
  public Statement bindNull(int i, Class<?> type) {
    throw new UnsupportedOperationException("Only named parameters are supported");
  }

  @Override
  public Publisher<? extends Result> execute() {
    if (this.statementType == StatementType.DDL) {
      return this.client
          .executeDdl(
              this.config.getFullyQualifiedDatabaseName(),
              Collections.singletonList(this.sql),
              this.config.getDdlOperationTimeout(),
              this.config.getDdlOperationPollInterval())
          .map(operation -> new SpannerResult(Flux.empty(), Mono.just(0)));
    } else if (this.statementType == StatementType.DML && !this.transaction.isPartitionedDml()) {
      return this.client
          .executeBatchDml(this.session, this.transaction, this.sql,
              this.statementBindings.getBindings(),
              this.statementBindings.getTypes())
          .flatMapIterable(ExecuteBatchDmlResponse::getResultSetsList)
          .map(partialResultSet -> Math.toIntExact(partialResultSet.getStats().getRowCountExact()))
          .map(rowCount -> new SpannerResult(Flux.empty(), Mono.just(rowCount)));
    }

    Flux<Struct> structFlux = Flux.fromIterable(this.statementBindings.getBindings());
    return structFlux.flatMap(this::runStreamingSql);
  }

  private Mono<SpannerResult> runStreamingSql(Struct params) {
    Flux<PartialResultSet> resultSetFlux =
        this.client.executeStreamingSql(
            this.session, this.transaction, this.sql, params, this.statementBindings.getTypes());

    if (this.statementType == StatementType.SELECT) {
      PartialResultRowExtractor partialResultRowExtractor = new PartialResultRowExtractor();
      return resultSetFlux
          .flatMapIterable(partialResultRowExtractor, this.config.getPartialResultSetFetchSize())
          .transform(result -> Mono.just(new SpannerResult(result, Mono.just(0))))
          .next();
    } else {
      return resultSetFlux.last()
          .map(partialResultSet -> {
            long rowsUpdated =
                Math.max(
                    partialResultSet.getStats().getRowCountExact(),
                    partialResultSet.getStats().getRowCountLowerBound());
            return Math.toIntExact(rowsUpdated);
          })
          .map(rowCount -> new SpannerResult(Flux.empty(), Mono.just(rowCount)));
    }
  }
}
