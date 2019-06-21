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
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.R2dbcNonTransientResourceException;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

/**
 * Configurable properties for Cloud Spanner.
 */
public class SpannerConnectionConfiguration {

  private static final String FULLY_QUALIFIED_DB_NAME_PATTERN =
      "projects/%s/instances/%s/databases/%s";

  /** Pattern used to validate that the user input database string is in the right format. */
  private static final String DB_NAME_VALIDATE_PATTERN =
      "projects\\/[\\w\\-]+\\/instances\\/[\\w\\-]+\\/databases\\/[\\w\\-]+$";

  private final String fullyQualifiedDbName;

  private final GoogleCredentials credentials;

  private int partialResultSetFetchSize;

  private Duration ddlOperationTimeout;

  private Duration ddlOperationPollInterval;

  /**
   * Constructor which initializes the configuration from an Cloud Spanner R2DBC url.
   */
  private SpannerConnectionConfiguration(String url, GoogleCredentials credentials) {
    String databaseString =
        ConnectionFactoryOptions.parse(url).getValue(ConnectionFactoryOptions.DATABASE);

    if (!databaseString.matches(DB_NAME_VALIDATE_PATTERN)) {
      throw new IllegalArgumentException(
          String.format(
              "Malformed Cloud Spanner Database String: %s. The url must have the format: %s",
              databaseString,
              FULLY_QUALIFIED_DB_NAME_PATTERN));
    }

    this.fullyQualifiedDbName = databaseString;
    this.credentials = credentials;
  }

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

  public int getPartialResultSetFetchSize() {
    return this.partialResultSetFetchSize;
  }

  public Duration getDdlOperationTimeout() {
    return this.ddlOperationTimeout;
  }

  public Duration getDdlOperationPollInterval() {
    return this.ddlOperationPollInterval;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpannerConnectionConfiguration that = (SpannerConnectionConfiguration) o;
    return this.partialResultSetFetchSize == that.partialResultSetFetchSize
        && Objects.equals(this.fullyQualifiedDbName, that.fullyQualifiedDbName)
        && Objects.equals(this.credentials, that.credentials)
        && Objects.equals(this.ddlOperationTimeout, that.ddlOperationTimeout)
        && Objects.equals(this.ddlOperationPollInterval, that.ddlOperationPollInterval);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(this.fullyQualifiedDbName,
            this.credentials,
            this.partialResultSetFetchSize,
            this.ddlOperationTimeout,
            this.ddlOperationPollInterval);
  }

  public static class Builder {

    private String url;

    private String projectId;

    private String instanceName;

    private String databaseName;

    private GoogleCredentials credentials;

    private int partialResultSetFetchSize = 1;

    private Duration ddlOperationTimeout = Duration.ofSeconds(600);

    private Duration ddlOperationPollInterval = Duration.ofSeconds(5);

    public Builder setUrl(String url) {
      this.url = url;
      return this;
    }

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

    public Builder setPartialResultSetFetchSize(int fetchSize) {
      this.partialResultSetFetchSize = fetchSize;
      return this;
    }

    public Builder setDdlOperationTimeout(Duration duration) {
      this.ddlOperationTimeout = duration;
      return this;
    }

    public Builder setDdlOperationPollInterval(Duration duration) {
      this.ddlOperationPollInterval = duration;
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

      SpannerConnectionConfiguration configuration;
      if (this.url != null) {
        configuration = new SpannerConnectionConfiguration(this.url, this.credentials);
      } else {
        configuration = new SpannerConnectionConfiguration(
            this.projectId, this.instanceName, this.databaseName, this.credentials);
      }

      configuration.partialResultSetFetchSize = this.partialResultSetFetchSize;
      configuration.ddlOperationTimeout = this.ddlOperationTimeout;
      configuration.ddlOperationPollInterval = this.ddlOperationPollInterval;

      return configuration;
    }

  }

}
