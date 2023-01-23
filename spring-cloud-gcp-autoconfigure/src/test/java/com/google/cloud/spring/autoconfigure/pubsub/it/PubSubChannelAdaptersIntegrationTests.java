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

package com.google.cloud.spring.autoconfigure.pubsub.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler.SuccessCallback;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;

/** Tests for Pub/Sub channel adapters. */
@EnabledIfSystemProperty(named = "it.pubsub", matches = "true")
class PubSubChannelAdaptersIntegrationTests {

  private static final int RECEIVE_TIMEOUT_MS = 10000;

  static PubSubAdmin pubSubAdmin;

  String topicName;

  String subscriptionName;

  ApplicationContextRunner contextRunner;

  @BeforeAll
  static void enableTests() throws IOException {
    pubSubAdmin =
        new PubSubAdmin(
            new DefaultGcpProjectIdProvider(),
            new DefaultCredentialsProvider(Credentials::new));
  }

  @BeforeEach
  void setUpPubSubResources() {
    this.topicName = "desafinado-" + UUID.randomUUID();
    this.subscriptionName = "doralice-" + UUID.randomUUID();

    if (pubSubAdmin.getTopic(this.topicName) == null) {
      pubSubAdmin.createTopic(this.topicName);
    }

    if (pubSubAdmin.getSubscription(this.subscriptionName) == null) {
      pubSubAdmin.createSubscription(this.subscriptionName, this.topicName, 10);
    }

    this.contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    GcpContextAutoConfiguration.class, GcpPubSubAutoConfiguration.class))
            .withBean("topicName", String.class, this.topicName)
            .withBean("subscriptionName", String.class, this.subscriptionName);
  }

  @AfterEach
  void tearDownPubSubResources() {
    pubSubAdmin.deleteSubscription(this.subscriptionName);
    pubSubAdmin.deleteTopic(this.topicName);
  }

  @Test
  void sendAndReceiveMessageAsString() {
    this.contextRunner
        .withUserConfiguration(PollableConfiguration.class, CommonConfiguration.class)
        .run(
            context -> {
              Map<String, Object> headers = new HashMap<>();
              // Only String values for now.
              headers.put("storm", "lift your skinny fists");
              headers.put("static", "lift your skinny fists");
              headers.put("sleep", "lift your skinny fists");

              Message originalMessage =
                  MessageBuilder.createMessage(
                      "I am a message (sendAndReceiveMessageAsString).".getBytes(),
                      new MessageHeaders(headers));
              context.getBean("inputChannel", MessageChannel.class).send(originalMessage);

              Message<?> message =
                  context
                      .getBean("outputChannel", PollableChannel.class)
                      .receive(RECEIVE_TIMEOUT_MS);
              assertThat(message).isNotNull();
              assertThat(message.getPayload()).isInstanceOf(byte[].class);
              String payload = new String((byte[]) message.getPayload());
              assertThat(payload).isEqualTo("I am a message (sendAndReceiveMessageAsString).");

              assertThat(message.getHeaders()).hasSize(6);
              assertThat(message.getHeaders()).containsEntry("storm", "lift your skinny fists");
              assertThat(message.getHeaders()).containsEntry("static", "lift your skinny fists");
              assertThat(message.getHeaders()).containsEntry("sleep", "lift your skinny fists");
              assertThat(message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE)).isNotNull();
            });
  }

  @Test
  void sendAndReceiveMessage() {
    this.contextRunner
        .withUserConfiguration(PollableConfiguration.class, CommonConfiguration.class)
        .run(
            context -> {
              context
                  .getBean("inputChannel", MessageChannel.class)
                  .send(
                      MessageBuilder.withPayload(
                              "I am a message (sendAndReceiveMessage).".getBytes())
                          .build());

              Message<?> message =
                  context
                      .getBean("outputChannel", PollableChannel.class)
                      .receive(RECEIVE_TIMEOUT_MS);
              assertThat(message).isNotNull();
              assertThat(message.getPayload()).isInstanceOf(byte[].class);
              String stringPayload = new String((byte[]) message.getPayload());
              assertThat(stringPayload).isEqualTo("I am a message (sendAndReceiveMessage).");
            });
  }

  @Test
  void sendAndReceiveMessageManualAck() {
    this.contextRunner
        .withUserConfiguration(PollableConfiguration.class, CommonConfiguration.class)
        .run(
            context -> {
              context.getBean(PubSubInboundChannelAdapter.class).setAckMode(AckMode.MANUAL);
              context
                  .getBean("inputChannel", MessageChannel.class)
                  .send(
                      MessageBuilder.withPayload(
                              "I am a message (sendAndReceiveMessageManualAck).".getBytes())
                          .build());

              PollableChannel channel = context.getBean("outputChannel", PollableChannel.class);

              Message<?> message = channel.receive(RECEIVE_TIMEOUT_MS);
              assertThat(message).isNotNull();
              BasicAcknowledgeablePubsubMessage origMessage =
                  (BasicAcknowledgeablePubsubMessage)
                      message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE);
              assertThat(origMessage).isNotNull();
              origMessage.nack();

              message = channel.receive(RECEIVE_TIMEOUT_MS);
              assertThat(message).isNotNull();
              origMessage =
                  (BasicAcknowledgeablePubsubMessage)
                      message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE);
              assertThat(origMessage).isNotNull();
              origMessage.ack();

              message = channel.receive(RECEIVE_TIMEOUT_MS);
              assertThat(message).isNull();
            });
  }

  // If this test flakes, delete it.
  // It verifies that in AUTO_ACK mode, the message is neither acked nor nacked, and that
  // redelivery happens after subscription's ackDeadline passes.
  // There is also a client library bug (https://github.com/googleapis/java-pubsub/issues/141) that
  // results in ackDeadline being extended by 60 seconds even when maxAckExtensionPeriod is zero,
  // making minimum redelivery time is ackDeadline + 60.
  @Test
  void sendAndReceiveMessageAutoAckWithFailure() {

    this.contextRunner
        .withUserConfiguration(SubscribableConfiguration.class, CommonConfiguration.class)
        .withPropertyValues("spring.cloud.gcp.pubsub.subscriber.max-ack-extension-period=0")
        .run(
            context -> {
              context.getBean(PubSubInboundChannelAdapter.class).setAckMode(AckMode.AUTO_ACK);
              context
                  .getBean("inputChannel", MessageChannel.class)
                  .send(
                      MessageBuilder.withPayload("This message is in trouble.".getBytes()).build());

              SubscribableChannel channel =
                  context.getBean("outputChannel", SubscribableChannel.class);

              AtomicInteger numReceivedMessages = new AtomicInteger(0);
              channel.subscribe(
                  msg -> {
                    if (numReceivedMessages.incrementAndGet() == 1) {
                      throw new RuntimeException("BOOM!");
                    }
                  });

              // wait for initial delivery
              Awaitility.await()
                  .atMost(10, TimeUnit.SECONDS)
                  .until(() -> numReceivedMessages.get() > 0);
              assertThat(numReceivedMessages.get()).isEqualTo(1);

              // Expect redelivery after at least 10 seconds but within 1.5 minutes:
              // 10 seconds subscription ackDeadline
              // + 60 seconds https://github.com/googleapis/java-pubsub/issues/141
              // + 20 seconds anti-flake buffer
              Awaitility.await()
                  .atLeast(9, TimeUnit.SECONDS)
                  .atMost(90, TimeUnit.SECONDS)
                  .until(() -> numReceivedMessages.get() > 1);
              assertThat(numReceivedMessages.get()).isEqualTo(2);
            });
  }

  @Test
  void sendAndReceiveMessageManualAckThroughAcknowledgementHeader() {
    this.contextRunner
        .withUserConfiguration(PollableConfiguration.class, CommonConfiguration.class)
        .run(
            context -> {
              context.getBean(PubSubInboundChannelAdapter.class).setAckMode(AckMode.MANUAL);
              context
                  .getBean("inputChannel", MessageChannel.class)
                  .send(
                      MessageBuilder.withPayload(
                              "I am a message (sendAndReceiveMessageManualAckThroughAcknowledgementHeader)."
                                  .getBytes())
                          .build());

              PollableChannel channel = context.getBean("outputChannel", PollableChannel.class);

              Message<?> message = channel.receive(RECEIVE_TIMEOUT_MS);
              assertThat(message).isNotNull();
              BasicAcknowledgeablePubsubMessage acker =
                  (BasicAcknowledgeablePubsubMessage)
                      message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE);
              assertThat(acker).isNotNull();
              acker.ack();

              message = channel.receive(RECEIVE_TIMEOUT_MS);
              assertThat(message).isNull();
            });
  }

  @Test
  void sendAndReceiveMessagePublishCallback() {
    this.contextRunner
        .withUserConfiguration(PollableConfiguration.class, CommonConfiguration.class)
        .run(
            context -> {
              SuccessCallback successCallback = mock(SuccessCallback.class);
              context.getBean(PubSubMessageHandler.class).setSuccessCallback(successCallback);
              context
                  .getBean("inputChannel", MessageChannel.class)
                  .send(
                      MessageBuilder.withPayload(
                              "I am a message (sendAndReceiveMessagePublishCallback).".getBytes())
                          .build());

              Message<?> message =
                  context
                      .getBean("outputChannel", PollableChannel.class)
                      .receive(RECEIVE_TIMEOUT_MS);
              assertThat(message).isNotNull();

              Awaitility.await()
                  .atMost(1, TimeUnit.SECONDS)
                  .untilAsserted(() ->
                      verify(successCallback).onSuccess(anyString(), any()));
            });
  }

  /** Spring Boot config for tests. */
  @Configuration
  static class PollableConfiguration {

    @Bean
    public MessageChannel outputChannel() {
      return new QueueChannel();
    }
  }

  @Configuration
  static class SubscribableConfiguration {

    @Bean
    public MessageChannel outputChannel() {
      return new PublishSubscribeChannel();
    }
  }

  @Configuration
  @EnableIntegration
  static class CommonConfiguration {

    @Bean
    public PubSubInboundChannelAdapter inboundChannelAdapter(
        PubSubTemplate pubSubTemplate,
        @Qualifier("outputChannel") MessageChannel outputChannel,
        @Qualifier("subscriptionName") String subscriptionName) {
      PubSubInboundChannelAdapter inboundChannelAdapter =
          new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
      inboundChannelAdapter.setOutputChannel(outputChannel);

      return inboundChannelAdapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "inputChannel")
    public PubSubMessageHandler outboundChannelAdapter(
        PubSubTemplate pubSubTemplate, @Qualifier("topicName") String topicName) {
      return new PubSubMessageHandler(pubSubTemplate, topicName);
    }
  }
}
