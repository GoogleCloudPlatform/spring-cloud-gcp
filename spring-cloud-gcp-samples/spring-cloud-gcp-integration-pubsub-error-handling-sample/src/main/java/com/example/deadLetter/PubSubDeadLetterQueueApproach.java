/*
 * Copyright 2021-2021 the original author or authors.
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

package com.example.deadLetter;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.google.cloud.spring.pubsub.support.PubSubTopicUtils;
import com.google.pubsub.v1.DeadLetterPolicy;
import com.google.pubsub.v1.Subscription;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;

import static com.example.ErrorHandlingApplication.UNIQUE_ID;

@Configuration
@Slf4j
public class PubSubDeadLetterQueueApproach {

	private static final String DEAD_LETTER_SUBSCRIPTION_NAME = "error-handling-sample-dead-letter-" + UNIQUE_ID;
	private static final String MAIN_SUBSCRIPTION_NAME = "error-handling-sample-main-" + UNIQUE_ID;

	@Autowired
	private GcpProjectIdProvider gcpProjectIdProvider;

	@Autowired
	private PubSubAdmin pubSubAdmin;

	@Autowired
	private PubSubTemplate pubSubTemplate;

	@PreDestroy
	public void cleanUp() {
		log.info("Deleting subscription " + MAIN_SUBSCRIPTION_NAME);
		pubSubAdmin.deleteSubscription(MAIN_SUBSCRIPTION_NAME);
		log.info("Deleting subscription " + DEAD_LETTER_SUBSCRIPTION_NAME);
		pubSubAdmin.deleteSubscription(DEAD_LETTER_SUBSCRIPTION_NAME);
	}

	@Configuration
	public class MainPiecesDeadLetterApproach {

		// (1) Create a subscription that forwards to the dead letter queue when the delivery retry max is hit.
		@Bean
		public Subscription mainSubscription(@Value("${mainTopic}") String mainTopic,
				@Value("${deadLetterTopic}") String deadLetterTopic) {
			String fullDeadLetterTopic = PubSubTopicUtils
					.toTopicName(deadLetterTopic, gcpProjectIdProvider.getProjectId())
					.toString();
			return pubSubAdmin.createSubscription(Subscription.newBuilder()
					.setName(MAIN_SUBSCRIPTION_NAME)
					.setTopic(mainTopic)
					.setDeadLetterPolicy(DeadLetterPolicy.newBuilder()
							.setDeadLetterTopic(fullDeadLetterTopic)
							.setMaxDeliveryAttempts(6)
							.build()));
		}

		// (2) Create a message channel for messages arriving from the subscription.
		@Bean
		public MessageChannel mainSourceMessageChannelDeadLetterApproach() {
			return new PublishSubscribeChannel();
		}

		// (3) Glue together steps (1) and (2). Messages received from PubSub are piped into the Spring Integration
		// infrastructure and routed to step (4)
		@Bean
		public PubSubInboundChannelAdapter mainMessageChannelAdapterDeadLetterApproach(
				@Qualifier("mainSourceMessageChannelDeadLetterApproach") MessageChannel inputChannel) {
			PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate,
					MAIN_SUBSCRIPTION_NAME);
			adapter.setOutputChannel(inputChannel);
			adapter.setAckMode(AckMode.MANUAL);
			adapter.setPayloadType(String.class);
			return adapter;
		}

		// (4) Define what happens to the messages arriving in the message channel. We will always nack() the message to
		// ensure it eventually gets forwarded to the dead letter queue.
		@ServiceActivator(inputChannel = "mainSourceMessageChannelDeadLetterApproach")
		public void mainMessageReceiver(String payload,
				@Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
			int attempt = Subscriber.getDeliveryAttempt(message.getPubsubMessage());
			log.info("Message arrived on mainSourceMessageChannelDeadLetterApproach, attempt " + attempt +
					", payload: " + payload);
			message.nack();
		}
	}

	@Configuration
	public class DeadLetterPieces {

		/**
		 * Indicator we've received the message on the dead letter queue. Visible for testing.
		 */
		public final AtomicBoolean deadLetterReceived = new AtomicBoolean(false);

		// (6) Create similar pieces for Dead Letter topic - first a subscription, then hook it into Spring Integration
		// infrastructure.
		@Bean
		public Subscription deadLetterSubscription(@Value("${deadLetterTopic}") String deadLetterTopic) {
			String fullDeadLetterTopic = PubSubTopicUtils
					.toTopicName(deadLetterTopic, gcpProjectIdProvider.getProjectId())
					.toString();
			return pubSubAdmin.createSubscription(Subscription.newBuilder()
					.setName(DEAD_LETTER_SUBSCRIPTION_NAME)
					.setTopic(fullDeadLetterTopic));
		}

		@Bean
		public MessageChannel deadLetterSourceMessageChannel() {
			return new PublishSubscribeChannel();
		}

		@Bean
		public PubSubInboundChannelAdapter deadLetterMessageChannelAdapter(
				@Qualifier("deadLetterSourceMessageChannel") MessageChannel inputChannel) {
			PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate,
					DEAD_LETTER_SUBSCRIPTION_NAME);
			adapter.setOutputChannel(inputChannel);
			adapter.setAckMode(AckMode.MANUAL);
			adapter.setPayloadType(String.class);
			return adapter;
		}

		// (7) Do something different  with messages that were undeliverable on the main topic.
		@ServiceActivator(inputChannel = "deadLetterSourceMessageChannel")
		public void deadLetterMessageReceiver(String payload,
				@Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
			log.info("Received message on dead letter topic! Payload: " + payload);
			message.ack();
			deadLetterReceived.set(true);
		}
	}
}
