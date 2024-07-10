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

package com.google.cloud.securityposture.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.securityposture.v1.SecurityPostureClient;
import com.google.cloud.securityposture.v1.SecurityPostureSettings;
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
 * Auto-configuration for {@link SecurityPostureClient}.
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
@ConditionalOnClass(SecurityPostureClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.securityposture.v1.security-posture.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(SecurityPostureSpringProperties.class)
public class SecurityPostureSpringAutoConfiguration {
  private final SecurityPostureSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(SecurityPostureSpringAutoConfiguration.class);

  protected SecurityPostureSpringAutoConfiguration(
      SecurityPostureSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from SecurityPosture-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultSecurityPostureTransportChannelProvider")
  public TransportChannelProvider defaultSecurityPostureTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return SecurityPostureSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return SecurityPostureSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a SecurityPostureSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultSecurityPostureTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in SecurityPostureSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link SecurityPostureSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public SecurityPostureSettings securityPostureSettings(
      @Qualifier("defaultSecurityPostureTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    SecurityPostureSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = SecurityPostureSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = SecurityPostureSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(SecurityPostureSettings.getDefaultEndpoint())
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
          SecurityPostureSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listPosturesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPosturesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listPosturesSettings().setRetrySettings(listPosturesRetrySettings);

      RetrySettings listPostureRevisionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPostureRevisionsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listPostureRevisionsSettings()
          .setRetrySettings(listPostureRevisionsRetrySettings);

      RetrySettings getPostureRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPostureSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getPostureSettings().setRetrySettings(getPostureRetrySettings);

      RetrySettings listPostureDeploymentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPostureDeploymentsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listPostureDeploymentsSettings()
          .setRetrySettings(listPostureDeploymentsRetrySettings);

      RetrySettings getPostureDeploymentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPostureDeploymentSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getPostureDeploymentSettings()
          .setRetrySettings(getPostureDeploymentRetrySettings);

      RetrySettings listPostureTemplatesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPostureTemplatesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listPostureTemplatesSettings()
          .setRetrySettings(listPostureTemplatesRetrySettings);

      RetrySettings getPostureTemplateRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPostureTemplateSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getPostureTemplateSettings()
          .setRetrySettings(getPostureTemplateRetrySettings);

      RetrySettings listLocationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listLocationsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listLocationsSettings().setRetrySettings(listLocationsRetrySettings);

      RetrySettings getLocationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getLocationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getLocationSettings().setRetrySettings(getLocationRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listPosturesRetry = clientProperties.getListPosturesRetry();
    if (listPosturesRetry != null) {
      RetrySettings listPosturesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPosturesSettings().getRetrySettings(), listPosturesRetry);
      clientSettingsBuilder.listPosturesSettings().setRetrySettings(listPosturesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listPostures from properties.");
      }
    }
    Retry listPostureRevisionsRetry = clientProperties.getListPostureRevisionsRetry();
    if (listPostureRevisionsRetry != null) {
      RetrySettings listPostureRevisionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPostureRevisionsSettings().getRetrySettings(),
              listPostureRevisionsRetry);
      clientSettingsBuilder
          .listPostureRevisionsSettings()
          .setRetrySettings(listPostureRevisionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listPostureRevisions from properties.");
      }
    }
    Retry getPostureRetry = clientProperties.getGetPostureRetry();
    if (getPostureRetry != null) {
      RetrySettings getPostureRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPostureSettings().getRetrySettings(), getPostureRetry);
      clientSettingsBuilder.getPostureSettings().setRetrySettings(getPostureRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getPosture from properties.");
      }
    }
    Retry listPostureDeploymentsRetry = clientProperties.getListPostureDeploymentsRetry();
    if (listPostureDeploymentsRetry != null) {
      RetrySettings listPostureDeploymentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPostureDeploymentsSettings().getRetrySettings(),
              listPostureDeploymentsRetry);
      clientSettingsBuilder
          .listPostureDeploymentsSettings()
          .setRetrySettings(listPostureDeploymentsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listPostureDeployments from properties.");
      }
    }
    Retry getPostureDeploymentRetry = clientProperties.getGetPostureDeploymentRetry();
    if (getPostureDeploymentRetry != null) {
      RetrySettings getPostureDeploymentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPostureDeploymentSettings().getRetrySettings(),
              getPostureDeploymentRetry);
      clientSettingsBuilder
          .getPostureDeploymentSettings()
          .setRetrySettings(getPostureDeploymentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getPostureDeployment from properties.");
      }
    }
    Retry listPostureTemplatesRetry = clientProperties.getListPostureTemplatesRetry();
    if (listPostureTemplatesRetry != null) {
      RetrySettings listPostureTemplatesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPostureTemplatesSettings().getRetrySettings(),
              listPostureTemplatesRetry);
      clientSettingsBuilder
          .listPostureTemplatesSettings()
          .setRetrySettings(listPostureTemplatesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listPostureTemplates from properties.");
      }
    }
    Retry getPostureTemplateRetry = clientProperties.getGetPostureTemplateRetry();
    if (getPostureTemplateRetry != null) {
      RetrySettings getPostureTemplateRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPostureTemplateSettings().getRetrySettings(),
              getPostureTemplateRetry);
      clientSettingsBuilder
          .getPostureTemplateSettings()
          .setRetrySettings(getPostureTemplateRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getPostureTemplate from properties.");
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
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a SecurityPostureClient bean configured with SecurityPostureSettings.
   *
   * @param securityPostureSettings settings to configure an instance of client bean.
   * @return a {@link SecurityPostureClient} bean configured with {@link SecurityPostureSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public SecurityPostureClient securityPostureClient(
      SecurityPostureSettings securityPostureSettings) throws IOException {
    return SecurityPostureClient.create(securityPostureSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-security-posture";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}