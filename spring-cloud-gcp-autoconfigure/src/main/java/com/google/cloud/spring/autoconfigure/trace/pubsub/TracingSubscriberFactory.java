package com.google.cloud.spring.autoconfigure.trace.pubsub;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.pubsub.v1.PullRequest;


final class TracingSubscriberFactory implements SubscriberFactory {
	private SubscriberFactory subscriberFactory;

	public TracingSubscriberFactory(SubscriberFactory subscriberFactory) {
		this.subscriberFactory = subscriberFactory;
	}

	@Override
	public String getProjectId() {
		return null;
	}

	@Override
	public Subscriber createSubscriber(String subscriptionName, MessageReceiver receiver) {
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
