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

package com.google.cloud.bigquery.datatransfer.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for DataTransferService client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.bigquery.datatransfer.v1.data-transfer-service")
public class DataTransferServiceSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for getDataSource. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getDataSourceRetry;
  /**
   * Allow override of retry settings at method-level for listDataSources. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listDataSourcesRetry;
  /**
   * Allow override of retry settings at method-level for createTransferConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createTransferConfigRetry;
  /**
   * Allow override of retry settings at method-level for updateTransferConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateTransferConfigRetry;
  /**
   * Allow override of retry settings at method-level for deleteTransferConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteTransferConfigRetry;
  /**
   * Allow override of retry settings at method-level for getTransferConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getTransferConfigRetry;
  /**
   * Allow override of retry settings at method-level for listTransferConfigs. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listTransferConfigsRetry;
  /**
   * Allow override of retry settings at method-level for scheduleTransferRuns. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry scheduleTransferRunsRetry;
  /**
   * Allow override of retry settings at method-level for startManualTransferRuns. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry startManualTransferRunsRetry;
  /**
   * Allow override of retry settings at method-level for getTransferRun. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getTransferRunRetry;
  /**
   * Allow override of retry settings at method-level for deleteTransferRun. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteTransferRunRetry;
  /**
   * Allow override of retry settings at method-level for listTransferRuns. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listTransferRunsRetry;
  /**
   * Allow override of retry settings at method-level for listTransferLogs. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listTransferLogsRetry;
  /**
   * Allow override of retry settings at method-level for checkValidCreds. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry checkValidCredsRetry;
  /**
   * Allow override of retry settings at method-level for enrollDataSources. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry enrollDataSourcesRetry;
  /**
   * Allow override of retry settings at method-level for unenrollDataSources. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry unenrollDataSourcesRetry;
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

  public Retry getGetDataSourceRetry() {
    return this.getDataSourceRetry;
  }

  public void setGetDataSourceRetry(Retry getDataSourceRetry) {
    this.getDataSourceRetry = getDataSourceRetry;
  }

  public Retry getListDataSourcesRetry() {
    return this.listDataSourcesRetry;
  }

  public void setListDataSourcesRetry(Retry listDataSourcesRetry) {
    this.listDataSourcesRetry = listDataSourcesRetry;
  }

  public Retry getCreateTransferConfigRetry() {
    return this.createTransferConfigRetry;
  }

  public void setCreateTransferConfigRetry(Retry createTransferConfigRetry) {
    this.createTransferConfigRetry = createTransferConfigRetry;
  }

  public Retry getUpdateTransferConfigRetry() {
    return this.updateTransferConfigRetry;
  }

  public void setUpdateTransferConfigRetry(Retry updateTransferConfigRetry) {
    this.updateTransferConfigRetry = updateTransferConfigRetry;
  }

  public Retry getDeleteTransferConfigRetry() {
    return this.deleteTransferConfigRetry;
  }

  public void setDeleteTransferConfigRetry(Retry deleteTransferConfigRetry) {
    this.deleteTransferConfigRetry = deleteTransferConfigRetry;
  }

  public Retry getGetTransferConfigRetry() {
    return this.getTransferConfigRetry;
  }

  public void setGetTransferConfigRetry(Retry getTransferConfigRetry) {
    this.getTransferConfigRetry = getTransferConfigRetry;
  }

  public Retry getListTransferConfigsRetry() {
    return this.listTransferConfigsRetry;
  }

  public void setListTransferConfigsRetry(Retry listTransferConfigsRetry) {
    this.listTransferConfigsRetry = listTransferConfigsRetry;
  }

  public Retry getScheduleTransferRunsRetry() {
    return this.scheduleTransferRunsRetry;
  }

  public void setScheduleTransferRunsRetry(Retry scheduleTransferRunsRetry) {
    this.scheduleTransferRunsRetry = scheduleTransferRunsRetry;
  }

  public Retry getStartManualTransferRunsRetry() {
    return this.startManualTransferRunsRetry;
  }

  public void setStartManualTransferRunsRetry(Retry startManualTransferRunsRetry) {
    this.startManualTransferRunsRetry = startManualTransferRunsRetry;
  }

  public Retry getGetTransferRunRetry() {
    return this.getTransferRunRetry;
  }

  public void setGetTransferRunRetry(Retry getTransferRunRetry) {
    this.getTransferRunRetry = getTransferRunRetry;
  }

  public Retry getDeleteTransferRunRetry() {
    return this.deleteTransferRunRetry;
  }

  public void setDeleteTransferRunRetry(Retry deleteTransferRunRetry) {
    this.deleteTransferRunRetry = deleteTransferRunRetry;
  }

  public Retry getListTransferRunsRetry() {
    return this.listTransferRunsRetry;
  }

  public void setListTransferRunsRetry(Retry listTransferRunsRetry) {
    this.listTransferRunsRetry = listTransferRunsRetry;
  }

  public Retry getListTransferLogsRetry() {
    return this.listTransferLogsRetry;
  }

  public void setListTransferLogsRetry(Retry listTransferLogsRetry) {
    this.listTransferLogsRetry = listTransferLogsRetry;
  }

  public Retry getCheckValidCredsRetry() {
    return this.checkValidCredsRetry;
  }

  public void setCheckValidCredsRetry(Retry checkValidCredsRetry) {
    this.checkValidCredsRetry = checkValidCredsRetry;
  }

  public Retry getEnrollDataSourcesRetry() {
    return this.enrollDataSourcesRetry;
  }

  public void setEnrollDataSourcesRetry(Retry enrollDataSourcesRetry) {
    this.enrollDataSourcesRetry = enrollDataSourcesRetry;
  }

  public Retry getUnenrollDataSourcesRetry() {
    return this.unenrollDataSourcesRetry;
  }

  public void setUnenrollDataSourcesRetry(Retry unenrollDataSourcesRetry) {
    this.unenrollDataSourcesRetry = unenrollDataSourcesRetry;
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
}
