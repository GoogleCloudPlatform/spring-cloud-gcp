/*
 * Copyright 2022-2022 Google LLC
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

package com.google.cloud.spring.autoconfigure.trace.pubsub;

import static brave.Span.Kind.PRODUCER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

import brave.handler.MutableSpan;
import brave.propagation.CurrentTraceContext.Scope;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.jupiter.api.Test;

class TraceHelperTests extends PubSubTestBase {

  @Test
  void should_add_b3_headers_to_messages() {
    TraceHelper traceHelper = new TraceHelper(pubSubTracing);

    PubsubMessage instrumentedMessage =
        traceHelper.instrumentMessage(producerMessage.build(), TEST_TOPIC);

    assertThat(instrumentedMessage.getAttributesOrThrow("b3")).isNotNull();
    assertThat(instrumentedMessage.getAttributesCount()).isEqualTo(1);
  }

  @Test
  void should_add_b3_headers_when_other_headers_exist() {
    PubsubMessage.Builder message = producerMessage.putAttributes("tx-id", "1");

    TraceHelper traceHelper = new TraceHelper(pubSubTracing);
    PubsubMessage instrumentedMessage = traceHelper.instrumentMessage(message.build(), TEST_TOPIC);

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertThat(instrumentedMessage.getAttributesMap())
        .containsEntry("tx-id", "1")
        .containsEntry("b3", producerSpan.traceId() + "-" + producerSpan.id() + "-1");
  }

  @Test
  void should_inject_child_context() {

    TraceHelper traceHelper = new TraceHelper(pubSubTracing);
    PubsubMessage instrumentedMessage;
    try (Scope scope = currentTraceContext.newScope(parent)) {
      instrumentedMessage =
          traceHelper.instrumentMessage(producerMessage.build(), TEST_TOPIC);
    }

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertChildOf(producerSpan, parent);
    assertThat(instrumentedMessage.getAttributesMap())
        .isNotNull()
        .containsEntry("b3", producerSpan.traceId() + "-" + producerSpan.id() + "-1");
  }

  @Test
  void should_add_parent_trace_when_context_injected_on_headers() {
    PubsubMessage.Builder message = producerMessage.putAttributes("tx-id", "1");
    TraceHelper traceHelper = new TraceHelper(pubSubTracing);

    pubSubTracing.producerInjector.inject(parent, new PubSubProducerRequest(message, "myTopic"));

    PubsubMessage instrumentedMessage =
        traceHelper.instrumentMessage(message.build(), TEST_TOPIC);

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertChildOf(producerSpan, parent);
    assertThat(instrumentedMessage.getAttributesMap())
        .containsEntry("b3", producerSpan.traceId() + "-" + producerSpan.id() + "-1");
  }

  @Test
  void send_should_set_name() {
    TraceHelper traceHelper = new TraceHelper(pubSubTracing);

    PubsubMessage instrumentedMessage =
        traceHelper.instrumentMessage(producerMessage.build(), TEST_TOPIC);

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertThat(producerSpan.name()).isEqualTo("publish");
  }

  @Test
  void send_should_tag_topic() {
    TraceHelper traceHelper = new TraceHelper(pubSubTracing);

    PubsubMessage instrumentedMessage =
        traceHelper.instrumentMessage(producerMessage.build(), TEST_TOPIC);

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertThat(producerSpan.tags()).containsOnly(entry("pubsub.topic", TEST_TOPIC));
  }

  @Test
  void send_shouldnt_tag_null_topic() {
    TraceHelper traceHelper = new TraceHelper(pubSubTracing);

    PubsubMessage instrumentedMessage =
        traceHelper.instrumentMessage(producerMessage.build(), null);

    MutableSpan producerSpan = spans.get(0);
    assertThat(producerSpan.kind()).isEqualTo(PRODUCER);
    assertThat(producerSpan.tags()).isEmpty();
  }


}
