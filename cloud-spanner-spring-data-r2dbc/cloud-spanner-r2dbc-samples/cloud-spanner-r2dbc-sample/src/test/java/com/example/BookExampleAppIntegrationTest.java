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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.ServiceOptions;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests the sample application.
 */
class BookExampleAppIntegrationTest {

  private static final String TEST_INSTANCE =
      System.getProperty("spanner.instance", "reactivetest");

  private static final String TEST_DATABASE =
      System.getProperty("spanner.database", "testdb");

  private static PrintStream systemOut;

  private static ByteArrayOutputStream baos;

  /**
   * Saves output.
   */
  @BeforeAll
  public static void checkToRun() {
    systemOut = System.out;
    baos = new ByteArrayOutputStream();
    TeeOutputStream out = new TeeOutputStream(systemOut, baos);
    System.setOut(new PrintStream(out));
  }

  @Test
  void testOutput() {
    BookExampleApp bookExampleApp =
        new BookExampleApp(TEST_INSTANCE, TEST_DATABASE, ServiceOptions.getDefaultProjectId());

    bookExampleApp.dropTableIfPresent();
    bookExampleApp.createTable();
    bookExampleApp.saveBooks();
    bookExampleApp.retrieveBooks();
    bookExampleApp.cleanup();

    assertThat(baos.toString()).contains("Table creation completed.");
    assertThat(baos.toString()).contains("Insert books transaction committed.");
    assertThat(baos.toString()).contains("Retrieved book: book1; Title: Book One");
    assertThat(baos.toString()).contains("Retrieved book: book2; Title: Book Two");
    assertThat(baos.toString())
        .contains(
            "Retrieved book: book3; Title: Book Three; "
                + "Extra Details: {\"rating\":9,\"series\":true}");
  }
}
