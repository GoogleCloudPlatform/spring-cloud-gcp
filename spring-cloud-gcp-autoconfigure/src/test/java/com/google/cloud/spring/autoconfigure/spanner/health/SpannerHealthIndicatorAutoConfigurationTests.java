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

package com.google.cloud.spring.autoconfigure.spanner.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.spanner.GcpSpannerAutoConfiguration;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tests for Spanner health check auto-config.
 *
 * @since 2.0.6
 */
class SpannerHealthIndicatorAutoConfigurationTests {

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpSpannerAutoConfiguration.class,
                  GcpContextAutoConfiguration.class,
                  SpannerHealthIndicatorAutoConfiguration.class))
          .withUserConfiguration(TestConfiguration.class)
          .withPropertyValues(
              "spring.cloud.gcp.spanner.project-id=test-project",
              "spring.cloud.gcp.spanner.instance-id=testInstance",
              "spring.cloud.gcp.spanner.database=testDatabase");

  @Test
  void testSpannerHealthIndicatorCreated() {
    final String defaultQuery = "SELECT 1";
    this.contextRunner.run(
        context -> {
          SpannerHealthIndicator indicator = context.getBean(SpannerHealthIndicator.class);
          assertThat(indicator).isNotNull();
          assertThat(indicator)
              .hasFieldOrPropertyWithValue("validationStatement", Statement.of(defaultQuery));
        });
  }

  @Test
  void testSpannerHealthIndicatorCreatedWithQuery() {
    final String customQuery = "SELECT 2";
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.spanner.health.query=" + customQuery)
        .run(
            context -> {
              SpannerHealthIndicator indicator = context.getBean(SpannerHealthIndicator.class);
              assertThat(indicator).isNotNull();
              assertThat(indicator)
                  .hasFieldOrPropertyWithValue("validationStatement", Statement.of(customQuery));
            });
  }

  @Test
  void testSpannerHealthIndicatorNotCreated() {

    ApplicationContextRunner contextRunnerNew = this.contextRunner.withPropertyValues("management.health.spanner.enabled=false");
    assertThatThrownBy(() -> contextRunnerNew
        .run(context -> context.getBean(SpannerHealthIndicator.class)))
        .isInstanceOf(NoSuchBeanDefinitionException.class);
  }

  /** Spring Boot config for tests. */
  @Configuration
  static class TestConfiguration {

    @Bean
    public CredentialsProvider credentialsProvider() {
      return () -> mock(Credentials.class);
    }

    @Bean
    public Gson gson() {
      return new Gson();
    }
  }
}
