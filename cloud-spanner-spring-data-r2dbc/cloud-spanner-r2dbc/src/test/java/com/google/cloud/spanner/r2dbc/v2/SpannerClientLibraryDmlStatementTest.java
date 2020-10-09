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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class SpannerClientLibraryDmlStatementTest {

  DatabaseClientReactiveAdapter mockAdapter;

  @BeforeEach
  public void setUpAdapterResponse() {
    this.mockAdapter = mock(DatabaseClientReactiveAdapter.class);
  }

  @Test
  public void executeSingleNoRowsUpdated() {
    when(this.mockAdapter.runDmlStatement(any(Statement.class))).thenReturn(Mono.just(0L));

    SpannerClientLibraryDmlStatement dmlStatement =
        new SpannerClientLibraryDmlStatement(this.mockAdapter, "irrelevant sql");

    StepVerifier.create(
            Flux.from(dmlStatement.execute()).flatMap(result -> result.getRowsUpdated()))
        .expectNext(0)
        .verifyComplete();
  }

  @Test
  public void executeMultiple() {
    when(this.mockAdapter.runDmlStatement(any(Statement.class))).thenReturn(Mono.just(0L));

    SpannerClientLibraryDmlStatement dmlStatement =
        new SpannerClientLibraryDmlStatement(this.mockAdapter, "irrelevant sql");

    StepVerifier.create(
            Flux.from(dmlStatement.execute()).flatMap(result -> result.getRowsUpdated()))
        .expectNext(0)
        .verifyComplete();
  }
}
