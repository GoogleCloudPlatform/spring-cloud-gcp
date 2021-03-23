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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.actuate.health.Status;
import org.springframework.util.concurrent.ListenableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the Pub/Sub Health Indicator.
 *
 * @author Vinicius Carvalho
 * @author Patrik Hörlin
 */
@ExtendWith(MockitoExtension.class)
class PubSubHealthIndicatorTests {

	@Mock
	private PubSubTemplate pubSubTemplate;

	@Mock
	ListenableFuture<List<AcknowledgeablePubsubMessage>> future;

	@Test
	void healthUp() throws Exception {
		when(future.get(anyLong(), any())).thenReturn(Collections.emptyList());
		when(pubSubTemplate.pullAsync(anyString(), anyInt(), anyBoolean())).thenReturn(future);

		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubTemplate, "test", 1000, true);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.UP);
	}

	@Test
	void acknowledgeEnabled_shouldAcknowledgeMessages() throws Exception {
		AcknowledgeablePubsubMessage msg = mock(AcknowledgeablePubsubMessage.class);
		when(future.get(anyLong(), any())).thenReturn(Arrays.asList(msg));
		when(pubSubTemplate.pullAsync(anyString(), anyInt(), anyBoolean())).thenReturn(future);

		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubTemplate, "test", 1000, true);
		healthIndicator.health();
		verify(msg).ack();
	}

	@Test
	void acknowledgeDisabled_shouldAcknowledgeMessages() throws Exception {
		AcknowledgeablePubsubMessage msg = mock(AcknowledgeablePubsubMessage.class);
		when(future.get(anyLong(), any())).thenReturn(Arrays.asList(msg));
		when(pubSubTemplate.pullAsync(anyString(), anyInt(), anyBoolean())).thenReturn(future);

		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubTemplate, "test", 1000, false);
		healthIndicator.health();
		verify(msg, never()).ack();
	}

	@ParameterizedTest
	@ValueSource(strings = {"NOT_FOUND", "PERMISSION_DENIED"})
	void healthUpForExpectedException(String code) throws Exception {
		Exception e = new ApiException(new IllegalStateException("Illegal State"), GrpcStatusCode.of(io.grpc.Status.Code.valueOf(code)), false);

		when(pubSubTemplate.pullAsync(anyString(), anyInt(), anyBoolean())).thenReturn(future);
		doThrow(new ExecutionException(e)).when(future).get(anyLong(), any());

		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubTemplate, null, 1000, true);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.UP);
	}

	@ParameterizedTest
	@ValueSource(strings = {"NOT_FOUND", "PERMISSION_DENIED"})
	void healthDownForApiException(String code) throws Exception {
		Exception e = new ApiException(new IllegalStateException("Illegal State"), GrpcStatusCode.of(io.grpc.Status.Code.valueOf(code)), false);

		when(pubSubTemplate.pullAsync(anyString(), anyInt(), anyBoolean())).thenReturn(future);
		doThrow(new ExecutionException(e)).when(future).get(anyLong(), any());

		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubTemplate, "test", 1000, true);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(Status.DOWN);
	}

	void healthExceptionTest(Exception e, Status expectedStatus) throws Exception {
		when(pubSubTemplate.pullAsync(anyString(), anyInt(), anyBoolean())).thenReturn(future);
		doThrow(e).when(future).get(anyLong(), any());

		PubSubHealthIndicator healthIndicator = new PubSubHealthIndicator(pubSubTemplate, "test", 1000, true);
		assertThat(healthIndicator.health().getStatus()).isEqualTo(expectedStatus);
	}

	@Test
	void healthDown() throws Exception {
		ExecutionException e = new ExecutionException("Exception", new IllegalArgumentException());
		healthExceptionTest(e, Status.DOWN);
	}

	@Test
	void healthDownGenericException() throws Exception {
		Exception e = new IllegalStateException("Illegal State");
		healthExceptionTest(e, Status.DOWN);
	}

	@Test
	void healthUnknownInterruptedException() throws Exception {
		Exception e = new InterruptedException("Interrupted");
		healthExceptionTest(e, Status.UNKNOWN);
	}

	@Test
	void healthUnknownTimeoutException() throws Exception {
		Exception e = new TimeoutException("Timed out waiting for result");
		healthExceptionTest(e, Status.UNKNOWN);
	}

	@Test
	void healthDownException() throws Exception {
		Exception e = new RuntimeException("Runtime error");
		healthExceptionTest(e, Status.DOWN);
	}
}
