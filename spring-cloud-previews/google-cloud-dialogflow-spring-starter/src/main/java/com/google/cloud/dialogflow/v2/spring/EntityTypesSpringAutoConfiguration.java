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

package com.google.cloud.dialogflow.v2.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.dialogflow.v2.EntityTypesClient;
import com.google.cloud.dialogflow.v2.EntityTypesSettings;
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
 * Auto-configuration for {@link EntityTypesClient}.
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
@ConditionalOnClass(EntityTypesClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.dialogflow.v2.entity-types.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(EntityTypesSpringProperties.class)
public class EntityTypesSpringAutoConfiguration {
  private final EntityTypesSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(EntityTypesSpringAutoConfiguration.class);

  protected EntityTypesSpringAutoConfiguration(
      EntityTypesSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from EntityTypes-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultEntityTypesTransportChannelProvider")
  public TransportChannelProvider defaultEntityTypesTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return EntityTypesSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return EntityTypesSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a EntityTypesSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultEntityTypesTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in EntityTypesSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link EntityTypesSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public EntityTypesSettings entityTypesSettings(
      @Qualifier("defaultEntityTypesTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    EntityTypesSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = EntityTypesSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = EntityTypesSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(EntityTypesSettings.getDefaultEndpoint())
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
          EntityTypesSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listEntityTypesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listEntityTypesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listEntityTypesSettings()
          .setRetrySettings(listEntityTypesRetrySettings);

      RetrySettings getEntityTypeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getEntityTypeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getEntityTypeSettings().setRetrySettings(getEntityTypeRetrySettings);

      RetrySettings createEntityTypeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createEntityTypeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createEntityTypeSettings()
          .setRetrySettings(createEntityTypeRetrySettings);

      RetrySettings updateEntityTypeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateEntityTypeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateEntityTypeSettings()
          .setRetrySettings(updateEntityTypeRetrySettings);

      RetrySettings deleteEntityTypeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteEntityTypeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .deleteEntityTypeSettings()
          .setRetrySettings(deleteEntityTypeRetrySettings);

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
    Retry listEntityTypesRetry = clientProperties.getListEntityTypesRetry();
    if (listEntityTypesRetry != null) {
      RetrySettings listEntityTypesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listEntityTypesSettings().getRetrySettings(),
              listEntityTypesRetry);
      clientSettingsBuilder
          .listEntityTypesSettings()
          .setRetrySettings(listEntityTypesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listEntityTypes from properties.");
      }
    }
    Retry getEntityTypeRetry = clientProperties.getGetEntityTypeRetry();
    if (getEntityTypeRetry != null) {
      RetrySettings getEntityTypeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getEntityTypeSettings().getRetrySettings(), getEntityTypeRetry);
      clientSettingsBuilder.getEntityTypeSettings().setRetrySettings(getEntityTypeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getEntityType from properties.");
      }
    }
    Retry createEntityTypeRetry = clientProperties.getCreateEntityTypeRetry();
    if (createEntityTypeRetry != null) {
      RetrySettings createEntityTypeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createEntityTypeSettings().getRetrySettings(),
              createEntityTypeRetry);
      clientSettingsBuilder
          .createEntityTypeSettings()
          .setRetrySettings(createEntityTypeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createEntityType from properties.");
      }
    }
    Retry updateEntityTypeRetry = clientProperties.getUpdateEntityTypeRetry();
    if (updateEntityTypeRetry != null) {
      RetrySettings updateEntityTypeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateEntityTypeSettings().getRetrySettings(),
              updateEntityTypeRetry);
      clientSettingsBuilder
          .updateEntityTypeSettings()
          .setRetrySettings(updateEntityTypeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateEntityType from properties.");
      }
    }
    Retry deleteEntityTypeRetry = clientProperties.getDeleteEntityTypeRetry();
    if (deleteEntityTypeRetry != null) {
      RetrySettings deleteEntityTypeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteEntityTypeSettings().getRetrySettings(),
              deleteEntityTypeRetry);
      clientSettingsBuilder
          .deleteEntityTypeSettings()
          .setRetrySettings(deleteEntityTypeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteEntityType from properties.");
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
   * Provides a EntityTypesClient bean configured with EntityTypesSettings.
   *
   * @param entityTypesSettings settings to configure an instance of client bean.
   * @return a {@link EntityTypesClient} bean configured with {@link EntityTypesSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public EntityTypesClient entityTypesClient(EntityTypesSettings entityTypesSettings)
      throws IOException {
    return EntityTypesClient.create(entityTypesSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-entity-types";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
