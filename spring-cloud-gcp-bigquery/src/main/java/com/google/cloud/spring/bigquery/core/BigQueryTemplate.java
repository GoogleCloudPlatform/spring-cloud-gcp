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
import com.google.cloud.bigquery.JobInfo.WriteDisposition;
import com.google.cloud.bigquery.JobStatus.State;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.TableDataWriteChannel;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import com.google.cloud.bigquery.storage.v1.BatchCommitWriteStreamsRequest;
import com.google.cloud.bigquery.storage.v1.BatchCommitWriteStreamsResponse;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.StorageError;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

/**
 * Helper class which simplifies common operations done in BigQuery.
 *
 * @since 1.2
 */
public class BigQueryTemplate implements BigQueryOperations {

  private final BigQuery bigQuery;

  private final String datasetName;

  private final TaskScheduler taskScheduler;

  private boolean autoDetectSchema = true;

  private WriteDisposition writeDisposition = WriteDisposition.WRITE_APPEND;

  private Duration jobPollInterval = Duration.ofSeconds(2);

  private static final int JSON_STREAM_WRITER_BATCH_SIZE = 1000; // write records in batches of 1000

  /**
   * Creates the {@link BigQuery} template.
   *
   * @param bigQuery the underlying client object used to interface with BigQuery
   * @param datasetName the name of the dataset in which all operations will take place
   */
  public BigQueryTemplate(BigQuery bigQuery, String datasetName) {
    this(bigQuery, datasetName, new DefaultManagedTaskScheduler());
  }

  /**
   * Creates the {@link BigQuery} template.
   *
   * @param bigQuery the underlying client object used to interface with BigQuery
   * @param datasetName the name of the dataset in which all operations will take place
   * @param taskScheduler the {@link TaskScheduler} used to poll for the status of long-running
   *     BigQuery operations
   */
  public BigQueryTemplate(BigQuery bigQuery, String datasetName, TaskScheduler taskScheduler) {
    Assert.notNull(bigQuery, "BigQuery client object must not be null.");
    Assert.notNull(datasetName, "Dataset name must not be null");
    Assert.notNull(taskScheduler, "TaskScheduler must not be null");

    this.bigQuery = bigQuery;
    this.datasetName = datasetName;
    this.taskScheduler = taskScheduler;
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
  public ListenableFuture<Job> writeDataToTable(
      String tableName, InputStream inputStream, FormatOptions dataFormatOptions) {
    return this.writeDataToTable(tableName, inputStream, dataFormatOptions, null);
  }

  @Override
  public ListenableFuture<Job> writeDataToTable(
      String tableName, InputStream inputStream, FormatOptions dataFormatOptions, Schema schema) {

    TableId tableId = TableId.of(datasetName, tableName);

    WriteChannelConfiguration.Builder writeChannelConfiguration =
        WriteChannelConfiguration.newBuilder(tableId)
            .setFormatOptions(dataFormatOptions)
            .setWriteDisposition(this.writeDisposition)
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

  @Override
  public ListenableFuture<WriteApiResponse> writeJsonStream(
      String tableName, InputStream jsonInputStream)
      throws DescriptorValidationException, IOException, InterruptedException {
    WriteApiResponse apiResponse = new WriteApiResponse();
    BigQueryWriteClient client = BigQueryWriteClient.create();
    TableName parentTable =
        TableName.of(bigQuery.getOptions().getProjectId(), datasetName, tableName);

    BigQueryJsonDataWriter writer = new BigQueryJsonDataWriter();
    // One time initialization.
    writer.initialize(parentTable, client);

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
            == JSON_STREAM_WRITER_BATCH_SIZE) { // append the batch, increment the offset and reset
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
        offset += jsonBatch.length();
      }

    } catch (ExecutionException e) {
      // If the wrapped exception is a StatusRuntimeException, check the state of the operation.
      // If the state is INTERNAL, CANCELLED, or ABORTED, you can retry. For more information, see:
      // https://grpc.github.io/grpc-java/javadoc/io/grpc/StatusRuntimeException.html
      throw new BigQueryException("Failed to append records. \n" + e);
    }

    // Final cleanup for the stream.
    writer.cleanup(client);
    // TODO(prasmish): Add logger @ Level.FINE to print offset and msg

    // Once all streams are done, if all writes were successful, commit all of them in one request.
    // If any streams failed, their workload may be
    // retried on a new stream, and then only the successful stream should be included in the
    // commit.
    BatchCommitWriteStreamsRequest commitRequest =
        BatchCommitWriteStreamsRequest.newBuilder()
            .setParent(parentTable.toString())
            .addWriteStreams(writer.getStreamName())
            .build();
    BatchCommitWriteStreamsResponse commitResponse = client.batchCommitWriteStreams(commitRequest);
    // If the response does not have a commit time, it means the commit operation failed.
    if (commitResponse.hasCommitTime() == false) {
      for (StorageError err : commitResponse.getStreamErrorsList()) {
        apiResponse.addError(err); // this object is returned to the user
      }
    }

    // set isSucccessful flag to true of there were no errors
    if (apiResponse.getErrors().size() == 0) {
      apiResponse.setSuccessful(true);
    }

    // TODO(prasmish): Currently we are returning null, program for ListenableFuture
    return null;
  }

  // @return the name of the BigQuery dataset that the template is operating in.
  public String getDatasetName() {
    return this.datasetName;
  }

  private SettableListenableFuture<Job> createJobFuture(Job pendingJob) {
    // Prepare the polling task for the ListenableFuture result returned to end-user
    SettableListenableFuture<Job> result = new SettableListenableFuture<>();

    ScheduledFuture<?> scheduledFuture =
        taskScheduler.scheduleAtFixedRate(
            () -> {
              try {
                Job job = pendingJob.reload();
                if (State.DONE.equals(job.getStatus().getState())) {
                  if (job.getStatus().getError() != null) {
                    result.setException(
                        new BigQueryException(job.getStatus().getError().getMessage()));
                  } else {
                    result.set(job);
                  }
                }
              } catch (Exception e) {
                result.setException(new BigQueryException(e.getMessage()));
              }
            },
            this.jobPollInterval);

    result.addCallback(
        response -> scheduledFuture.cancel(true),
        response -> {
          pendingJob.cancel();
          scheduledFuture.cancel(true);
        });

    return result;
  }
}
