/*
 * Copyright 2017-2020 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.sql;

/** Enum class containing MySQL and Postgresql constants. */
public enum DatabaseType {
  /** MySQL constants. */
  MYSQL(
      "com.mysql.cj.jdbc.Driver",
      "jdbc:mysql://google/%s?"
          + "socketFactory=com.google.cloud.sql.mysql.SocketFactory"
          + "&cloudSqlInstance=%s",
      "r2dbc:gcp:mysql://%s/%s",
      "root"),

  /** Postgresql constants. */
  POSTGRESQL(
      "org.postgresql.Driver",
      "jdbc:postgresql://google/%s?"
          + "socketFactory=com.google.cloud.sql.postgres.SocketFactory"
          + "&cloudSqlInstance=%s",
      "r2dbc:gcp:postgres://%s/%s",
      "postgres");

  private final String jdbcDriverName;

  private final String jdbcUrlTemplate;

  private final String r2dbcUrlTemplate;

  private final String defaultUsername;

  DatabaseType(
      String jdbcDriverName,
      String jdbcUrlTemplate,
      String r2dbcUrlTemplate,
      String defaultUsername) {
    this.jdbcDriverName = jdbcDriverName;
    this.jdbcUrlTemplate = jdbcUrlTemplate;
    this.r2dbcUrlTemplate = r2dbcUrlTemplate;
    this.defaultUsername = defaultUsername;
  }

  public String getJdbcDriverName() {
    return this.jdbcDriverName;
  }

  public String getJdbcUrlTemplate() {
    return this.jdbcUrlTemplate;
  }

  public String getR2dbcUrlTemplate() {
    return this.r2dbcUrlTemplate;
  }

  public String getDefaultUsername() {
    return defaultUsername;
  }
}
