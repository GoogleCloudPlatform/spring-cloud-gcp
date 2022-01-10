/*
 * Copyright 2017-2020 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.trace.pubsub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFuture;
import com.google.pubsub.v1.PullResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;

class TracingApiFuturePullResponseTest {

  static final String TEST_SUBSCRIPTION = "testSubscription";

  PubSubTracing mockPubSubTracing = mock(PubSubTracing.class);

  ApiFuture<PullResponse> mockDelegate = mock(ApiFuture.class);

  PullResponse mockPullResponse = mock(PullResponse.class);

  PullResponse mockWrappedPullResponse = mock(PullResponse.class);

  TracingApiFuturePullResponse tracingApiFuturePullResponse =
      new TracingApiFuturePullResponse(mockDelegate, mockPubSubTracing, TEST_SUBSCRIPTION);

  @Test
  void test_get() throws ExecutionException, InterruptedException {
    when(mockDelegate.get()).thenReturn(mockPullResponse);
    when(mockPubSubTracing.tracePullResponse(mockPullResponse, TEST_SUBSCRIPTION))
        .thenReturn(mockWrappedPullResponse);

    assertThat(tracingApiFuturePullResponse.get()).isEqualTo(mockWrappedPullResponse);
    verify(mockDelegate, times(1)).get();
    verify(mockPubSubTracing, times(1)).tracePullResponse(mockPullResponse, TEST_SUBSCRIPTION);
  }

  @Test
  void test_get_withTimeout() throws ExecutionException, InterruptedException, TimeoutException {
    when(mockDelegate.get(1L, TimeUnit.MINUTES)).thenReturn(mockPullResponse);
    when(mockPubSubTracing.tracePullResponse(mockPullResponse, TEST_SUBSCRIPTION))
        .thenReturn(mockWrappedPullResponse);

    assertThat(tracingApiFuturePullResponse.get(1L, TimeUnit.MINUTES))
        .isEqualTo(mockWrappedPullResponse);
    verify(mockDelegate, times(1)).get(1L, TimeUnit.MINUTES);
    verify(mockPubSubTracing, times(1)).tracePullResponse(mockPullResponse, TEST_SUBSCRIPTION);
  }

  @Test
  void test_addListener() {
    Runnable mockRunnable = mock(Runnable.class);
    Executor mockExecutor = mock(Executor.class);

    tracingApiFuturePullResponse.addListener(mockRunnable, mockExecutor);
    verify(mockDelegate, times(1)).addListener(mockRunnable, mockExecutor);
  }

  @Test
  void test_cancel() {
    tracingApiFuturePullResponse.cancel(true);
    verify(mockDelegate, times(1)).cancel(true);
  }

  @Test
  void test_isCancelled() {
    tracingApiFuturePullResponse.isCancelled();
    verify(mockDelegate, times(1)).isCancelled();
  }

  @Test
  void test_isDone() {
    tracingApiFuturePullResponse.isDone();
    verify(mockDelegate, times(1)).isDone();
  }
}
