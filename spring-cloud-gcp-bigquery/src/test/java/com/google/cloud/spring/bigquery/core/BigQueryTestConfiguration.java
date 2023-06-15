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

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.JobInfo.WriteDisposition;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.spring.bigquery.integration.outbound.BigQueryFileMessageHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.scheduling.TaskScheduler;

/** Provides autoconfiguration for the BigQuery integration tests. */
@EnableIntegration
@Configuration
public class BigQueryTestConfiguration {

  /** The BigQuery Dataset name used for the integration tests. */
  public static final String DATASET_NAME = "test_dataset";

  /** The BigQuery Write API Batch Size to used for the integration tests. */
  private static final int JSON_WRITER_BATCH_SIZE = 1000;

  @Bean
  public BigQuery bigQuery() {
    return BigQueryOptions.getDefaultInstance().getService();
  }

  @Bean
  public BigQueryWriteClient bigQueryWriteClient() throws IOException {
    return BigQueryWriteClient.create();
  }

  @Bean
  public BigQueryTemplate bigQueryTemplate(
      BigQuery bigQuery, BigQueryWriteClient bigQueryWriteClient, TaskScheduler taskScheduler) {
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
