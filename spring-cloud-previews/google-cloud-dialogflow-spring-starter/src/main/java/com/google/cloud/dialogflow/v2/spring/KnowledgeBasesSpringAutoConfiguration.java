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
import com.google.cloud.dialogflow.v2.KnowledgeBasesClient;
import com.google.cloud.dialogflow.v2.KnowledgeBasesSettings;
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
 * Auto-configuration for {@link KnowledgeBasesClient}.
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
@ConditionalOnClass(KnowledgeBasesClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.dialogflow.v2.knowledge-bases.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(KnowledgeBasesSpringProperties.class)
public class KnowledgeBasesSpringAutoConfiguration {
  private final KnowledgeBasesSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(KnowledgeBasesSpringAutoConfiguration.class);

  protected KnowledgeBasesSpringAutoConfiguration(
      KnowledgeBasesSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from KnowledgeBases-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultKnowledgeBasesTransportChannelProvider")
  public TransportChannelProvider defaultKnowledgeBasesTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return KnowledgeBasesSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return KnowledgeBasesSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a KnowledgeBasesSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultKnowledgeBasesTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in KnowledgeBasesSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link KnowledgeBasesSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public KnowledgeBasesSettings knowledgeBasesSettings(
      @Qualifier("defaultKnowledgeBasesTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    KnowledgeBasesSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = KnowledgeBasesSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = KnowledgeBasesSettings.newBuilder();
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
          KnowledgeBasesSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listKnowledgeBasesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listKnowledgeBasesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listKnowledgeBasesSettings()
          .setRetrySettings(listKnowledgeBasesRetrySettings);

      RetrySettings getKnowledgeBaseRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getKnowledgeBaseSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getKnowledgeBaseSettings()
          .setRetrySettings(getKnowledgeBaseRetrySettings);

      RetrySettings createKnowledgeBaseRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createKnowledgeBaseSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createKnowledgeBaseSettings()
          .setRetrySettings(createKnowledgeBaseRetrySettings);

      RetrySettings deleteKnowledgeBaseRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteKnowledgeBaseSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .deleteKnowledgeBaseSettings()
          .setRetrySettings(deleteKnowledgeBaseRetrySettings);

      RetrySettings updateKnowledgeBaseRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateKnowledgeBaseSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateKnowledgeBaseSettings()
          .setRetrySettings(updateKnowledgeBaseRetrySettings);

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
    Retry listKnowledgeBasesRetry = clientProperties.getListKnowledgeBasesRetry();
    if (listKnowledgeBasesRetry != null) {
      RetrySettings listKnowledgeBasesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listKnowledgeBasesSettings().getRetrySettings(),
              listKnowledgeBasesRetry);
      clientSettingsBuilder
          .listKnowledgeBasesSettings()
          .setRetrySettings(listKnowledgeBasesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listKnowledgeBases from properties.");
      }
    }
    Retry getKnowledgeBaseRetry = clientProperties.getGetKnowledgeBaseRetry();
    if (getKnowledgeBaseRetry != null) {
      RetrySettings getKnowledgeBaseRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getKnowledgeBaseSettings().getRetrySettings(),
              getKnowledgeBaseRetry);
      clientSettingsBuilder
          .getKnowledgeBaseSettings()
          .setRetrySettings(getKnowledgeBaseRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getKnowledgeBase from properties.");
      }
    }
    Retry createKnowledgeBaseRetry = clientProperties.getCreateKnowledgeBaseRetry();
    if (createKnowledgeBaseRetry != null) {
      RetrySettings createKnowledgeBaseRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createKnowledgeBaseSettings().getRetrySettings(),
              createKnowledgeBaseRetry);
      clientSettingsBuilder
          .createKnowledgeBaseSettings()
          .setRetrySettings(createKnowledgeBaseRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createKnowledgeBase from properties.");
      }
    }
    Retry deleteKnowledgeBaseRetry = clientProperties.getDeleteKnowledgeBaseRetry();
    if (deleteKnowledgeBaseRetry != null) {
      RetrySettings deleteKnowledgeBaseRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteKnowledgeBaseSettings().getRetrySettings(),
              deleteKnowledgeBaseRetry);
      clientSettingsBuilder
          .deleteKnowledgeBaseSettings()
          .setRetrySettings(deleteKnowledgeBaseRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteKnowledgeBase from properties.");
      }
    }
    Retry updateKnowledgeBaseRetry = clientProperties.getUpdateKnowledgeBaseRetry();
    if (updateKnowledgeBaseRetry != null) {
      RetrySettings updateKnowledgeBaseRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateKnowledgeBaseSettings().getRetrySettings(),
              updateKnowledgeBaseRetry);
      clientSettingsBuilder
          .updateKnowledgeBaseSettings()
          .setRetrySettings(updateKnowledgeBaseRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateKnowledgeBase from properties.");
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
   * Provides a KnowledgeBasesClient bean configured with KnowledgeBasesSettings.
   *
   * @param knowledgeBasesSettings settings to configure an instance of client bean.
   * @return a {@link KnowledgeBasesClient} bean configured with {@link KnowledgeBasesSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public KnowledgeBasesClient knowledgeBasesClient(KnowledgeBasesSettings knowledgeBasesSettings)
      throws IOException {
    return KnowledgeBasesClient.create(knowledgeBasesSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-knowledge-bases";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
