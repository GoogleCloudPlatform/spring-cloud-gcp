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

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.pubsub.v1.PullRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
		when(mockPubSubConfiguration.getSubscriber("midnight cowboy")).thenReturn(new PubSubConfiguration.Subscriber());
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "angeldust", mockPubSubConfiguration);
		factory.setCredentialsProvider(this.credentialsProvider);

		Subscriber subscriber = factory.createSubscriber("midnight cowboy", (message, consumer) -> {
		});

		assertThat(subscriber.getSubscriptionNameString())
				.isEqualTo("projects/angeldust/subscriptions/midnight cowboy");
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
	public void testGetExecutorProvider_userProvidedBean() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", null);
		factory.setExecutorProvider(mockExecutorProvider);

		assertThat(factory.getExecutorProvider("name"))
				.isSameAs(mockExecutorProvider);
	}

	@Test
	public void testGetExecutorProvider_allSubscribersWithDefaultConfig_oneCreated() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		when(mockPubSubConfiguration.getSubscriber("defaultSubscription1")).thenReturn(mockDefaultSubscriber1);
		when(mockDefaultSubscriber1.getExecutorThreads()).thenReturn(4);
		when(mockDefaultSubscriber1.isGlobal()).thenReturn(true);
		when(mockPubSubConfiguration.getSubscriber("defaultSubscription2")).thenReturn(mockDefaultSubscriber2);
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
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		when(mockPubSubConfiguration.getSubscriber("customSubscription1")).thenReturn(mockCustomSubscriber1);
		when(mockCustomSubscriber1.getExecutorThreads()).thenReturn(4);
		when(mockPubSubConfiguration.getSubscriber("customSubscription2")).thenReturn(mockCustomSubscriber2);
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
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);

		// One subscriber with subscription-specific subscriber properties
		when(mockPubSubConfiguration.getSubscriber("customSubscription1")).thenReturn(mockCustomSubscriber1);
		when(mockCustomSubscriber1.getExecutorThreads()).thenReturn(4);

		// Two subscribers with default/global subscriber properties
		when(mockPubSubConfiguration.getSubscriber("defaultSubscription1")).thenReturn(mockDefaultSubscriber1);
		when(mockDefaultSubscriber1.isGlobal()).thenReturn(true);
		when(mockDefaultSubscriber1.getExecutorThreads()).thenReturn(4);
		when(mockPubSubConfiguration.getSubscriber("defaultSubscription2")).thenReturn(mockDefaultSubscriber2);
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

}
