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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.spanner.Struct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Test for {@link SpannerClientLibraryResult}.
 */
class SpannerResultTest {

  private Flux<SpannerClientLibraryRow> resultSet;


  /**
   * Setup.
   */
  @BeforeEach
  public void setup() {

    this.resultSet = Flux.just(
            new SpannerClientLibraryRow(Struct.newBuilder().set("a").to(true).build()),
            new SpannerClientLibraryRow(Struct.newBuilder().set("b").to(false).build())
        );
  }

  @Test
  void getRowsUpdatedTest() {
    StepVerifier.create(
        ((Mono) new SpannerClientLibraryResult(this.resultSet, 2).getRowsUpdated()))
        .expectNext(2)
        .verifyComplete();
  }

  @Test
  void nullResultSetTest() {
    assertThatThrownBy(() -> new SpannerClientLibraryResult(null, 0))
        .hasMessage("A non-null flux of rows is required.");
  }

  @Test
  void mapTest() {

    Publisher<String> result = new SpannerClientLibraryResult(this.resultSet, 0)
        .map((row, metadata) ->
            row.get(0, Boolean.class)
                + "-"
                + metadata.getColumnMetadata(0).getName());

    StepVerifier.create(result)
        .expectNext("true-" + "a", "false-" + "a")
        .verifyComplete();
  }
}
