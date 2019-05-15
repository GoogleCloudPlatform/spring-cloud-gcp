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
import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Value;
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.StructType.Field;
import io.r2dbc.spi.Row;
import java.util.HashMap;
import java.util.List;

/**
 * {@link Row} implementation for Cloud Spanner.
 *
 */
public class SpannerRow implements Row {

  private static final Codecs codecs = new DefaultCodecs();

  private final List<Value> values;

  private final StructType rowMetadata;

  /** Mapping of column names to its integer index position in the row. */
  private final HashMap<String, Integer> columnNameIndex;

  /**
   * Builds a new Spanner row.
   *
   * @param values the list of values in each column.
   * @param rowMetadata the type information for each column.
   */
  public SpannerRow(List<Value> values, StructType rowMetadata) {
    this.values = values;
    this.rowMetadata = rowMetadata;

    this.columnNameIndex = new HashMap<>();
    for (int i = 0; i < rowMetadata.getFieldsCount(); i++) {
      Field currField = rowMetadata.getFields(i);
      this.columnNameIndex.put(currField.getName(), i);
    }
  }

  @VisibleForTesting
  List<Value> getValues() {
    return this.values;
  }

  @Override
  public <T> T get(Object identifier, Class<T> type) {

    int columnIndex = getColumnIndex(identifier);

    Value spannerValue = values.get(columnIndex);
    Field spannerValueMetadata = rowMetadata.getFields(columnIndex);

    T decodedValue = this.codecs.decode(spannerValue, spannerValueMetadata.getType(), type);
    return decodedValue;
  }

  private int getColumnIndex(Object identifier) {
    if (identifier instanceof Integer) {
      return (Integer) identifier;
    } else if (identifier instanceof String) {
      return getColumnIndexByName((String) identifier);
    } else {
      throw new IllegalArgumentException(
          String.format("Identifier '%s' is not a valid identifier. "
              + "Should either be an Integer index or a String column name.", identifier));
    }
  }

  private int getColumnIndexByName(String name) {
    if (!columnNameIndex.containsKey(name)) {
      throw new IllegalArgumentException(
          "The column name " + name + " does not exist for the Spanner row. "
              + "Available columns: " + columnNameIndex.keySet());
    }

    return columnNameIndex.get(name);
  }
}
