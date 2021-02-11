/*
 * Copyright 2019-2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spanner.r2dbc.springdata.it;

import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;

import com.google.cloud.ServiceOptions;
import com.google.cloud.spanner.r2dbc.springdata.it.entities.President;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Integration tests for the Spring Data R2DBC dialect.
 *
 * <p>By default, the test is configured to run tests in the `reactivetest` instance on the
 * `testdb` database. This can be configured by overriding the `spanner.instance` and
 * `spanner.database` system properties.
 */
class SpannerR2dbcDialectIT {

  private static final Logger logger = LoggerFactory.getLogger(SpannerR2dbcDialectIT.class);

  private static final String DRIVER_NAME = "spanner";

  private static final String TEST_INSTANCE =
      System.getProperty("spanner.instance", "reactivetest");

  private static final String TEST_DATABASE =
      System.getProperty("spanner.database", "testdb");

  private static final ConnectionFactory connectionFactory =
      ConnectionFactories.get(ConnectionFactoryOptions.builder()
          .option(Option.valueOf("project"), ServiceOptions.getDefaultProjectId())
          .option(DRIVER, DRIVER_NAME)
          .option(INSTANCE, TEST_INSTANCE)
          .option(DATABASE, TEST_DATABASE)
          .build());

  private DatabaseClient databaseClient;

  /**
   * Initializes the integration test environment for the Spanner R2DBC dialect.
   */
  @BeforeEach
  public void initializeTestEnvironment() {
    Connection connection = Mono.from(connectionFactory.create()).block();

    this.databaseClient = DatabaseClient.create(connectionFactory);

    if (SpannerTestUtils.tableExists(connection, "PRESIDENT")) {
      this.databaseClient.execute("DROP TABLE PRESIDENT")
          .fetch()
          .rowsUpdated()
          .block();
    }

    this.databaseClient.execute(
        "CREATE TABLE PRESIDENT ("
            + "  NAME STRING(256) NOT NULL,"
            + "  START_YEAR INT64 NOT NULL"
            + ") PRIMARY KEY (NAME)")
        .fetch()
        .rowsUpdated()
        .block();
  }

  @Test
  void testReadWrite() {
    insertPresident(new President("Bill Clinton", 1992));

    this.databaseClient.select()
        .from(President.class)
        .fetch()
        .first()
        .as(StepVerifier::create)
        .expectNextMatches(
            president -> president.getName().equals("Bill Clinton")
                && president.getStartYear() == 1992)
        .verifyComplete();
  }

  @Test
  void testLimitOffsetSupport() {
    insertPresident(new President("Bill Clinton", 1992));
    insertPresident(new President("Joe Smith", 1996));
    insertPresident(new President("Bob", 2000));
    insertPresident(new President("Hello", 2004));
    insertPresident(new President("George Washington", 2008));

    this.databaseClient.select()
        .from(President.class)
        // Get the page at index 1; 2 elements per page.
        .orderBy(Sort.by(Direction.ASC, "name"))
        .page(PageRequest.of(0, 2))
        .fetch()
        .all()
        .as(StepVerifier::create)
        .expectNextMatches(president -> president.getName().equals("Bill Clinton"))
        .expectNextMatches(president -> president.getName().equals("Bob"))
        .verifyComplete();
  }

  @Test
  void testRowMap() {
    insertPresident(new President("Bill Clinton", 1992));
    insertPresident(new President("Joe Smith", 1996));
    insertPresident(new President("Bob", 2000));
    insertPresident(new President("Hello", 2004));
    insertPresident(new President("George Washington", 2008));

    this.databaseClient.select()
        .from(President.class)
        .orderBy(Sort.by(Direction.ASC, "name"))
        .map(row -> (String) row.get("name"))
        .all()
        .as(StepVerifier::create)
        .expectNext(
            "Bill Clinton", "Bob", "George Washington", "Hello", "Joe Smith")
        .verifyComplete();
  }

  private void insertPresident(President president) {
    this.databaseClient
        .insert()
        .into(President.class)
        .using(president)
        .then()
        .as(StepVerifier::create)
        .verifyComplete();
  }
}
