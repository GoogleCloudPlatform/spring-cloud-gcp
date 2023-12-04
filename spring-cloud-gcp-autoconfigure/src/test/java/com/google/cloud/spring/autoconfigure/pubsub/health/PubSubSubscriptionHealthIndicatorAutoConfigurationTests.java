/*
 * Copyright 2018-2020 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.pubsub.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Tests for Pub/Sub Health Indicator autoconfiguration.
 */
class PubSubSubscriptionHealthIndicatorAutoConfigurationTests {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  PubSubSubscriptionHealthIndicatorAutoConfiguration.class,
                  GcpPubSubAutoConfiguration.class))
          .withBean(GcpProjectIdProvider.class, () -> () -> "fake project")
          .withBean(CredentialsProvider.class, () -> () -> mock(Credentials.class))
          .withBean(MetricServiceClient.class, () -> mock(MetricServiceClient.class));

  @Test
  void healthIndicatorPresent_defaults() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.pubsub.health.lagThreshold=1",
            "spring.cloud.gcp.pubsub.health.backlogThreshold=1")
        .run(ctx -> assertThat(ctx).hasSingleBean(PubSubSubscriptionHealthIndicator.class));
  }

  @Test
  void healthIndicatorNotPresent_whenDisabled() {
    this.contextRunner
        .withPropertyValues(
            "management.health.pubsub-subscriber.enabled:false",
            "spring.cloud.gcp.pubsub.health.lagThreshold=1",
            "spring.cloud.gcp.pubsub.health.backlogThreshold=1")
        .run(ctx -> assertThat(ctx).doesNotHaveBean(PubSubSubscriptionHealthIndicator.class));
  }

  @Test
  void healthIndicatorNotPresent_whenMissingLagThreshold() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.pubsub.health.backlogThreshold=1")
        .run(ctx -> assertThat(ctx).doesNotHaveBean(PubSubSubscriptionHealthIndicator.class));
  }

  @Test
  void healthIndicatorNotPresent_whenMissingBacklogThreshold() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.pubsub.health.lagThreshold=1")
        .run(ctx -> assertThat(ctx).doesNotHaveBean(PubSubSubscriptionHealthIndicator.class));
  }
}
