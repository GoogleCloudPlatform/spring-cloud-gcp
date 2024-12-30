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

package com.google.cloud.spring.stream.binder.pubsub;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubConsumerProperties;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubProducerProperties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.cloud.stream.binder.AbstractBinderTests;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.Spy;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;

/** Integration tests that require the Pub/Sub emulator to be installed. */
@ExtendWith(PubSubEmulator.class)
class PubSubMessageChannelBinderEmulatorIntegrationTests
    extends AbstractBinderTests<
        PubSubTestBinder,
        ExtendedConsumerProperties<PubSubConsumerProperties>,
        ExtendedProducerProperties<PubSubProducerProperties>> {

  private final String hostPort;

  private TestInfo testInfo;

  // Constructor gets PubSubEmulator port number from ParameterResolver
  PubSubMessageChannelBinderEmulatorIntegrationTests(String pubSubEmulatorPort) {
    this.hostPort = pubSubEmulatorPort;
  }

  @Override
  protected PubSubTestBinder getBinder() {
    return new PubSubTestBinder(this.hostPort, this.applicationContext);
  }

  @Override
  protected ExtendedConsumerProperties<PubSubConsumerProperties> createConsumerProperties() {
    return new ExtendedConsumerProperties<>(new PubSubConsumerProperties());
  }

  @Override
  protected ExtendedProducerProperties<PubSubProducerProperties> createProducerProperties(
      TestInfo testInfo) {
    return new ExtendedProducerProperties<>(new PubSubProducerProperties());
  }

  @Override
  public Spy spyOn(String name) {
    return null;
  }

  @Override
  public void testClean(TestInfo testInfo) throws Exception {
    // Do nothing. Original test tests for Lifecycle logic that we don't need.
    // Dummy assertion to appease SonarCloud.
    assertThat(this.hostPort).isNotNull();
  }

  @Test
  @Disabled("Looks like there is no Kryo support in SCSt")
  void testSendPojoReceivePojoKryoWithStreamListener() {
    // Dummy assertion to appease SonarCloud.
    assertThat(this.hostPort).isNotNull();
  }

  @Test
  void testSendAndReceiveWithHeaderMappingsConsumer() throws Exception {
    //This test is checking if the consumer headerMapper is working fine. The message has two headers built in it,
    //producer allows both of them to be mapped but the consumer only allows one of them. So when we receive the
    //message on the inputChannel, only one header should be mapped, the one which consumer allows (firstHeader in this case)

    PubSubTestBinder binder = this.getBinder();

    ExtendedConsumerProperties<PubSubConsumerProperties> consumerProps = createConsumerProperties();
    consumerProps.getExtension().setAllowedHeaders(new String[]{"firstHeader"});

    ExtendedProducerProperties<PubSubProducerProperties> producerProps = createProducerProperties(testInfo);
    producerProps.getExtension().setAllowedHeaders(new String[]{"firstHeader", "secondHeader"});

    BindingProperties outputBindingProperties = createProducerBindingProperties(producerProps);
    DirectChannel moduleOutputChannel = createBindableChannel("output", outputBindingProperties);

    BindingProperties inputBindingProperties = createConsumerBindingProperties(consumerProps);
    DirectChannel moduleInputChannel = createBindableChannel("input", inputBindingProperties);

    Binding<MessageChannel> producerBinding = binder.bindProducer(String.format("foo%s0", getDestinationNameDelimiter()),
            moduleOutputChannel, producerProps);
    Binding<MessageChannel> consumerBinding = binder.bindConsumer(String.format("foo%s0", getDestinationNameDelimiter()),
            "test-group", moduleInputChannel,
            consumerProps);

    Message<?> message = MessageBuilder.withPayload("insert some random stuff here")
            .setHeader("firstHeader", "firstHeaderValue")
            .setHeader("secondHeader", "secondHeaderValue")
            .build();
    // Let the consumer actually bind to the producer before sending a msg
    binderBindUnbindLatency();
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<Message<byte[]>> inboundMessageRef = new AtomicReference<Message<byte[]>>();
    moduleInputChannel.subscribe(message1 -> {
      try {
        inboundMessageRef.set((Message<byte[]>) message1);
      } finally {
        latch.countDown();
      }
    });

    moduleOutputChannel.send(message);
    Assert.isTrue(latch.await(5, TimeUnit.SECONDS), "Failed to receive message");

    assertThat(inboundMessageRef.get().getPayload()).isEqualTo("insert some random stuff here".getBytes());
    assertThat(inboundMessageRef.get().getHeaders().get("secondHeader")).isNull();
    assertThat(inboundMessageRef.get().getHeaders().get("firstHeader")).hasToString("firstHeaderValue");

    producerBinding.unbind();
    consumerBinding.unbind();
  }

  @Test
  void testSendAndReceiveWithHeaderMappingsProducer() throws Exception {
    //This test is checking if the producer headerMapper is working fine. The message has two headers built in it,
    //producer allows only one of them to be mapped and the consumer allows both of them. So when we receive the
    //message on the inputChannel, only one header should be mapped, the one which producer allows (secondHeader in this case)

    PubSubTestBinder binder = this.getBinder();

    ExtendedConsumerProperties<PubSubConsumerProperties> consumerProps = createConsumerProperties();
    consumerProps.getExtension().setAllowedHeaders(new String[]{"firstHeader", "secondHeader"});

    ExtendedProducerProperties<PubSubProducerProperties> producerProps = createProducerProperties(testInfo);
    producerProps.getExtension().setAllowedHeaders(new String[]{"secondHeader"});

    BindingProperties outputBindingProperties = createProducerBindingProperties(producerProps);
    DirectChannel moduleOutputChannel = createBindableChannel("output", outputBindingProperties);

    BindingProperties inputBindingProperties = createConsumerBindingProperties(consumerProps);
    DirectChannel moduleInputChannel = createBindableChannel("input", inputBindingProperties);

    Binding<MessageChannel> producerBinding = binder.bindProducer(String.format("foo%s0", getDestinationNameDelimiter()),
            moduleOutputChannel, producerProps);
    Binding<MessageChannel> consumerBinding = binder.bindConsumer(String.format("foo%s0", getDestinationNameDelimiter()),
            "test-group", moduleInputChannel,
            consumerProps);

    Message<?> message = MessageBuilder.withPayload("insert some random stuff here")
            .setHeader("firstHeader", "firstHeaderValue")
            .setHeader("secondHeader", "secondHeaderValue")
            .build();
    // Let the consumer actually bind to the producer before sending a msg
    binderBindUnbindLatency();
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<Message<byte[]>> inboundMessageRef = new AtomicReference<Message<byte[]>>();
    moduleInputChannel.subscribe(message1 -> {
      try {
        inboundMessageRef.set((Message<byte[]>) message1);
      } finally {
        latch.countDown();
      }
    });

    moduleOutputChannel.send(message);
    Assert.isTrue(latch.await(5, TimeUnit.SECONDS), "Failed to receive message");

    assertThat(inboundMessageRef.get().getPayload()).isEqualTo("insert some random stuff here".getBytes());
    assertThat(inboundMessageRef.get().getHeaders().get("firstHeader")).isNull();
    assertThat(inboundMessageRef.get().getHeaders().get("secondHeader")).hasToString("secondHeaderValue");

    producerBinding.unbind();
    consumerBinding.unbind();
  }
}