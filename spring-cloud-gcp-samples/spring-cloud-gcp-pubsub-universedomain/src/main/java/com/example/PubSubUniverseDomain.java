package com.example;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PubSubUniverseDomain {

  public static void main(String[] args) {
    SpringApplication.run(PubSubUniverseDomain.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner() {

    return args -> {
      outputTopicNames();
    };
  }

  @Autowired
  TopicAdminClient topicAdminClient;

  public void outputTopicNames() {
    for (Topic topic : topicAdminClient
        .listTopics(ProjectName.of("google-tpc-testing-environment:cloudsdk-test-project")).iterateAll()) {
      System.out.println(topic.getName());
    }
  }

}