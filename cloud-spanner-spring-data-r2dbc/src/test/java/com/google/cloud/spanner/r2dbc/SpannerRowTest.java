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

import com.google.cloud.spanner.r2dbc.codecs.Codecs;
import com.google.cloud.spanner.r2dbc.codecs.DefaultCodecs;
import com.google.protobuf.Value;
import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * Test for {@link SpannerRow}.
 */
public class SpannerRowTest {

  private static final Codecs codecs = new DefaultCodecs();
  private final SpannerRowMetadata rowMetadata
      = new SpannerRowMetadata(ResultSetMetadata.getDefaultInstance());

  @Test
  public void testInvalidIdentifier() {
    SpannerRow row = new SpannerRow(
        new ArrayList<>(),
        rowMetadata);

    assertThatThrownBy(() -> row.get(true, String.class))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Identifier 'true' is not a valid identifier.");
  }

  @Test
  public void testOutOfBoundsIndex() {
    SpannerRow row = new SpannerRow(
        new ArrayList<>(),
        rowMetadata);

    assertThatThrownBy(() -> row.get(4, String.class))
        .isInstanceOf(IndexOutOfBoundsException.class);
  }

  @Test
  public void testInvalidColumnLabel() {
    SpannerRow row = new SpannerRow(
        new ArrayList<>(),
        rowMetadata);

    assertThatThrownBy(() -> row.get("foobar", String.class))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("The column name foobar does not exist for the Spanner row.");
  }

  @Test
  public void testIndexingIntoColumns() {
    SpannerRowMetadata rowMetadata =
        createRowMetadata(TypeCode.STRING, TypeCode.INT64, TypeCode.BOOL);
    List<Value> rawSpannerRow = createRawSpannerRow("Hello", 25L, true);
    SpannerRow row = new SpannerRow(rawSpannerRow, rowMetadata);

    assertThat(row.get("column_2", Boolean.class)).isEqualTo(true);
    assertThat(row.get("column_0", String.class)).isEqualTo("Hello");
    assertThat(row.get(1, Long.class)).isEqualTo(25L);
  }

  private static List<Value> createRawSpannerRow(Object... rowItems) {
    List<Value> listValues = new ArrayList<>();

    for (int i = 0; i < rowItems.length; i++) {
      listValues.add(codecs.encode(rowItems[i]));
    }
    return listValues;
  }

  private static SpannerRowMetadata createRowMetadata(TypeCode... types) {
    StructType.Builder structType = StructType.newBuilder();

    for (int i = 0; i < types.length; i++) {
      Field field =
          Field.newBuilder()
              .setName("column_" + i)
              .setType(Type.newBuilder().setCode(types[i]).build())
              .build();
      structType.addFields(field);
    }

    ResultSetMetadata resultSetMetadata =
        ResultSetMetadata.newBuilder()
            .setRowType(structType)
            .build();

    return new SpannerRowMetadata(resultSetMetadata);
  }
}
