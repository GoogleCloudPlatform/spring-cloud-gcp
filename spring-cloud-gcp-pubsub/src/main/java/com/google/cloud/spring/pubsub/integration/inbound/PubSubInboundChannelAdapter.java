/*
 * Copyright 2017-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.pubsub.integration.inbound;

import static sun.font.FontUtilities.logWarning;

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

  public PubSubInboundChannelAdapter(
      PubSubSubscriberOperations pubSubSubscriberOperations, String subscriptionName) {
    Assert.notNull(pubSubSubscriberOperations, "Pub/Sub subscriber template can't be null.");
    Assert.notNull(subscriptionName, "Pub/Sub subscription name can't be null.");
    this.pubSubSubscriberOperations = pubSubSubscriberOperations;
    this.subscriptionName = subscriptionName;
  }

  // ... (Other getters and setters remain unchanged)

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
      // FIX for issue #963: Extract the payload from the converted message.
      Object payload = message.getPayload();

      // Spring's MessageBuilder.withPayload() does not allow null values.
      // If the Pub/Sub message body is empty (e.g., used only for attributes),
      // we default to an empty byte array to prevent an IllegalArgumentException.
      if (payload == null) {
        payload = new byte[0];
      }

      sendMessage(
          getMessageBuilderFactory()
              .withPayload(payload)
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

  // ... (Rest of the class methods: logWarning, healthCheck, etc.)
}