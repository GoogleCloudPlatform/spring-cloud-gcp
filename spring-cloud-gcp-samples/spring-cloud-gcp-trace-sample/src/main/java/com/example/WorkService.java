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
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import io.micrometer.observation.annotation.Observed;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** The busy-work service for the sample application. */
@Service
public class WorkService {
  private static final Log LOGGER = LogFactory.getLog(WorkService.class);

  @Value("${sampleTopic}")
  private String sampleTopic;

  private final RestTemplate restTemplate;

  private final PubSubTemplate pubSubTemplate;

  private final MessageChannel pubsubOutputChannel;

  public WorkService(
      RestTemplate restTemplate,
      PubSubTemplate pubSubTemplate,
      MessageChannel pubsubOutputChannel) {
    this.restTemplate = restTemplate;
    this.pubSubTemplate = pubSubTemplate;
    this.pubsubOutputChannel = pubsubOutputChannel;
  }

  @Observed(contextualName = "visit-meet-endpoint")
  public void visitMeetEndpoint(String meetUrl) {
    LOGGER.info("starting busy work");
    for (int i = 0; i < 3; i++) {
      this.restTemplate.getForObject(meetUrl, String.class);
    }
    LOGGER.info("finished busy work");
  }

  @Observed(contextualName = "send-message-spring-integration")
  public void sendMessageSpringIntegration(String text) throws MessagingException {
    final Message<?> message =
        MessageBuilder.withPayload(text).setHeader(GcpPubSubHeaders.TOPIC, sampleTopic).build();
    pubsubOutputChannel.send(message);
  }

  @Observed(contextualName = "send-message-pub-sub-template")
  public void sendMessagePubSubTemplate(String text) throws MessagingException {
    pubSubTemplate.publish(sampleTopic, text);
  }
}
