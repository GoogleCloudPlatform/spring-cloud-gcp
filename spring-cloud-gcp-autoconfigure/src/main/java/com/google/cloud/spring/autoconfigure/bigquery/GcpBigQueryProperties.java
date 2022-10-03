/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.bigquery;

import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.GcpScope;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/** Properties for configuring BigQuery. */
@ConfigurationProperties("spring.cloud.gcp.bigquery")
public class GcpBigQueryProperties implements CredentialsSupplier {

  /** Overrides the GCP OAuth2 credentials specified in the Core module. */
  @NestedConfigurationProperty
  private final Credentials credentials = new Credentials(GcpScope.BIG_QUERY.getUrl());

  /** Overrides the GCP project ID specified in the Core module to use for BigQuery. */
  private String projectId;

  /** Name of the BigQuery dataset to use. */
  private String datasetName;

  /**
   * Batching size which will be used by BigQueryJsonDataWriter. Note too large or too low values
   * might impact performance. Defaulted to 1000
   */
  private int jsonWriterBatchSize;

  /** The size of thread pool of ThreadPoolTaskScheduler used by GcpBigQueryAutoConfiguration */
  private int threadPoolSize;

  public int getJsonWriterBatchSize() {
    return jsonWriterBatchSize;
  }

  public int getThreadPoolSize() {
    return threadPoolSize;
  }

  public void setThreadPoolSize(int threadPoolSize) {
    this.threadPoolSize = threadPoolSize;
  }

  public void setJsonWriterBatchSize(int jsonWriterBatchSize) {
    this.jsonWriterBatchSize = jsonWriterBatchSize;
  }

  public Credentials getCredentials() {
    return this.credentials;
  }

  public String getProjectId() {
    return this.projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getDatasetName() {
    return this.datasetName;
  }

  public void setDatasetName(String datasetName) {
    this.datasetName = datasetName;
  }
}
