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

import com.google.spanner.v1.CommitResponse;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.Transaction;
import javax.annotation.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * An abstraction that wraps interaction with the Cloud Spanner Database APIs.
 */
public interface Client {
  /**
   * Create a Spanner session to be used in subsequent interactions with the database.
   * @param databaseName Fully qualified Spanner database name in the format
   * {@code projects/[PROJECT_ID]/instances/[INSTANCE]/databases/[DATABASE]}
   * @returns {@link Mono} of the generated session.
   */
  Mono<Session> createSession(String databaseName);

  /**
   * Deletes a Spanner session that is used to call Spanner APIs.
   * @param session The session you wish to close.
   * @return {@link Mono} indicating completion closing the session.
   */
  Mono<Void> deleteSession(Session session);

  /**
   * Begins a new Spanner {@link Transaction} within the provided {@link Session}.
   * @param session The {@link Session} object with which requests are made to the Spanner API.
   * @returns {@link Mono} of the transaction that was started.
   */
  Mono<Transaction> beginTransaction(Session session);

  /**
   * Commits a Spanner {@link Transaction} within the provided {@link Session}.
   * @param session The session object with which requests are made to the Spanner API.
   * @param transaction The transaction that you want to commit.
   * @returns {@link CommitResponse} describing the timestamp at which the transaction committed.
   */
  Mono<CommitResponse> commitTransaction(Session session, Transaction transaction);


  /**
   * Performs a rollback on a Spanner {@link Transaction} within the provided {@link Session}.
   * @param session The session object with which requests are made to the Spanner API.
   * @param transaction The transaction that you want to rollback.
   * @return {@link Mono} indicating completion of the rollback.
   */
  Mono<Void> rollbackTransaction(Session session, Transaction transaction);

  /**
   * Execute a streaming query and get partial results.
   */
  Flux<PartialResultSet> executeStreamingSql(
      Session session, @Nullable Transaction transaction, String sql);

  /**
   * Release any resources held by the {@link Client}.
   *
   * @return a {@link Mono} that indicates that a client has been closed
   */
  Mono<Void> close();
}
