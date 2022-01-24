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

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
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
 * These tests verifies that the pubsub-polling-binder-sample works.
 *
 * @since 1.2
 */
// Please use "-Dit.pubsub=true" to enable the tests
@EnabledIfSystemProperty(named = "it.pubsub", matches = "true")
@ExtendWith(OutputCaptureExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.cloud.stream.bindings.input.destination=sub1",
      "spring.cloud.stream.bindings.output.destination=sub1"
    })
@DirtiesContext
class SampleAppIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void testSample(CapturedOutput capturedOutput) throws Exception {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    String message = "test message " + UUID.randomUUID();

    map.add("messageBody", message);
    map.add("username", "testUserName");

    this.restTemplate.postForObject("/newMessage", map, String.class);

    Callable<Boolean> logCheck =
        () ->
            capturedOutput
                .toString()
                .contains("New message received from testUserName via polling: " + message);
    Awaitility.await().atMost(60, TimeUnit.SECONDS).until(logCheck);

    assertThat(logCheck.call()).isTrue();
  }
}
