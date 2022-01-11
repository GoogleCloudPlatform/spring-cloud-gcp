/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;

/** Tests for {@link MapBuilder}. */
class MapBuilderTests {

  @Test
  void mapWithDistinctKeysBuildsAsExpected() {
    Map<String, String> result =
        new MapBuilder<String, String>()
            .put("a", "alpha")
            .put("b", "beta")
            .put("g", "gamma")
            .build();
    assertThat(result)
        .containsOnlyKeys("a", "b", "g")
        .containsEntry("a", "alpha")
        .containsEntry("b", "beta")
        .containsEntry("g", "gamma");
  }

  @Test
  void emptyMapIsEmpty() {
    Map<String, String> result = new MapBuilder<String, String>().build();
    assertThat(result).isEmpty();
  }

  @Test
  void mapWithNullKeyThrowsException() {
    MapBuilder<String, String> mb = new MapBuilder<>();
    assertThatThrownBy(() -> mb.put(null, "nope"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Map key cannot be null.");
  }

  @Test
  void mapWithNullValueThrowsException() {
    MapBuilder<String, String> mb = new MapBuilder<>();
    assertThatThrownBy(() -> mb.put("nope", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Map value cannot be null.");
  }

  @Test
  void mapWithDuplicateKeysThrowsException() {
    MapBuilder<String, String> mb = new MapBuilder<>();
    mb.put("b", "beta");
    assertThatThrownBy(() -> mb.put("b", "vita"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Duplicate keys not allowed.");
  }
}
