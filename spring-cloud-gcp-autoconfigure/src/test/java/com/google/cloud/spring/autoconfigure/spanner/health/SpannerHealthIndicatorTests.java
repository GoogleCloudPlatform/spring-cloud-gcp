/*
 * Copyright 2017-2019 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

/**
 * Tests for {@link SpannerHealthIndicator}.
 *
 * @since 2.0.6
 */
@ExtendWith(MockitoExtension.class)
class SpannerHealthIndicatorTests {

  @Mock private SpannerTemplate spannerTemplate;

  @Mock private ResultSet resultSet;

  private static final String QUERY = "SELECT 2";

  @Test
  void testdoHealthCheckUp() throws Exception {
    SpannerHealthIndicator spannerHealthIndicator =
        new SpannerHealthIndicator(spannerTemplate, QUERY);

    when(spannerTemplate.executeQuery(any(), any())).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true);

    Health.Builder builder = new Health.Builder();

    spannerHealthIndicator.doHealthCheck(builder);

    assertThat(builder.build().getStatus()).isSameAs(Status.UP);
    verify(spannerTemplate).executeQuery(Statement.of(QUERY), null);
    verify(resultSet).next();
    // make sure Spanner ResultSet is closed to avoid session leak.
    verify(resultSet).close();
  }

  @Test
  void testdoHealthCheckDownSpannerTemplate() throws Exception {
    SpannerHealthIndicator spannerHealthIndicator =
        new SpannerHealthIndicator(spannerTemplate, QUERY);

    when(spannerTemplate.executeQuery(any(), any()))
        .thenThrow(new RuntimeException("Cloud Spanner is down!!!"));

    Health.Builder builder = new Health.Builder();

    assertThatThrownBy(() -> spannerHealthIndicator.doHealthCheck(builder))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Cloud Spanner is down!!!");

    verify(resultSet, never()).close();
  }

  @Test
  void testdoHealthCheckDownResultSet() throws Exception {
    SpannerHealthIndicator spannerHealthIndicator =
        new SpannerHealthIndicator(spannerTemplate, QUERY);

    when(spannerTemplate.executeQuery(any(), any())).thenReturn(resultSet);
    when(resultSet.next()).thenThrow(new RuntimeException("Cloud Spanner is down!!!"));

    Health.Builder builder = new Health.Builder();

    assertThatThrownBy(() -> spannerHealthIndicator.doHealthCheck(builder))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Cloud Spanner is down!!!");
    verify(resultSet).close();
  }

  @Test
  void testHealthy() {
    SpannerHealthIndicator spannerHealthIndicator =
        new SpannerHealthIndicator(spannerTemplate, QUERY);

    when(spannerTemplate.executeQuery(any(), any())).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true);

    assertThat(spannerHealthIndicator.health().getStatus()).isSameAs(Status.UP);
    verify(spannerTemplate).executeQuery(Statement.of(QUERY), null);
    verify(resultSet).next();
    verify(resultSet).close();
  }

  @Test
  void testUnhealthySpannerTemplate() {
    SpannerHealthIndicator spannerHealthIndicator =
        new SpannerHealthIndicator(spannerTemplate, QUERY);

    when(spannerTemplate.executeQuery(any(), any()))
        .thenThrow(new RuntimeException("Cloud Spanner is down!!!"));

    assertThat(spannerHealthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
    verify(spannerTemplate).executeQuery(Statement.of(QUERY), null);
    verify(resultSet, never()).next();
    verify(resultSet, never()).close();
  }

  @Test
  void testUnhealthyResultSet() {
    SpannerHealthIndicator spannerHealthIndicator =
        new SpannerHealthIndicator(spannerTemplate, QUERY);

    when(spannerTemplate.executeQuery(any(), any())).thenReturn(resultSet);
    when(resultSet.next()).thenThrow(new RuntimeException("Cloud Spanner is down!!!"));

    assertThat(spannerHealthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
    verify(spannerTemplate).executeQuery(Statement.of(QUERY), null);
    verify(resultSet).next();
    verify(resultSet).close();
  }
}
