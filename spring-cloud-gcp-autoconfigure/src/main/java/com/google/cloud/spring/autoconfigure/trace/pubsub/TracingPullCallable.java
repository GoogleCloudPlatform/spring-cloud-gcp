package com.google.cloud.spring.autoconfigure.trace.pubsub;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;

public class TracingPullCallable extends UnaryCallable<PullRequest, PullResponse> {
	private final UnaryCallable<PullRequest, PullResponse> delegate;

	private final String subscriptionName;

	private final PubSubTracing pubSubTracing;

	public TracingPullCallable(UnaryCallable<PullRequest, PullResponse> delegate, PubSubTracing pubSubTracing) {
		this.delegate = delegate;
		this.subscriptionName = null; // TODO: fill in the subscription name
		this.pubSubTracing = pubSubTracing;
	}

	@Override
	public ApiFuture<PullResponse> futureCall(PullRequest pullRequest, ApiCallContext apiCallContext) {
		return new TracingApiFuturePullResponse(delegate.futureCall(pullRequest, apiCallContext), pubSubTracing);
	}

	@Override
	public ApiFuture<PullResponse> futureCall(PullRequest request) {
		return new TracingApiFuturePullResponse(delegate.futureCall(request), pubSubTracing);
	}

	@Override
	public PullResponse call(PullRequest request, ApiCallContext context) {
		return pubSubTracing.tracePullResponse(delegate.call(request, context), subscriptionName);
	}

	@Override
	public PullResponse call(PullRequest request) {
		return pubSubTracing.tracePullResponse(delegate.call(request), subscriptionName);
	}

	@Override
	public UnaryCallable<PullRequest, PullResponse> withDefaultCallContext(ApiCallContext defaultCallContext) {
		return delegate.withDefaultCallContext(defaultCallContext);
	}
}
