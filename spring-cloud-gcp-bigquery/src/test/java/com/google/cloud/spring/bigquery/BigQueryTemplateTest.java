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

package com.google.cloud.spring.bigquery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.ServiceOptions;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.spi.BigQueryRpcFactory;
import com.google.cloud.bigquery.spi.v2.BigQueryRpc;
import com.google.cloud.bigquery.storage.v1.BatchCommitWriteStreamsResponse;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.cloud.spring.bigquery.core.BigQueryJsonDataWriter;
import com.google.cloud.spring.bigquery.core.BigQueryTemplate;
import com.google.cloud.spring.bigquery.core.WriteApiResponse;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;

@ExtendWith(MockitoExtension.class)
class BigQueryTemplateTest {

  private BigQueryWriteClient bigQueryWriteClientMock;
  private String newLineSeperatedJson =
      "{\"CompanyName\":\"TALES\",\"Description\":\"mark\",\"SerialNumber\":97,\"Leave\":0,\"EmpName\":\"Mark\"}\n"
          + "{\"CompanyName\":\"1Q84\",\"Description\":\"ark\",\"SerialNumber\":978,\"Leave\":0,\"EmpName\":\"HARUKI\"}";
  private static final String PROJECT = "project";
  private static final String DATASET = "dataset";
  private static final String TABLE = "table";
  private static final int JSON_WRITER_BATCH_SIZE = 1000;

  private BigQueryRpcFactory rpcFactoryMock;
  private BigQueryRpc bigqueryRpcMock;
  private BigQuery bigquery;
  private BigQueryOptions options;
  private Map<String, Object> bqInitSettings = new HashMap<>();
  BigQueryTemplate bqTemplateSpy;

  private Schema getDefaultSchema() {
    return Schema.of(
        Field.of("CompanyName", StandardSQLTypeName.STRING),
        Field.of("Description", StandardSQLTypeName.STRING),
        Field.of("SerialNumber", StandardSQLTypeName.NUMERIC),
        Field.of("Leave", StandardSQLTypeName.NUMERIC),
        Field.of("EmpName", StandardSQLTypeName.STRING));
  }

  /*
  Create mock instance of the required class
   */
  @BeforeEach
  public void setUp() {
    rpcFactoryMock = mock(BigQueryRpcFactory.class);
    bigqueryRpcMock = mock(BigQueryRpc.class);
    when(rpcFactoryMock.create(any(BigQueryOptions.class))).thenReturn(bigqueryRpcMock);
    options = createBigQueryOptionsForProject(PROJECT, rpcFactoryMock);
    bigQueryWriteClientMock = mock(BigQueryWriteClient.class);
    bigquery = options.getService();
    bqInitSettings.put("DATASET_NAME", DATASET);
    bqInitSettings.put("JSON_WRITER_BATCH_SIZE", JSON_WRITER_BATCH_SIZE);

    BigQueryTemplate bqTemplate =
        new BigQueryTemplate(
            bigquery, bigQueryWriteClientMock, bqInitSettings, new DefaultManagedTaskScheduler());
    bqTemplateSpy = Mockito.spy(bqTemplate);
  }

  private BigQueryOptions createBigQueryOptionsForProject(
      String project, BigQueryRpcFactory rpcFactory) {
    return BigQueryOptions.newBuilder()
        .setProjectId(project)
        .setServiceRpcFactory(rpcFactory)
        .setRetrySettings(ServiceOptions.getNoRetrySettings())
        .build();
  }

  @Test
  void getWriteApiResponseTest()
      throws DescriptorValidationException, IOException, InterruptedException {

    InputStream jsoninputStream = new ByteArrayInputStream(newLineSeperatedJson.getBytes());

    doReturn(mock(BigQueryJsonDataWriter.class))
        .when(bqTemplateSpy)
        .getBigQueryJsonDataWriter(any(TableName.class));

    BatchCommitWriteStreamsResponse batchCommitWriteStreamsResponse =
        BatchCommitWriteStreamsResponse.getDefaultInstance();

    doReturn(batchCommitWriteStreamsResponse)
        .when(bqTemplateSpy)
        .getCommitResponse(any(TableName.class), any(BigQueryJsonDataWriter.class));

    WriteApiResponse apiRes = bqTemplateSpy.getWriteApiResponse(TABLE, jsoninputStream);

    assertTrue(apiRes.isSuccessful());
    assertEquals(0, apiRes.getErrors().size());
  }

  @Test
  void writeJsonStreamTest()
      throws DescriptorValidationException, IOException, InterruptedException, ExecutionException {

    InputStream jsoninputStream = new ByteArrayInputStream(newLineSeperatedJson.getBytes());
    WriteApiResponse apiResponse = new WriteApiResponse();
    apiResponse.setSuccessful(true);
    doReturn(apiResponse)
        .when(bqTemplateSpy)
        .getWriteApiResponse(any(String.class), any(InputStream.class));

    ListenableFuture<WriteApiResponse> futRes =
        bqTemplateSpy.writeJsonStream(TABLE, jsoninputStream);
    WriteApiResponse apiRes = futRes.get();
    assertTrue(apiRes.isSuccessful());
    assertEquals(0, apiRes.getErrors().size());
  }

  @Test
  void writeJsonStreamWithSchemaTest()
      throws DescriptorValidationException, IOException, InterruptedException, ExecutionException {

    InputStream jsoninputStream = new ByteArrayInputStream(newLineSeperatedJson.getBytes());
    WriteApiResponse apiResponse = new WriteApiResponse();
    apiResponse.setSuccessful(true);
    doReturn(apiResponse)
        .when(bqTemplateSpy)
        .getWriteApiResponse(any(String.class), any(InputStream.class));

    doReturn(mock(Table.class))
        .when(bqTemplateSpy)
        .createTable(any(String.class), any(Schema.class));

    ListenableFuture<WriteApiResponse> futRes =
        bqTemplateSpy.writeJsonStream(TABLE, jsoninputStream, getDefaultSchema());
    WriteApiResponse apiRes = futRes.get();
    assertTrue(apiRes.isSuccessful());
    assertEquals(0, apiRes.getErrors().size());
  }


  @Test
  void writeJsonStreamFailsOnGenericWritingException()
      throws DescriptorValidationException, IOException, InterruptedException, ExecutionException {

    InputStream jsoninputStream = new ByteArrayInputStream(newLineSeperatedJson.getBytes());
    doReturn(mock(Table.class))
        .when(bqTemplateSpy)
        .createTable(any(String.class), any(Schema.class));

    doThrow(new NullPointerException("boom!"))
        .when(bqTemplateSpy)
        .getWriteApiResponse(any(String.class), any(InputStream.class));

    ListenableFuture<WriteApiResponse> futRes =
        bqTemplateSpy.writeJsonStream(TABLE, jsoninputStream, getDefaultSchema());
    assertThat(futRes)
        .withFailMessage("boom!")
        .failsWithin(Duration.ofSeconds(1));
  }
}
