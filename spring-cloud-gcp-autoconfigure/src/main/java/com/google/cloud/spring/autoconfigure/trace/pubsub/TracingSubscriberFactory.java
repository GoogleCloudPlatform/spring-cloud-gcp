package com.google.cloud.spring.autoconfigure.trace.pubsub;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.pubsub.v1.PullRequest;

final class TracingSubscriberFactory implements SubscriberFactory {
	private final PubSubTracing pubSubTracing;

	private final SubscriberFactory delegate;

	public TracingSubscriberFactory(PubSubTracing pubSubTracing, SubscriberFactory delegate) {
		this.pubSubTracing = pubSubTracing;
		this.delegate = delegate;
	}

	@Override
	public String getProjectId() {
		return delegate.getProjectId();
	}

	@Override
	public Subscriber createSubscriber(String subscriptionName, MessageReceiver receiver) {
		return delegate.createSubscriber(subscriptionName, new TracingMessageReceiver(pubSubTracing, receiver, subscriptionName));
	}

	@Override
	public PullRequest createPullRequest(String subscriptionName, Integer maxMessages, Boolean returnImmediately) {
		return delegate.createPullRequest(subscriptionName, maxMessages, returnImmediately);
	}

	@Override
	public SubscriberStub createSubscriberStub() {
		return new TracingSubscriberStub(delegate.createSubscriberStub(), pubSubTracing);
	}
}
