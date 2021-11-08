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

package com.google.cloud.spring.pubsub.core.health;


import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.google.api.core.ApiService;
import com.google.api.core.ApiService.State;
import com.google.api.gax.core.ExecutorProvider;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HealthTrackerRegistryImplTests {

	@Mock
	private MetricServiceClient metricServiceClient;

	@Mock
	private ExecutorProvider executorProvider;

	@Captor
	private ArgumentCaptor<ApiService.Listener> captor;

	private HealthTrackerRegistry healthTrackerRegistry;

	private static final String DEFAULT_PROJECT_ID = "project-id";
	private static final int DEFAULT_LAG_THRESHOLD = 100;
	private static final int DEFAULT_BACKLOG_THRESHOLD = 100;
	private static final int MINUTE_INTERNAL = 1;

	private ConcurrentHashMap<ProjectSubscriptionName, HealthTracker> healthTrackers = new ConcurrentHashMap<>();

	@Before
	public void setUp() throws Exception {
		healthTrackerRegistry = new HealthTrackerRegistryImpl(DEFAULT_PROJECT_ID, metricServiceClient, DEFAULT_LAG_THRESHOLD, DEFAULT_BACKLOG_THRESHOLD, MINUTE_INTERNAL, executorProvider, healthTrackers);
		healthTrackers.clear();
	}

	@Test
	public void testRegisterTrackerGivenProjectSubscriptionName() {
		String projectId = "project-id";
		String subscriptionId = "subscription-id";

		ProjectSubscriptionName subscription = ProjectSubscriptionName.of(projectId, subscriptionId);

		HealthTracker healthTracker = healthTrackerRegistry.registerTracker(subscription);

		assertThat(healthTracker.subscription()).isEqualTo(subscription);
	}

	@Test
	public void testRegisterTrackerGivenSubscriptionName() {
		String projectId = "project-id";
		String subscriptionId = "subscription-id";

		ProjectSubscriptionName subscription = ProjectSubscriptionName.of(projectId, subscriptionId);

		HealthTracker healthTracker = healthTrackerRegistry.registerTracker(subscriptionId);

		assertThat(healthTracker.subscription()).isEqualTo(subscription);
	}

	@Test
	public void testHealthTrackers() {
		String projectId = "project-id";
		String subscriptionId = "subscription-id";

		ProjectSubscriptionName subscription = ProjectSubscriptionName.of(projectId, subscriptionId);

		HealthTracker healthTracker = healthTrackerRegistry.registerTracker(subscription);
		Collection<HealthTracker> healthTrackers = healthTrackerRegistry.healthTrackers();

		assertThat(healthTrackers).hasSize(1);
		assertThat(healthTrackers.iterator().next()).isEqualTo(healthTracker);
	}

	@Test
	public void testIsTracked() {
		String projectId = "project-id";
		String subscriptionId = "subscription-id";

		ProjectSubscriptionName subscription = ProjectSubscriptionName.of(projectId, subscriptionId);

		assertThat(healthTrackerRegistry.isTracked(subscription)).isFalse();

		HealthTracker healthTracker = healthTrackerRegistry.registerTracker(subscription);

		assertThat(healthTrackerRegistry.isTracked(subscription)).isTrue();
	}

	@Test
	public void testProcessedMessage() {
		String projectId = "project-id";
		String subscription = "bad-subscription";

		ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscription);
		HealthTracker healthTracker = mock(HealthTracker.class);

		healthTrackers.put(subscriptionName, healthTracker);

		healthTrackerRegistry.processedMessage(subscriptionName);

		verify(healthTracker, times(1)).processedMessage();
	}

	@Test
	public void testAddListener() {
		String projectId = "project-id";
		String subscriptionId = "subscription-id";

		ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

		HealthTracker healthTracker = healthTrackerRegistry.registerTracker(subscriptionName);
		Subscriber subscriber = mock(Subscriber.class);
		when(subscriber.getSubscriptionNameString()).thenReturn(subscriptionName.toString());

		healthTrackerRegistry.addListener(subscriber);

		verify(subscriber, times(1)).addListener(captor.capture(), any());

		assertThat(healthTrackers.containsKey(subscriptionName)).isTrue();
		assertThat(healthTrackers.get(subscriptionName)).isEqualTo(healthTracker);

		ApiService.Listener listener = captor.getValue();
		listener.terminated(State.FAILED);

		assertThat(healthTrackers.containsKey(subscriptionName)).isFalse();
	}


	@Test
	public void testAddListenerNoHealthTracker() {
		String projectId = "project-id";
		String subscriptionId = "non-registered-subscription-id";

		ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

		Subscriber subscriber = mock(Subscriber.class);
		when(subscriber.getSubscriptionNameString()).thenReturn(subscriptionName.toString());

		healthTrackerRegistry.addListener(subscriber);

		verify(subscriber, times(0)).addListener(any(), any());
	}

	@Test
	public void testWrap() {
		String projectId = "project-id";
		String subscriptionId = "subscription-id";

		ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

		MessageReceiver receiver = mock(MessageReceiver.class);

		healthTrackerRegistry.wrap(subscriptionName, receiver);

		assertThat(healthTrackers.containsKey(subscriptionName)).isTrue();
	}

	@Test
	public void testProcessedMessageNoHealthTracker() {
		String projectId = "project-id";
		String subscriptionId = "non-registered-subscription-id";

		ConcurrentHashMap<ProjectSubscriptionName, HealthTracker> healthTrackers = mock(ConcurrentHashMap.class);

		healthTrackerRegistry = new HealthTrackerRegistryImpl(DEFAULT_PROJECT_ID, metricServiceClient, DEFAULT_LAG_THRESHOLD, DEFAULT_BACKLOG_THRESHOLD, MINUTE_INTERNAL, executorProvider, healthTrackers);

		ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

		healthTrackerRegistry.processedMessage(subscriptionName);

		verify(healthTrackers).containsKey(subscriptionName);
	}

}
