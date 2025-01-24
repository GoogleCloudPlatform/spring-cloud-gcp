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

package com.google.cloud.api.servicemanagement.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for ServiceManager client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.api.servicemanagement.v1.service-manager")
public class ServiceManagerSpringProperties implements CredentialsSupplier {
  /** OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries. */
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials(
          "https://www.googleapis.com/auth/cloud-platform",
          "https://www.googleapis.com/auth/cloud-platform.read-only",
          "https://www.googleapis.com/auth/service.management",
          "https://www.googleapis.com/auth/service.management.readonly");
  /** Quota project to use for billing. */
  private String quotaProjectId;
  /** Number of threads used for executors. */
  private Integer executorThreadCount;
  /** Allow override of default transport channel provider to use REST instead of gRPC. */
  private boolean useRest = false;
  /** Allow override of retry settings at service level, applying to all of its RPC methods. */
  @NestedConfigurationProperty private Retry retry;
  /**
   * Allow override of retry settings at method-level for listServices. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listServicesRetry;
  /**
   * Allow override of retry settings at method-level for getService. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getServiceRetry;
  /**
   * Allow override of retry settings at method-level for listServiceConfigs. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listServiceConfigsRetry;
  /**
   * Allow override of retry settings at method-level for getServiceConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getServiceConfigRetry;
  /**
   * Allow override of retry settings at method-level for createServiceConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createServiceConfigRetry;
  /**
   * Allow override of retry settings at method-level for listServiceRollouts. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listServiceRolloutsRetry;
  /**
   * Allow override of retry settings at method-level for getServiceRollout. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getServiceRolloutRetry;
  /**
   * Allow override of retry settings at method-level for generateConfigReport. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry generateConfigReportRetry;
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

  public Retry getListServicesRetry() {
    return this.listServicesRetry;
  }

  public void setListServicesRetry(Retry listServicesRetry) {
    this.listServicesRetry = listServicesRetry;
  }

  public Retry getGetServiceRetry() {
    return this.getServiceRetry;
  }

  public void setGetServiceRetry(Retry getServiceRetry) {
    this.getServiceRetry = getServiceRetry;
  }

  public Retry getListServiceConfigsRetry() {
    return this.listServiceConfigsRetry;
  }

  public void setListServiceConfigsRetry(Retry listServiceConfigsRetry) {
    this.listServiceConfigsRetry = listServiceConfigsRetry;
  }

  public Retry getGetServiceConfigRetry() {
    return this.getServiceConfigRetry;
  }

  public void setGetServiceConfigRetry(Retry getServiceConfigRetry) {
    this.getServiceConfigRetry = getServiceConfigRetry;
  }

  public Retry getCreateServiceConfigRetry() {
    return this.createServiceConfigRetry;
  }

  public void setCreateServiceConfigRetry(Retry createServiceConfigRetry) {
    this.createServiceConfigRetry = createServiceConfigRetry;
  }

  public Retry getListServiceRolloutsRetry() {
    return this.listServiceRolloutsRetry;
  }

  public void setListServiceRolloutsRetry(Retry listServiceRolloutsRetry) {
    this.listServiceRolloutsRetry = listServiceRolloutsRetry;
  }

  public Retry getGetServiceRolloutRetry() {
    return this.getServiceRolloutRetry;
  }

  public void setGetServiceRolloutRetry(Retry getServiceRolloutRetry) {
    this.getServiceRolloutRetry = getServiceRolloutRetry;
  }

  public Retry getGenerateConfigReportRetry() {
    return this.generateConfigReportRetry;
  }

  public void setGenerateConfigReportRetry(Retry generateConfigReportRetry) {
    this.generateConfigReportRetry = generateConfigReportRetry;
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
