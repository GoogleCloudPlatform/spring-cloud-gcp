package com.google.cloud.spring.autoconfigure.trace.pubsub;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContextOrSamplingFlags;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;

import static brave.Span.Kind.CONSUMER;

public class TracingMessageReceiver implements MessageReceiver {
	private final PubSubTracing pubSubTracing;

	private final MessageReceiver delegate;

	private final String subscriptionName;

	public TracingMessageReceiver(PubSubTracing pubSubTracing, MessageReceiver delegate, String subscriptionName) {
		this.pubSubTracing = pubSubTracing;
		this.delegate = delegate;
		this.subscriptionName = subscriptionName;
	}

	@Override
	public void receiveMessage(PubsubMessage pubsubMessage, AckReplyConsumer ackReplyConsumer) {
		// instrument message
		PubsubMessage.Builder messageBuilder = pubsubMessage.toBuilder();
		MessageConsumerRequest request = new MessageConsumerRequest(messageBuilder, subscriptionName);
		TraceContextOrSamplingFlags extracted =
				pubSubTracing.extractAndClearTraceIdHeaders(pubSubTracing.consumerExtractor, request, messageBuilder);

		Span consumerSpan = pubSubTracing.nextMessagingSpan(pubSubTracing.consumerSampler, request, extracted);
		Span listenerSpan = pubSubTracing.tracer.newChild(consumerSpan.context());

		if (!consumerSpan.isNoop()) {
			consumerSpan.name("next-message").kind(CONSUMER);
			if (pubSubTracing.remoteServiceName != null)
				consumerSpan.remoteServiceName(pubSubTracing.remoteServiceName);

			// incur timestamp overhead only once
			long timestamp = pubSubTracing.tracing.clock(consumerSpan.context()).currentTimeMicroseconds();
			consumerSpan.start(timestamp);
			long consumerFinish = timestamp + 1L; // save a clock reading
			consumerSpan.finish(consumerFinish);

			// not using scoped span as we want to start with a pre-configured time
			listenerSpan.name("on-message").start(consumerFinish);
		}

		Tracer.SpanInScope ws = pubSubTracing.tracer.withSpanInScope(listenerSpan);
		Throwable error = null;
		try {
			// pass instrumented message the actual receiver
			delegate.receiveMessage(messageBuilder.build(), ackReplyConsumer);
		}
		catch (Throwable t) {
			error = t;
			throw t;
		}
		finally {
			if (error != null) listenerSpan.error(error);
			listenerSpan.finish();
			ws.close();
		}

	}
}
