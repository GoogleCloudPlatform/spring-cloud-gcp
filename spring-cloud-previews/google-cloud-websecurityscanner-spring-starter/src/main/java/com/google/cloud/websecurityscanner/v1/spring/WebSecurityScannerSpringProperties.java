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

package com.google.cloud.websecurityscanner.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for WebSecurityScanner client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.websecurityscanner.v1.web-security-scanner")
public class WebSecurityScannerSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for createScanConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createScanConfigRetry;
  /**
   * Allow override of retry settings at method-level for deleteScanConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteScanConfigRetry;
  /**
   * Allow override of retry settings at method-level for getScanConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getScanConfigRetry;
  /**
   * Allow override of retry settings at method-level for listScanConfigs. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listScanConfigsRetry;
  /**
   * Allow override of retry settings at method-level for updateScanConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateScanConfigRetry;
  /**
   * Allow override of retry settings at method-level for startScanRun. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry startScanRunRetry;
  /**
   * Allow override of retry settings at method-level for getScanRun. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getScanRunRetry;
  /**
   * Allow override of retry settings at method-level for listScanRuns. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listScanRunsRetry;
  /**
   * Allow override of retry settings at method-level for stopScanRun. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry stopScanRunRetry;
  /**
   * Allow override of retry settings at method-level for listCrawledUrls. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listCrawledUrlsRetry;
  /**
   * Allow override of retry settings at method-level for getFinding. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getFindingRetry;
  /**
   * Allow override of retry settings at method-level for listFindings. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listFindingsRetry;
  /**
   * Allow override of retry settings at method-level for listFindingTypeStats. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listFindingTypeStatsRetry;

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

  public Retry getCreateScanConfigRetry() {
    return this.createScanConfigRetry;
  }

  public void setCreateScanConfigRetry(Retry createScanConfigRetry) {
    this.createScanConfigRetry = createScanConfigRetry;
  }

  public Retry getDeleteScanConfigRetry() {
    return this.deleteScanConfigRetry;
  }

  public void setDeleteScanConfigRetry(Retry deleteScanConfigRetry) {
    this.deleteScanConfigRetry = deleteScanConfigRetry;
  }

  public Retry getGetScanConfigRetry() {
    return this.getScanConfigRetry;
  }

  public void setGetScanConfigRetry(Retry getScanConfigRetry) {
    this.getScanConfigRetry = getScanConfigRetry;
  }

  public Retry getListScanConfigsRetry() {
    return this.listScanConfigsRetry;
  }

  public void setListScanConfigsRetry(Retry listScanConfigsRetry) {
    this.listScanConfigsRetry = listScanConfigsRetry;
  }

  public Retry getUpdateScanConfigRetry() {
    return this.updateScanConfigRetry;
  }

  public void setUpdateScanConfigRetry(Retry updateScanConfigRetry) {
    this.updateScanConfigRetry = updateScanConfigRetry;
  }

  public Retry getStartScanRunRetry() {
    return this.startScanRunRetry;
  }

  public void setStartScanRunRetry(Retry startScanRunRetry) {
    this.startScanRunRetry = startScanRunRetry;
  }

  public Retry getGetScanRunRetry() {
    return this.getScanRunRetry;
  }

  public void setGetScanRunRetry(Retry getScanRunRetry) {
    this.getScanRunRetry = getScanRunRetry;
  }

  public Retry getListScanRunsRetry() {
    return this.listScanRunsRetry;
  }

  public void setListScanRunsRetry(Retry listScanRunsRetry) {
    this.listScanRunsRetry = listScanRunsRetry;
  }

  public Retry getStopScanRunRetry() {
    return this.stopScanRunRetry;
  }

  public void setStopScanRunRetry(Retry stopScanRunRetry) {
    this.stopScanRunRetry = stopScanRunRetry;
  }

  public Retry getListCrawledUrlsRetry() {
    return this.listCrawledUrlsRetry;
  }

  public void setListCrawledUrlsRetry(Retry listCrawledUrlsRetry) {
    this.listCrawledUrlsRetry = listCrawledUrlsRetry;
  }

  public Retry getGetFindingRetry() {
    return this.getFindingRetry;
  }

  public void setGetFindingRetry(Retry getFindingRetry) {
    this.getFindingRetry = getFindingRetry;
  }

  public Retry getListFindingsRetry() {
    return this.listFindingsRetry;
  }

  public void setListFindingsRetry(Retry listFindingsRetry) {
    this.listFindingsRetry = listFindingsRetry;
  }

  public Retry getListFindingTypeStatsRetry() {
    return this.listFindingTypeStatsRetry;
  }

  public void setListFindingTypeStatsRetry(Retry listFindingTypeStatsRetry) {
    this.listFindingTypeStatsRetry = listFindingTypeStatsRetry;
  }
}
