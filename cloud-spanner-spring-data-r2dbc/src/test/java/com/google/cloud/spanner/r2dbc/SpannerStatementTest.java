/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spanner.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.r2dbc.client.Client;
import com.google.longrunning.Operation;
import com.google.protobuf.ByteString;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.spanner.v1.ExecuteBatchDmlResponse;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.ResultSet;
import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.ResultSetStats;
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import io.r2dbc.spi.Result;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Test for {@link SpannerStatement}.
 */
public class SpannerStatementTest {

  private static final SpannerConnectionConfiguration TEST_CONFIG =
      new SpannerConnectionConfiguration.Builder()
          .setInstanceName("test-instance")
          .setProjectId("project")
          .setDatabaseName("db")
          .build();

  private final Value a1 = Value.newBuilder().setBoolValue(false).build();
  private final Value a2 = Value.newBuilder().setStringValue("abc").build();

  private final ResultSetMetadata resultSetMetadata = ResultSetMetadata.newBuilder().setRowType(
      StructType.newBuilder()
          .addFields(Field.newBuilder().setName("boolField")
              .setType(Type.newBuilder().setCode(TypeCode.BOOL).build()).build())
          .addFields(Field.newBuilder().setName("stringField")
              .setType(Type.newBuilder().setCode(TypeCode.STRING).build()).build())
          .build()
  ).build();

  private Client mockClient = mock(Client.class);

  private StatementExecutionContext mockContext = mock(StatementExecutionContext.class);

  private SpannerConnection mockConnection = mock(SpannerConnection.class);


  @Before
  public void setupMocks() {
    when(this.mockConnection.beginTransaction()).thenReturn(Mono.empty());
    when(this.mockConnection.commitTransaction()).thenReturn(Mono.empty());
  }

  @Test
  public void executeDummyImplementation() {
    String sql = "select book from library";
    PartialResultSet partialResultSet = PartialResultSet.newBuilder()
        .setMetadata(ResultSetMetadata.newBuilder().setRowType(StructType.newBuilder()
            .addFields(
                Field.newBuilder().setName("book")
                    .setType(Type.newBuilder().setCode(TypeCode.STRING)))))
        .addValues(Value.newBuilder().setStringValue("Odyssey"))
        .build();
    when(this.mockClient.executeStreamingSql(same(this.mockContext), eq(sql), any(), any()))
        .thenReturn(Flux.just(partialResultSet));

    SpannerStatement statement =
        new SpannerStatement(this.mockClient, this.mockContext, sql, TEST_CONFIG);

    Flux<SpannerResult> result = (Flux<SpannerResult>) statement.execute();

    assertThat(result).isNotNull();

    StepVerifier.create(result.flatMap(
        spannerResult -> spannerResult.map((row, rowMetadata) -> (String) row.get(0))))
            .expectNext("Odyssey")
            .verifyComplete();

    verify(this.mockClient).executeStreamingSql(same(this.mockContext), eq(sql), any(), any());
  }

