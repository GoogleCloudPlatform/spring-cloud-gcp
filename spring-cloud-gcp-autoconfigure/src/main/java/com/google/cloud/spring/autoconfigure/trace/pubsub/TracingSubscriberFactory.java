package com.google.cloud.spring.autoconfigure.trace.pubsub;

import brave.Span;
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
		MessageReceiver wrappedMessageReceiver = new MessageReceiver(){
			@Override
			public void receiveMessage(PubsubMessage pubsubMessage, AckReplyConsumer ackReplyConsumer) {
				// instrument message
				// TODO: do not convert from message to builder multiple times
				MessageConsumerRequest request = new MessageConsumerRequest(pubsubMessage.toBuilder(), subscriptionName);
				TraceContextOrSamplingFlags extracted =
						springPubSubTracing.extractAndClearTraceIdHeaders(springPubSubTracing.consumerExtractor, request, pubsubMessage.toBuilder());

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

				// TODO: pass instrumented message?
				receiver.receiveMessage(pubsubMessage, ackReplyConsumer);
			}
		};
		return null;
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
