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

package com.google.cloud.spring.pubsub.support;

import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.core.PubSubException;
import com.google.cloud.spring.pubsub.core.publisher.PublisherCustomizer;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.springframework.util.Assert;

/**
 * The default {@link PublisherFactory} implementation.
 *
 * <p>Creates {@link Publisher}s for topics. Use {@link CachingPublisherFactory} to cache them.
 */
public class DefaultPublisherFactory implements PublisherFactory {

  private final String projectId;

  private ExecutorProvider executorProvider;

  private TransportChannelProvider channelProvider;

  private CredentialsProvider credentialsProvider;

  private HeaderProvider headerProvider;

  private RetrySettings retrySettings;

  private BatchingSettings batchingSettings;

  private Boolean enableMessageOrdering;

  private String endpoint;

  private List<PublisherCustomizer> customizers;

  /**
   * Create {@link DefaultPublisherFactory} instance based on the provided {@link
   * GcpProjectIdProvider}.
   *
   * <p>The {@link GcpProjectIdProvider} must not be null, neither provide an empty {@code
   * projectId}.
   *
   * @param projectIdProvider provides the default GCP project ID for selecting the topic
   */
  public DefaultPublisherFactory(GcpProjectIdProvider projectIdProvider) {
    Assert.notNull(projectIdProvider, "The project ID provider can't be null.");

    this.projectId = projectIdProvider.getProjectId();
    Assert.hasText(this.projectId, "The project ID can't be null or empty.");
  }

  /**
   * Set the provider for the executor that will be used by the publisher. Useful to specify the
   * number of threads to be used by each executor.
   *
   * @param executorProvider the executor provider to set
   */
  public void setExecutorProvider(ExecutorProvider executorProvider) {
    this.executorProvider = executorProvider;
  }

  /**
   * Set the provider for the channel to be used by the publisher. Useful to specify HTTP headers
   * for the REST API calls.
   *
   * @param channelProvider the channel provider to set
   */
  public void setChannelProvider(TransportChannelProvider channelProvider) {
    this.channelProvider = channelProvider;
  }

  /**
   * Set the provider for the GCP credentials to be used by the publisher on every API calls it
   * makes.
   *
   * @param credentialsProvider the credentials provider to set
   */
  public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
    this.credentialsProvider = credentialsProvider;
  }

  /**
   * Set the provider for the HTTP headers to be used in the Pub/Sub REST API requests.
   *
   * @param headerProvider the header provider to set
   */
  public void setHeaderProvider(HeaderProvider headerProvider) {
    this.headerProvider = headerProvider;
  }

  /**
   * Set the API call retry configuration.
   *
   * @param retrySettings the retry settings to set
   */
  public void setRetrySettings(RetrySettings retrySettings) {
    this.retrySettings = retrySettings;
  }

  /**
   * Set the API call batching configuration.
   *
   * @param batchingSettings the batching settings to set
   */
  public void setBatchingSettings(BatchingSettings batchingSettings) {
    this.batchingSettings = batchingSettings;
  }

  /**
   * Set whether message ordering should be enabled on the publisher.
   *
   * @param enableMessageOrdering whether to enable message ordering
   */
  public void setEnableMessageOrdering(Boolean enableMessageOrdering) {
    this.enableMessageOrdering = enableMessageOrdering;
  }

  /**
   * Set the publisher endpoint. Example: "us-east1-pubsub.googleapis.com:443". This is useful in
   * conjunction with enabling message ordering because sending messages to the same region ensures
   * they are received in order even when multiple publishers are used.
   *
   * @param endpoint publisher endpoint
   */
  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  /**
   * Accepts a list of {@link Publisher.Builder} customizers.
   * The customizers are applied in the order provided, so the later customizers can override
   * any settings provided by the earlier ones.
   */
  public void setCustomizers(List<PublisherCustomizer> customizers) {
    Assert.notNull(customizers, "Non-null customizers expected");
    this.customizers = Collections.unmodifiableList(customizers);
  }

  /**
   * Creates a {@link Publisher} for a given topic.
   *
   * <p></p>Configuration precedence:
   * <ol>
   *   <li>modifications applied by the factory customizers
   *   <li>{@code spring.cloud.gcp.pubsub.publisher} configuration options
   *   <li>client library defaults
   *</ol>
   *
   * @param topic destination topic
   * @return fully configured publisher
   */
  @Override
  public Publisher createPublisher(String topic) {
    try {
      Publisher.Builder publisherBuilder =
          Publisher.newBuilder(PubSubTopicUtils.toTopicName(topic, this.projectId));

      applyPublisherSettings(publisherBuilder);
      applyCustomizers(publisherBuilder, topic);

      return publisherBuilder.build();
    } catch (IOException ioe) {
      throw new PubSubException(
          "An error creating the Google Cloud Pub/Sub publisher " + "occurred.", ioe);
    }
  }

  void applyPublisherSettings(Publisher.Builder publisherBuilder) {
    if (this.executorProvider != null) {
      publisherBuilder.setExecutorProvider(this.executorProvider);
    }

    if (this.channelProvider != null) {
      publisherBuilder.setChannelProvider(this.channelProvider);
    }

    if (this.credentialsProvider != null) {
      publisherBuilder.setCredentialsProvider(this.credentialsProvider);
    }

    if (this.headerProvider != null) {
      publisherBuilder.setHeaderProvider(this.headerProvider);
    }

    if (this.retrySettings != null) {
      publisherBuilder.setRetrySettings(this.retrySettings);
    }

    if (this.batchingSettings != null) {
      publisherBuilder.setBatchingSettings(this.batchingSettings);
    }

    if (this.enableMessageOrdering != null) {
      publisherBuilder.setEnableMessageOrdering(this.enableMessageOrdering);
    }

    if (this.endpoint != null) {
      publisherBuilder.setEndpoint(this.endpoint);
    }
  }

  void applyCustomizers(Publisher.Builder publisherBuilder, String topic) {
    if (this.customizers == null) {
      return;
    }

    for (PublisherCustomizer customizer : this.customizers) {
      customizer.apply(publisherBuilder, topic);
    }
  }
}
