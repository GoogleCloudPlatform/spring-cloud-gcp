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

package com.google.cloud.aiplatform.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.aiplatform.v1.ModelServiceClient;
import com.google.cloud.aiplatform.v1.ModelServiceSettings;
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
 * Auto-configuration for {@link ModelServiceClient}.
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
@ConditionalOnClass(ModelServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.aiplatform.v1.model-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ModelServiceSpringProperties.class)
public class ModelServiceSpringAutoConfiguration {
  private final ModelServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(ModelServiceSpringAutoConfiguration.class);

  protected ModelServiceSpringAutoConfiguration(
      ModelServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from ModelService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultModelServiceTransportChannelProvider")
  public TransportChannelProvider defaultModelServiceTransportChannelProvider() {
    return ModelServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ModelServiceSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultModelServiceTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ModelServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ModelServiceSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ModelServiceSettings modelServiceSettings(
      @Qualifier("defaultModelServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ModelServiceSettings.Builder clientSettingsBuilder = ModelServiceSettings.newBuilder();
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(ModelServiceSettings.getDefaultEndpoint())
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
          ModelServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings getModelRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getModelSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getModelSettings().setRetrySettings(getModelRetrySettings);

      RetrySettings listModelsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listModelsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listModelsSettings().setRetrySettings(listModelsRetrySettings);

      RetrySettings listModelVersionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listModelVersionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listModelVersionsSettings()
          .setRetrySettings(listModelVersionsRetrySettings);

      RetrySettings updateModelRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateModelSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateModelSettings().setRetrySettings(updateModelRetrySettings);

      RetrySettings mergeVersionAliasesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.mergeVersionAliasesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .mergeVersionAliasesSettings()
          .setRetrySettings(mergeVersionAliasesRetrySettings);

      RetrySettings importModelEvaluationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.importModelEvaluationSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .importModelEvaluationSettings()
          .setRetrySettings(importModelEvaluationRetrySettings);

      RetrySettings batchImportModelEvaluationSlicesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.batchImportModelEvaluationSlicesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .batchImportModelEvaluationSlicesSettings()
          .setRetrySettings(batchImportModelEvaluationSlicesRetrySettings);

      RetrySettings batchImportEvaluatedAnnotationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.batchImportEvaluatedAnnotationsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .batchImportEvaluatedAnnotationsSettings()
          .setRetrySettings(batchImportEvaluatedAnnotationsRetrySettings);

      RetrySettings getModelEvaluationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getModelEvaluationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getModelEvaluationSettings()
          .setRetrySettings(getModelEvaluationRetrySettings);

      RetrySettings listModelEvaluationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listModelEvaluationsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listModelEvaluationsSettings()
          .setRetrySettings(listModelEvaluationsRetrySettings);

      RetrySettings getModelEvaluationSliceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getModelEvaluationSliceSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getModelEvaluationSliceSettings()
          .setRetrySettings(getModelEvaluationSliceRetrySettings);

      RetrySettings listModelEvaluationSlicesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listModelEvaluationSlicesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listModelEvaluationSlicesSettings()
          .setRetrySettings(listModelEvaluationSlicesRetrySettings);

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
    Retry getModelRetry = clientProperties.getGetModelRetry();
    if (getModelRetry != null) {
      RetrySettings getModelRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getModelSettings().getRetrySettings(), getModelRetry);
      clientSettingsBuilder.getModelSettings().setRetrySettings(getModelRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getModel from properties.");
      }
    }
    Retry listModelsRetry = clientProperties.getListModelsRetry();
    if (listModelsRetry != null) {
      RetrySettings listModelsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listModelsSettings().getRetrySettings(), listModelsRetry);
      clientSettingsBuilder.listModelsSettings().setRetrySettings(listModelsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listModels from properties.");
      }
    }
    Retry listModelVersionsRetry = clientProperties.getListModelVersionsRetry();
    if (listModelVersionsRetry != null) {
      RetrySettings listModelVersionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listModelVersionsSettings().getRetrySettings(),
              listModelVersionsRetry);
      clientSettingsBuilder
          .listModelVersionsSettings()
          .setRetrySettings(listModelVersionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listModelVersions from properties.");
      }
    }
    Retry updateModelRetry = clientProperties.getUpdateModelRetry();
    if (updateModelRetry != null) {
      RetrySettings updateModelRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateModelSettings().getRetrySettings(), updateModelRetry);
      clientSettingsBuilder.updateModelSettings().setRetrySettings(updateModelRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateModel from properties.");
      }
    }
    Retry mergeVersionAliasesRetry = clientProperties.getMergeVersionAliasesRetry();
    if (mergeVersionAliasesRetry != null) {
      RetrySettings mergeVersionAliasesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.mergeVersionAliasesSettings().getRetrySettings(),
              mergeVersionAliasesRetry);
      clientSettingsBuilder
          .mergeVersionAliasesSettings()
          .setRetrySettings(mergeVersionAliasesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for mergeVersionAliases from properties.");
      }
    }
    Retry importModelEvaluationRetry = clientProperties.getImportModelEvaluationRetry();
    if (importModelEvaluationRetry != null) {
      RetrySettings importModelEvaluationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.importModelEvaluationSettings().getRetrySettings(),
              importModelEvaluationRetry);
      clientSettingsBuilder
          .importModelEvaluationSettings()
          .setRetrySettings(importModelEvaluationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for importModelEvaluation from properties.");
      }
    }
    Retry batchImportModelEvaluationSlicesRetry =
        clientProperties.getBatchImportModelEvaluationSlicesRetry();
    if (batchImportModelEvaluationSlicesRetry != null) {
      RetrySettings batchImportModelEvaluationSlicesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.batchImportModelEvaluationSlicesSettings().getRetrySettings(),
              batchImportModelEvaluationSlicesRetry);
      clientSettingsBuilder
          .batchImportModelEvaluationSlicesSettings()
          .setRetrySettings(batchImportModelEvaluationSlicesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for batchImportModelEvaluationSlices from properties.");
      }
    }
    Retry batchImportEvaluatedAnnotationsRetry =
        clientProperties.getBatchImportEvaluatedAnnotationsRetry();
    if (batchImportEvaluatedAnnotationsRetry != null) {
      RetrySettings batchImportEvaluatedAnnotationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.batchImportEvaluatedAnnotationsSettings().getRetrySettings(),
              batchImportEvaluatedAnnotationsRetry);
      clientSettingsBuilder
          .batchImportEvaluatedAnnotationsSettings()
          .setRetrySettings(batchImportEvaluatedAnnotationsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for batchImportEvaluatedAnnotations from properties.");
      }
    }
    Retry getModelEvaluationRetry = clientProperties.getGetModelEvaluationRetry();
    if (getModelEvaluationRetry != null) {
      RetrySettings getModelEvaluationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getModelEvaluationSettings().getRetrySettings(),
              getModelEvaluationRetry);
      clientSettingsBuilder
          .getModelEvaluationSettings()
          .setRetrySettings(getModelEvaluationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getModelEvaluation from properties.");
      }
    }
    Retry listModelEvaluationsRetry = clientProperties.getListModelEvaluationsRetry();
    if (listModelEvaluationsRetry != null) {
      RetrySettings listModelEvaluationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listModelEvaluationsSettings().getRetrySettings(),
              listModelEvaluationsRetry);
      clientSettingsBuilder
          .listModelEvaluationsSettings()
          .setRetrySettings(listModelEvaluationsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listModelEvaluations from properties.");
      }
    }
    Retry getModelEvaluationSliceRetry = clientProperties.getGetModelEvaluationSliceRetry();
    if (getModelEvaluationSliceRetry != null) {
      RetrySettings getModelEvaluationSliceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getModelEvaluationSliceSettings().getRetrySettings(),
              getModelEvaluationSliceRetry);
      clientSettingsBuilder
          .getModelEvaluationSliceSettings()
          .setRetrySettings(getModelEvaluationSliceRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getModelEvaluationSlice from properties.");
      }
    }
    Retry listModelEvaluationSlicesRetry = clientProperties.getListModelEvaluationSlicesRetry();
    if (listModelEvaluationSlicesRetry != null) {
      RetrySettings listModelEvaluationSlicesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listModelEvaluationSlicesSettings().getRetrySettings(),
              listModelEvaluationSlicesRetry);
      clientSettingsBuilder
          .listModelEvaluationSlicesSettings()
          .setRetrySettings(listModelEvaluationSlicesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listModelEvaluationSlices from properties.");
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
   * Provides a ModelServiceClient bean configured with ModelServiceSettings.
   *
   * @param modelServiceSettings settings to configure an instance of client bean.
   * @return a {@link ModelServiceClient} bean configured with {@link ModelServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ModelServiceClient modelServiceClient(ModelServiceSettings modelServiceSettings)
      throws IOException {
    return ModelServiceClient.create(modelServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-model-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
