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

package com.example.errorChannel;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;

import com.example.ErrorHandlingApplication;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.pubsub.v1.Subscription;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandlingException;

@Configuration
@Slf4j
public class SpringIntegrationErrorChannelApproach {

	private static final String ERROR_CHANNEL_APPROACH_SUBSCRIPTION_NAME =
			"error-handling-sample-error-channel-" + ErrorHandlingApplication.UNIQUE_ID;

	@Autowired
	private PubSubAdmin pubSubAdmin;

	@Autowired
	private PubSubTemplate pubSubTemplate;

	/**
	 * Visibile for testing.
	 */
	public AtomicBoolean rethrowInErrorHandler = new AtomicBoolean(true);

	@PreDestroy
	public void cleanUp() {
		log.info("Deleting subscription " + ERROR_CHANNEL_APPROACH_SUBSCRIPTION_NAME);
		pubSubAdmin.deleteSubscription(ERROR_CHANNEL_APPROACH_SUBSCRIPTION_NAME);
	}


	// (1) Create a normal subscription
	@Bean
	public Subscription mainErrorChannelSubscription(@Value("${mainTopic}") String mainTopic) {
		return pubSubAdmin.createSubscription(Subscription.newBuilder()
				.setName(ERROR_CHANNEL_APPROACH_SUBSCRIPTION_NAME)
				.setTopic(mainTopic));
	}

	// (2) Create a message channel for messages arriving from the subscription.
	@Bean
	public MessageChannel mainSourceMessageChannel() {
		return new PublishSubscribeChannel();
	}

	// (3) Create a message channel for messages that went unhandled (threw exceptions).
	@Bean
	public MessageChannel mainSourceErrorChannel() {
		return new PublishSubscribeChannel();
	}

	// (4) Glue together steps (1), (2) and (3). Messages received from PubSub are piped into the Spring Integration
	// infrastructure and routed to step (5).
	@Bean
	public PubSubInboundChannelAdapter mainMessageChannelAdapter(
			@Qualifier("mainSourceMessageChannel") MessageChannel inputChannel,
			@Qualifier("mainSourceErrorChannel") MessageChannel errorChannel) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate,
				ERROR_CHANNEL_APPROACH_SUBSCRIPTION_NAME);
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.AUTO);
		adapter.setPayloadType(String.class);
		adapter.setErrorChannel(errorChannel);
		return adapter;
	}


	// (5) Process a message received from PubSub - oh, no! something went wrong during processing!
	@ServiceActivator(inputChannel = "mainSourceMessageChannel")
	public void mainMessageReceiver(Message<String> m) throws PubSubMessageProcessingException {
		log.info("Message arrived on mainSourceMessageChannel, payload: " + m.getPayload());
		throw new PubSubMessageProcessingException("Oh no, something went wrong!");
	}

	// (6) Handle any issues from processing separately.
	@ServiceActivator(inputChannel = "mainSourceErrorChannel")
	public void errorMessageReceiver(MessageHandlingException mhe) {
		log.info("Now on the error channel! Payload: " + mhe.getFailedMessage().getPayload().toString());
		if (rethrowInErrorHandler.getAndSet(false)) {
			// Sometimes even error processing doesn't go as expected...
			throw mhe;
		}
		// Successfully processed the error in the error channel!
		// The framework will now ack() the message.
	}

	private static class PubSubMessageProcessingException extends Exception {
		PubSubMessageProcessingException(String message) {
			super(message);
		}
	}
}
