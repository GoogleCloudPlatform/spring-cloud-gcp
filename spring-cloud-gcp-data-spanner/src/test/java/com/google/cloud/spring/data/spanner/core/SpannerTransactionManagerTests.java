/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.data.spanner.core;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.AbortedException;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ErrorCode;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.SpannerExceptionFactory;
import com.google.cloud.spanner.TransactionContext;
import com.google.cloud.spanner.TransactionManager;
import com.google.cloud.spanner.TransactionManager.TransactionState;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Tests for the Spanner transaction manager. */
class SpannerTransactionManagerTests {

  @Mock DatabaseClient databaseClient;

  @Mock TransactionContext transactionContext;

  @Mock TransactionManager transactionManager;

  @Mock DefaultTransactionStatus status;

  SpannerTransactionManager.Tx tx;

  SpannerTransactionManager manager;

  @BeforeEach
  void initMocks() {
    MockitoAnnotations.initMocks(this);

    this.manager = new SpannerTransactionManager(() -> this.databaseClient);

    this.tx = new SpannerTransactionManager.Tx(databaseClient);

    when(status.getTransaction()).thenReturn(tx);

    when(databaseClient.transactionManager()).thenReturn(this.transactionManager);

    TransactionSynchronizationManager.bindResource(this.databaseClient, tx);
  }

  @Test
  void testDoGetTransactionStarted() {
    when(transactionManager.getState()).thenReturn(TransactionState.STARTED);
    tx.transactionManager = transactionManager;
    tx.transactionContext = transactionContext;

    Assert.assertEquals(manager.doGetTransaction(), tx);

    verify(this.databaseClient, never()).transactionManager();
  }

  @Test
  void testDoGetTransactionStartedReadOnly() {
    tx.transactionManager = transactionManager;
    tx.transactionContext = transactionContext;
    tx.isReadOnly = true;

    Assert.assertEquals(manager.doGetTransaction(), tx);

    verify(this.databaseClient, never()).transactionManager();
  }

  @Test
  void testDoGetTransactionAborted() {
    TransactionManager transactionManagerAborted = mock(TransactionManager.class);
    when(transactionManagerAborted.getState()).thenReturn(TransactionState.ABORTED);

    tx.transactionManager = transactionManager;

    TransactionManager transactionManagerNew = mock(TransactionManager.class);
    when(transactionManagerNew.getState()).thenReturn(TransactionState.STARTED);

    when(this.databaseClient.transactionManager()).thenReturn(transactionManagerNew);

    Assert.assertNotEquals(
        "expected a new transaction but got the same one", tx, manager.doGetTransaction());
  }

  @Test
  void testDoBegin() {
    when(transactionManager.begin()).thenReturn(transactionContext);

    TransactionSynchronizationManager.unbindResource(this.databaseClient);

    TransactionDefinition definition = new DefaultTransactionDefinition();

    manager.doBegin(tx, definition);

    Assert.assertEquals(tx.getTransactionManager(), transactionManager);
    Assert.assertEquals(tx.getTransactionContext(), transactionContext);
    Assert.assertFalse(tx.isReadOnly());

    verify(transactionManager, times(1)).begin();
  }

  @Test
  void testDoBeginReadOnly() {
    when(transactionManager.begin()).thenReturn(transactionContext);

    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
    definition.setReadOnly(true);

    TransactionSynchronizationManager.unbindResource(this.databaseClient);

    manager.doBegin(tx, definition);

    Assert.assertNull(tx.getTransactionManager());
    Assert.assertNotNull(tx.getTransactionContext());
    Assert.assertNotEquals(tx.getTransactionContext(), transactionContext);
    Assert.assertTrue(tx.isReadOnly());

    verify(transactionManager, times(0)).begin();
    verify(transactionManager, times(0)).getState();
  }

  @Test
  void testDoCommit() {
    when(transactionManager.getState()).thenReturn(TransactionState.STARTED);
    when(transactionManager.begin()).thenReturn(this.transactionContext);
    doNothing().when(transactionManager).commit();

    tx.transactionManager = transactionManager;
    tx.transactionContext = transactionContext;

    manager.doCommit(status);

    verify(transactionManager, times(1)).commit();
  }

  @Test
  void testDoCommitNotStarted() {
    tx.transactionManager = transactionManager;
    tx.transactionContext = transactionContext;

    manager.doCommit(status);

    verify(transactionManager, never()).commit();
    verify(this.transactionContext, never()).close();
  }

  @Test
  void testDoCommitRollbackExceptions() {

    when(transactionManager.getState()).thenReturn(TransactionState.STARTED);
    Mockito.doThrow(AbortedException.class).when(transactionManager).commit();

    tx.transactionManager = transactionManager;

    assertThatThrownBy(() -> manager.doCommit(status))
        .isInstanceOf(UnexpectedRollbackException.class)
        .hasMessage("Transaction Got Rolled Back")
        .hasCauseExactlyInstanceOf(AbortedException.class);
  }

  @Test
  void testDoCommitDupeException() {

    SpannerException exception =
        SpannerExceptionFactory.newSpannerException(
            ErrorCode.ALREADY_EXISTS, "this is from a test");

    when(transactionManager.getState()).thenReturn(TransactionState.STARTED);
    Mockito.doThrow(exception).when(transactionManager).commit();

    tx.transactionManager = transactionManager;

    assertThatThrownBy(() -> manager.doCommit(status))
        .isInstanceOf(DuplicateKeyException.class)
        .hasMessage("ALREADY_EXISTS")
        .hasStackTraceContaining("ALREADY_EXISTS: this is from a test");
  }

  @Test
  void testDoRollback() {
    when(transactionManager.getState()).thenReturn(TransactionState.STARTED);
    when(transactionManager.begin()).thenReturn(this.transactionContext);
    doNothing().when(transactionManager).rollback();

    tx.transactionContext = transactionContext;
    tx.transactionManager = transactionManager;

    manager.doRollback(status);

    verify(transactionManager, times(1)).rollback();
  }

  @Test
  void testDoRollbackNotStarted() {
    tx.transactionManager = transactionManager;

    manager.doRollback(status);

    verify(transactionManager, never()).rollback();
  }
}
