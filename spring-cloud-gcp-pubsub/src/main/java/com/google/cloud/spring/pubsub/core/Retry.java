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

/**
 * Retry settings.
 */
public class Retry {

	/**
	 * TotalTimeout has ultimate control over how long the logic should keep trying the remote
	 * call until it gives up completely. The higher the total timeout, the more retries can
	 * be attempted.
	 */
	private Long totalTimeoutSeconds;

	/**
	 * InitialRetryDelay controls the delay before the first retry. Subsequent retries will
	 * use this value adjusted according to the RetryDelayMultiplier.
	 */
	private Long initialRetryDelaySeconds;

	/**
	 * RetryDelayMultiplier controls the change in retry delay. The retry delay of the
	 * previous call is multiplied by the RetryDelayMultiplier to calculate the retry delay
	 * for the next call.
	 */
	private Double retryDelayMultiplier;

	/**
	 * MaxRetryDelay puts a limit on the value of the retry delay, so that the
	 * RetryDelayMultiplier can't increase the retry delay higher than this amount.
	 */
	private Long maxRetryDelaySeconds;

	/**
	 * MaxAttempts defines the maximum number of attempts to perform. If this value is greater
	 * than 0, and the number of attempts reaches this limit, the logic will give up retrying
	 * even if the total retry time is still lower than TotalTimeout.
	 */
	private Integer maxAttempts;

	/**
	 * Jitter determines if the delay time should be randomized.
	 */
	private Boolean jittered;

	/**
	 * InitialRpcTimeout controls the timeout for the initial RPC. Subsequent calls will use
	 * this value adjusted according to the RpcTimeoutMultiplier.
	 */
	private Long initialRpcTimeoutSeconds;

	/**
	 * RpcTimeoutMultiplier controls the change in RPC timeout. The timeout of the previous
	 * call is multiplied by the RpcTimeoutMultiplier to calculate the timeout for the next
	 * call.
	 */
	private Double rpcTimeoutMultiplier;

	/**
	 * MaxRpcTimeout puts a limit on the value of the RPC timeout, so that the
	 * RpcTimeoutMultiplier can't increase the RPC timeout higher than this amount.
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
