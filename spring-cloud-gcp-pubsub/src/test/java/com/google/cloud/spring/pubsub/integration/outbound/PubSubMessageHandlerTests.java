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

package com.google.cloud.spring.pubsub.integration.outbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spring.core.util.MapBuilder;
import com.google.cloud.spring.pubsub.core.PubSubOperations;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.expression.Expression;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SettableListenableFuture;

/** Tests for the Pub/Sub message handler. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PubSubMessageHandlerTests {

  @Mock private PubSubOperations pubSubTemplate;

  private PubSubMessageHandler adapter;

  private Message<?> message;

  @BeforeEach
  void setUp() {
    this.message =
        new GenericMessage<byte[]>(
            "testPayload".getBytes(),
            new MapBuilder<String, Object>().put("key1", "value1").put("key2", "value2").build());
    SettableListenableFuture<String> future = new SettableListenableFuture<>();
    future.set("benfica");
    when(this.pubSubTemplate.publish(eq("testTopic"), eq("testPayload".getBytes()), anyMap()))
        .thenReturn(future);
    this.adapter = new PubSubMessageHandler(this.pubSubTemplate, "testTopic");
  }

  @Test
  void testPublish() {
    this.adapter.handleMessage(this.message);
    verify(this.pubSubTemplate).publish(eq("testTopic"), eq("testPayload".getBytes()), anyMap());
  }

  @Test
  void testPublishDynamicTopic() {
    Message<?> dynamicMessage =
        new GenericMessage<byte[]>(
            "testPayload".getBytes(),
            new MapBuilder<String, Object>()
                .put("key1", "value1")
                .put("key2", "value2")
                .put(GcpPubSubHeaders.TOPIC, "dynamicTopic")
                .build());
    this.adapter.handleMessage(dynamicMessage);
    verify(this.pubSubTemplate).publish(eq("dynamicTopic"), eq("testPayload".getBytes()), anyMap());
  }

  @Test
  void testSendToExpressionTopic() {
    this.adapter.setTopicExpressionString("headers['sendToTopic']");
    this.adapter.onInit();
    Message<?> expressionMessage =
        new GenericMessage<byte[]>(
            "testPayload".getBytes(),
            new MapBuilder<String, Object>()
                .put("key1", "value1")
                .put("key2", "value2")
                .put("sendToTopic", "expressionTopic")
                .build());
    this.adapter.handleMessage(expressionMessage);
    verify(this.pubSubTemplate)
        .publish(eq("expressionTopic"), eq("testPayload".getBytes()), anyMap());
  }

  @Test
  void testPublishSync() {
    this.adapter.setSync(true);
    Expression timeout = spy(this.adapter.getPublishTimeoutExpression());
    this.adapter.setPublishTimeoutExpression(timeout);

    this.adapter.handleMessage(this.message);
    verify(timeout).getValue(isNull(), eq(this.message), eq(Long.class));
  }

  @Test
  void testPublishCallback() {
    ListenableFutureCallback<String> callbackSpy =
        spy(
            new ListenableFutureCallback<String>() {
              @Override
              public void onFailure(Throwable ex) {}

              @Override
              public void onSuccess(String result) {}
            });

    this.adapter.setPublishCallback(callbackSpy);

    this.adapter.handleMessage(this.message);

    assertThat(this.adapter.getPublishCallback()).isSameAs(callbackSpy);

    verify(callbackSpy).onSuccess("benfica");
  }

  @Test
  void testSetPublishTimeoutExpressionStringWithNull() {

    assertThatThrownBy(() -> this.adapter.setPublishTimeoutExpressionString(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Publish timeout expression can't be null.");
  }

  @Test
  void testPublishTimeoutExpressionString() {
    String expressionString = "15";

    this.adapter.setPublishTimeoutExpressionString(expressionString);

    Expression exp = this.adapter.getPublishTimeoutExpression();

    assertThat(exp.getValue()).isEqualTo(Integer.parseInt(expressionString));
  }

  @Test
  void testPublishTimeout() {
    long timeout = 15;

    this.adapter.setPublishTimeout(timeout);

    Expression exp = this.adapter.getPublishTimeoutExpression();

    assertThat(exp.getValue()).isEqualTo(timeout);
  }

  @Test
  void testIsSync() {
    this.adapter.setSync(true);

    assertThat(this.adapter.isSync()).isTrue();

    this.adapter.setSync(false);

    assertThat(this.adapter.isSync()).isFalse();
  }

  @Test
  void testTopicWithNull() {

    assertThatThrownBy(() -> this.adapter.setTopic(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("The topic can't be null or empty");

  }

  @Test
  void testTopic() {
    String topic = "pubsub";
    this.adapter.setTopic(topic);

    Expression exp = this.adapter.getTopicExpression();

    assertThat(exp.getClass()).isEqualTo(LiteralExpression.class);
    assertThat(exp.getValue()).isEqualTo(topic);
  }

  @Test
  void testTopicExpression() {
    Expression expected = new ValueExpression<>("topic");

    this.adapter.setTopicExpression(expected);

    assertThat(this.adapter.getTopicExpression()).isEqualTo(expected);
  }

  @Test
  void testSetHeaderMapperWithNull() {

    assertThatThrownBy(() ->  this.adapter.setHeaderMapper(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("The header mapper can't be null.");
  }

  @Test
  void testPublishWithOrderingKey() {
    this.message =
        new GenericMessage<byte[]>(
            "testPayload".getBytes(),
            new MapBuilder<String, Object>().put(GcpPubSubHeaders.ORDERING_KEY, "key1").build());

    this.adapter.handleMessage(this.message);
    verify(this.pubSubTemplate)
        .publish(
            "testTopic",
            "testPayload".getBytes(),
            Collections.singletonMap(GcpPubSubHeaders.ORDERING_KEY, "key1"));
  }

  @Test
  void publishWithSuccessCallback() throws Exception {

    SettableListenableFuture<String> future = new SettableListenableFuture<>();
    future.set("published12345");
    when(this.pubSubTemplate.publish(eq("testTopic"), eq("testPayload"), anyMap()))
        .thenReturn(future);

    Message<String> testMessage =
        new GenericMessage<String>("testPayload", Collections.singletonMap("message_id", "123"));

    AtomicReference<String> messageIdRef = new AtomicReference<>();
    AtomicReference<String> ackIdRef = new AtomicReference<>();

    this.adapter.setSuccessCallback(
        (ackId, message) -> {
          messageIdRef.set(message.getHeaders().get("message_id", String.class));
          ackIdRef.set(ackId);
        });

    this.adapter.handleMessage(testMessage);
    Awaitility.await().atMost(Duration.ofSeconds(1)).untilAtomic(messageIdRef, notNullValue());

    assertThat(messageIdRef).hasValue("123");
    assertThat(ackIdRef).hasValue("published12345");
  }

  @Test
  void publishWithFailureCallback() throws Exception {

    SettableListenableFuture<String> future = new SettableListenableFuture<>();
    future.setException(new RuntimeException("boom!"));
    when(this.pubSubTemplate.publish(eq("testTopic"), eq("testPayload"), anyMap()))
        .thenReturn(future);

    Message<String> testMessage =
        new GenericMessage("testPayload", Collections.singletonMap("message_id", "123"));

    AtomicReference<Throwable> failureCauseRef = new AtomicReference<>();
    AtomicReference<String> messageIdRef = new AtomicReference<>();

    this.adapter.setFailureCallback(
        (exception, message) -> {
          failureCauseRef.set(exception);
          messageIdRef.set(message.getHeaders().get("message_id", String.class));
        });

    this.adapter.handleMessage(testMessage);
    Awaitility.await().atMost(Duration.ofSeconds(1)).untilAtomic(messageIdRef, notNullValue());

    assertThat(messageIdRef).hasValue("123");
    Throwable cause = failureCauseRef.get();
    assertThat(cause).isInstanceOf(RuntimeException.class).hasMessage("boom!");
  }
}
