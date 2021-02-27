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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.util.concurrent.ListenableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the PubSub Health Indicator.
 *
 * @author Patrik Hörlin
 */
@RunWith(MockitoJUnitRunner.class)
public class PubSubHealthTemplateTest {

	@Mock
	private PubSubTemplate pubSubTemplate;

	@SuppressWarnings("unchecked")
	@Test
	public void testPullAsync() throws Exception {
		ListenableFuture<List<AcknowledgeablePubsubMessage>> future = mock(ListenableFuture.class);

		when(future.get(anyLong(), any())).thenReturn(Collections.emptyList());
		when(pubSubTemplate.pullAsync(anyString(), anyInt(), anyBoolean())).thenReturn(future);
		PubSubHealthTemplate healthTemplate = new PubSubHealthTemplate(pubSubTemplate, "test-subscription", 1000);
		healthTemplate.pullAndAckAsync();

		verify(pubSubTemplate).pullAsync("test-subscription", 1, true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPullAsync_userSubscriptionNotSpecified_shouldGenerateRandomSubscription() throws Exception {
		ListenableFuture<List<AcknowledgeablePubsubMessage>> future = mock(ListenableFuture.class);

		when(future.get(anyLong(), any())).thenReturn(Collections.emptyList());
		when(pubSubTemplate.pullAsync(anyString(), anyInt(), anyBoolean())).thenReturn(future);
		PubSubHealthTemplate healthTemplate = new PubSubHealthTemplate(pubSubTemplate, null, 1000);
		healthTemplate.pullAndAckAsync();

		verify(pubSubTemplate).pullAsync(anyString(), eq(1), eq(true));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPullAsync_shouldAckMessages() throws Exception {
		ListenableFuture<List<AcknowledgeablePubsubMessage>> future = mock(ListenableFuture.class);
		AcknowledgeablePubsubMessage msg = mock(AcknowledgeablePubsubMessage.class);

		when(future.get(anyLong(), any())).thenReturn(List.of(msg));
		when(pubSubTemplate.pullAsync(anyString(), anyInt(), anyBoolean())).thenReturn(future);
		PubSubHealthTemplate healthTemplate = new PubSubHealthTemplate(pubSubTemplate, "test-subscription", 1000);
		healthTemplate.pullAndAckAsync();

		verify(msg).ack();
	}

	@ParameterizedTest
	@ValueSource(strings = {"NOT_FOUND", "PERMISSION_DENIED"})
	public void testExpectedException_userSubscriptionSpecified_shouldReturnFalse(String code) throws Exception {
		PubSubHealthTemplate healthTemplate = new PubSubHealthTemplate(pubSubTemplate, "test-subscription", 1000);
		Exception e = new ApiException(new IllegalStateException("Illegal State"), GrpcStatusCode.of(io.grpc.Status.Code.valueOf(code)), false);
		assertThat(healthTemplate.isExpectedExecutionException(new ExecutionException(e))).isFalse();
	}

	@ParameterizedTest
	@ValueSource(strings = {"NOT_FOUND", "PERMISSION_DENIED"})
	public void testExpectedException_userSubscriptionNotSpecified_shouldReturnTrue(String code) throws Exception {
		PubSubHealthTemplate healthTemplate = new PubSubHealthTemplate(pubSubTemplate, null, 1000);
		Exception e = new ApiException(new IllegalStateException("Illegal State"), GrpcStatusCode.of(io.grpc.Status.Code.valueOf(code)), false);
		assertThat(healthTemplate.isExpectedExecutionException(new ExecutionException(e))).isTrue();
	}
}
