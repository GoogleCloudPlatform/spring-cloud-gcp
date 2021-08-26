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

package com.google.cloud.spring.pubsub.support;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import com.google.api.core.ApiClock;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.pubsub.v1.PullRequest;
import org.threeten.bp.Duration;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;

/**
 * The default {@link SubscriberFactory} implementation.
 *
 * @author João André Martins
 * @author Mike Eltsufin
 * @author Doug Hoard
 * @author Chengyuan Zhao
 * @author Maurice Zeijen
 */
public class DefaultSubscriberFactory implements SubscriberFactory {

	private final String projectId;

	/**
	 * @deprecated Directly use application.properties to configure ExecutorProvider instead
	 * of Spring-managed beans
	 */
	@Deprecated
	private ExecutorProvider executorProvider;

	private TransportChannelProvider channelProvider;

	private CredentialsProvider credentialsProvider;

	private HeaderProvider headerProvider;

	private ExecutorProvider systemExecutorProvider;

	private FlowControlSettings flowControlSettings;

	private Duration maxAckExtensionPeriod;

	private Integer parallelPullCount;

	private String pullEndpoint;

	private ApiClock apiClock;

	private RetrySettings subscriberStubRetrySettings;

	private PubSubConfiguration pubSubConfiguration;

	private ConcurrentHashMap<String, ThreadPoolTaskScheduler> threadPoolTaskSchedulerMap = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, ExecutorProvider> executorProviderMap = new ConcurrentHashMap<>();

	private ExecutorProvider defaultExecutorProvider;

	/**
	 * Default {@link DefaultSubscriberFactory} constructor.
	 * @param projectIdProvider provides the default GCP project ID for selecting the
	 *     subscriptions
	 * @deprecated Use the new {@link DefaultSubscriberFactory (GcpProjectIdProvider,PubSubConfiguration)} instead
	 */
	@Deprecated
	public DefaultSubscriberFactory(GcpProjectIdProvider projectIdProvider) {
		this(projectIdProvider, null);
	}

	/**
	 * Default {@link DefaultSubscriberFactory} constructor.
	 * @param projectIdProvider provides the default GCP project ID for selecting the subscriptions
	 * @param pubSubConfiguration contains the subscriber properties to configure
	 */
	public DefaultSubscriberFactory(GcpProjectIdProvider projectIdProvider, PubSubConfiguration pubSubConfiguration) {
		Assert.notNull(projectIdProvider, "The project ID provider can't be null.");

		this.projectId = projectIdProvider.getProjectId();
		Assert.hasText(this.projectId, "The project ID can't be null or empty.");

		this.pubSubConfiguration = pubSubConfiguration;
	}

	@Override
	public String getProjectId() {
		return this.projectId;
	}

	/**
	 * Set the provider for the subscribers' executor. Useful to specify the number of threads to be
	 * used by each executor.
	 * @param executorProvider the executor provider to set
	 * @deprecated Directly use application.properties to configure ExecutorProvider instead
	 * of Spring-managed beans
	 */
	@Deprecated
	public void setExecutorProvider(ExecutorProvider executorProvider) {
		this.executorProvider = executorProvider;
	}

	/**
	 * Set the provider for the subscribers' transport channel.
	 * @param channelProvider the channel provider to set
	 */
	public void setChannelProvider(TransportChannelProvider channelProvider) {
		this.channelProvider = channelProvider;
	}

