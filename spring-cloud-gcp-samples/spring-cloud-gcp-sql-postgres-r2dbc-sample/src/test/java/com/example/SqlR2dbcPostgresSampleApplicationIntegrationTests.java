/*
 * Copyright 2021-2022 the original author or authors.
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

package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** Simple integration test to verify the SQL sample application with Postgres. */
@AutoConfigureTestRestTemplate
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {SqlApplication.class},
    properties = {
      "spring.cloud.gcp.sql.databaseName=code_samples_test_r2dbc_db",
      "spring.cloud.gcp.sql.instanceConnectionName=${GCLOUD_PROJECT}:us-central1:testpostgres",
      "spring.cloud.gcp.sql.enable-iam.auth=true"
    })
@Import(SqlR2dbcPostgresSampleApplicationIntegrationTests.SchemaConfiguration.class)
@EnabledIfSystemProperty(named = "it.cloudsql", matches = "true")
class SqlR2dbcPostgresSampleApplicationIntegrationTests {

  // Unique, timestamped schema name to isolate this test run from concurrent CI jobs.
  // The timestamp prefix allows the 24-hour janitor sweep to identify and prune orphaned schemas.
  private static final String SCHEMA_NAME =
      "test_schema_r2dbc_"
          + System.currentTimeMillis()
          + "_"
          + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

  private static ConnectionFactory staticConnectionFactory;

  private static boolean cleanedOldSchemas;

  /**
   * Configures R2DBC's schema property dynamically.
   *
   * <p>Setting {@code spring.r2dbc.properties.schema} instructs the PostgreSQL R2DBC driver to set
   * {@code search_path} to {@code SCHEMA_NAME} on every connection. Unqualified table references
   * in {@code schema.sql}, {@code data.sql}, and {@code cleanup.sql} execute inside our isolated
   * schema.
   */
  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.r2dbc.properties.schema", () -> SCHEMA_NAME);
  }

  /**
   * Provisions the isolated schema on the {@link ConnectionFactory} after it is initialized and
   * before {@link org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer} runs
   * {@code schema.sql}.
   */
  @TestConfiguration
  static class SchemaConfiguration {
    @Bean
    static BeanPostProcessor schemaCreatorPostProcessor() {
      return new BeanPostProcessor() {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
          if (bean instanceof ConnectionFactory connectionFactory) {
            staticConnectionFactory = connectionFactory;
            Mono.from(connectionFactory.create())
                .flatMap(
                    connection ->
                        Flux.from(
                                connection
                                    .createStatement("CREATE SCHEMA IF NOT EXISTS " + SCHEMA_NAME)
                                    .execute())
                            .flatMap(Result::getRowsUpdated)
                            .then(Mono.from(connection.close())))
                .block();
          }
          return bean;
        }
      };
    }
  }

  @Autowired private TestRestTemplate testRestTemplate;

  /**
   * Janitor sweep: queries the PostgreSQL metadata catalog for test schemas created more than 24
   * hours ago and drops them.
   */
  @BeforeEach
  void cleanOldSchemas() {
    if (cleanedOldSchemas || staticConnectionFactory == null) {
      return;
    }
    cleanedOldSchemas = true;
    long cutoff = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24);
    try {
      Mono.from(staticConnectionFactory.create())
          .flatMap(
              conn ->
                  Flux.from(
                          conn.createStatement(
                                  "SELECT schema_name FROM information_schema.schemata WHERE"
                                      + " schema_name LIKE 'test_schema_r2dbc_%'")
                              .execute())
                      .flatMap(
                          result ->
                              result.map(
                                  (row, rowMetadata) -> row.get("schema_name", String.class)))
                      .collectList()
                      .flatMap(
                          schemas -> {
                            Mono<Void> chain = Mono.empty();
                            for (String schema : schemas) {
                              if (schema.equals(SCHEMA_NAME)) {
                                continue;
                              }
                              String[] parts = schema.split("_");
                              if (parts.length >= 4) {
                                try {
                                  long timestamp = Long.parseLong(parts[3]);
                                  if (timestamp < cutoff) {
                                    chain =
                                        chain.then(
                                            Flux.from(
                                                    conn.createStatement(
                                                            "DROP SCHEMA IF EXISTS "
                                                                + schema
                                                                + " CASCADE")
                                                        .execute())
                                                .flatMap(Result::getRowsUpdated)
                                                .then());
                                  }
                                } catch (NumberFormatException ignored) {
                                  // ignore malformed schema names
                                }
                              }
                            }
                            return chain;
                          })
                      .then(Mono.from(conn.close())))
          .block();
    } catch (Exception ignored) {
      // Ignored; janitor in future test runs will clean up schemas older than 24 hours
    }
  }

  /** Best-effort teardown to drop the isolated schema upon test completion. */
  @AfterAll
  static void teardownSchema() {
    if (staticConnectionFactory != null && SCHEMA_NAME != null) {
      try {
        Mono.from(staticConnectionFactory.create())
            .flatMap(
                conn ->
                    Flux.from(
                            conn.createStatement(
                                    "DROP SCHEMA IF EXISTS " + SCHEMA_NAME + " CASCADE")
                                .execute())
                        .flatMap(Result::getRowsUpdated)
                        .then(Mono.from(conn.close())))
            .block();
      } catch (Exception ignored) {
        // Ignored; janitor in future test runs will clean up schemas older than 24 hours
      }
    }
  }

  @Test
  void testSqlRowsAccess() {
    ResponseEntity<String> result =
        this.testRestTemplate.exchange(
            "/getTuples", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    assertThat(result.getBody())
        .isEqualTo(
            "[luisao@example.com, Anderson, Silva][jonas@example.com, Jonas,"
                + " Goncalves][fejsa@example.com, Ljubomir, Fejsa]");
  }
}
