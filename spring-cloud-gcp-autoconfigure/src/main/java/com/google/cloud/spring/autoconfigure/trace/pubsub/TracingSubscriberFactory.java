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

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.pubsub.v1.PullRequest;

final class TracingSubscriberFactory implements SubscriberFactory {
  private final PubSubTracing pubSubTracing;

  private final SubscriberFactory delegate;

  TracingSubscriberFactory(PubSubTracing pubSubTracing, SubscriberFactory delegate) {
    this.pubSubTracing = pubSubTracing;
    this.delegate = delegate;
  }

  @Override
  public String getProjectId() {
    return delegate.getProjectId();
  }

  @Override
  public Subscriber createSubscriber(String subscriptionName, MessageReceiver receiver) {
    return delegate.createSubscriber(
        subscriptionName, pubSubTracing.messageReceiver(receiver, subscriptionName));
  }

  @Override
  public PullRequest createPullRequest(
      String subscriptionName, Integer maxMessages, Boolean returnImmediately) {
    return delegate.createPullRequest(subscriptionName, maxMessages, returnImmediately);
  }

  @Override
  public SubscriberStub createSubscriberStub(String subscriptionName) {
    return pubSubTracing.subscriberStub(delegate.createSubscriberStub(subscriptionName));
  }
}
