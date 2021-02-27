/*
 * Copyright 2019-2020 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.pubsub.health;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Tests for the Pub/Sub Health Indicator.
 *
 * @author Vinicius Carvalho
 * @author Patrik Hörlin
 */
@RunWith(MockitoJUnitRunner.class)
public class PubSubHealthIndicatorTests {

	@Mock
	private PubSubHealthTemplate pubSubHealthTemplate;

	@Test
	public void healthUpForExpectedException() throws Exception {
		ExecutionException e = new ExecutionException("Exception", new IllegalArgumentException());

		doThrow(e).when(pubSubHealthTemplate).pullAndAckAsync();
		when(pubSubHealthTemplate.isExpectedExecutionException(e)).thenReturn(true);

		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.UP);
	}

	@Test
	public void healthDown() throws Exception {
		ExecutionException e = new ExecutionException("Exception", new IllegalArgumentException());

		doThrow(e).when(pubSubHealthTemplate).pullAndAckAsync();
		when(pubSubHealthTemplate.isExpectedExecutionException(e)).thenReturn(false);

		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
	}

	@Test
	public void healthDownGenericException() throws Exception {
		Exception e = new IllegalStateException("Illegal State");

		doThrow(e).when(pubSubHealthTemplate).pullAndAckAsync();
		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
	}

	@Test
	public void healthUnknownInterruptedException() throws Exception {
		Exception e = new InterruptedException("Interrupted");

		doThrow(e).when(pubSubHealthTemplate).pullAndAckAsync();
		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.UNKNOWN);
	}

	@Test
	public void healthUnknownTimeoutException() throws Exception {
		Exception e = new TimeoutException("Timed out waiting for result");

		doThrow(e).when(pubSubHealthTemplate).pullAndAckAsync();
		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.UNKNOWN);
	}

	@Test
	public void healthDownException() throws InterruptedException, ExecutionException, TimeoutException {
		Exception e = new RuntimeException("Runtime error");

		doThrow(e).when(pubSubHealthTemplate).pullAndAckAsync();
		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
	}
}
