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
	private PubSubConfiguration.Subscriber mockSubscriber;

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
	public void testGetExecutorProvider_customProvided() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", null);
		factory.setExecutorProvider(mockExecutorProvider);

		assertThat(factory.getExecutorProvider("name"))
				.isEqualTo(mockExecutorProvider);
	}

	@Test
	public void testGetExecutorProvider_customNotProvided() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", mockPubSubConfiguration);
		when(mockPubSubConfiguration.getSubscriber("name")).thenReturn(new PubSubConfiguration.Subscriber());

		assertThat(factory.getExecutorProvider("name"))
				.isNotNull();
	}

	@Test
	public void testGetExecutorProvider_pubSubConfigurationIsNull() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", null);

		assertThat(factory.getExecutorProvider("name"))
				.isNull();
	}

	@Test
	public void testGetThreadScheduler() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", null);
		when(mockSubscriber.getExecutorThreads()).thenReturn(6);

		ThreadPoolTaskScheduler threadPoolTaskScheduler = factory
				.createThreadPoolTaskScheduler(mockSubscriber, "subscription-name");
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
}
