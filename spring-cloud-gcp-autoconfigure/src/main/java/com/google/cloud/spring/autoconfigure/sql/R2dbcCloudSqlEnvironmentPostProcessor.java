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

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Builds connection string for Cloud SQL through Spring R2DBC by requiring only a database and
 * instance connection name.
 */
public class R2dbcCloudSqlEnvironmentPostProcessor implements EnvironmentPostProcessor {
  private static final Log LOGGER = LogFactory.getLog(R2dbcCloudSqlEnvironmentPostProcessor.class);

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    if (environment.getPropertySources().contains("bootstrap")) {
      // Do not run in the bootstrap phase as the user configuration is not available yet
      return;
    }

    DatabaseType databaseType = getEnabledDatabaseType(environment);
    if (databaseType != null) {
      PropertiesRetriever propertiesRetriever = new PropertiesRetriever(environment);
      GcpCloudSqlProperties sqlProperties = propertiesRetriever.getCloudSqlProperties();
      String r2dbcUrl = createUrl(databaseType, sqlProperties);
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(
            "Default " + databaseType.name() + " R2dbcUrl provider. Connecting to " + r2dbcUrl);
      }

      // Add default username as fallback when not specified
      Map<String, Object> fallbackMap = new HashMap<>();
      fallbackMap.put("spring.r2dbc.username", databaseType.getDefaultUsername());
      environment
          .getPropertySources()
          .addLast(new MapPropertySource("CLOUD_SQL_R2DBC_USERNAME", fallbackMap));

      Map<String, Object> primaryMap = new HashMap<>();
      primaryMap.put("spring.r2dbc.url", r2dbcUrl);
      environment
          .getPropertySources()
          .addFirst(new MapPropertySource("CLOUD_SQL_R2DBC_URL", primaryMap));

      CredentialsPropertiesSetter.setCredentials(sqlProperties,
          propertiesRetriever.getGcpProperties());

      // Belt and braces approach for SSL and IAM auth properties
      Map<String, Object> r2dbcPropertiesMap = new HashMap<>();
      if (databaseType == DatabaseType.POSTGRESQL) {
        // Use multiple naming conventions to ensure the Spring Binder and Driver both catch it
        r2dbcPropertiesMap.put("spring.r2dbc.properties.sslMode", "disable");
        r2dbcPropertiesMap.put("spring.r2dbc.properties.ssl-mode", "disable");
        r2dbcPropertiesMap.put("spring.r2dbc.properties.sslmode", "disable");
      }
      if (sqlProperties.isEnableIamAuth()) {
        r2dbcPropertiesMap.put("spring.r2dbc.properties.ENABLE_IAM_AUTH", "true");
      }

      if (!r2dbcPropertiesMap.isEmpty()) {
        environment
            .getPropertySources()
            .addFirst(new MapPropertySource("CLOUD_SQL_R2DBC_PROPERTIES", r2dbcPropertiesMap));
      }
    }
  }

  String createUrl(DatabaseType databaseType, GcpCloudSqlProperties sqlProperties) {
    Assert.hasText(sqlProperties.getDatabaseName(), "A database name must be provided.");
    Assert.hasText(
        sqlProperties.getInstanceConnectionName(),
        "An instance connection name must be provided in the format"
            + " <PROJECT_ID>:<REGION>:<INSTANCE_ID>.");

    String url = String.format(
        databaseType.getR2dbcUrlTemplate(),
        sqlProperties.getInstanceConnectionName(),
        sqlProperties.getDatabaseName());

    if (databaseType == DatabaseType.POSTGRESQL && sqlProperties.isEnableIamAuth()) {
      // Append IAM auth if not already part of the template
      if (!url.contains("ENABLE_IAM_AUTH")) {
        url += (url.contains("?") ? "&" : "?") + "ENABLE_IAM_AUTH=true";
      }
    }
    return url;
  }

  /**
   * Returns {@link DatabaseType} constant based on whether postgresSQL R2DBC driver and
   * connector dependencies are present on the classpath. Returns null if Cloud SQL is not enabled
   * in Spring Cloud GCP, CredentialFactory is not present or ConnectionFactory (which is used to
   * enable Spring R2DBC autoconfiguration) is not present.
   *
   * @param environment environment to post-process
   * @return database type
   */
  DatabaseType getEnabledDatabaseType(ConfigurableEnvironment environment) {
    if (isR2dbcEnabled(environment)
        && isOnClasspath("com.google.cloud.sql.CredentialFactory")
        && isOnClasspath("io.r2dbc.spi.ConnectionFactory")
        && isOnClasspath("com.google.cloud.sql.core.GcpConnectionFactoryProviderPostgres")
        && isOnClasspath("io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider")) {
      return DatabaseType.POSTGRESQL;
    }
    return null;
  }

  private boolean isOnClasspath(String className) {
    return ClassUtils.isPresent(className, null);
  }

  private boolean isR2dbcEnabled(ConfigurableEnvironment environment) {
    return Boolean.parseBoolean(environment.getProperty("spring.cloud.gcp.sql.enabled", "true"))
        && Boolean.parseBoolean(
            environment.getProperty("spring.cloud.gcp.sql.r2dbc.enabled", "true"));
  }
}
