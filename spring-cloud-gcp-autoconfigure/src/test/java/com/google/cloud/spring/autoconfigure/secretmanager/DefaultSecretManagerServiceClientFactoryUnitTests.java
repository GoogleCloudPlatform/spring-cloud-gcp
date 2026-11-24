/*
 * Copyright 2026 Google LLC
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

package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.spring.autoconfigure.TestUtils;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link DefaultSecretManagerServiceClientFactory}. */
class DefaultSecretManagerServiceClientFactoryUnitTests {

  private final CredentialsProvider credentialsProvider = () -> TestUtils.MOCK_CREDENTIALS;

  @Test
  void regionalClientUsesDefaultUniverseDomainWhenUnset() {
    DefaultSecretManagerServiceClientFactory factory =
        new DefaultSecretManagerServiceClientFactory(credentialsProvider, null, env -> null);
    try (SecretManagerServiceClient client = factory.getClient("us-central1")) {
      assertThat(client.getSettings().getUniverseDomain()).isEqualTo("googleapis.com");
      assertThat(client.getSettings().getEndpoint())
          .isEqualTo("secretmanager.us-central1.rep.googleapis.com:443");
    }
  }

  @Test
  void regionalClientUsesEnvUniverseDomainWhenPropertyUnset() {
    DefaultSecretManagerServiceClientFactory factory =
        new DefaultSecretManagerServiceClientFactory(
            credentialsProvider,
            null,
            env -> "GOOGLE_CLOUD_UNIVERSE_DOMAIN".equals(env) ? "custom.example.com" : null);
    try (SecretManagerServiceClient client = factory.getClient("europe-west1")) {
      assertThat(client.getSettings().getUniverseDomain()).isEqualTo("custom.example.com");
      assertThat(client.getSettings().getEndpoint())
          .isEqualTo("secretmanager.europe-west1.rep.custom.example.com:443");
    }
  }

  @Test
  void explicitUniverseDomainTakesPrecedenceOverEnv() {
    DefaultSecretManagerServiceClientFactory factory =
        new DefaultSecretManagerServiceClientFactory(
            credentialsProvider,
            "explicit.example.com",
            env -> "GOOGLE_CLOUD_UNIVERSE_DOMAIN".equals(env) ? "env.example.com" : null);
    try (SecretManagerServiceClient client = factory.getClient("us-east1")) {
      assertThat(client.getSettings().getUniverseDomain()).isEqualTo("explicit.example.com");
      assertThat(client.getSettings().getEndpoint())
          .isEqualTo("secretmanager.us-east1.rep.explicit.example.com:443");
    }
  }

  @Test
  void globalClientUsesResolvedUniverseDomainWithoutRegionalEndpoint() {
    DefaultSecretManagerServiceClientFactory factory =
        new DefaultSecretManagerServiceClientFactory(
            credentialsProvider,
            null,
            env -> "GOOGLE_CLOUD_UNIVERSE_DOMAIN".equals(env) ? "custom.example.com" : null);
    try (SecretManagerServiceClient client = factory.getClient("global")) {
      assertThat(client.getSettings().getUniverseDomain()).isEqualTo("custom.example.com");
      assertThat(client.getSettings().getEndpoint())
          .isEqualTo("secretmanager.custom.example.com:443");
    }
  }
}
