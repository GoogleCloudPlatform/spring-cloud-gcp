/*
 * Copyright 2021-2021 the original author or authors.
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

package com.google.cloud.spring.pubsub.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.api.gax.batching.FlowController.LimitExceededBehavior;
import com.google.cloud.spring.pubsub.support.PubSubSubscriptionUtils;
import com.google.pubsub.v1.ProjectSubscriptionName;

/**
 * Properties for Publisher or Subscriber specific configurations.
 *
 * @author João André Martins
 * @author Mike Eltsufin
 * @author Chengyuan Zhao
 */
public class PubSubConfiguration {

	/**
	 * Default number of executor threads.
	 */
	public static final int DEFAULT_EXECUTOR_THREADS = 4;

	private static final Long DEFAULT_MAX_ACK_EXTENSION_PERIOD = 0L;

	private final ConcurrentHashMap<String, Subscriber> subscription = new ConcurrentHashMap<>();

	/**
	 * Contains global and default subscriber settings.
	 */
	private final Subscriber globalSubscriber = new Subscriber();

	/**
	 * Contains default publisher settings.
	 */
	private final Publisher publisher = new Publisher();

	public Subscriber getSubscriber() {
		return this.globalSubscriber;
	}

	public Publisher getPublisher() {
		return this.publisher;
	}

	public ConcurrentMap<String, Subscriber> getSubscription() {
		return this.subscription;
	}

	public Subscriber getSubscriber(String name, String projectId) {
		ProjectSubscriptionName fullyQualifiedName = PubSubSubscriptionUtils.toProjectSubscriptionName(name, projectId);
		String fullyQualifiedSubscriptionKey = fullyQualifiedName.toString();

		if (this.subscription.containsKey(fullyQualifiedSubscriptionKey)) {
			return this.subscription.get(fullyQualifiedSubscriptionKey);
		}

		String subscriptionName = fullyQualifiedName.getSubscription();
		String projectIdFromFullName = fullyQualifiedName.getProject();

		// Check that subscription name is present in map and the current project Id matches
		// the one parsed from the fully qualified name
		if (this.subscription.containsKey(subscriptionName) && projectIdFromFullName.equals(projectId)) {
			this.subscription.putIfAbsent(fullyQualifiedSubscriptionKey, this.subscription.get(subscriptionName));
			this.subscription.remove(subscriptionName);
			return this.subscription.get(fullyQualifiedSubscriptionKey);
		}

		return this.subscription.computeIfAbsent(fullyQualifiedSubscriptionKey,
				k -> this.globalSubscriber);
	}

	/**
	 * Computes flow control settings to use. The subscription-specific property takes
	 * precedence if both global and subscription-specific properties are set. If
	 * subscription-specific settings are not set then global settings are picked.
	 * @param subscriptionName subscription name
	 * @param projectId project id
	 * @return flow control settings
	 */
	public FlowControl computeSubscriberFlowControlSettings(String subscriptionName, String projectId) {
		FlowControl flowControl = getSubscriber(subscriptionName, projectId).getFlowControl();
		FlowControl globalFlowControl = this.globalSubscriber.getFlowControl();
		if (flowControl.getMaxOutstandingRequestBytes() == null) {
			flowControl.setMaxOutstandingRequestBytes(globalFlowControl.getMaxOutstandingRequestBytes());
		}
		if (flowControl.getMaxOutstandingElementCount() == null) {
			flowControl.setMaxOutstandingElementCount(globalFlowControl.getMaxOutstandingElementCount());
		}
		if (flowControl.getLimitExceededBehavior() == null) {
			flowControl.setLimitExceededBehavior(globalFlowControl.getLimitExceededBehavior());
		}
		return flowControl;
	}

	/**
	 * Computes parallel pull count. The subscription-specific property takes precedence if
	 * both global and subscription-specific properties are set. If subscription-specific
	 * configuration is not set then the global configuration is picked.
	 * @param subscriptionName subscription name
	 * @param projectId project id
	 * @return parallel pull count
	 */
	public Integer computeParallelPullCount(String subscriptionName, String projectId) {
		Integer parallelPullCount = getSubscriber(subscriptionName, projectId).getParallelPullCount();
		return parallelPullCount != null ? parallelPullCount : this.globalSubscriber.getParallelPullCount();
	}

