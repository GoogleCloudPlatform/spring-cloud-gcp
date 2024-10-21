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

package com.google.cloud.contactcenterinsights.v1.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for ContactCenterInsights client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.contactcenterinsights.v1.contact-center-insights")
public class ContactCenterInsightsSpringProperties implements CredentialsSupplier {
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
   * Allow override of retry settings at method-level for createConversation. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createConversationRetry;
  /**
   * Allow override of retry settings at method-level for updateConversation. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateConversationRetry;
  /**
   * Allow override of retry settings at method-level for getConversation. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getConversationRetry;
  /**
   * Allow override of retry settings at method-level for listConversations. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listConversationsRetry;
  /**
   * Allow override of retry settings at method-level for deleteConversation. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteConversationRetry;
  /**
   * Allow override of retry settings at method-level for getAnalysis. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getAnalysisRetry;
  /**
   * Allow override of retry settings at method-level for listAnalyses. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listAnalysesRetry;
  /**
   * Allow override of retry settings at method-level for deleteAnalysis. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteAnalysisRetry;
  /**
   * Allow override of retry settings at method-level for updateIssueModel. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateIssueModelRetry;
  /**
   * Allow override of retry settings at method-level for getIssueModel. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getIssueModelRetry;
  /**
   * Allow override of retry settings at method-level for listIssueModels. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listIssueModelsRetry;
  /**
   * Allow override of retry settings at method-level for getIssue. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getIssueRetry;
  /**
   * Allow override of retry settings at method-level for listIssues. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listIssuesRetry;
  /**
   * Allow override of retry settings at method-level for updateIssue. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateIssueRetry;
  /**
   * Allow override of retry settings at method-level for deleteIssue. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteIssueRetry;
  /**
   * Allow override of retry settings at method-level for calculateIssueModelStats. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry calculateIssueModelStatsRetry;
  /**
   * Allow override of retry settings at method-level for createPhraseMatcher. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createPhraseMatcherRetry;
  /**
   * Allow override of retry settings at method-level for getPhraseMatcher. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getPhraseMatcherRetry;
  /**
   * Allow override of retry settings at method-level for listPhraseMatchers. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listPhraseMatchersRetry;
  /**
   * Allow override of retry settings at method-level for deletePhraseMatcher. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deletePhraseMatcherRetry;
  /**
   * Allow override of retry settings at method-level for updatePhraseMatcher. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updatePhraseMatcherRetry;
  /**
   * Allow override of retry settings at method-level for calculateStats. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry calculateStatsRetry;
  /**
   * Allow override of retry settings at method-level for getSettings. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getSettingsRetry;
  /**
   * Allow override of retry settings at method-level for updateSettings. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateSettingsRetry;
  /**
   * Allow override of retry settings at method-level for getEncryptionSpec. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getEncryptionSpecRetry;
  /**
   * Allow override of retry settings at method-level for createView. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createViewRetry;
  /**
   * Allow override of retry settings at method-level for getView. If defined, this takes precedence
   * over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getViewRetry;
  /**
   * Allow override of retry settings at method-level for listViews. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listViewsRetry;
  /**
   * Allow override of retry settings at method-level for updateView. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateViewRetry;
  /**
   * Allow override of retry settings at method-level for deleteView. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteViewRetry;

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

  public Retry getCreateConversationRetry() {
    return this.createConversationRetry;
  }

  public void setCreateConversationRetry(Retry createConversationRetry) {
    this.createConversationRetry = createConversationRetry;
  }

  public Retry getUpdateConversationRetry() {
    return this.updateConversationRetry;
  }

  public void setUpdateConversationRetry(Retry updateConversationRetry) {
    this.updateConversationRetry = updateConversationRetry;
  }

  public Retry getGetConversationRetry() {
    return this.getConversationRetry;
  }

  public void setGetConversationRetry(Retry getConversationRetry) {
    this.getConversationRetry = getConversationRetry;
  }

  public Retry getListConversationsRetry() {
    return this.listConversationsRetry;
  }

  public void setListConversationsRetry(Retry listConversationsRetry) {
    this.listConversationsRetry = listConversationsRetry;
  }

  public Retry getDeleteConversationRetry() {
    return this.deleteConversationRetry;
  }

  public void setDeleteConversationRetry(Retry deleteConversationRetry) {
    this.deleteConversationRetry = deleteConversationRetry;
  }

  public Retry getGetAnalysisRetry() {
    return this.getAnalysisRetry;
  }

  public void setGetAnalysisRetry(Retry getAnalysisRetry) {
    this.getAnalysisRetry = getAnalysisRetry;
  }

  public Retry getListAnalysesRetry() {
    return this.listAnalysesRetry;
  }

  public void setListAnalysesRetry(Retry listAnalysesRetry) {
    this.listAnalysesRetry = listAnalysesRetry;
  }

  public Retry getDeleteAnalysisRetry() {
    return this.deleteAnalysisRetry;
  }

  public void setDeleteAnalysisRetry(Retry deleteAnalysisRetry) {
    this.deleteAnalysisRetry = deleteAnalysisRetry;
  }

  public Retry getUpdateIssueModelRetry() {
    return this.updateIssueModelRetry;
  }

  public void setUpdateIssueModelRetry(Retry updateIssueModelRetry) {
    this.updateIssueModelRetry = updateIssueModelRetry;
  }

  public Retry getGetIssueModelRetry() {
    return this.getIssueModelRetry;
  }

  public void setGetIssueModelRetry(Retry getIssueModelRetry) {
    this.getIssueModelRetry = getIssueModelRetry;
  }

  public Retry getListIssueModelsRetry() {
    return this.listIssueModelsRetry;
  }

  public void setListIssueModelsRetry(Retry listIssueModelsRetry) {
    this.listIssueModelsRetry = listIssueModelsRetry;
  }

  public Retry getGetIssueRetry() {
    return this.getIssueRetry;
  }

  public void setGetIssueRetry(Retry getIssueRetry) {
    this.getIssueRetry = getIssueRetry;
  }

  public Retry getListIssuesRetry() {
    return this.listIssuesRetry;
  }

  public void setListIssuesRetry(Retry listIssuesRetry) {
    this.listIssuesRetry = listIssuesRetry;
  }

  public Retry getUpdateIssueRetry() {
    return this.updateIssueRetry;
  }

  public void setUpdateIssueRetry(Retry updateIssueRetry) {
    this.updateIssueRetry = updateIssueRetry;
  }

  public Retry getDeleteIssueRetry() {
    return this.deleteIssueRetry;
  }

  public void setDeleteIssueRetry(Retry deleteIssueRetry) {
    this.deleteIssueRetry = deleteIssueRetry;
  }

  public Retry getCalculateIssueModelStatsRetry() {
    return this.calculateIssueModelStatsRetry;
  }

  public void setCalculateIssueModelStatsRetry(Retry calculateIssueModelStatsRetry) {
    this.calculateIssueModelStatsRetry = calculateIssueModelStatsRetry;
  }

  public Retry getCreatePhraseMatcherRetry() {
    return this.createPhraseMatcherRetry;
  }

  public void setCreatePhraseMatcherRetry(Retry createPhraseMatcherRetry) {
    this.createPhraseMatcherRetry = createPhraseMatcherRetry;
  }

  public Retry getGetPhraseMatcherRetry() {
    return this.getPhraseMatcherRetry;
  }

  public void setGetPhraseMatcherRetry(Retry getPhraseMatcherRetry) {
    this.getPhraseMatcherRetry = getPhraseMatcherRetry;
  }

  public Retry getListPhraseMatchersRetry() {
    return this.listPhraseMatchersRetry;
  }

  public void setListPhraseMatchersRetry(Retry listPhraseMatchersRetry) {
    this.listPhraseMatchersRetry = listPhraseMatchersRetry;
  }

  public Retry getDeletePhraseMatcherRetry() {
    return this.deletePhraseMatcherRetry;
  }

  public void setDeletePhraseMatcherRetry(Retry deletePhraseMatcherRetry) {
    this.deletePhraseMatcherRetry = deletePhraseMatcherRetry;
  }

  public Retry getUpdatePhraseMatcherRetry() {
    return this.updatePhraseMatcherRetry;
  }

  public void setUpdatePhraseMatcherRetry(Retry updatePhraseMatcherRetry) {
    this.updatePhraseMatcherRetry = updatePhraseMatcherRetry;
  }

  public Retry getCalculateStatsRetry() {
    return this.calculateStatsRetry;
  }

  public void setCalculateStatsRetry(Retry calculateStatsRetry) {
    this.calculateStatsRetry = calculateStatsRetry;
  }

  public Retry getGetSettingsRetry() {
    return this.getSettingsRetry;
  }

  public void setGetSettingsRetry(Retry getSettingsRetry) {
    this.getSettingsRetry = getSettingsRetry;
  }

  public Retry getUpdateSettingsRetry() {
    return this.updateSettingsRetry;
  }

  public void setUpdateSettingsRetry(Retry updateSettingsRetry) {
    this.updateSettingsRetry = updateSettingsRetry;
  }

  public Retry getGetEncryptionSpecRetry() {
    return this.getEncryptionSpecRetry;
  }

  public void setGetEncryptionSpecRetry(Retry getEncryptionSpecRetry) {
    this.getEncryptionSpecRetry = getEncryptionSpecRetry;
  }

  public Retry getCreateViewRetry() {
    return this.createViewRetry;
  }

  public void setCreateViewRetry(Retry createViewRetry) {
    this.createViewRetry = createViewRetry;
  }

  public Retry getGetViewRetry() {
    return this.getViewRetry;
  }

  public void setGetViewRetry(Retry getViewRetry) {
    this.getViewRetry = getViewRetry;
  }

  public Retry getListViewsRetry() {
    return this.listViewsRetry;
  }

  public void setListViewsRetry(Retry listViewsRetry) {
    this.listViewsRetry = listViewsRetry;
  }

  public Retry getUpdateViewRetry() {
    return this.updateViewRetry;
  }

  public void setUpdateViewRetry(Retry updateViewRetry) {
    this.updateViewRetry = updateViewRetry;
  }

  public Retry getDeleteViewRetry() {
    return this.deleteViewRetry;
  }

  public void setDeleteViewRetry(Retry deleteViewRetry) {
    this.deleteViewRetry = deleteViewRetry;
  }
}
