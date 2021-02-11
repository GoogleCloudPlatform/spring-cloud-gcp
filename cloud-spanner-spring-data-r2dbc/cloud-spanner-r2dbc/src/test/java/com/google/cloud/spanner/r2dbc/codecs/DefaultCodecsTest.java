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

package com.google.cloud.spanner.r2dbc.codecs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.protobuf.Value;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test for {@link DefaultCodecs}.
 */
class DefaultCodecsTest {

  private Codecs codecs = new DefaultCodecs();

  /**
   * Prepare parameters for parametrized test.
   */
  public static Stream<Arguments> data() {
    return Stream.of(
        arguments(new Boolean[]{true, false, true, null}, Boolean[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.BOOL).build()).build()),
        arguments(new ByteBuffer[]{
            ByteBuffer.wrap("ab".getBytes()), ByteBuffer.wrap("cd".getBytes()), null},
            ByteBuffer[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.BYTES).build()).build()),
        arguments(new LocalDate[]{
            LocalDate.of(800, 12, 31), LocalDate.of(2019, 1, 1), null},
            LocalDate[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.DATE).build()).build()),
        arguments(new Double[]{
            2.0d, 3.0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN, null},
            Double[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.FLOAT64).build()).build()),
        arguments(new Long[]{2L, 1003L, null}, Long[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.INT64).build()).build()),
        arguments(new String[]{"abc", "def", null}, String[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.STRING).build()).build()),
        arguments(new LocalDateTime[]{LocalDateTime.parse("2007-12-03T10:15:30"),
                LocalDateTime.parse("1999-06-05T10:12:51"), null},
            LocalDateTime[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(
                    Type.newBuilder().setCode(TypeCode.TIMESTAMP).build()).build()),

        arguments(true, Boolean.class, Type.newBuilder().setCode(TypeCode.BOOL).build()),
        arguments(false, Boolean.class, Type.newBuilder().setCode(TypeCode.BOOL).build()),
        arguments(ByteBuffer.wrap("ab".getBytes()), ByteBuffer.class,
            Type.newBuilder().setCode(TypeCode.BYTES).build()),
        arguments(LocalDate.of(1992, 12, 31), LocalDate.class,
            Type.newBuilder().setCode(TypeCode.DATE).build()),
        arguments(2.0d, Double.class, Type.newBuilder().setCode(TypeCode.FLOAT64).build()),
        arguments(12345L, Long.class, Type.newBuilder().setCode(TypeCode.INT64).build()),
        arguments(LocalDateTime.parse("1999-06-05T10:12:51"), LocalDateTime.class,
            Type.newBuilder().setCode(TypeCode.TIMESTAMP).build()),
        arguments("abc", String.class, Type.newBuilder().setCode(TypeCode.STRING).build())
    );
  }

  /** Validates that every supported type converts to expected value. */
  @ParameterizedTest
  @MethodSource("data")
  void codecsTest(Object val, Class<?> type, Type valueType) {
    Value value = this.codecs.encode(val);
    Value nullValue = this.codecs.encode(null);

    assertThat(this.codecs.decode(value, valueType, type)).isEqualTo(val);

    assertThat(this.codecs.decode(nullValue, valueType, type)).isNull();
  }

}
