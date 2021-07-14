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

import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SpannerClientLibraryDdlStatement implements Statement {

  private String query;

  private DatabaseClientReactiveAdapter clientLibraryAdapter;

  /**
   * Creates a ready-to-run Cloud Spanner DDL statement.
   *
   * @param query query to execute; does not support placeholders
   * @param clientLibraryAdapter client library implementation of core functionality
   */
  public SpannerClientLibraryDdlStatement(
      String query,
      DatabaseClientReactiveAdapter clientLibraryAdapter) {
    this.query = query;
    this.clientLibraryAdapter = clientLibraryAdapter;
  }

  @Override
  public Statement add() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement bind(int index, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement bind(String name, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement bindNull(int index, Class<?> type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement bindNull(String name, Class<?> type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mono<? extends Result> execute() {
    return this.clientLibraryAdapter
        .runDdlStatement(this.query)
        .thenReturn(new SpannerClientLibraryResult(Flux.empty(), 0));
  }
}
