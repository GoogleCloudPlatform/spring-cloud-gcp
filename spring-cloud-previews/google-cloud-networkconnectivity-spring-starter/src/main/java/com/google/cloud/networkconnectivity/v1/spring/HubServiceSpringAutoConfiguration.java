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

package com.google.cloud.networkconnectivity.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.networkconnectivity.v1.HubServiceClient;
import com.google.cloud.networkconnectivity.v1.HubServiceSettings;
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
 * Auto-configuration for {@link HubServiceClient}.
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
@ConditionalOnClass(HubServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.networkconnectivity.v1.hub-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(HubServiceSpringProperties.class)
public class HubServiceSpringAutoConfiguration {
  private final HubServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(HubServiceSpringAutoConfiguration.class);

  protected HubServiceSpringAutoConfiguration(
      HubServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from HubService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultHubServiceTransportChannelProvider")
  public TransportChannelProvider defaultHubServiceTransportChannelProvider() {
    return HubServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a HubServiceSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultHubServiceTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in HubServiceSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link HubServiceSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public HubServiceSettings hubServiceSettings(
      @Qualifier("defaultHubServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    HubServiceSettings.Builder clientSettingsBuilder = HubServiceSettings.newBuilder();
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
          HubServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listHubsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listHubsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listHubsSettings().setRetrySettings(listHubsRetrySettings);

      RetrySettings getHubRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getHubSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getHubSettings().setRetrySettings(getHubRetrySettings);

      RetrySettings listSpokesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSpokesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listSpokesSettings().setRetrySettings(listSpokesRetrySettings);

      RetrySettings getSpokeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSpokeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getSpokeSettings().setRetrySettings(getSpokeRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listHubsRetry = clientProperties.getListHubsRetry();
    if (listHubsRetry != null) {
      RetrySettings listHubsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listHubsSettings().getRetrySettings(), listHubsRetry);
      clientSettingsBuilder.listHubsSettings().setRetrySettings(listHubsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listHubs from properties.");
      }
    }
    Retry getHubRetry = clientProperties.getGetHubRetry();
    if (getHubRetry != null) {
      RetrySettings getHubRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getHubSettings().getRetrySettings(), getHubRetry);
      clientSettingsBuilder.getHubSettings().setRetrySettings(getHubRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getHub from properties.");
      }
    }
    Retry listSpokesRetry = clientProperties.getListSpokesRetry();
    if (listSpokesRetry != null) {
      RetrySettings listSpokesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSpokesSettings().getRetrySettings(), listSpokesRetry);
      clientSettingsBuilder.listSpokesSettings().setRetrySettings(listSpokesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listSpokes from properties.");
      }
    }
    Retry getSpokeRetry = clientProperties.getGetSpokeRetry();
    if (getSpokeRetry != null) {
      RetrySettings getSpokeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSpokeSettings().getRetrySettings(), getSpokeRetry);
      clientSettingsBuilder.getSpokeSettings().setRetrySettings(getSpokeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getSpoke from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a HubServiceClient bean configured with HubServiceSettings.
   *
   * @param hubServiceSettings settings to configure an instance of client bean.
   * @return a {@link HubServiceClient} bean configured with {@link HubServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public HubServiceClient hubServiceClient(HubServiceSettings hubServiceSettings)
      throws IOException {
    return HubServiceClient.create(hubServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-hub-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
