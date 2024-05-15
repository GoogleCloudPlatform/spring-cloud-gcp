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

package com.google.cloud.speech.v2.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.speech.v2.SpeechClient;
import com.google.cloud.speech.v2.SpeechSettings;
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
 * Auto-configuration for {@link SpeechClient}.
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
@ConditionalOnClass(SpeechClient.class)
@ConditionalOnProperty(value = "com.google.cloud.speech.v2.speech.enabled", matchIfMissing = true)
@EnableConfigurationProperties(SpeechSpringProperties.class)
public class SpeechSpringAutoConfiguration {
  private final SpeechSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(SpeechSpringAutoConfiguration.class);

  protected SpeechSpringAutoConfiguration(
      SpeechSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Speech-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultSpeechTransportChannelProvider")
  public TransportChannelProvider defaultSpeechTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return SpeechSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return SpeechSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a SpeechSettings bean configured to use a DefaultCredentialsProvider and the client
   * library's default transport channel provider (defaultSpeechTransportChannelProvider()). It also
   * configures the quota project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in SpeechSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link SpeechSettings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public SpeechSettings speechSettings(
      @Qualifier("defaultSpeechTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    SpeechSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = SpeechSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = SpeechSettings.newBuilder();
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
          SpeechSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listRecognizersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRecognizersSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listRecognizersSettings()
          .setRetrySettings(listRecognizersRetrySettings);

      RetrySettings getRecognizerRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRecognizerSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getRecognizerSettings().setRetrySettings(getRecognizerRetrySettings);

      RetrySettings recognizeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.recognizeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.recognizeSettings().setRetrySettings(recognizeRetrySettings);

      RetrySettings getConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConfigSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getConfigSettings().setRetrySettings(getConfigRetrySettings);

      RetrySettings updateConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateConfigSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateConfigSettings().setRetrySettings(updateConfigRetrySettings);

      RetrySettings listCustomClassesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listCustomClassesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listCustomClassesSettings()
          .setRetrySettings(listCustomClassesRetrySettings);

      RetrySettings getCustomClassRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getCustomClassSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getCustomClassSettings().setRetrySettings(getCustomClassRetrySettings);

      RetrySettings listPhraseSetsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPhraseSetsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listPhraseSetsSettings().setRetrySettings(listPhraseSetsRetrySettings);

      RetrySettings getPhraseSetRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPhraseSetSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getPhraseSetSettings().setRetrySettings(getPhraseSetRetrySettings);

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
    Retry listRecognizersRetry = clientProperties.getListRecognizersRetry();
    if (listRecognizersRetry != null) {
      RetrySettings listRecognizersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRecognizersSettings().getRetrySettings(),
              listRecognizersRetry);
      clientSettingsBuilder
          .listRecognizersSettings()
          .setRetrySettings(listRecognizersRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listRecognizers from properties.");
      }
    }
    Retry getRecognizerRetry = clientProperties.getGetRecognizerRetry();
    if (getRecognizerRetry != null) {
      RetrySettings getRecognizerRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRecognizerSettings().getRetrySettings(), getRecognizerRetry);
      clientSettingsBuilder.getRecognizerSettings().setRetrySettings(getRecognizerRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getRecognizer from properties.");
      }
    }
    Retry recognizeRetry = clientProperties.getRecognizeRetry();
    if (recognizeRetry != null) {
      RetrySettings recognizeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.recognizeSettings().getRetrySettings(), recognizeRetry);
      clientSettingsBuilder.recognizeSettings().setRetrySettings(recognizeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for recognize from properties.");
      }
    }
    Retry getConfigRetry = clientProperties.getGetConfigRetry();
    if (getConfigRetry != null) {
      RetrySettings getConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConfigSettings().getRetrySettings(), getConfigRetry);
      clientSettingsBuilder.getConfigSettings().setRetrySettings(getConfigRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getConfig from properties.");
      }
    }
    Retry updateConfigRetry = clientProperties.getUpdateConfigRetry();
    if (updateConfigRetry != null) {
      RetrySettings updateConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateConfigSettings().getRetrySettings(), updateConfigRetry);
      clientSettingsBuilder.updateConfigSettings().setRetrySettings(updateConfigRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateConfig from properties.");
      }
    }
    Retry listCustomClassesRetry = clientProperties.getListCustomClassesRetry();
    if (listCustomClassesRetry != null) {
      RetrySettings listCustomClassesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listCustomClassesSettings().getRetrySettings(),
              listCustomClassesRetry);
      clientSettingsBuilder
          .listCustomClassesSettings()
          .setRetrySettings(listCustomClassesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listCustomClasses from properties.");
      }
    }
    Retry getCustomClassRetry = clientProperties.getGetCustomClassRetry();
    if (getCustomClassRetry != null) {
      RetrySettings getCustomClassRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getCustomClassSettings().getRetrySettings(),
              getCustomClassRetry);
      clientSettingsBuilder.getCustomClassSettings().setRetrySettings(getCustomClassRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getCustomClass from properties.");
      }
    }
    Retry listPhraseSetsRetry = clientProperties.getListPhraseSetsRetry();
    if (listPhraseSetsRetry != null) {
      RetrySettings listPhraseSetsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPhraseSetsSettings().getRetrySettings(),
              listPhraseSetsRetry);
      clientSettingsBuilder.listPhraseSetsSettings().setRetrySettings(listPhraseSetsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listPhraseSets from properties.");
      }
    }
    Retry getPhraseSetRetry = clientProperties.getGetPhraseSetRetry();
    if (getPhraseSetRetry != null) {
      RetrySettings getPhraseSetRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPhraseSetSettings().getRetrySettings(), getPhraseSetRetry);
      clientSettingsBuilder.getPhraseSetSettings().setRetrySettings(getPhraseSetRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getPhraseSet from properties.");
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
   * Provides a SpeechClient bean configured with SpeechSettings.
   *
   * @param speechSettings settings to configure an instance of client bean.
   * @return a {@link SpeechClient} bean configured with {@link SpeechSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public SpeechClient speechClient(SpeechSettings speechSettings) throws IOException {
    return SpeechClient.create(speechSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-speech";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
