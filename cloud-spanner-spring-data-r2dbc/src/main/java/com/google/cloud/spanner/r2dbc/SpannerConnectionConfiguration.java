/*
 * Copyright 2019 Google LLC
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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spanner.r2dbc.util.Assert;
import io.r2dbc.spi.R2dbcNonTransientResourceException;
import java.io.IOException;

/**
 * Configurable properties for Cloud Spanner.
 */
public class SpannerConnectionConfiguration {

  private static final String FULLY_QUALIFIED_DB_NAME_PATTERN
      = "projects/%s/instances/%s/databases/%s";

  private final String fullyQualifiedDbName;

  private final GoogleCredentials credentials;

  private Integer partialResultSetFetchSize;

  /**
   * Basic property initializing constructor.
   *
   * @param projectId GCP project that contains the database.
   * @param instanceName instance to connect to
   * @param databaseName database to connect to.
   * @param credentials GCP credentials to authenticate service calls with.
   */
  private SpannerConnectionConfiguration(
      String projectId,
      String instanceName,
      String databaseName,
      GoogleCredentials credentials) {

    Assert.requireNonNull(projectId, "projectId must not be null");
    Assert.requireNonNull(instanceName, "instanceName must not be null");
    Assert.requireNonNull(databaseName, "databaseName must not be null");

    this.fullyQualifiedDbName = String.format(
        FULLY_QUALIFIED_DB_NAME_PATTERN, projectId, instanceName, databaseName);
    this.credentials = credentials;
  }

  /**
   * Turns configuration properties into a fully qualified database name.
   * @return fully qualified database name
   */
  public String getFullyQualifiedDatabaseName() {
    return this.fullyQualifiedDbName;
  }

  public GoogleCredentials getCredentials() {
    return this.credentials;
  }

  public Integer getPartialResultSetFetchSize() {
    return this.partialResultSetFetchSize;
  }

  public static class Builder {

    private String projectId;

    private String instanceName;

    private String databaseName;

    private GoogleCredentials credentials;

    private Integer partialResultSetFetchSize;

    public Builder setProjectId(String projectId) {
      this.projectId = projectId;
      return this;
    }

    public Builder setInstanceName(String instanceName) {
      this.instanceName = instanceName;
      return this;
    }

    public Builder setDatabaseName(String databaseName) {
      this.databaseName = databaseName;
      return this;
    }

    public Builder setCredentials(GoogleCredentials credentials) {
      this.credentials = credentials;
      return this;
    }

    public Builder setPartialResultSetFetchSize(Integer fetchSize) {
      this.partialResultSetFetchSize = fetchSize;
      return this;
    }

    /**
     * Constructs an instance of the {@link SpannerConnectionConfiguration}.
     */
    public SpannerConnectionConfiguration build() {
      try {
        if (this.credentials == null) {
          this.credentials = GoogleCredentials.getApplicationDefault();
        }
      } catch (IOException e) {
        throw new R2dbcNonTransientResourceException(
            "Could not acquire default application credentials", e);
      }

      SpannerConnectionConfiguration configuration = new SpannerConnectionConfiguration(
          this.projectId,
          this.instanceName,
          this.databaseName,
          this.credentials);

      configuration.partialResultSetFetchSize = this.partialResultSetFetchSize;

      return configuration;
    }

  }

}
