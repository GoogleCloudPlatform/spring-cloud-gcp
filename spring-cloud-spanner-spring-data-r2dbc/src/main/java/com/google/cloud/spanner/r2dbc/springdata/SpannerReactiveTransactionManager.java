/*
 * Copyright 2022-2023 Google LLC
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

package com.google.cloud.spanner.r2dbc.springdata;

import static org.springframework.transaction.TransactionDefinition.ISOLATION_READ_COMMITTED;
import static org.springframework.transaction.TransactionDefinition.ISOLATION_READ_UNCOMMITTED;
import static org.springframework.transaction.TransactionDefinition.ISOLATION_REPEATABLE_READ;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_NESTED;

import com.google.cloud.spanner.TimestampBound;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.TransactionDefinition;
import java.util.Arrays;
import java.util.List;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;

public class SpannerReactiveTransactionManager extends R2dbcTransactionManager {

  private static final List<Integer> UNSUPPORTED_ISOLATION_LEVELS = Arrays.asList(
      ISOLATION_READ_UNCOMMITTED,
      ISOLATION_READ_COMMITTED,
      ISOLATION_REPEATABLE_READ);

  private final TimestampBound timestampBound;

  public SpannerReactiveTransactionManager(ConnectionFactory connectionFactory,
      TimestampBound timestampBound) {
    super(connectionFactory);
    this.timestampBound = timestampBound;
  }

  public SpannerReactiveTransactionManager(ConnectionFactory connectionFactory) {
    super(connectionFactory);
    this.timestampBound = TimestampBound.strong();
  }

  @Override
  protected Mono<Void> doBegin(TransactionSynchronizationManager synchronizationManager,
      Object transaction, org.springframework.transaction.TransactionDefinition definition) {
    if (UNSUPPORTED_ISOLATION_LEVELS.contains(definition.getIsolationLevel())) {
      return Mono.error(new UnsupportedOperationException(
          "Only ISOLATION_DEFAULT and ISOLATION_SERIALIZABLE are supported."));
    }
    if (PROPAGATION_NESTED == definition.getPropagationBehavior()) {
      return Mono.error(new UnsupportedOperationException(
          "PROPAGATION_NESTED behaviour is not supported."));
    }
    return super.doBegin(synchronizationManager, transaction, definition);
  }

  @Override
  protected TransactionDefinition createTransactionDefinition(
      org.springframework.transaction.TransactionDefinition definition) {
    TransactionDefinition delegate = super.createTransactionDefinition(definition);
    return new SpannerTransactionDefinition(delegate, timestampBound);
  }

  public TimestampBound getTimestampBound() {
    return timestampBound;
  }
}
