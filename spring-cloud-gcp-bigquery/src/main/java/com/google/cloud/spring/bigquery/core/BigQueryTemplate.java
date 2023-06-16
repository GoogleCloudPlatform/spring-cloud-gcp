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
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobInfo.CreateDisposition;
import com.google.cloud.bigquery.JobInfo.WriteDisposition;
import com.google.cloud.bigquery.JobStatus.State;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableDataWriteChannel;
import com.google.cloud.bigquery.TableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import com.google.cloud.bigquery.storage.v1.BatchCommitWriteStreamsRequest;
import com.google.cloud.bigquery.storage.v1.BatchCommitWriteStreamsResponse;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.StorageError;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

/**
 * Helper class which simplifies common operations done in BigQuery.
 *
 * @since 1.2
 */
public class BigQueryTemplate implements BigQueryOperations {

  private final BigQuery bigQuery;

  private final String datasetName;

  private final TaskScheduler taskScheduler;

  private final BigQueryWriteClient bigQueryWriteClient;

  private boolean autoDetectSchema = true;

  private WriteDisposition writeDisposition = WriteDisposition.WRITE_APPEND;

  private CreateDisposition createDisposition;

  private Duration jobPollInterval = Duration.ofSeconds(2);

  private static final int DEFAULT_JSON_STREAM_WRITER_BATCH_SIZE =
      1000; // write records in batches of 1000

  private static final int MIN_JSON_STREAM_WRITER_BATCH_SIZE = 10; // minimum batch size

  private final Logger logger = LoggerFactory.getLogger(BigQueryTemplate.class);

  private final int jsonWriterBatchSize;

  /**
   * A Full constructor which creates the {@link BigQuery} template.
   *
   * @param bigQuery the underlying client object used to interface with BigQuery
   * @param bigQueryWriteClient the underlying BigQueryWriteClient reference use to connect with
   *     BigQuery Storage Write Client
   * @param bqInitSettings Properties required for initialisation of this class
   * @param taskScheduler the {@link TaskScheduler} used to poll for the status of long-running
   *     BigQuery operations
   */
  public BigQueryTemplate(
      BigQuery bigQuery,
      BigQueryWriteClient bigQueryWriteClient,
      Map<String, Object> bqInitSettings,
      TaskScheduler taskScheduler) {
    String bqDatasetName = (String) bqInitSettings.get("DATASET_NAME");
    Assert.notNull(bigQuery, "BigQuery client object must not be null.");
    Assert.notNull(bqDatasetName, "Dataset name must not be null");
    Assert.notNull(taskScheduler, "TaskScheduler must not be null");
    Assert.notNull(bigQueryWriteClient, "BigQueryWriteClient must not be null");
    jsonWriterBatchSize =
        (Integer)
            bqInitSettings.getOrDefault(
                "JSON_WRITER_BATCH_SIZE", DEFAULT_JSON_STREAM_WRITER_BATCH_SIZE);
    this.bigQuery = bigQuery;
    this.datasetName = bqDatasetName;
    this.taskScheduler = taskScheduler;
    this.bigQueryWriteClient = bigQueryWriteClient;
  }

  /**
   * Sets whether BigQuery should attempt to autodetect the schema of the data when loading data
   * into an empty table for the first time. If set to false, the schema must be defined explicitly
   * for the table before load.
   *
   * @param autoDetectSchema whether data schema should be autodetected from the structure of the
   *     data. Default is true.
   */
  public void setAutoDetectSchema(boolean autoDetectSchema) {
    this.autoDetectSchema = autoDetectSchema;
  }

  /**
   * Sets the {@link WriteDisposition} which specifies how data should be inserted into BigQuery
   * tables.
   *
   * @param writeDisposition whether to append to or truncate (overwrite) data in the BigQuery
   *     table. Default is {@code WriteDisposition.WRITE_APPEND} to append data to a table.
   */
  public void setWriteDisposition(WriteDisposition writeDisposition) {
    Assert.notNull(writeDisposition, "BigQuery write disposition must not be null.");
    this.writeDisposition = writeDisposition;
  }

  /**
   * Sets the {@link CreateDisposition} which specifies whether a new table may be created in
   * BigQuery if needed.
   *
   * @param createDisposition whether to never create a new table in the BigQuery table or only if
   *     needed.
   */
  public void setCreateDisposition(CreateDisposition createDisposition) {
    Assert.notNull(createDisposition, "BigQuery create disposition must not be null.");
    this.createDisposition = createDisposition;
  }