  @Test
  public void executeDummyImplementationBind() {
    //set up mock results
    PartialResultSet partialResultSet1 = PartialResultSet.newBuilder()
        .setMetadata(ResultSetMetadata.newBuilder().setRowType(StructType.newBuilder()
            .addFields(
                Field.newBuilder().setName("book")
                    .setType(Type.newBuilder().setCode(TypeCode.STRING)))))
        .addValues(Value.newBuilder().setStringValue("Odyssey"))
        .build();

    PartialResultSet partialResultSet2 = PartialResultSet.newBuilder()
        .setMetadata(ResultSetMetadata.newBuilder().setRowType(StructType.newBuilder()
            .addFields(
                Field.newBuilder().setName("book")
                    .setType(Type.newBuilder().setCode(TypeCode.STRING)))))
        .addValues(Value.newBuilder().setStringValue("Fables"))
        .build();

    String sql = "select book from library where id = @id";

    //set up mock client to return the results
    Map<String, Type> types = new HashMap<>();
    types.put("id", Type.newBuilder().setCode(TypeCode.STRING).build());
    Struct idBinding1 = Struct.newBuilder()
        .putFields("id", Value.newBuilder().setStringValue("b1").build()).build();
    when(this.mockClient.executeStreamingSql(this.mockContext, sql,
        idBinding1, types))
        .thenReturn(Flux.just(partialResultSet1));

    Struct idBinding2 = Struct.newBuilder().putFields("id", Value.newBuilder()
        .setStringValue("b2").build()).build();
    when(this.mockClient.executeStreamingSql(this.mockContext, sql,
        idBinding2, types))
        .thenReturn(Flux.just(partialResultSet2));

    //execute query
    SpannerStatement statement
        = new SpannerStatement(this.mockClient, this.mockContext, sql, TEST_CONFIG);

    Flux<SpannerResult> result = (Flux<SpannerResult>) statement
        .bind("id", "b1").add()
        .bind("id", "b2")
        .execute();

    StepVerifier.create(result.flatMap(
        spannerResult -> spannerResult.map((row, rowMetadata) -> (String) row.get(0))))
        .expectNext("Odyssey")
        .expectNext("Fables")
        .verifyComplete();

    verify(this.mockClient, times(1)).executeStreamingSql(this.mockContext, sql, idBinding1, types);
    verify(this.mockClient, times(1)).executeStreamingSql(this.mockContext, sql, idBinding2, types);
  }

  @Test
  public void readOneResultSetQueryTest() {
    PartialResultSet p1 = PartialResultSet.newBuilder().setMetadata(
        this.resultSetMetadata
    ).setChunkedValue(false)
        .addValues(this.a1)
        .addValues(this.a2).build();

    Flux<PartialResultSet> inputs = Flux.just(p1);

    when(this.mockClient.executeStreamingSql(
        same(this.mockContext), any(), any(), any())).thenReturn(inputs);

    Mono<Result> resultMono =
        Mono.from(new SpannerStatement(
            this.mockClient, this.mockContext, "SELECT * FROM table", TEST_CONFIG).execute());

    StepVerifier.create(resultMono.flatMap(r -> Mono.from(r.getRowsUpdated())))
        .expectNext(0)
        .verifyComplete();
    StepVerifier.create(resultMono.flatMapMany(r -> r
        .map((row, meta) -> row.get(0, Boolean.class).toString() + "-" + row.get(1, String.class))))
        .expectNext("false-abc")
        .verifyComplete();
  }

  @Test
  public void readMultiResultSetQueryTest() {
    PartialResultSet p1 = PartialResultSet.newBuilder().setMetadata(
        this.resultSetMetadata
    ).setChunkedValue(false)
        .addValues(this.a1).build();

    PartialResultSet p2 = PartialResultSet.newBuilder().setChunkedValue(false)
        .addValues(this.a2).build();

    Flux<PartialResultSet> inputs = Flux.just(p1, p2);

    when(this.mockClient.executeStreamingSql(
        same(this.mockContext), any(), any(), any())).thenReturn(inputs);

    StepVerifier.create(Flux.from(
        new SpannerStatement(this.mockClient, this.mockContext, "SELECT", TEST_CONFIG).execute())
          .flatMap(r -> Mono.from(r.getRowsUpdated())))
          .expectNext(0)
          .verifyComplete();
  }

  @Test
  public void readDmlQueryTest() {
    ResultSet resultSet = ResultSet.newBuilder()
        .setStats(ResultSetStats.newBuilder().setRowCountExact(555).build())
        .build();

    ExecuteBatchDmlResponse executeBatchDmlResponse = ExecuteBatchDmlResponse.newBuilder()
        .addResultSets(resultSet)
        .build();

    when(this.mockClient.executeBatchDml(any(), any(), any(), any()))
        .thenReturn(Mono.just(executeBatchDmlResponse));

    StepVerifier.create(Flux.from(
        new SpannerStatement(this.mockClient, this.mockContext, "Insert into books", TEST_CONFIG)
            .execute())
          .flatMap(r -> Mono.from(r.getRowsUpdated())))
          .expectNext(555)
          .verifyComplete();
  }

  @Test
  public void batchDmlQueryAutoCommitTransactionTest() {
    batchDmlQueryTest(null, 1, 1);
  }

