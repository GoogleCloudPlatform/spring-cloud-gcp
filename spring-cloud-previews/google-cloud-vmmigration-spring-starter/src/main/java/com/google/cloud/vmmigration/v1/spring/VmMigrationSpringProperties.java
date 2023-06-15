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

package com.google.cloud.vmmigration.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for VmMigration client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.vmmigration.v1.vm-migration")
public class VmMigrationSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for listSources. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listSourcesRetry;
  /**
   * Allow override of retry settings at method-level for getSource. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getSourceRetry;
  /**
   * Allow override of retry settings at method-level for fetchInventory. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry fetchInventoryRetry;
  /**
   * Allow override of retry settings at method-level for listUtilizationReports. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listUtilizationReportsRetry;
  /**
   * Allow override of retry settings at method-level for getUtilizationReport. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getUtilizationReportRetry;
  /**
   * Allow override of retry settings at method-level for listDatacenterConnectors. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listDatacenterConnectorsRetry;
  /**
   * Allow override of retry settings at method-level for getDatacenterConnector. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getDatacenterConnectorRetry;
  /**
   * Allow override of retry settings at method-level for listMigratingVms. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listMigratingVmsRetry;
  /**
   * Allow override of retry settings at method-level for getMigratingVm. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getMigratingVmRetry;
  /**
   * Allow override of retry settings at method-level for listCloneJobs. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listCloneJobsRetry;
  /**
   * Allow override of retry settings at method-level for getCloneJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getCloneJobRetry;
  /**
   * Allow override of retry settings at method-level for listCutoverJobs. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listCutoverJobsRetry;
  /**
   * Allow override of retry settings at method-level for getCutoverJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getCutoverJobRetry;
  /**
   * Allow override of retry settings at method-level for listGroups. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listGroupsRetry;
  /**
   * Allow override of retry settings at method-level for getGroup. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getGroupRetry;
  /**
   * Allow override of retry settings at method-level for listTargetProjects. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listTargetProjectsRetry;
  /**
   * Allow override of retry settings at method-level for getTargetProject. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getTargetProjectRetry;
  /**
   * Allow override of retry settings at method-level for listReplicationCycles. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listReplicationCyclesRetry;
  /**
   * Allow override of retry settings at method-level for getReplicationCycle. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getReplicationCycleRetry;
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

  public Retry getListSourcesRetry() {
    return this.listSourcesRetry;
  }

  public void setListSourcesRetry(Retry listSourcesRetry) {
    this.listSourcesRetry = listSourcesRetry;
  }

  public Retry getGetSourceRetry() {
    return this.getSourceRetry;
  }

  public void setGetSourceRetry(Retry getSourceRetry) {
    this.getSourceRetry = getSourceRetry;
  }

  public Retry getFetchInventoryRetry() {
    return this.fetchInventoryRetry;
  }

  public void setFetchInventoryRetry(Retry fetchInventoryRetry) {
    this.fetchInventoryRetry = fetchInventoryRetry;
  }

  public Retry getListUtilizationReportsRetry() {
    return this.listUtilizationReportsRetry;
  }

  public void setListUtilizationReportsRetry(Retry listUtilizationReportsRetry) {
    this.listUtilizationReportsRetry = listUtilizationReportsRetry;
  }

  public Retry getGetUtilizationReportRetry() {
    return this.getUtilizationReportRetry;
  }

  public void setGetUtilizationReportRetry(Retry getUtilizationReportRetry) {
    this.getUtilizationReportRetry = getUtilizationReportRetry;
  }

  public Retry getListDatacenterConnectorsRetry() {
    return this.listDatacenterConnectorsRetry;
  }

  public void setListDatacenterConnectorsRetry(Retry listDatacenterConnectorsRetry) {
    this.listDatacenterConnectorsRetry = listDatacenterConnectorsRetry;
  }

  public Retry getGetDatacenterConnectorRetry() {
    return this.getDatacenterConnectorRetry;
  }

  public void setGetDatacenterConnectorRetry(Retry getDatacenterConnectorRetry) {
    this.getDatacenterConnectorRetry = getDatacenterConnectorRetry;
  }

  public Retry getListMigratingVmsRetry() {
    return this.listMigratingVmsRetry;
  }

  public void setListMigratingVmsRetry(Retry listMigratingVmsRetry) {
    this.listMigratingVmsRetry = listMigratingVmsRetry;
  }

  public Retry getGetMigratingVmRetry() {
    return this.getMigratingVmRetry;
  }

  public void setGetMigratingVmRetry(Retry getMigratingVmRetry) {
    this.getMigratingVmRetry = getMigratingVmRetry;
  }

  public Retry getListCloneJobsRetry() {
    return this.listCloneJobsRetry;
  }

  public void setListCloneJobsRetry(Retry listCloneJobsRetry) {
    this.listCloneJobsRetry = listCloneJobsRetry;
  }

  public Retry getGetCloneJobRetry() {
    return this.getCloneJobRetry;
  }

  public void setGetCloneJobRetry(Retry getCloneJobRetry) {
    this.getCloneJobRetry = getCloneJobRetry;
  }

  public Retry getListCutoverJobsRetry() {
    return this.listCutoverJobsRetry;
  }

  public void setListCutoverJobsRetry(Retry listCutoverJobsRetry) {
    this.listCutoverJobsRetry = listCutoverJobsRetry;
  }

  public Retry getGetCutoverJobRetry() {
    return this.getCutoverJobRetry;
  }

  public void setGetCutoverJobRetry(Retry getCutoverJobRetry) {
    this.getCutoverJobRetry = getCutoverJobRetry;
  }

  public Retry getListGroupsRetry() {
    return this.listGroupsRetry;
  }

  public void setListGroupsRetry(Retry listGroupsRetry) {
    this.listGroupsRetry = listGroupsRetry;
  }

  public Retry getGetGroupRetry() {
    return this.getGroupRetry;
  }

  public void setGetGroupRetry(Retry getGroupRetry) {
    this.getGroupRetry = getGroupRetry;
  }

  public Retry getListTargetProjectsRetry() {
    return this.listTargetProjectsRetry;
  }

  public void setListTargetProjectsRetry(Retry listTargetProjectsRetry) {
    this.listTargetProjectsRetry = listTargetProjectsRetry;
  }

  public Retry getGetTargetProjectRetry() {
    return this.getTargetProjectRetry;
  }

  public void setGetTargetProjectRetry(Retry getTargetProjectRetry) {
    this.getTargetProjectRetry = getTargetProjectRetry;
  }

  public Retry getListReplicationCyclesRetry() {
    return this.listReplicationCyclesRetry;
  }

  public void setListReplicationCyclesRetry(Retry listReplicationCyclesRetry) {
    this.listReplicationCyclesRetry = listReplicationCyclesRetry;
  }

  public Retry getGetReplicationCycleRetry() {
    return this.getReplicationCycleRetry;
  }

  public void setGetReplicationCycleRetry(Retry getReplicationCycleRetry) {
    this.getReplicationCycleRetry = getReplicationCycleRetry;
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
