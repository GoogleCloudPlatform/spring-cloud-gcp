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


import java.io.IOException;

import com.google.api.gax.core.ExecutorProvider;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubProperties;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistryImpl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthContributorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Emmanouil Gkatziouras
 *
 * @since 2.0.5
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({HealthIndicator.class})
@ConditionalOnEnabledHealthIndicator("pubsub-subscriber")
@ConditionalOnProperty({"spring.cloud.gcp.pubsub.subscriber.lagThreshold", "spring.cloud.gcp.pubsub.subscriber.backlogThreshold"})
@Import(PubSubExecutorConfiguration.class)
@AutoConfigureBefore(GcpPubSubAutoConfiguration.class)
@EnableConfigurationProperties(GcpPubSubProperties.class)
public class PubSubSubscriptionHealthIndicatorAutoConfiguration  extends
	CompositeHealthContributorConfiguration<PubSubHealthIndicator, PubSubTemplate> {

	private final GcpPubSubProperties gcpPubSubProperties;

	public PubSubSubscriptionHealthIndicatorAutoConfiguration(
		GcpPubSubProperties gcpPubSubProperties) {
		this.gcpPubSubProperties = gcpPubSubProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public MetricServiceClient metricServiceClient() throws IOException {
		return MetricServiceClient.create();
	}

	@Bean
	@ConditionalOnMissingBean(name = "healthTrackerRegistry")
	public HealthTrackerRegistry healthTrackerRegistry(
		MetricServiceClient metricServiceClient,
		@Qualifier("subscriberExecutorProvider") ExecutorProvider executorProvider) {
		return new HealthTrackerRegistryImpl(metricServiceClient,
			gcpPubSubProperties.getSubscriber().getThreshold().getLagThreshold(),
			gcpPubSubProperties.getSubscriber().getThreshold().getBacklogThreshold(),
			gcpPubSubProperties.getSubscriber().getThreshold().getLookUpInterval(), executorProvider);
	}

	@Bean
	@ConditionalOnMissingBean(name = "pubSubSubscriptionHealthIndicator")
	public PubSubSubscriptionHealthIndicator pubSubSubscriptionHealthIndicator(HealthTrackerRegistry healthTrackerRegistry) {
		return new PubSubSubscriptionHealthIndicator(healthTrackerRegistry);
	}

}
