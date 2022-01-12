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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @since 2.0.2
 */
//Please enable the tests using "-Dit.pubsub=true"
@EnabledIfSystemProperty(named = "it.pubsub", matches = "true")
@ExtendWith(OutputCaptureExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class PubSubDeadLetterTopicSampleAppIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void testSample_deadLetterHandling(CapturedOutput capturedOutput) {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    String message = "test message " + UUID.randomUUID();

    map.add("messageBody", message);
    map.add("username", "testUserName");

    this.restTemplate.postForObject("/newMessage", map, String.class);

    await().atMost(60, TimeUnit.SECONDS)
        .pollDelay(3, TimeUnit.SECONDS)
        .untilAsserted(() -> assertThat(capturedOutput.toString())
            .contains("Nacking message (attempt 1)")
            .contains("Nacking message (attempt 6)")
            .contains("Received message on dead letter topic")
            .contains(message));
  }
}
