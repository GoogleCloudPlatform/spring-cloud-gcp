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

package com.google.cloud.spring.pubsub.support;

import com.google.api.core.ApiClock;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.core.PubSubConfiguration;
import com.google.cloud.spring.pubsub.core.PubSubException;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PullRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.springframework.util.Assert;
import org.threeten.bp.Duration;

/** The default {@link SubscriberFactory} implementation. */
public class DefaultSubscriberFactory implements SubscriberFactory {

  private final String projectId;

  private ExecutorProvider executorProvider;

  private TransportChannelProvider channelProvider;

  private CredentialsProvider credentialsProvider;

  private HeaderProvider headerProvider;

  private ExecutorProvider systemExecutorProvider;

  private FlowControlSettings flowControlSettings;

  private Duration maxAckExtensionPeriod;

  private Duration minDurationPerAckExtension;

  private Duration maxDurationPerAckExtension;

  private Integer parallelPullCount;

  private String pullEndpoint;

  private ApiClock apiClock;

  private RetrySettings subscriberStubRetrySettings;

  private HealthTrackerRegistry healthTrackerRegistry;

  private PubSubConfiguration pubSubConfiguration;

  private Map<ProjectSubscriptionName, FlowControlSettings> flowControlSettingsMap =
      new HashMap<>();

  private Map<ProjectSubscriptionName, RetrySettings> retrySettingsMap = new ConcurrentHashMap<>();

  private FlowControlSettings globalFlowControlSettings;

  private RetrySettings globalRetrySettings;

  private Map<ProjectSubscriptionName, ExecutorProvider> executorProviderMap = new ConcurrentHashMap<>();

  private ExecutorProvider globalExecutorProvider;

  private Code[] retryableCodes;

  /**
   * Default {@link DefaultSubscriberFactory} constructor.
   *
   * @param projectIdProvider provides the default GCP project ID for selecting the subscriptions
   * @param pubSubConfiguration contains the subscriber properties to configure
   */
  public DefaultSubscriberFactory(
      GcpProjectIdProvider projectIdProvider, PubSubConfiguration pubSubConfiguration) {
    Assert.notNull(projectIdProvider, "The project ID provider can't be null.");

    this.projectId = projectIdProvider.getProjectId();
    Assert.hasText(this.projectId, "The project ID can't be null or empty.");

    Assert.notNull(pubSubConfiguration, "The pub/sub configuration can't be null.");
    this.pubSubConfiguration = pubSubConfiguration;
  }

  @Override
  public String getProjectId() {
    return this.projectId;
  }

  /**
   * Set the provider for the subscribers' executor. Useful to specify the number of threads to be
   * used by each executor.
   *
   * @param executorProvider the executor provider to set
   */
  public void setExecutorProvider(ExecutorProvider executorProvider) {
    this.executorProvider = executorProvider;
  }

  /**
   * Set the provider for the subscribers' transport channel.
   *
   * @param channelProvider the channel provider to set
   */
  public void setChannelProvider(TransportChannelProvider channelProvider) {
    this.channelProvider = channelProvider;
  }

