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

package com.google.cloud.spring.autoconfigure.trace.pubsub;

import brave.Tracing;
import brave.messaging.MessagingTracing;
import com.google.cloud.spring.pubsub.support.PublisherFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.cloud.sleuth.autoconfig.brave.instrument.messaging.BraveMessagingAutoConfiguration;
import org.springframework.cloud.sleuth.brave.instrument.messaging.ConditionalOnMessagingEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMessagingEnabled
@ConditionalOnBean(Tracing.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.trace.pubsub.enabled", matchIfMissing = false)
@ConditionalOnClass({ PublisherFactory.class, MessagingTracing.class })
@AutoConfigureAfter({ BraveAutoConfiguration.class, BraveMessagingAutoConfiguration.class })
class TracePubSubAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	static TracePubSubBeanPostProcessor tracePubSubBeanPostProcessor(BeanFactory beanFactory) {
		return new TracePubSubBeanPostProcessor(beanFactory);
	}

	@Bean
	@ConditionalOnMissingBean
	PubSubTracing pubSubTracing(MessagingTracing messagingTracing) {
		return PubSubTracing.newBuilder(messagingTracing).build();
	}

}
