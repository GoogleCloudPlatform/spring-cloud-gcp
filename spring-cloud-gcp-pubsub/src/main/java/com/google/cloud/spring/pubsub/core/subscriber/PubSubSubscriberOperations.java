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

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.converter.ConvertedAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.converter.ConvertedBasicAcknowledgeablePubsubMessage;
import com.google.pubsub.v1.PubsubMessage;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * An abstraction for Google Cloud Pub/Sub subscription / pulling operations.
 *
 * @since 1.1
 */
public interface PubSubSubscriberOperations {

  /**
   * Add a callback method to an existing subscription.
   *
   * <p>The created {@link Subscriber} is returned so it can be stopped.
   *
   * @param subscription short subscription name, e.g., "subscriptionName", or the
   *     fully-qualified subscription name in the {@code
   *     projects/[project_name]/subscriptions/[subscription_name]} format
   * @param messageConsumer the callback method triggered when new messages arrive
   * @return subscriber listening to new messages
   * @since 1.1
   */
  Subscriber subscribe(
      String subscription, Consumer<BasicAcknowledgeablePubsubMessage> messageConsumer);

  /**
   * Add a callback method to an existing subscription that receives Pub/Sub messages converted to
   * the requested payload type.
   *
   * <p>The created {@link Subscriber} is returned so it can be stopped.
   *
   * @param subscription short subscription name, e.g., "subscriptionName", or the
   *     fully-qualified subscription name in the {@code
   *     projects/[project_name]/subscriptions/[subscription_name]} format
   * @param messageConsumer the callback method triggered when new messages arrive
   * @param payloadType the type to which the payload of the Pub/Sub message should be converted
   * @param <T> the type of the payload
   * @return subscriber listening to new messages
   * @since 1.1
   */
  <T> Subscriber subscribeAndConvert(
      String subscription,
      Consumer<ConvertedBasicAcknowledgeablePubsubMessage<T>> messageConsumer,
      Class<T> payloadType);

  /**
   * Pull and auto-acknowledge a number of messages from a Google Cloud Pub/Sub subscription.
   *
   * @param subscription short subscription name, e.g., "subscriptionName", or the
   *     fully-qualified subscription name in the {@code
   *     projects/[project_name]/subscriptions/[subscription_name]} format
   * @param maxMessages the maximum number of pulled messages. If this value is null then up to
   *     Integer.MAX_VALUE messages will be requested.
   * @param returnImmediately returns immediately even if subscription doesn't contain enough
   *     messages to satisfy {@code maxMessages}. Setting this parameter to {@code true} is not
   *     recommended as it may result in long delays in message delivery.
   * @return the list of received messages
   */
  List<PubsubMessage> pullAndAck(
      String subscription, Integer maxMessages, Boolean returnImmediately);

  /**
   * Asynchronously pull and auto-acknowledge a number of messages from a Google Cloud Pub/Sub
   * subscription.
   *
   * @param subscription short subscription name, e.g., "subscriptionName", or the
   *     fully-qualified subscription name in the {@code
   *     projects/[project_name]/subscriptions/[subscription_name]} format
   * @param maxMessages the maximum number of pulled messages. If this value is null then up to
   *     Integer.MAX_VALUE messages will be requested.
   * @param returnImmediately returns immediately even if subscription doesn't contain enough
   *     messages to satisfy {@code maxMessages}. Setting this parameter to {@code true} is not
   *     recommended as it may result in long delays in message delivery.
   * @return the CompletableFuture for the asynchronous execution, returning the list of received
   *     acknowledgeable messages
   * @since 1.2.3
   */
  CompletableFuture<List<PubsubMessage>> pullAndAckAsync(
      String subscription, Integer maxMessages, Boolean returnImmediately);

  /**
   * Pull a number of messages from a Google Cloud Pub/Sub subscription.
   *
   * @param subscription short subscription name, e.g., "subscriptionName", or the
   *     fully-qualified subscription name in the {@code
   *     projects/[project_name]/subscriptions/[subscription_name]} format
   * @param maxMessages the maximum number of pulled messages. If this value is null then up to
   *     Integer.MAX_VALUE messages will be requested.
   * @param returnImmediately returns immediately even if subscription doesn't contain enough
   *     messages to satisfy {@code maxMessages}. Setting this parameter to {@code true} is not
   *     recommended as it may result in long delays in message delivery.
   * @return the list of received acknowledgeable messages
   */
  List<AcknowledgeablePubsubMessage> pull(
      String subscription, Integer maxMessages, Boolean returnImmediately);

  /**
   * Asynchronously pull a number of messages from a Google Cloud Pub/Sub subscription.
   *
   * @param subscription short subscription name, e.g., "subscriptionName", or the
   *     fully-qualified subscription name in the {@code
   *     projects/[project_name]/subscriptions/[subscription_name]} format
   * @param maxMessages the maximum number of pulled messages. If this value is null then up to
   *     Integer.MAX_VALUE messages will be requested.
   * @param returnImmediately returns immediately even if subscription doesn't contain enough
   *     messages to satisfy {@code maxMessages}. Setting this parameter to {@code true} is not
   *     recommended as it may result in long delays in message delivery.
   * @return the CompletableFuture for the asynchronous execution, returning the list of received
   *     acknowledgeable messages
   * @since 1.2.3
   */
  CompletableFuture<List<AcknowledgeablePubsubMessage>> pullAsync(
      String subscription, Integer maxMessages, Boolean returnImmediately);