  @Test
  public void batchDmlQueryWithinTransactionTest() {
    batchDmlQueryTest(ByteString.copyFromUtf8("abc"), 0, 0);
  }

  private void batchDmlQueryTest(ByteString transactionId,
      int beginTransactionCount, int commitTransactionCount) {
    ResultSet resultSet1 = ResultSet.newBuilder()
        .setStats(ResultSetStats.newBuilder().setRowCountExact(555).build())
        .build();

    ResultSet resultSet2 = ResultSet.newBuilder()
        .setStats(ResultSetStats.newBuilder().setRowCountExact(777).build())
        .build();

    ExecuteBatchDmlResponse executeBatchDmlResponse = ExecuteBatchDmlResponse.newBuilder()
        .addResultSets(resultSet1)
        .addResultSets(resultSet2)
        .build();

    when(this.mockClient.executeBatchDml(any(), any()))
        .thenReturn(Mono.just(executeBatchDmlResponse));

    when(this.mockContext.getTransactionId()).thenReturn(transactionId);

    StepVerifier.create(
        Flux.from(new SpannerBatch(
            this.mockClient, this.mockContext, this.mockConnection)
            .add("Insert into books")
            .add("Insert into authors")
            .execute())
            .flatMap(r -> Mono.from(r.getRowsUpdated())))
        .expectNext(555)
        .expectNext(777)
        .verifyComplete();

    verify(this.mockConnection, times(beginTransactionCount)).beginTransaction();
    verify(this.mockConnection, times(commitTransactionCount)).commitTransaction();
  }

  @Test
  public void batchDmlExceptionTest() {
    assertThatThrownBy(() ->
        new SpannerBatch(this.mockClient, null, null)
        .add("select * from books"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Only DML statements are supported in batches");

  }

  @Test
  public void runDdlQueryTest() {
    when(this.mockClient.executeDdl(any(), any(), any(), any()))
        .thenReturn(Mono.just(Operation.getDefaultInstance()));

    String sql = "drop TABLE BOOKS";
    SpannerStatement statement =
        new SpannerStatement(this.mockClient, this.mockContext, sql, TEST_CONFIG);

    StepVerifier.create(Flux.from(statement.execute()).flatMap(result -> result.getRowsUpdated()))
        .expectNext(0)
        .verifyComplete();

    verify(this.mockClient, times(1))
        .executeDdl(
            "projects/project/instances/test-instance/databases/db",
            Collections.singletonList(sql),
            Duration.ofSeconds(600),
            Duration.ofSeconds(5));
  }

  @Test
  public void noopMapOnUpdateQueriesWhenNoRowsAffected() {
    String sql = "delete from Books where true";

    ResultSet resultSet = ResultSet.newBuilder()
        .setMetadata(ResultSetMetadata.getDefaultInstance())
        .setStats(ResultSetStats.getDefaultInstance())
        .build();

    ExecuteBatchDmlResponse executeBatchDmlResponse = ExecuteBatchDmlResponse.newBuilder()
        .addResultSets(resultSet)
        .build();

    when(this.mockClient.executeBatchDml(this.mockContext, sql,
        Arrays.asList(Struct.newBuilder().build()), Collections.EMPTY_MAP))
        .thenReturn(Mono.just(executeBatchDmlResponse));

    SpannerStatement statement =
        new SpannerStatement(this.mockClient, this.mockContext, sql, TEST_CONFIG);

    Flux<SpannerResult> result = (Flux<SpannerResult>) statement.execute();

    StepVerifier.create(result.flatMap(
        spannerResult -> spannerResult.map((row, rowMetadata) -> (String) row.get(0))
            .collectList()))
        .expectNext(Collections.emptyList())
        .verifyComplete();

    StepVerifier.create(result.flatMap(results -> Mono.from(results.getRowsUpdated())))
        .expectNext(0)
        .verifyComplete();

    verify(this.mockClient, times(1)).executeBatchDml(
        this.mockContext, sql, Arrays.asList(Struct.newBuilder().build()), Collections.EMPTY_MAP);
  }
}
