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

package com.google.cloud.container.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.container.v1.ClusterManagerClient;
import com.google.cloud.container.v1.ClusterManagerSettings;
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
 * Auto-configuration for {@link ClusterManagerClient}.
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
@ConditionalOnClass(ClusterManagerClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.container.v1.cluster-manager.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ClusterManagerSpringProperties.class)
public class ClusterManagerSpringAutoConfiguration {
  private final ClusterManagerSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(ClusterManagerSpringAutoConfiguration.class);

  protected ClusterManagerSpringAutoConfiguration(
      ClusterManagerSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from ClusterManager-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultClusterManagerTransportChannelProvider")
  public TransportChannelProvider defaultClusterManagerTransportChannelProvider() {
    return ClusterManagerSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ClusterManagerSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultClusterManagerTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ClusterManagerSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ClusterManagerSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ClusterManagerSettings clusterManagerSettings(
      @Qualifier("defaultClusterManagerTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ClusterManagerSettings.Builder clientSettingsBuilder = ClusterManagerSettings.newBuilder();
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
          ClusterManagerSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listClustersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listClustersSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listClustersSettings().setRetrySettings(listClustersRetrySettings);

      RetrySettings getClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getClusterSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getClusterSettings().setRetrySettings(getClusterRetrySettings);

      RetrySettings createClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createClusterSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createClusterSettings().setRetrySettings(createClusterRetrySettings);

      RetrySettings updateClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateClusterSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateClusterSettings().setRetrySettings(updateClusterRetrySettings);

      RetrySettings updateNodePoolRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateNodePoolSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateNodePoolSettings().setRetrySettings(updateNodePoolRetrySettings);

      RetrySettings setNodePoolAutoscalingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setNodePoolAutoscalingSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .setNodePoolAutoscalingSettings()
          .setRetrySettings(setNodePoolAutoscalingRetrySettings);

      RetrySettings setLoggingServiceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setLoggingServiceSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .setLoggingServiceSettings()
          .setRetrySettings(setLoggingServiceRetrySettings);

      RetrySettings setMonitoringServiceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setMonitoringServiceSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .setMonitoringServiceSettings()
          .setRetrySettings(setMonitoringServiceRetrySettings);

      RetrySettings setAddonsConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setAddonsConfigSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .setAddonsConfigSettings()
          .setRetrySettings(setAddonsConfigRetrySettings);

      RetrySettings setLocationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setLocationsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.setLocationsSettings().setRetrySettings(setLocationsRetrySettings);

      RetrySettings updateMasterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateMasterSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateMasterSettings().setRetrySettings(updateMasterRetrySettings);

      RetrySettings setMasterAuthRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setMasterAuthSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.setMasterAuthSettings().setRetrySettings(setMasterAuthRetrySettings);

      RetrySettings deleteClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteClusterSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteClusterSettings().setRetrySettings(deleteClusterRetrySettings);

      RetrySettings listOperationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listOperationsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listOperationsSettings().setRetrySettings(listOperationsRetrySettings);

      RetrySettings getOperationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getOperationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getOperationSettings().setRetrySettings(getOperationRetrySettings);

      RetrySettings cancelOperationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelOperationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .cancelOperationSettings()
          .setRetrySettings(cancelOperationRetrySettings);

      RetrySettings getServerConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getServerConfigSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getServerConfigSettings()
          .setRetrySettings(getServerConfigRetrySettings);

      RetrySettings getJSONWebKeysRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getJSONWebKeysSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getJSONWebKeysSettings().setRetrySettings(getJSONWebKeysRetrySettings);

      RetrySettings listNodePoolsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listNodePoolsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listNodePoolsSettings().setRetrySettings(listNodePoolsRetrySettings);

      RetrySettings getNodePoolRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getNodePoolSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getNodePoolSettings().setRetrySettings(getNodePoolRetrySettings);

      RetrySettings createNodePoolRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createNodePoolSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createNodePoolSettings().setRetrySettings(createNodePoolRetrySettings);

      RetrySettings deleteNodePoolRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteNodePoolSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteNodePoolSettings().setRetrySettings(deleteNodePoolRetrySettings);

      RetrySettings completeNodePoolUpgradeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.completeNodePoolUpgradeSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .completeNodePoolUpgradeSettings()
          .setRetrySettings(completeNodePoolUpgradeRetrySettings);

      RetrySettings rollbackNodePoolUpgradeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.rollbackNodePoolUpgradeSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .rollbackNodePoolUpgradeSettings()
          .setRetrySettings(rollbackNodePoolUpgradeRetrySettings);

      RetrySettings setNodePoolManagementRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setNodePoolManagementSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .setNodePoolManagementSettings()
          .setRetrySettings(setNodePoolManagementRetrySettings);

      RetrySettings setLabelsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setLabelsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.setLabelsSettings().setRetrySettings(setLabelsRetrySettings);

      RetrySettings setLegacyAbacRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setLegacyAbacSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.setLegacyAbacSettings().setRetrySettings(setLegacyAbacRetrySettings);

      RetrySettings startIPRotationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.startIPRotationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .startIPRotationSettings()
          .setRetrySettings(startIPRotationRetrySettings);

      RetrySettings completeIPRotationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.completeIPRotationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .completeIPRotationSettings()
          .setRetrySettings(completeIPRotationRetrySettings);

      RetrySettings setNodePoolSizeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setNodePoolSizeSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .setNodePoolSizeSettings()
          .setRetrySettings(setNodePoolSizeRetrySettings);

      RetrySettings setNetworkPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setNetworkPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .setNetworkPolicySettings()
          .setRetrySettings(setNetworkPolicyRetrySettings);

      RetrySettings setMaintenancePolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setMaintenancePolicySettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .setMaintenancePolicySettings()
          .setRetrySettings(setMaintenancePolicyRetrySettings);

      RetrySettings listUsableSubnetworksRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listUsableSubnetworksSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listUsableSubnetworksSettings()
          .setRetrySettings(listUsableSubnetworksRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listClustersRetry = clientProperties.getListClustersRetry();
    if (listClustersRetry != null) {
      RetrySettings listClustersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listClustersSettings().getRetrySettings(), listClustersRetry);
      clientSettingsBuilder.listClustersSettings().setRetrySettings(listClustersRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listClusters from properties.");
      }
    }
    Retry getClusterRetry = clientProperties.getGetClusterRetry();
    if (getClusterRetry != null) {
      RetrySettings getClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getClusterSettings().getRetrySettings(), getClusterRetry);
      clientSettingsBuilder.getClusterSettings().setRetrySettings(getClusterRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getCluster from properties.");
      }
    }
    Retry createClusterRetry = clientProperties.getCreateClusterRetry();
    if (createClusterRetry != null) {
      RetrySettings createClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createClusterSettings().getRetrySettings(), createClusterRetry);
      clientSettingsBuilder.createClusterSettings().setRetrySettings(createClusterRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createCluster from properties.");
      }
    }
    Retry updateClusterRetry = clientProperties.getUpdateClusterRetry();
    if (updateClusterRetry != null) {
      RetrySettings updateClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateClusterSettings().getRetrySettings(), updateClusterRetry);
      clientSettingsBuilder.updateClusterSettings().setRetrySettings(updateClusterRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateCluster from properties.");
      }
    }
    Retry updateNodePoolRetry = clientProperties.getUpdateNodePoolRetry();
    if (updateNodePoolRetry != null) {
      RetrySettings updateNodePoolRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateNodePoolSettings().getRetrySettings(),
              updateNodePoolRetry);
      clientSettingsBuilder.updateNodePoolSettings().setRetrySettings(updateNodePoolRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateNodePool from properties.");
      }
    }
    Retry setNodePoolAutoscalingRetry = clientProperties.getSetNodePoolAutoscalingRetry();
    if (setNodePoolAutoscalingRetry != null) {
      RetrySettings setNodePoolAutoscalingRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setNodePoolAutoscalingSettings().getRetrySettings(),
              setNodePoolAutoscalingRetry);
      clientSettingsBuilder
          .setNodePoolAutoscalingSettings()
          .setRetrySettings(setNodePoolAutoscalingRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for setNodePoolAutoscaling from properties.");
      }
    }
    Retry setLoggingServiceRetry = clientProperties.getSetLoggingServiceRetry();
    if (setLoggingServiceRetry != null) {
      RetrySettings setLoggingServiceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setLoggingServiceSettings().getRetrySettings(),
              setLoggingServiceRetry);
      clientSettingsBuilder
          .setLoggingServiceSettings()
          .setRetrySettings(setLoggingServiceRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for setLoggingService from properties.");
      }
    }
    Retry setMonitoringServiceRetry = clientProperties.getSetMonitoringServiceRetry();
    if (setMonitoringServiceRetry != null) {
      RetrySettings setMonitoringServiceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setMonitoringServiceSettings().getRetrySettings(),
              setMonitoringServiceRetry);
      clientSettingsBuilder
          .setMonitoringServiceSettings()
          .setRetrySettings(setMonitoringServiceRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for setMonitoringService from properties.");
      }
    }
    Retry setAddonsConfigRetry = clientProperties.getSetAddonsConfigRetry();
    if (setAddonsConfigRetry != null) {
      RetrySettings setAddonsConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setAddonsConfigSettings().getRetrySettings(),
              setAddonsConfigRetry);
      clientSettingsBuilder
          .setAddonsConfigSettings()
          .setRetrySettings(setAddonsConfigRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for setAddonsConfig from properties.");
      }
    }
    Retry setLocationsRetry = clientProperties.getSetLocationsRetry();
    if (setLocationsRetry != null) {
      RetrySettings setLocationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setLocationsSettings().getRetrySettings(), setLocationsRetry);
      clientSettingsBuilder.setLocationsSettings().setRetrySettings(setLocationsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for setLocations from properties.");
      }
    }
    Retry updateMasterRetry = clientProperties.getUpdateMasterRetry();
    if (updateMasterRetry != null) {
      RetrySettings updateMasterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateMasterSettings().getRetrySettings(), updateMasterRetry);
      clientSettingsBuilder.updateMasterSettings().setRetrySettings(updateMasterRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateMaster from properties.");
      }
    }
    Retry setMasterAuthRetry = clientProperties.getSetMasterAuthRetry();
    if (setMasterAuthRetry != null) {
      RetrySettings setMasterAuthRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setMasterAuthSettings().getRetrySettings(), setMasterAuthRetry);
      clientSettingsBuilder.setMasterAuthSettings().setRetrySettings(setMasterAuthRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for setMasterAuth from properties.");
      }
    }
    Retry deleteClusterRetry = clientProperties.getDeleteClusterRetry();
    if (deleteClusterRetry != null) {
      RetrySettings deleteClusterRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteClusterSettings().getRetrySettings(), deleteClusterRetry);
      clientSettingsBuilder.deleteClusterSettings().setRetrySettings(deleteClusterRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteCluster from properties.");
      }
    }
    Retry listOperationsRetry = clientProperties.getListOperationsRetry();
    if (listOperationsRetry != null) {
      RetrySettings listOperationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listOperationsSettings().getRetrySettings(),
              listOperationsRetry);
      clientSettingsBuilder.listOperationsSettings().setRetrySettings(listOperationsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listOperations from properties.");
      }
    }
    Retry getOperationRetry = clientProperties.getGetOperationRetry();
    if (getOperationRetry != null) {
      RetrySettings getOperationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getOperationSettings().getRetrySettings(), getOperationRetry);
      clientSettingsBuilder.getOperationSettings().setRetrySettings(getOperationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getOperation from properties.");
      }
    }
    Retry cancelOperationRetry = clientProperties.getCancelOperationRetry();
    if (cancelOperationRetry != null) {
      RetrySettings cancelOperationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelOperationSettings().getRetrySettings(),
              cancelOperationRetry);
      clientSettingsBuilder
          .cancelOperationSettings()
          .setRetrySettings(cancelOperationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for cancelOperation from properties.");
      }
    }
    Retry getServerConfigRetry = clientProperties.getGetServerConfigRetry();
    if (getServerConfigRetry != null) {
      RetrySettings getServerConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getServerConfigSettings().getRetrySettings(),
              getServerConfigRetry);
      clientSettingsBuilder
          .getServerConfigSettings()
          .setRetrySettings(getServerConfigRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getServerConfig from properties.");
      }
    }
    Retry getJSONWebKeysRetry = clientProperties.getGetJSONWebKeysRetry();
    if (getJSONWebKeysRetry != null) {
      RetrySettings getJSONWebKeysRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getJSONWebKeysSettings().getRetrySettings(),
              getJSONWebKeysRetry);
      clientSettingsBuilder.getJSONWebKeysSettings().setRetrySettings(getJSONWebKeysRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getJSONWebKeys from properties.");
      }
    }
    Retry listNodePoolsRetry = clientProperties.getListNodePoolsRetry();
    if (listNodePoolsRetry != null) {
      RetrySettings listNodePoolsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listNodePoolsSettings().getRetrySettings(), listNodePoolsRetry);
      clientSettingsBuilder.listNodePoolsSettings().setRetrySettings(listNodePoolsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listNodePools from properties.");
      }
    }
    Retry getNodePoolRetry = clientProperties.getGetNodePoolRetry();
    if (getNodePoolRetry != null) {
      RetrySettings getNodePoolRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getNodePoolSettings().getRetrySettings(), getNodePoolRetry);
      clientSettingsBuilder.getNodePoolSettings().setRetrySettings(getNodePoolRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getNodePool from properties.");
      }
    }
    Retry createNodePoolRetry = clientProperties.getCreateNodePoolRetry();
    if (createNodePoolRetry != null) {
      RetrySettings createNodePoolRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createNodePoolSettings().getRetrySettings(),
              createNodePoolRetry);
      clientSettingsBuilder.createNodePoolSettings().setRetrySettings(createNodePoolRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createNodePool from properties.");
      }
    }
    Retry deleteNodePoolRetry = clientProperties.getDeleteNodePoolRetry();
    if (deleteNodePoolRetry != null) {
      RetrySettings deleteNodePoolRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteNodePoolSettings().getRetrySettings(),
              deleteNodePoolRetry);
      clientSettingsBuilder.deleteNodePoolSettings().setRetrySettings(deleteNodePoolRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteNodePool from properties.");
      }
    }
    Retry completeNodePoolUpgradeRetry = clientProperties.getCompleteNodePoolUpgradeRetry();
    if (completeNodePoolUpgradeRetry != null) {
      RetrySettings completeNodePoolUpgradeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.completeNodePoolUpgradeSettings().getRetrySettings(),
              completeNodePoolUpgradeRetry);
      clientSettingsBuilder
          .completeNodePoolUpgradeSettings()
          .setRetrySettings(completeNodePoolUpgradeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for completeNodePoolUpgrade from properties.");
      }
    }
    Retry rollbackNodePoolUpgradeRetry = clientProperties.getRollbackNodePoolUpgradeRetry();
    if (rollbackNodePoolUpgradeRetry != null) {
      RetrySettings rollbackNodePoolUpgradeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.rollbackNodePoolUpgradeSettings().getRetrySettings(),
              rollbackNodePoolUpgradeRetry);
      clientSettingsBuilder
          .rollbackNodePoolUpgradeSettings()
          .setRetrySettings(rollbackNodePoolUpgradeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for rollbackNodePoolUpgrade from properties.");
      }
    }
    Retry setNodePoolManagementRetry = clientProperties.getSetNodePoolManagementRetry();
    if (setNodePoolManagementRetry != null) {
      RetrySettings setNodePoolManagementRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setNodePoolManagementSettings().getRetrySettings(),
              setNodePoolManagementRetry);
      clientSettingsBuilder
          .setNodePoolManagementSettings()
          .setRetrySettings(setNodePoolManagementRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for setNodePoolManagement from properties.");
      }
    }
    Retry setLabelsRetry = clientProperties.getSetLabelsRetry();
    if (setLabelsRetry != null) {
      RetrySettings setLabelsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setLabelsSettings().getRetrySettings(), setLabelsRetry);
      clientSettingsBuilder.setLabelsSettings().setRetrySettings(setLabelsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for setLabels from properties.");
      }
    }
    Retry setLegacyAbacRetry = clientProperties.getSetLegacyAbacRetry();
    if (setLegacyAbacRetry != null) {
      RetrySettings setLegacyAbacRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setLegacyAbacSettings().getRetrySettings(), setLegacyAbacRetry);
      clientSettingsBuilder.setLegacyAbacSettings().setRetrySettings(setLegacyAbacRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for setLegacyAbac from properties.");
      }
    }
    Retry startIPRotationRetry = clientProperties.getStartIPRotationRetry();
    if (startIPRotationRetry != null) {
      RetrySettings startIPRotationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.startIPRotationSettings().getRetrySettings(),
              startIPRotationRetry);
      clientSettingsBuilder
          .startIPRotationSettings()
          .setRetrySettings(startIPRotationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for startIPRotation from properties.");
      }
    }
    Retry completeIPRotationRetry = clientProperties.getCompleteIPRotationRetry();
    if (completeIPRotationRetry != null) {
      RetrySettings completeIPRotationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.completeIPRotationSettings().getRetrySettings(),
              completeIPRotationRetry);
      clientSettingsBuilder
          .completeIPRotationSettings()
          .setRetrySettings(completeIPRotationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for completeIPRotation from properties.");
      }
    }
    Retry setNodePoolSizeRetry = clientProperties.getSetNodePoolSizeRetry();
    if (setNodePoolSizeRetry != null) {
      RetrySettings setNodePoolSizeRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setNodePoolSizeSettings().getRetrySettings(),
              setNodePoolSizeRetry);
      clientSettingsBuilder
          .setNodePoolSizeSettings()
          .setRetrySettings(setNodePoolSizeRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for setNodePoolSize from properties.");
      }
    }
    Retry setNetworkPolicyRetry = clientProperties.getSetNetworkPolicyRetry();
    if (setNetworkPolicyRetry != null) {
      RetrySettings setNetworkPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setNetworkPolicySettings().getRetrySettings(),
              setNetworkPolicyRetry);
      clientSettingsBuilder
          .setNetworkPolicySettings()
          .setRetrySettings(setNetworkPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for setNetworkPolicy from properties.");
      }
    }
    Retry setMaintenancePolicyRetry = clientProperties.getSetMaintenancePolicyRetry();
    if (setMaintenancePolicyRetry != null) {
      RetrySettings setMaintenancePolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setMaintenancePolicySettings().getRetrySettings(),
              setMaintenancePolicyRetry);
      clientSettingsBuilder
          .setMaintenancePolicySettings()
          .setRetrySettings(setMaintenancePolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for setMaintenancePolicy from properties.");
      }
    }
    Retry listUsableSubnetworksRetry = clientProperties.getListUsableSubnetworksRetry();
    if (listUsableSubnetworksRetry != null) {
      RetrySettings listUsableSubnetworksRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listUsableSubnetworksSettings().getRetrySettings(),
              listUsableSubnetworksRetry);
      clientSettingsBuilder
          .listUsableSubnetworksSettings()
          .setRetrySettings(listUsableSubnetworksRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listUsableSubnetworks from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a ClusterManagerClient bean configured with ClusterManagerSettings.
   *
   * @param clusterManagerSettings settings to configure an instance of client bean.
   * @return a {@link ClusterManagerClient} bean configured with {@link ClusterManagerSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ClusterManagerClient clusterManagerClient(ClusterManagerSettings clusterManagerSettings)
      throws IOException {
    return ClusterManagerClient.create(clusterManagerSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-cluster-manager";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
