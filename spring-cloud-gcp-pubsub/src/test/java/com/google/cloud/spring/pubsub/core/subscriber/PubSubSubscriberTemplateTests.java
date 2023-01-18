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

package com.google.cloud.spring.pubsub.core.subscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.cloud.spring.pubsub.support.converter.ConvertedAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.converter.ConvertedBasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import com.google.protobuf.Empty;
import com.google.pubsub.v1.AcknowledgeRequest;
import com.google.pubsub.v1.ModifyAckDeadlineRequest;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Unit tests for {@link PubSubSubscriberTemplate}. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PubSubSubscriberTemplateTests {

  private PubSubSubscriberTemplate pubSubSubscriberTemplate;

  private final PubsubMessage pubsubMessage = PubsubMessage.newBuilder().build();

  @Mock private MessageReceiver messageReceiver;

  @Mock private AckReplyConsumer ackReplyConsumer;

  @Mock private SubscriberFactory subscriberFactory;

  @Mock private Subscriber subscriber;

  @Mock private PubSubMessageConverter messageConverter;

  @Mock private Consumer<BasicAcknowledgeablePubsubMessage> consumer;

  @Captor private ArgumentCaptor<BasicAcknowledgeablePubsubMessage> message;

  @Mock private Consumer<ConvertedBasicAcknowledgeablePubsubMessage<Boolean>> convertedConsumer;

  @Captor
  private ArgumentCaptor<ConvertedBasicAcknowledgeablePubsubMessage<Boolean>> convertedMessage;

  @Mock private SubscriberStub subscriberStub;

  @Mock private UnaryCallable<PullRequest, PullResponse> pullCallable;

  @Mock private UnaryCallable<AcknowledgeRequest, Empty> ackCallable;

  @Mock private UnaryCallable<ModifyAckDeadlineRequest, Empty> modifyAckDeadlineCallable;

  @Mock private ApiFuture<PullResponse> pullApiFuture;

  @Mock private ApiFuture<Empty> ackApiFuture;

  @BeforeEach
  void setUp() throws ExecutionException, InterruptedException {
    reset(this.subscriberFactory);
    reset(this.subscriberStub);
    reset(this.subscriber);
    reset(this.messageReceiver);
    reset(this.pullApiFuture);
    reset(this.ackApiFuture);

    when(this.subscriberFactory.getProjectId()).thenReturn("testProject");

    // for subscribe with MessageReceiver
    when(this.subscriberFactory.createSubscriber(any(String.class), any(MessageReceiver.class)))
        .then(
            invocation -> {
              this.messageReceiver = invocation.getArgument(1);
              return this.subscriber;
            });

    when(this.subscriber.startAsync())
        .then(
            invocation -> {
              this.messageReceiver.receiveMessage(this.pubsubMessage, this.ackReplyConsumer);
              return null;
            });

    // for pull
    when(this.subscriberFactory.createPullRequest(
            any(String.class), any(Integer.class), any(Boolean.class)))
        .then(
            invocation ->
                PullRequest.newBuilder().setSubscription(invocation.getArgument(0)).build());

    when(this.subscriberStub.acknowledgeCallable()).thenReturn(this.ackCallable);
    when(this.subscriberStub.modifyAckDeadlineCallable())
        .thenReturn(this.modifyAckDeadlineCallable);

    when(this.ackCallable.futureCall(any(AcknowledgeRequest.class))).thenReturn(this.ackApiFuture);

    when(this.modifyAckDeadlineCallable.futureCall(any(ModifyAckDeadlineRequest.class)))
        .thenReturn(this.ackApiFuture);

    doAnswer(
            invocation -> {
              Runnable runnable = invocation.getArgument(0);
              runnable.run();
              return null;
            })
        .when(this.ackApiFuture)
        .addListener(any(Runnable.class), any(Executor.class));

    when(this.ackApiFuture.isDone()).thenReturn(true);

    doNothing().when(this.ackReplyConsumer).ack();
    doNothing().when(this.ackReplyConsumer).nack();

    // for pull future
    when(this.pullCallable.futureCall(any(PullRequest.class))).thenReturn(this.pullApiFuture);

    doAnswer(
            invocation -> {
              Runnable runnable = invocation.getArgument(0);
              runnable.run();
              return null;
            })
        .when(this.pullApiFuture)
        .addListener(any(Runnable.class), any(Executor.class));
    when(this.pullApiFuture.isDone()).thenReturn(true);
    when(this.pullApiFuture.get())
        .thenReturn(
            PullResponse.newBuilder()
                .addReceivedMessages(ReceivedMessage.newBuilder().setMessage(this.pubsubMessage))
                .build());

    // create objects under test
    when(this.subscriberFactory.createSubscriberStub(any())).thenReturn(this.subscriberStub);
    when(this.subscriberStub.pullCallable()).thenReturn(this.pullCallable);
    when(this.pullCallable.call(any(PullRequest.class)))
        .thenReturn(
            PullResponse.newBuilder()
                .addReceivedMessages(ReceivedMessage.newBuilder().setMessage(this.pubsubMessage))
                .build());

    // create object under test
    this.pubSubSubscriberTemplate = spy(new PubSubSubscriberTemplate(this.subscriberFactory));
    this.pubSubSubscriberTemplate.setMessageConverter(this.messageConverter);
  }

  @Test
  void testSubscribe_AndManualAck()
      throws InterruptedException, ExecutionException, TimeoutException {
    this.pubSubSubscriberTemplate.subscribe("sub1", this.consumer);

    verify(this.subscriber).startAsync();
    verify(this.consumer).accept(this.message.capture());

    TestCompletableFutureCallback completableFutureCallback = new TestCompletableFutureCallback();

    CompletableFuture<Void> completableFuture = this.message.getValue().ack();

    assertThat(completableFuture).isNotNull();

    completableFuture.whenComplete(completableFutureCallback);
    completableFuture.get(10L, TimeUnit.SECONDS);

    assertThat(completableFuture.isDone()).isTrue();

    verify(this.ackReplyConsumer).ack();

    assertThat(completableFutureCallback.getThrowable()).isNull();
  }

  @Test
  void testSubscribe_AndManualNack()
      throws InterruptedException, ExecutionException, TimeoutException {
    this.pubSubSubscriberTemplate.subscribe("sub1", this.consumer);

    verify(this.subscriber).startAsync();
    verify(this.consumer).accept(this.message.capture());

    TestCompletableFutureCallback completableFutureCallback = new TestCompletableFutureCallback();

    CompletableFuture<Void> completableFuture = this.message.getValue().nack();

    assertThat(completableFuture).isNotNull();

    completableFuture.whenComplete(completableFutureCallback);
    completableFuture.get(10L, TimeUnit.SECONDS);

    assertThat(completableFuture.isDone()).isTrue();

    verify(this.ackReplyConsumer).nack();

    assertThat(completableFutureCallback.getThrowable()).isNull();
  }

  @Test
  void testSubscribeAndConvert_AndManualAck()
      throws InterruptedException, ExecutionException, TimeoutException {
    this.pubSubSubscriberTemplate.subscribeAndConvert(
        "sub1", this.convertedConsumer, Boolean.class);

    verify(this.subscriber).startAsync();
    verify(this.messageConverter).fromPubSubMessage(this.pubsubMessage, Boolean.class);
    verify(this.convertedConsumer).accept(this.convertedMessage.capture());

    assertThat(this.convertedMessage.getValue().getPubsubMessage()).isSameAs(this.pubsubMessage);
    assertThat(this.convertedMessage.getValue().getProjectSubscriptionName().getProject())
        .isEqualTo("testProject");
    assertThat(this.convertedMessage.getValue().getProjectSubscriptionName().getSubscription())
        .isEqualTo("sub1");

    TestCompletableFutureCallback completableFutureCallback = new TestCompletableFutureCallback();

    CompletableFuture<Void> completableFuture = this.convertedMessage.getValue().ack();

    assertThat(completableFuture).isNotNull();

    completableFuture.whenComplete(completableFutureCallback);
    completableFuture.get(10L, TimeUnit.SECONDS);

    assertThat(completableFuture.isDone()).isTrue();

    verify(this.ackReplyConsumer).ack();

    assertThat(completableFutureCallback.getThrowable()).isNull();
  }

  @Test
  void testSubscribeAndConvert_AndManualNack()
      throws InterruptedException, ExecutionException, TimeoutException {
    this.pubSubSubscriberTemplate.subscribeAndConvert(
        "sub1", this.convertedConsumer, Boolean.class);

    verify(this.subscriber).startAsync();
    verify(this.messageConverter).fromPubSubMessage(this.pubsubMessage, Boolean.class);
    verify(this.convertedConsumer).accept(this.convertedMessage.capture());

    assertThat(this.convertedMessage.getValue().getPubsubMessage()).isSameAs(this.pubsubMessage);
    assertThat(this.convertedMessage.getValue().getProjectSubscriptionName().getProject())
        .isEqualTo("testProject");
    assertThat(this.convertedMessage.getValue().getProjectSubscriptionName().getSubscription())
        .isEqualTo("sub1");

    TestCompletableFutureCallback completableFutureCallback = new TestCompletableFutureCallback();

    CompletableFuture<Void> completableFuture = this.convertedMessage.getValue().nack();

    assertThat(completableFuture).isNotNull();

    completableFuture.whenComplete(completableFutureCallback);
    completableFuture.get(10L, TimeUnit.SECONDS);

    assertThat(completableFuture.isDone()).isTrue();

    verify(this.ackReplyConsumer).nack();

    assertThat(completableFutureCallback.getThrowable()).isNull();
  }

  @Test
  void destroyingBeanClosesSubscriberStub() {
    this.pubSubSubscriberTemplate = new PubSubSubscriberTemplate(subscriberFactory);

    this.pubSubSubscriberTemplate.pull("sub2", 1, true);

    verify(this.subscriberFactory).createSubscriberStub("sub2");

    verify(this.subscriberStub, times(0)).close();

    this.pubSubSubscriberTemplate.destroy();
    verify(this.subscriberStub, times(1)).close();
  }

  @Test
  void testPull_AndManualAck()
      throws InterruptedException, ExecutionException, TimeoutException {

    List<AcknowledgeablePubsubMessage> result = this.pubSubSubscriberTemplate.pull("sub2", 1, true);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getPubsubMessage()).isSameAs(this.pubsubMessage);
    assertThat(result.get(0).getProjectSubscriptionName().getProject()).isEqualTo("testProject");
    assertThat(result.get(0).getProjectSubscriptionName().getSubscription()).isEqualTo("sub2");

    AcknowledgeablePubsubMessage acknowledgeablePubsubMessage = result.get(0);
    assertThat(acknowledgeablePubsubMessage.getAckId()).isNotNull();

    TestCompletableFutureCallback completableFutureCallback = new TestCompletableFutureCallback();

    CompletableFuture<Void> completableFuture = this.pubSubSubscriberTemplate.ack(result);

    assertThat(completableFuture).isNotNull();

    completableFuture.whenComplete(completableFutureCallback);
    completableFuture.get(10L, TimeUnit.SECONDS);

    assertThat(completableFuture.isDone()).isTrue();

    assertThat(completableFutureCallback.getThrowable()).isNull();
  }

  @Test
  void testPull_AndManualNack()
      throws InterruptedException, ExecutionException, TimeoutException {
    List<AcknowledgeablePubsubMessage> result = this.pubSubSubscriberTemplate.pull("sub2", 1, true);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getPubsubMessage()).isSameAs(this.pubsubMessage);
    assertThat(result.get(0).getProjectSubscriptionName().getProject()).isEqualTo("testProject");
    assertThat(result.get(0).getProjectSubscriptionName().getSubscription()).isEqualTo("sub2");

    AcknowledgeablePubsubMessage acknowledgeablePubsubMessage = result.get(0);
    assertThat(acknowledgeablePubsubMessage.getAckId()).isNotNull();

    TestCompletableFutureCallback completableFutureCallback = new TestCompletableFutureCallback();

    CompletableFuture<Void> completableFuture = this.pubSubSubscriberTemplate.nack(result);

    assertThat(completableFuture).isNotNull();

    completableFuture.whenComplete(completableFutureCallback);
    completableFuture.get(10L, TimeUnit.SECONDS);

    assertThat(completableFuture.isDone()).isTrue();

    assertThat(completableFutureCallback.getThrowable()).isNull();
  }

  @Test
  void testPull_AndManualMultiSubscriptionAck()
      throws InterruptedException, ExecutionException, TimeoutException {
    ExecutorService mockExecutor = Mockito.mock(ExecutorService.class);
    this.pubSubSubscriberTemplate.setAckExecutor(mockExecutor);

    List<AcknowledgeablePubsubMessage> result1 =
        this.pubSubSubscriberTemplate.pull("sub1", 1, true);
    List<AcknowledgeablePubsubMessage> result2 =
        this.pubSubSubscriberTemplate.pull("sub2", 1, true);
    Set<AcknowledgeablePubsubMessage> combinedMessages = new HashSet<>(result1);
    combinedMessages.addAll(result2);

    assertThat(combinedMessages).hasSize(2);

    TestCompletableFutureCallback completableFutureCallback = new TestCompletableFutureCallback();

    CompletableFuture<Void> completableFuture = this.pubSubSubscriberTemplate.ack(combinedMessages);
    assertThat(completableFuture).isNotNull();

    completableFuture.whenComplete(completableFutureCallback);
    completableFuture.get(10L, TimeUnit.SECONDS);

    assertThat(completableFuture.isDone()).isTrue();
    assertThat(completableFutureCallback.getThrowable()).isNull();
    verify(this.ackCallable, times(2)).futureCall(any(AcknowledgeRequest.class));
    verify(this.ackApiFuture, times(2)).addListener(any(), same(mockExecutor));
  }

  @Test
  void testPullAsync_AndManualAck()
      throws InterruptedException, ExecutionException, TimeoutException {

    CompletableFuture<List<AcknowledgeablePubsubMessage>> asyncResult =
        this.pubSubSubscriberTemplate.pullAsync("sub", 1, true);

    List<AcknowledgeablePubsubMessage> result = asyncResult.get(10L, TimeUnit.SECONDS);

    assertThat(asyncResult.isDone()).isTrue();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getPubsubMessage()).isSameAs(this.pubsubMessage);
    assertThat(result.get(0).getProjectSubscriptionName().getProject()).isEqualTo("testProject");
    assertThat(result.get(0).getProjectSubscriptionName().getSubscription()).isEqualTo("sub");

    AcknowledgeablePubsubMessage acknowledgeablePubsubMessage = result.get(0);
    assertThat(acknowledgeablePubsubMessage.getAckId()).isNotNull();

    TestCompletableFutureCallback completableFutureCallback =
        new TestCompletableFutureCallback();

    CompletableFuture<Void> completableFuture = this.pubSubSubscriberTemplate.ack(result);

    assertThat(completableFuture).isNotNull();

    completableFuture.whenComplete(completableFutureCallback);
    completableFuture.get(10L, TimeUnit.SECONDS);

    assertThat(completableFuture.isDone()).isTrue();

    assertThat(completableFutureCallback.getThrowable()).isNull();
  }

  @Test
  void testPullAndAck() {
    List<PubsubMessage> result = this.pubSubSubscriberTemplate.pullAndAck("sub2", 1, true);

    assertThat(result).hasSize(1);

    PubsubMessage pubsubMessage = result.get(0);
    assertThat(pubsubMessage).isSameAs(this.pubsubMessage);

    verify(this.pubSubSubscriberTemplate, times(1)).ack(any());
  }

  @Test
  void testPullAndAck_NoMessages() {
    when(this.pullCallable.call(any(PullRequest.class)))
        .thenReturn(PullResponse.newBuilder().build());

    List<PubsubMessage> result = this.pubSubSubscriberTemplate.pullAndAck("sub2", 1, true);

    assertThat(result).isEmpty();

    verify(this.pubSubSubscriberTemplate, never()).ack(any());
  }

  @Test
  void testPullAndAckAsync()
      throws InterruptedException, ExecutionException, TimeoutException {
    CompletableFuture<List<PubsubMessage>> asyncResult =
        this.pubSubSubscriberTemplate.pullAndAckAsync("sub2", 1, true);

    List<PubsubMessage> result = asyncResult.get(10L, TimeUnit.SECONDS);
    assertThat(asyncResult.isDone()).isTrue();

    assertThat(result).hasSize(1);

    PubsubMessage pubsubMessage = result.get(0);
    assertThat(pubsubMessage).isSameAs(this.pubsubMessage);

    verify(this.pubSubSubscriberTemplate, times(1)).ack(any());
  }

  @Test
  void testPullAndAckAsync_NoMessages()
      throws InterruptedException, ExecutionException, TimeoutException {
    when(this.pullApiFuture.get()).thenReturn(PullResponse.newBuilder().build());

    CompletableFuture<List<PubsubMessage>> asyncResult =
        this.pubSubSubscriberTemplate.pullAndAckAsync("sub2", 1, true);

    List<PubsubMessage> result = asyncResult.get(10L, TimeUnit.SECONDS);
    assertThat(asyncResult.isDone()).isTrue();

    assertThat(result).isEmpty();

    verify(this.pubSubSubscriberTemplate, never()).ack(any());
  }

  @Test
  void testPullAndConvert() {
    List<ConvertedAcknowledgeablePubsubMessage<BigInteger>> result =
        this.pubSubSubscriberTemplate.pullAndConvert("sub2", 1, true, BigInteger.class);

    verify(this.messageConverter).fromPubSubMessage(this.pubsubMessage, BigInteger.class);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getPubsubMessage()).isSameAs(this.pubsubMessage);
    assertThat(result.get(0).getProjectSubscriptionName().getProject()).isEqualTo("testProject");
    assertThat(result.get(0).getProjectSubscriptionName().getSubscription()).isEqualTo("sub2");
  }

  @Test
  void testPullAndConvertAsync()
      throws InterruptedException, ExecutionException, TimeoutException {
    CompletableFuture<List<ConvertedAcknowledgeablePubsubMessage<BigInteger>>> asyncResult =
        this.pubSubSubscriberTemplate.pullAndConvertAsync("sub2", 1, true, BigInteger.class);

    List<ConvertedAcknowledgeablePubsubMessage<BigInteger>> result =
        asyncResult.get(10L, TimeUnit.SECONDS);
    assertThat(asyncResult.isDone()).isTrue();

    verify(this.messageConverter).fromPubSubMessage(this.pubsubMessage, BigInteger.class);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getPubsubMessage()).isSameAs(this.pubsubMessage);
    assertThat(result.get(0).getProjectSubscriptionName().getProject()).isEqualTo("testProject");
    assertThat(result.get(0).getProjectSubscriptionName().getSubscription()).isEqualTo("sub2");
  }

  @Test
  void testPullNext() {

    PubsubMessage message = this.pubSubSubscriberTemplate.pullNext("sub2");

    assertThat(message).isSameAs(this.pubsubMessage);

    verify(this.subscriberFactory).createPullRequest("sub2", 1, true);
    verify(this.pubSubSubscriberTemplate, times(1)).ack(any());
  }

  @Test
  void testPullNext_NoMessages() {
    when(this.pullCallable.call(any(PullRequest.class)))
        .thenReturn(PullResponse.newBuilder().build());

    PubsubMessage message = this.pubSubSubscriberTemplate.pullNext("sub2");

    assertThat(message).isNull();

    verify(this.subscriberFactory).createPullRequest("sub2", 1, true);
    verify(this.pubSubSubscriberTemplate, never()).ack(any());
  }

  @Test
  void testPullNextAsync()
      throws InterruptedException, ExecutionException, TimeoutException {
    CompletableFuture<PubsubMessage> asyncResult =
        this.pubSubSubscriberTemplate.pullNextAsync("sub2");

    PubsubMessage message = asyncResult.get(10L, TimeUnit.SECONDS);
    assertThat(asyncResult.isDone()).isTrue();

    assertThat(message).isSameAs(this.pubsubMessage);

    verify(this.subscriberFactory).createPullRequest("sub2", 1, true);
    verify(this.pubSubSubscriberTemplate, times(1)).ack(any());
  }

  @Test
  void testPullNextAsync_NoMessages()
      throws InterruptedException, ExecutionException, TimeoutException {
    when(this.pullApiFuture.get()).thenReturn(PullResponse.newBuilder().build());

    CompletableFuture<PubsubMessage> asyncResult =
        this.pubSubSubscriberTemplate.pullNextAsync("sub2");

    PubsubMessage message = asyncResult.get(10L, TimeUnit.SECONDS);
    assertThat(asyncResult.isDone()).isTrue();

    assertThat(message).isNull();

    verify(this.subscriberFactory).createPullRequest("sub2", 1, true);
    verify(this.pubSubSubscriberTemplate, never()).ack(any());
  }

  private static class TestCompletableFutureCallback implements BiConsumer<Void, Throwable> {

    private Throwable throwable;

    public Throwable getThrowable() {
      return this.throwable;
    }

    @Override
    public void accept(Void unused, Throwable throwable) {

    }
  }
}
