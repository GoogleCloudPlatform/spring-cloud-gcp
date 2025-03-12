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

package com.google.cloud.language.v2.spring;

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
import com.google.cloud.language.v2.LanguageServiceClient;
import com.google.cloud.language.v2.LanguageServiceSettings;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.threeten.bp.Duration;

/**
 * This class is wiped out and re-created as part of the generation process. Please make changes to
 * https://github.com/GoogleCloudPlatform/spring-cloud-gcp/blob/main/spring-cloud-generator/spring-cloud-previews-template/google-cloud-language-spring-starter/src/test/java/com/google/cloud/language/v2/spring/LanguageAutoConfigurationTests.java
 * to retain the changes in the next generation workflow run.
 */
@ExtendWith(MockitoExtension.class)
class LanguageAutoConfigurationTests {

  private static final String SERVICE_CREDENTIAL_LOCATION =
      "src/test/resources/fake-credential-key.json";
  private static final String TOP_LEVEL_CREDENTIAL_LOCATION =
      "src/test/resources/fake-credential-key-2.json";
  private static final String SERVICE_CREDENTIAL_CLIENT_ID = "45678";
  private static final String TOP_LEVEL_CREDENTIAL_CLIENT_ID = "12345";
  private static final String SERVICE_OVERRIDE_CLIENT_ID = "56789";
  private static final String TRANSPORT_CHANNEL_PROVIDER_QUALIFIER_NAME =
      "defaultLanguageServiceTransportChannelProvider";

