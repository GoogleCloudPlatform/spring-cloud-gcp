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

import brave.Span;
import brave.SpanCustomizer;
import brave.Tracer;
import brave.Tracing;
import brave.messaging.MessagingRequest;
import brave.messaging.MessagingTracing;
import brave.propagation.Propagation;
import brave.propagation.Propagation.Getter;
import brave.propagation.TraceContext.Extractor;
import brave.propagation.TraceContext.Injector;
import brave.propagation.TraceContextOrSamplingFlags;
import brave.sampler.SamplerFunction;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;
import java.util.LinkedHashSet;
import java.util.Set;

/** Use this class to decorate your Pub/Sub publisher / subscriber and enable Tracing. */
final class PubSubTracing {
  /** Used for local message processors in {@link PubSubTracing#nextSpan(PubsubMessage.Builder)}. */
  static final Getter<PubsubMessage.Builder, String> GETTER =
      new Getter<PubsubMessage.Builder, String>() {
        @Override
        public String get(PubsubMessage.Builder request, String key) {
          return request.getAttributesOrDefault(key, null);
        }

        @Override
        public String toString() {
          return "PubsubMessage.Builder::getAttributesOrThrow";
        }
      };

  final Tracing tracing;

  final Tracer tracer;

  final Extractor<PubSubProducerRequest> producerExtractor;

  final Extractor<PubSubConsumerRequest> consumerExtractor;

  final Extractor<PubsubMessage.Builder> processorExtractor;

  final Injector<PubSubProducerRequest> producerInjector;

  final Injector<PubSubConsumerRequest> consumerInjector;

  final Set<String> traceIdHeaders;

  final TraceContextOrSamplingFlags emptyExtraction;

  final SamplerFunction<MessagingRequest> producerSampler;

  final SamplerFunction<MessagingRequest> consumerSampler;

  final String remoteServiceName;

  final boolean singleRootSpanOnReceiveBatch;

  PubSubTracing(Builder builder) { // intentionally hidden constructor
    this.tracing = builder.messagingTracing.tracing();
    this.tracer = tracing.tracer();
    MessagingTracing messagingTracing = builder.messagingTracing;
    Propagation<String> propagation = tracing.propagation();
    this.producerExtractor = propagation.extractor(PubSubProducerRequest.GETTER);
    this.consumerExtractor = propagation.extractor(PubSubConsumerRequest.GETTER);
    this.processorExtractor = propagation.extractor(GETTER);
    this.producerInjector = propagation.injector(PubSubProducerRequest.SETTER);
    this.consumerInjector = propagation.injector(PubSubConsumerRequest.SETTER);
    this.producerSampler = messagingTracing.producerSampler();
    this.consumerSampler = messagingTracing.consumerSampler();
    this.remoteServiceName = builder.remoteServiceName;
    this.singleRootSpanOnReceiveBatch = builder.singleRootSpanOnReceiveBatch;

    this.traceIdHeaders = new LinkedHashSet<>(propagation.keys());

    // When baggage or similar is in use, the result != TraceContextOrSamplingFlags.EMPTY
    this.emptyExtraction = propagation.extractor((c, k) -> null).extract(Boolean.TRUE);
  }

  public static PubSubTracing create(Tracing tracing) {
    return newBuilder(tracing).build();
  }

  public static PubSubTracing create(MessagingTracing messagingTracing) {
    return newBuilder(messagingTracing).build();
  }

  public static PubSubTracing.Builder newBuilder(Tracing tracing) {
    return newBuilder(MessagingTracing.create(tracing));
  }

  public static Builder newBuilder(MessagingTracing messagingTracing) {
    return new Builder(messagingTracing);
  }

  /** Creates an instrumented {@linkplain SubscriberStub} for use in message pulling scenario. */
  public TracingSubscriberStub subscriberStub(SubscriberStub subscriberStub) {
    return new TracingSubscriberStub(subscriberStub, this);
  }

  /** Creates an instrumented {@linkplain MessageReceiver} for use in message listening scenario. */
  public TracingMessageReceiver messageReceiver(
      MessageReceiver messageReceiver, String subscriptionName) {
    return new TracingMessageReceiver(messageReceiver, this, subscriptionName);
  }

  /**
   * Use this to create a span for processing the given message. Note: the result has no name and is
   * not started.
   *
   * <p>This creates a child from identifiers extracted from the message headers, or a new span if
   * one couldn't be extracted.
   */
  public Span nextSpan(PubsubMessage.Builder message) {
    // Even though the type is PubSubMessage.Builder, this is not a (remote) consumer span. Only
    // "pull/subscribe"
    // events create consumer spans. Since this is a processor span, we use the normal sampler.
    TraceContextOrSamplingFlags extracted =
        extractAndClearTraceIdHeaders(processorExtractor, message, message);
    Span result = tracer.nextSpan(extracted);
    if (extracted.context() == null && !result.isNoop()) {
      addTags(message, result);
    }
    return result;
  }

  /** Creates a potentially noop remote span representing this request. */
  Span nextMessagingSpan(
      SamplerFunction<MessagingRequest> sampler,
      MessagingRequest request,
      TraceContextOrSamplingFlags extracted) {
    Boolean sampled = extracted.sampled();
    // only recreate the context if the messaging sampler made a decision
    if (sampled == null) {
      sampled = sampler.trySample(request);
      if (sampled != null) {
        extracted = extracted.sampled(sampled.booleanValue());
      }
    }
    return tracer.nextSpan(extracted);
  }

