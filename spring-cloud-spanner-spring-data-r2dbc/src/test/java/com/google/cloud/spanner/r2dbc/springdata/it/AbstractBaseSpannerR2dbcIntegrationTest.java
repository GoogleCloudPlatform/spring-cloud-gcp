/*
 * Copyright 2024-2024 Google LLC
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

import com.google.cloud.ServiceOptions;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.r2dbc.springdata.SpannerR2dbcDialect;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Abstract base for Spanner R2DBC integration tests of {@link SpannerR2dbcDialect}.
 */
abstract class AbstractBaseSpannerR2dbcIntegrationTest {
  private static final Logger log =
      LoggerFactory.getLogger(AbstractBaseSpannerR2dbcIntegrationTest.class);
  protected static final String PROJECT_NAME =
      System.getProperty("gcp.project", ServiceOptions.getDefaultProjectId());
  protected static final String DRIVER_NAME = "spanner";
  protected static final String TEST_INSTANCE =
      System.getProperty("spanner.instance", "reactivetest");
  protected String testDatabase;
  protected Spanner spanner;
  protected ApplicationContextRunner contextRunner;

  /** Initializes the integration test environment for the Spanner R2DBC dialect. */
  void initializeTestEnvironment(Class configurationClass, String tableDdl) {
    this.testDatabase = "testdb_" + UUID.randomUUID().toString().replace('-', '_').substring(0, 20);

    this.spanner = SpannerOptions.newBuilder().setProjectId(PROJECT_NAME).build().getService();

    if (configurationClass != null) {
      this.contextRunner =
          new ApplicationContextRunner()
              .withPropertyValues("testDatabase=" + testDatabase)
              .withConfiguration(
                  UserConfigurations.of(
                      configurationClass));
    }

    log.info("Creating database {}", testDatabase);
    spanner.getDatabaseAdminClient().createDatabase(
        TEST_INSTANCE, testDatabase, List.of(tableDdl));
    log.info("Done creating database {}", testDatabase);
  }

  void initializeTestEnvironment(String tableDdl) {
    initializeTestEnvironment(null, tableDdl);
  }

  @AfterEach
  void cleanupTestDatabaseAfterTest() {
    log.info("Deleting database {}", testDatabase);
    this.spanner.getDatabaseAdminClient().dropDatabase(TEST_INSTANCE, testDatabase);
    log.info("Done deleting database {}", testDatabase);
    this.spanner.close();
  }
}
