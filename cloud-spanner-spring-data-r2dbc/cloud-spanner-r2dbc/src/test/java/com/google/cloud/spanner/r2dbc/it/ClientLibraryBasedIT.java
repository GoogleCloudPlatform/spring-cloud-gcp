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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.cloud.ServiceOptions;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.TimestampBound;
import com.google.cloud.spanner.Type;
import com.google.cloud.spanner.r2dbc.api.SpannerConnection;
import com.google.cloud.spanner.r2dbc.v2.SpannerClientLibraryConnectionFactory;
import io.r2dbc.spi.Closeable;
import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.ValidationDepth;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ClientLibraryBasedIT {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ClientLibraryBasedIT.class);

  static final String INSERT_QUERY = "INSERT BOOKS (UUID, TITLE, AUTHOR, CATEGORY, FICTION, "
      + "PUBLISHED, WORDS_PER_SENTENCE, PRICE) VALUES (@uuid, 'A Sound of Thunder', "
      + "'Ray Bradbury', @category, TRUE, '1952-06-28', @wordCount, @price)";

  private static final ConnectionFactory connectionFactory =
      ConnectionFactories.get(
          ConnectionFactoryOptions.builder()
              .option(Option.valueOf("project"), ServiceOptions.getDefaultProjectId())
              .option(DRIVER, DRIVER_NAME)
              .option(INSTANCE, DatabaseProperties.INSTANCE)
              .option(DATABASE, DatabaseProperties.DATABASE)
              .option(Option.valueOf("client-implementation"), "client-library")
              .build());

  Random random = new Random();

  /**
   * Recreates test table.
   * Call with {@code it.recreate-ddl=false} to skip dropping and recreating test tables.
   */
  @BeforeAll
  public static void setupSpannerTable() {

    if (!"false".equals(System.getProperty("it.recreate-ddl"))) {
      LOGGER.info("Dropping and re-creating table BOOKS.");
      Connection con = Mono.from(connectionFactory.create()).block();

      try {
        Mono.from(con.createStatement("DROP TABLE BOOKS").execute()).block();
      } catch (Exception e) {
        LOGGER.info("The BOOKS table doesn't exist", e);
      }

      Mono.from(
              con.createStatement(
                      "CREATE TABLE BOOKS ("
                          + "  UUID STRING(36) NOT NULL,"
                          + "  TITLE STRING(256) NOT NULL,"
                          + "  AUTHOR STRING(256),"
                          + "  SYNOPSIS STRING(MAX),"
                          + "  EDITIONS ARRAY<STRING(MAX)>,"
                          + "  FICTION BOOL,"
                          + "  PUBLISHED DATE,"
                          + "  WORDS_PER_SENTENCE FLOAT64,"
                          + "  CATEGORY INT64,"
                          + "  PRICE NUMERIC"
                          + ") PRIMARY KEY (UUID)")
                  .execute())
          .block();
    }
  }

  /**
   * Clears data before each test run.
   */
  @BeforeEach
  public void deleteData() {

    Connection conn =
        Mono.from(connectionFactory.create()).block();

    Mono.from(
        conn.createStatement("DELETE FROM BOOKS WHERE true").execute())
        .flatMap(rs -> Mono.from(rs.getRowsUpdated()))
        .block();
  }

  @AfterAll
  static void cleanUpEnvironment() {
    Closeable closeableConnectionFactory = (Closeable) connectionFactory;
    Mono.from(closeableConnectionFactory.close()).block();
  }

  @Test
  void testValidate() {
    Connection conn = Mono.from(connectionFactory.create()).block();
    boolean result = Mono.from(conn.validate(ValidationDepth.REMOTE)).block();
    assertThat(result).isTrue();

    Mono.from(conn.close()).block();
    result = Mono.from(conn.validate(ValidationDepth.REMOTE)).block();
    assertThat(result).isFalse();
  }

  @Test
  void testReadQuery() {

    Connection conn = Mono.from(connectionFactory.create()).block();

    StepVerifier.create(
        Mono.from(conn.createStatement("SELECT count(*) as count FROM BOOKS").execute())
            .flatMapMany(rs -> rs.map((row, rmeta) -> row.get(1, Long.class))))
        .expectNext(Long.valueOf(0))
        .verifyComplete();
    StepVerifier.create(
        Mono.from(conn.createStatement("SELECT count(*) as count FROM BOOKS").execute())
            .flatMapMany(rs -> rs.map((row, rmeta) -> row.get("count", Long.class))))
        .expectNext(Long.valueOf(0))
        .verifyComplete();
  }

  @Test
  void testMetadata() {

    Connection conn = Mono.from(connectionFactory.create()).block();

    StepVerifier.create(
        Mono.from(
            conn.createStatement(INSERT_QUERY)
                .bind("uuid", "abc")
                .bind("category", 100L)
                .bind("wordCount", 20.8)
                .bind("price", new BigDecimal("123.99"))
                .execute())
            .flatMapMany(rs -> rs.getRowsUpdated())
    ).expectNext(1).verifyComplete();

    StepVerifier.create(
        Mono.from(conn.createStatement("SELECT AUTHOR, PRICE FROM BOOKS LIMIT 1").execute())
            .flatMapMany(rs -> rs.map((row, rmeta) -> {
              List<ColumnMetadata> list = new ArrayList<>();
              rmeta.getColumnMetadatas().forEach(list::add);
              return list;
            })))
        .assertNext(metadataList -> {
          assertEquals(2, metadataList.size());
          assertEquals("AUTHOR", metadataList.get(0).getName());
          assertEquals("PRICE", metadataList.get(1).getName());
          assertEquals(Type.string(), metadataList.get(0).getNativeTypeMetadata());
          assertEquals(Type.numeric(), metadataList.get(1).getNativeTypeMetadata());
        })
        .verifyComplete();
  }

  @Test
  void testErrorPropagation() {

    Connection conn = Mono.from(connectionFactory.create()).block();

    StepVerifier.create(
        Mono.from(conn.createStatement("SELECT * FROM bad SQL no cookie").execute())
            .flatMapMany(rs -> rs.map((r, rm) -> "unused result"))
    ).verifyError(SpannerException.class);
  }


  @Test
  void testDmlInsert() {
    Connection conn = Mono.from(connectionFactory.create()).block();

    String id = "abc123-" + this.random.nextInt();

    StepVerifier.create(
        Mono.from(
            conn.createStatement(INSERT_QUERY)
                .bind("uuid", id)
                .bind("category", 100L)
                .bind("wordCount", 20.8)
                .bind("price", new BigDecimal("123.99"))
                .execute())
            .flatMapMany(rs -> rs.getRowsUpdated())
    ).expectNext(1).verifyComplete();

    StepVerifier.create(
        Mono.from(conn.createStatement("SELECT count(*) FROM BOOKS").execute())
            .flatMapMany(rs -> rs.map((row, rmeta) -> row.get(1, Long.class))))
        .expectNext(Long.valueOf(1))
        .verifyComplete();
    StepVerifier.create(
        Mono.from(
            conn.createStatement("SELECT WORDS_PER_SENTENCE FROM BOOKS WHERE UUID = @uuid")
                .bind("uuid", id)
                .execute()
        ).flatMapMany(rs -> rs.map((row, rmeta) -> row.get(1, Double.class))))
        .expectNext(20.8d).verifyComplete();
    StepVerifier.create(
        Mono.from(
            conn.createStatement("SELECT PRICE FROM BOOKS WHERE UUID = @uuid")
                .bind("uuid", id)
                .execute()
        ).flatMapMany(rs -> rs.map((row, rmeta) -> row.get(1, BigDecimal.class))))
        .expectNext(new BigDecimal("123.99")).verifyComplete();
  }

  @Test
  void testTransactionSingleStatementCommitted() {

    String uuid1 = "transaction1-commit1-" + this.random.nextInt();

    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> Flux.concat(
                c.beginTransaction(),
                Flux.from(c.createStatement(makeInsertQuery(uuid1, 100, 15.0))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated()),
                c.commitTransaction(),
                c.close()))
    ).expectNext(1).verifyComplete();

    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> c.createStatement(
                "SELECT COUNT(*) as num_rows FROM BOOKS WHERE UUID = @uuid")
                .bind("uuid", uuid1)
                .execute()
            ).flatMap(rs -> rs.map((row, rmeta) -> row.get("num_rows", Long.class))))
        // Expected row inserted
        .expectNext(Long.valueOf(1))
        .verifyComplete();
  }

  @Test
  void testTransactionMultipleStatementsCommitted() {

    String uuid1 = "transaction1-commit1-" + this.random.nextInt();
    String uuid2 = "transaction1-commit2-" + this.random.nextInt();

    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> Flux.concat(

                c.beginTransaction(),
                Flux.from(c.createStatement(makeInsertQuery(uuid1, 100, 15.0))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated()),
                Flux.from(c.createStatement(makeInsertQuery(uuid2, 100, 15.0))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated()),

                Flux.from(c.createStatement(
                    "UPDATE BOOKS SET CATEGORY=200 WHERE CATEGORY = 100").execute())
                    .flatMap(r -> r.getRowsUpdated()),
                c.commitTransaction()

            ))

    ).expectNext(1, 1, 2).verifyComplete();

    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> c.createStatement(
                "SELECT UUID FROM BOOKS WHERE CATEGORY = @category ORDER BY UUID")
                .bind("category", 200L)
                .execute()
            ).flatMap(rs -> rs.map((row, rmeta) -> row.get("UUID", String.class))))
        // Expected row inserted
        .expectNext(uuid1, uuid2)
        .verifyComplete();
  }

  @Test
  void testTransactionFollowedByStandaloneStatementCommitted() {

    String uuid1 = "transaction1-commit1-" + this.random.nextInt();
    String uuid2 = "transaction1-commit2-" + this.random.nextInt();

    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> Flux.concat(
                c.beginTransaction(),
                Flux.from(c.createStatement(makeInsertQuery(uuid1, 100, 15.0))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated()),
                c.commitTransaction(),
                Flux.from(c.createStatement(makeInsertQuery(uuid2, 100, 15.0))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated())
            ))

    ).expectNext(1, 1).verifyComplete();

    verifyIds(uuid1, uuid2);
  }


  @Test
  void testTransactionRolledBack() {
    String uuid = "transaction2-abort" + this.random.nextInt();

    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> Flux.concat(
                c.beginTransaction(),
                Flux.from(c.createStatement(makeInsertQuery(uuid, 100, 15.0))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated()),
                c.rollbackTransaction()))
    ).expectNext(1).verifyComplete();

    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> c.createStatement(
                "SELECT count(*) as count FROM BOOKS WHERE UUID=@uuid")
                .bind("uuid", uuid)
                .execute()
            ).flatMap(rs -> rs.map((row, rmeta) -> row.get("count", Long.class))))
        // Expect row not inserted
        .expectNext(Long.valueOf(0))
        .verifyComplete();
  }

  @Test
  void selectQueryReturnsUpdatedDataDuringAndAfterTransactionCommit() {

    String uuid1 = "transaction1-commit1-" + this.random.nextInt();

    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> Flux.concat(
                c.beginTransaction(),
                Flux.from(c.createStatement(makeInsertQuery(uuid1, 100, 15.0))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated()),
                Flux.from(c.createStatement("SELECT UUID FROM BOOKS WHERE UUID = @uuid")
                  .bind("uuid", uuid1).execute()
                ).flatMap(r -> r.map((row, rmeta) -> row.get("UUID", String.class))),
                c.commitTransaction()
            ))

    ).expectNext(1, uuid1).verifyComplete();

    verifyIds(uuid1);
  }

  @Test
  void selectQueryReturnsUpdatedDataDuringTransactionButNotAfterTransactionRollback() {

    String uuid1 = "transaction1-commit1-" + this.random.nextInt();

    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> Flux.concat(
                c.beginTransaction(),
                Flux.from(c.createStatement(makeInsertQuery(uuid1, 100, 15.0))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated()),
                Flux.from(c.createStatement("SELECT UUID FROM BOOKS WHERE UUID = @uuid")
                    .bind("uuid", uuid1).execute()
                ).flatMap(r -> r.map((row, rmeta) -> row.get("UUID", String.class))),
                c.rollbackTransaction()
            ))

    ).expectNext(1, uuid1).verifyComplete();

    // no data
    verifyIds();
  }

  @Test
  void ddlCreateAndDrop() {
    String listTables = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name=@table";
    String tableName = "test_table_" + this.random.nextInt(100000);

    Connection conn = Mono.from(connectionFactory.create()).block();

    StepVerifier.create(
        Flux.from(conn.createStatement(listTables).bind("table", tableName).execute())
            .flatMap(this::getFirstNumber)
    ).expectNext(0L).as("Table not found before creation").verifyComplete();

    StepVerifier.create(
        Mono.from(conn.createStatement(
            "CREATE TABLE " + tableName + " ("
                + "  NAME STRING(256) NOT NULL,"
                + "  START_YEAR INT64 NOT NULL"
                + ") PRIMARY KEY (NAME)")
            .execute()).flatMap(res -> Mono.from(res.getRowsUpdated()))
    ).expectNext().as("DDL execution returns zero affected rows")
    .verifyComplete();

    StepVerifier.create(
        Flux.from(conn.createStatement(listTables).bind("table", tableName).execute())
            .flatMap(this::getFirstNumber)
    ).expectNext(1L).as("Table found after creation").verifyComplete();

    StepVerifier.create(
        Flux.from(conn.createStatement("DROP TABLE " + tableName).execute())
            .flatMap(res -> res.map(
                (row, meta) -> "this should not happen because DDL does not return rows")))
        .expectNext().as("DDL execution returns zero affected rows")
        .verifyComplete();

    StepVerifier.create(
        Flux.from(conn.createStatement(listTables).bind("table", tableName).execute())
            .flatMap(this::getFirstNumber)
    ).expectNext(0L).as("Table not found after deletion").verifyComplete();
  }

  @Test
  void selectMultipleBoundParameterSetsNoTransaction() {

    String uuid1 = "params-no-transaction-" + this.random.nextInt();
    String uuid2 = "params-no-transaction-" + this.random.nextInt();
    String uuid3 = "params-no-transaction-" + this.random.nextInt();

    // set up 3 test rows
    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> Flux.concat(
                Flux.from(c.createStatement(makeInsertQuery(uuid1, 100, 3))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated()),
                Flux.from(c.createStatement(makeInsertQuery(uuid2, 100, 5))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated()),
                Flux.from(c.createStatement(makeInsertQuery(uuid3, 100, 7))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated())
            ))

    ).expectNext(1, 1, 1).verifyComplete();

    StepVerifier.create(
        Mono.from(connectionFactory.create()).flatMapMany(
            conn -> Flux.from(
                conn.createStatement("SELECT count(*) FROM BOOKS WHERE WORDS_PER_SENTENCE > @words")
                    .bind("words", 8).add()
                    .bind("words", 7).add()
                    .bind("words", 5).add()
                    .bind("words", 4).add()
                    .bind("words", 0).add()
                    .execute()
            )
            .flatMapSequential(rs -> rs.map((row, rmeta) -> row.get(1, Integer.class))))
    ).expectNext(0, 0, 1, 2, 3).as("Row count matches bound variables").verifyComplete();

  }

  /* This test
  1) exercises a different internal code path than selectMultipleBoundParameterSetsNoTransaction()
  2) omits the final add() for the last bound row.
  */
  @Test
  void selectMultipleBoundParameterSetsInTransaction() {

    String uuid1 = "params-no-transaction-" + this.random.nextInt();
    String uuid2 = "params-no-transaction-" + this.random.nextInt();
    String uuid3 = "params-no-transaction-" + this.random.nextInt();

    // set up 3 test rows
    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> Flux.concat(
                Flux.from(c.createStatement(makeInsertQuery(uuid1, 100, 3))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated()),
                Flux.from(c.createStatement(makeInsertQuery(uuid2, 100, 5))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated()),
                Flux.from(c.createStatement(makeInsertQuery(uuid3, 100, 7))
                    .execute()
                ).flatMap(r -> r.getRowsUpdated())
            ))

    ).expectNext(1, 1, 1).verifyComplete();

    StepVerifier.create(
        Mono.from(connectionFactory.create()).flatMapMany(
            conn -> Flux.concat(
                conn.beginTransaction(),
                Flux.from(
                  conn.createStatement(
                      "SELECT count(*) FROM BOOKS WHERE WORDS_PER_SENTENCE > @words")
                      .bind("words", 8).add()
                      .bind("words", 7).add()
                      .bind("words", 5).add()
                      .bind("words", 4).add()
                      .bind("words", 0) // Final .add() missing intentionally
                    .execute()
                ).flatMapSequential(rs -> rs.map((row, rmeta) -> row.get(1, Long.class))),
                conn.commitTransaction())
            )

    ).expectNext(0L, 0L, 1L, 2L, 3L).as("Row count matches bound variables").verifyComplete();

  }

  @Test
  void insertMultipleBoundParameterSetsNoTransaction() {

    String uuid1 = "params-no-transaction-" + this.random.nextInt();
    String uuid2 = "params-no-transaction-" + this.random.nextInt();
    String uuid3 = "params-no-transaction-" + this.random.nextInt();

    String statement =
        "INSERT BOOKS (UUID, TITLE) VALUES (@uuid, @title)";
    StepVerifier.create(
        Mono.from(connectionFactory.create()).flatMapMany(
            conn -> Flux.from(
                conn.createStatement(statement)
                    .bind("uuid", uuid1).bind("title", "A").add()
                    .bind("uuid", uuid2).bind("title", "B").add()
                    .bind("uuid", uuid3).bind("title", "C").add()
                    .execute()
            )
                .flatMap(rs -> rs.getRowsUpdated()))
    ).expectNext(1, 1, 1).as("Row insert count matches").verifyComplete();

    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> c.createStatement(
                "SELECT UUID, TITLE FROM BOOKS ORDER BY TITLE")
                .execute()
            ).flatMap(rs -> rs.map(
                (row, rmeta) -> row.get("TITLE", String.class) + row.get("UUID", String.class))))
        .expectNext("A" + uuid1, "B" + uuid2, "C" + uuid3)
        .as("Found previously inserted rows")
        .verifyComplete();
  }

  @Test
  void insertMultipleBoundParameterSetsInTransaction() {

    String uuid1 = "params-no-transaction-" + this.random.nextInt();
    String uuid2 = "params-no-transaction-" + this.random.nextInt();
    String uuid3 = "params-no-transaction-" + this.random.nextInt();

    String statement =
        "INSERT BOOKS (UUID, TITLE) VALUES (@uuid, @title)";
    StepVerifier.create(
        Mono.from(connectionFactory.create()).flatMapMany(
            conn -> Flux.concat(
                conn.beginTransaction(),
                Flux.from(conn.createStatement(statement)
                    .bind("uuid", uuid1).bind("title", "A").add()
                    .bind("uuid", uuid2).bind("title", "B").add()
                    .bind("uuid", uuid3).bind("title", "C").add()
                    .execute()).flatMap(rs -> rs.getRowsUpdated()),
                conn.commitTransaction()
            )
        )
    ).expectNext(1, 1, 1).as("Row insert count matches").verifyComplete();

    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> c.createStatement(
                "SELECT UUID, TITLE FROM BOOKS ORDER BY TITLE")
                .execute()
            ).flatMap(rs -> rs.map(
                (row, rmeta) -> row.get("TITLE", String.class) + row.get("UUID", String.class))))
        .expectNext("A" + uuid1, "B" + uuid2, "C" + uuid3)
        .as("Found previously inserted rows")
        .verifyComplete();
  }

  @Test
  void testStaleRead() throws InterruptedException {

    // Prevent a stale read from 1 second ago from seeing a nonexistent table.
    Thread.sleep(1000);

    String uuid1 = "transaction1-staleread" + this.random.nextInt();

    Connection conn = Mono.from(connectionFactory.create()).block();

    Statement readStatement = conn.createStatement("SELECT count(*) from BOOKS WHERE UUID=@uuid")
        .bind("uuid", uuid1);

    StepVerifier.create(Flux.concat(
          Flux.from(
              conn.createStatement(makeInsertQuery(uuid1, 100, 15.0)).execute()
          ).flatMap(r -> r.getRowsUpdated()),
          Flux.from(readStatement.execute()).flatMap(result -> result.map((row, rm) -> row.get(1))),
          ((SpannerConnection) conn).beginReadonlyTransaction(
              TimestampBound.ofExactStaleness(1, TimeUnit.SECONDS)),
          Flux.from(readStatement.execute()).flatMap(result -> result.map((row, rm) -> row.get(1))),
          conn.commitTransaction(),
          Flux.from(readStatement.execute()).flatMap(result -> result.map((row, rm) -> row.get(1)))
    )).expectNext(1)
        .as("row inserted")
        .expectNext(1L)
        .as("strong read returns the inserted row without a transaction")
        .expectNext(0L)
        .as("stale read returns nothing")
        .expectNext(1L)
        .as("strong read returns the inserted row after stale-read transaction terminates")
        .verifyComplete();
  }

  @Test
  void testStrongReadFromSubclassedConnection() throws InterruptedException {

    String uuid1 = "transaction1-strong-read" + this.random.nextInt();
    String sql = "SELECT count(*) from BOOKS WHERE UUID='" + uuid1 + "'";

    StepVerifier.create(
        Mono.from(connectionFactory.create()).flatMapMany(conn ->
            Flux.concat(
                Flux.from(conn.createStatement(sql).execute()).flatMap(this::getFirstNumber),
                Flux.from(conn.createStatement(makeInsertQuery(uuid1, 100, 15.0)).execute())
                    .flatMap(r -> r.getRowsUpdated()),
                Flux.from(conn.createStatement(sql).execute()).flatMap(this::getFirstNumber),
                ((SpannerConnection) conn).beginReadonlyTransaction(), // non-SPI method
                Flux.from(conn.createStatement(sql).execute()).flatMap(this::getFirstNumber),
                conn.commitTransaction(),
                Flux.from(conn.createStatement(sql).execute()).flatMap(this::getFirstNumber)
            )
        )).expectNext(0L)
        .as("empty table; nothing has been inserted yet")
        .expectNext(1)
        .as("row inserted")
        .expectNext(1L)
        .as("strong read returns the inserted row without a transaction")
        .expectNext(1L)
        .as("strong read in read-only transaction returns the inserted row, too")
        .expectNext(1L)
        .as("strong read still returns the inserted row, after transaction completes")
        .verifyComplete();
  }

  @Test
  void testConnectingThroughUrl() {
    ConnectionFactory urlBasedConnectionFactory =
        ConnectionFactories.get(DatabaseProperties.URL + "?client-implementation=client-library");
    assertThat(urlBasedConnectionFactory).isInstanceOf(SpannerClientLibraryConnectionFactory.class);
    SpannerClientLibraryConnectionFactory sclConnectionFactory =
        (SpannerClientLibraryConnectionFactory) urlBasedConnectionFactory;

    // clean up Spanner resources.
    Mono.from(sclConnectionFactory.close()).block();
  }

  private Publisher<Long> getFirstNumber(Result result) {
    return result.map((row, meta) -> (Long) row.get(1));
  }

  private String makeInsertQuery(String uuid, int category, double wordCount) {
    return "INSERT BOOKS "
        + "(UUID, TITLE, AUTHOR, CATEGORY, FICTION, "
        + "PUBLISHED, WORDS_PER_SENTENCE, PRICE)"
        + " VALUES "
        + "('" + uuid + "', 'A Sound of Thunder', 'Ray Bradbury', "
        + category + ", TRUE, "
        + "'1952-06-28', " + wordCount + ", 99.99);";
  }

  private void verifyIds(String... uuids) {
    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(c -> c.createStatement(
                "SELECT UUID FROM BOOKS ORDER BY UUID")
                .execute()
            ).flatMap(rs -> rs.map((row, rmeta) -> row.get("UUID", String.class))))
        .expectNext(uuids)
        .as("Expected rows inserted")
        .verifyComplete();
  }

}
