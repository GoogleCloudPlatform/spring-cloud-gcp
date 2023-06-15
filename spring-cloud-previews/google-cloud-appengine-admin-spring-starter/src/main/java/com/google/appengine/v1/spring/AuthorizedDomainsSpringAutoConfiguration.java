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

package com.google.appengine.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.appengine.v1.AuthorizedDomainsClient;
import com.google.appengine.v1.AuthorizedDomainsSettings;
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
 * Auto-configuration for {@link AuthorizedDomainsClient}.
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
@ConditionalOnClass(AuthorizedDomainsClient.class)
@ConditionalOnProperty(
    value = "com.google.appengine.v1.authorized-domains.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(AuthorizedDomainsSpringProperties.class)
public class AuthorizedDomainsSpringAutoConfiguration {
  private final AuthorizedDomainsSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(AuthorizedDomainsSpringAutoConfiguration.class);

  protected AuthorizedDomainsSpringAutoConfiguration(
      AuthorizedDomainsSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from AuthorizedDomains-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultAuthorizedDomainsTransportChannelProvider")
  public TransportChannelProvider defaultAuthorizedDomainsTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return AuthorizedDomainsSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return AuthorizedDomainsSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a AuthorizedDomainsSettings bean configured to use a DefaultCredentialsProvider and
   * the client library's default transport channel provider
   * (defaultAuthorizedDomainsTransportChannelProvider()). It also configures the quota project ID
   * and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in AuthorizedDomainsSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link AuthorizedDomainsSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public AuthorizedDomainsSettings authorizedDomainsSettings(
      @Qualifier("defaultAuthorizedDomainsTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    AuthorizedDomainsSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = AuthorizedDomainsSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = AuthorizedDomainsSettings.newBuilder();
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
          AuthorizedDomainsSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listAuthorizedDomainsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listAuthorizedDomainsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listAuthorizedDomainsSettings()
          .setRetrySettings(listAuthorizedDomainsRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listAuthorizedDomainsRetry = clientProperties.getListAuthorizedDomainsRetry();
    if (listAuthorizedDomainsRetry != null) {
      RetrySettings listAuthorizedDomainsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listAuthorizedDomainsSettings().getRetrySettings(),
              listAuthorizedDomainsRetry);
      clientSettingsBuilder
          .listAuthorizedDomainsSettings()
          .setRetrySettings(listAuthorizedDomainsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listAuthorizedDomains from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a AuthorizedDomainsClient bean configured with AuthorizedDomainsSettings.
   *
   * @param authorizedDomainsSettings settings to configure an instance of client bean.
   * @return a {@link AuthorizedDomainsClient} bean configured with {@link
   *     AuthorizedDomainsSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public AuthorizedDomainsClient authorizedDomainsClient(
      AuthorizedDomainsSettings authorizedDomainsSettings) throws IOException {
    return AuthorizedDomainsClient.create(authorizedDomainsSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-authorized-domains";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
