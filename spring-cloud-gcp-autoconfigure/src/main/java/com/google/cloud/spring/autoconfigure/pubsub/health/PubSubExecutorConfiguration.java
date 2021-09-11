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

package com.google.cloud.spring.autoconfigure.pubsub.health;

import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubProperties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author Emmanouil Gkatziouras
 *
 * @since 2.0.1
 */
@Configuration
public class PubSubExecutorConfiguration {

	private final GcpPubSubProperties gcpPubSubProperties;

	public PubSubExecutorConfiguration(
		GcpPubSubProperties gcpPubSubProperties) {
		this.gcpPubSubProperties = gcpPubSubProperties;
	}

	@Bean
	@ConditionalOnMissingBean(name = "pubsubPublisherThreadPool")
	public ThreadPoolTaskScheduler pubsubPublisherThreadPool() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(this.gcpPubSubProperties.getPublisher().getExecutorThreads());
		scheduler.setThreadNamePrefix("gcp-pubsub-publisher");
		scheduler.setDaemon(true);
		return scheduler;
	}

	@Bean
	@ConditionalOnMissingBean(name = "publisherExecutorProvider")
	public ExecutorProvider publisherExecutorProvider(
		@Qualifier("pubsubPublisherThreadPool") ThreadPoolTaskScheduler scheduler) {
		return FixedExecutorProvider.create(scheduler.getScheduledExecutor());
	}

	@Bean
	@ConditionalOnMissingBean(name = "pubsubSubscriberThreadPool")
	public ThreadPoolTaskScheduler pubsubSubscriberThreadPool() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(this.gcpPubSubProperties.getSubscriber().getExecutorThreads());
		scheduler.setThreadNamePrefix("gcp-pubsub-subscriber");
		scheduler.setDaemon(true);
		return scheduler;
	}

	@Bean
	@ConditionalOnMissingBean(name = "subscriberExecutorProvider")
	public ExecutorProvider subscriberExecutorProvider(
		@Qualifier("pubsubSubscriberThreadPool") ThreadPoolTaskScheduler scheduler) {
		return FixedExecutorProvider.create(scheduler.getScheduledExecutor());
	}


}
