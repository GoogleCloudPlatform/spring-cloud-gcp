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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.Statement;
import io.r2dbc.spi.Batch;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class SpannerBatchTest {

  DatabaseClientReactiveAdapter mockAdapter;

  @BeforeEach
  void setUpMocks() {
    this.mockAdapter = mock(DatabaseClientReactiveAdapter.class);
  }

  @Test
  void emptyBatchFails() {
    Batch batch = new SpannerBatch(this.mockAdapter);
    StepVerifier.create(
        Flux.from(batch.execute())
    ).verifyErrorMessage("Batch is empty.");
  }

  @Test
  void addNullFails() {
    Batch batch = new SpannerBatch(this.mockAdapter);
    assertThatThrownBy(() -> batch.add(null))
        .hasMessage("SQL must not be null.");
  }

  @Test
  void nonDmlDisallowedInBatch() {
    Batch batch = new SpannerBatch(this.mockAdapter);
    assertThatThrownBy(() -> batch.add("SELECT * FROM tbl"))
        .hasMessage("Only DML statements are supported in batches.");
    assertThatThrownBy(() -> batch.add("CREATE TABLE blah"))
        .hasMessage("Only DML statements are supported in batches.");
  }

  @Test
  void batchPassesCorrectQueriesToAdapter() {
    Batch batch = new SpannerBatch(this.mockAdapter);
    when(this.mockAdapter.runBatchDml(anyList()))
        .thenReturn(Flux.just(
            new SpannerClientLibraryResult(Flux.empty(), 35),
            new SpannerClientLibraryResult(Flux.empty(), 47)
        ));
    StepVerifier.create(
        Flux.from(
            batch.add("UPDATE tbl SET col1=val1")
                .add("UPDATE tbl SET col2=val2").execute()
        ).flatMap(r -> r.getRowsUpdated())
    ).expectNext(35, 47)
        .verifyComplete();

    ArgumentCaptor<List<Statement>> argCaptor = ArgumentCaptor.forClass(List.class);
    verify(this.mockAdapter).runBatchDml(argCaptor.capture());
    List<Statement> args = argCaptor.getValue();
    assertThat(args).hasSize(2);
    assertThat(args.get(0).getSql()).isEqualTo("UPDATE tbl SET col1=val1");
    assertThat(args.get(1).getSql()).isEqualTo("UPDATE tbl SET col2=val2");
  }
}
