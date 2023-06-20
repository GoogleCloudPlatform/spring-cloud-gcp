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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.cloud.monitoring.v3.MetricServiceClient.ListTimeSeriesPage;
import com.google.cloud.monitoring.v3.MetricServiceClient.ListTimeSeriesPagedResponse;
import com.google.monitoring.v3.ListTimeSeriesResponse;
import com.google.monitoring.v3.Point;
import com.google.monitoring.v3.ProjectName;
import com.google.monitoring.v3.TimeSeries;
import com.google.monitoring.v3.TypedValue;
import com.google.pubsub.v1.ProjectSubscriptionName;
import java.util.concurrent.locks.LockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for HealthTrackerImpl. */
@ExtendWith(MockitoExtension.class)
class HealthTrackerImplTests {

  private static final String PROJECT_ID = "project-id";
  private static final String SUBSCRIPTION_ID = "subscription-id";
  private static final int LAG_THRESHOLD = 1;
  private static final int BACKLOG_THRESHOLD = 200;
  private static final int MINUTE_INTERNAL = 1;

  @Mock private MetricServiceClient metricServiceClient;

  private HealthTracker healthTracker;

  @BeforeEach
  void setUp() throws Exception {
    metricServiceClient = mock(MetricServiceClient.class);
    healthTracker =
        new HealthTrackerImpl(
            ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_ID),
            metricServiceClient,
            LAG_THRESHOLD,
            BACKLOG_THRESHOLD,
            MINUTE_INTERNAL);
  }

  @Test
  void getSubscription() {
    healthTracker.subscription();
    assertThat(healthTracker.subscription())
        .isEqualTo(ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_ID));
  }

  @Test
  void noMessagesOverThresholdAfterProcessing() {
    healthTracker.processedMessage();
    long messagesOverThreshold = healthTracker.messagesOverThreshold();
    assertThat(messagesOverThreshold).isZero();
  }

  @Test
  void noMessagesOverThresholdNoProcessing() throws InterruptedException {
    LockSupport.parkNanos(1001 * 1000000);
    ListTimeSeriesPagedResponse listTimeSeriesPagedResponse =
        mock(ListTimeSeriesPagedResponse.class);

    TimeSeries timeSeries =
        TimeSeries.newBuilder()
            .addPoints(
                Point.newBuilder()
                    .setValue(TypedValue.newBuilder().setInt64Value(100).build())
                    .build())
            .build();

    ListTimeSeriesResponse timeSeriesResponse =
        ListTimeSeriesResponse.newBuilder().addTimeSeries(timeSeries).build();

    ListTimeSeriesPage listTimeSeriesPage = mock(ListTimeSeriesPage.class);
    when(listTimeSeriesPagedResponse.getPage()).thenReturn(listTimeSeriesPage);
    when(listTimeSeriesPage.getResponse()).thenReturn(timeSeriesResponse);
    doReturn(listTimeSeriesPagedResponse)
        .when(metricServiceClient)
        .listTimeSeries(any(ProjectName.class), anyString(), any(), any());

    long messagesOverThreshold = healthTracker.messagesOverThreshold();
    assertThat(messagesOverThreshold).isNegative();
  }

  @Test
  void noMessagesOverThresholdNoProcessingNoBacklogMessages() throws InterruptedException {
    LockSupport.parkNanos(1001 * 1000000);
    ListTimeSeriesPagedResponse listTimeSeriesPagedResponse =
        mock(ListTimeSeriesPagedResponse.class);

    ListTimeSeriesResponse timeSeriesResponse = ListTimeSeriesResponse.newBuilder().build();

    ListTimeSeriesPage listTimeSeriesPage = mock(ListTimeSeriesPage.class);
    when(listTimeSeriesPagedResponse.getPage()).thenReturn(listTimeSeriesPage);
    when(listTimeSeriesPage.getResponse()).thenReturn(timeSeriesResponse);
    doReturn(listTimeSeriesPagedResponse)
        .when(metricServiceClient)
        .listTimeSeries(any(ProjectName.class), anyString(), any(), any());

    long messagesOverThreshold = healthTracker.messagesOverThreshold();
    assertThat(messagesOverThreshold).isZero();
  }

  @Test
  void messagesOverThresholdNoProcessing() throws InterruptedException {
    LockSupport.parkNanos(1001 * 1000000);
    ListTimeSeriesPagedResponse listTimeSeriesPagedResponse =
        mock(ListTimeSeriesPagedResponse.class);

    TimeSeries timeSeries =
        TimeSeries.newBuilder()
            .addPoints(
                Point.newBuilder()
                    .setValue(TypedValue.newBuilder().setInt64Value(201).build())
                    .build())
            .build();

    ListTimeSeriesResponse timeSeriesResponse =
        ListTimeSeriesResponse.newBuilder().addTimeSeries(timeSeries).build();

    ListTimeSeriesPage listTimeSeriesPage = mock(ListTimeSeriesPage.class);
    when(listTimeSeriesPagedResponse.getPage()).thenReturn(listTimeSeriesPage);
    when(listTimeSeriesPage.getResponse()).thenReturn(timeSeriesResponse);
    doReturn(listTimeSeriesPagedResponse)
        .when(metricServiceClient)
        .listTimeSeries(any(ProjectName.class), anyString(), any(), any());

    long messagesOverThreshold = healthTracker.messagesOverThreshold();
    assertThat(messagesOverThreshold).isEqualTo(1);
  }
}