	/**
	 * Set the provider for the GCP credentials to be used by the subscribers' API calls.
	 * @param credentialsProvider the credentials provider to set
	 */
	public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
		this.credentialsProvider = credentialsProvider;
	}

	/**
	 * Set the provider for the HTTP headers to be added to the subscribers' REST API calls.
	 * @param headerProvider the header provider to set
	 */
	public void setHeaderProvider(HeaderProvider headerProvider) {
		this.headerProvider = headerProvider;
	}

	/**
	 * Set the provider for the system executor, to poll and manage lease extensions.
	 * @param systemExecutorProvider the system executor provider to set
	 */
	public void setSystemExecutorProvider(ExecutorProvider systemExecutorProvider) {
		this.systemExecutorProvider = systemExecutorProvider;
	}

	/**
	 * Set the flow control for the subscribers, including the behaviour for when the flow limits
	 * are hit.
	 * @param flowControlSettings the flow control settings to set
	 */
	public void setFlowControlSettings(FlowControlSettings flowControlSettings) {
		this.flowControlSettings = flowControlSettings;
	}

	/**
	 * Set the maximum period the ack timeout is extended by.
	 * @param maxAckExtensionPeriod the max ack extension period to set
	 */
	public void setMaxAckExtensionPeriod(Duration maxAckExtensionPeriod) {
		this.maxAckExtensionPeriod = maxAckExtensionPeriod;
	}

	/**
	 * Set the number of pull workers.
	 * @param parallelPullCount the parallel pull count to set
	 */
	public void setParallelPullCount(Integer parallelPullCount) {
		this.parallelPullCount = parallelPullCount;
	}

	/**
	 * Set the endpoint for synchronous pulling messages.
	 * @param pullEndpoint the pull endpoint to set
	 */
	public void setPullEndpoint(String pullEndpoint) {
		this.pullEndpoint = pullEndpoint;
	}

	/**
	 * Set the clock to use for the retry logic in synchronous pulling.
	 * @param apiClock the api clock to set
	 */
	public void setApiClock(ApiClock apiClock) {
		this.apiClock = apiClock;
	}

	/**
	 * Set the retry settings for the generated subscriber stubs.
	 * @param subscriberStubRetrySettings parameters for retrying pull requests when they fail,
	 * including jitter logic, timeout, and exponential backoff
	 */
	public void setSubscriberStubRetrySettings(RetrySettings subscriberStubRetrySettings) {
		this.subscriberStubRetrySettings = subscriberStubRetrySettings;
	}

	@Override
	public Subscriber createSubscriber(String subscriptionName, MessageReceiver receiver) {
		Subscriber.Builder subscriberBuilder = Subscriber.newBuilder(
				PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, this.projectId), receiver);

		if (this.channelProvider != null) {
			subscriberBuilder.setChannelProvider(this.channelProvider);
		}

		ExecutorProvider executor = getExecutorProvider(subscriptionName);
		if (executor != null) {
			subscriberBuilder.setExecutorProvider(executor);
		}

		if (this.credentialsProvider != null) {
			subscriberBuilder.setCredentialsProvider(this.credentialsProvider);
		}

		if (this.headerProvider != null) {
			subscriberBuilder.setHeaderProvider(this.headerProvider);
		}

		if (this.systemExecutorProvider != null) {
			subscriberBuilder.setSystemExecutorProvider(this.systemExecutorProvider);
		}

		if (this.flowControlSettings != null) {
			subscriberBuilder.setFlowControlSettings(this.flowControlSettings);
		}

		if (this.maxAckExtensionPeriod != null) {
			subscriberBuilder.setMaxAckExtensionPeriod(this.maxAckExtensionPeriod);
		}

		if (this.parallelPullCount != null) {
			subscriberBuilder.setParallelPullCount(this.parallelPullCount);
		}

		return subscriberBuilder.build();
	}

	@Override
	public PullRequest createPullRequest(String subscriptionName, Integer maxMessages,
			Boolean returnImmediately) {
		Assert.hasLength(subscriptionName, "The subscription name must be provided.");

		if (maxMessages == null) {
			maxMessages = Integer.MAX_VALUE;
		}
		Assert.isTrue(maxMessages > 0, "The maxMessages must be greater than 0.");

		PullRequest.Builder pullRequestBuilder =
				PullRequest.newBuilder()
						.setSubscription(
								PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, this.projectId).toString())
						.setMaxMessages(maxMessages);

		if (returnImmediately != null) {
			pullRequestBuilder.setReturnImmediately(returnImmediately);
		}

		return pullRequestBuilder.build();
	}

	@Override
	public SubscriberStub createSubscriberStub(String subscriptionName) {
		SubscriberStubSettings.Builder subscriberStubSettings = SubscriberStubSettings.newBuilder();

		if (this.credentialsProvider != null) {
			subscriberStubSettings.setCredentialsProvider(this.credentialsProvider);
		}

		if (this.pullEndpoint != null) {
			subscriberStubSettings.setEndpoint(this.pullEndpoint);
		}

		ExecutorProvider executor = getExecutorProvider(subscriptionName);
		if (executor != null) {
			subscriberStubSettings.setExecutorProvider(executor);
		}

		if (this.headerProvider != null) {
			subscriberStubSettings.setHeaderProvider(this.headerProvider);
		}

		if (this.channelProvider != null) {
			subscriberStubSettings.setTransportChannelProvider(this.channelProvider);
		}

		if (this.apiClock != null) {
			subscriberStubSettings.setClock(this.apiClock);
		}

		if (this.subscriberStubRetrySettings != null) {
			subscriberStubSettings.pullSettings().setRetrySettings(
					this.subscriberStubRetrySettings);
		}

		try {
			return GrpcSubscriberStub.create(subscriberStubSettings.build());
		}
		catch (IOException ex) {
			throw new RuntimeException("Error creating the SubscriberStub", ex);
		}
	}

	/**
	 * Creates {@link ExecutorProvider}. If a custom executor provider is set then the
	 * subscriber properties configured through the application.properties file will be
	 * ignored.
	 * @param subscriptionName subscription name
	 * @return executor provider
	 */
	public ExecutorProvider getExecutorProvider(String subscriptionName) {
		if (this.executorProvider != null) {
			return this.executorProvider;
		}
		if (this.pubSubConfiguration != null) {
			PubSubConfiguration.Subscriber subscriber = this.pubSubConfiguration.getSubscriber(subscriptionName,
					this.projectId);
			return getExecutorProviderFromConfigurations(subscriber, subscriptionName);
		}
		return null;
	}

	/**
	 * Creates {@link ExecutorProvider} for subscriber when Pub/Sub configurations (either
	 * global/default or subscription-specific) are present.
	 * @param subscriber subscriber properties
	 * @param subscriptionName subscription name
	 * @return executor provider
	 */
	ExecutorProvider getExecutorProviderFromConfigurations(PubSubConfiguration.Subscriber subscriber,
			String subscriptionName) {
		if (!subscriber.isGlobal()) {
			return createExecutorProvider(subscriber, subscriptionName);
		}

		if (this.defaultExecutorProvider != null) {
			return this.defaultExecutorProvider;
		}
		this.defaultExecutorProvider = createExecutorProvider(subscriber, subscriptionName);
		return this.defaultExecutorProvider;
	}

	ExecutorProvider createExecutorProvider(PubSubConfiguration.Subscriber subscriber, String subscriptionName) {
		if (this.executorProviderMap.containsKey(subscriptionName)) {
			return this.executorProviderMap.get(subscriptionName);
		}
		ThreadPoolTaskScheduler scheduler = createThreadPoolTaskScheduler(subscriber, subscriptionName);
		scheduler.initialize();
		ExecutorProvider executor = FixedExecutorProvider.create(scheduler.getScheduledExecutor());
		return this.executorProviderMap.computeIfAbsent(subscriptionName, k -> executor);
	}

	/**
	 * Creates {@link ThreadPoolTaskScheduler} given subscriber properties.
	 * @param subscriptionName subscription name
	 * @return thread pool scheduler
	 */
	ThreadPoolTaskScheduler createThreadPoolTaskScheduler(PubSubConfiguration.Subscriber subscriber,
			String subscriptionName) {
		if (this.threadPoolTaskSchedulerMap.containsKey(subscriptionName)) {
			return threadPoolTaskSchedulerMap.get(subscriptionName);
		}
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(subscriber.getExecutorThreads());
		String threadNamePrefix = "gcp-pubsub-subscriber" + "-" + subscriptionName;
		scheduler.setThreadNamePrefix(threadNamePrefix);
		scheduler.setDaemon(true);
		return this.threadPoolTaskSchedulerMap.computeIfAbsent(subscriptionName, k -> scheduler);
	}

	Map<String, ThreadPoolTaskScheduler> getThreadPoolTaskSchedulerMap() {
		return this.threadPoolTaskSchedulerMap;
	}

	Map<String, ExecutorProvider> getExecutorProviderMap() {
		return this.executorProviderMap;
	}

	@PreDestroy
	public void clearScheduler() {
		for (ThreadPoolTaskScheduler scheduler : threadPoolTaskSchedulerMap.values()) {
			scheduler.shutdown();
		}
	}
}
