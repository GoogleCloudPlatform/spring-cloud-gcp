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
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

/** Test for sample application web endpoints using SecretManagerTemplate. */
@EnabledIfSystemProperty(named = "it.secretmanager", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SecretManagerApplication.class)
class SecretManagerSampleTemplateIntegrationTests {

  @Autowired private SecretManagerTemplate secretManagerTemplate;

  @Autowired private TestRestTemplate testRestTemplate;

  private String secretName;

  @BeforeEach
  void createSecret() {
    this.secretName = String.format("secret-manager-sample-secret-%s", UUID.randomUUID());
    secretManagerTemplate.createSecret(this.secretName, "12345");
  }

  @AfterEach
  void deleteSecret() {
    if (secretManagerTemplate.secretExists(this.secretName)) {
      secretManagerTemplate.deleteSecret(this.secretName);
    }
  }

  @Test
  void testCreateSecret() {
    MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    params.add("secretId", this.secretName);
    params.add("projectId", "");
    params.add("secretPayload", "12345");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, new HttpHeaders());

    ResponseEntity<String> response =
        this.testRestTemplate.postForEntity("/createSecret", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Test
  void testReadSecret() {
    String getSecretUrl = String.format("/getSecret?secretId=%s", this.secretName);
    ResponseEntity<String> response =
        this.testRestTemplate.getForEntity(getSecretUrl, String.class);
    assertThat(response.getBody())
        .contains(String.format("Secret ID: %s | Value: 12345", this.secretName));
  }

  @Test
  void testDeleteSecret() {
    MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    params.add("secretId", this.secretName);
    params.add("projectId", "");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, new HttpHeaders());

    ResponseEntity<String> response =
        this.testRestTemplate.postForEntity("/deleteSecret", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }
}
