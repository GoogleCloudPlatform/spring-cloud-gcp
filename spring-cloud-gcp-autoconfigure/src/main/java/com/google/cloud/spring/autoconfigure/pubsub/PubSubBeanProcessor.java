package com.google.cloud.spring.autoconfigure.pubsub;

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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PubSubBeanProcessor implements BeanDefinitionRegistryPostProcessor {

    private ConfigurableEnvironment environment;
    private ConcurrentHashMap<String, ThreadPoolTaskScheduler> threadPoolTaskSchedulerMap = new ConcurrentHashMap<>();
    private ThreadPoolTaskScheduler globalScheduler;


    public PubSubBeanProcessor(ConfigurableEnvironment environment){
        this.environment = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
		Binder binder = new Binder(ConfigurationPropertySources.get(environment));
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

		// Register selectively configured beans
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

		// Register a global bean
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

	private ThreadPoolTaskScheduler createAndRegisterSchedulerBean(Integer executorThreads, String threadName, String beanName,
																   BeanDefinitionRegistry beanDefinitionRegistry) {
		ThreadPoolTaskScheduler scheduler = createThreadPoolTaskScheduler(executorThreads, threadName);
		beanDefinitionRegistry.registerBeanDefinition(beanName, BeanDefinitionBuilder.genericBeanDefinition(ThreadPoolTaskScheduler.class, () -> this.globalScheduler)
				.getBeanDefinition());
		return scheduler;
	}

	/**
     * Creates {@link ThreadPoolTaskScheduler} given the number of executor threads and a thread name.
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