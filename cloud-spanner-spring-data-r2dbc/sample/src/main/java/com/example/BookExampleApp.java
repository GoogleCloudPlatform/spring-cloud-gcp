/*
 * Copyright 2019 Google LLC
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
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

  public void createTable() {
    Mono.from(connectionFactory.create())
        .delayUntil(c -> c.createStatement("CREATE TABLE BOOKS ("
            + "  ID STRING(20) NOT NULL,"
            + "  TITLE STRING(MAX) NOT NULL"
            + ") PRIMARY KEY (ID)").execute())
        .doOnNext(x -> System.out.println("Table creation completed."))
        .block();
  }

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

  public void dropTableIfPresent() {
    try {
      Mono.from(connectionFactory.create())
          .delayUntil(c -> c.createStatement("DROP TABLE BOOKS").execute())
          .doOnNext(x -> System.out.println("Table drop completed."))
          .block();
    } catch (Exception e) {
      System.out.println("Table wasn't found, so no action was taken.");
    }
  }
}
