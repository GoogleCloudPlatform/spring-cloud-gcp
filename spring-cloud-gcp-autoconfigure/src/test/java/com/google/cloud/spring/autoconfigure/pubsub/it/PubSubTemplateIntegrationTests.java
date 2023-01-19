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

package com.google.cloud.spring.autoconfigure.pubsub.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.CachingPublisherFactory;
import com.google.cloud.spring.pubsub.support.converter.ConvertedAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Integration tests for Pub/Sub template. */
@EnabledIfSystemProperty(named = "it.pubsub", matches = "true")
class PubSubTemplateIntegrationTests {

  private static final Log LOGGER = LogFactory.getLog(PubSubTemplateIntegrationTests.class);

  private ApplicationContextRunner contextRunner;

  @BeforeEach
  void init() {
    contextRunner =
        new ApplicationContextRunner()
            .withPropertyValues("spring.cloud.gcp.pubsub.subscriber.max-ack-extension-period=0")
            .withConfiguration(
                AutoConfigurations.of(
                    GcpContextAutoConfiguration.class, GcpPubSubAutoConfiguration.class));
  }

  @Test
  void testCreatePublishPullNextAndDelete() {
    this.contextRunner.run(
        context -> {
          PubSubAdmin pubSubAdmin = context.getBean(PubSubAdmin.class);
          PubSubTemplate pubSubTemplate = context.getBean(PubSubTemplate.class);

          String topicName = "tarkus_" + UUID.randomUUID();
          String subscriptionName = "zatoichi_" + UUID.randomUUID();

          assertThat(pubSubAdmin.getTopic(topicName)).isNull();
          assertThat(pubSubAdmin.getSubscription(subscriptionName)).isNull();
          pubSubAdmin.createTopic(topicName);
          pubSubAdmin.createSubscription(subscriptionName, topicName);

          Map<String, String> headers = new HashMap<>();
          headers.put("cactuar", "tonberry");
          headers.put("fujin", "raijin");
          pubSubTemplate.publish(topicName, "tatatatata", headers).get();

          // get message
          AtomicReference<PubsubMessage> pubsubMessageRef = new AtomicReference<>();
          Awaitility.await()
              .atMost(30, TimeUnit.SECONDS)
              .until(
                  () -> {
                    pubsubMessageRef.set(pubSubTemplate.pullNext(subscriptionName));
                    return pubsubMessageRef.get() != null;
                  });
          PubsubMessage pubsubMessage = pubsubMessageRef.get();

          assertThat(pubsubMessage).isNotNull();
          assertThat(pubsubMessage.getData()).isEqualTo(ByteString.copyFromUtf8("tatatatata"));
          assertThat(pubsubMessage.getAttributesCount()).isEqualTo(2);
          assertThat(pubsubMessage.getAttributesOrThrow("cactuar")).isEqualTo("tonberry");
          assertThat(pubsubMessage.getAttributesOrThrow("fujin")).isEqualTo("raijin");

          assertThat(pubSubAdmin.getTopic(topicName)).isNotNull();
          assertThat(pubSubAdmin.getSubscription(subscriptionName)).isNotNull();
          assertThat(
                  pubSubAdmin.listTopics().stream()
                      .filter(topic -> topic.getName().endsWith(topicName))
                      .toArray())
              .hasSize(1);
          assertThat(
                  pubSubAdmin.listSubscriptions().stream()
                      .filter(subscription -> subscription.getName().endsWith(subscriptionName))
                      .toArray())
              .hasSize(1);
          pubSubAdmin.deleteSubscription(subscriptionName);
          pubSubAdmin.deleteTopic(topicName);
          assertThat(pubSubAdmin.getTopic(topicName)).isNull();
          assertThat(pubSubAdmin.getSubscription(subscriptionName)).isNull();
          assertThat(
                  pubSubAdmin.listTopics().stream()
                      .filter(topic -> topic.getName().endsWith(topicName)))
              .isEmpty();
          assertThat(
                  pubSubAdmin.listSubscriptions().stream()
                      .filter(subscription -> subscription.getName().endsWith(subscriptionName)))
              .isEmpty();
        });
  }

