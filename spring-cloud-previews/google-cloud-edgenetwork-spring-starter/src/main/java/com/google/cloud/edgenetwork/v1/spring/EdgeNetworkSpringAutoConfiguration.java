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

package com.google.cloud.edgenetwork.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.edgenetwork.v1.EdgeNetworkClient;
import com.google.cloud.edgenetwork.v1.EdgeNetworkSettings;
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
 * Auto-configuration for {@link EdgeNetworkClient}.
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
@ConditionalOnClass(EdgeNetworkClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.edgenetwork.v1.edge-network.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(EdgeNetworkSpringProperties.class)
public class EdgeNetworkSpringAutoConfiguration {
  private final EdgeNetworkSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(EdgeNetworkSpringAutoConfiguration.class);

  protected EdgeNetworkSpringAutoConfiguration(
      EdgeNetworkSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from EdgeNetwork-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultEdgeNetworkTransportChannelProvider")
  public TransportChannelProvider defaultEdgeNetworkTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return EdgeNetworkSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return EdgeNetworkSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a EdgeNetworkSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultEdgeNetworkTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in EdgeNetworkSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link EdgeNetworkSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public EdgeNetworkSettings edgeNetworkSettings(
      @Qualifier("defaultEdgeNetworkTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    EdgeNetworkSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = EdgeNetworkSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = EdgeNetworkSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(EdgeNetworkSettings.getDefaultEndpoint())
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
          EdgeNetworkSettings.defaultExecutorProviderBuilder()
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
      RetrySettings initializeZoneRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.initializeZoneSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.initializeZoneSettings().setRetrySettings(initializeZoneRetrySettings);

      RetrySettings listZonesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listZonesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listZonesSettings().setRetrySettings(listZonesRetrySettings);

      RetrySettings getZoneRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getZoneSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getZoneSettings().setRetrySettings(getZoneRetrySettings);

      RetrySettings listNetworksRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listNetworksSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listNetworksSettings().setRetrySettings(listNetworksRetrySettings);

      RetrySettings getNetworkRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getNetworkSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getNetworkSettings().setRetrySettings(getNetworkRetrySettings);

      RetrySettings diagnoseNetworkRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.diagnoseNetworkSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .diagnoseNetworkSettings()
          .setRetrySettings(diagnoseNetworkRetrySettings);

      RetrySettings listSubnetsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSubnetsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listSubnetsSettings().setRetrySettings(listSubnetsRetrySettings);

      RetrySettings getSubnetRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSubnetSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getSubnetSettings().setRetrySettings(getSubnetRetrySettings);

      RetrySettings listInterconnectsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listInterconnectsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listInterconnectsSettings()
          .setRetrySettings(listInterconnectsRetrySettings);

      RetrySettings getInterconnectRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getInterconnectSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getInterconnectSettings()
          .setRetrySettings(getInterconnectRetrySettings);

      RetrySettings diagnoseInterconnectRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.diagnoseInterconnectSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .diagnoseInterconnectSettings()
          .setRetrySettings(diagnoseInterconnectRetrySettings);

      RetrySettings listInterconnectAttachmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listInterconnectAttachmentsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listInterconnectAttachmentsSettings()
          .setRetrySettings(listInterconnectAttachmentsRetrySettings);

      RetrySettings getInterconnectAttachmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getInterconnectAttachmentSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getInterconnectAttachmentSettings()
          .setRetrySettings(getInterconnectAttachmentRetrySettings);

      RetrySettings listRoutersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRoutersSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listRoutersSettings().setRetrySettings(listRoutersRetrySettings);

      RetrySettings getRouterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRouterSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getRouterSettings().setRetrySettings(getRouterRetrySettings);

      RetrySettings diagnoseRouterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.diagnoseRouterSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.diagnoseRouterSettings().setRetrySettings(diagnoseRouterRetrySettings);

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
    Retry initializeZoneRetry = clientProperties.getInitializeZoneRetry();
    if (initializeZoneRetry != null) {
      RetrySettings initializeZoneRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.initializeZoneSettings().getRetrySettings(),
              initializeZoneRetry);
      clientSettingsBuilder.initializeZoneSettings().setRetrySettings(initializeZoneRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for initializeZone from properties.");
      }
    }
    Retry listZonesRetry = clientProperties.getListZonesRetry();
    if (listZonesRetry != null) {
      RetrySettings listZonesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listZonesSettings().getRetrySettings(), listZonesRetry);
      clientSettingsBuilder.listZonesSettings().setRetrySettings(listZonesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listZones from properties.");
      }
    }
    Retry getZoneRetry = clientProperties.getGetZoneRetry();
    if (getZoneRetry != null) {
      RetrySettings getZoneRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getZoneSettings().getRetrySettings(), getZoneRetry);
      clientSettingsBuilder.getZoneSettings().setRetrySettings(getZoneRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getZone from properties.");
      }
    }
    Retry listNetworksRetry = clientProperties.getListNetworksRetry();
    if (listNetworksRetry != null) {
      RetrySettings listNetworksRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listNetworksSettings().getRetrySettings(), listNetworksRetry);
      clientSettingsBuilder.listNetworksSettings().setRetrySettings(listNetworksRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listNetworks from properties.");
      }
    }
    Retry getNetworkRetry = clientProperties.getGetNetworkRetry();
    if (getNetworkRetry != null) {
      RetrySettings getNetworkRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getNetworkSettings().getRetrySettings(), getNetworkRetry);
      clientSettingsBuilder.getNetworkSettings().setRetrySettings(getNetworkRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getNetwork from properties.");
      }
    }
    Retry diagnoseNetworkRetry = clientProperties.getDiagnoseNetworkRetry();
    if (diagnoseNetworkRetry != null) {
      RetrySettings diagnoseNetworkRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.diagnoseNetworkSettings().getRetrySettings(),
              diagnoseNetworkRetry);
      clientSettingsBuilder
          .diagnoseNetworkSettings()
          .setRetrySettings(diagnoseNetworkRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for diagnoseNetwork from properties.");
      }
    }
    Retry listSubnetsRetry = clientProperties.getListSubnetsRetry();
    if (listSubnetsRetry != null) {
      RetrySettings listSubnetsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSubnetsSettings().getRetrySettings(), listSubnetsRetry);
      clientSettingsBuilder.listSubnetsSettings().setRetrySettings(listSubnetsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listSubnets from properties.");
      }
    }
    Retry getSubnetRetry = clientProperties.getGetSubnetRetry();
    if (getSubnetRetry != null) {
      RetrySettings getSubnetRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSubnetSettings().getRetrySettings(), getSubnetRetry);
      clientSettingsBuilder.getSubnetSettings().setRetrySettings(getSubnetRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getSubnet from properties.");
      }
    }
    Retry listInterconnectsRetry = clientProperties.getListInterconnectsRetry();
    if (listInterconnectsRetry != null) {
      RetrySettings listInterconnectsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listInterconnectsSettings().getRetrySettings(),
              listInterconnectsRetry);
      clientSettingsBuilder
          .listInterconnectsSettings()
          .setRetrySettings(listInterconnectsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listInterconnects from properties.");
      }
    }
    Retry getInterconnectRetry = clientProperties.getGetInterconnectRetry();
    if (getInterconnectRetry != null) {
      RetrySettings getInterconnectRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getInterconnectSettings().getRetrySettings(),
              getInterconnectRetry);
      clientSettingsBuilder
          .getInterconnectSettings()
          .setRetrySettings(getInterconnectRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getInterconnect from properties.");
      }
    }
    Retry diagnoseInterconnectRetry = clientProperties.getDiagnoseInterconnectRetry();
    if (diagnoseInterconnectRetry != null) {
      RetrySettings diagnoseInterconnectRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.diagnoseInterconnectSettings().getRetrySettings(),
              diagnoseInterconnectRetry);
      clientSettingsBuilder
          .diagnoseInterconnectSettings()
          .setRetrySettings(diagnoseInterconnectRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for diagnoseInterconnect from properties.");
      }
    }
    Retry listInterconnectAttachmentsRetry = clientProperties.getListInterconnectAttachmentsRetry();
    if (listInterconnectAttachmentsRetry != null) {
      RetrySettings listInterconnectAttachmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listInterconnectAttachmentsSettings().getRetrySettings(),
              listInterconnectAttachmentsRetry);
      clientSettingsBuilder
          .listInterconnectAttachmentsSettings()
          .setRetrySettings(listInterconnectAttachmentsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listInterconnectAttachments from properties.");
      }
    }
    Retry getInterconnectAttachmentRetry = clientProperties.getGetInterconnectAttachmentRetry();
    if (getInterconnectAttachmentRetry != null) {
      RetrySettings getInterconnectAttachmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getInterconnectAttachmentSettings().getRetrySettings(),
              getInterconnectAttachmentRetry);
      clientSettingsBuilder
          .getInterconnectAttachmentSettings()
          .setRetrySettings(getInterconnectAttachmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getInterconnectAttachment from properties.");
      }
    }
    Retry listRoutersRetry = clientProperties.getListRoutersRetry();
    if (listRoutersRetry != null) {
      RetrySettings listRoutersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRoutersSettings().getRetrySettings(), listRoutersRetry);
      clientSettingsBuilder.listRoutersSettings().setRetrySettings(listRoutersRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listRouters from properties.");
      }
    }
    Retry getRouterRetry = clientProperties.getGetRouterRetry();
    if (getRouterRetry != null) {
      RetrySettings getRouterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRouterSettings().getRetrySettings(), getRouterRetry);
      clientSettingsBuilder.getRouterSettings().setRetrySettings(getRouterRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getRouter from properties.");
      }
    }
    Retry diagnoseRouterRetry = clientProperties.getDiagnoseRouterRetry();
    if (diagnoseRouterRetry != null) {
      RetrySettings diagnoseRouterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.diagnoseRouterSettings().getRetrySettings(),
              diagnoseRouterRetry);
      clientSettingsBuilder.diagnoseRouterSettings().setRetrySettings(diagnoseRouterRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for diagnoseRouter from properties.");
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
   * Provides a EdgeNetworkClient bean configured with EdgeNetworkSettings.
   *
   * @param edgeNetworkSettings settings to configure an instance of client bean.
   * @return a {@link EdgeNetworkClient} bean configured with {@link EdgeNetworkSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public EdgeNetworkClient edgeNetworkClient(EdgeNetworkSettings edgeNetworkSettings)
      throws IOException {
    return EdgeNetworkClient.create(edgeNetworkSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-edge-network";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
