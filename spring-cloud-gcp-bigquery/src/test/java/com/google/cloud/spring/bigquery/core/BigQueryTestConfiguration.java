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

package com.google.cloud.spring.bigquery.core;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.JobInfo.WriteDisposition;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteSettings;
import com.google.cloud.spring.bigquery.integration.outbound.BigQueryFileMessageHandler;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/** Provides autoconfiguration for the BigQuery integration tests. */
@SpringBootConfiguration
public class BigQueryTestConfiguration {

  /** The BigQuery Dataset name used for the integration tests. */
  public static final String DATASET_NAME = "test_dataset";

  /** The BigQuery Write API Batch Size to used for the integration tests. */
  private static final int JSON_WRITER_BATCH_SIZE = 1000;

  private final String projectId;

  private final CredentialsProvider credentialsProvider;

  private int threadPoolSize = 4;

  public BigQueryTestConfiguration() throws IOException {

    this.projectId = new DefaultGcpProjectIdProvider().getProjectId();
    this.credentialsProvider = new DefaultCredentialsProvider(Credentials::new);
  }

  @Bean
  public BigQuery bigQuery() throws IOException {
    BigQueryOptions bigQueryOptions =
        BigQueryOptions.newBuilder()
            .setProjectId(this.projectId)
            .setCredentials(this.credentialsProvider.getCredentials())
            .setHeaderProvider(new UserAgentHeaderProvider(this.getClass()))
            .build();
    return bigQueryOptions.getService();
  }

  @Bean
  public BigQueryWriteClient bigQueryWriteClient() throws IOException {
    BigQueryWriteSettings bigQueryWriteSettings =
        BigQueryWriteSettings.newBuilder()
            .setCredentialsProvider(this.credentialsProvider)
            .setQuotaProjectId(this.projectId)
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
  public BigQueryTemplate bigQueryTemplate(
      BigQuery bigQuery,
      BigQueryWriteClient bigQueryWriteClient,
      @Qualifier("bigQueryThreadPoolTaskScheduler") ThreadPoolTaskScheduler taskScheduler) {
    Map<String, Object> bqInitSettings = new HashMap<>();
    bqInitSettings.put("DATASET_NAME", DATASET_NAME);
    bqInitSettings.put("JSON_WRITER_BATCH_SIZE", JSON_WRITER_BATCH_SIZE);
    BigQueryTemplate bigQueryTemplate =
        new BigQueryTemplate(bigQuery, bigQueryWriteClient, bqInitSettings, taskScheduler);
    bigQueryTemplate.setWriteDisposition(WriteDisposition.WRITE_TRUNCATE);
    return bigQueryTemplate;
  }

  @Bean
  public BigQueryFileMessageHandler messageHandler(BigQueryTemplate bigQueryTemplate) {
    BigQueryFileMessageHandler messageHandler = new BigQueryFileMessageHandler(bigQueryTemplate);
    return messageHandler;
  }
}
