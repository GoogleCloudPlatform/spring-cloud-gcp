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

package com.google.cloud.debugger.v2.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.debugger.v2.Debugger2Client;
import com.google.cloud.debugger.v2.Debugger2Settings;
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
 * Auto-configuration for {@link Debugger2Client}.
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
@ConditionalOnClass(Debugger2Client.class)
@ConditionalOnProperty(
    value = "com.google.cloud.debugger.v2.debugger2.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(Debugger2SpringProperties.class)
public class Debugger2SpringAutoConfiguration {
  private final Debugger2SpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(Debugger2SpringAutoConfiguration.class);

  protected Debugger2SpringAutoConfiguration(
      Debugger2SpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Debugger2-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultDebugger2TransportChannelProvider")
  public TransportChannelProvider defaultDebugger2TransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return Debugger2Settings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return Debugger2Settings.defaultTransportChannelProvider();
  }

  /**
   * Provides a Debugger2Settings bean configured to use a DefaultCredentialsProvider and the client
   * library's default transport channel provider (defaultDebugger2TransportChannelProvider()). It
   * also configures the quota project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in Debugger2SpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link Debugger2Settings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public Debugger2Settings debugger2Settings(
      @Qualifier("defaultDebugger2TransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    Debugger2Settings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = Debugger2Settings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = Debugger2Settings.newBuilder();
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
          Debugger2Settings.defaultExecutorProviderBuilder()
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
      RetrySettings setBreakpointRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setBreakpointSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.setBreakpointSettings().setRetrySettings(setBreakpointRetrySettings);

      RetrySettings getBreakpointRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBreakpointSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getBreakpointSettings().setRetrySettings(getBreakpointRetrySettings);

      RetrySettings deleteBreakpointRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteBreakpointSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .deleteBreakpointSettings()
          .setRetrySettings(deleteBreakpointRetrySettings);

      RetrySettings listBreakpointsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listBreakpointsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listBreakpointsSettings()
          .setRetrySettings(listBreakpointsRetrySettings);

      RetrySettings listDebuggeesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listDebuggeesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listDebuggeesSettings().setRetrySettings(listDebuggeesRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry setBreakpointRetry = clientProperties.getSetBreakpointRetry();
    if (setBreakpointRetry != null) {
      RetrySettings setBreakpointRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setBreakpointSettings().getRetrySettings(), setBreakpointRetry);
      clientSettingsBuilder.setBreakpointSettings().setRetrySettings(setBreakpointRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for setBreakpoint from properties.");
      }
    }
    Retry getBreakpointRetry = clientProperties.getGetBreakpointRetry();
    if (getBreakpointRetry != null) {
      RetrySettings getBreakpointRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBreakpointSettings().getRetrySettings(), getBreakpointRetry);
      clientSettingsBuilder.getBreakpointSettings().setRetrySettings(getBreakpointRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getBreakpoint from properties.");
      }
    }
    Retry deleteBreakpointRetry = clientProperties.getDeleteBreakpointRetry();
    if (deleteBreakpointRetry != null) {
      RetrySettings deleteBreakpointRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteBreakpointSettings().getRetrySettings(),
              deleteBreakpointRetry);
      clientSettingsBuilder
          .deleteBreakpointSettings()
          .setRetrySettings(deleteBreakpointRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteBreakpoint from properties.");
      }
    }
    Retry listBreakpointsRetry = clientProperties.getListBreakpointsRetry();
    if (listBreakpointsRetry != null) {
      RetrySettings listBreakpointsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listBreakpointsSettings().getRetrySettings(),
              listBreakpointsRetry);
      clientSettingsBuilder
          .listBreakpointsSettings()
          .setRetrySettings(listBreakpointsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listBreakpoints from properties.");
      }
    }
    Retry listDebuggeesRetry = clientProperties.getListDebuggeesRetry();
    if (listDebuggeesRetry != null) {
      RetrySettings listDebuggeesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listDebuggeesSettings().getRetrySettings(), listDebuggeesRetry);
      clientSettingsBuilder.listDebuggeesSettings().setRetrySettings(listDebuggeesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listDebuggees from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a Debugger2Client bean configured with Debugger2Settings.
   *
   * @param debugger2Settings settings to configure an instance of client bean.
   * @return a {@link Debugger2Client} bean configured with {@link Debugger2Settings}
   */
  @Bean
  @ConditionalOnMissingBean
  public Debugger2Client debugger2Client(Debugger2Settings debugger2Settings) throws IOException {
    return Debugger2Client.create(debugger2Settings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-debugger2";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