	/**
	 * Computes the max extension period. The subscription-specific property takes precedence
	 * if both global and subscription-specific properties are set. If none are set then the
	 * default (0) is returned.
	 * @param subscriptionName subscription name
	 * @param projectId project id
	 * @return max extension period
	 */
	public Long computeMaxAckExtensionPeriod(String subscriptionName, String projectId) {
		Long maxAckExtensionPeriod = getSubscriber(subscriptionName, projectId).getMaxAckExtensionPeriod();
		if (maxAckExtensionPeriod != null) {
			return maxAckExtensionPeriod;
		}
		Long globalMaxAckExtensionPeriod = this.globalSubscriber.getMaxAckExtensionPeriod();
		return globalMaxAckExtensionPeriod != null ? globalMaxAckExtensionPeriod : DEFAULT_MAX_ACK_EXTENSION_PERIOD;
	}

	/**
	 * Returns the pull endpoint. The subscription-specific property takes precedence if both
	 * global and subscription-specific properties are set. If subscription-specific
	 * configuration is not set then the global configuration is picked.
	 * @param subscriptionName subscription name
	 * @param projectId project id
	 * @return pull endpoint
	 */
	public String computePullEndpoint(String subscriptionName, String projectId) {
		String pullEndpoint = getSubscriber(subscriptionName, projectId).getPullEndpoint();
		return pullEndpoint != null ? pullEndpoint : this.globalSubscriber.getPullEndpoint();
	}

	/**
	 * Computes the retry settings. The subscription-specific property takes precedence if
	 * both global and subscription-specific properties are set. If subscription-specific
	 * settings are not set then the global settings are picked.
	 * @param subscriptionName subscription name
	 * @param projectId project id
	 * @return retry settings
	 */
	public Retry computeSubscriberRetrySettings(String subscriptionName, String projectId) {
		Retry retry = getSubscriber(subscriptionName, projectId).getRetry();
		Retry globalRetry = this.globalSubscriber.getRetry();
		if (retry.getTotalTimeoutSeconds() == null) {
			retry.setTotalTimeoutSeconds(globalRetry.getTotalTimeoutSeconds());
		}
		if (retry.getInitialRetryDelaySeconds() == null) {
			retry.setInitialRetryDelaySeconds(globalRetry.getInitialRetryDelaySeconds());
		}
		if (retry.getRetryDelayMultiplier() == null) {
			retry.setRetryDelayMultiplier(globalRetry.getRetryDelayMultiplier());
		}
		if (retry.getMaxRetryDelaySeconds() == null) {
			retry.setMaxRetryDelaySeconds(globalRetry.getMaxRetryDelaySeconds());
		}
		if (retry.getMaxAttempts() == null) {
			retry.setMaxAttempts(globalRetry.getMaxAttempts());
		}
		if (retry.getJittered() == null) {
			retry.setJittered(globalRetry.getJittered());
		}
		if (retry.getInitialRpcTimeoutSeconds() == null) {
			retry.setInitialRpcTimeoutSeconds(globalRetry.getInitialRpcTimeoutSeconds());
		}
		if (retry.getRpcTimeoutMultiplier() == null) {
			retry.setRpcTimeoutMultiplier(globalRetry.getRpcTimeoutMultiplier());
		}
		if (retry.getMaxRpcTimeoutSeconds() == null) {
			retry.setMaxRpcTimeoutSeconds(globalRetry.getMaxRpcTimeoutSeconds());
		}
		return retry;
	}

	/**
	 * Publisher settings.
	 */
	public static class Publisher {

		/**
		 * Number of threads used by every publisher.
		 */
		private int executorThreads = 4;

		/**
		 * Retry properties.
		 */
		private final Retry retry = new Retry();

		/**
		 * Batching properties.
		 */
		private final Batching batching = new Batching();

		/**
		 * Enable message ordering setting.
		 */
		private Boolean enableMessageOrdering;

		/**
		 * Set publisher endpoint. Example: "us-east1-pubsub.googleapis.com:443".
		 */
		private String endpoint;

		public Batching getBatching() {
			return this.batching;
		}

		public Retry getRetry() {
			return this.retry;
		}

		public int getExecutorThreads() {
			return this.executorThreads;
		}

		public void setExecutorThreads(int executorThreads) {
			this.executorThreads = executorThreads;
		}

		public Boolean getEnableMessageOrdering() {
			return enableMessageOrdering;
		}

		public void setEnableMessageOrdering(Boolean enableMessageOrdering) {
			this.enableMessageOrdering = enableMessageOrdering;
		}

