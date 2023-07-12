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

package com.google.showcase.v1beta1.spring;

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
import com.google.showcase.v1beta1.IdentityClient;
import com.google.showcase.v1beta1.IdentitySettings;
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
 * Auto-configuration for {@link IdentityClient}.
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
@ConditionalOnClass(IdentityClient.class)
@ConditionalOnProperty(
    value = "com.google.showcase.v1beta1.identity.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(IdentitySpringProperties.class)
public class IdentitySpringAutoConfiguration {
  private final IdentitySpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(IdentitySpringAutoConfiguration.class);

  protected IdentitySpringAutoConfiguration(
      IdentitySpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Identity-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultIdentityTransportChannelProvider")
  public TransportChannelProvider defaultIdentityTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return IdentitySettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return IdentitySettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a IdentitySettings bean configured to use a DefaultCredentialsProvider and the client
   * library's default transport channel provider (defaultIdentityTransportChannelProvider()). It
   * also configures the quota project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in IdentitySpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link IdentitySettings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public IdentitySettings identitySettings(
      @Qualifier("defaultIdentityTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    IdentitySettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = IdentitySettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = IdentitySettings.newBuilder();
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
          IdentitySettings.defaultExecutorProviderBuilder()
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
      RetrySettings createUserRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createUserSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createUserSettings().setRetrySettings(createUserRetrySettings);

      RetrySettings getUserRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getUserSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getUserSettings().setRetrySettings(getUserRetrySettings);

      RetrySettings updateUserRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateUserSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateUserSettings().setRetrySettings(updateUserRetrySettings);

      RetrySettings deleteUserRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteUserSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteUserSettings().setRetrySettings(deleteUserRetrySettings);

      RetrySettings listUsersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listUsersSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listUsersSettings().setRetrySettings(listUsersRetrySettings);

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
    Retry createUserRetry = clientProperties.getCreateUserRetry();
    if (createUserRetry != null) {
      RetrySettings createUserRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createUserSettings().getRetrySettings(), createUserRetry);
      clientSettingsBuilder.createUserSettings().setRetrySettings(createUserRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createUser from properties.");
      }
    }
    Retry getUserRetry = clientProperties.getGetUserRetry();
    if (getUserRetry != null) {
      RetrySettings getUserRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getUserSettings().getRetrySettings(), getUserRetry);
      clientSettingsBuilder.getUserSettings().setRetrySettings(getUserRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getUser from properties.");
      }
    }
    Retry updateUserRetry = clientProperties.getUpdateUserRetry();
    if (updateUserRetry != null) {
      RetrySettings updateUserRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateUserSettings().getRetrySettings(), updateUserRetry);
      clientSettingsBuilder.updateUserSettings().setRetrySettings(updateUserRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateUser from properties.");
      }
    }
    Retry deleteUserRetry = clientProperties.getDeleteUserRetry();
    if (deleteUserRetry != null) {
      RetrySettings deleteUserRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteUserSettings().getRetrySettings(), deleteUserRetry);
      clientSettingsBuilder.deleteUserSettings().setRetrySettings(deleteUserRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteUser from properties.");
      }
    }
    Retry listUsersRetry = clientProperties.getListUsersRetry();
    if (listUsersRetry != null) {
      RetrySettings listUsersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listUsersSettings().getRetrySettings(), listUsersRetry);
      clientSettingsBuilder.listUsersSettings().setRetrySettings(listUsersRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listUsers from properties.");
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
   * Provides a IdentityClient bean configured with IdentitySettings.
   *
   * @param identitySettings settings to configure an instance of client bean.
   * @return a {@link IdentityClient} bean configured with {@link IdentitySettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public IdentityClient identityClient(IdentitySettings identitySettings) throws IOException {
    return IdentityClient.create(identitySettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-identity";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
