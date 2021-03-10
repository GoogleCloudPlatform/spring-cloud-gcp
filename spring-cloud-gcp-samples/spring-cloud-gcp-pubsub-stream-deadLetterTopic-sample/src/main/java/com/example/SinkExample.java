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

package com.example;

import java.util.function.Consumer;

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

/**
 * Example of a sink for the sample app.
 *
 * @author Travis Tomsu
 */
@Configuration
@Slf4j
public class SinkExample {

	@Bean
	public Consumer<Message<UserMessage>> logUserMessage() {
		return message -> {
			UserMessage userMessage = message.getPayload();
			BasicAcknowledgeablePubsubMessage nackable =
					(BasicAcknowledgeablePubsubMessage) message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE);
			Assert.notNull(nackable, "Message was missing original message");
			Integer deliveryAttempt = Subscriber.getDeliveryAttempt(nackable.getPubsubMessage());

			// Typically you won't nack() _every_ message, but this demonstrates a max number of retries before the
			// message is routed to the dead letter queue.
			log.info("Nacking message {} from {} at {}: {}", deliveryAttempt, userMessage.getUsername(),
					userMessage.getCreatedAt(), userMessage.getBody());
			nackable.nack();
		};
	}

	@Bean
	public Consumer<UserMessage> deadLetterMessages() {
		return userMessage -> log.info("Received message on dead letter topic from {}: {}", userMessage.getUsername(),
				userMessage.getBody());
	}
}
