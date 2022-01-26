/*
 * Copyright 2021-2021 Google LLC
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

import com.google.cloud.spanner.Type;
import com.google.cloud.spanner.Type.StructField;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SpannerClientLibraryColumnMetadataTest {

  static Arguments[] types() {
    return new Arguments[] {
        arguments(Type.bool(), Boolean.class),
        arguments(Type.int64(), Long.class),
        arguments(Type.float64(), Double.class),
        arguments(Type.string(), String.class),
        arguments(Type.bytes(), ByteBuffer.class),
        arguments(Type.timestamp(), LocalDateTime.class),
        arguments(Type.date(), LocalDate.class),
        arguments(Type.numeric(), BigDecimal.class),

        arguments(Type.array(Type.bool()), Boolean[].class),
        arguments(Type.array(Type.int64()), Long[].class),
        arguments(Type.array(Type.float64()), Double[].class),
        arguments(Type.array(Type.string()), String[].class),
        arguments(Type.array(Type.bytes()), ByteBuffer[].class),
        arguments(Type.array(Type.timestamp()), LocalDateTime[].class),
        arguments(Type.array(Type.date()), LocalDate[].class),
        arguments(Type.array(Type.numeric()), BigDecimal[].class),

        arguments(Type.array(Type.bool()), Boolean[].class)

    };
  }

  @ParameterizedTest
  @MethodSource("types")
  void simpleTypeMetadataAsExpected(Type spannerType, Class javaType) {
    StructField field = StructField.of("col1", spannerType);
    SpannerClientLibraryColumnMetadata meta = new SpannerClientLibraryColumnMetadata(field);

    assertThat(meta.getNativeTypeMetadata()).isEqualTo(spannerType);
    assertThat(meta.getJavaType()).isEqualTo(javaType);
  }

  @Test
  void getTypeNotSupported() {
    StructField field = StructField.of("col1", Type.string());
    SpannerClientLibraryColumnMetadata metadata = new SpannerClientLibraryColumnMetadata(field);
    assertThatThrownBy(() -> metadata.getType())
        .isInstanceOf(UnsupportedOperationException.class);
  }
}
