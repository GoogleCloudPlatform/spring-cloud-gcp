package com.google.cloud.spring.autoconfigure.pubsub;

import org.junit.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ConcurrentMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Pub/Sub bean processor.
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
					.getBean("threadPoolScheduler_subscriptionName");
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
					.getBean("threadPoolScheduler_subscriptionName");
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
