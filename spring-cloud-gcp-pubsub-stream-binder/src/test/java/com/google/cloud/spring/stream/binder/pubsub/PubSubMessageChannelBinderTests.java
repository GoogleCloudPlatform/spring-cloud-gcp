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

package com.google.cloud.spring.stream.binder.pubsub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.PubSubHeaderMapper;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubMessageSource;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.stream.binder.pubsub.config.PubSubBinderConfiguration;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubConsumerProperties;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubExtendedBindingProperties;
import com.google.cloud.spring.stream.binder.pubsub.provisioning.PubSubChannelProvisioner;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.PollableMessageSource;
import org.springframework.cloud.stream.binding.BindingService;
import org.springframework.cloud.stream.config.ConsumerEndpointCustomizer;
import org.springframework.cloud.stream.config.ProducerMessageHandlerCustomizer;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;

/**
 * Tests for channel binder.
 *
 * @since 1.1
 */
@ExtendWith(MockitoExtension.class)
class PubSubMessageChannelBinderTests {
  private static final Log LOGGER = LogFactory.getLog(PubSubMessageChannelBinderTests.class);

  PubSubMessageChannelBinder binder;

  @Mock PubSubChannelProvisioner channelProvisioner;

  @Mock PubSubTemplate pubSubTemplate;

  @Mock PubSubAdmin pubSubAdmin;

  @Mock PubSubExtendedBindingProperties properties;

  @Mock ConsumerDestination consumerDestination;

  @Mock ProducerDestination producerDestination;

  @Mock ExtendedConsumerProperties<PubSubConsumerProperties> consumerProperties;

  @Mock MessageChannel errorChannel;

  @Mock HealthTrackerRegistry healthTrackerRegistry;

  ApplicationContextRunner baseContext =
      new ApplicationContextRunner()
          .withBean(PubSubTemplate.class, () -> pubSubTemplate)
          .withBean(PubSubAdmin.class, () -> pubSubAdmin)
          .withConfiguration(
              AutoConfigurations.of(
                  PubSubBinderConfiguration.class, PubSubExtendedBindingProperties.class));

  @Test
  void testAfterUnbindConsumer() {

    this.binder = new PubSubMessageChannelBinder(new String[0], this.channelProvisioner, this.pubSubTemplate, this.properties);
    this.binder.afterUnbindConsumer(this.consumerDestination, "group1", this.consumerProperties);

    verify(this.channelProvisioner).afterUnbindConsumer(this.consumerDestination);
  }

  @Test
  void producerSyncPropertyFalseByDefault() {

    this.binder = new PubSubMessageChannelBinder(new String[0], this.channelProvisioner, this.pubSubTemplate, this.properties);
    when(producerDestination.getName()).thenReturn("test-topic");
    baseContext.run(
        ctx -> {
          PubSubMessageChannelBinder binder = ctx.getBean(PubSubMessageChannelBinder.class);

          PubSubExtendedBindingProperties props =
              ctx.getBean("pubSubExtendedBindingProperties", PubSubExtendedBindingProperties.class);
          PubSubMessageHandler messageHandler =
              (PubSubMessageHandler)
                  binder.createProducerMessageHandler(
                      producerDestination,
                      new ExtendedProducerProperties<>(props.getExtendedProducerProperties("test")),
                      errorChannel);
          assertThat(messageHandler.isSync()).isFalse();
        });
  }

  @Test
  void producerSyncPropertyPropagatesToMessageHandler() {

    this.binder = new PubSubMessageChannelBinder(new String[0], this.channelProvisioner, this.pubSubTemplate, this.properties);
    when(producerDestination.getName()).thenReturn("test-topic");
    baseContext
        .withPropertyValues("spring.cloud.stream.gcp.pubsub.default.producer.sync=true")
        .run(
            ctx -> {
              PubSubMessageChannelBinder binder = ctx.getBean(PubSubMessageChannelBinder.class);

              PubSubExtendedBindingProperties props =
                  ctx.getBean(
                      "pubSubExtendedBindingProperties", PubSubExtendedBindingProperties.class);
              PubSubMessageHandler messageHandler =
                  (PubSubMessageHandler)
                      binder.createProducerMessageHandler(
                          producerDestination,
                          new ExtendedProducerProperties<>(
                              props.getExtendedProducerProperties("test")),
                          errorChannel);
              assertThat(messageHandler.isSync()).isTrue();
            });
  }

  @Test
  void producerHeaderPropertyPropagatesToMessageHandler() {
    this.binder = new PubSubMessageChannelBinder(new String[0], this.channelProvisioner, this.pubSubTemplate, this.properties);
    when(producerDestination.getName()).thenReturn("test-topic");
    baseContext
                .withPropertyValues("spring.cloud.stream.gcp.pubsub.default.producer.allowedHeaders=foo4,foo5")
                .run(
                        ctx -> {
                          PubSubMessageChannelBinder binder = ctx.getBean(PubSubMessageChannelBinder.class);

                          PubSubExtendedBindingProperties props =
                                    ctx.getBean("pubSubExtendedBindingProperties", PubSubExtendedBindingProperties.class);
                          PubSubMessageHandler messageHandler =
                                    (PubSubMessageHandler)
                                            binder.createProducerMessageHandler(
                                                    producerDestination,
                                                    new ExtendedProducerProperties<>(
                                                            props.getExtendedProducerProperties("test")),
                                                    errorChannel);
                          PubSubHeaderMapper mapper = (PubSubHeaderMapper) FieldUtils.readField(messageHandler, "headerMapper", true);
                          String[] headersToCheck = (String[]) FieldUtils.readField(mapper, "outboundHeaderPatterns", true);
                          Assert.assertArrayEquals(headersToCheck, new String[]{"foo4", "foo5"});
                        });
  }

