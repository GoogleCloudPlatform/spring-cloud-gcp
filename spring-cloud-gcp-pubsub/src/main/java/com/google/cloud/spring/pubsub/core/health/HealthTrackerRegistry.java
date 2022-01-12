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

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import java.util.Collection;

/**
 * A registry for the {@link HealthTracker} health trackers created per {@link
 * ProjectSubscriptionName}.
 *
 * @since 2.0.6
 */
public interface HealthTrackerRegistry {

  /**
   * Create a tracker for given subscription name and add tracker to registry.
   *
   * @param subscriptionName the subscription name to create a {@link ProjectSubscriptionName} using
   *     the default project-Id.
   * @return the {@link HealthTracker} that has been created and registered.
   */
  HealthTracker registerTracker(String subscriptionName);

  /**
   * Create a tracker for given {@link ProjectSubscriptionName} and add tracker to registry.
   *
   * @param projectSubscriptionName the full subscription name
   * @return the {@link HealthTracker} that has been created and registered.
   */
  HealthTracker registerTracker(ProjectSubscriptionName projectSubscriptionName);

  /**
   * Check if a {@link ProjectSubscriptionName} has a {@link HealthTracker} registered.
   *
   * @param projectSubscriptionName the full subscription name.
   * @return true if {@link HealthTracker} is registered.
   */
  boolean isTracked(ProjectSubscriptionName projectSubscriptionName);

  /**
   * Wrap a message receiver for a {@link ProjectSubscriptionName}. A {@link HealthTracker} will be
   * created and registered. On a new message received the registered {@link HealthTracker} will
   * record its processing.
   *
   * @param subscriptionName the full {@link ProjectSubscriptionName} name to monitor.
   * @param messageReceiver the {@link MessageReceiver} to wrap.
   * @return messageReceiver the wrapped {@link MessageReceiver}.
   */
  default MessageReceiver wrap(
      ProjectSubscriptionName subscriptionName, MessageReceiver messageReceiver) {
    HealthTracker healthTracker = registerTracker(subscriptionName);

    return (m, a) -> {
      messageReceiver.receiveMessage(m, a);
      healthTracker.processedMessage();
    };
  }

  /**
   * Propagate that a message has been processed to the corresponding {@link HealthTracker} if
   * registered.
   *
   * @param projectSubscriptionName the full {@link ProjectSubscriptionName} name to monitor.
   */
  void processedMessage(ProjectSubscriptionName projectSubscriptionName);

  /**
   * Attaches a listener to a Pub/Sub subscriber provided a {@link HealthTracker} is registered for
   * that subscription. On subscriber termination, the corresponding {@link HealthTracker} shall be
   * removed from the registry.
   *
   * @param subscriber the subscriber to attach a listener.
   */
  void addListener(Subscriber subscriber);

  /**
   * Retrieve all the health trackers registered.
   *
   * @return all known health trackers
   */
  Collection<HealthTracker> healthTrackers();
}
