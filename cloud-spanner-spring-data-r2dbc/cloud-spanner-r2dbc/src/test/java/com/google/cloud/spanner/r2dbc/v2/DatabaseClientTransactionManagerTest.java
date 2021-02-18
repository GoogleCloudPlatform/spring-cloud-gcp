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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.AsyncTransactionManager;
import com.google.cloud.spanner.AsyncTransactionManager.TransactionContextFuture;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ReadOnlyTransaction;
import com.google.cloud.spanner.TimestampBound;
import com.google.cloud.spanner.r2dbc.TransactionInProgressException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatabaseClientTransactionManagerTest {

  DatabaseClientTransactionManager transactionManager;
  DatabaseClient mockDbClient;
  AsyncTransactionManager mockClientLibraryTransactionManager;
  TransactionContextFuture mockTransactionFuture;
  ReadOnlyTransaction mockReadOnlyTransaction;

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
    this.mockReadOnlyTransaction = mock(ReadOnlyTransaction.class);

    when(this.mockDbClient.transactionManagerAsync())
        .thenReturn(this.mockClientLibraryTransactionManager);
    when(this.mockClientLibraryTransactionManager.beginAsync())
        .thenReturn(this.mockTransactionFuture);
    when(this.mockDbClient.readOnlyTransaction(TimestampBound.strong()))
        .thenReturn(this.mockReadOnlyTransaction);

    this.transactionManager =
        new DatabaseClientTransactionManager(
            this.mockDbClient, Executors.newSingleThreadExecutor());

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
}
