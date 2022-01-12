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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.auth.Credentials;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.cloud.spring.pubsub.support.DefaultSubscriberFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.threeten.bp.Duration;

/**
 * Tests for Pub/Sub autoconfiguration.
 */
class GcpPubSubAutoConfigurationTests {

  @Test
  void keepAliveValue_default() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties props = ctx.getBean(GcpPubSubProperties.class);
          assertThat(props.getKeepAliveIntervalMinutes()).isEqualTo(5);

          TransportChannelProvider subscriberTcp =
              ctx.getBean("subscriberTransportChannelProvider", TransportChannelProvider.class);
          TransportChannelProvider publisherTcp =
              ctx.getBean("publisherTransportChannelProvider", TransportChannelProvider.class);
          assertThat(
                  ((InstantiatingGrpcChannelProvider) subscriberTcp).getKeepAliveTime().toMinutes())
              .isEqualTo(5);
          assertThat(
                  ((InstantiatingGrpcChannelProvider) publisherTcp).getKeepAliveTime().toMinutes())
              .isEqualTo(5);
        });
  }

  @Test
  void keepAliveValue_custom() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues("spring.cloud.gcp.pubsub.keepAliveIntervalMinutes=2");

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties props = ctx.getBean(GcpPubSubProperties.class);
          assertThat(props.getKeepAliveIntervalMinutes()).isEqualTo(2);

          TransportChannelProvider subscriberTcp =
              ctx.getBean("subscriberTransportChannelProvider", TransportChannelProvider.class);
          TransportChannelProvider publisherTcp =
              ctx.getBean("publisherTransportChannelProvider", TransportChannelProvider.class);
          assertThat(
                  ((InstantiatingGrpcChannelProvider) subscriberTcp).getKeepAliveTime().toMinutes())
              .isEqualTo(2);
          assertThat(
                  ((InstantiatingGrpcChannelProvider) publisherTcp).getKeepAliveTime().toMinutes())
              .isEqualTo(2);
        });
  }

  @Test
  void maxInboundMessageSize_default() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          TransportChannelProvider subscriberTcp =
              ctx.getBean("subscriberTransportChannelProvider", TransportChannelProvider.class);
          assertThat(FieldUtils.readField(subscriberTcp, "maxInboundMessageSize", true))
              .isEqualTo(20 << 20);

          TransportChannelProvider publisherTcp =
              ctx.getBean("publisherTransportChannelProvider", TransportChannelProvider.class);
          assertThat(FieldUtils.readField(publisherTcp, "maxInboundMessageSize", true))
              .isEqualTo(Integer.MAX_VALUE);
        });
  }

  @Test
  void retryableCodes_default() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory defaultSubscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          assertThat(FieldUtils.readField(defaultSubscriberFactory, "retryableCodes", true))
              .isNull();
          assertThat(defaultSubscriberFactory.getRetryableCodes("subscription-name")).isNull();
        });
  }

  @Test
  void retryableCodes_empty() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues("spring.cloud.gcp.pubsub.subscriber.retryableCodes=");

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties properties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
          DefaultSubscriberFactory defaultSubscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);

          assertThat(properties.getSubscriber().getRetryableCodes()).isEqualTo(new Code[] {});
          assertThat(
                  properties.computeRetryableCodes(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo(new Code[] {});
          assertThat(defaultSubscriberFactory.getRetryableCodes("subscription-name"))
              .isEqualTo(new Code[] {});
        });
  }

  @Test
  void retryableCodes_Internal() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues("spring.cloud.gcp.pubsub.subscriber.retryableCodes=INTERNAL");

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties properties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
          DefaultSubscriberFactory defaultSubscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);

          assertThat(properties.getSubscriber().getRetryableCodes())
              .isEqualTo(new Code[] {Code.INTERNAL});
          assertThat(
                  properties.computeRetryableCodes(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo(new Code[] {Code.INTERNAL});
          assertThat(defaultSubscriberFactory.getRetryableCodes("subscription-name"))
              .isEqualTo(new Code[] {Code.INTERNAL});
        });
  }

  @Test
  void retryableCodes_many() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.retryableCodes=UNKNOWN,ABORTED,UNAVAILABLE,INTERNAL");

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties properties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
          DefaultSubscriberFactory defaultSubscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          Code[] expectedRetryableCodes =
              new Code[] {Code.UNKNOWN, Code.ABORTED, Code.UNAVAILABLE, Code.INTERNAL};

          assertThat(properties.getSubscriber().getRetryableCodes())
              .isEqualTo(expectedRetryableCodes);
          assertThat(
                  properties.computeRetryableCodes(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo(expectedRetryableCodes);
          assertThat(defaultSubscriberFactory.getRetryableCodes("subscription-name"))
              .isEqualTo(expectedRetryableCodes);
        });
  }

  @Test
  void retryableCodes_selectiveConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscription.subscription-name.retryableCodes=UNKNOWN,ABORTED,UNAVAILABLE,INTERNAL");

    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory defaultSubscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          assertThat(defaultSubscriberFactory.getRetryableCodes("subscription-name"))
              .isEqualTo(new Code[] {Code.UNKNOWN, Code.ABORTED, Code.UNAVAILABLE, Code.INTERNAL});
          assertThat(defaultSubscriberFactory.getRetryableCodes("other")).isNull();
        });
  }

  @Test
  void retryableCodes_globalAndSelectiveConfigurationSet_selectiveTakesPrecedence() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.retryableCodes=INTERNAL",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.retryableCodes=UNKNOWN,ABORTED,UNAVAILABLE");

    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory defaultSubscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          assertThat(defaultSubscriberFactory.getRetryableCodes("subscription-name"))
              .isEqualTo(new Code[] {Code.UNKNOWN, Code.ABORTED, Code.UNAVAILABLE});
          assertThat(defaultSubscriberFactory.getRetryableCodes("other"))
              .isEqualTo(new Code[] {Code.INTERNAL});
        });
  }

  @Test
  void retryableCodes_globalAndDifferentSelectiveConfigurationSet_pickGlobal() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.retryableCodes=INTERNAL",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.pull-counts=2");

    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory defaultSubscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          assertThat(defaultSubscriberFactory.getRetryableCodes("subscription-name"))
              .isEqualTo(new Code[] {Code.INTERNAL});
        });
  }

  @Test
  void customExecutorProviderUsedWhenProvided() {
    ExecutorProvider executorProvider = mock(ExecutorProvider.class);
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class)
            .withBean("subscriberExecutorProvider", ExecutorProvider.class, () -> executorProvider);

    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory factory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          assertThat(factory.getExecutorProvider("name")).isSameAs(executorProvider);
          assertThat(ctx.containsBean("globalSubscriberExecutorProvider")).isFalse();
          assertThat(ctx.containsBean("subscriberExecutorProvider-name")).isFalse();
        });
  }

  @Test
  void threadPoolScheduler_noConfigurationSet_globalCreated() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          ThreadPoolTaskScheduler globalSchedulerBean =
              (ThreadPoolTaskScheduler) ctx.getBean("globalPubSubSubscriberThreadPoolScheduler");

          assertThat(FieldUtils.readField(globalSchedulerBean, "poolSize", true)).isEqualTo(4);
          assertThat(globalSchedulerBean.getThreadNamePrefix())
              .isEqualTo("global-gcp-pubsub-subscriber");
          assertThat(globalSchedulerBean.isDaemon()).isTrue();
        });
  }

  @Test
  void subscriberThreadPoolTaskScheduler_globalConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues("spring.cloud.gcp.pubsub.subscriber.executor-threads=7")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          ThreadPoolTaskScheduler globalSchedulerBean =
              (ThreadPoolTaskScheduler) ctx.getBean("globalPubSubSubscriberThreadPoolScheduler");

          assertThat(gcpPubSubProperties.getSubscriber().getExecutorThreads()).isEqualTo(7);
          assertThat(globalSchedulerBean.getThreadNamePrefix())
              .isEqualTo("global-gcp-pubsub-subscriber");
          assertThat(FieldUtils.readField(globalSchedulerBean, "poolSize", true)).isEqualTo(7);
          assertThat(globalSchedulerBean.isDaemon()).isTrue();
        });
  }

  @Test
  void subscriberExecutorProvider_globalConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues("spring.cloud.gcp.pubsub.subscriber.executor-threads=7")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory factory =
              (DefaultSubscriberFactory) ctx.getBean("defaultSubscriberFactory");
          ExecutorProvider globalExecutorProvider =
              (ExecutorProvider) ctx.getBean("globalSubscriberExecutorProvider");

          assertThat(globalExecutorProvider).isNotNull();
          assertThat(factory.getExecutorProvider("other")).isSameAs(globalExecutorProvider);
        });
  }

  @Test
  void threadPoolTaskScheduler_selectiveConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscription.subscription-name.executor-threads=7")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {

          // Verify that selective and global beans have been created
          ThreadPoolTaskScheduler selectiveScheduler =
              (ThreadPoolTaskScheduler) ctx.getBean("threadPoolScheduler_subscription-name");
          ThreadPoolTaskScheduler globalScheduler =
              (ThreadPoolTaskScheduler) ctx.getBean("globalPubSubSubscriberThreadPoolScheduler");
          assertThat(selectiveScheduler.getThreadNamePrefix())
              .isEqualTo("gcp-pubsub-subscriber-subscription-name");
          assertThat(selectiveScheduler.isDaemon()).isTrue();
          assertThat(FieldUtils.readField(selectiveScheduler, "poolSize", true)).isEqualTo(7);
          assertThat(globalScheduler.getThreadNamePrefix())
              .isEqualTo("global-gcp-pubsub-subscriber");
          assertThat(FieldUtils.readField(globalScheduler, "poolSize", true)).isEqualTo(4);
          assertThat(globalScheduler.isDaemon()).isTrue();
        });
  }

  @Test
  void subscriberExecutorProvider_selectiveConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscription.subscription-name.executor-threads=7")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory factory =
              (DefaultSubscriberFactory) ctx.getBean("defaultSubscriberFactory");
          ExecutorProvider selectiveExecutorProvider =
              (ExecutorProvider) ctx.getBean("subscriberExecutorProvider-subscription-name");
          ExecutorProvider globalExecutorProvider =
              (ExecutorProvider) ctx.getBean("globalSubscriberExecutorProvider");

          assertThat(globalExecutorProvider).isNotNull();
          assertThat(selectiveExecutorProvider).isNotNull();
          assertThat(factory.getExecutorProvider("subscription-name"))
              .isSameAs(selectiveExecutorProvider);
        });
  }

  @Test
  void threadPoolScheduler_globalAndSelectiveConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.executor-threads=5",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.executor-threads=3")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {

          // Verify that selective and global beans have been created
          ThreadPoolTaskScheduler selectiveScheduler =
              (ThreadPoolTaskScheduler) ctx.getBean("threadPoolScheduler_subscription-name");
          ThreadPoolTaskScheduler globalScheduler =
              (ThreadPoolTaskScheduler) ctx.getBean("globalPubSubSubscriberThreadPoolScheduler");
          assertThat(selectiveScheduler.getThreadNamePrefix())
              .isEqualTo("gcp-pubsub-subscriber-subscription-name");
          assertThat(FieldUtils.readField(selectiveScheduler, "poolSize", true)).isEqualTo(3);
          assertThat(selectiveScheduler.isDaemon()).isTrue();
          assertThat(globalScheduler.getThreadNamePrefix())
              .isEqualTo("global-gcp-pubsub-subscriber");
          assertThat(FieldUtils.readField(globalScheduler, "poolSize", true)).isEqualTo(5);
          assertThat(globalScheduler.isDaemon()).isTrue();
        });
  }

  @Test
  void threadPoolTaskScheduler_globalAndDifferentSelectiveConfigurationSet_onlyGlobalCreated() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.executor-threads=5",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=3")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {

          // Verify that only global thread pool task scheduler is created
          ThreadPoolTaskScheduler globalScheduler =
              (ThreadPoolTaskScheduler) ctx.getBean("globalPubSubSubscriberThreadPoolScheduler");

          assertThat(globalScheduler.getThreadNamePrefix())
              .isEqualTo("global-gcp-pubsub-subscriber");
          assertThat(globalScheduler.isDaemon()).isTrue();
          assertThat(FieldUtils.readField(globalScheduler, "poolSize", true)).isEqualTo(5);
          assertThat(ctx.containsBean("threadPoolScheduler_subscription-name")).isFalse();
        });
  }

  @Test
  void subscriberExecutorProvider_globalAndDifferentSelectiveConfigurationSet_onlyGlobalCreated() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.executor-threads=5",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=3")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory factory =
              (DefaultSubscriberFactory) ctx.getBean("defaultSubscriberFactory");

          // Verify that global executor provider is created and used
          ExecutorProvider globalExecutorProvider =
              (ExecutorProvider) ctx.getBean("globalSubscriberExecutorProvider");
          assertThat(ctx.containsBean("subscriberExecutorProvider-subscription-name")).isFalse();
          assertThat(factory.getGlobalExecutorProvider()).isSameAs(globalExecutorProvider);
        });
  }

  @Test
  void subscriberExecutorProvider_globalAndSelectiveConfigurationSet_selectiveTakesPrecedence() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.executor-threads=5",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.executor-threads=3")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory factory =
              (DefaultSubscriberFactory) ctx.getBean("defaultSubscriberFactory");
          ExecutorProvider selectiveExecutorProvider =
              (ExecutorProvider) ctx.getBean("subscriberExecutorProvider-subscription-name");
          ExecutorProvider globalExecutorProvider =
              (ExecutorProvider) ctx.getBean("globalSubscriberExecutorProvider");

          assertThat(selectiveExecutorProvider).isNotNull();
          assertThat(globalExecutorProvider).isNotNull();
          assertThat(factory.getGlobalExecutorProvider()).isNotNull();
          assertThat(factory.getExecutorProvider("subscription-name"))
              .isSameAs(selectiveExecutorProvider);
        });
  }

  @Test
  void pullConfig_globalConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.max-ack-extension-period=7",
                "spring.cloud.gcp.pubsub.subscriber.parallel-pull-count=12",
                "spring.cloud.gcp.pubsub.subscriber.pull-endpoint=my-endpoint")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
          assertThat(
                  gcpPubSubProperties.computeMaxAckExtensionPeriod(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo(7L);
          assertThat(
                  gcpPubSubProperties.computeParallelPullCount(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo(12);
          assertThat(
                  gcpPubSubProperties.computePullEndpoint(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo("my-endpoint");
          assertThat(gcpPubSubProperties.getSubscriber().getMaxAckExtensionPeriod()).isEqualTo(7L);
          assertThat(gcpPubSubProperties.getSubscriber().getParallelPullCount()).isEqualTo(12);
          assertThat(gcpPubSubProperties.getSubscriber().getPullEndpoint())
              .isEqualTo("my-endpoint");
        });
  }

  @Test
  void pullConfig_selectiveConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscription.subscription-name.max-ack-extension-period=7",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=12",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.pull-endpoint=my-endpoint")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
          assertThat(
                  gcpPubSubProperties.computeMaxAckExtensionPeriod(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo(7L);
          assertThat(
                  gcpPubSubProperties.computeParallelPullCount(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo(12);
          assertThat(
                  gcpPubSubProperties.computePullEndpoint(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo("my-endpoint");
          assertThat(gcpPubSubProperties.getSubscription())
              .containsKey("projects/fake project/subscriptions/subscription-name");
        });
  }

  @Test
  void pullConfig_globalAndSelectiveConfigurationSet_selectiveTakesPrecedence() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.max-ack-extension-period=5",
                "spring.cloud.gcp.pubsub.subscriber.parallel-pull-count=10",
                "spring.cloud.gcp.pubsub.subscriber.pull-endpoint=other-endpoint",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.max-ack-extension-period=7",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=12",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.pull-endpoint=my-endpoint")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
          assertThat(
                  gcpPubSubProperties.computeMaxAckExtensionPeriod(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo(7L);
          assertThat(
                  gcpPubSubProperties.computeParallelPullCount(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo(12);
          assertThat(
                  gcpPubSubProperties.computePullEndpoint(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo("my-endpoint");
          assertThat(
                  gcpPubSubProperties.computeMaxAckExtensionPeriod(
                      "other", projectIdProvider.getProjectId()))
              .isEqualTo(5L);
          assertThat(
                  gcpPubSubProperties.computeParallelPullCount(
                      "other", projectIdProvider.getProjectId()))
              .isEqualTo(10);
          assertThat(
                  gcpPubSubProperties.computePullEndpoint(
                      "other", projectIdProvider.getProjectId()))
              .isEqualTo("other-endpoint");

          assertThat(gcpPubSubProperties.getSubscription()).hasSize(2);
          assertThat(gcpPubSubProperties.getSubscription())
              .containsKey("projects/fake project/subscriptions/subscription-name");
          assertThat(gcpPubSubProperties.getSubscription())
              .containsKey("projects/fake project/subscriptions/other");
        });
  }

  @Test
  void pullConfig_globalAndDifferentSelectiveConfigurationSet_pickGlobal() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.max-ack-extension-period=5",
                "spring.cloud.gcp.pubsub.subscriber.parallel-pull-count=10",
                "spring.cloud.gcp.pubsub.subscriber.pull-endpoint=other-endpoint",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.executor-threads=4")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
          assertThat(
                  gcpPubSubProperties.computeMaxAckExtensionPeriod(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo(5);
          assertThat(
                  gcpPubSubProperties.computeParallelPullCount(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo(10);
          assertThat(
                  gcpPubSubProperties.computePullEndpoint(
                      "subscription-name", projectIdProvider.getProjectId()))
              .isEqualTo("other-endpoint");
          assertThat(gcpPubSubProperties.getSubscription())
              .containsKey("projects/fake project/subscriptions/subscription-name");
        });
  }

  @Test
  void customRetrySettingsUsedWhenProvided() {
    RetrySettings retrySettings = mock(RetrySettings.class);
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class)
            .withBean("subscriberRetrySettings", RetrySettings.class, () -> retrySettings);

    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          assertThat(subscriberFactory.getRetrySettings("name")).isSameAs(retrySettings);
        });
  }

  @Test
  void retrySettings_globalConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
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

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
          PubSubConfiguration.Retry retrySettings =
              gcpPubSubProperties.computeSubscriberRetrySettings(
                  "subscription-name", projectIdProvider.getProjectId());
          assertThat(retrySettings.getTotalTimeoutSeconds()).isEqualTo(1L);
          assertThat(retrySettings.getInitialRetryDelaySeconds()).isEqualTo(2L);
          assertThat(retrySettings.getRetryDelayMultiplier()).isEqualTo(3);
          assertThat(retrySettings.getMaxRetryDelaySeconds()).isEqualTo(4);
          assertThat(retrySettings.getMaxAttempts()).isEqualTo(5);
          assertThat(retrySettings.getJittered()).isTrue();
          assertThat(retrySettings.getInitialRpcTimeoutSeconds()).isEqualTo(6);
          assertThat(retrySettings.getRpcTimeoutMultiplier()).isEqualTo(7);
          assertThat(retrySettings.getMaxRpcTimeoutSeconds()).isEqualTo(8);

          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          RetrySettings expectedRetrySettings =
              RetrySettings.newBuilder()
                  .setTotalTimeout(Duration.ofSeconds(1L))
                  .setInitialRetryDelay(Duration.ofSeconds(2L))
                  .setRetryDelayMultiplier(3)
                  .setMaxRetryDelay(Duration.ofSeconds(4L))
                  .setMaxAttempts(5)
                  .setInitialRpcTimeout(Duration.ofSeconds(6L))
                  .setRpcTimeoutMultiplier(7)
                  .setMaxRpcTimeout(Duration.ofSeconds(8))
                  .build();
          assertThat(subscriberFactory.getRetrySettings("name")).isEqualTo(expectedRetrySettings);
          assertThat(ctx.getBean("globalSubscriberRetrySettings", RetrySettings.class))
              .isEqualTo(expectedRetrySettings);
        });
  }

  @Test
  void retrySettings_selectiveConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
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

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
          PubSubConfiguration.Retry retrySettings =
              gcpPubSubProperties.computeSubscriberRetrySettings(
                  "subscription-name", projectIdProvider.getProjectId());

          assertThat(retrySettings.getTotalTimeoutSeconds()).isEqualTo(1L);
          assertThat(retrySettings.getInitialRetryDelaySeconds()).isEqualTo(2L);
          assertThat(retrySettings.getRetryDelayMultiplier()).isEqualTo(3);
          assertThat(retrySettings.getMaxRetryDelaySeconds()).isEqualTo(4);
          assertThat(retrySettings.getMaxAttempts()).isEqualTo(5);
          assertThat(retrySettings.getJittered()).isTrue();
          assertThat(retrySettings.getInitialRpcTimeoutSeconds()).isEqualTo(6);
          assertThat(retrySettings.getRpcTimeoutMultiplier()).isEqualTo(7);
          assertThat(retrySettings.getMaxRpcTimeoutSeconds()).isEqualTo(8);
          assertThat(gcpPubSubProperties.getSubscription()).hasSize(1);
          assertThat(gcpPubSubProperties.getSubscription())
              .containsKey("projects/fake project/subscriptions/subscription-name");

          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          RetrySettings expectedRetrySettings =
              RetrySettings.newBuilder()
                  .setTotalTimeout(Duration.ofSeconds(1L))
                  .setInitialRetryDelay(Duration.ofSeconds(2L))
                  .setRetryDelayMultiplier(3)
                  .setMaxRetryDelay(Duration.ofSeconds(4L))
                  .setMaxAttempts(5)
                  .setInitialRpcTimeout(Duration.ofSeconds(6L))
                  .setRpcTimeoutMultiplier(7)
                  .setMaxRpcTimeout(Duration.ofSeconds(8L))
                  .build();
          assertThat(subscriberFactory.getRetrySettings("subscription-name"))
              .isEqualTo(expectedRetrySettings);
          assertThat(ctx.getBean("subscriberRetrySettings-subscription-name", RetrySettings.class))
              .isEqualTo(expectedRetrySettings);
        });
  }

  @Test
  void retrySettings_globalAndSelectiveConfigurationSet_selectiveTakesPrecedence() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
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

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

          // Validate settings for subscribers that have subscription-specific retry settings
          // property set
          PubSubConfiguration.Retry retrySettings =
              gcpPubSubProperties.computeSubscriberRetrySettings(
                  "subscription-name", projectIdProvider.getProjectId());
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

          // Validate settings for subscribers that do **not** have subscription-specific retry
          // settings
          // property set
          PubSubConfiguration.Retry retrySettingsForOtherSubscriber =
              gcpPubSubProperties
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
          assertThat(gcpPubSubProperties.getSubscription()).hasSize(2);
          assertThat(gcpPubSubProperties.getSubscription())
              .containsKey("projects/fake project/subscriptions/subscription-name");
          assertThat(gcpPubSubProperties.getSubscription())
              .containsKey("projects/fake project/subscriptions/other");

          // Verify that beans for selective and global retry settings are created. Also
          // verify that selective retry setting takes precedence.
          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          RetrySettings expectedRetrySettingsForSubscriptionName =
              RetrySettings.newBuilder()
                  .setTotalTimeout(Duration.ofSeconds(1L))
                  .setInitialRetryDelay(Duration.ofSeconds(2L))
                  .setRetryDelayMultiplier(3)
                  .setMaxRetryDelay(Duration.ofSeconds(4L))
                  .setMaxAttempts(5)
                  .setInitialRpcTimeout(Duration.ofSeconds(6L))
                  .setRpcTimeoutMultiplier(7)
                  .setMaxRpcTimeout(Duration.ofSeconds(8))
                  .build();
          RetrySettings expectedRetrySettingsForOther =
              RetrySettings.newBuilder()
                  .setTotalTimeout(Duration.ofSeconds(10L))
                  .setInitialRetryDelay(Duration.ofSeconds(10L))
                  .setRetryDelayMultiplier(10)
                  .setMaxRetryDelay(Duration.ofSeconds(10L))
                  .setMaxAttempts(10)
                  .setInitialRpcTimeout(Duration.ofSeconds(10L))
                  .setRpcTimeoutMultiplier(10)
                  .setMaxRpcTimeout(Duration.ofSeconds(10))
                  .build();
          assertThat(subscriberFactory.getRetrySettings("subscription-name"))
              .isEqualTo(expectedRetrySettingsForSubscriptionName);
          assertThat(ctx.getBean("subscriberRetrySettings-subscription-name", RetrySettings.class))
              .isEqualTo(expectedRetrySettingsForSubscriptionName);
          assertThat(subscriberFactory.getRetrySettings("other"))
              .isEqualTo(expectedRetrySettingsForOther);
          assertThat(ctx.getBean("globalSubscriberRetrySettings", RetrySettings.class))
              .isEqualTo(expectedRetrySettingsForOther);
        });
  }

  @Test
  void retrySettings_globalAndDifferentSelectiveConfigurationSet_pickGlobal() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
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

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

          PubSubConfiguration.Retry retrySettings =
              gcpPubSubProperties.computeSubscriberRetrySettings(
                  "subscription-name", projectIdProvider.getProjectId());

          assertThat(retrySettings.getTotalTimeoutSeconds()).isEqualTo(10L);
          assertThat(retrySettings.getInitialRetryDelaySeconds()).isEqualTo(10L);
          assertThat(retrySettings.getRetryDelayMultiplier()).isEqualTo(10);
          assertThat(retrySettings.getMaxRetryDelaySeconds()).isEqualTo(10);
          assertThat(retrySettings.getMaxAttempts()).isEqualTo(10);
          assertThat(retrySettings.getInitialRpcTimeoutSeconds()).isEqualTo(10);
          assertThat(retrySettings.getRpcTimeoutMultiplier()).isEqualTo(10);
          assertThat(retrySettings.getMaxRpcTimeoutSeconds()).isEqualTo(10);

          // Verify that bean for global retry settings is created.
          RetrySettings expectedRetrySettings =
              RetrySettings.newBuilder()
                  .setTotalTimeout(Duration.ofSeconds(10L))
                  .setInitialRetryDelay(Duration.ofSeconds(10L))
                  .setRetryDelayMultiplier(10)
                  .setMaxRetryDelay(Duration.ofSeconds(10L))
                  .setMaxAttempts(10)
                  .setInitialRpcTimeout(Duration.ofSeconds(10L))
                  .setRpcTimeoutMultiplier(10)
                  .setMaxRpcTimeout(Duration.ofSeconds(10))
                  .build();
          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          assertThat(subscriberFactory.getRetrySettings("subscription-name"))
              .isEqualTo(expectedRetrySettings);
          assertThat(ctx.getBean("globalSubscriberRetrySettings", RetrySettings.class))
              .isEqualTo(expectedRetrySettings);
        });
  }

  @Test
  void retrySettings_subsetOfProperties_pickGlobalWhenSelectiveNotSpecified() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.retry.total-timeout-seconds=10",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.retry.initial-retry-delay-seconds=2",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.retry.retry-delay-multiplier=3",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.retry.max-retry-delay-seconds=4",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.retry.max-attempts=5",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.retry.initial-rpc-timeout-seconds=6",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.retry.rpc-timeout-multiplier=7",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.retry.max-rpc-timeout-seconds=8")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);
          PubSubConfiguration.Retry retry =
              gcpPubSubProperties.computeSubscriberRetrySettings(
                  "subscription-name", projectIdProvider.getProjectId());
          assertThat(retry.getTotalTimeoutSeconds()).isEqualTo(10L);
          assertThat(retry.getInitialRetryDelaySeconds()).isEqualTo(2L);
          assertThat(retry.getRetryDelayMultiplier()).isEqualTo(3);
          assertThat(retry.getMaxRetryDelaySeconds()).isEqualTo(4L);
          assertThat(retry.getMaxAttempts()).isEqualTo(5);
          assertThat(retry.getInitialRpcTimeoutSeconds()).isEqualTo(6L);
          assertThat(retry.getRpcTimeoutMultiplier()).isEqualTo(7);
          assertThat(retry.getMaxRpcTimeoutSeconds()).isEqualTo(8L);

          // Verify that beans for selective and global retry settings are created.
          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          RetrySettings expectedRetrySettingsForSubscriptionName =
              RetrySettings.newBuilder()
                  .setTotalTimeout(Duration.ofSeconds(10L))
                  .setInitialRetryDelay(Duration.ofSeconds(2L))
                  .setRetryDelayMultiplier(3)
                  .setMaxRetryDelay(Duration.ofSeconds(4L))
                  .setMaxAttempts(5)
                  .setInitialRpcTimeout(Duration.ofSeconds(6L))
                  .setRpcTimeoutMultiplier(7)
                  .setMaxRpcTimeout(Duration.ofSeconds(8L))
                  .build();
          RetrySettings expectedGlobalRetrySettings =
              RetrySettings.newBuilder().setTotalTimeout(Duration.ofSeconds(10L)).build();
          assertThat(subscriberFactory.getRetrySettings("subscription-name"))
              .isEqualTo(expectedRetrySettingsForSubscriptionName);
          assertThat(ctx.getBean("subscriberRetrySettings-subscription-name", RetrySettings.class))
              .isEqualTo(expectedRetrySettingsForSubscriptionName);
          assertThat(ctx.getBean("globalSubscriberRetrySettings", RetrySettings.class))
              .isEqualTo(expectedGlobalRetrySettings);
        });
  }

  @Test
  void customFlowControlUsedWhenProvided() {
    FlowControlSettings flowControlSettings = mock(FlowControlSettings.class);
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class)
            .withBean(
                "subscriberFlowControlSettings",
                FlowControlSettings.class,
                () -> flowControlSettings);

    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          assertThat(subscriberFactory.getFlowControlSettings("name"))
              .isSameAs(flowControlSettings);
          assertThat(ctx.containsBean("globalSubscriberFlowControlSettings")).isFalse();
        });
  }

  @Test
  void flowControlSettings_globalConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-element-Count=11",
                "spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-request-Bytes=12",
                "spring.cloud.gcp.pubsub.subscriber.flow-control.limit-exceeded-behavior=Ignore")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          PubSubConfiguration.FlowControl flowControlFromConfiguration =
              gcpPubSubProperties.getSubscriber().getFlowControl();
          FlowControlSettings expectedFlowControlSettings =
              FlowControlSettings.newBuilder()
                  .setMaxOutstandingElementCount(11L)
                  .setMaxOutstandingRequestBytes(12L)
                  .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Ignore)
                  .build();

          assertThat(flowControlFromConfiguration.getMaxOutstandingElementCount()).isEqualTo(11L);
          assertThat(flowControlFromConfiguration.getMaxOutstandingRequestBytes()).isEqualTo(12L);
          assertThat(flowControlFromConfiguration.getLimitExceededBehavior())
              .isEqualTo(FlowController.LimitExceededBehavior.Ignore);
          assertThat(subscriberFactory.getFlowControlSettings("name"))
              .isEqualTo(expectedFlowControlSettings);
          assertThat(ctx.getBean("globalSubscriberFlowControlSettings", FlowControlSettings.class))
              .isEqualTo(expectedFlowControlSettings);
        });
  }

  @Test
  void flowControlSettings_selectiveConfigurationSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.max-outstanding-element-Count=11",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.max-outstanding-request-Bytes=12",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.limit-exceeded-behavior=Ignore")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

          PubSubConfiguration.FlowControl flowControl =
              gcpPubSubProperties
                  .getSubscriber("subscription-name", projectIdProvider.getProjectId())
                  .getFlowControl();
          assertThat(flowControl.getMaxOutstandingElementCount()).isEqualTo(11L);
          assertThat(flowControl.getMaxOutstandingRequestBytes()).isEqualTo(12L);
          assertThat(flowControl.getLimitExceededBehavior())
              .isEqualTo(FlowController.LimitExceededBehavior.Ignore);
          assertThat(gcpPubSubProperties.getSubscription()).hasSize(1);
          assertThat(gcpPubSubProperties.getSubscription())
              .containsKey("projects/fake project/subscriptions/subscription-name");

          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          FlowControlSettings expectedFlowControlForSubscriptionName =
              FlowControlSettings.newBuilder()
                  .setMaxOutstandingElementCount(11L)
                  .setMaxOutstandingRequestBytes(12L)
                  .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Ignore)
                  .build();
          assertThat(subscriberFactory.getFlowControlSettings("subscription-name"))
              .isEqualTo(expectedFlowControlForSubscriptionName);
          assertThat(
                  ctx.getBean(
                      "subscriberFlowControlSettings-subscription-name", FlowControlSettings.class))
              .isEqualTo(expectedFlowControlForSubscriptionName);
        });
  }

  @Test
  void flowControlSettings_globalAndSelectiveConfigurationSet_selectiveTakesPrecedence() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-element-Count=10",
                "spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-request-Bytes=10",
                "spring.cloud.gcp.pubsub.subscriber.flow-control.limit-exceeded-behavior=Block",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.max-outstanding-element-Count=11",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.max-outstanding-request-Bytes=12",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.limit-exceeded-behavior=Ignore")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

          // Validate settings for subscribers that have subscription-specific flow control settings
          // property set
          PubSubConfiguration.FlowControl flowControl =
              gcpPubSubProperties.computeSubscriberFlowControlSettings(
                  "subscription-name", projectIdProvider.getProjectId());
          assertThat(flowControl.getMaxOutstandingElementCount()).isEqualTo(11L);
          assertThat(flowControl.getMaxOutstandingRequestBytes()).isEqualTo(12L);
          assertThat(flowControl.getLimitExceededBehavior())
              .isEqualTo(FlowController.LimitExceededBehavior.Ignore);

          // Validate settings for subscribers that do not have subscription-specific flow control
          // settings property set
          PubSubConfiguration.FlowControl flowControlForOtherSubscriber =
              gcpPubSubProperties.computeSubscriberFlowControlSettings(
                  "other", projectIdProvider.getProjectId());
          assertThat(flowControlForOtherSubscriber.getMaxOutstandingElementCount()).isEqualTo(10L);
          assertThat(flowControlForOtherSubscriber.getMaxOutstandingRequestBytes()).isEqualTo(10L);
          assertThat(flowControlForOtherSubscriber.getLimitExceededBehavior())
              .isEqualTo(FlowController.LimitExceededBehavior.Block);
          assertThat(gcpPubSubProperties.getSubscription()).hasSize(2);
          assertThat(gcpPubSubProperties.getSubscription())
              .containsKey("projects/fake project/subscriptions/subscription-name");
          assertThat(gcpPubSubProperties.getSubscription())
              .containsKey("projects/fake project/subscriptions/other");

          // Verify that beans for selective and global flow control settings are created.
          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          FlowControlSettings expectedFlowControlForSubscriptionName =
              FlowControlSettings.newBuilder()
                  .setMaxOutstandingElementCount(11L)
                  .setMaxOutstandingRequestBytes(12L)
                  .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Ignore)
                  .build();
          FlowControlSettings expectedFlowControlForOther =
              FlowControlSettings.newBuilder()
                  .setMaxOutstandingElementCount(10L)
                  .setMaxOutstandingRequestBytes(10L)
                  .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Block)
                  .build();
          assertThat(subscriberFactory.getFlowControlSettings("subscription-name"))
              .isEqualTo(expectedFlowControlForSubscriptionName);
          assertThat(
                  ctx.getBean(
                      "subscriberFlowControlSettings-subscription-name", FlowControlSettings.class))
              .isEqualTo(expectedFlowControlForSubscriptionName);
          assertThat(subscriberFactory.getFlowControlSettings("other"))
              .isEqualTo(expectedFlowControlForOther);
          assertThat(ctx.getBean("globalSubscriberFlowControlSettings", FlowControlSettings.class))
              .isEqualTo(expectedFlowControlForOther);
        });
  }

  @Test
  void flowControlSettings_globalAndDifferentSelectiveConfigurationSet_pickGlobal() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-element-Count=11",
                "spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-request-Bytes=12",
                "spring.cloud.gcp.pubsub.subscriber.flow-control.limit-exceeded-behavior=Ignore",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=2")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

          PubSubConfiguration.FlowControl flowControl =
              gcpPubSubProperties.computeSubscriberFlowControlSettings(
                  "subscription-name", projectIdProvider.getProjectId());
          assertThat(flowControl.getMaxOutstandingElementCount()).isEqualTo(11L);
          assertThat(flowControl.getMaxOutstandingRequestBytes()).isEqualTo(12L);
          assertThat(flowControl.getLimitExceededBehavior())
              .isEqualTo(FlowController.LimitExceededBehavior.Ignore);
          assertThat(gcpPubSubProperties.getSubscription()).hasSize(1);
          assertThat(gcpPubSubProperties.getSubscription())
              .containsKey("projects/fake project/subscriptions/subscription-name");

          // Verify that bean for global flow control settings is created.
          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          FlowControlSettings expectedFlowControlForSubscriptionName =
              FlowControlSettings.newBuilder()
                  .setMaxOutstandingElementCount(11L)
                  .setMaxOutstandingRequestBytes(12L)
                  .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Ignore)
                  .build();
          assertThat(subscriberFactory.getFlowControlSettings("subscription-name"))
              .isEqualTo(expectedFlowControlForSubscriptionName);
          assertThat(ctx.getBean("globalSubscriberFlowControlSettings", FlowControlSettings.class))
              .isEqualTo(expectedFlowControlForSubscriptionName);
        });
  }

  @Test
  void flowControlSettings_subsetOfProperties_pickGlobalWhenSelectiveNotSpecified() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withPropertyValues(
                "spring.cloud.gcp.pubsub.subscriber.flow-control.max-outstanding-element-Count=11",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.max-outstanding-request-Bytes=12",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.flow-control.limit-exceeded-behavior=Ignore",
                "spring.cloud.gcp.pubsub.subscription.subscription-name.parallel-pull-count=2")
            .withUserConfiguration(TestConfig.class);

    contextRunner.run(
        ctx -> {
          GcpPubSubProperties gcpPubSubProperties = ctx.getBean(GcpPubSubProperties.class);
          GcpProjectIdProvider projectIdProvider = ctx.getBean(GcpProjectIdProvider.class);

          PubSubConfiguration.FlowControl flowControl =
              gcpPubSubProperties.computeSubscriberFlowControlSettings(
                  "subscription-name", projectIdProvider.getProjectId());
          assertThat(flowControl.getMaxOutstandingElementCount()).isEqualTo(11L);
          assertThat(flowControl.getMaxOutstandingRequestBytes()).isEqualTo(12L);
          assertThat(flowControl.getLimitExceededBehavior())
              .isEqualTo(FlowController.LimitExceededBehavior.Ignore);

          // Verify that beans for selective and global flow control settings are created.
          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          FlowControlSettings expectedFlowControlForSubscriptionName =
              FlowControlSettings.newBuilder()
                  .setMaxOutstandingElementCount(11L)
                  .setMaxOutstandingRequestBytes(12L)
                  .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Ignore)
                  .build();
          FlowControlSettings expectedGlobalSettings =
              FlowControlSettings.newBuilder().setMaxOutstandingElementCount(11L).build();
          assertThat(subscriberFactory.getFlowControlSettings("subscription-name"))
              .isEqualTo(expectedFlowControlForSubscriptionName);
          assertThat(
                  ctx.getBean(
                      "subscriberFlowControlSettings-subscription-name", FlowControlSettings.class))
              .isEqualTo(expectedFlowControlForSubscriptionName);
          assertThat(ctx.getBean("globalSubscriberFlowControlSettings", FlowControlSettings.class))
              .isEqualTo(expectedGlobalSettings);
        });
  }

  @Test
  void createSubscriberStub_flowControlSettings_noPropertiesSet() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpPubSubAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class);
    contextRunner.run(
        ctx -> {
          DefaultSubscriberFactory subscriberFactory =
              ctx.getBean("defaultSubscriberFactory", DefaultSubscriberFactory.class);
          Subscriber subscriber =
              subscriberFactory.createSubscriber("subscription-name", (message, consumer) -> {});
          assertThat(subscriber.getFlowControlSettings())
              .isEqualTo(Subscriber.Builder.getDefaultFlowControlSettings());
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
