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
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;

final class TracingPullCallable extends UnaryCallable<PullRequest, PullResponse> {
  private final UnaryCallable<PullRequest, PullResponse> delegate;

  private final PubSubTracing pubSubTracing;

  TracingPullCallable(
      UnaryCallable<PullRequest, PullResponse> delegate, PubSubTracing pubSubTracing) {
    this.delegate = delegate;
    this.pubSubTracing = pubSubTracing;
  }

  @Override
  public ApiFuture<PullResponse> futureCall(
      PullRequest pullRequest, ApiCallContext apiCallContext) {
    return new TracingApiFuturePullResponse(
        delegate.futureCall(pullRequest, apiCallContext),
        pubSubTracing,
        pullRequest.getSubscription());
  }

  @Override
  public ApiFuture<PullResponse> futureCall(PullRequest request) {
    return new TracingApiFuturePullResponse(
        delegate.futureCall(request), pubSubTracing, request.getSubscription());
  }

  @Override
  public PullResponse call(PullRequest request, ApiCallContext context) {
    return pubSubTracing.tracePullResponse(
        delegate.call(request, context), request.getSubscription());
  }

  @Override
  public PullResponse call(PullRequest request) {
    return pubSubTracing.tracePullResponse(delegate.call(request), request.getSubscription());
  }

  @Override
  public UnaryCallable<PullRequest, PullResponse> withDefaultCallContext(
      ApiCallContext defaultCallContext) {
    return delegate.withDefaultCallContext(defaultCallContext);
  }
}
