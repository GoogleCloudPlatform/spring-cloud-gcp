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

package com.google.cloud.spring.autoconfigure.datastore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.cloud.NoCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.datastore.health.DatastoreHealthIndicator;
import com.google.cloud.spring.autoconfigure.datastore.health.DatastoreHealthIndicatorAutoConfiguration;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.data.datastore.core.DatastoreOperations;
import com.google.cloud.spring.data.datastore.core.DatastoreTransactionManager;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;

/** Tests for Datastore auto-config. */
class GcpDatastoreAutoConfigurationTests {

  /** Mock datastore for use in configuration. */
  public static Datastore MOCK_CLIENT = mock(Datastore.class);

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpDatastoreAutoConfiguration.class,
                  GcpContextAutoConfiguration.class,
                  DatastoreTransactionManagerAutoConfiguration.class,
                  DatastoreRepositoriesAutoConfiguration.class,
                  DatastoreHealthIndicatorAutoConfiguration.class))
          .withUserConfiguration(TestConfiguration.class)
          .withPropertyValues(
              "spring.cloud.gcp.datastore.project-id=test-project",
              "spring.cloud.gcp.datastore.database-id=test-database",
              "spring.cloud.gcp.datastore.namespace=testNamespace",
              "spring.cloud.gcp.datastore.host=localhost:8081",
              "management.health.datastore.enabled=false");

  @Test
  void testUserDatastoreBean() {
    ApplicationContextRunner runner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GcpDatastoreAutoConfiguration.class))
            .withUserConfiguration(TestConfigurationWithDatastoreBean.class)
            .withPropertyValues(
                "spring.cloud.gcp.datastore.project-id=test-project",
                "spring.cloud.gcp.datastore.database-id=test-database",
                "spring.cloud.gcp.datastore.namespace=testNamespace",
                "spring.cloud.gcp.datastore.host=localhost:8081",
                "management.health.datastore.enabled=false");

    runner.run(
        context -> {
          assertThat(getDatastoreBean(context)).isSameAs(MOCK_CLIENT);
        });
  }

  @Test
  void testUserDatastoreBeanNamespace() {
    ApplicationContextRunner runner =
        new ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    GcpDatastoreAutoConfiguration.class, GcpContextAutoConfiguration.class))
            .withUserConfiguration(TestConfigurationWithDatastoreBeanNamespaceProvider.class)
            .withPropertyValues(
                "spring.cloud.gcp.datastore.project-id=test-project",
                "spring.cloud.gcp.datastore.database-id=test-database",
                "spring.cloud.gcp.datastore.namespace=testNamespace",
                "spring.cloud.gcp.datastore.host=localhost:8081",
                "management.health.datastore.enabled=false");

    assertThatThrownBy(() -> runner.run(context -> getDatastoreBean(context)))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("failed to start");
  }

  @Test
  void testDatastoreSimpleClient() {
    this.contextRunner.run(context -> assertThat(context.getBean(Datastore.class)).isNotNull());
  }

  @Test
  void testDatastoreOptionsCorrectlySet() {
    this.contextRunner.run(
        context -> {
          DatastoreOptions datastoreOptions = getDatastoreBean(context).getOptions();
          assertThat(datastoreOptions.getProjectId()).isEqualTo("test-project");
          assertThat(datastoreOptions.getDatabaseId()).isEqualTo("test-database");
          assertThat(datastoreOptions.getNamespace()).isEqualTo("testNamespace");
          assertThat(datastoreOptions.getHost()).isEqualTo("localhost:8081");
        });
  }

  @Test
  void testDatastoreEmulatorCredentialsConfig() {
    this.contextRunner.run(
        context -> {
          CredentialsProvider defaultCredentialsProvider =
              context.getBean(CredentialsProvider.class);
          assertThat(defaultCredentialsProvider).isNotInstanceOf(NoCredentialsProvider.class);

          DatastoreOptions datastoreOptions = getDatastoreBean(context).getOptions();
          assertThat(datastoreOptions.getCredentials()).isInstanceOf(NoCredentials.class);
        });
  }

  @Test
  void testDatastoreOperationsCreated() {
    this.contextRunner.run(
        context -> assertThat(context.getBean(DatastoreOperations.class)).isNotNull());
  }

  @Test
  void testTestRepositoryCreated() {
    this.contextRunner.run(
        context -> assertThat(context.getBean(TestRepository.class)).isNotNull());
  }

  @Test
  void testIdConverterCreated() {
    this.contextRunner.run(
        context -> {
          BackendIdConverter idConverter = context.getBean(BackendIdConverter.class);
          assertThat(idConverter).isNotNull();
          assertThat(idConverter).isInstanceOf(DatastoreKeyIdConverter.class);
        });
  }

  @Test
  void datastoreTransactionManagerCreated() {
    this.contextRunner.run(
        context -> {
          DatastoreTransactionManager transactionManager =
              context.getBean(DatastoreTransactionManager.class);
          assertThat(transactionManager).isNotNull();
          assertThat(transactionManager).isInstanceOf(DatastoreTransactionManager.class);
        });
  }

  @Test
  void testDatastoreHealthIndicatorNotCreated() {
    this.contextRunner.run(
        context ->
            assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
                .isThrownBy(() -> context.getBean(DatastoreHealthIndicator.class)));
  }

  private Datastore getDatastoreBean(ApplicationContext context) {
    return (Datastore)
        ((Supplier)
                context.getBean(
                    context
                        .getBeanNamesForType(
                            ResolvableType.forClassWithGenerics(Supplier.class, Datastore.class))[
                        0]))
            .get();
  }

  /** Spring Boot config for tests. */
  @AutoConfigurationPackage
  static class TestConfiguration {

    @Bean
    public CredentialsProvider credentialsProvider() {
      return () -> NoCredentials.getInstance();
    }
  }

  /** Spring Boot config for tests with custom Datastore Bean. */
  @Configuration
  static class TestConfigurationWithDatastoreBean {

    @Bean
    public CredentialsProvider credentialsProvider() {
      return () -> NoCredentials.getInstance();
    }

    @Bean
    public GcpProjectIdProvider gcpProjectIdProvider() {
      return () -> "project123";
    }

    @Bean
    public Datastore datastore() {
      return MOCK_CLIENT;
    }
  }

  /** Spring Boot config for tests with custom Datastore Bean. */
  @Configuration
  static class TestConfigurationWithDatastoreBeanNamespaceProvider {

    @Bean
    public CredentialsProvider credentialsProvider() {
      return () -> NoCredentials.getInstance();
    }

    @Bean
    public Datastore datastore() {
      return MOCK_CLIENT;
    }

    @Bean
    public DatastoreNamespaceProvider datastoreNamespaceProvider() {
      return () -> "blah";
    }
  }
}
