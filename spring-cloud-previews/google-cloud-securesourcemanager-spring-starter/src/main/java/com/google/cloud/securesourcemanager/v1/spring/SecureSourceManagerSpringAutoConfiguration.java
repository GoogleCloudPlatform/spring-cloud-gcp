/*
 * Copyright 2024 Google LLC
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

package com.google.cloud.securesourcemanager.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.securesourcemanager.v1.SecureSourceManagerClient;
import com.google.cloud.securesourcemanager.v1.SecureSourceManagerSettings;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.Retry;
import com.google.cloud.spring.core.util.RetryUtil;
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
 * Auto-configuration for {@link SecureSourceManagerClient}.
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
@ConditionalOnClass(SecureSourceManagerClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.securesourcemanager.v1.secure-source-manager.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(SecureSourceManagerSpringProperties.class)
public class SecureSourceManagerSpringAutoConfiguration {
  private final SecureSourceManagerSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(SecureSourceManagerSpringAutoConfiguration.class);

  protected SecureSourceManagerSpringAutoConfiguration(
      SecureSourceManagerSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from SecureSourceManager-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultSecureSourceManagerTransportChannelProvider")
  public TransportChannelProvider defaultSecureSourceManagerTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return SecureSourceManagerSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return SecureSourceManagerSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a SecureSourceManagerSettings bean configured to use a DefaultCredentialsProvider and
   * the client library's default transport channel provider
   * (defaultSecureSourceManagerTransportChannelProvider()). It also configures the quota project ID
   * and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in SecureSourceManagerSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link SecureSourceManagerSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public SecureSourceManagerSettings secureSourceManagerSettings(
      @Qualifier("defaultSecureSourceManagerTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    SecureSourceManagerSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = SecureSourceManagerSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = SecureSourceManagerSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(SecureSourceManagerSettings.getDefaultEndpoint())
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
          SecureSourceManagerSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listInstancesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listInstancesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listInstancesSettings().setRetrySettings(listInstancesRetrySettings);

      RetrySettings getInstanceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getInstanceSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getInstanceSettings().setRetrySettings(getInstanceRetrySettings);

      RetrySettings listRepositoriesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRepositoriesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listRepositoriesSettings()
          .setRetrySettings(listRepositoriesRetrySettings);

      RetrySettings getRepositoryRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRepositorySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getRepositorySettings().setRetrySettings(getRepositoryRetrySettings);

      RetrySettings getIamPolicyRepoRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIamPolicyRepoSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getIamPolicyRepoSettings()
          .setRetrySettings(getIamPolicyRepoRetrySettings);

      RetrySettings setIamPolicyRepoRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setIamPolicyRepoSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .setIamPolicyRepoSettings()
          .setRetrySettings(setIamPolicyRepoRetrySettings);

      RetrySettings testIamPermissionsRepoRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.testIamPermissionsRepoSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .testIamPermissionsRepoSettings()
          .setRetrySettings(testIamPermissionsRepoRetrySettings);

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
    Retry listInstancesRetry = clientProperties.getListInstancesRetry();
    if (listInstancesRetry != null) {
      RetrySettings listInstancesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listInstancesSettings().getRetrySettings(), listInstancesRetry);
      clientSettingsBuilder.listInstancesSettings().setRetrySettings(listInstancesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listInstances from properties.");
      }
    }
    Retry getInstanceRetry = clientProperties.getGetInstanceRetry();
    if (getInstanceRetry != null) {
      RetrySettings getInstanceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getInstanceSettings().getRetrySettings(), getInstanceRetry);
      clientSettingsBuilder.getInstanceSettings().setRetrySettings(getInstanceRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getInstance from properties.");
      }
    }
    Retry listRepositoriesRetry = clientProperties.getListRepositoriesRetry();
    if (listRepositoriesRetry != null) {
      RetrySettings listRepositoriesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRepositoriesSettings().getRetrySettings(),
              listRepositoriesRetry);
      clientSettingsBuilder
          .listRepositoriesSettings()
          .setRetrySettings(listRepositoriesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listRepositories from properties.");
      }
    }
    Retry getRepositoryRetry = clientProperties.getGetRepositoryRetry();
    if (getRepositoryRetry != null) {
      RetrySettings getRepositoryRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRepositorySettings().getRetrySettings(), getRepositoryRetry);
      clientSettingsBuilder.getRepositorySettings().setRetrySettings(getRepositoryRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getRepository from properties.");
      }
    }
    Retry getIamPolicyRepoRetry = clientProperties.getGetIamPolicyRepoRetry();
    if (getIamPolicyRepoRetry != null) {
      RetrySettings getIamPolicyRepoRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIamPolicyRepoSettings().getRetrySettings(),
              getIamPolicyRepoRetry);
      clientSettingsBuilder
          .getIamPolicyRepoSettings()
          .setRetrySettings(getIamPolicyRepoRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getIamPolicyRepo from properties.");
      }
    }
    Retry setIamPolicyRepoRetry = clientProperties.getSetIamPolicyRepoRetry();
    if (setIamPolicyRepoRetry != null) {
      RetrySettings setIamPolicyRepoRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setIamPolicyRepoSettings().getRetrySettings(),
              setIamPolicyRepoRetry);
      clientSettingsBuilder
          .setIamPolicyRepoSettings()
          .setRetrySettings(setIamPolicyRepoRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for setIamPolicyRepo from properties.");
      }
    }
    Retry testIamPermissionsRepoRetry = clientProperties.getTestIamPermissionsRepoRetry();
    if (testIamPermissionsRepoRetry != null) {
      RetrySettings testIamPermissionsRepoRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.testIamPermissionsRepoSettings().getRetrySettings(),
              testIamPermissionsRepoRetry);
      clientSettingsBuilder
          .testIamPermissionsRepoSettings()
          .setRetrySettings(testIamPermissionsRepoRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for testIamPermissionsRepo from properties.");
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
   * Provides a SecureSourceManagerClient bean configured with SecureSourceManagerSettings.
   *
   * @param secureSourceManagerSettings settings to configure an instance of client bean.
   * @return a {@link SecureSourceManagerClient} bean configured with {@link
   *     SecureSourceManagerSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public SecureSourceManagerClient secureSourceManagerClient(
      SecureSourceManagerSettings secureSourceManagerSettings) throws IOException {
    return SecureSourceManagerClient.create(secureSourceManagerSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-secure-source-manager";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}