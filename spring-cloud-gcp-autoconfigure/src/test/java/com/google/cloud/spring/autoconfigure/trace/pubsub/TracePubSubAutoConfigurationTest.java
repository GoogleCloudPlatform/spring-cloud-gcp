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

import brave.handler.SpanHandler;
import brave.http.HttpRequestParser;
import brave.http.HttpTracingCustomizer;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.trace.StackdriverTraceAutoConfiguration;
import com.google.cloud.spring.autoconfigure.trace.StackdriverTraceAutoConfigurationTests;
import io.grpc.ManagedChannel;
import org.junit.Test;
import zipkin2.reporter.Sender;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.cloud.sleuth.autoconfig.brave.instrument.messaging.BraveMessagingAutoConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Trace Pub/Sub auto-config.
 */
public class TracePubSubAutoConfigurationTest {

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					TracePubSubAutoConfiguration.class,
					StackdriverTraceAutoConfiguration.class,
					GcpContextAutoConfiguration.class,
					BraveAutoConfiguration.class,
					BraveMessagingAutoConfiguration.class,
					RefreshAutoConfiguration.class))
			.withUserConfiguration(StackdriverTraceAutoConfigurationTests.MockConfiguration.class)
			.withBean(
					StackdriverTraceAutoConfiguration.SPAN_HANDLER_BEAN_NAME,
					SpanHandler.class,
					() -> SpanHandler.NOOP)
			.withPropertyValues("spring.cloud.gcp.project-id=proj",
					"spring.sleuth.sampler.probability=1.0");

	@Test
	public void test() {
		this.contextRunner
				.run(context -> {
					assertThat(context.getBean(HttpRequestParser.class)).isNotNull();
					assertThat(context.getBean(HttpTracingCustomizer.class)).isNotNull();
					assertThat(context.getBean(StackdriverTraceAutoConfiguration.SENDER_BEAN_NAME, Sender.class)).isNotNull();
					assertThat(context.getBean(ManagedChannel.class)).isNotNull();
				});
	}


	@Test
	public void testPubSubTracingDisabledByDefault() {
		this.contextRunner.run(context -> {
			assertThat(context.getBeansOfType(TracePubSubBeanPostProcessor.class)).isEmpty();
			assertThat(context.getBeansOfType(PubSubTracing.class)).isEmpty();
		});
	}

	@Test
	public void testPubSubTracingEnabled() {
		this.contextRunner
				.withPropertyValues("spring.cloud.gcp.trace.pubsub.enabled=true")
				.run(context -> {
					assertThat(context.getBean(TracePubSubBeanPostProcessor.class)).isNotNull();
					assertThat(context.getBean(PubSubTracing.class)).isNotNull();
				});
	}

}
