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
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.r2dbc.SpannerConnection;
import com.google.cloud.spanner.r2dbc.SpannerConnectionFactory;
import com.google.cloud.spanner.r2dbc.client.GrpcClient;
import com.google.cloud.spanner.r2dbc.util.ObservableReactiveUtil;
import com.google.spanner.v1.DatabaseName;
import com.google.spanner.v1.ListSessionsRequest;
import com.google.spanner.v1.ListSessionsResponse;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.SpannerGrpc.SpannerStub;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration test for connecting to a real Spanner instance.
 */
public class SpannerIT {

  public static final String TEST_INSTANCE = "reactivetest";

  public static final String TEST_DATABASE = "testdb";

  private static final ConnectionFactory connectionFactory =
      ConnectionFactories.get(ConnectionFactoryOptions.builder()
          // TODO: consider whether to bring autodiscovery of project ID
          .option(Option.valueOf("project"), ServiceOptions.getDefaultProjectId())
          .option(DRIVER, DRIVER_NAME)
          .option(INSTANCE, TEST_INSTANCE)
          .option(DATABASE, TEST_DATABASE)
          .build());

  private SpannerStub spanner;

  private GrpcClient grpcClient;

  private static final Logger logger = LoggerFactory.getLogger(SpannerIT.class);

  /**
   * Setup the Spanner stub for testing.
   */
  @Before
  public void setupStubs() throws IOException {
    this.grpcClient = new GrpcClient(GoogleCredentials.getApplicationDefault());
    this.spanner = this.grpcClient.getSpanner();
  }

  @After
  public void shutdown() {
    this.grpcClient.close().block();
  }

  /**
   * Setup the Spanner table for testing.
   */
  @BeforeClass
  public static void setupSpannerTable() throws InterruptedException, ExecutionException {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();

    DatabaseId id = DatabaseId.of(options.getProjectId(), TEST_INSTANCE, TEST_DATABASE);

    DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();

    try {
      dbAdminClient.updateDatabaseDdl(
          id.getInstanceId().getInstance(),
          id.getDatabase(),
          Collections.singletonList("DROP TABLE BOOKS"),
          null).get();
    } catch (Exception e) {
      logger.info("The BOOKS table doesn't exist", e);
    }

    dbAdminClient.updateDatabaseDdl(
        id.getInstanceId().getInstance(),
        id.getDatabase(),
        Collections.singletonList(
            "CREATE TABLE BOOKS ("
                + "  UUID STRING(36) NOT NULL,"
                + "  TITLE STRING(256) NOT NULL,"
                + "  AUTHOR STRING(256) NOT NULL,"
                + "  FICTION BOOL NOT NULL,"
                + "  PUBLISHED DATE NOT NULL,"
                + "  WORDS_PER_SENTENCE FLOAT64 NOT NULL,"
                + "  CATEGORY INT64 NOT NULL"
                + ") PRIMARY KEY (UUID)"),
        null).get();
  }

  @Test
  public void testSessionManagement() {
    assertThat(this.connectionFactory).isInstanceOf(SpannerConnectionFactory.class);

    Mono<Connection> connection = (Mono<Connection>) this.connectionFactory.create();
    SpannerConnection spannerConnection = (SpannerConnection) connection.block();
    String activeSessionName = spannerConnection.getSession().getName();

    List<String> activeSessions = getSessionNames();
    assertThat(activeSessions).contains(activeSessionName);

    Mono.from(spannerConnection.close()).block();

    activeSessions = getSessionNames();
    assertThat(activeSessions).doesNotContain(activeSessionName);
  }

  @BeforeEach
  public void cleanTable() {
    executeDmlQuery("DELETE FROM books WHERE true");
  }

