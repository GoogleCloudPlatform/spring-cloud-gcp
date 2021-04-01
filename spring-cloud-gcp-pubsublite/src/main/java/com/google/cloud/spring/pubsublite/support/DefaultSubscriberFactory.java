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

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.SubscriberInterface;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.spring.pubsub.support.StreamingSubscriberFactory;

public class DefaultSubscriberFactory implements StreamingSubscriberFactory {
	private final SubscriberFactorySettings settings;

	public DefaultSubscriberFactory(SubscriberFactorySettings settings) {
		this.settings = settings;
	}

	@Override
	public String getProjectId() {
		return settings.projectIdProvider().getProjectId();
	}

	@Override
	public SubscriberInterface createSubscriber(String subscriptionName, MessageReceiver receiver) {
		return settings.instantiate(SubscriptionName.of(subscriptionName), receiver);
	}
}
