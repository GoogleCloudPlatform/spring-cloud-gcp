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

package com.google.cloud.spring.pubsub.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiService;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.pubsub.core.publisher.PubSubPublisherTemplate;
import com.google.cloud.spring.pubsub.core.test.allowed.AllowedPayload;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for the Pub/Sub template. */
@ExtendWith(MockitoExtension.class)
class PubSubTemplateTests {

  @Mock private PublisherFactory mockPublisherFactory;

  @Mock private SubscriberFactory mockSubscriberFactory;

  @Mock private Publisher mockPublisher;

  @Mock private Subscriber mockSubscriber;

  private PubSubTemplate pubSubTemplate;

  private PubsubMessage pubsubMessage;

  private SettableApiFuture<String> settableApiFuture;

  private PubSubTemplate createTemplate() {

    return new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);
  }

  private PubSubPublisherTemplate createPublisherTemplate() {
    PubSubPublisherTemplate pubSubPublisherTemplate =
        new PubSubPublisherTemplate(this.mockPublisherFactory);
    pubSubPublisherTemplate.setMessageConverter(
        new JacksonPubSubMessageConverter(new ObjectMapper()));
    return pubSubPublisherTemplate;
  }

  @BeforeEach
  void setUp() {
    this.pubSubTemplate = createTemplate();
    this.settableApiFuture = SettableApiFuture.create();
    this.pubsubMessage =
        PubsubMessage.newBuilder().setData(ByteString.copyFrom("permanating".getBytes())).build();
  }

  @Test
  void testPublish() throws ExecutionException, InterruptedException {
    when(this.mockPublisherFactory.createPublisher("testTopic")).thenReturn(this.mockPublisher);
    when(this.mockPublisher.publish(isA(PubsubMessage.class))).thenReturn(this.settableApiFuture);
    this.settableApiFuture.set("result");
    CompletableFuture<String> future = this.pubSubTemplate.publish("testTopic", this.pubsubMessage);

    assertThat(future.get()).isEqualTo("result");
  }

  @Test
  void testPublish_String() {
    when(this.mockPublisherFactory.createPublisher("testTopic")).thenReturn(this.mockPublisher);
    when(this.mockPublisher.publish(isA(PubsubMessage.class))).thenReturn(this.settableApiFuture);
    this.pubSubTemplate.publish("testTopic", "testPayload");

    verify(this.mockPublisher, times(1)).publish(isA(PubsubMessage.class));
  }

  @Test
  void testPublish_Bytes() {
    when(this.mockPublisherFactory.createPublisher("testTopic")).thenReturn(this.mockPublisher);
    when(this.mockPublisher.publish(isA(PubsubMessage.class))).thenReturn(this.settableApiFuture);
    this.pubSubTemplate.publish("testTopic", "testPayload".getBytes());

    verify(this.mockPublisher, times(1)).publish(isA(PubsubMessage.class));
  }

  @Test
  void testPublish_Object() throws IOException {
    AllowedPayload allowedPayload = new AllowedPayload();
    allowedPayload.name = "allowed";
    allowedPayload.value = 12345;
    PubSubPublisherTemplate pubSubPublisherTemplate = spy(createPublisherTemplate());

    doAnswer(
            invocation -> {
              PubsubMessage message = invocation.getArgument(1);
              assertThat(message.getData().toStringUtf8())
                  .isEqualTo(
                      "{\"@class\":"
                          + "\"com.google.cloud.spring.pubsub.core.test.allowed.AllowedPayload\""
                          + ",\"name\":\"allowed\",\"value\":12345}");
              return null;
            })
        .when(pubSubPublisherTemplate)
        .publish(eq("test"), any());

    pubSubPublisherTemplate.publish("test", allowedPayload, null);
    verify(pubSubPublisherTemplate, times(1)).publish(eq("test"), isA(PubsubMessage.class));
  }

  @Test
  void testPublish_withHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("emperor of sand", "sultan's curse");
    headers.put("remission", "elephant man");

    when(this.mockPublisherFactory.createPublisher("testTopic")).thenReturn(this.mockPublisher);
    when(this.mockPublisher.publish(isA(PubsubMessage.class))).thenReturn(this.settableApiFuture);

    this.pubSubTemplate.publish("testTopic", "jaguar god", headers);

    verify(this.mockPublisher)
        .publish(
            argThat(
                message ->
                    message.getAttributesMap().get("emperor of sand").equals("sultan's curse")
                        && message.getAttributesMap().get("remission").equals("elephant man")));
  }

  @Test
  void testSend_noPublisher() {
    when(this.mockPublisherFactory.createPublisher("testTopic"))
        .thenThrow(new PubSubException("couldn't create the publisher."));

    assertThatThrownBy(() -> this.pubSubTemplate.publish("testTopic", this.pubsubMessage))
            .isInstanceOf(PubSubException.class)
            .hasMessage("couldn't create the publisher.");

  }

  @Test
  void testSend_onFailure() {

    when(this.mockPublisherFactory.createPublisher("testTopic")).thenReturn(this.mockPublisher);
    when(this.mockPublisher.publish(isA(PubsubMessage.class))).thenReturn(this.settableApiFuture);
    CompletableFuture<String> future = this.pubSubTemplate.publish("testTopic", this.pubsubMessage);
    this.settableApiFuture.setException(new Exception("future failed."));

    assertThatThrownBy(future::get)
        .isInstanceOf(ExecutionException.class)
        .hasStackTraceContaining("future failed.");
  }

  @Test
  void testPublish_onFailureWithPayload() {
    when(this.mockPublisherFactory.createPublisher("testTopic")).thenReturn(this.mockPublisher);
    when(this.mockPublisher.publish(isA(PubsubMessage.class))).thenReturn(this.settableApiFuture);
    CompletableFuture<String> future = this.pubSubTemplate.publish("testTopic", this.pubsubMessage);
    this.settableApiFuture.setException(new Exception("Publish failed"));
    assertThatThrownBy(future::get)
        .isInstanceOf(ExecutionException.class)
        .hasCauseInstanceOf(PubSubDeliveryException.class)
        .hasStackTraceContaining("Publish failed");
  }

  @Test
  void testSubscribe() {

    when(this.mockSubscriberFactory.createSubscriber(
            eq("testSubscription"), isA(MessageReceiver.class)))
            .thenReturn(this.mockSubscriber);
    when(this.mockSubscriber.startAsync()).thenReturn(mock(ApiService.class));

    Subscriber subscriber = this.pubSubTemplate.subscribe("testSubscription", message -> {});
    assertThat(subscriber).isEqualTo(this.mockSubscriber);
    verify(this.mockSubscriber, times(1)).startAsync();
  }
}
