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

import com.google.cloud.spanner.Statement;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Cloud Spanner implementation of R2DBC SPI for SELECT query statements.
 */
class SpannerClientLibraryStatement extends AbstractSpannerClientLibraryStatement {

  /**
   * Creates a ready-to-run Cloud Spanner statement.
   *
   * @param clientLibraryAdapter client library implementation of core functionality
   * @param query query to run, with `@` placeholders expected as parameters.
   */
  public SpannerClientLibraryStatement(
      DatabaseClientReactiveAdapter clientLibraryAdapter, String query) {
    super(clientLibraryAdapter, query);
  }

  @Override
  protected Mono<SpannerClientLibraryResult> executeSingle(Statement statement) {
    return this.clientLibraryAdapter
        .runSelectStatement(statement)
        .transform(
            rows ->
                Mono.deferContextual(
                    contextView ->
                        Mono.just(
                            new SpannerClientLibraryResult(rows.contextWrite(contextView), 0))))
        .single();
  }

  @Override
  protected Flux<SpannerClientLibraryResult> executeMultiple(List<Statement> statements) {
    return Flux.fromIterable(statements).flatMapSequential(statement -> executeSingle(statement));
  }
}
