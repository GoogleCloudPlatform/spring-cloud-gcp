package com.google.cloud.spring.autoconfigure.trace.pubsub;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.messaging.MessagingRequest;
import brave.messaging.MessagingTracing;
import brave.propagation.Propagation;
import brave.propagation.TraceContext.Extractor;
import brave.propagation.TraceContext.Injector;
import brave.propagation.TraceContextOrSamplingFlags;
import brave.sampler.SamplerFunction;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.PublisherInterface;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;

/**
 * Factory for Trace instrumented Spring Cloud GCP Pub/Sub classes.
 */
public final class PubSubTracing {

	final Tracing tracing;

	final Tracer tracer;

	final Extractor<MessageProducerRequest> producerExtractor;

	final Extractor<MessageConsumerRequest> consumerExtractor;

	final Injector<MessageProducerRequest> producerInjector;

	final Injector<MessageConsumerRequest> consumerInjector;

	final Set<String> traceIdHeaders;

	final TraceContextOrSamplingFlags emptyExtraction;

	final SamplerFunction<MessagingRequest> producerSampler, consumerSampler;

	final String remoteServiceName;

	final boolean singleRootSpanOnReceiveBatch;

	PubSubTracing(Builder builder) { // intentionally hidden constructor
		this.tracing = builder.messagingTracing.tracing();
		this.tracer = tracing.tracer();
		MessagingTracing messagingTracing = builder.messagingTracing;
		Propagation<String> propagation = tracing.propagation();
		this.producerExtractor = propagation.extractor(MessageProducerRequest.GETTER);
		this.consumerExtractor = propagation.extractor(MessageConsumerRequest.GETTER);
		this.producerInjector = propagation.injector(MessageProducerRequest.SETTER);
		this.consumerInjector = propagation.injector(MessageConsumerRequest.SETTER);
		this.producerSampler = messagingTracing.producerSampler();
		this.consumerSampler = messagingTracing.consumerSampler();
		this.remoteServiceName = builder.remoteServiceName;
		this.singleRootSpanOnReceiveBatch = builder.singleRootSpanOnReceiveBatch;

		this.traceIdHeaders = new LinkedHashSet<>(propagation.keys());

		// When baggage or similar is in use, the result != TraceContextOrSamplingFlags.EMPTY
		this.emptyExtraction = propagation.extractor((c, k) -> null).extract(Boolean.TRUE);
	}

	public static PubSubTracing create(MessagingTracing messagingTracing) {
		return newBuilder(messagingTracing).build();
	}

	public static Builder newBuilder(MessagingTracing messagingTracing) {
		return new Builder(messagingTracing);
	}

	/** Creates an instrumented {@linkplain PublisherFactory} */
	// TODO: move it out as it depends on Spring
	public TracingPublisherFactory newPublisherFactory(PublisherFactory publisherFactory) {
		return new TracingPublisherFactory(this, publisherFactory);
	}

	/** Creates an instrumented {@linkplain SubscriberFactory} */
	// TODO: move it out as it depends on Spring
	public TracingSubscriberFactory newSubscriber(SubscriberFactory subscriberFactory) {
		return new TracingSubscriberFactory(this, subscriberFactory);
	}

	/** Creates an instrumented {@linkplain PublisherInterface}. */
	public TracingPublisher publisher(PublisherInterface publisher, String topic) {
		return new TracingPublisher(publisher, this, topic);
	}

	/** Creates an instrumented {@linkplain SubscriberStub} for use in message pulling scenario. */
	public TracingSubscriberStub subscriberStub(SubscriberStub subscriberStub) {
		return new TracingSubscriberStub(subscriberStub, this);
	}

	/** Creates an instrumented {@linkplain MessageReceiver} for use in message listening scenario. */
	public TracingMessageReceiver messageReceiver(MessageReceiver messageReceiver, String subscriptionName) {
		return new TracingMessageReceiver(messageReceiver, this, subscriptionName);
	}

