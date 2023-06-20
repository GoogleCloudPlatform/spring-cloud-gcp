/*
 * Copyright 2018-2021 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.pubsub.health;

import com.google.cloud.spring.pubsub.core.health.HealthTracker;
import com.google.cloud.spring.pubsub.core.health.HealthTrackerRegistry;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

/**
 * @since 2.0.6
 */
public class PubSubSubscriptionHealthIndicator extends AbstractHealthIndicator {

  private final HealthTrackerRegistry healthTrackerRegistry;

  public PubSubSubscriptionHealthIndicator(HealthTrackerRegistry healthTrackerRegistry) {
    this.healthTrackerRegistry = healthTrackerRegistry;
  }

  @Override
  protected void doHealthCheck(Builder builder) throws Exception {
    builder.up();

    for (HealthTracker healthTracker : healthTrackerRegistry.healthTrackers()) {
      long messagesOverThreshold = healthTracker.messagesOverThreshold();

      if (messagesOverThreshold > 0) {
        builder.down();
        builder.withDetail(healthTracker.subscription().toString(), messagesOverThreshold);
      }
    }
  }
}
