package com.google.cloud.spring.data.datastore.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Transaction;
import com.google.datastore.v1.TransactionOptions;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.support.DefaultTransactionDefinition;

class DatastoreTransactionManagerTests {

  @Test
  void testBeginWithPreviousTransactionId() {
    Datastore datastore = mock(Datastore.class);
    Transaction transaction = mock(Transaction.class);
    ByteString prevId = ByteString.copyFromUtf8("test-id");

    when(datastore.newTransaction(any(TransactionOptions.class))).thenReturn(transaction);

    DatastoreTransactionManager manager = new DatastoreTransactionManager(() -> datastore);
    DatastoreTransactionManager.Tx tx = new DatastoreTransactionManager.Tx(datastore);

    tx.setPreviousTransactionId(prevId);

    manager.doBegin(tx, new DefaultTransactionDefinition());

    ArgumentCaptor<TransactionOptions> optionsCaptor = ArgumentCaptor.forClass(TransactionOptions.class);
    verify(datastore).newTransaction(optionsCaptor.capture());

    assertThat(optionsCaptor.getValue().getReadWrite().getPreviousTransaction()).isEqualTo(prevId);
  }
}