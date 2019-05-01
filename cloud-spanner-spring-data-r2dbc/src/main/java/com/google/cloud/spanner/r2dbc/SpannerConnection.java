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

import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Statement;
import org.reactivestreams.Publisher;

/**
 * {@link Connection} implementation for Cloud Spanner.
 */
public class SpannerConnection implements Connection {

  public Publisher<Void> beginTransaction() {
    return null;
  }

  public Publisher<Void> close() {
    return null;
  }

  public Publisher<Void> commitTransaction() {
    return null;
  }

  public Batch createBatch() {
    return null;
  }

  public Publisher<Void> createSavepoint(String s) {
    return null;
  }

  public Statement createStatement(String sql) {
    return new SpannerStatement(sql);
  }

  public Publisher<Void> releaseSavepoint(String s) {
    return null;
  }

  public Publisher<Void> rollbackTransaction() {
    return null;
  }

  public Publisher<Void> rollbackTransactionToSavepoint(String s) {
    return null;
  }

  public Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
    return null;
  }
}
