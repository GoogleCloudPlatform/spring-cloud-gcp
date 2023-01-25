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

package com.google.cloud.apigateway.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for ApiGatewayService client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.apigateway.v1.api-gateway-service")
public class ApiGatewayServiceSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for listGateways. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listGatewaysRetry;
  /**
   * Allow override of retry settings at method-level for getGateway. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getGatewayRetry;
  /**
   * Allow override of retry settings at method-level for listApis. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listApisRetry;
  /**
   * Allow override of retry settings at method-level for getApi. If defined, this takes precedence
   * over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getApiRetry;
  /**
   * Allow override of retry settings at method-level for listApiConfigs. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listApiConfigsRetry;
  /**
   * Allow override of retry settings at method-level for getApiConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getApiConfigRetry;

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

  public Retry getListGatewaysRetry() {
    return this.listGatewaysRetry;
  }

  public void setListGatewaysRetry(Retry listGatewaysRetry) {
    this.listGatewaysRetry = listGatewaysRetry;
  }

  public Retry getGetGatewayRetry() {
    return this.getGatewayRetry;
  }

  public void setGetGatewayRetry(Retry getGatewayRetry) {
    this.getGatewayRetry = getGatewayRetry;
  }

  public Retry getListApisRetry() {
    return this.listApisRetry;
  }

  public void setListApisRetry(Retry listApisRetry) {
    this.listApisRetry = listApisRetry;
  }

  public Retry getGetApiRetry() {
    return this.getApiRetry;
  }

  public void setGetApiRetry(Retry getApiRetry) {
    this.getApiRetry = getApiRetry;
  }

  public Retry getListApiConfigsRetry() {
    return this.listApiConfigsRetry;
  }

  public void setListApiConfigsRetry(Retry listApiConfigsRetry) {
    this.listApiConfigsRetry = listApiConfigsRetry;
  }

  public Retry getGetApiConfigRetry() {
    return this.getApiConfigRetry;
  }

  public void setGetApiConfigRetry(Retry getApiConfigRetry) {
    this.getApiConfigRetry = getApiConfigRetry;
  }
}
