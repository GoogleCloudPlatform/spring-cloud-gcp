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

package com.google.cloud.spring.stream.binder.pubsub;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import com.google.cloud.spring.pubsub.integration.PubSubHeaderMapper;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubMessageSource;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubConsumerProperties;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubExtendedBindingProperties;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubProducerProperties;
import com.google.cloud.spring.stream.binder.pubsub.provisioning.PubSubChannelProvisioner;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/** Message channel binder for Pub/Sub. */
public class PubSubMessageChannelBinder
    extends AbstractMessageChannelBinder<
        ExtendedConsumerProperties<PubSubConsumerProperties>,
        ExtendedProducerProperties<PubSubProducerProperties>,
        PubSubChannelProvisioner>
    implements ExtendedPropertiesBinder<
        MessageChannel, PubSubConsumerProperties, PubSubProducerProperties> {

  private final PubSubTemplate pubSubTemplate;

  private final PubSubExtendedBindingProperties pubSubExtendedBindingProperties;

  private final PubSubChannelProvisioner pubSubChannelProvisioner;

  private HealthTrackerRegistry healthTrackerRegistry;

  public PubSubMessageChannelBinder(
      String[] headersToEmbed,
      PubSubChannelProvisioner provisioningProvider,
      PubSubTemplate pubSubTemplate,
      PubSubExtendedBindingProperties pubSubExtendedBindingProperties) {

    super(headersToEmbed, provisioningProvider);
    this.pubSubTemplate = pubSubTemplate;
    this.pubSubExtendedBindingProperties = pubSubExtendedBindingProperties;
    this.pubSubChannelProvisioner = provisioningProvider;
  }

  public void setHealthTrackerRegistry(HealthTrackerRegistry healthTrackerRegistry) {
    this.healthTrackerRegistry = healthTrackerRegistry;
  }

  @Override
  protected MessageHandler createProducerMessageHandler(
      ProducerDestination destination,
      ExtendedProducerProperties<PubSubProducerProperties> producerProperties,
      MessageChannel errorChannel) {

    PubSubMessageHandler messageHandler = new PubSubMessageHandler(this.pubSubTemplate, destination.getName());

    PubSubProducerProperties props = producerProperties.getExtension();
    if (props != null && props.getAllowedHeaders() != null) {
      PubSubHeaderMapper headerMapper = new PubSubHeaderMapper();
      headerMapper.setOutboundHeaderPatterns(props.getAllowedHeaders());
      messageHandler.setHeaderMapper(headerMapper);
    }

    messageHandler.setBeanFactory(getBeanFactory());
    messageHandler.setSync(producerProperties.getExtension().isSync());
    return messageHandler;
  }

  @Override
  protected MessageProducer createConsumerEndpoint(
      ConsumerDestination destination,
      String group,
      ExtendedConsumerProperties<PubSubConsumerProperties> properties) {

    PubSubInboundChannelAdapter adapter =
        new PubSubInboundChannelAdapter(this.pubSubTemplate, destination.getName());

    PubSubConsumerProperties props = properties.getExtension();
    if (props != null && props.getAllowedHeaders() != null) {
      PubSubHeaderMapper headerMapper = new PubSubHeaderMapper();
      headerMapper.setInboundHeaderPatterns(props.getAllowedHeaders());
      adapter.setHeaderMapper(headerMapper);
    }

    if (healthTrackerRegistry != null) {
      adapter.setHealthTrackerRegistry(healthTrackerRegistry);
    }

    ErrorInfrastructure errorInfrastructure =
        registerErrorInfrastructure(destination, group, properties);
    adapter.setErrorChannel(errorInfrastructure.getErrorChannel());
    adapter.setAckMode(properties.getExtension().getAckMode());
    adapter.setBeanFactory(getBeanFactory());

    return adapter;
  }

  @Override
  protected String errorsBaseName(
      ConsumerDestination destination,
      String group,
      ExtendedConsumerProperties<PubSubConsumerProperties> properties) {
    return destination.getName() + ".errors";
  }

  @Override
  public PubSubConsumerProperties getExtendedConsumerProperties(String channelName) {
    return this.pubSubExtendedBindingProperties.getExtendedConsumerProperties(channelName);
  }

  @Override
  public PubSubProducerProperties getExtendedProducerProperties(String channelName) {
    return this.pubSubExtendedBindingProperties.getExtendedProducerProperties(channelName);
  }

  @Override
  public String getDefaultsPrefix() {
    return this.pubSubExtendedBindingProperties.getDefaultsPrefix();
  }

  @Override
  public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
    return this.pubSubExtendedBindingProperties.getExtendedPropertiesEntryClass();
  }

  @Override
  protected void afterUnbindConsumer(
      ConsumerDestination destination,
      String group,
      ExtendedConsumerProperties<PubSubConsumerProperties> consumerProperties) {
    super.afterUnbindConsumer(destination, group, consumerProperties);
    this.pubSubChannelProvisioner.afterUnbindConsumer(destination);
  }

  @Override
  protected PolledConsumerResources createPolledConsumerResources(
      String name,
      String group,
      ConsumerDestination destination,
      ExtendedConsumerProperties<PubSubConsumerProperties> consumerProperties) {
    PubSubMessageSource source = createPubSubMessageSource(destination, consumerProperties);
    return new PolledConsumerResources(
        source, registerErrorInfrastructure(destination, group, consumerProperties, true));
  }

  protected PubSubMessageSource createPubSubMessageSource(
      ConsumerDestination destination,
      ExtendedConsumerProperties<PubSubConsumerProperties> consumerProperties) {
    PubSubMessageSource source =
        new PubSubMessageSource(this.pubSubTemplate, destination.getName());
    source.setMaxFetchSize(consumerProperties.getExtension().getMaxFetchSize());
    return source;
  }
}
