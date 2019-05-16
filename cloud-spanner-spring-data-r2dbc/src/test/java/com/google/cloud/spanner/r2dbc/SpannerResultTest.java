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
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
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

  private Mono<ResultSetMetadata> resultSetMetadata;

  /**
   * Setup.
   */
  @Before
  public void setup() {
    this.resultSet =
        Flux.just(Collections.singletonList(Value.newBuilder().setStringValue("key1").build()),
            Collections.singletonList(Value.newBuilder().setStringValue("key2").build()));

    this.resultSetMetadata = Mono.just(
        ResultSetMetadata.newBuilder()
            .setRowType(
                StructType.newBuilder().addFields(
                    Field.newBuilder()
                        .setName("first_column")
                        .setType(Type.newBuilder().setCode(TypeCode.STRING))))
            .build());
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
    String columnName = this.resultSetMetadata.block()
        .getRowType()
        .getFields(0)
        .getName();

    List<String> result =
        new SpannerResult(this.resultSet, this.resultSetMetadata)
            .map((row, metadata) ->
                row.get(0, String.class)
                    + "-"
                    + metadata.getColumnMetadata(0).getName())
            .collectList()
            .block();

    assertThat(result)
        .containsExactly("key1-" + columnName, "key2-" + columnName);
  }

  @Test
  public void noResultsMapTest() {
    assertThat(new SpannerResult(Mono.just(2)).map((x, y) -> "unused"))
        .isEqualTo(Flux.empty());
  }
}
