/*
 * Copyright 2022-2022 Google LLC
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
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.PROJECT;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;

import com.google.cloud.Date;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.r2dbc.springdata.SpannerR2dbcDialect;
import com.google.cloud.spanner.r2dbc.springdata.it.entities.Card;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Integration tests verifying the {@link Timestamp},{@link Date} support of {@link
 * SpannerR2dbcDialect}.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = SpannerR2dbcDialectDateTimeBindingIntegrationTest.TestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpannerR2dbcDialectDateTimeBindingIntegrationTest {

  private static final Logger log =
      LoggerFactory.getLogger(SpannerR2dbcDialectDateTimeBindingIntegrationTest.class);

  private static final String PROJECT_NAME =
      System.getProperty("gcp.project", ServiceOptions.getDefaultProjectId());
  private static final String DRIVER_NAME = "spanner";

  private static final String TEST_INSTANCE =
      System.getProperty("spanner.instance", "reactivetest");

  private static final String TEST_DATABASE = System.getProperty("spanner.database", "testdb");

  private static final ConnectionFactory connectionFactory =
      ConnectionFactories.get(
          ConnectionFactoryOptions.builder()
              .option(Option.valueOf("project"), ServiceOptions.getDefaultProjectId())
              .option(PROJECT, PROJECT_NAME)
              .option(DRIVER, DRIVER_NAME)
              .option(INSTANCE, TEST_INSTANCE)
              .option(DATABASE, TEST_DATABASE)
              .build());

  @Autowired private R2dbcEntityTemplate r2dbcEntityTemplate;

  /** Initializes the integration test environment for the Spanner R2DBC dialect. */
  @BeforeAll
  static void initializeTestEnvironment() {
    Mono.from(connectionFactory.create())
      .flatMap(c -> Mono.from(c.createStatement("drop table card").execute())
        .doOnSuccess(x -> log.info("Table drop completed."))
        .doOnError(
            x -> {
              if (!x.getMessage().contains("Table not found")) {
                log.info("Table drop failed. {}", x.getMessage());
              }
            }
        )
        .onErrorResume(x -> Mono.empty())
        .thenReturn(c)
      )
      .flatMap(c -> Mono.from(c.createStatement(
          "create table card ("
              + "  id int64 not null,"
              + "  expiry_year int64 not null,"
              + "  expiry_month int64 not null,"
              + "  issue_date date not null,"
              + "  requested_at timestamp not null"
              + ") primary key (id)")
          .execute())
          .doOnSuccess(x -> log.info("Table creation completed."))
      ).block();
  }

  @AfterAll
  static void cleanupTableAfterTest() {
    Mono.from(connectionFactory.create())
      .flatMap(c -> Mono.from(c.createStatement("drop table card").execute())
          .doOnSuccess(x -> log.info("Table drop completed."))
          .doOnError(x -> log.info("Table drop failed."))
      ).block();
  }

  @Test
  void shouldReadWriteDateAndTimestampTypes() {
    Card card = new Card(1L, 2022, 12,
        LocalDate.parse("2021-12-31"),
        LocalDateTime.parse("2021-12-15T21:30:10"));

    this.r2dbcEntityTemplate
        .insert(Card.class)
        .using(card)
        .then()
        .as(StepVerifier::create)
        .verifyComplete();

    this.r2dbcEntityTemplate
        .select(Card.class)
        .first()
        .as(StepVerifier::create)
        .expectNextMatches(
            c ->
                c.getId() == 1L
                    && c.getExpiryYear() == 2022
                    && c.getExpiryMonth() == 12
                    && c.getIssueDate().equals(LocalDate.parse("2021-12-31"))
                    && c.getRequestedAt().equals(LocalDateTime.parse("2021-12-15T21:30:10")))
        .verifyComplete();
  }

  /** Register custom converters. */
  @Configuration
  static class TestConfiguration extends AbstractR2dbcConfiguration {

    @Autowired ApplicationContext applicationContext;

    @Override
    public ConnectionFactory connectionFactory() {
      return connectionFactory;
    }

    @Bean
    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
      return new R2dbcCustomConversions(
          getStoreConversions(),
          Arrays.asList(
              new DateToLocalDateConverter(),
              new LocalDateToDateConverter(),
              new LocalDateTimeToTimestampConverter(),
              new TimestampToLocalDateTimeConverter()));
    }
  }

  /** {@link Date} to {@link LocalDate} reading converter. */
  @ReadingConverter
  static class DateToLocalDateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate convert(Date row) {
      return LocalDate.parse(row.toString());
    }
  }

  /** {@link LocalDate} to {@link Date} writing converter. */
  @WritingConverter
  static class LocalDateToDateConverter implements Converter<LocalDate, Date> {

    @Override
    public Date convert(LocalDate source) {
      return Date.parseDate(source.toString());
    }
  }

  /** {@link Timestamp} to {@link LocalDateTime} reading converter. */
  @ReadingConverter
  static class TimestampToLocalDateTimeConverter implements Converter<Timestamp, LocalDateTime> {

    @Override
    public LocalDateTime convert(Timestamp row) {
      return OffsetDateTime.parse(row.toString()).toLocalDateTime();
    }
  }

  /** {@link LocalDateTime} to {@link Timestamp} writing converter. */
  @WritingConverter
  static class LocalDateTimeToTimestampConverter implements Converter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convert(LocalDateTime source) {
      return Timestamp.parseTimestamp(source.toString());
    }
  }
}
