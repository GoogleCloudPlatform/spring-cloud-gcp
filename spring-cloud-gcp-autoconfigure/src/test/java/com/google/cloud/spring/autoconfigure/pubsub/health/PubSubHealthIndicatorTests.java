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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import io.grpc.Status.Code;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.boot.actuate.health.Status;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
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
	public void healthUpFor404() throws Exception {
		SettableListenableFuture<List<AcknowledgeablePubsubMessage>> result = new SettableListenableFuture<>();
		result.setException(new ApiException(
				new IllegalStateException("Illegal State"), GrpcStatusCode.of(io.grpc.Status.Code.NOT_FOUND), false));

		when(pubSubHealthTemplate.pullAsync()).thenReturn(result);
		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.UP);
	}

	@Test
	public void healthUpFor403() throws Exception {
		SettableListenableFuture<List<AcknowledgeablePubsubMessage>> result = new SettableListenableFuture<>();
		result.setException(new ApiException(
				new IllegalStateException("Illegal State"), GrpcStatusCode.of(Code.PERMISSION_DENIED), false));

		when(pubSubHealthTemplate.pullAsync()).thenReturn(result);
		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.UP);
	}

	@Test
	public void healthDown() {
		SettableListenableFuture<List<AcknowledgeablePubsubMessage>> result = new SettableListenableFuture<>();
		result.setException(new ApiException(new IllegalStateException("Illegal State"),
				GrpcStatusCode.of(io.grpc.Status.Code.INVALID_ARGUMENT), false));

		when(pubSubHealthTemplate.pullAsync()).thenReturn(result);
		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
	}

	@Test
	public void healthDownGenericException() {
		SettableListenableFuture<List<AcknowledgeablePubsubMessage>> result = new SettableListenableFuture<>();
		result.setException(new IllegalStateException("Illegal State"));

		when(pubSubHealthTemplate.pullAsync()).thenReturn(result);
		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void healthUnknownInterruptedException() throws InterruptedException, ExecutionException, TimeoutException {
		ListenableFuture<List<AcknowledgeablePubsubMessage>> result = mock(ListenableFuture.class);
		when(result.get(anyLong(), any())).thenThrow(new InterruptedException("Interrupted"));

		when(pubSubHealthTemplate.pullAsync()).thenReturn(result);
		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.UNKNOWN);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void healthUnknownTimeoutException() throws InterruptedException, ExecutionException, TimeoutException {
		ListenableFuture<List<AcknowledgeablePubsubMessage>> result = mock(ListenableFuture.class);
		when(result.get(anyLong(), any())).thenThrow(new TimeoutException("Timed out waiting for result"));

		when(pubSubHealthTemplate.pullAsync()).thenReturn(result);
		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubHealthTemplate);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.UNKNOWN);
	}
}
