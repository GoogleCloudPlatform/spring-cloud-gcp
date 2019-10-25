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
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;
import static com.google.cloud.spanner.r2dbc.it.SpannerIT.TEST_DATABASE;
import static com.google.cloud.spanner.r2dbc.it.SpannerIT.TEST_INSTANCE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.google.cloud.ServiceOptions;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.test.TestKit;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCallback;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Tests bringing in "TCK" Example.java from r2dbc-spi-test.
 */
public class SpannerExample implements TestKit<String> {

  private static final ConnectionFactory connectionFactory =
      ConnectionFactories.get(ConnectionFactoryOptions.builder()
          .option(Option.valueOf("project"), ServiceOptions.getDefaultProjectId())
          .option(DRIVER, DRIVER_NAME)
          .option(INSTANCE, TEST_INSTANCE)
          .option(DATABASE, TEST_DATABASE)
          .build());

  private static final ConnectionPool pool =
      new ConnectionPool(ConnectionPoolConfiguration.builder(connectionFactory)
          .validationQuery("SELECT 1")
          .maxIdleTime(Duration.ofSeconds(10))
          .maxSize(15)
          .build());

  private static final Logger logger = LoggerFactory.getLogger(SpannerExample.class);

  private static final JdbcOperations jdbcOperations;

  static {
    // only execute() is needed.
    jdbcOperations = mock(JdbcOperations.class);

    doAnswer(invocation -> {
      String query = invocation.getArgument(0);
      executeDml(c -> c.createStatement(query.replace("INTO test ", "INTO test (value) ")
          .replace("INTO test_two_column", "INTO test_two_column (col1,col2)")));
      return null;
    }).when(jdbcOperations).execute((String) any());

    doAnswer(invocation -> {
      String query = invocation.getArgument(0);

      // The TCK uses java.sql JDBC classes that we have no implemented, but only in two cases
      // that we can detect and substitute here.
      if (query.equalsIgnoreCase("INSERT INTO clob_test VALUES (?)")) {
        executeDml(c -> c.createStatement("INSERT INTO clob_test (value) VALUES (@val)")
            .bind("val", "test-value"));
      } else if (query.equalsIgnoreCase("INSERT INTO blob_test VALUES (?)")) {
        executeDml(c -> c.createStatement("INSERT INTO blob_test (value) VALUES (@val)").bind("val",
            StandardCharsets.UTF_8.encode("test-value").array()));
      }

      return null;
    }).when(jdbcOperations).execute((String) any(), (PreparedStatementCallback) any());

    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();

    DatabaseId id = DatabaseId.of(options.getProjectId(), TEST_INSTANCE, TEST_DATABASE);
    DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();
    runDdl(id, dbAdminClient, "CREATE TABLE test ( value INT64 ) PRIMARY KEY (value)");
    runDdl(id, dbAdminClient,
        "CREATE TABLE test_two_column ( col1 INT64, col2 STRING(MAX) )  PRIMARY KEY (col1)");
    runDdl(id, dbAdminClient, "CREATE TABLE blob_test ( value BYTES(MAX) )  PRIMARY KEY (value)");
    runDdl(id, dbAdminClient, "CREATE TABLE clob_test ( value BYTES(MAX) )  PRIMARY KEY (value)");
  }

  private static void runDdl(DatabaseId id, DatabaseAdminClient dbAdminClient, String query) {
    try {
      dbAdminClient.updateDatabaseDdl(
          id.getInstanceId().getInstance(),
          id.getDatabase(),
          Collections.singletonList(query),
          null).get();
    } catch (Exception e) {
      logger.info("Couldn't run DDL", e);
    }
  }

