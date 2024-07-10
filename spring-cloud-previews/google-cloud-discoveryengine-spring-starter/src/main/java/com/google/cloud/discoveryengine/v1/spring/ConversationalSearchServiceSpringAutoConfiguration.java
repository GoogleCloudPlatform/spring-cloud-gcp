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

package com.google.cloud.discoveryengine.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.discoveryengine.v1.ConversationalSearchServiceClient;
import com.google.cloud.discoveryengine.v1.ConversationalSearchServiceSettings;
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
 * Auto-configuration for {@link ConversationalSearchServiceClient}.
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
@ConditionalOnClass(ConversationalSearchServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.discoveryengine.v1.conversational-search-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ConversationalSearchServiceSpringProperties.class)
public class ConversationalSearchServiceSpringAutoConfiguration {
  private final ConversationalSearchServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(ConversationalSearchServiceSpringAutoConfiguration.class);

  protected ConversationalSearchServiceSpringAutoConfiguration(
      ConversationalSearchServiceSpringProperties clientProperties,
      CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from ConversationalSearchService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultConversationalSearchServiceTransportChannelProvider")
  public TransportChannelProvider defaultConversationalSearchServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return ConversationalSearchServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return ConversationalSearchServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ConversationalSearchServiceSettings bean configured to use a
   * DefaultCredentialsProvider and the client library's default transport channel provider
   * (defaultConversationalSearchServiceTransportChannelProvider()). It also configures the quota
   * project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ConversationalSearchServiceSpringProperties. Method-level properties will take precedence
   * over service-level properties if available, and client library defaults will be used if neither
   * are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ConversationalSearchServiceSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ConversationalSearchServiceSettings conversationalSearchServiceSettings(
      @Qualifier("defaultConversationalSearchServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ConversationalSearchServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = ConversationalSearchServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = ConversationalSearchServiceSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(ConversationalSearchServiceSettings.getDefaultEndpoint())
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
          ConversationalSearchServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings converseConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.converseConversationSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .converseConversationSettings()
          .setRetrySettings(converseConversationRetrySettings);

      RetrySettings createConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createConversationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createConversationSettings()
          .setRetrySettings(createConversationRetrySettings);

      RetrySettings deleteConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteConversationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .deleteConversationSettings()
          .setRetrySettings(deleteConversationRetrySettings);

      RetrySettings updateConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateConversationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateConversationSettings()
          .setRetrySettings(updateConversationRetrySettings);

      RetrySettings getConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConversationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getConversationSettings()
          .setRetrySettings(getConversationRetrySettings);

      RetrySettings listConversationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConversationsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listConversationsSettings()
          .setRetrySettings(listConversationsRetrySettings);

      RetrySettings answerQueryRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.answerQuerySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.answerQuerySettings().setRetrySettings(answerQueryRetrySettings);

      RetrySettings getAnswerRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getAnswerSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getAnswerSettings().setRetrySettings(getAnswerRetrySettings);

      RetrySettings createSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createSessionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createSessionSettings().setRetrySettings(createSessionRetrySettings);

      RetrySettings deleteSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteSessionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteSessionSettings().setRetrySettings(deleteSessionRetrySettings);

      RetrySettings updateSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateSessionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateSessionSettings().setRetrySettings(updateSessionRetrySettings);

      RetrySettings getSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSessionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getSessionSettings().setRetrySettings(getSessionRetrySettings);

      RetrySettings listSessionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSessionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listSessionsSettings().setRetrySettings(listSessionsRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry converseConversationRetry = clientProperties.getConverseConversationRetry();
    if (converseConversationRetry != null) {
      RetrySettings converseConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.converseConversationSettings().getRetrySettings(),
              converseConversationRetry);
      clientSettingsBuilder
          .converseConversationSettings()
          .setRetrySettings(converseConversationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for converseConversation from properties.");
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
    Retry deleteConversationRetry = clientProperties.getDeleteConversationRetry();
    if (deleteConversationRetry != null) {
      RetrySettings deleteConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteConversationSettings().getRetrySettings(),
              deleteConversationRetry);
      clientSettingsBuilder
          .deleteConversationSettings()
          .setRetrySettings(deleteConversationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteConversation from properties.");
      }
    }
    Retry updateConversationRetry = clientProperties.getUpdateConversationRetry();
    if (updateConversationRetry != null) {
      RetrySettings updateConversationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateConversationSettings().getRetrySettings(),
              updateConversationRetry);
      clientSettingsBuilder
          .updateConversationSettings()
          .setRetrySettings(updateConversationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateConversation from properties.");
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
    Retry answerQueryRetry = clientProperties.getAnswerQueryRetry();
    if (answerQueryRetry != null) {
      RetrySettings answerQueryRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.answerQuerySettings().getRetrySettings(), answerQueryRetry);
      clientSettingsBuilder.answerQuerySettings().setRetrySettings(answerQueryRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for answerQuery from properties.");
      }
    }
    Retry getAnswerRetry = clientProperties.getGetAnswerRetry();
    if (getAnswerRetry != null) {
      RetrySettings getAnswerRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getAnswerSettings().getRetrySettings(), getAnswerRetry);
      clientSettingsBuilder.getAnswerSettings().setRetrySettings(getAnswerRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getAnswer from properties.");
      }
    }
    Retry createSessionRetry = clientProperties.getCreateSessionRetry();
    if (createSessionRetry != null) {
      RetrySettings createSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createSessionSettings().getRetrySettings(), createSessionRetry);
      clientSettingsBuilder.createSessionSettings().setRetrySettings(createSessionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createSession from properties.");
      }
    }
    Retry deleteSessionRetry = clientProperties.getDeleteSessionRetry();
    if (deleteSessionRetry != null) {
      RetrySettings deleteSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteSessionSettings().getRetrySettings(), deleteSessionRetry);
      clientSettingsBuilder.deleteSessionSettings().setRetrySettings(deleteSessionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteSession from properties.");
      }
    }
    Retry updateSessionRetry = clientProperties.getUpdateSessionRetry();
    if (updateSessionRetry != null) {
      RetrySettings updateSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateSessionSettings().getRetrySettings(), updateSessionRetry);
      clientSettingsBuilder.updateSessionSettings().setRetrySettings(updateSessionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateSession from properties.");
      }
    }
    Retry getSessionRetry = clientProperties.getGetSessionRetry();
    if (getSessionRetry != null) {
      RetrySettings getSessionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSessionSettings().getRetrySettings(), getSessionRetry);
      clientSettingsBuilder.getSessionSettings().setRetrySettings(getSessionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getSession from properties.");
      }
    }
    Retry listSessionsRetry = clientProperties.getListSessionsRetry();
    if (listSessionsRetry != null) {
      RetrySettings listSessionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSessionsSettings().getRetrySettings(), listSessionsRetry);
      clientSettingsBuilder.listSessionsSettings().setRetrySettings(listSessionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listSessions from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a ConversationalSearchServiceClient bean configured with
   * ConversationalSearchServiceSettings.
   *
   * @param conversationalSearchServiceSettings settings to configure an instance of client bean.
   * @return a {@link ConversationalSearchServiceClient} bean configured with {@link
   *     ConversationalSearchServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ConversationalSearchServiceClient conversationalSearchServiceClient(
      ConversationalSearchServiceSettings conversationalSearchServiceSettings) throws IOException {
    return ConversationalSearchServiceClient.create(conversationalSearchServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-conversational-search-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
