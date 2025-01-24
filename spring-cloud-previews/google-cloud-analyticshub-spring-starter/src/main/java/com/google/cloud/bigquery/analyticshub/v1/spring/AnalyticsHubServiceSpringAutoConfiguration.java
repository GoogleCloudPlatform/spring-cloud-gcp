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

package com.google.cloud.bigquery.analyticshub.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.bigquery.analyticshub.v1.AnalyticsHubServiceClient;
import com.google.cloud.bigquery.analyticshub.v1.AnalyticsHubServiceSettings;
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
 * Auto-configuration for {@link AnalyticsHubServiceClient}.
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
@ConditionalOnClass(AnalyticsHubServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.bigquery.analyticshub.v1.analytics-hub-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(AnalyticsHubServiceSpringProperties.class)
public class AnalyticsHubServiceSpringAutoConfiguration {
  private final AnalyticsHubServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(AnalyticsHubServiceSpringAutoConfiguration.class);

  protected AnalyticsHubServiceSpringAutoConfiguration(
      AnalyticsHubServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from AnalyticsHubService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultAnalyticsHubServiceTransportChannelProvider")
  public TransportChannelProvider defaultAnalyticsHubServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return AnalyticsHubServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return AnalyticsHubServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a AnalyticsHubServiceSettings bean configured to use a DefaultCredentialsProvider and
   * the client library's default transport channel provider
   * (defaultAnalyticsHubServiceTransportChannelProvider()). It also configures the quota project ID
   * and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in AnalyticsHubServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link AnalyticsHubServiceSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public AnalyticsHubServiceSettings analyticsHubServiceSettings(
      @Qualifier("defaultAnalyticsHubServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    AnalyticsHubServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = AnalyticsHubServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = AnalyticsHubServiceSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(AnalyticsHubServiceSettings.getDefaultEndpoint())
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
          AnalyticsHubServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listDataExchangesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listDataExchangesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listDataExchangesSettings()
          .setRetrySettings(listDataExchangesRetrySettings);

      RetrySettings listOrgDataExchangesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listOrgDataExchangesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listOrgDataExchangesSettings()
          .setRetrySettings(listOrgDataExchangesRetrySettings);

      RetrySettings getDataExchangeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getDataExchangeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getDataExchangeSettings()
          .setRetrySettings(getDataExchangeRetrySettings);

      RetrySettings createDataExchangeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createDataExchangeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createDataExchangeSettings()
          .setRetrySettings(createDataExchangeRetrySettings);

      RetrySettings updateDataExchangeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateDataExchangeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateDataExchangeSettings()
          .setRetrySettings(updateDataExchangeRetrySettings);

      RetrySettings deleteDataExchangeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteDataExchangeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .deleteDataExchangeSettings()
          .setRetrySettings(deleteDataExchangeRetrySettings);

      RetrySettings listListingsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listListingsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listListingsSettings().setRetrySettings(listListingsRetrySettings);

      RetrySettings getListingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getListingSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getListingSettings().setRetrySettings(getListingRetrySettings);

      RetrySettings createListingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createListingSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createListingSettings().setRetrySettings(createListingRetrySettings);

      RetrySettings updateListingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateListingSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateListingSettings().setRetrySettings(updateListingRetrySettings);

      RetrySettings deleteListingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteListingSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteListingSettings().setRetrySettings(deleteListingRetrySettings);

      RetrySettings subscribeListingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.subscribeListingSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .subscribeListingSettings()
          .setRetrySettings(subscribeListingRetrySettings);

      RetrySettings getSubscriptionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSubscriptionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getSubscriptionSettings()
          .setRetrySettings(getSubscriptionRetrySettings);

      RetrySettings listSubscriptionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSubscriptionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listSubscriptionsSettings()
          .setRetrySettings(listSubscriptionsRetrySettings);

      RetrySettings listSharedResourceSubscriptionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSharedResourceSubscriptionsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listSharedResourceSubscriptionsSettings()
          .setRetrySettings(listSharedResourceSubscriptionsRetrySettings);

      RetrySettings revokeSubscriptionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.revokeSubscriptionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .revokeSubscriptionSettings()
          .setRetrySettings(revokeSubscriptionRetrySettings);

      RetrySettings getIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIamPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getIamPolicySettings().setRetrySettings(getIamPolicyRetrySettings);

      RetrySettings setIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setIamPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.setIamPolicySettings().setRetrySettings(setIamPolicyRetrySettings);

      RetrySettings testIamPermissionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.testIamPermissionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .testIamPermissionsSettings()
          .setRetrySettings(testIamPermissionsRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listDataExchangesRetry = clientProperties.getListDataExchangesRetry();
    if (listDataExchangesRetry != null) {
      RetrySettings listDataExchangesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listDataExchangesSettings().getRetrySettings(),
              listDataExchangesRetry);
      clientSettingsBuilder
          .listDataExchangesSettings()
          .setRetrySettings(listDataExchangesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listDataExchanges from properties.");
      }
    }
    Retry listOrgDataExchangesRetry = clientProperties.getListOrgDataExchangesRetry();
    if (listOrgDataExchangesRetry != null) {
      RetrySettings listOrgDataExchangesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listOrgDataExchangesSettings().getRetrySettings(),
              listOrgDataExchangesRetry);
      clientSettingsBuilder
          .listOrgDataExchangesSettings()
          .setRetrySettings(listOrgDataExchangesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listOrgDataExchanges from properties.");
      }
    }
    Retry getDataExchangeRetry = clientProperties.getGetDataExchangeRetry();
    if (getDataExchangeRetry != null) {
      RetrySettings getDataExchangeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getDataExchangeSettings().getRetrySettings(),
              getDataExchangeRetry);
      clientSettingsBuilder
          .getDataExchangeSettings()
          .setRetrySettings(getDataExchangeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getDataExchange from properties.");
      }
    }
    Retry createDataExchangeRetry = clientProperties.getCreateDataExchangeRetry();
    if (createDataExchangeRetry != null) {
      RetrySettings createDataExchangeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createDataExchangeSettings().getRetrySettings(),
              createDataExchangeRetry);
      clientSettingsBuilder
          .createDataExchangeSettings()
          .setRetrySettings(createDataExchangeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createDataExchange from properties.");
      }
    }
    Retry updateDataExchangeRetry = clientProperties.getUpdateDataExchangeRetry();
    if (updateDataExchangeRetry != null) {
      RetrySettings updateDataExchangeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateDataExchangeSettings().getRetrySettings(),
              updateDataExchangeRetry);
      clientSettingsBuilder
          .updateDataExchangeSettings()
          .setRetrySettings(updateDataExchangeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateDataExchange from properties.");
      }
    }
    Retry deleteDataExchangeRetry = clientProperties.getDeleteDataExchangeRetry();
    if (deleteDataExchangeRetry != null) {
      RetrySettings deleteDataExchangeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteDataExchangeSettings().getRetrySettings(),
              deleteDataExchangeRetry);
      clientSettingsBuilder
          .deleteDataExchangeSettings()
          .setRetrySettings(deleteDataExchangeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteDataExchange from properties.");
      }
    }
    Retry listListingsRetry = clientProperties.getListListingsRetry();
    if (listListingsRetry != null) {
      RetrySettings listListingsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listListingsSettings().getRetrySettings(), listListingsRetry);
      clientSettingsBuilder.listListingsSettings().setRetrySettings(listListingsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listListings from properties.");
      }
    }
    Retry getListingRetry = clientProperties.getGetListingRetry();
    if (getListingRetry != null) {
      RetrySettings getListingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getListingSettings().getRetrySettings(), getListingRetry);
      clientSettingsBuilder.getListingSettings().setRetrySettings(getListingRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getListing from properties.");
      }
    }
    Retry createListingRetry = clientProperties.getCreateListingRetry();
    if (createListingRetry != null) {
      RetrySettings createListingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createListingSettings().getRetrySettings(), createListingRetry);
      clientSettingsBuilder.createListingSettings().setRetrySettings(createListingRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createListing from properties.");
      }
    }
    Retry updateListingRetry = clientProperties.getUpdateListingRetry();
    if (updateListingRetry != null) {
      RetrySettings updateListingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateListingSettings().getRetrySettings(), updateListingRetry);
      clientSettingsBuilder.updateListingSettings().setRetrySettings(updateListingRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateListing from properties.");
      }
    }
    Retry deleteListingRetry = clientProperties.getDeleteListingRetry();
    if (deleteListingRetry != null) {
      RetrySettings deleteListingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteListingSettings().getRetrySettings(), deleteListingRetry);
      clientSettingsBuilder.deleteListingSettings().setRetrySettings(deleteListingRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteListing from properties.");
      }
    }
    Retry subscribeListingRetry = clientProperties.getSubscribeListingRetry();
    if (subscribeListingRetry != null) {
      RetrySettings subscribeListingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.subscribeListingSettings().getRetrySettings(),
              subscribeListingRetry);
      clientSettingsBuilder
          .subscribeListingSettings()
          .setRetrySettings(subscribeListingRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for subscribeListing from properties.");
      }
    }
    Retry getSubscriptionRetry = clientProperties.getGetSubscriptionRetry();
    if (getSubscriptionRetry != null) {
      RetrySettings getSubscriptionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSubscriptionSettings().getRetrySettings(),
              getSubscriptionRetry);
      clientSettingsBuilder
          .getSubscriptionSettings()
          .setRetrySettings(getSubscriptionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getSubscription from properties.");
      }
    }
    Retry listSubscriptionsRetry = clientProperties.getListSubscriptionsRetry();
    if (listSubscriptionsRetry != null) {
      RetrySettings listSubscriptionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSubscriptionsSettings().getRetrySettings(),
              listSubscriptionsRetry);
      clientSettingsBuilder
          .listSubscriptionsSettings()
          .setRetrySettings(listSubscriptionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listSubscriptions from properties.");
      }
    }
    Retry listSharedResourceSubscriptionsRetry =
        clientProperties.getListSharedResourceSubscriptionsRetry();
    if (listSharedResourceSubscriptionsRetry != null) {
      RetrySettings listSharedResourceSubscriptionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSharedResourceSubscriptionsSettings().getRetrySettings(),
              listSharedResourceSubscriptionsRetry);
      clientSettingsBuilder
          .listSharedResourceSubscriptionsSettings()
          .setRetrySettings(listSharedResourceSubscriptionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listSharedResourceSubscriptions from properties.");
      }
    }
    Retry revokeSubscriptionRetry = clientProperties.getRevokeSubscriptionRetry();
    if (revokeSubscriptionRetry != null) {
      RetrySettings revokeSubscriptionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.revokeSubscriptionSettings().getRetrySettings(),
              revokeSubscriptionRetry);
      clientSettingsBuilder
          .revokeSubscriptionSettings()
          .setRetrySettings(revokeSubscriptionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for revokeSubscription from properties.");
      }
    }
    Retry getIamPolicyRetry = clientProperties.getGetIamPolicyRetry();
    if (getIamPolicyRetry != null) {
      RetrySettings getIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIamPolicySettings().getRetrySettings(), getIamPolicyRetry);
      clientSettingsBuilder.getIamPolicySettings().setRetrySettings(getIamPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getIamPolicy from properties.");
      }
    }
    Retry setIamPolicyRetry = clientProperties.getSetIamPolicyRetry();
    if (setIamPolicyRetry != null) {
      RetrySettings setIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setIamPolicySettings().getRetrySettings(), setIamPolicyRetry);
      clientSettingsBuilder.setIamPolicySettings().setRetrySettings(setIamPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for setIamPolicy from properties.");
      }
    }
    Retry testIamPermissionsRetry = clientProperties.getTestIamPermissionsRetry();
    if (testIamPermissionsRetry != null) {
      RetrySettings testIamPermissionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.testIamPermissionsSettings().getRetrySettings(),
              testIamPermissionsRetry);
      clientSettingsBuilder
          .testIamPermissionsSettings()
          .setRetrySettings(testIamPermissionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for testIamPermissions from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a AnalyticsHubServiceClient bean configured with AnalyticsHubServiceSettings.
   *
   * @param analyticsHubServiceSettings settings to configure an instance of client bean.
   * @return a {@link AnalyticsHubServiceClient} bean configured with {@link
   *     AnalyticsHubServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public AnalyticsHubServiceClient analyticsHubServiceClient(
      AnalyticsHubServiceSettings analyticsHubServiceSettings) throws IOException {
    return AnalyticsHubServiceClient.create(analyticsHubServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-analytics-hub-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
