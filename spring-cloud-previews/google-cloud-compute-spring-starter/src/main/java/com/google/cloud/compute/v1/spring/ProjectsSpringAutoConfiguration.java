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
import com.google.cloud.compute.v1.ProjectsClient;
import com.google.cloud.compute.v1.ProjectsSettings;
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
 * Auto-configuration for {@link ProjectsClient}.
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
@ConditionalOnClass(ProjectsClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.compute.v1.projects.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ProjectsSpringProperties.class)
public class ProjectsSpringAutoConfiguration {
  private final ProjectsSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(ProjectsSpringAutoConfiguration.class);

  protected ProjectsSpringAutoConfiguration(
      ProjectsSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Projects-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultProjectsTransportChannelProvider")
  public TransportChannelProvider defaultProjectsTransportChannelProvider() {
    return ProjectsSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ProjectsSettings bean configured to use a DefaultCredentialsProvider and the client
   * library's default transport channel provider (defaultProjectsTransportChannelProvider()). It
   * also configures the quota project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ProjectsSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ProjectsSettings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ProjectsSettings projectsSettings(
      @Qualifier("defaultProjectsTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ProjectsSettings.Builder clientSettingsBuilder = ProjectsSettings.newBuilder();
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(ProjectsSettings.getDefaultEndpoint())
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
          ProjectsSettings.defaultExecutorProviderBuilder()
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

      RetrySettings getXpnHostRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getXpnHostSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getXpnHostSettings().setRetrySettings(getXpnHostRetrySettings);

      RetrySettings getXpnResourcesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getXpnResourcesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getXpnResourcesSettings()
          .setRetrySettings(getXpnResourcesRetrySettings);

      RetrySettings listXpnHostsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listXpnHostsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listXpnHostsSettings().setRetrySettings(listXpnHostsRetrySettings);

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
    Retry getXpnHostRetry = clientProperties.getGetXpnHostRetry();
    if (getXpnHostRetry != null) {
      RetrySettings getXpnHostRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getXpnHostSettings().getRetrySettings(), getXpnHostRetry);
      clientSettingsBuilder.getXpnHostSettings().setRetrySettings(getXpnHostRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getXpnHost from properties.");
      }
    }
    Retry getXpnResourcesRetry = clientProperties.getGetXpnResourcesRetry();
    if (getXpnResourcesRetry != null) {
      RetrySettings getXpnResourcesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getXpnResourcesSettings().getRetrySettings(),
              getXpnResourcesRetry);
      clientSettingsBuilder
          .getXpnResourcesSettings()
          .setRetrySettings(getXpnResourcesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getXpnResources from properties.");
      }
    }
    Retry listXpnHostsRetry = clientProperties.getListXpnHostsRetry();
    if (listXpnHostsRetry != null) {
      RetrySettings listXpnHostsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listXpnHostsSettings().getRetrySettings(), listXpnHostsRetry);
      clientSettingsBuilder.listXpnHostsSettings().setRetrySettings(listXpnHostsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listXpnHosts from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a ProjectsClient bean configured with ProjectsSettings.
   *
   * @param projectsSettings settings to configure an instance of client bean.
   * @return a {@link ProjectsClient} bean configured with {@link ProjectsSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ProjectsClient projectsClient(ProjectsSettings projectsSettings) throws IOException {
    return ProjectsClient.create(projectsSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-projects";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
