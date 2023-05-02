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

package com.google.cloud.billing.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for CloudBilling client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.billing.v1.cloud-billing")
public class CloudBillingSpringProperties implements CredentialsSupplier {
  /** OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries. */
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials(
          "https://www.googleapis.com/auth/cloud-billing",
          "https://www.googleapis.com/auth/cloud-billing.readonly",
          "https://www.googleapis.com/auth/cloud-platform");
  /** Quota project to use for billing. */
  private String quotaProjectId;
  /** Number of threads used for executors. */
  private Integer executorThreadCount;
  /** Allow override of default transport channel provider to use REST instead of gRPC. */
  private boolean useRest = false;
  /** Allow override of retry settings at service level, applying to all of its RPC methods. */
  @NestedConfigurationProperty private Retry retry;
  /**
   * Allow override of retry settings at method-level for getBillingAccount. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getBillingAccountRetry;
  /**
   * Allow override of retry settings at method-level for listBillingAccounts. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listBillingAccountsRetry;
  /**
   * Allow override of retry settings at method-level for updateBillingAccount. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateBillingAccountRetry;
  /**
   * Allow override of retry settings at method-level for createBillingAccount. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createBillingAccountRetry;
  /**
   * Allow override of retry settings at method-level for listProjectBillingInfo. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listProjectBillingInfoRetry;
  /**
   * Allow override of retry settings at method-level for getProjectBillingInfo. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getProjectBillingInfoRetry;
  /**
   * Allow override of retry settings at method-level for updateProjectBillingInfo. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateProjectBillingInfoRetry;
  /**
   * Allow override of retry settings at method-level for getIamPolicy. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getIamPolicyRetry;
  /**
   * Allow override of retry settings at method-level for setIamPolicy. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry setIamPolicyRetry;
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

  public Retry getGetBillingAccountRetry() {
    return this.getBillingAccountRetry;
  }

  public void setGetBillingAccountRetry(Retry getBillingAccountRetry) {
    this.getBillingAccountRetry = getBillingAccountRetry;
  }

  public Retry getListBillingAccountsRetry() {
    return this.listBillingAccountsRetry;
  }

  public void setListBillingAccountsRetry(Retry listBillingAccountsRetry) {
    this.listBillingAccountsRetry = listBillingAccountsRetry;
  }

  public Retry getUpdateBillingAccountRetry() {
    return this.updateBillingAccountRetry;
  }

  public void setUpdateBillingAccountRetry(Retry updateBillingAccountRetry) {
    this.updateBillingAccountRetry = updateBillingAccountRetry;
  }

  public Retry getCreateBillingAccountRetry() {
    return this.createBillingAccountRetry;
  }

  public void setCreateBillingAccountRetry(Retry createBillingAccountRetry) {
    this.createBillingAccountRetry = createBillingAccountRetry;
  }

  public Retry getListProjectBillingInfoRetry() {
    return this.listProjectBillingInfoRetry;
  }

  public void setListProjectBillingInfoRetry(Retry listProjectBillingInfoRetry) {
    this.listProjectBillingInfoRetry = listProjectBillingInfoRetry;
  }

  public Retry getGetProjectBillingInfoRetry() {
    return this.getProjectBillingInfoRetry;
  }

  public void setGetProjectBillingInfoRetry(Retry getProjectBillingInfoRetry) {
    this.getProjectBillingInfoRetry = getProjectBillingInfoRetry;
  }

  public Retry getUpdateProjectBillingInfoRetry() {
    return this.updateProjectBillingInfoRetry;
  }

  public void setUpdateProjectBillingInfoRetry(Retry updateProjectBillingInfoRetry) {
    this.updateProjectBillingInfoRetry = updateProjectBillingInfoRetry;
  }

  public Retry getGetIamPolicyRetry() {
    return this.getIamPolicyRetry;
  }

  public void setGetIamPolicyRetry(Retry getIamPolicyRetry) {
    this.getIamPolicyRetry = getIamPolicyRetry;
  }

  public Retry getSetIamPolicyRetry() {
    return this.setIamPolicyRetry;
  }

  public void setSetIamPolicyRetry(Retry setIamPolicyRetry) {
    this.setIamPolicyRetry = setIamPolicyRetry;
  }

  public Retry getTestIamPermissionsRetry() {
    return this.testIamPermissionsRetry;
  }

  public void setTestIamPermissionsRetry(Retry testIamPermissionsRetry) {
    this.testIamPermissionsRetry = testIamPermissionsRetry;
  }
}
