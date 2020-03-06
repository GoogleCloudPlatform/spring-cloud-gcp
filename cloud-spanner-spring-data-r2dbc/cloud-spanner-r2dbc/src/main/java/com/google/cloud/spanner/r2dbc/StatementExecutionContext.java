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

import com.google.protobuf.ByteString;

/**
 * Contract for objects encapsulating Spanner state associated with a specific connection.
 * <ul>
 *   <li>Session is created per-connection and cannot be changed.
 *   <li>Transaction is initially {@code null}, and can change as connection begins and ends
 *   transactions.
 *   <li>monotonically increasing update sequence describes the order of DML statements within a
 *   transaction.
 * </ul>
 */
public interface StatementExecutionContext {

  /**
   * Retrieves the session name associated with the current connection.
   * @return valid name of a currently valid Cloud Spanner session.
   */
  String getSessionName();

  /**
   * Retrieves the next number in a monotonically increasing sequence.
   *
   * <p>Cloud Spanner uses the sequence number to prevent a DDL statement from being executed
   * multiple times.
   * @return a valid sequence number for DML updates.
   */
  long nextSeqNum();

  /**
   * Retrieves the current transaction ID, if present.
   * @return id of the current transaction or {@code null} if there is no active transaction.
   */
  ByteString getTransactionId();

  /**
   * Determines whether the current transaction, if present, is a Read/Write Cloud Spanner
   * transaction.
   * @return whether the current transaction is a Read/Write transaction ({@code false} if there is
   *     no active transaction).
   */
  boolean isTransactionReadWrite();

  /**
   * Determines whether the current transaction, if present, is a Partitioned DML Cloud Spanner
   * transaction.
   * @return whether the current transaction is a Partitioned DML transaction ({@code false} if
   *     there is no active transaction).
   */
  boolean isTransactionPartitionedDml();

}
