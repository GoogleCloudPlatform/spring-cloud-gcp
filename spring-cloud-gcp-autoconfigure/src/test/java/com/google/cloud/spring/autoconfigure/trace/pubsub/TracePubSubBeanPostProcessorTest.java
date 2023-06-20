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

package com.google.cloud.spring.autoconfigure.trace.pubsub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;

final class TracePubSubBeanPostProcessorTest {

  BeanFactory mockBeanFactory = mock(BeanFactory.class);

  PubSubTracing mockPubSubTracing = mock(PubSubTracing.class);

  TracePubSubBeanPostProcessor tracePubSubBeanPostProcessor =
      new TracePubSubBeanPostProcessor(mockBeanFactory);

  @Test
  void test_postProcessBeforeInitialization_SubscriberFactory() {
    SubscriberFactory mockSubscriberFactory = mock(SubscriberFactory.class);

    Object result =
        tracePubSubBeanPostProcessor.postProcessBeforeInitialization(
            mockSubscriberFactory, "subscriberFactory");

    assertThat(result).isInstanceOf(TracingSubscriberFactory.class);
  }

  @Test
  void test_postProcessBeforeInitialization_Other() {
    PubSubTemplate mockOther = mock(PubSubTemplate.class);

    Object result =
        tracePubSubBeanPostProcessor.postProcessBeforeInitialization(mockOther, "other");

    assertThat(result).isEqualTo(mockOther);
  }

  @Test
  void test_pubsubTracingCaching() {
    when(mockBeanFactory.getBean(PubSubTracing.class)).thenReturn(mockPubSubTracing);
    tracePubSubBeanPostProcessor.pubSubTracing();
    tracePubSubBeanPostProcessor.pubSubTracing();
    verify(mockBeanFactory, times(1)).getBean(PubSubTracing.class);
  }
}
