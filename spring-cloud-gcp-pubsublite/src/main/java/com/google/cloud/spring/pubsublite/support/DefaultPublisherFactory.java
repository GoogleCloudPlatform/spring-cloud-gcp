/*
 * Copyright 2017-2021 the original author or authors.
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

package com.google.cloud.spring.pubsublite.support;

import java.util.HashMap;
import java.util.Map;

import com.google.api.core.ApiService.Listener;
import com.google.api.core.ApiService.State;
import com.google.cloud.pubsub.v1.PublisherInterface;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.cloudpubsub.Publisher;
import com.google.cloud.pubsublite.internal.wire.SystemExecutors;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.errorprone.annotations.concurrent.GuardedBy;

public class DefaultPublisherFactory implements PublisherFactory {
	private final PublisherFactorySettings settings;
	@GuardedBy("this")
	private final Map<TopicName, Publisher> publishers = new HashMap<>();

	public DefaultPublisherFactory(PublisherFactorySettings settings) {
		this.settings = settings;
	}

	@Override
	public synchronized PublisherInterface createPublisher(String topic) {
		TopicName name = TopicName.of(topic);
		if (publishers.containsKey(name)) {
			return publishers.get(name);
		}
		Publisher publisher = settings.instantiate(name);
		publishers.put(name, publisher);
		publisher.addListener(new Listener() {
			@Override
			public void failed(State from, Throwable failure) {
				erase(name);
			}
		}, SystemExecutors.getAlarmExecutor());
		publisher.startAsync().awaitRunning();
		return publisher;
	}

	private synchronized void erase(TopicName topic) {
		publishers.remove(topic);
	}
}
