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

package com.google.cloud.spanner.r2dbc.v2;

import com.google.cloud.spanner.AsyncResultSet;
import com.google.cloud.spanner.AsyncResultSet.CallbackResponse;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Statement.Builder;
import com.google.cloud.spanner.r2dbc.statement.TypedNull;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import java.util.concurrent.ExecutorService;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

/**
 * Cloud Spanner implementation of R2DBC SPI for query statements.
 */
public class SpannerClientLibraryStatement implements Statement {

  private final Builder statementBuilder;

  private ExecutorService executorService;

  private DatabaseClient databaseClient;

  private String query;

  /**
   * Creates a ready-to-run Cloud Spanner statement.
   * @param databaseClient Cloud Spanner client library database client
   * @param query query to run, with `@` placeholders expected as parameters.
   * @param executorService to use for AsyncResultSet callback
   */
  // TODO: accept a transaction
  public SpannerClientLibraryStatement(DatabaseClient databaseClient, String query,
      ExecutorService executorService) {
    this.databaseClient = databaseClient;
    this.query = query;
    this.statementBuilder = com.google.cloud.spanner.Statement.newBuilder(this.query);
    this.executorService = executorService;
  }

  @Override
  public Statement add() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement bind(int index, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement bind(String name, Object value) {
    ClientLibraryBinder.bind(this.statementBuilder, name, value);
    return this;
  }

  @Override
  public Statement bindNull(int index, Class<?> type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement bindNull(String name, Class<?> type) {
    ClientLibraryBinder.bind(this.statementBuilder, name, new TypedNull(type));
    return this;
  }

  @Override
  public Publisher<? extends Result> execute() {
    // TODO: unplaceholder singleUse, extract into member to allow transaction options customization
    // make note -- timestamp bound passed as part of transaction type
    return Flux.<SpannerClientLibraryRow>create(
        sink -> {
          AsyncResultSet ars =
              this.databaseClient.singleUse().executeQueryAsync(this.statementBuilder.build());
          sink.onCancel(ars::cancel);

          // TODO: handle backpressure by asking callback to signal CallbackResponse.PAUSE
          //   Use sink.onRequest() to keep track of cumulative demand
          ars.setCallback(this.executorService, rs -> this.callback(sink, rs));
        })
        .transform(rowFlux -> Mono.just(new SpannerClientLibraryResult(rowFlux, Mono.just(0))));
  }

  private CallbackResponse callback(FluxSink sink, AsyncResultSet resultSet) {
    try {
      switch (resultSet.tryNext()) {
        case DONE:
          sink.complete();
          return CallbackResponse.DONE;
        case NOT_READY:
        default:
          return CallbackResponse.CONTINUE;
        case OK:
          sink.next(new SpannerClientLibraryRow(resultSet.getCurrentRowAsStruct()));
          return CallbackResponse.CONTINUE;
      }
    } catch (Throwable t) {
      sink.error(t);
      return CallbackResponse.DONE;
    }
  }
}
