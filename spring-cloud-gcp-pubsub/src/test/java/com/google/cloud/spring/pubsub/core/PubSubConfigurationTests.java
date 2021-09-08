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

public class PubSubConfigurationTests {

	@Test
	public void testDefaultSubscriberProperties() {
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Subscriber subscriber = pubSubConfiguration.getSubscriber();
		PubSubConfiguration.FlowControl flowControl = subscriber.getFlowControl();
		PubSubConfiguration.Retry retrySettings = subscriber.getRetry();

		assertThat(subscriber.getExecutorThreads()).isNull();
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
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Subscriber subscriber = pubSubConfiguration.getSubscriber();

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
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Subscriber subscriber = pubSubConfiguration.getSubscriber();
		PubSubConfiguration.FlowControl flowControl = subscriber.getFlowControl();

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
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Subscriber subscriber = pubSubConfiguration.getSubscriber();
		PubSubConfiguration.Retry retrySettings = subscriber.getRetry();

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

	@Test
	public void testSubscriberMapProperties_defaultOrGlobal_addToMap() {
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();

		assertThat(pubSubConfiguration.getSubscription()).isEmpty();
		assertThat(pubSubConfiguration.getSubscriber("subscription-name", "projectId").getExecutorThreads())
				.isNull();
		assertThat(pubSubConfiguration.getSubscription()).hasSize(1);
		assertThat(pubSubConfiguration.getSubscription().get("projects/projectId/subscriptions/subscription-name")
				.getExecutorThreads()).isNull();
	}

	@Test
	public void testSubscriberMapProperties_subscriptionName_returnCustom() {
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Subscriber subscriber = new PubSubConfiguration.Subscriber();
		subscriber.setExecutorThreads(8);

		pubSubConfiguration.getSubscription().put("subscription-name", subscriber);

		assertThat(pubSubConfiguration.getSubscription()).hasSize(1);
		assertThat(pubSubConfiguration.getSubscriber("subscription-name", "projectId").getExecutorThreads())
				.isEqualTo(8);
		assertThat(pubSubConfiguration.getSubscription()).hasSize(1);
		assertThat(pubSubConfiguration.getSubscription().get("projects/projectId/subscriptions/subscription-name")
				.getExecutorThreads()).isEqualTo(8);
	}

	@Test
	public void testSubscriberMapProperties_fullNamePresentInMap_returnCustom() {
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Subscriber subscriber = new PubSubConfiguration.Subscriber();
		subscriber.setExecutorThreads(8);

		pubSubConfiguration.getSubscription().put("projects/projectId/subscriptions/subscription-name", subscriber);

		assertThat(pubSubConfiguration.getSubscription()).hasSize(1);
		assertThat(pubSubConfiguration.getSubscriber("projects/projectId/subscriptions/subscription-name", "projectId")
				.getExecutorThreads()).isEqualTo(8);
		assertThat(pubSubConfiguration.getSubscription().get("projects/projectId/subscriptions/subscription-name")
				.getExecutorThreads()).isEqualTo(8);
	}

	@Test
	public void testSubscriberMapProperties_fullNamePresentInMap_projectIdIgnored_returnCustom() {
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Subscriber subscriber = new PubSubConfiguration.Subscriber();
		subscriber.setExecutorThreads(8);

		pubSubConfiguration.getSubscription().put("projects/otherProjectId/subscriptions/subscription-name",
				subscriber);

		assertThat(pubSubConfiguration.getSubscription()).hasSize(1);
		assertThat(pubSubConfiguration
				.getSubscriber("projects/otherProjectId/subscriptions/subscription-name", "projectId")
				.getExecutorThreads()).isEqualTo(8);
		assertThat(pubSubConfiguration.getSubscription().get("projects/otherProjectId/subscriptions/subscription-name")
				.getExecutorThreads()).isEqualTo(8);
	}

	@Test
	public void testComputeExecutorThreads_fullNameWithDifferentProjectId_returnCustom() {
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Subscriber subscriber = new PubSubConfiguration.Subscriber();
		subscriber.setExecutorThreads(8);

		pubSubConfiguration.getSubscription().put("projects/otherProjectId/subscriptions/subscription-name",
				subscriber);

		assertThat(pubSubConfiguration.getSubscription()).hasSize(1);
		assertThat(pubSubConfiguration
				.computeSubscriberExecutorThreads("projects/otherProjectId/subscriptions/subscription-name",
						"projectId")).isEqualTo(8);
	}

	@Test
	public void testComputeExecutorThreads_fullNameWithDifferentProjectId_returnDefault() {
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();

		assertThat(pubSubConfiguration.getSubscription()).isEmpty();
		assertThat(pubSubConfiguration
				.computeSubscriberExecutorThreads("projects/otherProjectId/subscriptions/subscription-name",
						"projectId")).isEqualTo(4);
	}

	@Test
	public void testSubscriberMapProperties_isGlobalIfAbsentFromMap() {
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();

		assertThat(pubSubConfiguration.getSubscription()).doesNotContainKey("subscription-name");
		assertThat(pubSubConfiguration.getSubscriber("subscription-name", "projectId").isGlobal()).isTrue();
	}

	@Test
	public void testSubscriberMapProperties_isNotGlobalIfPresentInMap() {
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		pubSubConfiguration.getSubscription().put("subscription-name", new PubSubConfiguration.Subscriber());

		assertThat(pubSubConfiguration.getSubscription()).containsKey("subscription-name");
		assertThat(pubSubConfiguration.getSubscription()).hasSize(1);
		assertThat(pubSubConfiguration.getSubscriber("subscription-name", "projectId").isGlobal()).isFalse();
	}

	@Test
	public void testDefaultPublisherProperties() {
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Publisher publisher = pubSubConfiguration.getPublisher();
		PubSubConfiguration.Batching batching = publisher.getBatching();
		PubSubConfiguration.Retry retrySettings = publisher.getRetry();

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
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Publisher publisher = pubSubConfiguration.getPublisher();

		publisher.setExecutorThreads(5);
		publisher.setEnableMessageOrdering(true);
		publisher.setEndpoint("fake-endpoint");

		assertThat(publisher.getExecutorThreads()).isEqualTo(5);
		assertThat(publisher.getEnableMessageOrdering()).isTrue();
		assertThat(publisher.getEndpoint()).isEqualTo("fake-endpoint");
	}

	@Test
	public void testPublisherBatchingSettings() {
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Publisher publisher = pubSubConfiguration.getPublisher();
		PubSubConfiguration.Batching batching = publisher.getBatching();

		batching.setElementCountThreshold(1L);
		batching.setRequestByteThreshold(5L);
		batching.setDelayThresholdSeconds(3L);
		batching.setEnabled(true);
		PubSubConfiguration.FlowControl flowControl = publisher.getBatching().getFlowControl();
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
		PubSubConfiguration pubSubConfiguration = new PubSubConfiguration();
		PubSubConfiguration.Publisher publisher = pubSubConfiguration.getPublisher();
		PubSubConfiguration.Retry retrySettings = publisher.getRetry();

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
