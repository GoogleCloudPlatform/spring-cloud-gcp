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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class AlloyDbEnvironmentPostProcessorTests {

  private AlloyDbEnvironmentPostProcessor initializer = new AlloyDbEnvironmentPostProcessor();

  private static final String INSTANCE_URI = "projects/test-proj/locations/us-central1/clusters/test-cluster/instances/test-instance";
  private static final String CREDENTIAL_LOCATION = "src/test/resources/fake-credential-key.json";
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
