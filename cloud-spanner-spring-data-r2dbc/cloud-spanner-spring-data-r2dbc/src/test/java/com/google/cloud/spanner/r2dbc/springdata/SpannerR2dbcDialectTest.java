/*
 * Copyright 2020-2021 Google LLC
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

package com.google.cloud.spanner.r2dbc.springdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.data.r2dbc.dialect.BindMarkers;
import org.springframework.data.relational.core.dialect.LimitClause;
import org.springframework.data.relational.core.dialect.LimitClause.Position;

class SpannerR2dbcDialectTest {

  @Test
  void testLimitClause() {
    LimitClause clause = new SpannerR2dbcDialect().limit();
    assertThat(clause.getOffset(100)).isEqualTo("LIMIT 9223372036854775807 OFFSET 100");
    assertThat(clause.getLimit(42)).isEqualTo("LIMIT 42");
    assertThat(clause.getLimitOffset(42, 100)).isEqualTo("LIMIT 42 OFFSET 100");
    assertThat(clause.getClausePosition()).isSameAs(Position.AFTER_ORDER_BY);
  }

  @Test
  void testBindMarkersFactory() {
    SpannerR2dbcDialect dialect = new SpannerR2dbcDialect();
    BindMarkers bindMarkers = dialect.getBindMarkersFactory().create();
    assertThat(bindMarkers).isNotNull();
    assertThat(bindMarkers.next().getPlaceholder()).isEqualTo("@val0");
    assertThat(bindMarkers.next().getPlaceholder()).isEqualTo("@val1");
  }

}
