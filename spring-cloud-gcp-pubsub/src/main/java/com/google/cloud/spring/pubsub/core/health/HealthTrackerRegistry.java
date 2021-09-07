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

package com.google.cloud.spring.pubsub.core.health;

import java.util.Collection;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;

/**
 * @author Emmanouil Gkatziouras
 *
 * @since 2.0.1
 */
public interface HealthTrackerRegistry {

	HealthTracker registerTracker(ProjectSubscriptionName projectSubscriptionName);

	boolean isTracked(ProjectSubscriptionName projectSubscriptionName);

	default MessageReceiver wrap(ProjectSubscriptionName subscriptionName, MessageReceiver messageReceiver) {
		HealthTracker healthTracker = registerTracker(subscriptionName);

		return (m, a) -> {
			messageReceiver.receiveMessage(m, a);
			healthTracker.processedMessage();
		};
	}

	void processedMessage(ProjectSubscriptionName projectSubscriptionName);

	void addListener(Subscriber subscriber);

	Collection<HealthTracker> healthTrackers();

}
