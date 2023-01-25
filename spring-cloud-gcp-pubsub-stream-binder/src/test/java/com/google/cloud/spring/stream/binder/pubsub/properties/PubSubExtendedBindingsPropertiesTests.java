/*
 * Copyright 2017-2022 the original author or authors.
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.spring.pubsub.integration.AckMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.cloud.stream.binder.BinderConfiguration;
import org.springframework.cloud.stream.binder.BinderType;
import org.springframework.cloud.stream.binder.BinderTypeRegistry;
import org.springframework.cloud.stream.binder.DefaultBinderFactory;
import org.springframework.cloud.stream.binder.DefaultBinderTypeRegistry;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.messaging.MessageChannel;

/**
 * Tests for extended binding properties.
 */
class PubSubExtendedBindingsPropertiesTests {

  private static Binder<MessageChannel, ?, ?> binder;

  @BeforeAll
  static void init() {
    DefaultBinderFactory binderFactory = createMockExtendedBinderFactory();
    binder = binderFactory.getBinder(null,
        MessageChannel.class);
  }

  @Test
  void testExtendedDefaultProducerProperties() {
    PubSubProducerProperties producerProperties = (PubSubProducerProperties) ((ExtendedPropertiesBinder<?, ?, ?>) binder)
        .getExtendedProducerProperties("default-output");
    assertThat(producerProperties.isAutoCreateResources()).isTrue();
    assertThat(producerProperties.getAllowedHeaders()).isNull();
    assertThat(producerProperties.isSync()).isFalse();
  }

  @Test
  void testExtendedDefaultConsumerProperties() {
    PubSubConsumerProperties consumerProperties = (PubSubConsumerProperties) ((ExtendedPropertiesBinder<?, ?, ?>) binder)
        .getExtendedConsumerProperties("default-input");
    assertThat(consumerProperties.isAutoCreateResources()).isTrue();
    assertThat(consumerProperties.getAllowedHeaders()).isNull();
    assertThat(consumerProperties.getAckMode()).isEqualTo(AckMode.AUTO);
    assertThat(consumerProperties.getMaxFetchSize()).isEqualTo(1);
    assertThat(consumerProperties.getSubscriptionName()).isNull();
    assertThat(consumerProperties.getDeadLetterPolicy()).isNull();
  }

  private static DefaultBinderFactory createMockExtendedBinderFactory() {
    BinderTypeRegistry binderTypeRegistry = createMockExtendedBinderTypeRegistry();
    return new DefaultBinderFactory(
        Collections.singletonMap("mock",
            new BinderConfiguration("mock", new HashMap<>(), true, true)),
        binderTypeRegistry, null);
  }

  private static DefaultBinderTypeRegistry createMockExtendedBinderTypeRegistry() {
    return new DefaultBinderTypeRegistry(
        Collections.singletonMap("mock", new BinderType("mock",
            new Class[]{ MockExtendedBinderConfiguration.class })));
  }

  @Configuration
  public static class MockExtendedBinderConfiguration {

    @SuppressWarnings("rawtypes")
    @Bean
    public Binder<?, ?, ?> extendedPropertiesBinder() {
      Binder mock = mock(Binder.class,
          Mockito.withSettings().defaultAnswer(Mockito.RETURNS_MOCKS)
              .extraInterfaces(ExtendedPropertiesBinder.class));
      ConfigurableEnvironment environment = new StandardEnvironment();
      Map<String, Object> propertiesToAdd = new HashMap<>();
      environment.getPropertySources()
          .addLast(new MapPropertySource("extPropertiesConfig", propertiesToAdd));
      ConfigurableApplicationContext applicationContext = new GenericApplicationContext();
      applicationContext.setEnvironment(environment);

      PubSubExtendedBindingProperties pubSubExtendedBindingProperties = new PubSubExtendedBindingProperties();
      pubSubExtendedBindingProperties.setApplicationContext(applicationContext);
      final PubSubConsumerProperties defaultConsumerProperties = pubSubExtendedBindingProperties
          .getExtendedConsumerProperties("default-input");
      final PubSubProducerProperties defaultProducerProperties = pubSubExtendedBindingProperties
          .getExtendedProducerProperties("default-output");
      when(((ExtendedPropertiesBinder) mock).getExtendedConsumerProperties("default-input"))
          .thenReturn(defaultConsumerProperties);
      when(((ExtendedPropertiesBinder) mock).getExtendedProducerProperties("default-output"))
          .thenReturn(defaultProducerProperties);
      return mock;
    }
  }
}
