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

package com.google.cloud.spanner.r2dbc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.protobuf.Value;
import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Test for {@link SpannerResult}.
 */
public class SpannerResultTest {

  private Flux<SpannerRow> resultSet;

  private ResultSetMetadata resultSetMetadata;

  /**
   * Setup.
   */
  @BeforeEach
  public void setup() {

    this.resultSetMetadata =
        ResultSetMetadata.newBuilder()
            .setRowType(
                StructType.newBuilder().addFields(
                    Field.newBuilder()
                        .setName("first_column")
                        .setType(Type.newBuilder().setCode(TypeCode.STRING))))
            .build();

    SpannerRowMetadata metadata = new SpannerRowMetadata(this.resultSetMetadata);
    this.resultSet = Flux
        .just(new SpannerRow(
                Collections.singletonList(Value.newBuilder().setStringValue("key1").build()),
                metadata),
            new SpannerRow(
                Collections.singletonList(Value.newBuilder().setStringValue("key2").build()),
                metadata));
  }

  @Test
  public void getRowsUpdatedTest() {
    StepVerifier.create(
        ((Mono) new SpannerResult(this.resultSet,Mono.just(2)).getRowsUpdated()))
        .expectNext(2)
        .verifyComplete();
  }

  @Test
  public void nullResultSetTest() {
    assertThatThrownBy(() -> new SpannerResult(null, Mono.empty()))
        .hasMessage("A non-null flux of rows is required.");
  }

  @Test
  public void nullRowsTest() {
    assertThatThrownBy(() -> new SpannerResult(Flux.empty(),null))
        .hasMessage("A non-null mono of rows updated is required.");
  }

  @Test
  public void mapTest() {

    String columnName = this.resultSetMetadata
        .getRowType()
        .getFields(0)
        .getName();

    Flux<String> result =
        new SpannerResult(this.resultSet, Mono.just(0))
            .map((row, metadata) ->
                row.get(0, String.class)
                    + "-"
                    + metadata.getColumnMetadata(0).getName());

    StepVerifier.create(result)
        .expectNext("key1-" + columnName, "key2-" + columnName)
        .verifyComplete();
  }
}
