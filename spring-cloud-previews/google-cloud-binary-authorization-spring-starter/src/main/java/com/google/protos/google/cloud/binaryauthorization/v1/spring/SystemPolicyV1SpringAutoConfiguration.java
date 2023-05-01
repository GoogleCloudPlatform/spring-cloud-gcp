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

package com.google.protos.google.cloud.binaryauthorization.v1.spring;

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
import com.google.protos.google.cloud.binaryauthorization.v1.SystemPolicyV1Client;
import com.google.protos.google.cloud.binaryauthorization.v1.SystemPolicyV1Settings;
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
 * Auto-configuration for {@link SystemPolicyV1Client}.
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
@ConditionalOnClass(SystemPolicyV1Client.class)
@ConditionalOnProperty(
    value = "com.google.protos.google.cloud.binaryauthorization.v1.system-policy-v1.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(SystemPolicyV1SpringProperties.class)
public class SystemPolicyV1SpringAutoConfiguration {
  private final SystemPolicyV1SpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(SystemPolicyV1SpringAutoConfiguration.class);

  protected SystemPolicyV1SpringAutoConfiguration(
      SystemPolicyV1SpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from SystemPolicyV1-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultSystemPolicyV1TransportChannelProvider")
  public TransportChannelProvider defaultSystemPolicyV1TransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return SystemPolicyV1Settings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return SystemPolicyV1Settings.defaultTransportChannelProvider();
  }

  /**
   * Provides a SystemPolicyV1Settings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultSystemPolicyV1TransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in SystemPolicyV1SpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link SystemPolicyV1Settings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public SystemPolicyV1Settings systemPolicyV1Settings(
      @Qualifier("defaultSystemPolicyV1TransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    SystemPolicyV1Settings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = SystemPolicyV1Settings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = SystemPolicyV1Settings.newBuilder();
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
          SystemPolicyV1Settings.defaultExecutorProviderBuilder()
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
      RetrySettings getSystemPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSystemPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getSystemPolicySettings()
          .setRetrySettings(getSystemPolicyRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry getSystemPolicyRetry = clientProperties.getGetSystemPolicyRetry();
    if (getSystemPolicyRetry != null) {
      RetrySettings getSystemPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSystemPolicySettings().getRetrySettings(),
              getSystemPolicyRetry);
      clientSettingsBuilder
          .getSystemPolicySettings()
          .setRetrySettings(getSystemPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getSystemPolicy from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a SystemPolicyV1Client bean configured with SystemPolicyV1Settings.
   *
   * @param systemPolicyV1Settings settings to configure an instance of client bean.
   * @return a {@link SystemPolicyV1Client} bean configured with {@link SystemPolicyV1Settings}
   */
  @Bean
  @ConditionalOnMissingBean
  public SystemPolicyV1Client systemPolicyV1Client(SystemPolicyV1Settings systemPolicyV1Settings)
      throws IOException {
    return SystemPolicyV1Client.create(systemPolicyV1Settings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-system-policy-v1";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
