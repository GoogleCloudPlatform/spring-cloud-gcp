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

package com.google.cloud.spring.data.datastore.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.spring.data.datastore.core.convert.DatastoreEntityConverter;
import com.google.cloud.spring.data.datastore.core.convert.ObjectToKeyFactory;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.datastore.v1.TransactionOptions;
import com.google.datastore.v1.TransactionOptions.ReadWrite;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class tests that {@link DatastoreTemplate} is using the
 * transction-specific read-write when
 * inside transactions.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@DisabledInAotMode
class DatastoreTransactionTemplateTests {

  private static final ByteString TRANSACTION_ID = ByteString.copyFromUtf8("transaction-id");

  private final Key key = Key.newBuilder("a", "b", "c").build();

  @MockitoBean
  Datastore datastore;

  @MockitoBean
  Transaction transaction;

  @Autowired
  TransactionalService transactionalService;

  @MockitoBean
  ObjectToKeyFactory objectToKeyFactory;

  @BeforeEach
  void setUp() {
    when(this.datastore.newTransaction()).thenReturn(this.transaction);
    when(this.datastore.newTransaction(any())).thenReturn(this.transaction);
    when(this.transaction.isActive()).thenReturn(true);
    when(this.transaction.getTransactionId()).thenReturn(TRANSACTION_ID);

    // This test class does not verify the integrity of key/object/entity
    // relationships.
    // DatastoreTemplateTests verifies the integrity of those relationships.
    when(this.objectToKeyFactory.getKeyFromId(any(), any())).thenReturn(this.key);
    when(this.objectToKeyFactory.getKeyFromObject(any(), any())).thenReturn(this.key);
    when(this.objectToKeyFactory.allocateKeyForObject(any(), any())).thenReturn(this.key);

    doAnswer(
        invocation -> {
          List<Object> result = new ArrayList<>();
          result.add(null);
          return result;
        })
        .when(this.transaction)
        .fetch((Key[]) any());

    doAnswer(
        invocation -> {
          List<Object> result = new ArrayList<>();
          result.add(null);
          return result;
        })
        .when(this.datastore)
        .fetch((Key[]) any());
  }

  @Test
  void newTransaction() {
    this.transactionalService.doInTransaction(new TestEntity(), new TestEntity());
    verify(this.datastore, times(1)).newTransaction();
    verify(this.transaction, times(1)).commit();
    verify(this.transaction, times(0)).rollback();
    verify(this.transaction, times(2)).put((FullEntity<?>[]) any());
    verify(this.transaction, times(1)).add((FullEntity<?>[]) any());
    verify(this.transaction, times(1)).fetch((Key[]) any());
    verify(this.transaction, times(1)).delete(any());
  }

  @Test
  void rollBackTransaction() {
    Exception exception = null;
    try {
      this.transactionalService.doInTransactionWithException(new TestEntity(), new TestEntity());
    } catch (Exception ex) {
      exception = ex;
    }
    assertThat(exception).isNotNull();
    verify(this.transaction, times(0)).commit();
    verify(this.transaction, times(1)).rollback();
    verify(this.datastore, times(1)).newTransaction();
  }

  @Test
  void retryTransaction() {
    when(this.transaction.commit())
        .thenThrow(new DatastoreException(10, "Txn aborted", "ABORTED"))
        .thenReturn(null);

    this.transactionalService.doInRetryingTransaction(new TestEntity(), new TestEntity());

    verify(this.datastore, times(1)).newTransaction();
    verify(this.datastore, times(1)).newTransaction(
        TransactionOptions.newBuilder().setReadWrite(ReadWrite.newBuilder().setPreviousTransaction(TRANSACTION_ID))
            .build());
    verify(this.transaction, times(2)).add((FullEntity<?>[]) any());
    verify(this.transaction, times(2)).commit();
  }

  @Test
  void retryTransactionCommitHardFailure() {
    when(this.transaction.commit())
        .thenThrow(new DatastoreException(10, "Txn aborted", "ABORTED"));

    Exception exception = null;
    try {
      this.transactionalService.doInRetryingTransaction(new TestEntity(), new TestEntity());
    } catch (Exception ex) {
      exception = ex;
    }
    assertThat(exception).isNotNull();
    verify(this.datastore, times(1)).newTransaction();
    verify(this.datastore, times(2)).newTransaction(
        TransactionOptions.newBuilder().setReadWrite(ReadWrite.newBuilder().setPreviousTransaction(TRANSACTION_ID))
            .build());
    verify(this.transaction, times(3)).add((FullEntity<?>[]) any());
    verify(this.transaction, times(3)).commit();
    verify(this.transaction, times(0)).rollback();
  }

