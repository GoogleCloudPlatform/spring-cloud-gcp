/*
 * Copyright 2021-2022 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/** Tests for {@link R2dbcCloudSqlEnvironmentPostProcessor}. */
class R2dbcCloudSqlEnvironmentPostProcessorTests {

  private R2dbcCloudSqlEnvironmentPostProcessor r2dbcPostProcessor =
      new R2dbcCloudSqlEnvironmentPostProcessor();

  ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withInitializer(
              configurableApplicationContext ->
                  r2dbcPostProcessor.postProcessEnvironment(
                      configurableApplicationContext.getEnvironment(), new SpringApplication()));

  @Test
  void testCreateUrl_mySql() {
    GcpCloudSqlProperties properties = new GcpCloudSqlProperties();
    properties.setDatabaseName("my-database");
    properties.setInstanceConnectionName("my-instance-connection-name");
    String r2dbcUrl = r2dbcPostProcessor.createUrl(DatabaseType.MYSQL, properties);
    assertThat(r2dbcUrl).isEqualTo("r2dbc:gcp:mysql://my-instance-connection-name/my-database");
  }

  @Test
  void testCreateUrl_postgres() {
    GcpCloudSqlProperties properties = new GcpCloudSqlProperties();
    properties.setDatabaseName("my-database");
    properties.setInstanceConnectionName("my-instance-connection-name");
    String r2dbcUrl = r2dbcPostProcessor.createUrl(DatabaseType.POSTGRESQL, properties);
    assertThat(r2dbcUrl).isEqualTo("r2dbc:gcp:postgres://my-instance-connection-name/my-database");
  }

  @Test
  void testSetR2dbcProperty_mySql_defaultUsername() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.sql.databaseName=my-database",
            "spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance")
        .run(
            context -> {
              assertThat(context.getEnvironment().getProperty("spring.r2dbc.url"))
                  .isEqualTo("r2dbc:gcp:mysql://my-project:region:my-instance/my-database");
              assertThat(context.getEnvironment().getProperty("spring.r2dbc.username"))
                  .isEqualTo("root");
            });
  }

  @Test
  void testSetR2dbcProperty_mySql_usernameProvided() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.sql.databaseName=my-database",
            "spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance",
            "spring.r2dbc.username=my-username")
        .run(
            context -> {
              assertThat(context.getEnvironment().getProperty("spring.r2dbc.url"))
                  .isEqualTo("r2dbc:gcp:mysql://my-project:region:my-instance/my-database");
              assertThat(context.getEnvironment().getProperty("spring.r2dbc.username"))
                  .isEqualTo("my-username");
            });
  }

  @Test
  void testSetR2dbcProperty_mySql_urlProvidedByUserIgnored() {
    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.sql.databaseName=my-database",
            "spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance",
            "spring.r2dbc.url=ignored")
        .run(
            context -> {
              assertThat(context.getEnvironment().getProperty("spring.r2dbc.url"))
                  .isEqualTo("r2dbc:gcp:mysql://my-project:region:my-instance/my-database");
            });
  }

  @Test
  void testSetR2dbcProperty_postgres() {
    verifyThatCorrectUrlAndUsernameSet(new String[] {"io.r2dbc.postgresql"},
        "postgres",
        "r2dbc:gcp:postgres://my-project:region:my-instance/my-database");
  }

  @Test
  void testSetR2dbcProperty_mysql() {
    verifyThatCorrectUrlAndUsernameSet(new String[] {"dev.miku.r2dbc.mysql"},
        "root",
        "r2dbc:gcp:mysql://my-project:region:my-instance/my-database");
  }

  /**
   * Verifies that correct database properties got injected into context, given a passed-in list of
   * packages to retain on the classpath.
   *
   * @param driverPackagesToInclude a list of driver packages to keep on the classpath
   * @param username expected {@code spring.r2dbc.username} value to verify
   * @param url expected {@code spring.r2dbc.username} value to verify
   */
  private void verifyThatCorrectUrlAndUsernameSet(
      String[] driverPackagesToInclude, String username, String url) {
    // Because `FilteredClassLoader` accepts a list of packages to remove from classpath,
    // `driverPackagesToInclude` is used to calculate the inverse list of packages to _exclude_.
    Set<String> driverPackagesToExclude = new HashSet<>(Arrays.asList(
        "dev.miku.r2dbc.mysql",
        "io.r2dbc.postgresql"
    ));
    driverPackagesToExclude.removeAll(Arrays.asList(driverPackagesToInclude));

    this.contextRunner
        .withPropertyValues(
            "spring.cloud.gcp.sql.databaseName=my-database",
            "spring.cloud.gcp.sql.instanceConnectionName=my-project:region:my-instance")
        .withClassLoader(new FilteredClassLoader(driverPackagesToExclude.toArray(new String[0])))
        .run(
            context -> {
              assertThat(context.getEnvironment().getProperty("spring.r2dbc.url"))
                  .isEqualTo(url);
              assertThat(context.getEnvironment().getProperty("spring.r2dbc.username"))
                  .isEqualTo(username);
            });
  }

  @Test
  void testGetEnabledDatatype_noR2dbcConnectorsPresent() {
    this.contextRunner
        .withClassLoader(
            new FilteredClassLoader(
                "com.google.cloud.sql.core.GcpConnectionFactoryProviderMysql",
                "com.google.cloud.sql.core.GcpConnectionFactoryProviderPostgres"))
        .run(
            context -> {
              assertThat(r2dbcPostProcessor.getEnabledDatabaseType(context.getEnvironment()))
                  .isNull();
            });
  }

  @Test
  void testGetEnabledDatatype_noConnectionFactoryPresent() {
    this.contextRunner
        .withClassLoader(new FilteredClassLoader("io.r2dbc.spi.ConnectionFactory"))
        .run(
            context -> {
              assertThat(r2dbcPostProcessor.getEnabledDatabaseType(context.getEnvironment()))
                  .isNull();
            });
  }

  @Test
  void testGetEnabledDatatype_r2dbcDisabled() {
    this.contextRunner
        .withPropertyValues("spring.cloud.gcp.sql.r2dbc.enabled=false")
        .run(
            context -> {
              assertThat(r2dbcPostProcessor.getEnabledDatabaseType(context.getEnvironment()))
                  .isNull();
            });
  }
}
