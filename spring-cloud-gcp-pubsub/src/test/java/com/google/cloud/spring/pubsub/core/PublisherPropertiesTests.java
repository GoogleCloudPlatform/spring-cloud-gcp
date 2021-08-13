/*
 * Copyright 2021-2021 the original author or authors.
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

package com.google.cloud.spring.pubsub.core;

import com.google.api.gax.batching.FlowController;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PublisherProperties}
 */
public class PublisherPropertiesTests {

	@Test
	public void testDefaultPublisherProperties() {
		PublisherProperties pubsubProperties = new PublisherProperties();
		PublisherProperties.Publisher publisher = pubsubProperties.getPublisher();
		Batching batching = publisher.getBatching();
		Retry retrySettings = publisher.getRetry();

		assertThat(publisher.getExecutorThreads()).isEqualTo(4);
		assertThat(publisher.getEnableMessageOrdering()).isNull();
		assertThat(publisher.getEndpoint()).isNull();
		assertThat(batching.getElementCountThreshold()).isNull();
		assertThat(batching.getRequestByteThreshold()).isNull();
		assertThat(batching.getDelayThresholdSeconds()).isNull();
		assertThat(batching.getEnabled()).isNull();
		assertThat(batching.getFlowControl().getLimitExceededBehavior()).isNull();
		assertThat(batching.getFlowControl().getMaxOutstandingElementCount()).isNull();
		assertThat(batching.getFlowControl().getMaxOutstandingRequestBytes()).isNull();
		assertThat(retrySettings.getTotalTimeoutSeconds()).isNull();
		assertThat(retrySettings.getRetryDelayMultiplier()).isNull();
		assertThat(retrySettings.getMaxRetryDelaySeconds()).isNull();
		assertThat(retrySettings.getMaxAttempts()).isNull();
		assertThat(retrySettings.getJittered()).isNull();
		assertThat(retrySettings.getInitialRpcTimeoutSeconds()).isNull();
		assertThat(retrySettings.getRpcTimeoutMultiplier()).isNull();
		assertThat(retrySettings.getMaxRpcTimeoutSeconds()).isNull();
	}

	@Test
	public void testPublisherProperties() {
		PublisherProperties pubsubProperties = new PublisherProperties();
		PublisherProperties.Publisher publisher = pubsubProperties.getPublisher();

		publisher.setExecutorThreads(5);
		publisher.setEnableMessageOrdering(true);
		publisher.setEndpoint("fake-endpoint");

		assertThat(publisher.getExecutorThreads()).isEqualTo(5);
		assertThat(publisher.getEnableMessageOrdering()).isTrue();
		assertThat(publisher.getEndpoint()).isEqualTo("fake-endpoint");
	}

	@Test
	public void testPublisherBatchingSettings() {
		PublisherProperties pubsubProperties = new PublisherProperties();
		PublisherProperties.Publisher publisher = pubsubProperties.getPublisher();
		Batching batching = publisher.getBatching();

		batching.setElementCountThreshold(1L);
		batching.setRequestByteThreshold(5L);
		batching.setDelayThresholdSeconds(3L);
		batching.setEnabled(true);
		FlowControl flowControl = publisher.getBatching().getFlowControl();
		flowControl.setLimitExceededBehavior(FlowController.LimitExceededBehavior.Ignore);
		flowControl.setMaxOutstandingElementCount(6L);
		flowControl.setMaxOutstandingRequestBytes(3L);

		assertThat(batching.getElementCountThreshold()).isEqualTo(1L);
		assertThat(batching.getRequestByteThreshold()).isEqualTo(5L);
		assertThat(batching.getDelayThresholdSeconds()).isEqualTo(3L);
		assertThat(batching.getEnabled()).isTrue();
		assertThat(batching.getFlowControl().getLimitExceededBehavior())
				.isEqualTo(FlowController.LimitExceededBehavior.Ignore);
		assertThat(batching.getFlowControl().getMaxOutstandingElementCount()).isEqualTo(6L);
		assertThat(batching.getFlowControl().getMaxOutstandingRequestBytes()).isEqualTo(3L);
	}

	@Test
	public void testPublisherRetrySettings() {
		PublisherProperties pubsubProperties = new PublisherProperties();
		PublisherProperties.Publisher publisher = pubsubProperties.getPublisher();
		Retry retrySettings = publisher.getRetry();

		retrySettings.setTotalTimeoutSeconds(3L);
		retrySettings.setRetryDelayMultiplier(12.0);
		retrySettings.setMaxRetryDelaySeconds(2L);
		retrySettings.setMaxAttempts(3);
		retrySettings.setJittered(true);
		retrySettings.setInitialRpcTimeoutSeconds(6L);
		retrySettings.setRpcTimeoutMultiplier(12.0);
		retrySettings.setMaxRpcTimeoutSeconds(8L);

		assertThat(retrySettings.getTotalTimeoutSeconds()).isEqualTo(3L);
		assertThat(retrySettings.getRetryDelayMultiplier()).isEqualTo(12.0);
		assertThat(retrySettings.getMaxRetryDelaySeconds()).isEqualTo(2L);
		assertThat(retrySettings.getMaxAttempts()).isEqualTo(3);
		assertThat(retrySettings.getJittered()).isTrue();
		assertThat(retrySettings.getInitialRpcTimeoutSeconds()).isEqualTo(6L);
		assertThat(retrySettings.getRpcTimeoutMultiplier()).isEqualTo(12.0);
		assertThat(retrySettings.getMaxRpcTimeoutSeconds()).isEqualTo(8L);
	}
}
