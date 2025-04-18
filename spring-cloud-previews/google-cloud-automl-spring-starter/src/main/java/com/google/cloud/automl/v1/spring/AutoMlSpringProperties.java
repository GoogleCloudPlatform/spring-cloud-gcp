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

package com.google.cloud.automl.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for AutoMl client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.automl.v1.auto-ml")
public class AutoMlSpringProperties implements CredentialsSupplier {
  /** OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries. */
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials("https://www.googleapis.com/auth/cloud-platform");
  /** Quota project to use for billing. */
  private String quotaProjectId;
  /** Number of threads used for executors. */
  private Integer executorThreadCount;
  /** Allow override of default transport channel provider to use REST instead of gRPC. */
  private boolean useRest = false;
  /** Allow override of retry settings at service level, applying to all of its RPC methods. */
  @NestedConfigurationProperty private Retry retry;
  /**
   * Allow override of retry settings at method-level for getDataset. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getDatasetRetry;
  /**
   * Allow override of retry settings at method-level for listDatasets. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listDatasetsRetry;
  /**
   * Allow override of retry settings at method-level for updateDataset. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateDatasetRetry;
  /**
   * Allow override of retry settings at method-level for getAnnotationSpec. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getAnnotationSpecRetry;
  /**
   * Allow override of retry settings at method-level for getModel. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getModelRetry;
  /**
   * Allow override of retry settings at method-level for listModels. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listModelsRetry;
  /**
   * Allow override of retry settings at method-level for updateModel. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateModelRetry;
  /**
   * Allow override of retry settings at method-level for getModelEvaluation. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getModelEvaluationRetry;
  /**
   * Allow override of retry settings at method-level for listModelEvaluations. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listModelEvaluationsRetry;

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

  public boolean getUseRest() {
    return this.useRest;
  }

  public void setUseRest(boolean useRest) {
    this.useRest = useRest;
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

  public Retry getGetDatasetRetry() {
    return this.getDatasetRetry;
  }

  public void setGetDatasetRetry(Retry getDatasetRetry) {
    this.getDatasetRetry = getDatasetRetry;
  }

  public Retry getListDatasetsRetry() {
    return this.listDatasetsRetry;
  }

  public void setListDatasetsRetry(Retry listDatasetsRetry) {
    this.listDatasetsRetry = listDatasetsRetry;
  }

  public Retry getUpdateDatasetRetry() {
    return this.updateDatasetRetry;
  }

  public void setUpdateDatasetRetry(Retry updateDatasetRetry) {
    this.updateDatasetRetry = updateDatasetRetry;
  }

  public Retry getGetAnnotationSpecRetry() {
    return this.getAnnotationSpecRetry;
  }

  public void setGetAnnotationSpecRetry(Retry getAnnotationSpecRetry) {
    this.getAnnotationSpecRetry = getAnnotationSpecRetry;
  }

  public Retry getGetModelRetry() {
    return this.getModelRetry;
  }

  public void setGetModelRetry(Retry getModelRetry) {
    this.getModelRetry = getModelRetry;
  }

  public Retry getListModelsRetry() {
    return this.listModelsRetry;
  }

  public void setListModelsRetry(Retry listModelsRetry) {
    this.listModelsRetry = listModelsRetry;
  }

  public Retry getUpdateModelRetry() {
    return this.updateModelRetry;
  }

  public void setUpdateModelRetry(Retry updateModelRetry) {
    this.updateModelRetry = updateModelRetry;
  }

  public Retry getGetModelEvaluationRetry() {
    return this.getModelEvaluationRetry;
  }

  public void setGetModelEvaluationRetry(Retry getModelEvaluationRetry) {
    this.getModelEvaluationRetry = getModelEvaluationRetry;
  }

  public Retry getListModelEvaluationsRetry() {
    return this.listModelEvaluationsRetry;
  }

  public void setListModelEvaluationsRetry(Retry listModelEvaluationsRetry) {
    this.listModelEvaluationsRetry = listModelEvaluationsRetry;
  }
}
