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

import com.google.api.core.ApiFuture;
import com.google.pubsub.v1.PullResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

final class TracingApiFuturePullResponse implements ApiFuture<PullResponse> {
  private final ApiFuture<PullResponse> delegate;

  private final String subscriptionName;

  private final PubSubTracing pubSubTracing;

  TracingApiFuturePullResponse(
      ApiFuture<PullResponse> delegate, PubSubTracing pubSubTracing, String subscriptionName) {
    this.delegate = delegate;
    this.subscriptionName = subscriptionName;
    this.pubSubTracing = pubSubTracing;
  }

  @Override
  public PullResponse get() throws InterruptedException, ExecutionException {
    return pubSubTracing.tracePullResponse(delegate.get(), subscriptionName);
  }

  @Override
  public PullResponse get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return pubSubTracing.tracePullResponse(delegate.get(timeout, unit), subscriptionName);
  }

  // Simply delegated methods below...

  @Override
  public void addListener(Runnable runnable, Executor executor) {
    delegate.addListener(runnable, executor);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return delegate.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return delegate.isCancelled();
  }

  @Override
  public boolean isDone() {
    return delegate.isDone();
  }
}