  @Test
  void testPullAndAck() {
    this.contextRunner.run(
        context -> {
          PubSubAdmin pubSubAdmin = context.getBean(PubSubAdmin.class);
          String topicName = "peel-the-paint" + UUID.randomUUID();
          String subscriptionName = "i-lost-my-head" + UUID.randomUUID();
          pubSubAdmin.createTopic(topicName);
          pubSubAdmin.createSubscription(subscriptionName, topicName, 10);

          PubSubTemplate pubSubTemplate = context.getBean(PubSubTemplate.class);

          List<Future<String>> futures = new ArrayList<>();
          futures.add(pubSubTemplate.publish(topicName, "message1"));
          futures.add(pubSubTemplate.publish(topicName, "message2"));
          futures.add(pubSubTemplate.publish(topicName, "message3"));

          futures.parallelStream()
              .forEach(
                  f -> {
                    try {
                      f.get(5, TimeUnit.SECONDS);
                    } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                      LOGGER.error(ex);
                      Thread.currentThread().interrupt();
                    }
                  });

          List<AcknowledgeablePubsubMessage> ackableMessages = new ArrayList<>();
          Set<String> messagesSet = new HashSet<>();
          for (int i = 0; i < 5 && messagesSet.size() < 3; i++) {
            List<AcknowledgeablePubsubMessage> newMessages =
                pubSubTemplate.pull(subscriptionName, 4, false);
            ackableMessages.addAll(newMessages);
            messagesSet.addAll(
                newMessages.stream()
                    .map(message -> message.getPubsubMessage().getData().toStringUtf8())
                    .toList());
          }

          assertThat(messagesSet).as("check that we received all the messages").hasSize(3);

          ackableMessages.forEach(
              message -> {
                try {
                  if (message.getPubsubMessage().getData().toStringUtf8().equals("message1")) {
                    message.ack().get(); // sync call
                  } else {
                    message.nack().get(); // sync call
                  }
                } catch (InterruptedException | ExecutionException ex) {
                  LOGGER.error(ex);
                  Thread.currentThread().interrupt();
                }
              });

          AtomicInteger messagesCount = new AtomicInteger(0);
          await()
              .pollDelay(Duration.ofSeconds(5))
              .pollInterval(Duration.ofMillis(100))
              .timeout(Duration.ofMinutes(1))
              .untilAsserted(
                  () -> {
                    List<AcknowledgeablePubsubMessage> newAckableMessages =
                        pubSubTemplate.pull(subscriptionName, 4, true);
                    newAckableMessages.forEach(BasicAcknowledgeablePubsubMessage::ack);
                    int finalCount = messagesCount.addAndGet(newAckableMessages.size());

                    assertThat(finalCount)
                        .as("check that we get both nacked messages back")
                        .isEqualTo(2);
                  });

          pubSubAdmin.deleteSubscription(subscriptionName);
          pubSubAdmin.deleteTopic(topicName);
        });
  }

  @Test
  void testPubSubTemplateLoadsMessageConverter() {
    this.contextRunner
        .withUserConfiguration(JsonPayloadTestConfiguration.class)
        .run(
            context -> {
              PubSubAdmin pubSubAdmin = context.getBean(PubSubAdmin.class);
              PubSubTemplate pubSubTemplate = context.getBean(PubSubTemplate.class);

              String topicName = "json-payload-topic" + UUID.randomUUID();
              String subscriptionName = "json-payload-subscription" + UUID.randomUUID();
              pubSubAdmin.createTopic(topicName);
              pubSubAdmin.createSubscription(subscriptionName, topicName, 10);

              TestUser user = new TestUser("John", "password");
              pubSubTemplate.publish(topicName, user);

              await()
                  .atMost(Duration.ofSeconds(10))
                  .untilAsserted(
                      () -> {
                        List<ConvertedAcknowledgeablePubsubMessage<TestUser>> messages =
                            pubSubTemplate.pullAndConvert(
                                subscriptionName, 1, true, TestUser.class);
                        assertThat(messages).hasSize(1);

                        TestUser receivedTestUser = messages.get(0).getPayload();
                        assertThat(receivedTestUser.username).isEqualTo("John");
                        assertThat(receivedTestUser.password).isEqualTo("password");
                      });

              pubSubAdmin.deleteSubscription(subscriptionName);
              pubSubAdmin.deleteTopic(topicName);
            });
  }

  @Test
  void testPublisherShutdown() {
    AtomicReference<Publisher> publisherAtomicReference1 = new AtomicReference<>();
    AtomicReference<Publisher> publisherAtomicReference2 = new AtomicReference<>();
    this.contextRunner.run(
        context -> {
          CachingPublisherFactory publisherFactory = context.getBean(CachingPublisherFactory.class);
          publisherAtomicReference1.set(publisherFactory.createPublisher("test-topic-1"));
          publisherAtomicReference2.set(publisherFactory.createPublisher("test_topic-2"));
        }
    );

    PubsubMessage message = PubsubMessage.newBuilder()
        .setData(ByteString.copyFromUtf8("random test msg"))
        .build();

    Publisher publisher1 = publisherAtomicReference1.get();
    assertThatThrownBy(() -> publisher1.publish(message))
        .isExactlyInstanceOf(IllegalStateException.class);

    Publisher publisher2 = publisherAtomicReference2.get();
    assertThatThrownBy(() -> publisher2.publish(message))
        .isExactlyInstanceOf(IllegalStateException.class);
  }

  /** Beans for test. */
  @Configuration
  static class JsonPayloadTestConfiguration {

    @Bean
    public PubSubMessageConverter pubSubMessageConverter() {
      return new JacksonPubSubMessageConverter(new ObjectMapper());
    }
  }

  /** A test JSON payload. */
  static class TestUser {

    public final String username;

    public final String password;

    @JsonCreator
    TestUser(@JsonProperty("username") String username, @JsonProperty("password") String password) {
      this.username = username;
      this.password = password;
    }
  }
}
