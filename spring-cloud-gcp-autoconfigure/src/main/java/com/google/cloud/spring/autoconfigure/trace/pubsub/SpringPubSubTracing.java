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
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.pubsub.v1.PubsubMessage;

/**
 * Factory for Trace instrumented Spring Cloud GCP Pub/Sub classes.
 */
public final class SpringPubSubTracing {

	public static SpringPubSubTracing create(MessagingTracing messagingTracing) {
		return newBuilder(messagingTracing).build();
	}

	public static Builder newBuilder(MessagingTracing messagingTracing) {
		return new Builder(messagingTracing);
	}

	public static final class Builder {
		final MessagingTracing messagingTracing;
		String remoteServiceName = "pubsub";

		Builder(MessagingTracing messagingTracing) {
			if (messagingTracing == null) throw new NullPointerException("messagingTracing == null");
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

		public SpringPubSubTracing build() {
			return new SpringPubSubTracing(this);
		}
	}

	final Tracing tracing;
	final Tracer tracer;
	final Extractor<MessageProducerRequest> producerExtractor;
	final Extractor<MessageConsumerRequest> consumerExtractor;
	final Injector<MessageProducerRequest> producerInjector;
	final Injector<MessageConsumerRequest> consumerInjector;
	final String[] traceIdHeaders;
	final SamplerFunction<MessagingRequest> producerSampler, consumerSampler;
	final String remoteServiceName;

	SpringPubSubTracing(Builder builder) { // intentionally hidden constructor
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

	/** Creates an instrumented {@linkplain PublisherFactory} */
	public TracingPublisherFactory newPublisher(PublisherFactory publisherFactory) {
		return new TracingPublisherFactory(this, publisherFactory);
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
}