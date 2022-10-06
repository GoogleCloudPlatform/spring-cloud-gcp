/*
 * Copyright 2017-2022 the original author or authors.
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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.bigquery.storage.v1.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.CreateWriteStreamRequest;
import com.google.cloud.bigquery.storage.v1.Exceptions;
import com.google.cloud.bigquery.storage.v1.Exceptions.StorageException;
import com.google.cloud.bigquery.storage.v1.FinalizeWriteStreamResponse;
import com.google.cloud.bigquery.storage.v1.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.cloud.bigquery.storage.v1.WriteStream;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import java.io.IOException;
import java.util.concurrent.Phaser;
import javax.annotation.concurrent.GuardedBy;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helper class for using BigQuery storage write API in exactly once delivery mode. */
public class BigQueryJsonDataWriter implements AutoCloseable {

  private final JsonStreamWriter streamWriter;
  // Track the number of in-flight requests to wait for all responses before shutting down.
  private final Phaser inflightRequestCount = new Phaser(1);

  private final Object lock = new Object();

  private final Logger logger = LoggerFactory.getLogger(BigQueryJsonDataWriter.class);

  private final BigQueryWriteClient bigQueryWriteClient;

  @GuardedBy("lock")
  private RuntimeException error = null;

  /**
   * Package-Private constructor for initializing BigQueryJsonDataWriter
   *
   * @param parentTable against which the writer has to be initialized
   * @param bigQueryWriteClient BigQueryWriteClient reference which has to be used for writing to
   *     the database
   */
  BigQueryJsonDataWriter(TableName parentTable, BigQueryWriteClient bigQueryWriteClient)
      throws DescriptorValidationException, IOException, InterruptedException {
    // Initialize a write stream for the specified table.
    // For more information on WriteStream.Type, see:
    // https://googleapis.dev/java/google-cloud-bigquerystorage/latest/com/google/cloud/bigquery/storage/v1/WriteStream.Type.html
    WriteStream stream = WriteStream.newBuilder().setType(WriteStream.Type.PENDING).build();

    CreateWriteStreamRequest createWriteStreamRequest =
        CreateWriteStreamRequest.newBuilder()
            .setParent(parentTable.toString())
            .setWriteStream(stream)
            .build();
    WriteStream writeStream = bigQueryWriteClient.createWriteStream(createWriteStreamRequest);

    // Use the JSON stream writer to send records in JSON format.
    // For more information about JsonStreamWriter, see:
    // https://googleapis.dev/java/google-cloud-bigquerystorage/latest/com/google/cloud/bigquery/storage/v1beta2/JsonStreamWriter.html
    streamWriter =
        JsonStreamWriter.newBuilder(writeStream.getName(), writeStream.getTableSchema()).build();
    this.bigQueryWriteClient = bigQueryWriteClient;
  }

  /**
   * @param data JSONArray to be appended
   * @param offset offset at which data has to be added
   */
  public void append(JSONArray data, long offset)
      throws DescriptorValidationException, IOException {
    synchronized (this.lock) {
      // If earlier appends have failed, we need to reset before continuing.
      if (this.error != null) {
        throw this.error;
      }
    }
    // Append asynchronously for increased throughput.
    ApiFuture<AppendRowsResponse> future = streamWriter.append(data, offset);
    ApiFutures.addCallback(future, new AppendCompleteCallback(), MoreExecutors.directExecutor());
    // Increase the count of in-flight requests.
    inflightRequestCount.register();
  }

  /** Call this method before committing the stream */
  public void finalizeWriteStream() {
    // Wait for all in-flight requests to complete.
    inflightRequestCount.arriveAndAwaitAdvance();

    // Verify that no error occurred in the stream.
    synchronized (this.lock) {
      if (this.error != null) {
        throw this.error;
      }
    }

    // Finalize the stream.
    FinalizeWriteStreamResponse finalizeResponse =
        bigQueryWriteClient.finalizeWriteStream(streamWriter.getStreamName());
    logger.info("Rows written: {}", finalizeResponse.getRowCount());
  }

  public String getStreamName() {
    return streamWriter.getStreamName();
  }

  @Override
  public void close() throws Exception {
    // Close the connection to the server.
    streamWriter.close();
  }

  class AppendCompleteCallback implements ApiFutureCallback<AppendRowsResponse> {
    private final Logger logger = LoggerFactory.getLogger(AppendCompleteCallback.class);

    public void onSuccess(AppendRowsResponse response) {
      logger.info("Append {} success", response.getAppendResult().getOffset().getValue());
      done();
    }

    public void onFailure(Throwable throwable) {
      String errMsg = null;
      synchronized (lock) {
        if (error == null) {
          StorageException storageException = Exceptions.toStorageException(throwable);
          error = (storageException != null) ? storageException : new RuntimeException(throwable);
          errMsg = error.getMessage();
        }
      }
      logger.warn("Error: {}", errMsg);
      done();
    }

    private void done() {
      // Reduce the count of in-flight requests.
      inflightRequestCount.arriveAndDeregister();
    }
  }
}