  /**
   * Pull a number of messages from a Google Cloud Pub/Sub subscription and convert them to Spring
   * messages with the desired payload type.
   *
   * @param subscription short subscription name, e.g., "subscriptionName", or the
   *     fully-qualified subscription name in the {@code
   *     projects/[project_name]/subscriptions/[subscription_name]} format
   * @param maxMessages the maximum number of pulled messages. If this value is null then up to
   *     Integer.MAX_VALUE messages will be requested.
   * @param returnImmediately returns immediately even if subscription doesn't contain enough
   *     messages to satisfy {@code maxMessages}. Setting this parameter to {@code true} is not
   *     recommended as it may result in long delays in message delivery.
   * @param payloadType the type to which the payload of the Pub/Sub messages should be converted
   * @param <T> the type of the payload
   * @return the list of received acknowledgeable messages
   * @since 1.1
   */
  <T> List<ConvertedAcknowledgeablePubsubMessage<T>> pullAndConvert(
      String subscription, Integer maxMessages, Boolean returnImmediately, Class<T> payloadType);

  /**
   * Asynchronously pull a number of messages from a Google Cloud Pub/Sub subscription and convert
   * them to Spring messages with the desired payload type.
   *
   * @param subscription short subscription name, e.g., "subscriptionName", or the
   *     fully-qualified subscription name in the {@code
   *     projects/[project_name]/subscriptions/[subscription_name]} format
   * @param maxMessages the maximum number of pulled messages. If this value is null then up to
   *     Integer.MAX_VALUE messages will be requested.
   * @param returnImmediately returns immediately even if subscription doesn't contain enough
   *     messages to satisfy {@code maxMessages}. Setting this parameter to {@code true} is not
   *     recommended as it may result in long delays in message delivery.
   * @param payloadType the type to which the payload of the Pub/Sub messages should be converted
   * @param <T> the type of the payload
   * @return the CompletableFuture for the asynchronous execution, returning the list of received
   *     acknowledgeable messages
   * @since 1.2.3
   */
  <T> CompletableFuture<List<ConvertedAcknowledgeablePubsubMessage<T>>> pullAndConvertAsync(
      String subscription, Integer maxMessages, Boolean returnImmediately, Class<T> payloadType);

  /**
   * Pull and auto-acknowledge a message from a Google Cloud Pub/Sub subscription.
   *
   * @param subscription short subscription name, e.g., "subscriptionName", or the
   *     fully-qualified subscription name in the {@code
   *     projects/[project_name]/subscriptions/[subscription_name]} format
   * @return a received message, or {@code null} if none exists in the subscription
   */
  PubsubMessage pullNext(String subscription);

  /**
   * Asynchronously pull and auto-acknowledge a message from a Google Cloud Pub/Sub subscription.
   *
   * @param subscription short subscription name, e.g., "subscriptionName", or the
   *     fully-qualified subscription name in the {@code
   *     projects/[project_name]/subscriptions/[subscription_name]} format
   * @return the CompletableFuture for the asynchronous execution, returning a received message, or
   *     {@code null} if none exists in the subscription
   * @since 1.2.3
   */
  CompletableFuture<PubsubMessage> pullNextAsync(String subscription);

  /**
   * Acknowledge a batch of messages. The messages must have the same project id.
   *
   * @param acknowledgeablePubsubMessages messages to be acknowledged
   * @return {@code CompletableFuture<Void>} the CompletableFuture for the asynchronous execution
   */
  CompletableFuture<Void> ack(
      Collection<? extends AcknowledgeablePubsubMessage> acknowledgeablePubsubMessages);

  /**
   * Negatively acknowledge a batch of messages. The messages must have the same project id.
   *
   * @param acknowledgeablePubsubMessages messages to be negatively acknowledged
   * @return {@code CompletableFuture<Void>} the CompletableFuture for the asynchronous execution
   */
  CompletableFuture<Void> nack(
      Collection<? extends AcknowledgeablePubsubMessage> acknowledgeablePubsubMessages);

  /**
   * Modify the ack deadline of a batch of messages. The messages must have the same project id.
   *
   * @param acknowledgeablePubsubMessages messages to be modified
   * @param ackDeadlineSeconds the new ack deadline in seconds. A deadline of 0 effectively nacks
   *     the messages.
   * @return {@code CompletableFuture<Void>} the CompletableFuture for the asynchronous execution
   * @since 1.1
   */
  CompletableFuture<Void> modifyAckDeadline(
      Collection<? extends AcknowledgeablePubsubMessage> acknowledgeablePubsubMessages,
      int ackDeadlineSeconds);
}
