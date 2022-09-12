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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.pubsub.v1.Publisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for the {@link CachingPublisherFactory}. */
@ExtendWith(MockitoExtension.class)
class CachingPublisherFactoryTests {

  @Mock private PublisherFactory delegate;

  @Mock private Publisher publisher1;

  @Mock private Publisher publisher2;

  @Test
  void testGetPublisherCaching() {
    CachingPublisherFactory cachingPublisherFactory = new CachingPublisherFactory(delegate);

    when(delegate.createPublisher("topic1")).thenReturn(publisher1);
    when(delegate.createPublisher("topic2")).thenReturn(publisher2);

    assertThat(cachingPublisherFactory.createPublisher("topic1")).isEqualTo(publisher1);
    assertThat(cachingPublisherFactory.createPublisher("topic1")).isEqualTo(publisher1);

    assertThat(cachingPublisherFactory.createPublisher("topic2")).isEqualTo(publisher2);
    assertThat(cachingPublisherFactory.createPublisher("topic2")).isEqualTo(publisher2);
    assertThat(cachingPublisherFactory.getDelegate()).isEqualTo(delegate);

    verify(delegate, times(1)).createPublisher("topic1");
    verify(delegate, times(1)).createPublisher("topic2");

    cachingPublisherFactory.shutdown();
    verify(publisher1, times(1)).shutdown();
    verify(publisher2, times(1)).shutdown();
  }
}
