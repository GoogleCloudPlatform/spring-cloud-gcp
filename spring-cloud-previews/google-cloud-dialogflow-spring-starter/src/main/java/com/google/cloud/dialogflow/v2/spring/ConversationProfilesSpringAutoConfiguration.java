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

package com.google.cloud.dialogflow.v2.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.dialogflow.v2.ConversationProfilesClient;
import com.google.cloud.dialogflow.v2.ConversationProfilesSettings;
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
 * Auto-configuration for {@link ConversationProfilesClient}.
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
@ConditionalOnClass(ConversationProfilesClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.dialogflow.v2.conversation-profiles.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ConversationProfilesSpringProperties.class)
public class ConversationProfilesSpringAutoConfiguration {
  private final ConversationProfilesSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(ConversationProfilesSpringAutoConfiguration.class);

  protected ConversationProfilesSpringAutoConfiguration(
      ConversationProfilesSpringProperties clientProperties,
      CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from ConversationProfiles-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultConversationProfilesTransportChannelProvider")
  public TransportChannelProvider defaultConversationProfilesTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return ConversationProfilesSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return ConversationProfilesSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ConversationProfilesSettings bean configured to use a DefaultCredentialsProvider and
   * the client library's default transport channel provider
   * (defaultConversationProfilesTransportChannelProvider()). It also configures the quota project
   * ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ConversationProfilesSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ConversationProfilesSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ConversationProfilesSettings conversationProfilesSettings(
      @Qualifier("defaultConversationProfilesTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ConversationProfilesSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = ConversationProfilesSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = ConversationProfilesSettings.newBuilder();
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
          ConversationProfilesSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listConversationProfilesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConversationProfilesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listConversationProfilesSettings()
          .setRetrySettings(listConversationProfilesRetrySettings);

      RetrySettings getConversationProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConversationProfileSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getConversationProfileSettings()
          .setRetrySettings(getConversationProfileRetrySettings);

      RetrySettings createConversationProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createConversationProfileSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .createConversationProfileSettings()
          .setRetrySettings(createConversationProfileRetrySettings);

      RetrySettings updateConversationProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateConversationProfileSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .updateConversationProfileSettings()
          .setRetrySettings(updateConversationProfileRetrySettings);

      RetrySettings deleteConversationProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteConversationProfileSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .deleteConversationProfileSettings()
          .setRetrySettings(deleteConversationProfileRetrySettings);

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
    Retry listConversationProfilesRetry = clientProperties.getListConversationProfilesRetry();
    if (listConversationProfilesRetry != null) {
      RetrySettings listConversationProfilesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConversationProfilesSettings().getRetrySettings(),
              listConversationProfilesRetry);
      clientSettingsBuilder
          .listConversationProfilesSettings()
          .setRetrySettings(listConversationProfilesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listConversationProfiles from properties.");
      }
    }
    Retry getConversationProfileRetry = clientProperties.getGetConversationProfileRetry();
    if (getConversationProfileRetry != null) {
      RetrySettings getConversationProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConversationProfileSettings().getRetrySettings(),
              getConversationProfileRetry);
      clientSettingsBuilder
          .getConversationProfileSettings()
          .setRetrySettings(getConversationProfileRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getConversationProfile from properties.");
      }
    }
    Retry createConversationProfileRetry = clientProperties.getCreateConversationProfileRetry();
    if (createConversationProfileRetry != null) {
      RetrySettings createConversationProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createConversationProfileSettings().getRetrySettings(),
              createConversationProfileRetry);
      clientSettingsBuilder
          .createConversationProfileSettings()
          .setRetrySettings(createConversationProfileRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createConversationProfile from properties.");
      }
    }
    Retry updateConversationProfileRetry = clientProperties.getUpdateConversationProfileRetry();
    if (updateConversationProfileRetry != null) {
      RetrySettings updateConversationProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateConversationProfileSettings().getRetrySettings(),
              updateConversationProfileRetry);
      clientSettingsBuilder
          .updateConversationProfileSettings()
          .setRetrySettings(updateConversationProfileRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateConversationProfile from properties.");
      }
    }
    Retry deleteConversationProfileRetry = clientProperties.getDeleteConversationProfileRetry();
    if (deleteConversationProfileRetry != null) {
      RetrySettings deleteConversationProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteConversationProfileSettings().getRetrySettings(),
              deleteConversationProfileRetry);
      clientSettingsBuilder
          .deleteConversationProfileSettings()
          .setRetrySettings(deleteConversationProfileRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteConversationProfile from properties.");
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
   * Provides a ConversationProfilesClient bean configured with ConversationProfilesSettings.
   *
   * @param conversationProfilesSettings settings to configure an instance of client bean.
   * @return a {@link ConversationProfilesClient} bean configured with {@link
   *     ConversationProfilesSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ConversationProfilesClient conversationProfilesClient(
      ConversationProfilesSettings conversationProfilesSettings) throws IOException {
    return ConversationProfilesClient.create(conversationProfilesSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-conversation-profiles";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
