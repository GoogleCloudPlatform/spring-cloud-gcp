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

package com.google.cloud.spring.pubsub.core.subscriber;

import java.util.function.Consumer;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.SubscriberInterface;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.PubSubSubscriptionUtils;
import com.google.cloud.spring.pubsub.support.StreamingSubscriberFactory;
import com.google.cloud.spring.pubsub.support.converter.ConvertedBasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.SimplePubSubMessageConverter;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

/**
 * Default implementation of {@link PubSubStreamingSubscriberOperations}.
 *
 * <p>The main Google Cloud Pub/Sub integration component for consuming
 * messages from subscriptions asynchronously.
 */
public class PubSubStreamingSubscriberTemplate
		implements PubSubStreamingSubscriberOperations {

	private final StreamingSubscriberFactory streamingSubscriberFactory;
	private PubSubMessageConverter pubSubMessageConverter = new SimplePubSubMessageConverter();

	/**
	 * Default {@link PubSubSubscriberTemplate} constructor.
	 *
	 * @param streamingSubscriberFactory the {@link SubscriberInterface} factory
	 *                          to subscribe to subscriptions or pull messages.
	 */
	public PubSubStreamingSubscriberTemplate(StreamingSubscriberFactory streamingSubscriberFactory) {
		Assert.notNull(streamingSubscriberFactory, "The streamingSubscriberFactory can't be null.");

		this.streamingSubscriberFactory = streamingSubscriberFactory;
	}

	/**
	 * Get the converter used to convert a message payload to the desired type.
	 *
	 * @return the currently used converter
	 */
	public PubSubMessageConverter getMessageConverter() {
		return this.pubSubMessageConverter;
	}

	/**
	 * Set the converter used to convert a message payload to the desired type.
	 *
	 * @param pubSubMessageConverter the converter to set
	 */
	public void setMessageConverter(PubSubMessageConverter pubSubMessageConverter) {
		Assert.notNull(pubSubMessageConverter, "The pubSubMessageConverter can't be null.");

		this.pubSubMessageConverter = pubSubMessageConverter;
	}

	@Override
	public SubscriberInterface subscribe(String subscription,
			Consumer<BasicAcknowledgeablePubsubMessage> messageConsumer) {
		Assert.notNull(messageConsumer, "The messageConsumer can't be null.");

		SubscriberInterface subscriber =
				this.streamingSubscriberFactory.createSubscriber(subscription,
						(message, ackReplyConsumer) -> messageConsumer.accept(
								new PushedAcknowledgeablePubsubMessage(
										PubSubSubscriptionUtils.toProjectSubscriptionName(subscription,
												this.streamingSubscriberFactory.getProjectId()),
										message,
										ackReplyConsumer)));
		subscriber.startAsync();
		return subscriber;
	}

	@Override
	public <T> SubscriberInterface subscribeAndConvert(String subscription,
			Consumer<ConvertedBasicAcknowledgeablePubsubMessage<T>> messageConsumer, Class<T> payloadType) {
		Assert.notNull(messageConsumer, "The messageConsumer can't be null.");

		SubscriberInterface subscriber =
				this.streamingSubscriberFactory.createSubscriber(subscription,
						(message, ackReplyConsumer) -> messageConsumer.accept(
								new ConvertedPushedAcknowledgeablePubsubMessage<>(
										PubSubSubscriptionUtils.toProjectSubscriptionName(subscription,
												this.streamingSubscriberFactory.getProjectId()),
										message,
										this.getMessageConverter().fromPubSubMessage(message, payloadType),
										ackReplyConsumer)));
		subscriber.startAsync();
		return subscriber;
	}

	private static class PushedAcknowledgeablePubsubMessage extends
			AbstractBasicAcknowledgeablePubsubMessage {

		private final AckReplyConsumer ackReplyConsumer;

		PushedAcknowledgeablePubsubMessage(ProjectSubscriptionName projectSubscriptionName, PubsubMessage message,
				AckReplyConsumer ackReplyConsumer) {
			super(projectSubscriptionName, message);
			this.ackReplyConsumer = ackReplyConsumer;
		}

		@Override
		public ListenableFuture<Void> ack() {
			SettableListenableFuture<Void> settableListenableFuture = new SettableListenableFuture<>();

			try {
				this.ackReplyConsumer.ack();
				settableListenableFuture.set(null);
			}
			catch (Exception e) {
				settableListenableFuture.setException(e);
			}

			return settableListenableFuture;
		}

		@Override
		public ListenableFuture<Void> nack() {
			SettableListenableFuture<Void> settableListenableFuture = new SettableListenableFuture<>();

			try {
				this.ackReplyConsumer.nack();
				settableListenableFuture.set(null);
			}
			catch (Exception e) {
				settableListenableFuture.setException(e);
			}

			return settableListenableFuture;
		}

		@Override
		public String toString() {
			return "PushedAcknowledgeablePubsubMessage{" +
					"projectId='" + getProjectSubscriptionName().getProject() + '\'' +
					", subscriptionName='" + getProjectSubscriptionName().getSubscription() + '\'' +
					", message=" + getPubsubMessage() +
					'}';
		}
	}

	private static class ConvertedPushedAcknowledgeablePubsubMessage<T> extends
			PushedAcknowledgeablePubsubMessage
			implements ConvertedBasicAcknowledgeablePubsubMessage<T> {

		private final T payload;

		ConvertedPushedAcknowledgeablePubsubMessage(ProjectSubscriptionName projectSubscriptionName,
				PubsubMessage message, T payload, AckReplyConsumer ackReplyConsumer) {
			super(projectSubscriptionName, message, ackReplyConsumer);
			this.payload = payload;
		}

		@Override
		public T getPayload() {
			return this.payload;
		}
	}
}
