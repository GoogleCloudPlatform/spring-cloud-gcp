/*
 * Copyright 2017-2020 the original author or authors.
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

import brave.Tags;
import brave.TracingCustomizer;
import brave.baggage.BaggagePropagation;
import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.B3Propagation;
import brave.propagation.Propagation;
import brave.propagation.stackdriver.StackdriverTracePropagation;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.rpc.EndpointContext;
import com.google.auth.Credentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.MoreCallCredentials;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.micrometer.tracing.brave.autoconfigure.BraveAutoConfiguration;
import org.springframework.boot.micrometer.tracing.brave.autoconfigure.zipkin.ZipkinWithBraveTracingAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import zipkin2.reporter.BytesEncoder;
import zipkin2.reporter.BytesMessageSender;
import zipkin2.reporter.ReporterMetrics;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.stackdriver.StackdriverSender;
import zipkin2.reporter.stackdriver.StackdriverSender.Builder;
import zipkin2.reporter.stackdriver.brave.StackdriverV2Encoder;

/** Config for Stackdriver Trace. */
@AutoConfiguration
@EnableConfigurationProperties({GcpTraceProperties.class})
@ConditionalOnProperty(
    value = {"spring.cloud.gcp.trace.enabled"},
    matchIfMissing = true)
@ConditionalOnClass(StackdriverSender.class)
@AutoConfigureBefore({BraveAutoConfiguration.class, ZipkinWithBraveTracingAutoConfiguration.class})
public class StackdriverTraceAutoConfiguration {

  private static final Log LOGGER = LogFactory.getLog(StackdriverTraceAutoConfiguration.class);

  /**
   * Stackdriver encoder bean name. Name of the bean matters for supporting multiple tracing
   * systems.
   */
  public static final String ENCODER_BEAN_NAME = "stackdriverEncoder";

  /**
   * Stackdriver sender bean name. Name of the bean matters for supporting multiple tracing systems.
   */
  public static final String SENDER_BEAN_NAME = "stackdriverSender";

  /**
   * Stackdriver span handler bean name. Name of the bean matters for supporting multiple tracing
   * systems.
   */
  public static final String SPAN_HANDLER_BEAN_NAME = "stackdriverSpanHandler";

  /**
   * Stackdriver customizer bean name. Name of the bean matters for supporting multiple tracing
   * systems.
   */
  public static final String CUSTOMIZER_BEAN_NAME = "stackdriverTracingCustomizer";

  private final GcpProjectIdProvider finalProjectIdProvider;

  private final CredentialsProvider finalCredentialsProvider;

  private final String universeDomain;

  private final String endpoint;

  private final UserAgentHeaderProvider headerProvider =
      new UserAgentHeaderProvider(this.getClass());

  private ThreadPoolTaskScheduler defaultTraceSenderThreadPool;

  @Autowired
  public StackdriverTraceAutoConfiguration(
      GcpProjectIdProvider gcpProjectIdProvider,
      CredentialsProvider credentialsProvider,
      GcpTraceProperties gcpTraceProperties)
      throws IOException {
    this(gcpProjectIdProvider, credentialsProvider, gcpTraceProperties, System::getenv);
  }

  StackdriverTraceAutoConfiguration(
      GcpProjectIdProvider gcpProjectIdProvider,
      CredentialsProvider credentialsProvider,
      GcpTraceProperties gcpTraceProperties,
      Function<String, String> envProvider)
      throws IOException {
    this.finalProjectIdProvider =
        (gcpTraceProperties.getProjectId() != null)
            ? gcpTraceProperties::getProjectId
            : gcpProjectIdProvider;
    this.finalCredentialsProvider =
        gcpTraceProperties.getCredentials().hasKey()
            ? new DefaultCredentialsProvider(gcpTraceProperties)
            : credentialsProvider;
    this.universeDomain = gcpTraceProperties.getResolvedUniverseDomain(envProvider);
    this.endpoint = gcpTraceProperties.getResolvedEndpoint(envProvider);
  }

  /**
   * Validates that the credential's universe domain (when present) matches the configured universe
   * domain to prevent sending credentials to the wrong universe.
   */
  static void validateUniverseDomain(Credentials credentials, String expectedUniverseDomain)
      throws IOException {
    if (credentials == null || credentials instanceof NoCredentials) {
      return;
    }
    String credentialsUniverseDomain = credentials.getUniverseDomain();
    if (credentialsUniverseDomain != null
        && !credentialsUniverseDomain.equals(expectedUniverseDomain)) {
      throw new IllegalStateException(
          String.format(
              EndpointContext.INVALID_UNIVERSE_DOMAIN_ERROR_TEMPLATE,
              expectedUniverseDomain,
              credentialsUniverseDomain));
    }
  }

  @Bean(CUSTOMIZER_BEAN_NAME)
  @ConditionalOnMissingBean(name = CUSTOMIZER_BEAN_NAME)
  public TracingCustomizer stackdriverTracingCustomizer(
      @Qualifier(SPAN_HANDLER_BEAN_NAME) SpanHandler spanHandler) {
    return builder -> builder.supportsJoin(false).traceId128Bit(true).addSpanHandler(spanHandler);
  }

