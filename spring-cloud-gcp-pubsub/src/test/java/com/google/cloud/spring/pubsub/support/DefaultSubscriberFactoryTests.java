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

import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.pubsub.v1.PullRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.Duration;

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
	private CredentialsProvider credentialsProvider;

	@Mock
	private PubSubConfiguration pubSubConfiguration;

	@Mock
	private PubSubConfiguration.Retry retry;

	@Mock
	private PubSubConfiguration.FlowControl flowControl;

	/**
	 * used to check exception messages and types.
	 */
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testNewSubscriber() {
		when(pubSubConfiguration.getSubscriber("midnight cowboy"))
				.thenReturn(new PubSubConfiguration.Subscriber());
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "angeldust", pubSubConfiguration);
		factory.setCredentialsProvider(this.credentialsProvider);

		Subscriber subscriber = factory.createSubscriber("midnight cowboy", (message, consumer) -> { });

		assertThat(subscriber.getSubscriptionNameString())
				.isEqualTo("projects/angeldust/subscriptions/midnight cowboy");
	}

	@Test
	public void testNewDefaultSubscriberFactory_nullProjectProvider() {
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("The project ID provider can't be null.");
		new DefaultSubscriberFactory(null, null);
	}

	@Test
	public void testNewDefaultSubscriberFactory_nullProject() {
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("The project ID can't be null or empty.");
		new DefaultSubscriberFactory(() -> null, null);
	}

	@Test
	public void testCreatePullRequest_greaterThanZeroMaxMessages() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", null);
		factory.setCredentialsProvider(this.credentialsProvider);

		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("The maxMessages must be greater than 0.");
		factory.createPullRequest("test", -1, true);
	}

	@Test
	public void testCreatePullRequest_nonNullMaxMessages() {
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", null);
		factory.setCredentialsProvider(this.credentialsProvider);

		PullRequest request = factory.createPullRequest("test", null, true);
		assertThat(request.getMaxMessages()).isEqualTo(Integer.MAX_VALUE);
	}

	@Test
	public void testGetRetrySettings() {
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

		RetrySettings actualRetrySettings = factory.getRetrySettings(retry);

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
	public void testGetFlowControlSettings() {
		FlowControlSettings expectedFlowSettings = FlowControlSettings.newBuilder()
				.setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block).setMaxOutstandingElementCount(10L)
				.setMaxOutstandingRequestBytes(10L)
				.build();
		DefaultSubscriberFactory factory = new DefaultSubscriberFactory(() -> "project", null);
		factory.setFlowControlSettings(expectedFlowSettings);

		FlowControlSettings actualFlowSettings = factory.getFlowControlSettings(flowControl);

		assertThat(actualFlowSettings.getLimitExceededBehavior()).isEqualTo(FlowController.LimitExceededBehavior.Block);
		assertThat(actualFlowSettings.getMaxOutstandingElementCount()).isEqualTo(10L);
		assertThat(actualFlowSettings.getMaxOutstandingRequestBytes()).isEqualTo(10L);
	}
}
