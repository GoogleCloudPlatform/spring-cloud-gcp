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

package com.google.cloud.spanner.r2dbc.statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import org.junit.jupiter.api.Test;

class StatementBindingsTest {

  @Test
  void addBasicBinding() {
    StatementBindings statementBindings = new StatementBindings();
    statementBindings.createBind("name", "John");
    statementBindings.createBind("age", 50);
    statementBindings.createBind("array", new String[]{"a", "b"});
    statementBindings.completeBinding();

    assertThat(statementBindings.getTypes())
        .containsExactly(
          entry("array", Type.newBuilder().setCode(TypeCode.ARRAY)
              .setArrayElementType(Type.newBuilder().setCode(TypeCode.STRING).build()).build()),
          entry("name", Type.newBuilder().setCode(TypeCode.STRING).build()),
          entry("age", Type.newBuilder().setCode(TypeCode.INT64).build())
        );

    assertThat(statementBindings.getBindings())
        .containsExactly(
            Struct.newBuilder()
                .putFields("name", Value.newBuilder().setStringValue("John").build())
                .putFields("age", Value.newBuilder().setStringValue("50").build())
                .putFields("array", Value.newBuilder().setListValue(
                    ListValue.newBuilder().addValues(Value.newBuilder().setStringValue("a").build())
                        .addValues(Value.newBuilder().setStringValue("b").build()).build()).build())
                .build());
  }

  @Test
  void testNoopAddBinding() {
    StatementBindings statementBindings = new StatementBindings();
    statementBindings.completeBinding();
    statementBindings.completeBinding();
    statementBindings.completeBinding();

    assertThat(statementBindings.getTypes()).isEmpty();
    assertThat(statementBindings.getBindings()).containsExactly(Struct.getDefaultInstance());
  }
}
