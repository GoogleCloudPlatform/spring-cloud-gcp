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

package com.google.cloud.spanner.r2dbc.it;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import java.util.function.BiFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SpannerQueryUtil {

  /**
   * Executes a read query and runs the provided {@code mappingFunction} on the elements returned.
   */
  static <T> List<T> executeReadQuery(
      Connection connection,
      String sql,
      BiFunction<Row, RowMetadata, T> mappingFunction) {

    return Flux.from(connection.createStatement(sql).execute())
        .flatMap(spannerResult -> spannerResult.map(mappingFunction))
        .collectList()
        .block();
  }

  /**
   * Executes a DML query and returns the rows updated.
   */
  static int executeDmlQuery(Connection connection, String sql) {

    Mono.from(connection.beginTransaction()).block();
    int rowsUpdated = Mono.from(connection.createStatement(sql).execute())
        .flatMap(result -> Mono.from(result.getRowsUpdated()))
        .block();
    Mono.from(connection.commitTransaction()).block();

    return rowsUpdated;
  }
}
