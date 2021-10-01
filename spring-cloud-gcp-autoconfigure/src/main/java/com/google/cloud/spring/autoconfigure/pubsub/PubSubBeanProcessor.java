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

package com.google.cloud.spring.autoconfigure.pubsub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.cloud.spring.pubsub.support.PubSubSubscriptionUtils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Class to register ThreadPoolTaskScheduler beans.
 */
public class PubSubBeanProcessor implements BeanDefinitionRegistryPostProcessor {

	private ConfigurableEnvironment environment;

	private ConcurrentHashMap<String, ThreadPoolTaskScheduler> threadPoolTaskSchedulerMap = new ConcurrentHashMap<>();

	private ThreadPoolTaskScheduler globalScheduler;

	public PubSubBeanProcessor(ConfigurableEnvironment environment) {
		this.environment = environment;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
		Binder binder = new Binder(ConfigurationPropertySources.get(this.environment));
		String cloudPropertiesPrefix = GcpProperties.class.getAnnotation(ConfigurationProperties.class)
				.value();
		GcpProperties gcpProperties = binder.bind(cloudPropertiesPrefix, GcpProperties.class)
				.orElse(new GcpProperties());
		GcpProjectIdProvider projectIdProvider = (gcpProperties.getProjectId() != null)
				? gcpProperties::getProjectId
				: new DefaultGcpProjectIdProvider();
		String cloudPubSubPropertiesPrefix = GcpPubSubProperties.class.getAnnotation(ConfigurationProperties.class)
				.value();
		GcpPubSubProperties pubSubProperties = binder.bind(cloudPubSubPropertiesPrefix, GcpPubSubProperties.class)
				.orElse(new GcpPubSubProperties());

		// Register selective threadPoolTaskScheduler beans
		Map<String, PubSubConfiguration.Subscriber> subscriberMap = pubSubProperties.getSubscription();
		for (Map.Entry<String, PubSubConfiguration.Subscriber> subscription : subscriberMap.entrySet()) {
			String subscriptionName = subscription.getKey();
			PubSubConfiguration.Subscriber selectiveSubscriber = subscriberMap.get(subscriptionName);
			Integer selectiveExecutorThreads = selectiveSubscriber.getExecutorThreads();
			if (selectiveExecutorThreads != null) {
				String threadName = "gcp-pubsub-subscriber-" + subscriptionName;
				String beanName = "threadPoolScheduler_" + subscriptionName;
				ThreadPoolTaskScheduler selectiveScheduler = createAndRegisterSchedulerBean(selectiveExecutorThreads,
						threadName, beanName, beanDefinitionRegistry);
				String fullyQualifiedName = PubSubSubscriptionUtils
						.toProjectSubscriptionName(subscriptionName, projectIdProvider.getProjectId()).toString();
				this.threadPoolTaskSchedulerMap.putIfAbsent(fullyQualifiedName, selectiveScheduler);
			}
		}

		// Register global threadPoolTaskScheduler configuration bean
		PubSubConfiguration.Subscriber globalSubscriber = pubSubProperties.getSubscriber();
		Integer globalExecutorThreads = globalSubscriber.getExecutorThreads();
		Integer numThreads = globalExecutorThreads != null ? globalExecutorThreads
				: PubSubConfiguration.DEFAULT_EXECUTOR_THREADS;
		this.globalScheduler = createAndRegisterSchedulerBean(numThreads, "global-gcp-pubsub-subscriber",
				"globalThreadPoolScheduler", beanDefinitionRegistry);

	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory)
			throws BeansException {
		// Do nothing.
	}

	/**
	 * Creates a {@link ThreadPoolTaskScheduler} and registers it as a bean.
	 * @param executorThreads number of executor threads
	 * @param threadName thread name
	 * @param beanName bean name
	 * @param beanDefinitionRegistry bean registry
	 * @return a {@link ThreadPoolTaskScheduler}
	 */
	private ThreadPoolTaskScheduler createAndRegisterSchedulerBean(Integer executorThreads, String threadName,
			String beanName,
			BeanDefinitionRegistry beanDefinitionRegistry) {
		ThreadPoolTaskScheduler scheduler = createThreadPoolTaskScheduler(executorThreads, threadName);
		beanDefinitionRegistry.registerBeanDefinition(beanName,
				BeanDefinitionBuilder.genericBeanDefinition(ThreadPoolTaskScheduler.class, () -> scheduler)
						.getBeanDefinition());
		return scheduler;
	}

	/**
	 * Creates {@link ThreadPoolTaskScheduler} given the number of executor threads and a
	 * thread name.
	 * @param executorThreads number of executor threads
	 * @param threadName thread name prefix to set for the scheduler
	 * @return thread pool scheduler
	 */
	ThreadPoolTaskScheduler createThreadPoolTaskScheduler(Integer executorThreads, String threadName) {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(executorThreads);
		scheduler.setThreadNamePrefix(threadName);
		scheduler.setDaemon(true);
		return scheduler;
	}

	public ConcurrentMap<String, ThreadPoolTaskScheduler> getThreadPoolSchedulerMap() {
		return this.threadPoolTaskSchedulerMap;
	}

	public ThreadPoolTaskScheduler getGlobalScheduler() {
		return this.globalScheduler;
	}
}