  private static void executeDml(Function<Connection, Statement> statementFunc) {
    Mono.from(pool.create())
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
    return pool;
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

  @Ignore
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

  @Ignore
  @Override
  @Test
  public void bindFails() {
    /*
    This test focuses on various corner cases for position-binding, which is unsupported.
     */
  }

  // override to fix DDL for Spanner.
  @Override
  @Test
  public void prepareStatement() {
    Mono.from(getConnectionFactory().create())
        .delayUntil(c -> c.beginTransaction())
        .flatMapMany(connection -> {
          Statement statement = connection.createStatement(
              String.format("INSERT INTO test (value) VALUES(%s)", getPlaceholder(0)));

          IntStream.range(0, 10)
              .forEach(i -> statement.bind(getIdentifier(0), i).add());

          return Flux.from(statement
              .execute())
              .concatWith(close(connection));
        })
        .as(StepVerifier::create)
        .expectNextCount(10).as("values from insertions")
        .verifyComplete();
  }

  // override. column names are case-sensitive in Spanner.
  @Override
  @Test
  public void duplicateColumnNames() {
    getJdbcOperations().execute("INSERT INTO test_two_column VALUES (100, 'hello')");

    Mono.from(getConnectionFactory().create())
        .flatMapMany(connection -> Flux.from(connection

            .createStatement("SELECT col1 AS value, col2 AS VALUE FROM test_two_column")
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

  // override. column names are case-sensitive in Spanner.
  @Override
  @Test
  public void columnMetadata() {
    getJdbcOperations().execute("INSERT INTO test_two_column VALUES (100, 'hello')");

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
  @Ignore
  @Test
  public void returnGeneratedValues() {
    /*
    This tests auto-generated key columns in Spanner. This isn't supported.
     */
  }

  @Override
  @Ignore
  @Test
  public void clobInsert() {
    /*
    the Clob is a streaming character object. Streaming into writes is NOT supported.
     */
  }

  @Override
  @Ignore
  @Test
  public void blobInsert() {
    /*
    the Blob is a streaming byte object. Streaming into writes is NOT supported.
     */
  }

  @Override
  @Ignore
  @Test
  public void blobSelect() {
    /*
    the Blob is a streaming byte object. Streaming cell values are NOT supported.
     */
  }

  @Override
  @Ignore
  @Test
  public void clobSelect() {
    /*
    the Clob is a streaming byte object. Streaming cell values are NOT supported.
     */
  }

  @Override
  @Ignore
  @Test
  public void batch() {
    /*
    batch DML support is not supported currently.
     */
  }

  @Override
  @Ignore
  @Test
  public void savePoint() {
    /*
    Save points are not supported.
     */
  }

  @Override
  @Ignore
  @Test
  public void savePointStartsTransaction() {
    /*
    Save points are not supported.
     */
  }

  @Override
  @Ignore
  @Test
  public void prepareStatementWithIncompleteBindingFails() {
    /*
    We do not currently do client-side verification of bindings: https://github.com/GoogleCloudPlatform/cloud-spanner-r2dbc/issues/74
     */
  }

  @Override
  @Ignore
  @Test
  public void prepareStatementWithIncompleteBatchFails() {
    /*
    We do not currently do client-side verification of bindings: https://github.com/GoogleCloudPlatform/cloud-spanner-r2dbc/issues/74
     */
  }

  // DML syntax needed to be fixed.
  @Override
  @Test
  public void transactionCommit() {
    executeDml(c -> c.createStatement("INSERT INTO test (value) VALUES (100)"));

    Mono.from(getConnectionFactory().create())
        .flatMapMany(connection -> Mono.from(connection.beginTransaction())
            .<Object>thenMany(Flux.from(connection.createStatement("SELECT value FROM test")
                .execute())
                .flatMap(TestKit::extractColumns))

            // NOTE: this defer is a from() in the original. needs a follow up to resolve
            .concatWith(Flux.from(connection.createStatement(
                String.format("INSERT INTO test (value) VALUES (%s)", getPlaceholder(0)))
                .bind(getIdentifier(0), 200)
                .execute())
                .flatMap(TestKit::extractRowsUpdated))
            .concatWith(Flux.from(connection.createStatement("SELECT value FROM test")
                .execute())
                .flatMap(TestKit::extractColumns))
            .concatWith(Flux.from(connection.createStatement("SELECT value FROM test")
                .execute())
                .flatMap(TestKit::extractColumns))
            .concatWith(close(connection)))
        .as(StepVerifier::create)
        .expectNext(Collections.singletonList(100)).as("value from select 1")
        .expectNext(1).as("rows inserted")
        .expectNext(Arrays.asList(100, 200)).as("values from select 2")
        .expectNext(Arrays.asList(100, 200)).as("values from select 3")
        .verifyComplete();
  }

  @Override
  @Ignore
  @Test
  public void compoundStatement() {
    /*
    Compound statements (statements with more than 1 semi-colon) are not supported.
     */
  }

  // DML syntax fix.
  @Override
  @Test
  public void bindNull() {
    Mono.from(getConnectionFactory().create())
        .delayUntil(c -> c.beginTransaction())
        .flatMapMany(connection -> Flux.from(connection

            .createStatement(
                String.format("INSERT INTO test (value) VALUES(%s)", getPlaceholder(0)))
            .bindNull(getIdentifier(0), Integer.class).add()
            .execute())

            .concatWith(close(connection)))
        .as(StepVerifier::create)
        .expectNextCount(1).as("rows inserted")
        .verifyComplete();
  }
}