  @Test
  void consumerMaxFetchPropertyPropagatesToMessageSource() {
    this.binder = new PubSubMessageChannelBinder(new String[0], this.channelProvisioner, this.pubSubTemplate, this.properties);
    when(consumerDestination.getName()).thenReturn("test-subscription");
    baseContext
        .withPropertyValues("spring.cloud.stream.gcp.pubsub.default.consumer.maxFetchSize=20")
        .run(
            ctx -> {
              PubSubMessageChannelBinder binder = ctx.getBean(PubSubMessageChannelBinder.class);
              PubSubExtendedBindingProperties props =
                  ctx.getBean(
                      "pubSubExtendedBindingProperties", PubSubExtendedBindingProperties.class);

              PubSubMessageSource source =
                  binder.createPubSubMessageSource(
                      consumerDestination,
                      new ExtendedConsumerProperties<>(
                          props.getExtendedConsumerProperties("test")));
              assertThat(source.getMaxFetchSize()).isEqualTo(20);
            });
  }

  @Test
  void testCreateConsumerWithRegistry() {

    this.binder = new PubSubMessageChannelBinder(new String[0], this.channelProvisioner, this.pubSubTemplate, this.properties);
    when(consumerDestination.getName()).thenReturn("test-subscription");
    baseContext.run(
        ctx -> {
          PubSubMessageChannelBinder binder = ctx.getBean(PubSubMessageChannelBinder.class);
          PubSubExtendedBindingProperties props =
              ctx.getBean("pubSubExtendedBindingProperties", PubSubExtendedBindingProperties.class);
          binder.setHealthTrackerRegistry(healthTrackerRegistry);

          MessageProducer messageProducer =
              binder.createConsumerEndpoint(
                  consumerDestination,
                  "testGroup",
                  new ExtendedConsumerProperties<>(props.getExtendedConsumerProperties("test")));

          assertThat(messageProducer).isInstanceOf(PubSubInboundChannelAdapter.class);
          PubSubInboundChannelAdapter inboundChannelAdapter =
              (PubSubInboundChannelAdapter) messageProducer;
          assertThat(inboundChannelAdapter.getAckMode()).isSameAs(AckMode.AUTO);
          assertThat(inboundChannelAdapter.healthCheckEnabled()).isTrue();
        });
  }

  @Test
  void testProducerAndConsumerCustomizers() {

    this.binder = new PubSubMessageChannelBinder(new String[0], this.channelProvisioner, this.pubSubTemplate, this.properties);
    baseContext
        .withUserConfiguration(PubSubBinderTestConfig.class)
        .withPropertyValues("spring.cloud.stream.bindings.input.group=testGroup")
        .run(
            context -> {
              DirectFieldAccessor channelBindingServiceAccessor =
                  new DirectFieldAccessor(context.getBean(BindingService.class));
              @SuppressWarnings("unchecked")
              Map<String, List<Binding<MessageChannel>>> consumerBindings =
                  (Map<String, List<Binding<MessageChannel>>>)
                      channelBindingServiceAccessor.getPropertyValue("consumerBindings");
              assertThat(
                      new DirectFieldAccessor(consumerBindings.get("input").get(0))
                          .getPropertyValue("lifecycle.beanName"))
                  .isEqualTo("setByCustomizer:input");

              @SuppressWarnings("unchecked")
              Map<String, Binding<MessageChannel>> producerBindings =
                  (Map<String, Binding<MessageChannel>>)
                      channelBindingServiceAccessor.getPropertyValue("producerBindings");
              assertThat(
                      new DirectFieldAccessor(producerBindings.get("output"))
                          .getPropertyValue("val$producerMessageHandler.beanName"))
                  .isEqualTo("setByCustomizer:output");
            });
  }

