/*
 * Copyright 2017-2020 the original author or authors.
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

import brave.Span;
import brave.propagation.B3SingleFormat;
import brave.propagation.CurrentTraceContext.Scope;
import org.junit.jupiter.api.Test;

class PubSubTracingTest extends PubSubTestBase {
  @Test
  void nextSpan_prefers_b3_header() {
    consumerMessage.putAttributes("b3", B3SingleFormat.writeB3SingleFormat(incoming));

    Span child;
    try (Scope ws = tracing.currentTraceContext().newScope(parent)) {
      child = pubSubTracing.nextSpan(consumerMessage);
    }
    child.finish();

    assertThat(spans.get(0).id()).isEqualTo(child.context().spanIdString());
    assertChildOf(spans.get(0), incoming);
  }

  @Test
  void nextSpan_uses_current_context() {
    Span child;
    try (Scope ws = tracing.currentTraceContext().newScope(parent)) {
      child = pubSubTracing.nextSpan(consumerMessage);
    }
    child.finish();

    assertThat(spans.get(0).id()).isEqualTo(child.context().spanIdString());
    assertChildOf(spans.get(0), parent);
  }

  @Test
  void nextSpan_should_create_span_if_no_headers() {
    assertThat(pubSubTracing.nextSpan(consumerMessage)).isNotNull();
  }

  @Test
  void nextSpan_should_create_span_with_baggage() {
    addB3MultiHeaders(parent, consumerMessage);
    consumerMessage.putAttributes(BAGGAGE_FIELD_KEY, "user1");

    Span span = pubSubTracing.nextSpan(consumerMessage);
    assertThat(BAGGAGE_FIELD.getValue(span.context())).contains("user1");
  }

  @Test
  void nextSpan_should_clear_propagation_headers() {
    addB3MultiHeaders(parent, consumerMessage);

    pubSubTracing.nextSpan(consumerMessage);
    assertThat(consumerMessage.getAttributesMap()).isEmpty();
  }

  @Test
  void nextSpan_should_retain_baggage_headers() {
    consumerMessage.putAttributes(BAGGAGE_FIELD_KEY, "some-baggage");

    pubSubTracing.nextSpan(consumerMessage);
    assertThat(consumerMessage.getAttributesOrDefault(BAGGAGE_FIELD_KEY, null)).isNotNull();
  }

  @Test
  void nextSpan_should_not_clear_other_headers() {
    consumerMessage.putAttributes("foo", "bar");

    pubSubTracing.nextSpan(consumerMessage);
    assertThat(consumerMessage.getAttributesOrDefault("foo", null)).isNotNull();
  }
}
