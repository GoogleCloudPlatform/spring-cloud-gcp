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
import static com.google.cloud.spanner.r2dbc.it.SpannerQueryUtil.executeDmlQuery;
import static com.google.cloud.spanner.r2dbc.it.SpannerQueryUtil.executeReadQuery;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.spanner.r2dbc.SpannerConnection;
import com.google.cloud.spanner.r2dbc.SpannerConnectionFactory;
import com.google.cloud.spanner.r2dbc.SpannerResult;
import com.google.cloud.spanner.r2dbc.client.GrpcClient;
import com.google.cloud.spanner.r2dbc.util.ObservableReactiveUtil;
import com.google.common.base.Strings;
import com.google.spanner.v1.DatabaseName;
import com.google.spanner.v1.ListSessionsRequest;
import com.google.spanner.v1.ListSessionsResponse;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.SpannerGrpc.SpannerStub;
import com.google.spanner.v1.TransactionOptions;
import com.google.spanner.v1.TransactionOptions.PartitionedDml;
import com.google.spanner.v1.TransactionOptions.ReadOnly;
import com.google.spanner.v1.TransactionOptions.ReadWrite;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import io.r2dbc.spi.Result;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Integration test for connecting to a real Spanner instance.
 */
public class SpannerIT {

  private static final ConnectionFactory connectionFactory =
      ConnectionFactories.get(ConnectionFactoryOptions.builder()
          // TODO: consider whether to bring autodiscovery of project ID
          .option(Option.valueOf("project"), ServiceOptions.getDefaultProjectId())
          .option(DRIVER, DRIVER_NAME)
          .option(INSTANCE, DatabaseProperties.INSTANCE)
          .option(DATABASE, DatabaseProperties.DATABASE)
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

    executeDmlQuery(
        connectionFactory,
        "DELETE FROM books WHERE true");
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

    SpannerConnection con =
        Mono.from(connectionFactory.create())
            .cast(SpannerConnection.class)
            .block();

    try {
      Mono.from(con.createStatement("DROP TABLE BOOKS").execute()).block();
    } catch (Exception e) {
      logger.info("The BOOKS table doesn't exist", e);
    }

    Mono.from(con.createStatement(
        "CREATE TABLE BOOKS ("
            + "  UUID STRING(36) NOT NULL,"
            + "  TITLE STRING(256) NOT NULL,"
            + "  AUTHOR STRING(256) NOT NULL,"
            + "  SYNOPSIS STRING(MAX),"
            + "  EDITIONS ARRAY<STRING(MAX)>,"
            + "  FICTION BOOL NOT NULL,"
            + "  PUBLISHED DATE NOT NULL,"
            + "  WORDS_PER_SENTENCE FLOAT64 NOT NULL,"
            + "  CATEGORY INT64 NOT NULL"
            + ") PRIMARY KEY (UUID)").execute())
        .block();
  }

