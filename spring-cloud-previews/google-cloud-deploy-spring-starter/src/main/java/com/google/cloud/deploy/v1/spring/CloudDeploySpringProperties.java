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

package com.google.cloud.deploy.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for CloudDeploy client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.deploy.v1.cloud-deploy")
public class CloudDeploySpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for listDeliveryPipelines. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listDeliveryPipelinesRetry;
  /**
   * Allow override of retry settings at method-level for getDeliveryPipeline. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getDeliveryPipelineRetry;
  /**
   * Allow override of retry settings at method-level for listTargets. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listTargetsRetry;
  /**
   * Allow override of retry settings at method-level for rollbackTarget. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry rollbackTargetRetry;
  /**
   * Allow override of retry settings at method-level for getTarget. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getTargetRetry;
  /**
   * Allow override of retry settings at method-level for listCustomTargetTypes. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listCustomTargetTypesRetry;
  /**
   * Allow override of retry settings at method-level for getCustomTargetType. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getCustomTargetTypeRetry;
  /**
   * Allow override of retry settings at method-level for listReleases. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listReleasesRetry;
  /**
   * Allow override of retry settings at method-level for getRelease. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getReleaseRetry;
  /**
   * Allow override of retry settings at method-level for abandonRelease. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry abandonReleaseRetry;
  /**
   * Allow override of retry settings at method-level for listDeployPolicies. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listDeployPoliciesRetry;
  /**
   * Allow override of retry settings at method-level for getDeployPolicy. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getDeployPolicyRetry;
  /**
   * Allow override of retry settings at method-level for approveRollout. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry approveRolloutRetry;
  /**
   * Allow override of retry settings at method-level for advanceRollout. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry advanceRolloutRetry;
  /**
   * Allow override of retry settings at method-level for cancelRollout. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry cancelRolloutRetry;
  /**
   * Allow override of retry settings at method-level for listRollouts. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listRolloutsRetry;
  /**
   * Allow override of retry settings at method-level for getRollout. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getRolloutRetry;
  /**
   * Allow override of retry settings at method-level for ignoreJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry ignoreJobRetry;
  /**
   * Allow override of retry settings at method-level for retryJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry retryJobRetry;
  /**
   * Allow override of retry settings at method-level for listJobRuns. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listJobRunsRetry;
  /**
   * Allow override of retry settings at method-level for getJobRun. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getJobRunRetry;
  /**
   * Allow override of retry settings at method-level for terminateJobRun. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry terminateJobRunRetry;
  /**
   * Allow override of retry settings at method-level for getConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getConfigRetry;
  /**
   * Allow override of retry settings at method-level for getAutomation. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getAutomationRetry;
  /**
   * Allow override of retry settings at method-level for listAutomations. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listAutomationsRetry;
  /**
   * Allow override of retry settings at method-level for getAutomationRun. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getAutomationRunRetry;
  /**
   * Allow override of retry settings at method-level for listAutomationRuns. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listAutomationRunsRetry;
  /**
   * Allow override of retry settings at method-level for cancelAutomationRun. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry cancelAutomationRunRetry;
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

  public Retry getListDeliveryPipelinesRetry() {
    return this.listDeliveryPipelinesRetry;
  }

  public void setListDeliveryPipelinesRetry(Retry listDeliveryPipelinesRetry) {
    this.listDeliveryPipelinesRetry = listDeliveryPipelinesRetry;
  }

  public Retry getGetDeliveryPipelineRetry() {
    return this.getDeliveryPipelineRetry;
  }

  public void setGetDeliveryPipelineRetry(Retry getDeliveryPipelineRetry) {
    this.getDeliveryPipelineRetry = getDeliveryPipelineRetry;
  }

  public Retry getListTargetsRetry() {
    return this.listTargetsRetry;
  }

  public void setListTargetsRetry(Retry listTargetsRetry) {
    this.listTargetsRetry = listTargetsRetry;
  }

  public Retry getRollbackTargetRetry() {
    return this.rollbackTargetRetry;
  }

  public void setRollbackTargetRetry(Retry rollbackTargetRetry) {
    this.rollbackTargetRetry = rollbackTargetRetry;
  }

  public Retry getGetTargetRetry() {
    return this.getTargetRetry;
  }

  public void setGetTargetRetry(Retry getTargetRetry) {
    this.getTargetRetry = getTargetRetry;
  }

  public Retry getListCustomTargetTypesRetry() {
    return this.listCustomTargetTypesRetry;
  }

  public void setListCustomTargetTypesRetry(Retry listCustomTargetTypesRetry) {
    this.listCustomTargetTypesRetry = listCustomTargetTypesRetry;
  }

  public Retry getGetCustomTargetTypeRetry() {
    return this.getCustomTargetTypeRetry;
  }

  public void setGetCustomTargetTypeRetry(Retry getCustomTargetTypeRetry) {
    this.getCustomTargetTypeRetry = getCustomTargetTypeRetry;
  }

  public Retry getListReleasesRetry() {
    return this.listReleasesRetry;
  }

  public void setListReleasesRetry(Retry listReleasesRetry) {
    this.listReleasesRetry = listReleasesRetry;
  }

  public Retry getGetReleaseRetry() {
    return this.getReleaseRetry;
  }

  public void setGetReleaseRetry(Retry getReleaseRetry) {
    this.getReleaseRetry = getReleaseRetry;
  }

  public Retry getAbandonReleaseRetry() {
    return this.abandonReleaseRetry;
  }

  public void setAbandonReleaseRetry(Retry abandonReleaseRetry) {
    this.abandonReleaseRetry = abandonReleaseRetry;
  }

  public Retry getListDeployPoliciesRetry() {
    return this.listDeployPoliciesRetry;
  }

  public void setListDeployPoliciesRetry(Retry listDeployPoliciesRetry) {
    this.listDeployPoliciesRetry = listDeployPoliciesRetry;
  }

  public Retry getGetDeployPolicyRetry() {
    return this.getDeployPolicyRetry;
  }

  public void setGetDeployPolicyRetry(Retry getDeployPolicyRetry) {
    this.getDeployPolicyRetry = getDeployPolicyRetry;
  }

  public Retry getApproveRolloutRetry() {
    return this.approveRolloutRetry;
  }

  public void setApproveRolloutRetry(Retry approveRolloutRetry) {
    this.approveRolloutRetry = approveRolloutRetry;
  }

  public Retry getAdvanceRolloutRetry() {
    return this.advanceRolloutRetry;
  }

  public void setAdvanceRolloutRetry(Retry advanceRolloutRetry) {
    this.advanceRolloutRetry = advanceRolloutRetry;
  }

  public Retry getCancelRolloutRetry() {
    return this.cancelRolloutRetry;
  }

  public void setCancelRolloutRetry(Retry cancelRolloutRetry) {
    this.cancelRolloutRetry = cancelRolloutRetry;
  }

  public Retry getListRolloutsRetry() {
    return this.listRolloutsRetry;
  }

  public void setListRolloutsRetry(Retry listRolloutsRetry) {
    this.listRolloutsRetry = listRolloutsRetry;
  }

  public Retry getGetRolloutRetry() {
    return this.getRolloutRetry;
  }

  public void setGetRolloutRetry(Retry getRolloutRetry) {
    this.getRolloutRetry = getRolloutRetry;
  }

  public Retry getIgnoreJobRetry() {
    return this.ignoreJobRetry;
  }

  public void setIgnoreJobRetry(Retry ignoreJobRetry) {
    this.ignoreJobRetry = ignoreJobRetry;
  }

  public Retry getRetryJobRetry() {
    return this.retryJobRetry;
  }

  public void setRetryJobRetry(Retry retryJobRetry) {
    this.retryJobRetry = retryJobRetry;
  }

  public Retry getListJobRunsRetry() {
    return this.listJobRunsRetry;
  }

  public void setListJobRunsRetry(Retry listJobRunsRetry) {
    this.listJobRunsRetry = listJobRunsRetry;
  }

  public Retry getGetJobRunRetry() {
    return this.getJobRunRetry;
  }

  public void setGetJobRunRetry(Retry getJobRunRetry) {
    this.getJobRunRetry = getJobRunRetry;
  }

  public Retry getTerminateJobRunRetry() {
    return this.terminateJobRunRetry;
  }

  public void setTerminateJobRunRetry(Retry terminateJobRunRetry) {
    this.terminateJobRunRetry = terminateJobRunRetry;
  }

  public Retry getGetConfigRetry() {
    return this.getConfigRetry;
  }

  public void setGetConfigRetry(Retry getConfigRetry) {
    this.getConfigRetry = getConfigRetry;
  }

  public Retry getGetAutomationRetry() {
    return this.getAutomationRetry;
  }

  public void setGetAutomationRetry(Retry getAutomationRetry) {
    this.getAutomationRetry = getAutomationRetry;
  }

  public Retry getListAutomationsRetry() {
    return this.listAutomationsRetry;
  }

  public void setListAutomationsRetry(Retry listAutomationsRetry) {
    this.listAutomationsRetry = listAutomationsRetry;
  }

  public Retry getGetAutomationRunRetry() {
    return this.getAutomationRunRetry;
  }

  public void setGetAutomationRunRetry(Retry getAutomationRunRetry) {
    this.getAutomationRunRetry = getAutomationRunRetry;
  }

  public Retry getListAutomationRunsRetry() {
    return this.listAutomationRunsRetry;
  }

  public void setListAutomationRunsRetry(Retry listAutomationRunsRetry) {
    this.listAutomationRunsRetry = listAutomationRunsRetry;
  }

  public Retry getCancelAutomationRunRetry() {
    return this.cancelAutomationRunRetry;
  }

  public void setCancelAutomationRunRetry(Retry cancelAutomationRunRetry) {
    this.cancelAutomationRunRetry = cancelAutomationRunRetry;
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
