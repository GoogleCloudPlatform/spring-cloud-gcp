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

package com.google.cloud.iap.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.iap.v1.IdentityAwareProxyAdminServiceClient;
import com.google.cloud.iap.v1.IdentityAwareProxyAdminServiceSettings;
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
 * Auto-configuration for {@link IdentityAwareProxyAdminServiceClient}.
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
@ConditionalOnClass(IdentityAwareProxyAdminServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.iap.v1.identity-aware-proxy-admin-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(IdentityAwareProxyAdminServiceSpringProperties.class)
public class IdentityAwareProxyAdminServiceSpringAutoConfiguration {
  private final IdentityAwareProxyAdminServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(IdentityAwareProxyAdminServiceSpringAutoConfiguration.class);

  protected IdentityAwareProxyAdminServiceSpringAutoConfiguration(
      IdentityAwareProxyAdminServiceSpringProperties clientProperties,
      CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Using credentials from IdentityAwareProxyAdminService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultIdentityAwareProxyAdminServiceTransportChannelProvider")
  public TransportChannelProvider defaultIdentityAwareProxyAdminServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return IdentityAwareProxyAdminServiceSettings.defaultHttpJsonTransportProviderBuilder()
          .build();
    }
    return IdentityAwareProxyAdminServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a IdentityAwareProxyAdminServiceSettings bean configured to use a
   * DefaultCredentialsProvider and the client library's default transport channel provider
   * (defaultIdentityAwareProxyAdminServiceTransportChannelProvider()). It also configures the quota
   * project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in IdentityAwareProxyAdminServiceSpringProperties. Method-level properties will take precedence
   * over service-level properties if available, and client library defaults will be used if neither
   * are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link IdentityAwareProxyAdminServiceSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public IdentityAwareProxyAdminServiceSettings identityAwareProxyAdminServiceSettings(
      @Qualifier("defaultIdentityAwareProxyAdminServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    IdentityAwareProxyAdminServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = IdentityAwareProxyAdminServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = IdentityAwareProxyAdminServiceSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(IdentityAwareProxyAdminServiceSettings.getDefaultEndpoint())
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
          IdentityAwareProxyAdminServiceSettings.defaultExecutorProviderBuilder()
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

      RetrySettings getIapSettingsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIapSettingsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getIapSettingsSettings().setRetrySettings(getIapSettingsRetrySettings);

      RetrySettings updateIapSettingsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateIapSettingsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateIapSettingsSettings()
          .setRetrySettings(updateIapSettingsRetrySettings);

      RetrySettings listTunnelDestGroupsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listTunnelDestGroupsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listTunnelDestGroupsSettings()
          .setRetrySettings(listTunnelDestGroupsRetrySettings);

      RetrySettings createTunnelDestGroupRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createTunnelDestGroupSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .createTunnelDestGroupSettings()
          .setRetrySettings(createTunnelDestGroupRetrySettings);

      RetrySettings getTunnelDestGroupRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getTunnelDestGroupSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getTunnelDestGroupSettings()
          .setRetrySettings(getTunnelDestGroupRetrySettings);

      RetrySettings deleteTunnelDestGroupRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteTunnelDestGroupSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .deleteTunnelDestGroupSettings()
          .setRetrySettings(deleteTunnelDestGroupRetrySettings);

      RetrySettings updateTunnelDestGroupRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateTunnelDestGroupSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .updateTunnelDestGroupSettings()
          .setRetrySettings(updateTunnelDestGroupRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
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
    Retry getIapSettingsRetry = clientProperties.getGetIapSettingsRetry();
    if (getIapSettingsRetry != null) {
      RetrySettings getIapSettingsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIapSettingsSettings().getRetrySettings(),
              getIapSettingsRetry);
      clientSettingsBuilder.getIapSettingsSettings().setRetrySettings(getIapSettingsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getIapSettings from properties.");
      }
    }
    Retry updateIapSettingsRetry = clientProperties.getUpdateIapSettingsRetry();
    if (updateIapSettingsRetry != null) {
      RetrySettings updateIapSettingsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateIapSettingsSettings().getRetrySettings(),
              updateIapSettingsRetry);
      clientSettingsBuilder
          .updateIapSettingsSettings()
          .setRetrySettings(updateIapSettingsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateIapSettings from properties.");
      }
    }
    Retry listTunnelDestGroupsRetry = clientProperties.getListTunnelDestGroupsRetry();
    if (listTunnelDestGroupsRetry != null) {
      RetrySettings listTunnelDestGroupsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listTunnelDestGroupsSettings().getRetrySettings(),
              listTunnelDestGroupsRetry);
      clientSettingsBuilder
          .listTunnelDestGroupsSettings()
          .setRetrySettings(listTunnelDestGroupsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listTunnelDestGroups from properties.");
      }
    }
    Retry createTunnelDestGroupRetry = clientProperties.getCreateTunnelDestGroupRetry();
    if (createTunnelDestGroupRetry != null) {
      RetrySettings createTunnelDestGroupRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createTunnelDestGroupSettings().getRetrySettings(),
              createTunnelDestGroupRetry);
      clientSettingsBuilder
          .createTunnelDestGroupSettings()
          .setRetrySettings(createTunnelDestGroupRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createTunnelDestGroup from properties.");
      }
    }
    Retry getTunnelDestGroupRetry = clientProperties.getGetTunnelDestGroupRetry();
    if (getTunnelDestGroupRetry != null) {
      RetrySettings getTunnelDestGroupRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getTunnelDestGroupSettings().getRetrySettings(),
              getTunnelDestGroupRetry);
      clientSettingsBuilder
          .getTunnelDestGroupSettings()
          .setRetrySettings(getTunnelDestGroupRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getTunnelDestGroup from properties.");
      }
    }
    Retry deleteTunnelDestGroupRetry = clientProperties.getDeleteTunnelDestGroupRetry();
    if (deleteTunnelDestGroupRetry != null) {
      RetrySettings deleteTunnelDestGroupRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteTunnelDestGroupSettings().getRetrySettings(),
              deleteTunnelDestGroupRetry);
      clientSettingsBuilder
          .deleteTunnelDestGroupSettings()
          .setRetrySettings(deleteTunnelDestGroupRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteTunnelDestGroup from properties.");
      }
    }
    Retry updateTunnelDestGroupRetry = clientProperties.getUpdateTunnelDestGroupRetry();
    if (updateTunnelDestGroupRetry != null) {
      RetrySettings updateTunnelDestGroupRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateTunnelDestGroupSettings().getRetrySettings(),
              updateTunnelDestGroupRetry);
      clientSettingsBuilder
          .updateTunnelDestGroupSettings()
          .setRetrySettings(updateTunnelDestGroupRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateTunnelDestGroup from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a IdentityAwareProxyAdminServiceClient bean configured with
   * IdentityAwareProxyAdminServiceSettings.
   *
   * @param identityAwareProxyAdminServiceSettings settings to configure an instance of client bean.
   * @return a {@link IdentityAwareProxyAdminServiceClient} bean configured with {@link
   *     IdentityAwareProxyAdminServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public IdentityAwareProxyAdminServiceClient identityAwareProxyAdminServiceClient(
      IdentityAwareProxyAdminServiceSettings identityAwareProxyAdminServiceSettings)
      throws IOException {
    return IdentityAwareProxyAdminServiceClient.create(identityAwareProxyAdminServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-identity-aware-proxy-admin-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
