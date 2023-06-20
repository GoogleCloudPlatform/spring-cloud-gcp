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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.google.cloud.spanner.r2dbc.ConversionFailureException;
import org.junit.jupiter.api.Test;

class SpannerClientLibraryConvertersTest {

  @Test
  void convertTest() {
    assertThat(
            SpannerClientLibraryConverters.convert(
                "{\"rating\":9,\"open\":true}", JsonWrapper.class))
        .isInstanceOf(JsonWrapper.class)
        .isEqualTo(JsonWrapper.of("{\"rating\":9,\"open\":true}"));

    assertThatThrownBy(() -> SpannerClientLibraryConverters.convert(1234L, JsonWrapper.class))
        .isInstanceOf(ConversionFailureException.class)
        .hasMessage(
            "Unable to convert class java.lang.Long "
                + "to class com.google.cloud.spanner.r2dbc.v2.JsonWrapper");

    assertThat(SpannerClientLibraryConverters.convert(12345L, Integer.class))
        .isInstanceOf(Integer.class)
        .isEqualTo(12345);

    assertThatThrownBy(
            () -> SpannerClientLibraryConverters.convert(Integer.MAX_VALUE + 1L, Integer.class))
        .isInstanceOf(ConversionFailureException.class)
        .hasMessage("2147483648 is out of range for Integer");
  }
}
