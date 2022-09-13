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

package com.google.cloud.spring.pubsub.support;

import com.google.cloud.pubsub.v1.Publisher;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PreDestroy;

/**
 * The caching implementation of the {@link PublisherFactory}.
 *
 * <p>Creates {@link Publisher}s for topics once using delegate, caches and reuses them.
 */
public class CachingPublisherFactory implements PublisherFactory {
  /** {@link Publisher} cache, enforces only one {@link Publisher} per Pub/Sub topic exists. */
  private final ConcurrentHashMap<String, Publisher> publishers =
      new ConcurrentHashMap<>();

  private final PublisherFactory delegate;

  /**
   * Constructs a caching {@link PublisherFactory} using the delegate.
   *
   * @param delegate a {@link PublisherFactory} that needs to be cached.
   */
  public CachingPublisherFactory(PublisherFactory delegate) {
    this.delegate = delegate;
  }

  @Override
  public Publisher createPublisher(String topic) {
    return this.publishers.computeIfAbsent(topic, delegate::createPublisher);
  }

  /**
   * Returns the delegate.
   *
   * @return the delegate.
   */
  public PublisherFactory getDelegate() {
    return delegate;
  }

  /**
   * Shutdown all cached {@link Publisher} gracefully.
   */
  @PreDestroy
  public void shutdown() {
    publishers.forEachValue(1L, Publisher::shutdown);
  }
}
