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
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class PollingReceiverIntegrationTestConfiguration {

  private String topicName;
  private String subscriptionName;
  private TopicAdminClient topicAdminClient;

  private SubscriptionAdminClient subscriptionAdminClient;

  public PollingReceiverIntegrationTestConfiguration(
      TopicAdminClient topicAdminClient, SubscriptionAdminClient subscriptionAdminClient, @Value("${topicName}") String topicName,
      @Value("${subscriptionName}") String subscriptionName) {
    this.topicAdminClient = topicAdminClient;
    this.subscriptionAdminClient = subscriptionAdminClient;
    this.topicName = topicName;
    this.subscriptionName = subscriptionName;
  }

  @Bean
  public Topic createTopic() {
    String projectName = ProjectName.of(ServiceOptions.getDefaultProjectId()).getProject();
    return topicAdminClient.createTopic(TopicName.of(projectName, this.topicName));
  }

  @Bean
  public Subscription createSubscription() {
    String projectName = ProjectName.of(ServiceOptions.getDefaultProjectId()).getProject();
    return subscriptionAdminClient.createSubscription(
        SubscriptionName.of(projectName, this.subscriptionName),
        TopicName.of(projectName, this.topicName),
        PushConfig.getDefaultInstance(),
        10);
  }
}
