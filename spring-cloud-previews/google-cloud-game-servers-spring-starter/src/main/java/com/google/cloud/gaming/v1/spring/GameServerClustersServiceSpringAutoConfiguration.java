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

package com.google.cloud.gaming.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.gaming.v1.GameServerClustersServiceClient;
import com.google.cloud.gaming.v1.GameServerClustersServiceSettings;
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
 * Auto-configuration for {@link GameServerClustersServiceClient}.
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
@ConditionalOnClass(GameServerClustersServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.gaming.v1.game-server-clusters-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(GameServerClustersServiceSpringProperties.class)
public class GameServerClustersServiceSpringAutoConfiguration {
  private final GameServerClustersServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(GameServerClustersServiceSpringAutoConfiguration.class);

  protected GameServerClustersServiceSpringAutoConfiguration(
      GameServerClustersServiceSpringProperties clientProperties,
      CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from GameServerClustersService-specific configuration");
      }
      this.credentialsProvider =
          ((CredentialsProvider) new DefaultCredentialsProvider(this.clientProperties));
    } else {
      this.credentialsProvider = credentialsProvider;
    }
  }

  /**
   * Provides a default transport channel provider bean. The default is gRPC and will default to it
   * unless the useRest option is supported and provided to use HTTP transport instead
   *
   * @return a default transport channel provider.
   */
  @Bean
  @ConditionalOnMissingBean(name = "defaultGameServerClustersServiceTransportChannelProvider")
  public TransportChannelProvider defaultGameServerClustersServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return GameServerClustersServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return GameServerClustersServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a GameServerClustersServiceSettings bean configured to use a
   * DefaultCredentialsProvider and the client library's default transport channel provider
   * (defaultGameServerClustersServiceTransportChannelProvider()). It also configures the quota
   * project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in GameServerClustersServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link GameServerClustersServiceSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public GameServerClustersServiceSettings gameServerClustersServiceSettings(
      @Qualifier("defaultGameServerClustersServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    GameServerClustersServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = GameServerClustersServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = GameServerClustersServiceSettings.newBuilder();
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
          GameServerClustersServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listGameServerClustersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listGameServerClustersSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listGameServerClustersSettings()
          .setRetrySettings(listGameServerClustersRetrySettings);

      RetrySettings getGameServerClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getGameServerClusterSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getGameServerClusterSettings()
          .setRetrySettings(getGameServerClusterRetrySettings);

      RetrySettings previewCreateGameServerClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.previewCreateGameServerClusterSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .previewCreateGameServerClusterSettings()
          .setRetrySettings(previewCreateGameServerClusterRetrySettings);

      RetrySettings previewDeleteGameServerClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.previewDeleteGameServerClusterSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .previewDeleteGameServerClusterSettings()
          .setRetrySettings(previewDeleteGameServerClusterRetrySettings);

      RetrySettings previewUpdateGameServerClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.previewUpdateGameServerClusterSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .previewUpdateGameServerClusterSettings()
          .setRetrySettings(previewUpdateGameServerClusterRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listGameServerClustersRetry = clientProperties.getListGameServerClustersRetry();
    if (listGameServerClustersRetry != null) {
      RetrySettings listGameServerClustersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listGameServerClustersSettings().getRetrySettings(),
              listGameServerClustersRetry);
      clientSettingsBuilder
          .listGameServerClustersSettings()
          .setRetrySettings(listGameServerClustersRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listGameServerClusters from properties.");
      }
    }
    Retry getGameServerClusterRetry = clientProperties.getGetGameServerClusterRetry();
    if (getGameServerClusterRetry != null) {
      RetrySettings getGameServerClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getGameServerClusterSettings().getRetrySettings(),
              getGameServerClusterRetry);
      clientSettingsBuilder
          .getGameServerClusterSettings()
          .setRetrySettings(getGameServerClusterRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getGameServerCluster from properties.");
      }
    }
    Retry previewCreateGameServerClusterRetry =
        clientProperties.getPreviewCreateGameServerClusterRetry();
    if (previewCreateGameServerClusterRetry != null) {
      RetrySettings previewCreateGameServerClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.previewCreateGameServerClusterSettings().getRetrySettings(),
              previewCreateGameServerClusterRetry);
      clientSettingsBuilder
          .previewCreateGameServerClusterSettings()
          .setRetrySettings(previewCreateGameServerClusterRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for previewCreateGameServerCluster from properties.");
      }
    }
    Retry previewDeleteGameServerClusterRetry =
        clientProperties.getPreviewDeleteGameServerClusterRetry();
    if (previewDeleteGameServerClusterRetry != null) {
      RetrySettings previewDeleteGameServerClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.previewDeleteGameServerClusterSettings().getRetrySettings(),
              previewDeleteGameServerClusterRetry);
      clientSettingsBuilder
          .previewDeleteGameServerClusterSettings()
          .setRetrySettings(previewDeleteGameServerClusterRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for previewDeleteGameServerCluster from properties.");
      }
    }
    Retry previewUpdateGameServerClusterRetry =
        clientProperties.getPreviewUpdateGameServerClusterRetry();
    if (previewUpdateGameServerClusterRetry != null) {
      RetrySettings previewUpdateGameServerClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.previewUpdateGameServerClusterSettings().getRetrySettings(),
              previewUpdateGameServerClusterRetry);
      clientSettingsBuilder
          .previewUpdateGameServerClusterSettings()
          .setRetrySettings(previewUpdateGameServerClusterRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for previewUpdateGameServerCluster from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a GameServerClustersServiceClient bean configured with
   * GameServerClustersServiceSettings.
   *
   * @param gameServerClustersServiceSettings settings to configure an instance of client bean.
   * @return a {@link GameServerClustersServiceClient} bean configured with {@link
   *     GameServerClustersServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public GameServerClustersServiceClient gameServerClustersServiceClient(
      GameServerClustersServiceSettings gameServerClustersServiceSettings) throws IOException {
    return GameServerClustersServiceClient.create(gameServerClustersServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-game-server-clusters-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}