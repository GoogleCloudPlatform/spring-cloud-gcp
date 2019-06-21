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
import com.google.cloud.spanner.r2dbc.statement.StatementParser;
import com.google.cloud.spanner.r2dbc.statement.StatementType;
import com.google.cloud.spanner.r2dbc.util.Assert;
import com.google.spanner.v1.ExecuteBatchDmlResponse;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Result;
import java.util.ArrayList;
import java.util.List;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link Batch} implementation for Cloud Spanner.
 */
public class SpannerBatch implements Batch {

  private final SpannerConnection connection;
  private final StatementExecutionContext ctx;
  private final Client client;

  private List<String> statements = new ArrayList<>();

  SpannerBatch(Client client, StatementExecutionContext ctx, SpannerConnection connection) {
    this.client = client;
    this.ctx = ctx;
    this.connection = connection;
  }

  @Override
  public Batch add(String sql) {
    Assert.requireNonNull(sql, "SQL must not be null");
    if (StatementParser.getStatementType(sql) != StatementType.DML) {
      throw new IllegalArgumentException("Only DML statements are supported in batches");
    }

    this.statements.add(sql);
    return this;
  }

  @Override
  public Publisher<? extends Result> execute() {
    return executeInTransaction().flatMapIterable(ExecuteBatchDmlResponse::getResultSetsList)
        .map(resultSet -> {
          int count = Math.toIntExact(resultSet.getStats().getRowCountExact());
          return new SpannerResult(Flux.empty(), Mono.just(count));
        });
  }

  private Mono<ExecuteBatchDmlResponse> executeInTransaction() {
    return this.ctx.getTransactionId() == null
        ? this.connection.beginTransaction()
            .then(this.client
                .executeBatchDml(this.ctx, this.statements))
            .delayUntil(r -> this.connection.commitTransaction())
        : this.client
            .executeBatchDml(this.ctx, this.statements);
  }
}
