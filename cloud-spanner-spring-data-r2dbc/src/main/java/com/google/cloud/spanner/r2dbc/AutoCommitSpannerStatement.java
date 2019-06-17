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

import com.google.cloud.spanner.r2dbc.client.Client;
import com.google.cloud.spanner.r2dbc.util.Assert;
import io.r2dbc.spi.Result;
import org.reactivestreams.Publisher;

/**
 * A `SpannerStatement` that is single-use for read and write.
 */
public class AutoCommitSpannerStatement extends SpannerStatement {

  private final SpannerConnection spannerConnection;

  /**
   * Creates a Spanner statement for a given SQL statement.
   *
   * @param client cloud spanner client to use for performing the query operation
   * @param sql the query to execute
   * @param config config about the database and instance to use
   * @param spannerConnection the connection used for this single-use statement.
   */
  public AutoCommitSpannerStatement(Client client, String sql,
      SpannerConnectionConfiguration config, SpannerConnection spannerConnection) {
    super(client, spannerConnection, sql, config);
    this.spannerConnection = Assert
        .requireNonNull(spannerConnection, "A non-null SpannerConnection is required.");
  }

  @Override
  public Publisher<? extends Result> execute() {
    return this.spannerConnection.getTransactionId() == null
        ? this.spannerConnection.beginTransaction()
        .thenMany(super.execute())
        .delayUntil(r -> this.spannerConnection.commitTransaction())
        : super.execute();
  }

}
