/*
 * Copyright 2024 Google LLC
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
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Simple integration test to verify the AlloyDB sample application with Postgres. */
@EnabledIfSystemProperty(named = "it.alloydb", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {AlloyDbApplication.class},
    properties = {
      "spring.cloud.gcp.alloydb.database-name=code_samples_test_db",
      "spring.cloud.gcp.alloydb.instance-connection-uri=projects/${GCLOUD_PROJECT}/locations/us-central1/clusters/testcluster/instances/testpostgres",
      "spring.datasource.username=postgres",
      "spring.sql.init.continue-on-error=true"
    })
class AlloyDbSampleApplicationIntegrationTests {

  @Autowired private TestRestTemplate testRestTemplate;

  @Autowired private JdbcTemplate jdbcTemplate;

  @AfterEach
  void clearTable() {
    this.jdbcTemplate.execute("DROP TABLE IF EXISTS users");
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
}
