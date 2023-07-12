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

package com.google.cloud.eventarc.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for Eventarc client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.eventarc.v1.eventarc")
public class EventarcSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for getTrigger. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getTriggerRetry;
  /**
   * Allow override of retry settings at method-level for listTriggers. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listTriggersRetry;
  /**
   * Allow override of retry settings at method-level for getChannel. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getChannelRetry;
  /**
   * Allow override of retry settings at method-level for listChannels. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listChannelsRetry;
  /**
   * Allow override of retry settings at method-level for getProvider. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getProviderRetry;
  /**
   * Allow override of retry settings at method-level for listProviders. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listProvidersRetry;
  /**
   * Allow override of retry settings at method-level for getChannelConnection. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getChannelConnectionRetry;
  /**
   * Allow override of retry settings at method-level for listChannelConnections. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listChannelConnectionsRetry;
  /**
   * Allow override of retry settings at method-level for getGoogleChannelConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getGoogleChannelConfigRetry;
  /**
   * Allow override of retry settings at method-level for updateGoogleChannelConfig. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateGoogleChannelConfigRetry;
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

  public Retry getGetTriggerRetry() {
    return this.getTriggerRetry;
  }

  public void setGetTriggerRetry(Retry getTriggerRetry) {
    this.getTriggerRetry = getTriggerRetry;
  }

  public Retry getListTriggersRetry() {
    return this.listTriggersRetry;
  }

  public void setListTriggersRetry(Retry listTriggersRetry) {
    this.listTriggersRetry = listTriggersRetry;
  }

  public Retry getGetChannelRetry() {
    return this.getChannelRetry;
  }

  public void setGetChannelRetry(Retry getChannelRetry) {
    this.getChannelRetry = getChannelRetry;
  }

  public Retry getListChannelsRetry() {
    return this.listChannelsRetry;
  }

  public void setListChannelsRetry(Retry listChannelsRetry) {
    this.listChannelsRetry = listChannelsRetry;
  }

  public Retry getGetProviderRetry() {
    return this.getProviderRetry;
  }

  public void setGetProviderRetry(Retry getProviderRetry) {
    this.getProviderRetry = getProviderRetry;
  }

  public Retry getListProvidersRetry() {
    return this.listProvidersRetry;
  }

  public void setListProvidersRetry(Retry listProvidersRetry) {
    this.listProvidersRetry = listProvidersRetry;
  }

  public Retry getGetChannelConnectionRetry() {
    return this.getChannelConnectionRetry;
  }

  public void setGetChannelConnectionRetry(Retry getChannelConnectionRetry) {
    this.getChannelConnectionRetry = getChannelConnectionRetry;
  }

  public Retry getListChannelConnectionsRetry() {
    return this.listChannelConnectionsRetry;
  }

  public void setListChannelConnectionsRetry(Retry listChannelConnectionsRetry) {
    this.listChannelConnectionsRetry = listChannelConnectionsRetry;
  }

  public Retry getGetGoogleChannelConfigRetry() {
    return this.getGoogleChannelConfigRetry;
  }

  public void setGetGoogleChannelConfigRetry(Retry getGoogleChannelConfigRetry) {
    this.getGoogleChannelConfigRetry = getGoogleChannelConfigRetry;
  }

  public Retry getUpdateGoogleChannelConfigRetry() {
    return this.updateGoogleChannelConfigRetry;
  }

  public void setUpdateGoogleChannelConfigRetry(Retry updateGoogleChannelConfigRetry) {
    this.updateGoogleChannelConfigRetry = updateGoogleChannelConfigRetry;
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
