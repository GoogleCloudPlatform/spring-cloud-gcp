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

package com.google.cloud.spanner.r2dbc.v2;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.Value;
import org.junit.jupiter.api.Test;

class SpannerClientLibraryRowTest {

  @Test
  void nonNullStructRequired() {
    assertThatThrownBy(() -> new SpannerClientLibraryRow(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("rowFields must not be null");
  }

  @Test
  void getMetadataUnsupported() {
    Struct struct = Struct.newBuilder().add(Value.string("some result")).build();
    SpannerClientLibraryRow row = new SpannerClientLibraryRow(struct);
    assertThatThrownBy(() -> row.getMetadata())
        .isInstanceOf(UnsupportedOperationException.class);
  }

}
