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

package com.google.cloud.spanner.r2dbc.it;

import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.DRIVER_NAME;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.test.TestKit.TestStatement.INSERT_TWO_COLUMNS;
import static io.r2dbc.spi.test.TestKit.TestStatement.INSERT_VALUE200;
import static io.r2dbc.spi.test.TestKit.TestStatement.SELECT_VALUE;
import static io.r2dbc.spi.test.TestKit.TestStatement.SELECT_VALUE_TWO_COLUMNS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.google.cloud.ServiceOptions;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.test.TestKit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Cloud Spanner R2DBC TCK test implementation.
 */
public class SpannerTestKit implements TestKit<String> {

  private static final String DISABLE_UNSUPPORTED_FUNCTIONALITY =
      "Functionality not supported in Cloud Spanner";

  private static final ConnectionFactory connectionFactory =
      ConnectionFactories.get(ConnectionFactoryOptions.builder()
          .option(Option.valueOf("project"), ServiceOptions.getDefaultProjectId())
          .option(DRIVER, DRIVER_NAME)
          .option(INSTANCE, DatabaseProperties.INSTANCE)
          .option(DATABASE, DatabaseProperties.DATABASE)
          .option(Option.valueOf("client-implementation"), "grpc")
          .build());

  private static final Logger logger = LoggerFactory.getLogger(SpannerTestKit.class);

  private static JdbcOperations jdbcOperations;

  private static DatabaseAdminClient dbAdminClient;

  @BeforeAll
  static void setUp() {

    // only execute() is needed.
    jdbcOperations = mock(JdbcOperations.class);

    doAnswer(invocation -> {
      executeDml(c -> c.createStatement(invocation.getArgument(0)));
      return null;
    }).when(jdbcOperations).execute((String) any());

    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    dbAdminClient = spanner.getDatabaseAdminClient();

    DatabaseId id = DatabaseId.of(
        options.getProjectId(), DatabaseProperties.INSTANCE, DatabaseProperties.DATABASE);
    createTableIfNeeded(id, "test", " ( value INT64 ) PRIMARY KEY (value)");
    createTableIfNeeded(
        id, "test_two_column", " ( col1 INT64, col2 STRING(MAX) )  PRIMARY KEY (col1)");
    createTableIfNeeded(id, "blob_test", " ( value BYTES(MAX) )  PRIMARY KEY (value)");
    createTableIfNeeded(id, "clob_test", " ( value BYTES(MAX) )  PRIMARY KEY (value)");
  }


  private static void createTableIfNeeded(DatabaseId id, String tableName, String definition) {
    Connection connection = Mono.from(connectionFactory.create()).block();

    Boolean tableExists = false;

    try {
      tableExists = Flux.from(connection.createStatement(
              "SELECT table_name FROM information_schema.tables WHERE table_name = @name")
                  .bind("name", tableName)
                  .execute())
          .flatMap(result -> result.map((r, m) -> r))
          .hasElements()
          .block();
    } finally {
      Mono.from(connection.close()).block();
    }

    if (!tableExists) {
      logger.info("Table " + tableName + " does not exist; creating");
      try {
        dbAdminClient.updateDatabaseDdl(
                id.getInstanceId().getInstance(),
                id.getDatabase(),
                Collections.singletonList("CREATE TABLE " + tableName + definition),
                null)
            .get();
      } catch (Exception e) {
        logger.info("Couldn't run DDL", e);
      }
    }
  }

  private static void executeDml(Function<Connection, Statement> statementFunc) {
    Mono.from(connectionFactory.create())
        .delayUntil(c -> c.beginTransaction())
        .delayUntil(c -> Flux.from(statementFunc.apply(c).execute())
            .flatMapSequential(r -> Mono.from(r.getRowsUpdated())))
        .delayUntil(c -> c.commitTransaction())
        .delayUntil(c -> c.close())
        .block();
  }

  private static <T> Mono<T> close(Connection connection) {
    return Mono.from(connection
        .close())
        .then(Mono.empty());
  }

  @Override
  public ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

  // we don't need to create tables because it is slow. we do it upfront.
  @Override
  @BeforeEach
  public void createTable() {
    // clean up tables (doesn't actually drop tables in override)
    dropTable();
  }

  // we don't actually want to drop them because recreating is VERY slow.
  @Override
  @AfterEach
  public void dropTable() {
    getJdbcOperations().execute("DELETE FROM test WHERE 1=1");
    getJdbcOperations().execute("DELETE FROM test_two_column WHERE 1=1");
    getJdbcOperations().execute("DELETE FROM blob_test WHERE 1=1");
    getJdbcOperations().execute("DELETE FROM clob_test WHERE 1=1");
  }

  @Override
  public String getCreateTableWithAutogeneratedKey() {
    throw new UnsupportedOperationException("Cloud Spanner doesn't have auto-generated keys.");
  }

  @Override
  public String getIdentifier(int index) {
    return getPlaceholder(index).substring(1);
  }

  @Override
  public String getPlaceholder(int index) {
    return "@Param" + index;
  }

