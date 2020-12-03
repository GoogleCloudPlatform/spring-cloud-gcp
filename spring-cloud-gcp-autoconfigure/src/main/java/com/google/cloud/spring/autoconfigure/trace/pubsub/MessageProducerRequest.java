package com.google.cloud.spring.autoconfigure.trace.pubsub;


import brave.Span.Kind;
import brave.messaging.ProducerRequest;
import brave.propagation.Propagation.RemoteGetter;
import brave.propagation.Propagation.RemoteSetter;
import com.google.pubsub.v1.PubsubMessage;

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

	MessageProducerRequest(PubsubMessage delegate, String topic) {
		if (delegate == null) throw new NullPointerException("delegate == null");
		if (topic == null) throw new NullPointerException("topic == null");
		this.delegate = PubsubMessage.newBuilder(delegate);
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
		return delegate.build();
	}
}
