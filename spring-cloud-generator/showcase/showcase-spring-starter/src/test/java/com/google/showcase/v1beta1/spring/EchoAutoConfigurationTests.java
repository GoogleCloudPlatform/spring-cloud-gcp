/*
 * Copyright 2023 Google LLC
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

package com.google.showcase.v1beta1.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.TransportChannel;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.showcase.v1beta1.EchoClient;
import com.google.showcase.v1beta1.EchoSettings;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.threeten.bp.Duration;

@ExtendWith(MockitoExtension.class)
class EchoAutoConfigurationTests {

  private static final String SERVICE_CREDENTIAL_LOCATION =
      "src/test/resources/fake-credential-key.json";
  private static final String TOP_LEVEL_CREDENTIAL_LOCATION =
      "src/test/resources/fake-credential-key-2.json";
  private static final String SERVICE_CREDENTIAL_CLIENT_ID = "45678";
  private static final String TOP_LEVEL_CREDENTIAL_CLIENT_ID = "12345";
  private static final String SERVICE_OVERRIDE_CLIENT_ID = "56789";
  private static final String TRANSPORT_CHANNEL_PROVIDER_QUALIFIER_NAME =
      "defaultEchoTransportChannelProvider";

  @Mock private TransportChannel mockTransportChannel;
  @Mock private ApiCallContext mockApiCallContext;
  @Mock private TransportChannelProvider mockTransportChannelProvider;
  @Mock private CredentialsProvider mockCredentialsProvider;

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, EchoSpringAutoConfiguration.class))
          .withPropertyValues(
              "spring.cloud.gcp.credentials.location=file:" + TOP_LEVEL_CREDENTIAL_LOCATION);

  @Test
  void testEchoClientCreated() {
    this.contextRunner.run(
        ctx -> {
          EchoClient client = ctx.getBean(EchoClient.class);
          assertThat(client).isNotNull();
        });
  }

  @Test
  void testCredentials_fromServicePropertiesIfSpecified() {
    this.contextRunner
        .withPropertyValues(
            "com.google.showcase.v1beta1.echo.credentials.location=file:"
                + SERVICE_CREDENTIAL_LOCATION)
        .run(
            ctx -> {
              EchoClient client = ctx.getBean(EchoClient.class);
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
          EchoClient client = ctx.getBean(EchoClient.class);
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
              EchoClient client = ctx.getBean(EchoClient.class);
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
          EchoClient client = ctx.getBean(EchoClient.class);
          TransportChannelProvider transportChannelProvider =
              client.getSettings().getTransportChannelProvider();
          TransportChannelProvider defaultTransportChannelprovider =
              EchoSettings.defaultTransportChannelProvider();
          assertThat(transportChannelProvider)
              .usingRecursiveComparison()
              .isEqualTo(defaultTransportChannelprovider);
        });
  }

  //  @Test
  //  void testCustomTransportChannelProviderSetToRest() {
  //    this.contextRunner
  //        .withPropertyValues("com.google.showcase.v1beta1.echo.use-rest=true")
  //        .run(
  //            ctx -> {
  //              EchoClient client = ctx.getBean(EchoClient.class);
  //              TransportChannelProvider transportChannelProvider =
  //                  client.getSettings().getTransportChannelProvider();
  //              TransportChannelProvider defaultHttpJsonTransportChannelprovider =
  //                  EchoSettings.defaultHttpJsonTransportProviderBuilder().build();
  //              assertThat(transportChannelProvider)
  //                  .usingRecursiveComparison()
  //                  .isEqualTo(defaultHttpJsonTransportChannelprovider);
  //            });
  //  }

  @Test
  void testQuotaProjectIdFromProperties() {
    this.contextRunner
        .withPropertyValues(
            "com.google.showcase.v1beta1.echo.quota-project-id=" + SERVICE_OVERRIDE_CLIENT_ID)
        .run(
            ctx -> {
              EchoClient client = ctx.getBean(EchoClient.class);
              String quotaProjectId = client.getSettings().getQuotaProjectId();
              assertThat(quotaProjectId).isEqualTo(SERVICE_OVERRIDE_CLIENT_ID);
            });
  }

  @Test
  void testExecutorThreadCountFromProperties() {
    Integer customExecutorThreadCount = 3;
    this.contextRunner
        .withPropertyValues(
            "com.google.showcase.v1beta1.echo.executor-thread-count="
                + customExecutorThreadCount.toString())
        .run(
            ctx -> {
              EchoClient client = ctx.getBean(EchoClient.class);
              InstantiatingExecutorProvider backgroundExecutorProvider =
                  ((InstantiatingExecutorProvider)
                      client.getSettings().getBackgroundExecutorProvider());
              assertThat(backgroundExecutorProvider.toBuilder().getExecutorThreadCount())
                  .isEqualTo(customExecutorThreadCount);
            });
  }

  //  @Test
  //  void testCustomTransportChannelProviderUsedWhenProvided() throws IOException {
  //    when(mockTransportChannelProvider.getTransportName()).thenReturn("grpc");
  //    when(mockTransportChannelProvider.getTransportChannel()).thenReturn(mockTransportChannel);
  //    when(mockTransportChannel.getEmptyCallContext()).thenReturn(mockApiCallContext);
  //    when(mockApiCallContext.withCredentials(any())).thenReturn(mockApiCallContext);
  //    when(mockApiCallContext.withTransportChannel(any())).thenReturn(mockApiCallContext);
  //
  //    contextRunner
  //        .withBean(
  //            TRANSPORT_CHANNEL_PROVIDER_QUALIFIER_NAME,
  //            TransportChannelProvider.class,
  //            () -> mockTransportChannelProvider)
  //        .run(
  //            ctx -> {
  //              EchoClient client = ctx.getBean(EchoClient.class);
  //              assertThat(client.getSettings().getTransportChannelProvider())
  //                  .isSameAs(mockTransportChannelProvider);
  //            });
  //  }

  @Test
  void testCustomServiceSettingsUsedWhenProvided() throws IOException {
    EchoSettings customEchoSettings =
        EchoSettings.newBuilder()
            .setCredentialsProvider(mockCredentialsProvider)
            .setQuotaProjectId(SERVICE_OVERRIDE_CLIENT_ID)
            .build();
    contextRunner
        .withBean("echoSettings", EchoSettings.class, () -> customEchoSettings)
        .run(
            ctx -> {
              EchoClient client = ctx.getBean(EchoClient.class);
              assertThat(client.getSettings()).isSameAs(customEchoSettings);
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
            "com.google.showcase.v1beta1.echo.retry.retry-delay-multiplier="
                + customMultiplier.toString(),
            "com.google.showcase.v1beta1.echo.retry.max-retry-delay=" + customDurationString)
        .run(
            ctx -> {
              EchoClient client = ctx.getBean(EchoClient.class);

              RetrySettings pagedExpandRetrySettings =
                  client.getSettings().pagedExpandSettings().getRetrySettings();
              assertThat(pagedExpandRetrySettings.getRetryDelayMultiplier())
                  .isEqualTo(customMultiplier);
              assertThat(pagedExpandRetrySettings.getMaxRetryDelay()).isEqualTo(customDuration);
              // if properties only override certain retry settings, others should still take on
              // client library defaults
              assertThat(pagedExpandRetrySettings.getInitialRetryDelay())
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
            "com.google.showcase.v1beta1.echo.retry.retry-delay-multiplier="
                + customServiceMultiplier.toString(),
            "com.google.showcase.v1beta1.echo.retry.max-retry-delay=" + customDurationString,
            "com.google.showcase.v1beta1.echo.paged-expand-retry.retry-delay-multiplier="
                + customMethodMultiplier.toString())
        .run(
            ctx -> {
              EchoClient client = ctx.getBean(EchoClient.class);

              RetrySettings pagedExpandRetrySettings =
                  client.getSettings().pagedExpandSettings().getRetrySettings();
              // Method-level override should take precedence over service-level
              assertThat(pagedExpandRetrySettings.getRetryDelayMultiplier())
                  .isEqualTo(customMethodMultiplier);
              // For settings without method-level overrides but when service-level is provided,
              // fall back to that
              assertThat(pagedExpandRetrySettings.getMaxRetryDelay()).isEqualTo(customDuration);
              // Settings with neither method not service-level overrides should still take on
              // client library defaults
              assertThat(pagedExpandRetrySettings.getInitialRetryDelay())
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
            "com.google.showcase.v1beta1.echo.retry.retry-delay-multiplier="
                + customServiceMultiplier.toString(),
            "com.google.showcase.v1beta1.echo.retry.rpc-timeout-multiplier="
                + customServiceMultiplier.toString(),
            "com.google.showcase.v1beta1.echo.retry.initial-retry-delay="
                + customServiceDurationString,
            "com.google.showcase.v1beta1.echo.retry.max-retry-delay=" + customServiceDurationString,
            "com.google.showcase.v1beta1.echo.retry.initial-rpc-timeout="
                + customServiceDurationString,
            "com.google.showcase.v1beta1.echo.retry.max-rpc-timeout=" + customServiceDurationString,
            "com.google.showcase.v1beta1.echo.retry.total-timeout=" + customServiceDurationString,
            "com.google.showcase.v1beta1.echo.retry.max-attempts="
                + customServiceMaxAttempts.toString(),
            // method-level, all configurable settings
            "com.google.showcase.v1beta1.echo.paged-expand-retry.retry-delay-multiplier="
                + customMethodMultiplier.toString(),
            "com.google.showcase.v1beta1.echo.paged-expand-retry.rpc-timeout-multiplier="
                + customMethodMultiplier.toString(),
            "com.google.showcase.v1beta1.echo.paged-expand-retry.initial-retry-delay="
                + customMethodDurationString,
            "com.google.showcase.v1beta1.echo.paged-expand-retry.max-retry-delay="
                + customMethodDurationString,
            "com.google.showcase.v1beta1.echo.paged-expand-retry.initial-rpc-timeout="
                + customMethodDurationString,
            "com.google.showcase.v1beta1.echo.paged-expand-retry.max-rpc-timeout="
                + customMethodDurationString,
            "com.google.showcase.v1beta1.echo.paged-expand-retry.total-timeout="
                + customMethodDurationString,
            "com.google.showcase.v1beta1.echo.paged-expand-retry.max-attempts="
                + customMethodMaxAttempts.toString())
        .run(
            ctx -> {
              EchoClient client = ctx.getBean(EchoClient.class);
              RetrySettings echoRetrySettings =
                  client.getSettings().echoSettings().getRetrySettings();
              RetrySettings pagedExpandRetrySettings =
                  client.getSettings().pagedExpandSettings().getRetrySettings();

              // Method-level overrides should be used for paged-expand
              assertThat(pagedExpandRetrySettings.getRetryDelayMultiplier())
                  .isEqualTo(customMethodMultiplier);
              assertThat(pagedExpandRetrySettings.getRpcTimeoutMultiplier())
                  .isEqualTo(customMethodMultiplier);
              assertThat(pagedExpandRetrySettings.getInitialRetryDelay())
                  .isEqualTo(customMethodDuration);
              assertThat(pagedExpandRetrySettings.getInitialRpcTimeout())
                  .isEqualTo(customMethodDuration);
              assertThat(pagedExpandRetrySettings.getMaxRetryDelay())
                  .isEqualTo(customMethodDuration);
              assertThat(pagedExpandRetrySettings.getMaxRpcTimeout())
                  .isEqualTo(customMethodDuration);
              assertThat(pagedExpandRetrySettings.getTotalTimeout())
                  .isEqualTo(customMethodDuration);
              assertThat(pagedExpandRetrySettings.getMaxAttempts())
                  .isEqualTo(customMethodMaxAttempts);

              // Service-level overrides should be used for echo
              assertThat(echoRetrySettings.getRetryDelayMultiplier())
                  .isEqualTo(customServiceMultiplier);
              assertThat(echoRetrySettings.getRpcTimeoutMultiplier())
                  .isEqualTo(customServiceMultiplier);
              assertThat(echoRetrySettings.getInitialRetryDelay()).isEqualTo(customServiceDuration);
              assertThat(echoRetrySettings.getInitialRpcTimeout()).isEqualTo(customServiceDuration);
              assertThat(echoRetrySettings.getMaxRetryDelay()).isEqualTo(customServiceDuration);
              assertThat(echoRetrySettings.getMaxRpcTimeout()).isEqualTo(customServiceDuration);
              assertThat(echoRetrySettings.getTotalTimeout()).isEqualTo(customServiceDuration);
              assertThat(echoRetrySettings.getMaxAttempts()).isEqualTo(customServiceMaxAttempts);
            });
  }
}