  @Mock private TransportChannel mockTransportChannel;
  @Mock private ApiCallContext mockApiCallContext;
  @Mock private TransportChannelProvider mockTransportChannelProvider;
  @Mock private CredentialsProvider mockCredentialsProvider;

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, LanguageServiceSpringAutoConfiguration.class))
          .withPropertyValues(
              "spring.cloud.gcp.credentials.location=file:" + TOP_LEVEL_CREDENTIAL_LOCATION);

  @Test
  void testLanguageServiceClientCreated() {
    this.contextRunner.run(
        ctx -> {
          LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
          assertThat(client).isNotNull();
        });
  }

  @Test
  void testCredentials_fromServicePropertiesIfSpecified() {
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v2.language-service.credentials.location=file:"
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
  void testCredentials_fromTopLevelIfNoServiceProperties() {
    this.contextRunner.run(
        ctx -> {
          LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
          Credentials credentials = client.getSettings().getCredentialsProvider().getCredentials();
          assertThat(((ServiceAccountCredentials) credentials).getClientId())
              .isEqualTo(TOP_LEVEL_CREDENTIAL_CLIENT_ID);
        });
  }

  @Test
  void testShouldGetTransportChannelProviderFromBeanWithQualifierName() throws IOException {
    this.contextRunner
        .withBean(
            "anotherTransportChannelProvider",
            TransportChannelProvider.class,
            () -> mockTransportChannelProvider)
        .run(
            ctx -> {
              assertThat(ctx.getBeanNamesForType(TransportChannelProvider.class))
                  .containsExactlyInAnyOrder(
                      "anotherTransportChannelProvider", TRANSPORT_CHANNEL_PROVIDER_QUALIFIER_NAME);
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              TransportChannelProvider transportChannelProviderBean =
                  (TransportChannelProvider) ctx.getBean(TRANSPORT_CHANNEL_PROVIDER_QUALIFIER_NAME);
              TransportChannelProvider transportChannelProvider =
                  client.getSettings().getTransportChannelProvider();
              assertThat(transportChannelProvider).isSameAs(transportChannelProviderBean);
              assertThat(transportChannelProvider).isNotSameAs(mockTransportChannelProvider);
            });
  }

  @Test
  void testShouldUseDefaultTransportChannelProvider() {
    this.contextRunner.run(
        ctx -> {
          LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
          TransportChannelProvider transportChannelProvider =
              client.getSettings().getTransportChannelProvider();
          TransportChannelProvider defaultTransportChannelprovider =
              LanguageServiceSettings.defaultTransportChannelProvider();
          assertThat(transportChannelProvider)
              .usingRecursiveComparison()
              .isEqualTo(defaultTransportChannelprovider);
        });
  }

  @Test
  void testCustomTransportChannelProviderSetToRest() {
    this.contextRunner
        .withPropertyValues("com.google.cloud.language.v2.language-service.use-rest=true")
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              TransportChannelProvider transportChannelProvider =
                  client.getSettings().getTransportChannelProvider();
              TransportChannelProvider defaultHttpJsonTransportChannelprovider =
                  LanguageServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
              assertThat(transportChannelProvider)
                  .usingRecursiveComparison()
                  .isEqualTo(defaultHttpJsonTransportChannelprovider);
            });
  }

  @Test
  void testQuotaProjectIdFromProperties() {
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v2.language-service.quota-project-id="
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
    Integer customExecutorThreadCount = 3;
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v2.language-service.executor-thread-count="
                + customExecutorThreadCount.toString())
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              InstantiatingExecutorProvider backgroundExecutorProvider =
                  ((InstantiatingExecutorProvider)
                      client.getSettings().getBackgroundExecutorProvider());
              assertThat(backgroundExecutorProvider.toBuilder().getExecutorThreadCount())
                  .isEqualTo(customExecutorThreadCount);
            });
  }

  @Test
  void testCustomTransportChannelProviderUsedWhenProvided() throws IOException {
    when(mockTransportChannelProvider.getTransportName()).thenReturn("grpc");
    when(mockTransportChannelProvider.getTransportChannel()).thenReturn(mockTransportChannel);
    when(mockTransportChannel.getEmptyCallContext()).thenReturn(mockApiCallContext);
    when(mockApiCallContext.withCredentials(any())).thenReturn(mockApiCallContext);
    when(mockApiCallContext.withTransportChannel(any())).thenReturn(mockApiCallContext);
    when(mockApiCallContext.withEndpointContext(any())).thenReturn(mockApiCallContext);

    contextRunner
        .withBean(
            TRANSPORT_CHANNEL_PROVIDER_QUALIFIER_NAME,
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
    Double customMultiplier = 2.0;
    String customDurationString = "PT0.9S";
    Duration customDuration = Duration.ofMillis(900);
    Duration defaultDurationExpected = Duration.ofMillis(100);
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v2.language-service.retry.retry-delay-multiplier="
                + customMultiplier.toString(),
            "com.google.cloud.language.v2.language-service.retry.max-retry-delay="
                + customDurationString)
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);

              RetrySettings analyzeSentimentRetrySettings =
                  client.getSettings().analyzeSentimentSettings().getRetrySettings();
              assertThat(analyzeSentimentRetrySettings.getRetryDelayMultiplier())
                  .isEqualTo(customMultiplier);
              assertThat(analyzeSentimentRetrySettings.getMaxRetryDelay())
                  .isEqualTo(customDuration);
              // if properties only override certain retry settings, others should still take on
              // client library defaults
              assertThat(analyzeSentimentRetrySettings.getInitialRetryDelay())
                  .isEqualTo(defaultDurationExpected);
            });
  }

  @Test
  void testRetrySettingsFromProperties_serviceAndMethodLevel() {
    Double customServiceMultiplier = 2.0;
    Double customMethodMultiplier = 3.0;
    String customDurationString = "PT0.9S";
    Duration customDuration = Duration.ofMillis(900);
    Duration defaultDurationExpected = Duration.ofMillis(100);
    this.contextRunner
        .withPropertyValues(
            "com.google.cloud.language.v2.language-service.retry.retry-delay-multiplier="
                + customServiceMultiplier.toString(),
            "com.google.cloud.language.v2.language-service.retry.max-retry-delay="
                + customDurationString,
            "com.google.cloud.language.v2.language-service.annotate-text-retry.retry-delay-multiplier="
                + customMethodMultiplier.toString())
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);

              RetrySettings annotateTextRetrySettings =
                  client.getSettings().annotateTextSettings().getRetrySettings();
              // Method-level override should take precedence over service-level
              assertThat(annotateTextRetrySettings.getRetryDelayMultiplier())
                  .isEqualTo(customMethodMultiplier);
              // For settings without method-level overrides but when service-level is provided,
              // fall back to that
              assertThat(annotateTextRetrySettings.getMaxRetryDelay()).isEqualTo(customDuration);
              // Settings with neither method not service-level overrides should still take on
              // client library defaults
              assertThat(annotateTextRetrySettings.getInitialRetryDelay())
                  .isEqualTo(defaultDurationExpected); // default
            });
  }

  @Test
  void testRetrySettingsFromProperties_serviceAndMethodLevel_allRetrySettings() {
    Double customServiceMultiplier = 2.0;
    String customServiceDurationString = "PT0.5S";
    Duration customServiceDuration = Duration.ofMillis(500);
    Integer customServiceMaxAttempts = 2;

    Double customMethodMultiplier = 3.0;
    String customMethodDurationString = "PT0.6S";
    Duration customMethodDuration = Duration.ofMillis(600);
    Integer customMethodMaxAttempts = 3;

    this.contextRunner
        .withPropertyValues(
            // service-level, all configurable settings
            "com.google.cloud.language.v2.language-service.retry.retry-delay-multiplier="
                + customServiceMultiplier.toString(),
            "com.google.cloud.language.v2.language-service.retry.rpc-timeout-multiplier="
                + customServiceMultiplier.toString(),
            "com.google.cloud.language.v2.language-service.retry.initial-retry-delay="
                + customServiceDurationString,
            "com.google.cloud.language.v2.language-service.retry.max-retry-delay="
                + customServiceDurationString,
            "com.google.cloud.language.v2.language-service.retry.initial-rpc-timeout="
                + customServiceDurationString,
            "com.google.cloud.language.v2.language-service.retry.max-rpc-timeout="
                + customServiceDurationString,
            "com.google.cloud.language.v2.language-service.retry.total-timeout="
                + customServiceDurationString,
            "com.google.cloud.language.v2.language-service.retry.max-attempts="
                + customServiceMaxAttempts.toString(),
            // method-level, all configurable settings
            "com.google.cloud.language.v2.language-service.annotate-text-retry.retry-delay-multiplier="
                + customMethodMultiplier.toString(),
            "com.google.cloud.language.v2.language-service.annotate-text-retry.rpc-timeout-multiplier="
                + customMethodMultiplier.toString(),
            "com.google.cloud.language.v2.language-service.annotate-text-retry.initial-retry-delay="
                + customMethodDurationString,
            "com.google.cloud.language.v2.language-service.annotate-text-retry.max-retry-delay="
                + customMethodDurationString,
            "com.google.cloud.language.v2.language-service.annotate-text-retry.initial-rpc-timeout="
                + customMethodDurationString,
            "com.google.cloud.language.v2.language-service.annotate-text-retry.max-rpc-timeout="
                + customMethodDurationString,
            "com.google.cloud.language.v2.language-service.annotate-text-retry.total-timeout="
                + customMethodDurationString,
            "com.google.cloud.language.v2.language-service.annotate-text-retry.max-attempts="
                + customMethodMaxAttempts.toString())
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              RetrySettings annotateTextRetrySettings =
                  client.getSettings().annotateTextSettings().getRetrySettings();
              RetrySettings analyzeSentimentRetrySettings =
                  client.getSettings().analyzeSentimentSettings().getRetrySettings();

              // Method-level overrides should be used for annotateText
              assertThat(annotateTextRetrySettings.getRetryDelayMultiplier())
                  .isEqualTo(customMethodMultiplier);
              assertThat(annotateTextRetrySettings.getRpcTimeoutMultiplier())
                  .isEqualTo(customMethodMultiplier);
              assertThat(annotateTextRetrySettings.getInitialRetryDelay())
                  .isEqualTo(customMethodDuration);
              assertThat(annotateTextRetrySettings.getInitialRpcTimeout())
                  .isEqualTo(customMethodDuration);
              assertThat(annotateTextRetrySettings.getMaxRetryDelay())
                  .isEqualTo(customMethodDuration);
              assertThat(annotateTextRetrySettings.getMaxRpcTimeout())
                  .isEqualTo(customMethodDuration);
              assertThat(annotateTextRetrySettings.getTotalTimeout())
                  .isEqualTo(customMethodDuration);
              assertThat(annotateTextRetrySettings.getMaxAttempts())
                  .isEqualTo(customMethodMaxAttempts);

              // Service-level overrides should be used for analyzeSentiment
              assertThat(analyzeSentimentRetrySettings.getRetryDelayMultiplier())
                  .isEqualTo(customServiceMultiplier);
              assertThat(analyzeSentimentRetrySettings.getRpcTimeoutMultiplier())
                  .isEqualTo(customServiceMultiplier);
              assertThat(analyzeSentimentRetrySettings.getInitialRetryDelay())
                  .isEqualTo(customServiceDuration);
              assertThat(analyzeSentimentRetrySettings.getInitialRpcTimeout())
                  .isEqualTo(customServiceDuration);
              assertThat(analyzeSentimentRetrySettings.getMaxRetryDelay())
                  .isEqualTo(customServiceDuration);
              assertThat(analyzeSentimentRetrySettings.getMaxRpcTimeout())
                  .isEqualTo(customServiceDuration);
              assertThat(analyzeSentimentRetrySettings.getTotalTimeout())
                  .isEqualTo(customServiceDuration);
              assertThat(analyzeSentimentRetrySettings.getMaxAttempts())
                  .isEqualTo(customServiceMaxAttempts);
            });
  }

  @Test
  void testRetrySettingsFromProperties_methodLevel_totalTimeout() {
    String customTimeoutString = "PT10S";
    Duration customTimeoutDuration = Duration.ofSeconds(10);

    this.contextRunner
        .withPropertyValues(
            // configure total timeout on method-level
            "com.google.cloud.language.v2.language-service.analyze-sentiment-retry.total-timeout="
                + customTimeoutString,
            "com.google.cloud.language.v2.language-service.analyze-entities-retry.total-timeout="
                + customTimeoutString,
            "com.google.cloud.language.v2.language-service.analyze-entity-sentiment-retry.total-timeout="
                + customTimeoutString,
            "com.google.cloud.language.v2.language-service.classify-text-retry.total-timeout="
                + customTimeoutString,
            "com.google.cloud.language.v2.language-service.annotate-text-retry.total-timeout="
                + customTimeoutString)
        .run(
            ctx -> {
              LanguageServiceClient client = ctx.getBean(LanguageServiceClient.class);
              LanguageServiceSettings settings = client.getSettings();

              // Method-level configurations
              assertThat(settings.analyzeEntitiesSettings().getRetrySettings().getTotalTimeout())
                  .isEqualTo(customTimeoutDuration);
              assertThat(settings.analyzeEntitiesSettings().getRetrySettings().getTotalTimeout())
                  .isEqualTo(customTimeoutDuration);
              assertThat(settings.analyzeSentimentSettings().getRetrySettings().getTotalTimeout())
                  .isEqualTo(customTimeoutDuration);
              assertThat(settings.classifyTextSettings().getRetrySettings().getTotalTimeout())
                  .isEqualTo(customTimeoutDuration);
              assertThat(settings.annotateTextSettings().getRetrySettings().getTotalTimeout())
                  .isEqualTo(customTimeoutDuration);
            });
  }
}
