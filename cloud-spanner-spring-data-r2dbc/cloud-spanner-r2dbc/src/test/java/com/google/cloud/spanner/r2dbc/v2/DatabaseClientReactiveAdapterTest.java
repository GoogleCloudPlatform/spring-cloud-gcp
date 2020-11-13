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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFutures;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.r2dbc.SpannerConnectionConfiguration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class DatabaseClientReactiveAdapterTest {

  // DatabaseClientReactiveAdapter dependencies
  private SpannerConnectionConfiguration config;
  private Spanner spannerClient;
  private DatabaseClient dbClient;
  private DatabaseAdminClient dbAdminClient;
  private DatabaseClientTransactionManager txnManager;
  private ExecutorService executorService;

  private DatabaseClientReactiveAdapter adapter;

  @BeforeEach
  void setup() {
    this.config = mock(SpannerConnectionConfiguration.class);
    this.spannerClient = mock(Spanner.class);
    this.dbClient = mock(DatabaseClient.class);
    this.dbAdminClient = mock(DatabaseAdminClient.class);
    this.txnManager = mock(DatabaseClientTransactionManager.class);
    this.executorService = Executors.newSingleThreadExecutor();

    this.adapter = new DatabaseClientReactiveAdapter(
        this.config,
        this.spannerClient,
        this.dbClient,
        this.dbAdminClient,
        this.executorService,
        this.txnManager);

    when(this.txnManager.commitTransaction()).thenReturn(ApiFutures.immediateFuture(null));
  }

  @AfterEach
  void shutdown() {
    this.executorService.shutdownNow();
  }

  @Test
  public void testChangeAutocommitCommitsCurrentTransaction() {
    when(this.txnManager.isInTransaction()).thenReturn(true);
    assertThat(this.adapter.isAutoCommit()).isTrue();

    // Toggle autocommit setting.
    Mono.from(this.adapter.setAutoCommit(false)).block();
    assertThat(this.adapter.isAutoCommit()).isFalse();
    verify(this.txnManager, times(1)).commitTransaction();
  }

  @Test
  public void testSameAutocommitNoop() {
    when(this.txnManager.isInTransaction()).thenReturn(true);
    assertThat(this.adapter.isAutoCommit()).isTrue();

    // Toggle autocommit setting.
    Mono.from(this.adapter.setAutoCommit(true)).block();
    assertThat(this.adapter.isAutoCommit()).isTrue();
    verify(this.txnManager, times(0)).commitTransaction();
  }
}
