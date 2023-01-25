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

package com.google.cloud.talent.v4.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for JobService client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.talent.v4.job-service")
public class JobServiceSpringProperties implements CredentialsSupplier {
  /** OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries. */
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials(
          "https://www.googleapis.com/auth/cloud-platform", "https://www.googleapis.com/auth/jobs");
  /** Quota project to use for billing. */
  private String quotaProjectId;
  /** Number of threads used for executors. */
  private Integer executorThreadCount;
  /** Allow override of default transport channel provider to use REST instead of gRPC. */
  private boolean useRest = false;
  /** Allow override of retry settings at service level, applying to all of its RPC methods. */
  @NestedConfigurationProperty private Retry retry;
  /**
   * Allow override of retry settings at method-level for createJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry createJobRetry;
  /**
   * Allow override of retry settings at method-level for getJob. If defined, this takes precedence
   * over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getJobRetry;
  /**
   * Allow override of retry settings at method-level for updateJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateJobRetry;
  /**
   * Allow override of retry settings at method-level for deleteJob. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry deleteJobRetry;
  /**
   * Allow override of retry settings at method-level for listJobs. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listJobsRetry;
  /**
   * Allow override of retry settings at method-level for searchJobs. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry searchJobsRetry;
  /**
   * Allow override of retry settings at method-level for searchJobsForAlert. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry searchJobsForAlertRetry;

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

  public Retry getCreateJobRetry() {
    return this.createJobRetry;
  }

  public void setCreateJobRetry(Retry createJobRetry) {
    this.createJobRetry = createJobRetry;
  }

  public Retry getGetJobRetry() {
    return this.getJobRetry;
  }

  public void setGetJobRetry(Retry getJobRetry) {
    this.getJobRetry = getJobRetry;
  }

  public Retry getUpdateJobRetry() {
    return this.updateJobRetry;
  }

  public void setUpdateJobRetry(Retry updateJobRetry) {
    this.updateJobRetry = updateJobRetry;
  }

  public Retry getDeleteJobRetry() {
    return this.deleteJobRetry;
  }

  public void setDeleteJobRetry(Retry deleteJobRetry) {
    this.deleteJobRetry = deleteJobRetry;
  }

  public Retry getListJobsRetry() {
    return this.listJobsRetry;
  }

  public void setListJobsRetry(Retry listJobsRetry) {
    this.listJobsRetry = listJobsRetry;
  }

  public Retry getSearchJobsRetry() {
    return this.searchJobsRetry;
  }

  public void setSearchJobsRetry(Retry searchJobsRetry) {
    this.searchJobsRetry = searchJobsRetry;
  }

  public Retry getSearchJobsForAlertRetry() {
    return this.searchJobsForAlertRetry;
  }

  public void setSearchJobsForAlertRetry(Retry searchJobsForAlertRetry) {
    this.searchJobsForAlertRetry = searchJobsForAlertRetry;
  }
}
