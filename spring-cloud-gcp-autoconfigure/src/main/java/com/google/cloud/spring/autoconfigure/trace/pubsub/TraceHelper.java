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

import brave.Span;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.google.pubsub.v1.PubsubMessage;

class TraceHelper {

  private final PubSubTracing pubSubTracing;

  TraceHelper(PubSubTracing pubSubTracing) {
    this.pubSubTracing = pubSubTracing;
  }

  /**
   * Adds tracing headers to an outgoing Pub/Sub message.
   * Uses the current application trace context; falls back to original message header context
   * if not available.
   *
   * @param originalMessage message to instrument
   * @param topic destination topic, used as channel name and {@link PubSubTags#PUBSUB_TOPIC_TAG}.
   */
  PubsubMessage instrumentMessage(PubsubMessage originalMessage, String topic) {
    PubsubMessage.Builder messageBuilder = PubsubMessage.newBuilder(originalMessage);
    PubSubProducerRequest request = new PubSubProducerRequest(messageBuilder, topic);

    TraceContext maybeParent = pubSubTracing.tracing.currentTraceContext().get();

    Span span;
    if (maybeParent == null) {
      TraceContextOrSamplingFlags extracted =
          pubSubTracing.extractAndClearTraceIdHeaders(
              pubSubTracing.producerExtractor, request, messageBuilder);
      span = pubSubTracing.nextMessagingSpan(pubSubTracing.producerSampler, request, extracted);
    } else { // If we have a span in scope assume headers were cleared before
      span = pubSubTracing.tracer.newChild(maybeParent);
    }

    if (!span.isNoop()) {
      span.kind(PRODUCER).name("publish");
      if (topic != null) {
        span.tag(PubSubTags.PUBSUB_TOPIC_TAG, topic);
      }
      if (pubSubTracing.remoteServiceName != null) {
        span.remoteServiceName(pubSubTracing.remoteServiceName);
      }
      // incur timestamp overhead only once
      long timestamp = pubSubTracing.tracing.clock(span.context()).currentTimeMicroseconds();
      // the span is just an instant, since we don't yet track how long it takes to publish and
      // carry that forward
      // TODO: register a listener on the publish future
      span.start(timestamp).finish(timestamp);
    }

    // inject span context into the messageBuilder
    pubSubTracing.producerInjector.inject(span.context(), request);

    return messageBuilder.build();
  }

}
