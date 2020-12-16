/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.autoconfigure.trace.pubsub;

import com.google.cloud.pubsub.v1.PublisherInterface;
import com.google.cloud.spring.autoconfigure.trace.pubsub.brave.PubSubTracing;
import com.google.cloud.spring.pubsub.support.PublisherFactory;

final class TracingPublisherFactory implements PublisherFactory {
	private final PubSubTracing pubSubTracing;

	private final PublisherFactory publisherFactory;

	TracingPublisherFactory(PubSubTracing pubSubTracing, PublisherFactory publisherFactory) {
		this.pubSubTracing = pubSubTracing;
		this.publisherFactory = publisherFactory;
	}

	@Override
	public PublisherInterface createPublisher(String topic) {
		return pubSubTracing.publisher(publisherFactory.createPublisher(topic),  topic);
	}

}
