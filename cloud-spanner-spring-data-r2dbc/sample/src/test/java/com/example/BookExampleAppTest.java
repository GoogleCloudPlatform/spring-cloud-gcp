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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.ServiceOptions;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the sample application.
 */
public class BookExampleAppTest {

  public static final String TEST_INSTANCE = "reactivetest";

  public static final String TEST_DATABASE = "testdb";

  private static PrintStream systemOut;

  private static ByteArrayOutputStream baos;

  @BeforeClass
  public static void checkToRun() {
    systemOut = System.out;
    baos = new ByteArrayOutputStream();
    TeeOutputStream out = new TeeOutputStream(systemOut, baos);
    System.setOut(new PrintStream(out));
  }

  @Test
  public void testOutput() {
    BookExampleApp bookExampleApp = new BookExampleApp(TEST_INSTANCE, TEST_DATABASE, ServiceOptions
        .getDefaultProjectId());

    bookExampleApp.dropTableIfPresent();
    bookExampleApp.createTable();
    bookExampleApp.saveBooks();
    bookExampleApp.retrieveBooks();

    assertThat(baos.toString()).contains("Table creation completed.");
    assertThat(baos.toString()).contains("Insert books transaction committed.");
    assertThat(baos.toString()).contains("Retrieved book: book1 Book One");
    assertThat(baos.toString()).contains("Retrieved book: book2 Book Two");
  }
}