  @Test
  void retryTransactionRuntimeException() {
    when(this.transaction.commit())
        .thenThrow(new DatastoreException(10, "Txn aborted", "ABORTED"));

    Exception exception = null;
    try {
      this.transactionalService.doInRetryingTransactionWithoutCommit(new TestEntity(), new TestEntity());
    } catch (Exception ex) {
      exception = ex;
    }
    assertThat(exception).isNotNull();
    // Spring only rolls back prior to commit, so there isn't a benefit to using the
    // previous transaction id.
    verify(this.datastore, times(3)).newTransaction();
    verify(this.transaction, times(3)).add((FullEntity<?>[]) any());
    verify(this.transaction, times(0)).commit();
    verify(this.transaction, times(3)).rollback();
  }

  @Test
  void doWithoutTransactionTest() {
    this.transactionalService.doWithoutTransaction(new TestEntity(), new TestEntity());
    verify(this.transaction, never()).commit();
    verify(this.transaction, never()).rollback();
    verify(this.transaction, never()).put((FullEntity<?>) any());
    verify(this.transaction, never()).add((FullEntity<?>) any());
    verify(this.transaction, never()).fetch((Key[]) any());
    verify(this.transaction, never()).delete(any());
    verify(this.datastore, never()).newTransaction();
  }

  @Test
  void unsupportedIsolationTest() {

    assertThatThrownBy(() -> this.transactionalService.doNothingUnsupportedIsolation())
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("DatastoreTransactionManager supports only "
            + "isolation level TransactionDefinition.ISOLATION_DEFAULT or ISOLATION_SERIALIZABLE");
  }

  @Test
  void unsupportedPropagationTest() {

    assertThatThrownBy(() -> this.transactionalService.doNothingUnsupportedPropagation())
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("DatastoreTransactionManager supports only "
            + "propagation behavior TransactionDefinition.PROPAGATION_REQUIRED");
  }

  /** Spring config for the tests. */
  @Configuration
  @EnableRetry
  @EnableTransactionManagement
  static class Config {

    @Bean
    DatastoreTemplate datastoreTemplateTemplate(
        Datastore datastore, ObjectToKeyFactory objectToKeyFactory) {
      return new DatastoreTemplate(
          () -> datastore,
          mock(DatastoreEntityConverter.class),
          new DatastoreMappingContext(),
          objectToKeyFactory);
    }

    @Bean
    Datastore datastore() {
      return mock(Datastore.class);
    }

    @Bean
    ObjectToKeyFactory objectToKeyFactory() {
      return mock(ObjectToKeyFactory.class);
    }

    @Bean
    TransactionalService transactionalService() {
      return new TransactionalService();
    }

    @Bean
    DatastoreTransactionManager datastoreTransactionManager(Datastore datastore) {
      return new DatastoreTransactionManager(() -> datastore);
    }
  }

  /** A service object used in the test that performs the transactions. */
  public static class TransactionalService {
    @Autowired
    DatastoreTemplate datastoreTemplate;

    @Transactional
    public void doInTransaction(TestEntity entity1, TestEntity entity2) {
      this.datastoreTemplate.findById("abc", TestEntity.class);
      this.datastoreTemplate.insert(entity1);
      this.datastoreTemplate.save(entity2);
      this.datastoreTemplate.delete(entity1);
      this.datastoreTemplate.save(entity2);
    }

    @Transactional
    public void doInTransactionWithException(TestEntity entity1, TestEntity entity2) {
      this.datastoreTemplate.findById("abc", TestEntity.class);
      this.datastoreTemplate.insert(entity1);
      this.datastoreTemplate.save(entity2);
      this.datastoreTemplate.delete(entity1);
      this.datastoreTemplate.save(entity2);
      throw new RuntimeException("oops");
    }

    @Retryable(maxAttempts = 3)
    @Transactional
    public void doInRetryingTransaction(TestEntity entity1, TestEntity entity2) {
      this.datastoreTemplate.insert(entity1);
    }

    @Retryable(maxAttempts = 3)
    @Transactional
    public void doInRetryingTransactionWithoutCommit(TestEntity entity1, TestEntity entity2) {
      this.datastoreTemplate.insert(entity1);
      throw new RuntimeException("oops");
    }

    public void doWithoutTransaction(TestEntity entity1, TestEntity entity2) {
      this.datastoreTemplate.findById("abc", TestEntity.class);
      this.datastoreTemplate.insert(entity1);
      this.datastoreTemplate.save(entity2);
      this.datastoreTemplate.delete(entity1);
      this.datastoreTemplate.save(entity2);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void doNothingUnsupportedIsolation() {
      // This method does nothing, but should fail anyway because of the unsupported
      // isolation.
    }

    @Transactional(propagation = Propagation.NESTED)
    public void doNothingUnsupportedPropagation() {
      // This method does nothing, but should fail anyway because of the unsupported
      // propagation.
    }
  }

  @Entity(name = "custom_test_kind")
  private static class TestEntity {
    @Id
    String id;
  }
}
