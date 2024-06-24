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
import com.google.showcase.v1beta1.MessagingClient;
import com.google.showcase.v1beta1.MessagingSettings;
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
 * Auto-configuration for {@link MessagingClient}.
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
@ConditionalOnClass(MessagingClient.class)
@ConditionalOnProperty(
    value = "com.google.showcase.v1beta1.messaging.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(MessagingSpringProperties.class)
public class MessagingSpringAutoConfiguration {
  private final MessagingSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(MessagingSpringAutoConfiguration.class);

  protected MessagingSpringAutoConfiguration(
      MessagingSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Messaging-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultMessagingTransportChannelProvider")
  public TransportChannelProvider defaultMessagingTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return MessagingSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return MessagingSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a MessagingSettings bean configured to use a DefaultCredentialsProvider and the client
   * library's default transport channel provider (defaultMessagingTransportChannelProvider()). It
   * also configures the quota project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in MessagingSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link MessagingSettings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public MessagingSettings messagingSettings(
      @Qualifier("defaultMessagingTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    MessagingSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = MessagingSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = MessagingSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(MessagingSettings.getDefaultEndpoint())
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
          MessagingSettings.defaultExecutorProviderBuilder()
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
      RetrySettings createRoomRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createRoomSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createRoomSettings().setRetrySettings(createRoomRetrySettings);

      RetrySettings getRoomRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRoomSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getRoomSettings().setRetrySettings(getRoomRetrySettings);

      RetrySettings updateRoomRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateRoomSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateRoomSettings().setRetrySettings(updateRoomRetrySettings);

      RetrySettings deleteRoomRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteRoomSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteRoomSettings().setRetrySettings(deleteRoomRetrySettings);

      RetrySettings listRoomsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRoomsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listRoomsSettings().setRetrySettings(listRoomsRetrySettings);

      RetrySettings createBlurbRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createBlurbSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createBlurbSettings().setRetrySettings(createBlurbRetrySettings);

      RetrySettings getBlurbRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBlurbSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getBlurbSettings().setRetrySettings(getBlurbRetrySettings);

      RetrySettings updateBlurbRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateBlurbSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateBlurbSettings().setRetrySettings(updateBlurbRetrySettings);

      RetrySettings deleteBlurbRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteBlurbSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteBlurbSettings().setRetrySettings(deleteBlurbRetrySettings);

      RetrySettings listBlurbsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listBlurbsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listBlurbsSettings().setRetrySettings(listBlurbsRetrySettings);

      RetrySettings listLocationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listLocationsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listLocationsSettings().setRetrySettings(listLocationsRetrySettings);

      RetrySettings getLocationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getLocationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getLocationSettings().setRetrySettings(getLocationRetrySettings);

      RetrySettings setIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setIamPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.setIamPolicySettings().setRetrySettings(setIamPolicyRetrySettings);

      RetrySettings getIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIamPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getIamPolicySettings().setRetrySettings(getIamPolicyRetrySettings);

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
    Retry createRoomRetry = clientProperties.getCreateRoomRetry();
    if (createRoomRetry != null) {
      RetrySettings createRoomRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createRoomSettings().getRetrySettings(), createRoomRetry);
      clientSettingsBuilder.createRoomSettings().setRetrySettings(createRoomRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createRoom from properties.");
      }
    }
    Retry getRoomRetry = clientProperties.getGetRoomRetry();
    if (getRoomRetry != null) {
      RetrySettings getRoomRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRoomSettings().getRetrySettings(), getRoomRetry);
      clientSettingsBuilder.getRoomSettings().setRetrySettings(getRoomRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getRoom from properties.");
      }
    }
    Retry updateRoomRetry = clientProperties.getUpdateRoomRetry();
    if (updateRoomRetry != null) {
      RetrySettings updateRoomRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateRoomSettings().getRetrySettings(), updateRoomRetry);
      clientSettingsBuilder.updateRoomSettings().setRetrySettings(updateRoomRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateRoom from properties.");
      }
    }
    Retry deleteRoomRetry = clientProperties.getDeleteRoomRetry();
    if (deleteRoomRetry != null) {
      RetrySettings deleteRoomRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteRoomSettings().getRetrySettings(), deleteRoomRetry);
      clientSettingsBuilder.deleteRoomSettings().setRetrySettings(deleteRoomRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteRoom from properties.");
      }
    }
    Retry listRoomsRetry = clientProperties.getListRoomsRetry();
    if (listRoomsRetry != null) {
      RetrySettings listRoomsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRoomsSettings().getRetrySettings(), listRoomsRetry);
      clientSettingsBuilder.listRoomsSettings().setRetrySettings(listRoomsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listRooms from properties.");
      }
    }
    Retry createBlurbRetry = clientProperties.getCreateBlurbRetry();
    if (createBlurbRetry != null) {
      RetrySettings createBlurbRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createBlurbSettings().getRetrySettings(), createBlurbRetry);
      clientSettingsBuilder.createBlurbSettings().setRetrySettings(createBlurbRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createBlurb from properties.");
      }
    }
    Retry getBlurbRetry = clientProperties.getGetBlurbRetry();
    if (getBlurbRetry != null) {
      RetrySettings getBlurbRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBlurbSettings().getRetrySettings(), getBlurbRetry);
      clientSettingsBuilder.getBlurbSettings().setRetrySettings(getBlurbRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getBlurb from properties.");
      }
    }
    Retry updateBlurbRetry = clientProperties.getUpdateBlurbRetry();
    if (updateBlurbRetry != null) {
      RetrySettings updateBlurbRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateBlurbSettings().getRetrySettings(), updateBlurbRetry);
      clientSettingsBuilder.updateBlurbSettings().setRetrySettings(updateBlurbRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateBlurb from properties.");
      }
    }
    Retry deleteBlurbRetry = clientProperties.getDeleteBlurbRetry();
    if (deleteBlurbRetry != null) {
      RetrySettings deleteBlurbRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteBlurbSettings().getRetrySettings(), deleteBlurbRetry);
      clientSettingsBuilder.deleteBlurbSettings().setRetrySettings(deleteBlurbRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteBlurb from properties.");
      }
    }
    Retry listBlurbsRetry = clientProperties.getListBlurbsRetry();
    if (listBlurbsRetry != null) {
      RetrySettings listBlurbsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listBlurbsSettings().getRetrySettings(), listBlurbsRetry);
      clientSettingsBuilder.listBlurbsSettings().setRetrySettings(listBlurbsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listBlurbs from properties.");
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
   * Provides a MessagingClient bean configured with MessagingSettings.
   *
   * @param messagingSettings settings to configure an instance of client bean.
   * @return a {@link MessagingClient} bean configured with {@link MessagingSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public MessagingClient messagingClient(MessagingSettings messagingSettings) throws IOException {
    return MessagingClient.create(messagingSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-messaging";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
