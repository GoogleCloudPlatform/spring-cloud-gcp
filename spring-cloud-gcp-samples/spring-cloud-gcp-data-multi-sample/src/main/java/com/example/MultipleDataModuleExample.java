/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/** An example demonstrating the use of both Spring Data Cloud Spanner and Datastore. */
@SpringBootApplication
public class MultipleDataModuleExample {

  // Internally uses a Spring Data Datastore repository
  @Autowired private PersonService personService;

  // Internally uses a Spring Data Cloud Spanner repository
  @Autowired private TraderService traderService;

  @Autowired private SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

  @Autowired private SpannerSchemaUtils spannerSchemaUtils;

  public static void main(String[] args) {
    SpringApplication.run(MultipleDataModuleExample.class, args);
  }

  void createTablesIfNotExists() {
    if (!this.spannerDatabaseAdminTemplate.tableExists("traders_multi_sample")) {
      this.spannerDatabaseAdminTemplate.executeDdlStrings(
          List.of(
              this.spannerSchemaUtils
                  .getCreateTableDdlString(Trader.class)
                  .replaceFirst("^CREATE TABLE ", "CREATE TABLE IF NOT EXISTS ")),
          true);
    }
  }

  @Bean
  ApplicationRunner applicationRunner() {
    return args -> {
      createTablesIfNotExists();

      runWithRetry(
          () -> {
            System.out.println("Deleting all entities.");

            this.personService.deleteAll();
            this.traderService.deleteAll();

            System.out.println(
                "The number of Person entities is now: " + this.personService.count());
            System.out.println(
                "The number of Trader entities is now: " + this.traderService.count());

            System.out.println("Saving one entity with each repository.");

            this.traderService.save(new Trader("id1", "trader", "one"));
            this.personService.save(new Person(1L, "person1"));

            System.out.println(
                "The number of Person entities is now: " + this.personService.count());
            System.out.println(
                "The number of Trader entities is now: " + this.traderService.count());
          });
    };
  }

  private void runWithRetry(Runnable runnable) {
    for (int attempt = 1; attempt <= 5; attempt++) {
      try {
        runnable.run();
        return;
      } catch (Exception ex) {
        if (attempt == 5 || !isRetryable(ex)) {
          throw ex;
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw ex;
        }
      }
    }
  }

  private boolean isRetryable(Throwable ex) {
    while (ex != null) {
      String msg = ex.getMessage();
      if (msg != null
          && (msg.contains("Database schema has changed")
              || msg.contains("Table not found: traders_multi_sample")
              || msg.contains("ABORTED"))) {
        return true;
      }
      ex = ex.getCause();
    }
    return false;
  }
}
