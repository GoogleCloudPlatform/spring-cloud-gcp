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

package com.google.devtools.cloudprofiler.v2.spring;

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
import com.google.devtools.cloudprofiler.v2.ProfilerServiceClient;
import com.google.devtools.cloudprofiler.v2.ProfilerServiceSettings;
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
 * Auto-configuration for {@link ProfilerServiceClient}.
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
@ConditionalOnClass(ProfilerServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.devtools.cloudprofiler.v2.profiler-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ProfilerServiceSpringProperties.class)
public class ProfilerServiceSpringAutoConfiguration {
  private final ProfilerServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(ProfilerServiceSpringAutoConfiguration.class);

  protected ProfilerServiceSpringAutoConfiguration(
      ProfilerServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from ProfilerService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultProfilerServiceTransportChannelProvider")
  public TransportChannelProvider defaultProfilerServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return ProfilerServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return ProfilerServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ProfilerServiceSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultProfilerServiceTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ProfilerServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ProfilerServiceSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ProfilerServiceSettings profilerServiceSettings(
      @Qualifier("defaultProfilerServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ProfilerServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = ProfilerServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = ProfilerServiceSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(ProfilerServiceSettings.getDefaultEndpoint())
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
          ProfilerServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings createProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createProfileSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createProfileSettings().setRetrySettings(createProfileRetrySettings);

      RetrySettings createOfflineProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createOfflineProfileSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .createOfflineProfileSettings()
          .setRetrySettings(createOfflineProfileRetrySettings);

      RetrySettings updateProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateProfileSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateProfileSettings().setRetrySettings(updateProfileRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry createProfileRetry = clientProperties.getCreateProfileRetry();
    if (createProfileRetry != null) {
      RetrySettings createProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createProfileSettings().getRetrySettings(), createProfileRetry);
      clientSettingsBuilder.createProfileSettings().setRetrySettings(createProfileRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createProfile from properties.");
      }
    }
    Retry createOfflineProfileRetry = clientProperties.getCreateOfflineProfileRetry();
    if (createOfflineProfileRetry != null) {
      RetrySettings createOfflineProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createOfflineProfileSettings().getRetrySettings(),
              createOfflineProfileRetry);
      clientSettingsBuilder
          .createOfflineProfileSettings()
          .setRetrySettings(createOfflineProfileRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createOfflineProfile from properties.");
      }
    }
    Retry updateProfileRetry = clientProperties.getUpdateProfileRetry();
    if (updateProfileRetry != null) {
      RetrySettings updateProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateProfileSettings().getRetrySettings(), updateProfileRetry);
      clientSettingsBuilder.updateProfileSettings().setRetrySettings(updateProfileRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateProfile from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a ProfilerServiceClient bean configured with ProfilerServiceSettings.
   *
   * @param profilerServiceSettings settings to configure an instance of client bean.
   * @return a {@link ProfilerServiceClient} bean configured with {@link ProfilerServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ProfilerServiceClient profilerServiceClient(
      ProfilerServiceSettings profilerServiceSettings) throws IOException {
    return ProfilerServiceClient.create(profilerServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-profiler-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
