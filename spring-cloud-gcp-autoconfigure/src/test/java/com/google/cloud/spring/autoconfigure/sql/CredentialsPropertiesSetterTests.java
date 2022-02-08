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
import static org.junit.Assert.assertThrows;

import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Tests for {@link CredentialsPropertiesSetter}.
 */
class CredentialsPropertiesSetterTests {

  @TempDir
  Path temporaryDirectory;

  @Test
  void testSetCredentials_encodedKey_setFromCloudSqlProperties() {
    ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues("spring.cloud.gcp.sql.credentials.encodedKey=myGcpEncodedKey");
    contextRunner.run(
        context -> {
          ConfigurableEnvironment environment = context.getEnvironment();
          PropertiesRetriever propertiesRetriever = new PropertiesRetriever(environment);

          CredentialsPropertiesSetter.setCredentials(propertiesRetriever.getCloudSqlProperties(),
              propertiesRetriever.getGcpProperties());

          assertThat(environment
              .getProperty(SqlCredentialFactory.CREDENTIAL_ENCODED_KEY_PROPERTY_NAME))
              .isEqualTo("myGcpEncodedKey");
          assertThat(environment
              .getProperty(SqlCredentialFactory.CREDENTIAL_FACTORY_PROPERTY))
              .isEqualTo("com.google.cloud.spring.autoconfigure.sql.SqlCredentialFactory");
        });
  }

  @Test
  void testSetCredentials_encodedKey_setFromGcpProperties() {
    ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues("spring.cloud.gcp.credentials.encodedKey=myGcpEncodedKey");
    contextRunner.run(
        context -> {
          ConfigurableEnvironment environment = context.getEnvironment();
          PropertiesRetriever propertiesRetriever = new PropertiesRetriever(environment);

          CredentialsPropertiesSetter.setCredentials(propertiesRetriever.getCloudSqlProperties(),
              propertiesRetriever.getGcpProperties());

          assertThat(environment
              .getProperty(SqlCredentialFactory.CREDENTIAL_ENCODED_KEY_PROPERTY_NAME))
              .isEqualTo("myGcpEncodedKey");
          assertThat(environment
              .getProperty(SqlCredentialFactory.CREDENTIAL_FACTORY_PROPERTY))
              .isEqualTo("com.google.cloud.spring.autoconfigure.sql.SqlCredentialFactory");
        });
  }

  @Test
  void testSetCredentials_location_setFromCloudSqlProperties() {
    Path credentialsFile = temporaryDirectory.resolve("credentials.json");
    ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues("spring.cloud.gcp.sql.credentials.location=file:" + credentialsFile,
            "spring.cloud.gcp.credentials.location=ignore");
    contextRunner.run(
        context -> {
          ConfigurableEnvironment environment = context.getEnvironment();
          PropertiesRetriever propertiesRetriever = new PropertiesRetriever(environment);

          CredentialsPropertiesSetter.setCredentials(propertiesRetriever.getCloudSqlProperties(),
              propertiesRetriever.getGcpProperties());

          assertThat(environment
              .getProperty(SqlCredentialFactory.CREDENTIAL_LOCATION_PROPERTY_NAME))
              .endsWith("credentials.json");
          assertThat(environment
              .getProperty(SqlCredentialFactory.CREDENTIAL_FACTORY_PROPERTY))
              .isEqualTo("com.google.cloud.spring.autoconfigure.sql.SqlCredentialFactory");
        });
  }

  @Test
  void testSetCredentials_location_setFromGcpProperties() {
    Path credentialsFile = temporaryDirectory.resolve("credentials.json");
    ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues("spring.cloud.gcp.credentials.location=file:" + credentialsFile);
    contextRunner.run(
        context -> {
          ConfigurableEnvironment environment = context.getEnvironment();
          PropertiesRetriever propertiesRetriever = new PropertiesRetriever(environment);

          CredentialsPropertiesSetter.setCredentials(propertiesRetriever.getCloudSqlProperties(),
              propertiesRetriever.getGcpProperties());

          assertThat(environment
              .getProperty(SqlCredentialFactory.CREDENTIAL_LOCATION_PROPERTY_NAME))
              .endsWith("credentials.json");
          assertThat(environment
              .getProperty(SqlCredentialFactory.CREDENTIAL_FACTORY_PROPERTY))
              .isEqualTo("com.google.cloud.spring.autoconfigure.sql.SqlCredentialFactory");
        });
  }

  @Test
  void testSetCredentialsFileProperty_invalidLocation() {
    ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues("spring.cloud.gcp.credentials.location=invalid/path");
    contextRunner.run(
        context -> {
          ConfigurableEnvironment environment = context.getEnvironment();
          PropertiesRetriever propertiesRetriever = new PropertiesRetriever(environment);
          GcpCloudSqlProperties sqlProperties = propertiesRetriever.getCloudSqlProperties();
          GcpProperties gcpProperties = propertiesRetriever.getGcpProperties();
          IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
              () -> CredentialsPropertiesSetter.setCredentials(sqlProperties, gcpProperties));
          assertThat(exception).hasMessageContaining(
              "Error reading Cloud SQL credentials file: class path resource [invalid/path].");
        }
    );
  }
}
