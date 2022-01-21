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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.auth.Credentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/** Tests for the Firestore emulator config. */
class GcpFirestoreEmulatorAutoConfigurationTests {

  GcpProjectIdProvider projectId = () -> "projectId";

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpFirestoreEmulatorAutoConfiguration.class, GcpFirestoreAutoConfiguration.class))
          .withBean("projectId", GcpProjectIdProvider.class, () -> projectId);

  @Test
  void testEmulatorEnabledConfig() {

    CredentialsProvider mockedCredentialsProvider = () -> mock(NoCredentials.class);

    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.firestore.projectId=test-project",
            "spring.cloud.gcp.firestore.emulator.enabled=true",
            "spring.cloud.gcp.firestore.host-port=localhost:8080")
        .withBean(
            "mockedCredentialsProvider", CredentialsProvider.class, () -> mockedCredentialsProvider)
        .run(
            context -> {
              CredentialsProvider defaultCredentialsProvider =
                  context.getBean(CredentialsProvider.class);
              assertThat(defaultCredentialsProvider).isSameAs(mockedCredentialsProvider);

              GcpFirestoreAutoConfiguration firestoreAutoConfiguration =
                  context.getBean(GcpFirestoreAutoConfiguration.class);
              assertThat(firestoreAutoConfiguration.getCredentialsProvider())
                  .isInstanceOf(NoCredentialsProvider.class);

              FirestoreOptions datastoreOptions = context.getBean(Firestore.class).getOptions();
              assertThat(datastoreOptions.getProjectId()).isEqualTo("test-project");

              InstantiatingGrpcChannelProvider channelProvider =
                  (InstantiatingGrpcChannelProvider) datastoreOptions.getTransportChannelProvider();
              assertThat(channelProvider.getEndpoint()).isEqualTo("localhost:8080");
            });
  }

  @Test
  void testDefaultConfig() {
    CredentialsProvider mockedCredentialsProvider = () -> mock(Credentials.class);

    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.firestore.projectId=test-project")
        .withBean(
            "mockedCredentialsProvider", CredentialsProvider.class, () -> mockedCredentialsProvider)
        .run(
            context -> {
              GcpFirestoreProperties gcpFirestoreProperties =
                  context.getBean(GcpFirestoreProperties.class);
              assertThat(gcpFirestoreProperties.getEmulator().isEnabled()).isFalse();

              GcpFirestoreAutoConfiguration firestoreAutoConfiguration =
                  context.getBean(GcpFirestoreAutoConfiguration.class);
              assertThat(firestoreAutoConfiguration.getCredentialsProvider())
                  .isEqualTo(mockedCredentialsProvider);

              FirestoreOptions datastoreOptions = context.getBean(Firestore.class).getOptions();
              assertThat(datastoreOptions.getProjectId()).isEqualTo("test-project");

              InstantiatingGrpcChannelProvider channelProvider =
                  (InstantiatingGrpcChannelProvider) datastoreOptions.getTransportChannelProvider();
              assertThat(channelProvider.getEndpoint()).isEqualTo("firestore.googleapis.com:443");
            });
  }
}
