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

package com.google.cloud.aiplatform.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for TensorboardService client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.aiplatform.v1.tensorboard-service")
public class TensorboardServiceSpringProperties implements CredentialsSupplier {
  /** OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries. */
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials(
          "https://www.googleapis.com/auth/cloud-platform",
          "https://www.googleapis.com/auth/cloud-platform.read-only");
  /** Quota project to use for billing. */
  private String quotaProjectId;
  /** Number of threads used for executors. */
  private Integer executorThreadCount;
  /** Allow override of retry settings at service level, applying to all of its RPC methods. */
  @NestedConfigurationProperty private Retry retry;
  /**
   * Allow override of retry settings at method-level for getTensorboard. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getTensorboardRetry;
  /**
   * Allow override of retry settings at method-level for readTensorboardUsage. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry readTensorboardUsageRetry;
  /**
   * Allow override of retry settings at method-level for listTensorboards. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listTensorboardsRetry;
  /**
   * Allow override of retry settings at method-level for createTensorboardExperiment. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createTensorboardExperimentRetry;
  /**
   * Allow override of retry settings at method-level for getTensorboardExperiment. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getTensorboardExperimentRetry;
  /**
   * Allow override of retry settings at method-level for updateTensorboardExperiment. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateTensorboardExperimentRetry;
  /**
   * Allow override of retry settings at method-level for listTensorboardExperiments. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listTensorboardExperimentsRetry;
  /**
   * Allow override of retry settings at method-level for createTensorboardRun. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createTensorboardRunRetry;
  /**
   * Allow override of retry settings at method-level for batchCreateTensorboardRuns. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry batchCreateTensorboardRunsRetry;
  /**
   * Allow override of retry settings at method-level for getTensorboardRun. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getTensorboardRunRetry;
  /**
   * Allow override of retry settings at method-level for updateTensorboardRun. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateTensorboardRunRetry;
  /**
   * Allow override of retry settings at method-level for listTensorboardRuns. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listTensorboardRunsRetry;
  /**
   * Allow override of retry settings at method-level for batchCreateTensorboardTimeSeries. If
   * defined, this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry batchCreateTensorboardTimeSeriesRetry;
  /**
   * Allow override of retry settings at method-level for createTensorboardTimeSeries. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createTensorboardTimeSeriesRetry;
  /**
   * Allow override of retry settings at method-level for getTensorboardTimeSeries. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getTensorboardTimeSeriesRetry;
  /**
   * Allow override of retry settings at method-level for updateTensorboardTimeSeries. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateTensorboardTimeSeriesRetry;
  /**
   * Allow override of retry settings at method-level for listTensorboardTimeSeries. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listTensorboardTimeSeriesRetry;
  /**
   * Allow override of retry settings at method-level for batchReadTensorboardTimeSeriesData. If
   * defined, this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry batchReadTensorboardTimeSeriesDataRetry;
  /**
   * Allow override of retry settings at method-level for readTensorboardTimeSeriesData. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry readTensorboardTimeSeriesDataRetry;
  /**
   * Allow override of retry settings at method-level for writeTensorboardExperimentData. If
   * defined, this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry writeTensorboardExperimentDataRetry;
  /**
   * Allow override of retry settings at method-level for writeTensorboardRunData. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry writeTensorboardRunDataRetry;
  /**
   * Allow override of retry settings at method-level for exportTensorboardTimeSeriesData. If
   * defined, this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry exportTensorboardTimeSeriesDataRetry;
  /**
   * Allow override of retry settings at method-level for listLocations. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listLocationsRetry;
  /**
   * Allow override of retry settings at method-level for getLocation. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getLocationRetry;
  /**
   * Allow override of retry settings at method-level for setIamPolicy. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry setIamPolicyRetry;
  /**
   * Allow override of retry settings at method-level for getIamPolicy. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getIamPolicyRetry;
  /**
   * Allow override of retry settings at method-level for testIamPermissions. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry testIamPermissionsRetry;

  @Override
  public Credentials getCredentials() {
    return this.credentials;
  }

  public String getQuotaProjectId() {
    return this.quotaProjectId;
  }

  public void setQuotaProjectId(String quotaProjectId) {
    this.quotaProjectId = quotaProjectId;
  }

  public Integer getExecutorThreadCount() {
    return this.executorThreadCount;
  }

  public void setExecutorThreadCount(Integer executorThreadCount) {
    this.executorThreadCount = executorThreadCount;
  }

  public Retry getRetry() {
    return this.retry;
  }

  public void setRetry(Retry retry) {
    this.retry = retry;
  }

  public Retry getGetTensorboardRetry() {
    return this.getTensorboardRetry;
  }

  public void setGetTensorboardRetry(Retry getTensorboardRetry) {
    this.getTensorboardRetry = getTensorboardRetry;
  }

  public Retry getReadTensorboardUsageRetry() {
    return this.readTensorboardUsageRetry;
  }

  public void setReadTensorboardUsageRetry(Retry readTensorboardUsageRetry) {
    this.readTensorboardUsageRetry = readTensorboardUsageRetry;
  }

  public Retry getListTensorboardsRetry() {
    return this.listTensorboardsRetry;
  }

  public void setListTensorboardsRetry(Retry listTensorboardsRetry) {
    this.listTensorboardsRetry = listTensorboardsRetry;
  }

  public Retry getCreateTensorboardExperimentRetry() {
    return this.createTensorboardExperimentRetry;
  }

  public void setCreateTensorboardExperimentRetry(Retry createTensorboardExperimentRetry) {
    this.createTensorboardExperimentRetry = createTensorboardExperimentRetry;
  }

  public Retry getGetTensorboardExperimentRetry() {
    return this.getTensorboardExperimentRetry;
  }

  public void setGetTensorboardExperimentRetry(Retry getTensorboardExperimentRetry) {
    this.getTensorboardExperimentRetry = getTensorboardExperimentRetry;
  }

  public Retry getUpdateTensorboardExperimentRetry() {
    return this.updateTensorboardExperimentRetry;
  }

  public void setUpdateTensorboardExperimentRetry(Retry updateTensorboardExperimentRetry) {
    this.updateTensorboardExperimentRetry = updateTensorboardExperimentRetry;
  }

  public Retry getListTensorboardExperimentsRetry() {
    return this.listTensorboardExperimentsRetry;
  }

  public void setListTensorboardExperimentsRetry(Retry listTensorboardExperimentsRetry) {
    this.listTensorboardExperimentsRetry = listTensorboardExperimentsRetry;
  }

  public Retry getCreateTensorboardRunRetry() {
    return this.createTensorboardRunRetry;
  }

  public void setCreateTensorboardRunRetry(Retry createTensorboardRunRetry) {
    this.createTensorboardRunRetry = createTensorboardRunRetry;
  }

  public Retry getBatchCreateTensorboardRunsRetry() {
    return this.batchCreateTensorboardRunsRetry;
  }

  public void setBatchCreateTensorboardRunsRetry(Retry batchCreateTensorboardRunsRetry) {
    this.batchCreateTensorboardRunsRetry = batchCreateTensorboardRunsRetry;
  }

  public Retry getGetTensorboardRunRetry() {
    return this.getTensorboardRunRetry;
  }

  public void setGetTensorboardRunRetry(Retry getTensorboardRunRetry) {
    this.getTensorboardRunRetry = getTensorboardRunRetry;
  }

  public Retry getUpdateTensorboardRunRetry() {
    return this.updateTensorboardRunRetry;
  }

  public void setUpdateTensorboardRunRetry(Retry updateTensorboardRunRetry) {
    this.updateTensorboardRunRetry = updateTensorboardRunRetry;
  }

  public Retry getListTensorboardRunsRetry() {
    return this.listTensorboardRunsRetry;
  }

  public void setListTensorboardRunsRetry(Retry listTensorboardRunsRetry) {
    this.listTensorboardRunsRetry = listTensorboardRunsRetry;
  }

  public Retry getBatchCreateTensorboardTimeSeriesRetry() {
    return this.batchCreateTensorboardTimeSeriesRetry;
  }

  public void setBatchCreateTensorboardTimeSeriesRetry(
      Retry batchCreateTensorboardTimeSeriesRetry) {
    this.batchCreateTensorboardTimeSeriesRetry = batchCreateTensorboardTimeSeriesRetry;
  }

  public Retry getCreateTensorboardTimeSeriesRetry() {
    return this.createTensorboardTimeSeriesRetry;
  }

  public void setCreateTensorboardTimeSeriesRetry(Retry createTensorboardTimeSeriesRetry) {
    this.createTensorboardTimeSeriesRetry = createTensorboardTimeSeriesRetry;
  }

  public Retry getGetTensorboardTimeSeriesRetry() {
    return this.getTensorboardTimeSeriesRetry;
  }

  public void setGetTensorboardTimeSeriesRetry(Retry getTensorboardTimeSeriesRetry) {
    this.getTensorboardTimeSeriesRetry = getTensorboardTimeSeriesRetry;
  }

  public Retry getUpdateTensorboardTimeSeriesRetry() {
    return this.updateTensorboardTimeSeriesRetry;
  }

  public void setUpdateTensorboardTimeSeriesRetry(Retry updateTensorboardTimeSeriesRetry) {
    this.updateTensorboardTimeSeriesRetry = updateTensorboardTimeSeriesRetry;
  }

  public Retry getListTensorboardTimeSeriesRetry() {
    return this.listTensorboardTimeSeriesRetry;
  }

  public void setListTensorboardTimeSeriesRetry(Retry listTensorboardTimeSeriesRetry) {
    this.listTensorboardTimeSeriesRetry = listTensorboardTimeSeriesRetry;
  }

  public Retry getBatchReadTensorboardTimeSeriesDataRetry() {
    return this.batchReadTensorboardTimeSeriesDataRetry;
  }

  public void setBatchReadTensorboardTimeSeriesDataRetry(
      Retry batchReadTensorboardTimeSeriesDataRetry) {
    this.batchReadTensorboardTimeSeriesDataRetry = batchReadTensorboardTimeSeriesDataRetry;
  }

  public Retry getReadTensorboardTimeSeriesDataRetry() {
    return this.readTensorboardTimeSeriesDataRetry;
  }

  public void setReadTensorboardTimeSeriesDataRetry(Retry readTensorboardTimeSeriesDataRetry) {
    this.readTensorboardTimeSeriesDataRetry = readTensorboardTimeSeriesDataRetry;
  }

  public Retry getWriteTensorboardExperimentDataRetry() {
    return this.writeTensorboardExperimentDataRetry;
  }

  public void setWriteTensorboardExperimentDataRetry(Retry writeTensorboardExperimentDataRetry) {
    this.writeTensorboardExperimentDataRetry = writeTensorboardExperimentDataRetry;
  }

  public Retry getWriteTensorboardRunDataRetry() {
    return this.writeTensorboardRunDataRetry;
  }

  public void setWriteTensorboardRunDataRetry(Retry writeTensorboardRunDataRetry) {
    this.writeTensorboardRunDataRetry = writeTensorboardRunDataRetry;
  }

  public Retry getExportTensorboardTimeSeriesDataRetry() {
    return this.exportTensorboardTimeSeriesDataRetry;
  }

  public void setExportTensorboardTimeSeriesDataRetry(Retry exportTensorboardTimeSeriesDataRetry) {
    this.exportTensorboardTimeSeriesDataRetry = exportTensorboardTimeSeriesDataRetry;
  }

  public Retry getListLocationsRetry() {
    return this.listLocationsRetry;
  }

  public void setListLocationsRetry(Retry listLocationsRetry) {
    this.listLocationsRetry = listLocationsRetry;
  }

  public Retry getGetLocationRetry() {
    return this.getLocationRetry;
  }

  public void setGetLocationRetry(Retry getLocationRetry) {
    this.getLocationRetry = getLocationRetry;
  }

  public Retry getSetIamPolicyRetry() {
    return this.setIamPolicyRetry;
  }

  public void setSetIamPolicyRetry(Retry setIamPolicyRetry) {
    this.setIamPolicyRetry = setIamPolicyRetry;
  }

  public Retry getGetIamPolicyRetry() {
    return this.getIamPolicyRetry;
  }

  public void setGetIamPolicyRetry(Retry getIamPolicyRetry) {
    this.getIamPolicyRetry = getIamPolicyRetry;
  }

  public Retry getTestIamPermissionsRetry() {
    return this.testIamPermissionsRetry;
  }

  public void setTestIamPermissionsRetry(Retry testIamPermissionsRetry) {
    this.testIamPermissionsRetry = testIamPermissionsRetry;
  }
}
