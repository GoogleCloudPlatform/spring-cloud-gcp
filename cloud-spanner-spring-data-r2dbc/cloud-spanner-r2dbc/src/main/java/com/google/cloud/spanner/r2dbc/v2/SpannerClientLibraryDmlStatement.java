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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Cloud Spanner implementation of R2DBC SPI for DML statements.
 */
public class SpannerClientLibraryDmlStatement extends AbstractSpannerClientLibraryStatement {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SpannerClientLibraryDmlStatement.class);

  /**
   * Creates a ready-to-run Cloud Spanner DML statement.
   * @param clientLibraryAdapter client library implementation of core functionality
   * @param query query to run
   */
  public SpannerClientLibraryDmlStatement(DatabaseClientReactiveAdapter clientLibraryAdapter,
      String query) {
    super(clientLibraryAdapter, query);
  }

  @Override
  public Publisher<? extends Result> execute() {
    return this.clientLibraryAdapter
        .runDmlStatement(this.statementBuilder.build())
        .transform(numRowsUpdatedMono -> Mono.just(
            new SpannerClientLibraryResult(Flux.empty(), numRowsUpdatedMono.map(this::longToInt))));
  }

  private int longToInt(Long numRows) {
    if (numRows > Integer.MAX_VALUE) {
      LOGGER.warn("Number of updated rows exceeds maximum integer value; actual rows updated = %s; "
          + "returning max int value", numRows);
      return Integer.MAX_VALUE;
    }
    return numRows.intValue();
  }

}
