/*
 * Copyright 2020-2020 Google LLC
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

package com.google.cloud.spanner.r2dbc.v2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFutures;
import com.google.cloud.spanner.AsyncTransactionManager;
import com.google.cloud.spanner.AsyncTransactionManager.AsyncTransactionFunction;
import com.google.cloud.spanner.AsyncTransactionManager.AsyncTransactionStep;
import com.google.cloud.spanner.AsyncTransactionManager.TransactionContextFuture;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ReadOnlyTransaction;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.TimestampBound;
import com.google.cloud.spanner.TransactionContext;
import com.google.cloud.spanner.r2dbc.TransactionInProgressException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatabaseClientTransactionManagerTest {

  DatabaseClientTransactionManager transactionManager;
  DatabaseClient mockDbClient;
  AsyncTransactionManager mockClientLibraryTransactionManager;
  TransactionContextFuture mockTransactionFuture;
  TransactionContext mockTransactionContext;
  ReadOnlyTransaction mockReadOnlyTransaction;
  AsyncTransactionStep mockAsyncTransactionStep;

  private static PrintStream systemErr;
  private static ByteArrayOutputStream redirectedOutput = new ByteArrayOutputStream();

  @BeforeAll
  public static void redirectOutput() {
    systemErr = System.out;
    System.setErr(new PrintStream(redirectedOutput));
  }

  @AfterAll
  public static void restoreOutput() {
    System.setErr(systemErr);
  }

  /** Sets up mocks. */
  @BeforeEach
  public void setUp() {
    this.mockDbClient = mock(DatabaseClient.class);
    this.mockClientLibraryTransactionManager = mock(AsyncTransactionManager.class);
    this.mockTransactionFuture = mock(TransactionContextFuture.class);
    this.mockTransactionContext = mock(TransactionContext.class);
    this.mockReadOnlyTransaction = mock(ReadOnlyTransaction.class);
    this.mockAsyncTransactionStep = mock(AsyncTransactionStep.class);

    when(this.mockDbClient.transactionManagerAsync())
        .thenReturn(this.mockClientLibraryTransactionManager);
    when(this.mockClientLibraryTransactionManager.beginAsync())
        .thenReturn(this.mockTransactionFuture);
    when(this.mockTransactionFuture.then(any(), any())).thenAnswer(invocation -> {
      ((AsyncTransactionFunction) invocation.getArgument(0))
          .apply(this.mockTransactionContext, null);
      return this.mockAsyncTransactionStep;
    });
    when(this.mockTransactionContext.executeUpdateAsync(any()))
        .thenReturn(ApiFutures.immediateFuture(42L));

    when(this.mockClientLibraryTransactionManager.closeAsync())
        .thenReturn(ApiFutures.immediateFuture(null));
    when(this.mockDbClient.readOnlyTransaction(TimestampBound.strong()))
        .thenReturn(this.mockReadOnlyTransaction);

    this.transactionManager = new DatabaseClientTransactionManager(this.mockDbClient);

  }

  @Test
  void testReadonlyTransactionStartedWhileReadWriteInProgressFails() {

    this.transactionManager.beginTransaction();
    TimestampBound strongBound = TimestampBound.strong();
    assertThatThrownBy(() ->
        this.transactionManager.beginReadonlyTransaction(strongBound)
    ).isInstanceOf(TransactionInProgressException.class)
        .hasMessage(TransactionInProgressException.MSG_READWRITE);
  }

  @Test
  void testReadWriteTransactionStartedWhileReadonlyInProgressFails() {

    this.transactionManager.beginReadonlyTransaction(TimestampBound.strong());
    assertThatThrownBy(() ->
        this.transactionManager.beginTransaction()
    ).isInstanceOf(TransactionInProgressException.class)
        .hasMessage(TransactionInProgressException.MSG_READONLY);
  }

  @Test
  void testReadonlyTransactionStartedWhileReadonlyInProgressFails() {
    TimestampBound strongBound = TimestampBound.strong();
    this.transactionManager.beginReadonlyTransaction(strongBound);
    assertThatThrownBy(() ->
        this.transactionManager.beginReadonlyTransaction(strongBound)
    ).isInstanceOf(TransactionInProgressException.class)
        .hasMessage(TransactionInProgressException.MSG_READONLY);
  }

  @Test
  void testReadWriteTransactionStartedWhileReadwriteInProgressFails() {

    this.transactionManager.beginTransaction();
    assertThatThrownBy(() ->
        this.transactionManager.beginTransaction()
    ).isInstanceOf(TransactionInProgressException.class)
        .hasMessage(TransactionInProgressException.MSG_READWRITE);
  }

  @Test
  void testCommitWithNoStatementsLogsWarning() {
    this.transactionManager.beginTransaction();
    this.transactionManager.commitTransaction();
    assertThat(redirectedOutput.toString())
        .contains("Read/Write transaction committing without any statements.");
  }

  @Test
  void commitTransactionWithNoBeginLogsWarning() throws Exception {
    this.transactionManager.commitTransaction().get();

    assertThat(redirectedOutput.toString())
        .contains("Commit called outside of an active transaction.");

    verifyNoInteractions(this.mockReadOnlyTransaction);
    verifyNoInteractions(this.mockClientLibraryTransactionManager);
  }

  @Test
  void commitReadonlyTransactionClosesDelegate() throws Exception {
    this.transactionManager.beginReadonlyTransaction(TimestampBound.strong());
    this.transactionManager.commitTransaction().get();

    verify(this.mockReadOnlyTransaction).close();
  }

  @Test
  void commitReadWriteTransactionCommitsDelegate() throws Exception {
    this.transactionManager.beginTransaction().get();
    this.transactionManager
        .runInTransaction(ctx -> ctx.executeUpdateAsync(Statement.of("SET something")));
    this.transactionManager.commitTransaction();

    verify(this.mockTransactionContext).executeUpdateAsync(any());
    verify(this.mockAsyncTransactionStep).commitAsync();
    verifyNoInteractions(this.mockReadOnlyTransaction);
  }

  @Test
  void clearTransactionManagerWithoutTransactionClearsState() throws Exception {

    this.transactionManager.clearTransactionManager().get();

    assertThat(this.transactionManager.isInReadonlyTransaction()).isFalse();
    assertThat(this.transactionManager.isInReadWriteTransaction()).isFalse();

    verifyNoInteractions(this.mockClientLibraryTransactionManager);
  }

  @Test
  void clearTransactionManagerInReadonlyTransactionClearsState() throws Exception {

    this.transactionManager.beginReadonlyTransaction(TimestampBound.strong());

    assertThat(this.transactionManager.isInReadonlyTransaction())
        .as("should be in readonly transaction when active").isTrue();
    assertThat(this.transactionManager.isInReadWriteTransaction())
        .as("should not be in read/write transaction at all (before close)").isFalse();

    this.transactionManager.clearTransactionManager().get();

    assertThat(this.transactionManager.isInReadonlyTransaction())
        .as("should no longer be in readonly transaction after close").isFalse();
    assertThat(this.transactionManager.isInReadWriteTransaction())
        .as("should not be in read/write transaction at all (after close)").isFalse();

    verifyNoInteractions(this.mockClientLibraryTransactionManager);
  }

  @Test
  void clearTransactionManagerInReadwriteTransactionClearsState() throws Exception {

    this.transactionManager.beginTransaction().get();

    assertThat(this.transactionManager.isInReadonlyTransaction())
        .as("should not be in readonly transaction at all (before close)").isFalse();
    assertThat(this.transactionManager.isInReadWriteTransaction())
        .as("should be in read/write transaction when active").isTrue();

    this.transactionManager.clearTransactionManager().get();

    assertThat(this.transactionManager.isInReadonlyTransaction())
        .as("should not be in readonly transaction at all (after close)").isFalse();
    assertThat(this.transactionManager.isInReadWriteTransaction())
        .as("should no longer be in read/write transaction after close").isFalse();

  }
}
