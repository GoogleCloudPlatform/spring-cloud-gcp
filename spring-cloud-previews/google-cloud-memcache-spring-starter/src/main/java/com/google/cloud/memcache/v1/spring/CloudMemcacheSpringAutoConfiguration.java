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

package com.google.cloud.memcache.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.memcache.v1.CloudMemcacheClient;
import com.google.cloud.memcache.v1.CloudMemcacheSettings;
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
 * Auto-configuration for {@link CloudMemcacheClient}.
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
@ConditionalOnClass(CloudMemcacheClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.memcache.v1.cloud-memcache.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(CloudMemcacheSpringProperties.class)
public class CloudMemcacheSpringAutoConfiguration {
  private final CloudMemcacheSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(CloudMemcacheSpringAutoConfiguration.class);

  protected CloudMemcacheSpringAutoConfiguration(
      CloudMemcacheSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from CloudMemcache-specific configuration");
      }
      this.credentialsProvider =
          ((CredentialsProvider) new DefaultCredentialsProvider(this.clientProperties));
    } else {
      this.credentialsProvider = credentialsProvider;
    }
  }

  /**
   * Provides a default transport channel provider bean. The default is gRPC and will default to it
   * unless the useRest option is supported and provided to use HTTP transport instead
   *
   * @return a default transport channel provider.
   */
  @Bean
  @ConditionalOnMissingBean(name = "defaultCloudMemcacheTransportChannelProvider")
  public TransportChannelProvider defaultCloudMemcacheTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return CloudMemcacheSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return CloudMemcacheSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a CloudMemcacheSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultCloudMemcacheTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in CloudMemcacheSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link CloudMemcacheSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public CloudMemcacheSettings cloudMemcacheSettings(
      @Qualifier("defaultCloudMemcacheTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    CloudMemcacheSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = CloudMemcacheSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = CloudMemcacheSettings.newBuilder();
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
          CloudMemcacheSettings.defaultExecutorProviderBuilder()
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
   * Provides a CloudMemcacheClient bean configured with CloudMemcacheSettings.
   *
   * @param cloudMemcacheSettings settings to configure an instance of client bean.
   * @return a {@link CloudMemcacheClient} bean configured with {@link CloudMemcacheSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public CloudMemcacheClient cloudMemcacheClient(CloudMemcacheSettings cloudMemcacheSettings)
      throws IOException {
    return CloudMemcacheClient.create(cloudMemcacheSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-cloud-memcache";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
