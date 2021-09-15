/*
 * Copyright 2019-2020 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.pubsub;

import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.auth.Credentials;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.cloud.spring.pubsub.support.DefaultSubscriberFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for Pub/Sub autoconfiguration.
 *
 * @author Elena Felder
 * @author Mike Eltsufin
 */
public class GcpPubSubAutoConfigurationTests {

	@Test
	public void keepAliveValue_default() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties props = ctx.getBean(GcpPubSubProperties.class);
			assertThat(props.getKeepAliveIntervalMinutes()).isEqualTo(5);

			TransportChannelProvider subscriberTcp = ctx.getBean("subscriberTransportChannelProvider",
					TransportChannelProvider.class);
			TransportChannelProvider publisherTcp = ctx.getBean("publisherTransportChannelProvider",
					TransportChannelProvider.class);
			assertThat(((InstantiatingGrpcChannelProvider) subscriberTcp).getKeepAliveTime().toMinutes())
					.isEqualTo(5);
			assertThat(((InstantiatingGrpcChannelProvider) publisherTcp).getKeepAliveTime().toMinutes())
					.isEqualTo(5);
		});
	}

	@Test
	public void keepAliveValue_custom() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withUserConfiguration(TestConfig.class)
				.withPropertyValues("spring.cloud.gcp.pubsub.keepAliveIntervalMinutes=2");

		contextRunner.run(ctx -> {
			GcpPubSubProperties props = ctx.getBean(GcpPubSubProperties.class);
			assertThat(props.getKeepAliveIntervalMinutes()).isEqualTo(2);

			TransportChannelProvider subscriberTcp = ctx.getBean("subscriberTransportChannelProvider",
					TransportChannelProvider.class);
			TransportChannelProvider publisherTcp = ctx.getBean("publisherTransportChannelProvider",
					TransportChannelProvider.class);
			assertThat(((InstantiatingGrpcChannelProvider) subscriberTcp).getKeepAliveTime().toMinutes())
					.isEqualTo(2);
			assertThat(((InstantiatingGrpcChannelProvider) publisherTcp).getKeepAliveTime().toMinutes())
					.isEqualTo(2);
		});
	}

	@Test
	public void maxInboundMessageSize_default() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {

			TransportChannelProvider subscriberTcp = ctx.getBean("subscriberTransportChannelProvider",
					TransportChannelProvider.class);
			assertThat(FieldUtils.readField(subscriberTcp, "maxInboundMessageSize", true))
					.isEqualTo(20 << 20);

			TransportChannelProvider publisherTcp = ctx.getBean("publisherTransportChannelProvider",
					TransportChannelProvider.class);
			assertThat(FieldUtils.readField(publisherTcp, "maxInboundMessageSize", true))
					.isEqualTo(Integer.MAX_VALUE);
		});
	}

	@Test
	public void customExecutorProviderUsedWhenProvided() {
		ExecutorProvider executorProvider = mock(ExecutorProvider.class);
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withUserConfiguration(TestConfig.class)
				.withBean("subscriberExecutorProvider", ExecutorProvider.class, () -> executorProvider);

		contextRunner.run(ctx -> {
			DefaultSubscriberFactory subscriberFactory = ctx
					.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
			assertThat(subscriberFactory.getExecutorProvider("name")).isSameAs(executorProvider);
		});
	}

	@Test
	public void executorThreads_globalConfigurationSet() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues("spring.cloud.gcp.pubsub.subscriber.executor-threads=7")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
			assertThat(gcpPubSubProperties.getSubscriber().getExecutorThreads()).isEqualTo(7);
			assertThat(
					gcpPubSubProperties.computeSubscriberExecutorThreads("subscription-name", projectIdProvider.getProjectId()))
							.isEqualTo(7);
		});
	}

	@Test
	public void executorThreads_selectiveConfigurationSet() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscription.subscription-name.executor-threads=7")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
			assertThat(
					gcpPubSubProperties.computeSubscriberExecutorThreads("subscription-name", projectIdProvider.getProjectId()))
							.isEqualTo(7);
		});
	}

	@Test
	public void executorThreads_globalAndSelectiveConfigurationSet_selectiveTakesPrecedence() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscriber.executor-threads=5",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.executor-threads=3")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
			assertThat(
					gcpPubSubProperties.computeSubscriberExecutorThreads("subscription-name", projectIdProvider.getProjectId()))
							.isEqualTo(3);
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
			assertThat(
					gcpPubSubProperties.computeSubscriberExecutorThreads("other", projectIdProvider.getProjectId()))
							.isEqualTo(5);
			assertThat(gcpPubSubProperties.getSubscription())
					.hasSize(2);
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/other");
		});
	}

	@Test
	public void executorThreads_globalAndDifferentSelectiveConfigurationSet_pickGlobal() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscriber.executor-threads=5",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=3")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
			assertThat(
					gcpPubSubProperties.computeSubscriberExecutorThreads("subscription-name", projectIdProvider.getProjectId()))
					.isEqualTo(5);
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
			assertThat(
					gcpPubSubProperties.computeSubscriberExecutorThreads("other", projectIdProvider.getProjectId()))
					.isEqualTo(5);
		});
	}

	@Test
	public void executorThreads_noConfigurationSet_pickDefault() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
			assertThat(
					gcpPubSubProperties.computeSubscriberExecutorThreads("subscription-name",
							projectIdProvider.getProjectId()))
					.isEqualTo(4);
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
		});
	}

	@Test
	public void pullConfig_globalConfigurationSet() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues("spring.cloud.gcp.pubsub.subscriber.max-ack-extension-period=7",
						"spring.cloud.gcp.pubsub.subscriber.parallel-pull-count=12",
						"spring.cloud.gcp.pubsub.subscriber.pull-endpoint=my-endpoint")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
			assertThat(gcpPubSubProperties.computeMaxAckExtensionPeriod("subscription-name",
					projectIdProvider.getProjectId())).isEqualTo(7L);
			assertThat(
					gcpPubSubProperties.computeParallelPullCount("subscription-name", projectIdProvider.getProjectId()))
							.isEqualTo(12);
			assertThat(gcpPubSubProperties.computePullEndpoint("subscription-name", projectIdProvider.getProjectId()))
					.isEqualTo("my-endpoint");
			assertThat(gcpPubSubProperties.getSubscriber().getMaxAckExtensionPeriod()).isEqualTo(7L);
			assertThat(gcpPubSubProperties.getSubscriber().getParallelPullCount()).isEqualTo(12);
			assertThat(gcpPubSubProperties.getSubscriber().getPullEndpoint()).isEqualTo("my-endpoint");
		});
	}

	@Test
	public void pullConfig_selectiveConfigurationSet() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscription.subscription-name.max-ack-extension-period=7",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=12",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.pull-endpoint=my-endpoint")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
			assertThat(gcpPubSubProperties.computeMaxAckExtensionPeriod("subscription-name",
					projectIdProvider.getProjectId())).isEqualTo(7L);
			assertThat(
					gcpPubSubProperties.computeParallelPullCount("subscription-name", projectIdProvider.getProjectId()))
							.isEqualTo(12);
			assertThat(gcpPubSubProperties.computePullEndpoint("subscription-name", projectIdProvider.getProjectId()))
					.isEqualTo("my-endpoint");
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
		});
	}

	@Test
	public void pullConfig_globalAndSelectiveConfigurationSet_selectiveTakesPrecedence() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscriber.max-ack-extension-period=5",
						"spring.cloud.gcp.pubsub.subscriber.parallel-pull-count=10",
						"spring.cloud.gcp.pubsub.subscriber.pull-endpoint=other-endpoint",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.max-ack-extension-period=7",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=12",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.pull-endpoint=my-endpoint")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
			assertThat(gcpPubSubProperties.computeMaxAckExtensionPeriod("subscription-name",
					projectIdProvider.getProjectId())).isEqualTo(7L);
			assertThat(
					gcpPubSubProperties.computeParallelPullCount("subscription-name", projectIdProvider.getProjectId()))
							.isEqualTo(12);
			assertThat(gcpPubSubProperties.computePullEndpoint("subscription-name", projectIdProvider.getProjectId()))
					.isEqualTo("my-endpoint");
			assertThat(gcpPubSubProperties.computeMaxAckExtensionPeriod("other", projectIdProvider.getProjectId()))
					.isEqualTo(5L);
			assertThat(
					gcpPubSubProperties.computeParallelPullCount("other", projectIdProvider.getProjectId()))
							.isEqualTo(10);
			assertThat(gcpPubSubProperties.computePullEndpoint("other", projectIdProvider.getProjectId()))
					.isEqualTo("other-endpoint");

			assertThat(gcpPubSubProperties.getSubscription())
					.hasSize(2);
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/other");
		});
	}

	@Test
	public void pullConfig_globalAndDifferentSelectiveConfigurationSet_pickGlobal() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscriber.max-ack-extension-period=5",
						"spring.cloud.gcp.pubsub.subscriber.parallel-pull-count=10",
						"spring.cloud.gcp.pubsub.subscriber.pull-endpoint=other-endpoint",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.executor-threads=4"
						)
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
			assertThat(
					gcpPubSubProperties.computeMaxAckExtensionPeriod("subscription-name", projectIdProvider.getProjectId()))
					.isEqualTo(5);
			assertThat(
					gcpPubSubProperties.computeParallelPullCount("subscription-name", projectIdProvider.getProjectId()))
					.isEqualTo(10);
			assertThat(
					gcpPubSubProperties.computePullEndpoint("subscription-name", projectIdProvider.getProjectId()))
					.isEqualTo("other-endpoint");
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
		});
	}

	@Test
	public void customRetrySettingsUsedWhenProvided() {
		RetrySettings retrySettings = mock(RetrySettings.class);
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withUserConfiguration(TestConfig.class)
				.withBean("subscriberRetrySettings", RetrySettings.class, () -> retrySettings);

		contextRunner.run(ctx -> {
			DefaultSubscriberFactory subscriberFactory = ctx
					.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
			assertThat(subscriberFactory.getRetrySettings("name")).isSameAs(retrySettings);
		});
	}

	@Test
	public void retrySettings_globalConfigurationSet() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscriber.retry.total-timeout-seconds=1",
						"spring.cloud.gcp.pubsub.subscriber.retry.initial-retry-delay-seconds=2",
						"spring.cloud.gcp.pubsub.subscriber.retry.retry-delay-multiplier=3",
						"spring.cloud.gcp.pubsub.subscriber.retry.max-retry-delay-seconds=4",
						"spring.cloud.gcp.pubsub.subscriber.retry.max-attempts=5",
						"spring.cloud.gcp.pubsub.subscriber.retry.jittered=true",
						"spring.cloud.gcp.pubsub.subscriber.retry.initial-rpc-timeout-seconds=6",
						"spring.cloud.gcp.pubsub.subscriber.retry.rpc-timeout-multiplier=7",
						"spring.cloud.gcp.pubsub.subscriber.retry.max-rpc-timeout-seconds=8")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
			PubSubConfiguration.Retry retrySettings = gcpPubSubProperties
					.computeSubscriberRetrySettings("subscription-name", projectIdProvider.getProjectId());
			assertThat(retrySettings.getTotalTimeoutSeconds()).isEqualTo(1L);
			assertThat(retrySettings.getInitialRetryDelaySeconds()).isEqualTo(2L);
			assertThat(retrySettings.getRetryDelayMultiplier()).isEqualTo(3);
			assertThat(retrySettings.getMaxRetryDelaySeconds()).isEqualTo(4);
			assertThat(retrySettings.getMaxAttempts()).isEqualTo(5);
			assertThat(retrySettings.getJittered()).isTrue();
			assertThat(retrySettings.getInitialRpcTimeoutSeconds()).isEqualTo(6);
			assertThat(retrySettings.getRpcTimeoutMultiplier()).isEqualTo(7);
			assertThat(retrySettings.getMaxRpcTimeoutSeconds()).isEqualTo(8);
		});
	}

	@Test
	public void retrySettings_selectiveConfigurationSet() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.total-timeout-seconds=1",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.initial-retry-delay-seconds=2",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.retry-delay-multiplier=3",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.max-retry-delay-seconds=4",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.max-attempts=5",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.jittered=true",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.initial-rpc-timeout-seconds=6",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.rpc-timeout-multiplier=7",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.max-rpc-timeout-seconds=8")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
			PubSubConfiguration.Retry retrySettings = gcpPubSubProperties
					.computeSubscriberRetrySettings("subscription-name", projectIdProvider.getProjectId());
			assertThat(retrySettings.getTotalTimeoutSeconds()).isEqualTo(1L);
			assertThat(retrySettings.getInitialRetryDelaySeconds()).isEqualTo(2L);
			assertThat(retrySettings.getRetryDelayMultiplier()).isEqualTo(3);
			assertThat(retrySettings.getMaxRetryDelaySeconds()).isEqualTo(4);
			assertThat(retrySettings.getMaxAttempts()).isEqualTo(5);
			assertThat(retrySettings.getJittered()).isTrue();
			assertThat(retrySettings.getInitialRpcTimeoutSeconds()).isEqualTo(6);
			assertThat(retrySettings.getRpcTimeoutMultiplier()).isEqualTo(7);
			assertThat(retrySettings.getMaxRpcTimeoutSeconds()).isEqualTo(8);

			assertThat(gcpPubSubProperties.getSubscription())
					.hasSize(1);
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
		});
	}

	@Test
	public void retrySettings_globalAndSelectiveConfigurationSet_selectiveTakesPrecedence() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscriber.retry.total-timeout-seconds=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.initial-retry-delay-seconds=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.retry-delay-multiplier=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.max-retry-delay-seconds=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.max-attempts=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.initial-rpc-timeout-seconds=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.rpc-timeout-multiplier=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.max-rpc-timeout-seconds=10",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.total-timeout-seconds=1",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.initial-retry-delay-seconds=2",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.retry-delay-multiplier=3",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.max-retry-delay-seconds=4",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.max-attempts=5",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.initial-rpc-timeout-seconds=6",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.rpc-timeout-multiplier=7",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.retry.max-rpc-timeout-seconds=8")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

			// Validate settings for subscribers that have subscription-specific retry settings
			// property set
			PubSubConfiguration.Retry retrySettings = gcpPubSubProperties
					.computeSubscriberRetrySettings("subscription-name", projectIdProvider.getProjectId());
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
			assertThat(retrySettings.getTotalTimeoutSeconds()).isEqualTo(1L);
			assertThat(retrySettings.getInitialRetryDelaySeconds()).isEqualTo(2L);
			assertThat(retrySettings.getRetryDelayMultiplier()).isEqualTo(3);
			assertThat(retrySettings.getMaxRetryDelaySeconds()).isEqualTo(4);
			assertThat(retrySettings.getMaxAttempts()).isEqualTo(5);
			assertThat(retrySettings.getInitialRpcTimeoutSeconds()).isEqualTo(6);
			assertThat(retrySettings.getRpcTimeoutMultiplier()).isEqualTo(7);
			assertThat(retrySettings.getMaxRpcTimeoutSeconds()).isEqualTo(8);

			// Validate settings for subscribers that do not have subscription-specific retry settings
			// property set
			PubSubConfiguration.Retry retrySettingsForOtherSubscriber = gcpPubSubProperties
					.getSubscriber("other", projectIdProvider.getProjectId())
					.getRetry();
			assertThat(retrySettingsForOtherSubscriber.getTotalTimeoutSeconds()).isEqualTo(10L);
			assertThat(retrySettingsForOtherSubscriber.getInitialRetryDelaySeconds()).isEqualTo(10L);
			assertThat(retrySettingsForOtherSubscriber.getRetryDelayMultiplier()).isEqualTo(10);
			assertThat(retrySettingsForOtherSubscriber.getMaxRetryDelaySeconds()).isEqualTo(10);
			assertThat(retrySettingsForOtherSubscriber.getMaxAttempts()).isEqualTo(10);
			assertThat(retrySettingsForOtherSubscriber.getInitialRpcTimeoutSeconds()).isEqualTo(10);
			assertThat(retrySettingsForOtherSubscriber.getRpcTimeoutMultiplier()).isEqualTo(10);
			assertThat(retrySettingsForOtherSubscriber.getMaxRpcTimeoutSeconds()).isEqualTo(10);

			assertThat(gcpPubSubProperties.getSubscription())
					.hasSize(2);
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/other");
		});
	}

	@Test
	public void retrySettings_globalAndDifferentSelectiveConfigurationSet_pickGlobal() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscriber.retry.total-timeout-seconds=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.initial-retry-delay-seconds=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.retry-delay-multiplier=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.max-retry-delay-seconds=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.max-attempts=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.initial-rpc-timeout-seconds=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.rpc-timeout-multiplier=10",
						"spring.cloud.gcp.pubsub.subscriber.retry.max-rpc-timeout-seconds=10",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.executor-threads=2")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

			PubSubConfiguration.Retry retrySettingsForOtherSubscriber = gcpPubSubProperties
					.computeSubscriberRetrySettings("subscription-name", projectIdProvider.getProjectId());
			assertThat(retrySettingsForOtherSubscriber.getTotalTimeoutSeconds()).isEqualTo(10L);
			assertThat(retrySettingsForOtherSubscriber.getInitialRetryDelaySeconds()).isEqualTo(10L);
			assertThat(retrySettingsForOtherSubscriber.getRetryDelayMultiplier()).isEqualTo(10);
			assertThat(retrySettingsForOtherSubscriber.getMaxRetryDelaySeconds()).isEqualTo(10);
			assertThat(retrySettingsForOtherSubscriber.getMaxAttempts()).isEqualTo(10);
			assertThat(retrySettingsForOtherSubscriber.getInitialRpcTimeoutSeconds()).isEqualTo(10);
			assertThat(retrySettingsForOtherSubscriber.getRpcTimeoutMultiplier()).isEqualTo(10);
			assertThat(retrySettingsForOtherSubscriber.getMaxRpcTimeoutSeconds()).isEqualTo(10);
		});
	}

	@Test
	public void customFlowControlUsedWhenProvided() {
		FlowControlSettings flowControlSettings = mock(FlowControlSettings.class);
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withUserConfiguration(TestConfig.class)
				.withBean("subscriberFlowControlSettings", FlowControlSettings.class, () -> flowControlSettings);

		contextRunner.run(ctx -> {
			DefaultSubscriberFactory subscriberFactory = ctx
					.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
			assertThat(subscriberFactory.getFlowControlSettings("name")).isSameAs(flowControlSettings);
		});
	}

	@Test
	public void flowControlSettings_globalConfigurationSet() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-element-Count=11",
						"spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-request-Bytes=12",
						"spring.cloud.gcp.pubsub.subscriber.flow-control.limit-exceeded-behavior=Ignore")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			PubSubConfiguration.FlowControl flowControl = gcpPubSubProperties.getSubscriber().getFlowControl();
			assertThat(flowControl.getMaxOutstandingElementCount()).isEqualTo(11L);
			assertThat(flowControl.getMaxOutstandingRequestBytes()).isEqualTo(12L);
			assertThat(flowControl.getLimitExceededBehavior()).isEqualTo(FlowController.LimitExceededBehavior.Ignore);
		});
	}

	@Test
	public void flowControlSettings_selectiveConfigurationSet() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.max-outstanding-element-Count=11",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.max-outstanding-request-Bytes=12",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.limit-exceeded-behavior=Ignore")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

			PubSubConfiguration.FlowControl flowControl = gcpPubSubProperties
					.getSubscriber("subscription-name", projectIdProvider.getProjectId())
					.getFlowControl();

			assertThat(flowControl.getMaxOutstandingElementCount()).isEqualTo(11L);
			assertThat(flowControl.getMaxOutstandingRequestBytes()).isEqualTo(12L);
			assertThat(flowControl.getLimitExceededBehavior()).isEqualTo(FlowController.LimitExceededBehavior.Ignore);

			assertThat(gcpPubSubProperties.getSubscription())
					.hasSize(1);
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
		});
	}

	@Test
	public void flowControlSettings_globalAndSelectiveConfigurationSet_selectiveTakesPrecedence() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-element-Count=10",
						"spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-request-Bytes=10",
						"spring.cloud.gcp.pubsub.subscriber.flow-control.limit-exceeded-behavior=Block",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.max-outstanding-element-Count=11",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.max-outstanding-request-Bytes=12",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.limit-exceeded-behavior=Ignore")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

			// Validate settings for subscribers that have subscription-specific flow control settings
			// property set
			PubSubConfiguration.FlowControl flowControl = gcpPubSubProperties
					.getSubscriber("subscription-name", projectIdProvider.getProjectId())
					.getFlowControl();
			assertThat(flowControl.getMaxOutstandingElementCount()).isEqualTo(11L);
			assertThat(flowControl.getMaxOutstandingRequestBytes()).isEqualTo(12L);
			assertThat(flowControl.getLimitExceededBehavior()).isEqualTo(FlowController.LimitExceededBehavior.Ignore);

			// Validate settings for subscribers that do not have subscription-specific flow control
			// settings property set
			PubSubConfiguration.FlowControl flowControlForOtherSubscriber = gcpPubSubProperties
					.getSubscriber("other", projectIdProvider.getProjectId())
					.getFlowControl();
			assertThat(flowControlForOtherSubscriber.getMaxOutstandingElementCount()).isEqualTo(10L);
			assertThat(flowControlForOtherSubscriber.getMaxOutstandingRequestBytes()).isEqualTo(10L);
			assertThat(flowControlForOtherSubscriber.getLimitExceededBehavior())
					.isEqualTo(FlowController.LimitExceededBehavior.Block);

			assertThat(gcpPubSubProperties.getSubscription())
					.hasSize(2);
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/other");
		});
	}

	@Test
	public void flowControlSettings_globalAndDifferentSelectiveConfigurationSet_pickGlobal() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-element-Count=11",
						"spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-request-Bytes=12",
						"spring.cloud.gcp.pubsub.subscriber.flow-control.limit-exceeded-behavior=Ignore",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=2")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

			PubSubConfiguration.FlowControl flowControl = gcpPubSubProperties
					.computeSubscriberFlowControlSettings("subscription-name", projectIdProvider.getProjectId());
			assertThat(flowControl.getMaxOutstandingElementCount()).isEqualTo(11L);
			assertThat(flowControl.getMaxOutstandingRequestBytes()).isEqualTo(12L);
			assertThat(flowControl.getLimitExceededBehavior()).isEqualTo(FlowController.LimitExceededBehavior.Ignore);

			assertThat(gcpPubSubProperties.getSubscription())
					.hasSize(1);
			assertThat(gcpPubSubProperties.getSubscription())
					.containsKey("projects/fake project/subscriptions/subscription-name");
		});
	}

	@Test
	public void flowControlSettings_subProperties_pickGlobalWhenSelectiveNotSpecified() {
		ApplicationContextRunner contextRunner = new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
				.withPropertyValues(
						"spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-element-Count=11",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.max-outstanding-request-Bytes=12",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.limit-exceeded-behavior=Ignore",
						"spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=2")
				.withUserConfiguration(TestConfig.class);

		contextRunner.run(ctx -> {
			GcpPubSubProperties gcpPubSubProperties = ctx
					.getBean(GcpPubSubProperties.class);
			GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

			PubSubConfiguration.FlowControl flowControl = gcpPubSubProperties
					.computeSubscriberFlowControlSettings("subscription-name", projectIdProvider.getProjectId());
			assertThat(flowControl.getMaxOutstandingElementCount()).isEqualTo(11L);
			assertThat(flowControl.getMaxOutstandingRequestBytes()).isEqualTo(12L);
			assertThat(flowControl.getLimitExceededBehavior()).isEqualTo(FlowController.LimitExceededBehavior.Ignore);
		});
	}

	static class TestConfig {

		@Bean
		public GcpProjectIdProvider projectIdProvider() {
			return () -> "fake project";
		}

		@Bean
		public CredentialsProvider googleCredentials() {
			return () -> mock(Credentials.class);
		}

	}
}
