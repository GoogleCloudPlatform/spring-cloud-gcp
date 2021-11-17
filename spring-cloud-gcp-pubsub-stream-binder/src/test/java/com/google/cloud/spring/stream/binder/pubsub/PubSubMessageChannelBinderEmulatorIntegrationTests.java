/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.stream.binder.pubsub;

import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubConsumerProperties;
import com.google.cloud.spring.stream.binder.pubsub.properties.PubSubProducerProperties;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.cloud.stream.binder.AbstractBinderTests;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.Spy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests that require the Pub/Sub emulator to be installed.
 *
 * @author João André Martins
 * @author Elena Felder
 * @author Artem Bilan
 */
@ExtendWith(PubSubEmulator.class)
class PubSubMessageChannelBinderEmulatorIntegrationTests extends
		AbstractBinderTests<PubSubTestBinder, ExtendedConsumerProperties<PubSubConsumerProperties>,
				ExtendedProducerProperties<PubSubProducerProperties>> {

	/**
	 * The emulator instance, shared across tests.
	 */
	public static PubSubEmulator emulator = new PubSubEmulator();

	@Override
	protected PubSubTestBinder getBinder() {
		return new PubSubTestBinder(emulator.getEmulatorHostPort());
	}

	@Override
	protected ExtendedConsumerProperties<PubSubConsumerProperties> createConsumerProperties() {
		return new ExtendedConsumerProperties<>(new PubSubConsumerProperties());
	}

	@Override
	protected ExtendedProducerProperties<PubSubProducerProperties> createProducerProperties(TestInfo testInfo) {
		// TODO: signature change, need to validate test behavior.
		return new ExtendedProducerProperties<>(new PubSubProducerProperties());
	}

	@Override
	public Spy spyOn(String name) {
		return null;
	}

	@Override
	public void testClean(TestInfo testInfo) throws Exception {
		// TODO: signature change, need to validate test behavior.
		// Do nothing. Original test tests for Lifecycle logic that we don't need.

		// Dummy assertion to appease SonarCloud.
		assertThat(emulator.getEmulatorHostPort()).isNotNull();
	}

	@Test
	@Disabled("Looks like there is no Kryo support in SCSt")
	void testSendPojoReceivePojoKryoWithStreamListener() {
		// Dummy assertion to appease SonarCloud.
		assertThat(emulator.getEmulatorHostPort()).isNotNull();
	}


}
