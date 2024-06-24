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

package com.google.showcase.v1beta1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for Compliance client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.showcase.v1beta1.compliance")
public class ComplianceSpringProperties implements CredentialsSupplier {
  /** OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries. */
  @NestedConfigurationProperty private final Credentials credentials = new Credentials();
  /** Quota project to use for billing. */
  private String quotaProjectId;
  /** Number of threads used for executors. */
  private Integer executorThreadCount;
  /** Allow override of default transport channel provider to use REST instead of gRPC. */
  private boolean useRest = false;
  /** Allow override of retry settings at service level, applying to all of its RPC methods. */
  @NestedConfigurationProperty private Retry retry;
  /**
   * Allow override of retry settings at method-level for repeatDataBody. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry repeatDataBodyRetry;
  /**
   * Allow override of retry settings at method-level for repeatDataBodyInfo. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry repeatDataBodyInfoRetry;
  /**
   * Allow override of retry settings at method-level for repeatDataQuery. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry repeatDataQueryRetry;
  /**
   * Allow override of retry settings at method-level for repeatDataSimplePath. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry repeatDataSimplePathRetry;
  /**
   * Allow override of retry settings at method-level for repeatDataPathResource. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry repeatDataPathResourceRetry;
  /**
   * Allow override of retry settings at method-level for repeatDataPathTrailingResource. If
   * defined, this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry repeatDataPathTrailingResourceRetry;
  /**
   * Allow override of retry settings at method-level for repeatDataBodyPut. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry repeatDataBodyPutRetry;
  /**
   * Allow override of retry settings at method-level for repeatDataBodyPatch. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry repeatDataBodyPatchRetry;
  /**
   * Allow override of retry settings at method-level for getEnum. If defined, this takes precedence
   * over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getEnumRetry;
  /**
   * Allow override of retry settings at method-level for verifyEnum. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry verifyEnumRetry;
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

  public Retry getRepeatDataBodyRetry() {
    return this.repeatDataBodyRetry;
  }

  public void setRepeatDataBodyRetry(Retry repeatDataBodyRetry) {
    this.repeatDataBodyRetry = repeatDataBodyRetry;
  }

  public Retry getRepeatDataBodyInfoRetry() {
    return this.repeatDataBodyInfoRetry;
  }

  public void setRepeatDataBodyInfoRetry(Retry repeatDataBodyInfoRetry) {
    this.repeatDataBodyInfoRetry = repeatDataBodyInfoRetry;
  }

  public Retry getRepeatDataQueryRetry() {
    return this.repeatDataQueryRetry;
  }

  public void setRepeatDataQueryRetry(Retry repeatDataQueryRetry) {
    this.repeatDataQueryRetry = repeatDataQueryRetry;
  }

  public Retry getRepeatDataSimplePathRetry() {
    return this.repeatDataSimplePathRetry;
  }

  public void setRepeatDataSimplePathRetry(Retry repeatDataSimplePathRetry) {
    this.repeatDataSimplePathRetry = repeatDataSimplePathRetry;
  }

  public Retry getRepeatDataPathResourceRetry() {
    return this.repeatDataPathResourceRetry;
  }

  public void setRepeatDataPathResourceRetry(Retry repeatDataPathResourceRetry) {
    this.repeatDataPathResourceRetry = repeatDataPathResourceRetry;
  }

  public Retry getRepeatDataPathTrailingResourceRetry() {
    return this.repeatDataPathTrailingResourceRetry;
  }

  public void setRepeatDataPathTrailingResourceRetry(Retry repeatDataPathTrailingResourceRetry) {
    this.repeatDataPathTrailingResourceRetry = repeatDataPathTrailingResourceRetry;
  }

  public Retry getRepeatDataBodyPutRetry() {
    return this.repeatDataBodyPutRetry;
  }

  public void setRepeatDataBodyPutRetry(Retry repeatDataBodyPutRetry) {
    this.repeatDataBodyPutRetry = repeatDataBodyPutRetry;
  }

  public Retry getRepeatDataBodyPatchRetry() {
    return this.repeatDataBodyPatchRetry;
  }

  public void setRepeatDataBodyPatchRetry(Retry repeatDataBodyPatchRetry) {
    this.repeatDataBodyPatchRetry = repeatDataBodyPatchRetry;
  }

  public Retry getGetEnumRetry() {
    return this.getEnumRetry;
  }

  public void setGetEnumRetry(Retry getEnumRetry) {
    this.getEnumRetry = getEnumRetry;
  }

  public Retry getVerifyEnumRetry() {
    return this.verifyEnumRetry;
  }

  public void setVerifyEnumRetry(Retry verifyEnumRetry) {
    this.verifyEnumRetry = verifyEnumRetry;
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
