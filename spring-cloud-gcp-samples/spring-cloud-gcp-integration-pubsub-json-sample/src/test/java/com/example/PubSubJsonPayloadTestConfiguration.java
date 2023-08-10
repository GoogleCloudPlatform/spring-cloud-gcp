/*
 * Copyright 2023 Google LLC
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

package com.example;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

public class PubSubJsonPayloadTestConfiguration {

  private final TopicAdminClient topicAdminClient;

  private final SubscriptionAdminClient subscriptionAdminClient;

  public PubSubJsonPayloadTestConfiguration(
      TopicAdminClient topicAdminClient, SubscriptionAdminClient subscriptionAdminClient) {
    this.topicAdminClient = topicAdminClient;
    this.subscriptionAdminClient = subscriptionAdminClient;
  }

  @Bean
  public Topic createTopic(@Qualifier("topicName") String topicName) {
    String projectName = ProjectName.of(ServiceOptions.getDefaultProjectId()).getProject();
    return topicAdminClient.createTopic(TopicName.of(projectName, topicName));
  }

  @Bean
  public Subscription createSubscription(
      @Qualifier("topicName") String topicName,
      @Qualifier("subscriptionName") String subscriptionName) {
    String projectName = ProjectName.of(ServiceOptions.getDefaultProjectId()).getProject();
    return subscriptionAdminClient.createSubscription(
        SubscriptionName.of(projectName, subscriptionName),
        TopicName.of(projectName, topicName),
        PushConfig.getDefaultInstance(),
        10);
  }
}
