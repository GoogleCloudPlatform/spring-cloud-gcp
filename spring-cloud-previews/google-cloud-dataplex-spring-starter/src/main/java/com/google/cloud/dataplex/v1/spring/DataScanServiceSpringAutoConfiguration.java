/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.dataplex.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.dataplex.v1.DataScanServiceClient;
import com.google.cloud.dataplex.v1.DataScanServiceSettings;
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
 * Auto-configuration for {@link DataScanServiceClient}.
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
@ConditionalOnClass(DataScanServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.dataplex.v1.data-scan-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(DataScanServiceSpringProperties.class)
public class DataScanServiceSpringAutoConfiguration {
  private final DataScanServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(DataScanServiceSpringAutoConfiguration.class);

  protected DataScanServiceSpringAutoConfiguration(
      DataScanServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from DataScanService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultDataScanServiceTransportChannelProvider")
  public TransportChannelProvider defaultDataScanServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return DataScanServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return DataScanServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a DataScanServiceSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultDataScanServiceTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in DataScanServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link DataScanServiceSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public DataScanServiceSettings dataScanServiceSettings(
      @Qualifier("defaultDataScanServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    DataScanServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = DataScanServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = DataScanServiceSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(DataScanServiceSettings.getDefaultEndpoint())
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
          DataScanServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings getDataScanRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getDataScanSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getDataScanSettings().setRetrySettings(getDataScanRetrySettings);

      RetrySettings listDataScansRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listDataScansSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listDataScansSettings().setRetrySettings(listDataScansRetrySettings);

      RetrySettings runDataScanRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.runDataScanSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.runDataScanSettings().setRetrySettings(runDataScanRetrySettings);

      RetrySettings getDataScanJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getDataScanJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getDataScanJobSettings().setRetrySettings(getDataScanJobRetrySettings);

      RetrySettings listDataScanJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listDataScanJobsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listDataScanJobsSettings()
          .setRetrySettings(listDataScanJobsRetrySettings);

      RetrySettings generateDataQualityRulesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateDataQualityRulesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .generateDataQualityRulesSettings()
          .setRetrySettings(generateDataQualityRulesRetrySettings);

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
    Retry getDataScanRetry = clientProperties.getGetDataScanRetry();
    if (getDataScanRetry != null) {
      RetrySettings getDataScanRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getDataScanSettings().getRetrySettings(), getDataScanRetry);
      clientSettingsBuilder.getDataScanSettings().setRetrySettings(getDataScanRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getDataScan from properties.");
      }
    }
    Retry listDataScansRetry = clientProperties.getListDataScansRetry();
    if (listDataScansRetry != null) {
      RetrySettings listDataScansRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listDataScansSettings().getRetrySettings(), listDataScansRetry);
      clientSettingsBuilder.listDataScansSettings().setRetrySettings(listDataScansRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listDataScans from properties.");
      }
    }
    Retry runDataScanRetry = clientProperties.getRunDataScanRetry();
    if (runDataScanRetry != null) {
      RetrySettings runDataScanRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.runDataScanSettings().getRetrySettings(), runDataScanRetry);
      clientSettingsBuilder.runDataScanSettings().setRetrySettings(runDataScanRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for runDataScan from properties.");
      }
    }
    Retry getDataScanJobRetry = clientProperties.getGetDataScanJobRetry();
    if (getDataScanJobRetry != null) {
      RetrySettings getDataScanJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getDataScanJobSettings().getRetrySettings(),
              getDataScanJobRetry);
      clientSettingsBuilder.getDataScanJobSettings().setRetrySettings(getDataScanJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getDataScanJob from properties.");
      }
    }
    Retry listDataScanJobsRetry = clientProperties.getListDataScanJobsRetry();
    if (listDataScanJobsRetry != null) {
      RetrySettings listDataScanJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listDataScanJobsSettings().getRetrySettings(),
              listDataScanJobsRetry);
      clientSettingsBuilder
          .listDataScanJobsSettings()
          .setRetrySettings(listDataScanJobsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listDataScanJobs from properties.");
      }
    }
    Retry generateDataQualityRulesRetry = clientProperties.getGenerateDataQualityRulesRetry();
    if (generateDataQualityRulesRetry != null) {
      RetrySettings generateDataQualityRulesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateDataQualityRulesSettings().getRetrySettings(),
              generateDataQualityRulesRetry);
      clientSettingsBuilder
          .generateDataQualityRulesSettings()
          .setRetrySettings(generateDataQualityRulesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for generateDataQualityRules from properties.");
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
   * Provides a DataScanServiceClient bean configured with DataScanServiceSettings.
   *
   * @param dataScanServiceSettings settings to configure an instance of client bean.
   * @return a {@link DataScanServiceClient} bean configured with {@link DataScanServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public DataScanServiceClient dataScanServiceClient(
      DataScanServiceSettings dataScanServiceSettings) throws IOException {
    return DataScanServiceClient.create(dataScanServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-data-scan-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
