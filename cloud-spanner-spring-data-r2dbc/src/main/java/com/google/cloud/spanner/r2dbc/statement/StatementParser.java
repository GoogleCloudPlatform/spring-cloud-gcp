/*
 * Copyright 2019 Google LLC
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

import java.util.Arrays;
import java.util.List;

/**
 * Parses SQL statements to determine what type of statement it is.
 */
public class StatementParser {

  /** Matches on the Spanner SQL hints in the form @{hintName...} */
  private static final String SQL_OPTIONS_REGEX = "(?m)@\\{.*\\}";

  private static final List<String> SELECT_STATEMENTS = Arrays.asList("select");
  private static final List<String> DDL_STATEMENTS = Arrays.asList("create", "drop", "alter");
  private static final List<String> DML_STATEMENTS = Arrays.asList("insert", "update", "delete");

  private StatementParser() {}

  /**
   * Returns the statement type of a given SQL string.
   */
  public static StatementType getStatementType(String sql) {
    String processedSql = processSql(sql);

    if (statementStartsWith(processedSql, SELECT_STATEMENTS)) {
      return StatementType.SELECT;
    } else if (statementStartsWith(processedSql, DDL_STATEMENTS)) {
      return StatementType.DDL;
    } else if (statementStartsWith(processedSql, DML_STATEMENTS)) {
      return StatementType.DML;
    } else {
      return StatementType.UNKNOWN;
    }
  }

  private static String processSql(String rawSql) {
    return rawSql
        .replaceAll(SQL_OPTIONS_REGEX, "")
        .trim()
        .toLowerCase();
  }

  private static boolean statementStartsWith(String sqlStatement, List<String> prefixes) {
    return prefixes.stream().anyMatch(sqlPrefix -> sqlStatement.startsWith(sqlPrefix));
  }
}
