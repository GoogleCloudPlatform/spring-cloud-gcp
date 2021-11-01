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


import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.monitoring.v3.ListTimeSeriesResponse;
import com.google.monitoring.v3.ProjectName;
import com.google.monitoring.v3.TimeInterval;
import com.google.monitoring.v3.TimeSeries;
import com.google.protobuf.util.Timestamps;
import com.google.pubsub.v1.ProjectSubscriptionName;

import static com.google.monitoring.v3.ListTimeSeriesRequest.TimeSeriesView.FULL;

/**
 * @author Emmanouil Gkatziouras
 * @since 2.0.6
 */
public class HealthTrackerImpl implements HealthTracker {

	private static final int SECOND_IN_MILLIS = 1000;
	private static final long NO_MESSAGES_OVER_THRESHOLD = 0L;
	private final ProjectSubscriptionName projectSubscriptionName;
	private final MetricServiceClient metricServiceClient;
	private final String undeliveredFilter;
	private final Integer lagThreshold;
	private final Integer backlogThreshold;
	private final Integer lookUpInternal;

	private final AtomicLong processedAt = new AtomicLong(System.currentTimeMillis());

	public HealthTrackerImpl(ProjectSubscriptionName projectSubscriptionName,
		MetricServiceClient metricServiceClient,
		Integer lagThreshold,
		Integer backlogThreshold,
		Integer lookUpInternal) {
		this.projectSubscriptionName = projectSubscriptionName;
		this.metricServiceClient = metricServiceClient;
		this.undeliveredFilter = undeliveredFilter(projectSubscriptionName.getSubscription());
		this.lagThreshold = lagThreshold;
		this.backlogThreshold = backlogThreshold;
		this.lookUpInternal = lookUpInternal;
	}

	@Override
	public void processedMessage() {
		long newValue = System.currentTimeMillis();
		long oldValue = processedAt.longValue();

		while (oldValue < newValue) {
			if (processedAt.compareAndSet(oldValue, newValue)) {
				return;
			}
			else {
				oldValue = processedAt.longValue();
			}
		}
	}

	@Override
	public ProjectSubscriptionName subscription() {
		return projectSubscriptionName;
	}

	@Override
	public long messagesOverThreshold() {
		long currentMillis = System.currentTimeMillis();

		if (hasNotExceedTimeThreshold(currentMillis)) {
			return NO_MESSAGES_OVER_THRESHOLD;
		}

		Optional<Long> messagesOpt = getBackLogMessages(currentMillis);

		if (!messagesOpt.isPresent()) {
			return NO_MESSAGES_OVER_THRESHOLD;
		}

		long messageBackLog = messagesOpt.get();

		return messageBackLog - backlogThreshold;
	}

	private boolean hasNotExceedTimeThreshold(long currentMillis) {
		long elapsedMillis = currentMillis - processedAt.longValue();
		long elapsedSeconds = elapsedMillis / SECOND_IN_MILLIS;
		return elapsedSeconds < lagThreshold;
	}

	private Optional<Long> getBackLogMessages(long currentMillis) {
		TimeInterval timeInterval = timeInterval(currentMillis);

		ListTimeSeriesResponse timeSeriesResponse = metricServiceClient.listTimeSeries(
			ProjectName.of(projectSubscriptionName.getProject()),
			undeliveredFilter,
			timeInterval,
			FULL
		).getPage().getResponse();

		if (timeSeriesResponse.getTimeSeriesCount() > 0) {
			TimeSeries timeSeries = timeSeriesResponse.getTimeSeries(0);

			if (timeSeries.getPointsCount() > 0) {
				return Optional.of(timeSeries.getPoints(0).getValue().getInt64Value());
			}
		}

		return Optional.empty();
	}

	private TimeInterval timeInterval(long currentMillis) {
		TimeInterval timeInterval;

		timeInterval = TimeInterval.newBuilder()
			.setStartTime(Timestamps.fromMillis(currentMillis - lookUpInternal * 60 * 1000))
			.setEndTime(Timestamps.fromMillis(currentMillis))
			.build();

		return timeInterval;
	}

}
