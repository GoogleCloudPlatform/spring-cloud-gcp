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

import com.google.cloud.spanner.r2dbc.SpannerConnectionConfiguration;
import com.google.cloud.spanner.r2dbc.SpannerResult;
import com.google.cloud.spanner.r2dbc.client.Client;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SpannerClientLibraryDdlStatement implements Statement {

  // YOLO; very temporary. TODO: manage disposal.
  private ExecutorService executorService = Executors.newSingleThreadExecutor();

  //private DatabaseAdminClient databaseAdminClient;
  private Client grpcClient;

  private String query;

  private SpannerConnectionConfiguration config;

  /**
   * Creates a ready-to-run Cloud Spanner DDL statement.
   *
   * @param query query to execute; does not support placeholders
   * @param grpcClient TEMPORARY, to be removed
   * @param config spanner config containing operation timeouts etc.
   */
  public SpannerClientLibraryDdlStatement(
      String query,
      Client grpcClient,
      SpannerConnectionConfiguration config) {
    this.grpcClient = grpcClient;
    this.query = query;
    this.config = config;
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
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement bindNull(int index, Class<?> type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Statement bindNull(String name, Class<?> type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Publisher<? extends Result> execute() {
    // There is no DDL async support, so fall back to gRPC.

    return this.grpcClient
        .executeDdl(
            this.config.getFullyQualifiedDatabaseName(),
            Collections.singletonList(this.query),
            this.config.getDdlOperationTimeout(),
            this.config.getDdlOperationPollInterval())
        .map(operation -> new SpannerResult(Flux.empty(), Mono.just(0)));
  }
}
