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

package com.example;

import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.DRIVER_NAME;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The main class for the functions of the sample application.
 */
public class BookExampleApp {

  private final ConnectionFactory connectionFactory;

  /**
   * Constructor.
   *
   * @param sampleInstance the sample instance to use.
   * @param sampleDatabase the sample database to use.
   * @param sampleProjectId the sample project to use.
   */
  public BookExampleApp(String sampleInstance, String sampleDatabase,
      String sampleProjectId) {
    this.connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
        .option(Option.valueOf("project"), sampleProjectId)
        .option(DRIVER, DRIVER_NAME)
        .option(INSTANCE, sampleInstance)
        .option(DATABASE, sampleDatabase)
        .build());
  }

  /**
   * Creates a table named BOOKS.
   */
  public void createTable() {
    Mono.from(this.connectionFactory.create())
        .delayUntil(c -> c.createStatement("CREATE TABLE BOOKS ("
            + "  ID STRING(20) NOT NULL,"
            + "  TITLE STRING(MAX) NOT NULL"
            + ") PRIMARY KEY (ID)").execute())
        .doOnNext(x -> System.out.println("Table creation completed."))
        .block();
  }

  /**
   * Saves two books.
   */
  public void saveBooks() {
    Mono.from(this.connectionFactory.create())
        .delayUntil(c -> c.beginTransaction())
        .delayUntil(c ->
            Mono.fromRunnable(() ->
                Flux.from(c.createStatement(
                    "INSERT BOOKS "
                        + "(ID, TITLE)"
                        + " VALUES "
                        + "(@id, @title)")
                    .bind("id", "book1")
                    .bind("title", "Book One")
                    .add()
                    .bind("id", "book2")
                    .bind("title", "Book Two")
                    .execute())
                    .flatMapSequential(r -> Mono.from(r.getRowsUpdated()))
                    .collectList().block()
            )
        )
        .delayUntil(c -> c.commitTransaction())
        .doOnNext(x -> System.out.println("Insert books transaction committed."))
        .block();
  }

  /**
   * Finds books in the table named BOOKS.
   */
  public void retrieveBooks() {
    Mono.from(this.connectionFactory.create())
        .flatMapMany(connection -> connection
            .createStatement("SELECT * FROM books").execute())
        .flatMap(spannerResult -> spannerResult.map(
            (r, meta) -> "Retrieved book: " + r.get("ID", String.class) + " " + r
                .get("TITLE", String.class)
        ))
        .doOnNext(System.out::println)
        .collectList()
        .block();
  }

  /**
   * Drops the BOOKS table.
   */
  public void dropTableIfPresent() {
    try {
      Mono.from(this.connectionFactory.create())
          .delayUntil(c -> c.createStatement("DROP TABLE BOOKS").execute())
          .doOnNext(x -> System.out.println("Table drop completed."))
          .block();
    } catch (Exception e) {
      System.out.println("Table wasn't found, so no action was taken.");
    }
  }
}
