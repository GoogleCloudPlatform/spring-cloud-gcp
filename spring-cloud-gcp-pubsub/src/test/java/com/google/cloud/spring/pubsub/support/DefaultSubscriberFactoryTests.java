/*
 * Copyright 2017-2018 the original author or authors.
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

import java.util.Map;

import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.pubsub.v1.PullRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.Duration;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the subscriber factory.
 *
 * @author João André Martins
 * @author Chengyuan Zhao
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultSubscriberFactoryTests {

	@Mock
	ExecutorProvider mockExecutorProvider;
	@Mock
	private CredentialsProvider credentialsProvider;
	@Mock
	private PubSubConfiguration mockPubSubConfiguration;
	@Mock
	private PubSubConfiguration.Subscriber mockDefaultSubscriber1;
	@Mock
	private PubSubConfiguration.Subscriber mockDefaultSubscriber2;
	@Mock
	private PubSubConfiguration.Subscriber mockCustomSubscriber1;
	@Mock
	private PubSubConfiguration.Subscriber mockCustomSubscriber2;
	@Mock
	private ThreadPoolTaskScheduler mockScheduler1;
	@Mock
	private ThreadPoolTaskScheduler mockScheduler2;

	/**
	 * used to check exception messages and types.
	 */
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testNewSubscriber() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "angeldust");
		factory.setCredentialsProvider(this.credentialsProvider);

		Subscriber subscriber = factory.createSubscriber("midnight cowboy", (message, consumer) -> {
		});

		assertThat(subscriber.getSubscriptionNameString())
				.isEqualTo("projects/angeldust/subscriptions/midnight cowboy");
	}

	@Test
	public void testNewSubscriber_constructorWithPubSubConfiguration() {
		GcpProjectIdProvider projectIdProvider = () -> "angeldust";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		when(mockPubSubConfiguration.getSubscriber("midnight cowboy", projectIdProvider.getProjectId()))
				.thenReturn(new PubSubConfiguration.Subscriber());
		factory.setCredentialsProvider(this.credentialsProvider);

		Subscriber subscriber = factory.createSubscriber("midnight cowboy", (message, consumer) -> {
		});

		assertThat(subscriber.getSubscriptionNameString())
				.isEqualTo("projects/angeldust/subscriptions/midnight cowboy");

		factory.getThreadPoolTaskSchedulerMap().get("midnight cowboy").shutdown();
	}

	@Test
	public void testNewSubscriber_constructorWithPubSubConfiguration_pubSubConfigurationIsNull() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "angeldust", null);
		factory.setCredentialsProvider(this.credentialsProvider);

		Subscriber subscriber = factory.createSubscriber("midnight cowboy", (message, consumer) -> {
		});

		assertThat(subscriber.getSubscriptionNameString())
				.isEqualTo("projects/angeldust/subscriptions/midnight cowboy");
	}

	@Test
	public void testNewDefaultSubscriberFactory_nullProjectProvider() {
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("The project ID provider can't be null.");
		new DefaultSubscriberFactory(null);
	}

	@Test
	public void testNewDefaultSubscriberFactory_nullProject() {
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("The project ID can't be null or empty.");
		new DefaultSubscriberFactory(() -> null);
	}

	@Test
	public void testCreatePullRequest_greaterThanZeroMaxMessages() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project");
		factory.setCredentialsProvider(this.credentialsProvider);

		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("The maxMessages must be greater than 0.");
		factory.createPullRequest("test", -1, true);
	}

	@Test
	public void testCreatePullRequest_nonNullMaxMessages() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project");
		factory.setCredentialsProvider(this.credentialsProvider);

		PullRequest request = factory.createPullRequest("test", null, true);
		assertThat(request.getMaxMessages()).isEqualTo(Integer.MAX_VALUE);
	}

	@Test
	public void testCreateSubscriber_validateSetProperties() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		factory.setCredentialsProvider(this.credentialsProvider);
		PubSubConfiguration.Subscriber subscriber = new PubSubConfiguration.Subscriber();

		subscriber.getFlowControl().setLimitExceededBehavior(FlowController.LimitExceededBehavior.Ignore);
		subscriber.setMaxAckExtensionPeriod(2L);
		subscriber.setParallelPullCount(2);
		when(mockPubSubConfiguration.getSubscriber("defaultSubscription", "project"))
				.thenReturn(subscriber);

		Subscriber expectedSubscriber = factory.createSubscriber("defaultSubscription", (message, consumer) -> {
		});

		assertThat(expectedSubscriber.getFlowControlSettings().getLimitExceededBehavior())
				.isEqualTo(FlowController.LimitExceededBehavior.Ignore);
		assertThat(expectedSubscriber).hasFieldOrPropertyWithValue("maxAckExtensionPeriod", Duration.ofSeconds(2L))
				.hasFieldOrPropertyWithValue("numPullers", 2);

		factory.getThreadPoolTaskSchedulerMap().get("defaultSubscription").shutdown();
	}

	@Test
	public void testGetExecutorProvider_userProvidedBean() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", null);
		factory.setExecutorProvider(mockExecutorProvider);

		assertThat(factory.getExecutorProvider("name"))
				.isSameAs(mockExecutorProvider);
	}

	@Test
	public void testGetExecutorProvider_allSubscribersWithDefaultConfig_oneCreated() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		when(mockPubSubConfiguration.getSubscriber("defaultSubscription1", projectIdProvider.getProjectId())).thenReturn(mockDefaultSubscriber1);
		when(mockDefaultSubscriber1.getExecutorThreads()).thenReturn(4);
		when(mockDefaultSubscriber1.isGlobal()).thenReturn(true);
		when(mockPubSubConfiguration.getSubscriber("defaultSubscription2", projectIdProvider.getProjectId())).thenReturn(mockDefaultSubscriber2);
		when(mockDefaultSubscriber2.isGlobal()).thenReturn(true);

		ExecutorProvider executorProviderForSub1 = factory.getExecutorProvider("defaultSubscription1");
		ExecutorProvider executorProviderForSub2 = factory.getExecutorProvider("defaultSubscription2");

		// Verify that only one executor provider and one scheduler are created
		assertThat(executorProviderForSub1).isNotNull();
		assertThat(executorProviderForSub2).isNotNull();
		assertThat(factory.getExecutorProviderMap()).hasSize(1);
		assertThat(factory.getThreadPoolTaskSchedulerMap()).hasSize(1);
	}

	@Test
	public void testGetExecutorProvider_allSubscribersWithCustomConfigs_manyCreated() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		when(mockPubSubConfiguration.getSubscriber("customSubscription1", projectIdProvider.getProjectId())).thenReturn(mockCustomSubscriber1);
		when(mockCustomSubscriber1.getExecutorThreads()).thenReturn(4);
		when(mockPubSubConfiguration.getSubscriber("customSubscription2", projectIdProvider.getProjectId())).thenReturn(mockCustomSubscriber2);
		when(mockCustomSubscriber2.getExecutorThreads()).thenReturn(4);

		ExecutorProvider executorProviderForSub1 = factory.getExecutorProvider("customSubscription1");
		ExecutorProvider executorProviderForSub2 = factory.getExecutorProvider("customSubscription2");

		// Verify that two executor providers and two schedulers are created
		assertThat(executorProviderForSub1).isNotNull();
		assertThat(executorProviderForSub2).isNotNull();
		assertThat(factory.getExecutorProviderMap()).hasSize(2);
		assertThat(factory.getThreadPoolTaskSchedulerMap()).hasSize(2);
	}

	@Test
	public void testGetExecutorProvider_subscribersWithDefaultAndCustomConfigs() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);

		// One subscriber with subscription-specific subscriber properties
		when(mockPubSubConfiguration.getSubscriber("customSubscription1", projectIdProvider.getProjectId())).thenReturn(mockCustomSubscriber1);
		when(mockCustomSubscriber1.getExecutorThreads()).thenReturn(4);

		// Two subscribers with default/global subscriber properties
		when(mockPubSubConfiguration.getSubscriber("defaultSubscription1", projectIdProvider.getProjectId())).thenReturn(mockDefaultSubscriber1);
		when(mockDefaultSubscriber1.isGlobal()).thenReturn(true);
		when(mockDefaultSubscriber1.getExecutorThreads()).thenReturn(4);
		when(mockPubSubConfiguration.getSubscriber("defaultSubscription2", projectIdProvider.getProjectId())).thenReturn(mockDefaultSubscriber2);
		when(mockDefaultSubscriber2.isGlobal()).thenReturn(true);

		ExecutorProvider executorProviderForCustom1 = factory.getExecutorProvider("customSubscription1");
		ExecutorProvider executorProviderForDefault1 = factory.getExecutorProvider("defaultSubscription1");
		ExecutorProvider executorProviderForDefault2 = factory.getExecutorProvider("defaultSubscription2");

		// Verify that only two executor providers and two schedulers are created
		assertThat(executorProviderForCustom1).isNotNull();
		assertThat(executorProviderForDefault1).isNotNull();
		assertThat(executorProviderForDefault2).isNotNull();
		assertThat(factory.getExecutorProviderMap()).hasSize(2);
		assertThat(factory.getThreadPoolTaskSchedulerMap()).hasSize(2);
	}

	@Test
	public void testGetExecutorProvider_pubSubConfigurationIsNull() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", null);

		assertThat(factory.getExecutorProvider("name"))
				.isNull();
	}

	@Test
	public void testCreateThreadPoolTaskScheduler() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		when(mockCustomSubscriber1.getExecutorThreads()).thenReturn(6);

		ThreadPoolTaskScheduler threadPoolTaskScheduler = factory
				.createThreadPoolTaskScheduler(mockCustomSubscriber1, "subscription-name");

		assertThat(
				threadPoolTaskScheduler.getThreadNamePrefix())
						.isEqualTo("gcp-pubsub-subscriber-subscription-name");
		assertThat(
				threadPoolTaskScheduler.getPoolSize())
						.isEqualTo(6);
		assertThat(
				threadPoolTaskScheduler.isDaemon())
						.isTrue();
	}

	@Test
	public void shutdownScheduler() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		Map<String, ThreadPoolTaskScheduler> threadPoolTaskSchedulerMap = factory.getThreadPoolTaskSchedulerMap();
		threadPoolTaskSchedulerMap.put("sub1", mockScheduler1);
		threadPoolTaskSchedulerMap.put("sub2", mockScheduler2);

		factory.clearScheduler();

		verify(mockScheduler1, times(1)).shutdown();
		verify(mockScheduler2, times(1)).shutdown();
	}

	@Test
	public void testGetRetrySettings_userProvidedBean() {
		RetrySettings expectedRetrySettings = RetrySettings.newBuilder()
				.setTotalTimeout(Duration.ofSeconds(10))
				.setInitialRetryDelay(Duration.ofSeconds(10))
				.setRetryDelayMultiplier(10.0)
				.setInitialRpcTimeout(Duration.ofSeconds(10))
				.setMaxRetryDelay(Duration.ofSeconds(10))
				.setMaxAttempts(10)
				.setRpcTimeoutMultiplier(10.0)
				.setMaxRpcTimeout(Duration.ofSeconds(10))
				.build();
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", null);
		factory.setSubscriberStubRetrySettings(expectedRetrySettings);

		RetrySettings actualRetrySettings = factory.getRetrySettings("defaultSubscriber");

		assertThat(actualRetrySettings.getTotalTimeout()).isEqualTo(Duration.ofSeconds(10));
		assertThat(actualRetrySettings.getInitialRetryDelay()).isEqualTo(Duration.ofSeconds(10));
		assertThat(actualRetrySettings.getRetryDelayMultiplier()).isEqualTo(10.0);
		assertThat(actualRetrySettings.getInitialRpcTimeout()).isEqualTo(Duration.ofSeconds(10));
		assertThat(actualRetrySettings.getMaxRetryDelay()).isEqualTo(Duration.ofSeconds(10));
		assertThat(actualRetrySettings.getMaxAttempts()).isEqualTo(10);
		assertThat(actualRetrySettings.getRpcTimeoutMultiplier()).isEqualTo(10.0);
		assertThat(actualRetrySettings.getMaxRpcTimeout()).isEqualTo(Duration.ofSeconds(10));
	}

	@Test
	public void testGetRetrySettings_configurationIsPresent() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		when(mockPubSubConfiguration.getSubscriber("defaultSubscription1", projectIdProvider.getProjectId()))
				.thenReturn(mockDefaultSubscriber1);
		PubSubConfiguration.Retry retrySettings = new PubSubConfiguration.Retry();
		retrySettings.setTotalTimeoutSeconds(10L);
		retrySettings.setInitialRetryDelaySeconds(10L);
		retrySettings.setRetryDelayMultiplier(10.0);
		retrySettings.setMaxRetryDelaySeconds(10L);
		retrySettings.setMaxAttempts(10);
		retrySettings.setInitialRpcTimeoutSeconds(10L);
		retrySettings.setRpcTimeoutMultiplier(10.0);
		retrySettings.setMaxRpcTimeoutSeconds(10L);

		when(mockDefaultSubscriber1.getRetry()).thenReturn(retrySettings);

		RetrySettings actualRetrySettings = factory.getRetrySettings("defaultSubscription1");

		assertThat(actualRetrySettings.getTotalTimeout()).isEqualTo(Duration.ofSeconds(10));
		assertThat(actualRetrySettings.getInitialRetryDelay()).isEqualTo(Duration.ofSeconds(10));
		assertThat(actualRetrySettings.getRetryDelayMultiplier()).isEqualTo(10.0);
		assertThat(actualRetrySettings.getInitialRpcTimeout()).isEqualTo(Duration.ofSeconds(10));
		assertThat(actualRetrySettings.getMaxRetryDelay()).isEqualTo(Duration.ofSeconds(10));
		assertThat(actualRetrySettings.getMaxAttempts()).isEqualTo(10);
		assertThat(actualRetrySettings.getRpcTimeoutMultiplier()).isEqualTo(10.0);
		assertThat(actualRetrySettings.getMaxRpcTimeout()).isEqualTo(Duration.ofSeconds(10));
	}

	@Test
	public void testGetRetrySettings_configurationIsNull() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, null);

		assertThat(factory.getRetrySettings("defaultSubscription1")).isNull();
	}

	@Test
	public void testGetFlowControlSettings_userProvidedBean() {
		FlowControlSettings expectedFlowSettings = FlowControlSettings.newBuilder()
				.setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block).setMaxOutstandingElementCount(10L)
				.setMaxOutstandingRequestBytes(10L)
				.build();
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", null);
		factory.setFlowControlSettings(expectedFlowSettings);

		FlowControlSettings actualFlowSettings = factory.getFlowControlSettings("defaultSubscription1");

		assertThat(actualFlowSettings.getLimitExceededBehavior()).isEqualTo(FlowController.LimitExceededBehavior.Block);
		assertThat(actualFlowSettings.getMaxOutstandingElementCount()).isEqualTo(10L);
		assertThat(actualFlowSettings.getMaxOutstandingRequestBytes()).isEqualTo(10L);
	}

	@Test
	public void testGetFlowControlSettings_configurationIsPresent() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		when(mockPubSubConfiguration.getSubscriber("defaultSubscription1", projectIdProvider.getProjectId()))
				.thenReturn(mockDefaultSubscriber1);
		PubSubConfiguration.FlowControl flowControl = new PubSubConfiguration.FlowControl();
		flowControl.setMaxOutstandingRequestBytes(10L);
		when(mockDefaultSubscriber1.getFlowControl()).thenReturn(flowControl);

		FlowControlSettings actualFlowSettings = factory.getFlowControlSettings("defaultSubscription1");

		assertThat(actualFlowSettings.getMaxOutstandingRequestBytes()).isEqualTo(10L);
		assertThat(actualFlowSettings.getMaxOutstandingElementCount()).isNull();
		assertThat(actualFlowSettings.getLimitExceededBehavior()).isEqualTo(FlowController.LimitExceededBehavior.Block);
	}

	@Test
	public void testGetFlowControlSettings_configurationIsNull() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, null);

		assertThat(factory.getFlowControlSettings("defaultSubscription1")).isNull();
	}
}
