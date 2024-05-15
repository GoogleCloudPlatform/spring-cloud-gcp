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

package com.google.cloud.aiplatform.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for FeatureOnlineStoreAdminService client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.aiplatform.v1.feature-online-store-admin-service")
public class FeatureOnlineStoreAdminServiceSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for getFeatureOnlineStore. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getFeatureOnlineStoreRetry;
  /**
   * Allow override of retry settings at method-level for listFeatureOnlineStores. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listFeatureOnlineStoresRetry;
  /**
   * Allow override of retry settings at method-level for getFeatureView. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getFeatureViewRetry;
  /**
   * Allow override of retry settings at method-level for listFeatureViews. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listFeatureViewsRetry;
  /**
   * Allow override of retry settings at method-level for syncFeatureView. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry syncFeatureViewRetry;
  /**
   * Allow override of retry settings at method-level for getFeatureViewSync. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getFeatureViewSyncRetry;
  /**
   * Allow override of retry settings at method-level for listFeatureViewSyncs. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listFeatureViewSyncsRetry;
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

  public Retry getGetFeatureOnlineStoreRetry() {
    return this.getFeatureOnlineStoreRetry;
  }

  public void setGetFeatureOnlineStoreRetry(Retry getFeatureOnlineStoreRetry) {
    this.getFeatureOnlineStoreRetry = getFeatureOnlineStoreRetry;
  }

  public Retry getListFeatureOnlineStoresRetry() {
    return this.listFeatureOnlineStoresRetry;
  }

  public void setListFeatureOnlineStoresRetry(Retry listFeatureOnlineStoresRetry) {
    this.listFeatureOnlineStoresRetry = listFeatureOnlineStoresRetry;
  }

  public Retry getGetFeatureViewRetry() {
    return this.getFeatureViewRetry;
  }

  public void setGetFeatureViewRetry(Retry getFeatureViewRetry) {
    this.getFeatureViewRetry = getFeatureViewRetry;
  }

  public Retry getListFeatureViewsRetry() {
    return this.listFeatureViewsRetry;
  }

  public void setListFeatureViewsRetry(Retry listFeatureViewsRetry) {
    this.listFeatureViewsRetry = listFeatureViewsRetry;
  }

  public Retry getSyncFeatureViewRetry() {
    return this.syncFeatureViewRetry;
  }

  public void setSyncFeatureViewRetry(Retry syncFeatureViewRetry) {
    this.syncFeatureViewRetry = syncFeatureViewRetry;
  }

  public Retry getGetFeatureViewSyncRetry() {
    return this.getFeatureViewSyncRetry;
  }

  public void setGetFeatureViewSyncRetry(Retry getFeatureViewSyncRetry) {
    this.getFeatureViewSyncRetry = getFeatureViewSyncRetry;
  }

  public Retry getListFeatureViewSyncsRetry() {
    return this.listFeatureViewSyncsRetry;
  }

  public void setListFeatureViewSyncsRetry(Retry listFeatureViewSyncsRetry) {
    this.listFeatureViewSyncsRetry = listFeatureViewSyncsRetry;
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
