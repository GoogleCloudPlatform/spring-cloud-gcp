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

package com.google.cloud.spring.autoconfigure.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.spring.core.DefaultGcpEnvironmentProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpEnvironmentProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ContextConsumer;
import org.springframework.context.annotation.Bean;

/** Tests for the top-level context auto-configuration. */
class GcpContextAutoConfigurationTests {

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(GcpContextAutoConfiguration.class))
          .withUserConfiguration(TestConfiguration.class);

  @Test
  void testGetProjectIdProvider_withGcpProperties() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.projectId=tonberry")
        .run(
            context -> {
              GcpProjectIdProvider projectIdProvider = context.getBean(GcpProjectIdProvider.class);
              assertThat(projectIdProvider.getProjectId()).isEqualTo("tonberry");
            });
  }

  @Test
  void testGetProjectIdProvider_withoutGcpProperties() {
    this.contextRunner.run(
        context -> {
          GcpProjectIdProvider projectIdProvider = context.getBean(GcpProjectIdProvider.class);
          assertThat(projectIdProvider).isInstanceOf(DefaultGcpProjectIdProvider.class);
        });
  }

  @Test
  void testEnvironmentProvider() {
    this.contextRunner.run(
        context -> {
          GcpEnvironmentProvider environmentProvider =
              context.getBean(GcpEnvironmentProvider.class);
          assertThat(environmentProvider).isNotNull();
          assertThat(environmentProvider).isInstanceOf(DefaultGcpEnvironmentProvider.class);
        });
  }

  @Test
  void testGetProjectIdProviderBeanExistence_withGcpCoreEnabled() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.core.enabled=true")
        .run(checkNumberOfBeansOfTypeGcpProjectIdProvider(1));
  }

  @Test
  void testGetProjectIdProviderBeanExistence_withGcpCoreMissing() {
    this.contextRunner.run(checkNumberOfBeansOfTypeGcpProjectIdProvider(1));
  }

  @Test
  void testGetProjectIdProviderBeanExistence_withGcpCoreDisabled() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.core.enabled=false")
        .run(checkNumberOfBeansOfTypeGcpProjectIdProvider(0));
  }

  private ContextConsumer<AssertableApplicationContext>
      checkNumberOfBeansOfTypeGcpProjectIdProvider(int count) {
    return context -> assertThat(context.getBeansOfType(GcpProjectIdProvider.class)).hasSize(count);
  }

  private static class TestConfiguration {

    @Bean
    public CredentialsProvider googleCredentials() {
      return () -> mock(Credentials.class);
    }
  }
}
