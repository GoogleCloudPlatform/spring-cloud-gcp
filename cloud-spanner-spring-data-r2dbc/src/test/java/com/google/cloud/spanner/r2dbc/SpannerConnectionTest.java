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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.r2dbc.client.Client;
import com.google.protobuf.Value;
import com.google.spanner.v1.CommitResponse;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Transaction;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import io.r2dbc.spi.Statement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Test for {@link SpannerConnection}.
 */
public class SpannerConnectionTest {

  private static final Session TEST_SESSION =
      Session.newBuilder().setName("project/session/1234").build();

  private Client mockClient;

  /**
   * Initializes the mocks in the test.
   */
  @Before
  public void setupMocks() {
    this.mockClient = Mockito.mock(Client.class);
    when(this.mockClient.beginTransaction(any()))
        .thenReturn(Mono.just(Transaction.getDefaultInstance()));
    when(this.mockClient.commitTransaction(any(), any()))
        .thenReturn(Mono.just(CommitResponse.getDefaultInstance()));
    when(this.mockClient.rollbackTransaction(any(), any()))
        .thenReturn(Mono.empty());
  }

  @Test
  public void executeStatementReturnsWorkingStatementWithCorrectQuery() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION);
    String sql = "select book from library";
    PartialResultSet partialResultSet = PartialResultSet.newBuilder()
        .setMetadata(ResultSetMetadata.newBuilder().setRowType(StructType.newBuilder()
            .addFields(
                Field.newBuilder().setName("book")
                    .setType(Type.newBuilder().setCode(TypeCode.STRING)))))
        .addValues(Value.newBuilder().setStringValue("Odyssey"))
        .build();

    when(this.mockClient.executeStreamingSql(TEST_SESSION, Mono.empty(), sql))
        .thenReturn(Flux.just(partialResultSet));

    Statement statement = connection.createStatement(sql);
    assertThat(statement).isInstanceOf(SpannerStatement.class);
    Mono<SpannerResult> result = (Mono<SpannerResult>)statement.execute();
    result.block().map((r, m) -> (String)r.get(0)).blockFirst().equals("Odyssey");

    verify(this.mockClient).executeStreamingSql(TEST_SESSION, Mono.empty(), sql);
  }

  @Test
  public void beginAndCommitTransactions() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION);

    Mono.from(connection.commitTransaction()).block();
    verify(this.mockClient, never()).commitTransaction(any(), any());

    Mono.from(connection.beginTransaction()).block();
    Mono.from(connection.commitTransaction()).block();
    verify(this.mockClient, times(1))
        .beginTransaction(TEST_SESSION);
    verify(this.mockClient, times(1))
        .commitTransaction(TEST_SESSION, Transaction.getDefaultInstance());
  }

  @Test
  public void rollbackTransactions() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION);

    Mono.from(connection.rollbackTransaction()).block();
    verify(this.mockClient, never()).rollbackTransaction(any(), any());

    Mono.from(connection.beginTransaction()).block();
    Mono.from(connection.rollbackTransaction()).block();
    verify(this.mockClient, times(1))
        .beginTransaction(TEST_SESSION);
    verify(this.mockClient, times(1))
        .rollbackTransaction(TEST_SESSION, Transaction.getDefaultInstance());
  }
}
