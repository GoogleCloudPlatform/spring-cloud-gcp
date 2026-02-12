/*
 * Copyright 2025 Google LLC
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
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@EnabledIfSystemProperty(named = "it.secretmanager", matches = "true")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestRestTemplate
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SecretManagerApplication.class)
class SecretManagerRegionalSampleTemplateIntegrationTests {

  @Autowired private SecretManagerTemplate secretManagerTemplate;

  @Autowired private TestRestTemplate testRestTemplate;

  private String secretName;

  @BeforeEach
  void createRegionalSecret() {
    this.secretName = String.format("secret-manager-sample-regional-secret-%s", UUID.randomUUID());
    secretManagerTemplate.createSecret(this.secretName, "54321", "us-central1");
  }

  @AfterEach
  void deleteRegionalSecret() {
    if (secretManagerTemplate.secretExists(
        this.secretName, secretManagerTemplate.getProjectId(), "us-central1")) {
      secretManagerTemplate.deleteSecret(
          this.secretName, secretManagerTemplate.getProjectId(), "us-central1");
    }
  }

  @Test
  void testCreateRegionalSecret() {
    MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    params.add("secretId", this.secretName);
    params.add("projectId", "");
    params.add("secretPayload", "54321");
    params.add("locationId", "us-central1");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, new HttpHeaders());

    ResponseEntity<String> response =
        this.testRestTemplate.postForEntity("/createSecret", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Test
  void testReadRegionalSecret() {
    String getSecretUrl =
        String.format("/getSecret?secretId=%s&locationId=us-central1", this.secretName);
    ResponseEntity<String> response =
        this.testRestTemplate.getForEntity(getSecretUrl, String.class);
    assertThat(response.getBody())
        .contains(String.format("Secret ID: %s | Value: 54321", this.secretName));
  }

  @Test
  void testDeleteRegionalSecret() {
    MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    params.add("secretId", this.secretName);
    params.add("projectId", "");
    params.add("locationId", "us-central1");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, new HttpHeaders());

    ResponseEntity<String> response =
        this.testRestTemplate.postForEntity("/deleteSecret", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }
}
