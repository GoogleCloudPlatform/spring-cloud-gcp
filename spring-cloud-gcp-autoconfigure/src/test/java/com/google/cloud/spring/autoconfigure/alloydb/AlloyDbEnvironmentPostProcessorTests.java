/*
 * Copyright 2024 Google LLC
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

package com.google.cloud.spring.autoconfigure.alloydb;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;

class AlloyDbEnvironmentPostProcessorTests {

  private AlloyDbEnvironmentPostProcessor initializer = new AlloyDbEnvironmentPostProcessor();

  private static final String INSTANCE_URI = "projects/test-proj/locations/us-central1/clusters/test-cluster/instances/test-instance";
  private static final String SERVICE_ENDPOINT = "googleapis.example.com:443";

  private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withPropertyValues("spring.cloud.gcp.alloydb.database-name=test-database")
      .withInitializer(configurableApplicationContext -> initializer.postProcessEnvironment(
          configurableApplicationContext.getEnvironment(), new SpringApplication()))
      .withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class))
      .withUserConfiguration(Config.class);

  @Test
  void testAlloyDbDataSource() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.datasource.password=")
        .run(
            context -> {
              HikariDataSource dataSource = (HikariDataSource) context.getBean(DataSource.class);
              assertThat(dataSource.getDriverClassName()).matches("org.postgresql.Driver");
              assertThat(dataSource.getJdbcUrl())
                  .isEqualTo(
                      "jdbc:postgresql:///test-database?"
                          + "socketFactory=com.google.cloud.alloydb.SocketFactory"
                          + String.format("&alloydbInstanceName=%s", INSTANCE_URI));
              assertThat(dataSource.getUsername()).matches("postgres");
              assertThat(dataSource.getPassword()).isNull();
            });
  }

  @Test
  void testAlloyDbSpringDataSourceUrlPropertyOverride() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.datasource.password=",
            "spring.datasource.url=jdbc:h2:mem:none;MODE=MySQL;DB_CLOSE_ON_EXIT=FALSE")
        .run(
            context -> {
              HikariDataSource dataSource = (HikariDataSource) context.getBean(DataSource.class);
              assertThat(dataSource.getDriverClassName()).matches("org.postgresql.Driver");
              assertThat(dataSource.getJdbcUrl())
                  .isEqualTo(
                    "jdbc:postgresql:///test-database?"
                        + "socketFactory=com.google.cloud.alloydb.SocketFactory"
                        + String.format("&alloydbInstanceName=%s", INSTANCE_URI));
              assertThat(dataSource.getUsername()).matches("postgres");
              assertThat(dataSource.getPassword()).isNull();
              assertThat(getSpringDatasourceDriverClassName(context))
                  .matches("org.postgresql.Driver");
              assertThat(context.getEnvironment().getProperty("spring.datasource.url"))
                  .isEqualTo(
                    "jdbc:postgresql:///test-database?"
                        + "socketFactory=com.google.cloud.alloydb.SocketFactory"
                        + String.format("&alloydbInstanceName=%s", INSTANCE_URI));
            });
  }

  @Test
  void testAlloyDbDataSourceWithIgnoredProvidedUrl() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.datasource.password=",
            "spring.datasource.url=test-url")
        .run(
            context -> {
              HikariDataSource dataSource = (HikariDataSource) context.getBean(DataSource.class);
              assertThat(dataSource.getDriverClassName()).matches("org.postgresql.Driver");
              assertThat(dataSource.getJdbcUrl())
                  .isEqualTo(
                      "jdbc:postgresql:///test-database?"
                          + "socketFactory=com.google.cloud.alloydb.SocketFactory"
                          + String.format("&alloydbInstanceName=%s", INSTANCE_URI));
              assertThat(dataSource.getUsername()).matches("postgres");
              assertThat(dataSource.getPassword()).isNull();
              assertThat(getSpringDatasourceDriverClassName(context))
                  .matches("org.postgresql.Driver");
            });
  }

  @Test
  void testUserAndPassword() {
    this.contextRunner
        .withPropertyValues(
            "spring.datasource.username=watchmaker",
            "spring.datasource.password=pass",
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI))
        .run(
            context -> {
              HikariDataSource dataSource = (HikariDataSource) context.getBean(DataSource.class);
              assertThat(getSpringDatasourceUrl(context))
                  .isEqualTo(
                      "jdbc:postgresql:///test-database?"
                          + "socketFactory=com.google.cloud.alloydb.SocketFactory"
                          + String.format("&alloydbInstanceName=%s", INSTANCE_URI));
              assertThat(dataSource.getUsername()).matches("watchmaker");
              assertThat(dataSource.getPassword()).matches("pass");
              assertThat(getSpringDatasourceDriverClassName(context))
                  .matches("org.postgresql.Driver");
            });
  }

  @Test
  void testInstanceConnectionUri() {
    this.contextRunner
        .withPropertyValues(String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI))
        .run(
            context -> {
              assertThat(getSpringDatasourceUrl(context))
                  .isEqualTo(
                      "jdbc:postgresql:///test-database?"
                          + "socketFactory=com.google.cloud.alloydb.SocketFactory"
                          + String.format("&alloydbInstanceName=%s", INSTANCE_URI));
              assertThat(getSpringDatasourceDriverClassName(context))
                  .matches("org.postgresql.Driver");
            });
  }

  @Test
  void testIamAuth() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.cloud.gcp.alloydb.enable-iam-auth=true")
        .run(
            context -> {
              DataSourceProperties dataSourceProperties = context.getBean(DataSourceProperties.class);
              assertThat(dataSourceProperties.getUrl())
                  .contains("&alloydbEnableIAMAuth=true&sslmode=disable");
            });
  }

  @Test
  void testIpType() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.cloud.gcp.alloydb.ip-type=PUBLIC")
        .run(
            context -> {
              DataSourceProperties dataSourceProperties = context.getBean(DataSourceProperties.class);
              assertThat(dataSourceProperties.getUrl()).contains("&alloydbIpType=PUBLIC");
            });
  }

  @Test
  void testAdminServiceEndpoint() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            String.format("spring.cloud.gcp.alloydb.admin-service-endpoint=%s", SERVICE_ENDPOINT))
        .run(
            context -> {
              DataSourceProperties dataSourceProperties = context.getBean(DataSourceProperties.class);
              assertThat(dataSourceProperties.getUrl()).contains(String.format("&alloydbAdminServiceEndpoint=%s", SERVICE_ENDPOINT));
            });
  }

  @Test
  void testQuotaProject() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.cloud.gcp.alloydb.quota-project=new-project")
        .run(
            context -> {
              DataSourceProperties dataSourceProperties = context.getBean(DataSourceProperties.class);
              assertThat(dataSourceProperties.getUrl()).contains("&alloydbQuotaProject=new-project");
            });
  }

  @Test
  void testTargetPrincipal() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.cloud.gcp.alloydb.target-principal=IMPERSONATED_USER")
        .run(
            context -> {
              DataSourceProperties dataSourceProperties = context.getBean(DataSourceProperties.class);
              assertThat(dataSourceProperties.getUrl()).contains("&alloydbTargetPrincipal=IMPERSONATED_USER");
            });
  }

  @Test
  void testDelegates() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.cloud.gcp.alloydb.delegates=IMPERSONATED_USER")
        .run(
            context -> {
              DataSourceProperties dataSourceProperties = context.getBean(DataSourceProperties.class);
              assertThat(dataSourceProperties.getUrl()).contains("&alloydbDelegates=IMPERSONATED_USER");
            });
  }

  @Test
  void testNamedConnector() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.cloud.gcp.alloydb.named-connector=test-connector")
        .run(
            context -> {
              DataSourceProperties dataSourceProperties = context.getBean(DataSourceProperties.class);
              assertThat(dataSourceProperties.getUrl()).contains("&alloydbNamedConnector=test-connector");
            });
  }

  @Test
  void testDataSourceProperties() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.datasource.hikari.connection-test-query=select 1",
            "spring.datasource.hikari.maximum-pool-size=19")
        .run(
            context -> {
              HikariDataSource dataSource = (HikariDataSource) context.getBean(DataSource.class);
              assertThat(getSpringDatasourceUrl(context))
                  .isEqualTo(
                      "jdbc:postgresql:///test-database?"
                          + "socketFactory=com.google.cloud.alloydb.SocketFactory"
                          + String.format("&alloydbInstanceName=%s", INSTANCE_URI));
              assertThat(getSpringDatasourceDriverClassName(context))
                  .matches("org.postgresql.Driver");
              assertThat(dataSource.getMaximumPoolSize()).isEqualTo(19);
              assertThat(dataSource.getConnectionTestQuery()).matches("select 1");
            });
  }

  @Test
  void testSkipIfAlloyDbPropertyIsDisabled() {
    new ApplicationContextRunner()
        .withPropertyValues("spring.cloud.gcp.alloydb.enabled=false")
        .withInitializer(
            configurableApplicationContext ->
                initializer.postProcessEnvironment(
                    configurableApplicationContext.getEnvironment(), new SpringApplication()))
        .run(
            context -> {
              assertThat(getSpringDatasourceUrl(context)).isNull();
            });
  }

  @Test
  void testSecretManagerPlaceholdersNotResolved() {
    this.contextRunner
        .withPropertyValues(
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.cloud.gcp.alloydb.database-name=${sm://my-db}")
        .run(
            context -> {
              assertThat(
                      context
                          .getEnvironment()
                          .getPropertySources()
                          .get("ALLOYDB_DATA_SOURCE_URL")
                          .getProperty("spring.datasource.url"))
                  .isEqualTo(
                      "jdbc:postgresql:///${sm://my-db}?"
                          + "socketFactory=com.google.cloud.alloydb.SocketFactory"
                          + String.format("&alloydbInstanceName=%s", INSTANCE_URI));
            });
  }

  @Test
  void testEnvPlaceholdersResolved() {
    this.contextRunner
        .withPropertyValues(
            "DB_NAME=mydb",
            String.format("spring.cloud.gcp.alloydb.instance-connection-uri=%s", INSTANCE_URI),
            "spring.cloud.gcp.alloydb.database-name=${DB_NAME:not_available}")
        .run(
            context -> {
              assertThat(
                      context
                          .getEnvironment()
                          .getPropertySources()
                          .get("ALLOYDB_DATA_SOURCE_URL")
                          .getProperty("spring.datasource.url"))
                  .isEqualTo(
                      "jdbc:postgresql:///mydb?"
                          + "socketFactory=com.google.cloud.alloydb.SocketFactory"
                          + String.format("&alloydbInstanceName=%s", INSTANCE_URI));
            });
  }

  @Test
  void testSkipOnBootstrap() {
    new ApplicationContextRunner()
        .withPropertyValues("spring.cloud.gcp.alloydb.databaseName=test-database")
        .withInitializer(
            new ApplicationContextInitializer<ConfigurableApplicationContext>() {
              @Override
              public void initialize(
                  ConfigurableApplicationContext configurableApplicationContext) {
                // add a property source called "bootstrap" to mark it as the bootstrap phase
                configurableApplicationContext
                    .getEnvironment()
                    .getPropertySources()
                    .addFirst(
                        new PropertySource<Object>("bootstrap") {
                          @Override
                          public Object getProperty(String name) {
                            return null;
                          }
                        });
              }
            })
        .withInitializer(
            configurableApplicationContext ->
                initializer.postProcessEnvironment(
                    configurableApplicationContext.getEnvironment(), new SpringApplication()))
        .run(
            context -> {
              assertThat(getSpringDatasourceUrl(context)).isNull();
            });
  }

  private String getSpringDatasourceUrl(ApplicationContext context) {
    return context.getEnvironment().getProperty("spring.datasource.url");
  }

  private String getSpringDatasourceDriverClassName(ApplicationContext context) {
    return context.getEnvironment().getProperty("spring.datasource.driver-class-name");
  }

  @Configuration
  static class Config {

    @Bean
    public CredentialsProvider credentialsProvider() {
      return NoCredentialsProvider.create();
    }
  }
}
