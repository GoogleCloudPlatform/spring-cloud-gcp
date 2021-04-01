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

package com.google.cloud.spring.pubsub.core.subscriber;

import java.util.function.Consumer;

import com.google.cloud.pubsub.v1.SubscriberInterface;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.converter.ConvertedBasicAcknowledgeablePubsubMessage;

/**
 * An abstraction for Google Cloud Pub/Sub streaming operations.
 *
 * @author Vinicius Carvalho
 * @author João André Martins
 * @author Mike Eltsufin
 * @author Chengyuan Zhao
 * @author Doug Hoard
 * @author Maurice Zeijen
 *
 * @since 1.1
 */
public interface PubSubStreamingSubscriberOperations {
	/**
	 * Add a callback method to an existing subscription.
	 * <p>The created {@link SubscriberInterface} is returned so it can be stopped.
	 * @param subscription canonical subscription name, e.g., "subscriptionName", or the fully-qualified
	 * subscription name in the {@code projects/<project_name>/subscriptions/<subscription_name>} format
	 * @param messageConsumer the callback method triggered when new messages arrive
	 * @return subscriber listening to new messages
	 * @since 1.1
	 */
	SubscriberInterface subscribe(String subscription, Consumer<BasicAcknowledgeablePubsubMessage> messageConsumer);

	/**
	 * Add a callback method to an existing subscription that receives Pub/Sub messages converted to the requested
	 * payload type.
	 * <p>The created {@link SubscriberInterface} is returned so it can be stopped.
	 * @param subscription canonical subscription name, e.g., "subscriptionName", or the fully-qualified
	 * subscription name in the {@code projects/<project_name>/subscriptions/<subscription_name>} format
	 * @param messageConsumer the callback method triggered when new messages arrive
	 * @param payloadType the type to which the payload of the Pub/Sub message should be converted
	 * @param <T> the type of the payload
	 * @return subscriber listening to new messages
	 * @since 1.1
	 */
	<T> SubscriberInterface subscribeAndConvert(String subscription,
			Consumer<ConvertedBasicAcknowledgeablePubsubMessage<T>> messageConsumer, Class<T> payloadType);
}
