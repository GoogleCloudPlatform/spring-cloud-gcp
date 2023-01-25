/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.pubsub.integration.inbound;

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberOperations;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.PubSubHeaderMapper;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.google.cloud.spring.pubsub.support.converter.ConvertedBasicAcknowledgeablePubsubMessage;
import com.google.pubsub.v1.ProjectSubscriptionName;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.util.Assert;

/**
 * Converts from GCP Pub/Sub message to Spring message and sends the Spring message to the attached
 * channels.
 */
public class PubSubInboundChannelAdapter extends MessageProducerSupport {

  private static final Log LOGGER = LogFactory.getLog(PubSubInboundChannelAdapter.class);

  private final String subscriptionName;

  private final PubSubSubscriberOperations pubSubSubscriberOperations;

  private Subscriber subscriber;

  private AckMode ackMode = AckMode.AUTO;

  private HeaderMapper<Map<String, String>> headerMapper = new PubSubHeaderMapper();

  private Class<?> payloadType = byte[].class;

  private HealthTrackerRegistry healthTrackerRegistry;

  /**
   * Instantiates a streaming Pub/Sub subscription adapter.
   *
   * @param pubSubSubscriberOperations {@link PubSubSubscriberOperations} to use
   * @param subscriptionName short subscription name, e.g., "subscriptionName", or the fully-qualified subscription name
   *                        in the {@code projects/[project_name]/subscriptions/[subscription_name]} format
   */
  public PubSubInboundChannelAdapter(
      PubSubSubscriberOperations pubSubSubscriberOperations, String subscriptionName) {
    Assert.notNull(pubSubSubscriberOperations, "Pub/Sub subscriber template can't be null.");
    Assert.notNull(subscriptionName, "Pub/Sub subscription name can't be null.");
    this.pubSubSubscriberOperations = pubSubSubscriberOperations;
    this.subscriptionName = subscriptionName;
  }

  public AckMode getAckMode() {
    return this.ackMode;
  }

  public void setAckMode(AckMode ackMode) {
    Assert.notNull(ackMode, "The acknowledgement mode can't be null.");
    this.ackMode = ackMode;
  }

  public void setHealthTrackerRegistry(HealthTrackerRegistry healthTrackerRegistry) {
    this.healthTrackerRegistry = healthTrackerRegistry;
  }

  public Class<?> getPayloadType() {
    return this.payloadType;
  }

  /**
   * Set the desired type of the payload of the {@link org.springframework.messaging.Message}
   * constructed by converting the incoming Pub/Sub message. The channel adapter will use the {@link
   * com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter} configured for {@link
   * PubSubSubscriberOperations#subscribeAndConvert(String, java.util.function.Consumer, Class)}.
   * The default payload type is {@code byte[].class}.
   *
   * @param payloadType the type of the payload of the {@link org.springframework.messaging.Message}
   *     produced by the adapter. Cannot be set to null.
   */
  public void setPayloadType(Class<?> payloadType) {
    Assert.notNull(payloadType, "The payload type cannot be null.");
    this.payloadType = payloadType;
  }

  /**
   * Set the header mapper to map headers from incoming {@link com.google.pubsub.v1.PubsubMessage}
   * into {@link org.springframework.messaging.Message}.
   *
   * @param headerMapper the header mapper
   */
  public void setHeaderMapper(HeaderMapper<Map<String, String>> headerMapper) {
    Assert.notNull(headerMapper, "The header mapper can't be null.");
    this.headerMapper = headerMapper;
  }

  @Override
  protected void doStart() {
    super.doStart();

    addToHealthRegistry();

    this.subscriber =
        this.pubSubSubscriberOperations.subscribeAndConvert(
            this.subscriptionName, this::consumeMessage, this.payloadType);

    addListeners();
  }

  @Override
  protected void doStop() {
    if (this.subscriber != null) {
      this.subscriber.stopAsync();
    }

    super.doStop();
  }

  private void consumeMessage(ConvertedBasicAcknowledgeablePubsubMessage<?> message) {
    Map<String, Object> messageHeaders =
        this.headerMapper.toHeaders(message.getPubsubMessage().getAttributesMap());

    // Send the original message downstream so that the user can decide on when to
    // ack/nack, or just have access to the original message for any other reason.
    messageHeaders.put(GcpPubSubHeaders.ORIGINAL_MESSAGE, message);

    try {
      sendMessage(
          getMessageBuilderFactory()
              .withPayload(message.getPayload())
              .copyHeaders(messageHeaders)
              .build());

      processedMessage(message.getProjectSubscriptionName());

      if (this.ackMode == AckMode.AUTO_ACK || this.ackMode == AckMode.AUTO) {
        message.ack();
      }
    } catch (RuntimeException re) {
      if (this.ackMode == AckMode.AUTO) {
        message.nack();
        logWarning(message, re, "message nacked automatically.");
      } else {
        logWarning(message, re, "message neither acked nor nacked.");
      }
    }
  }

  private void logWarning(
      ConvertedBasicAcknowledgeablePubsubMessage<?> message,
      RuntimeException re,
      String actionMessage) {
    LOGGER.warn(String.format("Sending Spring message [%s] failed; %s",
        message.getPubsubMessage().getMessageId(), actionMessage));
    // Starting from Spring 3.0, nested exception message is NOT included in stacktrace.
    // However, customers may still rely on messages in nested exception to troubleshoot,
    // so we explicitly log failure messages.
    // See https://github.com/spring-projects/spring-framework/issues/25162 for more info.
    if (re instanceof MessageDeliveryException messageDeliveryException) {
      LOGGER.warn(messageDeliveryException.getFailedMessage());
    } else {
      LOGGER.warn(re.getMessage());
    }
  }

  private void addToHealthRegistry() {
    if (healthCheckEnabled()) {
      healthTrackerRegistry.registerTracker(subscriptionName);
    }
  }

  private void addListeners() {
    if (healthCheckEnabled()) {
      healthTrackerRegistry.addListener(subscriber);
    }
  }

  private void processedMessage(ProjectSubscriptionName projectSubscriptionName) {
    if (healthCheckEnabled()) {
      healthTrackerRegistry.processedMessage(projectSubscriptionName);
    }
  }

  public boolean healthCheckEnabled() {
    return healthTrackerRegistry != null;
  }
}
