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

package com.google.cloud.dialogflow.v2.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.dialogflow.v2.ConversationsClient;
import com.google.cloud.dialogflow.v2.ConversationsSettings;
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
 * Auto-configuration for {@link ConversationsClient}.
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
@ConditionalOnClass(ConversationsClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.dialogflow.v2.conversations.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ConversationsSpringProperties.class)
public class ConversationsSpringAutoConfiguration {
  private final ConversationsSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(ConversationsSpringAutoConfiguration.class);

  protected ConversationsSpringAutoConfiguration(
      ConversationsSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Conversations-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultConversationsTransportChannelProvider")
  public TransportChannelProvider defaultConversationsTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return ConversationsSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return ConversationsSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ConversationsSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultConversationsTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ConversationsSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ConversationsSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ConversationsSettings conversationsSettings(
      @Qualifier("defaultConversationsTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ConversationsSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = ConversationsSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = ConversationsSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(ConversationsSettings.getDefaultEndpoint())
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
          ConversationsSettings.defaultExecutorProviderBuilder()
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
      RetrySettings createConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createConversationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createConversationSettings()
          .setRetrySettings(createConversationRetrySettings);

      RetrySettings listConversationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConversationsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listConversationsSettings()
          .setRetrySettings(listConversationsRetrySettings);

      RetrySettings getConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConversationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getConversationSettings()
          .setRetrySettings(getConversationRetrySettings);

      RetrySettings completeConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.completeConversationSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .completeConversationSettings()
          .setRetrySettings(completeConversationRetrySettings);

      RetrySettings ingestContextReferencesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.ingestContextReferencesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .ingestContextReferencesSettings()
          .setRetrySettings(ingestContextReferencesRetrySettings);

      RetrySettings listMessagesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listMessagesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listMessagesSettings().setRetrySettings(listMessagesRetrySettings);

      RetrySettings suggestConversationSummaryRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.suggestConversationSummarySettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .suggestConversationSummarySettings()
          .setRetrySettings(suggestConversationSummaryRetrySettings);

      RetrySettings generateStatelessSummaryRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateStatelessSummarySettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .generateStatelessSummarySettings()
          .setRetrySettings(generateStatelessSummaryRetrySettings);

      RetrySettings generateStatelessSuggestionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateStatelessSuggestionSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .generateStatelessSuggestionSettings()
          .setRetrySettings(generateStatelessSuggestionRetrySettings);

      RetrySettings searchKnowledgeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.searchKnowledgeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .searchKnowledgeSettings()
          .setRetrySettings(searchKnowledgeRetrySettings);

      RetrySettings generateSuggestionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateSuggestionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .generateSuggestionsSettings()
          .setRetrySettings(generateSuggestionsRetrySettings);

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
    Retry createConversationRetry = clientProperties.getCreateConversationRetry();
    if (createConversationRetry != null) {
      RetrySettings createConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createConversationSettings().getRetrySettings(),
              createConversationRetry);
      clientSettingsBuilder
          .createConversationSettings()
          .setRetrySettings(createConversationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createConversation from properties.");
      }
    }
    Retry listConversationsRetry = clientProperties.getListConversationsRetry();
    if (listConversationsRetry != null) {
      RetrySettings listConversationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConversationsSettings().getRetrySettings(),
              listConversationsRetry);
      clientSettingsBuilder
          .listConversationsSettings()
          .setRetrySettings(listConversationsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listConversations from properties.");
      }
    }
    Retry getConversationRetry = clientProperties.getGetConversationRetry();
    if (getConversationRetry != null) {
      RetrySettings getConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConversationSettings().getRetrySettings(),
              getConversationRetry);
      clientSettingsBuilder
          .getConversationSettings()
          .setRetrySettings(getConversationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getConversation from properties.");
      }
    }
    Retry completeConversationRetry = clientProperties.getCompleteConversationRetry();
    if (completeConversationRetry != null) {
      RetrySettings completeConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.completeConversationSettings().getRetrySettings(),
              completeConversationRetry);
      clientSettingsBuilder
          .completeConversationSettings()
          .setRetrySettings(completeConversationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for completeConversation from properties.");
      }
    }
    Retry ingestContextReferencesRetry = clientProperties.getIngestContextReferencesRetry();
    if (ingestContextReferencesRetry != null) {
      RetrySettings ingestContextReferencesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.ingestContextReferencesSettings().getRetrySettings(),
              ingestContextReferencesRetry);
      clientSettingsBuilder
          .ingestContextReferencesSettings()
          .setRetrySettings(ingestContextReferencesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for ingestContextReferences from properties.");
      }
    }
    Retry listMessagesRetry = clientProperties.getListMessagesRetry();
    if (listMessagesRetry != null) {
      RetrySettings listMessagesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listMessagesSettings().getRetrySettings(), listMessagesRetry);
      clientSettingsBuilder.listMessagesSettings().setRetrySettings(listMessagesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listMessages from properties.");
      }
    }
    Retry suggestConversationSummaryRetry = clientProperties.getSuggestConversationSummaryRetry();
    if (suggestConversationSummaryRetry != null) {
      RetrySettings suggestConversationSummaryRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.suggestConversationSummarySettings().getRetrySettings(),
              suggestConversationSummaryRetry);
      clientSettingsBuilder
          .suggestConversationSummarySettings()
          .setRetrySettings(suggestConversationSummaryRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for suggestConversationSummary from properties.");
      }
    }
    Retry generateStatelessSummaryRetry = clientProperties.getGenerateStatelessSummaryRetry();
    if (generateStatelessSummaryRetry != null) {
      RetrySettings generateStatelessSummaryRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateStatelessSummarySettings().getRetrySettings(),
              generateStatelessSummaryRetry);
      clientSettingsBuilder
          .generateStatelessSummarySettings()
          .setRetrySettings(generateStatelessSummaryRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for generateStatelessSummary from properties.");
      }
    }
    Retry generateStatelessSuggestionRetry = clientProperties.getGenerateStatelessSuggestionRetry();
    if (generateStatelessSuggestionRetry != null) {
      RetrySettings generateStatelessSuggestionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateStatelessSuggestionSettings().getRetrySettings(),
              generateStatelessSuggestionRetry);
      clientSettingsBuilder
          .generateStatelessSuggestionSettings()
          .setRetrySettings(generateStatelessSuggestionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for generateStatelessSuggestion from properties.");
      }
    }
    Retry searchKnowledgeRetry = clientProperties.getSearchKnowledgeRetry();
    if (searchKnowledgeRetry != null) {
      RetrySettings searchKnowledgeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.searchKnowledgeSettings().getRetrySettings(),
              searchKnowledgeRetry);
      clientSettingsBuilder
          .searchKnowledgeSettings()
          .setRetrySettings(searchKnowledgeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for searchKnowledge from properties.");
      }
    }
    Retry generateSuggestionsRetry = clientProperties.getGenerateSuggestionsRetry();
    if (generateSuggestionsRetry != null) {
      RetrySettings generateSuggestionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateSuggestionsSettings().getRetrySettings(),
              generateSuggestionsRetry);
      clientSettingsBuilder
          .generateSuggestionsSettings()
          .setRetrySettings(generateSuggestionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for generateSuggestions from properties.");
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
   * Provides a ConversationsClient bean configured with ConversationsSettings.
   *
   * @param conversationsSettings settings to configure an instance of client bean.
   * @return a {@link ConversationsClient} bean configured with {@link ConversationsSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ConversationsClient conversationsClient(ConversationsSettings conversationsSettings)
      throws IOException {
    return ConversationsClient.create(conversationsSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-conversations";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
