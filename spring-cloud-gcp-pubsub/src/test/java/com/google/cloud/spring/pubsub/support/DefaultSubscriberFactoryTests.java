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

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.gax.rpc.TransportChannel;
import com.google.cloud.NoCredentials;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.cloud.spring.pubsub.core.PubSubException;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PullRequest;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.Duration;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
	private ExecutorProvider mockExecutorProvider;

	@Mock
	private TransportChannel mockTransportChannel;

	@Mock
	private ApiCallContext mockApiCallContext;

	@Mock
	private CredentialsProvider credentialsProvider;

	@Mock
	private PubSubConfiguration mockPubSubConfiguration;

	@Mock
	private PubSubConfiguration.Subscriber mockSubscriber;

	@Mock
	private ThreadPoolTaskScheduler mockScheduler;

	@Mock
	private ThreadPoolTaskScheduler mockGlobalScheduler;
	@Mock
	private HealthTrackerRegistry healthTrackerRegistry;
	@Mock
	private ExecutorProvider executorProvider;

	/**
	 * used to check exception messages and types.
	 */
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() {
		when(this.mockTransportChannel.getEmptyCallContext()).thenReturn(this.mockApiCallContext);
		when(this.mockApiCallContext.withCredentials(any())).thenReturn(this.mockApiCallContext);
		when(this.mockApiCallContext.withTransportChannel(any())).thenReturn(this.mockApiCallContext);
	}

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
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, new PubSubConfiguration());
		factory.setCredentialsProvider(this.credentialsProvider);

		Subscriber subscriber = factory.createSubscriber("midnight cowboy", (message, consumer) -> {
		});

		assertThat(subscriber.getSubscriptionNameString())
				.isEqualTo("projects/angeldust/subscriptions/midnight cowboy");

	}

	@Test
	public void testNewSubscriber_constructorWithPubSubConfiguration_nullPubSubConfiguration() {
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("The pub/sub configuration can't be null.");
		new DefaultSubscriberFactory(() -> "angeldust", null);
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
	public void testGetExecutorProvider_userProvidedBean() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		factory.setExecutorProvider(mockExecutorProvider);

		assertThat(factory.getExecutorProvider("name"))
				.isSameAs(mockExecutorProvider);
	}

	@Test
	public void testGetExecutorProvider_allSubscribersWithDefaultConfig_oneCreated() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		factory.setGlobalScheduler(mockGlobalScheduler);

		ExecutorProvider executorProviderForSub1 = factory.getExecutorProvider("defaultSubscription1");
		ExecutorProvider executorProviderForSub2 = factory.getExecutorProvider("defaultSubscription2");

		// Verify that only global executor provider is created
		assertThat(executorProviderForSub1).isNotNull();
		assertThat(executorProviderForSub2).isNotNull();
		assertThat(factory.getExecutorProviderMap()).isEmpty();
		assertThat(factory.getDefaultExecutorProvider()).isNotNull();
	}

	@Test
	public void testGetExecutorProvider_allSubscribersWithCustomConfigs_manyCreated() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		ConcurrentHashMap<String, ThreadPoolTaskScheduler> threadPoolSchedulerMap = new ConcurrentHashMap<>();
		threadPoolSchedulerMap.put("projects/project/subscriptions/customSubscription1", mockScheduler);
		threadPoolSchedulerMap.put("projects/project/subscriptions/customSubscription2", mockScheduler);
		factory.setThreadPoolTaskSchedulerMap(threadPoolSchedulerMap);

		ExecutorProvider executorProviderForSub1 = factory.getExecutorProvider("customSubscription1");
		ExecutorProvider executorProviderForSub2 = factory.getExecutorProvider("customSubscription2");

		// Verify that two executor providers are created
		assertThat(executorProviderForSub1).isNotNull();
		assertThat(executorProviderForSub2).isNotNull();
		assertThat(factory.getExecutorProviderMap()).hasSize(2);
	}

	@Test
	public void testGetExecutorProvider_subscribersWithDefaultAndCustomConfigs() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		ConcurrentHashMap<String, ThreadPoolTaskScheduler> threadPoolSchedulerMap = new ConcurrentHashMap<>();
		threadPoolSchedulerMap.put("projects/project/subscriptions/customSubscription1", mockScheduler);
		factory.setThreadPoolTaskSchedulerMap(threadPoolSchedulerMap);
		factory.setGlobalScheduler(mockGlobalScheduler);

		ExecutorProvider executorProviderForDefault1 = factory.getExecutorProvider("defaultSubscription1");
		ExecutorProvider executorProviderForDefault2 = factory.getExecutorProvider("defaultSubscription2");
		ExecutorProvider executorProviderForCustom1 = factory.getExecutorProvider("customSubscription1");

		// Verify that one subscription-specific and a global executor provider are created.
		assertThat(executorProviderForCustom1).isNotNull();
		assertThat(executorProviderForDefault1).isNotNull();
		assertThat(executorProviderForDefault2).isNotNull();
		assertThat(factory.getExecutorProviderMap()).hasSize(1);
		assertThat(factory.getDefaultExecutorProvider()).isNotNull();
	}

	@Test
	public void testGetExecutorProvider_schedulerNotPresent_isNull() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);

		ExecutorProvider executorProvider = factory.getExecutorProvider("subscription-name");

		assertThat(factory.fetchThreadPoolTaskScheduler("subscription-name")).isNull();
		assertThat(executorProvider).isNull();
	}

	@Test
	public void testBuildSubscriberStubSettings_executorProvider_pickUserProvidedBean() throws IOException {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", new PubSubConfiguration());
		factory.setExecutorProvider(mockExecutorProvider);

		SubscriberStubSettings subscriberStubSettings = factory.buildGlobalSubscriberStubSettings();

		assertThat(subscriberStubSettings.getBackgroundExecutorProvider()).isEqualTo(mockExecutorProvider);
	}

	@Test
	public void testBuildSubscriberStubSettings_executorProvider_pickGlobalConfiguration() throws IOException {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, new PubSubConfiguration());
		factory.setGlobalScheduler(mockGlobalScheduler);

		SubscriberStubSettings subscriberStubSettings = factory.buildGlobalSubscriberStubSettings();
		ExecutorProvider executorProvider = subscriberStubSettings.getBackgroundExecutorProvider();

		// Verify that only global executor provider is created
		assertThat(executorProvider).isNotNull();
		assertThat(factory.getDefaultExecutorProvider()).isNotNull();
	}

	@Test
	public void testFetchThreadPoolTaskScheduler_presentInMap() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);

		ConcurrentHashMap<String, ThreadPoolTaskScheduler> threadPoolSchedulerMap = new ConcurrentHashMap<>();
		threadPoolSchedulerMap.put("projects/project/subscriptions/subscription-name", mockScheduler);
		factory.setThreadPoolTaskSchedulerMap(threadPoolSchedulerMap);
		when(mockScheduler.getThreadNamePrefix()).thenReturn("my-thread-name");
		when(mockScheduler.getPoolSize()).thenReturn(2);
		when(mockScheduler.isDaemon()).thenReturn(true);

		ThreadPoolTaskScheduler threadPoolTaskScheduler = factory
				.fetchThreadPoolTaskScheduler("subscription-name");

		assertThat(
				threadPoolTaskScheduler.getThreadNamePrefix())
						.isEqualTo("my-thread-name");
		assertThat(
				threadPoolTaskScheduler.getPoolSize())
						.isEqualTo(2);
		assertThat(
				threadPoolTaskScheduler.isDaemon())
						.isTrue();
	}

	@Test
	public void testFetchThreadPoolTaskScheduler_fullyQualifiedNameNotInMap_pickGlobal() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);

		ConcurrentHashMap<String, ThreadPoolTaskScheduler> threadPoolSchedulerMap = new ConcurrentHashMap<>();
		threadPoolSchedulerMap.put("projects/project/subscriptions/subscription-name", mockScheduler);
		factory.setThreadPoolTaskSchedulerMap(threadPoolSchedulerMap);
		factory.setGlobalScheduler(mockGlobalScheduler);
		when(mockGlobalScheduler.getThreadNamePrefix()).thenReturn("global-thread-name");
		when(mockGlobalScheduler.getPoolSize()).thenReturn(2);
		when(mockGlobalScheduler.isDaemon()).thenReturn(true);

		ThreadPoolTaskScheduler threadPoolTaskScheduler = factory
				.fetchThreadPoolTaskScheduler("projects/project1/subscriptions/subscription-name");

		assertThat(
				threadPoolTaskScheduler.getThreadNamePrefix())
						.isEqualTo("global-thread-name");
		assertThat(
				threadPoolTaskScheduler.getPoolSize())
						.isEqualTo(2);
		assertThat(
				threadPoolTaskScheduler.isDaemon())
						.isTrue();
	}

	@Test
	public void testFetchThreadPoolTaskScheduler_notPresentInMap_pickGlobal() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);

		factory.setGlobalScheduler(mockGlobalScheduler);
		when(mockGlobalScheduler.getThreadNamePrefix()).thenReturn("my-thread-name");
		when(mockGlobalScheduler.getPoolSize()).thenReturn(2);
		when(mockGlobalScheduler.isDaemon()).thenReturn(true);

		ThreadPoolTaskScheduler threadPoolTaskScheduler = factory
				.fetchThreadPoolTaskScheduler("subscription-name");

		assertThat(
				threadPoolTaskScheduler.getThreadNamePrefix())
						.isEqualTo("my-thread-name");
		assertThat(
				threadPoolTaskScheduler.getPoolSize())
						.isEqualTo(2);
		assertThat(
				threadPoolTaskScheduler.isDaemon())
						.isTrue();
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
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
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
		PubSubConfiguration.Retry retrySettings = new PubSubConfiguration.Retry();
		retrySettings.setTotalTimeoutSeconds(10L);
		retrySettings.setInitialRetryDelaySeconds(10L);
		retrySettings.setRetryDelayMultiplier(10.0);
		retrySettings.setMaxRetryDelaySeconds(10L);
		retrySettings.setMaxAttempts(10);
		retrySettings.setInitialRpcTimeoutSeconds(10L);
		retrySettings.setRpcTimeoutMultiplier(10.0);
		retrySettings.setMaxRpcTimeoutSeconds(10L);
		when(mockPubSubConfiguration.computeSubscriberRetrySettings("defaultSubscription1",
				projectIdProvider.getProjectId()))
						.thenReturn(retrySettings);

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
	public void testGetRetrySettings_newConfiguration() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, new PubSubConfiguration());

		assertThat(factory.getRetrySettings("defaultSubscription1")).isNull();
	}

	@Test
	public void testBuildGlobalSubscriberStubSettings_retry_pickUserBean() throws IOException {
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
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", new PubSubConfiguration());
		factory.setSubscriberStubRetrySettings(expectedRetrySettings);

		SubscriberStubSettings subscriberStubSettings = factory.buildGlobalSubscriberStubSettings();
		RetrySettings actualRetrySettings = subscriberStubSettings.pullSettings().getRetrySettings();

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
	public void testBuildGlobalSubscriberStubSettings_retry_pickGlobalConfiguration() throws IOException {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		PubSubConfiguration.Retry retrySettings = new PubSubConfiguration.Retry();
		retrySettings.setTotalTimeoutSeconds(10L);
		retrySettings.setInitialRetryDelaySeconds(10L);
		retrySettings.setRetryDelayMultiplier(10.0);
		retrySettings.setMaxRetryDelaySeconds(10L);
		retrySettings.setMaxAttempts(10);
		retrySettings.setInitialRpcTimeoutSeconds(10L);
		retrySettings.setRpcTimeoutMultiplier(10.0);
		retrySettings.setMaxRpcTimeoutSeconds(10L);
		when(mockPubSubConfiguration.getSubscriber()).thenReturn(mockSubscriber);
		when(mockSubscriber.getRetry()).thenReturn(retrySettings);

		SubscriberStubSettings subscriberStubSettings = factory.buildGlobalSubscriberStubSettings();
		RetrySettings actualRetrySettings = subscriberStubSettings.pullSettings().getRetrySettings();

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
	public void testCreateSubscriber_validateSetProperties() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		factory.setCredentialsProvider(this.credentialsProvider);
		factory.setGlobalScheduler(mockScheduler);
		PubSubConfiguration.FlowControl flowControl = new PubSubConfiguration.FlowControl();
		flowControl.setLimitExceededBehavior(FlowController.LimitExceededBehavior.Ignore);
		when(mockPubSubConfiguration.computeSubscriberFlowControlSettings("defaultSubscription",
				projectIdProvider.getProjectId())).thenReturn(flowControl);
		when(mockPubSubConfiguration.computeMaxAckExtensionPeriod("defaultSubscription",
				projectIdProvider.getProjectId()))
				.thenReturn(2L);
		when(mockPubSubConfiguration.computeParallelPullCount("defaultSubscription", projectIdProvider.getProjectId()))
				.thenReturn(2);

		Subscriber expectedSubscriber = factory.createSubscriber("defaultSubscription", (message, consumer) -> {
		});

		assertThat(expectedSubscriber.getFlowControlSettings().getLimitExceededBehavior())
				.isEqualTo(FlowController.LimitExceededBehavior.Ignore);
		assertThat(expectedSubscriber).hasFieldOrPropertyWithValue("maxAckExtensionPeriod", Duration.ofSeconds(2L))
				.hasFieldOrPropertyWithValue("numPullers", 2);
	}

	@Test
	public void testGetFlowControlSettings_userProvidedBean() {
		FlowControlSettings expectedFlowSettings = FlowControlSettings.newBuilder()
				.setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block).setMaxOutstandingElementCount(10L)
				.setMaxOutstandingRequestBytes(10L)
				.build();
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
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
		PubSubConfiguration.FlowControl flowControl = new PubSubConfiguration.FlowControl();
		flowControl.setMaxOutstandingRequestBytes(10L);
		when(mockPubSubConfiguration.computeSubscriberFlowControlSettings("defaultSubscription1",
				projectIdProvider.getProjectId())).thenReturn(flowControl);

		FlowControlSettings actualFlowSettings = factory.getFlowControlSettings("defaultSubscription1");

		assertThat(actualFlowSettings.getMaxOutstandingRequestBytes()).isEqualTo(10L);
		assertThat(actualFlowSettings.getMaxOutstandingElementCount()).isNull();
		assertThat(actualFlowSettings.getLimitExceededBehavior()).isEqualTo(FlowController.LimitExceededBehavior.Block);
	}

	@Test
	public void testGetFlowControlSettings_newConfiguration() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, new PubSubConfiguration());

		assertThat(factory.getFlowControlSettings("defaultSubscription1")).isNull();
	}

	@Test
	public void testGetMaxAckExtensionPeriod_userSetValue() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		factory.setMaxAckExtensionPeriod(Duration.ofSeconds(1));

		assertThat(factory.getMaxAckExtensionPeriod("subscription-name")).isEqualTo(Duration.ofSeconds(1));
	}

	@Test
	public void testGetMaxAckExtensionPeriod_configurationIsPresent() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		when(mockPubSubConfiguration.computeMaxAckExtensionPeriod("subscription-name",
				projectIdProvider.getProjectId())).thenReturn(1L);

		assertThat(factory.getMaxAckExtensionPeriod("subscription-name")).isEqualTo(Duration.ofSeconds(1));
	}

	@Test
	public void testGetMaxAckExtensionPeriod_newConfiguration() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, new PubSubConfiguration());

		assertThat(factory.getMaxAckExtensionPeriod("subscription-name")).isEqualTo(Duration.ofSeconds(0L));
	}

	@Test
	public void testGetParallelPullCount_userSetValue() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		factory.setParallelPullCount(1);

		assertThat(factory.getPullCount("subscription-name")).isEqualTo(1);
	}

	@Test
	public void testGetParallelPullCount_configurationIsPresent() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		when(mockPubSubConfiguration.computeParallelPullCount("subscription-name",
				projectIdProvider.getProjectId())).thenReturn(1);

		assertThat(factory.getPullCount("subscription-name")).isEqualTo(1);
	}

	@Test
	public void testGetParallelPullCount_newConfiguration() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, new PubSubConfiguration());

		assertThat(factory.getPullCount("subscription-name")).isNull();
	}

	@Test
	public void testGetPullEndpoint_userSetValue() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, mockPubSubConfiguration);
		factory.setPullEndpoint("my-endpoint");

		assertThat(factory.getPullEndpoint("subscription-name")).isEqualTo("my-endpoint");
	}

	@Test
	public void testGetPullEndpoint_configurationIsPresent() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		when(mockPubSubConfiguration.computePullEndpoint("subscription-name",
				projectIdProvider.getProjectId())).thenReturn("my-endpoint");

		assertThat(factory.getPullEndpoint("subscription-name")).isEqualTo("my-endpoint");
	}

	@Test
	public void testGetPullEndpoint_newConfiguration() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", new PubSubConfiguration());

		assertThat(factory.getPullEndpoint("subscription-name")).isNull();
	}

	@Test
	public void testBuildGlobalSubscriberStubSettings_pullEndpoint_pickUserProvidedBean() throws IOException {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", new PubSubConfiguration());
		factory.setPullEndpoint("my-endpoint");
		SubscriberStubSettings globalSubscriberSettings = factory.buildGlobalSubscriberStubSettings();
		assertThat(globalSubscriberSettings.getEndpoint()).isEqualTo("my-endpoint");
	}

	@Test
	public void testBuildGlobalSubscriberStubSettings_pullEndpoint_pickGlobalConfiguration() throws IOException {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		when(mockPubSubConfiguration.getSubscriber()).thenReturn(mockSubscriber);
		when(mockSubscriber.getRetry()).thenReturn(new PubSubConfiguration.Retry());
		when(mockSubscriber.getPullEndpoint()).thenReturn("my-endpoint");

		SubscriberStubSettings subscriberStubSettings = factory.buildGlobalSubscriberStubSettings();
		assertThat(subscriberStubSettings.getEndpoint()).isEqualTo("my-endpoint");
	}

	@Test
	public void testBuildSubscriberStubSettings_retryableCodes() throws IllegalAccessException, IOException {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, new PubSubConfiguration());
		factory.setRetryableCodes(new Code[] { Code.INTERNAL });

		assertThat(FieldUtils.readField(factory, "retryableCodes", true))
				.isEqualTo(new Code[] { Code.INTERNAL });

		SubscriberStubSettings settings = factory.buildSubscriberStubSettings("someSubscription");
		assertThat(settings.pullSettings().getRetryableCodes())
				.containsExactly(Code.INTERNAL);

	}

	@Test
	public void testBuildGlobalSubscriberStubSettings_retryableCodes() throws IOException, IllegalAccessException {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, new PubSubConfiguration());
		factory.setRetryableCodes(new Code[] { Code.INTERNAL });

		assertThat(FieldUtils.readField(factory, "retryableCodes", true))
				.isEqualTo(new Code[] { Code.INTERNAL });

		SubscriberStubSettings settings = factory.buildGlobalSubscriberStubSettings();
		assertThat(settings.pullSettings().getRetryableCodes())
				.containsExactly(Code.INTERNAL);
	}

	@Test
	public void createSubscriberStubSucceeds_noSubscriptionNameAndNewConfiguration() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, new PubSubConfiguration());
		factory.setChannelProvider(FixedTransportChannelProvider.create(this.mockTransportChannel));
		factory.setCredentialsProvider(() -> NoCredentials.getInstance());

		SubscriberStub stub = factory.createSubscriberStub();
		assertThat(stub.isShutdown()).isFalse();
	}

	@Test
	public void createSubscriberStubSucceeds() {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, new PubSubConfiguration());
		factory.setChannelProvider(FixedTransportChannelProvider.create(this.mockTransportChannel));
		factory.setCredentialsProvider(() -> NoCredentials.getInstance());

		SubscriberStub stub = factory.createSubscriberStub("unusedSubscription");
		assertThat(stub.isShutdown()).isFalse();
	}

	@Test
	public void createSubscriberStubFailsOnBadCredentials() throws IOException {
		GcpProjectIdProvider projectIdProvider = () -> "project";
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(projectIdProvider, new PubSubConfiguration());
		factory.setChannelProvider(FixedTransportChannelProvider.create(this.mockTransportChannel));

		CredentialsProvider mockCredentialsProvider = mock(CredentialsProvider.class);
		factory.setCredentialsProvider(mockCredentialsProvider);

		when(mockCredentialsProvider.getCredentials()).thenThrow(new IOException("boom"));

		assertThatThrownBy(() -> factory.createSubscriberStub("unusedSubscription"))
				.isInstanceOf(PubSubException.class)
				.hasMessageContaining("Error creating the SubscriberStub");
	}


	@Test
	public void testNewSubscriber_shouldNotAddToHealthCheck() {
		ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of("angeldust", "midnight cowboy");

		when(healthTrackerRegistry.isTracked(subscriptionName)).thenReturn(true);

		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "angeldust");
		factory.setCredentialsProvider(this.credentialsProvider);
		factory.setHealthTrackerRegistry(healthTrackerRegistry);

		Subscriber subscriber = factory.createSubscriber("midnight cowboy", (message, consumer) -> { });
		assertThat(subscriber.getSubscriptionNameString())
				.isEqualTo("projects/angeldust/subscriptions/midnight cowboy");

		verify(healthTrackerRegistry, times(1)).isTracked(subscriptionName);
		verify(healthTrackerRegistry, times(0)).wrap(eq(subscriptionName), any());
	}

	@Test
	public void testNewSubscriber_shouldAddToHealthCheck() {
		ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of("angeldust", "midnight cowboy");

		when(healthTrackerRegistry.isTracked(subscriptionName)).thenReturn(false);

		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "angeldust");
		factory.setCredentialsProvider(this.credentialsProvider);
		factory.setHealthTrackerRegistry(healthTrackerRegistry);

		Subscriber subscriber = factory.createSubscriber("midnight cowboy", (message, consumer) -> { });
		assertThat(subscriber.getSubscriptionNameString())
				.isEqualTo("projects/angeldust/subscriptions/midnight cowboy");

		verify(healthTrackerRegistry).isTracked(subscriptionName);
		verify(healthTrackerRegistry).wrap(any(), any());
		verify(healthTrackerRegistry).addListener(any());
	}

}
