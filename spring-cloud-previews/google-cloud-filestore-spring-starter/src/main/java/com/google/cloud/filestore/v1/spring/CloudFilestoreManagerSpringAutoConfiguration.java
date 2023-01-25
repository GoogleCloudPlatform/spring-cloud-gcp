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

package com.google.cloud.filestore.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.filestore.v1.CloudFilestoreManagerClient;
import com.google.cloud.filestore.v1.CloudFilestoreManagerSettings;
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
 * Auto-configuration for {@link CloudFilestoreManagerClient}.
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
@ConditionalOnClass(CloudFilestoreManagerClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.filestore.v1.cloud-filestore-manager.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(CloudFilestoreManagerSpringProperties.class)
public class CloudFilestoreManagerSpringAutoConfiguration {
  private final CloudFilestoreManagerSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(CloudFilestoreManagerSpringAutoConfiguration.class);

  protected CloudFilestoreManagerSpringAutoConfiguration(
      CloudFilestoreManagerSpringProperties clientProperties,
      CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from CloudFilestoreManager-specific configuration");
      }
      this.credentialsProvider =
          ((CredentialsProvider) new DefaultCredentialsProvider(this.clientProperties));
    } else {
      this.credentialsProvider = credentialsProvider;
    }
  }

  /**
   * Provides a default transport channel provider bean. The default is gRPC and will default to it
   * unless the useRest option is provided to use HTTP transport instead
   *
   * @return a default transport channel provider.
   */
  @Bean
  @ConditionalOnMissingBean(name = "defaultCloudFilestoreManagerTransportChannelProvider")
  public TransportChannelProvider defaultCloudFilestoreManagerTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return CloudFilestoreManagerSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return CloudFilestoreManagerSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a CloudFilestoreManagerSettings bean configured to use the default credentials
   * provider (obtained with cloudFilestoreManagerCredentials()) and its default transport channel
   * provider (defaultCloudFilestoreManagerTransportChannelProvider()). It also configures the quota
   * project ID if provided. It will configure an executor provider in case there is more than one
   * thread configured in the client
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in CloudFilestoreManagerSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link CloudFilestoreManagerSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public CloudFilestoreManagerSettings cloudFilestoreManagerSettings(
      @Qualifier("defaultCloudFilestoreManagerTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    CloudFilestoreManagerSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = CloudFilestoreManagerSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = CloudFilestoreManagerSettings.newBuilder();
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
          CloudFilestoreManagerSettings.defaultExecutorProviderBuilder()
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

      RetrySettings listBackupsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listBackupsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listBackupsSettings().setRetrySettings(listBackupsRetrySettings);

      RetrySettings getBackupRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBackupSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getBackupSettings().setRetrySettings(getBackupRetrySettings);

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
    Retry listBackupsRetry = clientProperties.getListBackupsRetry();
    if (listBackupsRetry != null) {
      RetrySettings listBackupsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listBackupsSettings().getRetrySettings(), listBackupsRetry);
      clientSettingsBuilder.listBackupsSettings().setRetrySettings(listBackupsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listBackups from properties.");
      }
    }
    Retry getBackupRetry = clientProperties.getGetBackupRetry();
    if (getBackupRetry != null) {
      RetrySettings getBackupRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBackupSettings().getRetrySettings(), getBackupRetry);
      clientSettingsBuilder.getBackupSettings().setRetrySettings(getBackupRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getBackup from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a CloudFilestoreManagerClient bean configured with CloudFilestoreManagerSettings.
   *
   * @param cloudFilestoreManagerSettings settings to configure an instance of client bean.
   * @return a {@link CloudFilestoreManagerClient} bean configured with {@link
   *     CloudFilestoreManagerSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public CloudFilestoreManagerClient cloudFilestoreManagerClient(
      CloudFilestoreManagerSettings cloudFilestoreManagerSettings) throws IOException {
    return CloudFilestoreManagerClient.create(cloudFilestoreManagerSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-cloud-filestore-manager";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
