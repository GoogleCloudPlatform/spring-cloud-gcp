package com.google.cloud.spring.autoconfigure.trace.pubsub;

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
import com.google.cloud.pubsub.v1.PublisherInterface;
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

	final String[] traceIdHeaders;

	final SamplerFunction<MessagingRequest> producerSampler, consumerSampler;

	final String remoteServiceName;

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
		this.traceIdHeaders = propagation.keys().toArray(new String[0]);

	}

	public static PubSubTracing create(MessagingTracing messagingTracing) {
		return newBuilder(messagingTracing).build();
	}

	public static Builder newBuilder(MessagingTracing messagingTracing) {
		return new Builder(messagingTracing);
	}

	/** Creates an instrumented {@linkplain PublisherFactory} */
	public TracingPublisherFactory newPublisherFactory(PublisherFactory publisherFactory) {
		return new TracingPublisherFactory(this, publisherFactory);
	}

	public TracingPublisher newTracingPublisher(PublisherInterface publisher, String topic) {
		return new TracingPublisher(publisher, this, topic);
	}

	/** Creates an instrumented {@linkplain SubscriberFactory} */
	public TracingSubscriberFactory newSubscriber(SubscriberFactory subscriberFactory) {
		return new TracingSubscriberFactory(this, subscriberFactory);
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

		PullResponse.Builder wrappedPullResponseBuilder = delegate.toBuilder();
		for (int i = 0; i < delegate.getReceivedMessagesCount(); i++) {
			wrappedPullResponseBuilder.setReceivedMessages(i, traceReceivedMessage(delegate.getReceivedMessages(i), subscriptionName));
		}

		return wrappedPullResponseBuilder.build();
	}

	private ReceivedMessage traceReceivedMessage(ReceivedMessage receivedMessage, String subscriptionName) {
		PubsubMessage.Builder wrappedMessage = receivedMessage.getMessage().toBuilder();
		postProcessMessageForConsuming(wrappedMessage, subscriptionName);
		return receivedMessage.toBuilder()
				.setMessage(wrappedMessage.build())
				.build();
	}

	private void postProcessMessageForConsuming(PubsubMessage.Builder messageBuilder, String subscriptionName) {
		MessageConsumerRequest request = new MessageConsumerRequest(messageBuilder, subscriptionName);
		TraceContextOrSamplingFlags extracted =
				extractAndClearTraceIdHeaders(consumerExtractor, request, messageBuilder);

		Span span = nextMessagingSpan(consumerSampler, request, extracted);
		if (!span.isNoop()) {

			span.name("pull").kind(Span.Kind.CONSUMER);
			if (remoteServiceName != null)
				span.remoteServiceName(remoteServiceName);

			// incur timestamp overhead only once
			long timestamp = tracing.clock(span.context()).currentTimeMicroseconds();

			span.start(timestamp).finish(timestamp);
		}
		consumerInjector.inject(span.context(), request);
	}

	public static final class Builder {
		final MessagingTracing messagingTracing;

		String remoteServiceName = "pubsub";

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

		public PubSubTracing build() {
			return new PubSubTracing(this);
		}
	}

}