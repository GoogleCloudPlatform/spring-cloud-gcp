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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.r2dbc.client.Client;
import com.google.protobuf.Value;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Test for {@link SpannerStatement}.
 */
public class SpannerStatementTest {

  private static final Session TEST_SESSION =
      Session.newBuilder().setName("project/session/1234").build();

  @Test
  public void executeDummyImplementation() {

    Client mockClient = Mockito.mock(Client.class);
    String sql = "select book from library";
    PartialResultSet partialResultSet = PartialResultSet.newBuilder()
        .setMetadata(ResultSetMetadata.newBuilder().setRowType(StructType.newBuilder()
            .addFields(
                Field.newBuilder().setName("book")
                    .setType(Type.newBuilder().setCode(TypeCode.STRING)))))
        .addValues(Value.newBuilder().setStringValue("Odyssey"))
        .build();
    when(mockClient.executeStreamingSql(TEST_SESSION, Mono.empty(), sql))
        .thenReturn(Flux.just(partialResultSet));

    SpannerStatement statement
        = new SpannerStatement(mockClient, TEST_SESSION, Mono.empty(),sql);

    Mono<SpannerResult> result = (Mono<SpannerResult>)statement.execute();

    assertThat(result).isNotNull();

    result.block().map((r, m) -> (String)r.get(0)).blockFirst().equals("Odyssey");

    verify(mockClient).executeStreamingSql(TEST_SESSION, Mono.empty(), sql);
  }

}
