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

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.Retry;
import com.google.cloud.spring.core.util.RetryUtil;
import com.google.showcase.v1beta1.TestingClient;
import com.google.showcase.v1beta1.TestingSettings;
import java.io.IOException;
import java.util.Collections;
import javax.annotation.Generated;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Auto-configuration for {@link TestingClient}.
 *
 * <p>Provides auto-configuration for Spring Boot
 *
 * <p>The default instance has everything set to sensible defaults:
 *
 * <ul>
 *   <li>The default transport provider is used.
 *   <li>Credentials are acquired automatically through Application Default Credentials.
 *   <li>Retries are configured for idempotent methods but not for non-idempotent methods.
 * </ul>
 */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@AutoConfiguration
@AutoConfigureAfter(GcpContextAutoConfiguration.class)
@ConditionalOnClass(TestingClient.class)
@ConditionalOnProperty(value = "com.google.showcase.v1beta1.testing.enabled", matchIfMissing = true)
@EnableConfigurationProperties(TestingSpringProperties.class)
public class TestingSpringAutoConfiguration {
  private final TestingSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(TestingSpringAutoConfiguration.class);

  protected TestingSpringAutoConfiguration(
      TestingSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Testing-specific configuration");
      }
      this.credentialsProvider =
          ((CredentialsProvider) new DefaultCredentialsProvider(this.clientProperties));
    } else {
      this.credentialsProvider = credentialsProvider;
    }
  }

  /**
   * Provides a default transport channel provider bean, corresponding to the client library's
   * default transport channel provider. If the library supports both GRPC and REST transport, and
   * the useRest property is configured, the HTTP/JSON transport provider will be used instead of
   * GRPC.
   *
   * @return a default transport channel provider.
   */
  @Bean
  @ConditionalOnMissingBean(name = "defaultTestingTransportChannelProvider")
  public TransportChannelProvider defaultTestingTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return TestingSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return TestingSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a TestingSettings bean configured to use a DefaultCredentialsProvider and the client
   * library's default transport channel provider (defaultTestingTransportChannelProvider()). It
   * also configures the quota project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in TestingSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link TestingSettings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public TestingSettings testingSettings(
      @Qualifier("defaultTestingTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    TestingSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = TestingSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = TestingSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setHeaderProvider(this.userAgentHeaderProvider());
    if (this.clientProperties.getQuotaProjectId() != null) {
      clientSettingsBuilder.setQuotaProjectId(this.clientProperties.getQuotaProjectId());
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Quota project id set to "
                + this.clientProperties.getQuotaProjectId()
                + ", this overrides project id from credentials.");
      }
    }
    if (this.clientProperties.getExecutorThreadCount() != null) {
      ExecutorProvider executorProvider =
          TestingSettings.defaultExecutorProviderBuilder()
              .setExecutorThreadCount(this.clientProperties.getExecutorThreadCount())
              .build();
      clientSettingsBuilder.setBackgroundExecutorProvider(executorProvider);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Background executor thread count is "
                + this.clientProperties.getExecutorThreadCount());
      }
    }
    Retry serviceRetry = clientProperties.getRetry();
    if (serviceRetry != null) {
      RetrySettings createSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createSessionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createSessionSettings().setRetrySettings(createSessionRetrySettings);

      RetrySettings getSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSessionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getSessionSettings().setRetrySettings(getSessionRetrySettings);

      RetrySettings listSessionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSessionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listSessionsSettings().setRetrySettings(listSessionsRetrySettings);

      RetrySettings deleteSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteSessionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteSessionSettings().setRetrySettings(deleteSessionRetrySettings);

      RetrySettings reportSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.reportSessionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.reportSessionSettings().setRetrySettings(reportSessionRetrySettings);

      RetrySettings listTestsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listTestsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listTestsSettings().setRetrySettings(listTestsRetrySettings);

      RetrySettings deleteTestRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteTestSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteTestSettings().setRetrySettings(deleteTestRetrySettings);

      RetrySettings verifyTestRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.verifyTestSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.verifyTestSettings().setRetrySettings(verifyTestRetrySettings);

      RetrySettings listLocationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listLocationsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listLocationsSettings().setRetrySettings(listLocationsRetrySettings);

      RetrySettings getLocationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getLocationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getLocationSettings().setRetrySettings(getLocationRetrySettings);

      RetrySettings setIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setIamPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.setIamPolicySettings().setRetrySettings(setIamPolicyRetrySettings);

      RetrySettings getIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIamPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getIamPolicySettings().setRetrySettings(getIamPolicyRetrySettings);

      RetrySettings testIamPermissionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.testIamPermissionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .testIamPermissionsSettings()
          .setRetrySettings(testIamPermissionsRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry createSessionRetry = clientProperties.getCreateSessionRetry();
    if (createSessionRetry != null) {
      RetrySettings createSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createSessionSettings().getRetrySettings(), createSessionRetry);
      clientSettingsBuilder.createSessionSettings().setRetrySettings(createSessionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createSession from properties.");
      }
    }
    Retry getSessionRetry = clientProperties.getGetSessionRetry();
    if (getSessionRetry != null) {
      RetrySettings getSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSessionSettings().getRetrySettings(), getSessionRetry);
      clientSettingsBuilder.getSessionSettings().setRetrySettings(getSessionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getSession from properties.");
      }
    }
    Retry listSessionsRetry = clientProperties.getListSessionsRetry();
    if (listSessionsRetry != null) {
      RetrySettings listSessionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSessionsSettings().getRetrySettings(), listSessionsRetry);
      clientSettingsBuilder.listSessionsSettings().setRetrySettings(listSessionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listSessions from properties.");
      }
    }
    Retry deleteSessionRetry = clientProperties.getDeleteSessionRetry();
    if (deleteSessionRetry != null) {
      RetrySettings deleteSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteSessionSettings().getRetrySettings(), deleteSessionRetry);
      clientSettingsBuilder.deleteSessionSettings().setRetrySettings(deleteSessionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteSession from properties.");
      }
    }
    Retry reportSessionRetry = clientProperties.getReportSessionRetry();
    if (reportSessionRetry != null) {
      RetrySettings reportSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.reportSessionSettings().getRetrySettings(), reportSessionRetry);
      clientSettingsBuilder.reportSessionSettings().setRetrySettings(reportSessionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for reportSession from properties.");
      }
    }
    Retry listTestsRetry = clientProperties.getListTestsRetry();
    if (listTestsRetry != null) {
      RetrySettings listTestsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listTestsSettings().getRetrySettings(), listTestsRetry);
      clientSettingsBuilder.listTestsSettings().setRetrySettings(listTestsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listTests from properties.");
      }
    }
    Retry deleteTestRetry = clientProperties.getDeleteTestRetry();
    if (deleteTestRetry != null) {
      RetrySettings deleteTestRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteTestSettings().getRetrySettings(), deleteTestRetry);
      clientSettingsBuilder.deleteTestSettings().setRetrySettings(deleteTestRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteTest from properties.");
      }
    }
    Retry verifyTestRetry = clientProperties.getVerifyTestRetry();
    if (verifyTestRetry != null) {
      RetrySettings verifyTestRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.verifyTestSettings().getRetrySettings(), verifyTestRetry);
      clientSettingsBuilder.verifyTestSettings().setRetrySettings(verifyTestRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for verifyTest from properties.");
      }
    }
    Retry listLocationsRetry = clientProperties.getListLocationsRetry();
    if (listLocationsRetry != null) {
      RetrySettings listLocationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listLocationsSettings().getRetrySettings(), listLocationsRetry);
      clientSettingsBuilder.listLocationsSettings().setRetrySettings(listLocationsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listLocations from properties.");
      }
    }
    Retry getLocationRetry = clientProperties.getGetLocationRetry();
    if (getLocationRetry != null) {
      RetrySettings getLocationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getLocationSettings().getRetrySettings(), getLocationRetry);
      clientSettingsBuilder.getLocationSettings().setRetrySettings(getLocationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getLocation from properties.");
      }
    }
    Retry setIamPolicyRetry = clientProperties.getSetIamPolicyRetry();
    if (setIamPolicyRetry != null) {
      RetrySettings setIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setIamPolicySettings().getRetrySettings(), setIamPolicyRetry);
      clientSettingsBuilder.setIamPolicySettings().setRetrySettings(setIamPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for setIamPolicy from properties.");
      }
    }
    Retry getIamPolicyRetry = clientProperties.getGetIamPolicyRetry();
    if (getIamPolicyRetry != null) {
      RetrySettings getIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIamPolicySettings().getRetrySettings(), getIamPolicyRetry);
      clientSettingsBuilder.getIamPolicySettings().setRetrySettings(getIamPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getIamPolicy from properties.");
      }
    }
    Retry testIamPermissionsRetry = clientProperties.getTestIamPermissionsRetry();
    if (testIamPermissionsRetry != null) {
      RetrySettings testIamPermissionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.testIamPermissionsSettings().getRetrySettings(),
              testIamPermissionsRetry);
      clientSettingsBuilder
          .testIamPermissionsSettings()
          .setRetrySettings(testIamPermissionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for testIamPermissions from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a TestingClient bean configured with TestingSettings.
   *
   * @param testingSettings settings to configure an instance of client bean.
   * @return a {@link TestingClient} bean configured with {@link TestingSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public TestingClient testingClient(TestingSettings testingSettings) throws IOException {
    return TestingClient.create(testingSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-testing";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