  @Test
  public void testLargeReadWrite() {
    // string size must be below 10 MB
    int maxStringLength = 100000;

    int numberOfBooks = 20;

    List<Book> books = new ArrayList<>();

    for (int i = 0; i < numberOfBooks; i++) {
      String bookString = String.valueOf(i);
      int copies = maxStringLength / bookString.length();
      String[] editions = new String[copies];
      Arrays.fill(editions, bookString);
      books.add(
          new Book("id" + i, "title" + i, "author" + i,
              Strings.repeat(bookString, copies), editions,
              i % 3 == 0, LocalDate.now(), i + 0.1, i));
    }

    Mono.from(this.connectionFactory.create())
        .delayUntil(c -> c.beginTransaction())
        .delayUntil(c ->
            Flux.fromIterable(books)
                .concatMapDelayError(book ->
                    Flux.from(c.createStatement(
                        "INSERT BOOKS (UUID, TITLE, AUTHOR, SYNOPSIS, EDITIONS, "
                            + "CATEGORY, FICTION, PUBLISHED, WORDS_PER_SENTENCE)"
                            + " VALUES (@uuid, @title, @author, @synopsis, @editions, "
                            + "@category, @fiction, @published, @wps);")
                        .bind("uuid", book.getId())
                        .bind("author", book.getAuthor())
                        .bind("category", book.getCategory())
                        .bind("synopsis", book.getSynopsis())
                        .bind("editions", book.getEditions())
                        .bind("title", book.getTitle())
                        .bind("fiction", book.getFiction())
                        .bind("published", book.getPublished())
                        .bind("wps", book.getWordsPerSentence())
                        .execute())
                        .doOnNext(r -> logger.info("Inserting book: " + book.getId()))
                ).flatMap(r -> Mono.from(r.getRowsUpdated()))
        )
        .delayUntil(c -> c.commitTransaction())
        .delayUntil(c -> c.close())
        .block();

    List<Book> result = Mono.from(this.connectionFactory.create())
        .map(connection -> connection
            .createStatement("SELECT * FROM books ORDER BY category")
        )
        .flatMapMany(statement -> statement.execute())
        .flatMapSequential(spannerResult -> spannerResult.map((r, meta) -> new Book(
            r.get("UUID", String.class),
            r.get("TITLE", String.class),
            r.get("AUTHOR", String.class),
            r.get("SYNOPSIS", String.class),
            r.get("EDITIONS", String[].class),
            r.get("FICTION", Boolean.class),
            r.get("PUBLISHED", LocalDate.class),
            r.get("WORDS_PER_SENTENCE", Double.class),
            r.get("CATEGORY", Integer.class)
        )))
        .collectList()
        .block();

    assertThat(result).isEqualTo(books);
  }

  @Test
  public void testSessionManagement() {
    assertThat(this.connectionFactory).isInstanceOf(SpannerConnectionFactory.class);

    Mono<Connection> connection = (Mono<Connection>) this.connectionFactory.create();
    SpannerConnection spannerConnection = (SpannerConnection) connection.block();
    String activeSessionName = spannerConnection.getSessionName();

    List<String> activeSessions = getSessionNames();
    assertThat(activeSessions).contains(activeSessionName);

    Mono.from(spannerConnection.close()).block();

    activeSessions = getSessionNames();
    assertThat(activeSessions).doesNotContain(activeSessionName);
  }

  @Test
  public void testRunDmlAfterTransaction() {
    SpannerConnection connection =
        Mono.from(this.connectionFactory.create())
            .cast(SpannerConnection.class)
            .block();

    StepVerifier.create(
        Mono.from(connection.beginTransaction())
            .then(Mono.just(
                connection.createStatement(
                    "INSERT BOOKS "
                        + "(UUID, TITLE, AUTHOR, CATEGORY, FICTION, "
                        + "PUBLISHED, WORDS_PER_SENTENCE)"
                        + " VALUES "
                        + "(@uuid, @title, @author, @category, @fiction, @published, @wps);")
                    .bind("uuid", "1")
                    .bind("author", "a")
                    .bind("category", 100L)
                    .bind("title", "b1")
                    .bind("fiction", true)
                    .bind("published", LocalDate.of(2008, 5, 1))
                    .bind("wps", 20.8)))
            .delayUntil(statement -> connection.commitTransaction())
            .flatMapMany(statement -> statement.execute())
            .cast(SpannerResult.class))
        .assertNext(result -> assertThat(result.getRowsUpdated().block()).isEqualTo(1))
        .verifyComplete();
  }

