/*
 * Copyright 2017-2019 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Integration test for the sender sample app.
 *
 * @since 1.1
 */
@EnabledIfSystemProperty(named = "it.pubsub-integration", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class SenderIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private PubSubTemplate pubSubTemplate;

  @Test
  void testSample() throws Exception {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    String message = "test message " + UUID.randomUUID();

    map.add("message", message);
    map.add("times", 1);

    this.restTemplate.postForObject("/postMessage", map, String.class);

    List<AcknowledgeablePubsubMessage> messages;

    boolean messageReceived = false;
    for (int i = 0; i < 100; i++) {
      messages = this.pubSubTemplate.pull("exampleSubscription", 10, true);
      messages.forEach(BasicAcknowledgeablePubsubMessage::ack);

      if (messages.stream()
          .anyMatch(m -> m.getPubsubMessage().getData().toStringUtf8().startsWith(message))) {
        messageReceived = true;
        break;
      }
      Thread.sleep(100);
    }
    assertThat(messageReceived).isTrue();
  }
}
