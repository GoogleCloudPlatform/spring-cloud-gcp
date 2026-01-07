/*
 * Copyright 2017-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.data.datastore.core;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.Transaction;
import com.google.datastore.v1.TransactionOptions;
import com.google.protobuf.ByteString;
import java.util.function.Supplier;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Cloud Datastore transaction manager.
 *
 * @since 1.1
 */
public class DatastoreTransactionManager extends AbstractPlatformTransactionManager {

  private final Supplier<Datastore> datastore;

  public DatastoreTransactionManager(final Supplier<Datastore> datastore) {
    this.datastore = datastore;
  }

  @Override
  @NonNull
  protected Object doGetTransaction() throws TransactionException {
    Tx tx = (Tx) TransactionSynchronizationManager.getResource(datastore.get());
    if (tx != null && tx.transaction != null && tx.transaction.isActive()) {
      return tx;
    }
    return new Tx(datastore.get());
  }

  @Override
  protected void doBegin(@Nullable Object transactionObject, @NonNull TransactionDefinition transactionDefinition)
      throws TransactionException {
    if (transactionDefinition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT
        && transactionDefinition.getIsolationLevel()
        != TransactionDefinition.ISOLATION_SERIALIZABLE) {
      throw new IllegalStateException(
          "DatastoreTransactionManager supports only isolation level "
              + "TransactionDefinition.ISOLATION_DEFAULT or ISOLATION_SERIALIZABLE");
    }
    if (transactionDefinition.getPropagationBehavior()
        != TransactionDefinition.PROPAGATION_REQUIRED) {
      throw new IllegalStateException(
          "DatastoreTransactionManager supports only propagation behavior "
              + "TransactionDefinition.PROPAGATION_REQUIRED");
    }

    // Cast and verify the transaction object to meet nullability requirements
    Tx tx = (Tx) transactionObject;
    if (tx == null) {
      throw new IllegalStateException("Transaction object must not be null.");
    }

    // Building TransactionOptions dynamically to support previous transaction ID
    TransactionOptions.Builder optionsBuilder = TransactionOptions.newBuilder();

    if (transactionDefinition.isReadOnly()) {
      optionsBuilder.setReadOnly(TransactionOptions.ReadOnly.getDefaultInstance());
    } else {
      TransactionOptions.ReadWrite.Builder readWriteBuilder = TransactionOptions.ReadWrite.newBuilder();

      // Support for previous transaction ID for idempotency or sequential transactions
      if (tx.getPreviousTransactionId() != null) {
        readWriteBuilder.setPreviousTransaction(tx.getPreviousTransactionId());
      }
      optionsBuilder.setReadWrite(readWriteBuilder.build());
    }

    try {
      tx.transaction = tx.datastore.newTransaction(optionsBuilder.build());
    } catch (DatastoreException ex) {
      throw new TransactionSystemException("Could not create Cloud Datastore transaction", ex);
    }

    TransactionSynchronizationManager.bindResource(tx.datastore, tx);
  }

  @Override
  protected void doCommit(@NonNull DefaultTransactionStatus defaultTransactionStatus)
      throws TransactionException {
    Tx tx = (Tx) defaultTransactionStatus.getTransaction();
    try {
      if (tx.transaction != null && tx.transaction.isActive()) {
        tx.transaction.commit();
      } else {
        this.logger.debug("Transaction was not committed because it is no longer active.");
      }
    } catch (DatastoreException ex) {
      throw new TransactionSystemException("Cloud Datastore transaction failed to commit.", ex);
    }
  }

  @Override
  protected void doRollback(@NonNull DefaultTransactionStatus defaultTransactionStatus)
      throws TransactionException {
    Tx tx = (Tx) defaultTransactionStatus.getTransaction();
    try {
      if (tx.transaction != null && tx.transaction.isActive()) {
        tx.transaction.rollback();
      } else {
        this.logger.debug("Transaction was not rolled back because it is no longer active.");
      }
    } catch (DatastoreException ex) {
      throw new TransactionSystemException("Cloud Datastore transaction failed to rollback.", ex);
    }
  }

  @Override
  protected boolean isExistingTransaction(@Nullable Object transaction) {
    return transaction != null && ((Tx) transaction).transaction != null;
  }

  @Override
  protected void doCleanupAfterCompletion(@Nullable Object transaction) {
    if (transaction instanceof Tx tx) {
      TransactionSynchronizationManager.unbindResource(tx.datastore);
    }
  }

  /** A class to contain the transaction context. */
  public static class Tx {
    private Transaction transaction;
    private final Datastore datastore;
    private ByteString previousTransactionId;

    public Tx(Datastore datastore) {
      this.datastore = datastore;
    }

    public Transaction getTransaction() {
      return this.transaction;
    }

    public void setTransaction(Transaction transaction) {
      this.transaction = transaction;
    }

    public Datastore getDatastore() {
      return datastore;
    }

    /**
     * Gets the previous transaction ID.
     * @return the previous transaction ID as ByteString.
     */
    public ByteString getPreviousTransactionId() {
      return previousTransactionId;
    }

    /**
     * Sets the previous transaction ID to be used when starting this transaction.
     * @param previousTransactionId the transaction ID to resume.
     */
    public void setPreviousTransactionId(ByteString previousTransactionId) {
      this.previousTransactionId = previousTransactionId;
    }
  }
}