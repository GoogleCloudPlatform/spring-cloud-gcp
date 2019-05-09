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

import com.google.common.annotations.VisibleForTesting;
import com.google.spanner.v1.ResultSetMetadata;
import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.RowMetadata;

/**
 * {@link RowMetadata} implementation for Cloud Spanner.
 */
public class SpannerRowMetadata implements RowMetadata {

  private final ResultSetMetadata rowMetadata;

  /**
   * Constructor.
   *
   * @param resultSetMetadata the row from Cloud Spanner.
   */
  public SpannerRowMetadata(ResultSetMetadata resultSetMetadata) {
    this.rowMetadata = resultSetMetadata;
  }

  @VisibleForTesting
  ResultSetMetadata getRowMetadata() {
    return this.rowMetadata;
  }

  @Override
  public ColumnMetadata getColumnMetadata(Object identifier) {
    // TODO
    return new SpannerColumnMetadata();
  }

  @Override
  public Iterable<? extends ColumnMetadata> getColumnMetadatas() {
    // TODO
    return null;
  }
}
