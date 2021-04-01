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

package com.google.cloud.spring.pubsub.support;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.SubscriberInterface;

/**
 * Interface used by the {@link com.google.cloud.spring.pubsub.core.PubSubTemplate} to create
 * supporting objects for consuming messages from Pub/Sub subscriptions using streaming pull.
 */
public interface StreamingSubscriberFactory {
	/**
	 * Method to get the project id.
	 * @return the project id
	 * @since 1.1
	 */
	String getProjectId();

	/**
	 * Create a {@link SubscriberInterface} for the specified subscription name and wired it up to
	 * asynchronously deliver messages to the provided {@link MessageReceiver}.
	 * @param subscriptionName the name of the subscription
	 * @param receiver the callback for receiving messages asynchronously
	 * @return the {@link SubscriberInterface} that was created to bind the receiver to the subscription
	 */
	SubscriberInterface createSubscriber(String subscriptionName, MessageReceiver receiver);
}
