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
 * These tests verifies that the pubsub-binder-sample works.
 *
 * @since 1.1
 */
@EnabledIfSystemProperty(named = "it.pubsub", matches = "true")
@ExtendWith(SpringExtension.class)
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.cloud.stream.bindings.input.destination=my-topic",
      "spring.cloud.stream.bindings.output.destination=my-topic",
      "spring.cloud.stream.bindings.input.group=my-group"
    })
@DirtiesContext
class PubSubBinderSampleAppIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void testSample_successfulMessage(CapturedOutput capturedOutput) throws Exception {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    String message = "test message " + UUID.randomUUID();

    map.add("messageBody", message);
    map.add("username", "testUserName");
    map.add("throwError", false);

    this.restTemplate.postForObject("/newMessage", map, String.class);

    await()
        .atMost(20, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                assertThat(capturedOutput.toString())
                    .contains("New message received from testUserName: " + message + " at "));
  }

  @Test
  void testSample_error(CapturedOutput capturedOutput) throws Exception {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    String message = "test message " + UUID.randomUUID();

    map.add("messageBody", message);
    map.add("username", "testUserName");
    map.add("throwError", true);

    this.restTemplate.postForObject("/newMessage", map, String.class);

    await()
        .atMost(20, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                assertThat(capturedOutput.toString())
                    .contains("The message that was sent is now processed by the error handler."));
  }
}
