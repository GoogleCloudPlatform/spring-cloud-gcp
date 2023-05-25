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

package com.google.cloud.clouddms.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.clouddms.v1.DataMigrationServiceClient;
import com.google.cloud.clouddms.v1.DataMigrationServiceSettings;
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
 * Auto-configuration for {@link DataMigrationServiceClient}.
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
@ConditionalOnClass(DataMigrationServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.clouddms.v1.data-migration-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(DataMigrationServiceSpringProperties.class)
public class DataMigrationServiceSpringAutoConfiguration {
  private final DataMigrationServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(DataMigrationServiceSpringAutoConfiguration.class);

  protected DataMigrationServiceSpringAutoConfiguration(
      DataMigrationServiceSpringProperties clientProperties,
      CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from DataMigrationService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultDataMigrationServiceTransportChannelProvider")
  public TransportChannelProvider defaultDataMigrationServiceTransportChannelProvider() {
    return DataMigrationServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a DataMigrationServiceSettings bean configured to use a DefaultCredentialsProvider and
   * the client library's default transport channel provider
   * (defaultDataMigrationServiceTransportChannelProvider()). It also configures the quota project
   * ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in DataMigrationServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link DataMigrationServiceSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public DataMigrationServiceSettings dataMigrationServiceSettings(
      @Qualifier("defaultDataMigrationServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    DataMigrationServiceSettings.Builder clientSettingsBuilder =
        DataMigrationServiceSettings.newBuilder();
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
          DataMigrationServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listMigrationJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listMigrationJobsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listMigrationJobsSettings()
          .setRetrySettings(listMigrationJobsRetrySettings);

      RetrySettings getMigrationJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getMigrationJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getMigrationJobSettings()
          .setRetrySettings(getMigrationJobRetrySettings);

      RetrySettings generateSshScriptRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateSshScriptSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .generateSshScriptSettings()
          .setRetrySettings(generateSshScriptRetrySettings);

      RetrySettings listConnectionProfilesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConnectionProfilesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listConnectionProfilesSettings()
          .setRetrySettings(listConnectionProfilesRetrySettings);

      RetrySettings getConnectionProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConnectionProfileSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getConnectionProfileSettings()
          .setRetrySettings(getConnectionProfileRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listMigrationJobsRetry = clientProperties.getListMigrationJobsRetry();
    if (listMigrationJobsRetry != null) {
      RetrySettings listMigrationJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listMigrationJobsSettings().getRetrySettings(),
              listMigrationJobsRetry);
      clientSettingsBuilder
          .listMigrationJobsSettings()
          .setRetrySettings(listMigrationJobsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listMigrationJobs from properties.");
      }
    }
    Retry getMigrationJobRetry = clientProperties.getGetMigrationJobRetry();
    if (getMigrationJobRetry != null) {
      RetrySettings getMigrationJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getMigrationJobSettings().getRetrySettings(),
              getMigrationJobRetry);
      clientSettingsBuilder
          .getMigrationJobSettings()
          .setRetrySettings(getMigrationJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getMigrationJob from properties.");
      }
    }
    Retry generateSshScriptRetry = clientProperties.getGenerateSshScriptRetry();
    if (generateSshScriptRetry != null) {
      RetrySettings generateSshScriptRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateSshScriptSettings().getRetrySettings(),
              generateSshScriptRetry);
      clientSettingsBuilder
          .generateSshScriptSettings()
          .setRetrySettings(generateSshScriptRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for generateSshScript from properties.");
      }
    }
    Retry listConnectionProfilesRetry = clientProperties.getListConnectionProfilesRetry();
    if (listConnectionProfilesRetry != null) {
      RetrySettings listConnectionProfilesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConnectionProfilesSettings().getRetrySettings(),
              listConnectionProfilesRetry);
      clientSettingsBuilder
          .listConnectionProfilesSettings()
          .setRetrySettings(listConnectionProfilesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listConnectionProfiles from properties.");
      }
    }
    Retry getConnectionProfileRetry = clientProperties.getGetConnectionProfileRetry();
    if (getConnectionProfileRetry != null) {
      RetrySettings getConnectionProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConnectionProfileSettings().getRetrySettings(),
              getConnectionProfileRetry);
      clientSettingsBuilder
          .getConnectionProfileSettings()
          .setRetrySettings(getConnectionProfileRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getConnectionProfile from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a DataMigrationServiceClient bean configured with DataMigrationServiceSettings.
   *
   * @param dataMigrationServiceSettings settings to configure an instance of client bean.
   * @return a {@link DataMigrationServiceClient} bean configured with {@link
   *     DataMigrationServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public DataMigrationServiceClient dataMigrationServiceClient(
      DataMigrationServiceSettings dataMigrationServiceSettings) throws IOException {
    return DataMigrationServiceClient.create(dataMigrationServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-data-migration-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
