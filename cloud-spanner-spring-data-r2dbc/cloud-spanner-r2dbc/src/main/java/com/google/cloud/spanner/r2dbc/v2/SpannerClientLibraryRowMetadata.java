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

import com.google.cloud.spanner.Type.StructField;
import com.google.spanner.v1.ResultSetMetadata;
import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * {@link RowMetadata} implementation for Cloud Spanner.
 */
class SpannerClientLibraryRowMetadata implements RowMetadata {

  private final List<ColumnMetadata> columnMetadatas;

  private final List<String> columnNames;

  /**
   * Mapping of column name to its integer index position in the row.
   */
  private final HashMap<String, Integer> columnNameIndex = new HashMap<>();

  /**
   * Extracts column metadata and initializes lookup data structures from the passed-in
   * {@link ResultSetMetadata}.
   *
   * @param structFields the fields of a row from Cloud Spanner.
   */
  public SpannerClientLibraryRowMetadata(List<StructField> structFields) {
    List<ColumnMetadata> tmpColumnMetadata = new ArrayList<>();
    List<String> tmpColumnNames = new ArrayList<>();

    for (int i = 0; i < structFields.size(); i++) {
      StructField field = structFields.get(i);
      ColumnMetadata metadata = new SpannerClientLibraryColumnMetadata(field);
      tmpColumnMetadata.add(metadata);
      tmpColumnNames.add(field.getName());
      String columnName = field.getName().toLowerCase();
      if (!this.columnNameIndex.containsKey(columnName)) {
        this.columnNameIndex.put(columnName, i);
      }
    }

    this.columnMetadatas = Collections.unmodifiableList(tmpColumnMetadata);
    this.columnNames = Collections.unmodifiableList(tmpColumnNames);
  }

  @Override
  public ColumnMetadata getColumnMetadata(int index) {
    return this.columnMetadatas.get(index);
  }

  @Override
  public ColumnMetadata getColumnMetadata(String identifier) {
    int index = getColumnIndexByName(identifier);
    return this.columnMetadatas.get(index);
  }

  @Override
  public List<? extends ColumnMetadata> getColumnMetadatas() {
    return this.columnMetadatas;
  }

  @Override
  public Collection<String> getColumnNames() {
    return this.columnNames;
  }

  protected int getColumnIndexByName(String name) {
    String identifier = name.toLowerCase();
    if (!this.columnNameIndex.containsKey(identifier)) {
      throw new IllegalArgumentException(
          "The column name " + name + " does not exist for the Spanner row. "
              + "Available columns: " + this.columnNameIndex.keySet());
    }

    return this.columnNameIndex.get(identifier);
  }
}
