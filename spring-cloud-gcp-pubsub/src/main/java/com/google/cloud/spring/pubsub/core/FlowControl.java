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

import com.google.api.gax.batching.FlowController;

/**
 * flow control settings.
 */
public class FlowControl {

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
	private FlowController.LimitExceededBehavior limitExceededBehavior;

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

	public FlowController.LimitExceededBehavior getLimitExceededBehavior() {
		return this.limitExceededBehavior;
	}

	public void setLimitExceededBehavior(
			FlowController.LimitExceededBehavior limitExceededBehavior) {
		this.limitExceededBehavior = limitExceededBehavior;
	}
}
