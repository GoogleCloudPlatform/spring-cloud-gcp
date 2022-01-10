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

import brave.Span.Kind;
import brave.internal.Nullable;
import brave.messaging.ProducerRequest;
import brave.propagation.Propagation.RemoteGetter;
import brave.propagation.Propagation.RemoteSetter;
import com.google.pubsub.v1.PubsubMessage;

/**
 * Adds support for injecting and extracting context headers in {@link PubsubMessage.Builder}, for
 * the producer side (publishing).
 */
final class PubSubProducerRequest extends ProducerRequest {

  static final RemoteGetter<PubSubProducerRequest> GETTER =
      new RemoteGetter<PubSubProducerRequest>() {
        @Override
        public Kind spanKind() {
          return Kind.PRODUCER;
        }

        @Override
        public String get(PubSubProducerRequest request, String name) {
          return request.delegate.getAttributesOrDefault(name, null);
        }

        @Override
        public String toString() {
          return "PubsubMessage.Builder::getAttribute";
        }
      };

  static final RemoteSetter<PubSubProducerRequest> SETTER =
      new RemoteSetter<PubSubProducerRequest>() {
        @Override
        public Kind spanKind() {
          return Kind.PRODUCER;
        }

        @Override
        public void put(PubSubProducerRequest request, String name, String value) {
          request.delegate.putAttributes(name, value);
        }

        @Override
        public String toString() {
          return "PubsubMessage.Builder::putAttributes";
        }
      };

  final PubsubMessage.Builder delegate;

  final String topic;

  PubSubProducerRequest(PubsubMessage.Builder delegate, @Nullable String topic) {
    if (delegate == null) {
      throw new NullPointerException("delegate == null");
    }
    this.delegate = delegate;
    this.topic = topic;
  }

  @Override
  public String operation() {
    return "send";
  }

  @Override
  public String channelKind() {
    return "topic";
  }

  @Override
  public String channelName() {
    return topic;
  }

  @Override
  public Kind spanKind() {
    return Kind.PRODUCER;
  }

  @Override
  public Object unwrap() {
    return delegate;
  }
}
