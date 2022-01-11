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

package com.google.cloud.spring.data.spanner.core.mapping.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spanner.Statement;
import org.junit.jupiter.api.Test;

/** Tests for DML before-execution event. */
class BeforeExecuteDmlEventTest {

  @Test
  void equalsHashcodeTest() {
    BeforeExecuteDmlEvent beforeExecuteDmlEvent = new BeforeExecuteDmlEvent(Statement.of("a"));
    BeforeExecuteDmlEvent beforeExecuteDmlEvent1 = new BeforeExecuteDmlEvent(Statement.of("a"));

    BeforeExecuteDmlEvent beforeExecuteDmlEvent2 = new BeforeExecuteDmlEvent(Statement.of("b"));

    assertThat(beforeExecuteDmlEvent)
        .isNotNull()
        .isEqualTo(beforeExecuteDmlEvent)
        .isEqualTo(beforeExecuteDmlEvent1)
        .isNotEqualTo(new Object())
        .hasSameHashCodeAs(beforeExecuteDmlEvent)
        .hasSameHashCodeAs(beforeExecuteDmlEvent1);

    assertThat(beforeExecuteDmlEvent2).isNotEqualTo(beforeExecuteDmlEvent);
    assertThat(beforeExecuteDmlEvent2.hashCode()).isNotEqualTo(beforeExecuteDmlEvent.hashCode());
  }
}
