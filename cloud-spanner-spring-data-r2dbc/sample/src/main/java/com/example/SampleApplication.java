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

/**
 * An example R2DBC application using the Cloud Spanner R2DBC.
 */
public class SampleApplication {

  private static final String SAMPLE_INSTANCE = "";

  private static final String SAMPLE_DATABASE = "";

  private static final String SAMPLE_PROJECT = "";

  public static void main(String[] args) {

    BookExampleApp bookExampleApp = new BookExampleApp(SAMPLE_INSTANCE, SAMPLE_DATABASE,
        SAMPLE_PROJECT);

    bookExampleApp.dropTableIfPresent();
    bookExampleApp.createTable();
    bookExampleApp.saveBooks();
    bookExampleApp.retrieveBooks();
  }
}
