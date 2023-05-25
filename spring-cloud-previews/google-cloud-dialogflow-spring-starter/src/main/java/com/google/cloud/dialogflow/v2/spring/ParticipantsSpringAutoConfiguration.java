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

package com.google.cloud.dialogflow.v2.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.dialogflow.v2.ParticipantsClient;
import com.google.cloud.dialogflow.v2.ParticipantsSettings;
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
 * Auto-configuration for {@link ParticipantsClient}.
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
@ConditionalOnClass(ParticipantsClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.dialogflow.v2.participants.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ParticipantsSpringProperties.class)
public class ParticipantsSpringAutoConfiguration {
  private final ParticipantsSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(ParticipantsSpringAutoConfiguration.class);

  protected ParticipantsSpringAutoConfiguration(
      ParticipantsSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Participants-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultParticipantsTransportChannelProvider")
  public TransportChannelProvider defaultParticipantsTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return ParticipantsSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return ParticipantsSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ParticipantsSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultParticipantsTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ParticipantsSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ParticipantsSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ParticipantsSettings participantsSettings(
      @Qualifier("defaultParticipantsTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ParticipantsSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = ParticipantsSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = ParticipantsSettings.newBuilder();
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
          ParticipantsSettings.defaultExecutorProviderBuilder()
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
      RetrySettings createParticipantRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createParticipantSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createParticipantSettings()
          .setRetrySettings(createParticipantRetrySettings);

      RetrySettings getParticipantRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getParticipantSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getParticipantSettings().setRetrySettings(getParticipantRetrySettings);

      RetrySettings listParticipantsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listParticipantsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listParticipantsSettings()
          .setRetrySettings(listParticipantsRetrySettings);

      RetrySettings updateParticipantRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateParticipantSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateParticipantSettings()
          .setRetrySettings(updateParticipantRetrySettings);

      RetrySettings analyzeContentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.analyzeContentSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.analyzeContentSettings().setRetrySettings(analyzeContentRetrySettings);

      RetrySettings suggestArticlesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.suggestArticlesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .suggestArticlesSettings()
          .setRetrySettings(suggestArticlesRetrySettings);

      RetrySettings suggestFaqAnswersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.suggestFaqAnswersSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .suggestFaqAnswersSettings()
          .setRetrySettings(suggestFaqAnswersRetrySettings);

      RetrySettings suggestSmartRepliesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.suggestSmartRepliesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .suggestSmartRepliesSettings()
          .setRetrySettings(suggestSmartRepliesRetrySettings);

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
    Retry createParticipantRetry = clientProperties.getCreateParticipantRetry();
    if (createParticipantRetry != null) {
      RetrySettings createParticipantRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createParticipantSettings().getRetrySettings(),
              createParticipantRetry);
      clientSettingsBuilder
          .createParticipantSettings()
          .setRetrySettings(createParticipantRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createParticipant from properties.");
      }
    }
    Retry getParticipantRetry = clientProperties.getGetParticipantRetry();
    if (getParticipantRetry != null) {
      RetrySettings getParticipantRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getParticipantSettings().getRetrySettings(),
              getParticipantRetry);
      clientSettingsBuilder.getParticipantSettings().setRetrySettings(getParticipantRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getParticipant from properties.");
      }
    }
    Retry listParticipantsRetry = clientProperties.getListParticipantsRetry();
    if (listParticipantsRetry != null) {
      RetrySettings listParticipantsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listParticipantsSettings().getRetrySettings(),
              listParticipantsRetry);
      clientSettingsBuilder
          .listParticipantsSettings()
          .setRetrySettings(listParticipantsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listParticipants from properties.");
      }
    }
    Retry updateParticipantRetry = clientProperties.getUpdateParticipantRetry();
    if (updateParticipantRetry != null) {
      RetrySettings updateParticipantRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateParticipantSettings().getRetrySettings(),
              updateParticipantRetry);
      clientSettingsBuilder
          .updateParticipantSettings()
          .setRetrySettings(updateParticipantRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateParticipant from properties.");
      }
    }
    Retry analyzeContentRetry = clientProperties.getAnalyzeContentRetry();
    if (analyzeContentRetry != null) {
      RetrySettings analyzeContentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.analyzeContentSettings().getRetrySettings(),
              analyzeContentRetry);
      clientSettingsBuilder.analyzeContentSettings().setRetrySettings(analyzeContentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for analyzeContent from properties.");
      }
    }
    Retry suggestArticlesRetry = clientProperties.getSuggestArticlesRetry();
    if (suggestArticlesRetry != null) {
      RetrySettings suggestArticlesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.suggestArticlesSettings().getRetrySettings(),
              suggestArticlesRetry);
      clientSettingsBuilder
          .suggestArticlesSettings()
          .setRetrySettings(suggestArticlesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for suggestArticles from properties.");
      }
    }
    Retry suggestFaqAnswersRetry = clientProperties.getSuggestFaqAnswersRetry();
    if (suggestFaqAnswersRetry != null) {
      RetrySettings suggestFaqAnswersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.suggestFaqAnswersSettings().getRetrySettings(),
              suggestFaqAnswersRetry);
      clientSettingsBuilder
          .suggestFaqAnswersSettings()
          .setRetrySettings(suggestFaqAnswersRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for suggestFaqAnswers from properties.");
      }
    }
    Retry suggestSmartRepliesRetry = clientProperties.getSuggestSmartRepliesRetry();
    if (suggestSmartRepliesRetry != null) {
      RetrySettings suggestSmartRepliesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.suggestSmartRepliesSettings().getRetrySettings(),
              suggestSmartRepliesRetry);
      clientSettingsBuilder
          .suggestSmartRepliesSettings()
          .setRetrySettings(suggestSmartRepliesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for suggestSmartReplies from properties.");
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
   * Provides a ParticipantsClient bean configured with ParticipantsSettings.
   *
   * @param participantsSettings settings to configure an instance of client bean.
   * @return a {@link ParticipantsClient} bean configured with {@link ParticipantsSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ParticipantsClient participantsClient(ParticipantsSettings participantsSettings)
      throws IOException {
    return ParticipantsClient.create(participantsSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-participants";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