  @Test
  public void testDmlExceptions() {
    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(conn -> conn.createStatement("INSERT BOOKS asdfasdfasdf").execute()))
        .expectErrorMatches(err -> err.getMessage().contains("Syntax error:"))
        .verify();
  }

  @Test
  public void testMultipleDmlExceptions() {
    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(conn ->
                conn.createBatch()
                    .add("INSERT BOOKS (UUID, TITLE, AUTHOR, CATEGORY, FICTION, "
                        + "PUBLISHED, WORDS_PER_SENTENCE) VALUES ('23', 'blarg', "
                        + "'joe', 245, true, DATE '2013-12-25', 20)")
                    .add("INSERT BOOKS asdfasdfasdfjasidg")
                    .add("INSERT BOOKS (UUID, TITLE, AUTHOR, CATEGORY, FICTION, PUBLISHED, "
                        + "WORDS_PER_SENTENCE) VALUES ('24', 'blarg2', 'Bob', "
                        + "245, true, DATE '2013-12-25', 20)")
                    .execute()))
        .expectNextMatches(result -> Mono.from(result.getRowsUpdated()).block() == 1)
        .expectErrorMatches(err -> err.getMessage().contains("Syntax error:"))
        .verify();
  }

  @Test
  public void testDataIntegrityExceptions() {
    StepVerifier.create(
        Mono.from(connectionFactory.create())
            .flatMapMany(conn ->
                conn.createBatch()
                    .add("INSERT BOOKS (UUID, TITLE, AUTHOR, CATEGORY, FICTION, "
                        + "PUBLISHED, WORDS_PER_SENTENCE) VALUES ('23', 'blarg', "
                        + "'joe', 245, true, DATE '2013-12-25', 20)")
                    .add("INSERT BOOKS (UUID, TITLE, AUTHOR, CATEGORY, FICTION, PUBLISHED, "
                        + "WORDS_PER_SENTENCE) VALUES ('23', 'blarg2', 'Bob', "
                        + "245, true, DATE '2013-12-25', 20)")
                    .execute()))
        .expectErrorMatches(err -> err instanceof R2dbcDataIntegrityViolationException)
        .verify();
  }


  @Test
  public void testSingleUseDml() {
    long count = executeReadQuery(
        connectionFactory,
        "Select count(1) as count FROM books",
        (row, rowMetadata) -> row.get("count", Long.class)).get(0);
    assertThat(count).isEqualTo(0);

    // Note that there is NO call to beginTransaction or commitTransaction.
    StepVerifier.create(
        Mono.from(this.connectionFactory.create())
            .flatMapMany(c -> c.createStatement(
                "INSERT BOOKS "
                    + "(UUID, TITLE, AUTHOR, CATEGORY, FICTION, "
                    + "PUBLISHED, WORDS_PER_SENTENCE)"
                    + " VALUES "
                    + "(@uuid, @title, @author, @category, @fiction, @published, @wps);")
                .bind("uuid", "1")
                .bind("author", "a")
                .bind("category", 100L)
                .bind("title", "b1")
                .bind("fiction", true)
                .bind("published", LocalDate.of(2008, 5, 1))
                .bind("wps", 20.8)
                .add()
                .bind("uuid", "2")
                .bind("author", "b")
                .bind("category", 100L)
                .bind("title", "b2")
                .bind("fiction", false)
                .bind("published", LocalDate.of(2018, 1, 6))
                .bind("wps", 15.1)
                .add()
                .bind("uuid", "3")
                .bind("author", "c")
                .bind("category", 100L)
                .bind("title", "b3")
                .bind("fiction", false)
                .bind("published", LocalDate.of(2016, 1, 6))
                .bind("wps", 15.22).execute())
            .flatMapSequential(r -> Mono.from(r.getRowsUpdated()))
    ).expectNext(1, 1, 1)
        .verifyComplete();

    long retrieved = executeReadQuery(connectionFactory,
        "Select count(1) as count FROM books",
        (row, rowMetadata) -> row.get("count", Long.class)).get(0);
    assertThat(retrieved).isEqualTo(3);
  }

  @Test
  public void testQuerying() {
    long count = executeReadQuery(
        connectionFactory,
        "Select count(1) as count FROM books",
        (row, rowMetadata) -> row.get("count", Long.class)).get(0);
    assertThat(count).isEqualTo(0);

    StepVerifier.create(
        Mono.from(this.connectionFactory.create())
            .flatMapMany(c -> Flux.concat(
                c.beginTransaction(),
                Flux.from(c.createStatement(
                    "INSERT BOOKS "
                        + "(UUID, TITLE, AUTHOR, CATEGORY, FICTION, PUBLISHED, WORDS_PER_SENTENCE)"
                        + " VALUES "
                        + "(@uuid, @title, @author, @category, @fiction, @published, @wps);")
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
                    .execute()).flatMapSequential(r -> Mono.from(r.getRowsUpdated())),
                c.commitTransaction()))
    ).expectNext(1).expectNext(1).verifyComplete();

    StepVerifier.create(Mono.from(this.connectionFactory.create())
        .flatMapMany(c -> Flux.concat(
            c.beginTransaction(),
            Flux.from(c.createStatement(
                        "UPDATE BOOKS SET CATEGORY = @new_cat WHERE CATEGORY = @old_cat")
                        .bind("new_cat", 101L)
                        .bind("old_cat", 100L)
                        .execute()
            ).flatMap(r -> Mono.from(r.getRowsUpdated())),
            c.commitTransaction())))
        .expectNext(2)
        .verifyComplete();

    StepVerifier.create(Mono.from(this.connectionFactory.create())
        .flatMapMany(c -> Flux.concat(
            c.beginTransaction(),
            Flux.from(c.createBatch()
                        .add("UPDATE BOOKS SET CATEGORY = 102 WHERE CATEGORY = 101")
                        .add("UPDATE BOOKS SET CATEGORY = 202 WHERE CATEGORY = 201")
                        .add("UPDATE BOOKS SET CATEGORY = 302 WHERE CATEGORY = 301")
                        .execute())
                        .flatMap(r -> r.getRowsUpdated()),
            c.commitTransaction()))
    )
        .expectNext(2)
        .expectNext(0)
        .expectNext(0)
        .verifyComplete();

    List<String> authorStrings = executeReadQuery(
        connectionFactory,
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

    int rowsUpdated = executeDmlQuery(connectionFactory, "DELETE FROM books WHERE true");
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
        connectionFactory,
        "SELECT title, author FROM books where author = 'Nobody P. Smith'",
        (r, meta) -> r.get(0, String.class));

    assertThat(results).isEmpty();
  }

  @Test
  public void testMultiTransactionType() {
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

    SpannerConnection connection =
        Mono.from(this.connectionFactory.create())
            .cast(SpannerConnection.class)
            .block();

    connection.beginTransaction(
        TransactionOptions.newBuilder()
            .setPartitionedDml(PartitionedDml.getDefaultInstance())
            .build())
        .block();
    int rowsUpdated = Mono.from(
        connection.createStatement("UPDATE BOOKS SET TITLE = 'bad-book' WHERE true")
            .execute())
        .flatMap(result -> Mono.from(result.getRowsUpdated()))
        .block();
    connection.commitTransaction().block();
    assertThat(rowsUpdated).isEqualTo(2);

    connection.beginTransaction(
        TransactionOptions.newBuilder()
            .setReadOnly(ReadOnly.getDefaultInstance())
            .build())
        .block();
    List<String> titles =
        Mono.from(connection.createStatement("SELECT title FROM BOOKS").execute())
            .flatMapMany(result -> result.map((row, rowMetadata) -> row.get(0, String.class)))
            .collectList()
            .block();
    assertThat(titles).containsExactlyInAnyOrder("bad-book", "bad-book");

    connection.beginTransaction(
        TransactionOptions.newBuilder()
            .setReadWrite(ReadWrite.getDefaultInstance())
            .build())
        .block();
    Mono.from(connection.createStatement("DELETE FROM BOOKS WHERE true").execute()).block();
    connection.commitTransaction().block();
    titles =
        Mono.from(connection.createStatement("SELECT title FROM BOOKS").execute())
            .flatMapMany(result -> result.map((row, rowMetadata) -> row.get(0, String.class)))
            .collectList()
            .block();
    assertThat(titles).isEmpty();
  }

  private List<String> getSessionNames() {
    String databaseName = DatabaseName.format(ServiceOptions.getDefaultProjectId(),
        DatabaseProperties.INSTANCE, DatabaseProperties.DATABASE);

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
