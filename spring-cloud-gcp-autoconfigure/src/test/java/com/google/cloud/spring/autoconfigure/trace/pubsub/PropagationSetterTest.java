/*
 * Copyright 2013-2020 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.trace.pubsub;

import static org.assertj.core.api.Assertions.assertThat;

import brave.propagation.Propagation;
import org.junit.jupiter.api.Test;

// Copied from:
// https://github.com/openzipkin/brave/blob/master/brave-tests/src/main/java/brave/test/propagation/PropagationSetterTest.java
// Brought this class internally as temporary fix
// for assertj related test errors to unblock other upgrades.
abstract class PropagationSetterTest<R> {
  protected final Propagation<String> propagation = Propagation.B3_STRING;

  protected abstract R request();

  protected abstract Propagation.Setter<R, String> setter();

  protected abstract Iterable<String> read(R request, String key);

  @Test
  void set() {
    setter().put(request(), "X-B3-TraceId", "48485a3953bb6124");

    assertThat(read(request(), "X-B3-TraceId")).containsExactly("48485a3953bb6124");
  }

  @Test
  void set128() {
    setter().put(request(), "X-B3-TraceId", "463ac35c9f6413ad48485a3953bb6124");

    assertThat(read(request(), "X-B3-TraceId")).containsExactly("463ac35c9f6413ad48485a3953bb6124");
  }

  @Test
  void setTwoKeys() {
    setter().put(request(), "X-B3-TraceId", "463ac35c9f6413ad48485a3953bb6124");
    setter().put(request(), "X-B3-SpanId", "48485a3953bb6124");

    assertThat(read(request(), "X-B3-TraceId")).containsExactly("463ac35c9f6413ad48485a3953bb6124");
    assertThat(read(request(), "X-B3-SpanId")).containsExactly("48485a3953bb6124");
  }

  @Test
  void reset() {
    setter().put(request(), "X-B3-TraceId", "48485a3953bb6124");
    setter().put(request(), "X-B3-TraceId", "463ac35c9f6413ad");

    assertThat(read(request(), "X-B3-TraceId")).containsExactly("463ac35c9f6413ad");
  }
}
