/*
 * Copyright 2017-2020 the original author or authors.
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

package com.google.cloud.spring.stream.binder.pubsub.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.core.publisher.PubSubPublisherTemplate;
import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.cloud.spring.stream.binder.pubsub.PubSubMessageChannelBinder;
import com.google.cloud.spring.stream.binder.pubsub.config.PubSubBinderConfiguration;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubExtendedBindingsPropertiesTests.PubSubBindingsTestConfiguration;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.BinderFactory;
import org.springframework.cloud.stream.config.BinderFactoryAutoConfiguration;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests for extended binding properties. */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {
        PubSubBindingsTestConfiguration.class,
        BinderFactoryAutoConfiguration.class,
        PubSubBinderConfiguration.class,
        PubSubExtendedBindingProperties.class,
        BindingServiceConfiguration.class
    },
    properties = {
      "spring.cloud.stream.pollable-source=customIn",
      "spring.cloud.stream.gcp.pubsub.bindings.input.consumer.ack-mode=AUTO_ACK",
      "spring.cloud.stream.gcp.pubsub.bindings.input.consumer.auto-create-resources=true",
      "spring.cloud.stream.gcp.pubsub.default.consumer.auto-create-resources=false"
    })
class PubSubExtendedBindingsPropertiesTests {

  @Autowired private ConfigurableApplicationContext context;

  @Test
  void testExtendedPropertiesOverrideDefaults() {
    BinderFactory binderFactory = this.context.getBeanFactory().getBean(BinderFactory.class);
    PubSubMessageChannelBinder binder =
        (PubSubMessageChannelBinder) binderFactory.getBinder("pubsub", MessageChannel.class);

    assertThat(binder.getExtendedConsumerProperties("custom-in").isAutoCreateResources()).isFalse();
    assertThat(binder.getExtendedConsumerProperties("input").isAutoCreateResources()).isTrue();

    assertThat(binder.getExtendedConsumerProperties("custom-in").getAckMode())
        .isEqualTo(AckMode.AUTO);
    assertThat(binder.getExtendedConsumerProperties("input").getAckMode())
        .isEqualTo(AckMode.AUTO_ACK);
  }

  /** Spring Boot config for tests. */
  @Configuration
  static class PubSubBindingsTestConfiguration {

    @Bean
    public PubSubAdmin pubSubAdmin() {
      PubSubAdmin pubSubAdminMock = Mockito.mock(PubSubAdmin.class);
      when(pubSubAdminMock.createSubscription(anyString(), anyString()))
          .thenReturn(Subscription.getDefaultInstance());
      when(pubSubAdminMock.getSubscription(anyString()))
          .thenReturn(Subscription.getDefaultInstance());
      when(pubSubAdminMock.getTopic(anyString())).thenReturn(Topic.getDefaultInstance());
      return pubSubAdminMock;
    }

    @Bean
    public PubSubTemplate pubSubTemplate() {
      PublisherFactory publisherFactory = Mockito.mock(PublisherFactory.class);

      SubscriberFactory subscriberFactory = Mockito.mock(SubscriberFactory.class);
      when(subscriberFactory.getProjectId()).thenReturn("test-project");
      when(subscriberFactory.createSubscriberStub(any()))
          .thenReturn(Mockito.mock(SubscriberStub.class));
      when(subscriberFactory.createSubscriber(anyString(), any()))
          .thenReturn(Mockito.mock(Subscriber.class));

      return new PubSubTemplate(
          new PubSubPublisherTemplate(publisherFactory),
          new PubSubSubscriberTemplate(subscriberFactory));
    }

    @Bean
    Consumer<String> process() {
      return System.out::println;
    }

    @Bean
    Consumer<String> processCustom() {
      return System.out::println;
    }

    @Bean
    public Supplier<String> customIn() {
      return () -> "empty";
    }
  }
}
