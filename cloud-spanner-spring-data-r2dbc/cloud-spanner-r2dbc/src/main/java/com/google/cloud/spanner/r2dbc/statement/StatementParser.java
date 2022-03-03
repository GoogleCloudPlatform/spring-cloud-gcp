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

package com.google.cloud.spanner.r2dbc.statement;

import com.google.cloud.spanner.Dialect;

/**
 * Parses SQL statements to determine what type of statement it is.
 */
public class StatementParser {

  private static com.google.cloud.spanner.connection.AbstractStatementParser clientLibraryParser =
      com.google.cloud.spanner.connection.AbstractStatementParser.getInstance(
          Dialect.GOOGLE_STANDARD_SQL);

  private StatementParser() {
    // Prevent instantiation.
  }

  /**
   * Returns the statement type of a given SQL string.
   *
   * @param sql the input SQL string.
   *
   * @return the type of statement of the SQL string.
   */
  public static StatementType getStatementType(String sql) {
    if (clientLibraryParser.isQuery(sql)) {
      return StatementType.SELECT;
    } else if (clientLibraryParser.isDdlStatement(sql)) {
      return StatementType.DDL;
    } else if (clientLibraryParser.isUpdateStatement(sql)) {
      return StatementType.DML;
    } else {
      return StatementType.UNKNOWN;
    }
  }
}
