/*
 * Copyright 2021-2021 the original author or authors.
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

package com.google.cloud.spring.pubsub.core;

import com.google.api.gax.batching.FlowController.LimitExceededBehavior;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.spring.pubsub.support.PubSubSubscriptionUtils;
import com.google.pubsub.v1.ProjectSubscriptionName;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/** Properties for Publisher or Subscriber specific configurations. */
public class PubSubConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(PubSubConfiguration.class);

  /** Default number of executor threads. */
  public static final int DEFAULT_EXECUTOR_THREADS = 4;

  private static final Long DEFAULT_MAX_ACK_EXTENSION_PERIOD = 0L;

  /**
   * Automatically extracted user-provided properties. Contains only short subscription keys
   * user-provided properties, therefore do not use except in initialize().
   */
  private Map<String, Subscriber> subscription = new HashMap<>();

  /** Properties keyed by fully qualified subscription name.
   * Initialized once; effectively a singleton.
   */
  private Map<ProjectSubscriptionName, Subscriber> fullyQualifiedSubscriptionProperties;

  /** Contains global and default subscriber settings. */
  private final Subscriber globalSubscriber = new Subscriber();

  /** Contains default publisher settings. */
  private final Publisher publisher = new Publisher();

  /** Contains default health settings. */
  private final Health health = new Health();

  public Subscriber getSubscriber() {
    return this.globalSubscriber;
  }

  public Publisher getPublisher() {
    return this.publisher;
  }

  public Health getHealth() {
    return health;
  }

  /**
   * This method will be called by Spring Framework when binding user properties.
   * Also potentially useful for tests.
   *
   * @param subscriberProperties map of user-defined properties.
   */
  public void setSubscription(Map<String, Subscriber> subscriberProperties) {
    Assert.isNull(this.fullyQualifiedSubscriptionProperties,
        "Pub/Sub properties have already been initialized; cannot update subscription properties");

    this.subscription = subscriberProperties;
  }

  /**
   * Returns an immutable map of subscription properties keyed by the fully-qualified
   * {@link ProjectSubscriptionName}.
   *
   * <p>Cannot be called before {@link #initialize(String)}.
   *
   * @return map of subscription properties
   */
  public Map<ProjectSubscriptionName, Subscriber> getFullyQualifiedSubscriberProperties() {
    Assert.notNull(this.fullyQualifiedSubscriptionProperties, "Please call initialize() prior to retrieving properties.");
    return this.fullyQualifiedSubscriptionProperties;
  }

  /**
   * Standardizes all subscription properties to be keyed by their fully qualified subscription
   * names. Not thread-safe.
   *
   * <p>If a `fully-qualified-name` property is present, it is used as a key for all subscription
   * properties under the same group. Otherwise, the provided configuration group key is assumed to
   * be the short subscription name in the current project.
   *
   * @param defaultProjectId Project to use with short subscription names
   */
  public void initialize(String defaultProjectId) {
    if (this.fullyQualifiedSubscriptionProperties != null) {
      logger.warn("Pub/Sub configuration can only be initialized once; ignoring request.");
      return;
    }

    Map<ProjectSubscriptionName, Subscriber> fullyQualifiedProps = new HashMap<>();
    for (Entry<String, Subscriber> entry : this.subscription.entrySet()) {
      // Subscription name is either a valid short name, or a made-up name with fully-qualified provided as a property
      Subscriber subscriberProperties = entry.getValue();
      String qualifiedName = subscriberProperties.fullyQualifiedName != null
          ? subscriberProperties.fullyQualifiedName : entry.getKey();
      ProjectSubscriptionName projectSubscriptionName =
          PubSubSubscriptionUtils.toProjectSubscriptionName(qualifiedName, defaultProjectId);
      if (fullyQualifiedProps.containsKey(projectSubscriptionName)) {
        logger.warn("Found multiple configurations for {}; ignoring properties with key {}",
            projectSubscriptionName, entry.getKey());
      } else {
        fullyQualifiedProps.put(projectSubscriptionName, subscriberProperties);
      }
    }

    this.fullyQualifiedSubscriptionProperties = Collections.unmodifiableMap(fullyQualifiedProps);
  }

  /**
   * Returns properties for the specified fully-qualified {@link ProjectSubscriptionName}.
   *
   * @param projectSubscriptionName fully-qualified {@link ProjectSubscriptionName}
   * @return user-provided subscription properties
   */
  public Subscriber getSubscriptionProperties(ProjectSubscriptionName projectSubscriptionName) {
    Assert.notNull(this.fullyQualifiedSubscriptionProperties, "Please call initialize() prior to retrieving properties.");

    if (this.fullyQualifiedSubscriptionProperties.containsKey(projectSubscriptionName)) {
      return this.fullyQualifiedSubscriptionProperties.get(projectSubscriptionName);
    }

    return globalSubscriber;
  }

  /**
   * Computes flow control settings to use. The subscription-specific property takes precedence if
   * both global and subscription-specific properties are set. If subscription-specific settings are
   * not set then global settings are picked.
   *
   * @param projectSubscriptionName Fully qualified subscription name
   * @return flow control settings defaulting to global where not provided
   */
  public FlowControl computeSubscriberFlowControlSettings(ProjectSubscriptionName projectSubscriptionName) {
    FlowControl flowControl = getSubscriptionProperties(projectSubscriptionName).getFlowControl();
    FlowControl globalFlowControl = this.globalSubscriber.getFlowControl();
    // It is possible for flowControl and globalFlowControl to be the same object.
    // In the future, can return it here if that's the case.
    if (flowControl.getMaxOutstandingRequestBytes() == null) {
      flowControl.setMaxOutstandingRequestBytes(globalFlowControl.getMaxOutstandingRequestBytes());
    }
    if (flowControl.getMaxOutstandingElementCount() == null) {
      flowControl.setMaxOutstandingElementCount(globalFlowControl.getMaxOutstandingElementCount());
    }
    if (flowControl.getLimitExceededBehavior() == null) {
      flowControl.setLimitExceededBehavior(globalFlowControl.getLimitExceededBehavior());
    }
    return flowControl;
  }

  /**
   * Computes parallel pull count. The subscription-specific property takes precedence if both
   * global and subscription-specific properties are set. If subscription-specific configuration is
   * not set then the global configuration is picked.
   *
   * @param subscriptionName subscription name
   * @param projectId project id
   * @return parallel pull count
   */
  public Integer computeParallelPullCount(String subscriptionName, String projectId) {
    Integer parallelPullCount =
        getSubscriptionProperties(PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, projectId))
            .getParallelPullCount();

    return parallelPullCount != null
        ? parallelPullCount
        : this.globalSubscriber.getParallelPullCount();
  }

  /**
   * Retrieves collection of retryable codes from configuration. The subscription-specific property
   * takes precedence if both global and subscription-specific properties are set. If
   * subscription-specific configuration is not set then the global configuration is picked.
   *
   * @param subscriptionName subscription name
   * @param projectId project id
   * @return retryable codes
   */
  public Code[] computeRetryableCodes(String subscriptionName, String projectId) {
    Code[] retryableCodes =
        getSubscriptionProperties(PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, projectId))
            .getRetryableCodes();
    return retryableCodes != null ? retryableCodes : this.globalSubscriber.getRetryableCodes();
  }

  /**
   * Computes the max extension period. The subscription-specific property takes precedence if both
   * global and subscription-specific properties are set. If none are set then the default (0) is
   * returned.
   *
   * @param subscriptionName subscription name
   * @param projectId project id
   * @return max extension period
   */
  public Long computeMaxAckExtensionPeriod(String subscriptionName, String projectId) {
    Long maxAckExtensionPeriod =
        getSubscriptionProperties(PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, projectId))
        .getMaxAckExtensionPeriod();

    if (maxAckExtensionPeriod != null) {
      return maxAckExtensionPeriod;
    }
    Long globalMaxAckExtensionPeriod = this.globalSubscriber.getMaxAckExtensionPeriod();
    return globalMaxAckExtensionPeriod != null
        ? globalMaxAckExtensionPeriod
        : DEFAULT_MAX_ACK_EXTENSION_PERIOD;
  }

  /**
   * Computes the lower bound for a single mod ack extension period. The subscription-specific
   * property takes precedence if both global and subscription-specific properties are set. If none
   * are set then the default (0) is returned.
   *
   * @param subscriptionName subscription name
   * @param projectId project id
   * @return min duration per ack extension
   */
  @Nullable
  public Long computeMinDurationPerAckExtension(String subscriptionName, String projectId) {
    Long minDurationPerAckExtension =
        getSubscriptionProperties(PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, projectId))
            .getMinDurationPerAckExtension();

    if (minDurationPerAckExtension != null) {
      return minDurationPerAckExtension;
    }

    return this.globalSubscriber.getMinDurationPerAckExtension();
  }

  /**
   * Computes the upper bound for a single mod ack extension period. The subscription-specific
   * property takes precedence if both global and subscription-specific properties are set. If none
   * are set then the default (0) is returned.
   *
   * @param subscriptionName subscription name
   * @param projectId project id
   * @return max duration per ack extension
   */
  @Nullable
  public Long computeMaxDurationPerAckExtension(String subscriptionName, String projectId) {
    Long maxDurationPerAckExtension =
        getSubscriptionProperties(PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, projectId))
            .getMaxDurationPerAckExtension();

    if (maxDurationPerAckExtension != null) {
      return maxDurationPerAckExtension;
    }

    return this.globalSubscriber.getMaxDurationPerAckExtension();
  }

  /**
   * Returns the pull endpoint. The subscription-specific property takes precedence if both global
   * and subscription-specific properties are set. If subscription-specific configuration is not set
   * then the global configuration is picked.
   *
   * @param subscriptionName subscription name
   * @param projectId project id
   * @return pull endpoint
   */
  public String computePullEndpoint(String subscriptionName, String projectId) {
    String pullEndpoint =
        getSubscriptionProperties(PubSubSubscriptionUtils.toProjectSubscriptionName(subscriptionName, projectId))
        .getPullEndpoint();
    return pullEndpoint != null ? pullEndpoint : this.globalSubscriber.getPullEndpoint();
  }

  /**
   * Computes the retry settings. The subscription-specific property takes precedence if both global
   * and subscription-specific properties are set. If subscription-specific settings are not set
   * then the global settings are picked.
   *
   * @param projectSubscriptionName The fully qualified subscription name
   * @return retry settings
   */
  public Retry computeSubscriberRetrySettings(ProjectSubscriptionName projectSubscriptionName) {
    Retry retry = getSubscriptionProperties(projectSubscriptionName).getRetry();
    Retry globalRetry = this.globalSubscriber.getRetry();
    if (retry.getTotalTimeoutSeconds() == null) {
      retry.setTotalTimeoutSeconds(globalRetry.getTotalTimeoutSeconds());
    }
    if (retry.getInitialRetryDelaySeconds() == null) {
      retry.setInitialRetryDelaySeconds(globalRetry.getInitialRetryDelaySeconds());
    }
    if (retry.getRetryDelayMultiplier() == null) {
      retry.setRetryDelayMultiplier(globalRetry.getRetryDelayMultiplier());
    }
    if (retry.getMaxRetryDelaySeconds() == null) {
      retry.setMaxRetryDelaySeconds(globalRetry.getMaxRetryDelaySeconds());
    }
    if (retry.getMaxAttempts() == null) {
      retry.setMaxAttempts(globalRetry.getMaxAttempts());
    }
    if (retry.getJittered() == null) {
      retry.setJittered(globalRetry.getJittered());
    }
    if (retry.getInitialRpcTimeoutSeconds() == null) {
      retry.setInitialRpcTimeoutSeconds(globalRetry.getInitialRpcTimeoutSeconds());
    }
    if (retry.getRpcTimeoutMultiplier() == null) {
      retry.setRpcTimeoutMultiplier(globalRetry.getRpcTimeoutMultiplier());
    }
    if (retry.getMaxRpcTimeoutSeconds() == null) {
      retry.setMaxRpcTimeoutSeconds(globalRetry.getMaxRpcTimeoutSeconds());
    }
    return retry;
  }

  /** Publisher settings. */
  public static class Publisher {

    /** Number of threads used by every publisher. */
    private int executorThreads = 4;

    /** Retry properties. */
    private final Retry retry = new Retry();

    /** Batching properties. */
    private final Batching batching = new Batching();

    /** Enable message ordering setting. */
    private Boolean enableMessageOrdering;

    /** Set publisher endpoint. Example: "us-east1-pubsub.googleapis.com:443". */
    private String endpoint;

    public Batching getBatching() {
      return this.batching;
    }

    public Retry getRetry() {
      return this.retry;
    }

    public int getExecutorThreads() {
      return this.executorThreads;
    }

    public void setExecutorThreads(int executorThreads) {
      this.executorThreads = executorThreads;
    }

    public Boolean getEnableMessageOrdering() {
      return enableMessageOrdering;
    }

    public void setEnableMessageOrdering(Boolean enableMessageOrdering) {
      this.enableMessageOrdering = enableMessageOrdering;
    }

    public String getEndpoint() {
      return endpoint;
    }

    public void setEndpoint(String endpoint) {
      this.endpoint = endpoint;
    }
  }

  /** Subscriber settings. */
  public static class Subscriber {

    /** Fully qualified subscription name to use as key in property maps */
    private String fullyQualifiedName;

    /** Number of threads used by every subscriber. */
    private Integer executorThreads;

    /** Number of threads used for batch acknowledgement. */
    private int maxAcknowledgementThreads = 4;

    /** The optional pull endpoint setting for the subscriber factory. */
    private String pullEndpoint;

    /** The optional max ack extension period in seconds for the subscriber factory. */
    private Long maxAckExtensionPeriod;

    /**
     * The optional lower bound for a single mod ack extension period in seconds for the subscriber
     * factory.
     */
    private Long minDurationPerAckExtension;

    /**
     * The optional upper bound for a single mod ack extension period in seconds for the subscriber
     * factory.
     */
    private Long maxDurationPerAckExtension;

    /**
     * The optional parallel pull count setting for the subscriber factory.
     */
    private Integer parallelPullCount;

    /** Retry settings for subscriber factory. */
    private final Retry retry = new Retry();

    /** Flow control settings for subscriber factory. */
    private final FlowControl flowControl = new FlowControl();

    /** RPC status codes that should be retried when pulling messages. */
    private Code[] retryableCodes = null;

    public String getFullyQualifiedName() {
      return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
      this.fullyQualifiedName = fullyQualifiedName;
    }

    public Retry getRetry() {
      return this.retry;
    }

    public Code[] getRetryableCodes() {
      return retryableCodes;
    }

    public void setRetryableCodes(Code[] retryableCodes) {
      this.retryableCodes = retryableCodes;
    }

    public FlowControl getFlowControl() {
      return this.flowControl;
    }

    public String getPullEndpoint() {
      return this.pullEndpoint;
    }

    public void setPullEndpoint(String pullEndpoint) {
      this.pullEndpoint = pullEndpoint;
    }

    public Long getMaxAckExtensionPeriod() {
      return this.maxAckExtensionPeriod;
    }

    public void setMaxAckExtensionPeriod(Long maxAckExtensionPeriod) {
      this.maxAckExtensionPeriod = maxAckExtensionPeriod;
    }

    public Long getMinDurationPerAckExtension() {
      return minDurationPerAckExtension;
    }

    public void setMinDurationPerAckExtension(Long minDurationPerAckExtension) {
      this.minDurationPerAckExtension = minDurationPerAckExtension;
    }

    public Long getMaxDurationPerAckExtension() {
      return maxDurationPerAckExtension;
    }

    public void setMaxDurationPerAckExtension(Long maxDurationPerAckExtension) {
      this.maxDurationPerAckExtension = maxDurationPerAckExtension;
    }

    public Integer getParallelPullCount() {
      return this.parallelPullCount;
    }

    public void setParallelPullCount(Integer parallelPullCount) {
      this.parallelPullCount = parallelPullCount;
    }

    public Integer getExecutorThreads() {
      return this.executorThreads;
    }

    /**
     * Set the number of executor threads.
     *
     * @param executorThreads the number of threads
     */
    public void setExecutorThreads(int executorThreads) {
      this.executorThreads = executorThreads;
    }

    public int getMaxAcknowledgementThreads() {
      return this.maxAcknowledgementThreads;
    }

    public void setMaxAcknowledgementThreads(int maxAcknowledgementThreads) {
      this.maxAcknowledgementThreads = maxAcknowledgementThreads;
    }
  }

  /** Health Check settings. */
  public static class Health {

    /**
     * Threshold in seconds over message processing lag. If messages have recently processed with
     * the lagThreshold from now, the subscriber is healthy.
     */
    private Integer lagThreshold;

    /**
     * The threshold number of messages for a subscription backlog. Backlog size over this threshold
     * in combination with non recently processed messages, is an indicator of an unhealthy
     * subscription. This maps to a subscribption's num_undelivered_messages metric on GCP
     * Monitoring.
     */
    private Integer backlogThreshold;

    /** The optional interval in seconds for subscription backlog lookup. */
    private Integer lookUpInterval = 1;

    /** Number of threads used for Health Check Executors. */
    private int executorThreads = 4;

    public Integer getLagThreshold() {
      return lagThreshold;
    }

    public void setLagThreshold(Integer lagThreshold) {
      this.lagThreshold = lagThreshold;
    }

    public Integer getBacklogThreshold() {
      return backlogThreshold;
    }

    public void setBacklogThreshold(Integer backlogThreshold) {
      this.backlogThreshold = backlogThreshold;
    }

    public Integer getLookUpInterval() {
      return lookUpInterval;
    }

    public void setLookUpInterval(Integer lookUpInterval) {
      this.lookUpInterval = lookUpInterval;
    }

    public int getExecutorThreads() {
      return executorThreads;
    }

    public void setExecutorThreads(int executorThreads) {
      this.executorThreads = executorThreads;
    }
  }

  /** Retry settings. */
  public static class Retry {

    /**
     * TotalTimeout has ultimate control over how long the logic should keep trying the remote call
     * until it gives up completely. The higher the total timeout, the more retries can be
     * attempted.
     */
    private Long totalTimeoutSeconds;

    /**
     * InitialRetryDelay controls the delay before the first retry. Subsequent retries will use this
     * value adjusted according to the RetryDelayMultiplier.
     */
    private Long initialRetryDelaySeconds;

    /**
     * RetryDelayMultiplier controls the change in retry delay. The retry delay of the previous call
     * is multiplied by the RetryDelayMultiplier to calculate the retry delay for the next call.
     */
    private Double retryDelayMultiplier;

    /**
     * MaxRetryDelay puts a limit on the value of the retry delay, so that the RetryDelayMultiplier
     * can't increase the retry delay higher than this amount.
     */
    private Long maxRetryDelaySeconds;

    /**
     * MaxAttempts defines the maximum number of attempts to perform. If this value is greater than
     * 0, and the number of attempts reaches this limit, the logic will give up retrying even if the
     * total retry time is still lower than TotalTimeout.
     */
    private Integer maxAttempts;

    /** Jitter determines if the delay time should be randomized. */
    private Boolean jittered;

    /**
     * InitialRpcTimeout controls the timeout for the initial RPC. Subsequent calls will use this
     * value adjusted according to the RpcTimeoutMultiplier.
     */
    private Long initialRpcTimeoutSeconds;

    /**
     * RpcTimeoutMultiplier controls the change in RPC timeout. The timeout of the previous call is
     * multiplied by the RpcTimeoutMultiplier to calculate the timeout for the next call.
     */
    private Double rpcTimeoutMultiplier;

    /**
     * MaxRpcTimeout puts a limit on the value of the RPC timeout, so that the RpcTimeoutMultiplier
     * can't increase the RPC timeout higher than this amount.
     */
    private Long maxRpcTimeoutSeconds;

    public Long getTotalTimeoutSeconds() {
      return this.totalTimeoutSeconds;
    }

    public void setTotalTimeoutSeconds(Long totalTimeoutSeconds) {
      this.totalTimeoutSeconds = totalTimeoutSeconds;
    }

    public Long getInitialRetryDelaySeconds() {
      return this.initialRetryDelaySeconds;
    }

    public void setInitialRetryDelaySeconds(Long initialRetryDelaySeconds) {
      this.initialRetryDelaySeconds = initialRetryDelaySeconds;
    }

    public Double getRetryDelayMultiplier() {
      return this.retryDelayMultiplier;
    }

    public void setRetryDelayMultiplier(Double retryDelayMultiplier) {
      this.retryDelayMultiplier = retryDelayMultiplier;
    }

    public Long getMaxRetryDelaySeconds() {
      return this.maxRetryDelaySeconds;
    }

    public void setMaxRetryDelaySeconds(Long maxRetryDelaySeconds) {
      this.maxRetryDelaySeconds = maxRetryDelaySeconds;
    }

    public Integer getMaxAttempts() {
      return this.maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
      this.maxAttempts = maxAttempts;
    }

    public Boolean getJittered() {
      return this.jittered;
    }

    public void setJittered(Boolean jittered) {
      this.jittered = jittered;
    }

    public Long getInitialRpcTimeoutSeconds() {
      return this.initialRpcTimeoutSeconds;
    }

    public void setInitialRpcTimeoutSeconds(Long initialRpcTimeoutSeconds) {
      this.initialRpcTimeoutSeconds = initialRpcTimeoutSeconds;
    }

    public Double getRpcTimeoutMultiplier() {
      return this.rpcTimeoutMultiplier;
    }

    public void setRpcTimeoutMultiplier(Double rpcTimeoutMultiplier) {
      this.rpcTimeoutMultiplier = rpcTimeoutMultiplier;
    }

    /**
     * get the max RPC timeout seconds.
     *
     * @return the number of seconds of the RPC timeout
     */
    public Long getMaxRpcTimeoutSeconds() {
      return this.maxRpcTimeoutSeconds;
    }

    public void setMaxRpcTimeoutSeconds(Long maxRpcTimeoutSeconds) {
      this.maxRpcTimeoutSeconds = maxRpcTimeoutSeconds;
    }
  }

  /** flow control settings. */
  public static class FlowControl {

    /** Maximum number of outstanding elements to keep in memory before enforcing flow control. */
    private Long maxOutstandingElementCount;

    /** Maximum number of outstanding bytes to keep in memory before enforcing flow control. */
    private Long maxOutstandingRequestBytes;

    /** The behavior when the specified limits are exceeded. */
    private LimitExceededBehavior limitExceededBehavior;

    public Long getMaxOutstandingElementCount() {
      return this.maxOutstandingElementCount;
    }

    public void setMaxOutstandingElementCount(Long maxOutstandingElementCount) {
      this.maxOutstandingElementCount = maxOutstandingElementCount;
    }

    public Long getMaxOutstandingRequestBytes() {
      return this.maxOutstandingRequestBytes;
    }

    public void setMaxOutstandingRequestBytes(Long maxOutstandingRequestBytes) {
      this.maxOutstandingRequestBytes = maxOutstandingRequestBytes;
    }

    public LimitExceededBehavior getLimitExceededBehavior() {
      return this.limitExceededBehavior;
    }

    public void setLimitExceededBehavior(LimitExceededBehavior limitExceededBehavior) {
      this.limitExceededBehavior = limitExceededBehavior;
    }
  }

  /** Batching settings. */
  public static class Batching {

    /** Flow control settings for batching. */
    private final FlowControl flowControl = new FlowControl();

    /** The element count threshold to use for batching. */
    private Long elementCountThreshold;

    /** The request byte threshold to use for batching. */
    private Long requestByteThreshold;

    /**
     * The delay threshold to use for batching. After this amount of time has elapsed (counting from
     * the first element added), the elements will be wrapped up in a batch and sent.
     */
    private Long delayThresholdSeconds;

    /** Enables batching if true. */
    private Boolean enabled;

    public Long getElementCountThreshold() {
      return this.elementCountThreshold;
    }

    public void setElementCountThreshold(Long elementCountThreshold) {
      this.elementCountThreshold = elementCountThreshold;
    }

    public Long getRequestByteThreshold() {
      return this.requestByteThreshold;
    }

    public void setRequestByteThreshold(Long requestByteThreshold) {
      this.requestByteThreshold = requestByteThreshold;
    }

    public Long getDelayThresholdSeconds() {
      return this.delayThresholdSeconds;
    }

    public void setDelayThresholdSeconds(Long delayThresholdSeconds) {
      this.delayThresholdSeconds = delayThresholdSeconds;
    }

    public Boolean getEnabled() {
      return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
    }

    public FlowControl getFlowControl() {
      return this.flowControl;
    }
  }


}