  @Test
  public void testQuerying() {
    cleanTable();

    long count = executeReadQuery(
        "Select count(1) as count FROM books",
        (row, rowMetadata) -> row.get("count", Long.class)).get(0);
    assertThat(count).isEqualTo(0);

    Mono.from(this.connectionFactory.create())
        .delayUntil(c -> c.beginTransaction())
        .delayUntil(c -> Flux.from(c.createStatement(
            "INSERT BOOKS (UUID, TITLE, AUTHOR, CATEGORY, FICTION, PUBLISHED, WORDS_PER_SENTENCE)"
                + " VALUES (@uuid, @title, @author, @category, @fiction, @published, @wps);")
            .bind("uuid", "2b2cbd78-ecd8-430e-b685-fa7910f8a4c7")
            .bind("author", "Douglas Crockford")
            .bind("category", 100L)
            .bind("title", "JavaScript: The Good Parts")
            .bind("fiction", true)
            .bind("published", LocalDate.of(2008, 5, 1))
            .bind("wps", 20.8)
            .add()
            .bind("uuid", "df0e3d06-2743-4691-8e51-6d33d90c5cb9")
            .bind("author", "Joshua Bloch")
            .bind("category", 100L)
            .bind("title", "Effective Java")
            .bind("fiction", false)
            .bind("published", LocalDate.of(2018, 1, 6))
            .bind("wps", 15.1)
            .execute()).flatMapSequential(r -> Mono.from(r.getRowsUpdated())))
        .delayUntil(c -> c.commitTransaction())
        .block();

    List<String> authorStrings = executeReadQuery(
        "SELECT title, author FROM books",
        (r, meta) -> r.get(0, String.class) + " by " + r.get(1, String.class));

    assertThat(authorStrings).containsExactlyInAnyOrder(
        "JavaScript: The Good Parts by Douglas Crockford",
        "Effective Java by Joshua Bloch");

    List<String> result2 = Mono.from(this.connectionFactory.create())
        .map(connection -> connection
            .createStatement("SELECT title, author FROM books WHERE author = @author"))
        .flatMapMany(statement -> statement
            .bind("author", "Joshua Bloch")
            .execute())
        .flatMap(spannerResult -> spannerResult.map(
            (r, meta) -> r.get(0, String.class) + " by " + r.get(1, String.class)
        ))
        .doOnNext(s -> System.out.println("Book: " + s))
        .collectList()
        .block();

    assertThat(result2).containsExactly("Effective Java by Joshua Bloch");

    List<String> result3 = Mono.from(this.connectionFactory.create())
        .map(connection -> connection
            .createStatement("SELECT title, author FROM books WHERE author = @author"))
        .flatMapMany(statement -> statement
            .bind("author", "Joshua Bloch").add()
            .bind("author", "Douglas Crockford")
            .execute())
        .flatMap(spannerResult -> spannerResult.map(
            (r, meta) -> r.get(0, String.class) + " by " + r.get(1, String.class)
        ))
        .doOnNext(s -> System.out.println("Book: " + s))
        .collectList()
        .block();

    assertThat(result3).containsExactlyInAnyOrder(
        "JavaScript: The Good Parts by Douglas Crockford",
        "Effective Java by Joshua Bloch");

    int rowsUpdated = executeDmlQuery("DELETE FROM books WHERE true");
    assertThat(rowsUpdated).isEqualTo(2);
  }

  @Test
  public void testNoopUpdate() {
    Result result = Mono.from(connectionFactory.create())
        .delayUntil(c -> c.beginTransaction())
        .flatMap(c -> Mono.from(c.createStatement(
            "UPDATE BOOKS set author = 'blah2' where title = 'asdasdf_dont_exist'").execute()))
        .block();

    int rowsUpdated = Mono.from(result.getRowsUpdated()).block();
    assertThat(rowsUpdated).isEqualTo(0);

    List<String> rowsReturned =
        Flux.from(result.map((row, metadata) -> row.toString()))
            .collectList()
            .block();
    assertThat(rowsReturned).isEmpty();
  }

  @Test
  public void testEmptySelect() {
    List<String> results = executeReadQuery(
        "SELECT title, author FROM books where author = 'Nobody P. Smith'",
        (r, meta) -> r.get(0, String.class));

    assertThat(results).isEmpty();
  }

  /**
   * Executes a DML query and returns the rows updated.
   */
  private int executeDmlQuery(String sql) {
    Connection connection = Mono.from(connectionFactory.create()).block();

    Mono.from(connection.beginTransaction()).block();
    int rowsUpdated = Mono.from(connection.createStatement(sql).execute())
        .flatMap(result -> Mono.from(result.getRowsUpdated()))
        .block();
    Mono.from(connection.commitTransaction()).block();

    return rowsUpdated;
  }

  /**
   * Executes a read query and runs the provided {@code mappingFunction} on the elements returned.
   */
  private <T> List<T> executeReadQuery(
      String sql,
      BiFunction<Row, RowMetadata, T> mappingFunction) {

    return Mono.from(connectionFactory.create())
        .map(connection -> connection.createStatement(sql))
        .flatMapMany(statement -> statement.execute())
        .flatMap(spannerResult -> spannerResult.map(mappingFunction))
        .collectList()
        .block();
  }

  private List<String> getSessionNames() {
    String databaseName =
        DatabaseName.format(ServiceOptions.getDefaultProjectId(), TEST_INSTANCE, TEST_DATABASE);

    ListSessionsRequest listSessionsRequest =
        ListSessionsRequest.newBuilder()
            .setDatabase(databaseName)
            .build();

    ListSessionsResponse listSessionsResponse =
        ObservableReactiveUtil.<ListSessionsResponse>unaryCall(
            obs -> this.spanner.listSessions(listSessionsRequest, obs))
            .block();

    return listSessionsResponse.getSessionsList()
        .stream()
        .map(Session::getName)
        .collect(Collectors.toList());
  }
}
