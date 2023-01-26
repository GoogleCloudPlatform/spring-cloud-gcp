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
import com.google.cloud.aiplatform.v1.JobServiceClient;
import com.google.cloud.aiplatform.v1.JobServiceSettings;
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
 * Auto-configuration for {@link JobServiceClient}.
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
@ConditionalOnClass(JobServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.aiplatform.v1.job-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(JobServiceSpringProperties.class)
public class JobServiceSpringAutoConfiguration {
  private final JobServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(JobServiceSpringAutoConfiguration.class);

  protected JobServiceSpringAutoConfiguration(
      JobServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from JobService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultJobServiceTransportChannelProvider")
  public TransportChannelProvider defaultJobServiceTransportChannelProvider() {
    return JobServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a JobServiceSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultJobServiceTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in JobServiceSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link JobServiceSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public JobServiceSettings jobServiceSettings(
      @Qualifier("defaultJobServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    JobServiceSettings.Builder clientSettingsBuilder = JobServiceSettings.newBuilder();
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
          JobServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings createCustomJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createCustomJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createCustomJobSettings()
          .setRetrySettings(createCustomJobRetrySettings);

      RetrySettings getCustomJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getCustomJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getCustomJobSettings().setRetrySettings(getCustomJobRetrySettings);

      RetrySettings listCustomJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listCustomJobsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listCustomJobsSettings().setRetrySettings(listCustomJobsRetrySettings);

      RetrySettings cancelCustomJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelCustomJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .cancelCustomJobSettings()
          .setRetrySettings(cancelCustomJobRetrySettings);

      RetrySettings createDataLabelingJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createDataLabelingJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .createDataLabelingJobSettings()
          .setRetrySettings(createDataLabelingJobRetrySettings);

      RetrySettings getDataLabelingJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getDataLabelingJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getDataLabelingJobSettings()
          .setRetrySettings(getDataLabelingJobRetrySettings);

      RetrySettings listDataLabelingJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listDataLabelingJobsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listDataLabelingJobsSettings()
          .setRetrySettings(listDataLabelingJobsRetrySettings);

      RetrySettings cancelDataLabelingJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelDataLabelingJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .cancelDataLabelingJobSettings()
          .setRetrySettings(cancelDataLabelingJobRetrySettings);

      RetrySettings createHyperparameterTuningJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createHyperparameterTuningJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .createHyperparameterTuningJobSettings()
          .setRetrySettings(createHyperparameterTuningJobRetrySettings);

      RetrySettings getHyperparameterTuningJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getHyperparameterTuningJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getHyperparameterTuningJobSettings()
          .setRetrySettings(getHyperparameterTuningJobRetrySettings);

      RetrySettings listHyperparameterTuningJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listHyperparameterTuningJobsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listHyperparameterTuningJobsSettings()
          .setRetrySettings(listHyperparameterTuningJobsRetrySettings);

      RetrySettings cancelHyperparameterTuningJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelHyperparameterTuningJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .cancelHyperparameterTuningJobSettings()
          .setRetrySettings(cancelHyperparameterTuningJobRetrySettings);

      RetrySettings createBatchPredictionJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createBatchPredictionJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .createBatchPredictionJobSettings()
          .setRetrySettings(createBatchPredictionJobRetrySettings);

      RetrySettings getBatchPredictionJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBatchPredictionJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getBatchPredictionJobSettings()
          .setRetrySettings(getBatchPredictionJobRetrySettings);

      RetrySettings listBatchPredictionJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listBatchPredictionJobsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listBatchPredictionJobsSettings()
          .setRetrySettings(listBatchPredictionJobsRetrySettings);

      RetrySettings cancelBatchPredictionJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelBatchPredictionJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .cancelBatchPredictionJobSettings()
          .setRetrySettings(cancelBatchPredictionJobRetrySettings);

      RetrySettings createModelDeploymentMonitoringJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createModelDeploymentMonitoringJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .createModelDeploymentMonitoringJobSettings()
          .setRetrySettings(createModelDeploymentMonitoringJobRetrySettings);

      RetrySettings searchModelDeploymentMonitoringStatsAnomaliesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder
                  .searchModelDeploymentMonitoringStatsAnomaliesSettings()
                  .getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .searchModelDeploymentMonitoringStatsAnomaliesSettings()
          .setRetrySettings(searchModelDeploymentMonitoringStatsAnomaliesRetrySettings);

      RetrySettings getModelDeploymentMonitoringJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getModelDeploymentMonitoringJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getModelDeploymentMonitoringJobSettings()
          .setRetrySettings(getModelDeploymentMonitoringJobRetrySettings);

      RetrySettings listModelDeploymentMonitoringJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listModelDeploymentMonitoringJobsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listModelDeploymentMonitoringJobsSettings()
          .setRetrySettings(listModelDeploymentMonitoringJobsRetrySettings);

      RetrySettings pauseModelDeploymentMonitoringJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.pauseModelDeploymentMonitoringJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .pauseModelDeploymentMonitoringJobSettings()
          .setRetrySettings(pauseModelDeploymentMonitoringJobRetrySettings);

      RetrySettings resumeModelDeploymentMonitoringJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.resumeModelDeploymentMonitoringJobSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .resumeModelDeploymentMonitoringJobSettings()
          .setRetrySettings(resumeModelDeploymentMonitoringJobRetrySettings);

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
    Retry createCustomJobRetry = clientProperties.getCreateCustomJobRetry();
    if (createCustomJobRetry != null) {
      RetrySettings createCustomJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createCustomJobSettings().getRetrySettings(),
              createCustomJobRetry);
      clientSettingsBuilder
          .createCustomJobSettings()
          .setRetrySettings(createCustomJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createCustomJob from properties.");
      }
    }
    Retry getCustomJobRetry = clientProperties.getGetCustomJobRetry();
    if (getCustomJobRetry != null) {
      RetrySettings getCustomJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getCustomJobSettings().getRetrySettings(), getCustomJobRetry);
      clientSettingsBuilder.getCustomJobSettings().setRetrySettings(getCustomJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getCustomJob from properties.");
      }
    }
    Retry listCustomJobsRetry = clientProperties.getListCustomJobsRetry();
    if (listCustomJobsRetry != null) {
      RetrySettings listCustomJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listCustomJobsSettings().getRetrySettings(),
              listCustomJobsRetry);
      clientSettingsBuilder.listCustomJobsSettings().setRetrySettings(listCustomJobsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listCustomJobs from properties.");
      }
    }
    Retry cancelCustomJobRetry = clientProperties.getCancelCustomJobRetry();
    if (cancelCustomJobRetry != null) {
      RetrySettings cancelCustomJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelCustomJobSettings().getRetrySettings(),
              cancelCustomJobRetry);
      clientSettingsBuilder
          .cancelCustomJobSettings()
          .setRetrySettings(cancelCustomJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for cancelCustomJob from properties.");
      }
    }
    Retry createDataLabelingJobRetry = clientProperties.getCreateDataLabelingJobRetry();
    if (createDataLabelingJobRetry != null) {
      RetrySettings createDataLabelingJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createDataLabelingJobSettings().getRetrySettings(),
              createDataLabelingJobRetry);
      clientSettingsBuilder
          .createDataLabelingJobSettings()
          .setRetrySettings(createDataLabelingJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createDataLabelingJob from properties.");
      }
    }
    Retry getDataLabelingJobRetry = clientProperties.getGetDataLabelingJobRetry();
    if (getDataLabelingJobRetry != null) {
      RetrySettings getDataLabelingJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getDataLabelingJobSettings().getRetrySettings(),
              getDataLabelingJobRetry);
      clientSettingsBuilder
          .getDataLabelingJobSettings()
          .setRetrySettings(getDataLabelingJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getDataLabelingJob from properties.");
      }
    }
    Retry listDataLabelingJobsRetry = clientProperties.getListDataLabelingJobsRetry();
    if (listDataLabelingJobsRetry != null) {
      RetrySettings listDataLabelingJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listDataLabelingJobsSettings().getRetrySettings(),
              listDataLabelingJobsRetry);
      clientSettingsBuilder
          .listDataLabelingJobsSettings()
          .setRetrySettings(listDataLabelingJobsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listDataLabelingJobs from properties.");
      }
    }
    Retry cancelDataLabelingJobRetry = clientProperties.getCancelDataLabelingJobRetry();
    if (cancelDataLabelingJobRetry != null) {
      RetrySettings cancelDataLabelingJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelDataLabelingJobSettings().getRetrySettings(),
              cancelDataLabelingJobRetry);
      clientSettingsBuilder
          .cancelDataLabelingJobSettings()
          .setRetrySettings(cancelDataLabelingJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for cancelDataLabelingJob from properties.");
      }
    }
    Retry createHyperparameterTuningJobRetry =
        clientProperties.getCreateHyperparameterTuningJobRetry();
    if (createHyperparameterTuningJobRetry != null) {
      RetrySettings createHyperparameterTuningJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createHyperparameterTuningJobSettings().getRetrySettings(),
              createHyperparameterTuningJobRetry);
      clientSettingsBuilder
          .createHyperparameterTuningJobSettings()
          .setRetrySettings(createHyperparameterTuningJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createHyperparameterTuningJob from properties.");
      }
    }
    Retry getHyperparameterTuningJobRetry = clientProperties.getGetHyperparameterTuningJobRetry();
    if (getHyperparameterTuningJobRetry != null) {
      RetrySettings getHyperparameterTuningJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getHyperparameterTuningJobSettings().getRetrySettings(),
              getHyperparameterTuningJobRetry);
      clientSettingsBuilder
          .getHyperparameterTuningJobSettings()
          .setRetrySettings(getHyperparameterTuningJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getHyperparameterTuningJob from properties.");
      }
    }
    Retry listHyperparameterTuningJobsRetry =
        clientProperties.getListHyperparameterTuningJobsRetry();
    if (listHyperparameterTuningJobsRetry != null) {
      RetrySettings listHyperparameterTuningJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listHyperparameterTuningJobsSettings().getRetrySettings(),
              listHyperparameterTuningJobsRetry);
      clientSettingsBuilder
          .listHyperparameterTuningJobsSettings()
          .setRetrySettings(listHyperparameterTuningJobsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listHyperparameterTuningJobs from properties.");
      }
    }
    Retry cancelHyperparameterTuningJobRetry =
        clientProperties.getCancelHyperparameterTuningJobRetry();
    if (cancelHyperparameterTuningJobRetry != null) {
      RetrySettings cancelHyperparameterTuningJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelHyperparameterTuningJobSettings().getRetrySettings(),
              cancelHyperparameterTuningJobRetry);
      clientSettingsBuilder
          .cancelHyperparameterTuningJobSettings()
          .setRetrySettings(cancelHyperparameterTuningJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for cancelHyperparameterTuningJob from properties.");
      }
    }
    Retry createBatchPredictionJobRetry = clientProperties.getCreateBatchPredictionJobRetry();
    if (createBatchPredictionJobRetry != null) {
      RetrySettings createBatchPredictionJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createBatchPredictionJobSettings().getRetrySettings(),
              createBatchPredictionJobRetry);
      clientSettingsBuilder
          .createBatchPredictionJobSettings()
          .setRetrySettings(createBatchPredictionJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createBatchPredictionJob from properties.");
      }
    }
    Retry getBatchPredictionJobRetry = clientProperties.getGetBatchPredictionJobRetry();
    if (getBatchPredictionJobRetry != null) {
      RetrySettings getBatchPredictionJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBatchPredictionJobSettings().getRetrySettings(),
              getBatchPredictionJobRetry);
      clientSettingsBuilder
          .getBatchPredictionJobSettings()
          .setRetrySettings(getBatchPredictionJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getBatchPredictionJob from properties.");
      }
    }
    Retry listBatchPredictionJobsRetry = clientProperties.getListBatchPredictionJobsRetry();
    if (listBatchPredictionJobsRetry != null) {
      RetrySettings listBatchPredictionJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listBatchPredictionJobsSettings().getRetrySettings(),
              listBatchPredictionJobsRetry);
      clientSettingsBuilder
          .listBatchPredictionJobsSettings()
          .setRetrySettings(listBatchPredictionJobsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listBatchPredictionJobs from properties.");
      }
    }
    Retry cancelBatchPredictionJobRetry = clientProperties.getCancelBatchPredictionJobRetry();
    if (cancelBatchPredictionJobRetry != null) {
      RetrySettings cancelBatchPredictionJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.cancelBatchPredictionJobSettings().getRetrySettings(),
              cancelBatchPredictionJobRetry);
      clientSettingsBuilder
          .cancelBatchPredictionJobSettings()
          .setRetrySettings(cancelBatchPredictionJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for cancelBatchPredictionJob from properties.");
      }
    }
    Retry createModelDeploymentMonitoringJobRetry =
        clientProperties.getCreateModelDeploymentMonitoringJobRetry();
    if (createModelDeploymentMonitoringJobRetry != null) {
      RetrySettings createModelDeploymentMonitoringJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createModelDeploymentMonitoringJobSettings().getRetrySettings(),
              createModelDeploymentMonitoringJobRetry);
      clientSettingsBuilder
          .createModelDeploymentMonitoringJobSettings()
          .setRetrySettings(createModelDeploymentMonitoringJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createModelDeploymentMonitoringJob from properties.");
      }
    }
    Retry searchModelDeploymentMonitoringStatsAnomaliesRetry =
        clientProperties.getSearchModelDeploymentMonitoringStatsAnomaliesRetry();
    if (searchModelDeploymentMonitoringStatsAnomaliesRetry != null) {
      RetrySettings searchModelDeploymentMonitoringStatsAnomaliesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder
                  .searchModelDeploymentMonitoringStatsAnomaliesSettings()
                  .getRetrySettings(),
              searchModelDeploymentMonitoringStatsAnomaliesRetry);
      clientSettingsBuilder
          .searchModelDeploymentMonitoringStatsAnomaliesSettings()
          .setRetrySettings(searchModelDeploymentMonitoringStatsAnomaliesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for searchModelDeploymentMonitoringStatsAnomalies from properties.");
      }
    }
    Retry getModelDeploymentMonitoringJobRetry =
        clientProperties.getGetModelDeploymentMonitoringJobRetry();
    if (getModelDeploymentMonitoringJobRetry != null) {
      RetrySettings getModelDeploymentMonitoringJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getModelDeploymentMonitoringJobSettings().getRetrySettings(),
              getModelDeploymentMonitoringJobRetry);
      clientSettingsBuilder
          .getModelDeploymentMonitoringJobSettings()
          .setRetrySettings(getModelDeploymentMonitoringJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getModelDeploymentMonitoringJob from properties.");
      }
    }
    Retry listModelDeploymentMonitoringJobsRetry =
        clientProperties.getListModelDeploymentMonitoringJobsRetry();
    if (listModelDeploymentMonitoringJobsRetry != null) {
      RetrySettings listModelDeploymentMonitoringJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listModelDeploymentMonitoringJobsSettings().getRetrySettings(),
              listModelDeploymentMonitoringJobsRetry);
      clientSettingsBuilder
          .listModelDeploymentMonitoringJobsSettings()
          .setRetrySettings(listModelDeploymentMonitoringJobsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listModelDeploymentMonitoringJobs from properties.");
      }
    }
    Retry pauseModelDeploymentMonitoringJobRetry =
        clientProperties.getPauseModelDeploymentMonitoringJobRetry();
    if (pauseModelDeploymentMonitoringJobRetry != null) {
      RetrySettings pauseModelDeploymentMonitoringJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.pauseModelDeploymentMonitoringJobSettings().getRetrySettings(),
              pauseModelDeploymentMonitoringJobRetry);
      clientSettingsBuilder
          .pauseModelDeploymentMonitoringJobSettings()
          .setRetrySettings(pauseModelDeploymentMonitoringJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for pauseModelDeploymentMonitoringJob from properties.");
      }
    }
    Retry resumeModelDeploymentMonitoringJobRetry =
        clientProperties.getResumeModelDeploymentMonitoringJobRetry();
    if (resumeModelDeploymentMonitoringJobRetry != null) {
      RetrySettings resumeModelDeploymentMonitoringJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.resumeModelDeploymentMonitoringJobSettings().getRetrySettings(),
              resumeModelDeploymentMonitoringJobRetry);
      clientSettingsBuilder
          .resumeModelDeploymentMonitoringJobSettings()
          .setRetrySettings(resumeModelDeploymentMonitoringJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for resumeModelDeploymentMonitoringJob from properties.");
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
   * Provides a JobServiceClient bean configured with JobServiceSettings.
   *
   * @param jobServiceSettings settings to configure an instance of client bean.
   * @return a {@link JobServiceClient} bean configured with {@link JobServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public JobServiceClient jobServiceClient(JobServiceSettings jobServiceSettings)
      throws IOException {
    return JobServiceClient.create(jobServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-job-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
