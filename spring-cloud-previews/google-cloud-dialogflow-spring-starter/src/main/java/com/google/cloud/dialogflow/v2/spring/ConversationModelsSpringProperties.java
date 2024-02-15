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

package com.google.cloud.dialogflow.v2.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for ConversationModels client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.dialogflow.v2.conversation-models")
public class ConversationModelsSpringProperties implements CredentialsSupplier {
  /** OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries. */
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials(
          "https://www.googleapis.com/auth/cloud-platform",
          "https://www.googleapis.com/auth/dialogflow");
  /** Quota project to use for billing. */
  private String quotaProjectId;
  /** Number of threads used for executors. */
  private Integer executorThreadCount;
  /** Allow override of default transport channel provider to use REST instead of gRPC. */
  private boolean useRest = false;
  /** Allow override of retry settings at service level, applying to all of its RPC methods. */
  @NestedConfigurationProperty private Retry retry;
  /**
   * Allow override of retry settings at method-level for getConversationModel. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getConversationModelRetry;
  /**
   * Allow override of retry settings at method-level for listConversationModels. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listConversationModelsRetry;
  /**
   * Allow override of retry settings at method-level for getConversationModelEvaluation. If
   * defined, this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getConversationModelEvaluationRetry;
  /**
   * Allow override of retry settings at method-level for listConversationModelEvaluations. If
   * defined, this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listConversationModelEvaluationsRetry;
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

  public Retry getGetConversationModelRetry() {
    return this.getConversationModelRetry;
  }

  public void setGetConversationModelRetry(Retry getConversationModelRetry) {
    this.getConversationModelRetry = getConversationModelRetry;
  }

  public Retry getListConversationModelsRetry() {
    return this.listConversationModelsRetry;
  }

  public void setListConversationModelsRetry(Retry listConversationModelsRetry) {
    this.listConversationModelsRetry = listConversationModelsRetry;
  }

  public Retry getGetConversationModelEvaluationRetry() {
    return this.getConversationModelEvaluationRetry;
  }

  public void setGetConversationModelEvaluationRetry(Retry getConversationModelEvaluationRetry) {
    this.getConversationModelEvaluationRetry = getConversationModelEvaluationRetry;
  }

  public Retry getListConversationModelEvaluationsRetry() {
    return this.listConversationModelEvaluationsRetry;
  }

  public void setListConversationModelEvaluationsRetry(
      Retry listConversationModelEvaluationsRetry) {
    this.listConversationModelEvaluationsRetry = listConversationModelEvaluationsRetry;
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
}
