/*
 * Copyright 2022 Google LLC
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

package com.google.cloud.language.v1.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.LanguageServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.threeten.bp.Duration;

class LanguageAutoConfigurationTests {

  private static final String SERVICE_CREDENTIAL_LOCATION =
      "src/test/resources/fake-credential-key.json";
  private static final String SERVICE_CREDENTIAL_CLIENT_ID = "45678";
  private static final String SERVICE_OVERRIDE_CLIENT_ID = "56789";

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(LanguageServiceSpringAutoConfiguration.class));

  @Test
  void testLanguageServiceClientCreated() {
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v1.spring.auto.language-service.enabled=true")
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              assertThat(client).isNotNull();
            });
  }

  @Test
  void testShouldTakeServiceCredentials() {
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v1.spring.auto.language-service.enabled=true",
            "com.google.cloud.language.v1.spring.auto.language-service.credentials.location=file:"
                + SERVICE_CREDENTIAL_LOCATION)
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              Credentials credentials =
                  client.getSettings().getCredentialsProvider().getCredentials();
              assertThat(((ServiceAccountCredentials) credentials).getClientId())
                  .isEqualTo(SERVICE_CREDENTIAL_CLIENT_ID);
            });
  }

  @Test
  void testShouldUseDefaultGrpcTransport() {
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v1.spring.auto.language-service.enabled=true")
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              String transportName =
                  client.getSettings().getTransportChannelProvider().getTransportName();
              assertThat(transportName).isEqualTo("grpc");
            });
  }

  @Test
  void testQuotaProjectIdFromProperties() {
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v1.spring.auto.language-service.enabled=true",
            "com.google.cloud.language.v1.spring.auto.language-service.quota-project-id="
                + SERVICE_OVERRIDE_CLIENT_ID)
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              String quotaProjectId = client.getSettings().getQuotaProjectId();
              assertThat(quotaProjectId).isEqualTo(SERVICE_OVERRIDE_CLIENT_ID);
            });
  }

  @Test
  void testExecutorThreadCountFromProperties() {
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v1.spring.auto.language-service.enabled=true",
            "com.google.cloud.language.v1.spring.auto.language-service.executor-thread-count=3")
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              InstantiatingExecutorProvider backgroundExecutorProvider =
                  ((InstantiatingExecutorProvider)
                      client.getSettings().getBackgroundExecutorProvider());
              assertThat(backgroundExecutorProvider.toBuilder().getExecutorThreadCount())
                  .isEqualTo(3);
            });
  }

  @Test
  void testRetrySettingsFromProperties() {
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v1.spring.auto.language-service.enabled=true",
            "com.google.cloud.language.v1.spring.auto.language-service.analyze-sentiment-retry-delay-multiplier="
                + "2",
            "com.google.cloud.language.v1.spring.auto.language-service.analyze-sentiment-rpc-timeout-multiplier="
                + "2",
            "com.google.cloud.language.v1.spring.auto.language-service.analyze-sentiment-initial-retry-delay="
                + "PT0.5S",
            "com.google.cloud.language.v1.spring.auto.language-service.analyze-sentiment-max-retry-delay="
                + "PT30S",
            "com.google.cloud.language.v1.spring.auto.language-service.analyze-sentiment-initial-rpc-timeout="
                + "PT5M",
            "com.google.cloud.language.v1.spring.auto.language-service.analyze-sentiment-max-rpc-timeout="
                + "PT15M",
            "com.google.cloud.language.v1.spring.auto.language-service.analyze-sentiment-total-timeout="
                + "PT10M")
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              RetrySettings retrySettings =
                  client.getSettings().analyzeSentimentSettings().getRetrySettings();
              assertThat(retrySettings.getRetryDelayMultiplier()).isEqualTo(2);
              assertThat(retrySettings.getRpcTimeoutMultiplier()).isEqualTo(2);
              assertThat(retrySettings.getInitialRetryDelay()).isEqualTo(Duration.ofMillis(500));
              assertThat(retrySettings.getMaxRetryDelay()).isEqualTo(Duration.ofSeconds(30));
              assertThat(retrySettings.getInitialRpcTimeout()).isEqualTo(Duration.ofMinutes(5));
              assertThat(retrySettings.getMaxRpcTimeout()).isEqualTo(Duration.ofMinutes(15));
              assertThat(retrySettings.getTotalTimeout()).isEqualTo(Duration.ofMinutes(10));
            });
  }
}
