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

import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

/**
 * Tests for the Reactive Pub/Sub sample application.
 *
 * @since 1.2
 */
@EnabledIfSystemProperty(named = "it.pubsub", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ReactiveReceiverApplication.class)
@AutoConfigureWebTestClient
@TestInstance(Lifecycle.PER_CLASS)
class ReactiveReceiverApplicationIntegrationTest {

  // Dynamic UUIDs ensure complete resource isolation when multiple CI matrix runners
  // or PR builds execute concurrently against the shared GCP project.
  // Without a UUID suffix, parallel runners would pull messages from each other's
  // subscription, or runner A's @AfterAll teardown would delete runner B's topic mid-test.
  private static final String TEST_TOPIC = "reactive-sample-topic-" + UUID.randomUUID();

  private static final String TEST_SUBSCRIPTION = "reactive-sample-sub-" + UUID.randomUUID();

  /**
   * Overrides sample.topic and sample.subscription so the application binds to this test run's
   * isolated Pub/Sub resources.
   */
  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("sample.topic", () -> TEST_TOPIC);
    registry.add("sample.subscription", () -> TEST_SUBSCRIPTION);
  }

  @LocalServerPort private int port;

  @Autowired private WebTestClient webTestClient;

  @Autowired private PubSubTemplate pubSubTemplate;

  @Autowired private PubSubAdmin pubSubAdmin;

  /** Provisions the isolated topic and subscription before tests run. */
  @BeforeAll
  void setUp() {
    if (this.pubSubAdmin.getTopic(TEST_TOPIC) == null) {
      this.pubSubAdmin.createTopic(TEST_TOPIC);
    }
    if (this.pubSubAdmin.getSubscription(TEST_SUBSCRIPTION) == null) {
      this.pubSubAdmin.createSubscription(TEST_SUBSCRIPTION, TEST_TOPIC);
    }
  }

  /** Tears down the isolated subscription and topic after all tests have completed. */
  @AfterAll
  void tearDown() {
    if (this.pubSubAdmin.getSubscription(TEST_SUBSCRIPTION) != null) {
      this.pubSubAdmin.deleteSubscription(TEST_SUBSCRIPTION);
    }
    if (this.pubSubAdmin.getTopic(TEST_TOPIC) != null) {
      this.pubSubAdmin.deleteTopic(TEST_TOPIC);
    }
    this.pubSubAdmin.close();
  }

  @Test
  void testSample() throws UnsupportedEncodingException {
    webTestClient
        .post()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/postMessage")
                    .queryParam("message", "reactive test msg")
                    .queryParam("count", (ReactiveController.MAX_RESPONSE_ITEMS) + "")
                    .build())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .exchange();

    FluxExchangeResult<String> result =
        webTestClient
            .get()
            .uri("/getMessages")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .returnResult(String.class);

    AtomicInteger i = new AtomicInteger(0);
    StepVerifier.create(result.getResponseBody())
        .expectNextCount(ReactiveController.MAX_RESPONSE_ITEMS)
        .thenConsumeWhile(s -> s.startsWith("reactive test msg " + i.getAndIncrement()))
        .thenCancel()
        .verify();
  }
}
