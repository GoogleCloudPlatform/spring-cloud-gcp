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

package com.google.cloud.spring.bigquery.integration.outbound;

import static com.google.cloud.spring.bigquery.core.BigQueryTestConfiguration.DATASET_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Field.Mode;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.spring.bigquery.core.BigQueryTestConfiguration;
import com.google.cloud.spring.bigquery.integration.BigQuerySpringMessageHeaders;
import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@EnabledIfSystemProperty(named = "it.bigquery", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BigQueryTestConfiguration.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class BigQueryFileMessageHandlerIntegrationTests {

  private static final String TABLE_NAME = "test_table";

  @Autowired private ThreadPoolTaskScheduler taskScheduler;

  @Autowired private BigQuery bigquery;

  @Autowired private BigQueryFileMessageHandler messageHandler;

  @BeforeEach
  @AfterEach
  void setup() {
    // Clear the previous dataset before beginning the test.
    this.bigquery.delete(TableId.of(DATASET_NAME, TABLE_NAME));
  }

  @Test
  void testLoadFileWithSchema() throws InterruptedException, ExecutionException {
    Schema schema =
        Schema.of(
            Field.newBuilder("CountyId", StandardSQLTypeName.STRING).setMode(Mode.NULLABLE).build(),
            Field.newBuilder("State", StandardSQLTypeName.STRING).setMode(Mode.NULLABLE).build(),
            Field.newBuilder("County", StandardSQLTypeName.STRING).setMode(Mode.NULLABLE).build());

    HashMap<String, Object> messageHeaders = new HashMap<>();
    messageHeaders.put(BigQuerySpringMessageHeaders.TABLE_NAME, TABLE_NAME);
    messageHeaders.put(BigQuerySpringMessageHeaders.FORMAT_OPTIONS, FormatOptions.csv());
    messageHeaders.put(BigQuerySpringMessageHeaders.TABLE_SCHEMA, schema);

    Message<File> message =
        MessageBuilder.createMessage(
            new File("src/test/resources/data.csv"), new MessageHeaders(messageHeaders));

    CompletableFuture<Job> jobFuture =
        (CompletableFuture<Job>) this.messageHandler.handleRequestMessage(message);

    // Assert that a BigQuery polling task is scheduled successfully.
    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () ->
                assertThat(this.taskScheduler.getScheduledThreadPoolExecutor().getQueue())
                    .hasSize(1));
    jobFuture.get();

    QueryJobConfiguration queryJobConfiguration =
        QueryJobConfiguration.newBuilder("SELECT * FROM test_dataset.test_table").build();
    TableResult result = this.bigquery.query(queryJobConfiguration);

    assertThat(result.getTotalRows()).isEqualTo(1);
    assertThat(result.getValues().iterator().next().get("State").getStringValue())
        .isEqualTo("Alabama");
    assertThat(result.getSchema()).isEqualTo(schema);

    // This asserts that the BigQuery job polling task is no longer in the scheduler.
    assertThat(this.taskScheduler.getScheduledThreadPoolExecutor().getQueue()).isEmpty();
  }

  @Test
  void testLoadFile() throws InterruptedException, ExecutionException {
    HashMap<String, Object> messageHeaders = new HashMap<>();
    this.messageHandler.setTableName(TABLE_NAME);
    this.messageHandler.setFormatOptions(FormatOptions.csv());

    Message<File> message =
        MessageBuilder.createMessage(
            new File("src/test/resources/data.csv"), new MessageHeaders(messageHeaders));

    CompletableFuture<Job> jobFuture =
        (CompletableFuture<Job>) this.messageHandler.handleRequestMessage(message);

    // Assert that a BigQuery polling task is scheduled successfully.
    await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () ->
                assertThat(this.taskScheduler.getScheduledThreadPoolExecutor().getQueue())
                    .hasSize(1));
    jobFuture.get();

    QueryJobConfiguration queryJobConfiguration =
        QueryJobConfiguration.newBuilder("SELECT * FROM test_dataset.test_table").build();
    TableResult result = this.bigquery.query(queryJobConfiguration);

    assertThat(result.getTotalRows()).isEqualTo(1);
    assertThat(result.getValues().iterator().next().get("State").getStringValue())
        .isEqualTo("Alabama");

    // This asserts that the BigQuery job polling task is no longer in the scheduler.
    assertThat(this.taskScheduler.getScheduledThreadPoolExecutor().getQueue()).isEmpty();
  }

  @Test
  void testLoadFile_sync() throws InterruptedException {
    this.messageHandler.setSync(true);

    HashMap<String, Object> messageHeaders = new HashMap<>();
    messageHeaders.put(BigQuerySpringMessageHeaders.TABLE_NAME, TABLE_NAME);
    messageHeaders.put(BigQuerySpringMessageHeaders.FORMAT_OPTIONS, FormatOptions.csv());

    Message<File> message =
        MessageBuilder.createMessage(
            new File("src/test/resources/data.csv"), new MessageHeaders(messageHeaders));

    Job job = (Job) this.messageHandler.handleRequestMessage(message);
    assertThat(job).isNotNull();

    QueryJobConfiguration queryJobConfiguration =
        QueryJobConfiguration.newBuilder("SELECT * FROM test_dataset.test_table").build();
    TableResult result = this.bigquery.query(queryJobConfiguration);
    assertThat(result.getTotalRows()).isEqualTo(1);
  }

  @Test
  void testLoadFile_cancel() {
    HashMap<String, Object> messageHeaders = new HashMap<>();
    messageHeaders.put(BigQuerySpringMessageHeaders.TABLE_NAME, TABLE_NAME);
    messageHeaders.put(BigQuerySpringMessageHeaders.FORMAT_OPTIONS, FormatOptions.csv());

    Message<File> message =
        MessageBuilder.createMessage(
            new File("src/test/resources/data.csv"), new MessageHeaders(messageHeaders));

    CompletableFuture<?> jobFuture =
        (CompletableFuture<?>) this.messageHandler.handleRequestMessage(message);
    assertThat(jobFuture).isNotNull();
    jobFuture.cancel(true);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              // This asserts that the BigQuery job polling task is no longer in the scheduler after
              // cancel.
              assertThat(this.taskScheduler.getScheduledThreadPoolExecutor().getQueue()).isEmpty();
            });
  }
}
