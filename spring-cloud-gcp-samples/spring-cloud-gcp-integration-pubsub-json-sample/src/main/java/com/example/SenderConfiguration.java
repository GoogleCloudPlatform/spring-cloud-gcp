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

package com.example;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageHandler;

/** Configuration for sending custom JSON payloads to a Pub/Sub topic. */
@Configuration
public class SenderConfiguration {

  private static final Log LOGGER = LogFactory.getLog(SenderConfiguration.class);

  @Bean
  public String topicName(@Value("${topicName}") String topicName) {
    return topicName;
  }

  @Bean
  public DirectChannel pubSubOutputChannel() {
    return new DirectChannel();
  }

  @Bean
  @ServiceActivator(inputChannel = "pubSubOutputChannel")
  public MessageHandler messageSender(
      PubSubTemplate pubSubTemplate, @Qualifier("topicName") String topicName) {
    PubSubMessageHandler adapter = new PubSubMessageHandler(pubSubTemplate, topicName);
    adapter.setSuccessCallback((ackId, message) -> LOGGER.info("Message was sent successfully."));
    adapter.setFailureCallback(
        (cause, message) -> LOGGER.info("There was an error sending the message."));
    return adapter;
  }

  /** an interface that allows sending a person to Pub/Sub. */
  @MessagingGateway(defaultRequestChannel = "pubSubOutputChannel")
  public interface PubSubPersonGateway {
    void sendPersonToPubSub(Person person);
  }
}
