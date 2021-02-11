/*
 * Copyright 2019-2020 Google LLC
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
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.r2dbc.client.Client;
import com.google.protobuf.ByteString;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.spanner.v1.CommitResponse;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Transaction;
import com.google.spanner.v1.TransactionOptions;
import com.google.spanner.v1.TransactionOptions.PartitionedDml;
import com.google.spanner.v1.TransactionOptions.ReadOnly;
import com.google.spanner.v1.TransactionOptions.ReadWrite;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.ValidationDepth;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

/**
 * Test for {@link SpannerConnection}.
 */
class SpannerConnectionTest {

  static final String TEST_SESSION_NAME = "project/session/1234";
  static final Session TEST_SESSION =
      Session.newBuilder().setName(TEST_SESSION_NAME).build();
  static final Struct EMPTY_STRUCT = Struct.newBuilder().build();
  static final Map<String, Type> EMPTY_TYPE_MAP = Collections.emptyMap();

  private static final SpannerConnectionConfiguration TEST_CONFIG =
      new SpannerConnectionConfiguration.Builder()
          .setInstanceName("test-instance")
          .setProjectId("project")
          .setDatabaseName("db")
          .setCredentials(NoCredentials.getInstance())
          .build();

  private static final TransactionOptions READ_WRITE_TRANSACTION =
      TransactionOptions.newBuilder()
          .setReadWrite(ReadWrite.getDefaultInstance())
          .build();

  private static final TransactionOptions PARTITIONED_DML_TRANSACTION =
      TransactionOptions.newBuilder()
          .setPartitionedDml(PartitionedDml.getDefaultInstance())
          .build();

  private static final TransactionOptions READ_ONLY_TRANSACTION =
      TransactionOptions.newBuilder()
          .setReadOnly(ReadOnly.getDefaultInstance())
          .build();

  private Client mockClient;

  /**
   * Initializes the mocks in the test.
   */
  @BeforeEach
  public void setupMocks() {
    this.mockClient = mock(Client.class);

    when(this.mockClient.beginTransaction(any(), any()))
        .thenReturn(Mono.just(Transaction.getDefaultInstance()));
    when(this.mockClient.commitTransaction(any(), any()))
        .thenReturn(Mono.just(CommitResponse.getDefaultInstance()));
    when(this.mockClient.rollbackTransaction(any(), any()))
        .thenReturn(Mono.empty());
  }

  @Test
  void executeStatementReturnsWorkingStatementWithCorrectQuery() {
    SpannerConnection connection
        = new SpannerConnection(this.mockClient, TEST_SESSION, TEST_CONFIG);
    String sql = "select book from library";
    PartialResultSet partialResultSet = PartialResultSet.newBuilder()
        .setMetadata(ResultSetMetadata.newBuilder().setRowType(StructType.newBuilder()
            .addFields(
                Field.newBuilder().setName("book")
                    .setType(Type.newBuilder().setCode(TypeCode.STRING)))))
        .addValues(Value.newBuilder().setStringValue("Odyssey"))
        .build();

    when(this.mockClient.executeStreamingSql(
          any(StatementExecutionContext.class), eq(sql), eq(EMPTY_STRUCT), eq(EMPTY_TYPE_MAP)))
        .thenReturn(Flux.just(makeBookPrs("Odyssey")));

    Statement statement = connection.createStatement(sql);
    assertThat(statement).isInstanceOf(SpannerStatement.class);

    StepVerifier.create(
        ((Flux<SpannerResult>) statement.execute())
            .flatMap(res -> res.map((r, m) -> (String) r.get(0))))
        .expectNext("Odyssey")
        .expectComplete()
        .verify();

    verify(this.mockClient).executeStreamingSql(any(StatementExecutionContext.class), eq(sql),
        eq(EMPTY_STRUCT), eq(EMPTY_TYPE_MAP));

    // Single use READ query doesn't need these round trips below.
    verify(this.mockClient, times(0)).beginTransaction(eq(TEST_SESSION_NAME), any());
    verify(this.mockClient, times(0)).commitTransaction(eq(TEST_SESSION_NAME), any());
  }

  @Test
  void executeDmlInTransactionStartingAfterCreationTest() {
    SpannerConnection connection
        = new SpannerConnection(this.mockClient, TEST_SESSION, TEST_CONFIG);
    String sql = "insert into books values (title) @title";

    when(this.mockClient.executeBatchDml(any(), any())).thenReturn(Flux.empty());

    StepVerifier.create(
        Mono.fromSupplier(() -> connection.createStatement(sql))
            .delayUntil(s -> connection.beginTransaction())
            .doOnSuccess(SpannerStatement::execute))
        .consumeNextWith(x -> {})
        .verifyComplete();
    verify(this.mockClient, times(1)).beginTransaction(eq(TEST_SESSION_NAME), any());
  }

