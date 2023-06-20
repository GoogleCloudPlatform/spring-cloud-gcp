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

/**
 * Simple integration test to verify the SQL sample application. This test will use the properties
 * set in resources/application.properties.
 *
 * <p>Run with: mvn -Dit.cloudsql test
 */
//Please use "-Dit.cloudsql=true" to enable the tests
@EnabledIfSystemProperty(named = "it.cloudsql", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {SqlApplication.class})
class SqlMysqlSampleApplicationIntegrationTests {

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
