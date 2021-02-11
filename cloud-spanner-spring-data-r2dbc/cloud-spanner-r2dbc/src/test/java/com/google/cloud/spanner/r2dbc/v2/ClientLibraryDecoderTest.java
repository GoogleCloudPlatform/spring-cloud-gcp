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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.Value;
import com.google.cloud.spanner.r2dbc.ConversionFailureException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test for {@link ClientLibraryDecoder}.
 */
class ClientLibraryDecoderTest {

  /**
   * Prepare parameters for parametrized test.
   */
  static Stream<Arguments> decodingWithConversion() {
    return Stream.of(
        arguments(Integer.class, 12345L, (Function<Object, Value>) (o) -> Value.int64((Long) o),
            12345, null),
        arguments(Integer.class, Integer.MAX_VALUE + 1L,
            (Function<Object, Value>) (o) -> Value.int64((Long) o), null,
            new ConversionFailureException("2147483648 is out of range for Integer")),
        arguments(Integer.class, Integer.MIN_VALUE - 1L,
            (Function<Object, Value>) (o) -> Value.int64((Long) o), null,
            new ConversionFailureException("-2147483649 is out of range for Integer"))
    );
  }

  static Stream<Arguments> data() {
    return Stream.of(
        // Arrays
        arguments(
            long[].class,
            new long[]{123456L, 45678L},
            (Function<Object, Value>) (o) -> Value.int64Array((long[]) o)),
        arguments(
            double[].class,
            new double[]{1.0d, 123.0d},
            (Function<Object, Value>) (o) -> Value.float64Array((double[]) o)),
        arguments(
            boolean[].class,
            new boolean[]{true, false},
            (Function<Object, Value>) (o) -> Value.boolArray((boolean[]) o)),
        arguments(Long.class, 12345L, (Function<Object, Value>) (o) -> Value.int64((Long) o)),
        arguments(
            List.class,
            Arrays.asList(123456L, 45678L),
            (Function<Object, Value>) (o) -> Value.int64Array((Iterable<Long>) o)),
        arguments(Double.class, 2.0d, (Function<Object, Value>) (o) -> Value.float64((Double) o)),
        arguments(
            List.class,
            Arrays.asList(1.0d, 123.0d),
            (Function<Object, Value>) (o) -> Value.float64Array((Iterable<Double>) o)),
        arguments(Boolean.class, true, (Function<Object, Value>) (o) -> Value.bool((Boolean) o)),
        arguments(Boolean.class, false, (Function<Object, Value>) (o) -> Value.bool((Boolean) o)),
        arguments(
            List.class,
            Arrays.asList(true, false),
            (Function<Object, Value>) (o) -> Value.boolArray((Iterable<Boolean>) o)),
        arguments(
            ByteArray.class,
            ByteArray.copyFrom("abc"),
            (Function<Object, Value>) (o) -> Value.bytes((ByteArray) o)),
        arguments(
            List.class,
            Arrays.asList(ByteArray.copyFrom("abc"), ByteArray.copyFrom("cba")),
            (Function<Object, Value>) (o) -> Value.bytesArray((Iterable<ByteArray>) o)),
        arguments(
            Date.class,
            Date.fromYearMonthDay(1992, 12, 31),
            (Function<Object, Value>) (o) -> Value.date((Date) o)),
        arguments(
            List.class,
            Arrays.asList(Date.fromYearMonthDay(1992, 12, 31), Date.fromYearMonthDay(1993, 11, 3)),
            (Function<Object, Value>) (o) -> Value.dateArray((Iterable<Date>) o)),
        arguments(String.class, "abc", (Function<Object, Value>) (o) -> Value.string((String) o)),
        arguments(
            List.class,
            Arrays.asList("abc", "cba"),
            (Function<Object, Value>) (o) -> Value.stringArray((Iterable<String>) o)),
        arguments(
            Timestamp.class,
            Timestamp.ofTimeMicroseconds(123456),
            (Function<Object, Value>) (o) -> Value.timestamp((Timestamp) o)),
        arguments(
            List.class,
            Arrays.asList(
                Timestamp.ofTimeMicroseconds(123456), Timestamp.ofTimeMicroseconds(654321)),
            (Function<Object, Value>) (o) -> Value.timestampArray((Iterable<Timestamp>) o)),
        arguments(
            BigDecimal.class,
            BigDecimal.TEN,
            (Function<Object, Value>) (o) -> Value.numeric((BigDecimal) o)),
        arguments(
            List.class,
            Arrays.asList(
                BigDecimal.TEN, BigDecimal.ZERO),
            (Function<Object, Value>) (o) -> Value.numericArray((Iterable<BigDecimal>) o))
    );
  }

  /**
   * Validates that every supported type converts to expected value.
   */
  @ParameterizedTest
  @MethodSource("data")
  void codecsTest(Class<?> type, Object value, Function<Object, Value> valueBuilder) {
    codecsTest(type, value, valueBuilder, null, null);
  }

  @ParameterizedTest
  @MethodSource("decodingWithConversion")
  void codecsTest(Class<?> type, Object value, Function<Object, Value> valueBuilder,
      Object expectedVal, Exception exception) {
    Object expected = expectedVal == null ? value : expectedVal;
    Struct row =
        Struct.newBuilder().add(valueBuilder.apply(value)).add(valueBuilder.apply(null)).build();

    if (exception == null) {
      assertThat(ClientLibraryDecoder.decode(row, 0, type)).isEqualTo(expected);
      assertThat(ClientLibraryDecoder.decode(row, 1, type)).isNull();
    } else {
      assertThatThrownBy(() -> ClientLibraryDecoder.decode(row, 0, type))
          .isInstanceOf(exception.getClass()).hasMessage(exception.getMessage());
    }
  }
}
