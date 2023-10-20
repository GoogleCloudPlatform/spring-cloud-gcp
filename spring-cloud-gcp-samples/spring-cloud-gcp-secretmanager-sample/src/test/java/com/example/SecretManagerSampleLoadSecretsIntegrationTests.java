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

package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Tests sample application endpoint that loads secrets as properties into the application context.
 * Application secret named "application-secret" must exist and have a value of "Hello world.".
 */
@EnabledIfSystemProperty(named = "it.secretmanager", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SecretManagerApplication.class)
class SecretManagerSampleLoadSecretsIntegrationTests {

  @Autowired private SecretManagerTemplate secretManagerTemplate;

  @Autowired private TestRestTemplate testRestTemplate;

  private static final String SECRET_CONTENT = "Hello world.";

  @Test
  void testApplicationStartupSecretLoadsCorrectly() {
    ResponseEntity<String> response = this.testRestTemplate.getForEntity("/", String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody())
        .contains("<b>Application secret from @Value:</b> <i>" + SECRET_CONTENT + "</i>");
    assertThat(response.getBody())
        .contains(
            "<b>Application secret from @ConfigurationProperties:</b> <i>"
                + SECRET_CONTENT
                + "</i>");
  }
}
