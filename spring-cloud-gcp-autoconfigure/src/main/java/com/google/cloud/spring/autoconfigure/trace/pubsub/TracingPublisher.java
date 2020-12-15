package com.google.cloud.spring.autoconfigure.trace.pubsub;

import brave.Span;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.PublisherInterface;
import com.google.pubsub.v1.PubsubMessage;

import static brave.Span.Kind.PRODUCER;

public class TracingPublisher implements PublisherInterface {
	private final PublisherInterface delegate;

	private final PubSubTracing pubSubTracing;

	private final String topic;

	public TracingPublisher(PublisherInterface delegate, PubSubTracing pubSubTracing, String topic) {
		this.delegate = delegate;
		this.pubSubTracing = pubSubTracing;
		this.topic = topic;
	}

	@Override
	public ApiFuture<String> publish(PubsubMessage message) {
		System.out.println("tracing publisher");
		PubsubMessage.Builder builder = message.toBuilder();
		postProcessMessageForPublishing(builder);
		PubsubMessage tracedMessage = builder.putAttributes("tracing-is-happening", "test").build();
		return delegate.publish(tracedMessage);
	}

	private void postProcessMessageForPublishing(PubsubMessage.Builder messageBuilder) {
		MessageProducerRequest request = new MessageProducerRequest(messageBuilder, topic);

		TraceContext maybeParent = pubSubTracing.tracing.currentTraceContext().get();
		// Unlike message consumers, we try current span before trying extraction. This is the proper
		// order because the span in scope should take precedence over a potentially stale header entry.
		//
		// NOTE: Brave instrumentation used properly does not result in stale header entries, as we
		// always clear message headers after reading.
		Span span;
		if (maybeParent == null) {
			TraceContextOrSamplingFlags extracted =
					pubSubTracing.extractAndClearTraceIdHeaders(pubSubTracing.producerExtractor, request, messageBuilder);
			span = pubSubTracing.nextMessagingSpan(pubSubTracing.producerSampler, request, extracted);
		}
		else { // If we have a span in scope assume headers were cleared before
			span = pubSubTracing.tracer.newChild(maybeParent);
		}

		if (!span.isNoop()) {
			span.kind(PRODUCER).name("publish");
			if (pubSubTracing.remoteServiceName != null)
				span.remoteServiceName(pubSubTracing.remoteServiceName);
			// incur timestamp overhead only once
			long timestamp = pubSubTracing.tracing.clock(span.context()).currentTimeMicroseconds();
			// the span is just an instant, since we can't track how long it takes to publish and carry that forward
			span.start(timestamp).finish(timestamp);
		}

		// inject span context into the messageBuilder
		pubSubTracing.producerInjector.inject(span.context(), request);
	}
}