  /**
   * Sets the {@link Duration} amount of time to wait between successive polls on the status of a
   * BigQuery job.
   *
   * @param jobPollInterval the {@link Duration} poll interval for BigQuery job status polling
   */
  public void setJobPollInterval(Duration jobPollInterval) {
    Assert.notNull(jobPollInterval, "BigQuery job polling interval must not be null");
    this.jobPollInterval = jobPollInterval;
  }

  @Override
  public CompletableFuture<Job> writeDataToTable(
      String tableName, InputStream inputStream, FormatOptions dataFormatOptions) {
    return this.writeDataToTable(tableName, inputStream, dataFormatOptions, null);
  }

  @Override
  public CompletableFuture<Job> writeDataToTable(
      String tableName, InputStream inputStream, FormatOptions dataFormatOptions, Schema schema) {

    TableId tableId = TableId.of(datasetName, tableName);

    WriteChannelConfiguration.Builder writeChannelConfiguration =
        WriteChannelConfiguration.newBuilder(tableId)
            .setFormatOptions(dataFormatOptions)
            .setWriteDisposition(this.writeDisposition)
            .setCreateDisposition(this.createDisposition)
            .setAutodetect(this.autoDetectSchema);

    if (schema != null) {
      writeChannelConfiguration.setSchema(schema);
    }

    TableDataWriteChannel writer = bigQuery.writer(writeChannelConfiguration.build());

    try (OutputStream sink = Channels.newOutputStream(writer)) {
      // Write data from data input file to BigQuery
      StreamUtils.copy(inputStream, sink);
    } catch (IOException e) {
      throw new BigQueryException("Failed to write data to BigQuery tables.", e);
    }

    if (writer.getJob() == null) {
      throw new BigQueryException("Failed to initialize the BigQuery write job.");
    }

    return createJobFuture(writer.getJob());
  }

  /**
   * This method uses BigQuery Storage Write API to write new line delimited JSON file to the
   * specified table. This method creates a table with the specified schema.
   *
   * @param tableName name of the table to write to
   * @param jsonInputStream input stream of the json file to be written
   * @return {@link CompletableFuture} containing the WriteApiResponse indicating completion of
   *     operation
   */
  @Override
  public CompletableFuture<WriteApiResponse> writeJsonStream(
      String tableName, InputStream jsonInputStream, Schema schema) {
    createTable(tableName, schema); // create table if it's not already created
    return writeJsonStream(tableName, jsonInputStream);
  }

  @VisibleForTesting
  public Table createTable(
      String tableName, Schema schema) { // create table if it's not already created
    TableId tableId = TableId.of(datasetName, tableName);
    Table table = bigQuery.getTable(TableId.of(datasetName, tableName));
    if (table == null || !table.exists()) {
      TableDefinition tableDefinition = StandardTableDefinition.of(schema);
      TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();
      return bigQuery.create(tableInfo);
    } else {
      return null;
    }
  }

  /**
   * This method uses BigQuery Storage Write API to write new line delimited JSON file to the
   * specified table. The Table should already be created as BigQuery Storage Write API doesn't
   * create it automatically.
   *
   * @param tableName name of the table to write to
   * @param jsonInputStream input stream of the json file to be written
   * @return {@link CompletableFuture} containing the WriteApiResponse indicating completion of
   *     operation
   */
  @Override
  public CompletableFuture<WriteApiResponse> writeJsonStream(
      String tableName, InputStream jsonInputStream) {

    CompletableFuture<WriteApiResponse> writeApiFutureResponse = new CompletableFuture<>();
    Runnable asyncTask =
        () -> {
          try {
            WriteApiResponse apiResponse = getWriteApiResponse(tableName, jsonInputStream);
            writeApiFutureResponse.complete(apiResponse);
          } catch (Exception e) {
            writeApiFutureResponse.completeExceptionally(e);
            // Restore interrupted state in case of an InterruptedException
            Thread.currentThread().interrupt();
            logger.warn("Unable to get write API response.", e);
          }
        };
    ScheduledFuture<?> asyncTaskScheduledFuture =
        taskScheduler.schedule(asyncTask, Instant.now()); // Run this task
    // register success and failure callback
    writeApiFutureResponse.whenComplete(
        (writeApiResponse, exception) -> {
          if (exception != null) {
            logger.error("asyncTask interrupted", exception);
            if (exception instanceof CancellationException) { // user have cancelled it
              asyncTaskScheduledFuture.cancel(true);
            }
          } else if (writeApiResponse != null && !writeApiResponse.isSuccessful()) {
            logger.warn("Write operation failed");
          } else {
            logger.info("Data successfully written");
          }
        });

    return writeApiFutureResponse;
  }

  @VisibleForTesting
  public BigQueryJsonDataWriter getBigQueryJsonDataWriter(TableName parentTable)
      throws DescriptorValidationException, IOException, InterruptedException {
    return new BigQueryJsonDataWriter(parentTable, bigQueryWriteClient);
  }

