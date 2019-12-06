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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import io.r2dbc.spi.ColumnMetadata;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link SpannerRowMetadata}.
 */
public class SpannerRowMetadataTest {

  @Test
  public void testHandleMissingColumn() {
    SpannerRowMetadata metadata = new SpannerRowMetadata(ResultSetMetadata.newBuilder().build());
    assertThatThrownBy(() -> metadata.getColumnMetadata("columnName"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The column name columnName does not exist for the Spanner row.");
  }

  @Test
  public void testHandleOutOfBoundsIndex() {
    SpannerRowMetadata metadata = new SpannerRowMetadata(ResultSetMetadata.newBuilder().build());
    assertThatThrownBy(() -> metadata.getColumnMetadata(4))
        .isInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  public void testEmptyResultSetMetadata() {
    SpannerRowMetadata metadata = new SpannerRowMetadata(ResultSetMetadata.newBuilder().build());
    assertThat(metadata.getColumnMetadatas()).isEmpty();
    assertThat(metadata.getColumnNames()).isEmpty();
  }

  @Test
  public void testSpannerRowMetadataRetrieval() {
    ResultSetMetadata resultSetMetadata = buildResultSetMetadata(
        TypeCode.INT64,
        TypeCode.STRING,
        TypeCode.BOOL);

    SpannerRowMetadata metadata = new SpannerRowMetadata(resultSetMetadata);

    ColumnMetadata column1 = metadata.getColumnMetadata("column_1");
    assertThat(column1.getNativeTypeMetadata())
        .isEqualTo(Type.newBuilder().setCode(TypeCode.STRING).build());

    ColumnMetadata column0 = metadata.getColumnMetadata(0);
    assertThat(column0.getName()).isEqualTo("column_0");
    assertThat(column0.getNativeTypeMetadata()).isEqualTo(
        Type.newBuilder().setCode(TypeCode.INT64).build());
  }

  @Test
  public void getColumnNamesReturnsCorrectNamesWhenColumnsPresent() {
    ResultSetMetadata resultSetMetadata
        = buildResultSetMetadata(TypeCode.INT64, TypeCode.STRING, TypeCode.BOOL);
    SpannerRowMetadata metadata = new SpannerRowMetadata(resultSetMetadata);

    assertThat(metadata.getColumnNames()).containsExactly("column_0", "column_1", "column_2");
  }

  @Test
  public void getColumnNamesReturnsEmptyCollectionWhenNoColumns() {
    SpannerRowMetadata metadata = new SpannerRowMetadata(ResultSetMetadata.getDefaultInstance());

    assertThat(metadata.getColumnNames()).isEmpty();
  }

  private static ResultSetMetadata buildResultSetMetadata(TypeCode... types) {
    StructType.Builder structType = StructType.newBuilder();

    for (int i = 0; i < types.length; i++) {
      Field field =
          Field.newBuilder()
              .setName("column_" + i)
              .setType(Type.newBuilder().setCode(types[i]).build())
              .build();
      structType.addFields(field);
    }

    return ResultSetMetadata.newBuilder()
        .setRowType(structType)
        .build();
  }
}
