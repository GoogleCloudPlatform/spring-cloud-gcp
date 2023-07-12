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

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteSettings;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.bigquery.core.BigQueryTemplate;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/** Provides client objects for interfacing with BigQuery. */
@AutoConfiguration
@AutoConfigureAfter(GcpContextAutoConfiguration.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.bigquery.enabled", matchIfMissing = true)
@ConditionalOnClass({BigQuery.class, BigQueryTemplate.class})
@EnableConfigurationProperties(GcpBigQueryProperties.class)
public class GcpBigQueryAutoConfiguration {

  private final String projectId;

  private final CredentialsProvider credentialsProvider;

  private final String datasetName;

  private int jsonWriterBatchSize;

  private int threadPoolSize;

  GcpBigQueryAutoConfiguration(
      GcpBigQueryProperties gcpBigQueryProperties,
      GcpProjectIdProvider projectIdProvider,
      CredentialsProvider credentialsProvider)
      throws IOException {

    this.projectId =
        (gcpBigQueryProperties.getProjectId() != null)
            ? gcpBigQueryProperties.getProjectId()
            : projectIdProvider.getProjectId();

    this.credentialsProvider =
        (gcpBigQueryProperties.getCredentials().hasKey()
            ? new DefaultCredentialsProvider(gcpBigQueryProperties)
            : credentialsProvider);

    this.datasetName = gcpBigQueryProperties.getDatasetName();

    this.jsonWriterBatchSize = gcpBigQueryProperties.getJsonWriterBatchSize();

    this.threadPoolSize = getThreadPoolSize(gcpBigQueryProperties.getThreadPoolSize());
  }

  /**
   * This method ensures that we use the DEFAULT_THREAD_POOL_SIZE if the user doesn't set this
   * property or if they set it too high
   *
   * @return threadPoolSize
   */
  private int getThreadPoolSize(int threadPoolSize) {
    int defaultThreadPoolSize = 4;
    int maxThreadPoolSize = 100;
    return (threadPoolSize <= 0 || threadPoolSize > maxThreadPoolSize)
        ? defaultThreadPoolSize
        : threadPoolSize;
  }

  @Bean
  @ConditionalOnMissingBean
  public BigQuery bigQuery() throws IOException {
    BigQueryOptions bigQueryOptions =
        BigQueryOptions.newBuilder()
            .setProjectId(this.projectId)
            .setCredentials(this.credentialsProvider.getCredentials())
            .setHeaderProvider(new UserAgentHeaderProvider(GcpBigQueryAutoConfiguration.class))
            .build();
    return bigQueryOptions.getService();
  }

  @Bean
  @ConditionalOnMissingBean
  public BigQueryWriteClient bigQueryWriteClient() throws IOException {
    BigQueryWriteSettings bigQueryWriteSettings =
        BigQueryWriteSettings.newBuilder()
            .setCredentialsProvider(this.credentialsProvider)
            .setQuotaProjectId(this.projectId)
            .setHeaderProvider(new UserAgentHeaderProvider(GcpBigQueryAutoConfiguration.class))
            .build();
    return BigQueryWriteClient.create(bigQueryWriteSettings);
  }

  @Bean
  @ConditionalOnMissingBean(name = "bigQueryThreadPoolTaskScheduler")
  public ThreadPoolTaskScheduler bigQueryThreadPoolTaskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(threadPoolSize);
    scheduler.setThreadNamePrefix("gcp-bigquery");
    scheduler.setDaemon(true);
    return scheduler;
  }

  @Bean
  @ConditionalOnMissingBean
  public BigQueryTemplate bigQueryTemplate(
      BigQuery bigQuery,
      BigQueryWriteClient bigQueryWriteClient,
      @Qualifier("bigQueryThreadPoolTaskScheduler")
      ThreadPoolTaskScheduler bigQueryThreadPoolTaskScheduler) {
    Map<String, Object> bqInitSettings = new HashMap<>();
    bqInitSettings.put("DATASET_NAME", this.datasetName);
    bqInitSettings.put("JSON_WRITER_BATCH_SIZE", this.jsonWriterBatchSize);
    return new BigQueryTemplate(
        bigQuery, bigQueryWriteClient, bqInitSettings, bigQueryThreadPoolTaskScheduler);
  }
}