  public WriteApiResponse getWriteApiResponse(String tableName, InputStream jsonInputStream)
      throws DescriptorValidationException, IOException, InterruptedException {
    WriteApiResponse apiResponse = new WriteApiResponse();
    TableName parentTable =
        TableName.of(bigQuery.getOptions().getProjectId(), datasetName, tableName);

    // Initialize a write stream for the specified table.
    BigQueryJsonDataWriter writer = getBigQueryJsonDataWriter(parentTable);

    try {
      // Write data in batches. Ref: https://cloud.google.com/bigquery/quotas#write-api-limits
      long offset = 0;
      int currentBatchSize = 0;

      BufferedReader jsonReader = new BufferedReader(new InputStreamReader(jsonInputStream));
      String jsonLine = null;
      JSONArray jsonBatch = new JSONArray();
      while ((jsonLine = jsonReader.readLine()) != null) { // read the input stream line by line
        JSONObject jsonObj = new JSONObject(jsonLine); // cast the JSON string into JSON Object
        jsonBatch.put(jsonObj);
        currentBatchSize++;
        if (currentBatchSize
            == getBatchSize()) { // append the batch, increment the offset and reset
          // the batch
          writer.append(jsonBatch, offset);
          offset += jsonBatch.length();
          jsonBatch = new JSONArray();
          currentBatchSize = 0;
        }
      }

      if (jsonBatch.length()
          != 0) { // there might be records less than JSON_STREAM_WRITER_BATCH_SIZE, append those as
        // well
        writer.append(jsonBatch, offset);
      }

    } catch (Exception e) {
      throw new BigQueryException("Failed to append records. \n" + e);
    }

    // Finalize the stream before committing it
    writer.finalizeWriteStream();

    BatchCommitWriteStreamsResponse commitResponse = getCommitResponse(parentTable, writer);
    // If the response does not have a commit time, it means the commit operation failed.
    if (!commitResponse.hasCommitTime()) {
      for (StorageError err : commitResponse.getStreamErrorsList()) {
        apiResponse.addError(err); // this object is returned to the user
      }
    }

    // set isSuccessful flag to true of there were no errors
    if (apiResponse.getErrors().isEmpty()) {
      apiResponse.setSuccessful(true);
    }

    return apiResponse;
  }

  @VisibleForTesting
  public BatchCommitWriteStreamsResponse getCommitResponse(
      TableName parentTable, BigQueryJsonDataWriter writer) {
    // commit the stream
    BatchCommitWriteStreamsRequest commitRequest =
        BatchCommitWriteStreamsRequest.newBuilder()
            .setParent(parentTable.toString())
            .addWriteStreams(writer.getStreamName())
            .build();
    return bigQueryWriteClient.batchCommitWriteStreams(commitRequest);
  }

  /**
   * This method ensures that we use the DEFAULT_JSON_STREAM_WRITER_BATCH_SIZE if the user doesn't
   * set this property or if they set it too low.
   *
   * @return jsonWriterBatchSize
   */
  private int getBatchSize() {
    return jsonWriterBatchSize > MIN_JSON_STREAM_WRITER_BATCH_SIZE
        ? jsonWriterBatchSize
        : DEFAULT_JSON_STREAM_WRITER_BATCH_SIZE;
  }

  // @return the name of the BigQuery dataset that the template is operating in.
  public String getDatasetName() {
    return this.datasetName;
  }

  // @return the name of the BigQuery jsonWriterBatchSize that the template is operating in.
  public int getJsonWriterBatchSize() {
    return this.jsonWriterBatchSize;
  }

  private CompletableFuture<Job> createJobFuture(Job pendingJob) {
    // Prepare the polling task for the CompletableFuture result returned to end-user
    CompletableFuture<Job> result = new CompletableFuture<>();
    ScheduledFuture<?> scheduledFuture =
        taskScheduler.scheduleAtFixedRate(
            () -> {
              try {
                Job job = pendingJob.reload();
                if (State.DONE.equals(job.getStatus().getState())) {
                  if (job.getStatus().getError() != null) {
                    result.completeExceptionally(
                        new BigQueryException(job.getStatus().getError().getMessage()));
                  } else {
                    result.complete(job);
                  }
                }
              } catch (Exception e) {
                result.completeExceptionally(new BigQueryException(e.getMessage()));
              }
            },
            this.jobPollInterval);

    result.whenComplete(
        (response, exception) -> {
          if (exception != null) {
            pendingJob.cancel();
            scheduledFuture.cancel(true);
            return;
          }
          scheduledFuture.cancel(true);
        });

    return result;
  }
}
