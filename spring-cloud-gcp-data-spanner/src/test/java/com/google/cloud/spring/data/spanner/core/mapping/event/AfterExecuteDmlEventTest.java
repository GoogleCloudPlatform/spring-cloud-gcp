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

/** Tests for DML after-execution event. */
class AfterExecuteDmlEventTest {

  @Test
  void equalsHashcodeTest() {
    AfterExecuteDmlEvent afterExecuteDmlEventa1 = new AfterExecuteDmlEvent(Statement.of("a"), 1L);
    AfterExecuteDmlEvent afterExecuteDmlEventa1x = new AfterExecuteDmlEvent(Statement.of("a"), 1L);

    AfterExecuteDmlEvent afterExecuteDmlEventa2 = new AfterExecuteDmlEvent(Statement.of("a"), 2L);

    AfterExecuteDmlEvent afterExecuteDmlEventb1 = new AfterExecuteDmlEvent(Statement.of("b"), 1L);
    AfterExecuteDmlEvent afterExecuteDmlEventb2 = new AfterExecuteDmlEvent(Statement.of("b"), 2L);

    assertThat(afterExecuteDmlEventa1)
        .isNotNull()
        .isEqualTo(afterExecuteDmlEventa1)
        .isEqualTo(afterExecuteDmlEventa1x)
        .isNotEqualTo(afterExecuteDmlEventa2)
        .isNotEqualTo(afterExecuteDmlEventb2)
        .isNotEqualTo(new Object())
        .hasSameHashCodeAs(afterExecuteDmlEventa1)
        .hasSameHashCodeAs(afterExecuteDmlEventa1x);

    assertThat(afterExecuteDmlEventa1.hashCode())
        .isNotEqualTo(afterExecuteDmlEventa2.hashCode())
        .isNotEqualTo(afterExecuteDmlEventb2.hashCode());

    assertThat(afterExecuteDmlEventb1).isNotEqualTo(afterExecuteDmlEventb2);
    assertThat(afterExecuteDmlEventb1.hashCode()).isNotEqualTo(afterExecuteDmlEventb2.hashCode());
  }
}
