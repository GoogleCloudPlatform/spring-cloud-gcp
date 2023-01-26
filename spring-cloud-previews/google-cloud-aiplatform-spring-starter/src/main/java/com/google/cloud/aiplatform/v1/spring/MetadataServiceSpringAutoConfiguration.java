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

package com.google.cloud.aiplatform.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.aiplatform.v1.MetadataServiceClient;
import com.google.cloud.aiplatform.v1.MetadataServiceSettings;
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
 * Auto-configuration for {@link MetadataServiceClient}.
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
@ConditionalOnClass(MetadataServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.aiplatform.v1.metadata-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(MetadataServiceSpringProperties.class)
public class MetadataServiceSpringAutoConfiguration {
  private final MetadataServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(MetadataServiceSpringAutoConfiguration.class);

  protected MetadataServiceSpringAutoConfiguration(
      MetadataServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from MetadataService-specific configuration");
      }
      this.credentialsProvider =
          ((CredentialsProvider) new DefaultCredentialsProvider(this.clientProperties));
    } else {
      this.credentialsProvider = credentialsProvider;
    }
  }

  /**
   * Provides a default transport channel provider bean. The default is gRPC and will default to it
   * unless the useRest option is provided to use HTTP transport instead
   *
   * @return a default transport channel provider.
   */
  @Bean
  @ConditionalOnMissingBean(name = "defaultMetadataServiceTransportChannelProvider")
  public TransportChannelProvider defaultMetadataServiceTransportChannelProvider() {
    return MetadataServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a MetadataServiceSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultMetadataServiceTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in MetadataServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link MetadataServiceSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public MetadataServiceSettings metadataServiceSettings(
      @Qualifier("defaultMetadataServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    MetadataServiceSettings.Builder clientSettingsBuilder = MetadataServiceSettings.newBuilder();
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
          MetadataServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings getMetadataStoreRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getMetadataStoreSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getMetadataStoreSettings()
          .setRetrySettings(getMetadataStoreRetrySettings);

      RetrySettings listMetadataStoresRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listMetadataStoresSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listMetadataStoresSettings()
          .setRetrySettings(listMetadataStoresRetrySettings);

      RetrySettings createArtifactRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createArtifactSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createArtifactSettings().setRetrySettings(createArtifactRetrySettings);

      RetrySettings getArtifactRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getArtifactSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getArtifactSettings().setRetrySettings(getArtifactRetrySettings);

      RetrySettings listArtifactsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listArtifactsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listArtifactsSettings().setRetrySettings(listArtifactsRetrySettings);

      RetrySettings updateArtifactRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateArtifactSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateArtifactSettings().setRetrySettings(updateArtifactRetrySettings);

      RetrySettings createContextRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createContextSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createContextSettings().setRetrySettings(createContextRetrySettings);

      RetrySettings getContextRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getContextSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getContextSettings().setRetrySettings(getContextRetrySettings);

      RetrySettings listContextsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listContextsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listContextsSettings().setRetrySettings(listContextsRetrySettings);

      RetrySettings updateContextRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateContextSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateContextSettings().setRetrySettings(updateContextRetrySettings);

      RetrySettings addContextArtifactsAndExecutionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.addContextArtifactsAndExecutionsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .addContextArtifactsAndExecutionsSettings()
          .setRetrySettings(addContextArtifactsAndExecutionsRetrySettings);

      RetrySettings addContextChildrenRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.addContextChildrenSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .addContextChildrenSettings()
          .setRetrySettings(addContextChildrenRetrySettings);

      RetrySettings removeContextChildrenRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.removeContextChildrenSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .removeContextChildrenSettings()
          .setRetrySettings(removeContextChildrenRetrySettings);

      RetrySettings queryContextLineageSubgraphRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.queryContextLineageSubgraphSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .queryContextLineageSubgraphSettings()
          .setRetrySettings(queryContextLineageSubgraphRetrySettings);

      RetrySettings createExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createExecutionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createExecutionSettings()
          .setRetrySettings(createExecutionRetrySettings);

      RetrySettings getExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getExecutionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getExecutionSettings().setRetrySettings(getExecutionRetrySettings);

      RetrySettings listExecutionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listExecutionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listExecutionsSettings().setRetrySettings(listExecutionsRetrySettings);

      RetrySettings updateExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateExecutionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateExecutionSettings()
          .setRetrySettings(updateExecutionRetrySettings);

      RetrySettings addExecutionEventsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.addExecutionEventsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .addExecutionEventsSettings()
          .setRetrySettings(addExecutionEventsRetrySettings);

      RetrySettings queryExecutionInputsAndOutputsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.queryExecutionInputsAndOutputsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .queryExecutionInputsAndOutputsSettings()
          .setRetrySettings(queryExecutionInputsAndOutputsRetrySettings);

      RetrySettings createMetadataSchemaRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createMetadataSchemaSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .createMetadataSchemaSettings()
          .setRetrySettings(createMetadataSchemaRetrySettings);

      RetrySettings getMetadataSchemaRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getMetadataSchemaSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getMetadataSchemaSettings()
          .setRetrySettings(getMetadataSchemaRetrySettings);

      RetrySettings listMetadataSchemasRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listMetadataSchemasSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listMetadataSchemasSettings()
          .setRetrySettings(listMetadataSchemasRetrySettings);

      RetrySettings queryArtifactLineageSubgraphRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.queryArtifactLineageSubgraphSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .queryArtifactLineageSubgraphSettings()
          .setRetrySettings(queryArtifactLineageSubgraphRetrySettings);

      RetrySettings listLocationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listLocationsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listLocationsSettings().setRetrySettings(listLocationsRetrySettings);

      RetrySettings getLocationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getLocationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getLocationSettings().setRetrySettings(getLocationRetrySettings);

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

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry getMetadataStoreRetry = clientProperties.getGetMetadataStoreRetry();
    if (getMetadataStoreRetry != null) {
      RetrySettings getMetadataStoreRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getMetadataStoreSettings().getRetrySettings(),
              getMetadataStoreRetry);
      clientSettingsBuilder
          .getMetadataStoreSettings()
          .setRetrySettings(getMetadataStoreRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getMetadataStore from properties.");
      }
    }
    Retry listMetadataStoresRetry = clientProperties.getListMetadataStoresRetry();
    if (listMetadataStoresRetry != null) {
      RetrySettings listMetadataStoresRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listMetadataStoresSettings().getRetrySettings(),
              listMetadataStoresRetry);
      clientSettingsBuilder
          .listMetadataStoresSettings()
          .setRetrySettings(listMetadataStoresRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listMetadataStores from properties.");
      }
    }
    Retry createArtifactRetry = clientProperties.getCreateArtifactRetry();
    if (createArtifactRetry != null) {
      RetrySettings createArtifactRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createArtifactSettings().getRetrySettings(),
              createArtifactRetry);
      clientSettingsBuilder.createArtifactSettings().setRetrySettings(createArtifactRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createArtifact from properties.");
      }
    }
    Retry getArtifactRetry = clientProperties.getGetArtifactRetry();
    if (getArtifactRetry != null) {
      RetrySettings getArtifactRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getArtifactSettings().getRetrySettings(), getArtifactRetry);
      clientSettingsBuilder.getArtifactSettings().setRetrySettings(getArtifactRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getArtifact from properties.");
      }
    }
    Retry listArtifactsRetry = clientProperties.getListArtifactsRetry();
    if (listArtifactsRetry != null) {
      RetrySettings listArtifactsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listArtifactsSettings().getRetrySettings(), listArtifactsRetry);
      clientSettingsBuilder.listArtifactsSettings().setRetrySettings(listArtifactsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listArtifacts from properties.");
      }
    }
    Retry updateArtifactRetry = clientProperties.getUpdateArtifactRetry();
    if (updateArtifactRetry != null) {
      RetrySettings updateArtifactRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateArtifactSettings().getRetrySettings(),
              updateArtifactRetry);
      clientSettingsBuilder.updateArtifactSettings().setRetrySettings(updateArtifactRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateArtifact from properties.");
      }
    }
    Retry createContextRetry = clientProperties.getCreateContextRetry();
    if (createContextRetry != null) {
      RetrySettings createContextRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createContextSettings().getRetrySettings(), createContextRetry);
      clientSettingsBuilder.createContextSettings().setRetrySettings(createContextRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createContext from properties.");
      }
    }
    Retry getContextRetry = clientProperties.getGetContextRetry();
    if (getContextRetry != null) {
      RetrySettings getContextRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getContextSettings().getRetrySettings(), getContextRetry);
      clientSettingsBuilder.getContextSettings().setRetrySettings(getContextRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getContext from properties.");
      }
    }
    Retry listContextsRetry = clientProperties.getListContextsRetry();
    if (listContextsRetry != null) {
      RetrySettings listContextsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listContextsSettings().getRetrySettings(), listContextsRetry);
      clientSettingsBuilder.listContextsSettings().setRetrySettings(listContextsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listContexts from properties.");
      }
    }
    Retry updateContextRetry = clientProperties.getUpdateContextRetry();
    if (updateContextRetry != null) {
      RetrySettings updateContextRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateContextSettings().getRetrySettings(), updateContextRetry);
      clientSettingsBuilder.updateContextSettings().setRetrySettings(updateContextRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateContext from properties.");
      }
    }
    Retry addContextArtifactsAndExecutionsRetry =
        clientProperties.getAddContextArtifactsAndExecutionsRetry();
    if (addContextArtifactsAndExecutionsRetry != null) {
      RetrySettings addContextArtifactsAndExecutionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.addContextArtifactsAndExecutionsSettings().getRetrySettings(),
              addContextArtifactsAndExecutionsRetry);
      clientSettingsBuilder
          .addContextArtifactsAndExecutionsSettings()
          .setRetrySettings(addContextArtifactsAndExecutionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for addContextArtifactsAndExecutions from properties.");
      }
    }
    Retry addContextChildrenRetry = clientProperties.getAddContextChildrenRetry();
    if (addContextChildrenRetry != null) {
      RetrySettings addContextChildrenRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.addContextChildrenSettings().getRetrySettings(),
              addContextChildrenRetry);
      clientSettingsBuilder
          .addContextChildrenSettings()
          .setRetrySettings(addContextChildrenRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for addContextChildren from properties.");
      }
    }
    Retry removeContextChildrenRetry = clientProperties.getRemoveContextChildrenRetry();
    if (removeContextChildrenRetry != null) {
      RetrySettings removeContextChildrenRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.removeContextChildrenSettings().getRetrySettings(),
              removeContextChildrenRetry);
      clientSettingsBuilder
          .removeContextChildrenSettings()
          .setRetrySettings(removeContextChildrenRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for removeContextChildren from properties.");
      }
    }
    Retry queryContextLineageSubgraphRetry = clientProperties.getQueryContextLineageSubgraphRetry();
    if (queryContextLineageSubgraphRetry != null) {
      RetrySettings queryContextLineageSubgraphRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.queryContextLineageSubgraphSettings().getRetrySettings(),
              queryContextLineageSubgraphRetry);
      clientSettingsBuilder
          .queryContextLineageSubgraphSettings()
          .setRetrySettings(queryContextLineageSubgraphRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for queryContextLineageSubgraph from properties.");
      }
    }
    Retry createExecutionRetry = clientProperties.getCreateExecutionRetry();
    if (createExecutionRetry != null) {
      RetrySettings createExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createExecutionSettings().getRetrySettings(),
              createExecutionRetry);
      clientSettingsBuilder
          .createExecutionSettings()
          .setRetrySettings(createExecutionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createExecution from properties.");
      }
    }
    Retry getExecutionRetry = clientProperties.getGetExecutionRetry();
    if (getExecutionRetry != null) {
      RetrySettings getExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getExecutionSettings().getRetrySettings(), getExecutionRetry);
      clientSettingsBuilder.getExecutionSettings().setRetrySettings(getExecutionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getExecution from properties.");
      }
    }
    Retry listExecutionsRetry = clientProperties.getListExecutionsRetry();
    if (listExecutionsRetry != null) {
      RetrySettings listExecutionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listExecutionsSettings().getRetrySettings(),
              listExecutionsRetry);
      clientSettingsBuilder.listExecutionsSettings().setRetrySettings(listExecutionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listExecutions from properties.");
      }
    }
    Retry updateExecutionRetry = clientProperties.getUpdateExecutionRetry();
    if (updateExecutionRetry != null) {
      RetrySettings updateExecutionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateExecutionSettings().getRetrySettings(),
              updateExecutionRetry);
      clientSettingsBuilder
          .updateExecutionSettings()
          .setRetrySettings(updateExecutionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateExecution from properties.");
      }
    }
    Retry addExecutionEventsRetry = clientProperties.getAddExecutionEventsRetry();
    if (addExecutionEventsRetry != null) {
      RetrySettings addExecutionEventsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.addExecutionEventsSettings().getRetrySettings(),
              addExecutionEventsRetry);
      clientSettingsBuilder
          .addExecutionEventsSettings()
          .setRetrySettings(addExecutionEventsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for addExecutionEvents from properties.");
      }
    }
    Retry queryExecutionInputsAndOutputsRetry =
        clientProperties.getQueryExecutionInputsAndOutputsRetry();
    if (queryExecutionInputsAndOutputsRetry != null) {
      RetrySettings queryExecutionInputsAndOutputsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.queryExecutionInputsAndOutputsSettings().getRetrySettings(),
              queryExecutionInputsAndOutputsRetry);
      clientSettingsBuilder
          .queryExecutionInputsAndOutputsSettings()
          .setRetrySettings(queryExecutionInputsAndOutputsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for queryExecutionInputsAndOutputs from properties.");
      }
    }
    Retry createMetadataSchemaRetry = clientProperties.getCreateMetadataSchemaRetry();
    if (createMetadataSchemaRetry != null) {
      RetrySettings createMetadataSchemaRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createMetadataSchemaSettings().getRetrySettings(),
              createMetadataSchemaRetry);
      clientSettingsBuilder
          .createMetadataSchemaSettings()
          .setRetrySettings(createMetadataSchemaRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createMetadataSchema from properties.");
      }
    }
    Retry getMetadataSchemaRetry = clientProperties.getGetMetadataSchemaRetry();
    if (getMetadataSchemaRetry != null) {
      RetrySettings getMetadataSchemaRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getMetadataSchemaSettings().getRetrySettings(),
              getMetadataSchemaRetry);
      clientSettingsBuilder
          .getMetadataSchemaSettings()
          .setRetrySettings(getMetadataSchemaRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getMetadataSchema from properties.");
      }
    }
    Retry listMetadataSchemasRetry = clientProperties.getListMetadataSchemasRetry();
    if (listMetadataSchemasRetry != null) {
      RetrySettings listMetadataSchemasRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listMetadataSchemasSettings().getRetrySettings(),
              listMetadataSchemasRetry);
      clientSettingsBuilder
          .listMetadataSchemasSettings()
          .setRetrySettings(listMetadataSchemasRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listMetadataSchemas from properties.");
      }
    }
    Retry queryArtifactLineageSubgraphRetry =
        clientProperties.getQueryArtifactLineageSubgraphRetry();
    if (queryArtifactLineageSubgraphRetry != null) {
      RetrySettings queryArtifactLineageSubgraphRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.queryArtifactLineageSubgraphSettings().getRetrySettings(),
              queryArtifactLineageSubgraphRetry);
      clientSettingsBuilder
          .queryArtifactLineageSubgraphSettings()
          .setRetrySettings(queryArtifactLineageSubgraphRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for queryArtifactLineageSubgraph from properties.");
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
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a MetadataServiceClient bean configured with MetadataServiceSettings.
   *
   * @param metadataServiceSettings settings to configure an instance of client bean.
   * @return a {@link MetadataServiceClient} bean configured with {@link MetadataServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public MetadataServiceClient metadataServiceClient(
      MetadataServiceSettings metadataServiceSettings) throws IOException {
    return MetadataServiceClient.create(metadataServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-metadata-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
