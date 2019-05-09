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

package com.google.cloud.spanner.r2dbc.client;

import com.google.spanner.v1.ExecuteSqlRequest;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.Session;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * An abstraction that wraps interaction with the Cloud Spanner Database APIs.
 */
public interface Client {

  /**
   * Release any resources held by the {@link Client}.
   *
   * @return a {@link Mono} that indicates that a client has been closed
   */
  Mono<Void> close();

  /**
   * Execute a streaming query and get partial results.
   */
  Publisher<PartialResultSet> executeStreamingSql(ExecuteSqlRequest request);

  /**
   * Create a Spanner session to be used in subsequent interactions with the database.
   * @param databaseName Fully qualified Spanner database name in the format
   * {@code projects/[PROJECT_ID]/instances/[INSTANCE]/databases/[DATABASE]}
   * @returns {@link Mono} of the generated session.
   */
  Mono<Session> createSession(String databaseName);

}
