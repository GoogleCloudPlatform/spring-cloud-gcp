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

package com.google.cloud.spanner.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.r2dbc.SpannerConnectionConfiguration.Builder;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test for {@link SpannerConnectionConfiguration}.
 */
class SpannerConnectionConfigurationTest {

  GoogleCredentials mockCredentials = Mockito.mock(GoogleCredentials.class);

  SpannerConnectionConfiguration.Builder configurationBuilder;

  /**
   * Sets up mock credentials to avoid accessing filesystem to get default credentials.
   */
  @BeforeEach
  public void setUpMockCredentials() {
    this.configurationBuilder = new SpannerConnectionConfiguration.Builder()
        .setCredentials(this.mockCredentials);
  }

  @Test
  void missingInstanceNameTriggersException() {
    Builder builder = new SpannerConnectionConfiguration.Builder()
        .setProjectId("project1")
        .setDatabaseName("db")
        .setCredentials(NoCredentials.getInstance());

    assertThatThrownBy(() -> builder.build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("instanceName must not be null");
  }

  @Test
  void missingDatabaseNameTriggersException() {
    Builder builder = new SpannerConnectionConfiguration.Builder()
        .setProjectId("project1")
        .setInstanceName("an-instance")
        .setCredentials(NoCredentials.getInstance());

    assertThatThrownBy(() -> builder.build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("databaseName must not be null");
  }

  @Test
  void missingProjectIdTriggersException() {
    Builder builder = new SpannerConnectionConfiguration.Builder()
        .setInstanceName("an-instance")
        .setDatabaseName("db")
        .setCredentials(NoCredentials.getInstance());

    assertThatThrownBy(() -> builder.build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("projectId must not be null");
  }

  @Test
  void passingCustomGoogleCredentials() {

    SpannerConnectionConfiguration configuration = this.configurationBuilder
            .setProjectId("project")
            .setInstanceName("an-instance")
            .setDatabaseName("db")
            .build();

    assertThat(configuration.getCredentials()).isSameAs(this.mockCredentials);
  }

  @Test
  void nonNullConstructorParametersPassPreconditions() {
    SpannerConnectionConfiguration config = this.configurationBuilder
        .setProjectId("project1")
        .setInstanceName("an-instance")
        .setDatabaseName("db")
        .build();
    assertThat(config.getFullyQualifiedDatabaseName())
        .isEqualTo("projects/project1/instances/an-instance/databases/db");
  }

  @Test
  void partialResultSetFetchSize() {
    SpannerConnectionConfiguration config = this.configurationBuilder
        .setPartialResultSetFetchSize(42)
        .setProjectId("project1")
        .setInstanceName("an-instance")
        .setDatabaseName("db")
        .build();
    assertThat(config.getPartialResultSetFetchSize()).isEqualTo(42);
  }

  @Test
  void ddlOperationWaitSettings() {
    SpannerConnectionConfiguration config = this.configurationBuilder
        .setProjectId("project1")
        .setInstanceName("an-instance")
        .setDatabaseName("db")
        .setDdlOperationTimeout(Duration.ofSeconds(23))
        .setDdlOperationPollInterval(Duration.ofSeconds(45))
        .build();

    assertThat(config.getDdlOperationTimeout()).isEqualTo(Duration.ofSeconds(23));
    assertThat(config.getDdlOperationPollInterval()).isEqualTo(Duration.ofSeconds(45));
  }

  @Test
  void databaseUrlMatchesPropertyConfiguration() {
    SpannerConnectionConfiguration urlBased =
        this.configurationBuilder
            .setUrl("r2dbc:cloudspanner://spanner.googleapis.com:443/"
                + "projects/my-project/instances/my-instance/databases/my-database")
            .build();

    SpannerConnectionConfiguration propertyBased = this.configurationBuilder
        .setProjectId("my-project")
        .setInstanceName("my-instance")
        .setDatabaseName("my-database")
        .build();

    assertThat(urlBased).isEqualTo(propertyBased);
  }

  @Test
  void databaseUrlExtracting() {
    SpannerConnectionConfiguration config =
        this.configurationBuilder
            .setUrl("r2dbc:cloudspanner://spanner.googleapis.com:443/"
                + "projects/my-project/instances/my-instance/databases/my-database")
            .build();

    assertThat(config.getFullyQualifiedDatabaseName())
        .isEqualTo("projects/my-project/instances/my-instance/databases/my-database");
  }

  @Test
  void invalidUrlFormats() {
    assertThatThrownBy(() ->
        this.configurationBuilder
            .setUrl("r2dbc:cloudspanner://spanner.googleapis.com:443/"
                + "projects//instances/my-instance/databases/my-database"))
        .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() ->
        this.configurationBuilder
            .setUrl("r2dbc:cloudspanner://spanner.googleapis.com:443/"
                + "projects/proj/instances//databases/my-database"))
        .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() ->
        this.configurationBuilder
            .setUrl("r2dbc:cloudspanner://spanner.googleapis.com:443/"
                + "projects/a/instances/b/databases/c/d"))
        .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() ->
        this.configurationBuilder
            .setUrl("r2dbc:cloudspanner://spanner.googleapis.com:443/"
                + "projects/a/instances/b/databases/c d"))
        .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() ->
        this.configurationBuilder
            .setUrl("r2dbc:cloudspanner://spanner.googleapis.com:443/"
                + "foobar"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
