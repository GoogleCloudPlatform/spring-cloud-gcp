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

package com.google.cloud.aiplatform.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for FeaturestoreService client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.aiplatform.v1.featurestore-service")
public class FeaturestoreServiceSpringProperties implements CredentialsSupplier {
  /** OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries. */
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials("https://www.googleapis.com/auth/cloud-platform");
  /** Quota project to use for billing. */
  private String quotaProjectId;
  /** Number of threads used for executors. */
  private Integer executorThreadCount;
  /** Allow override of retry settings at service level, applying to all of its RPC methods. */
  @NestedConfigurationProperty private Retry retry;
  /**
   * Allow override of retry settings at method-level for getFeaturestore. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getFeaturestoreRetry;
  /**
   * Allow override of retry settings at method-level for listFeaturestores. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listFeaturestoresRetry;
  /**
   * Allow override of retry settings at method-level for getEntityType. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getEntityTypeRetry;
  /**
   * Allow override of retry settings at method-level for listEntityTypes. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listEntityTypesRetry;
  /**
   * Allow override of retry settings at method-level for updateEntityType. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateEntityTypeRetry;
  /**
   * Allow override of retry settings at method-level for getFeature. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getFeatureRetry;
  /**
   * Allow override of retry settings at method-level for listFeatures. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listFeaturesRetry;
  /**
   * Allow override of retry settings at method-level for updateFeature. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateFeatureRetry;
  /**
   * Allow override of retry settings at method-level for searchFeatures. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry searchFeaturesRetry;
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

  public Retry getGetFeaturestoreRetry() {
    return this.getFeaturestoreRetry;
  }

  public void setGetFeaturestoreRetry(Retry getFeaturestoreRetry) {
    this.getFeaturestoreRetry = getFeaturestoreRetry;
  }

  public Retry getListFeaturestoresRetry() {
    return this.listFeaturestoresRetry;
  }

  public void setListFeaturestoresRetry(Retry listFeaturestoresRetry) {
    this.listFeaturestoresRetry = listFeaturestoresRetry;
  }

  public Retry getGetEntityTypeRetry() {
    return this.getEntityTypeRetry;
  }

  public void setGetEntityTypeRetry(Retry getEntityTypeRetry) {
    this.getEntityTypeRetry = getEntityTypeRetry;
  }

  public Retry getListEntityTypesRetry() {
    return this.listEntityTypesRetry;
  }

  public void setListEntityTypesRetry(Retry listEntityTypesRetry) {
    this.listEntityTypesRetry = listEntityTypesRetry;
  }

  public Retry getUpdateEntityTypeRetry() {
    return this.updateEntityTypeRetry;
  }

  public void setUpdateEntityTypeRetry(Retry updateEntityTypeRetry) {
    this.updateEntityTypeRetry = updateEntityTypeRetry;
  }

  public Retry getGetFeatureRetry() {
    return this.getFeatureRetry;
  }

  public void setGetFeatureRetry(Retry getFeatureRetry) {
    this.getFeatureRetry = getFeatureRetry;
  }

  public Retry getListFeaturesRetry() {
    return this.listFeaturesRetry;
  }

  public void setListFeaturesRetry(Retry listFeaturesRetry) {
    this.listFeaturesRetry = listFeaturesRetry;
  }

  public Retry getUpdateFeatureRetry() {
    return this.updateFeatureRetry;
  }

  public void setUpdateFeatureRetry(Retry updateFeatureRetry) {
    this.updateFeatureRetry = updateFeatureRetry;
  }

  public Retry getSearchFeaturesRetry() {
    return this.searchFeaturesRetry;
  }

  public void setSearchFeaturesRetry(Retry searchFeaturesRetry) {
    this.searchFeaturesRetry = searchFeaturesRetry;
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
