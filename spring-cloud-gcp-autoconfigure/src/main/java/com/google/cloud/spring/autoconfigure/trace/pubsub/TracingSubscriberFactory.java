package com.google.cloud.spring.autoconfigure.trace.pubsub;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContextOrSamplingFlags;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PullRequest;

import static brave.Span.Kind.CONSUMER;

final class TracingSubscriberFactory implements SubscriberFactory {
	private SpringPubSubTracing springPubSubTracing;
	private SubscriberFactory subscriberFactory;

	public TracingSubscriberFactory(SpringPubSubTracing springPubSubTracing, SubscriberFactory subscriberFactory) {
		this.springPubSubTracing = springPubSubTracing;
		this.subscriberFactory = subscriberFactory;
	}

	@Override
	public String getProjectId() {
		return subscriberFactory.getProjectId();
	}

	@Override
	public Subscriber createSubscriber(String subscriptionName, MessageReceiver receiver) {
		return subscriberFactory.createSubscriber(subscriptionName,
				new TracingMessageReceiver(receiver, subscriptionName));
	}

	private final class TracingMessageReceiver implements MessageReceiver {
		private final MessageReceiver receiver;
		private final String subscriptionName;

		public TracingMessageReceiver(MessageReceiver receiver, String subscriptionName) {
			this.receiver = receiver;
			this.subscriptionName = subscriptionName;
		}

		@Override
		public void receiveMessage(PubsubMessage pubsubMessage, AckReplyConsumer ackReplyConsumer) {
			// instrument message
			PubsubMessage.Builder messageBuilder = pubsubMessage.toBuilder();
			MessageConsumerRequest request = new MessageConsumerRequest(messageBuilder, subscriptionName);
			TraceContextOrSamplingFlags extracted =
					springPubSubTracing.extractAndClearTraceIdHeaders(springPubSubTracing.consumerExtractor, request, messageBuilder);

			Span consumerSpan = springPubSubTracing.nextMessagingSpan(springPubSubTracing.consumerSampler, request, extracted);
			Span listenerSpan = springPubSubTracing.tracer.newChild(consumerSpan.context());

			if (!consumerSpan.isNoop()) {
				consumerSpan.name("next-message").kind(CONSUMER);
				if (springPubSubTracing.remoteServiceName != null) consumerSpan.remoteServiceName(springPubSubTracing.remoteServiceName);

				// incur timestamp overhead only once
				long timestamp = springPubSubTracing.tracing.clock(consumerSpan.context()).currentTimeMicroseconds();
				consumerSpan.start(timestamp);
				long consumerFinish = timestamp + 1L; // save a clock reading
				consumerSpan.finish(consumerFinish);

				// not using scoped span as we want to start with a pre-configured time
				listenerSpan.name("on-message").start(consumerFinish);
			}

			Tracer.SpanInScope ws = springPubSubTracing.tracer.withSpanInScope(listenerSpan);
			Throwable error = null;
			try {
				// pass instrumented message the actual receiver
				receiver.receiveMessage(messageBuilder.build(), ackReplyConsumer);
			} catch (Throwable t) {
				error = t;
				throw t;
			} finally {
				if (error != null) listenerSpan.error(error);
				listenerSpan.finish();
				ws.close();
			}

		}
	}

	@Override
	public PullRequest createPullRequest(String subscriptionName, Integer maxMessages, Boolean returnImmediately) {
		return subscriberFactory.createPullRequest(subscriptionName, maxMessages, returnImmediately);
	}

	@Override
	public SubscriberStub createSubscriberStub() {
		return subscriberFactory.createSubscriberStub();
	}

}
