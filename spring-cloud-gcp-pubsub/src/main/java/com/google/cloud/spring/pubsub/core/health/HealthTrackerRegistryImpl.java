/*
 * Copyright 2017-2021 the original author or authors.
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
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;

/**
 * @author Emmanouil Gkatziouras
 *
 * @since 2.0.1
 */
public class HealthTrackerRegistryImpl implements HealthTrackerRegistry {

	private static final Log LOGGER = LogFactory.getLog(HealthTrackerRegistryImpl.class);

	private final MetricServiceClient metricServiceClient;
	private final Integer lagThreshold;
	private final Integer backlogThreshold;
	private final ExecutorProvider executorProvider;

	private final ConcurrentHashMap<ProjectSubscriptionName, HealthTracker> healthTrackers;

	private static final String SUBSCRIPTION_FAILED_MESSAGE = "Subscription backlog reached threshold. Check your process and if scaling is needed.";

	public HealthTrackerRegistryImpl(
		MetricServiceClient metricServiceClient,
		Integer lagThreshold,
		Integer backlogThreshold,
		ExecutorProvider executorProvider) {
		this(metricServiceClient, lagThreshold, backlogThreshold, executorProvider, new ConcurrentHashMap<>());
	}

	public HealthTrackerRegistryImpl(
		MetricServiceClient metricServiceClient,
		Integer lagThreshold,
		Integer backlogThreshold,
		ExecutorProvider executorProvider,
		ConcurrentHashMap<ProjectSubscriptionName, HealthTracker> healthTrackers) {
		Assert.notNull(metricServiceClient, "MetricServiceClient can't be null");
		this.metricServiceClient = metricServiceClient;
		this.lagThreshold = lagThreshold;
		this.backlogThreshold = backlogThreshold;
		this.healthTrackers = healthTrackers;
		this.executorProvider = executorProvider;
	}

	@Override
	public HealthTracker registerTracker(ProjectSubscriptionName projectSubscriptionName) {
		HealthTracker healthTracker = new HealthTrackerImpl(projectSubscriptionName,
			metricServiceClient, lagThreshold, backlogThreshold);
		healthTrackers.put(projectSubscriptionName, healthTracker);
		return healthTracker;
	}

	@Override
	public boolean isTracked(ProjectSubscriptionName projectSubscriptionName) {
		return healthTrackers.containsKey(projectSubscriptionName);
	}

	@Override
	public void processedMessage(ProjectSubscriptionName subscription) {
		if (!healthTrackers.containsKey(subscription)) {
			LOGGER.warn("No HealthTracker register for subscription " + subscription.toString());
			return;
		}

		healthTrackers.get(subscription).processedMessage();
	}

	@Override
	public void addListener(Subscriber subscriber) {
		ProjectSubscriptionName subscription = ProjectSubscriptionName.parse(subscriber.getSubscriptionNameString());

		if (!healthTrackers.containsKey(subscription)) {
			LOGGER.warn("No HealthTracker register for subscription " + subscription.toString() + " ,will not register listener");
			return;
		}


		ApiService.Listener listener = new ApiService.Listener() {
			@Override
			public void terminated(State from) {
				healthTrackers.remove(subscription);
			}
		};

		subscriber.addListener(listener, executorProvider.getExecutor());
	}

	public Collection<HealthTracker> healthTrackers() {
		return healthTrackers.values();
	}

}