  @Bean(SPAN_HANDLER_BEAN_NAME)
  @ConditionalOnMissingBean(name = SPAN_HANDLER_BEAN_NAME)
  public SpanHandler stackdriverSpanHandler(
      @Qualifier(SENDER_BEAN_NAME) BytesMessageSender sender,
      @Qualifier(ENCODER_BEAN_NAME) BytesEncoder<MutableSpan> encoder,
      ReporterMetrics reporterMetrics,
      GcpTraceProperties trace) {
    return AsyncZipkinSpanHandler.newBuilder(sender)
        .metrics(reporterMetrics)
        .queuedMaxSpans(1000)
        .messageTimeout(trace.getMessageTimeout(), TimeUnit.SECONDS)
        .build(encoder);
  }

  @Bean
  @ConditionalOnMissingBean
  ReporterMetrics reporterMetrics() {
    return ReporterMetrics.NOOP_METRICS;
  }

  @Bean
  @ConditionalOnMissingBean(name = "traceExecutorProvider")
  public ExecutorProvider traceExecutorProvider(
      GcpTraceProperties traceProperties,
      @Qualifier("traceSenderThreadPool") Optional<ThreadPoolTaskScheduler> userProvidedScheduler) {
    ThreadPoolTaskScheduler scheduler;
    if (userProvidedScheduler.isPresent()) {
      scheduler = userProvidedScheduler.get();
    } else {
      this.defaultTraceSenderThreadPool = new ThreadPoolTaskScheduler();
      scheduler = this.defaultTraceSenderThreadPool;
      scheduler.setPoolSize(traceProperties.getNumExecutorThreads());
      scheduler.setThreadNamePrefix("gcp-trace-sender");
      scheduler.setDaemon(true);
      scheduler.initialize();
    }
    return FixedExecutorProvider.create(scheduler.getScheduledExecutor());
  }

  @Bean(destroyMethod = "shutdownNow")
  @ConditionalOnMissingBean(name = "stackdriverSenderChannel")
  public ManagedChannel stackdriverSenderChannel() {
    return ManagedChannelBuilder.forTarget(this.endpoint)
        .userAgent(this.headerProvider.getUserAgent())
        .build();
  }

  @Bean(SENDER_BEAN_NAME)
  @ConditionalOnMissingBean(name = SENDER_BEAN_NAME)
  public BytesMessageSender stackdriverSender(
      GcpTraceProperties traceProperties,
      @Qualifier("traceExecutorProvider") ExecutorProvider executorProvider,
      @Qualifier("stackdriverSenderChannel") ManagedChannel channel)
      throws IOException {
    Credentials credentials = this.finalCredentialsProvider.getCredentials();
    // This path uses a raw ManagedChannel and bypasses GAX EndpointContext validation, so verify
    // the credential's universe domain explicitly before attaching call credentials.
    validateUniverseDomain(credentials, this.universeDomain);
    CallOptions callOptions =
        CallOptions.DEFAULT
            .withCallCredentials(MoreCallCredentials.from(credentials))
            .withExecutor(executorProvider.getExecutor());

    if (traceProperties.getAuthority() != null) {
      callOptions = callOptions.withAuthority(traceProperties.getAuthority());
    }

    if (traceProperties.getCompression() != null) {
      callOptions = callOptions.withCompression(traceProperties.getCompression());
    }

    if (traceProperties.getDeadlineMs() != null) {
      callOptions =
          callOptions.withDeadlineAfter(traceProperties.getDeadlineMs(), TimeUnit.MILLISECONDS);
    }

    if (traceProperties.getMaxInboundSize() != null) {
      callOptions = callOptions.withMaxInboundMessageSize(traceProperties.getMaxInboundSize());
    }

    if (traceProperties.getMaxOutboundSize() != null) {
      callOptions = callOptions.withMaxOutboundMessageSize(traceProperties.getMaxOutboundSize());
    }

    if (traceProperties.isWaitForReady() != null) {
      if (Boolean.TRUE.equals(traceProperties.isWaitForReady())) {
        callOptions = callOptions.withWaitForReady();
      } else {
        callOptions = callOptions.withoutWaitForReady();
      }
    }

    final Builder builder =
        StackdriverSender.newBuilder(channel)
            .projectId(this.finalProjectIdProvider.getProjectId())
            .callOptions(callOptions);

    if (traceProperties.getServerResponseTimeoutMs() != null) {
      builder.serverResponseTimeoutMs(traceProperties.getServerResponseTimeoutMs());
    }

    return builder.build();
  }

  @Bean
  @ConditionalOnMissingBean
  public BaggagePropagation.FactoryBuilder baggagePropagationFactoryBuilder() {
    Propagation.Factory primary =
        B3Propagation.newFactoryBuilder().injectFormat(B3Propagation.Format.MULTI).build();
    return BaggagePropagation.newFactoryBuilder(StackdriverTracePropagation.newFactory(primary));
  }

  @Bean(ENCODER_BEAN_NAME)
  @ConditionalOnMissingBean(name = ENCODER_BEAN_NAME)
  public BytesEncoder<MutableSpan> spanBytesEncoder() {
    return new StackdriverV2Encoder(Tags.ERROR);
  }

  @PreDestroy
  public void closeScheduler() {
    if (this.defaultTraceSenderThreadPool != null) {
      this.defaultTraceSenderThreadPool.shutdown();
    }
  }
}
