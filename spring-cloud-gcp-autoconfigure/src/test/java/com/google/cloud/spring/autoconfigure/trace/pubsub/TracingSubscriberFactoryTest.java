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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.pubsub.v1.PullRequest;
import org.junit.jupiter.api.Test;

class TracingSubscriberFactoryTest {

  static final String TEST_SUBSCRIPTION = "testSubscription";

  PubSubTracing mockPubSubTracing = mock(PubSubTracing.class);

  SubscriberFactory mockDelegate = mock(SubscriberFactory.class);

  TracingSubscriberFactory tracingSubscriberFactory =
      new TracingSubscriberFactory(mockPubSubTracing, mockDelegate);

  @Test
  void test_getProjectId() {
    when(mockDelegate.getProjectId()).thenReturn("testProjectId");

    assertThat(tracingSubscriberFactory.getProjectId()).isEqualTo("testProjectId");
    verify(mockDelegate, times(1)).getProjectId();
  }

  @Test
  void test_createSubscriber() {
    Subscriber mockSubscriber = mock(Subscriber.class);
    MessageReceiver mockMessageReceiver = mock(MessageReceiver.class);
    TracingMessageReceiver mockWrappedMessageReceiver = mock(TracingMessageReceiver.class);
    when(mockPubSubTracing.messageReceiver(mockMessageReceiver, TEST_SUBSCRIPTION))
        .thenReturn(mockWrappedMessageReceiver);
    when(mockDelegate.createSubscriber(TEST_SUBSCRIPTION, mockWrappedMessageReceiver))
        .thenReturn(mockSubscriber);

    assertThat(tracingSubscriberFactory.createSubscriber(TEST_SUBSCRIPTION, mockMessageReceiver))
        .isEqualTo(mockSubscriber);
    verify(mockDelegate, times(1)).createSubscriber(TEST_SUBSCRIPTION, mockWrappedMessageReceiver);
    verify(mockPubSubTracing, times(1)).messageReceiver(mockMessageReceiver, TEST_SUBSCRIPTION);
  }

  @Test
  void test_createPullRequest() {
    PullRequest mockPullRequest = mock(PullRequest.class);
    when(mockDelegate.createPullRequest(TEST_SUBSCRIPTION, 10, true)).thenReturn(mockPullRequest);

    assertThat(tracingSubscriberFactory.createPullRequest(TEST_SUBSCRIPTION, 10, true))
        .isEqualTo(mockPullRequest);
    verify(mockDelegate, times(1)).createPullRequest(TEST_SUBSCRIPTION, 10, true);
  }

  @Test
  void test_createSubscriberStub() {
    SubscriberStub mockSubscriberStub = mock(SubscriberStub.class);
    TracingSubscriberStub mockTracingSubscriberStub = mock(TracingSubscriberStub.class);
    when(mockDelegate.createSubscriberStub(any())).thenReturn(mockSubscriberStub);
    when(mockPubSubTracing.subscriberStub(mockSubscriberStub))
        .thenReturn(mockTracingSubscriberStub);

    assertThat(tracingSubscriberFactory.createSubscriberStub("subscription-name"))
        .isEqualTo(mockTracingSubscriberStub);
    verify(mockDelegate, times(1)).createSubscriberStub("subscription-name");
    verify(mockPubSubTracing, times(1)).subscriberStub(mockSubscriberStub);
  }
}
