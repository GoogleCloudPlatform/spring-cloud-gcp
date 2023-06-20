/*
 * Copyright 2022-2022 Google LLC
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.Type;
import com.google.cloud.spanner.ValueBinder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class SpannerTypeTest {

  ValueBinder<Statement.Builder> mockBinder;

  @BeforeEach
  void setUp() {
    this.mockBinder = mock(ValueBinder.class);
  }

  @Test
  void isArrayOnlyReturnsTrueForArrayTypes() {
    assertTrue(SpannerType.of(Type.array(Type.string())).isArray());
    assertTrue(SpannerType.of(Type.array(Type.json())).isArray());
    assertTrue(SpannerType.of(Type.array(Type.array(Type.date()))).isArray());

    assertFalse(SpannerType.of(Type.date()).isArray());
    assertFalse(SpannerType.of(Type.float64()).isArray());
    assertFalse(SpannerType.of(Type.int64()).isArray());
    assertFalse(SpannerType.of(Type.json()).isArray());
    assertFalse(SpannerType.of(Type.numeric()).isArray());
    assertFalse(SpannerType.of(Type.string()).isArray());
    assertFalse(SpannerType.of(Type.struct()).isArray());
    assertFalse(SpannerType.of(Type.timestamp()).isArray());
  }

  @Test
  void getNameReturnsSpannerTypeName() {

    assertEquals("ARRAY<STRING>", SpannerType.of(Type.array(Type.string())).getName());
    assertEquals("ARRAY<INT64>", SpannerType.of(Type.array(Type.int64())).getName());

    assertEquals("DATE", SpannerType.of(Type.date()).getName());
    assertEquals("FLOAT64", SpannerType.of(Type.float64()).getName());
    assertEquals("INT64", SpannerType.of(Type.int64()).getName());
    assertEquals("JSON", SpannerType.of(Type.json()).getName());
    assertEquals("NUMERIC", SpannerType.of(Type.numeric()).getName());
    assertEquals("STRING", SpannerType.of(Type.string()).getName());
    assertEquals("STRUCT<>", SpannerType.of(Type.struct()).getName());
    assertEquals("TIMESTAMP", SpannerType.of(Type.timestamp()).getName());
  }

  @Test
  void getJavaTypeReturnsCorrectType() {

    assertEquals(Iterable.class, SpannerType.of(Type.array(Type.string())).getJavaType());
    assertEquals(Iterable.class, SpannerType.of(Type.array(Type.int64())).getJavaType());

    assertEquals(Date.class, SpannerType.of(Type.date()).getJavaType());
    assertEquals(Double.class, SpannerType.of(Type.float64()).getJavaType());
    assertEquals(Long.class, SpannerType.of(Type.int64()).getJavaType());
    assertEquals(BigDecimal.class, SpannerType.of(Type.numeric()).getJavaType());
    assertEquals(String.class, SpannerType.of(Type.string()).getJavaType());
    assertEquals(Timestamp.class, SpannerType.of(Type.timestamp()).getJavaType());
    assertEquals(Struct.class, SpannerType.of(Type.struct()).getJavaType());

    assertNull(SpannerType.of(Type.json()).getJavaType());
  }

  @Test
  void bindIterableWorksForBooleanArray() {
    List<Boolean> valueList = Arrays.asList(true, false, false);
    SpannerType.of(Type.array(Type.bool())).bindIterable(this.mockBinder, valueList);
    verify(this.mockBinder).toBoolArray(valueList);
  }

  @Test
  void bindIterableWorksForLongArray() {
    List<Long> valueList = Arrays.asList(12L, 24L, 36L);
    SpannerType.of(Type.array(Type.int64())).bindIterable(this.mockBinder, valueList);
    verify(this.mockBinder).toInt64Array(valueList);
  }

  @Test
  void bindIterableWorksForNumericArray() {
    List<BigDecimal> valueList = Arrays.asList(BigDecimal.TEN, BigDecimal.ONE);
    SpannerType.of(Type.array(Type.numeric())).bindIterable(this.mockBinder, valueList);
    verify(this.mockBinder).toNumericArray(valueList);
  }

  @Test
  void bindIterableWorksForDoubleArray() {
    List<Double> valueList = Arrays.asList(3.14, 2.71);
    SpannerType.of(Type.array(Type.float64())).bindIterable(this.mockBinder, valueList);
    verify(this.mockBinder).toFloat64Array(valueList);
  }

  @Test
  void bindIterableWorksForStringArray() {
    List<String> valueList = Arrays.asList("thing1", "thing2");
    SpannerType.of(Type.array(Type.string())).bindIterable(this.mockBinder, valueList);
    verify(this.mockBinder).toStringArray(valueList);
  }

  @Test
  void bindIterableWorksForBytesArray() {
    List<ByteArray> valueList = Arrays.asList(ByteArray.copyFrom("thing1"));
    SpannerType.of(Type.array(Type.bytes())).bindIterable(this.mockBinder, valueList);
    verify(this.mockBinder).toBytesArray(valueList);
  }

  @Test
  void bindIterableWorksForTimestampArray() {
    List<Timestamp> valueList = Arrays.asList(Timestamp.now());
    SpannerType.of(Type.array(Type.timestamp())).bindIterable(this.mockBinder, valueList);
    verify(this.mockBinder).toTimestampArray(valueList);
  }

  @Test
  void bindIterableWorksForDateArray() {
    List<Date> valueList = Arrays.asList(Date.fromYearMonthDay(2022, 02, 02));
    SpannerType.of(Type.array(Type.date())).bindIterable(this.mockBinder, valueList);
    verify(this.mockBinder).toDateArray(valueList);
  }

  @Test
  void bindIterableFailsForNonArrayTypes() {
    ValueBinder<Statement.Builder> mockBinder = mock(ValueBinder.class);

    SpannerType spannerType = SpannerType.of(Type.string());
    List<String> valueList = Arrays.asList("thing1", "thing2");

    assertThatThrownBy(() -> spannerType.bindIterable(this.mockBinder, valueList))
        .isInstanceOf(BindingFailureException.class)
        .hasMessage("Iterable cannot be bound to a non-array Spanner type.");

    verifyNoInteractions(this.mockBinder);
  }

  @Test
  void bindIterableFailsForUnsupportedTypes() {
    ValueBinder<Statement.Builder> mockBinder = mock(ValueBinder.class);

    SpannerType spannerType = SpannerType.of(Type.array(Type.struct()));

    List<String> valueList = new ArrayList<>();
    assertThatThrownBy(() -> spannerType.bindIterable(this.mockBinder, valueList))
        .isInstanceOf(BindingFailureException.class)
        .hasMessage("Array binder not found for type ARRAY<STRUCT<>>");

    verifyNoInteractions(this.mockBinder);
  }
}
