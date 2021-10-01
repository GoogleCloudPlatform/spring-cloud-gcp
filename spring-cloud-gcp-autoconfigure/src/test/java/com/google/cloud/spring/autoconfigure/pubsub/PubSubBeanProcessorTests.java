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

import java.util.concurrent.ConcurrentMap;

import org.junit.Test;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PubSubBeanProcessor}.
 *
 */
public class PubSubBeanProcessorTests {

	@Test
	public void testPostProcessorBeanDefinitionRegistry_global() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withPropertyValues(
						"spring.cloud.gcp.projectId=project",
						"spring.cloud.gcp.pubsub.subscriber.executor-threads=3");
		contextRunner.run(ctx -> {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) ctx.getAutowireCapableBeanFactory();
			PubSubBeanProcessor pubSubBeanProcessor = new PubSubBeanProcessor(ctx.getEnvironment());

			pubSubBeanProcessor.postProcessBeanDefinitionRegistry(registry);

			ThreadPoolTaskScheduler scheduler = pubSubBeanProcessor.getGlobalScheduler();
			assertThat(scheduler.getPoolSize()).isEqualTo(3);
			assertThat(scheduler.getThreadNamePrefix()).isEqualTo("global-gcp-pubsub-subscriber");
			ThreadPoolTaskScheduler globalSchedulerBean = (ThreadPoolTaskScheduler) ctx
					.getBean("globalThreadPoolScheduler");
			assertThat(globalSchedulerBean).isNotNull();
			assertThat(pubSubBeanProcessor.getThreadPoolSchedulerMap()).isEmpty();
		});
	}

	@Test
	public void testPostProcessorBeanDefinitionRegistry_selective() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withPropertyValues(
						"spring.cloud.gcp.projectId=project",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.executor-threads=3");
		contextRunner.run(ctx -> {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) ctx.getAutowireCapableBeanFactory();
			PubSubBeanProcessor pubSubBeanProcessor = new PubSubBeanProcessor(ctx.getEnvironment());

			pubSubBeanProcessor.postProcessBeanDefinitionRegistry(registry);

			// Verify thread pool task schedulers have been correctly created
			ConcurrentMap<String, ThreadPoolTaskScheduler> schedulerMap = pubSubBeanProcessor
					.getThreadPoolSchedulerMap();
			assertThat(schedulerMap).hasSize(1);
			ThreadPoolTaskScheduler selectiveScheduler = schedulerMap
					.get("projects/project/subscriptions/subscription-name");
			ThreadPoolTaskScheduler globalScheduler = pubSubBeanProcessor.getGlobalScheduler();
			assertThat(selectiveScheduler.getPoolSize()).isEqualTo(3);
			assertThat(selectiveScheduler.getThreadNamePrefix()).isEqualTo("gcp-pubsub-subscriber-subscription-name");
			assertThat(globalScheduler.getPoolSize()).isEqualTo(4);
			assertThat(globalScheduler.getThreadNamePrefix()).isEqualTo("global-gcp-pubsub-subscriber");

			// Verify that selective and global beans have been created
			ThreadPoolTaskScheduler selectiveSchedulerBean = (ThreadPoolTaskScheduler) ctx
					.getBean("threadPoolScheduler_subscription-name");
			ThreadPoolTaskScheduler globalSchedulerBean = (ThreadPoolTaskScheduler) ctx
					.getBean("globalThreadPoolScheduler");
			assertThat(selectiveSchedulerBean).isNotNull();
			assertThat(globalSchedulerBean).isNotNull();
		});
	}

	@Test
	public void testPostProcessorBeanDefinitionRegistry_globalAndSelective() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withPropertyValues(
						"spring.cloud.gcp.projectId=project",
						"spring.cloud.gcp.pubsub.subscriber.executor-threads=3",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.executor-threads=1");
		contextRunner.run(ctx -> {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) ctx.getAutowireCapableBeanFactory();
			PubSubBeanProcessor pubSubBeanProcessor = new PubSubBeanProcessor(ctx.getEnvironment());

			pubSubBeanProcessor.postProcessBeanDefinitionRegistry(registry);

			// Verify thread pool task schedulers have been correctly created
			ConcurrentMap<String, ThreadPoolTaskScheduler> schedulerMap = pubSubBeanProcessor
					.getThreadPoolSchedulerMap();
			assertThat(schedulerMap).hasSize(1);
			ThreadPoolTaskScheduler selectiveScheduler = schedulerMap
					.get("projects/project/subscriptions/subscription-name");
			ThreadPoolTaskScheduler globalScheduler = pubSubBeanProcessor.getGlobalScheduler();
			assertThat(selectiveScheduler.getPoolSize()).isEqualTo(1);
			assertThat(selectiveScheduler.getThreadNamePrefix()).isEqualTo("gcp-pubsub-subscriber-subscription-name");
			assertThat(globalScheduler.getPoolSize()).isEqualTo(3);
			assertThat(globalScheduler.getThreadNamePrefix()).isEqualTo("global-gcp-pubsub-subscriber");

			// Verify that selective and global beans have been created
			ThreadPoolTaskScheduler selectiveSchedulerBean = (ThreadPoolTaskScheduler) ctx
					.getBean("threadPoolScheduler_subscription-name");
			ThreadPoolTaskScheduler globalSchedulerBean = (ThreadPoolTaskScheduler) ctx
					.getBean("globalThreadPoolScheduler");
			assertThat(selectiveSchedulerBean).isNotNull();
			assertThat(globalSchedulerBean).isNotNull();
		});
	}

	@Test
	public void testPostProcessorBeanDefinitionRegistry_globalAndDifferentSelectivePropertySet() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withPropertyValues(
						"spring.cloud.gcp.projectId=project",
						"spring.cloud.gcp.pubsub.subscriber.executor-threads=3",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=1");
		contextRunner.run(ctx -> {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) ctx.getAutowireCapableBeanFactory();
			PubSubBeanProcessor pubSubBeanProcessor = new PubSubBeanProcessor(ctx.getEnvironment());

			pubSubBeanProcessor.postProcessBeanDefinitionRegistry(registry);

			// Verify that only global thread pool task scheduler created
			assertThat(pubSubBeanProcessor.getThreadPoolSchedulerMap()).isEmpty();
			ThreadPoolTaskScheduler globalScheduler = pubSubBeanProcessor.getGlobalScheduler();
			assertThat(globalScheduler.getPoolSize()).isEqualTo(3);
			assertThat(globalScheduler.getThreadNamePrefix()).isEqualTo("global-gcp-pubsub-subscriber");

			// Verify that global bean has been created
			ThreadPoolTaskScheduler globalSchedulerBean = (ThreadPoolTaskScheduler) ctx
					.getBean("globalThreadPoolScheduler");
			assertThat(globalSchedulerBean).isNotNull();
		});
	}

	@Test
	public void testPostProcessorBeanDefinitionRegistry_noConfigurationSet_pickDefault() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withPropertyValues(
						"spring.cloud.gcp.projectId=project");
		contextRunner.run(ctx -> {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) ctx.getAutowireCapableBeanFactory();
			PubSubBeanProcessor pubSubBeanProcessor = new PubSubBeanProcessor(ctx.getEnvironment());

			pubSubBeanProcessor.postProcessBeanDefinitionRegistry(registry);

			// Verify that only global thread pool task scheduler created
			assertThat(pubSubBeanProcessor.getThreadPoolSchedulerMap()).isEmpty();
			ThreadPoolTaskScheduler globalScheduler = pubSubBeanProcessor.getGlobalScheduler();
			assertThat(globalScheduler.getPoolSize()).isEqualTo(4);
			assertThat(globalScheduler.getThreadNamePrefix()).isEqualTo("global-gcp-pubsub-subscriber");

			// Verify that global bean has been created
			ThreadPoolTaskScheduler globalSchedulerBean = (ThreadPoolTaskScheduler) ctx
					.getBean("globalThreadPoolScheduler");
			assertThat(globalSchedulerBean).isNotNull();
		});
	}
}
