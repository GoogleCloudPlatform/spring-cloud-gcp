/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.autoconfigure.spanner.health;

import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.data.spanner.core.SpannerQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

/**
 * Default implementation of {@link org.springframework.boot.actuate.health.HealthIndicator} for
 * Spanner. Validates if connection is successful by executing query from the spannerTemplate using
 * {@link SpannerTemplate#executeQuery(Statement, SpannerQueryOptions)}.
 *
 * <p>If there is no error, this health indicator will signal "up".
 *
 * @since 2.0.6
 */
public class SpannerHealthIndicator extends AbstractHealthIndicator {

  private Statement validationStatement;

  private SpannerTemplate spannerTemplate;

  /**
   * SpannerHealthIndicator constructor.
   *
   * @param spannerTemplate spannerTemplate to execute query
   * @param validationQuery query to execute
   */
  public SpannerHealthIndicator(final SpannerTemplate spannerTemplate, String validationQuery) {
    super("Spanner health check failed");
    this.spannerTemplate = spannerTemplate;
    this.validationStatement = Statement.of(validationQuery);
  }

  @Override
  protected void doHealthCheck(Builder builder) throws Exception {
    try (ResultSet resultSet = spannerTemplate.executeQuery(validationStatement, null)) {
      // Touch the record
      resultSet.next();
    }

    builder.up();
  }
}