  <R> TraceContextOrSamplingFlags extractAndClearTraceIdHeaders(
      Extractor<R> extractor, R request, PubsubMessage.Builder message) {
    TraceContextOrSamplingFlags extracted = extractor.extract(request);
    // Clear any propagation keys present in the headers
    if (extracted.samplingFlags() == null) { // then trace IDs were extracted
      clearTraceIdHeaders(message);
    }
    return extracted;
  }

  // We can't just skip clearing headers we use because we might inject B3 single, yet have stale B3
  // multi, or visa versa.
  void clearTraceIdHeaders(PubsubMessage.Builder message) {
    for (String traceIdHeader : traceIdHeaders) {
      message.removeAttributes(traceIdHeader);
    }
  }

  /** When an upstream context was not present, lookup keys are unlikely added. */
  static void addTags(PubsubMessage.Builder message, SpanCustomizer result) {
    if (message.getMessageId() != null && !"".equals(message.getMessageId())) {
      result.tag(PubSubTags.PUBSUB_MESSAGE_ID_TAG, message.getMessageId());
    }
  }

  PullResponse tracePullResponse(PullResponse delegate, String subscriptionName) {

    if (delegate.getReceivedMessagesCount() == 0 || tracing.isNoop()) {
      return delegate;
    }

    Span[] batchSpan = null;
    if (singleRootSpanOnReceiveBatch) {
      batchSpan = new Span[1];
    }

    PullResponse.Builder wrappedPullResponseBuilder = delegate.toBuilder();
    for (int i = 0; i < delegate.getReceivedMessagesCount(); i++) {
      wrappedPullResponseBuilder.setReceivedMessages(
          i, traceReceivedMessage(delegate.getReceivedMessages(i), subscriptionName, batchSpan));
    }

    return wrappedPullResponseBuilder.build();
  }

  private ReceivedMessage traceReceivedMessage(
      ReceivedMessage receivedMessage, String subscriptionName, Span[] batchSpan) {
    PubsubMessage.Builder wrappedMessage = receivedMessage.getMessage().toBuilder();
    postProcessMessageForConsuming(wrappedMessage, subscriptionName, batchSpan);
    return receivedMessage.toBuilder().setMessage(wrappedMessage.build()).build();
  }

  private void postProcessMessageForConsuming(
      PubsubMessage.Builder messageBuilder, String subscriptionName, Span[] batchSpan) {
    long timestamp = 0;
    PubSubConsumerRequest request = new PubSubConsumerRequest(messageBuilder, subscriptionName);
    TraceContextOrSamplingFlags extracted =
        extractAndClearTraceIdHeaders(consumerExtractor, request, messageBuilder);

    // If we extracted neither a trace context, nor request-scoped data (extra),
    // and sharing trace is enabled make or reuse a span for this topic
    if (extracted.equals(emptyExtraction) && singleRootSpanOnReceiveBatch) {
      Span span = batchSpan[0];
      if (span == null) {
        span = nextMessagingSpan(consumerSampler, request, extracted);
        if (!span.isNoop()) {
          setConsumerSpan(span, subscriptionName);

          timestamp = tracing.clock(span.context()).currentTimeMicroseconds();
          span.start(timestamp);
        }
        batchSpan[0] = span;
      }
      consumerInjector.inject(span.context(), request);
    } else { // we extracted request-scoped data, so cannot share a consumer span.
      Span span = nextMessagingSpan(consumerSampler, request, extracted);
      if (!span.isNoop()) {
        setConsumerSpan(span, subscriptionName);

        timestamp = tracing.clock(span.context()).currentTimeMicroseconds();
        span.start(timestamp);
        span.finish(timestamp); // span won't be shared by other messages
      }
      consumerInjector.inject(span.context(), request);
    }

    if (singleRootSpanOnReceiveBatch && batchSpan[0] != null) {
      batchSpan[0].finish(timestamp);
    }
  }

  private void setConsumerSpan(Span span, String subscriptionName) {
    span.tag(PubSubTags.PUBSUB_SUBSCRIPTION_TAG, subscriptionName);
    span.name("pull").kind(Span.Kind.CONSUMER);
    if (remoteServiceName != null) {
      span.remoteServiceName(remoteServiceName);
    }
  }

  public static final class Builder {
    final MessagingTracing messagingTracing;

    String remoteServiceName = "pubsub";

    boolean singleRootSpanOnReceiveBatch = true;

    Builder(MessagingTracing messagingTracing) {
      if (messagingTracing == null) {
        throw new NullPointerException("messagingTracing == null");
      }
      this.messagingTracing = messagingTracing;
    }

    /**
     * The remote service name that describes the broker in the dependency graph. Defaults to
     * "pubsub"
     */
    public Builder remoteServiceName(String remoteServiceName) {
      this.remoteServiceName = remoteServiceName;
      return this;
    }

    /**
     * Controls the sharing of a {@code pull} span for incoming spans with no trace context.
     *
     * <p>If true, all the spans received in a pull batch that do not have trace-context will be
     * added to a single new {@code pull} root span. Otherwise, a {@code pull} span will be created
     * for each such message.
     */
    public Builder singleRootSpanOnReceiveBatch(boolean singleRootSpanOnReceiveBatch) {
      this.singleRootSpanOnReceiveBatch = singleRootSpanOnReceiveBatch;
      return this;
    }

    public PubSubTracing build() {
      return new PubSubTracing(this);
    }
  }
}
