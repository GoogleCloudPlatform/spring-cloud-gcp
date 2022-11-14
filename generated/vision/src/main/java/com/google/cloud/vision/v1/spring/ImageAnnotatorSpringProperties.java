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

package com.google.cloud.vision.v1.spring;

import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.threeten.bp.Duration;

@ConfigurationProperties("com.google.cloud.vision.v1.spring.auto.image-annotator")
public class ImageAnnotatorSpringProperties implements CredentialsSupplier {
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials(
          "https://www.googleapis.com/auth/cloud-platform",
          "https://www.googleapis.com/auth/cloud-vision");

  private String quotaProjectId;
  private Integer executorThreadCount;
  private boolean useRest;
  private Duration batchAnnotateImagesInitialRetryDelay;
  private Double batchAnnotateImagesRetryDelayMultiplier;
  private Duration batchAnnotateImagesMaxRetryDelay;
  private Duration batchAnnotateImagesInitialRpcTimeout;
  private Double batchAnnotateImagesRpcTimeoutMultiplier;
  private Duration batchAnnotateImagesMaxRpcTimeout;
  private Duration batchAnnotateImagesTotalTimeout;
  private Duration batchAnnotateFilesInitialRetryDelay;
  private Double batchAnnotateFilesRetryDelayMultiplier;
  private Duration batchAnnotateFilesMaxRetryDelay;
  private Duration batchAnnotateFilesInitialRpcTimeout;
  private Double batchAnnotateFilesRpcTimeoutMultiplier;
  private Duration batchAnnotateFilesMaxRpcTimeout;
  private Duration batchAnnotateFilesTotalTimeout;
  private Duration asyncBatchAnnotateImagesInitialRetryDelay;
  private Double asyncBatchAnnotateImagesRetryDelayMultiplier;
  private Duration asyncBatchAnnotateImagesMaxRetryDelay;
  private Duration asyncBatchAnnotateImagesInitialRpcTimeout;
  private Double asyncBatchAnnotateImagesRpcTimeoutMultiplier;
  private Duration asyncBatchAnnotateImagesMaxRpcTimeout;
  private Duration asyncBatchAnnotateImagesTotalTimeout;
  private Duration asyncBatchAnnotateFilesInitialRetryDelay;
  private Double asyncBatchAnnotateFilesRetryDelayMultiplier;
  private Duration asyncBatchAnnotateFilesMaxRetryDelay;
  private Duration asyncBatchAnnotateFilesInitialRpcTimeout;
  private Double asyncBatchAnnotateFilesRpcTimeoutMultiplier;
  private Duration asyncBatchAnnotateFilesMaxRpcTimeout;
  private Duration asyncBatchAnnotateFilesTotalTimeout;

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

  public Integer getExecutorThreadCount() {
    return this.executorThreadCount;
  }

  public void setExecutorThreadCount(Integer executorThreadCount) {
    this.executorThreadCount = executorThreadCount;
  }

  public Duration getBatchAnnotateImagesInitialRetryDelay() {
    return this.batchAnnotateImagesInitialRetryDelay;
  }

  public void setBatchAnnotateImagesInitialRetryDelay(
      Duration batchAnnotateImagesInitialRetryDelay) {
    this.batchAnnotateImagesInitialRetryDelay = batchAnnotateImagesInitialRetryDelay;
  }

  public Double getBatchAnnotateImagesRetryDelayMultiplier() {
    return this.batchAnnotateImagesRetryDelayMultiplier;
  }

  public void setBatchAnnotateImagesRetryDelayMultiplier(
      Double batchAnnotateImagesRetryDelayMultiplier) {
    this.batchAnnotateImagesRetryDelayMultiplier = batchAnnotateImagesRetryDelayMultiplier;
  }

  public Duration getBatchAnnotateImagesMaxRetryDelay() {
    return this.batchAnnotateImagesMaxRetryDelay;
  }

  public void setBatchAnnotateImagesMaxRetryDelay(Duration batchAnnotateImagesMaxRetryDelay) {
    this.batchAnnotateImagesMaxRetryDelay = batchAnnotateImagesMaxRetryDelay;
  }

  public Duration getBatchAnnotateImagesInitialRpcTimeout() {
    return this.batchAnnotateImagesInitialRpcTimeout;
  }

  public void setBatchAnnotateImagesInitialRpcTimeout(
      Duration batchAnnotateImagesInitialRpcTimeout) {
    this.batchAnnotateImagesInitialRpcTimeout = batchAnnotateImagesInitialRpcTimeout;
  }

  public Double getBatchAnnotateImagesRpcTimeoutMultiplier() {
    return this.batchAnnotateImagesRpcTimeoutMultiplier;
  }

  public void setBatchAnnotateImagesRpcTimeoutMultiplier(
      Double batchAnnotateImagesRpcTimeoutMultiplier) {
    this.batchAnnotateImagesRpcTimeoutMultiplier = batchAnnotateImagesRpcTimeoutMultiplier;
  }

