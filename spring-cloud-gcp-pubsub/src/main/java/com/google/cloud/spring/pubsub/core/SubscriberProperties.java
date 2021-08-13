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
 * Properties for Subscriber-specific configurations.
 */
public class SubscriberProperties {

	/**
	 * Default subscriber settings.
	 */
	private final Subscriber subscriber = new Subscriber();

	public Subscriber getSubscriber() {
		return this.subscriber;
	}

	/**
	 * Subscriber settings.
	 */
	public static class Subscriber {

		/**
		 * Retry settings for subscriber factory.
		 */
		private final Retry retry = new Retry();
		/**
		 * Flow control settings for subscriber factory.
		 */
		private final FlowControl flowControl = new FlowControl();
		/**
		 * Number of threads used by every subscriber.
		 */
		private int executorThreads = 4;
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
		private Long maxAckExtensionPeriod = 0L;
		/**
		 * The optional parallel pull count setting for the subscriber factory.
		 */
		private Integer parallelPullCount;

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

		public int getExecutorThreads() {
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
}
