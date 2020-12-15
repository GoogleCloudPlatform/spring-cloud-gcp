package com.google.cloud.spring.autoconfigure.trace.pubsub;

import com.google.cloud.pubsub.v1.PublisherInterface;
import com.google.cloud.spring.pubsub.support.PublisherFactory;

final class TracingPublisherFactory implements PublisherFactory {
	private final PubSubTracing pubSubTracing;

	private final PublisherFactory publisherFactory;

	public TracingPublisherFactory(PubSubTracing pubSubTracing, PublisherFactory publisherFactory) {
		this.pubSubTracing = pubSubTracing;
		this.publisherFactory = publisherFactory;
	}

	@Override
	public PublisherInterface createPublisher(String topic) {
		return new TracingPublisher(publisherFactory.createPublisher(topic), pubSubTracing, topic);
	}

}