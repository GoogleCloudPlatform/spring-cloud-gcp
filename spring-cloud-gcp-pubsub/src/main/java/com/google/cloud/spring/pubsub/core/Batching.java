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
 * Batching settings.
 */
public class Batching {

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
	 * The delay threshold to use for batching. After this amount of time has elapsed
	 * (counting from the first element added), the elements will be wrapped up in a batch and
	 * sent.
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