  @Test
  void testConsumerEndpointCreation() {

    this.binder = new PubSubMessageChannelBinder(new String[0], this.channelProvisioner, this.pubSubTemplate, this.properties);
    when(consumerDestination.getName()).thenReturn("test-subscription");
    baseContext
        .withPropertyValues(
            "spring.cloud.stream.bindings.input.group=testGroup",
            "spring.cloud.stream.gcp.pubsub.default.consumer.ackMode=MANUAL")
        .run(
            ctx -> {
              PubSubMessageChannelBinder binder = ctx.getBean(PubSubMessageChannelBinder.class);
              PubSubExtendedBindingProperties props =
                  ctx.getBean(
                      "pubSubExtendedBindingProperties", PubSubExtendedBindingProperties.class);

              assertThat(binder).isNotNull();
              MessageProducer messageProducer =
                  binder.createConsumerEndpoint(
                      consumerDestination,
                      "testGroup",
                      new ExtendedConsumerProperties<>(
                          props.getExtendedConsumerProperties("test")));

              assertThat(messageProducer).isInstanceOf(PubSubInboundChannelAdapter.class);
              PubSubInboundChannelAdapter inboundChannelAdapter =
                  (PubSubInboundChannelAdapter) messageProducer;
              assertThat(inboundChannelAdapter.getAckMode()).isSameAs(AckMode.MANUAL);
              inboundChannelAdapter.start();
              inboundChannelAdapter.setErrorChannelName("test-subscription.errors");
              assertThat(inboundChannelAdapter.getErrorChannel()).isNotNull();
            });
  }

  @Test
  void testConsumerEndpointCreationWithNoHeadersProvided() {

    this.binder = new PubSubMessageChannelBinder(new String[0], this.channelProvisioner, this.pubSubTemplate, this.properties);
    when(consumerDestination.getName()).thenReturn("test-subscription");
    baseContext
        .run(
             ctx -> {
               PubSubMessageChannelBinder binder = ctx.getBean(PubSubMessageChannelBinder.class);
               PubSubExtendedBindingProperties props =
                                    ctx.getBean(
                                            "pubSubExtendedBindingProperties", PubSubExtendedBindingProperties.class);

               assertThat(binder).isNotNull();
               MessageProducer messageProducer =
                                    binder.createConsumerEndpoint(
                                            consumerDestination,
                                            "testGroup",
                                            new ExtendedConsumerProperties<>(
                                                    props.getExtendedConsumerProperties("test")));
               assertThat(messageProducer).isInstanceOf(PubSubInboundChannelAdapter.class);
               PubSubInboundChannelAdapter inboundChannelAdapter =
                                    (PubSubInboundChannelAdapter) messageProducer;
               PubSubHeaderMapper mapper = (PubSubHeaderMapper) FieldUtils.readField(inboundChannelAdapter, "headerMapper", true);
               String [] headersToCheck = (String[]) FieldUtils.readField(mapper, "inboundHeaderPatterns", true);
               Assert.assertArrayEquals(headersToCheck, (String[]) FieldUtils.readField(mapper, "inboundHeaderPatterns", true));
          });
  }

  @Test
  void testConsumerEndpointCreationWithHeadersProvided() {

    this.binder = new PubSubMessageChannelBinder(new String[0], this.channelProvisioner, this.pubSubTemplate, this.properties);
    when(consumerDestination.getName()).thenReturn("test-subscription");
    baseContext
                .withPropertyValues("spring.cloud.stream.gcp.pubsub.default.consumer.allowedHeaders=foo2,foo3")
                .run(
                        ctx -> {
                          PubSubMessageChannelBinder binder = ctx.getBean(PubSubMessageChannelBinder.class);
                          PubSubExtendedBindingProperties props =
                                    ctx.getBean(
                                            "pubSubExtendedBindingProperties", PubSubExtendedBindingProperties.class);

                          assertThat(binder).isNotNull();
                          MessageProducer messageProducer =
                                    binder.createConsumerEndpoint(
                                            consumerDestination,
                                            "testGroup",
                                            new ExtendedConsumerProperties<>(
                                                    props.getExtendedConsumerProperties("test")));
                          assertThat(messageProducer).isInstanceOf(PubSubInboundChannelAdapter.class);
                          PubSubInboundChannelAdapter inboundChannelAdapter =
                                    (PubSubInboundChannelAdapter) messageProducer;
                          PubSubHeaderMapper mapper = (PubSubHeaderMapper) FieldUtils.readField(inboundChannelAdapter, "headerMapper", true);
                          String [] headersToCheck = (String[]) FieldUtils.readField(mapper, "inboundHeaderPatterns", true);
                          Assert.assertArrayEquals(headersToCheck, new String[]{"foo2", "foo3"});
                        });
  }

  public interface PollableSource {
    @Input
    PollableMessageSource source();
  }

  @EnableBinding({Processor.class, PollableSource.class})
  @EnableAutoConfiguration
  public static class PubSubBinderTestConfig {

    @Bean
    public ConsumerEndpointCustomizer<PubSubInboundChannelAdapter> consumerCustomizer() {
      return (p, q, g) -> p.setBeanName("setByCustomizer:" + q);
    }

    @Bean
    public ProducerMessageHandlerCustomizer<PubSubMessageHandler> handlerCustomizer() {
      return (handler, destinationName) ->
          handler.setBeanName("setByCustomizer:" + destinationName);
    }

    @StreamListener(Sink.INPUT)
    public void process(String payload) throws InterruptedException {
      LOGGER.info("received: " + payload);
    }

    @Bean
    public GcpProjectIdProvider projectIdProvider() {
      return () -> "fake project";
    }

    @Bean
    public CredentialsProvider googleCredentials() {
      return () -> mock(Credentials.class);
    }
  }
}