	/** Creates a potentially noop remote span representing this request */
	Span nextMessagingSpan(
			SamplerFunction<MessagingRequest> sampler,
			MessagingRequest request,
			TraceContextOrSamplingFlags extracted
	) {
		Boolean sampled = extracted.sampled();
		// only recreate the context if the messaging sampler made a decision
		if (sampled == null && (sampled = sampler.trySample(request)) != null) {
			extracted = extracted.sampled(sampled.booleanValue());
		}
		return tracer.nextSpan(extracted);
	}

	<R> TraceContextOrSamplingFlags extractAndClearTraceIdHeaders(
			Extractor<R> extractor, R request, PubsubMessage.Builder message
	) {
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
		for (String traceIDHeader : traceIdHeaders) message.removeAttributes(traceIDHeader);
	}

	PullResponse tracePullResponse(PullResponse delegate, String subscriptionName) {

		if (delegate.getReceivedMessagesCount() == 0 || tracing.isNoop())
			return delegate;

		Span[] batchSpan = null;
		if (singleRootSpanOnReceiveBatch) {
			batchSpan = new Span[1];
		}

		PullResponse.Builder wrappedPullResponseBuilder = delegate.toBuilder();
		for (int i = 0; i < delegate.getReceivedMessagesCount(); i++) {
			wrappedPullResponseBuilder.setReceivedMessages(i, traceReceivedMessage(delegate.getReceivedMessages(i), subscriptionName, batchSpan));
		}

		return wrappedPullResponseBuilder.build();
	}

	private ReceivedMessage traceReceivedMessage(ReceivedMessage receivedMessage, String subscriptionName, Span[] batchSpan) {
		PubsubMessage.Builder wrappedMessage = receivedMessage.getMessage().toBuilder();
		postProcessMessageForConsuming(wrappedMessage, subscriptionName, batchSpan);
		return receivedMessage.toBuilder()
				.setMessage(wrappedMessage.build())
				.build();
	}

	private void postProcessMessageForConsuming(PubsubMessage.Builder messageBuilder, String subscriptionName, Span[] batchSpan) {
		long timestamp = 0;
		MessageConsumerRequest request = new MessageConsumerRequest(messageBuilder, subscriptionName);
		TraceContextOrSamplingFlags extracted =
				extractAndClearTraceIdHeaders(consumerExtractor, request, messageBuilder);

		// If we extracted neither a trace context, nor request-scoped data (extra),
		// and sharing trace is enabled make or reuse a span for this topic
		if (extracted.equals(emptyExtraction) && singleRootSpanOnReceiveBatch) {
			Span span = batchSpan[0];
			if (span == null) {
				span = nextMessagingSpan(consumerSampler, request, extracted);
				if (!span.isNoop()) {
					setConsumerSpan(span);

					// incur timestamp overhead only once
					if (timestamp == 0L) {
						timestamp = tracing.clock(span.context()).currentTimeMicroseconds();
					}
					span.start(timestamp);
				}
				batchSpan[0] = span;
			}
			consumerInjector.inject(span.context(), request);
		} else { // we extracted request-scoped data, so cannot share a consumer span.
			Span span = nextMessagingSpan(consumerSampler, request, extracted);
			if (!span.isNoop()) {

				setConsumerSpan(span);

				// incur timestamp overhead only once
				if (timestamp == 0L) {
					timestamp = tracing.clock(span.context()).currentTimeMicroseconds();
				}

				span.start(timestamp).finish(timestamp); // span won't be shared by other records
			}
			consumerInjector.inject(span.context(), request);
		}

		if (batchSpan[0] != null) {
			batchSpan[0].finish(timestamp);
		}
	}

	private void setConsumerSpan(Span span) {
		span.name("pull").kind(Span.Kind.CONSUMER);
		if (remoteServiceName != null)
			span.remoteServiceName(remoteServiceName);
	}

	public static final class Builder {
		final MessagingTracing messagingTracing;

		String remoteServiceName = "pubsub";

		boolean singleRootSpanOnReceiveBatch = true;

		Builder(MessagingTracing messagingTracing) {
			if (messagingTracing == null)
				throw new NullPointerException("messagingTracing == null");
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
		 *
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