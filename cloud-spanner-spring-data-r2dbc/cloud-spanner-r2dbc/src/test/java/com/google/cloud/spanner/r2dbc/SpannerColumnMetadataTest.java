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

package com.google.cloud.spanner.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link SpannerColumnMetadata}.
 */
class SpannerColumnMetadataTest {

  @Test
  void testEmptyFieldName() {
    SpannerColumnMetadata metadata = new SpannerColumnMetadata(Field.getDefaultInstance());
    assertThat(metadata.getName()).isEmpty();
  }

  @Test
  void testSpannerColumnCorrectMetadata() {
    Field rawColumnInfo =
        Field.newBuilder()
            .setName("firstColumn")
            .setType(Type.newBuilder().setCode(TypeCode.STRING))
            .build();

    SpannerColumnMetadata metadata = new SpannerColumnMetadata(rawColumnInfo);
    assertThat(metadata.getName()).isEqualTo("firstColumn");
    assertThat(metadata.getNativeTypeMetadata()).isEqualTo(rawColumnInfo.getType());
  }
}
