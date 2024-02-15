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

package com.google.cloud.compute.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.compute.v1.RegionInstanceGroupManagersClient;
import com.google.cloud.compute.v1.RegionInstanceGroupManagersSettings;
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
 * Auto-configuration for {@link RegionInstanceGroupManagersClient}.
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
@ConditionalOnClass(RegionInstanceGroupManagersClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.compute.v1.region-instance-group-managers.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(RegionInstanceGroupManagersSpringProperties.class)
public class RegionInstanceGroupManagersSpringAutoConfiguration {
  private final RegionInstanceGroupManagersSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(RegionInstanceGroupManagersSpringAutoConfiguration.class);

  protected RegionInstanceGroupManagersSpringAutoConfiguration(
      RegionInstanceGroupManagersSpringProperties clientProperties,
      CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from RegionInstanceGroupManagers-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultRegionInstanceGroupManagersTransportChannelProvider")
  public TransportChannelProvider defaultRegionInstanceGroupManagersTransportChannelProvider() {
    return RegionInstanceGroupManagersSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a RegionInstanceGroupManagersSettings bean configured to use a
   * DefaultCredentialsProvider and the client library's default transport channel provider
   * (defaultRegionInstanceGroupManagersTransportChannelProvider()). It also configures the quota
   * project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in RegionInstanceGroupManagersSpringProperties. Method-level properties will take precedence
   * over service-level properties if available, and client library defaults will be used if neither
   * are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link RegionInstanceGroupManagersSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public RegionInstanceGroupManagersSettings regionInstanceGroupManagersSettings(
      @Qualifier("defaultRegionInstanceGroupManagersTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    RegionInstanceGroupManagersSettings.Builder clientSettingsBuilder =
        RegionInstanceGroupManagersSettings.newBuilder();
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
          RegionInstanceGroupManagersSettings.defaultExecutorProviderBuilder()
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
      RetrySettings getRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getSettings().setRetrySettings(getRetrySettings);

      RetrySettings listRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listSettings().setRetrySettings(listRetrySettings);

      RetrySettings listErrorsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listErrorsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listErrorsSettings().setRetrySettings(listErrorsRetrySettings);

      RetrySettings listManagedInstancesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listManagedInstancesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listManagedInstancesSettings()
          .setRetrySettings(listManagedInstancesRetrySettings);

      RetrySettings listPerInstanceConfigsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPerInstanceConfigsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listPerInstanceConfigsSettings()
          .setRetrySettings(listPerInstanceConfigsRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry getRetry = clientProperties.getGetRetry();
    if (getRetry != null) {
      RetrySettings getRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSettings().getRetrySettings(), getRetry);
      clientSettingsBuilder.getSettings().setRetrySettings(getRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for get from properties.");
      }
    }
    Retry listRetry = clientProperties.getListRetry();
    if (listRetry != null) {
      RetrySettings listRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSettings().getRetrySettings(), listRetry);
      clientSettingsBuilder.listSettings().setRetrySettings(listRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for list from properties.");
      }
    }
    Retry listErrorsRetry = clientProperties.getListErrorsRetry();
    if (listErrorsRetry != null) {
      RetrySettings listErrorsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listErrorsSettings().getRetrySettings(), listErrorsRetry);
      clientSettingsBuilder.listErrorsSettings().setRetrySettings(listErrorsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listErrors from properties.");
      }
    }
    Retry listManagedInstancesRetry = clientProperties.getListManagedInstancesRetry();
    if (listManagedInstancesRetry != null) {
      RetrySettings listManagedInstancesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listManagedInstancesSettings().getRetrySettings(),
              listManagedInstancesRetry);
      clientSettingsBuilder
          .listManagedInstancesSettings()
          .setRetrySettings(listManagedInstancesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listManagedInstances from properties.");
      }
    }
    Retry listPerInstanceConfigsRetry = clientProperties.getListPerInstanceConfigsRetry();
    if (listPerInstanceConfigsRetry != null) {
      RetrySettings listPerInstanceConfigsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPerInstanceConfigsSettings().getRetrySettings(),
              listPerInstanceConfigsRetry);
      clientSettingsBuilder
          .listPerInstanceConfigsSettings()
          .setRetrySettings(listPerInstanceConfigsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listPerInstanceConfigs from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a RegionInstanceGroupManagersClient bean configured with
   * RegionInstanceGroupManagersSettings.
   *
   * @param regionInstanceGroupManagersSettings settings to configure an instance of client bean.
   * @return a {@link RegionInstanceGroupManagersClient} bean configured with {@link
   *     RegionInstanceGroupManagersSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public RegionInstanceGroupManagersClient regionInstanceGroupManagersClient(
      RegionInstanceGroupManagersSettings regionInstanceGroupManagersSettings) throws IOException {
    return RegionInstanceGroupManagersClient.create(regionInstanceGroupManagersSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-region-instance-group-managers";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
