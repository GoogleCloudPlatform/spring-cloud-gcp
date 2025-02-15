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
   * Allow override of retry settings at method-level for createAnalysisRule. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createAnalysisRuleRetry;
  /**
   * Allow override of retry settings at method-level for getAnalysisRule. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getAnalysisRuleRetry;
  /**
   * Allow override of retry settings at method-level for listAnalysisRules. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listAnalysisRulesRetry;
  /**
   * Allow override of retry settings at method-level for updateAnalysisRule. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateAnalysisRuleRetry;
  /**
   * Allow override of retry settings at method-level for deleteAnalysisRule. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteAnalysisRuleRetry;
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
  /**
   * Allow override of retry settings at method-level for createQaQuestion. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createQaQuestionRetry;
  /**
   * Allow override of retry settings at method-level for getQaQuestion. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getQaQuestionRetry;
  /**
   * Allow override of retry settings at method-level for updateQaQuestion. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateQaQuestionRetry;
  /**
   * Allow override of retry settings at method-level for deleteQaQuestion. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteQaQuestionRetry;
  /**
   * Allow override of retry settings at method-level for listQaQuestions. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listQaQuestionsRetry;
  /**
   * Allow override of retry settings at method-level for createQaScorecard. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createQaScorecardRetry;
  /**
   * Allow override of retry settings at method-level for getQaScorecard. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getQaScorecardRetry;
  /**
   * Allow override of retry settings at method-level for updateQaScorecard. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateQaScorecardRetry;
  /**
   * Allow override of retry settings at method-level for deleteQaScorecard. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteQaScorecardRetry;
  /**
   * Allow override of retry settings at method-level for listQaScorecards. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listQaScorecardsRetry;
  /**
   * Allow override of retry settings at method-level for createQaScorecardRevision. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createQaScorecardRevisionRetry;
  /**
   * Allow override of retry settings at method-level for getQaScorecardRevision. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getQaScorecardRevisionRetry;
  /**
   * Allow override of retry settings at method-level for deployQaScorecardRevision. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deployQaScorecardRevisionRetry;
  /**
   * Allow override of retry settings at method-level for undeployQaScorecardRevision. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry undeployQaScorecardRevisionRetry;
  /**
   * Allow override of retry settings at method-level for deleteQaScorecardRevision. If defined,
   * this takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteQaScorecardRevisionRetry;
  /**
   * Allow override of retry settings at method-level for listQaScorecardRevisions. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listQaScorecardRevisionsRetry;
  /**
   * Allow override of retry settings at method-level for createFeedbackLabel. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createFeedbackLabelRetry;
  /**
   * Allow override of retry settings at method-level for listFeedbackLabels. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listFeedbackLabelsRetry;
  /**
   * Allow override of retry settings at method-level for getFeedbackLabel. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getFeedbackLabelRetry;
  /**
   * Allow override of retry settings at method-level for updateFeedbackLabel. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateFeedbackLabelRetry;
  /**
   * Allow override of retry settings at method-level for deleteFeedbackLabel. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteFeedbackLabelRetry;
  /**
   * Allow override of retry settings at method-level for listAllFeedbackLabels. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listAllFeedbackLabelsRetry;

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

  public Retry getCreateAnalysisRuleRetry() {
    return this.createAnalysisRuleRetry;
  }

  public void setCreateAnalysisRuleRetry(Retry createAnalysisRuleRetry) {
    this.createAnalysisRuleRetry = createAnalysisRuleRetry;
  }

  public Retry getGetAnalysisRuleRetry() {
    return this.getAnalysisRuleRetry;
  }

  public void setGetAnalysisRuleRetry(Retry getAnalysisRuleRetry) {
    this.getAnalysisRuleRetry = getAnalysisRuleRetry;
  }

  public Retry getListAnalysisRulesRetry() {
    return this.listAnalysisRulesRetry;
  }

  public void setListAnalysisRulesRetry(Retry listAnalysisRulesRetry) {
    this.listAnalysisRulesRetry = listAnalysisRulesRetry;
  }

  public Retry getUpdateAnalysisRuleRetry() {
    return this.updateAnalysisRuleRetry;
  }

  public void setUpdateAnalysisRuleRetry(Retry updateAnalysisRuleRetry) {
    this.updateAnalysisRuleRetry = updateAnalysisRuleRetry;
  }

  public Retry getDeleteAnalysisRuleRetry() {
    return this.deleteAnalysisRuleRetry;
  }

  public void setDeleteAnalysisRuleRetry(Retry deleteAnalysisRuleRetry) {
    this.deleteAnalysisRuleRetry = deleteAnalysisRuleRetry;
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

  public Retry getCreateQaQuestionRetry() {
    return this.createQaQuestionRetry;
  }

  public void setCreateQaQuestionRetry(Retry createQaQuestionRetry) {
    this.createQaQuestionRetry = createQaQuestionRetry;
  }

  public Retry getGetQaQuestionRetry() {
    return this.getQaQuestionRetry;
  }

  public void setGetQaQuestionRetry(Retry getQaQuestionRetry) {
    this.getQaQuestionRetry = getQaQuestionRetry;
  }

  public Retry getUpdateQaQuestionRetry() {
    return this.updateQaQuestionRetry;
  }

  public void setUpdateQaQuestionRetry(Retry updateQaQuestionRetry) {
    this.updateQaQuestionRetry = updateQaQuestionRetry;
  }

  public Retry getDeleteQaQuestionRetry() {
    return this.deleteQaQuestionRetry;
  }

  public void setDeleteQaQuestionRetry(Retry deleteQaQuestionRetry) {
    this.deleteQaQuestionRetry = deleteQaQuestionRetry;
  }

  public Retry getListQaQuestionsRetry() {
    return this.listQaQuestionsRetry;
  }

  public void setListQaQuestionsRetry(Retry listQaQuestionsRetry) {
    this.listQaQuestionsRetry = listQaQuestionsRetry;
  }

  public Retry getCreateQaScorecardRetry() {
    return this.createQaScorecardRetry;
  }

  public void setCreateQaScorecardRetry(Retry createQaScorecardRetry) {
    this.createQaScorecardRetry = createQaScorecardRetry;
  }

  public Retry getGetQaScorecardRetry() {
    return this.getQaScorecardRetry;
  }

  public void setGetQaScorecardRetry(Retry getQaScorecardRetry) {
    this.getQaScorecardRetry = getQaScorecardRetry;
  }

  public Retry getUpdateQaScorecardRetry() {
    return this.updateQaScorecardRetry;
  }

  public void setUpdateQaScorecardRetry(Retry updateQaScorecardRetry) {
    this.updateQaScorecardRetry = updateQaScorecardRetry;
  }

  public Retry getDeleteQaScorecardRetry() {
    return this.deleteQaScorecardRetry;
  }

  public void setDeleteQaScorecardRetry(Retry deleteQaScorecardRetry) {
    this.deleteQaScorecardRetry = deleteQaScorecardRetry;
  }

  public Retry getListQaScorecardsRetry() {
    return this.listQaScorecardsRetry;
  }

  public void setListQaScorecardsRetry(Retry listQaScorecardsRetry) {
    this.listQaScorecardsRetry = listQaScorecardsRetry;
  }

  public Retry getCreateQaScorecardRevisionRetry() {
    return this.createQaScorecardRevisionRetry;
  }

  public void setCreateQaScorecardRevisionRetry(Retry createQaScorecardRevisionRetry) {
    this.createQaScorecardRevisionRetry = createQaScorecardRevisionRetry;
  }

  public Retry getGetQaScorecardRevisionRetry() {
    return this.getQaScorecardRevisionRetry;
  }

  public void setGetQaScorecardRevisionRetry(Retry getQaScorecardRevisionRetry) {
    this.getQaScorecardRevisionRetry = getQaScorecardRevisionRetry;
  }

  public Retry getDeployQaScorecardRevisionRetry() {
    return this.deployQaScorecardRevisionRetry;
  }

  public void setDeployQaScorecardRevisionRetry(Retry deployQaScorecardRevisionRetry) {
    this.deployQaScorecardRevisionRetry = deployQaScorecardRevisionRetry;
  }

  public Retry getUndeployQaScorecardRevisionRetry() {
    return this.undeployQaScorecardRevisionRetry;
  }

  public void setUndeployQaScorecardRevisionRetry(Retry undeployQaScorecardRevisionRetry) {
    this.undeployQaScorecardRevisionRetry = undeployQaScorecardRevisionRetry;
  }

  public Retry getDeleteQaScorecardRevisionRetry() {
    return this.deleteQaScorecardRevisionRetry;
  }

  public void setDeleteQaScorecardRevisionRetry(Retry deleteQaScorecardRevisionRetry) {
    this.deleteQaScorecardRevisionRetry = deleteQaScorecardRevisionRetry;
  }

  public Retry getListQaScorecardRevisionsRetry() {
    return this.listQaScorecardRevisionsRetry;
  }

  public void setListQaScorecardRevisionsRetry(Retry listQaScorecardRevisionsRetry) {
    this.listQaScorecardRevisionsRetry = listQaScorecardRevisionsRetry;
  }

  public Retry getCreateFeedbackLabelRetry() {
    return this.createFeedbackLabelRetry;
  }

  public void setCreateFeedbackLabelRetry(Retry createFeedbackLabelRetry) {
    this.createFeedbackLabelRetry = createFeedbackLabelRetry;
  }

  public Retry getListFeedbackLabelsRetry() {
    return this.listFeedbackLabelsRetry;
  }

  public void setListFeedbackLabelsRetry(Retry listFeedbackLabelsRetry) {
    this.listFeedbackLabelsRetry = listFeedbackLabelsRetry;
  }

  public Retry getGetFeedbackLabelRetry() {
    return this.getFeedbackLabelRetry;
  }

  public void setGetFeedbackLabelRetry(Retry getFeedbackLabelRetry) {
    this.getFeedbackLabelRetry = getFeedbackLabelRetry;
  }

  public Retry getUpdateFeedbackLabelRetry() {
    return this.updateFeedbackLabelRetry;
  }

  public void setUpdateFeedbackLabelRetry(Retry updateFeedbackLabelRetry) {
    this.updateFeedbackLabelRetry = updateFeedbackLabelRetry;
  }

  public Retry getDeleteFeedbackLabelRetry() {
    return this.deleteFeedbackLabelRetry;
  }

  public void setDeleteFeedbackLabelRetry(Retry deleteFeedbackLabelRetry) {
    this.deleteFeedbackLabelRetry = deleteFeedbackLabelRetry;
  }

  public Retry getListAllFeedbackLabelsRetry() {
    return this.listAllFeedbackLabelsRetry;
  }

  public void setListAllFeedbackLabelsRetry(Retry listAllFeedbackLabelsRetry) {
    this.listAllFeedbackLabelsRetry = listAllFeedbackLabelsRetry;
  }
}
