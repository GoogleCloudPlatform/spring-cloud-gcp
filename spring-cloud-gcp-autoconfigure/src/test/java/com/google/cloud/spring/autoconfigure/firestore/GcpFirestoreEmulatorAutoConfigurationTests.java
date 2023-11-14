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

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.auth.Credentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Tests for Firestore Emulator autoconfiguration.
 */
class GcpFirestoreEmulatorAutoConfigurationTests {

  ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpFirestoreEmulatorAutoConfiguration.class,
                  GcpContextAutoConfiguration.class,
                  GcpFirestoreAutoConfiguration.class,
                  FirestoreTransactionManagerAutoConfiguration.class));

  @Test
  void testAutoConfigurationEnabled() {
    contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.firestore.project-id=",
            "spring.cloud.gcp.firestore.emulator.enabled=true",
            "spring.cloud.gcp.firestore.host-port=localhost:9000")
        .run(
            context -> {
              FirestoreOptions firestoreOptions = context.getBean(FirestoreOptions.class);
              String endpoint =
                  ((InstantiatingGrpcChannelProvider)
                      firestoreOptions.getTransportChannelProvider())
                      .getEndpoint();
              assertThat(endpoint).isEqualTo("localhost:9000");

              assertThat(firestoreOptions.getProjectId()).isEqualTo("unused");
            });
  }

  @Test
  void testAutoConfigurationDisabled() {
    contextRunner
        .withBean(CredentialsProvider.class, () -> NoCredentials::getInstance)
        .withBean(GcpProjectIdProvider.class, () -> () -> "my-project")
        .run(
            context -> {
              FirestoreOptions firestoreOptions = context.getBean(FirestoreOptions.class);
              String endpoint =
                  ((InstantiatingGrpcChannelProvider) firestoreOptions.getTransportChannelProvider())
                      .getEndpoint();
              assertThat(endpoint).isEqualTo("firestore.googleapis.com:443");
            });
  }

  @Test
  void testThatIfProjectIdIsGivenItWillBeUsed() {
    contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.firestore.project-id=demo",
            "spring.cloud.gcp.firestore.emulator.enabled=true",
            "spring.cloud.gcp.firestore.host-port=localhost:9000")
        .run(
            context -> {
              FirestoreOptions firestoreOptions = context.getBean(FirestoreOptions.class);
              assertThat(firestoreOptions.getProjectId()).isEqualTo("demo");
            });
  }

  @Test
  void testEmulatorEnabledConfig() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.firestore.projectId=test-project",
            "spring.cloud.gcp.firestore.emulator.enabled=true",
            "spring.cloud.gcp.firestore.host-port=localhost:8080")
        .run(
            context -> {
              FirestoreOptions datastoreOptions = context.getBean(Firestore.class).getOptions();
              assertThat(datastoreOptions.getProjectId()).isEqualTo("test-project");

              InstantiatingGrpcChannelProvider channelProvider =
                  (InstantiatingGrpcChannelProvider) datastoreOptions.getTransportChannelProvider();
              assertThat(channelProvider.getEndpoint()).isEqualTo("localhost:8080");
            });
  }

  @Test
  void testDefaultConfig() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.firestore.projectId=test-project")
        .withBean(CredentialsProvider.class, () -> NoCredentials::getInstance)
        .withBean(GcpProjectIdProvider.class, () -> () -> "my-project")
        .run(
            context -> {
              GcpFirestoreProperties gcpFirestoreProperties =
                  context.getBean(GcpFirestoreProperties.class);
              assertThat(gcpFirestoreProperties.getEmulator().isEnabled()).isFalse();

              FirestoreOptions datastoreOptions = context.getBean(Firestore.class).getOptions();
              assertThat(datastoreOptions.getProjectId()).isEqualTo("test-project");

              InstantiatingGrpcChannelProvider channelProvider =
                  (InstantiatingGrpcChannelProvider) datastoreOptions.getTransportChannelProvider();
              assertThat(channelProvider.getEndpoint()).isEqualTo("firestore.googleapis.com:443");
            });
  }

  @Test
  void testFirestoreTemplateParent() {
    contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.firestore.project-id=demo",
            "spring.cloud.gcp.firestore.emulator.enabled=true",
            "spring.cloud.gcp.firestore.database-id=testdb")
        .run(
            context -> {
              FirestoreTemplate template = context.getBean(FirestoreTemplate.class);
              assertThat("projects/demo/databases/testdb/documents").isEqualTo(
                  ReflectionTestUtils.getField(template, "parent"));
            });
  }

  @Test
  void testDefaultCredentials() {
    contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.firestore.project-id=demo",
            "spring.cloud.gcp.firestore.emulator.enabled=true",
            "spring.cloud.gcp.firestore.database-id=testdb")
        .run(
            context -> {
              FirestoreOptions firestoreOptions = context.getBean(FirestoreOptions.class);
              Credentials credentials = firestoreOptions.getCredentials();
              List<String> header = credentials.getRequestMetadata()
                  .get("google-cloud-resource-prefix");
              assertThat(header).isEqualTo(List.of("projects/demo/databases/testdb"));
            });
  }
}
