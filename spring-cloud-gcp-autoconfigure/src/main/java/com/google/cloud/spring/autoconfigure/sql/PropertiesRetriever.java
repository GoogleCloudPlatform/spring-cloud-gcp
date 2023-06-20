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

import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PlaceholdersResolver;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.Environment;

/**
 * Helper class to derive Cloud SQL and GCP properties from the user configuration
 * (application.properties, for example).
 */
class PropertiesRetriever {

  private Binder binder;

  PropertiesRetriever(Environment environment) {
    // Bind properties without resolving Secret Manager placeholders
    this.binder =
        new Binder(
            ConfigurationPropertySources.get(environment),
            new NonSecretsManagerPropertiesPlaceholdersResolver(environment),
            null,
            null,
            null);
  }

  GcpCloudSqlProperties getCloudSqlProperties() {
    String cloudSqlPropertiesPrefix =
        GcpCloudSqlProperties.class.getAnnotation(ConfigurationProperties.class).value();
    return this.binder
        .bind(cloudSqlPropertiesPrefix, GcpCloudSqlProperties.class)
        .orElse(new GcpCloudSqlProperties());
  }

  GcpProperties getGcpProperties() {
    String gcpPropertiesPrefix =
        GcpProperties.class.getAnnotation(ConfigurationProperties.class).value();
    return this.binder.bind(gcpPropertiesPrefix, GcpProperties.class).orElse(new GcpProperties());
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
