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

package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Tests for the receiver application.
 *
 * @since 1.2
 */
// Please use "-Dit.pubsub-integration=true" to enable the tests
@EnabledIfSystemProperty(named = "it.pubsub-integration", matches = "true")
@ExtendWith(SpringExtension.class)
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(
    properties = {"spring.main.allow-bean-definition-overriding=true"},
    classes = {PollingReceiverIntegrationTestConfiguration.class})
@DirtiesContext
class PollingReceiverIntegrationTest {

  private static final String PROJECT_NAME =
      ProjectName.of(ServiceOptions.getDefaultProjectId()).getProject();
  @Autowired private Topic testTopic;
  @Autowired private Subscription testSubscription;
  @Autowired private PubSubTemplate pubSubTemplate;
  @Autowired private TopicAdminClient topicAdminClient;
  @Autowired private SubscriptionAdminClient subscriptionAdminClient;

  @Test
  void testSample(CapturedOutput capturedOutput) {
    String message = "test message " + UUID.randomUUID();
    String expectedString = "Message arrived by Synchronous Pull! Payload: " + message;

    this.pubSubTemplate.publish(testTopic.getName(), message);

    Awaitility.await()
        .atMost(180, TimeUnit.SECONDS)
        .until(() -> capturedOutput.toString().contains(expectedString));
    assertThat(capturedOutput.toString()).contains(expectedString);
  }

  @AfterEach
  void cleanUp() {
    List<String> projectTopics = fetchTopicNamesFromProject();
    String topicName = testTopic.getName();
    if (projectTopics.contains(topicName)) {
      this.topicAdminClient.deleteTopic(topicName);
    }
    List<String> projectSubscriptions = fetchSubscriptionNamesFromProject();
    String subscriptionName = testSubscription.getName();
    if (projectSubscriptions.contains(subscriptionName)) {
      this.subscriptionAdminClient.deleteSubscription(subscriptionName);
    }
  }

  private List<String> fetchTopicNamesFromProject() {
    TopicAdminClient.ListTopicsPagedResponse listTopicsResponse =
        topicAdminClient.listTopics("projects/" + PROJECT_NAME);
    return StreamSupport.stream(listTopicsResponse.iterateAll().spliterator(), false)
        .map(Topic::getName)
        .collect(Collectors.toList());
  }

  private List<String> fetchSubscriptionNamesFromProject() {
    SubscriptionAdminClient.ListSubscriptionsPagedResponse response =
        subscriptionAdminClient.listSubscriptions("projects/" + PROJECT_NAME);
    return StreamSupport.stream(response.iterateAll().spliterator(), false)
        .map(Subscription::getName)
        .collect(Collectors.toList());
  }
}
