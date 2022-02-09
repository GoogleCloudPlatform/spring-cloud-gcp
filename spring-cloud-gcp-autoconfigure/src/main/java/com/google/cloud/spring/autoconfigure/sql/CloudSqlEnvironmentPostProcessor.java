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

import com.google.cloud.sql.core.CoreSocketFactory;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.ClassUtils;

/**
 * Provides Google Cloud SQL instance connectivity through Spring JDBC by providing only a database
 * and instance connection name.
 */
public class CloudSqlEnvironmentPostProcessor implements EnvironmentPostProcessor {
  private static final Log LOGGER = LogFactory.getLog(CloudSqlEnvironmentPostProcessor.class);

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {

    if (environment.getPropertySources().contains("bootstrap")) {
      // Do not run in the bootstrap phase as the user configuration is not available yet
      return;
    }

    DatabaseType databaseType = getEnabledDatabaseType(environment);

    if (databaseType != null) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("post-processing Cloud SQL properties for + " + databaseType.name());
      }

      PropertiesRetriever propertiesRetriever = new PropertiesRetriever(environment);
      GcpCloudSqlProperties sqlProperties = propertiesRetriever.getCloudSqlProperties();
      CloudSqlJdbcInfoProvider cloudSqlJdbcInfoProvider =
          new DefaultCloudSqlJdbcInfoProvider(sqlProperties, databaseType);
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(
            "Default "
                + databaseType.name()
                + " JdbcUrl provider. Connecting to "
                + cloudSqlJdbcInfoProvider.getJdbcUrl()
                + " with driver "
                + cloudSqlJdbcInfoProvider.getJdbcDriverClass());
      }

      // configure default JDBC driver and username as fallback values when not specified
      Map<String, Object> fallbackMap = new HashMap<>();
      fallbackMap.put("spring.datasource.username", databaseType.getDefaultUsername());
      fallbackMap.put(
          "spring.datasource.driver-class-name", cloudSqlJdbcInfoProvider.getJdbcDriverClass());
      environment
          .getPropertySources()
          .addLast(new MapPropertySource("CLOUD_SQL_DATA_SOURCE_FALLBACK", fallbackMap));

      // always set the spring.datasource.url property in the environment
      Map<String, Object> primaryMap = new HashMap<>();
      primaryMap.put("spring.datasource.url", cloudSqlJdbcInfoProvider.getJdbcUrl());
      environment
          .getPropertySources()
          .addFirst(new MapPropertySource("CLOUD_SQL_DATA_SOURCE_URL", primaryMap));

      CredentialsPropertiesSetter.setCredentials(sqlProperties, propertiesRetriever.getGcpProperties());

      // support usage metrics
      CoreSocketFactory.setApplicationName(
          "spring-cloud-gcp-sql/" + this.getClass().getPackage().getImplementationVersion());
    }
  }

  private DatabaseType getEnabledDatabaseType(ConfigurableEnvironment environment) {
    if (isJdbcEnabled(environment)
        && isOnClasspath("javax.sql.DataSource")
        && isOnClasspath("org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType")
        && isOnClasspath("com.google.cloud.sql.CredentialFactory")) {
      if (isOnClasspath("com.google.cloud.sql.mysql.SocketFactory")
          && isOnClasspath("com.mysql.cj.jdbc.Driver")) {
        return DatabaseType.MYSQL;
      } else if (isOnClasspath("com.google.cloud.sql.postgres.SocketFactory")
          && isOnClasspath("org.postgresql.Driver")) {
        return DatabaseType.POSTGRESQL;
      }
    }
    return null;
  }

  private boolean isJdbcEnabled(ConfigurableEnvironment environment) {
    return Boolean.parseBoolean(environment.getProperty("spring.cloud.gcp.sql.enabled", "true"))
        && Boolean.parseBoolean(
        environment.getProperty("spring.cloud.gcp.sql.jdbc.enabled", "true"));
  }

  private boolean isOnClasspath(String className) {
    return ClassUtils.isPresent(className, null);
  }

}
