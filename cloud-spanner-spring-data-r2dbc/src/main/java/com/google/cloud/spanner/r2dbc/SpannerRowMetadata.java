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

import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.StructType.Field;
import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.RowMetadata;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link RowMetadata} implementation for Cloud Spanner.
 */
public class SpannerRowMetadata implements RowMetadata {

  private final List<ColumnMetadata> columnMetadatas;

  /**
   * Mapping of column names to its integer index position in the row.
   */
  private final HashMap<String, Integer> columnNameIndex;

  /**
   * Constructor.
   *
   * @param resultSetMetadata the row from Cloud Spanner.
   */
  public SpannerRowMetadata(ResultSetMetadata resultSetMetadata) {
    this.columnMetadatas = resultSetMetadata.getRowType().getFieldsList()
        .stream()
        .map(SpannerColumnMetadata::new)
        .collect(Collectors.toList());

    this.columnNameIndex = new HashMap<>();
    for (int i = 0; i < resultSetMetadata.getRowType().getFieldsCount(); i++) {
      Field currField = resultSetMetadata.getRowType().getFields(i);
      this.columnNameIndex.put(currField.getName(), i);
    }
  }

  @Override
  public ColumnMetadata getColumnMetadata(Object identifier) {
    int columnIndex = getColumnIndex(identifier);
    return columnMetadatas.get(columnIndex);
  }

  @Override
  public Iterable<? extends ColumnMetadata> getColumnMetadatas() {
    return Collections.unmodifiableList(columnMetadatas);
  }

  /**
   * Returns the column index of the value in a row for the given {@code identifier}.
   */
  int getColumnIndex(Object identifier) {
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
