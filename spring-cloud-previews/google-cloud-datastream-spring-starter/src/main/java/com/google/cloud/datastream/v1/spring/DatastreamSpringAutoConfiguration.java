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

package com.google.cloud.datastream.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.datastream.v1.DatastreamClient;
import com.google.cloud.datastream.v1.DatastreamSettings;
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
 * Auto-configuration for {@link DatastreamClient}.
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
@ConditionalOnClass(DatastreamClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.datastream.v1.datastream.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(DatastreamSpringProperties.class)
public class DatastreamSpringAutoConfiguration {
  private final DatastreamSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(DatastreamSpringAutoConfiguration.class);

  protected DatastreamSpringAutoConfiguration(
      DatastreamSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Datastream-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultDatastreamTransportChannelProvider")
  public TransportChannelProvider defaultDatastreamTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return DatastreamSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return DatastreamSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a DatastreamSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultDatastreamTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in DatastreamSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link DatastreamSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public DatastreamSettings datastreamSettings(
      @Qualifier("defaultDatastreamTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    DatastreamSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = DatastreamSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = DatastreamSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(DatastreamSettings.getDefaultEndpoint())
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
          DatastreamSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listConnectionProfilesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConnectionProfilesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listConnectionProfilesSettings()
          .setRetrySettings(listConnectionProfilesRetrySettings);

      RetrySettings getConnectionProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConnectionProfileSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getConnectionProfileSettings()
          .setRetrySettings(getConnectionProfileRetrySettings);

      RetrySettings discoverConnectionProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.discoverConnectionProfileSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .discoverConnectionProfileSettings()
          .setRetrySettings(discoverConnectionProfileRetrySettings);

      RetrySettings listStreamsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listStreamsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listStreamsSettings().setRetrySettings(listStreamsRetrySettings);

      RetrySettings getStreamRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getStreamSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getStreamSettings().setRetrySettings(getStreamRetrySettings);

      RetrySettings getStreamObjectRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getStreamObjectSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getStreamObjectSettings()
          .setRetrySettings(getStreamObjectRetrySettings);

      RetrySettings lookupStreamObjectRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.lookupStreamObjectSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .lookupStreamObjectSettings()
          .setRetrySettings(lookupStreamObjectRetrySettings);

      RetrySettings listStreamObjectsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listStreamObjectsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listStreamObjectsSettings()
          .setRetrySettings(listStreamObjectsRetrySettings);

      RetrySettings startBackfillJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.startBackfillJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .startBackfillJobSettings()
          .setRetrySettings(startBackfillJobRetrySettings);

      RetrySettings stopBackfillJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.stopBackfillJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .stopBackfillJobSettings()
          .setRetrySettings(stopBackfillJobRetrySettings);

      RetrySettings fetchStaticIpsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.fetchStaticIpsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.fetchStaticIpsSettings().setRetrySettings(fetchStaticIpsRetrySettings);

      RetrySettings getPrivateConnectionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPrivateConnectionSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getPrivateConnectionSettings()
          .setRetrySettings(getPrivateConnectionRetrySettings);

      RetrySettings listPrivateConnectionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPrivateConnectionsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listPrivateConnectionsSettings()
          .setRetrySettings(listPrivateConnectionsRetrySettings);

      RetrySettings getRouteRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRouteSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getRouteSettings().setRetrySettings(getRouteRetrySettings);

      RetrySettings listRoutesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRoutesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listRoutesSettings().setRetrySettings(listRoutesRetrySettings);

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
    Retry listConnectionProfilesRetry = clientProperties.getListConnectionProfilesRetry();
    if (listConnectionProfilesRetry != null) {
      RetrySettings listConnectionProfilesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConnectionProfilesSettings().getRetrySettings(),
              listConnectionProfilesRetry);
      clientSettingsBuilder
          .listConnectionProfilesSettings()
          .setRetrySettings(listConnectionProfilesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listConnectionProfiles from properties.");
      }
    }
    Retry getConnectionProfileRetry = clientProperties.getGetConnectionProfileRetry();
    if (getConnectionProfileRetry != null) {
      RetrySettings getConnectionProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConnectionProfileSettings().getRetrySettings(),
              getConnectionProfileRetry);
      clientSettingsBuilder
          .getConnectionProfileSettings()
          .setRetrySettings(getConnectionProfileRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getConnectionProfile from properties.");
      }
    }
    Retry discoverConnectionProfileRetry = clientProperties.getDiscoverConnectionProfileRetry();
    if (discoverConnectionProfileRetry != null) {
      RetrySettings discoverConnectionProfileRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.discoverConnectionProfileSettings().getRetrySettings(),
              discoverConnectionProfileRetry);
      clientSettingsBuilder
          .discoverConnectionProfileSettings()
          .setRetrySettings(discoverConnectionProfileRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for discoverConnectionProfile from properties.");
      }
    }
    Retry listStreamsRetry = clientProperties.getListStreamsRetry();
    if (listStreamsRetry != null) {
      RetrySettings listStreamsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listStreamsSettings().getRetrySettings(), listStreamsRetry);
      clientSettingsBuilder.listStreamsSettings().setRetrySettings(listStreamsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listStreams from properties.");
      }
    }
    Retry getStreamRetry = clientProperties.getGetStreamRetry();
    if (getStreamRetry != null) {
      RetrySettings getStreamRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getStreamSettings().getRetrySettings(), getStreamRetry);
      clientSettingsBuilder.getStreamSettings().setRetrySettings(getStreamRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getStream from properties.");
      }
    }
    Retry getStreamObjectRetry = clientProperties.getGetStreamObjectRetry();
    if (getStreamObjectRetry != null) {
      RetrySettings getStreamObjectRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getStreamObjectSettings().getRetrySettings(),
              getStreamObjectRetry);
      clientSettingsBuilder
          .getStreamObjectSettings()
          .setRetrySettings(getStreamObjectRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getStreamObject from properties.");
      }
    }
    Retry lookupStreamObjectRetry = clientProperties.getLookupStreamObjectRetry();
    if (lookupStreamObjectRetry != null) {
      RetrySettings lookupStreamObjectRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.lookupStreamObjectSettings().getRetrySettings(),
              lookupStreamObjectRetry);
      clientSettingsBuilder
          .lookupStreamObjectSettings()
          .setRetrySettings(lookupStreamObjectRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for lookupStreamObject from properties.");
      }
    }
    Retry listStreamObjectsRetry = clientProperties.getListStreamObjectsRetry();
    if (listStreamObjectsRetry != null) {
      RetrySettings listStreamObjectsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listStreamObjectsSettings().getRetrySettings(),
              listStreamObjectsRetry);
      clientSettingsBuilder
          .listStreamObjectsSettings()
          .setRetrySettings(listStreamObjectsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listStreamObjects from properties.");
      }
    }
    Retry startBackfillJobRetry = clientProperties.getStartBackfillJobRetry();
    if (startBackfillJobRetry != null) {
      RetrySettings startBackfillJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.startBackfillJobSettings().getRetrySettings(),
              startBackfillJobRetry);
      clientSettingsBuilder
          .startBackfillJobSettings()
          .setRetrySettings(startBackfillJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for startBackfillJob from properties.");
      }
    }
    Retry stopBackfillJobRetry = clientProperties.getStopBackfillJobRetry();
    if (stopBackfillJobRetry != null) {
      RetrySettings stopBackfillJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.stopBackfillJobSettings().getRetrySettings(),
              stopBackfillJobRetry);
      clientSettingsBuilder
          .stopBackfillJobSettings()
          .setRetrySettings(stopBackfillJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for stopBackfillJob from properties.");
      }
    }
    Retry fetchStaticIpsRetry = clientProperties.getFetchStaticIpsRetry();
    if (fetchStaticIpsRetry != null) {
      RetrySettings fetchStaticIpsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.fetchStaticIpsSettings().getRetrySettings(),
              fetchStaticIpsRetry);
      clientSettingsBuilder.fetchStaticIpsSettings().setRetrySettings(fetchStaticIpsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for fetchStaticIps from properties.");
      }
    }
    Retry getPrivateConnectionRetry = clientProperties.getGetPrivateConnectionRetry();
    if (getPrivateConnectionRetry != null) {
      RetrySettings getPrivateConnectionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPrivateConnectionSettings().getRetrySettings(),
              getPrivateConnectionRetry);
      clientSettingsBuilder
          .getPrivateConnectionSettings()
          .setRetrySettings(getPrivateConnectionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getPrivateConnection from properties.");
      }
    }
    Retry listPrivateConnectionsRetry = clientProperties.getListPrivateConnectionsRetry();
    if (listPrivateConnectionsRetry != null) {
      RetrySettings listPrivateConnectionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPrivateConnectionsSettings().getRetrySettings(),
              listPrivateConnectionsRetry);
      clientSettingsBuilder
          .listPrivateConnectionsSettings()
          .setRetrySettings(listPrivateConnectionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listPrivateConnections from properties.");
      }
    }
    Retry getRouteRetry = clientProperties.getGetRouteRetry();
    if (getRouteRetry != null) {
      RetrySettings getRouteRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getRouteSettings().getRetrySettings(), getRouteRetry);
      clientSettingsBuilder.getRouteSettings().setRetrySettings(getRouteRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getRoute from properties.");
      }
    }
    Retry listRoutesRetry = clientProperties.getListRoutesRetry();
    if (listRoutesRetry != null) {
      RetrySettings listRoutesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRoutesSettings().getRetrySettings(), listRoutesRetry);
      clientSettingsBuilder.listRoutesSettings().setRetrySettings(listRoutesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listRoutes from properties.");
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
   * Provides a DatastreamClient bean configured with DatastreamSettings.
   *
   * @param datastreamSettings settings to configure an instance of client bean.
   * @return a {@link DatastreamClient} bean configured with {@link DatastreamSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public DatastreamClient datastreamClient(DatastreamSettings datastreamSettings)
      throws IOException {
    return DatastreamClient.create(datastreamSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-datastream";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
