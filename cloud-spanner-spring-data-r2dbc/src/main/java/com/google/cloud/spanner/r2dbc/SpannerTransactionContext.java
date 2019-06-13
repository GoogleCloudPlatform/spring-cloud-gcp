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

import com.google.spanner.v1.Transaction;
import com.google.spanner.v1.TransactionOptions;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;

/**
 * A class to hold transaction-related data.
 */
public class SpannerTransactionContext {

  private final AtomicLong seqNum = new AtomicLong(0);

  private final Transaction transaction;

  private final TransactionOptions transactionOptions;

  private SpannerTransactionContext(
      Transaction transaction, TransactionOptions transactionOptions) {

    this.transaction = transaction;
    this.transactionOptions = transactionOptions;
  }

  public Transaction getTransaction() {
    return this.transaction;
  }

  public long nextSeqNum() {
    return this.seqNum.getAndIncrement();
  }

  public boolean isReadWrite() {
    return this.transactionOptions.hasReadWrite();
  }

  public boolean isPartitionedDml() {
    return this.transactionOptions.hasPartitionedDml();
  }

  /**
   * Creates the SpannerTransactionContext.
   * @param transaction spanner transaction
   * @return spanner transaction context
   */
  public static @Nullable SpannerTransactionContext from(
      Transaction transaction, TransactionOptions transactionOptions) {

    if (transaction == null) {
      return null;
    }
    return new SpannerTransactionContext(transaction, transactionOptions);
  }
}
