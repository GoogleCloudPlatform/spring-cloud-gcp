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

import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link SpannerHealthIndicator}.
 *
 * @author ikeyat
 */
@RunWith(MockitoJUnitRunner.class)
public class SpannerHealthIndicatorTests {

	@Mock
	private SpannerTemplate spannerTemplate;

	@Mock
	private ResultSet resultSet;

	private final String QUERY = "SELECT 2";

	@Test
	public void testdoHealthCheckUp() throws Exception {
		SpannerHealthIndicator SpannerHealthIndicator = new SpannerHealthIndicator(spannerTemplate, QUERY);

		when(spannerTemplate.executeQuery(any(), any())).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);

		Health.Builder builder = new Health.Builder();

		SpannerHealthIndicator.doHealthCheck(builder);

		assertThat(builder.build().getStatus()).isSameAs(Status.UP);
		verify(spannerTemplate).executeQuery(Statement.of(QUERY), null);
		verify(resultSet).next();
	}

	@Test(expected = Exception.class)
	public void testdoHealthCheckDownSpannerTemplate() throws Exception {
		SpannerHealthIndicator SpannerHealthIndicator = new SpannerHealthIndicator(spannerTemplate, QUERY);

		when(spannerTemplate.executeQuery(any(), any())).thenThrow(new RuntimeException("Cloud Spanner is down!!!"));

		Health.Builder builder = new Health.Builder();

		SpannerHealthIndicator.doHealthCheck(builder);
	}

	@Test(expected = Exception.class)
	public void testdoHealthCheckDownResultSet() throws Exception {
		SpannerHealthIndicator SpannerHealthIndicator = new SpannerHealthIndicator(spannerTemplate, QUERY);

		when(spannerTemplate.executeQuery(any(), any())).thenReturn(resultSet);
		when(resultSet.next()).thenThrow(new RuntimeException("Cloud Spanner is down!!!"));

		Health.Builder builder = new Health.Builder();

		SpannerHealthIndicator.doHealthCheck(builder);
	}

	@Test
	public void testHealthy() {
		SpannerHealthIndicator SpannerHealthIndicator = new SpannerHealthIndicator(spannerTemplate, QUERY);

		when(spannerTemplate.executeQuery(any(), any())).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);

		assertThat(SpannerHealthIndicator.health().getStatus()).isSameAs(Status.UP);
		verify(spannerTemplate).executeQuery(Statement.of(QUERY), null);
		verify(resultSet).next();
	}

	@Test
	public void testUnhealthySpannerTemplate() {
		SpannerHealthIndicator SpannerHealthIndicator = new SpannerHealthIndicator(spannerTemplate, QUERY);

		when(spannerTemplate.executeQuery(any(), any())).thenThrow(new RuntimeException("Cloud Spanner is down!!!"));

		assertThat(SpannerHealthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
		verify(spannerTemplate).executeQuery(Statement.of(QUERY), null);
		verify(resultSet, never()).next();
	}

	@Test
	public void testUnhealthyResultSet() {
		SpannerHealthIndicator SpannerHealthIndicator = new SpannerHealthIndicator(spannerTemplate, QUERY);

		when(spannerTemplate.executeQuery(any(), any())).thenReturn(resultSet);
		when(resultSet.next()).thenThrow(new RuntimeException("Cloud Spanner is down!!!"));

		assertThat(SpannerHealthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
		verify(spannerTemplate).executeQuery(Statement.of(QUERY), null);
		verify(resultSet).next();
	}
}
