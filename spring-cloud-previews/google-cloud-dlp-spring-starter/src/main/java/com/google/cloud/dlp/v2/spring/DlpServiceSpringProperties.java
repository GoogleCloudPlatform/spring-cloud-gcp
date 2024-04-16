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

package com.google.cloud.dlp.v2.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for DlpService client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.dlp.v2.dlp-service")
public class DlpServiceSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for inspectContent. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry inspectContentRetry;
  /**
   * Allow override of retry settings at method-level for redactImage. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry redactImageRetry;
  /**
   * Allow override of retry settings at method-level for deidentifyContent. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deidentifyContentRetry;
  /**
   * Allow override of retry settings at method-level for reidentifyContent. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry reidentifyContentRetry;
  /**
   * Allow override of retry settings at method-level for listInfoTypes. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listInfoTypesRetry;
  /**
   * Allow override of retry settings at method-level for createInspectTemplate. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createInspectTemplateRetry;
  /**
   * Allow override of retry settings at method-level for updateInspectTemplate. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateInspectTemplateRetry;
  /**
   * Allow override of retry settings at method-level for getInspectTemplate. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getInspectTemplateRetry;
  /**
   * Allow override of retry settings at method-level for listInspectTemplates. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listInspectTemplatesRetry;
  /**
   * Allow override of retry settings at method-level for deleteInspectTemplate. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteInspectTemplateRetry;
  /**
   * Allow override of retry settings at method-level for createDeidentifyTemplate. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createDeidentifyTemplateRetry;
  /**
   * Allow override of retry settings at method-level for updateDeidentifyTemplate. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateDeidentifyTemplateRetry;
  /**
   * Allow override of retry settings at method-level for getDeidentifyTemplate. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getDeidentifyTemplateRetry;
  /**
   * Allow override of retry settings at method-level for listDeidentifyTemplates. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listDeidentifyTemplatesRetry;
  /**
   * Allow override of retry settings at method-level for deleteDeidentifyTemplate. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteDeidentifyTemplateRetry;
  /**
   * Allow override of retry settings at method-level for createJobTrigger. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createJobTriggerRetry;
  /**
   * Allow override of retry settings at method-level for updateJobTrigger. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateJobTriggerRetry;
  /**
   * Allow override of retry settings at method-level for hybridInspectJobTrigger. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry hybridInspectJobTriggerRetry;
  /**
   * Allow override of retry settings at method-level for getJobTrigger. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getJobTriggerRetry;
  /**
   * Allow override of retry settings at method-level for listJobTriggers. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listJobTriggersRetry;
  /**
   * Allow override of retry settings at method-level for deleteJobTrigger. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteJobTriggerRetry;
  /**
   * Allow override of retry settings at method-level for activateJobTrigger. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry activateJobTriggerRetry;
  /**
   * Allow override of retry settings at method-level for createDiscoveryConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createDiscoveryConfigRetry;
  /**
   * Allow override of retry settings at method-level for updateDiscoveryConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateDiscoveryConfigRetry;
  /**
   * Allow override of retry settings at method-level for getDiscoveryConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getDiscoveryConfigRetry;
  /**
   * Allow override of retry settings at method-level for listDiscoveryConfigs. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listDiscoveryConfigsRetry;
  /**
   * Allow override of retry settings at method-level for deleteDiscoveryConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteDiscoveryConfigRetry;
  /**
   * Allow override of retry settings at method-level for createDlpJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createDlpJobRetry;
  /**
   * Allow override of retry settings at method-level for listDlpJobs. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listDlpJobsRetry;
  /**
   * Allow override of retry settings at method-level for getDlpJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getDlpJobRetry;
  /**
   * Allow override of retry settings at method-level for deleteDlpJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteDlpJobRetry;
  /**
   * Allow override of retry settings at method-level for cancelDlpJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry cancelDlpJobRetry;
  /**
   * Allow override of retry settings at method-level for createStoredInfoType. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createStoredInfoTypeRetry;
  /**
   * Allow override of retry settings at method-level for updateStoredInfoType. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateStoredInfoTypeRetry;
  /**
   * Allow override of retry settings at method-level for getStoredInfoType. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getStoredInfoTypeRetry;
  /**
   * Allow override of retry settings at method-level for listStoredInfoTypes. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listStoredInfoTypesRetry;
  /**
   * Allow override of retry settings at method-level for deleteStoredInfoType. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteStoredInfoTypeRetry;
  /**
   * Allow override of retry settings at method-level for listProjectDataProfiles. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listProjectDataProfilesRetry;
  /**
   * Allow override of retry settings at method-level for listTableDataProfiles. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listTableDataProfilesRetry;
  /**
   * Allow override of retry settings at method-level for listColumnDataProfiles. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listColumnDataProfilesRetry;
  /**
   * Allow override of retry settings at method-level for getProjectDataProfile. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getProjectDataProfileRetry;
  /**
   * Allow override of retry settings at method-level for getTableDataProfile. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getTableDataProfileRetry;
  /**
   * Allow override of retry settings at method-level for getColumnDataProfile. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getColumnDataProfileRetry;
  /**
   * Allow override of retry settings at method-level for hybridInspectDlpJob. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry hybridInspectDlpJobRetry;
  /**
   * Allow override of retry settings at method-level for finishDlpJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry finishDlpJobRetry;

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

  public Retry getInspectContentRetry() {
    return this.inspectContentRetry;
  }

  public void setInspectContentRetry(Retry inspectContentRetry) {
    this.inspectContentRetry = inspectContentRetry;
  }

  public Retry getRedactImageRetry() {
    return this.redactImageRetry;
  }

  public void setRedactImageRetry(Retry redactImageRetry) {
    this.redactImageRetry = redactImageRetry;
  }

  public Retry getDeidentifyContentRetry() {
    return this.deidentifyContentRetry;
  }

  public void setDeidentifyContentRetry(Retry deidentifyContentRetry) {
    this.deidentifyContentRetry = deidentifyContentRetry;
  }

  public Retry getReidentifyContentRetry() {
    return this.reidentifyContentRetry;
  }

  public void setReidentifyContentRetry(Retry reidentifyContentRetry) {
    this.reidentifyContentRetry = reidentifyContentRetry;
  }

  public Retry getListInfoTypesRetry() {
    return this.listInfoTypesRetry;
  }

  public void setListInfoTypesRetry(Retry listInfoTypesRetry) {
    this.listInfoTypesRetry = listInfoTypesRetry;
  }

  public Retry getCreateInspectTemplateRetry() {
    return this.createInspectTemplateRetry;
  }

  public void setCreateInspectTemplateRetry(Retry createInspectTemplateRetry) {
    this.createInspectTemplateRetry = createInspectTemplateRetry;
  }

  public Retry getUpdateInspectTemplateRetry() {
    return this.updateInspectTemplateRetry;
  }

  public void setUpdateInspectTemplateRetry(Retry updateInspectTemplateRetry) {
    this.updateInspectTemplateRetry = updateInspectTemplateRetry;
  }

  public Retry getGetInspectTemplateRetry() {
    return this.getInspectTemplateRetry;
  }

  public void setGetInspectTemplateRetry(Retry getInspectTemplateRetry) {
    this.getInspectTemplateRetry = getInspectTemplateRetry;
  }

  public Retry getListInspectTemplatesRetry() {
    return this.listInspectTemplatesRetry;
  }

  public void setListInspectTemplatesRetry(Retry listInspectTemplatesRetry) {
    this.listInspectTemplatesRetry = listInspectTemplatesRetry;
  }

  public Retry getDeleteInspectTemplateRetry() {
    return this.deleteInspectTemplateRetry;
  }

  public void setDeleteInspectTemplateRetry(Retry deleteInspectTemplateRetry) {
    this.deleteInspectTemplateRetry = deleteInspectTemplateRetry;
  }

  public Retry getCreateDeidentifyTemplateRetry() {
    return this.createDeidentifyTemplateRetry;
  }

  public void setCreateDeidentifyTemplateRetry(Retry createDeidentifyTemplateRetry) {
    this.createDeidentifyTemplateRetry = createDeidentifyTemplateRetry;
  }

  public Retry getUpdateDeidentifyTemplateRetry() {
    return this.updateDeidentifyTemplateRetry;
  }

  public void setUpdateDeidentifyTemplateRetry(Retry updateDeidentifyTemplateRetry) {
    this.updateDeidentifyTemplateRetry = updateDeidentifyTemplateRetry;
  }

  public Retry getGetDeidentifyTemplateRetry() {
    return this.getDeidentifyTemplateRetry;
  }

  public void setGetDeidentifyTemplateRetry(Retry getDeidentifyTemplateRetry) {
    this.getDeidentifyTemplateRetry = getDeidentifyTemplateRetry;
  }

  public Retry getListDeidentifyTemplatesRetry() {
    return this.listDeidentifyTemplatesRetry;
  }

  public void setListDeidentifyTemplatesRetry(Retry listDeidentifyTemplatesRetry) {
    this.listDeidentifyTemplatesRetry = listDeidentifyTemplatesRetry;
  }

  public Retry getDeleteDeidentifyTemplateRetry() {
    return this.deleteDeidentifyTemplateRetry;
  }

  public void setDeleteDeidentifyTemplateRetry(Retry deleteDeidentifyTemplateRetry) {
    this.deleteDeidentifyTemplateRetry = deleteDeidentifyTemplateRetry;
  }

  public Retry getCreateJobTriggerRetry() {
    return this.createJobTriggerRetry;
  }

  public void setCreateJobTriggerRetry(Retry createJobTriggerRetry) {
    this.createJobTriggerRetry = createJobTriggerRetry;
  }

  public Retry getUpdateJobTriggerRetry() {
    return this.updateJobTriggerRetry;
  }

  public void setUpdateJobTriggerRetry(Retry updateJobTriggerRetry) {
    this.updateJobTriggerRetry = updateJobTriggerRetry;
  }

  public Retry getHybridInspectJobTriggerRetry() {
    return this.hybridInspectJobTriggerRetry;
  }

  public void setHybridInspectJobTriggerRetry(Retry hybridInspectJobTriggerRetry) {
    this.hybridInspectJobTriggerRetry = hybridInspectJobTriggerRetry;
  }

  public Retry getGetJobTriggerRetry() {
    return this.getJobTriggerRetry;
  }

  public void setGetJobTriggerRetry(Retry getJobTriggerRetry) {
    this.getJobTriggerRetry = getJobTriggerRetry;
  }

  public Retry getListJobTriggersRetry() {
    return this.listJobTriggersRetry;
  }

  public void setListJobTriggersRetry(Retry listJobTriggersRetry) {
    this.listJobTriggersRetry = listJobTriggersRetry;
  }

  public Retry getDeleteJobTriggerRetry() {
    return this.deleteJobTriggerRetry;
  }

  public void setDeleteJobTriggerRetry(Retry deleteJobTriggerRetry) {
    this.deleteJobTriggerRetry = deleteJobTriggerRetry;
  }

  public Retry getActivateJobTriggerRetry() {
    return this.activateJobTriggerRetry;
  }

  public void setActivateJobTriggerRetry(Retry activateJobTriggerRetry) {
    this.activateJobTriggerRetry = activateJobTriggerRetry;
  }

  public Retry getCreateDiscoveryConfigRetry() {
    return this.createDiscoveryConfigRetry;
  }

  public void setCreateDiscoveryConfigRetry(Retry createDiscoveryConfigRetry) {
    this.createDiscoveryConfigRetry = createDiscoveryConfigRetry;
  }

  public Retry getUpdateDiscoveryConfigRetry() {
    return this.updateDiscoveryConfigRetry;
  }

  public void setUpdateDiscoveryConfigRetry(Retry updateDiscoveryConfigRetry) {
    this.updateDiscoveryConfigRetry = updateDiscoveryConfigRetry;
  }

  public Retry getGetDiscoveryConfigRetry() {
    return this.getDiscoveryConfigRetry;
  }

  public void setGetDiscoveryConfigRetry(Retry getDiscoveryConfigRetry) {
    this.getDiscoveryConfigRetry = getDiscoveryConfigRetry;
  }

  public Retry getListDiscoveryConfigsRetry() {
    return this.listDiscoveryConfigsRetry;
  }

  public void setListDiscoveryConfigsRetry(Retry listDiscoveryConfigsRetry) {
    this.listDiscoveryConfigsRetry = listDiscoveryConfigsRetry;
  }

  public Retry getDeleteDiscoveryConfigRetry() {
    return this.deleteDiscoveryConfigRetry;
  }

  public void setDeleteDiscoveryConfigRetry(Retry deleteDiscoveryConfigRetry) {
    this.deleteDiscoveryConfigRetry = deleteDiscoveryConfigRetry;
  }

  public Retry getCreateDlpJobRetry() {
    return this.createDlpJobRetry;
  }

  public void setCreateDlpJobRetry(Retry createDlpJobRetry) {
    this.createDlpJobRetry = createDlpJobRetry;
  }

  public Retry getListDlpJobsRetry() {
    return this.listDlpJobsRetry;
  }

  public void setListDlpJobsRetry(Retry listDlpJobsRetry) {
    this.listDlpJobsRetry = listDlpJobsRetry;
  }

  public Retry getGetDlpJobRetry() {
    return this.getDlpJobRetry;
  }

  public void setGetDlpJobRetry(Retry getDlpJobRetry) {
    this.getDlpJobRetry = getDlpJobRetry;
  }

  public Retry getDeleteDlpJobRetry() {
    return this.deleteDlpJobRetry;
  }

  public void setDeleteDlpJobRetry(Retry deleteDlpJobRetry) {
    this.deleteDlpJobRetry = deleteDlpJobRetry;
  }

  public Retry getCancelDlpJobRetry() {
    return this.cancelDlpJobRetry;
  }

  public void setCancelDlpJobRetry(Retry cancelDlpJobRetry) {
    this.cancelDlpJobRetry = cancelDlpJobRetry;
  }

  public Retry getCreateStoredInfoTypeRetry() {
    return this.createStoredInfoTypeRetry;
  }

  public void setCreateStoredInfoTypeRetry(Retry createStoredInfoTypeRetry) {
    this.createStoredInfoTypeRetry = createStoredInfoTypeRetry;
  }

  public Retry getUpdateStoredInfoTypeRetry() {
    return this.updateStoredInfoTypeRetry;
  }

  public void setUpdateStoredInfoTypeRetry(Retry updateStoredInfoTypeRetry) {
    this.updateStoredInfoTypeRetry = updateStoredInfoTypeRetry;
  }

  public Retry getGetStoredInfoTypeRetry() {
    return this.getStoredInfoTypeRetry;
  }

  public void setGetStoredInfoTypeRetry(Retry getStoredInfoTypeRetry) {
    this.getStoredInfoTypeRetry = getStoredInfoTypeRetry;
  }

  public Retry getListStoredInfoTypesRetry() {
    return this.listStoredInfoTypesRetry;
  }

  public void setListStoredInfoTypesRetry(Retry listStoredInfoTypesRetry) {
    this.listStoredInfoTypesRetry = listStoredInfoTypesRetry;
  }

  public Retry getDeleteStoredInfoTypeRetry() {
    return this.deleteStoredInfoTypeRetry;
  }

  public void setDeleteStoredInfoTypeRetry(Retry deleteStoredInfoTypeRetry) {
    this.deleteStoredInfoTypeRetry = deleteStoredInfoTypeRetry;
  }

  public Retry getListProjectDataProfilesRetry() {
    return this.listProjectDataProfilesRetry;
  }

  public void setListProjectDataProfilesRetry(Retry listProjectDataProfilesRetry) {
    this.listProjectDataProfilesRetry = listProjectDataProfilesRetry;
  }

  public Retry getListTableDataProfilesRetry() {
    return this.listTableDataProfilesRetry;
  }

  public void setListTableDataProfilesRetry(Retry listTableDataProfilesRetry) {
    this.listTableDataProfilesRetry = listTableDataProfilesRetry;
  }

  public Retry getListColumnDataProfilesRetry() {
    return this.listColumnDataProfilesRetry;
  }

  public void setListColumnDataProfilesRetry(Retry listColumnDataProfilesRetry) {
    this.listColumnDataProfilesRetry = listColumnDataProfilesRetry;
  }

  public Retry getGetProjectDataProfileRetry() {
    return this.getProjectDataProfileRetry;
  }

  public void setGetProjectDataProfileRetry(Retry getProjectDataProfileRetry) {
    this.getProjectDataProfileRetry = getProjectDataProfileRetry;
  }

  public Retry getGetTableDataProfileRetry() {
    return this.getTableDataProfileRetry;
  }

  public void setGetTableDataProfileRetry(Retry getTableDataProfileRetry) {
    this.getTableDataProfileRetry = getTableDataProfileRetry;
  }

  public Retry getGetColumnDataProfileRetry() {
    return this.getColumnDataProfileRetry;
  }

  public void setGetColumnDataProfileRetry(Retry getColumnDataProfileRetry) {
    this.getColumnDataProfileRetry = getColumnDataProfileRetry;
  }

  public Retry getHybridInspectDlpJobRetry() {
    return this.hybridInspectDlpJobRetry;
  }

  public void setHybridInspectDlpJobRetry(Retry hybridInspectDlpJobRetry) {
    this.hybridInspectDlpJobRetry = hybridInspectDlpJobRetry;
  }

  public Retry getFinishDlpJobRetry() {
    return this.finishDlpJobRetry;
  }

  public void setFinishDlpJobRetry(Retry finishDlpJobRetry) {
    this.finishDlpJobRetry = finishDlpJobRetry;
  }
}
