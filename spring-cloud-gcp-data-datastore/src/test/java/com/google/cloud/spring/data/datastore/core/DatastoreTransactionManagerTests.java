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

package com.google.cloud.spring.data.datastore.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.spring.data.datastore.core.DatastoreTransactionManager.Tx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

/** Tests for the Datastore transactional annotation manager. */
class DatastoreTransactionManagerTests {

  @Mock Datastore datastore;

  @Mock Transaction transaction;

  private Tx tx;

  private DatastoreTransactionManager manager;

  private DefaultTransactionStatus status = mock(DefaultTransactionStatus.class);

  @BeforeEach
  void initMocks() {
    MockitoAnnotations.initMocks(this);

    this.manager = new DatastoreTransactionManager(() -> datastore);
    this.tx = (Tx) manager.doGetTransaction();

    when(this.datastore.newTransaction()).thenReturn(this.transaction);
    when(this.status.getTransaction()).thenReturn(this.tx);
  }

  @Test
  void testDoGetTransactionActive() {
    this.manager.doBegin(this.tx, TransactionDefinition.withDefaults());
    when(this.transaction.isActive()).thenReturn(true);
    this.tx.setTransaction(this.transaction);
    assertThat(this.manager.doGetTransaction()).isSameAs(this.tx);
  }

  @Test
  void testDoGetTransactionNotActive() {
    this.manager.doBegin(this.tx, TransactionDefinition.withDefaults());
    when(this.transaction.isActive()).thenReturn(false);
    this.tx.setTransaction(this.transaction);
    assertThat(this.manager.doGetTransaction()).isNotSameAs(this.tx);
  }

  @Test
  void testDoGetTransactionNoTransaction() {
    assertThat(this.manager.doGetTransaction()).isNotSameAs(this.tx);
  }

  @Test
  void testDoBegin() {
    TransactionDefinition definition = new DefaultTransactionDefinition();
    this.manager.doBegin(this.tx, definition);
    verify(this.datastore, times(1)).newTransaction();
  }

  @Test
  void testDoCommit() {
    when(this.transaction.isActive()).thenReturn(true);
    this.tx.setTransaction(this.transaction);
    this.manager.doCommit(this.status);
    verify(this.transaction, times(1)).commit();
  }

  @Test
  void testDoCommitFailure() {
    DatastoreException exception = new DatastoreException(0, "", "");
    when(this.transaction.isActive()).thenReturn(true);
    when(this.transaction.commit()).thenThrow(exception);
    this.tx.setTransaction(this.transaction);

    assertThatThrownBy(() -> this.manager.doCommit(this.status))
            .isInstanceOf(TransactionSystemException.class)
            .hasMessage("Cloud Datastore transaction failed to commit.")
            .hasCause(exception);
  }

  @Test
  void testDoCommitNotActive() {
    when(this.transaction.isActive()).thenReturn(false);
    this.tx.setTransaction(this.transaction);
    this.manager.doCommit(this.status);
    verify(this.transaction, never()).commit();
  }

  @Test
  void testDoRollback() {
    when(this.transaction.isActive()).thenReturn(true);
    this.tx.setTransaction(this.transaction);
    this.manager.doRollback(this.status);
    verify(this.transaction, times(1)).rollback();
  }

  @Test
  void testDoRollbackFailure() {
    DatastoreException exception = new DatastoreException(0, "", "");
    when(this.transaction.isActive()).thenReturn(true);
    doThrow(exception).when(this.transaction).rollback();
    this.tx.setTransaction(this.transaction);

    assertThatThrownBy(() -> this.manager.doRollback(this.status))
            .isInstanceOf(TransactionSystemException.class)
            .hasMessage("Cloud Datastore transaction failed to rollback.")
            .hasCause(exception);
  }

  @Test
  void testDoRollbackNotActive() {
    when(this.transaction.isActive()).thenReturn(false);
    this.tx.setTransaction(this.transaction);
    this.manager.doRollback(this.status);
    verify(this.transaction, never()).rollback();
  }
}
