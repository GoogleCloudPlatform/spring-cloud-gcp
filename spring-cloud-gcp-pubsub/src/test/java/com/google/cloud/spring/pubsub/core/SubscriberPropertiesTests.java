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
 * Tests for {@link SubscriberProperties}
 */
public class SubscriberPropertiesTests {

	@Test
	public void testDefaultSubscriberProperties() {
		SubscriberProperties subscriberProperties = new SubscriberProperties();
		SubscriberProperties.Subscriber subscriber = subscriberProperties.getSubscriber();
		FlowControl flowControl = subscriber.getFlowControl();
		Retry retrySettings = subscriber.getRetry();

		assertThat(subscriber.getExecutorThreads()).isEqualTo(4);
		assertThat(subscriber.getMaxAcknowledgementThreads()).isEqualTo(4);
		assertThat(subscriber.getParallelPullCount()).isNull();
		assertThat(subscriber.getMaxAckExtensionPeriod()).isZero();
		assertThat(subscriber.getPullEndpoint()).isNull();
		assertThat(flowControl.getLimitExceededBehavior())
				.isNull();
		assertThat(flowControl.getMaxOutstandingElementCount()).isNull();
		assertThat(flowControl.getMaxOutstandingRequestBytes()).isNull();
		assertThat(retrySettings.getTotalTimeoutSeconds()).isNull();
		assertThat(retrySettings.getInitialRetryDelaySeconds()).isNull();
		assertThat(retrySettings.getRetryDelayMultiplier()).isNull();
		assertThat(retrySettings.getMaxRetryDelaySeconds()).isNull();
		assertThat(retrySettings.getMaxAttempts()).isNull();
		assertThat(retrySettings.getJittered()).isNull();
		assertThat(retrySettings.getInitialRpcTimeoutSeconds()).isNull();
		assertThat(retrySettings.getRpcTimeoutMultiplier()).isNull();
		assertThat(retrySettings.getMaxRpcTimeoutSeconds()).isNull();
	}

	@Test
	public void testSubscriberProperties() {
		SubscriberProperties subscriberProperties = new SubscriberProperties();
		SubscriberProperties.Subscriber subscriber = subscriberProperties.getSubscriber();

		subscriber.setExecutorThreads(1);
		subscriber.setMaxAcknowledgementThreads(3);
		subscriber.setParallelPullCount(1);
		subscriber.setMaxAckExtensionPeriod(1L);
		subscriber.setPullEndpoint("fake-endpoint");

		assertThat(subscriber.getExecutorThreads()).isEqualTo(1);
		assertThat(subscriber.getMaxAcknowledgementThreads()).isEqualTo(3);
		assertThat(subscriber.getParallelPullCount()).isEqualTo(1);
		assertThat(subscriber.getMaxAckExtensionPeriod()).isEqualTo(1L);
		assertThat(subscriber.getPullEndpoint()).isEqualTo("fake-endpoint");
	}

	@Test
	public void testSubscriberFlowControlSettings() {
		SubscriberProperties subscriberProperties = new SubscriberProperties();
		SubscriberProperties.Subscriber subscriber = subscriberProperties.getSubscriber();
		FlowControl flowControl = subscriber.getFlowControl();

		flowControl.setLimitExceededBehavior(FlowController.LimitExceededBehavior.Ignore);
		flowControl.setMaxOutstandingElementCount(1L);
		flowControl.setMaxOutstandingRequestBytes(2L);

		assertThat(flowControl.getLimitExceededBehavior())
				.isEqualTo(FlowController.LimitExceededBehavior.Ignore);
		assertThat(flowControl.getMaxOutstandingElementCount()).isEqualTo(1L);
		assertThat(flowControl.getMaxOutstandingRequestBytes()).isEqualTo(2L);
	}

	@Test
	public void testSubscriberRetrySettings() {
		SubscriberProperties subscriberProperties = new SubscriberProperties();
		SubscriberProperties.Subscriber subscriber = subscriberProperties.getSubscriber();
		Retry retrySettings = subscriber.getRetry();

		retrySettings.setTotalTimeoutSeconds(10L);
		retrySettings.setInitialRetryDelaySeconds(15L);
		retrySettings.setRetryDelayMultiplier(12.0);
		retrySettings.setMaxRetryDelaySeconds(10L);
		retrySettings.setMaxAttempts(13);
		retrySettings.setJittered(true);
		retrySettings.setInitialRpcTimeoutSeconds(11L);
		retrySettings.setRpcTimeoutMultiplier(14.0);
		retrySettings.setMaxRpcTimeoutSeconds(9L);

		assertThat(retrySettings.getTotalTimeoutSeconds()).isEqualTo(10L);
		assertThat(retrySettings.getInitialRetryDelaySeconds()).isEqualTo(15L);
		assertThat(retrySettings.getRetryDelayMultiplier()).isEqualTo(12.0);
		assertThat(retrySettings.getMaxRetryDelaySeconds()).isEqualTo(10L);
		assertThat(retrySettings.getMaxAttempts()).isEqualTo(13);
		assertThat(retrySettings.getJittered()).isTrue();
		assertThat(retrySettings.getInitialRpcTimeoutSeconds()).isEqualTo(11L);
		assertThat(retrySettings.getRpcTimeoutMultiplier()).isEqualTo(14.0);
		assertThat(retrySettings.getMaxRpcTimeoutSeconds()).isEqualTo(9L);
	}
}
