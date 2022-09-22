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

package com.google.cloud.spring.autoconfigure.spanner;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.threeten.bp.Duration;

class GcpSpannerEmulatorAutoConfigurationTests {

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpSpannerEmulatorAutoConfiguration.class,
                  GcpSpannerAutoConfiguration.class,
                  GcpContextAutoConfiguration.class))
          .withUserConfiguration(TestConfiguration.class)
          .withPropertyValues("spring.cloud.gcp.spanner.project-id=test-project");

  @Test
  void testEmulatorAutoConfigurationEnabled() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.spanner.emulator.enabled=true")
        .run(
            context -> {
              SpannerOptions spannerOptions = context.getBean(SpannerOptions.class);
              assertThat(spannerOptions.getEndpoint()).isEqualTo("localhost:9010");

              assertThat(context.getBean(CredentialsProvider.class).getCredentials())
                  .isEqualTo(NoCredentials.getInstance());
            });
  }

  @Test
  void testEmulatorAutoConfigurationEnabledCustomHostPort() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.spanner.emulator.enabled=true",
            "spring.cloud.gcp.spanner.emulator-host=localhost:9090")
        .run(
            context -> {
              SpannerOptions spannerOptions = context.getBean(SpannerOptions.class);
              assertThat(spannerOptions.getEndpoint()).isEqualTo("localhost:9090");
            });
  }

  @Test
  void testEmulatorAutoConfigurationDisabled() {
    this.contextRunner
        .withUserConfiguration(GcpSpannerAutoConfigurationTests.TestConfiguration.class)
        .run(
            context -> {
              SpannerOptions spannerOptions = context.getBean(SpannerOptions.class);
              assertThat(spannerOptions.getEndpoint()).isEqualTo("spanner.googleapis.com:443");
            });
  }

  @Test
  void testEmulatorSpannerCustomizerProvided() {
    Duration duration = Duration.ofSeconds(42);
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.spanner.emulator.enabled=true")
        .withBean(
            SpannerOptionsCustomizer.class,
            () -> {
              return builder -> {
                builder
                    .getSpannerStubSettingsBuilder()
                    .executeSqlSettings()
                    .setRetrySettings(
                        RetrySettings.newBuilder().setMaxRetryDelay(duration).build());
              };
            })
        .run(
            context -> {
              SpannerOptions spannerOptions = context.getBean(SpannerOptions.class);
              assertThat(spannerOptions).isNotNull();
              assertThat(
                      spannerOptions
                          .getSpannerStubSettings()
                          .executeSqlSettings()
                          .getRetrySettings()
                          .getMaxRetryDelay())
                  .isEqualTo(duration);
              // unchanged options stay at their default values
              SpannerOptions defaultSpannerOptions =
                  SpannerOptions.newBuilder()
                      .setProjectId("unused")
                      .setCredentials(NoCredentials.getInstance())
                      .build();
              assertThat(spannerOptions.getNumChannels())
                  .isEqualTo(defaultSpannerOptions.getNumChannels());
            });
  }

  /**
   * Spring Boot config for tests.
   */
  @Configuration
  static class TestConfiguration {

    @Bean
    public Gson gson() {
      return new Gson();
    }
  }
}
