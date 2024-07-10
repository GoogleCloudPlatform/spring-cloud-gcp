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

package com.google.cloud.iap.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.iap.v1.IdentityAwareProxyOAuthServiceClient;
import com.google.cloud.iap.v1.IdentityAwareProxyOAuthServiceSettings;
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
 * Auto-configuration for {@link IdentityAwareProxyOAuthServiceClient}.
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
@ConditionalOnClass(IdentityAwareProxyOAuthServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.iap.v1.identity-aware-proxy-o-auth-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(IdentityAwareProxyOAuthServiceSpringProperties.class)
public class IdentityAwareProxyOAuthServiceSpringAutoConfiguration {
  private final IdentityAwareProxyOAuthServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(IdentityAwareProxyOAuthServiceSpringAutoConfiguration.class);

  protected IdentityAwareProxyOAuthServiceSpringAutoConfiguration(
      IdentityAwareProxyOAuthServiceSpringProperties clientProperties,
      CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Using credentials from IdentityAwareProxyOAuthService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultIdentityAwareProxyOAuthServiceTransportChannelProvider")
  public TransportChannelProvider defaultIdentityAwareProxyOAuthServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return IdentityAwareProxyOAuthServiceSettings.defaultHttpJsonTransportProviderBuilder()
          .build();
    }
    return IdentityAwareProxyOAuthServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a IdentityAwareProxyOAuthServiceSettings bean configured to use a
   * DefaultCredentialsProvider and the client library's default transport channel provider
   * (defaultIdentityAwareProxyOAuthServiceTransportChannelProvider()). It also configures the quota
   * project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in IdentityAwareProxyOAuthServiceSpringProperties. Method-level properties will take precedence
   * over service-level properties if available, and client library defaults will be used if neither
   * are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link IdentityAwareProxyOAuthServiceSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public IdentityAwareProxyOAuthServiceSettings identityAwareProxyOAuthServiceSettings(
      @Qualifier("defaultIdentityAwareProxyOAuthServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    IdentityAwareProxyOAuthServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = IdentityAwareProxyOAuthServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = IdentityAwareProxyOAuthServiceSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(IdentityAwareProxyOAuthServiceSettings.getDefaultEndpoint())
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
          IdentityAwareProxyOAuthServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listBrandsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listBrandsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listBrandsSettings().setRetrySettings(listBrandsRetrySettings);

      RetrySettings createBrandRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createBrandSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createBrandSettings().setRetrySettings(createBrandRetrySettings);

      RetrySettings getBrandRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBrandSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getBrandSettings().setRetrySettings(getBrandRetrySettings);

      RetrySettings createIdentityAwareProxyClientRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createIdentityAwareProxyClientSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .createIdentityAwareProxyClientSettings()
          .setRetrySettings(createIdentityAwareProxyClientRetrySettings);

      RetrySettings listIdentityAwareProxyClientsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listIdentityAwareProxyClientsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listIdentityAwareProxyClientsSettings()
          .setRetrySettings(listIdentityAwareProxyClientsRetrySettings);

      RetrySettings getIdentityAwareProxyClientRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIdentityAwareProxyClientSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getIdentityAwareProxyClientSettings()
          .setRetrySettings(getIdentityAwareProxyClientRetrySettings);

      RetrySettings resetIdentityAwareProxyClientSecretRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder
                  .resetIdentityAwareProxyClientSecretSettings()
                  .getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .resetIdentityAwareProxyClientSecretSettings()
          .setRetrySettings(resetIdentityAwareProxyClientSecretRetrySettings);

      RetrySettings deleteIdentityAwareProxyClientRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteIdentityAwareProxyClientSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .deleteIdentityAwareProxyClientSettings()
          .setRetrySettings(deleteIdentityAwareProxyClientRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listBrandsRetry = clientProperties.getListBrandsRetry();
    if (listBrandsRetry != null) {
      RetrySettings listBrandsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listBrandsSettings().getRetrySettings(), listBrandsRetry);
      clientSettingsBuilder.listBrandsSettings().setRetrySettings(listBrandsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listBrands from properties.");
      }
    }
    Retry createBrandRetry = clientProperties.getCreateBrandRetry();
    if (createBrandRetry != null) {
      RetrySettings createBrandRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createBrandSettings().getRetrySettings(), createBrandRetry);
      clientSettingsBuilder.createBrandSettings().setRetrySettings(createBrandRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createBrand from properties.");
      }
    }
    Retry getBrandRetry = clientProperties.getGetBrandRetry();
    if (getBrandRetry != null) {
      RetrySettings getBrandRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBrandSettings().getRetrySettings(), getBrandRetry);
      clientSettingsBuilder.getBrandSettings().setRetrySettings(getBrandRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getBrand from properties.");
      }
    }
    Retry createIdentityAwareProxyClientRetry =
        clientProperties.getCreateIdentityAwareProxyClientRetry();
    if (createIdentityAwareProxyClientRetry != null) {
      RetrySettings createIdentityAwareProxyClientRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createIdentityAwareProxyClientSettings().getRetrySettings(),
              createIdentityAwareProxyClientRetry);
      clientSettingsBuilder
          .createIdentityAwareProxyClientSettings()
          .setRetrySettings(createIdentityAwareProxyClientRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createIdentityAwareProxyClient from properties.");
      }
    }
    Retry listIdentityAwareProxyClientsRetry =
        clientProperties.getListIdentityAwareProxyClientsRetry();
    if (listIdentityAwareProxyClientsRetry != null) {
      RetrySettings listIdentityAwareProxyClientsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listIdentityAwareProxyClientsSettings().getRetrySettings(),
              listIdentityAwareProxyClientsRetry);
      clientSettingsBuilder
          .listIdentityAwareProxyClientsSettings()
          .setRetrySettings(listIdentityAwareProxyClientsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listIdentityAwareProxyClients from properties.");
      }
    }
    Retry getIdentityAwareProxyClientRetry = clientProperties.getGetIdentityAwareProxyClientRetry();
    if (getIdentityAwareProxyClientRetry != null) {
      RetrySettings getIdentityAwareProxyClientRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIdentityAwareProxyClientSettings().getRetrySettings(),
              getIdentityAwareProxyClientRetry);
      clientSettingsBuilder
          .getIdentityAwareProxyClientSettings()
          .setRetrySettings(getIdentityAwareProxyClientRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getIdentityAwareProxyClient from properties.");
      }
    }
    Retry resetIdentityAwareProxyClientSecretRetry =
        clientProperties.getResetIdentityAwareProxyClientSecretRetry();
    if (resetIdentityAwareProxyClientSecretRetry != null) {
      RetrySettings resetIdentityAwareProxyClientSecretRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder
                  .resetIdentityAwareProxyClientSecretSettings()
                  .getRetrySettings(),
              resetIdentityAwareProxyClientSecretRetry);
      clientSettingsBuilder
          .resetIdentityAwareProxyClientSecretSettings()
          .setRetrySettings(resetIdentityAwareProxyClientSecretRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for resetIdentityAwareProxyClientSecret from properties.");
      }
    }
    Retry deleteIdentityAwareProxyClientRetry =
        clientProperties.getDeleteIdentityAwareProxyClientRetry();
    if (deleteIdentityAwareProxyClientRetry != null) {
      RetrySettings deleteIdentityAwareProxyClientRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteIdentityAwareProxyClientSettings().getRetrySettings(),
              deleteIdentityAwareProxyClientRetry);
      clientSettingsBuilder
          .deleteIdentityAwareProxyClientSettings()
          .setRetrySettings(deleteIdentityAwareProxyClientRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteIdentityAwareProxyClient from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a IdentityAwareProxyOAuthServiceClient bean configured with
   * IdentityAwareProxyOAuthServiceSettings.
   *
   * @param identityAwareProxyOAuthServiceSettings settings to configure an instance of client bean.
   * @return a {@link IdentityAwareProxyOAuthServiceClient} bean configured with {@link
   *     IdentityAwareProxyOAuthServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public IdentityAwareProxyOAuthServiceClient identityAwareProxyOAuthServiceClient(
      IdentityAwareProxyOAuthServiceSettings identityAwareProxyOAuthServiceSettings)
      throws IOException {
    return IdentityAwareProxyOAuthServiceClient.create(identityAwareProxyOAuthServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-identity-aware-proxy-o-auth-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
