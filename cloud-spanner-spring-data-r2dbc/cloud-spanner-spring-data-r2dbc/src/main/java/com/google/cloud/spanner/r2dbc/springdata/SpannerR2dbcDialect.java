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

package com.google.cloud.spanner.r2dbc.springdata;

import org.springframework.data.r2dbc.dialect.BindMarkersFactory;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.relational.core.dialect.AbstractDialect;
import org.springframework.data.relational.core.dialect.LimitClause;

/**
 * The {@link R2dbcDialect} implementation which enables usage of Spring Data R2DBC with Cloud
 * Spanner.
 */
public class SpannerR2dbcDialect extends AbstractDialect implements R2dbcDialect {
  private static final BindMarkersFactory NAMED =
      BindMarkersFactory.named("@", "val", 32);

  private static final LimitClause LIMIT_CLAUSE = new LimitClause() {
    @Override
    public String getLimit(long limit) {
      return "LIMIT " + limit;
    }

    @Override
    public String getOffset(long offset) {
      return "LIMIT " + Long.MAX_VALUE + " OFFSET " + offset;
    }

    @Override
    public String getLimitOffset(long limit, long offset) {
      return "LIMIT " + limit + " OFFSET " + offset;
    }

    @Override
    public Position getClausePosition() {
      return Position.AFTER_ORDER_BY;
    }
  };

  @Override
  public BindMarkersFactory getBindMarkersFactory() {
    return NAMED;
  }

  @Override
  public LimitClause limit() {
    return LIMIT_CLAUSE;
  }
}
