/*
 * Copyright 2017-2018 the original author or authors.
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
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.auth.Credentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.data.spanner.core.SpannerOperations;
import com.google.cloud.spring.data.spanner.core.SpannerTransactionManager;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.threeten.bp.Duration;

/** Tests for Spanner auto-config. */
class GcpSpannerAutoConfigurationTests {

  /** Mock Gson object for use in configuration. */
  public static Gson MOCK_GSON = mock(Gson.class);

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpSpannerAutoConfiguration.class,
                  GcpContextAutoConfiguration.class,
                  SpannerTransactionManagerAutoConfiguration.class,
                  SpannerRepositoriesAutoConfiguration.class))
          .withUserConfiguration(TestConfiguration.class)
          .withPropertyValues(
              "spring.cloud.gcp.spanner.project-id=test-project",
              "spring.cloud.gcp.spanner.instance-id=testInstance",
              "spring.cloud.gcp.spanner.database=testDatabase");

  @Test
  void testSpannerOperationsCreated() {
    this.contextRunner.run(
        context -> {
          assertThat(context.getBean(SpannerOperations.class)).isNotNull();
        });
  }

  @Test
  void testTestRepositoryCreated() {
    this.contextRunner.run(
        context -> {
          assertThat(context.getBean(TestRepository.class)).isNotNull();
        });
  }

  @Test
  void testUserGsonBean() {
    this.contextRunner.run(
        context -> {
          Gson gsonBean = context.getBean(Gson.class);
          assertThat(gsonBean).isSameAs(MOCK_GSON);
          assertThat(context.getBean(SpannerMappingContext.class).getGson()).isSameAs(MOCK_GSON);
        });
  }

  @Test
  void testDatabaseAdminClientCreated() {
    this.contextRunner.run(
        context -> {
          assertThat(context.getBean(SpannerDatabaseAdminTemplate.class)).isNotNull();
        });
  }

  @Test
  void testSchemaUtilsCreated() {
    this.contextRunner.run(
        context -> {
          assertThat(context.getBean(SpannerSchemaUtils.class)).isNotNull();
        });
  }

  @Test
  void testIdConverterCreated() {
    this.contextRunner.run(
        context -> {
          BackendIdConverter idConverter = context.getBean(BackendIdConverter.class);
          assertThat(idConverter).isNotNull();
          assertThat(idConverter).isInstanceOf(SpannerKeyIdConverter.class);
        });
  }

  @Test
  void spannerTransactionManagerCreated() {
    this.contextRunner.run(
        context -> {
          SpannerTransactionManager transactionManager =
              context.getBean(SpannerTransactionManager.class);
          assertThat(transactionManager).isNotNull();
          assertThat(transactionManager).isInstanceOf(SpannerTransactionManager.class);
        });
  }

  @Test
  void testIdConverterNotCreated() {
    this.contextRunner
        .withClassLoader(new FilteredClassLoader("org.springframework.data.rest.webmvc.spi"))
        .run(context -> assertThat(context.getBeansOfType(BackendIdConverter.class)).isEmpty());
  }

  @Test
  void testSpannerCustomizerProvided() {
    Duration duration = Duration.ofSeconds(42);
    this.contextRunner
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

  /** Spring Boot config for tests. */
  @AutoConfigurationPackage
  static class TestConfiguration {

    /** Mock bean for credentials provider. */
    @Bean
    public CredentialsProvider credentialsProvider() {
      return () -> mock(Credentials.class);
    }

    /** Mock bean for Gson. */
    @Bean
    public Gson customGson() {
      return MOCK_GSON;
    }
  }
}
