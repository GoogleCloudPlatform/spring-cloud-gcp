/*
 * Copyright 2017-2023 the original author or authors.
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
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

class GcpSecretManagerAutoConfigurationUnitTests {
  private ApplicationContextRunner contextRunner;

  @BeforeEach
  void init() {
    contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            GcpSecretManagerAutoConfiguration.class,
            GcpContextAutoConfiguration.class))
        .withPropertyValues("spring.cloud.gcp.project-id=globalProject")
        .withUserConfiguration(TestConfig.class);
  }

  @Test
  void testProjectIdWithSecretManagerProperties() {
    contextRunner
        .withPropertyValues("spring.cloud.gcp.secretmanager.project-id=secretManagerProject")
        .run(
            ctx -> assertThat(ctx.getBean(SecretManagerTemplate.class)
                .getProjectId()).isEqualTo("secretManagerProject"));
  }

  @Test
  void testProjectIdWithGcpProperties() {
    contextRunner.run(
        ctx -> assertThat(ctx.getBean(SecretManagerTemplate.class)
            .getProjectId()).isEqualTo("globalProject"));
  }

  @Test
  void testSecretManagerServiceClientExists() {
    contextRunner.run(
        ctx -> assertThat(ctx.getBean(SecretManagerServiceClient.class))
            .isNotNull());
  }

  @Test
  void testSecretManagerTemplateExists() {
    contextRunner.run(
        ctx -> assertThat(ctx.getBean(SecretManagerTemplate.class))
            .isNotNull());
  }

  static class TestConfig {

    @Bean
    public CredentialsProvider googleCredentials() {
      return () -> mock(Credentials.class);
    }
  }
}
