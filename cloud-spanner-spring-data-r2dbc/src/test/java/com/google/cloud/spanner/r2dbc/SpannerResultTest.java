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

package com.google.cloud.spanner.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.protobuf.Value;
import com.google.spanner.v1.ResultSetMetadata;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Test for {@link SpannerResult}.
 */
public class SpannerResultTest {

  private Flux<List<Value>> resultSet;

  private Mono<ResultSetMetadata> resultSetMetadata = Mono
      .just(ResultSetMetadata.newBuilder().build());

  /**
   * Setup.
   */
  @Before
  public void setup() {
    this.resultSet = Flux
        .just(Collections.singletonList(Value.newBuilder().setStringValue("key1").build()),
            Collections.singletonList(Value.newBuilder().setStringValue("key2").build()));
  }

  @Test
  public void getRowsUpdatedTest() {
    assertThat(
        ((Mono) new SpannerResult(this.resultSet, this.resultSetMetadata).getRowsUpdated()).block())
        .isEqualTo(0);
    assertThat(((Mono) new SpannerResult(Mono.just(2)).getRowsUpdated()).block())
        .isEqualTo(2);
  }

  @Test
  public void nullResultSetTest() {
    assertThatThrownBy(() -> new SpannerResult(null, null))
        .hasMessage("A non-null flux of rows is required.");
  }

  @Test
  public void nullRowMetadataTest() {
    assertThatThrownBy(() -> new SpannerResult(this.resultSet, null))
        .hasMessage("Non-null row metadata is required.");
  }

  @Test
  public void mapTest() {
    String metadataString = this.resultSetMetadata.block().toString();
    assertThat(new SpannerResult(this.resultSet, this.resultSetMetadata).map((row, metadata) ->
        ((SpannerRow) row).getValues().get(0).getStringValue() + "-"
            + ((SpannerRowMetadata) metadata).getRowMetadata().toString()).collectList().block())
        .containsExactly("key1-" + metadataString, "key2-" + metadataString);
  }

  @Test
  public void noResultsMapTest() {
    assertThat(new SpannerResult(Mono.just(2)).map((x, y) -> "unused"))
        .isEqualTo(Flux.empty());
  }
}
