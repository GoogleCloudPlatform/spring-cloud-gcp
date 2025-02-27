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

package com.google.cloud.domains.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for Domains client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.domains.v1.domains")
public class DomainsSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for searchDomains. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry searchDomainsRetry;
  /**
   * Allow override of retry settings at method-level for retrieveRegisterParameters. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry retrieveRegisterParametersRetry;
  /**
   * Allow override of retry settings at method-level for retrieveTransferParameters. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry retrieveTransferParametersRetry;
  /**
   * Allow override of retry settings at method-level for listRegistrations. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listRegistrationsRetry;
  /**
   * Allow override of retry settings at method-level for getRegistration. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getRegistrationRetry;
  /**
   * Allow override of retry settings at method-level for retrieveAuthorizationCode. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry retrieveAuthorizationCodeRetry;
  /**
   * Allow override of retry settings at method-level for resetAuthorizationCode. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry resetAuthorizationCodeRetry;

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

  public Retry getSearchDomainsRetry() {
    return this.searchDomainsRetry;
  }

  public void setSearchDomainsRetry(Retry searchDomainsRetry) {
    this.searchDomainsRetry = searchDomainsRetry;
  }

  public Retry getRetrieveRegisterParametersRetry() {
    return this.retrieveRegisterParametersRetry;
  }

  public void setRetrieveRegisterParametersRetry(Retry retrieveRegisterParametersRetry) {
    this.retrieveRegisterParametersRetry = retrieveRegisterParametersRetry;
  }

  public Retry getRetrieveTransferParametersRetry() {
    return this.retrieveTransferParametersRetry;
  }

  public void setRetrieveTransferParametersRetry(Retry retrieveTransferParametersRetry) {
    this.retrieveTransferParametersRetry = retrieveTransferParametersRetry;
  }

  public Retry getListRegistrationsRetry() {
    return this.listRegistrationsRetry;
  }

  public void setListRegistrationsRetry(Retry listRegistrationsRetry) {
    this.listRegistrationsRetry = listRegistrationsRetry;
  }

  public Retry getGetRegistrationRetry() {
    return this.getRegistrationRetry;
  }

  public void setGetRegistrationRetry(Retry getRegistrationRetry) {
    this.getRegistrationRetry = getRegistrationRetry;
  }

  public Retry getRetrieveAuthorizationCodeRetry() {
    return this.retrieveAuthorizationCodeRetry;
  }

  public void setRetrieveAuthorizationCodeRetry(Retry retrieveAuthorizationCodeRetry) {
    this.retrieveAuthorizationCodeRetry = retrieveAuthorizationCodeRetry;
  }

  public Retry getResetAuthorizationCodeRetry() {
    return this.resetAuthorizationCodeRetry;
  }

  public void setResetAuthorizationCodeRetry(Retry resetAuthorizationCodeRetry) {
    this.resetAuthorizationCodeRetry = resetAuthorizationCodeRetry;
  }
}
