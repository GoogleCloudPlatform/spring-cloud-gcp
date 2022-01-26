/*
 * Copyright 2017-2018 the original author or authors.
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.TransportChannel;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.spring.pubsub.core.publisher.PublisherCustomizer;
import com.google.pubsub.v1.ProjectTopicName;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests for the publisher factory.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultPublisherFactoryTests {

  /** used to test exception messages and types. */
  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Mock private CredentialsProvider credentialsProvider;

  DefaultPublisherFactory factory;

  @Before
  public void setUp() throws IOException {
    factory = new DefaultPublisherFactory(() -> "projectId");
    factory.setCredentialsProvider(NoCredentialsProvider.create());
    TransportChannelProvider mockChannelProvider = mock(TransportChannelProvider.class);
    TransportChannel mockTransportChannel = mock(TransportChannel.class);
    when(mockChannelProvider.getTransportChannel()).thenReturn(mockTransportChannel);
    ApiCallContext mockContext = mock(ApiCallContext.class);
    when(mockTransportChannel.getEmptyCallContext()).thenReturn(mockContext);
    when(mockContext.withTransportChannel(any())).thenReturn(mockContext);
    factory.setChannelProvider(mockChannelProvider);
  }


  @Test
  public void testGetPublisher() {
    DefaultPublisherFactory factory = new DefaultPublisherFactory(() -> "projectId");
    factory.setCredentialsProvider(this.credentialsProvider);
    Publisher publisher = factory.createPublisher("testTopic");

    assertThat(((ProjectTopicName) publisher.getTopicName()).getTopic()).isEqualTo("testTopic");
    assertThat(((ProjectTopicName) publisher.getTopicName()).getProject()).isEqualTo("projectId");
  }

  @Test
  public void testNewDefaultPublisherFactory_nullProjectIdProvider() {
    this.expectedException.expect(IllegalArgumentException.class);
    this.expectedException.expectMessage("The project ID provider can't be null.");
    new DefaultPublisherFactory(null);
  }

  @Test
  public void testNewDefaultPublisherFactory_nullProjectId() {
    this.expectedException.expect(IllegalArgumentException.class);
    this.expectedException.expectMessage("The project ID can't be null or empty.");
    new DefaultPublisherFactory(() -> null);
  }

  @Test
  public void createPublisherUsesCustomizersInOrder() {
    final AtomicInteger counter = new AtomicInteger(1);

    PublisherCustomizer c1 = (pb, t) -> {
      assertThat(counter.getAndIncrement()).isEqualTo(1);
    };
    PublisherCustomizer c2 = (pb, t) -> {
      assertThat(counter.getAndIncrement()).isEqualTo(2);
    };
    PublisherCustomizer c3 = (pb, t) -> {
      assertThat(counter.getAndIncrement()).isEqualTo(3);
    };

    factory.setCustomizers(Arrays.asList(c1, c2, c3));
    factory.createPublisher("testtopic");

    assertThat(counter).hasValue(4);
  }

  @Test
  public void createPublisherWithoutCustomizersWorksFine() throws Exception {

    Publisher publisher = factory.createPublisher("testtopic");

    Publisher defaultPublisher = Publisher.newBuilder("testtopic")
        .setCredentialsProvider(NoCredentialsProvider.create())
        .build();
    assertThat(publisher.getBatchingSettings()).isSameAs(defaultPublisher.getBatchingSettings());
  }

  @Test
  public void createPublisherWithExplicitNullCustomizersFails() {
    assertThatThrownBy(() -> factory.setCustomizers(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Non-null customizers expected");
  }
}
