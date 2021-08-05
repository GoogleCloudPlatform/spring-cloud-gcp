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

package com.google.cloud.spring.autoconfigure.firestore;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.auth.Credentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for the Firestore emulator config.
 *
 * @author Gary Wong
 */
class GcpFirestoreEmulatorAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					GcpContextAutoConfiguration.class,
					GcpFirestoreEmulatorAutoConfiguration.class,
					GcpFirestoreAutoConfiguration.class))
			.withUserConfiguration(GcpFirestoreEmulatorAutoConfigurationTests.TestConfiguration.class);

	@Test
	void testEmulatorEnabledConfig() {
		this.contextRunner.withPropertyValues(
				"spring.cloud.gcp.firestore.projectId=test-project",
				"spring.cloud.gcp.firestore.emulator.enabled=true",
				"spring.cloud.gcp.firestore.host-port=localhost:8080"
		).run(context -> {
			CredentialsProvider defaultCredentialsProvider = context.getBean(CredentialsProvider.class);
			assertThat(defaultCredentialsProvider).isNotInstanceOf(NoCredentialsProvider.class);

			GcpFirestoreAutoConfiguration firestoreAutoConfiguration = context.getBean(GcpFirestoreAutoConfiguration.class);
			assertThat(firestoreAutoConfiguration.getCredentialsProvider()).isInstanceOf(NoCredentialsProvider.class);

			FirestoreOptions datastoreOptions = context.getBean(Firestore.class).getOptions();
			assertThat(datastoreOptions.getProjectId()).isEqualTo("test-project");

			InstantiatingGrpcChannelProvider channelProvider = (InstantiatingGrpcChannelProvider) datastoreOptions.getTransportChannelProvider();
			assertThat(channelProvider.getEndpoint()).isEqualTo("localhost:8080");
		});
	}

	@Test
	void testDefaultConfig() {
		this.contextRunner.withPropertyValues(
				"spring.cloud.gcp.firestore.projectId=test-project"
		).run(context -> {
			GcpFirestoreProperties gcpFirestoreProperties = context.getBean(GcpFirestoreProperties.class);
			assertThat(gcpFirestoreProperties.getEmulator().isEnabled()).isFalse();

			CredentialsProvider defaultCredentialsProvider = context.getBean(CredentialsProvider.class);
			assertThat(defaultCredentialsProvider).isNotInstanceOf(DefaultCredentialsProvider.class);

			FirestoreOptions datastoreOptions = context.getBean(Firestore.class).getOptions();
			assertThat(datastoreOptions.getProjectId()).isEqualTo("test-project");

			InstantiatingGrpcChannelProvider channelProvider = (InstantiatingGrpcChannelProvider) datastoreOptions.getTransportChannelProvider();
			assertThat(channelProvider.getEndpoint()).isEqualTo("firestore.googleapis.com:443");
		});
	}

	private static class TestConfiguration {
		@Bean
		public CredentialsProvider googleCredentials() {
			return () -> mock(Credentials.class);
		}
	}
}
