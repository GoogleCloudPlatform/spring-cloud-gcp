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

package com.google.cloud.spring.pubsub.support;

import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link PubsubMessage} wrapper that allows it to be acknowledged.
 *
 * @since 1.1
 */
public interface BasicAcknowledgeablePubsubMessage {

  /**
   * Accessor for the project subscription name of the Pub/Sub message.
   *
   * @return the project subscription name
   */
  ProjectSubscriptionName getProjectSubscriptionName();

  /**
   * Accessor for the wrapped {@link PubsubMessage}.
   *
   * @return the wrapped Pub/Sub message
   */
  PubsubMessage getPubsubMessage();

  /**
   * Acknowledge (ack) the message asynchronously.
   *
   * @return {@code CompletableFuture<Void>}}
   */
  CompletableFuture<Void> ack();

  /**
   * Negatatively achnowledge (nack) the message asynchronously.
   *
   * @return {@code CompletableFuture<Void>}}
   */
  CompletableFuture<Void> nack();
}
