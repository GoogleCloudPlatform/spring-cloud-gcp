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

import com.google.pubsub.v1.ProjectSubscriptionName;

/**
 * Health tracker for a Pub/Sub subscription. The tracker will monitor messages being processed and
 * the subscription's message backlog. If backlog message size exceeds the message backlog
 * threshold, the tracker will return the number of messages over the threshold.
 *
 * @since 2.0.6
 */
public interface HealthTracker {

  /** Template for undelivered messages filter. */
  String UNDELIVERED_FILTER_TEMPLATE =
      "metric.type=\"pubsub.googleapis.com/subscription/num_undelivered_messages\""
          + " resource.type=\"pubsub_subscription\" resource.label.subscription_id=\"%s\"";

  /** Record that a message has been processed. */
  void processedMessage();

  /**
   * Accessor for the Subscription being monitored by the tracker.
   *
   * @return the project subscription name
   */
  ProjectSubscriptionName subscription();

  /**
   * If messages have been processed recently returns zero. If no message have been recently
   * processed the tracker will retrieve the number of messages in the subscription backlog. If the
   * backlogs exceeds the threshold the messages over the threshold shall be returned.
   *
   * @return the messages over the threshold
   */
  long messagesOverThreshold();

  /**
   * Creates a GCP Monitoring filter to query the message backlog of a Pub/Sub subscription.
   *
   * @param subscriptionId the subscription id to add to the filter.
   * @return Monitoring Pub/Sub Subscription filter for undelivered messages.
   */
  default String undeliveredFilter(String subscriptionId) {
    return String.format(UNDELIVERED_FILTER_TEMPLATE, subscriptionId);
  }
}
