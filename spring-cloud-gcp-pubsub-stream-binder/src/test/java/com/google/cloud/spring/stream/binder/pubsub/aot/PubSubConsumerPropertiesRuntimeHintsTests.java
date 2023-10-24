/*
 * Copyright 2023 Google LLC
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

package com.google.cloud.spring.stream.binder.pubsub.aot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.aot.hint.predicate.RuntimeHintsPredicates.reflection;

import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubConsumerProperties;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;

public class PubSubConsumerPropertiesRuntimeHintsTests {

  @Test
  void registerPubSubConsumerProperties() {
    RuntimeHints runtimeHints = new RuntimeHints();
    PubSubConsumerPropertiesRuntimeHints registrar = new PubSubConsumerPropertiesRuntimeHints();
    registrar.registerHints(runtimeHints, null);
    assertThat(runtimeHints).matches(reflection().onType(PubSubConsumerProperties.class));
    assertThat(runtimeHints)
        .matches(reflection().onType(PubSubConsumerProperties.DeadLetterPolicy.class));
  }
}
