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
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import com.google.api.core.ApiClock;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.cloud.spring.pubsub.core.PubSubException;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import com.google.pubsub.v1.ProjectSubscriptionName;
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
 * @author Emmanouil Gkatziouras
 */
public class DefaultSubscriberFactory implements SubscriberFactory {

	private final String projectId;

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

	private HealthTrackerRegistry healthTrackerRegistry;

	private PubSubConfiguration pubSubConfiguration;

	private ConcurrentMap<String, ThreadPoolTaskScheduler> threadPoolTaskSchedulerMap = new ConcurrentHashMap<>();

	private ThreadPoolTaskScheduler globalScheduler;

	private ConcurrentMap<String, FlowControlSettings> flowControlSettingsMap = new ConcurrentHashMap<>();

	private FlowControlSettings globalFlowControlSettings;

	private ConcurrentHashMap<String, ExecutorProvider> executorProviderMap = new ConcurrentHashMap<>();

	private ExecutorProvider defaultExecutorProvider;

	private Code[] retryableCodes;

	/**
	 * Default {@link DefaultSubscriberFactory} constructor.
	 * @param projectIdProvider provides the default GCP project ID for selecting the
	 *     subscriptions
	 * @deprecated Use the new {@link DefaultSubscriberFactory (GcpProjectIdProvider,PubSubConfiguration)} instead
	 */
	@Deprecated
	public DefaultSubscriberFactory(GcpProjectIdProvider projectIdProvider) {
		this(projectIdProvider, new PubSubConfiguration());
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

		Assert.notNull(pubSubConfiguration, "The pub/sub configuration can't be null.");
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
	 */
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
	 * @param subscriberStubRetrySettings parameters for retrying pull requests when they
	 *     fail, including jitter logic, timeout, and exponential backoff
	 */
	public void setSubscriberStubRetrySettings(RetrySettings subscriberStubRetrySettings) {
		this.subscriberStubRetrySettings = subscriberStubRetrySettings;
	}

	/**
	 * Set the retryable codes for subscriber pull settings.
	 * @param retryableCodes pull RPC response codes that should be retried.
	 */
	public void setRetryableCodes(Code[] retryableCodes) {
		this.retryableCodes = retryableCodes;
	}

	/**
	 * Set the health tracker chain for the generated subscriptions.
	 * @param healthTrackerRegistry parameter for registering health trackers when creating subscriptions
	 */
	public void setHealthTrackerRegistry(HealthTrackerRegistry healthTrackerRegistry) {
		this.healthTrackerRegistry = healthTrackerRegistry;
	}

	@Override
	public Subscriber createSubscriber(String subscriptionName, MessageReceiver receiver) {
		ProjectSubscriptionName projectSubscriptionName = PubSubSubscriptionUtils
				.toProjectSubscriptionName(subscriptionName, this.projectId);

		boolean shouldAddToHealthCheck = shouldAddToHealthCheck(subscriptionName);
		if (shouldAddToHealthCheck) {
			receiver = healthTrackerRegistry.wrap(projectSubscriptionName, receiver);
		}

		Subscriber.Builder subscriberBuilder = Subscriber.newBuilder(projectSubscriptionName, receiver);

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

		FlowControlSettings flowControl = getFlowControlSettings(subscriptionName);
		if (flowControl != null) {
			subscriberBuilder.setFlowControlSettings(flowControl);
		}

		Duration ackExtensionPeriod = getMaxAckExtensionPeriod(subscriptionName);
		if (ackExtensionPeriod != null) {
			subscriberBuilder
					.setMaxAckExtensionPeriod(ackExtensionPeriod);
		}

		// Set the number of pull workers.
		Integer pullCount = getPullCount(subscriptionName);
		if (pullCount != null) {
			subscriberBuilder.setParallelPullCount(pullCount);
		}

		Subscriber subscriber = subscriberBuilder.build();

		if (shouldAddToHealthCheck) {
			healthTrackerRegistry.addListener(subscriber);
		}

		return subscriber;
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
	public SubscriberStub createSubscriberStub() {
		try {
			return GrpcSubscriberStub.create(buildGlobalSubscriberStubSettings());
		}
		catch (IOException ex) {
			throw new PubSubException("Error creating the SubscriberStub", ex);
		}
	}

	@Override
	public SubscriberStub createSubscriberStub(String subscriptionName) {
		try {
			return GrpcSubscriberStub.create(buildSubscriberStubSettings(subscriptionName));
		}
		catch (IOException ex) {
			throw new PubSubException("Error creating the SubscriberStub", ex);
		}
	}

	SubscriberStubSettings buildGlobalSubscriberStubSettings() throws IOException {
		SubscriberStubSettings.Builder subscriberStubSettings = buildStubSettingsWithoutConfigurations();

		if (this.pullEndpoint != null) {
			subscriberStubSettings.setEndpoint(this.pullEndpoint);
		}
		else {
			applyGlobalPullEndpoint(subscriberStubSettings);
		}

		ExecutorProvider executor = this.executorProvider != null ? this.executorProvider
				: createGlobalExecutorProvider();
		if (executor != null) {
			subscriberStubSettings.setBackgroundExecutorProvider(executor);
		}

		if (this.subscriberStubRetrySettings != null) {
			subscriberStubSettings.pullSettings().setRetrySettings(this.subscriberStubRetrySettings);
		}
		else {
			applyGlobalRetrySettings(subscriberStubSettings);
		}

		if (this.retryableCodes != null) {
			subscriberStubSettings.pullSettings().setRetryableCodes(
					this.retryableCodes);
		}

		return subscriberStubSettings.build();
	}

	private void applyGlobalPullEndpoint(SubscriberStubSettings.Builder subscriberStubSettings) {
		String endpoint = this.pubSubConfiguration.getSubscriber().getPullEndpoint();
		if (endpoint != null) {
			subscriberStubSettings.setEndpoint(endpoint);
		}
	}

	private void applyGlobalRetrySettings(SubscriberStubSettings.Builder subscriberStubSettings) {
		PubSubConfiguration.Retry retry = this.pubSubConfiguration.getSubscriber().getRetry();
		RetrySettings retrySettings = buildRetrySettings(retry);
		if (retrySettings != null) {
			subscriberStubSettings.pullSettings().setRetrySettings(retrySettings);
		}
	}

	SubscriberStubSettings buildSubscriberStubSettings(String subscriptionName) throws IOException {
		SubscriberStubSettings.Builder subscriberStubSettings = buildStubSettingsWithoutConfigurations();

		String endpoint = getPullEndpoint(subscriptionName);
		if (endpoint != null) {
			subscriberStubSettings.setEndpoint(endpoint);
		}

		ExecutorProvider executor = getExecutorProvider(subscriptionName);
		if (executor != null) {
			subscriberStubSettings.setBackgroundExecutorProvider(executor);
		}

		RetrySettings retrySettings = getRetrySettings(subscriptionName);
		if (retrySettings != null) {
			subscriberStubSettings.pullSettings().setRetrySettings(retrySettings);
		}

		if (this.retryableCodes != null) {
			subscriberStubSettings.pullSettings().setRetryableCodes(
					this.retryableCodes);
		}

		return subscriberStubSettings.build();
	}

	SubscriberStubSettings.Builder buildStubSettingsWithoutConfigurations() {

		SubscriberStubSettings.Builder subscriberStubSettings = SubscriberStubSettings.newBuilder();

		if (this.credentialsProvider != null) {
			subscriberStubSettings.setCredentialsProvider(this.credentialsProvider);
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

		if (this.retryableCodes != null) {
			subscriberStubSettings.pullSettings().setRetryableCodes(
					this.retryableCodes);
		}

		return subscriberStubSettings;
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
		return getExecutorProviderFromConfigurations(subscriptionName);
	}

	/**
	 * Creates {@link ExecutorProvider} given a subscription name.
	 * @param subscriptionName subscription name
	 * @return executor provider
	 */
	ExecutorProvider getExecutorProviderFromConfigurations(String subscriptionName) {
		ThreadPoolTaskScheduler scheduler = fetchThreadPoolTaskScheduler(subscriptionName);
		if (scheduler == null) {
			return null;
		}
		if (!scheduler.equals(this.globalScheduler)) {
			return createExecutorProvider(subscriptionName, scheduler);
		}
		return createGlobalExecutorProvider();
	}

	ExecutorProvider createGlobalExecutorProvider() {
		if (this.globalScheduler == null) {
			return null;
		}
		if (this.defaultExecutorProvider != null) {
			return this.defaultExecutorProvider;
		}
		this.globalScheduler.initialize();
		this.defaultExecutorProvider = FixedExecutorProvider.create(this.globalScheduler.getScheduledExecutor());
		return this.defaultExecutorProvider;
	}

	ExecutorProvider createExecutorProvider(String subscriptionName, ThreadPoolTaskScheduler scheduler) {
		if (this.executorProviderMap.containsKey(subscriptionName)) {
			return this.executorProviderMap.get(subscriptionName);
		}
		scheduler.initialize();
		ExecutorProvider executor = FixedExecutorProvider.create(scheduler.getScheduledExecutor());
		return this.executorProviderMap.computeIfAbsent(subscriptionName, k -> executor);
	}

	/**
	 * Returns {@link ThreadPoolTaskScheduler} given a subscription name. If
	 * subscription-specific scheduler for the subscription name is not found then return a
	 * global threadPoolTaskScheduler, otherwise, return the subscription-specific scheduler.
	 * @param subscriptionName subscription name
	 * @return thread pool scheduler
	 */
	public ThreadPoolTaskScheduler fetchThreadPoolTaskScheduler(String subscriptionName) {
		String fullyQualifiedName = PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, projectId)
				.toString();
		if (this.threadPoolTaskSchedulerMap.containsKey(fullyQualifiedName)) {
			return threadPoolTaskSchedulerMap.get(fullyQualifiedName);
		}
		return this.globalScheduler;
	}

	Map<String, ExecutorProvider> getExecutorProviderMap() {
		return this.executorProviderMap;
	}

	ExecutorProvider getDefaultExecutorProvider() {
		return this.defaultExecutorProvider;
	}

	/**
	 * Creates {@link RetrySettings}, given subscriber retry properties. Returns null if none
	 * of the retry settings are set. Note that if retry settings are set using a
	 * Spring-managed bean then subscription-specific settings in application.properties are
	 * ignored.
	 * @param subscriptionName subscription name
	 * @return retry settings for subscriber
	 */
	public RetrySettings getRetrySettings(String subscriptionName) {
		if (this.subscriberStubRetrySettings != null) {
			return this.subscriberStubRetrySettings;
		}

		PubSubConfiguration.Retry retryProperties = this.pubSubConfiguration
				.computeSubscriberRetrySettings(subscriptionName, this.projectId);
		return buildRetrySettings(retryProperties);
	}

	public RetrySettings buildRetrySettings(PubSubConfiguration.Retry retryProperties) {
		RetrySettings.Builder builder = RetrySettings.newBuilder();
		boolean shouldBuild = ifSet(retryProperties.getInitialRetryDelaySeconds(),
				x -> builder.setInitialRetryDelay(Duration.ofSeconds(x)));
		shouldBuild |= ifSet(retryProperties.getInitialRpcTimeoutSeconds(),
				x -> builder.setInitialRpcTimeout(Duration.ofSeconds(x)));
		shouldBuild |= ifSet(retryProperties.getMaxAttempts(), builder::setMaxAttempts);
		shouldBuild |= ifSet(retryProperties.getMaxRetryDelaySeconds(),
				x -> builder.setMaxRetryDelay(Duration.ofSeconds(x)));
		shouldBuild |= ifSet(retryProperties.getMaxRpcTimeoutSeconds(),
				x -> builder.setMaxRpcTimeout(Duration.ofSeconds(x)));
		shouldBuild |= ifSet(retryProperties.getRetryDelayMultiplier(), builder::setRetryDelayMultiplier);
		shouldBuild |= ifSet(retryProperties.getTotalTimeoutSeconds(),
				x -> builder.setTotalTimeout(Duration.ofSeconds(x)));
		shouldBuild |= ifSet(retryProperties.getRpcTimeoutMultiplier(), builder::setRpcTimeoutMultiplier);
		return shouldBuild ? builder.build() : null;
	}

	/**
	 * Fetches subscriber {@link FlowControlSettings}. User-provided bean takes precedence
	 * over properties from application.properties. Returns subscription-specific flow control
	 * settings if present, otherwise, returns global subscriber settings.
	 * @param subscriptionName subscription name
	 * @return flow control settings for subscriber
	 */
	public FlowControlSettings getFlowControlSettings(
			String subscriptionName) {
		String fullyQualifiedName = PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, projectId)
				.toString();
		if (this.flowControlSettings != null) {
			return this.flowControlSettings;
		}
		if (flowControlSettingsMap.containsKey(fullyQualifiedName)) {
			return this.flowControlSettingsMap.get(fullyQualifiedName);
		}
		return this.globalFlowControlSettings;
	}

	private <T> boolean ifSet(T property, Consumer<T> consumer) {
		if (property != null) {
			consumer.accept(property);
			return true;
		}
		return false;
	}

	Duration getMaxAckExtensionPeriod(String subscriptionName) {
		if (this.maxAckExtensionPeriod != null) {
			return this.maxAckExtensionPeriod;
		}
		return Duration
				.ofSeconds(this.pubSubConfiguration.computeMaxAckExtensionPeriod(subscriptionName, projectId));
	}

	Integer getPullCount(String subscriptionName) {
		if (this.parallelPullCount != null) {
			return this.parallelPullCount;
		}
		return this.pubSubConfiguration.computeParallelPullCount(subscriptionName, projectId);
	}

	String getPullEndpoint(String subscriptionName) {
		if (this.pullEndpoint != null) {
			return this.pullEndpoint;
		}
		return this.pubSubConfiguration.computePullEndpoint(subscriptionName, projectId);
	}

	public void setThreadPoolTaskSchedulerMap(
			ConcurrentMap<String, ThreadPoolTaskScheduler> threadPoolTaskSchedulerMap) {
		this.threadPoolTaskSchedulerMap = threadPoolTaskSchedulerMap;
	}

	public void setGlobalScheduler(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
		this.globalScheduler = threadPoolTaskScheduler;
	}

	public void setFlowControlSettingsMap(ConcurrentMap<String, FlowControlSettings> flowControlSettingsMap) {
		this.flowControlSettingsMap = flowControlSettingsMap;
	}

	public void setGlobalFlowControlSettings(FlowControlSettings flowControlSettings) {
		this.globalFlowControlSettings = flowControlSettings;
	}

	private boolean shouldAddToHealthCheck(String subscriptionName) {
		if (healthTrackerRegistry == null) {
			return false;
		}

		ProjectSubscriptionName projectSubscriptionName = PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, this.projectId);
		return !healthTrackerRegistry.isTracked(projectSubscriptionName);
	}

}
