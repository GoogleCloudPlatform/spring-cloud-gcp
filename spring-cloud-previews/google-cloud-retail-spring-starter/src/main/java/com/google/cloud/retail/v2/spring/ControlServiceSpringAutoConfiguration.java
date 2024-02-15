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

package com.google.cloud.retail.v2.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.retail.v2.ControlServiceClient;
import com.google.cloud.retail.v2.ControlServiceSettings;
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
 * Auto-configuration for {@link ControlServiceClient}.
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
@ConditionalOnClass(ControlServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.retail.v2.control-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ControlServiceSpringProperties.class)
public class ControlServiceSpringAutoConfiguration {
  private final ControlServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(ControlServiceSpringAutoConfiguration.class);

  protected ControlServiceSpringAutoConfiguration(
      ControlServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from ControlService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultControlServiceTransportChannelProvider")
  public TransportChannelProvider defaultControlServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return ControlServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return ControlServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ControlServiceSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultControlServiceTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ControlServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ControlServiceSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ControlServiceSettings controlServiceSettings(
      @Qualifier("defaultControlServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ControlServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = ControlServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = ControlServiceSettings.newBuilder();
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
          ControlServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings createControlRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createControlSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createControlSettings().setRetrySettings(createControlRetrySettings);

      RetrySettings deleteControlRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteControlSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteControlSettings().setRetrySettings(deleteControlRetrySettings);

      RetrySettings updateControlRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateControlSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateControlSettings().setRetrySettings(updateControlRetrySettings);

      RetrySettings getControlRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getControlSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getControlSettings().setRetrySettings(getControlRetrySettings);

      RetrySettings listControlsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listControlsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listControlsSettings().setRetrySettings(listControlsRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry createControlRetry = clientProperties.getCreateControlRetry();
    if (createControlRetry != null) {
      RetrySettings createControlRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createControlSettings().getRetrySettings(), createControlRetry);
      clientSettingsBuilder.createControlSettings().setRetrySettings(createControlRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createControl from properties.");
      }
    }
    Retry deleteControlRetry = clientProperties.getDeleteControlRetry();
    if (deleteControlRetry != null) {
      RetrySettings deleteControlRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteControlSettings().getRetrySettings(), deleteControlRetry);
      clientSettingsBuilder.deleteControlSettings().setRetrySettings(deleteControlRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteControl from properties.");
      }
    }
    Retry updateControlRetry = clientProperties.getUpdateControlRetry();
    if (updateControlRetry != null) {
      RetrySettings updateControlRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateControlSettings().getRetrySettings(), updateControlRetry);
      clientSettingsBuilder.updateControlSettings().setRetrySettings(updateControlRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateControl from properties.");
      }
    }
    Retry getControlRetry = clientProperties.getGetControlRetry();
    if (getControlRetry != null) {
      RetrySettings getControlRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getControlSettings().getRetrySettings(), getControlRetry);
      clientSettingsBuilder.getControlSettings().setRetrySettings(getControlRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getControl from properties.");
      }
    }
    Retry listControlsRetry = clientProperties.getListControlsRetry();
    if (listControlsRetry != null) {
      RetrySettings listControlsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listControlsSettings().getRetrySettings(), listControlsRetry);
      clientSettingsBuilder.listControlsSettings().setRetrySettings(listControlsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listControls from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a ControlServiceClient bean configured with ControlServiceSettings.
   *
   * @param controlServiceSettings settings to configure an instance of client bean.
   * @return a {@link ControlServiceClient} bean configured with {@link ControlServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ControlServiceClient controlServiceClient(ControlServiceSettings controlServiceSettings)
      throws IOException {
    return ControlServiceClient.create(controlServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-control-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
