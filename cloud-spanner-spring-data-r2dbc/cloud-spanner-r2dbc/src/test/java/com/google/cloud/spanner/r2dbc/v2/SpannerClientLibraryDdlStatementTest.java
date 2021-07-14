/*
 * Copyright 2021-2021 Google LLC
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.r2dbc.spi.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class SpannerClientLibraryDdlStatementTest {

  DatabaseClientReactiveAdapter mockClientLibraryAdapter;

  @BeforeEach
  void setUp() {
    this.mockClientLibraryAdapter = mock(DatabaseClientReactiveAdapter.class);
    when(this.mockClientLibraryAdapter.runDdlStatement(anyString()))
        .thenReturn(Mono.empty());
  }

  @Test
  void parameterBindingNotSupportedInDdl() {
    SpannerClientLibraryDdlStatement statement =
        new SpannerClientLibraryDdlStatement("unused", this.mockClientLibraryAdapter);
    assertThrows(UnsupportedOperationException.class,
        () -> statement.bind(1, "val"));
    assertThrows(UnsupportedOperationException.class,
        () -> statement.bind("col", "val"));
    assertThrows(UnsupportedOperationException.class,
        () -> statement.bindNull(1, Long.class));
    assertThrows(UnsupportedOperationException.class,
        () -> statement.bindNull("col", Long.class));
  }

  @Test
  void executeDdlAffectsZeroRows() {
    SpannerClientLibraryDdlStatement statement =
        new SpannerClientLibraryDdlStatement("unused", this.mockClientLibraryAdapter);

    StepVerifier.create(
        statement.execute().flatMap((Result r) -> Mono.from(r.getRowsUpdated()))
    ).expectNext(0)
        .verifyComplete();
  }

}
