/*
 * Copyright 2020-2020 Google LLC
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.spanner.Type;
import com.google.cloud.spanner.Type.StructField;
import io.r2dbc.spi.ColumnMetadata;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link SpannerClientLibraryRowMetadata}.
 */
class SpannerClientLibraryRowMetadataTest {

  @Test
  void testHandleMissingColumn() {
    SpannerClientLibraryRowMetadata metadata =
        new SpannerClientLibraryRowMetadata(Collections.emptyList());

    assertThatThrownBy(() -> metadata.getColumnMetadata("columnName"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The column name columnName does not exist for the Spanner row.");
  }

  @Test
  void testHandleOutOfBoundsIndex() {
    SpannerClientLibraryRowMetadata metadata =
        new SpannerClientLibraryRowMetadata(Collections.emptyList());

    assertThatThrownBy(() -> metadata.getColumnMetadata(4))
        .isInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  void testEmptyResultSetMetadata() {
    SpannerClientLibraryRowMetadata metadata =
        new SpannerClientLibraryRowMetadata(Collections.emptyList());

    assertThat(metadata.getColumnMetadatas()).isEmpty();
    assertThat(metadata.getColumnNames()).isEmpty();
  }

  @Test
  void testSpannerRowMetadataRetrieval() {

    List<StructField> structFields =
        buildResultSetMetadata(Type.int64(), Type.string(), Type.bool());
    SpannerClientLibraryRowMetadata metadata = new SpannerClientLibraryRowMetadata(structFields);

    ColumnMetadata column1 = metadata.getColumnMetadata("column_1");
    assertThat(column1.getNativeTypeMetadata())
        .isEqualTo(Type.string());

    ColumnMetadata column0 = metadata.getColumnMetadata(0);
    assertThat(column0.getName()).isEqualTo("column_0");
    assertThat(column0.getNativeTypeMetadata()).isEqualTo(Type.int64());
  }

  @Test
  void testSpannerRowMetadataCaseInsensitivity() {
    List<StructField> structFields =
        buildResultSetMetadata(Type.int64(), Type.string(), Type.bool());

    // When a get method contains several columns with same name, then the value of the first
    //matching column will be returned
    structFields.add(StructField.of("Column_1", Type.numeric()));
    SpannerClientLibraryRowMetadata metadata = new SpannerClientLibraryRowMetadata(structFields);

    assertThat(metadata.getColumnNames())
        .containsExactly("column_0", "column_1", "column_2", "Column_1");
    ColumnMetadata column1 = metadata.getColumnMetadata("column_1");
    assertThat(column1.getNativeTypeMetadata()).isEqualTo(Type.string());
  }

  @Test
  void getColumnNamesReturnsCorrectNamesWhenColumnsPresent() {
    SpannerClientLibraryRowMetadata metadata =
        new SpannerClientLibraryRowMetadata(
            buildResultSetMetadata(Type.int64(), Type.string(), Type.bool()));

    assertThat(metadata.getColumnNames()).containsExactly("column_0", "column_1", "column_2");
  }

  @Test
  void getColumnNamesReturnsEmptyCollectionWhenNoColumns() {
    SpannerClientLibraryRowMetadata metadata =
        new SpannerClientLibraryRowMetadata(Collections.EMPTY_LIST);

    assertThat(metadata.getColumnNames()).isEmpty();
  }

  private static List<StructField> buildResultSetMetadata(com.google.cloud.spanner.Type... types) {
    List<StructField> fields = new ArrayList<>();
    for (int i = 0; i < types.length; i++) {
      fields.add(StructField.of("column_" + i, types[i]));
    }
    return fields;
  }
}
