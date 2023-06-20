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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Application secret named "application-secret" must exist and have a value of "Hello world.".
 */
@EnabledIfSystemProperty(named = "it.secretmanager", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SecretManagerApplication.class)
class SecretManagerSampleIntegrationTests {

  private static final String SECRET_TO_DELETE = "secret-manager-sample-delete-secret";

  @Autowired
  private SecretManagerTemplate secretManagerTemplate;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  void testApplicationStartup() {
    ResponseEntity<String> response = this.testRestTemplate.getForEntity("/", String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody()).contains(
        "<b>Application secret from @Value:</b> <i>Hello world.</i>");
    assertThat(response.getBody()).contains(
        "<b>Application secret from @ConfigurationProperties:</b> <i>Hello world.</i>");
  }

  @Test
  void testCreateReadSecret() {
    MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    params.add("secretId", "secret-manager-sample-secret");
    params.add("projectId", "");
    params.add("secretPayload", "12345");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, new HttpHeaders());

    ResponseEntity<String> response =
        this.testRestTemplate.postForEntity("/createSecret", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

    response =
        this.testRestTemplate.getForEntity(
            "/getSecret?secretId=secret-manager-sample-secret", String.class);
    assertThat(response.getBody())
        .contains("Secret ID: secret-manager-sample-secret | Value: 12345");
  }

  @Test
  void testDeleteSecret() {
    secretManagerTemplate.createSecret(SECRET_TO_DELETE, "test");

    MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    params.add("secretId", SECRET_TO_DELETE);
    params.add("projectId", "");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, new HttpHeaders());

    ResponseEntity<String> response =
        this.testRestTemplate.postForEntity("/deleteSecret", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }
}
