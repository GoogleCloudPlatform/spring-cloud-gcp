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
import java.util.function.Consumer;

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

	private ApiClock apiClock;

	private RetrySettings subscriberStubRetrySettings;

	private PubSubConfiguration pubSubConfiguration;

	private ThreadPoolTaskScheduler threadPoolTaskScheduler;

	/**
	 * Default {@link DefaultSubscriberFactory} constructor.
	 * @param projectIdProvider provides the default GCP project ID for selecting the
	 *     subscriptions
	 * @deprecated Use the new {@link DefaultSubscriberFactory
	 * (GcpProjectIdProvider,PubSubConfiguration)} instead
	 */
	@Deprecated
	public DefaultSubscriberFactory(GcpProjectIdProvider projectIdProvider) {
		this(projectIdProvider, new PubSubConfiguration());
	}

	/**
	 * Default {@link DefaultSubscriberFactory} constructor.
	 * @param projectIdProvider provides the default GCP project ID for selecting the
	 *     subscriptions
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
	 * Set the provider for the subscribers' executor. Useful to specify the number of threads
	 * to be used by each executor.
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
	 * Set the flow control for the subscribers, including the behaviour for when the flow
	 * limits are hit.
	 * @param flowControlSettings the flow control settings to set
	 */
	public void setFlowControlSettings(FlowControlSettings flowControlSettings) {
		this.flowControlSettings = flowControlSettings;
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

	@Override
	public Subscriber createSubscriber(String subscriptionName, MessageReceiver receiver) {
		Subscriber.Builder subscriberBuilder = Subscriber.newBuilder(
				PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, this.projectId), receiver);

		PubSubConfiguration.Subscriber subscriberProperties = this.pubSubConfiguration
				.getSubscriber(subscriptionName);

		if (this.channelProvider != null) {
			subscriberBuilder.setChannelProvider(this.channelProvider);
		}

		subscriberBuilder.setExecutorProvider(getExecutorProvider(subscriberProperties, subscriptionName));


		if (this.credentialsProvider != null) {
			subscriberBuilder.setCredentialsProvider(this.credentialsProvider);
		}

		if (this.headerProvider != null) {
			subscriberBuilder.setHeaderProvider(this.headerProvider);
		}

		if (this.systemExecutorProvider != null) {
			subscriberBuilder.setSystemExecutorProvider(this.systemExecutorProvider);
		}

		FlowControlSettings flowControl = getFlowControlSettings(subscriberProperties.getFlowControl());
		if (flowControl != null) {
			subscriberBuilder.setFlowControlSettings(flowControl);
		}

		// Set the maximum period the ack timeout is extended by.
		if (subscriberProperties.getMaxAckExtensionPeriod() != null) {
			subscriberBuilder
					.setMaxAckExtensionPeriod(Duration.ofSeconds(subscriberProperties.getMaxAckExtensionPeriod()));
		}

		// Set the number of pull workers.
		if (subscriberProperties.getParallelPullCount() != null) {
			subscriberBuilder.setParallelPullCount(subscriberProperties.getParallelPullCount());
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

		PubSubConfiguration.Subscriber subscriberProperties = this.pubSubConfiguration
				.getSubscriber(subscriptionName);

		if (this.credentialsProvider != null) {
			subscriberStubSettings.setCredentialsProvider(this.credentialsProvider);
		}

		// Set the endpoint for synchronous pulling messages.
		if (subscriberProperties.getPullEndpoint() != null) {
			subscriberStubSettings.setEndpoint(subscriberProperties.getPullEndpoint());
		}

		// Set the endpoint for synchronous pulling messages.
		subscriberStubSettings.setExecutorProvider(getExecutorProvider(subscriberProperties, subscriptionName));

		if (this.headerProvider != null) {
			subscriberStubSettings.setHeaderProvider(this.headerProvider);
		}

		if (this.channelProvider != null) {
			subscriberStubSettings.setTransportChannelProvider(this.channelProvider);
		}

		if (this.apiClock != null) {
			subscriberStubSettings.setClock(this.apiClock);
		}

		RetrySettings retrySettings = getRetrySettings(subscriberProperties.getRetry());
		if (retrySettings != null) {
			subscriberStubSettings.pullSettings().setRetrySettings(retrySettings);
		}

		try {
			return GrpcSubscriberStub.create(subscriberStubSettings.build());
		}
		catch (IOException ex) {
			throw new RuntimeException("Error creating the SubscriberStub", ex);
		}
	}

	/**
	 * Creates {@link ExecutorProvider} given subscriber properties.
	 * @param subscriber subscriber properties
	 * @return executor provider
	 */
	public ExecutorProvider getExecutorProvider(PubSubConfiguration.Subscriber subscriber, String subscriptionName) {
		if (this.executorProvider != null) {
			return this.executorProvider;
		}
		this.threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		this.threadPoolTaskScheduler.setPoolSize(subscriber.getExecutorThreads());
		String threadNamePrefix = "gcp-pubsub-subscriber" + "-" + subscriptionName;
		this.threadPoolTaskScheduler.setThreadNamePrefix(threadNamePrefix);
		this.threadPoolTaskScheduler.setDaemon(true);
		this.threadPoolTaskScheduler.initialize();

		return FixedExecutorProvider.create(this.threadPoolTaskScheduler.getScheduledExecutor());
	}

	/**
	 * Creates {@link RetrySettings}, given subscriber retry properties. Returns null if none
	 * of the retry settings are set. Note that if retry settings are set using a
	 * Spring-managed bean then subscription-specific settings in application.properties are
	 * ignored.
	 * @param retryProperties subscriber retry properties
	 * @return retry settings for subscriber
	 */
	RetrySettings getRetrySettings(PubSubConfiguration.Retry retryProperties) {
		if (this.subscriberStubRetrySettings != null) {
			return this.subscriberStubRetrySettings;
		}

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

	private <T> boolean ifSet(T property, Consumer<T> consumer) {
		if (property != null) {
			consumer.accept(property);
			return true;
		}
		return false;
	}

	/**
	 * Creates {@link FlowControlSettings}, given subscriber flow control settings. Returns
	 * null if none of the flow control settings are set. Note that if flow control settings
	 * are set using a Spring-managed bean then subscription-specific settings in
	 * application.properties are ignored.
	 * @param flowControl flow control settings
	 * @return flow control settings for subscriber
	 */
	FlowControlSettings getFlowControlSettings(
			PubSubConfiguration.FlowControl flowControl) {
		if (this.flowControlSettings != null) {
			return this.flowControlSettings;
		}
		FlowControlSettings.Builder builder = FlowControlSettings.newBuilder();
		boolean shouldBuild = ifSet(flowControl.getLimitExceededBehavior(), builder::setLimitExceededBehavior);
		shouldBuild |= ifSet(flowControl.getMaxOutstandingElementCount(), builder::setMaxOutstandingElementCount);
		shouldBuild |= ifSet(flowControl.getMaxOutstandingRequestBytes(), builder::setMaxOutstandingRequestBytes);

		return shouldBuild ? builder.build() : null;
	}

	@PreDestroy
	public void clearScheduler() {
		this.threadPoolTaskScheduler.shutdown();
	}
}
