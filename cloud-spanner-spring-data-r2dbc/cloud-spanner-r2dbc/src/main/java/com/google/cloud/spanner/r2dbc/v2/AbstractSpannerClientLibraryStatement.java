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

import com.google.cloud.spanner.r2dbc.statement.TypedNull;
import com.google.spanner.v1.ExecuteSqlRequest.QueryOptions;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import java.util.ArrayList;
import java.util.List;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Cloud Spanner base implementation of R2DBC SPI for query and DML statements.
 *
 * <p>Supports parameter binding.
 */
abstract class AbstractSpannerClientLibraryStatement implements Statement {

  protected final DatabaseClientReactiveAdapter clientLibraryAdapter;

  private com.google.cloud.spanner.Statement.Builder currentStatementBuilder;

  private boolean startedBinding = false;

  private List<com.google.cloud.spanner.Statement> statements;

  private final String query;

  private final QueryOptions queryOptions;

  /**
   * Creates a ready-to-run Cloud Spanner statement.
   *
   * @param clientLibraryAdapter client library implementation of core functionality
   * @param query query to run, with `@` placeholders expected as parameters.
   */
  public AbstractSpannerClientLibraryStatement(
      DatabaseClientReactiveAdapter clientLibraryAdapter, String query) {
    this.clientLibraryAdapter = clientLibraryAdapter;
    this.query = query;
    this.queryOptions = this.clientLibraryAdapter.getQueryOptions();

    this.currentStatementBuilder = createStatementBuilder(this.query, this.queryOptions);
  }

  private static com.google.cloud.spanner.Statement.Builder createStatementBuilder(
      String query, QueryOptions options) {

    com.google.cloud.spanner.Statement.Builder builder =
        com.google.cloud.spanner.Statement.newBuilder(query);
    if (options != null) {
      builder = builder.withQueryOptions(options);
    }
    return builder;
  }

  @Override
  public Statement add() {
    if (this.statements == null) {
      this.statements = new ArrayList<>();
    }

    this.statements.add(this.currentStatementBuilder.build());
    this.currentStatementBuilder = createStatementBuilder(this.query, this.queryOptions);
    this.startedBinding = false;

    return this;
  }

  @Override
  public Publisher<? extends Result> execute() {
    if (this.statements != null) {
      if (this.startedBinding) {
        this.statements.add(this.currentStatementBuilder.build());
        this.startedBinding = false;
        this.currentStatementBuilder = null;
      }
      return executeMultiple(this.statements);
    }
    return executeSingle(this.currentStatementBuilder.build());
  }

  protected abstract Mono<SpannerClientLibraryResult> executeSingle(
      com.google.cloud.spanner.Statement statement);

  protected abstract Flux<SpannerClientLibraryResult> executeMultiple(
      List<com.google.cloud.spanner.Statement> statements);

  @Override
  public Statement bind(int index, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement bind(String name, Object value) {
    ClientLibraryBinder.bind(this.currentStatementBuilder, name, value);
    this.startedBinding = true;
    return this;
  }

  @Override
  public Statement bindNull(int index, Class<?> type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement bindNull(String name, Class<?> type) {
    ClientLibraryBinder.bind(this.currentStatementBuilder, name, new TypedNull(type));
    return this;
  }

}
