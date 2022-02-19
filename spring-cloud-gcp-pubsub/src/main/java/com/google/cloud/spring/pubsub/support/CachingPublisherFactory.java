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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;

/**
 * The caching implementation of the {@link PublisherFactory}.
 *
 * <p>Creates {@link Publisher}s for topics once using delegate, caches and reuses them.
 * When application context closes, all publishers retrieved through this class will be cleanly
 * shut down.
 */
public class CachingPublisherFactory implements PublisherFactory {
  /** {@link Publisher} cache, enforces only one {@link Publisher} per Pub/Sub topic exists. */
  private final ConcurrentHashMap<String, Publisher> publishers =
      new ConcurrentHashMap<>();

  private PublisherFactory delegate;

  private int shutdownTimeoutSeconds = 120;

  private int shutdownTotalTimeoutSeconds = 600;

  /**
   * Constructs a caching {@link PublisherFactory} using the delegate.
   *
   * @param delegate a {@link PublisherFactory} that needs to be cachecd.
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

  @PreDestroy
  public void shutdownPublishers() {
    if (this.publishers.size() < 1) {
      return;
    }

    List<Callable<Boolean>> publisherTerminationTasks = new ArrayList<>();

    for (Publisher publisher : this.publishers.values()) {

      publisherTerminationTasks.add(
          () -> {
            try {
              System.out.println("Shutting down this one publisher");
              publisher.shutdown();
              boolean terminated = publisher.awaitTermination(this.shutdownTimeoutSeconds,
                  TimeUnit.SECONDS);
              System.out.println("Is publisher terminated? " + terminated);
              return terminated;
            } catch (Throwable e) {
              System.out.println("EXCEPTOIN: ");
              e.printStackTrace();
            }
            System.out.println("uh I don't know");
            return false;

          });
    }

    ExecutorService executorService = Executors.newFixedThreadPool(this.publishers.size());
    try {
      System.out.println("Shutting down all publishers");
      executorService.invokeAll(
          publisherTerminationTasks,
          this.shutdownTotalTimeoutSeconds, TimeUnit.SECONDS);
    } catch(InterruptedException e) {
      // Do nothing; fall through to executor service shutdown.
      System.out.println("Thread interrupted");
    } finally {
      System.out.println("Shutting down executor service");
      executorService.shutdown();
      try {
        System.out.println("Awaiting termination of executor service");
        boolean terminated = executorService.awaitTermination(this.shutdownTotalTimeoutSeconds, TimeUnit.SECONDS);
        System.out.println("Terminated? " + terminated);
      } catch (InterruptedException e) {
        // Do nothing; allow Spring context to shut down.
      }
    }
  }
}
