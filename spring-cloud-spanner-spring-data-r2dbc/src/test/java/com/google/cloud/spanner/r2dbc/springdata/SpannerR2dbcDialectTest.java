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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.r2dbc.v2.JsonWrapper;
import java.util.Collection;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.relational.core.dialect.LimitClause;
import org.springframework.data.relational.core.dialect.LockClause;
import org.springframework.data.relational.core.sql.LockMode;
import org.springframework.data.relational.core.sql.LockOptions;
import org.springframework.data.relational.core.sql.SQL;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.binding.BindMarkers;

class SpannerR2dbcDialectTest {

  @Test
  void testLimitClause() {
    LimitClause clause = new SpannerR2dbcDialect().limit();
    assertThat(clause.getOffset(100)).isEqualTo("LIMIT 9223372036854775807 OFFSET 100");
    assertThat(clause.getLimit(42)).isEqualTo("LIMIT 42");
    assertThat(clause.getLimitOffset(42, 100)).isEqualTo("LIMIT 42 OFFSET 100");
    assertThat(clause.getClausePosition()).isSameAs(LimitClause.Position.AFTER_ORDER_BY);
  }

  @Test
  void testBindMarkersFactory() {
    SpannerR2dbcDialect dialect = new SpannerR2dbcDialect();
    BindMarkers bindMarkers = dialect.getBindMarkersFactory().create();
    assertThat(bindMarkers).isNotNull();
    assertThat(bindMarkers.next().getPlaceholder()).isEqualTo("@val0");
    assertThat(bindMarkers.next().getPlaceholder()).isEqualTo("@val1");
  }

  @Test
  void lockStringAlwaysEmpty() {
    SpannerR2dbcDialect dialect = new SpannerR2dbcDialect();
    Table table = SQL.table("aTable");
    Select sql = Select.builder().select(table.column("aColumn"))
        .from(table)
        .build();
    LockOptions lockOptions = new LockOptions(LockMode.PESSIMISTIC_READ, sql.getFrom());

    LockClause lock = dialect.lock();

    assertNotNull(lock);
    assertThat(lock.getLock(lockOptions)).isEmpty();
    assertThat(lock.getClausePosition()).isSameAs(LockClause.Position.AFTER_FROM_TABLE);
  }

  @Test
  void testSimpleType() {
    SpannerR2dbcDialect dialect = new SpannerR2dbcDialect();
    SimpleTypeHolder simpleTypeHolder = dialect.getSimpleTypeHolder();
    assertThat(Stream.of(JsonWrapper.class, Timestamp.class, Date.class, ByteArray.class)
        .allMatch(simpleTypeHolder::isSimpleType)).isTrue();
  }

  @Test
  void testConverter() {
    SpannerR2dbcDialect dialect = new SpannerR2dbcDialect();
    Collection<Object> converters = dialect.getConverters();
    assertTrue(
        converters.stream()
            .anyMatch(converter -> converter.getClass().equals(JsonToMapConverter.class)));
    assertTrue(
        converters.stream()
            .anyMatch(converter -> converter.getClass().equals(MapToJsonConverter.class)));
  }
}
