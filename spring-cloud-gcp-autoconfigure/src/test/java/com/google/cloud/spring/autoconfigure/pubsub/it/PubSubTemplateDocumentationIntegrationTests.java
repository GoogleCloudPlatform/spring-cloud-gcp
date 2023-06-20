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
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.google.cloud.spring.pubsub.support.converter.ConvertedAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.concurrent.ListenableFutureCallback;

/** Documentation tests for Pub/Sub. */
@EnabledIfSystemProperty(named = "it.pubsub-docs", matches = "true")
class PubSubTemplateDocumentationIntegrationTests {

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withPropertyValues(
              "spring.cloud.gcp.pubsub.subscriber.max-ack-extension-period=0",
              "spring.cloud.gcp.pubsub.publisher.enable-message-ordering=true",
              "spring.cloud.gcp.pubsub.publisher.endpoint=us-east1-pubsub.googleapis.com:443")
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, GcpPubSubAutoConfiguration.class));

  @Test
  void testCreatePublishPullNextAndDelete() {
    pubSubTest(
        (AssertableApplicationContext context,
            PubSubTemplate pubSubTemplate,
            String subscriptionName,
            String topicName) -> {
          // tag::publish[]
          Map<String, String> headers = Collections.singletonMap("key1", "val1");
          pubSubTemplate.publish(topicName, "message", headers).get();
          // end::publish[]

          await()
              .atMost(Duration.ofSeconds(30))
              .untilAsserted(
                  () -> {
                    PubsubMessage pubsubMessage = pubSubTemplate.pullNext(subscriptionName);

                    assertThat(pubsubMessage).isNotNull();
                    assertThat(pubsubMessage.getData())
                        .isEqualTo(ByteString.copyFromUtf8("message"));
                    assertThat(pubsubMessage.getAttributesCount()).isEqualTo(1);
                    assertThat(pubsubMessage.getAttributesOrThrow("key1")).isEqualTo("val1");
                  });
        });
  }

  @Test
  void testCreatePublishPullNextAndDelete_ordering() {
    pubSubTest(
        (AssertableApplicationContext context,
            PubSubTemplate pubSubTemplate,
            String subscriptionName,
            String topicName) -> {
          // tag::publish_ordering[]
          Map<String, String> headers =
              Collections.singletonMap(GcpPubSubHeaders.ORDERING_KEY, "key1");
          pubSubTemplate.publish(topicName, "message1", headers).get();
          pubSubTemplate.publish(topicName, "message2", headers).get();
          // end::publish_ordering[]

          // message1
          await()
              .atMost(Duration.ofSeconds(30))
              .untilAsserted(
                  () -> {
                    PubsubMessage pubsubMessage = pubSubTemplate.pullNext(subscriptionName);

                    assertThat(pubsubMessage).isNotNull();
                    assertThat(pubsubMessage.getData())
                        .isEqualTo(ByteString.copyFromUtf8("message1"));
                    assertThat(pubsubMessage.getAttributesCount()).isZero();
                  });

          // message2
          await()
              .atMost(Duration.ofSeconds(30))
              .untilAsserted(
                  () -> {
                    PubsubMessage pubsubMessage = pubSubTemplate.pullNext(subscriptionName);

                    assertThat(pubsubMessage).isNotNull();
                    assertThat(pubsubMessage.getData())
                        .isEqualTo(ByteString.copyFromUtf8("message2"));
                    assertThat(pubsubMessage.getAttributesCount()).isZero();
                  });
        });
  }

  private void pubSubTest(PubSubTest pubSubTest, Class... configClass) {
    ApplicationContextRunner contextRunner =
        configClass.length == 0
            ? this.contextRunner
            : this.contextRunner.withUserConfiguration(configClass[0]);
    contextRunner.run(
        context -> {
          PubSubAdmin pubSubAdmin = context.getBean(PubSubAdmin.class);
          PubSubTemplate pubSubTemplate = context.getBean(PubSubTemplate.class);

          String subscriptionName = "test_subscription_" + UUID.randomUUID();
          String topicName = "test_topic_" + UUID.randomUUID();

          try {
            assertThat(pubSubAdmin.getTopic(topicName)).isNull();
            assertThat(pubSubAdmin.getSubscription(subscriptionName)).isNull();

            // tag::create_topic[]
            pubSubAdmin.createTopic(topicName);
            // end::create_topic[]
            await()
                .atMost(Duration.ofMinutes(1))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> assertThat(pubSubAdmin.getTopic(topicName)).isNotNull());

            // tag::create_subscription[]
            pubSubAdmin.createSubscription(subscriptionName, topicName);
            // end::create_subscription[]

            await()
                .atMost(Duration.ofMinutes(1))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(
                    () -> assertThat(pubSubAdmin.getSubscription(subscriptionName)).isNotNull());

            pubSubTest.run(context, pubSubTemplate, subscriptionName, topicName);
          } finally {
            // tag::list_subscriptions[]
            List<String> subscriptions =
                pubSubAdmin.listSubscriptions().stream()
                    .map(Subscription::getName)
                    .collect(Collectors.toList());
            // end::list_subscriptions[]

            // tag::list_topics[]
            List<String> topics =
                pubSubAdmin.listTopics().stream().map(Topic::getName).collect(Collectors.toList());
            // end::list_topics[]

            pubSubAdmin.deleteSubscription(subscriptionName);
            pubSubAdmin.deleteTopic(topicName);

            assertThat(subscriptions.stream().map(this::getLastPart)).contains(subscriptionName);
            assertThat(topics.stream().map(this::getLastPart)).contains(topicName);
          }
        });
  }

  private String getLastPart(String s) {
    String[] split = s.split("/");
    return split[split.length - 1];
  }

  @Test
  void subscribeSimpleTest() {
    pubSubTest(
        (AssertableApplicationContext context,
            PubSubTemplate pubSubTemplate,
            String subscriptionName,
            String topicName) -> {
          pubSubTemplate.publish(topicName, "message");

          Logger logger = new Logger();
          // tag::subscribe[]
          Subscriber subscriber =
              pubSubTemplate.subscribe(
                  subscriptionName,
                  message -> {
                    logger.info(
                        "Message received from "
                            + subscriptionName
                            + " subscription: "
                            + message.getPubsubMessage().getData().toStringUtf8());
                    message.ack();
                  });
          // end::subscribe[]

          List<String> messages = logger.getMessages();
          Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> !messages.isEmpty());
          assertThat(messages)
              .containsExactly(
                  "Message received from " + subscriptionName + " subscription: message");
        });
  }

  @Test
  void testPubSubTemplatePull() {
    pubSubTest(
        (AssertableApplicationContext context,
            PubSubTemplate pubSubTemplate,
            String subscriptionName,
            String topicName) -> {
          pubSubTemplate.publish(topicName, "message");
          Logger logger = new Logger();
          await()
              .atMost(Duration.ofSeconds(10))
              .untilAsserted(
                  () -> {
                    // tag::pull[]
                    int maxMessages = 10;
                    boolean returnImmediately = false;
                    List<AcknowledgeablePubsubMessage> messages =
                        pubSubTemplate.pull(subscriptionName, maxMessages, returnImmediately);
                    // end::pull[]

                    assertThat(messages).hasSize(1);

                    // tag::pull[]

                    // acknowledge the messages
                    pubSubTemplate.ack(messages);

                    messages.forEach(
                        message ->
                            logger.info(message.getPubsubMessage().getData().toStringUtf8()));

                    // end::pull[]

                    assertThat(logger.getMessages()).containsExactly("message");
                  });
        });
  }

  @Test
  void testPubSubTemplateLoadsMessageConverter() {
    pubSubTest(
        (AssertableApplicationContext context,
            PubSubTemplate pubSubTemplate,
            String subscriptionName,
            String topicName) -> {
          // tag::json_publish[]
          TestUser user = new TestUser();
          user.setUsername("John");
          user.setPassword("password");
          pubSubTemplate.publish(topicName, user);
          // end::json_publish[]

          await()
              .atMost(Duration.ofSeconds(10))
              .untilAsserted(
                  () -> {
                    // tag::json_pull[]
                    int maxMessages = 1;
                    boolean returnImmediately = false;
                    List<ConvertedAcknowledgeablePubsubMessage<TestUser>> messages =
                        pubSubTemplate.pullAndConvert(
                            subscriptionName, maxMessages, returnImmediately, TestUser.class);
                    // end::json_pull[]

                    assertThat(messages).hasSize(1);

                    // tag::json_pull[]

                    ConvertedAcknowledgeablePubsubMessage<TestUser> message = messages.get(0);

                    // acknowledge the message
                    message.ack();

                    TestUser receivedTestUser = message.getPayload();
                    // end::json_pull[]

                    assertThat(receivedTestUser.username).isEqualTo("John");
                    assertThat(receivedTestUser.password).isEqualTo("password");
                  });
        },
        JsonPayloadTestConfiguration.class);
  }

  @Test
  void testSpelExpressionMessageRouting() {
    pubSubTest(
        (AssertableApplicationContext context,
            PubSubTemplate pubSubTemplate,
            String subscriptionName,
            String topicName) -> {
          PubSubMessageHandler adapter = context.getBean(PubSubMessageHandler.class);

          String payload = "payload";
          GenericMessage<String> message =
              new GenericMessage<>(payload, Collections.singletonMap("sendToTopic", topicName));
          adapter.handleMessage(message);

          await()
              .atMost(Duration.ofMinutes(1))
              .pollInterval(Duration.ofSeconds(2))
              .untilAsserted(
                  () -> {
                    PubsubMessage pubsubMessage = pubSubTemplate.pullNext(subscriptionName);
                    assertThat(pubsubMessage).isNotNull();
                    assertThat(pubsubMessage.getData()).isEqualTo(ByteString.copyFromUtf8(payload));
                  });
        },
        MessageHandlerTestConfiguration.class);
  }

  /** Beans for test. */
  @Configuration
  static class JsonPayloadTestConfiguration {
    // tag::json_bean[]
    // Note: The ObjectMapper is used to convert Java POJOs to and from JSON.
    // You will have to configure your own instance if you are unable to depend
    // on the ObjectMapper provided by Spring Boot starters.
    @Bean
    public PubSubMessageConverter pubSubMessageConverter() {
      return new JacksonPubSubMessageConverter(new ObjectMapper());
    }
    // end::json_bean[]
  }

  @Configuration
  static class MessageHandlerTestConfiguration {

    // This bean needs to go through a proper @Configuration class because it has an package-private
    // "onInit()"
    // method that needs to be called to work with SpEL expressions.
    @Bean
    public PubSubMessageHandler pubSubMessageHandler(PubSubTemplate pubSubTemplate) {

      // tag::message_router[]
      PubSubMessageHandler adapter = new PubSubMessageHandler(pubSubTemplate, "myDefaultTopic");
      adapter.setTopicExpressionString("headers['sendToTopic']");
      // end::message_router[]

      // tag::adapter_callback[]
      adapter.setPublishCallback(
          new ListenableFutureCallback<String>() {
            @Override
            public void onFailure(Throwable ex) {}

            @Override
            public void onSuccess(String result) {}
          });
      // end::adapter_callback[]

      return adapter;
    }
  }

  /** A test JSON payload. */
  // tag::json_convertible_class[]
  static class TestUser {

    String username;

    String password;

    public String getUsername() {
      return this.username;
    }

    void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return this.password;
    }

    void setPassword(String password) {
      this.password = password;
    }
  }
  // end::json_convertible_class[]

  interface PubSubTest {
    void run(
        AssertableApplicationContext context,
        PubSubTemplate pubSubTemplate,
        String subscription,
        String topic)
        throws ExecutionException, InterruptedException;
  }

  class Logger {
    List<String> messages = new ArrayList<>();

    void info(String message) {
      this.messages.add(message);
    }

    List<String> getMessages() {
      return this.messages;
    }
  }
}
