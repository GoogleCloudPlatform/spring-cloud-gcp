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

package com.google.cloud.spring.autoconfigure.pubsub;

import com.google.api.core.ApiClock;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.RetrySettings.Builder;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.cloud.pubsub.v1.stub.PublisherStubSettings;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.cloud.spring.pubsub.core.PubSubException;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import com.google.cloud.spring.pubsub.core.publisher.PubSubPublisherTemplate;
import com.google.cloud.spring.pubsub.core.publisher.PublisherCustomizer;
import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberTemplate;
import com.google.cloud.spring.pubsub.support.CachingPublisherFactory;
import com.google.cloud.spring.pubsub.support.DefaultPublisherFactory;
import com.google.cloud.spring.pubsub.support.DefaultSubscriberFactory;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import com.google.pubsub.v1.ProjectSubscriptionName;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.threeten.bp.Duration;

/** Auto-config for Pub/Sub. */
@AutoConfiguration
@AutoConfigureAfter(GcpContextAutoConfiguration.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.pubsub.enabled", matchIfMissing = true)
@ConditionalOnClass(PubSubTemplate.class)
@EnableConfigurationProperties(GcpPubSubProperties.class)
public class GcpPubSubAutoConfiguration {
  private static final Logger logger = LoggerFactory.getLogger(GcpPubSubAutoConfiguration.class);

  private final GcpPubSubProperties gcpPubSubProperties;

  private final GcpProjectIdProvider finalProjectIdProvider;

  private final CredentialsProvider finalCredentialsProvider;

  private final HeaderProvider headerProvider = new UserAgentHeaderProvider(this.getClass());

  private final Map<ProjectSubscriptionName, ThreadPoolTaskScheduler> threadPoolTaskSchedulerMap =
      new HashMap<>();

  private final Map<ProjectSubscriptionName, FlowControlSettings> subscriberFlowControlSettingsMap =
      new HashMap<>();

  private final Map<ProjectSubscriptionName, RetrySettings> subscriberRetrySettingsMap =
      new HashMap<>();

  private final Map<ProjectSubscriptionName, ExecutorProvider> executorProviderMap =
      new HashMap<>();

  private final ApplicationContext applicationContext;

  private ThreadPoolTaskScheduler globalScheduler;

  private FlowControlSettings globalFlowControlSettings;

  private RetrySettings globalRetrySettings;

  private ExecutorProvider globalExecutorProvider;

  private ObjectProvider<SelectiveSchedulerThreadNameProvider> selectiveSchedulerThreadNameProvider;

  public GcpPubSubAutoConfiguration(
      GcpPubSubProperties gcpPubSubProperties,
      GcpProjectIdProvider gcpProjectIdProvider,
      CredentialsProvider credentialsProvider,
      ObjectProvider<SelectiveSchedulerThreadNameProvider> selectiveSchedulerThreadNameProvider,
      ApplicationContext applicationContext)
      throws IOException {
    this.gcpPubSubProperties = gcpPubSubProperties;
    this.applicationContext = applicationContext;
    this.selectiveSchedulerThreadNameProvider = selectiveSchedulerThreadNameProvider;
    this.finalProjectIdProvider =
        (gcpPubSubProperties.getProjectId() != null)
            ? gcpPubSubProperties::getProjectId
            : gcpProjectIdProvider;

    if (gcpPubSubProperties.getEmulatorHost() == null
        || "false".equals(gcpPubSubProperties.getEmulatorHost())) {
      this.finalCredentialsProvider =
          gcpPubSubProperties.getCredentials().hasKey()
              ? new DefaultCredentialsProvider(gcpPubSubProperties)
              : credentialsProvider;
    } else {
      // Since we cannot create a general NoCredentialsProvider if the emulator host is enabled
      // (because it would also be used for the other components), we have to create one here
      // for this particular case.
      this.finalCredentialsProvider = NoCredentialsProvider.create();
    }

    this.gcpPubSubProperties.initialize(this.finalProjectIdProvider.getProjectId());
  }

  @Bean
  @ConditionalOnMissingBean(name = "pubsubPublisherThreadPool")
  public ThreadPoolTaskScheduler pubsubPublisherThreadPool() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(this.gcpPubSubProperties.getPublisher().getExecutorThreads());
    scheduler.setThreadNamePrefix("gcp-pubsub-publisher");
    scheduler.setDaemon(true);
    return scheduler;
  }

  @Bean
  @ConditionalOnMissingBean(name = "publisherExecutorProvider")
  public ExecutorProvider publisherExecutorProvider(
      @Qualifier("pubsubPublisherThreadPool") ThreadPoolTaskScheduler scheduler) {
    return FixedExecutorProvider.create(scheduler.getScheduledExecutor());
  }

  @Bean
  @ConditionalOnMissingBean
  public PubSubPublisherTemplate pubSubPublisherTemplate(
      PublisherFactory publisherFactory,
      ObjectProvider<PubSubMessageConverter> pubSubMessageConverter) {
    PubSubPublisherTemplate pubSubPublisherTemplate = new PubSubPublisherTemplate(publisherFactory);
    pubSubMessageConverter.ifUnique(pubSubPublisherTemplate::setMessageConverter);
    return pubSubPublisherTemplate;
  }

  @Bean
  @ConditionalOnMissingBean(name = "pubSubAcknowledgementExecutor")
  public Executor pubSubAcknowledgementExecutor() {
    ThreadPoolTaskExecutor ackExecutor = new ThreadPoolTaskExecutor();
    ackExecutor.setMaxPoolSize(
        this.gcpPubSubProperties.getSubscriber().getMaxAcknowledgementThreads());
    ackExecutor.setThreadNamePrefix("gcp-pubsub-ack-executor");
    ackExecutor.setDaemon(true);
    return ackExecutor;
  }

  @Bean
  @ConditionalOnMissingBean
  public PubSubSubscriberTemplate pubSubSubscriberTemplate(
      SubscriberFactory subscriberFactory,
      ObjectProvider<PubSubMessageConverter> pubSubMessageConverter,
      @Qualifier("pubSubAsynchronousPullExecutor") ObjectProvider<Executor> asyncPullExecutor,
      @Qualifier("pubSubAcknowledgementExecutor") Executor ackExecutor) {
    PubSubSubscriberTemplate pubSubSubscriberTemplate =
        new PubSubSubscriberTemplate(subscriberFactory);
    pubSubMessageConverter.ifUnique(pubSubSubscriberTemplate::setMessageConverter);
    pubSubSubscriberTemplate.setAckExecutor(ackExecutor);
    asyncPullExecutor.ifAvailable(pubSubSubscriberTemplate::setAsyncPullExecutor);
    return pubSubSubscriberTemplate;
  }

  @Bean
  @ConditionalOnMissingBean
  public PubSubTemplate pubSubTemplate(
      PubSubPublisherTemplate pubSubPublisherTemplate,
      PubSubSubscriberTemplate pubSubSubscriberTemplate) {
    return new PubSubTemplate(pubSubPublisherTemplate, pubSubSubscriberTemplate);
  }

  private FlowControlSettings buildFlowControlSettings(
      PubSubConfiguration.FlowControl flowControl) {
    FlowControlSettings.Builder builder = FlowControlSettings.newBuilder();

    boolean shouldBuild =
        ifSet(flowControl.getLimitExceededBehavior(), builder::setLimitExceededBehavior);
    shouldBuild |=
        ifSet(flowControl.getMaxOutstandingElementCount(), builder::setMaxOutstandingElementCount);
    shouldBuild |=
        ifSet(flowControl.getMaxOutstandingRequestBytes(), builder::setMaxOutstandingRequestBytes);

    return shouldBuild ? builder.build() : null;
  }

  @Bean
  @ConditionalOnMissingBean
  public SubscriberFactory defaultSubscriberFactory(
      @Qualifier("subscriberExecutorProvider") Optional<ExecutorProvider> executorProvider,
      @Qualifier("subscriberSystemExecutorProvider")
          ObjectProvider<ExecutorProvider> systemExecutorProvider,
      @Qualifier("subscriberFlowControlSettings")
          ObjectProvider<FlowControlSettings> flowControlSettings,
      @Qualifier("subscriberApiClock") ObjectProvider<ApiClock> apiClock,
      @Qualifier("subscriberRetrySettings") ObjectProvider<RetrySettings> retrySettings,
      @Qualifier("healthTrackerRegistry")
          ObjectProvider<HealthTrackerRegistry> healthTrackerRegistry,
      @Qualifier("subscriberTransportChannelProvider")
          TransportChannelProvider subscriberTransportChannelProvider) {
    DefaultSubscriberFactory factory =
        new DefaultSubscriberFactory(this.finalProjectIdProvider, this.gcpPubSubProperties);

    if (executorProvider.isPresent()) {
      logger.warn(
          "The subscriberExecutorProvider bean is being deprecated. Please use"
              + " application.properties to configure properties");
      factory.setExecutorProvider(executorProvider.get());
    }
    factory.setExecutorProviderMap(this.executorProviderMap);
    factory.setGlobalExecutorProvider(this.globalExecutorProvider);

    factory.setCredentialsProvider(this.finalCredentialsProvider);
    factory.setHeaderProvider(this.headerProvider);
    factory.setChannelProvider(subscriberTransportChannelProvider);
    systemExecutorProvider.ifAvailable(factory::setSystemExecutorProvider);
    if (flowControlSettings.getIfAvailable() != null) {
      logger.warn(
          "The subscriberFlowControlSettings bean is being deprecated. Please use"
              + " application.properties to configure properties");
      factory.setFlowControlSettings(flowControlSettings.getIfAvailable());
    }
    factory.setFlowControlSettingsMap(this.subscriberFlowControlSettingsMap);
    factory.setGlobalFlowControlSettings(this.globalFlowControlSettings);
    apiClock.ifAvailable(factory::setApiClock);
    if (retrySettings.getIfAvailable() != null) {
      logger.warn(
          "The subscriberRetrySettings bean is being deprecated. Please use application.properties"
              + " to configure properties");
      factory.setSubscriberStubRetrySettings(retrySettings.getIfAvailable());
    }
    factory.setRetrySettingsMap(this.subscriberRetrySettingsMap);
    factory.setGlobalRetrySettings(this.globalRetrySettings);
    healthTrackerRegistry.ifAvailable(factory::setHealthTrackerRegistry);

    return factory;
  }

  @Bean
  @ConditionalOnMissingBean(name = "publisherBatchSettings")
  public BatchingSettings publisherBatchSettings() {
    BatchingSettings.Builder builder = BatchingSettings.newBuilder();

    PubSubConfiguration.Batching batching = this.gcpPubSubProperties.getPublisher().getBatching();

    FlowControlSettings flowControlSettings = buildFlowControlSettings(batching.getFlowControl());
    if (flowControlSettings != null) {
      builder.setFlowControlSettings(flowControlSettings);
    }

    boolean shouldBuild =
        ifSet(
            batching.getDelayThresholdSeconds(),
            x -> builder.setDelayThreshold(Duration.ofSeconds(x)));
    shouldBuild |= ifSet(batching.getElementCountThreshold(), builder::setElementCountThreshold);
    shouldBuild |= ifSet(batching.getEnabled(), builder::setIsEnabled);
    shouldBuild |= ifSet(batching.getRequestByteThreshold(), builder::setRequestByteThreshold);

    return shouldBuild ? builder.build() : null;
  }

  @Bean
  @ConditionalOnMissingBean(name = "publisherRetrySettings")
  public RetrySettings publisherRetrySettings() {
    return buildRetrySettings(this.gcpPubSubProperties.getPublisher().getRetry());
  }

  private RetrySettings buildRetrySettings(PubSubConfiguration.Retry retryProperties) {
    Builder builder = RetrySettings.newBuilder();

    boolean shouldBuild =
        ifSet(
            retryProperties.getInitialRetryDelaySeconds(),
            x -> builder.setInitialRetryDelay(Duration.ofSeconds(x)));
    shouldBuild |=
        ifSet(
            retryProperties.getInitialRpcTimeoutSeconds(),
            x -> builder.setInitialRpcTimeout(Duration.ofSeconds(x)));
    shouldBuild |= ifSet(retryProperties.getJittered(), builder::setJittered);
    shouldBuild |= ifSet(retryProperties.getMaxAttempts(), builder::setMaxAttempts);
    shouldBuild |=
        ifSet(
            retryProperties.getMaxRetryDelaySeconds(),
            x -> builder.setMaxRetryDelay(Duration.ofSeconds(x)));
    shouldBuild |=
        ifSet(
            retryProperties.getMaxRpcTimeoutSeconds(),
            x -> builder.setMaxRpcTimeout(Duration.ofSeconds(x)));
    shouldBuild |=
        ifSet(retryProperties.getRetryDelayMultiplier(), builder::setRetryDelayMultiplier);
    shouldBuild |=
        ifSet(
            retryProperties.getTotalTimeoutSeconds(),
            x -> builder.setTotalTimeout(Duration.ofSeconds(x)));
    shouldBuild |=
        ifSet(retryProperties.getRpcTimeoutMultiplier(), builder::setRpcTimeoutMultiplier);

    return shouldBuild ? builder.build() : null;
  }

  private <T> boolean ifSet(T property, Consumer<T> consumer) {
    if (property != null) {
      consumer.accept(property);
      return true;
    }
    return false;
  }

  @Bean
  @ConditionalOnMissingBean
  public PublisherFactory defaultPublisherFactory(
      @Qualifier("publisherExecutorProvider") ExecutorProvider executorProvider,
      @Qualifier("publisherBatchSettings") ObjectProvider<BatchingSettings> batchingSettings,
      @Qualifier("publisherRetrySettings") ObjectProvider<RetrySettings> retrySettings,
      @Qualifier("publisherTransportChannelProvider")
          TransportChannelProvider publisherTransportChannelProvider,
      ObjectProvider<PublisherCustomizer> customizersProvider) {
    DefaultPublisherFactory factory = new DefaultPublisherFactory(this.finalProjectIdProvider);
    factory.setExecutorProvider(executorProvider);
    factory.setCredentialsProvider(this.finalCredentialsProvider);
    factory.setHeaderProvider(this.headerProvider);
    factory.setChannelProvider(publisherTransportChannelProvider);
    retrySettings.ifAvailable(factory::setRetrySettings);
    batchingSettings.ifAvailable(factory::setBatchingSettings);
    factory.setEnableMessageOrdering(gcpPubSubProperties.getPublisher().getEnableMessageOrdering());
    factory.setEndpoint(gcpPubSubProperties.getPublisher().getEndpoint());

    List<PublisherCustomizer> customizers = customizersProvider.orderedStream()
        .collect(Collectors.toList());
    Collections.reverse(customizers); // highest priority customizer needs to be last
    factory.setCustomizers(customizers);

    return new CachingPublisherFactory(factory);
  }

  @Bean
  @ConditionalOnMissingBean
  public PubSubAdmin pubSubAdmin(
      TopicAdminClient topicAdminClient, SubscriptionAdminClient subscriptionAdminClient) {
    return new PubSubAdmin(this.finalProjectIdProvider, topicAdminClient, subscriptionAdminClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public TopicAdminClient topicAdminClient(TopicAdminSettings topicAdminSettings) {
    try {
      return TopicAdminClient.create(topicAdminSettings);
    } catch (IOException ioe) {
      throw new PubSubException("An error occurred while creating TopicAdminClient.", ioe);
    }
  }

  @Bean
  @ConditionalOnMissingBean
  public TopicAdminSettings topicAdminSettings(
      @Qualifier("publisherTransportChannelProvider")
          TransportChannelProvider publisherTransportChannelProvider) {
    try {
      return TopicAdminSettings.newBuilder()
          .setCredentialsProvider(this.finalCredentialsProvider)
          .setHeaderProvider(this.headerProvider)
          .setTransportChannelProvider(publisherTransportChannelProvider)
          .build();
    } catch (IOException ioe) {
      throw new PubSubException("An error occurred while creating TopicAdminSettings.", ioe);
    }
  }

  @Bean
  @ConditionalOnMissingBean
  public SubscriptionAdminClient subscriptionAdminClient(
      @Qualifier("subscriberTransportChannelProvider")
          TransportChannelProvider subscriberTransportChannelProvider) {
    try {
      return SubscriptionAdminClient.create(
          SubscriptionAdminSettings.newBuilder()
              .setCredentialsProvider(this.finalCredentialsProvider)
              .setHeaderProvider(this.headerProvider)
              .setTransportChannelProvider(subscriberTransportChannelProvider)
              .build());
    } catch (IOException ioe) {
      throw new PubSubException("An error occurred while creating SubscriptionAdminClient.", ioe);
    }
  }

  @Bean
  @ConditionalOnMissingBean(name = "subscriberTransportChannelProvider")
  public TransportChannelProvider subscriberTransportChannelProvider() {
    return SubscriberStubSettings.defaultGrpcTransportProviderBuilder()
        .setKeepAliveTime(
            Duration.ofMinutes(this.gcpPubSubProperties.getKeepAliveIntervalMinutes()))
        .build();
  }

  @Bean
  @ConditionalOnMissingBean(name = "publisherTransportChannelProvider")
  public TransportChannelProvider publisherTransportChannelProvider() {
    return PublisherStubSettings.defaultGrpcTransportProviderBuilder()
        .setKeepAliveTime(
            Duration.ofMinutes(this.gcpPubSubProperties.getKeepAliveIntervalMinutes()))
        .build();
  }

  @PostConstruct
  public void registerSubscriberSettings() {
    GenericApplicationContext context = (GenericApplicationContext) this.applicationContext;
    registerSubscriberThreadPoolSchedulerBeans(context);
    registerExecutorProviderBeans(context);
    registerSubscriberFlowControlSettingsBeans(context);
    registerSubscriberRetrySettingsBeans(context);
  }

  private void registerSubscriberThreadPoolSchedulerBeans(GenericApplicationContext context) {
    Integer numThreads = getGlobalExecutorThreads();
    this.globalScheduler =
        createAndRegisterSchedulerBean(
            numThreads,
            "global-gcp-pubsub-subscriber",
            "globalPubSubSubscriberThreadPoolScheduler",
            context);
    registerSelectiveSchedulerBeans(context);
  }

  private void registerSubscriberFlowControlSettingsBeans(GenericApplicationContext context) {
    if (context.containsBean("subscriberFlowControlSettings")) {
      return;
    }
    this.globalFlowControlSettings =
        buildFlowControlSettings(this.gcpPubSubProperties.getSubscriber().getFlowControl());
    if (this.globalFlowControlSettings != null) {
      context.registerBeanDefinition(
          "globalSubscriberFlowControlSettings",
          BeanDefinitionBuilder.genericBeanDefinition(
                  FlowControlSettings.class, () -> this.globalFlowControlSettings)
              .getBeanDefinition());
    }
    createAndRegisterSelectiveFlowControlSettings(context);
  }

  private void registerExecutorProviderBeans(GenericApplicationContext context) {
    if (context.containsBean("subscriberExecutorProvider")) {
      return;
    }
    if (this.globalScheduler != null) {
      this.globalExecutorProvider =
          createAndRegisterExecutorProvider(
              "globalSubscriberExecutorProvider", this.globalScheduler, context);
    }
    createAndRegisterSelectiveExecutorProvider(context);
  }

  private void registerSubscriberRetrySettingsBeans(GenericApplicationContext context) {
    if (context.containsBean("subscriberRetrySettings")) {
      return;
    }
    this.globalRetrySettings =
        buildRetrySettings(this.gcpPubSubProperties.getSubscriber().getRetry());
    if (this.globalRetrySettings != null) {
      context.registerBeanDefinition(
          "globalSubscriberRetrySettings",
          BeanDefinitionBuilder.genericBeanDefinition(
                  RetrySettings.class, () -> this.globalRetrySettings)
              .getBeanDefinition());
    }
    createAndRegisterSelectiveRetrySettings(context);
  }

  /**
   * Creates and registers {@link ThreadPoolTaskScheduler} for subscription-specific configurations.
   *
   * @param context application context
   */
  private void registerSelectiveSchedulerBeans(GenericApplicationContext context) {
    Map<ProjectSubscriptionName, PubSubConfiguration.Subscriber> subscriberMap =
        this.gcpPubSubProperties.getFullyQualifiedSubscriberProperties();
    for (Map.Entry<ProjectSubscriptionName, PubSubConfiguration.Subscriber> subscription :
        subscriberMap.entrySet()) {
      ProjectSubscriptionName fullSubscriptionName = subscription.getKey();
      PubSubConfiguration.Subscriber selectiveSubscriber = subscription.getValue();
      Integer selectiveExecutorThreads = selectiveSubscriber.getExecutorThreads();
      if (selectiveExecutorThreads != null) {
        String qualifiedName = fullSubscriptionName.toString(); // will include slashes
        String threadName = selectiveSchedulerThreadNameProvider
            .getIfAvailable(
                () -> subscriptionName -> "gcp-pubsub-subscriber-" + subscriptionName.toString())
            .getThreadName(fullSubscriptionName);
        String beanName = "threadPoolScheduler_" + qualifiedName;
        ThreadPoolTaskScheduler selectiveScheduler =
            createAndRegisterSchedulerBean(selectiveExecutorThreads, threadName, beanName, context);
        this.threadPoolTaskSchedulerMap.putIfAbsent(fullSubscriptionName, selectiveScheduler);
      }
    }
  }

  /**
   * Creates a {@link ThreadPoolTaskScheduler} and registers it as a bean.
   *
   * @param executorThreads number of executor threads
   * @param threadName thread name
   * @param beanName bean name
   * @param context application context
   * @return a {@link ThreadPoolTaskScheduler}
   */
  private ThreadPoolTaskScheduler createAndRegisterSchedulerBean(
      Integer executorThreads,
      String threadName,
      String beanName,
      GenericApplicationContext context) {
    ThreadPoolTaskScheduler scheduler = createThreadPoolTaskScheduler(executorThreads, threadName);
    context.registerBeanDefinition(
        beanName,
        BeanDefinitionBuilder.genericBeanDefinition(ThreadPoolTaskScheduler.class, () -> scheduler)
            .getBeanDefinition());
    return scheduler;
  }

  /**
   * Creates {@link ThreadPoolTaskScheduler} given the number of executor threads and a thread name.
   *
   * @param executorThreads number of executor threads
   * @param threadName thread name prefix to set for the scheduler
   * @return thread pool scheduler
   */
  private ThreadPoolTaskScheduler createThreadPoolTaskScheduler(
      Integer executorThreads, String threadName) {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(executorThreads);
    scheduler.setThreadNamePrefix(threadName);
    scheduler.setDaemon(true);
    return scheduler;
  }

  private void createAndRegisterSelectiveFlowControlSettings(GenericApplicationContext context) {
    Map<ProjectSubscriptionName, PubSubConfiguration.Subscriber> subscriberMap =
        this.gcpPubSubProperties.getFullyQualifiedSubscriberProperties();

    for (Map.Entry<ProjectSubscriptionName, PubSubConfiguration.Subscriber> subscription :
        subscriberMap.entrySet()) {
      ProjectSubscriptionName fullSubscriptionName = subscription.getKey();

      String qualifiedName = fullSubscriptionName.toString();
      PubSubConfiguration.FlowControl flowControl =
          this.gcpPubSubProperties.computeSubscriberFlowControlSettings(fullSubscriptionName);
      FlowControlSettings flowControlSettings = buildFlowControlSettings(flowControl);
      if (flowControlSettings != null
          && !flowControlSettings.equals(this.globalFlowControlSettings)) {
        this.subscriberFlowControlSettingsMap.putIfAbsent(
            fullSubscriptionName, flowControlSettings);
        String beanName = "subscriberFlowControlSettings-" + qualifiedName;
        context.registerBeanDefinition(
            beanName,
            BeanDefinitionBuilder.genericBeanDefinition(
                    FlowControlSettings.class, () -> flowControlSettings)
                .getBeanDefinition());
      }
    }
  }

  private void createAndRegisterSelectiveExecutorProvider(GenericApplicationContext context) {
    for (Map.Entry<ProjectSubscriptionName, ThreadPoolTaskScheduler> schedulerSet :
        this.threadPoolTaskSchedulerMap.entrySet()) {
      ProjectSubscriptionName fullSubscriptionName = schedulerSet.getKey();
      String qualifiedName = fullSubscriptionName.toString();
      if (!this.executorProviderMap.containsKey(fullSubscriptionName)) {
        ThreadPoolTaskScheduler scheduler = schedulerSet.getValue();
        ExecutorProvider executorProvider =
            createAndRegisterExecutorProvider(
                "subscriberExecutorProvider-" + qualifiedName,
                scheduler,
                context);
        this.executorProviderMap.putIfAbsent(fullSubscriptionName, executorProvider);
      }
    }
  }

  private ExecutorProvider createAndRegisterExecutorProvider(
      String beanName, ThreadPoolTaskScheduler scheduler, GenericApplicationContext context) {
    scheduler.initialize();
    ExecutorProvider executor = FixedExecutorProvider.create(scheduler.getScheduledExecutor());
    context.registerBeanDefinition(
        beanName,
        BeanDefinitionBuilder.genericBeanDefinition(ExecutorProvider.class, () -> executor)
            .getBeanDefinition());
    return executor;
  }

  private void createAndRegisterSelectiveRetrySettings(GenericApplicationContext context) {
    Map<ProjectSubscriptionName, PubSubConfiguration.Subscriber> subscriberMap =
        this.gcpPubSubProperties.getFullyQualifiedSubscriberProperties();

    for (Map.Entry<ProjectSubscriptionName, PubSubConfiguration.Subscriber> subscription :
        subscriberMap.entrySet()) {
      ProjectSubscriptionName qualifiedName = subscription.getKey();

      PubSubConfiguration.Retry retry =
          this.gcpPubSubProperties.computeSubscriberRetrySettings(qualifiedName);
      RetrySettings retrySettings = buildRetrySettings(retry);
      if (retrySettings != null && !retrySettings.equals(this.globalRetrySettings)) {
        this.subscriberRetrySettingsMap.putIfAbsent(qualifiedName, retrySettings);
        String beanName = "subscriberRetrySettings-" + qualifiedName.toString();
        context.registerBeanDefinition(
            beanName,
            BeanDefinitionBuilder.genericBeanDefinition(RetrySettings.class, () -> retrySettings)
                .getBeanDefinition());
      }
    }
  }

  private Integer getGlobalExecutorThreads() {
    Integer numThreads = this.gcpPubSubProperties.getSubscriber().getExecutorThreads();
    return numThreads != null ? numThreads : PubSubConfiguration.DEFAULT_EXECUTOR_THREADS;
  }
}
