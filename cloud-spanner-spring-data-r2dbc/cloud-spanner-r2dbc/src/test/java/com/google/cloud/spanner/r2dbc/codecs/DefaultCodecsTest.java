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

package com.google.cloud.spanner.r2dbc.codecs;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Value;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test for {@link DefaultCodecs}.
 */
@RunWith(Parameterized.class)
public class DefaultCodecsTest {

  private Codecs codecs = new DefaultCodecs();

  /**
   * Prepare parameters for parametrized test.
   */
  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {new Boolean[]{true, false, true, null}, Boolean[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.BOOL).build()).build()},
        {new ByteBuffer[]{ByteBuffer.wrap("ab".getBytes()), ByteBuffer.wrap("cd".getBytes()), null},
            ByteBuffer[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.BYTES).build()).build()},
        {new LocalDate[]{LocalDate.of(800, 12, 31), LocalDate.of(2019, 1, 1), null},
            LocalDate[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.DATE).build()).build()},
        {new Double[]{2.0d, 3.0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN,
            null}, Double[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.FLOAT64).build()).build()},
        {new Long[]{2L, 1003L, null}, Long[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.INT64).build()).build()},
        {new String[]{"abc", "def", null}, String[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(Type.newBuilder().setCode(TypeCode.STRING).build()).build()},
        {new LocalDateTime[]{LocalDateTime.parse("2007-12-03T10:15:30"),
            LocalDateTime.parse("1999-06-05T10:12:51"), null},
            LocalDateTime[].class,
            Type.newBuilder().setCode(TypeCode.ARRAY)
                .setArrayElementType(
                    Type.newBuilder().setCode(TypeCode.TIMESTAMP).build()).build()},

        {true, Boolean.class, Type.newBuilder().setCode(TypeCode.BOOL).build()},
        {false, Boolean.class, Type.newBuilder().setCode(TypeCode.BOOL).build()},
        {ByteBuffer.wrap("ab".getBytes()), ByteBuffer.class,
            Type.newBuilder().setCode(TypeCode.BYTES).build()},
        {LocalDate.of(1992, 12, 31), LocalDate.class,
            Type.newBuilder().setCode(TypeCode.DATE).build()},
        {2.0d, Double.class, Type.newBuilder().setCode(TypeCode.FLOAT64).build()},
        {12345L, Long.class, Type.newBuilder().setCode(TypeCode.INT64).build()},
        {LocalDateTime.parse("1999-06-05T10:12:51"), LocalDateTime.class,
            Type.newBuilder().setCode(TypeCode.TIMESTAMP).build()},
        {"abc", String.class, Type.newBuilder().setCode(TypeCode.STRING).build()},
    });
  }

  @Parameter
  public Object val;

  @Parameter(1)
  public Class<?> type;

  @Parameter(2)
  public Type valueType;

  @Test
  public void codecsTest() {
    Value value = this.codecs.encode(this.val);
    Value nullValue = this.codecs.encode(null);

    assertThat(this.codecs.decode(value, this.valueType, this.type)).isEqualTo(this.val);

    assertThat(this.codecs.decode(nullValue, this.valueType, this.type)).isNull();
  }

}
