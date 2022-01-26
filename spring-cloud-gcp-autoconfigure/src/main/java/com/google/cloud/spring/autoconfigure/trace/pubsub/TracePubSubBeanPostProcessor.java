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

package com.google.cloud.spring.autoconfigure.trace.pubsub;

import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

class TracePubSubBeanPostProcessor implements BeanPostProcessor {
  private final BeanFactory beanFactory;

  private PubSubTracing tracing;

  TracePubSubBeanPostProcessor(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (bean instanceof SubscriberFactory) {
      return new TracingSubscriberFactory(pubSubTracing(), (SubscriberFactory) bean);
    }
    return bean;
  }

  PubSubTracing pubSubTracing() {
    if (this.tracing == null) {
      this.tracing = this.beanFactory.getBean(PubSubTracing.class);
    }
    return this.tracing;
  }
}
