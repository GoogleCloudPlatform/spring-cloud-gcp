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
import com.google.showcase.v1beta1.EchoClient;
import com.google.showcase.v1beta1.EchoSettings;
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
 * Auto-configuration for {@link EchoClient}.
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
@ConditionalOnClass(EchoClient.class)
@ConditionalOnProperty(value = "com.google.showcase.v1beta1.echo.enabled", matchIfMissing = true)
@EnableConfigurationProperties(EchoSpringProperties.class)
public class EchoSpringAutoConfiguration {
  private final EchoSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(EchoSpringAutoConfiguration.class);

  protected EchoSpringAutoConfiguration(
      EchoSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Echo-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultEchoTransportChannelProvider")
  public TransportChannelProvider defaultEchoTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return EchoSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return EchoSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a EchoSettings bean configured to use a DefaultCredentialsProvider and the client
   * library's default transport channel provider (defaultEchoTransportChannelProvider()). It also
   * configures the quota project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in EchoSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link EchoSettings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public EchoSettings echoSettings(
      @Qualifier("defaultEchoTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    EchoSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = EchoSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = EchoSettings.newBuilder();
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
          EchoSettings.defaultExecutorProviderBuilder()
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
      RetrySettings echoRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.echoSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.echoSettings().setRetrySettings(echoRetrySettings);

      RetrySettings pagedExpandRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.pagedExpandSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.pagedExpandSettings().setRetrySettings(pagedExpandRetrySettings);

      RetrySettings pagedExpandLegacyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.pagedExpandLegacySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .pagedExpandLegacySettings()
          .setRetrySettings(pagedExpandLegacyRetrySettings);

      RetrySettings pagedExpandLegacyMappedRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.pagedExpandLegacyMappedSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .pagedExpandLegacyMappedSettings()
          .setRetrySettings(pagedExpandLegacyMappedRetrySettings);

      RetrySettings blockRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.blockSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.blockSettings().setRetrySettings(blockRetrySettings);

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
    Retry echoRetry = clientProperties.getEchoRetry();
    if (echoRetry != null) {
      RetrySettings echoRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.echoSettings().getRetrySettings(), echoRetry);
      clientSettingsBuilder.echoSettings().setRetrySettings(echoRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for echo from properties.");
      }
    }
    Retry pagedExpandRetry = clientProperties.getPagedExpandRetry();
    if (pagedExpandRetry != null) {
      RetrySettings pagedExpandRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.pagedExpandSettings().getRetrySettings(), pagedExpandRetry);
      clientSettingsBuilder.pagedExpandSettings().setRetrySettings(pagedExpandRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for pagedExpand from properties.");
      }
    }
    Retry pagedExpandLegacyRetry = clientProperties.getPagedExpandLegacyRetry();
    if (pagedExpandLegacyRetry != null) {
      RetrySettings pagedExpandLegacyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.pagedExpandLegacySettings().getRetrySettings(),
              pagedExpandLegacyRetry);
      clientSettingsBuilder
          .pagedExpandLegacySettings()
          .setRetrySettings(pagedExpandLegacyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for pagedExpandLegacy from properties.");
      }
    }
    Retry pagedExpandLegacyMappedRetry = clientProperties.getPagedExpandLegacyMappedRetry();
    if (pagedExpandLegacyMappedRetry != null) {
      RetrySettings pagedExpandLegacyMappedRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.pagedExpandLegacyMappedSettings().getRetrySettings(),
              pagedExpandLegacyMappedRetry);
      clientSettingsBuilder
          .pagedExpandLegacyMappedSettings()
          .setRetrySettings(pagedExpandLegacyMappedRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for pagedExpandLegacyMapped from properties.");
      }
    }
    Retry blockRetry = clientProperties.getBlockRetry();
    if (blockRetry != null) {
      RetrySettings blockRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.blockSettings().getRetrySettings(), blockRetry);
      clientSettingsBuilder.blockSettings().setRetrySettings(blockRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for block from properties.");
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
   * Provides a EchoClient bean configured with EchoSettings.
   *
   * @param echoSettings settings to configure an instance of client bean.
   * @return a {@link EchoClient} bean configured with {@link EchoSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public EchoClient echoClient(EchoSettings echoSettings) throws IOException {
    return EchoClient.create(echoSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-echo";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
