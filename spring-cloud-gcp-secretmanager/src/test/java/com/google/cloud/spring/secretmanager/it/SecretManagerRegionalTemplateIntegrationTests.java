/*
 * Copyright 2017-2024 the original author or authors.
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

package com.google.cloud.spring.secretmanager.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Integration tests for {@link SecretManagerTemplate} for regional secrets. */
@EnabledIfSystemProperty(named = "it.secretmanager", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SecretManagerRegionalTestConfiguration.class})
class SecretManagerRegionalTemplateIntegrationTests {

  @Autowired SecretManagerTemplate secretManagerTemplate;

  private String secretName;

  @BeforeEach
  void createSecret() {
    this.secretName = String.format("test-reg-secret-%s", UUID.randomUUID());

    secretManagerTemplate.createSecret(secretName, "4321");
    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () -> {
              String secretString = secretManagerTemplate.getSecretString(secretName);
              assertThat(secretString).isEqualTo("4321");
            });
  }

  @AfterEach
  void deleteSecret() {
    secretManagerTemplate.deleteSecret(this.secretName);
  }

  @Test
  void testReadWriteSecrets() {
    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () -> {
              String secretString = secretManagerTemplate.getSecretString(this.secretName);
              assertThat(secretString).isEqualTo("4321");

              byte[] secretBytes = secretManagerTemplate.getSecretBytes(this.secretName);
              assertThat(secretBytes).isEqualTo("4321".getBytes());
            });
  }

  @Test
  void testReadMissingSecret() {

    assertThatThrownBy(() -> secretManagerTemplate.getSecretBytes("test-NON-EXISTING-reg-secret"))
              .isInstanceOf(com.google.api.gax.rpc.NotFoundException.class);
  }

  @Test
  void testUpdateSecrets() {

    secretManagerTemplate.createSecret(this.secretName, "9999");
    await()
        .atMost(Duration.ofSeconds(10))
        .untilAsserted(
            () -> {
              String secretString = secretManagerTemplate.getSecretString(this.secretName);
              assertThat(secretString).isEqualTo("9999");

              byte[] secretBytes = secretManagerTemplate.getSecretBytes(this.secretName);
              assertThat(secretBytes).isEqualTo("9999".getBytes());
            });
  }
}
