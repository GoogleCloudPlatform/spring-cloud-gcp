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

package com.google.cloud.gsuiteaddons.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for GSuiteAddOns client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.gsuiteaddons.v1.g-suite-add-ons")
public class GSuiteAddOnsSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for getAuthorization. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getAuthorizationRetry;
  /**
   * Allow override of retry settings at method-level for createDeployment. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createDeploymentRetry;
  /**
   * Allow override of retry settings at method-level for replaceDeployment. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry replaceDeploymentRetry;
  /**
   * Allow override of retry settings at method-level for getDeployment. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getDeploymentRetry;
  /**
   * Allow override of retry settings at method-level for listDeployments. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listDeploymentsRetry;
  /**
   * Allow override of retry settings at method-level for deleteDeployment. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteDeploymentRetry;
  /**
   * Allow override of retry settings at method-level for installDeployment. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry installDeploymentRetry;
  /**
   * Allow override of retry settings at method-level for uninstallDeployment. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry uninstallDeploymentRetry;
  /**
   * Allow override of retry settings at method-level for getInstallStatus. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getInstallStatusRetry;

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

  public Retry getGetAuthorizationRetry() {
    return this.getAuthorizationRetry;
  }

  public void setGetAuthorizationRetry(Retry getAuthorizationRetry) {
    this.getAuthorizationRetry = getAuthorizationRetry;
  }

  public Retry getCreateDeploymentRetry() {
    return this.createDeploymentRetry;
  }

  public void setCreateDeploymentRetry(Retry createDeploymentRetry) {
    this.createDeploymentRetry = createDeploymentRetry;
  }

  public Retry getReplaceDeploymentRetry() {
    return this.replaceDeploymentRetry;
  }

  public void setReplaceDeploymentRetry(Retry replaceDeploymentRetry) {
    this.replaceDeploymentRetry = replaceDeploymentRetry;
  }

  public Retry getGetDeploymentRetry() {
    return this.getDeploymentRetry;
  }

  public void setGetDeploymentRetry(Retry getDeploymentRetry) {
    this.getDeploymentRetry = getDeploymentRetry;
  }

  public Retry getListDeploymentsRetry() {
    return this.listDeploymentsRetry;
  }

  public void setListDeploymentsRetry(Retry listDeploymentsRetry) {
    this.listDeploymentsRetry = listDeploymentsRetry;
  }

  public Retry getDeleteDeploymentRetry() {
    return this.deleteDeploymentRetry;
  }

  public void setDeleteDeploymentRetry(Retry deleteDeploymentRetry) {
    this.deleteDeploymentRetry = deleteDeploymentRetry;
  }

  public Retry getInstallDeploymentRetry() {
    return this.installDeploymentRetry;
  }

  public void setInstallDeploymentRetry(Retry installDeploymentRetry) {
    this.installDeploymentRetry = installDeploymentRetry;
  }

  public Retry getUninstallDeploymentRetry() {
    return this.uninstallDeploymentRetry;
  }

  public void setUninstallDeploymentRetry(Retry uninstallDeploymentRetry) {
    this.uninstallDeploymentRetry = uninstallDeploymentRetry;
  }

  public Retry getGetInstallStatusRetry() {
    return this.getInstallStatusRetry;
  }

  public void setGetInstallStatusRetry(Retry getInstallStatusRetry) {
    this.getInstallStatusRetry = getInstallStatusRetry;
  }
}
