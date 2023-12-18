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

import static com.google.cloud.spring.bigquery.core.BigQueryTestConfiguration.DATASET_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobStatus;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.spring.bigquery.TestRuntimeHints;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration tests for BigQuery.
 *
 * @since 1.2
 */
// Please create a table "test_dataset" in BigQuery to run the tests successfully
@EnabledIfSystemProperty(named = "it.bigquery", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BigQueryTestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ImportRuntimeHints(TestRuntimeHints.class)
class BigQueryTemplateIntegrationTests {

  private static final String SELECT_FORMAT = "SELECT * FROM %s";

  @Autowired BigQuery bigQuery;

  @Autowired BigQueryTemplate bigQueryTemplate;

  @Value("data.csv")
  Resource dataFile;

  @Value("data.json")
  Resource jsonDataFile;

  private String tableName;

  private String selectQuery;

  private String selectQueryDesc;

  @BeforeEach
  void generateRandomTableName() {
    String uuid = UUID.randomUUID().toString().replace("-", "");
    this.tableName = "template_test_table_" + uuid;
    this.selectQuery = String.format(SELECT_FORMAT, DATASET_NAME + "." + tableName);
    this.selectQueryDesc =
        String.format(
            SELECT_FORMAT, DATASET_NAME + "." + tableName + " order by SerialNumber desc");
  }

  @AfterEach
  void cleanupTestEnvironment() {
    // Delete table after test.
    this.bigQuery.delete(TableId.of(DATASET_NAME, tableName));
  }

  @Test
  void testLoadFileWithSchema() throws Exception {
    Schema schema =
        Schema.of(
            Field.of("CountyId", StandardSQLTypeName.INT64),
            Field.of("State", StandardSQLTypeName.STRING),
            Field.of("County", StandardSQLTypeName.STRING));

    CompletableFuture<Job> bigQueryJobFuture =
        bigQueryTemplate.writeDataToTable(
            tableName, dataFile.getInputStream(), FormatOptions.csv(), schema);

    Job job = bigQueryJobFuture.get();
    assertThat(job.getStatus().getState()).isEqualTo(JobStatus.State.DONE);

    QueryJobConfiguration queryJobConfiguration =
        QueryJobConfiguration.newBuilder(this.selectQuery).build();
    TableResult result = this.bigQuery.query(queryJobConfiguration);

    assertThat(result.getTotalRows()).isEqualTo(1);
    assertThat(result.getValues().iterator().next().get("State").getStringValue())
        .isEqualTo("Alabama");
  }

  @Test
  void testJsonFileLoadWithSchema() throws Exception {
    Schema schema =
        Schema.of(
            Field.of("CompanyName", StandardSQLTypeName.STRING),
            Field.of("Description", StandardSQLTypeName.STRING),
            Field.of("SerialNumber", StandardSQLTypeName.NUMERIC),
            Field.of("Leave", StandardSQLTypeName.NUMERIC),
            Field.of("EmpName", StandardSQLTypeName.STRING));

    CompletableFuture<WriteApiResponse> writeApiFuture =
        bigQueryTemplate.writeJsonStream(tableName, jsonDataFile.getInputStream(), schema);

    WriteApiResponse writeApiResponse =
        writeApiFuture.get(); // wait for the response to be available
    assertThat(writeApiResponse.isSuccessful()).isTrue();

    QueryJobConfiguration queryJobConfiguration =
        QueryJobConfiguration.newBuilder(this.selectQueryDesc).build();
    TableResult result = this.bigQuery.query(queryJobConfiguration);

    assertThat(result.getTotalRows()).isEqualTo(1089);
    assertThat(result.getValues().iterator().next().get("SerialNumber").getLongValue())
        .isEqualTo(9789386445658L);
  }

  @Test
  void testLoadFile() throws IOException, ExecutionException, InterruptedException {
    CompletableFuture<Job> bigQueryJobFuture =
        bigQueryTemplate.writeDataToTable(
            this.tableName, dataFile.getInputStream(), FormatOptions.csv());

    Job job = bigQueryJobFuture.get();
    assertThat(job.getStatus().getState()).isEqualTo(JobStatus.State.DONE);

    QueryJobConfiguration queryJobConfiguration =
        QueryJobConfiguration.newBuilder(this.selectQuery).build();
    TableResult result = this.bigQuery.query(queryJobConfiguration);

    assertThat(result.getTotalRows()).isEqualTo(1);
    assertThat(result.getValues().iterator().next().get("State").getStringValue())
        .isEqualTo("Alabama");
  }

  @Test
  void testLoadBytes() throws ExecutionException, InterruptedException {
    byte[] byteArray = "CountyId,State,County\n1001,Alabama,Autauga County\n".getBytes();
    ByteArrayInputStream byteStream = new ByteArrayInputStream(byteArray);

    CompletableFuture<Job> bigQueryJobFuture =
        bigQueryTemplate.writeDataToTable(this.tableName, byteStream, FormatOptions.csv());

    Job job = bigQueryJobFuture.get();
    assertThat(job.getStatus().getState()).isEqualTo(JobStatus.State.DONE);

    QueryJobConfiguration queryJobConfiguration =
        QueryJobConfiguration.newBuilder(this.selectQuery).build();
    TableResult result = this.bigQuery.query(queryJobConfiguration);

    assertThat(result.getTotalRows()).isEqualTo(1);
    assertThat(result.getValues().iterator().next().get("State").getStringValue())
        .isEqualTo("Alabama");
  }
}
