package com.google.cloud.spring.autoconfigure.trace.pubsub;

import brave.Span.Kind;
import brave.messaging.ConsumerRequest;
import brave.propagation.Propagation.RemoteGetter;
import brave.propagation.Propagation.RemoteSetter;
import com.google.pubsub.v1.PubsubMessage;

final class MessageConsumerRequest extends ConsumerRequest {
	static final RemoteGetter<MessageConsumerRequest> GETTER =
			new RemoteGetter<MessageConsumerRequest>() {
				@Override
				public Kind spanKind() {
					return Kind.CONSUMER;
				}

				@Override
				public String get(MessageConsumerRequest request, String name) {
					return request.delegate.getAttributesOrDefault(name, null);
				}

				@Override
				public String toString() {
					return "PubsubMessage::getAttribute";
				}
			};

	static final RemoteSetter<MessageConsumerRequest> SETTER =
			new RemoteSetter<MessageConsumerRequest>() {
				@Override
				public Kind spanKind() {
					return Kind.CONSUMER;
				}

				@Override
				public void put(MessageConsumerRequest request, String name, String value) {
					request.delegate.putAttributes(name, value);
				}

				@Override
				public String toString() {
					return "PubsubMessage.Builder::putAttributes";
				}
			};

	final PubsubMessage.Builder delegate;

	final String subscription;

	MessageConsumerRequest(PubsubMessage.Builder delegate, String subscription) {
		if (delegate == null) throw new NullPointerException("delegate == null");
		if (subscription == null) throw new NullPointerException("subscription == null");
		this.delegate = delegate;
		this.subscription = subscription;
	}

	@Override
	public Kind spanKind() {
		return Kind.CONSUMER;
	}

	@Override
	public Object unwrap() {
		return delegate.build();
	}

	@Override
	public String operation() {
		return "receive";
	}

	@Override
	public String channelKind() {
		return "subscription";
	}

	@Override
	public String channelName() {
		return subscription;
	}

	public String messageId() {
		return delegate.getMessageId();
	}
}