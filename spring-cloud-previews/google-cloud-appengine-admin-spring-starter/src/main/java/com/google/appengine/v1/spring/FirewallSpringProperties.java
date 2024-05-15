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

package com.google.appengine.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for Firewall client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.appengine.v1.firewall")
public class FirewallSpringProperties implements CredentialsSupplier {
  /** OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries. */
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials(
          "https://www.googleapis.com/auth/appengine.admin",
          "https://www.googleapis.com/auth/cloud-platform",
          "https://www.googleapis.com/auth/cloud-platform.read-only");
  /** Quota project to use for billing. */
  private String quotaProjectId;
  /** Number of threads used for executors. */
  private Integer executorThreadCount;
  /** Allow override of default transport channel provider to use REST instead of gRPC. */
  private boolean useRest = false;
  /** Allow override of retry settings at service level, applying to all of its RPC methods. */
  @NestedConfigurationProperty private Retry retry;
  /**
   * Allow override of retry settings at method-level for listIngressRules. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listIngressRulesRetry;
  /**
   * Allow override of retry settings at method-level for batchUpdateIngressRules. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry batchUpdateIngressRulesRetry;
  /**
   * Allow override of retry settings at method-level for createIngressRule. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createIngressRuleRetry;
  /**
   * Allow override of retry settings at method-level for getIngressRule. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getIngressRuleRetry;
  /**
   * Allow override of retry settings at method-level for updateIngressRule. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateIngressRuleRetry;
  /**
   * Allow override of retry settings at method-level for deleteIngressRule. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteIngressRuleRetry;

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

  public Retry getListIngressRulesRetry() {
    return this.listIngressRulesRetry;
  }

  public void setListIngressRulesRetry(Retry listIngressRulesRetry) {
    this.listIngressRulesRetry = listIngressRulesRetry;
  }

  public Retry getBatchUpdateIngressRulesRetry() {
    return this.batchUpdateIngressRulesRetry;
  }

  public void setBatchUpdateIngressRulesRetry(Retry batchUpdateIngressRulesRetry) {
    this.batchUpdateIngressRulesRetry = batchUpdateIngressRulesRetry;
  }

  public Retry getCreateIngressRuleRetry() {
    return this.createIngressRuleRetry;
  }

  public void setCreateIngressRuleRetry(Retry createIngressRuleRetry) {
    this.createIngressRuleRetry = createIngressRuleRetry;
  }

  public Retry getGetIngressRuleRetry() {
    return this.getIngressRuleRetry;
  }

  public void setGetIngressRuleRetry(Retry getIngressRuleRetry) {
    this.getIngressRuleRetry = getIngressRuleRetry;
  }

  public Retry getUpdateIngressRuleRetry() {
    return this.updateIngressRuleRetry;
  }

  public void setUpdateIngressRuleRetry(Retry updateIngressRuleRetry) {
    this.updateIngressRuleRetry = updateIngressRuleRetry;
  }

  public Retry getDeleteIngressRuleRetry() {
    return this.deleteIngressRuleRetry;
  }

  public void setDeleteIngressRuleRetry(Retry deleteIngressRuleRetry) {
    this.deleteIngressRuleRetry = deleteIngressRuleRetry;
  }
}
