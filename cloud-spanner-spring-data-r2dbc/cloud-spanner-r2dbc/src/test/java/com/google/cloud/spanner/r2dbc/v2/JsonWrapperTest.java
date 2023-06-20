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

import com.google.cloud.spanner.Value;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


class JsonWrapperTest {

  static Stream<JsonWrapper> values() {
    return Stream.of(JsonWrapper.of("json-string"), new JsonWrapper("json-string"));
  }

  @MethodSource("values")
  @ParameterizedTest
  void testGetUnderlyingString(JsonWrapper json) {
    assertThat(json).hasToString("json-string");
  }

  @MethodSource("values")
  @ParameterizedTest
  void testGetSpannerValue(JsonWrapper json) {
    assertThat(json.getJsonVal()).isInstanceOf(Value.class);
  }

  @Test
  void testEquals() {
    String jsonString = "a json string";
    JsonWrapper json1 = JsonWrapper.of(jsonString);
    JsonWrapper json2 = JsonWrapper.of("a json string");
    assertThat(json1).isEqualTo(json1).isNotEqualTo(null).isNotEqualTo(jsonString).isEqualTo(json2);
  }

  @Test
  void testHashCode() {
    JsonWrapper json1 =
        JsonWrapper.of("object with same underlying string should have same hash code.");
    JsonWrapper json2 =
        JsonWrapper.of("object with same underlying string should have same hash code.");
    assertThat(json1).hasSameHashCodeAs(json2);
  }
}
