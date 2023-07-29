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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ReceiverTestConfiguration {

  private static final String TEST_TOPIC =
      String.format("pubsub-spring-integration-sample-receiver-exampleTopic-%s", UUID.randomUUID());
  private static final String TEST_SUBSCRIPTION =
      String.format(
          "pubsub-spring-integration-sample-receiver-exampleSubscription-%s", UUID.randomUUID());

  private TopicAdminClient topicAdminClient;

  private SubscriptionAdminClient subscriptionAdminClient;

  public ReceiverTestConfiguration(
      TopicAdminClient topicAdminClient, SubscriptionAdminClient subscriptionAdminClient) {
    this.topicAdminClient = topicAdminClient;
    this.subscriptionAdminClient = subscriptionAdminClient;
  }

  @Bean
  public Topic createTopic() {
    String projectName = ProjectName.of(ServiceOptions.getDefaultProjectId()).getProject();
    return topicAdminClient.createTopic(TopicName.of(projectName, TEST_TOPIC));
  }

  @Bean
  public Subscription createSubscription() {
    String projectName = ProjectName.of(ServiceOptions.getDefaultProjectId()).getProject();
    return subscriptionAdminClient.createSubscription(
        SubscriptionName.of(projectName, TEST_SUBSCRIPTION),
        TopicName.of(projectName, TEST_TOPIC),
        PushConfig.getDefaultInstance(),
        10);
  }

  @Bean
  public String subscription() {
    return TEST_SUBSCRIPTION;
  }
}
