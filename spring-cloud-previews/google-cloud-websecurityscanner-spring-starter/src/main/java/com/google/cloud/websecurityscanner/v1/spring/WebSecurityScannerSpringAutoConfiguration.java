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

package com.google.cloud.websecurityscanner.v1.spring;

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
import com.google.cloud.websecurityscanner.v1.WebSecurityScannerClient;
import com.google.cloud.websecurityscanner.v1.WebSecurityScannerSettings;
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
 * Auto-configuration for {@link WebSecurityScannerClient}.
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
@ConditionalOnClass(WebSecurityScannerClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.websecurityscanner.v1.web-security-scanner.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(WebSecurityScannerSpringProperties.class)
public class WebSecurityScannerSpringAutoConfiguration {
  private final WebSecurityScannerSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(WebSecurityScannerSpringAutoConfiguration.class);

  protected WebSecurityScannerSpringAutoConfiguration(
      WebSecurityScannerSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from WebSecurityScanner-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultWebSecurityScannerTransportChannelProvider")
  public TransportChannelProvider defaultWebSecurityScannerTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return WebSecurityScannerSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return WebSecurityScannerSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a WebSecurityScannerSettings bean configured to use a DefaultCredentialsProvider and
   * the client library's default transport channel provider
   * (defaultWebSecurityScannerTransportChannelProvider()). It also configures the quota project ID
   * and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in WebSecurityScannerSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link WebSecurityScannerSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public WebSecurityScannerSettings webSecurityScannerSettings(
      @Qualifier("defaultWebSecurityScannerTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    WebSecurityScannerSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = WebSecurityScannerSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = WebSecurityScannerSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(WebSecurityScannerSettings.getDefaultEndpoint())
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
          WebSecurityScannerSettings.defaultExecutorProviderBuilder()
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
      RetrySettings createScanConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createScanConfigSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createScanConfigSettings()
          .setRetrySettings(createScanConfigRetrySettings);

      RetrySettings deleteScanConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteScanConfigSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .deleteScanConfigSettings()
          .setRetrySettings(deleteScanConfigRetrySettings);

      RetrySettings getScanConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getScanConfigSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getScanConfigSettings().setRetrySettings(getScanConfigRetrySettings);

      RetrySettings listScanConfigsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listScanConfigsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listScanConfigsSettings()
          .setRetrySettings(listScanConfigsRetrySettings);

      RetrySettings updateScanConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateScanConfigSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateScanConfigSettings()
          .setRetrySettings(updateScanConfigRetrySettings);

      RetrySettings startScanRunRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.startScanRunSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.startScanRunSettings().setRetrySettings(startScanRunRetrySettings);

      RetrySettings getScanRunRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getScanRunSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getScanRunSettings().setRetrySettings(getScanRunRetrySettings);

      RetrySettings listScanRunsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listScanRunsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listScanRunsSettings().setRetrySettings(listScanRunsRetrySettings);

      RetrySettings stopScanRunRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.stopScanRunSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.stopScanRunSettings().setRetrySettings(stopScanRunRetrySettings);

      RetrySettings listCrawledUrlsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listCrawledUrlsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listCrawledUrlsSettings()
          .setRetrySettings(listCrawledUrlsRetrySettings);

      RetrySettings getFindingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getFindingSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getFindingSettings().setRetrySettings(getFindingRetrySettings);

      RetrySettings listFindingsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listFindingsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listFindingsSettings().setRetrySettings(listFindingsRetrySettings);

      RetrySettings listFindingTypeStatsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listFindingTypeStatsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listFindingTypeStatsSettings()
          .setRetrySettings(listFindingTypeStatsRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry createScanConfigRetry = clientProperties.getCreateScanConfigRetry();
    if (createScanConfigRetry != null) {
      RetrySettings createScanConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createScanConfigSettings().getRetrySettings(),
              createScanConfigRetry);
      clientSettingsBuilder
          .createScanConfigSettings()
          .setRetrySettings(createScanConfigRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createScanConfig from properties.");
      }
    }
    Retry deleteScanConfigRetry = clientProperties.getDeleteScanConfigRetry();
    if (deleteScanConfigRetry != null) {
      RetrySettings deleteScanConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteScanConfigSettings().getRetrySettings(),
              deleteScanConfigRetry);
      clientSettingsBuilder
          .deleteScanConfigSettings()
          .setRetrySettings(deleteScanConfigRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteScanConfig from properties.");
      }
    }
    Retry getScanConfigRetry = clientProperties.getGetScanConfigRetry();
    if (getScanConfigRetry != null) {
      RetrySettings getScanConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getScanConfigSettings().getRetrySettings(), getScanConfigRetry);
      clientSettingsBuilder.getScanConfigSettings().setRetrySettings(getScanConfigRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getScanConfig from properties.");
      }
    }
    Retry listScanConfigsRetry = clientProperties.getListScanConfigsRetry();
    if (listScanConfigsRetry != null) {
      RetrySettings listScanConfigsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listScanConfigsSettings().getRetrySettings(),
              listScanConfigsRetry);
      clientSettingsBuilder
          .listScanConfigsSettings()
          .setRetrySettings(listScanConfigsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listScanConfigs from properties.");
      }
    }
    Retry updateScanConfigRetry = clientProperties.getUpdateScanConfigRetry();
    if (updateScanConfigRetry != null) {
      RetrySettings updateScanConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateScanConfigSettings().getRetrySettings(),
              updateScanConfigRetry);
      clientSettingsBuilder
          .updateScanConfigSettings()
          .setRetrySettings(updateScanConfigRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateScanConfig from properties.");
      }
    }
    Retry startScanRunRetry = clientProperties.getStartScanRunRetry();
    if (startScanRunRetry != null) {
      RetrySettings startScanRunRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.startScanRunSettings().getRetrySettings(), startScanRunRetry);
      clientSettingsBuilder.startScanRunSettings().setRetrySettings(startScanRunRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for startScanRun from properties.");
      }
    }
    Retry getScanRunRetry = clientProperties.getGetScanRunRetry();
    if (getScanRunRetry != null) {
      RetrySettings getScanRunRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getScanRunSettings().getRetrySettings(), getScanRunRetry);
      clientSettingsBuilder.getScanRunSettings().setRetrySettings(getScanRunRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getScanRun from properties.");
      }
    }
    Retry listScanRunsRetry = clientProperties.getListScanRunsRetry();
    if (listScanRunsRetry != null) {
      RetrySettings listScanRunsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listScanRunsSettings().getRetrySettings(), listScanRunsRetry);
      clientSettingsBuilder.listScanRunsSettings().setRetrySettings(listScanRunsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listScanRuns from properties.");
      }
    }
    Retry stopScanRunRetry = clientProperties.getStopScanRunRetry();
    if (stopScanRunRetry != null) {
      RetrySettings stopScanRunRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.stopScanRunSettings().getRetrySettings(), stopScanRunRetry);
      clientSettingsBuilder.stopScanRunSettings().setRetrySettings(stopScanRunRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for stopScanRun from properties.");
      }
    }
    Retry listCrawledUrlsRetry = clientProperties.getListCrawledUrlsRetry();
    if (listCrawledUrlsRetry != null) {
      RetrySettings listCrawledUrlsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listCrawledUrlsSettings().getRetrySettings(),
              listCrawledUrlsRetry);
      clientSettingsBuilder
          .listCrawledUrlsSettings()
          .setRetrySettings(listCrawledUrlsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listCrawledUrls from properties.");
      }
    }
    Retry getFindingRetry = clientProperties.getGetFindingRetry();
    if (getFindingRetry != null) {
      RetrySettings getFindingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getFindingSettings().getRetrySettings(), getFindingRetry);
      clientSettingsBuilder.getFindingSettings().setRetrySettings(getFindingRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getFinding from properties.");
      }
    }
    Retry listFindingsRetry = clientProperties.getListFindingsRetry();
    if (listFindingsRetry != null) {
      RetrySettings listFindingsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listFindingsSettings().getRetrySettings(), listFindingsRetry);
      clientSettingsBuilder.listFindingsSettings().setRetrySettings(listFindingsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listFindings from properties.");
      }
    }
    Retry listFindingTypeStatsRetry = clientProperties.getListFindingTypeStatsRetry();
    if (listFindingTypeStatsRetry != null) {
      RetrySettings listFindingTypeStatsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listFindingTypeStatsSettings().getRetrySettings(),
              listFindingTypeStatsRetry);
      clientSettingsBuilder
          .listFindingTypeStatsSettings()
          .setRetrySettings(listFindingTypeStatsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listFindingTypeStats from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a WebSecurityScannerClient bean configured with WebSecurityScannerSettings.
   *
   * @param webSecurityScannerSettings settings to configure an instance of client bean.
   * @return a {@link WebSecurityScannerClient} bean configured with {@link
   *     WebSecurityScannerSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public WebSecurityScannerClient webSecurityScannerClient(
      WebSecurityScannerSettings webSecurityScannerSettings) throws IOException {
    return WebSecurityScannerClient.create(webSecurityScannerSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-web-security-scanner";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
