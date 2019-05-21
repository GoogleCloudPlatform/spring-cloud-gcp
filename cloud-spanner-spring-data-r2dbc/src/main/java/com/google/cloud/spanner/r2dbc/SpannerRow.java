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

import com.google.cloud.spanner.r2dbc.codecs.Codecs;
import com.google.cloud.spanner.r2dbc.codecs.DefaultCodecs;
import com.google.protobuf.Value;
import com.google.spanner.v1.Type;
import io.r2dbc.spi.Row;
import java.util.List;

/**
 * {@link Row} implementation for Cloud Spanner.
 *
 */
public class SpannerRow implements Row {

  private static final Codecs codecs = new DefaultCodecs();

  private final List<Value> values;

  private final SpannerRowMetadata rowMetadata;

  /**
   * Builds a new Spanner row.
   *
   * @param values the list of values in each column.
   * @param rowMetadata the type information for each column.
   */
  public SpannerRow(List<Value> values, SpannerRowMetadata rowMetadata) {
    this.values = values;
    this.rowMetadata = rowMetadata;
  }

  @Override
  public <T> T get(Object identifier, Class<T> returnType) {
    int columnIndex = rowMetadata.getColumnIndex(identifier);

    Value spannerValue = values.get(columnIndex);
    Type spannerType = (Type) rowMetadata.getColumnMetadata(identifier).getNativeTypeMetadata();

    T decodedValue = codecs.decode(spannerValue, spannerType, returnType);
    return decodedValue;
  }

  public List<Value> getValues() {
    return this.values;
  }

  public SpannerRowMetadata getRowMetadata() {
    return this.rowMetadata;
  }
}
