/*
 * Copyright 2017-2021 the original author or authors.
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

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.cloud.monitoring.v3.MetricServiceSettings;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubProperties;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistryImpl;
import java.io.IOException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.health.autoconfigure.contributor.CompositeHealthContributorConfiguration;
import org.springframework.boot.health.autoconfigure.contributor.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.StringUtils;

/**
 * @since 2.0.6
 */
@AutoConfiguration
@ConditionalOnClass({HealthIndicator.class, MetricServiceClient.class})
@ConditionalOnEnabledHealthIndicator("pubsub-subscriber")
@ConditionalOnProperty({
  "spring.cloud.gcp.pubsub.health.lagThreshold",
  "spring.cloud.gcp.pubsub.health.backlogThreshold"
})
@AutoConfigureBefore(GcpPubSubAutoConfiguration.class)
@EnableConfigurationProperties(GcpPubSubProperties.class)
public class PubSubSubscriptionHealthIndicatorAutoConfiguration
    extends CompositeHealthContributorConfiguration<
        PubSubSubscriptionHealthIndicator, HealthTrackerRegistry> {

  private final GcpPubSubProperties gcpPubSubProperties;

  private final String projectId;

  @Nullable private final CredentialsProvider credentialsProvider;

  public PubSubSubscriptionHealthIndicatorAutoConfiguration(
      GcpPubSubProperties gcpPubSubProperties, GcpProjectIdProvider projectIdProvider) {
    this(gcpPubSubProperties, projectIdProvider, null);
  }

  @Autowired
  public PubSubSubscriptionHealthIndicatorAutoConfiguration(
      GcpPubSubProperties gcpPubSubProperties,
      GcpProjectIdProvider projectIdProvider,
      @Nullable ObjectProvider<CredentialsProvider> credentialsProvider) {
    super(PubSubSubscriptionHealthIndicator::new);
    this.projectId =
        (gcpPubSubProperties.getProjectId() != null)
            ? gcpPubSubProperties.getProjectId()
            : projectIdProvider.getProjectId();
    this.gcpPubSubProperties = gcpPubSubProperties;
    this.credentialsProvider =
        credentialsProvider != null ? credentialsProvider.getIfAvailable() : null;
  }

  @Bean
  @ConditionalOnMissingBean
  public MetricServiceClient metricServiceClient() throws IOException {
    MetricServiceSettings.Builder settingsBuilder = MetricServiceSettings.newBuilder();
    // Propagate configured credentials and universe domain so lag/backlog health checks do not
    // fall back to default ADC or googleapis.com.
    CredentialsProvider resolvedCredentialsProvider =
        this.gcpPubSubProperties.getCredentials().hasKey()
            ? new DefaultCredentialsProvider(this.gcpPubSubProperties)
            : this.credentialsProvider;
    if (resolvedCredentialsProvider != null) {
      settingsBuilder.setCredentialsProvider(resolvedCredentialsProvider);
    }
    String universeDomain = resolveUniverseDomain(this.gcpPubSubProperties);
    if (universeDomain != null) {
      settingsBuilder.setUniverseDomain(universeDomain);
    }
    return MetricServiceClient.create(settingsBuilder.build());
  }

  /**
   * Resolves the configured universe domain from publisher/subscriber properties, failing if both
   * are explicitly set to different values.
   */
  private static String resolveUniverseDomain(GcpPubSubProperties properties) {
    String publisherUniverseDomain = properties.getPublisher().getUniverseDomain();
    String subscriberUniverseDomain = properties.getSubscriber().getUniverseDomain();
    boolean hasPublisher = StringUtils.hasText(publisherUniverseDomain);
    boolean hasSubscriber = StringUtils.hasText(subscriberUniverseDomain);
    if (hasPublisher
        && hasSubscriber
        && !publisherUniverseDomain.equals(subscriberUniverseDomain)) {
      throw new IllegalArgumentException(
          String.format(
              "Conflicting universe domains configured: publisher (%s) and subscriber (%s) must"
                  + " match.",
              publisherUniverseDomain, subscriberUniverseDomain));
    }
    return hasPublisher
        ? publisherUniverseDomain
        : (hasSubscriber ? subscriberUniverseDomain : null);
  }

  @Bean
  @ConditionalOnMissingBean(name = "pubSubHealthThreadPool")
  public ThreadPoolTaskScheduler pubSubHealthThreadPool() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(gcpPubSubProperties.getHealth().getExecutorThreads());
    scheduler.setThreadNamePrefix("gcp-pubsub-health");
    scheduler.setDaemon(true);
    return scheduler;
  }

  @Bean
  @ConditionalOnMissingBean(name = "healthCheckExecutorProvider")
  public ExecutorProvider healthCheckExecutorProvider(
      @Qualifier("pubSubHealthThreadPool") ThreadPoolTaskScheduler scheduler) {
    return FixedExecutorProvider.create(scheduler.getScheduledExecutor());
  }

  @Bean
  @ConditionalOnMissingBean(name = "healthTrackerRegistry")
  public HealthTrackerRegistry healthTrackerRegistry(
      MetricServiceClient metricServiceClient,
      @Qualifier("healthCheckExecutorProvider") ExecutorProvider executorProvider) {
    return new HealthTrackerRegistryImpl(
        projectId,
        metricServiceClient,
        gcpPubSubProperties.getHealth().getLagThreshold(),
        gcpPubSubProperties.getHealth().getBacklogThreshold(),
        gcpPubSubProperties.getHealth().getLookUpInterval(),
        executorProvider);
  }

  @Bean
  @ConditionalOnMissingBean(name = "pubSubSubscriptionHealthIndicator")
  public PubSubSubscriptionHealthIndicator pubSubSubscriptionHealthIndicator(
      HealthTrackerRegistry healthTrackerRegistry) {
    return new PubSubSubscriptionHealthIndicator(healthTrackerRegistry);
  }
}
