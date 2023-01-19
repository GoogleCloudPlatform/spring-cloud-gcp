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

package com.google.cloud.spring.pubsub.core.publisher;

import com.google.pubsub.v1.PubsubMessage;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * An abstraction for Google Cloud Pub/Sub publisher operations.
 *
 * @since 1.1
 */
public interface PubSubPublisherOperations {

  /**
   * Send a message to Pub/Sub.
   *
   * @param topic short topic name, e.g., "topicName", or the fully-qualified topic name in the
   *     {@code projects/[project_name]/topics/[topic_name]} format
   * @param payload an object that will be serialized and sent
   * @param headers the headers to publish
   * @param <T> the type of the payload to publish
   * @return the listenable future of the call
   */
  <T> CompletableFuture<String> publish(String topic, T payload, Map<String, String> headers);

  /**
   * Send a message to Pub/Sub.
   *
   * @param topic short topic name, e.g., "topicName", or the fully-qualified topic name in the
   *     {@code projects/[project_name]/topics/[topic_name]} format
   * @param payload an object that will be serialized and sent
   * @param <T> the type of the payload to publish
   * @return the listenable future of the call
   */
  <T> CompletableFuture<String> publish(String topic, T payload);

  /**
   * Send a message to Pub/Sub.
   *
   * @param topic short topic name, e.g., "topicName", or the fully-qualified topic name in the
   *     {@code projects/[project_name]/topics/[topic_name]} format
   * @param pubsubMessage a Google Cloud Pub/Sub API message
   * @return the listenable future of the call
   */
  CompletableFuture<String> publish(String topic, PubsubMessage pubsubMessage);
}
