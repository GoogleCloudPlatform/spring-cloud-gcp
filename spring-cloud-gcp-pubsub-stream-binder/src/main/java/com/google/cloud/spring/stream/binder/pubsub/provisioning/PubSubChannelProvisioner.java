/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.stream.binder.pubsub.provisioning;

import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubConsumerProperties;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubProducerProperties;
import com.google.pubsub.v1.DeadLetterPolicy;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/** Provisioning provider for Pub/Sub. */
public class PubSubChannelProvisioner
    implements ProvisioningProvider<
        ExtendedConsumerProperties<PubSubConsumerProperties>,
        ExtendedProducerProperties<PubSubProducerProperties>> {

  private static final Log LOGGER = LogFactory.getLog(PubSubChannelProvisioner.class);

  private final PubSubAdmin pubSubAdmin;

  private final Set<String> anonymousGroupSubscriptionNames = new HashSet<>();

  public PubSubChannelProvisioner(PubSubAdmin pubSubAdmin) {
    this.pubSubAdmin = pubSubAdmin;
  }

  @Override
  public ProducerDestination provisionProducerDestination(
      String topic, ExtendedProducerProperties<PubSubProducerProperties> properties) {
    ensureTopicExists(topic, properties.getExtension().isAutoCreateResources());

    return new PubSubProducerDestination(topic);
  }

  @Override
  public ConsumerDestination provisionConsumerDestination(
      String topicName,
      String group,
      ExtendedConsumerProperties<PubSubConsumerProperties> properties) {

    String customName = properties.getExtension().getSubscriptionName();

    boolean autoCreate = properties.getExtension().isAutoCreateResources();
    PubSubConsumerProperties.DeadLetterPolicy deadLetterPolicy =
        properties.getExtension().getDeadLetterPolicy();

    // topicName may be either the short or fully-qualified version.
    String topicShortName =
        TopicName.isParsableFrom(topicName) ? TopicName.parse(topicName).getTopic() : topicName;
    if (autoCreate) {
      ensureTopicExists(topicShortName, autoCreate);
    }

    String subscriptionName = null;
    if (StringUtils.hasText(customName)) {
      if (StringUtils.hasText(group)) {
        LOGGER.warn(
            "Either subscriptionName or group can be specified, but not both. "
                + "Using subscriptionName '"
                + customName
                + "'.");
      }
      subscriptionName = customName;
    } else if (StringUtils.hasText(group)) {
      subscriptionName = topicShortName + "." + group;
    }

    if (autoCreate) {
      if (!StringUtils.hasText(subscriptionName)) {
        // Generate anonymous random group since one wasn't provided
        subscriptionName = "anonymous." + topicShortName + "." + UUID.randomUUID();
        this.anonymousGroupSubscriptionNames.add(subscriptionName);
      }
      ensureSubscriptionExists(subscriptionName, topicName, deadLetterPolicy, autoCreate);
    }

    Assert.hasText(subscriptionName, "Subscription Name cannot be null or empty");
    return new PubSubConsumerDestination(subscriptionName);
  }

  public void afterUnbindConsumer(ConsumerDestination destination) {
    if (this.anonymousGroupSubscriptionNames.remove(destination.getName())) {
      try {
        this.pubSubAdmin.deleteSubscription(destination.getName());
      } catch (Exception ex) {
        LOGGER.warn(
            "Failed to delete auto-created anonymous subscription '"
                + destination.getName()
                + "'.");
      }
    }
  }

  Topic ensureTopicExists(String topicName, boolean autoCreate) {
    Topic topic = this.pubSubAdmin.getTopic(topicName);
    if (topic != null) {
      return topic;
    }

    if (autoCreate) {
      try {
        return this.pubSubAdmin.createTopic(topicName);
      } catch (AlreadyExistsException alreadyExistsException) {
        // Sometimes 2+ instances of this application will race to create the topic, so this ensures
        // we retry in the non-winning instances. In the rare case it fails, we throw an exception.
        return ensureTopicExists(topicName, false);
      }
    }
    throw new ProvisioningException("Non-existing '" + topicName + "' topic.");
  }

  Subscription ensureSubscriptionExists(
      String subscriptionName,
      String topicName,
      PubSubConsumerProperties.DeadLetterPolicy deadLetterPolicy,
      boolean autoCreate) {
    Subscription subscription = this.pubSubAdmin.getSubscription(subscriptionName);
    if (subscription == null) {
      return createSubscription(subscriptionName, topicName, deadLetterPolicy, autoCreate);
    }
    return subscription;
  }

  private Subscription createSubscription(
      String subscriptionName,
      String topicName,
      PubSubConsumerProperties.DeadLetterPolicy deadLetterPolicy,
      boolean autoCreate) {
    Subscription.Builder builder =
        Subscription.newBuilder().setName(subscriptionName).setTopic(topicName);

    if (deadLetterPolicy != null) {
      String dlTopicName = deadLetterPolicy.getDeadLetterTopic();
      Assert.hasText(dlTopicName, "Dead letter policy cannot have null or empty topic");

      Topic dlTopic = ensureTopicExists(dlTopicName, autoCreate);

      DeadLetterPolicy.Builder dlpBuilder =
          DeadLetterPolicy.newBuilder().setDeadLetterTopic(dlTopic.getName());

      Integer maxAttempts = deadLetterPolicy.getMaxDeliveryAttempts();
      if (maxAttempts != null) {
        dlpBuilder.setMaxDeliveryAttempts(maxAttempts);
      }
      builder.setDeadLetterPolicy(dlpBuilder);
    }

    return this.pubSubAdmin.createSubscription(builder);
  }
}
