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

package com.google.cloud.spring.bigquery.core;

import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.Schema;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Defines operations for use with BigQuery.
 *
 * @since 1.2
 */
public interface BigQueryOperations {

  /**
   * Writes data to a specified BigQuery table.
   *
   * @param tableName name of the table to write to
   * @param inputStream input stream of the table data to write
   * @param dataFormatOptions the format of the data to write
   * @return {@link CompletableFuture} containing the BigQuery Job indicating completion of operation
   * @throws BigQueryException if errors occur when loading data to the BigQuery table
   */
  CompletableFuture<Job> writeDataToTable(
      String tableName, InputStream inputStream, FormatOptions dataFormatOptions);

  /**
   * Writes data to a specified BigQuery table with a manually-specified table Schema.
   *
   * <p>Example:
   *
   * <pre>{@code
   * Schema schema = Schema.of(
   *    Field.of("CountyId", StandardSQLTypeName.INT64),
   *    Field.of("State", StandardSQLTypeName.STRING),
   *    Field.of("County", StandardSQLTypeName.STRING)
   * );
   *
   * CompletableFuture<Job> bigQueryJobFuture =
   *     bigQueryTemplate.writeDataToTable(
   *          TABLE_NAME, dataFile.getInputStream(), FormatOptions.csv(), schema);
   * }</pre>
   *
   * @param tableName name of the table to write to
   * @param inputStream input stream of the table data to write
   * @param dataFormatOptions the format of the data to write
   * @param schema the schema of the table being loaded
   * @return {@link CompletableFuture} containing the BigQuery Job indicating completion of operation
   * @throws BigQueryException if errors occur when loading data to the BigQuery table
   */
  CompletableFuture<Job> writeDataToTable(
      String tableName, InputStream inputStream, FormatOptions dataFormatOptions, Schema schema);

  /**
   * This method uses BigQuery Storage Write API to write new line delimited JSON file to the
   * specified table. The Table should already be created as BigQuery Storage Write API doesn't
   * create it automatically.
   *
   * @param tableName name of the table to write to
   * @param jsonInputStream input stream of the json file to be written
   * @return {@link CompletableFuture} containing the WriteApiResponse indicating completion of
   *     operation
   */
  CompletableFuture<WriteApiResponse> writeJsonStream(String tableName, InputStream jsonInputStream);

  /**
   * This method uses BigQuery Storage Write API to write new line delimited JSON file to the
   * specified table. This method creates a table with the specified schema.
   *
   * @param tableName name of the table to write to
   * @param jsonInputStream input stream of the json file to be written
   * @return {@link CompletableFuture} containing the WriteApiResponse indicating completion of
   *     operation
   */
  CompletableFuture<WriteApiResponse> writeJsonStream(
      String tableName, InputStream jsonInputStream, Schema schema);
}
