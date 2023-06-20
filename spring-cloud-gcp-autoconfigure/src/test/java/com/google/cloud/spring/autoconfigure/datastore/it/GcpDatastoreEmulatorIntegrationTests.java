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

package com.google.cloud.spring.autoconfigure.datastore.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.datastore.DatastoreRepositoriesAutoConfiguration;
import com.google.cloud.spring.autoconfigure.datastore.DatastoreTransactionManagerAutoConfiguration;
import com.google.cloud.spring.autoconfigure.datastore.GcpDatastoreAutoConfiguration;
import com.google.cloud.spring.autoconfigure.datastore.GcpDatastoreEmulatorAutoConfiguration;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.data.annotation.Id;

/**
 * Tests for Datastore Emulator integration with the datastore itself.
 *
 * @since 1.2
 */
@EnabledIfSystemProperty(named = "it.datastore", matches = "true")
class GcpDatastoreEmulatorIntegrationTests {

  @Test
  void testDatastoreEmulatorConfiguration() {
    DatastoreOptions.Builder builder = DatastoreOptions.newBuilder();

    new ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                GcpDatastoreAutoConfiguration.class,
                GcpContextAutoConfiguration.class,
                DatastoreTransactionManagerAutoConfiguration.class,
                DatastoreRepositoriesAutoConfiguration.class,
                GcpDatastoreEmulatorAutoConfiguration.class))
        .withUserConfiguration(TestConfiguration.class)
        .withPropertyValues(
            "spring.cloud.gcp.project-id=test-project",
            "spring.cloud.gcp.datastore.namespace=test-namespace",
            "spring.cloud.gcp.datastore.emulator.port=8181",
            "spring.cloud.gcp.datastore.emulator.enabled=true",
            "spring.cloud.gcp.datastore.emulator.consistency=0.9")
        .run(
            context -> {
              DatastoreTemplate datastore = context.getBean(DatastoreTemplate.class);
              Datastore datastoreClient =
                  (Datastore)
                      ((Supplier)
                              context.getBean(
                                  context
                                      .getBeanNamesForType(
                                          ResolvableType.forClassWithGenerics(
                                              Supplier.class, Datastore.class))[0]))
                          .get();
              GcpProjectIdProvider projectIdProvider = context.getBean(GcpProjectIdProvider.class);

              builder
                  .setServiceFactory(datastoreOptions -> datastoreClient)
                  .setProjectId(projectIdProvider.getProjectId());

              EmulatorEntityTest entity = new EmulatorEntityTest();
              entity.setProperty("property-test");

              datastore.save(entity);

              assertThat(entity.getId()).isNotNull();

              assertThat(datastore.findById(entity.getId(), EmulatorEntityTest.class).getProperty())
                  .isEqualTo("property-test");
            });

    Datastore datastore = builder.build().getService();

    EntityQuery query =
        Query.newEntityQueryBuilder()
            .setKind("RandomKind")
            .setFilter(StructuredQuery.PropertyFilter.eq("key", "value"))
            .build();

    assertThatExceptionOfType(DatastoreException.class).isThrownBy(() -> datastore.run(query));
  }

  /** Spring Boot config for tests. */
  @AutoConfigurationPackage
  static class TestConfiguration {

    @Bean
    public CredentialsProvider credentialsProvider() {
      return () -> mock(Credentials.class);
    }
  }

  /** Document to be stored on Datastore. An instance of `LocalDatastoreHelper` as a bean. */
  @com.google.cloud.spring.data.datastore.core.mapping.Entity
  static class EmulatorEntityTest {

    @Id private Long id;

    private String property;

    public void setId(Long id) {
      this.id = id;
    }

    Long getId() {
      return this.id;
    }

    void setProperty(String property) {
      this.property = property;
    }

    String getProperty() {
      return this.property;
    }
  }
}
