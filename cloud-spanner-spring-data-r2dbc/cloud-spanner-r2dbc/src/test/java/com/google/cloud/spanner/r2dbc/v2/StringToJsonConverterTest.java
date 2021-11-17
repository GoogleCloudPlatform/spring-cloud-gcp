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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.google.cloud.spanner.r2dbc.ConversionFailureException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;


class StringToJsonConverterTest {

  @Test
  void canConvert() {
    StringToJsonConverter converter = new StringToJsonConverter();
    assertThat(converter.canConvert(Long.class, JsonWrapper.class)).isFalse();
    assertThat(converter.canConvert(String.class, Long.class)).isFalse();
    assertThat(converter.canConvert(String.class, JsonWrapper.class)).isTrue();
  }

  @Test
  void convert() {
    StringToJsonConverter converter = new StringToJsonConverter();

    AssertionsForClassTypes.assertThat(
            converter.convert(
                    "{\"rating\":9,\"open\":true}"))
            .isInstanceOf(JsonWrapper.class)
            .isEqualTo(JsonWrapper.of("{\"rating\":9,\"open\":true}"));

    assertThatThrownBy(() -> converter.convert(1234L))
            .isInstanceOf(ConversionFailureException.class)
            .hasMessage(
                    "Unable to convert class java.lang.Class "
                            + "to class com.google.cloud.spanner.r2dbc.v2.JsonWrapper");
  }
}
