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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SampleTestConfiguration {

  private TopicAdminClient topicAdminClient;

  private SubscriptionAdminClient subscriptionAdminClient;

  public SampleTestConfiguration(
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
      @Qualifier("subscriptionName") String subscriptionName,
      @Qualifier("topicName") String topicName) {
    String projectName = ProjectName.of(ServiceOptions.getDefaultProjectId()).getProject();
    return subscriptionAdminClient.createSubscription(
        SubscriptionName.of(projectName, subscriptionName),
        TopicName.of(projectName, topicName),
        PushConfig.getDefaultInstance(),
        10);
  }
}
