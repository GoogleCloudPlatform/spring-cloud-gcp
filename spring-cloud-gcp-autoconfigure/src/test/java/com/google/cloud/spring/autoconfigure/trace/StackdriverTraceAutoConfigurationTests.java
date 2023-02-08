/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import brave.Tracer;
import brave.TracingCustomizer;
import brave.handler.SpanHandler;
import brave.propagation.TraceContextOrSamplingFlags;
import com.google.api.gax.core.ExecutorProvider;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.trace.StackdriverTraceAutoConfigurationTests.MultipleSpanHandlersConfig.GcpTraceService;
import com.google.cloud.spring.autoconfigure.trace.StackdriverTraceAutoConfigurationTests.MultipleSpanHandlersConfig.OtherSender;
import com.google.devtools.cloudtrace.v2.BatchWriteSpansRequest;
import com.google.devtools.cloudtrace.v2.Span;
import com.google.devtools.cloudtrace.v2.TraceServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import zipkin2.Call;
import zipkin2.CheckResult;
import zipkin2.codec.BytesEncoder;
import zipkin2.codec.Encoding;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.stackdriver.StackdriverSender;

/** Tests for auto-config. */
class StackdriverTraceAutoConfigurationTests {

  private ApplicationContextRunner contextRunner;

  @BeforeEach
  void init() {
    contextRunner = new ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                StackdriverTraceAutoConfiguration.class,
                GcpContextAutoConfiguration.class,
                BraveAutoConfiguration.class,
                RefreshAutoConfiguration.class))
        .withUserConfiguration(MockConfiguration.class)
        .withPropertyValues(
            "spring.cloud.gcp.project-id=proj");
  }

  @Test
  void test() {
    this.contextRunner
        .withBean(
            StackdriverTraceAutoConfiguration.SPAN_HANDLER_BEAN_NAME,
            SpanHandler.class,
            () -> SpanHandler.NOOP)
        .run(
            context -> {
              assertThat(
                      context.getBean(
                          StackdriverTraceAutoConfiguration.SENDER_BEAN_NAME, Sender.class))
                  .isNotNull();
              assertThat(context.getBean(ManagedChannel.class)).isNotNull();
            });
  }

  @Test
  void testEncodingSchema() {
    this.contextRunner
        .run(
            context -> assertThat(
                context.getBean(BytesEncoder.class))
                .isEqualTo(SpanBytesEncoder.PROTO3));
  }

  @Test
  void testDefaultConfig() {
    this.contextRunner
        .withBean(
            StackdriverTraceAutoConfiguration.SPAN_HANDLER_BEAN_NAME,
            SpanHandler.class,
            () -> SpanHandler.NOOP)
        .run(
            context -> {
              assertThat(
                      context.getBean(
                          StackdriverTraceAutoConfiguration.SENDER_BEAN_NAME, Sender.class))
                  .isNotNull()
                  .isInstanceOf(StackdriverSender.class);
              final StackdriverSender sender =
                  (StackdriverSender)
                      context.getBean(
                          StackdriverTraceAutoConfiguration.SENDER_BEAN_NAME, Sender.class);
              final CallOptions callOptions =
                  (CallOptions) FieldUtils.readField(sender, "callOptions", true);
              assertThat(callOptions).isNotNull();
              assertThat(callOptions.getMaxInboundMessageSize()).isNull();
              assertThat(callOptions.getMaxOutboundMessageSize()).isNull();
              assertThat(callOptions.getCompressor()).isNull();
              assertThat(callOptions.getAuthority()).isNull();
              assertThat(callOptions.isWaitForReady()).isFalse();
              assertThat(FieldUtils.readField(sender, "serverResponseTimeoutMs", true))
                  .isEqualTo(5000L);
            });
  }

  @Test
  void testServerResponseTimeout() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.trace.server-response-timeout-ms=1000")
        .withBean(
            StackdriverTraceAutoConfiguration.SPAN_HANDLER_BEAN_NAME,
            SpanHandler.class,
            () -> SpanHandler.NOOP)
        .run(
            context -> {
              assertThat(
                      context.getBean(
                          StackdriverTraceAutoConfiguration.SENDER_BEAN_NAME, Sender.class))
                  .isNotNull()
                  .isInstanceOf(StackdriverSender.class);
              final StackdriverSender sender =
                  (StackdriverSender)
                      context.getBean(
                          StackdriverTraceAutoConfiguration.SENDER_BEAN_NAME, Sender.class);
              assertThat(FieldUtils.readField(sender, "serverResponseTimeoutMs", true))
                  .isEqualTo(1000L);
            });
  }

  @Test
  void supportsMultipleReporters() {
    this.contextRunner
        .withConfiguration(
            AutoConfigurations.of(
                BraveAutoConfiguration.class,
                StackdriverTraceAutoConfiguration.class,
                GcpContextAutoConfiguration.class,
                RefreshAutoConfiguration.class))
        .withUserConfiguration(MultipleSpanHandlersConfig.class)
        .run(
            context -> {
              assertThat(context.getBean(ManagedChannel.class)).isNotNull();
              assertThat(context.getBeansOfType(Sender.class)).hasSize(2);
              assertThat(context.getBeansOfType(Sender.class))
                  .containsKeys("stackdriverSender", "otherSender");
              assertThat(context.getBeansOfType(SpanHandler.class))
                  .containsKeys("stackdriverSpanHandler", "otherSpanHandler");

              brave.Span span = context
                  .getBean(Tracer.class)
                  // always send the trace
                  .nextSpan(TraceContextOrSamplingFlags.SAMPLED)
                  .name("foo")
                  .tag("foo", "bar")
                  .start();
              span.finish();
              String spanId = span.context().spanIdString();
              GcpTraceService gcpTraceService =
                  context.getBean(GcpTraceService.class);

              await()
                  .atMost(10, TimeUnit.SECONDS)
                  .pollInterval(Duration.ofSeconds(1))
                  .untilAsserted(
                      () -> {
                        assertThat(gcpTraceService.hasSpan(spanId)).isTrue();

                        Span traceSpan = gcpTraceService.getSpan(spanId);
                        assertThat(traceSpan.getDisplayName().getValue()).isEqualTo("foo");
                        assertThat(traceSpan.getAttributes().getAttributeMapMap())
                            .containsKey("foo");
                        assertThat(
                                traceSpan
                                    .getAttributes()
                                    .getAttributeMapMap()
                                    .get("foo")
                                    .getStringValue()
                                    .getValue())
                            .isEqualTo("bar");
                      });

              OtherSender sender =
                  (OtherSender) context.getBean("otherSender");
              await()
                  .atMost(10, TimeUnit.SECONDS)
                  .untilAsserted(() -> assertThat(sender.isSpanSent()).isTrue());
            });
  }

  @Test
  void testAsyncReporterHealthCheck() {
    Sender senderMock = mock(Sender.class);
    when(senderMock.check()).thenReturn(CheckResult.OK);
    when(senderMock.encoding()).thenReturn(SpanBytesEncoder.PROTO3.encoding());

    this.contextRunner
        .withBean(
            StackdriverTraceAutoConfiguration.SENDER_BEAN_NAME, Sender.class, () -> senderMock)
        .run(
            context -> {
              SpanHandler spanHandler =
                  context.getBean(
                      StackdriverTraceAutoConfiguration.SPAN_HANDLER_BEAN_NAME, SpanHandler.class);
              assertThat(spanHandler).isNotNull();
              verify(senderMock, times(1)).check();
            });
  }

  @Test
  void defaultSchedulerUsedWhenNoneProvided() {
    this.contextRunner
        .withBean(
            StackdriverTraceAutoConfiguration.SPAN_HANDLER_BEAN_NAME,
            SpanHandler.class,
            () -> SpanHandler.NOOP)
        .run(
            context -> {
              final ExecutorProvider executorProvider =
                  context.getBean("traceExecutorProvider", ExecutorProvider.class);
              assertThat(executorProvider.getExecutor()).isNotNull();
            });
  }

  @Test
  void customSchedulerUsedWhenAvailable() {
    ThreadPoolTaskScheduler threadPoolTaskSchedulerMock = mock(ThreadPoolTaskScheduler.class);
    ScheduledExecutorService scheduledExecutorServiceMock = mock(ScheduledExecutorService.class);
    when(threadPoolTaskSchedulerMock.getScheduledExecutor())
        .thenReturn(scheduledExecutorServiceMock);

    this.contextRunner
        .withBean(
            StackdriverTraceAutoConfiguration.SPAN_HANDLER_BEAN_NAME,
            SpanHandler.class,
            () -> SpanHandler.NOOP)
        .withBean(
            "traceSenderThreadPool",
            ThreadPoolTaskScheduler.class,
            () -> threadPoolTaskSchedulerMock)
        .run(
            context -> {
              final ExecutorProvider executorProvider =
                  context.getBean("traceExecutorProvider", ExecutorProvider.class);
              assertThat(executorProvider.getExecutor()).isEqualTo(scheduledExecutorServiceMock);
            });
  }

  /** Spring config for tests with multiple reporters. */
  static class MultipleSpanHandlersConfig {

    private static final String GRPC_SERVER_NAME = "in-process-grpc-server-name";

    @Bean(destroyMethod = "shutdownNow")
    Server server(GcpTraceService gcpTraceService) throws IOException {
      return InProcessServerBuilder.forName(GRPC_SERVER_NAME)
          .addService(gcpTraceService)
          .directExecutor()
          .build()
          .start();
    }

    @Bean
    GcpTraceService gcpTraceService() {
      return new GcpTraceService();
    }

    @Bean(destroyMethod = "shutdownNow")
    ManagedChannel stackdriverSenderChannel() {
      return InProcessChannelBuilder.forName(GRPC_SERVER_NAME).directExecutor().build();
    }

    @Bean
    TracingCustomizer otherTracingCustomizer(SpanHandler otherSpanHandler) {
      return builder -> builder.addSpanHandler(otherSpanHandler);
    }

    @Bean
    SpanHandler otherSpanHandler(OtherSender otherSender) {
      AsyncReporter<zipkin2.Span> reporter = AsyncReporter.create(otherSender);
      return AsyncZipkinSpanHandler.create(reporter);
    }

    @Bean
    OtherSender otherSender() {
      return new OtherSender();
    }

    /** Custom sender for verification. */
    static class OtherSender extends Sender {

      private boolean spanSent = false;

      boolean isSpanSent() {
        return this.spanSent;
      }

      @Override
      public Encoding encoding() {
        return Encoding.JSON;
      }

      @Override
      public int messageMaxBytes() {
        return Integer.MAX_VALUE;
      }

      @Override
      public int messageSizeInBytes(List<byte[]> encodedSpans) {
        return encoding().listSizeInBytes(encodedSpans);
      }

      @Override
      public Call<Void> sendSpans(List<byte[]> encodedSpans) {
        this.spanSent = true;
        return Call.create(null);
      }
    }

    /** Used as implementation on the in-process gRPC server for verification. */
    static class GcpTraceService extends TraceServiceGrpc.TraceServiceImplBase {

      private final Map<String, Span> traces = new HashMap<>();

      boolean hasSpan(String spanId) {
        return this.traces.containsKey(spanId);
      }

      Span getSpan(String spanId) {
        return this.traces.get(spanId);
      }

      @Override
      public void batchWriteSpans(
          BatchWriteSpansRequest request, StreamObserver<Empty> responseObserver) {
        request.getSpansList().forEach(span -> this.traces.put(span.getSpanId(), span));
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
      }
    }

    static class OtherSpanHandler extends SpanHandler {}
  }
}
