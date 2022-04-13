/*
 * Copyright 2021-2021 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.pubsub.it;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.batching.FlowController;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubProperties;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.threeten.bp.Duration;

@EnabledIfSystemProperty(named = "it.pubsub", matches = "true")
class PubSubAutoConfigurationIntegrationTests {

  private static final Log LOGGER =
      LogFactory.getLog(PubSubAutoConfigurationIntegrationTests.class);

  private static GcpProjectIdProvider projectIdProvider;

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withPropertyValues(
              "spring.cloud.gcp.pubsub.subscriber.retryableCodes=INTERNAL",
              "spring.cloud.gcp.pubsub.subscription.test-sub-1.executor-threads=3",
              "spring.cloud.gcp.pubsub.subscription.test-sub-1.retry.total-timeout-seconds=600",
              "spring.cloud.gcp.pubsub.subscription.test-sub-1.retry.initial-retry-delay-seconds=100",
              "spring.cloud.gcp.pubsub.subscription.test-sub-1.retry.retry-delay-multiplier=1.3",
              "spring.cloud.gcp.pubsub.subscription.test-sub-1.retry.max-retry-delay-seconds=600",
              "spring.cloud.gcp.pubsub.subscription.test-sub-1.retry.max-attempts=1",
              "spring.cloud.gcp.pubsub.subscription.test-sub-1.retry.initial-rpc-timeout-seconds=600",
              "spring.cloud.gcp.pubsub.subscription.test-sub-1.retry.rpc-timeout-multiplier=1",
              "spring.cloud.gcp.pubsub.subscription.test-sub-1.retry.max-rpc-timeout-seconds=600",
              "spring.cloud.gcp.pubsub.subscription.test-sub-2.executor-threads=1",
              "spring.cloud.gcp.pubsub.subscription.test-sub-2.max-ack-extension-period=0",
              "spring.cloud.gcp.pubsub.subscription.test-sub-2.parallel-pull-count=1",
              "spring.cloud.gcp.pubsub.subscription.test-sub-2.flow-control.max-outstanding-element-Count=1",
              "spring.cloud.gcp.pubsub.subscription.test-sub-2.flow-control.max-outstanding-request-Bytes=1",
              "spring.cloud.gcp.pubsub.subscription.test-sub-2.flow-control.limit-exceeded-behavior=Ignore")
          .withConfiguration(
              AutoConfigurations.of(
                  GcpContextAutoConfiguration.class, GcpPubSubAutoConfiguration.class));

  @BeforeAll
  static void enableTests() {
    projectIdProvider = new DefaultGcpProjectIdProvider();
  }
  /*

  @Test
  void testPull() {

    this.contextRunner.run(
        context -> {
          PubSubAdmin pubSubAdmin = context.getBean(PubSubAdmin.class);
          String topicName = "test-topic";
          String subscriptionName = "test-sub-1";
          if (pubSubAdmin.getTopic(topicName) != null) {
            pubSubAdmin.deleteTopic(topicName);
          }
          if (pubSubAdmin.getSubscription(subscriptionName) != null) {
            pubSubAdmin.deleteSubscription(subscriptionName);
          }

          pubSubAdmin.createTopic(topicName);
          pubSubAdmin.createSubscription(subscriptionName, topicName, 10);

          PubSubTemplate pubSubTemplate = context.getBean(PubSubTemplate.class);

          pubSubTemplate.publish(topicName, "message1");
          pubSubTemplate.pull(subscriptionName, 4, false);

          // Validate auto-config properties
          GcpPubSubProperties gcpPubSubProperties = context.getBean(GcpPubSubProperties.class);
          RetrySettings expectedRetrySettings =
              RetrySettings.newBuilder()
                  .setTotalTimeout(Duration.ofSeconds(600L))
                  .setInitialRetryDelay(Duration.ofSeconds(100L))
                  .setRetryDelayMultiplier(1.3)
                  .setMaxRetryDelay(Duration.ofSeconds(600L))
                  .setMaxAttempts(1)
                  .setInitialRpcTimeout(Duration.ofSeconds(600L))
                  .setRpcTimeoutMultiplier(1)
                  .setMaxRpcTimeout(Duration.ofSeconds(600L))
                  .build();/* @Test
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

          PubSubConfiguration.Retry retry =
              gcpPubSubProperties.computeSubscriberRetrySettings(
                  subscriptionName, projectIdProvider.getProjectId());
          assertThat(retry.getTotalTimeoutSeconds()).isEqualTo(600L);
          assertThat(retry.getInitialRetryDelaySeconds()).isEqualTo(100L);
          assertThat(retry.getRetryDelayMultiplier()).isEqualTo(1.3);
          assertThat(retry.getMaxRetryDelaySeconds()).isEqualTo(600L);
          assertThat(retry.getMaxAttempts()).isEqualTo(1);
          assertThat(retry.getInitialRpcTimeoutSeconds()).isEqualTo(600L);
          assertThat(retry.getRpcTimeoutMultiplier()).isEqualTo(1);
          assertThat(retry.getMaxRpcTimeoutSeconds()).isEqualTo(600L);
          ThreadPoolTaskScheduler scheduler =
              (ThreadPoolTaskScheduler) context.getBean("threadPoolScheduler_test-sub-1");
          assertThat(scheduler).isNotNull();
          assertThat(scheduler.getThreadNamePrefix()).isEqualTo("gcp-pubsub-subscriber-test-sub-1");
          assertThat(scheduler.isDaemon()).isTrue();
          assertThat(
                  (ThreadPoolTaskScheduler)
                      context.getBean("globalPubSubSubscriberThreadPoolScheduler"))
              .isNotNull();
          assertThat((ExecutorProvider) context.getBean("subscriberExecutorProvider-test-sub-1"))
              .isNotNull();
          assertThat((ExecutorProvider) context.getBean("globalSubscriberExecutorProvider"))
              .isNotNull();
          assertThat(
                  gcpPubSubProperties.computeRetryableCodes(
                      "test-sub-1", projectIdProvider.getProjectId()))
              .isEqualTo(new Code[] {Code.INTERNAL});
          assertThat((RetrySettings) context.getBean("subscriberRetrySettings-test-sub-1"))
              .isEqualTo(expectedRetrySettings);

          pubSubAdmin.deleteSubscription(subscriptionName);
          pubSubAdmin.deleteTopic(topicName);
        });
  }

*/
  // THIS TEST SHOWS ISSUES WELL
  @Test
  void testSubscribe() {
    this.contextRunner.run(
        context -> {
          PubSubAdmin pubSubAdmin = context.getBean(PubSubAdmin.class);
          PubSubTemplate pubSubTemplate = context.getBean(PubSubTemplate.class);

          String topicName = "test-topic";
          String subscriptionName = "test-sub-2";
          if (pubSubAdmin.getTopic(topicName) != null) {
            pubSubAdmin.deleteTopic(topicName);
          }
          if (pubSubAdmin.getSubscription(subscriptionName) != null) {
            pubSubAdmin.deleteSubscription(subscriptionName);
          }

          assertThat(pubSubAdmin.getTopic(topicName)).isNull();
          assertThat(pubSubAdmin.getSubscription(subscriptionName)).isNull();
          pubSubAdmin.createTopic(topicName);
          pubSubAdmin.createSubscription(subscriptionName, topicName);
          pubSubTemplate.publish(topicName, "tatatatata").get();
          pubSubTemplate.subscribe(
              subscriptionName,
              message -> {
                LOGGER.info(
                    "Message received from "
                        + subscriptionName
                        + " subscription: "
                        + message.getPubsubMessage().getData().toStringUtf8());
                message.ack();
              });
//  -> {GenericBeanDefinition@8518} "Generic bean: class [com.google.api.gax.batching.FlowControlSettings]; scope=; abstract=false; lazyInit=null; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null"
          // Validate auto-config properties
          GcpPubSubProperties gcpPubSubProperties = context.getBean(GcpPubSubProperties.class);

          /*
          PubSubConfiguration.FlowControl flowControl =
              gcpPubSubProperties.computeSubscriberFlowControlSettings(
                  subscriptionName, projectIdProvider.getProjectId());

          */

          FlowControlSettings flowControlSettings =
              FlowControlSettings.newBuilder()
                  .setMaxOutstandingElementCount(1L)
                  .setMaxOutstandingRequestBytes(1L)
                  .setLimitExceededBehavior(FlowController.LimitExceededBehavior.Ignore)
                  .build();
          assertThat(
                  (FlowControlSettings) context.getBean("subscriberFlowControlSettings-test-sub-2"))
              .isEqualTo(flowControlSettings);
/*          assertThat(flowControl.getMaxOutstandingElementCount()).isEqualTo(1L);
          assertThat(flowControl.getMaxOutstandingRequestBytes()).isEqualTo(1L);
          assertThat(flowControl.getLimitExceededBehavior())
              .isEqualTo(FlowController.LimitExceededBehavior.Ignore);*/
          assertThat(
                  gcpPubSubProperties.computeMaxAckExtensionPeriod(
                      subscriptionName, projectIdProvider.getProjectId()))
              .isZero();
          assertThat(
                  gcpPubSubProperties.computeParallelPullCount(
                      subscriptionName, projectIdProvider.getProjectId()))
              .isEqualTo(1);
          ThreadPoolTaskScheduler scheduler =
              (ThreadPoolTaskScheduler) context.getBean("threadPoolScheduler_test-sub-2");
          assertThat(scheduler).isNotNull();
          assertThat(scheduler.getThreadNamePrefix()).isEqualTo("gcp-pubsub-subscriber-test-sub-2");
          assertThat(scheduler.isDaemon()).isTrue();
          assertThat(
                  (ThreadPoolTaskScheduler)
                      context.getBean("globalPubSubSubscriberThreadPoolScheduler"))
              .isNotNull();
          assertThat((ExecutorProvider) context.getBean("subscriberExecutorProvider-test-sub-2"))
              .isNotNull();
          assertThat((ExecutorProvider) context.getBean("globalSubscriberExecutorProvider"))
              .isNotNull();

          pubSubAdmin.deleteSubscription(subscriptionName);
          pubSubAdmin.deleteTopic(topicName);


        });
  }
}