  /**
   * Set the provider for the GCP credentials to be used by the subscribers' API calls.
   *
   * @param credentialsProvider the credentials provider to set
   */
  public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
    this.credentialsProvider = credentialsProvider;
  }

  /**
   * Set the provider for the HTTP headers to be added to the subscribers' REST API calls.
   *
   * @param headerProvider the header provider to set
   */
  public void setHeaderProvider(HeaderProvider headerProvider) {
    this.headerProvider = headerProvider;
  }

  /**
   * Set the provider for the system executor, to poll and manage lease extensions.
   *
   * @param systemExecutorProvider the system executor provider to set
   */
  public void setSystemExecutorProvider(ExecutorProvider systemExecutorProvider) {
    this.systemExecutorProvider = systemExecutorProvider;
  }

  /**
   * Set the flow control for the subscribers, including the behaviour for when the flow limits are
   * hit.
   *
   * @param flowControlSettings the flow control settings to set
   */
  public void setFlowControlSettings(FlowControlSettings flowControlSettings) {
    this.flowControlSettings = flowControlSettings;
  }

  /**
   * Set the maximum period the ack timeout is extended by.
   *
   * @param maxAckExtensionPeriod the max ack extension period to set
   */
  public void setMaxAckExtensionPeriod(Duration maxAckExtensionPeriod) {
    this.maxAckExtensionPeriod = maxAckExtensionPeriod;
  }

  /**
   * Set the number of pull workers.
   *
   * @param parallelPullCount the parallel pull count to set
   */
  public void setParallelPullCount(Integer parallelPullCount) {
    this.parallelPullCount = parallelPullCount;
  }

  /**
   * Set the endpoint for pulling messages.
   *
   * @param pullEndpoint the pull endpoint to set
   */
  public void setPullEndpoint(String pullEndpoint) {
    this.pullEndpoint = pullEndpoint;
  }

  /**
   * Set the clock to use for the retry logic in synchronous pulling.
   *
   * @param apiClock the api clock to set
   */
  public void setApiClock(ApiClock apiClock) {
    this.apiClock = apiClock;
  }

  /**
   * Set the retry settings for the generated subscriber stubs.
   *
   * @param subscriberStubRetrySettings parameters for retrying pull requests when they fail,
   *     including jitter logic, timeout, and exponential backoff
   */
  public void setSubscriberStubRetrySettings(RetrySettings subscriberStubRetrySettings) {
    this.subscriberStubRetrySettings = subscriberStubRetrySettings;
  }

  /**
   * Set the retryable codes for subscriber pull settings.
   *
   * @param retryableCodes pull RPC response codes that should be retried.
   */
  public void setRetryableCodes(Code[] retryableCodes) {
    this.retryableCodes = retryableCodes;
  }

  /**
   * Set the health tracker chain for the generated subscriptions.
   *
   * @param healthTrackerRegistry parameter for registering health trackers when creating
   *     subscriptions
   */
  public void setHealthTrackerRegistry(HealthTrackerRegistry healthTrackerRegistry) {
    this.healthTrackerRegistry = healthTrackerRegistry;
  }

  @Override
  public Subscriber createSubscriber(String subscriptionName, MessageReceiver receiver) {
    ProjectSubscriptionName projectSubscriptionName =
        PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, this.projectId);

    boolean shouldAddToHealthCheck = shouldAddToHealthCheck(subscriptionName);
    if (shouldAddToHealthCheck) {
      receiver = healthTrackerRegistry.wrap(projectSubscriptionName, receiver);
    }

    Subscriber.Builder subscriberBuilder = Subscriber.newBuilder(projectSubscriptionName, receiver);

    if (this.channelProvider != null) {
      subscriberBuilder.setChannelProvider(this.channelProvider);
    }

    ExecutorProvider executor = getExecutorProvider(subscriptionName);
    if (executor != null) {
      subscriberBuilder.setExecutorProvider(executor);
    }

    if (this.credentialsProvider != null) {
      subscriberBuilder.setCredentialsProvider(this.credentialsProvider);
    }

    if (this.headerProvider != null) {
      subscriberBuilder.setHeaderProvider(this.headerProvider);
    }

    if (this.systemExecutorProvider != null) {
      subscriberBuilder.setSystemExecutorProvider(this.systemExecutorProvider);
    }

    String endpoint = getPullEndpoint(subscriptionName);
    if (endpoint != null) {
      subscriberBuilder.setEndpoint(endpoint);
    }

    FlowControlSettings flowControl = getFlowControlSettings(subscriptionName);
    if (flowControl != null) {
      subscriberBuilder.setFlowControlSettings(flowControl);
    }

    Duration ackExtensionPeriod = getMaxAckExtensionPeriod(subscriptionName);
    if (ackExtensionPeriod != null) {
      subscriberBuilder.setMaxAckExtensionPeriod(ackExtensionPeriod);
    }

    Duration durationPerAckExtension = getMinDurationPerAckExtension(subscriptionName);
    if (durationPerAckExtension != null) {
      subscriberBuilder.setMinDurationPerAckExtension(durationPerAckExtension);
    }

    durationPerAckExtension = getMaxDurationPerAckExtension(subscriptionName);
    if (durationPerAckExtension != null) {
      subscriberBuilder.setMaxDurationPerAckExtension(durationPerAckExtension);
    }

    // Set the number of pull workers.
    Integer pullCount = getPullCount(subscriptionName);
    if (pullCount != null) {
      subscriberBuilder.setParallelPullCount(pullCount);
    }

    Subscriber subscriber = subscriberBuilder.build();

    if (shouldAddToHealthCheck) {
      healthTrackerRegistry.addListener(subscriber);
    }

    return subscriber;
  }

  @Override
  public PullRequest createPullRequest(
      String subscriptionName, Integer maxMessages, Boolean returnImmediately) {
    Assert.hasLength(subscriptionName, "The subscription name must be provided.");

    if (maxMessages == null) {
      maxMessages = Integer.MAX_VALUE;
    }
    Assert.isTrue(maxMessages > 0, "The maxMessages must be greater than 0.");

    PullRequest.Builder pullRequestBuilder =
        PullRequest.newBuilder()
            .setSubscription(
                PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, this.projectId)
                    .toString())
            .setMaxMessages(maxMessages);

    if (returnImmediately != null) {
      pullRequestBuilder.setReturnImmediately(returnImmediately);
    }

    return pullRequestBuilder.build();
  }

  @Override
  public SubscriberStub createSubscriberStub(String subscriptionName) {
    try {
      return GrpcSubscriberStub.create(buildSubscriberStubSettings(subscriptionName));
    } catch (IOException ex) {
      throw new PubSubException("Error creating the SubscriberStub", ex);
    }
  }

  SubscriberStubSettings buildGlobalSubscriberStubSettings() throws IOException {
    SubscriberStubSettings.Builder subscriberStubSettings =
        buildStubSettingsWithoutConfigurations();

    String endpoint =
        this.pullEndpoint != null
            ? this.pullEndpoint
            : this.pubSubConfiguration.getSubscriber().getPullEndpoint();
    if (endpoint != null) {
      subscriberStubSettings.setEndpoint(endpoint);
    }

    ExecutorProvider executor =
        this.executorProvider != null ? this.executorProvider : this.globalExecutorProvider;
    if (executor != null) {
      subscriberStubSettings.setBackgroundExecutorProvider(executor);
    }

    RetrySettings retrySettings =
        this.subscriberStubRetrySettings != null
            ? this.subscriberStubRetrySettings
            : this.globalRetrySettings;
    if (retrySettings != null) {
      subscriberStubSettings.pullSettings().setRetrySettings(retrySettings);
    }

    Code[] codes =
        this.retryableCodes != null
            ? this.retryableCodes
            : this.pubSubConfiguration.getSubscriber().getRetryableCodes();
    if (codes != null) {
      subscriberStubSettings.pullSettings().setRetryableCodes(codes);
    }

    return subscriberStubSettings.build();
  }

  SubscriberStubSettings buildSubscriberStubSettings(String subscriptionName) throws IOException {
    SubscriberStubSettings.Builder subscriberStubSettings =
        buildStubSettingsWithoutConfigurations();

    String endpoint = getPullEndpoint(subscriptionName);
    if (endpoint != null) {
      subscriberStubSettings.setEndpoint(endpoint);
    }

    ExecutorProvider executor = getExecutorProvider(subscriptionName);
    if (executor != null) {
      subscriberStubSettings.setBackgroundExecutorProvider(executor);
    }

    RetrySettings retrySettings = getRetrySettings(subscriptionName);
    if (retrySettings != null) {
      subscriberStubSettings.pullSettings().setRetrySettings(retrySettings);
    }

    Code[] codes = getRetryableCodes(subscriptionName);
    if (codes != null) {
      subscriberStubSettings.pullSettings().setRetryableCodes(codes);
    }

    return subscriberStubSettings.build();
  }

  SubscriberStubSettings.Builder buildStubSettingsWithoutConfigurations() {

    SubscriberStubSettings.Builder subscriberStubSettings = SubscriberStubSettings.newBuilder();

    if (this.credentialsProvider != null) {
      subscriberStubSettings.setCredentialsProvider(this.credentialsProvider);
    }

    if (this.headerProvider != null) {
      subscriberStubSettings.setHeaderProvider(this.headerProvider);
    }

    if (this.channelProvider != null) {
      subscriberStubSettings.setTransportChannelProvider(this.channelProvider);
    }

    if (this.apiClock != null) {
      subscriberStubSettings.setClock(this.apiClock);
    }
    return subscriberStubSettings;
  }

  /**
   * Creates {@link ExecutorProvider}. If a custom executor provider is set then the subscriber
   * properties configured through the application.properties file will be ignored.
   *
   * @param subscriptionName subscription name
   * @return executor provider
   */
  public ExecutorProvider getExecutorProvider(String subscriptionName) {
    if (this.executorProvider != null) {
      return this.executorProvider;
    }
    ProjectSubscriptionName projectSubscriptionName =
        PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, projectId);
    if (this.executorProviderMap.containsKey(projectSubscriptionName)) {
      return this.executorProviderMap.get(projectSubscriptionName);
    }
    return this.globalExecutorProvider;
  }

  /**
   * Creates {@link RetrySettings}, given subscriber retry properties. Returns null if none of the
   * retry settings are set. Note that if retry settings are set using a Spring-managed bean then
   * subscription-specific settings in application.properties are ignored.
   *
   * @param subscriptionName subscription name
   * @return retry settings for subscriber
   */
  public RetrySettings getRetrySettings(String subscriptionName) {
    if (this.subscriberStubRetrySettings != null) {
      return this.subscriberStubRetrySettings;
    }
    ProjectSubscriptionName projectSubscriptionName =
        PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, projectId);
    if (retrySettingsMap.containsKey(projectSubscriptionName)) {
      return this.retrySettingsMap.get(projectSubscriptionName);
    }
    return this.globalRetrySettings;
  }

  /**
   * Fetches subscriber {@link FlowControlSettings}. User-provided bean takes precedence over
   * properties from application.properties. Returns subscription-specific flow control settings if
   * present, otherwise, returns global subscriber settings.
   *
   * @param subscriptionName subscription name
   * @return flow control settings for subscriber
   */
  public FlowControlSettings getFlowControlSettings(String subscriptionName) {
    ProjectSubscriptionName projectSubscriptionName =
        PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, projectId);
    if (this.flowControlSettings != null) {
      return this.flowControlSettings;
    }
    if (flowControlSettingsMap.containsKey(projectSubscriptionName)) {
      return this.flowControlSettingsMap.get(projectSubscriptionName);
    }
    return this.globalFlowControlSettings;
  }

  Duration getMaxAckExtensionPeriod(String subscriptionName) {
    if (this.maxAckExtensionPeriod != null) {
      return this.maxAckExtensionPeriod;
    }
    return Duration.ofSeconds(
        this.pubSubConfiguration.computeMaxAckExtensionPeriod(subscriptionName, projectId));
  }

  @Nullable
  Duration getMinDurationPerAckExtension(String subscriptionName) {
    if (this.minDurationPerAckExtension != null) {
      return this.minDurationPerAckExtension;
    }

    Long extension =
        this.pubSubConfiguration.computeMinDurationPerAckExtension(subscriptionName, projectId);

    return extension == null ? null : Duration.ofSeconds(extension);
  }

  /**
   * Sets the min duration per ack extension override for all subscriptions.
   *
   * @param minDurationPerAckExtension the min duration per ack extension
   */
  public void setMinDurationPerAckExtension(@Nullable Duration minDurationPerAckExtension) {
    this.minDurationPerAckExtension = minDurationPerAckExtension;
  }

  @Nullable
  Duration getMaxDurationPerAckExtension(String subscriptionName) {
    if (this.maxDurationPerAckExtension != null) {
      return this.maxDurationPerAckExtension;
    }
    Long extension =
        this.pubSubConfiguration.computeMaxDurationPerAckExtension(subscriptionName, projectId);

    return extension == null ? null : Duration.ofSeconds(extension);
  }

  /**
   * Sets the max duration per ack extension override for all subscriptions.
   *
   * @param maxDurationPerAckExtension the max duration per ack extension
   */
  public void setMaxDurationPerAckExtension(@Nullable Duration maxDurationPerAckExtension) {
    this.maxDurationPerAckExtension = maxDurationPerAckExtension;
  }

  Integer getPullCount(String subscriptionName) {
    if (this.parallelPullCount != null) {
      return this.parallelPullCount;
    }
    return this.pubSubConfiguration.computeParallelPullCount(subscriptionName, projectId);
  }

  String getPullEndpoint(String subscriptionName) {
    if (this.pullEndpoint != null) {
      return this.pullEndpoint;
    }
    return this.pubSubConfiguration.computePullEndpoint(subscriptionName, projectId);
  }

  public Code[] getRetryableCodes(String subscriptionName) {
    if (this.retryableCodes != null) {
      return this.retryableCodes;
    }
    return this.pubSubConfiguration.computeRetryableCodes(subscriptionName, projectId);
  }

  public void setExecutorProviderMap(Map<ProjectSubscriptionName, ExecutorProvider> executorProviderMap) {
    this.executorProviderMap = executorProviderMap;
  }

  public void setGlobalExecutorProvider(ExecutorProvider executorProvider) {
    this.globalExecutorProvider = executorProvider;
  }

  public ExecutorProvider getGlobalExecutorProvider() {
    return this.globalExecutorProvider;
  }

  public void setFlowControlSettingsMap(
      Map<ProjectSubscriptionName, FlowControlSettings> flowControlSettingsMap) {
    this.flowControlSettingsMap = flowControlSettingsMap;
  }

  public void setGlobalFlowControlSettings(FlowControlSettings flowControlSettings) {
    this.globalFlowControlSettings = flowControlSettings;
  }

  public void setRetrySettingsMap(Map<ProjectSubscriptionName, RetrySettings> retrySettingsMap) {
    this.retrySettingsMap = retrySettingsMap;
  }

  public void setGlobalRetrySettings(RetrySettings retrySettings) {
    this.globalRetrySettings = retrySettings;
  }

  private boolean shouldAddToHealthCheck(String subscriptionName) {
    if (healthTrackerRegistry == null) {
      return false;
    }

    ProjectSubscriptionName projectSubscriptionName =
        PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, this.projectId);
    return !healthTrackerRegistry.isTracked(projectSubscriptionName);
  }
}
