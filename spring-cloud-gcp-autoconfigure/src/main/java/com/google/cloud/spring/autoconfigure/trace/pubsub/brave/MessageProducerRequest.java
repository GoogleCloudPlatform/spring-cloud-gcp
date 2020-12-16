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

package com.google.cloud.spring.autoconfigure.trace.pubsub.brave;

import brave.Span.Kind;
import brave.messaging.ProducerRequest;
import brave.propagation.Propagation.RemoteGetter;
import brave.propagation.Propagation.RemoteSetter;
import com.google.pubsub.v1.PubsubMessage;

/**
 * Adds support for injecting and extracting context headers in {@link PubsubMessage.Builder},
 * for the producer side (publishing).
 */
final class MessageProducerRequest extends ProducerRequest {

	static final RemoteGetter<MessageProducerRequest> GETTER =
			new RemoteGetter<MessageProducerRequest>() {
				@Override
				public Kind spanKind() {
					return Kind.PRODUCER;
				}

				@Override
				public String get(MessageProducerRequest request, String name) {
					return request.delegate.getAttributesOrDefault(name, null);
				}

				@Override
				public String toString() {
					return "PubsubMessage::getAttribute";
				}
			};

	static final RemoteSetter<MessageProducerRequest> SETTER =
			new RemoteSetter<MessageProducerRequest>() {
				@Override
				public Kind spanKind() {
					return Kind.PRODUCER;
				}

				@Override
				public void put(MessageProducerRequest request, String name, String value) {
					request.delegate.putAttributes(name, value);
				}

				@Override
				public String toString() {
					return "PubsubMessage.Builder::putAttributes";
				}
			};

	final PubsubMessage.Builder delegate;

	final String topic;

	MessageProducerRequest(PubsubMessage.Builder delegate, String topic) {
		if (delegate == null) {
			throw new NullPointerException("delegate == null");
		}
		if (topic == null) {
			throw new NullPointerException("topic == null");
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
