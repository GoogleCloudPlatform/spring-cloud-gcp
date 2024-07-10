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

package com.google.cloud.tpu.v2.spring;

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
import com.google.cloud.tpu.v2.TpuClient;
import com.google.cloud.tpu.v2.TpuSettings;
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
 * Auto-configuration for {@link TpuClient}.
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
@ConditionalOnClass(TpuClient.class)
@ConditionalOnProperty(value = "com.google.cloud.tpu.v2.tpu.enabled", matchIfMissing = true)
@EnableConfigurationProperties(TpuSpringProperties.class)
public class TpuSpringAutoConfiguration {
  private final TpuSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(TpuSpringAutoConfiguration.class);

  protected TpuSpringAutoConfiguration(
      TpuSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Tpu-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultTpuTransportChannelProvider")
  public TransportChannelProvider defaultTpuTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return TpuSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return TpuSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a TpuSettings bean configured to use a DefaultCredentialsProvider and the client
   * library's default transport channel provider (defaultTpuTransportChannelProvider()). It also
   * configures the quota project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in TpuSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link TpuSettings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public TpuSettings tpuSettings(
      @Qualifier("defaultTpuTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    TpuSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = TpuSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = TpuSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(TpuSettings.getDefaultEndpoint())
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
          TpuSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listNodesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listNodesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listNodesSettings().setRetrySettings(listNodesRetrySettings);

      RetrySettings getNodeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getNodeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getNodeSettings().setRetrySettings(getNodeRetrySettings);

      RetrySettings generateServiceIdentityRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateServiceIdentitySettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .generateServiceIdentitySettings()
          .setRetrySettings(generateServiceIdentityRetrySettings);

      RetrySettings listAcceleratorTypesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listAcceleratorTypesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listAcceleratorTypesSettings()
          .setRetrySettings(listAcceleratorTypesRetrySettings);

      RetrySettings getAcceleratorTypeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getAcceleratorTypeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getAcceleratorTypeSettings()
          .setRetrySettings(getAcceleratorTypeRetrySettings);

      RetrySettings listRuntimeVersionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRuntimeVersionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listRuntimeVersionsSettings()
          .setRetrySettings(listRuntimeVersionsRetrySettings);

      RetrySettings getRuntimeVersionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRuntimeVersionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getRuntimeVersionSettings()
          .setRetrySettings(getRuntimeVersionRetrySettings);

      RetrySettings getGuestAttributesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getGuestAttributesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getGuestAttributesSettings()
          .setRetrySettings(getGuestAttributesRetrySettings);

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
    Retry listNodesRetry = clientProperties.getListNodesRetry();
    if (listNodesRetry != null) {
      RetrySettings listNodesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listNodesSettings().getRetrySettings(), listNodesRetry);
      clientSettingsBuilder.listNodesSettings().setRetrySettings(listNodesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listNodes from properties.");
      }
    }
    Retry getNodeRetry = clientProperties.getGetNodeRetry();
    if (getNodeRetry != null) {
      RetrySettings getNodeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getNodeSettings().getRetrySettings(), getNodeRetry);
      clientSettingsBuilder.getNodeSettings().setRetrySettings(getNodeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getNode from properties.");
      }
    }
    Retry generateServiceIdentityRetry = clientProperties.getGenerateServiceIdentityRetry();
    if (generateServiceIdentityRetry != null) {
      RetrySettings generateServiceIdentityRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateServiceIdentitySettings().getRetrySettings(),
              generateServiceIdentityRetry);
      clientSettingsBuilder
          .generateServiceIdentitySettings()
          .setRetrySettings(generateServiceIdentityRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for generateServiceIdentity from properties.");
      }
    }
    Retry listAcceleratorTypesRetry = clientProperties.getListAcceleratorTypesRetry();
    if (listAcceleratorTypesRetry != null) {
      RetrySettings listAcceleratorTypesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listAcceleratorTypesSettings().getRetrySettings(),
              listAcceleratorTypesRetry);
      clientSettingsBuilder
          .listAcceleratorTypesSettings()
          .setRetrySettings(listAcceleratorTypesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listAcceleratorTypes from properties.");
      }
    }
    Retry getAcceleratorTypeRetry = clientProperties.getGetAcceleratorTypeRetry();
    if (getAcceleratorTypeRetry != null) {
      RetrySettings getAcceleratorTypeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getAcceleratorTypeSettings().getRetrySettings(),
              getAcceleratorTypeRetry);
      clientSettingsBuilder
          .getAcceleratorTypeSettings()
          .setRetrySettings(getAcceleratorTypeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getAcceleratorType from properties.");
      }
    }
    Retry listRuntimeVersionsRetry = clientProperties.getListRuntimeVersionsRetry();
    if (listRuntimeVersionsRetry != null) {
      RetrySettings listRuntimeVersionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRuntimeVersionsSettings().getRetrySettings(),
              listRuntimeVersionsRetry);
      clientSettingsBuilder
          .listRuntimeVersionsSettings()
          .setRetrySettings(listRuntimeVersionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listRuntimeVersions from properties.");
      }
    }
    Retry getRuntimeVersionRetry = clientProperties.getGetRuntimeVersionRetry();
    if (getRuntimeVersionRetry != null) {
      RetrySettings getRuntimeVersionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRuntimeVersionSettings().getRetrySettings(),
              getRuntimeVersionRetry);
      clientSettingsBuilder
          .getRuntimeVersionSettings()
          .setRetrySettings(getRuntimeVersionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getRuntimeVersion from properties.");
      }
    }
    Retry getGuestAttributesRetry = clientProperties.getGetGuestAttributesRetry();
    if (getGuestAttributesRetry != null) {
      RetrySettings getGuestAttributesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getGuestAttributesSettings().getRetrySettings(),
              getGuestAttributesRetry);
      clientSettingsBuilder
          .getGuestAttributesSettings()
          .setRetrySettings(getGuestAttributesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getGuestAttributes from properties.");
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
   * Provides a TpuClient bean configured with TpuSettings.
   *
   * @param tpuSettings settings to configure an instance of client bean.
   * @return a {@link TpuClient} bean configured with {@link TpuSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public TpuClient tpuClient(TpuSettings tpuSettings) throws IOException {
    return TpuClient.create(tpuSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-tpu";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
