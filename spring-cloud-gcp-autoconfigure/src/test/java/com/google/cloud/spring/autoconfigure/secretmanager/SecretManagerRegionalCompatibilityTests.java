/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.BootstrapRegistry.InstanceSupplier;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;


/**
 * Unit tests to check compatibility of Secret Manager for regional endpoints.
 */
class SecretManagerRegionalCompatibilityTests {

  private static final String PROJECT_NAME = "regional-secret-manager-project";
  private static final String LOCATION = "us-central1";
  private SpringApplicationBuilder application;
  private SecretManagerServiceClient client;

  @BeforeEach
  void init() {
    application = new SpringApplicationBuilder(SecretManagerRegionalCompatibilityTests.class)
        .web(WebApplicationType.NONE)
        .properties(
            "spring.cloud.gcp.secretmanager.project-id=" + PROJECT_NAME,
            "spring.cloud.gcp.sql.enabled=false",
            "spring.cloud.gcp.secretmanager.location=" + LOCATION);

    client = mock(SecretManagerServiceClient.class);
    SecretVersionName secretVersionName =
        SecretVersionName.newProjectLocationSecretSecretVersionBuilder()
            .setProject(PROJECT_NAME)
            .setLocation(LOCATION)
            .setSecret("my-reg-secret")
            .setSecretVersion("latest")
            .build();
    when(client.accessSecretVersion(secretVersionName))
        .thenReturn(
            AccessSecretVersionResponse.newBuilder()
                .setPayload(
                    SecretPayload.newBuilder().setData(ByteString.copyFromUtf8("newRegSecret")))
                .build());
    secretVersionName =
        SecretVersionName.newProjectLocationSecretSecretVersionBuilder()
            .setProject(PROJECT_NAME)
            .setLocation(LOCATION)
            .setSecret("fake-reg-secret")
            .setSecretVersion("latest")
            .build();
    when(client.accessSecretVersion(secretVersionName))
        .thenThrow(NotFoundException.class);
  }

  /**
   * Users with the new configuration (i.e., using `spring.config.import`) should get {@link
   * com.google.cloud.spring.secretmanager.SecretManagerTemplate} autoconfiguration and properties
   * resolved.
   */
  @Test
  void testRegionalConfigurationWhenDefaultSecretIsNotAllowed() {
    application.properties(
            "spring.config.import=sm://")
        .addBootstrapRegistryInitializer(
            (registry) -> registry.registerIfAbsent(
                SecretManagerServiceClient.class,
                InstanceSupplier.of(client)
            )
        );
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty("sm://projects/regional-secret-manager-project/locations/us-central1/secrets/my-reg-secret/versions/latest")).isEqualTo("newRegSecret");
      assertThatThrownBy(() -> environment.getProperty("sm://projects/regional-secret-manager-project/locations/us-central1/secrets/fake-reg-secret/versions/latest"))
          .isExactlyInstanceOf(NotFoundException.class);
    }
  }

  @Test
  void testRegionalConfigurationWhenDefaultSecretIsAllowed() {
    application.properties(
            "spring.cloud.gcp.secretmanager.allow-default-secret=true",
            "spring.config.import=sm://")
        .addBootstrapRegistryInitializer(
            (registry) -> registry.registerIfAbsent(
                SecretManagerServiceClient.class,
                InstanceSupplier.of(client)
            )
        );
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty("sm://projects/regional-secret-manager-project/locations/us-central1/secrets/my-reg-secret/versions/latest")).isEqualTo("newRegSecret");
      assertThat(environment.getProperty("sm://projects/regional-secret-manager-project/locations/us-central1/secrets/fake-reg-secret/versions/latest")).isNull();
    }
  }
}
