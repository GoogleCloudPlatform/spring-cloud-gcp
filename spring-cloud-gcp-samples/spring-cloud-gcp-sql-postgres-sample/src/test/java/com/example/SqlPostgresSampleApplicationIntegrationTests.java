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

package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ClassUtils;

/** Simple integration test to verify the SQL sample application with Postgres. */
@EnabledIfSystemProperty(named = "it.cloudsql", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {SqlApplication.class},
    properties = {
      "spring.cloud.gcp.sql.database-name=code_samples_test_db",
      "spring.cloud.gcp.sql.instance-connection-name=${GCLOUD_PROJECT}:us-central1:testpostgres",
      "spring.datasource.username=postgres",
      "spring.sql.init.continue-on-error=true",
      "spring.sql.init.mode=always"
    })
class SqlPostgresSampleApplicationIntegrationTests {

  private static final String SCHEMA_NAME =
      "test_schema_"
          + System.currentTimeMillis()
          + "_"
          + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

  private static boolean cleanedOldSchemas;

  private static JdbcTemplate staticJdbcTemplate;

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.datasource.hikari.connection-init-sql",
        () ->
            "CREATE SCHEMA IF NOT EXISTS "
                + SCHEMA_NAME
                + "; SET search_path TO "
                + SCHEMA_NAME
                + ", public;");
  }

  @Autowired private TestRestTemplate testRestTemplate;

  @Autowired
  void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    staticJdbcTemplate = jdbcTemplate;
  }

  @BeforeEach
  void cleanOldSchemas() {
    if (cleanedOldSchemas || staticJdbcTemplate == null) {
      return;
    }
    cleanedOldSchemas = true;
    long cutoff = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24);
    List<String> schemas =
        staticJdbcTemplate.queryForList(
            "SELECT schema_name FROM information_schema.schemata WHERE schema_name LIKE 'test_schema_%'",
            String.class);
    for (String schema : schemas) {
      if (schema.equals(SCHEMA_NAME)) {
        continue;
      }
      String[] parts = schema.split("_");
      if (parts.length >= 3) {
        try {
          long timestamp = Long.parseLong(parts[2]);
          if (timestamp < cutoff) {
            staticJdbcTemplate.execute("DROP SCHEMA IF EXISTS " + schema + " CASCADE");
          }
        } catch (NumberFormatException ignored) {
          // ignore malformed schema names
        }
      }
    }
  }

  @AfterAll
  static void teardownSchema() {
    if (staticJdbcTemplate != null && SCHEMA_NAME != null) {
      try {
        staticJdbcTemplate.execute("DROP SCHEMA IF EXISTS " + SCHEMA_NAME + " CASCADE");
      } catch (Exception ignored) {
        // Ignored; janitor in future test runs will clean up schemas older than 24 hours
      }
    }
  }

  @Test
  void testSqlRowsAccess() {
    ResponseEntity<List<String>> result =
        this.testRestTemplate.exchange(
            "/getTuples", HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {});

    assertThat(result.getBody())
        .containsExactlyInAnyOrder(
            "[luisao@example.com, Anderson, Silva]",
            "[jonas@example.com, Jonas, Goncalves]",
            "[fejsa@example.com, Ljubomir, Fejsa]");
  }

  @Test
  void testNoAllyDbLoaded() {
    assertThat(ClassUtils.isPresent("com.google.cloud.alloydb.SocketFactory", null))
        .isFalse();
  }
}
