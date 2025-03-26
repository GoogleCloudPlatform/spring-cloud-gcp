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

package com.google.cloud.spring.autoconfigure.paramconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

/** Tests for Parameter Config bootstrap configuration. */
class GcpParamConfigBootstrapConfigurationTest {

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(GcpParamConfigBootstrapConfiguration.class))
          .withUserConfiguration(TestConfiguration.class);

  @Test
  void testParamConfigurationValueDefaultsAreAsExpected() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.paramconfig.enabled=true")
        .run(
            context -> {
              GcpParamConfigProperties config = context.getBean(GcpParamConfigProperties.class);
              assertThat(config.getName()).isEqualTo("application");
              assertThat(config.getProfile()).isEqualTo("default");
              assertThat(config.getLocation()).isEqualTo("global");
              assertThat(config.isEnabled()).isTrue();
            });
  }

  @Test
  void testParamConfigurationValuesAreCorrectlyLoaded() {
    this.contextRunner
        .withPropertyValues(
            "spring.application.name=myapp",
            "spring.cloud.gcp.paramconfig.enabled=true",
            "spring.cloud.gcp.paramconfig.project-id=pariah",
            "spring.cloud.gcp.paramconfig.location=us-central1")
        .run(
            context -> {
              GcpParamConfigProperties config = context.getBean(GcpParamConfigProperties.class);
              assertThat(config.getName()).isEqualTo("myapp");
              assertThat(config.getProfile()).isEqualTo("default");
              assertThat(config.getLocation()).isEqualTo("us-central1");
              assertThat(config.isEnabled()).isTrue();
              assertThat(config.getProjectId()).isEqualTo("pariah");
            });
  }

  @Test
  void testParamConfigurationValuesAreCorrectlyLoadedWithCustomProfile() {
    this.contextRunner
        .withPropertyValues(
            "spring.application.name=myapp",
            "spring.profiles.active=prod",
            "spring.cloud.gcp.paramconfig.enabled=true",
            "spring.cloud.gcp.paramconfig.project-id=pariah",
            "spring.cloud.gcp.paramconfig.location=us-central1")
        .run(
            context -> {
              GcpParamConfigProperties config = context.getBean(GcpParamConfigProperties.class);
              assertThat(config.getName()).isEqualTo("myapp");
              assertThat(config.getProfile()).isEqualTo("prod");
              assertThat(config.getLocation()).isEqualTo("us-central1");
              assertThat(config.isEnabled()).isTrue();
              assertThat(config.getProjectId()).isEqualTo("pariah");
            });
  }

  @Test
  void testParamConfigurationValuesAreCorrectlyLoadedWithMultiProfile() {
    this.contextRunner
        .withPropertyValues(
            "spring.application.name=myapp",
            "spring.profiles.active=dev,prod",
            "spring.cloud.gcp.paramconfig.enabled=true",
            "spring.cloud.gcp.paramconfig.project-id=pariah",
            "spring.cloud.gcp.paramconfig.location=us-central1")
        .run(
            context -> {
              GcpParamConfigProperties config = context.getBean(GcpParamConfigProperties.class);
              assertThat(config.getName()).isEqualTo("myapp");
              assertThat(config.getProfile()).isEqualTo("prod");
              assertThat(config.getLocation()).isEqualTo("us-central1");
              assertThat(config.isEnabled()).isTrue();
              assertThat(config.getProjectId()).isEqualTo("pariah");
            });
  }

  @Test
  void testParamConfigurationValuesAreCorrectlyLoadedWithParamConfigKeys() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.paramconfig.name=myapp",
            "spring.cloud.gcp.paramconfig.profile=dev,prod",
            "spring.cloud.gcp.paramconfig.enabled=true",
            "spring.cloud.gcp.paramconfig.project-id=pariah",
            "spring.cloud.gcp.paramconfig.location=us-central1")
        .run(
            context -> {
              GcpParamConfigProperties config = context.getBean(GcpParamConfigProperties.class);
              assertThat(config.getName()).isEqualTo("myapp");
              assertThat(config.getProfile()).isEqualTo("prod");
              assertThat(config.getLocation()).isEqualTo("us-central1");
              assertThat(config.isEnabled()).isTrue();
              assertThat(config.getProjectId()).isEqualTo("pariah");
            });
  }

  @Test
  void testParamConfigurationDisabled() {
    this.contextRunner.run(
        context ->
            assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
                .isThrownBy(() -> context.getBean(GcpParamConfigProperties.class)));
  }

  private static class TestConfiguration {

    @Bean
    public ParameterManagerClient parameterManagerClient() {
      return mock(ParameterManagerClient.class);
    }

    @Bean
    public GoogleParamConfigPropertySourceLocator googleParamConfigPropertySourceLocator() {
      return mock(GoogleParamConfigPropertySourceLocator.class);
    }
  }
}
