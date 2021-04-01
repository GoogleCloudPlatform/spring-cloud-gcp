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

package com.google.cloud.spring.autoconfigure.pubsublite;

import java.io.IOException;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsublite.GcpPubSubLiteProperties.FlowControl;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.StreamingSubscriberFactory;
import com.google.cloud.spring.pubsublite.support.DefaultPublisherFactory;
import com.google.cloud.spring.pubsublite.support.DefaultSubscriberFactory;
import com.google.cloud.spring.pubsublite.support.PublisherFactorySettings;
import com.google.cloud.spring.pubsublite.support.SubscriberFactorySettings;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(GcpContextAutoConfiguration.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.pubsublite.enabled", matchIfMissing = true)
@ConditionalOnClass(PublisherFactorySettings.class)
@EnableConfigurationProperties(GcpPubSubLiteProperties.class)
public class GcpPubSubLiteAutoConfiguration {
	private final GcpPubSubLiteProperties properties;

	private final GcpProjectIdProvider finalProjectIdProvider;

	private final CredentialsProvider finalCredentialsProvider;

	public GcpPubSubLiteAutoConfiguration(GcpPubSubLiteProperties properties,
			GcpProjectIdProvider gcpProjectIdProvider,
			CredentialsProvider credentialsProvider) throws IOException {
		this.properties = properties;
		this.finalProjectIdProvider = (properties.getProjectId() != null)
				? properties::getProjectId
				: gcpProjectIdProvider;
		this.finalCredentialsProvider = properties.getCredentials().hasKey()
				? new DefaultCredentialsProvider(properties)
				: credentialsProvider;
	}

	@Bean
	@ConditionalOnMissingBean
	public PublisherFactory defaultPublisherFactory() {
		PublisherFactorySettings settings = PublisherFactorySettings.newBuilder()
				.setLocation(CloudZone.parse(properties.getLocation()))
				.setProjectIdProvider(finalProjectIdProvider)
				.setCredentialsProvider(finalCredentialsProvider)
				.build();
		return new DefaultPublisherFactory(settings);
	}

	@Bean
	@ConditionalOnMissingBean
	public StreamingSubscriberFactory defaultSubscriberFactory() {
		FlowControl flowControl = properties.getSubscriber().getPerPartitionFlowControl();
		SubscriberFactorySettings settings = SubscriberFactorySettings.newBuilder()
				.setLocation(CloudZone.parse(properties.getLocation()))
				.setProjectIdProvider(finalProjectIdProvider)
				.setCredentialsProvider(finalCredentialsProvider)
				.setPerPartitionFlowControlSettings(FlowControlSettings.builder()
						.setBytesOutstanding(flowControl.getMaxOutstandingRequestBytes())
						.setMessagesOutstanding(flowControl.getMaxOutstandingElementCount())
						.build())
				.build();
		return new DefaultSubscriberFactory(settings);
	}
}
