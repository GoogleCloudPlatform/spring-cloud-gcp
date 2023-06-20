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

package com.google.cloud.spanner.r2dbc;

import io.r2dbc.spi.R2dbcNonTransientException;

/**
 * Non-retryable exception indicating another transaction is already in progress in this connection.
 */
public class TransactionInProgressException extends R2dbcNonTransientException {

  public static final String MSG_READONLY =
      "Cannot begin a new transaction because a readonly transaction is already in progress.";
  public static final String MSG_READWRITE =
      "Cannot begin a new transaction because a read/write transaction is already in progress.";

  public TransactionInProgressException(boolean isReadwrite) {
    super(isReadwrite ? MSG_READWRITE : MSG_READONLY);
  }

}