		public String getEndpoint() {
			return endpoint;
		}

		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}
	}

	/**
	 * Subscriber settings.
	 */
	public static class Subscriber {

		/**
		 * Number of threads used by every subscriber.
		 */
		private Integer executorThreads;

		/**
		 * Number of threads used for batch acknowledgement.
		 */
		private int maxAcknowledgementThreads = 4;

		/**
		 * The optional pull endpoint setting for the subscriber factory.
		 */
		private String pullEndpoint;

		/**
		 * The optional max ack extension period in seconds for the subscriber factory.
		 */
		private Long maxAckExtensionPeriod;

		/**
		 * The optional parallel pull count setting for the subscriber factory.
		 */
		private Integer parallelPullCount;

		/**
		 * Retry settings for subscriber factory.
		 */
		private final Retry retry = new Retry();

		/**
		 * Flow control settings for subscriber factory.
		 */
		private final FlowControl flowControl = new FlowControl();

		public Retry getRetry() {
			return this.retry;
		}

		public FlowControl getFlowControl() {
			return this.flowControl;
		}

		public String getPullEndpoint() {
			return this.pullEndpoint;
		}

		public void setPullEndpoint(String pullEndpoint) {
			this.pullEndpoint = pullEndpoint;
		}

		public Long getMaxAckExtensionPeriod() {
			return this.maxAckExtensionPeriod;
		}

		public void setMaxAckExtensionPeriod(Long maxAckExtensionPeriod) {
			this.maxAckExtensionPeriod = maxAckExtensionPeriod;
		}

		public Integer getParallelPullCount() {
			return this.parallelPullCount;
		}

		public void setParallelPullCount(Integer parallelPullCount) {
			this.parallelPullCount = parallelPullCount;
		}

		public Integer getExecutorThreads() {
			return this.executorThreads;
		}

		/**
		 * Set the number of executor threads.
		 * @param executorThreads the number of threads
		 */
		public void setExecutorThreads(int executorThreads) {
			this.executorThreads = executorThreads;
		}


		public int getMaxAcknowledgementThreads() {
			return this.maxAcknowledgementThreads;
		}

		public void setMaxAcknowledgementThreads(int maxAcknowledgementThreads) {
			this.maxAcknowledgementThreads = maxAcknowledgementThreads;
		}
	}

	/**
	 * Retry settings.
	 */
	public static class Retry {

		/**
		 * TotalTimeout has ultimate control over how long the logic should keep trying the remote call
		 * until it gives up completely. The higher the total timeout, the more retries can be
		 * attempted.
		 */
		private Long totalTimeoutSeconds;

		/**
		 * InitialRetryDelay controls the delay before the first retry. Subsequent retries will use this
		 * value adjusted according to the RetryDelayMultiplier.
		 */
		private Long initialRetryDelaySeconds;

		/**
		 * RetryDelayMultiplier controls the change in retry delay. The retry delay of the previous call
		 * is multiplied by the RetryDelayMultiplier to calculate the retry delay for the next call.
		 */
		private Double retryDelayMultiplier;

		/**
		 * MaxRetryDelay puts a limit on the value of the retry delay, so that the RetryDelayMultiplier
		 * can't increase the retry delay higher than this amount.
		 */
		private Long maxRetryDelaySeconds;

		/**
		 * MaxAttempts defines the maximum number of attempts to perform.
		 * If this value is greater than 0, and the number of attempts reaches this limit,
		 * the logic will give up retrying even if the total retry time is still lower
		 * than TotalTimeout.
		 */
		private Integer maxAttempts;

		/**
		 * Jitter determines if the delay time should be randomized.
		 */
		private Boolean jittered;

		/**
		 * InitialRpcTimeout controls the timeout for the initial RPC. Subsequent calls will use this
		 * value adjusted according to the RpcTimeoutMultiplier.
		 */
		private Long initialRpcTimeoutSeconds;

		/**
		 * RpcTimeoutMultiplier controls the change in RPC timeout. The timeout of the previous call is
		 * multiplied by the RpcTimeoutMultiplier to calculate the timeout for the next call.
		 */
		private Double rpcTimeoutMultiplier;

		/**
		 * MaxRpcTimeout puts a limit on the value of the RPC timeout, so that the RpcTimeoutMultiplier
		 * can't increase the RPC timeout higher than this amount.
		 */
		private Long maxRpcTimeoutSeconds;

		public Long getTotalTimeoutSeconds() {
			return this.totalTimeoutSeconds;
		}

		public void setTotalTimeoutSeconds(Long totalTimeoutSeconds) {
			this.totalTimeoutSeconds = totalTimeoutSeconds;
		}

		public Long getInitialRetryDelaySeconds() {
			return this.initialRetryDelaySeconds;
		}

		public void setInitialRetryDelaySeconds(Long initialRetryDelaySeconds) {
			this.initialRetryDelaySeconds = initialRetryDelaySeconds;
		}

		public Double getRetryDelayMultiplier() {
			return this.retryDelayMultiplier;
		}

		public void setRetryDelayMultiplier(Double retryDelayMultiplier) {
			this.retryDelayMultiplier = retryDelayMultiplier;
		}

		public Long getMaxRetryDelaySeconds() {
			return this.maxRetryDelaySeconds;
		}

		public void setMaxRetryDelaySeconds(Long maxRetryDelaySeconds) {
			this.maxRetryDelaySeconds = maxRetryDelaySeconds;
		}

		public Integer getMaxAttempts() {
			return this.maxAttempts;
		}

		public void setMaxAttempts(Integer maxAttempts) {
			this.maxAttempts = maxAttempts;
		}

		public Boolean getJittered() {
			return this.jittered;
		}

		public void setJittered(Boolean jittered) {
			this.jittered = jittered;
		}

		public Long getInitialRpcTimeoutSeconds() {
			return this.initialRpcTimeoutSeconds;
		}

		public void setInitialRpcTimeoutSeconds(Long initialRpcTimeoutSeconds) {
			this.initialRpcTimeoutSeconds = initialRpcTimeoutSeconds;
		}

		public Double getRpcTimeoutMultiplier() {
			return this.rpcTimeoutMultiplier;
		}

		public void setRpcTimeoutMultiplier(Double rpcTimeoutMultiplier) {
			this.rpcTimeoutMultiplier = rpcTimeoutMultiplier;
		}

		/**
		 * get the max RPC timeout seconds.
		 * @return the number of seconds of the RPC timeout
		 */
		public Long getMaxRpcTimeoutSeconds() {
			return this.maxRpcTimeoutSeconds;
		}

		public void setMaxRpcTimeoutSeconds(Long maxRpcTimeoutSeconds) {
			this.maxRpcTimeoutSeconds = maxRpcTimeoutSeconds;
		}
	}

	/**
	 * flow control settings.
	 */
	public static class FlowControl {

		/**
		 * Maximum number of outstanding elements to keep in memory before enforcing flow control.
		 */
		private Long maxOutstandingElementCount;

		/**
		 * Maximum number of outstanding bytes to keep in memory before enforcing flow control.
		 */
		private Long maxOutstandingRequestBytes;

		/**
		 * The behavior when the specified limits are exceeded.
		 */
		private LimitExceededBehavior limitExceededBehavior;

		public Long getMaxOutstandingElementCount() {
			return this.maxOutstandingElementCount;
		}

		public void setMaxOutstandingElementCount(
				Long maxOutstandingElementCount) {
			this.maxOutstandingElementCount = maxOutstandingElementCount;
		}

		public Long getMaxOutstandingRequestBytes() {
			return this.maxOutstandingRequestBytes;
		}

		public void setMaxOutstandingRequestBytes(
				Long maxOutstandingRequestBytes) {
			this.maxOutstandingRequestBytes = maxOutstandingRequestBytes;
		}

		public LimitExceededBehavior getLimitExceededBehavior() {
			return this.limitExceededBehavior;
		}

		public void setLimitExceededBehavior(
				LimitExceededBehavior limitExceededBehavior) {
			this.limitExceededBehavior = limitExceededBehavior;
		}
	}

	/**
	 * Batching settings.
	 */
	public static class Batching {

		/**
		 * Flow control settings for batching.
		 */
		private final FlowControl flowControl = new FlowControl();

		/**
		 * The element count threshold to use for batching.
		 */
		private Long elementCountThreshold;

		/**
		 * The request byte threshold to use for batching.
		 */
		private Long requestByteThreshold;

		/**
		 * The delay threshold to use for batching. After this amount of time has elapsed (counting
		 * from the first element added), the elements will be wrapped up in a batch and sent.
		 */
		private Long delayThresholdSeconds;

		/**
		 * Enables batching if true.
		 */
		private Boolean enabled;

		public Long getElementCountThreshold() {
			return this.elementCountThreshold;
		}

		public void setElementCountThreshold(Long elementCountThreshold) {
			this.elementCountThreshold = elementCountThreshold;
		}

		public Long getRequestByteThreshold() {
			return this.requestByteThreshold;
		}

		public void setRequestByteThreshold(Long requestByteThreshold) {
			this.requestByteThreshold = requestByteThreshold;
		}

		public Long getDelayThresholdSeconds() {
			return this.delayThresholdSeconds;
		}

		public void setDelayThresholdSeconds(Long delayThresholdSeconds) {
			this.delayThresholdSeconds = delayThresholdSeconds;
		}

		public Boolean getEnabled() {
			return this.enabled;
		}

		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}

		public FlowControl getFlowControl() {
			return this.flowControl;
		}
	}
}
