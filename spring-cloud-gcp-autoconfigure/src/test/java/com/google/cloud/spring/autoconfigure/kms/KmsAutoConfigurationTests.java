/*
 * Copyright 2020-2020 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.kms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.kms.KmsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Unit tests for {@link GcpKmsAutoConfiguration}. */
class KmsAutoConfigurationTests {

  private static final String CORE_PROJECT_NAME = "core-project";
  private static final String KMS_PROJECT_NAME = "hollow-light-of-the-sealed-land";
  private static final String KMS_CREDENTIAL_LOCATION = "src/test/resources/fake-credential-key.json";
  private static final String CORE_CREDENTIAL_CLIENT_ID = "12345";
  private static final String KMS_CREDENTIAL_CLIENT_ID = "45678";

  private SpringApplicationBuilder applicationBuilder =
      new SpringApplicationBuilder(TestConfiguration.class, GcpKmsAutoConfiguration.class)
          .properties(
              "spring.cloud.gcp.kms.project-id=" + KMS_PROJECT_NAME,
              "spring.cloud.bootstrap.enabled=true",
              "spring.cloud.gcp.sql.enabled=false")
          .web(WebApplicationType.NONE);

  private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(
          AutoConfigurations.of(GcpKmsAutoConfiguration.class))
      .withUserConfiguration(TestConfiguration.class);

  @Test
  void testKeyManagementClientCreated() {
    try (ConfigurableApplicationContext c = applicationBuilder.run()) {
      KeyManagementServiceClient client = c.getBean(KeyManagementServiceClient.class);
      assertThat(client).isNotNull();
    }
  }

  @Test
  void testKmsTemplateCreated() {
    try (ConfigurableApplicationContext c = applicationBuilder.run()) {
      KmsTemplate kmsTemplate = c.getBean(KmsTemplate.class);
      assertThat(kmsTemplate).isNotNull();
    }
  }

  @Test
  void testShouldTakeCoreCredentials() {
    this.contextRunner
        .run(ctx -> {
          KeyManagementServiceClient client = ctx.getBean(KeyManagementServiceClient.class);
          Credentials credentials = client.getSettings().getCredentialsProvider().getCredentials();
          assertThat(((UserCredentials) credentials).getClientId()).isEqualTo(
              CORE_CREDENTIAL_CLIENT_ID);
        });
  }

  @Test
  void testShouldTakeKmsCredentials() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.kms.credentials.location=file:" + KMS_CREDENTIAL_LOCATION)
        .run(ctx -> {
          KeyManagementServiceClient client = ctx.getBean(KeyManagementServiceClient.class);
          Credentials credentials = client.getSettings().getCredentialsProvider().getCredentials();
          assertThat(((ServiceAccountCredentials) credentials).getClientId()).isEqualTo(
              KMS_CREDENTIAL_CLIENT_ID);
        });
  }

  @Test
  void testShouldTakeKmsProjectIdWhenPresent() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.kms.project-id=" + KMS_PROJECT_NAME)
        .run(ctx -> {
          GcpKmsAutoConfiguration autoConfiguration = ctx.getBean(GcpKmsAutoConfiguration.class);
          assertThat(autoConfiguration.getGcpProjectIdProvider().getProjectId()).isEqualTo(
              KMS_PROJECT_NAME);
        });
  }

  @Test
  void testShouldTakeCoreProjectId() {
    this.contextRunner
        .run(ctx -> {
          GcpKmsAutoConfiguration autoConfiguration = ctx.getBean(GcpKmsAutoConfiguration.class);
          assertThat(autoConfiguration.getGcpProjectIdProvider().getProjectId()).isEqualTo(
              CORE_PROJECT_NAME);
        });
  }

  @Configuration
  static class TestConfiguration {

    @Bean
    public static CredentialsProvider googleCredentials() {
      UserCredentials mockUserCredential = mock(UserCredentials.class);
      when(mockUserCredential.getClientId()).thenReturn(CORE_CREDENTIAL_CLIENT_ID);
      return () -> mockUserCredential;
    }

    @Bean
    public static GcpProjectIdProvider gcpProjectIdProvider() {
      return () -> CORE_PROJECT_NAME;
    }
  }
}
