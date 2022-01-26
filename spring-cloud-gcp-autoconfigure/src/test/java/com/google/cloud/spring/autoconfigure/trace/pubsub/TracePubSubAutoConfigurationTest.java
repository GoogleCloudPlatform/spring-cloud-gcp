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

import static com.google.cloud.spring.autoconfigure.trace.StackdriverTraceAutoConfiguration.REPORTER_BEAN_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import brave.handler.SpanHandler;
import brave.http.HttpRequestParser;
import brave.http.HttpTracingCustomizer;
import com.google.api.core.ApiFunction;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.trace.MockConfiguration;
import com.google.cloud.spring.autoconfigure.trace.StackdriverTraceAutoConfiguration;
import com.google.cloud.spring.pubsub.core.publisher.PublisherCustomizer;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.sleuth.autoconfig.brave.BraveAutoConfiguration;
import org.springframework.cloud.sleuth.autoconfig.brave.instrument.messaging.BraveMessagingAutoConfiguration;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.Sender;

/** Tests for Trace Pub/Sub auto-config. */
class TracePubSubAutoConfigurationTest {

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  TracePubSubAutoConfiguration.class,
                  StackdriverTraceAutoConfiguration.class,
                  GcpContextAutoConfiguration.class,
                  BraveAutoConfiguration.class,
                  BraveMessagingAutoConfiguration.class,
                  RefreshAutoConfiguration.class))
          .withUserConfiguration(MockConfiguration.class)
          .withBean(
              StackdriverTraceAutoConfiguration.SPAN_HANDLER_BEAN_NAME,
              SpanHandler.class,
              () -> SpanHandler.NOOP)
          // Prevent healthcheck from triggering a real call to Trace.
          .withBean(REPORTER_BEAN_NAME, Reporter.class, () -> mock(Reporter.class))
          .withPropertyValues(
              "spring.cloud.gcp.project-id=proj", "spring.sleuth.sampler.probability=1.0");

  @Test
  void test() {
    this.contextRunner.run(
        context -> {
          assertThat(context.getBean(HttpRequestParser.class)).isNotNull();
          assertThat(context.getBean(HttpTracingCustomizer.class)).isNotNull();
          assertThat(
                  context.getBean(StackdriverTraceAutoConfiguration.SENDER_BEAN_NAME, Sender.class))
              .isNotNull();
          assertThat(context.getBean(ManagedChannel.class)).isNotNull();
        });
  }

  @Test
  void testPubSubTracingDisabledByDefault() {
    this.contextRunner.run(
        context -> {
          assertThat(context.getBeansOfType(TracePubSubBeanPostProcessor.class)).isEmpty();
          assertThat(context.getBeansOfType(PubSubTracing.class)).isEmpty();
        });
  }

  @Test
  void testPubSubTracingEnabled() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.trace.pubsub.enabled=true")
        .run(
            context -> {
              assertThat(context.getBean(TracePubSubBeanPostProcessor.class)).isNotNull();
              assertThat(context.getBean(PubSubTracing.class)).isNotNull();
            });
  }

  @Test
  void tracePubSubCustomizerAppliedLast() {
    PublisherCustomizer noopCustomizer = (pb, t) -> {};
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.trace.pubsub.enabled=true")
        .withBean(PublisherCustomizer.class, () -> noopCustomizer)
        .run(context -> {
          ObjectProvider<PublisherCustomizer> customizersProvider =
              context.getBeanProvider(PublisherCustomizer.class);
          List<PublisherCustomizer> customizers =
              customizersProvider.orderedStream().collect(Collectors.toList());
          assertThat(customizers).hasSize(2);

          // Object provider lists highest priority first, so default priority `noopCustomizer`
          // will be second
          assertThat(customizers.get(1)).isSameAs(noopCustomizer);

          PublisherCustomizer traceCustomizer = customizers.get(0);
          Publisher.Builder spyBuilder = spy(Publisher.newBuilder("test"));
          traceCustomizer.apply(spyBuilder, "test");
          verify(spyBuilder).setTransform(any(ApiFunction.class));
        });
  }
}
