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

package com.google.cloud.spanner.r2dbc.it;

import static io.r2dbc.spi.test.TestKit.TestStatement.INSERT_BLOB_VALUE_PLACEHOLDER;
import static io.r2dbc.spi.test.TestKit.TestStatement.INSERT_CLOB_VALUE_PLACEHOLDER;
import static io.r2dbc.spi.test.TestKit.TestStatement.INSERT_TWO_COLUMNS;
import static io.r2dbc.spi.test.TestKit.TestStatement.INSERT_VALUE100;
import static io.r2dbc.spi.test.TestKit.TestStatement.INSERT_VALUE200;
import static io.r2dbc.spi.test.TestKit.TestStatement.INSERT_VALUE_PLACEHOLDER;
import static io.r2dbc.spi.test.TestKit.TestStatement.SELECT_VALUE_TWO_COLUMNS;

import io.r2dbc.spi.test.TestKit.TestStatement;
import java.util.HashMap;
import java.util.Map;

class SpannerTestKitStatements {

  static final Map<TestStatement, String> STATEMENTS = new HashMap<>();

  static {
    STATEMENTS.put(INSERT_VALUE100, "INSERT INTO test (test_value) VALUES (100)");
    STATEMENTS.put(INSERT_VALUE200, "INSERT INTO test (test_value) VALUES (200)");
    STATEMENTS.put(INSERT_TWO_COLUMNS,
        "INSERT INTO test_two_column (col1, col2) VALUES (100, 'hello')");
    STATEMENTS.put(INSERT_BLOB_VALUE_PLACEHOLDER, "INSERT INTO blob_test VALUES (?)");
    STATEMENTS.put(INSERT_CLOB_VALUE_PLACEHOLDER, "INSERT INTO clob_test VALUES (?)");
    STATEMENTS.put(INSERT_VALUE_PLACEHOLDER, "INSERT INTO test (test_value) VALUES (%s)");

    // Spanner column names are case-sensitive
    STATEMENTS.put(SELECT_VALUE_TWO_COLUMNS,
        "SELECT col1 AS test_value, col2 AS TEST_VALUE FROM test_two_column");
  }

  static String expand(TestStatement statement, Object... args) {
    String sql = STATEMENTS.get(statement);
    if (sql == null) {
      sql = statement.getSql();
    }

    return String.format(sql, args);
  }
}
