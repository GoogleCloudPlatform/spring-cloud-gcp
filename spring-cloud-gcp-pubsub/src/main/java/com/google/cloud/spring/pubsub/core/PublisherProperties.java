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
 * Properties for Publisher-specific configurations.
 */
public class PublisherProperties {

	/**
	 * Default publisher settings.
	 */
	private final Publisher publisher = new Publisher();

	public Publisher getPublisher() {
		return this.publisher;
	}

	/**
	 * Publisher settings.
	 */
	public static class Publisher {

		/**
		 * Retry properties.
		 */
		private final Retry retry = new Retry();
		/**
		 * Batching properties.
		 */
		private final Batching batching = new Batching();
		/**
		 * Number of threads used by every publisher.
		 */
		private int executorThreads = 4;
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
}
