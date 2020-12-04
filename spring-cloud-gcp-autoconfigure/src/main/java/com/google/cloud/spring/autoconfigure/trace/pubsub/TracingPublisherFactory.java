package com.google.cloud.spring.autoconfigure.trace.pubsub;

import brave.Span;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.google.cloud.pubsub.v1.PublisherInterface;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.pubsub.v1.PubsubMessage;

import static brave.Span.Kind.PRODUCER;

final class TracingPublisherFactory implements PublisherFactory {
	private SpringPubSubTracing springPubSubTracing;

	private PublisherFactory publisherFactory;

	public TracingPublisherFactory(SpringPubSubTracing springPubSubTracing, PublisherFactory publisherFactory) {
		this.springPubSubTracing = springPubSubTracing;
		this.publisherFactory = publisherFactory;
	}

	@Override
	public PublisherInterface createPublisher(String topic) {
		PublisherInterface publisher = publisherFactory.createPublisher(topic);
		return message -> {
			System.out.println("tracing publisher");
			PubsubMessage tracedMessage = PubsubMessage.newBuilder(message).putAttributes("tracing-is-happening", "test").build();
			return publisher.publish(tracedMessage);
		};
	}

	public PubsubMessage postProcessMessageForPublishing(PubsubMessage message, String topic) {
		MessageProducerRequest request = new MessageProducerRequest(message, topic);


		TraceContext maybeParent = springPubSubTracing.tracing.currentTraceContext().get();
		// Unlike message consumers, we try current span before trying extraction. This is the proper
		// order because the span in scope should take precedence over a potentially stale header entry.
		//
		// NOTE: Brave instrumentation used properly does not result in stale header entries, as we
		// always clear message headers after reading.
		Span span;
		if (maybeParent == null) {
			TraceContextOrSamplingFlags extracted =
					springPubSubTracing.extractAndClearTraceIdHeaders(springPubSubTracing.producerExtractor, request, message.toBuilder());
			span = springPubSubTracing.nextMessagingSpan(springPubSubTracing.producerSampler, request, extracted);
		}
		else { // If we have a span in scope assume headers were cleared before
			span = springPubSubTracing.tracer.newChild(maybeParent);
		}

		if (!span.isNoop()) {
			span.kind(PRODUCER).name("publish");
			if (springPubSubTracing.remoteServiceName != null) span.remoteServiceName(springPubSubTracing.remoteServiceName);
			// incur timestamp overhead only once
			long timestamp = springPubSubTracing.tracing.clock(span.context()).currentTimeMicroseconds();
			span.start(timestamp).finish(timestamp);
		}

		springPubSubTracing.producerInjector.inject(span.context(), request);

		// TODO: return instrumented message?
		return message;
	}



}