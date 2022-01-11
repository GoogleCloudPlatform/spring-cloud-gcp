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

package com.google.cloud.spring.stream.binder.pubsub.properties;

import com.google.cloud.spring.pubsub.integration.AckMode;

/** Consumer properties for Pub/Sub. */
public class PubSubConsumerProperties extends PubSubCommonProperties {

  private AckMode ackMode = AckMode.AUTO;

  private Integer maxFetchSize = 1;

  private String subscriptionName = null;

  private DeadLetterPolicy deadLetterPolicy = null;

  public AckMode getAckMode() {
    return ackMode;
  }

  public void setAckMode(AckMode ackMode) {
    this.ackMode = ackMode;
  }

  public Integer getMaxFetchSize() {
    return maxFetchSize;
  }

  public void setMaxFetchSize(Integer maxFetchSize) {
    this.maxFetchSize = maxFetchSize;
  }

  public String getSubscriptionName() {
    return subscriptionName;
  }

  public void setSubscriptionName(String subscriptionName) {
    this.subscriptionName = subscriptionName;
  }

  public DeadLetterPolicy getDeadLetterPolicy() {
    return deadLetterPolicy;
  }

  public void setDeadLetterPolicy(DeadLetterPolicy deadLetterPolicy) {
    this.deadLetterPolicy = deadLetterPolicy;
  }

  public static class DeadLetterPolicy {
    private String deadLetterTopic;

    private Integer maxDeliveryAttempts;

    public String getDeadLetterTopic() {
      return deadLetterTopic;
    }

    public void setDeadLetterTopic(String deadLetterTopic) {
      this.deadLetterTopic = deadLetterTopic;
    }

    public Integer getMaxDeliveryAttempts() {
      return maxDeliveryAttempts;
    }

    public void setMaxDeliveryAttempts(Integer maxDeliveryAttempts) {
      this.maxDeliveryAttempts = maxDeliveryAttempts;
    }
  }
}