  @Override
  public JdbcOperations getJdbcOperations() {
    return jdbcOperations;
  }

  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Override
  @Test
  public void transactionRollback() {
    /*
    This test is not supported by Cloud Spanner. It tests that a row written during a transaction
    is visible to read queries during that transaction.

    Cloud Spanner transactions always only let you read the starting-state regardless of writes
    during the transaction.
     */
  }

  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Override
  @Test
  public void bindFails() {
    /*
    This test focuses on various corner cases for position-binding, which is unsupported.
     */
  }

  /* Overrides parent test because
   * 1) column names are case-sensitive in Spanner
   * 2) Spanner returns Long instead of Integer
   */
  @Override
  @Test
  public void duplicateColumnNames() {
    getJdbcOperations().execute(expand(INSERT_TWO_COLUMNS));

    Mono.from(getConnectionFactory().create())
        .flatMapMany(connection -> Flux.from(connection

            .createStatement(expand(SELECT_VALUE_TWO_COLUMNS))
            .execute())

            .flatMap(result -> result
                .map((row, rowMetadata) -> Arrays.asList(row.get("value"), row.get("VALUE"))))
            .flatMapIterable(Function.identity())

            .concatWith(close(connection)))
        .as(StepVerifier::create)
        .expectNext(100L).as("value from col1")
        .expectNext("hello").as("value from col2")
        .verifyComplete();
  }

  /* Overrides parent test because column names are case-sensitive in Spanner */
  @Override
  @Test
  public void columnMetadata() {
    getJdbcOperations().execute(expand(INSERT_TWO_COLUMNS));

    Mono.from(getConnectionFactory().create())
        .flatMapMany(connection -> Flux.from(connection

            .createStatement("SELECT col1 AS value, col2 AS VALUE FROM test_two_column")
            .execute())
            .flatMap(result -> {
              return result.map((row, rowMetadata) -> {
                Collection<String> columnNames = rowMetadata.getColumnNames();
                return Arrays.asList(rowMetadata.getColumnMetadata("value").getName(),
                    rowMetadata.getColumnMetadata("VALUE").getName(), columnNames.contains("value"),
                    columnNames.contains(
                        "VALUE"));
              });
            })
            .flatMapIterable(Function.identity())
            .concatWith(close(connection)))
        .as(StepVerifier::create)
        .expectNext("value").as("Column label col1")
        .expectNext("VALUE").as("Column label col2 (get by uppercase)")
        .expectNext(true).as("getColumnNames.contains(value)")
        .expectNext(true).as("getColumnNames.contains(VALUE)")
        .verifyComplete();
  }

  @Override
  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Test
  public void returnGeneratedValues() {
    /*
    This tests auto-generated key columns in Spanner. This isn't supported.
     */
  }

  @Override
  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Test
  public void clobInsert() {
    /*
    the Clob is a streaming character object. Streaming into writes is NOT supported.
     */
  }

  @Override
  @Disabled
  @Test
  public void blobInsert() {
    /*
    the Blob is a streaming byte object. Streaming into writes is NOT supported.
     */
  }

  @Override
  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Test
  public void blobSelect() {
    /*
    the Blob is a streaming byte object. Streaming cell values are NOT supported.
     */
  }

  @Override
  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Test
  public void clobSelect() {
    /*
    the Clob is a streaming byte object. Streaming cell values are NOT supported.
     */
  }

  @Override
  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Test
  public void batch() {
    /*
    batch DML support is not supported currently.
     */
  }

  @Override
  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Test
  public void savePoint() {
    /*
    Save points are not supported.
     */
  }

  @Override
  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Test
  public void savePointStartsTransaction() {
    /*
    Save points are not supported.
     */
  }

  @Override
  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Test
  public void prepareStatementWithIncompleteBindingFails() {
    /*
    We do not currently do client-side verification of bindings: https://github.com/GoogleCloudPlatform/cloud-spanner-r2dbc/issues/74
     */
  }

  @Override
  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Test
  public void prepareStatementWithIncompleteBatchFails() {
    /*
    We do not currently do client-side verification of bindings: https://github.com/GoogleCloudPlatform/cloud-spanner-r2dbc/issues/74
     */
  }

  @Override
  @Disabled (DISABLE_UNSUPPORTED_FUNCTIONALITY)
  @Test
  public void compoundStatement() {
    /*
    Compound statements (statements with more than 1 semi-colon) are not supported.
     */
  }

  /* Overrides parent test because Spanner returns Long instead of Integer */
  @Override
  @Test
  public void changeAutoCommitCommitsTransaction() {
    Mono.from(getConnectionFactory().create())
        .flatMapMany(connection ->
            Flux.from(connection.setAutoCommit(false))
                .thenMany(connection.beginTransaction())
                .thenMany(connection.createStatement(expand(INSERT_VALUE200)).execute())
                .flatMap(Result::getRowsUpdated)
                .thenMany(connection.setAutoCommit(true))
                .thenMany(connection.createStatement(expand(SELECT_VALUE)).execute())
                .flatMap(it -> it.map((row, metadata) -> row.get("value")))
                .concatWith(close(connection))
        )
        .as(StepVerifier::create)
        // Cloud Spanner only has a 64 bit "integer"
        .expectNext(200L)
        .as("autoCommit(true) committed the transaction. Expecting a value to be present")
        .verifyComplete();
  }

  @Override
  public String expand(TestStatement statement, Object... args) {
    return SpannerTestKitStatements.expand(statement, args);
  }
}
