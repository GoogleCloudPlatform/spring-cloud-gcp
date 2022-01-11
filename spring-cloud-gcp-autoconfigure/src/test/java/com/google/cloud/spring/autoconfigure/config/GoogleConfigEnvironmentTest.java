/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.cloud.spring.autoconfigure.config.GoogleConfigEnvironment.Variable;
import java.util.Base64;
import org.junit.jupiter.api.Test;

/** Tests for the environment config. */
class GoogleConfigEnvironmentTest {
  @Test
  void testSetVariabeValue() {
    GoogleConfigEnvironment.Variable var = new Variable();
    String value = "v a l u e";
    String encodedString = Base64.getEncoder().encodeToString(value.getBytes());
    var.setValue(encodedString);
    assertThat(var.getValue()).isEqualTo(value);
  }

  @Test
  void testSetNullValue() {
    GoogleConfigEnvironment googleConfigEnvironment = mock(GoogleConfigEnvironment.class);
    googleConfigEnvironment.setVariables(null);
    assertThat(googleConfigEnvironment.getVariables()).isEmpty();
  }
}
