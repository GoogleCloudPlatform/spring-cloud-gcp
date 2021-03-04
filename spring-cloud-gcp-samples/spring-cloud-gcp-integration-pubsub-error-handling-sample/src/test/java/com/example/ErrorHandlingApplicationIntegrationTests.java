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

package com.example;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.example.deadLetter.PubSubDeadLetterQueueApproach;
import com.example.errorChannel.SpringIntegrationErrorChannelApproach;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { ErrorHandlingApplication.class })
@DirtiesContext
public class ErrorHandlingApplicationIntegrationTests {

	@Autowired
	PubSubTemplate pubSubTemplate;

	@Value("${main-topic}")
	private String mainTopic;

	@Autowired
	private PubSubDeadLetterQueueApproach.DeadLetterPieces deadLetterPieces;

	@Autowired
	private SpringIntegrationErrorChannelApproach errorChannelApproach;

	@BeforeClass
	public static void prepare() {
		assumeThat(
				"PUB/SUB-sample integration tests are disabled. Please use '-Dit.pubsub-integration=true' "
						+ "to enable them. ",
				System.getProperty("it.pubsub-integration"), is("true"));
	}

	@Test
	public void testErrorHandlingSamples() {
		String payload = "testPayload-" + UUID.randomUUID();
		pubSubTemplate.publish(mainTopic, payload);

		await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {
			assertThat(deadLetterPieces.deadLetterReceived.get()).isTrue();
			assertThat(errorChannelApproach.rethrowInErrorHandler.get()).isFalse();
		});
	}
}
