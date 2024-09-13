/*
 * Copyright 2024 Google LLC
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

package com.google.cloud.spring.autoconfigure.core;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.aot.hint.predicate.RuntimeHintsPredicates.reflection;

import com.google.cloud.spring.core.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;

class CredentialsRuntimeHintsTest {
  @Test
  void registerCredentials() {
    RuntimeHints runtimeHints = new RuntimeHints();
    CredentialsRuntimeHints registrar = new CredentialsRuntimeHints();
    registrar.registerHints(runtimeHints, null);
    assertThat(runtimeHints).matches(reflection().onType(Credentials.class));
  }
}
