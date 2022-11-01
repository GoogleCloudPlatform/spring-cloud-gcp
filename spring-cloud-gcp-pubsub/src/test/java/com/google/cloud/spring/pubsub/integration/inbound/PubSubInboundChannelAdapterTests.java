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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberOperations;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.google.cloud.spring.pubsub.support.converter.ConvertedBasicAcknowledgeablePubsubMessage;
import com.google.pubsub.v1.PubsubMessage;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.handler.ServiceActivatingHandler;
import org.springframework.integration.support.MutableMessageBuilder;
import org.springframework.integration.support.MutableMessageBuilderFactory;
import org.springframework.integration.test.util.TestUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;

/** {@link PubSubInboundChannelAdapter} unit tests. */
@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class PubSubInboundChannelAdapterTests {

  private final TestUtils.TestApplicationContext context = TestUtils.createTestApplicationContext();

  private PubSubInboundChannelAdapter adapter;

  private static final String EXCEPTION_MESSAGE = "Simulated downstream message processing failure";

  @Mock private PubSubSubscriberOperations mockPubSubSubscriberOperations;

  @Mock private MessageChannel mockMessageChannel;

  @Mock private ConvertedBasicAcknowledgeablePubsubMessage mockAcknowledgeableMessage;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {

    this.adapter =
        new PubSubInboundChannelAdapter(this.mockPubSubSubscriberOperations, "testSubscription");
    this.adapter.setOutputChannel(this.mockMessageChannel);
    this.adapter.setBeanFactory(this.context);

  }

  private void setupSubscribeAndConvert() {
    when(this.mockMessageChannel.send(any())).thenReturn(true);

    when(mockAcknowledgeableMessage.getPubsubMessage())
            .thenReturn(PubsubMessage.newBuilder().build());
    when(mockAcknowledgeableMessage.getPayload()).thenReturn("Test message payload.");

    when(this.mockPubSubSubscriberOperations.subscribeAndConvert(
            anyString(), any(Consumer.class), any(Class.class)))
            .then(
                    invocationOnMock -> {
                      Consumer<ConvertedBasicAcknowledgeablePubsubMessage> messageConsumer =
                              invocationOnMock.getArgument(1);
                      messageConsumer.accept(mockAcknowledgeableMessage);
                      return null;
                    });
  }

  @AfterEach
  void tearDown() {
    this.context.close();
  }

  @Test
  void testNonNullAckMode() {

    assertThatThrownBy(
            () -> {
              this.adapter.setAckMode(null);
            })
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("The acknowledgement mode can't be null.");
  }

  @Test
  void testAckModeAuto_nacksWhenDownstreamProcessingFails(CapturedOutput capturedOutput) {
    setupSubscribeAndConvert();

    when(this.mockMessageChannel.send(any())).thenThrow(new RuntimeException(EXCEPTION_MESSAGE));

    this.adapter.setAckMode(AckMode.AUTO);
    this.adapter.setOutputChannel(this.mockMessageChannel);

    this.adapter.start();

    verify(mockAcknowledgeableMessage).nack();

    assertThat(capturedOutput).contains("failed; message nacked automatically").contains(EXCEPTION_MESSAGE);
  }

  @Test
  void testAckModeAuto_nacksWhenDownstreamProcessingFailsWhenContextShutdown(CapturedOutput capturedOutput) {

    setupSubscribeAndConvert();
    this.adapter.setAckMode(AckMode.AUTO);
    this.adapter.setOutputChannel(this.mockMessageChannel);

    PublishSubscribeChannel errorChannel = new PublishSubscribeChannel(true);
    // Simulating what FinalRethrowingErrorMessageHandler would do.
    MessageHandler errorHandler =
        message -> {
          throw new RuntimeException("error channel fails, too");
        };
    errorChannel.subscribe(errorHandler);
    ServiceActivatingHandler handler =
        new ServiceActivatingHandler(
            msg -> {
              throw new RuntimeException("error handling failed");
            });
    handler.setBeanFactory(this.context);
    handler.afterPropertiesSet();
    this.adapter.setErrorChannel(errorChannel);

    when(this.mockMessageChannel.send(any()))
        .then(
            input -> {
              errorChannel.unsubscribe(errorHandler);
              this.adapter.stop();
              throw new RuntimeException(EXCEPTION_MESSAGE);
            });
    this.adapter.start();

    verify(mockAcknowledgeableMessage).nack();
    verify(mockAcknowledgeableMessage, times(0)).ack();

    // original message handling exception
    assertThat(capturedOutput).contains("failed; message nacked automatically").contains(EXCEPTION_MESSAGE);
  }

  @Test
  void testAckModeAutoAck_neitherAcksNorNacksWhenMessageProcessingFails(CapturedOutput capturedOutput) {

    setupSubscribeAndConvert();
    when(this.mockMessageChannel.send(any())).thenThrow(new RuntimeException(EXCEPTION_MESSAGE));

    this.adapter.setAckMode(AckMode.AUTO_ACK);

    this.adapter.start();

    // When exception thrown, verify that neither ack() nor nack() is called.
    verify(mockAcknowledgeableMessage, times(0)).ack();
    verify(mockAcknowledgeableMessage, times(0)).nack();

    assertThat(capturedOutput).contains("failed; message neither acked nor nacked").contains(EXCEPTION_MESSAGE);
  }

  @Test
  void testSetHealthRegistry_Success() {

    setupSubscribeAndConvert();
    HealthTrackerRegistry healthTrackerRegistry = mock(HealthTrackerRegistry.class);
    adapter.setHealthTrackerRegistry(healthTrackerRegistry);
    adapter.doStart();
    verify(healthTrackerRegistry).registerTracker("testSubscription");

  }

  @Test
  void testMessageProcessed_successWhenRegistrySet() {

    setupSubscribeAndConvert();
    HealthTrackerRegistry healthTrackerRegistry = mock(HealthTrackerRegistry.class);
    adapter.setHealthTrackerRegistry(healthTrackerRegistry);
    adapter.doStart();

    verify(healthTrackerRegistry, times(1)).registerTracker(any(String.class));

    this.mockMessageChannel.send(new GenericMessage<>("test-message"));

    verify(healthTrackerRegistry, times(1)).processedMessage(any());

  }

  @Test
  void testAddingSubscription_successWhenSubscriberAdded() {

    setupSubscribeAndConvert();
    HealthTrackerRegistry healthTrackerRegistry = mock(HealthTrackerRegistry.class);
    adapter.setHealthTrackerRegistry(healthTrackerRegistry);
    adapter.doStart();
    verify(healthTrackerRegistry, times(1)).addListener(any());

  }

  @Test
  @SuppressWarnings("unchecked")
  void customMessageBuilderFactoryUsedWhenAvailable() {

    setupSubscribeAndConvert();
    MutableMessageBuilderFactory factory = mock(MutableMessageBuilderFactory.class);
    when(factory.withPayload(any()))
        .thenReturn(MutableMessageBuilder.withPayload("custom payload"));

    this.adapter.setMessageBuilderFactory(factory);

    this.adapter.start();

    verify(factory, times(1)).withPayload(any());
    ArgumentCaptor<Message<String>> argument = ArgumentCaptor.forClass(Message.class);
    verify(this.mockMessageChannel).send(argument.capture());
    assertThat(argument.getValue().getPayload()).isEqualTo("custom payload");
  }

  @Test
  void consumeMessageAttachesOriginalMessageHeaderInManualMode() {

    setupSubscribeAndConvert();
    this.adapter.setAckMode(AckMode.MANUAL);
    this.adapter.start();
    verifyOriginalMessage();

  }

  @Test
  void consumeMessageAttachesOriginalMessageHeaderInAutoMode() {

    setupSubscribeAndConvert();
    this.adapter.setAckMode(AckMode.AUTO);
    this.adapter.start();
    verifyOriginalMessage();

  }

  @Test
  void consumeMessageAttachesOriginalMessageHeaderInAutoAckMode() {

    setupSubscribeAndConvert();
    this.adapter.setAckMode(AckMode.AUTO_ACK);
    this.adapter.start();
    verifyOriginalMessage();

  }

  @SuppressWarnings("unchecked")
  private void verifyOriginalMessage() {

    ArgumentCaptor<Message<?>> argument = ArgumentCaptor.forClass(Message.class);
    verify(this.mockMessageChannel).send(argument.capture());
    MessageHeaders headers = argument.getValue().getHeaders();
    assertThat(headers).containsKey(GcpPubSubHeaders.ORIGINAL_MESSAGE);
    assertThat(headers.get(GcpPubSubHeaders.ORIGINAL_MESSAGE)).isNotNull();
    assertThat(headers)
        .containsEntry(GcpPubSubHeaders.ORIGINAL_MESSAGE, mockAcknowledgeableMessage);
  }
}