  @Test
  void noopCommitTransactionWhenTransactionNotStarted() {
    SpannerConnection connection =
        new SpannerConnection(this.mockClient, TEST_SESSION, TEST_CONFIG);

    // No-op commit when connection is not started.
    Mono.from(connection.commitTransaction()).block();
    verify(this.mockClient, never()).commitTransaction(any(), any());
  }

  @Test
  void beginAndCommitTransactions() {
    SpannerConnection connection =
        new SpannerConnection(this.mockClient, TEST_SESSION, TEST_CONFIG);

    PublisherProbe<Transaction> beginTransactionProbe = PublisherProbe.of(
        Mono.just(Transaction.getDefaultInstance()));
    PublisherProbe<CommitResponse> commitTransactionProbe = PublisherProbe.of(
        Mono.just(CommitResponse.getDefaultInstance()));

    when(this.mockClient.beginTransaction(TEST_SESSION_NAME, READ_WRITE_TRANSACTION))
        .thenReturn(beginTransactionProbe.mono());
    when(this.mockClient.commitTransaction(TEST_SESSION_NAME, Transaction.getDefaultInstance()))
        .thenReturn(commitTransactionProbe.mono());

    Mono.from(connection.beginTransaction())
        .then(Mono.from(connection.commitTransaction()))
        .subscribe();
    verify(this.mockClient, times(1))
        .beginTransaction(TEST_SESSION_NAME, READ_WRITE_TRANSACTION);
    verify(this.mockClient, times(1))
        .commitTransaction(TEST_SESSION_NAME, Transaction.getDefaultInstance());

    beginTransactionProbe.assertWasSubscribed();
    commitTransactionProbe.assertWasSubscribed();
  }

  @Test
  void rollbackTransactions() {
    SpannerConnection connection =
        new SpannerConnection(this.mockClient, TEST_SESSION, TEST_CONFIG);

    PublisherProbe<Transaction> beginTransactionProbe = PublisherProbe.of(
        Mono.just(Transaction.getDefaultInstance()));
    PublisherProbe<Void> rollbackProbe = PublisherProbe.empty();

    when(this.mockClient.beginTransaction(TEST_SESSION_NAME, READ_WRITE_TRANSACTION))
        .thenReturn(beginTransactionProbe.mono());
    when(this.mockClient.rollbackTransaction(TEST_SESSION_NAME, Transaction.getDefaultInstance()))
        .thenReturn(rollbackProbe.mono());

    Mono.from(connection.rollbackTransaction()).block();
    verify(this.mockClient, never()).rollbackTransaction(any(), any());

    Mono.from(connection.beginTransaction()).block();
    Mono.from(connection.rollbackTransaction()).block();
    verify(this.mockClient, times(1))
        .beginTransaction(TEST_SESSION_NAME, READ_WRITE_TRANSACTION);
    verify(this.mockClient, times(1))
        .rollbackTransaction(TEST_SESSION_NAME, Transaction.getDefaultInstance());

    beginTransactionProbe.assertWasSubscribed();
    rollbackProbe.assertWasSubscribed();
  }

  @Test
  void testCustomTransactionType() {
    SpannerConnection connection = new SpannerConnection(
        this.mockClient, TEST_SESSION, TEST_CONFIG);

    StepVerifier
        .create(connection.beginTransaction(PARTITIONED_DML_TRANSACTION))
        .verifyComplete();
    verify(this.mockClient, times(1))
        .beginTransaction(TEST_SESSION_NAME, PARTITIONED_DML_TRANSACTION);

    // Partitioned DML transactions should not be committed.
    StepVerifier
        .create(connection.commitTransaction())
        .verifyComplete();
    verify(this.mockClient, times(0))
        .commitTransaction(any(), any());
  }

  @Test
  void executionContextHasCorrectSessionName() {
    SpannerConnection connection = new SpannerConnection(
        this.mockClient, Session.newBuilder().setName("session-name").build(), null);
    assertThat(connection.getSessionName()).isEqualTo("session-name");
  }

