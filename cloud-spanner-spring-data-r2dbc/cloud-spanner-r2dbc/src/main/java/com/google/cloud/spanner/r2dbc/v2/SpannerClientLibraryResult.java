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
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.function.BiFunction;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SpannerClientLibraryResult implements Result {

  private final Flux<SpannerClientLibraryRow> resultRows;

  private final Mono<Integer> rowsUpdated;

  private RowMetadata rowMetadata;

  public SpannerClientLibraryResult(
      Flux<SpannerClientLibraryRow> resultRows, Mono<Integer> rowsUpdated) {
    this.resultRows = resultRows;
    this.rowsUpdated = rowsUpdated;
  }

  @Override
  public Publisher<Integer> getRowsUpdated() {
    return this.rowsUpdated;
  }

  @Override
  public <T> Publisher<T> map(BiFunction<Row, RowMetadata, ? extends T> mappingFunction) {
    if (this.resultRows == null) {
      return Flux.empty();
    }

    return this.resultRows.map(row -> {
      if (this.rowMetadata == null) {
        this.rowMetadata = row.generateMetadata();
      }
      return mappingFunction.apply(row, this.rowMetadata);
    });
  }
}