  public Duration getBatchAnnotateImagesMaxRpcTimeout() {
    return this.batchAnnotateImagesMaxRpcTimeout;
  }

  public void setBatchAnnotateImagesMaxRpcTimeout(Duration batchAnnotateImagesMaxRpcTimeout) {
    this.batchAnnotateImagesMaxRpcTimeout = batchAnnotateImagesMaxRpcTimeout;
  }

  public Duration getBatchAnnotateImagesTotalTimeout() {
    return this.batchAnnotateImagesTotalTimeout;
  }

  public void setBatchAnnotateImagesTotalTimeout(Duration batchAnnotateImagesTotalTimeout) {
    this.batchAnnotateImagesTotalTimeout = batchAnnotateImagesTotalTimeout;
  }

  public Duration getBatchAnnotateFilesInitialRetryDelay() {
    return this.batchAnnotateFilesInitialRetryDelay;
  }

  public void setBatchAnnotateFilesInitialRetryDelay(Duration batchAnnotateFilesInitialRetryDelay) {
    this.batchAnnotateFilesInitialRetryDelay = batchAnnotateFilesInitialRetryDelay;
  }

  public Double getBatchAnnotateFilesRetryDelayMultiplier() {
    return this.batchAnnotateFilesRetryDelayMultiplier;
  }

  public void setBatchAnnotateFilesRetryDelayMultiplier(
      Double batchAnnotateFilesRetryDelayMultiplier) {
    this.batchAnnotateFilesRetryDelayMultiplier = batchAnnotateFilesRetryDelayMultiplier;
  }

  public Duration getBatchAnnotateFilesMaxRetryDelay() {
    return this.batchAnnotateFilesMaxRetryDelay;
  }

  public void setBatchAnnotateFilesMaxRetryDelay(Duration batchAnnotateFilesMaxRetryDelay) {
    this.batchAnnotateFilesMaxRetryDelay = batchAnnotateFilesMaxRetryDelay;
  }

  public Duration getBatchAnnotateFilesInitialRpcTimeout() {
    return this.batchAnnotateFilesInitialRpcTimeout;
  }

  public void setBatchAnnotateFilesInitialRpcTimeout(Duration batchAnnotateFilesInitialRpcTimeout) {
    this.batchAnnotateFilesInitialRpcTimeout = batchAnnotateFilesInitialRpcTimeout;
  }

  public Double getBatchAnnotateFilesRpcTimeoutMultiplier() {
    return this.batchAnnotateFilesRpcTimeoutMultiplier;
  }

  public void setBatchAnnotateFilesRpcTimeoutMultiplier(
      Double batchAnnotateFilesRpcTimeoutMultiplier) {
    this.batchAnnotateFilesRpcTimeoutMultiplier = batchAnnotateFilesRpcTimeoutMultiplier;
  }

  public Duration getBatchAnnotateFilesMaxRpcTimeout() {
    return this.batchAnnotateFilesMaxRpcTimeout;
  }

  public void setBatchAnnotateFilesMaxRpcTimeout(Duration batchAnnotateFilesMaxRpcTimeout) {
    this.batchAnnotateFilesMaxRpcTimeout = batchAnnotateFilesMaxRpcTimeout;
  }

  public Duration getBatchAnnotateFilesTotalTimeout() {
    return this.batchAnnotateFilesTotalTimeout;
  }

  public void setBatchAnnotateFilesTotalTimeout(Duration batchAnnotateFilesTotalTimeout) {
    this.batchAnnotateFilesTotalTimeout = batchAnnotateFilesTotalTimeout;
  }

  public Duration getAsyncBatchAnnotateImagesInitialRetryDelay() {
    return this.asyncBatchAnnotateImagesInitialRetryDelay;
  }

  public void setAsyncBatchAnnotateImagesInitialRetryDelay(
      Duration asyncBatchAnnotateImagesInitialRetryDelay) {
    this.asyncBatchAnnotateImagesInitialRetryDelay = asyncBatchAnnotateImagesInitialRetryDelay;
  }

  public Double getAsyncBatchAnnotateImagesRetryDelayMultiplier() {
    return this.asyncBatchAnnotateImagesRetryDelayMultiplier;
  }

  public void setAsyncBatchAnnotateImagesRetryDelayMultiplier(
      Double asyncBatchAnnotateImagesRetryDelayMultiplier) {
    this.asyncBatchAnnotateImagesRetryDelayMultiplier =
        asyncBatchAnnotateImagesRetryDelayMultiplier;
  }

  public Duration getAsyncBatchAnnotateImagesMaxRetryDelay() {
    return this.asyncBatchAnnotateImagesMaxRetryDelay;
  }

  public void setAsyncBatchAnnotateImagesMaxRetryDelay(
      Duration asyncBatchAnnotateImagesMaxRetryDelay) {
    this.asyncBatchAnnotateImagesMaxRetryDelay = asyncBatchAnnotateImagesMaxRetryDelay;
  }

  public Duration getAsyncBatchAnnotateImagesInitialRpcTimeout() {
    return this.asyncBatchAnnotateImagesInitialRpcTimeout;
  }