  @Test
  void executionContextDoesNotHaveTransactionWhenInitialized() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    assertThat(connection.getTransactionId()).isNull();
  }

  @Test
  void executionContextHasCorrectTransactionIdWhenTransactionSet() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    ByteString transactionId = ByteString.copyFrom("transaction-id".getBytes());

    when(this.mockClient.beginTransaction(eq(TEST_SESSION_NAME), any()))
        .thenReturn(Mono.just(Transaction.newBuilder().setId(transactionId).build()));
    when(this.mockClient.rollbackTransaction(eq(TEST_SESSION_NAME), any()))
        .thenReturn(Mono.empty());

    StepVerifier.create(connection.beginTransaction()).verifyComplete();
    assertThat(connection.getTransactionId()).isEqualTo(transactionId);

    StepVerifier.create(connection.rollbackTransaction()).verifyComplete();
    assertThat(connection.getTransactionId()).isNull();
  }

  @Test
  void nextSeqNumIsSequential() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    long prevNum = connection.nextSeqNum();

    for (int i = 0; i < 9; i++) {
      long num = connection.nextSeqNum();

      if (num <= prevNum) {
        fail("Expected to be monotonically increasing; received " + prevNum + ", then " + num);
      }
      prevNum = num;
    }
  }

  @Test
  void autocommitOnByDefault() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    assertThat(connection.isAutoCommit()).isTrue();
  }

  @Test
  void turningAutocommitOnIsNoopWhenAlreadyOn() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    StepVerifier.create(connection.setAutoCommit(true))
        .verifyComplete();
    assertThat(connection.isAutoCommit()).isTrue();
    verifyZeroInteractions(this.mockClient);
  }

  @Test
  void turningAutocommitOffIsNoopWhenAlreadyOff() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    StepVerifier.create(
        Mono.from(connection.setAutoCommit(false))
            .then(Mono.from(connection.setAutoCommit(false)))
    ).verifyComplete();

    assertThat(connection.isAutoCommit()).isFalse();
    verifyZeroInteractions(this.mockClient);
  }

  @Test
  void turningAutocommitOffWorksLocally() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    StepVerifier.create(connection.setAutoCommit(false))
        .verifyComplete();
    assertThat(connection.isAutoCommit()).isFalse();
    verifyZeroInteractions(this.mockClient);
  }

  @Test
  void startingTransactionTurnsOffAutocommit() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    StepVerifier.create(
        Mono.from(connection.beginTransaction())
    ).verifyComplete();

    verify(this.mockClient).beginTransaction(eq(TEST_SESSION_NAME), any());
    assertThat(connection.isAutoCommit()).isFalse();
  }

  @Test
  void turningAutocommitOnCommitsExistingTransaction() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    StepVerifier.create(
        Mono.from(connection.setAutoCommit(false))
            .then(connection.beginTransaction())
            .then(Mono.from(connection.setAutoCommit(true)))
    ).verifyComplete();

    verify(this.mockClient).beginTransaction(eq(TEST_SESSION_NAME), any());
    verify(this.mockClient).commitTransaction(eq(TEST_SESSION_NAME), any());
    assertThat(connection.isAutoCommit()).isTrue();
  }

  @Test
  void turningAutocommitOnDoesNotAffectNonReadwriteTransaction() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    TransactionOptions readonlyTransaction =
        TransactionOptions.newBuilder().setReadOnly(ReadOnly.getDefaultInstance()).build();

    StepVerifier.create(
            Mono.from(connection.setAutoCommit(false))
                .then(connection.beginTransaction(readonlyTransaction))
                .then(Mono.from(connection.setAutoCommit(true))))
        .verifyComplete();

    verify(this.mockClient).beginTransaction(TEST_SESSION_NAME, readonlyTransaction);
    verify(this.mockClient, times(0)).commitTransaction(eq(TEST_SESSION_NAME), any());
    assertThat(connection.isAutoCommit()).isTrue();
  }

  @Test
  void localValidatePassesOnNewConnection() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    StepVerifier.create(connection.validate(ValidationDepth.LOCAL))
        .expectNext(true)
        .verifyComplete();
    verifyZeroInteractions(this.mockClient);
  }

  @Test
  void localValidateFailsOnClosedConnection() {
    when(this.mockClient.commitTransaction(any(), any()))
        .thenReturn(Mono.just(CommitResponse.getDefaultInstance()));
    when(this.mockClient.deleteSession(any())).thenReturn(Mono.empty());

    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);

    StepVerifier.create(
        connection.close()
            .then(Mono.from(connection.validate(ValidationDepth.LOCAL))))
        .expectNext(false)
        .verifyComplete();
    verify(this.mockClient, times(0)).healthcheck(any());
  }

  @Test
  void remoteValidateCallsServerHealthcheck() {
    when(this.mockClient.healthcheck(any())).thenReturn(Mono.just(true));

    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    StepVerifier.create(connection.validate(ValidationDepth.REMOTE))
        .expectNext(true)
        .verifyComplete();
    verify(this.mockClient).healthcheck(connection);
  }

  @Test
  void getConnectionMetadata() {
    SpannerConnection connection = new SpannerConnection(this.mockClient, TEST_SESSION, null);
    assertThat(connection.getMetadata().getDatabaseProductName()).isEqualTo("Cloud Spanner");
    assertThat(connection.getMetadata().getDatabaseVersion()).isEqualTo("n/a");
  }

  private PartialResultSet makeBookPrs(String bookName) {
    return PartialResultSet.newBuilder()
        .setMetadata(ResultSetMetadata.newBuilder().setRowType(StructType.newBuilder()
            .addFields(
                Field.newBuilder().setName("book")
                    .setType(Type.newBuilder().setCode(TypeCode.STRING)))))
        .addValues(Value.newBuilder().setStringValue(bookName))
        .build();
  }
}
