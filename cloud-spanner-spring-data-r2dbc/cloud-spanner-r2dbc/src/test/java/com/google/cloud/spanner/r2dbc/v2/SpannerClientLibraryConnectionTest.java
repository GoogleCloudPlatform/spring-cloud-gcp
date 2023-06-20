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

package com.google.cloud.spanner.r2dbc.v2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.TimestampBound;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.IsolationLevel;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class SpannerClientLibraryConnectionTest {

  DatabaseClientReactiveAdapter mockAdapter;

  SpannerClientLibraryConnection connection;

  /** Sets up mocks. */
  @BeforeEach
  public void setUpMocks() {
    this.mockAdapter = mock(DatabaseClientReactiveAdapter.class);
    this.connection = new SpannerClientLibraryConnection(this.mockAdapter);
  }

  @Test
  void beginReadonlyTransactionUsesStrongConsistencyByDefault() {

    when(this.mockAdapter.beginReadonlyTransaction(any())).thenReturn(Mono.empty());

    StepVerifier.create(this.connection.beginReadonlyTransaction())
        .verifyComplete();

    verify(this.mockAdapter).beginReadonlyTransaction(TimestampBound.strong());
  }

  @Test
  void batchUsesCorrectAdapter() {
    Batch batch = this.connection.createBatch();
    when(this.mockAdapter.runBatchDml(anyList()))
        .thenReturn(Flux.just(
            new SpannerClientLibraryResult(Flux.empty(), 35)
        ));
    StepVerifier.create(
        Flux.from(
            batch.add("UPDATE tbl SET col1=val1").execute()
        ).flatMap(r -> r.getRowsUpdated())
    ).expectNext(35)
    .verifyComplete();

    ArgumentCaptor<List<Statement>> argCaptor = ArgumentCaptor.forClass(List.class);
    verify(this.mockAdapter).runBatchDml(argCaptor.capture());
    List<Statement> args = argCaptor.getValue();
    assertThat(args).hasSize(1);
    assertThat(args.get(0).getSql()).isEqualTo("UPDATE tbl SET col1=val1");
  }

  @Test
  void beginTransactionCustomDefinitionNotSupported() {
    StepVerifier.create(
        this.connection.beginTransaction(IsolationLevel.SERIALIZABLE)
    ).verifyError(UnsupportedOperationException.class);
  }

  @Test
  void setLockWaitTimeoutNotSupported() {
    StepVerifier.create(
        this.connection.setLockWaitTimeout(Duration.ofSeconds(1))
    ).verifyError(UnsupportedOperationException.class);
  }

  @Test
  void setStatementTimeoutNotSupported() {
    StepVerifier.create(
        this.connection.setStatementTimeout(Duration.ofSeconds(1))
    ).verifyError(UnsupportedOperationException.class);
  }
}
