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

package com.google.cloud.workflows.v1.spring;

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
import com.google.cloud.workflows.v1.WorkflowsClient;
import com.google.cloud.workflows.v1.WorkflowsSettings;
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
 * Auto-configuration for {@link WorkflowsClient}.
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
@ConditionalOnClass(WorkflowsClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.workflows.v1.workflows.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(WorkflowsSpringProperties.class)
public class WorkflowsSpringAutoConfiguration {
  private final WorkflowsSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(WorkflowsSpringAutoConfiguration.class);

  protected WorkflowsSpringAutoConfiguration(
      WorkflowsSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Workflows-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultWorkflowsTransportChannelProvider")
  public TransportChannelProvider defaultWorkflowsTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return WorkflowsSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return WorkflowsSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a WorkflowsSettings bean configured to use a DefaultCredentialsProvider and the client
   * library's default transport channel provider (defaultWorkflowsTransportChannelProvider()). It
   * also configures the quota project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in WorkflowsSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link WorkflowsSettings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public WorkflowsSettings workflowsSettings(
      @Qualifier("defaultWorkflowsTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    WorkflowsSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = WorkflowsSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = WorkflowsSettings.newBuilder();
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
          WorkflowsSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listWorkflowsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listWorkflowsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listWorkflowsSettings().setRetrySettings(listWorkflowsRetrySettings);

      RetrySettings getWorkflowRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getWorkflowSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getWorkflowSettings().setRetrySettings(getWorkflowRetrySettings);

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
    Retry listWorkflowsRetry = clientProperties.getListWorkflowsRetry();
    if (listWorkflowsRetry != null) {
      RetrySettings listWorkflowsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listWorkflowsSettings().getRetrySettings(), listWorkflowsRetry);
      clientSettingsBuilder.listWorkflowsSettings().setRetrySettings(listWorkflowsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listWorkflows from properties.");
      }
    }
    Retry getWorkflowRetry = clientProperties.getGetWorkflowRetry();
    if (getWorkflowRetry != null) {
      RetrySettings getWorkflowRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getWorkflowSettings().getRetrySettings(), getWorkflowRetry);
      clientSettingsBuilder.getWorkflowSettings().setRetrySettings(getWorkflowRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getWorkflow from properties.");
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
   * Provides a WorkflowsClient bean configured with WorkflowsSettings.
   *
   * @param workflowsSettings settings to configure an instance of client bean.
   * @return a {@link WorkflowsClient} bean configured with {@link WorkflowsSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public WorkflowsClient workflowsClient(WorkflowsSettings workflowsSettings) throws IOException {
    return WorkflowsClient.create(workflowsSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-workflows";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