  public void setAsyncBatchAnnotateImagesInitialRpcTimeout(
      Duration asyncBatchAnnotateImagesInitialRpcTimeout) {
    this.asyncBatchAnnotateImagesInitialRpcTimeout = asyncBatchAnnotateImagesInitialRpcTimeout;
  }

  public Double getAsyncBatchAnnotateImagesRpcTimeoutMultiplier() {
    return this.asyncBatchAnnotateImagesRpcTimeoutMultiplier;
  }

  public void setAsyncBatchAnnotateImagesRpcTimeoutMultiplier(
      Double asyncBatchAnnotateImagesRpcTimeoutMultiplier) {
    this.asyncBatchAnnotateImagesRpcTimeoutMultiplier =
        asyncBatchAnnotateImagesRpcTimeoutMultiplier;
  }

  public Duration getAsyncBatchAnnotateImagesMaxRpcTimeout() {
    return this.asyncBatchAnnotateImagesMaxRpcTimeout;
  }

  public void setAsyncBatchAnnotateImagesMaxRpcTimeout(
      Duration asyncBatchAnnotateImagesMaxRpcTimeout) {
    this.asyncBatchAnnotateImagesMaxRpcTimeout = asyncBatchAnnotateImagesMaxRpcTimeout;
  }

  public Duration getAsyncBatchAnnotateImagesTotalTimeout() {
    return this.asyncBatchAnnotateImagesTotalTimeout;
  }

  public void setAsyncBatchAnnotateImagesTotalTimeout(
      Duration asyncBatchAnnotateImagesTotalTimeout) {
    this.asyncBatchAnnotateImagesTotalTimeout = asyncBatchAnnotateImagesTotalTimeout;
  }

  public Duration getAsyncBatchAnnotateFilesInitialRetryDelay() {
    return this.asyncBatchAnnotateFilesInitialRetryDelay;
  }

  public void setAsyncBatchAnnotateFilesInitialRetryDelay(
      Duration asyncBatchAnnotateFilesInitialRetryDelay) {
    this.asyncBatchAnnotateFilesInitialRetryDelay = asyncBatchAnnotateFilesInitialRetryDelay;
  }

  public Double getAsyncBatchAnnotateFilesRetryDelayMultiplier() {
    return this.asyncBatchAnnotateFilesRetryDelayMultiplier;
  }

  public void setAsyncBatchAnnotateFilesRetryDelayMultiplier(
      Double asyncBatchAnnotateFilesRetryDelayMultiplier) {
    this.asyncBatchAnnotateFilesRetryDelayMultiplier = asyncBatchAnnotateFilesRetryDelayMultiplier;
  }

  public Duration getAsyncBatchAnnotateFilesMaxRetryDelay() {
    return this.asyncBatchAnnotateFilesMaxRetryDelay;
  }

  public void setAsyncBatchAnnotateFilesMaxRetryDelay(
      Duration asyncBatchAnnotateFilesMaxRetryDelay) {
    this.asyncBatchAnnotateFilesMaxRetryDelay = asyncBatchAnnotateFilesMaxRetryDelay;
  }

  public Duration getAsyncBatchAnnotateFilesInitialRpcTimeout() {
    return this.asyncBatchAnnotateFilesInitialRpcTimeout;
  }

  public void setAsyncBatchAnnotateFilesInitialRpcTimeout(
      Duration asyncBatchAnnotateFilesInitialRpcTimeout) {
    this.asyncBatchAnnotateFilesInitialRpcTimeout = asyncBatchAnnotateFilesInitialRpcTimeout;
  }

  public Double getAsyncBatchAnnotateFilesRpcTimeoutMultiplier() {
    return this.asyncBatchAnnotateFilesRpcTimeoutMultiplier;
  }

  public void setAsyncBatchAnnotateFilesRpcTimeoutMultiplier(
      Double asyncBatchAnnotateFilesRpcTimeoutMultiplier) {
    this.asyncBatchAnnotateFilesRpcTimeoutMultiplier = asyncBatchAnnotateFilesRpcTimeoutMultiplier;
  }

  public Duration getAsyncBatchAnnotateFilesMaxRpcTimeout() {
    return this.asyncBatchAnnotateFilesMaxRpcTimeout;
  }

  public void setAsyncBatchAnnotateFilesMaxRpcTimeout(
      Duration asyncBatchAnnotateFilesMaxRpcTimeout) {
    this.asyncBatchAnnotateFilesMaxRpcTimeout = asyncBatchAnnotateFilesMaxRpcTimeout;
  }

  public Duration getAsyncBatchAnnotateFilesTotalTimeout() {
    return this.asyncBatchAnnotateFilesTotalTimeout;
  }

  public void setAsyncBatchAnnotateFilesTotalTimeout(Duration asyncBatchAnnotateFilesTotalTimeout) {
    this.asyncBatchAnnotateFilesTotalTimeout = asyncBatchAnnotateFilesTotalTimeout;
  }
}
