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

package com.google.cloud.cloudcontrolspartner.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for CloudControlsPartnerCore client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.cloudcontrolspartner.v1.cloud-controls-partner-core")
public class CloudControlsPartnerCoreSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for getWorkload. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getWorkloadRetry;
  /**
   * Allow override of retry settings at method-level for listWorkloads. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listWorkloadsRetry;
  /**
   * Allow override of retry settings at method-level for getCustomer. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getCustomerRetry;
  /**
   * Allow override of retry settings at method-level for listCustomers. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listCustomersRetry;
  /**
   * Allow override of retry settings at method-level for getEkmConnections. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getEkmConnectionsRetry;
  /**
   * Allow override of retry settings at method-level for getPartnerPermissions. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getPartnerPermissionsRetry;
  /**
   * Allow override of retry settings at method-level for listAccessApprovalRequests. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listAccessApprovalRequestsRetry;
  /**
   * Allow override of retry settings at method-level for getPartner. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getPartnerRetry;

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

  public Retry getGetWorkloadRetry() {
    return this.getWorkloadRetry;
  }

  public void setGetWorkloadRetry(Retry getWorkloadRetry) {
    this.getWorkloadRetry = getWorkloadRetry;
  }

  public Retry getListWorkloadsRetry() {
    return this.listWorkloadsRetry;
  }

  public void setListWorkloadsRetry(Retry listWorkloadsRetry) {
    this.listWorkloadsRetry = listWorkloadsRetry;
  }

  public Retry getGetCustomerRetry() {
    return this.getCustomerRetry;
  }

  public void setGetCustomerRetry(Retry getCustomerRetry) {
    this.getCustomerRetry = getCustomerRetry;
  }

  public Retry getListCustomersRetry() {
    return this.listCustomersRetry;
  }

  public void setListCustomersRetry(Retry listCustomersRetry) {
    this.listCustomersRetry = listCustomersRetry;
  }

  public Retry getGetEkmConnectionsRetry() {
    return this.getEkmConnectionsRetry;
  }

  public void setGetEkmConnectionsRetry(Retry getEkmConnectionsRetry) {
    this.getEkmConnectionsRetry = getEkmConnectionsRetry;
  }

  public Retry getGetPartnerPermissionsRetry() {
    return this.getPartnerPermissionsRetry;
  }

  public void setGetPartnerPermissionsRetry(Retry getPartnerPermissionsRetry) {
    this.getPartnerPermissionsRetry = getPartnerPermissionsRetry;
  }

  public Retry getListAccessApprovalRequestsRetry() {
    return this.listAccessApprovalRequestsRetry;
  }

  public void setListAccessApprovalRequestsRetry(Retry listAccessApprovalRequestsRetry) {
    this.listAccessApprovalRequestsRetry = listAccessApprovalRequestsRetry;
  }

  public Retry getGetPartnerRetry() {
    return this.getPartnerRetry;
  }

  public void setGetPartnerRetry(Retry getPartnerRetry) {
    this.getPartnerRetry = getPartnerRetry;
  }
}
