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
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * Cloud Spanner implementation of R2DBC SPI for SELECT query statements.
 */
public class SpannerClientLibraryStatement extends AbstractSpannerClientLibraryStatement {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SpannerClientLibraryStatement.class);

  /**
   * Creates a ready-to-run Cloud Spanner statement.
   * @param clientLibraryAdapter client library implementation of core functionality
   * @param query query to run, with `@` placeholders expected as parameters.
   */
  public SpannerClientLibraryStatement(
      DatabaseClientReactiveAdapter clientLibraryAdapter, String query) {
    super(clientLibraryAdapter, query);
  }

  @Override
  public Publisher<? extends Result> execute() {
    return this.clientLibraryAdapter
        .runSelectStatement(this.statementBuilder.build())
        .transform(rows -> Mono.just(new SpannerClientLibraryResult(rows, Mono.empty())));
  }

}
