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

import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberTemplate;
import com.google.cloud.spring.pubsub.reactive.PubSubReactiveFactory;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Reactive Pub/Sub support autoconfiguration.
 *
 * @since 1.2
 */
@AutoConfiguration
@AutoConfigureAfter(GcpPubSubAutoConfiguration.class)
@ConditionalOnClass({Flux.class, PubSubSubscriberTemplate.class})
@ConditionalOnProperty(
    value = {"spring.cloud.gcp.pubsub.reactive.enabled", "spring.cloud.gcp.pubsub.enabled"},
    matchIfMissing = true)
public class GcpPubSubReactiveAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public PubSubReactiveFactory pubSubReactiveFactory(
      PubSubSubscriberTemplate subscriberTemplate,
      @Qualifier("pubSubReactiveScheduler") Optional<Scheduler> userProvidedScheduler) {

    Scheduler scheduler = userProvidedScheduler.orElseGet(Schedulers::parallel);
    return new PubSubReactiveFactory(subscriberTemplate, scheduler);
  }
}
