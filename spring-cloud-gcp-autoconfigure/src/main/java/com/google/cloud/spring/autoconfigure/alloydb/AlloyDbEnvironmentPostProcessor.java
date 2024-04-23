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

import com.google.cloud.alloydb.ConnectorRegistry;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PlaceholdersResolver;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Provides Google AlloyDB instance connectivity through Spring JDBC by
 * providing only a database and instance connection URI.
 */
public class AlloyDbEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private static final String JDBC_URL_TEMPLATE = "jdbc:postgresql:///%s?socketFactory=com.google.cloud.alloydb.SocketFactory";
  private static final String JDBC_DRIVER_CLASS = "org.postgresql.Driver";
  private static final String DEFAULT_USERNAME = "postgres";

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {

    if (environment.getPropertySources().contains("bootstrap")) {
      // Do not run in the bootstrap phase as the user configuration is not available
      // yet
      return;
    }

    boolean isAlloyDbEnabled = Boolean.parseBoolean(environment.getProperty("spring.cloud.gcp.alloydb.enabled", "true"));
    if (!isAlloyDbEnabled) {
      return;
    }

    String propertiesPrefix = AlloyDbProperties.class.getAnnotation(ConfigurationProperties.class).value();
    AlloyDbProperties alloyDbProperties = new Binder(
        ConfigurationPropertySources.get(environment),
        new NonSecretsManagerPropertiesPlaceholdersResolver(environment),
        null,
        null,
        null)
        .bind(propertiesPrefix, AlloyDbProperties.class)
        .orElse(new AlloyDbProperties());

    if (isOnClasspath("com.google.cloud.alloydb.SocketFactory")
        && isOnClasspath(JDBC_DRIVER_CLASS)) {
      // configure default JDBC driver and username as fallback values when not
      // specified
      Map<String, Object> fallbackMap = new HashMap<>();
      fallbackMap.put("spring.datasource.username", DEFAULT_USERNAME);
      fallbackMap.put(
          "spring.datasource.driver-class-name", JDBC_DRIVER_CLASS);
      environment
          .getPropertySources()
          .addLast(new MapPropertySource("ALLOYDB_DATA_SOURCE_FALLBACK", fallbackMap));

      // always set the spring.datasource.url property in the environment
      Map<String, Object> primaryMap = new HashMap<>();
      primaryMap.put("spring.datasource.url", getJdbcUrl(alloyDbProperties));
      environment
          .getPropertySources()
          .addFirst(new MapPropertySource("ALLOYDB_DATA_SOURCE_URL", primaryMap));
    }

    // support usage metrics
    ConnectorRegistry.addArtifactId(
        "spring-cloud-gcp-alloydb/" + this.getClass().getPackage().getImplementationVersion());
  }

  private String getJdbcUrl(AlloyDbProperties properties) {
    String jdbcUrl = String.format(JDBC_URL_TEMPLATE, properties.getDatabaseName());

    if (StringUtils.hasText(properties.getInstanceConnectionUri())) {
      jdbcUrl += "&alloydbInstanceName=" + properties.getInstanceConnectionUri();
    }

    if (StringUtils.hasText(properties.getIpType())) {
      jdbcUrl += "&alloydbIpType=" + properties.getIpType();
    }

    if (properties.isEnableIamAuth()) {
      jdbcUrl += "&alloydbEnableIAMAuth=true&sslmode=disable";
    }

    if (StringUtils.hasText(properties.getAdminServiceEndpoint())) {
      jdbcUrl += "&alloydbAdminServiceEndpoint=" + properties.getAdminServiceEndpoint();
    }

    if (StringUtils.hasText(properties.getQuotaProject())) {
      jdbcUrl += "&alloydbQuotaProject=" + properties.getQuotaProject();
    }

    if (StringUtils.hasText(properties.getTargetPrincipal())) {
      jdbcUrl += "&alloydbTargetPrincipal=" + properties.getTargetPrincipal();
    }

    if (StringUtils.hasText(properties.getDelegates())) {
      jdbcUrl += "&alloydbDelegates=" + properties.getDelegates();
    }

    if (StringUtils.hasText(properties.getNamedConnector())) {
      jdbcUrl += "&alloydbNamedConnector=" + properties.getNamedConnector();
    }

    return jdbcUrl;
  }

  private boolean isOnClasspath(String className) {
    return ClassUtils.isPresent(className, null);
  }

  private static class NonSecretsManagerPropertiesPlaceholdersResolver
      implements PlaceholdersResolver {

    private PlaceholdersResolver resolver;

    NonSecretsManagerPropertiesPlaceholdersResolver(Environment environment) {
      this.resolver = new PropertySourcesPlaceholdersResolver(environment);
    }

    @Override
    public Object resolvePlaceholders(Object value) {
      if (value.toString().contains("sm://")) {
        return value;
      } else {
        return resolver.resolvePlaceholders(value);
      }
    }
  }
}