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

package com.google.cloud.speech.v2.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for Speech client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.speech.v2.speech")
public class SpeechSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for listRecognizers. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listRecognizersRetry;
  /**
   * Allow override of retry settings at method-level for getRecognizer. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getRecognizerRetry;
  /**
   * Allow override of retry settings at method-level for recognize. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry recognizeRetry;
  /**
   * Allow override of retry settings at method-level for getConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getConfigRetry;
  /**
   * Allow override of retry settings at method-level for updateConfig. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateConfigRetry;
  /**
   * Allow override of retry settings at method-level for listCustomClasses. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listCustomClassesRetry;
  /**
   * Allow override of retry settings at method-level for getCustomClass. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getCustomClassRetry;
  /**
   * Allow override of retry settings at method-level for listPhraseSets. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listPhraseSetsRetry;
  /**
   * Allow override of retry settings at method-level for getPhraseSet. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getPhraseSetRetry;
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

  public Retry getListRecognizersRetry() {
    return this.listRecognizersRetry;
  }

  public void setListRecognizersRetry(Retry listRecognizersRetry) {
    this.listRecognizersRetry = listRecognizersRetry;
  }

  public Retry getGetRecognizerRetry() {
    return this.getRecognizerRetry;
  }

  public void setGetRecognizerRetry(Retry getRecognizerRetry) {
    this.getRecognizerRetry = getRecognizerRetry;
  }

  public Retry getRecognizeRetry() {
    return this.recognizeRetry;
  }

  public void setRecognizeRetry(Retry recognizeRetry) {
    this.recognizeRetry = recognizeRetry;
  }

  public Retry getGetConfigRetry() {
    return this.getConfigRetry;
  }

  public void setGetConfigRetry(Retry getConfigRetry) {
    this.getConfigRetry = getConfigRetry;
  }

  public Retry getUpdateConfigRetry() {
    return this.updateConfigRetry;
  }

  public void setUpdateConfigRetry(Retry updateConfigRetry) {
    this.updateConfigRetry = updateConfigRetry;
  }

  public Retry getListCustomClassesRetry() {
    return this.listCustomClassesRetry;
  }

  public void setListCustomClassesRetry(Retry listCustomClassesRetry) {
    this.listCustomClassesRetry = listCustomClassesRetry;
  }

  public Retry getGetCustomClassRetry() {
    return this.getCustomClassRetry;
  }

  public void setGetCustomClassRetry(Retry getCustomClassRetry) {
    this.getCustomClassRetry = getCustomClassRetry;
  }

  public Retry getListPhraseSetsRetry() {
    return this.listPhraseSetsRetry;
  }

  public void setListPhraseSetsRetry(Retry listPhraseSetsRetry) {
    this.listPhraseSetsRetry = listPhraseSetsRetry;
  }

  public Retry getGetPhraseSetRetry() {
    return this.getPhraseSetRetry;
  }

  public void setGetPhraseSetRetry(Retry getPhraseSetRetry) {
    this.getPhraseSetRetry = getPhraseSetRetry;
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
