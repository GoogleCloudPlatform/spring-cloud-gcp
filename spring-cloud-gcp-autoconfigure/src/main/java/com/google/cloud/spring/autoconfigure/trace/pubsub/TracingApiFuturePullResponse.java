package com.google.cloud.spring.autoconfigure.trace.pubsub;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.api.core.ApiFuture;
import com.google.pubsub.v1.PullResponse;

public class TracingApiFuturePullResponse implements ApiFuture<PullResponse> {
	private final ApiFuture<PullResponse> delegate;

	private final String subscriptionName;

	private final PubSubTracing pubSubTracing;

	public TracingApiFuturePullResponse(ApiFuture<PullResponse> delegate, PubSubTracing pubSubTracing) {
		this.delegate = delegate;
		this.subscriptionName = null; // TODO: set subscription name
		this.pubSubTracing = pubSubTracing;
	}

	@Override
	public PullResponse get() throws InterruptedException, ExecutionException {
		return pubSubTracing.tracePullResponse(delegate.get(), subscriptionName);
	}

	@Override
	public PullResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
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
