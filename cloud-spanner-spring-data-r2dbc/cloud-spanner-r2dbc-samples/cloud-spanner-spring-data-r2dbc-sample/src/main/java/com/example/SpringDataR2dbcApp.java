/*
 * Copyright 2020 Google LLC
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

import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Hooks;

/**
 * Driver application showing Cloud Spanner R2DBC use with Spring Data.
 */
@SpringBootApplication
@EnableR2dbcRepositories
public class SpringDataR2dbcApp {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringDataR2dbcApp.class);

  private static final String SPANNER_INSTANCE = System.getProperty("spanner.instance");

  private static final String SPANNER_DATABASE = System.getProperty("spanner.database");

  private static final String GCP_PROJECT = System.getProperty("gcp.project");

  @Autowired
  private DatabaseClient r2dbcClient;

  public static void main(String[] args) {
    Hooks.onOperatorDebug();
    Assert.notNull(INSTANCE, "Please provide spanner.instance property");
    Assert.notNull(DATABASE, "Please provide spanner.database property");
    Assert.notNull(GCP_PROJECT, "Please provide gcp.project property");

    SpringApplication.run(SpringDataR2dbcApp.class, args);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void setUpData() {
    LOGGER.info("Setting up test table BOOK...");
    try {
      r2dbcClient
          .sql(
              "CREATE TABLE BOOK ("
                  + "  ID STRING(36) NOT NULL,"
                  + "  TITLE STRING(MAX) NOT NULL,"
                  + "  EXTRADETAILS JSON,"
                  + "  REVIEWS JSON,"
                  + "  CATEGORIES ARRAY<STRING(64)>"
                  + ") PRIMARY KEY (ID)")
          .fetch()
          .rowsUpdated()
          .block();

    } catch (Exception e) {
      LOGGER.info("Failed to set up test table BOOK", e);
      return;
    }
    LOGGER.info("Finished setting up test table BOOK");
    LOGGER.info("App Started..visit http://localhost:8080/index.html");
  }

  @EventListener({ContextClosedEvent.class})
  public void tearDownData() {
    LOGGER.info("Deleting test table BOOK...");
    try {
      r2dbcClient.sql("DROP TABLE BOOK")
          .fetch().rowsUpdated().block();
    } catch (Exception e) {
      LOGGER.info("Failed to delete test table BOOK", e);
      return;
    }

    LOGGER.info("Finished deleting test table BOOK.");
  }

  @Bean
  public RouterFunction<ServerResponse> indexRouter() {
    // Serve static index.html at root.
    return route(
        GET("/"),
        req -> ServerResponse.permanentRedirect(URI.create("/index.html")).build());
  }
}
