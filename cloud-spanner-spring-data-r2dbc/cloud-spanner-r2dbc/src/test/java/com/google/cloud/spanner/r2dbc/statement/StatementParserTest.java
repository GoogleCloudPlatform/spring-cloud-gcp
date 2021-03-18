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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatementParserTest {

  @Test
  void parsesSelectQueries() {
    String sql = "SELECT * from blahblahblah";
    assertThat(StatementParser.getStatementType(sql)).isEqualTo(StatementType.SELECT);
  }

  @Test
  void parsesDmlQueries() {
    String sql = "InSeRt INTO TABLE_NAME VALUES (value1, value2, value3, ...valueN)";
    assertThat(StatementParser.getStatementType(sql)).isEqualTo(StatementType.DML);

    sql = "DELETE FROM target_name WHERE true";
    assertThat(StatementParser.getStatementType(sql)).isEqualTo(StatementType.DML);

    sql = "UPDATE Singers\n"
        + "SET BirthDate = '1990-10-10'\n"
        + "WHERE FirstName = 'Marc' AND LastName = 'Richards'";
    assertThat(StatementParser.getStatementType(sql)).isEqualTo(StatementType.DML);
  }

  @Test
  void parsesDdlQueries() {
    String sql = "drop table Blarg";
    assertThat(StatementParser.getStatementType(sql)).isEqualTo(StatementType.DDL);

    sql = "CREATE table foobar5000";
    assertThat(StatementParser.getStatementType(sql)).isEqualTo(StatementType.DDL);

    sql = "ALTER TABLE table Add Column name STRING";
    assertThat(StatementParser.getStatementType(sql)).isEqualTo(StatementType.DDL);
  }

  @Test
  void parseUnknownQuery() {
    String sql = "int number = 5";
    assertThat(StatementParser.getStatementType(sql)).isEqualTo(StatementType.UNKNOWN);
  }

  @Test
  void parseQueryWithOptionsPrefix() {
    String sql = "@{FORCE_INDEX=index_name} @{JOIN_METHOD=HASH_JOIN} SELECT * FROM blahblah";
    assertThat(StatementParser.getStatementType(sql)).isEqualTo(StatementType.SELECT);
  }
}
