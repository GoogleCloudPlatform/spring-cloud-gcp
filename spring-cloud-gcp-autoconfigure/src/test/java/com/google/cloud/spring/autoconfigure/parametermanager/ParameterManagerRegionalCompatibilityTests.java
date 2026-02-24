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

package com.google.cloud.spring.autoconfigure.parametermanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterVersion;
import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.parametermanager.v1.ParameterVersionPayload;
import com.google.cloud.spring.autoconfigure.TestUtils;
import com.google.cloud.spring.parametermanager.ParameterManagerClientFactory;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.bootstrap.BootstrapRegistry.InstanceSupplier;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;


/** Unit tests to check compatibility of Parameter Manager. */
class ParameterManagerRegionalCompatibilityTests {

  /**
   * Default value for the latest version of the parameter.
   */
  public static final String GLOBAL_LOCATION = "global";

  private static final String PROJECT_NAME = "hollow-light-of-the-sealed-land";
  private static final String LOCATION = "us-central1";
  private SpringApplicationBuilder application;
  private ParameterManagerClient client;
  private ParameterManagerClientFactory parameterManagerClientFactory;

  @BeforeEach
  void init() {
    application = new SpringApplicationBuilder(ParameterManagerCompatibilityTests.class)
        .web(WebApplicationType.NONE)
        .properties(
            "spring.cloud.gcp.parametermanager.project-id=" + PROJECT_NAME,
            "spring.cloud.gcp.sql.enabled=false");

    client = mock(ParameterManagerClient.class);
    ParameterManagerClient parameterManagerServiceClient = mock(ParameterManagerClient.class);
    parameterManagerClientFactory = mock(ParameterManagerClientFactory.class);
    when(parameterManagerClientFactory.getClient(GLOBAL_LOCATION)).thenReturn(client);
    when(parameterManagerClientFactory.getClient(LOCATION)).thenReturn(parameterManagerServiceClient);
    ParameterVersionName parameterVersionName =
        ParameterVersionName.of(PROJECT_NAME, LOCATION, "my-param", "v1");
    when(client.getParameterVersion(parameterVersionName))
        .thenReturn(
            ParameterVersion.newBuilder()
                .setPayload(
                    ParameterVersionPayload.newBuilder()
                        .setData(ByteString.copyFromUtf8("new Parameter Version"))
                        .build())
                .build());
    parameterVersionName = ParameterVersionName.of(PROJECT_NAME, LOCATION, "fake-param", "v1");
    when(client.getParameterVersion(parameterVersionName)).thenThrow(NotFoundException.class);
  }

  /**
   * Users with the new configuration (i.e., using `spring.config.import`) should get {@link
   * com.google.cloud.spring.parametermanager.ParameterManagerTemplate} autoconfiguration and properties
   * resolved.
   */
  void testConfigurationWhenDefaultParameterIsNotAllowed(String projectIdPropertyName) {
    application
        .properties(projectIdPropertyName + PROJECT_NAME, "spring.config.import=pm@")
        .addBootstrapRegistryInitializer(
            (registry) ->
                registry.registerIfAbsent(
                    ParameterManagerClient.class, InstanceSupplier.of(client)));
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty("pm@global/my-param/v1"))
          .isEqualTo("new Parameter Version");
      assertThatThrownBy(() -> environment.getProperty("pm@global/fake-param/v1"))
          .isExactlyInstanceOf(NotFoundException.class);
    }
  }

  void testConfigurationWhenDefaultParameterIsAllowed(String projectIdPropertyName) {
    application
        .properties(
            projectIdPropertyName + PROJECT_NAME,
            "spring.cloud.gcp.parametermanager.allow-default-parameter=true",
            "spring.config.import=pm@")
        .addBootstrapRegistryInitializer(
            (registry) ->
                registry.registerIfAbsent(
                    ParameterManagerClient.class, InstanceSupplier.of(client)));
    application.sources(TestConfig.class);
    try (ConfigurableApplicationContext applicationContext = application.run()) {
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      assertThat(environment.getProperty("pm@global/my-param/v1"))
          .isEqualTo("new Parameter Version");
      assertThat(environment.getProperty("pm@global/fake-param/v1")).isNull();
    }
  }

  static class TestConfig {

    @Bean
    public CredentialsProvider googleCredentials() {
      return () -> TestUtils.MOCK_CREDENTIALS;
    }
  }
}
