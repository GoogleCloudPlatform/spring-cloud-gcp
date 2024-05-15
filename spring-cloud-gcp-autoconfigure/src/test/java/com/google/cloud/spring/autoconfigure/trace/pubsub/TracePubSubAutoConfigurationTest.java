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

import brave.Tracing;
import brave.handler.SpanHandler;
import brave.messaging.MessagingTracing;
import com.google.api.core.ApiFunction;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.trace.MockConfiguration;
import com.google.cloud.spring.autoconfigure.trace.StackdriverTraceAutoConfiguration;
import com.google.cloud.spring.pubsub.core.publisher.PublisherCustomizer;
import io.grpc.ManagedChannel;
import io.micrometer.observation.aop.ObservedAspect;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.Sender;

/** Tests for Trace Pub/Sub auto-config. */
class TracePubSubAutoConfigurationTest {

  private ApplicationContextRunner contextRunner;

  @BeforeEach
  void init() {
    contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    TracePubSubAutoConfiguration.class,
                    StackdriverTraceAutoConfiguration.class,
                    GcpContextAutoConfiguration.class,
                    RefreshAutoConfiguration.class,
                    ObservationAutoConfiguration.class))
            .withUserConfiguration(MockConfiguration.class)
            .withBean(
                StackdriverTraceAutoConfiguration.SPAN_HANDLER_BEAN_NAME,
                SpanHandler.class,
                () -> SpanHandler.NOOP)
            // Prevent health-check from triggering a real call to Trace.
            .withBean(REPORTER_BEAN_NAME, Reporter.class, () -> mock(Reporter.class))
            .withPropertyValues(
                "spring.cloud.gcp.project-id=proj");
  }

  @Test
  void test() {
    this.contextRunner.run(
        context -> {
          assertThat(
                  context.getBean(StackdriverTraceAutoConfiguration.SENDER_BEAN_NAME, Sender.class))
              .isNotNull();
          assertThat(context.getBean(ManagedChannel.class)).isNotNull();
        });
  }

  @Test
  void testPubSubTracingDisabledByDefault() {
    this.contextRunner
        .run(
            context -> {
              assertThat(context.getBeansOfType(TracePubSubBeanPostProcessor.class)).isEmpty();
              assertThat(context.getBeansOfType(PubSubTracing.class)).isEmpty();
              assertThat(context.getBeansOfType(MessagingTracing.class)).isEmpty();
            });
  }

  @Test
  void testPubSubTracingEnabled() {
    Tracing tracing = Tracing.newBuilder().build();
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.trace.pubsub.enabled=true")
        .withBean(Tracing.class, () -> tracing)
        .run(
            context -> {
              assertThat(context.getBean(TracePubSubBeanPostProcessor.class)).isNotNull();
              assertThat(context.getBean(PubSubTracing.class)).isNotNull();
              assertThat(context.getBean(MessagingTracing.class)).isNotNull();
              assertThat(context.getBean(ObservedAspect.class)).isNotNull();
            });
  }

  @Test
  void tracePubSubCustomizerAppliedLast() {
    PublisherCustomizer noopCustomizer = (pb, t) -> {};
    Tracing tracing = Tracing.newBuilder().build();
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.trace.pubsub.enabled=true")
        .withBean(Tracing.class, () -> tracing)
        .withBean(MessagingTracing.class, () -> MessagingTracing.newBuilder(tracing).build())
        .withBean(PublisherCustomizer.class, () -> noopCustomizer)
        .run(context -> {
          ObjectProvider<PublisherCustomizer> customizersProvider =
              context.getBeanProvider(PublisherCustomizer.class);
          List<PublisherCustomizer> customizers =
              customizersProvider.orderedStream().collect(Collectors.toList());
          assertThat(customizers).hasSize(2);

          // Object provider lists the highest priority first, so default priority `noopCustomizer`
          // will be second
          assertThat(customizers.get(1)).isSameAs(noopCustomizer);

          PublisherCustomizer traceCustomizer = customizers.get(0);
          Publisher.Builder spyBuilder = spy(Publisher.newBuilder("test"));
          traceCustomizer.apply(spyBuilder, "test");
          verify(spyBuilder).setTransform(any(ApiFunction.class));
        });
  }
}
