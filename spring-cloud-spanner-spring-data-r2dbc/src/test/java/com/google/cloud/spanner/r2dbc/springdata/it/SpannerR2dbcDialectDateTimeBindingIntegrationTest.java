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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.test.StepVerifier;

/**
 * Integration tests verifying the {@link Timestamp},{@link Date} support of {@link
 * SpannerR2dbcDialect}.
 */
@EnabledIfSystemProperty(named = "it.spanner", matches = "true")
class SpannerR2dbcDialectDateTimeBindingIntegrationTest extends AbstractBaseSpannerR2dbcIntegrationTest {
  /** Initializes the integration test environment for the Spanner R2DBC dialect. */
  @BeforeEach
  void initializeTestEnvironment() {
    super.initializeTestEnvironment(
        SpannerR2dbcDialectDateTimeBindingIntegrationTest.TestConfiguration.class,
        "create table card ("
            + "  id int64 not null,"
            + "  expiry_year int64 not null,"
            + "  expiry_month int64 not null,"
            + "  issue_date date not null,"
            + "  requested_at timestamp not null"
            + ") primary key (id)");
  }

  @Test
  void shouldReadWriteDateAndTimestampTypes() {
    Card card =
        new Card(
            1L,
            2022,
            12,
            LocalDate.parse("2021-12-31"),
            LocalDateTime.parse("2021-12-15T21:30:10"));

    this.contextRunner.run(ctx -> {
      R2dbcEntityTemplate r2dbcEntityTemplate = ctx.getBean(R2dbcEntityTemplate.class);

      r2dbcEntityTemplate
          .insert(Card.class)
          .using(card)
          .then()
          .as(StepVerifier::create)
          .verifyComplete();

      r2dbcEntityTemplate
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
    });
  }

  /** Register custom converters. */
  @Configuration
  static class TestConfiguration extends AbstractR2dbcConfiguration {
    @Value("${testDatabase}")
    private String testDatabase;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
      return ConnectionFactories.get(
          ConnectionFactoryOptions.builder()
              .option(Option.valueOf("project"), ServiceOptions.getDefaultProjectId())
              .option(PROJECT, PROJECT_NAME)
              .option(DRIVER, DRIVER_NAME)
              .option(INSTANCE, TEST_INSTANCE)
              .option(DATABASE, testDatabase)
              .build());
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
