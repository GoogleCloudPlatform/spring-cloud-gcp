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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//Please use "-Dit.vision=true" to enable the tests
@EnabledIfSystemProperty(named = "it.vision", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = Application.class,
    properties = "application.ocr-bucket=vision-integration-test-bucket")
class VisionOcrSampleIntegrationTest {

  @Autowired private TestRestTemplate testRestTemplate;

  @Test
  void testApplicationStartup() {
    ResponseEntity<String> response = this.testRestTemplate.getForEntity("/", String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

    response = this.testRestTemplate.getForEntity("/status", String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }
}
