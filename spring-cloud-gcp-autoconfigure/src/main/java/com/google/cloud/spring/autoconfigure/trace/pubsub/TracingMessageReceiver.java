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

import static brave.Span.Kind.CONSUMER;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContextOrSamplingFlags;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;

final class TracingMessageReceiver implements MessageReceiver {
  private final PubSubTracing pubSubTracing;

  private final MessageReceiver delegate;

  private final String subscriptionName;

  TracingMessageReceiver(
      MessageReceiver delegate, PubSubTracing pubSubTracing, String subscriptionName) {
    this.pubSubTracing = pubSubTracing;
    this.delegate = delegate;
    this.subscriptionName = subscriptionName;
  }

  @Override
  public void receiveMessage(PubsubMessage pubsubMessage, AckReplyConsumer ackReplyConsumer) {
    // instrument message
    PubsubMessage.Builder messageBuilder = pubsubMessage.toBuilder();
    PubSubConsumerRequest request = new PubSubConsumerRequest(messageBuilder, subscriptionName);
    TraceContextOrSamplingFlags extracted =
        pubSubTracing.extractAndClearTraceIdHeaders(
            pubSubTracing.consumerExtractor, request, messageBuilder);

    Span consumerSpan =
        pubSubTracing.nextMessagingSpan(pubSubTracing.consumerSampler, request, extracted);
    Span listenerSpan = pubSubTracing.tracer.newChild(consumerSpan.context());

    if (!consumerSpan.isNoop()) {
      consumerSpan
          .name("next-message")
          .kind(CONSUMER); // TODO: do we need this consumer span at all?
      consumerSpan.tag(PubSubTags.PUBSUB_SUBSCRIPTION_TAG, subscriptionName);
      if (pubSubTracing.remoteServiceName != null) {
        consumerSpan.remoteServiceName(pubSubTracing.remoteServiceName);
      }

      // incur timestamp overhead only once
      long timestamp =
          pubSubTracing.tracing.clock(consumerSpan.context()).currentTimeMicroseconds();
      consumerSpan.start(timestamp);
      long consumerFinish = timestamp + 1L; // save a clock reading
      consumerSpan.finish(consumerFinish);

      // not using scoped span as we want to start with a pre-configured time
      listenerSpan.name("on-message").start(consumerFinish);
    }

    Tracer.SpanInScope ws = pubSubTracing.tracer.withSpanInScope(listenerSpan);
    Exception error = null;
    try {
      // pass instrumented message the actual receiver
      delegate.receiveMessage(messageBuilder.build(), ackReplyConsumer);
    } catch (Exception t) {
      error = t;
      throw t;
    } finally {
      if (error != null) {
        listenerSpan.error(error);
      }
      listenerSpan.finish();
      ws.close();
    }
  }
}
