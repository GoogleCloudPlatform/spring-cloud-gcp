/*
 * Copyright 2019 Google LLC
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

package com.google.cloud.spanner.r2dbc.it;

import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.DRIVER_NAME;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.URL;
import static com.google.cloud.spanner.r2dbc.it.SpannerQueryUtil.executeReadQuery;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.ServiceOptions;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.R2dbcNonTransientException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class SpannerDdlIT {

  private static final Logger logger = LoggerFactory.getLogger(SpannerDdlIT.class);

  private static final ConnectionFactory connectionFactory =
      ConnectionFactories.get(ConnectionFactoryOptions.builder()
          .option(DRIVER, DRIVER_NAME)
          .option(URL,
              "r2dbc:spanner://spanner.googleapis.com:443/projects/"
                  + ServiceOptions.getDefaultProjectId()
                  + "/instances/reactivetest/databases/testdb")
          .build());

  /**
   * Setup the testing environment for DDL integration tests.
   */
  @Before
  public void setupEnvironment() {
    try {
      Mono.from(connectionFactory.create())
          .delayUntil(c -> c.createStatement("DROP TABLE PRESIDENTS").execute())
          .block();
    } catch (Exception e) {
      logger.debug("Did not drop PRESIDENTS table.", e);
    }
  }

  @Test
  public void testDdlErrorPropagation() {
    assertThat(listTables()).doesNotContain("PRESIDENTS");

    assertThatThrownBy(() ->
        Mono.from(connectionFactory.create())
            .delayUntil(c -> c.createStatement("DROP TABLE PRESIDENTS").execute())
            .block())
        .isInstanceOf(R2dbcNonTransientException.class)
        .hasMessageContaining("Table not found: PRESIDENTS");
  }

  @Test
  public void testCreateAndDrop() {
    assertThat(listTables()).doesNotContain("PRESIDENTS");

    Mono.from(connectionFactory.create())
        .delayUntil(c -> c.createStatement(
            "CREATE TABLE PRESIDENTS ("
                + "  NAME STRING(256) NOT NULL,"
                + "  START_YEAR INT64 NOT NULL"
                + ") PRIMARY KEY (NAME)")
            .execute())
        .block();
    assertThat(listTables()).contains("PRESIDENTS");

    Mono.from(connectionFactory.create())
        .delayUntil(c -> c.createStatement("DROP TABLE PRESIDENTS").execute())
        .block();
    assertThat(listTables()).doesNotContain("PRESIDENTS");
  }

  private List<String> listTables() {
    return executeReadQuery(
        connectionFactory,
        "SELECT table_name "
            + " FROM information_schema.tables"
            + " WHERE table_catalog = '' and table_schema = ''",
        (row, rowMetadata) -> row.get(0, String.class));
  }
}
