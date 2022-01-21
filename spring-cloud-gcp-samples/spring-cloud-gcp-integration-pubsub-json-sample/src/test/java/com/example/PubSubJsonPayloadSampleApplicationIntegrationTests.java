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
import static org.awaitility.Awaitility.await;

import com.google.cloud.spring.core.util.MapBuilder;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests the Pub/Sub Json payload app. */
// Please use "-Dit.pubsub-integration=true" to enable the tests
@EnabledIfSystemProperty(named = "it.pubsub-integration", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {PubSubJsonPayloadApplication.class})
class PubSubJsonPayloadSampleApplicationIntegrationTests {

  @Autowired private TestRestTemplate testRestTemplate;

  @Test
  void testReceivesJsonPayload() {
    Random random = new Random();
    int age = random.nextInt(200);

    Map<String, String> params =
        new MapBuilder<String, String>().put("name", "Bob").put("age", String.valueOf(age)).build();

    this.testRestTemplate.postForObject(
        "/createPerson?name={name}&age={age}", null, String.class, params);

    await()
        .atMost(Duration.ofSeconds(10))
        .untilAsserted(
            () -> {
              ResponseEntity<List<Person>> response =
                  this.testRestTemplate.exchange(
                      "/listPersons",
                      HttpMethod.GET,
                      null,
                      new ParameterizedTypeReference<List<Person>>() {});

              assertThat(response.getBody()).containsOnlyOnce(new Person("Bob", age));
            });
  }
}
