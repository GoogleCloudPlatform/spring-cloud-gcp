/*
 * Copyright 2017-2018 the original author or authors.
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


import java.util.concurrent.ConcurrentHashMap;

import com.google.api.gax.core.ExecutorProvider;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.cloud.spring.pubsub.core.health.HealthTracker;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistryImpl;
import com.google.pubsub.v1.ProjectSubscriptionName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PubSubSubscriptionHealthIndicatorTests {

	@Mock
	private MetricServiceClient metricServiceClient;

	private PubSubSubscriptionHealthIndicator healthIndicator;

	private static final int DEFAULT_LAG_THRESHOLD = 100;
	private static final int DEFAULT_BACKLOG_THRESHOLD = 100;

	private ConcurrentHashMap<ProjectSubscriptionName, HealthTracker> healthTrackers = new ConcurrentHashMap<>();

	@Before
	public void setUp() throws Exception {
		ExecutorProvider executorProvider = mock(ExecutorProvider.class);
		HealthTrackerRegistry trackerRegistry = new HealthTrackerRegistryImpl(metricServiceClient, DEFAULT_LAG_THRESHOLD, DEFAULT_BACKLOG_THRESHOLD, executorProvider, healthTrackers);
		healthIndicator = new PubSubSubscriptionHealthIndicator(trackerRegistry);
		healthTrackers.clear();
	}

	@Test
	public void testHealthCheckFailure() throws Exception {
		ProjectSubscriptionName goodSubscription = ProjectSubscriptionName.of("project", "good-subscription");
		HealthTracker goodTracker = mock(HealthTracker.class);
		when(goodTracker.messagesOverThreshold()).thenReturn(0L);
		healthTrackers.put(goodSubscription, goodTracker);

		ProjectSubscriptionName badSubscription = ProjectSubscriptionName.of("project", "bad-subscription");
		HealthTracker badTracker = mock(HealthTracker.class);
		long messagesInBacklog = 1000L;
		when(badTracker.messagesOverThreshold()).thenReturn(messagesInBacklog);
		when(badTracker.subscription()).thenReturn(badSubscription);

		healthTrackers.put(badSubscription, badTracker);

		Health.Builder builder = new Health.Builder();
		healthIndicator.doHealthCheck(builder);

		Health health = builder.build();
		assertThat(health.getStatus()).isEqualTo(Status.DOWN);
		assertThat(health.getDetails().get(badSubscription.toString())).isEqualTo(messagesInBacklog);
	}

	@Test
	public void testHealthCheckSucceeded() throws Exception {
		ProjectSubscriptionName key = ProjectSubscriptionName.of("project", "good-subscription");
		HealthTracker healthTracker = mock(HealthTracker.class);
		when(healthTracker.messagesOverThreshold()).thenReturn(0L);

		healthTrackers.put(key, healthTracker);

		Health.Builder builder = new Health.Builder();
		healthIndicator.doHealthCheck(builder);

		Health health = builder.build();
		assertThat(health.getStatus()).isEqualTo(Status.UP);
	}

}
