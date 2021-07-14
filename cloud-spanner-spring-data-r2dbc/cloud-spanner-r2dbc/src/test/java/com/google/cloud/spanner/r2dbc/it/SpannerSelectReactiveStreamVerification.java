/*
 * Copyright 2021-2021 Google LLC
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

import com.google.cloud.ServiceOptions;
import io.r2dbc.spi.Closeable;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.Row;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class SpannerSelectReactiveStreamVerification extends
    PublisherVerification<Row> {

  private static ConnectionFactory connectionFactory;

  private static TestDatabaseHelper dbHelper;

  @BeforeSuite
  static void createConnectionFactory() {
    connectionFactory = ConnectionFactories.get(
        ConnectionFactoryOptions.builder()
            .option(Option.valueOf("project"), ServiceOptions.getDefaultProjectId())
            .option(DRIVER, DRIVER_NAME)
            .option(INSTANCE, DatabaseProperties.INSTANCE)
            .option(DATABASE, DatabaseProperties.DATABASE)
            .build());

    dbHelper = new TestDatabaseHelper(connectionFactory);
    dbHelper.createTableIfNecessary();
    dbHelper.clearTestData();
    dbHelper.addTestData(30);
  }

  @AfterSuite
  static void closeConnectionFactory() {
    ((Closeable) connectionFactory).close();
  }

  public SpannerSelectReactiveStreamVerification() {
    super(new TestEnvironment(200));
  }

  @Override
  public Publisher<Row> createPublisher(long l) {
    return Mono.from(connectionFactory.create())
        .flatMapMany(conn ->
            Flux.from(
                  conn.createStatement("SELECT * FROM BOOKS ORDER BY TITLE LIMIT " + l).execute())
                .flatMap(rs -> rs.map((r, rm) -> r), 1, /* turn off prefetch */ 1)
        // not closing connection to avoid changing demand (delayUntil uses buffer of 32)
        //.delayUntil(r -> conn.close())
        );
  }

  @Override
  public Publisher<Row> createFailedPublisher() {
    return Mono.from(connectionFactory.create())
        .flatMapMany(conn -> conn.createStatement("SELECT * FROM bad SQL no cookie").execute())
        .flatMap(rs -> rs.map((r, rm) -> r));
  }

  @Override
  public long maxElementsFromPublisher() {
    // only 30 rows set up in the database
    return 30;
  }


}
