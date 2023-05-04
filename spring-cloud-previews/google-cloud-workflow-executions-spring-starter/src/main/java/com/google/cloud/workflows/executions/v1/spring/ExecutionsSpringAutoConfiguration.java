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

package com.google.cloud.workflows.executions.v1.spring;

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
import com.google.cloud.workflows.executions.v1.ExecutionsClient;
import com.google.cloud.workflows.executions.v1.ExecutionsSettings;
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
 * Auto-configuration for {@link ExecutionsClient}.
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
@ConditionalOnClass(ExecutionsClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.workflows.executions.v1.executions.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ExecutionsSpringProperties.class)
public class ExecutionsSpringAutoConfiguration {
  private final ExecutionsSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(ExecutionsSpringAutoConfiguration.class);

  protected ExecutionsSpringAutoConfiguration(
      ExecutionsSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Executions-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultExecutionsTransportChannelProvider")
  public TransportChannelProvider defaultExecutionsTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return ExecutionsSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return ExecutionsSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ExecutionsSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultExecutionsTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ExecutionsSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ExecutionsSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ExecutionsSettings executionsSettings(
      @Qualifier("defaultExecutionsTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ExecutionsSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = ExecutionsSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = ExecutionsSettings.newBuilder();
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
          ExecutionsSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listExecutionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listExecutionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listExecutionsSettings().setRetrySettings(listExecutionsRetrySettings);

      RetrySettings createExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createExecutionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createExecutionSettings()
          .setRetrySettings(createExecutionRetrySettings);

      RetrySettings getExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getExecutionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getExecutionSettings().setRetrySettings(getExecutionRetrySettings);

      RetrySettings cancelExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelExecutionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .cancelExecutionSettings()
          .setRetrySettings(cancelExecutionRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listExecutionsRetry = clientProperties.getListExecutionsRetry();
    if (listExecutionsRetry != null) {
      RetrySettings listExecutionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listExecutionsSettings().getRetrySettings(),
              listExecutionsRetry);
      clientSettingsBuilder.listExecutionsSettings().setRetrySettings(listExecutionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listExecutions from properties.");
      }
    }
    Retry createExecutionRetry = clientProperties.getCreateExecutionRetry();
    if (createExecutionRetry != null) {
      RetrySettings createExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createExecutionSettings().getRetrySettings(),
              createExecutionRetry);
      clientSettingsBuilder
          .createExecutionSettings()
          .setRetrySettings(createExecutionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createExecution from properties.");
      }
    }
    Retry getExecutionRetry = clientProperties.getGetExecutionRetry();
    if (getExecutionRetry != null) {
      RetrySettings getExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getExecutionSettings().getRetrySettings(), getExecutionRetry);
      clientSettingsBuilder.getExecutionSettings().setRetrySettings(getExecutionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getExecution from properties.");
      }
    }
    Retry cancelExecutionRetry = clientProperties.getCancelExecutionRetry();
    if (cancelExecutionRetry != null) {
      RetrySettings cancelExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelExecutionSettings().getRetrySettings(),
              cancelExecutionRetry);
      clientSettingsBuilder
          .cancelExecutionSettings()
          .setRetrySettings(cancelExecutionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for cancelExecution from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a ExecutionsClient bean configured with ExecutionsSettings.
   *
   * @param executionsSettings settings to configure an instance of client bean.
   * @return a {@link ExecutionsClient} bean configured with {@link ExecutionsSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ExecutionsClient executionsClient(ExecutionsSettings executionsSettings)
      throws IOException {
    return ExecutionsClient.create(executionsSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-executions";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
