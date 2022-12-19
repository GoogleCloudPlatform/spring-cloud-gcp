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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.TransportChannel;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.threeten.bp.Duration;

@ExtendWith(MockitoExtension.class)
class LanguageAutoConfigurationTests {

  private static final String SERVICE_CREDENTIAL_LOCATION =
      "src/test/resources/fake-credential-key.json";
  private static final String SERVICE_CREDENTIAL_CLIENT_ID = "45678";
  private static final String SERVICE_OVERRIDE_CLIENT_ID = "56789";

  @Mock private TransportChannel mockTransportChannel;
  @Mock private ApiCallContext mockApiCallContext;
  @Mock private TransportChannelProvider mockTransportChannelProvider;
  @Mock private CredentialsProvider mockCredentialsProvider;

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, LanguageServiceSpringAutoConfiguration.class));

  @Test
  void testLanguageServiceClientCreated() {
    this.contextRunner.run(
        ctx -> {
          LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
          assertThat(client).isNotNull();
        });
  }

  @Test
  void testServiceCredentialsFromProperties() {
    this.contextRunner
        .withPropertyValues(
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
  void testShouldUseCredentialsBeanWhenPropertiesNotProvided() {
    this.contextRunner.run(
        ctx -> {
          LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
          assertThat(client.getSettings().getCredentialsProvider()).isNotNull();
        });
  }

  @Test
  void testShouldUseDefaultGrpcTransport() {
    this.contextRunner.run(
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
  void testCustomTransportChannelProviderUsedWhenProvided() throws IOException {
    when(mockTransportChannelProvider.getTransportName()).thenReturn("grpc");
    when(mockTransportChannelProvider.getTransportChannel()).thenReturn(mockTransportChannel);
    when(mockTransportChannel.getEmptyCallContext()).thenReturn(mockApiCallContext);
    when(mockApiCallContext.withCredentials(any())).thenReturn(mockApiCallContext);
    when(mockApiCallContext.withTransportChannel(any())).thenReturn(mockApiCallContext);

    contextRunner
        .withBean(
            "defaultLanguageServiceTransportChannelProvider",
            TransportChannelProvider.class,
            () -> mockTransportChannelProvider)
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              assertThat(client.getSettings().getTransportChannelProvider())
                  .isSameAs(mockTransportChannelProvider);
            });
  }

  @Test
  void testCustomServiceSettingsUsedWhenProvided() throws IOException {
    LanguageServiceSettings customLanguageServiceSettings =
        LanguageServiceSettings.newBuilder()
            .setCredentialsProvider(mockCredentialsProvider)
            .setQuotaProjectId(SERVICE_OVERRIDE_CLIENT_ID)
            .build();
    contextRunner
        .withBean(
            "languageServiceSettings",
            LanguageServiceSettings.class,
            () -> customLanguageServiceSettings)
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              assertThat(client.getSettings()).isSameAs(customLanguageServiceSettings);
            });
  }

  @Test
  void testRetrySettingsFromProperties_serviceLevel() {
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.retry-delay-multiplier="
                + "2",
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.max-retry-delay="
                + "PT0.9S")
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);

              RetrySettings analyzeSentimentRetrySettings =
                  client.getSettings().analyzeSentimentSettings().getRetrySettings();
              assertThat(analyzeSentimentRetrySettings.getRetryDelayMultiplier()).isEqualTo(2);
              assertThat(analyzeSentimentRetrySettings.getMaxRetryDelay())
                  .isEqualTo(Duration.ofMillis(900));
              // if properties only override certain retry settings, others should still take on
              // client library defaults
              assertThat(analyzeSentimentRetrySettings.getInitialRetryDelay())
                  .isEqualTo(Duration.ofMillis(100)); // default
            });
  }

  @Test
  void testRetrySettingsFromProperties_serviceAndMethodLevel() {
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.retry-delay-multiplier="
                + "2",
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.max-retry-delay="
                + "PT0.9S",
            "com.google.cloud.language.v1.spring.auto.language-service.annotate-text-retry-settings.retry-delay-multiplier="
                + "3")
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);

              RetrySettings annotateTextRetrySettings =
                  client.getSettings().annotateTextSettings().getRetrySettings();
              // Method-level override should take precedence over service-level
              assertThat(annotateTextRetrySettings.getRetryDelayMultiplier()).isEqualTo(3);
              // For settings without method-level overrides but when service-level is provided,
              // fall back to that
              assertThat(annotateTextRetrySettings.getMaxRetryDelay())
                  .isEqualTo(Duration.ofMillis(900));
              // Settings with neither method not service-level overrides should still take on
              // client library defaults
              assertThat(annotateTextRetrySettings.getInitialRetryDelay())
                  .isEqualTo(Duration.ofMillis(100)); // default
            });
  }

  @Test
  void testRetrySettingsFromProperties_serviceAndMethodLevel_allRetrySettings() {
    this.contextRunner
        .withPropertyValues(
            // service-level, all configurable settings
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.retry-delay-multiplier="
                + "2",
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.rpc-timeout-multiplier="
                + "2",
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.initial-retry-delay="
                + "PT0.5S",
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.max-retry-delay="
                + "PT5S",
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.initial-rpc-timeout="
                + "PT0.5S",
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.max-rpc-timeout="
                + "PT5S",
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.total-timeout="
                + "PT5M",
            "com.google.cloud.language.v1.spring.auto.language-service.retry-settings.max-attempts="
                + "2",
            // method-level, all configurable settings
            "com.google.cloud.language.v1.spring.auto.language-service.annotate-text-retry-settings.retry-delay-multiplier="
                + "3",
            "com.google.cloud.language.v1.spring.auto.language-service.annotate-text-retry-settings.rpc-timeout-multiplier="
                + "3",
            "com.google.cloud.language.v1.spring.auto.language-service.annotate-text-retry-settings.initial-retry-delay="
                + "PT0.6S",
            "com.google.cloud.language.v1.spring.auto.language-service.annotate-text-retry-settings.max-retry-delay="
                + "PT6S",
            "com.google.cloud.language.v1.spring.auto.language-service.annotate-text-retry-settings.initial-rpc-timeout="
                + "PT0.6S",
            "com.google.cloud.language.v1.spring.auto.language-service.annotate-text-retry-settings.max-rpc-timeout="
                + "PT6S",
            "com.google.cloud.language.v1.spring.auto.language-service.annotate-text-retry-settings.total-timeout="
                + "PT6M",
            "com.google.cloud.language.v1.spring.auto.language-service.annotate-text-retry-settings.max-attempts="
                + "3")
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              RetrySettings annotateTextRetrySettings =
                  client.getSettings().annotateTextSettings().getRetrySettings();
              RetrySettings analyzeSentimentRetrySettings =
                  client.getSettings().analyzeSentimentSettings().getRetrySettings();

              // Method-level overrides should be used for annotateText
              assertThat(annotateTextRetrySettings.getRetryDelayMultiplier()).isEqualTo(3);
              assertThat(annotateTextRetrySettings.getRpcTimeoutMultiplier()).isEqualTo(3);
              assertThat(annotateTextRetrySettings.getInitialRetryDelay())
                  .isEqualTo(Duration.ofMillis(600));
              assertThat(annotateTextRetrySettings.getInitialRpcTimeout())
                  .isEqualTo(Duration.ofMillis(600));
              assertThat(annotateTextRetrySettings.getMaxRetryDelay())
                  .isEqualTo(Duration.ofSeconds(6));
              assertThat(annotateTextRetrySettings.getMaxRpcTimeout())
                  .isEqualTo(Duration.ofSeconds(6));
              assertThat(annotateTextRetrySettings.getTotalTimeout())
                  .isEqualTo(Duration.ofMinutes(6));
              assertThat(annotateTextRetrySettings.getMaxAttempts()).isEqualTo(3);

              // Service-level overrides should be used for analyzeSentiment
              assertThat(analyzeSentimentRetrySettings.getRetryDelayMultiplier()).isEqualTo(2);
              assertThat(analyzeSentimentRetrySettings.getRpcTimeoutMultiplier()).isEqualTo(2);
              assertThat(analyzeSentimentRetrySettings.getInitialRetryDelay())
                  .isEqualTo(Duration.ofMillis(500));
              assertThat(analyzeSentimentRetrySettings.getInitialRpcTimeout())
                  .isEqualTo(Duration.ofMillis(500));
              assertThat(analyzeSentimentRetrySettings.getMaxRetryDelay())
                  .isEqualTo(Duration.ofSeconds(5));
              assertThat(analyzeSentimentRetrySettings.getMaxRpcTimeout())
                  .isEqualTo(Duration.ofSeconds(5));
              assertThat(analyzeSentimentRetrySettings.getTotalTimeout())
                  .isEqualTo(Duration.ofMinutes(5));
              assertThat(analyzeSentimentRetrySettings.getMaxAttempts()).isEqualTo(2);
            });
  }
}
