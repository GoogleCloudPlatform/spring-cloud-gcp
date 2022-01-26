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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.r2dbc.util.Assert;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.R2dbcNonTransientResourceException;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Configurable properties for Cloud Spanner.
 */
public class SpannerConnectionConfiguration {

  public static final String FQDN_PATTERN_GENERATE =
      "projects/%s/instances/%s/databases/%s";

  /** Pattern used to validate that the user input database string is in the right format. */
  public static final Pattern FQDN_PATTERN_PARSE = Pattern.compile(
      "projects\\/([\\w\\-]+)\\/instances\\/([\\w\\-]+)\\/databases\\/([\\w\\-]+)$");

  private static final String USER_AGENT_LIBRARY_NAME = "cloud-spanner-r2dbc";

  private static final String PACKAGE_VERSION =
      SpannerConnectionConfiguration.class.getPackage().getImplementationVersion();

  private static final String USER_AGENT_KEY = "user-agent";

  // TODO: check how to handle full URL (it gets parsed by SPI, we only get pieces)
  private final String fullyQualifiedDbName;

  private String projectId;

  private String instanceName;

  private String databaseName;

  private final OAuth2Credentials credentials;

  private int partialResultSetFetchSize;

  private Duration ddlOperationTimeout;

  private Duration ddlOperationPollInterval;

  private boolean usePlainText;

  private String optimizerVersion;

  private boolean readonly;

  private boolean autocommit;

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
      OAuth2Credentials credentials) {

    Assert.requireNonNull(projectId, "projectId must not be null");
    Assert.requireNonNull(instanceName, "instanceName must not be null");
    Assert.requireNonNull(databaseName, "databaseName must not be null");

    this.projectId = projectId;
    this.instanceName = instanceName;
    this.databaseName = databaseName;

    this.fullyQualifiedDbName = String.format(
        FQDN_PATTERN_GENERATE, projectId, instanceName, databaseName);
    this.credentials = credentials;
  }

  public String getProjectId() {
    return this.projectId;
  }

  public String getInstanceName() {
    return this.instanceName;
  }

  public String getDatabaseName() {
    return this.databaseName;
  }

  /**
   * Turns configuration properties into a fully qualified database name.
   *
   * @return fully qualified database name
   */
  public String getFullyQualifiedDatabaseName() {
    return this.fullyQualifiedDbName;
  }

  public OAuth2Credentials getCredentials() {
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

  public boolean isUsePlainText() {
    return this.usePlainText;
  }

  public String getOptimizerVersion() {
    return this.optimizerVersion;
  }

  public boolean isReadonly() {
    return this.readonly;
  }

  public boolean isAutocommit() {
    return this.autocommit;
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

  /**
   * Converts current custom configuration object into client library {@link SpannerOptions}.
   *
   * <p>Supports customizable project ID, credentials and UserAgent header.
   *
   * @return configured spanner options.
   */
  public SpannerOptions buildSpannerOptions() {
    SpannerOptions.Builder optionsBuilder = SpannerOptions.newBuilder();

    if (this.projectId != null) {
      optionsBuilder.setProjectId(this.projectId);
    }

    if (this.credentials != null) {
      optionsBuilder.setCredentials(this.credentials);
    }

    optionsBuilder.setHeaderProvider(() ->
        Collections.singletonMap(USER_AGENT_KEY, USER_AGENT_LIBRARY_NAME + "/" + PACKAGE_VERSION));

    // usePlainText is not currently used in the client library.
    // TODO (GH-200): allow customizing emulator with optionsBuilder.setEmulatorHost()

    return optionsBuilder.build();
  }

  /**
   * Builder for the enclosing class.
   */
  public static class Builder {

    private String fullyQualifiedDatabaseName;

    private String projectId;

    private String instanceName;

    private String databaseName;

    private OAuth2Credentials credentials;

    private int partialResultSetFetchSize = 1;

    private Duration ddlOperationTimeout = Duration.ofSeconds(600);

    private Duration ddlOperationPollInterval = Duration.ofSeconds(5);

    private boolean usePlainText = false;

    private String optimizerVersion;

    private boolean readonly = false;

    private boolean autocommit = true;

    /**
     * R2DBC SPI does not provide the full URL to drivers after parsing the connection string.
     * Therefore, this usecase is only possible if the client application provides a URL property
     * directly through the programmatic configuration with
     * {@code ConnectionFactories.get(ConnectionFactoryOptions)}.
     */
    @Deprecated
    public Builder setUrl(String url) {
      String databaseString =
          (String) ConnectionFactoryOptions.parse(url).getValue(ConnectionFactoryOptions.DATABASE);

      validateFullyQualifiedDatabaseName(databaseString);
      this.fullyQualifiedDatabaseName = databaseString;
      return this;
    }

    /**
     * Sets fully qualified database name.
     *
     * @param databaseName fully qualified database name in the format of
     *                     "projects/%s/instances/%s/databases/%s"
     *
     * @return builder for chaining
     */
    public Builder setFullyQualifiedDatabaseName(String databaseName) {
      validateFullyQualifiedDatabaseName(databaseName);
      this.fullyQualifiedDatabaseName = databaseName;
      return this;
    }

    private void validateFullyQualifiedDatabaseName(String databaseString) {
      if (!FQDN_PATTERN_PARSE.matcher(databaseString).matches()) {
        throw new IllegalArgumentException(
            String.format(
                "Malformed Cloud Spanner Database String: %s. The url must have the format: %s",
                databaseString,
                FQDN_PATTERN_GENERATE));
      }
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

    public Builder setCredentials(OAuth2Credentials credentials) {
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

    public Builder setUsePlainText(boolean usePlainText) {
      this.usePlainText = true;
      return this;
    }

    public Builder setOptimizerVersion(String optimizerVersion) {
      this.optimizerVersion = optimizerVersion;
      return this;
    }

    public Builder setReadonly(boolean readonly) {
      this.readonly = readonly;
      return this;
    }

    public Builder setAutocommit(boolean autocommit) {
      this.autocommit = autocommit;
      return this;
    }

    /**
     * Constructs an instance of the {@link SpannerConnectionConfiguration}.
     *
     * @return the built {@link SpannerConnectionConfiguration} object.
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

      if (this.fullyQualifiedDatabaseName != null) {
        Matcher matcher = FQDN_PATTERN_PARSE.matcher(this.fullyQualifiedDatabaseName);
        if (matcher.find()) {
          this.projectId = matcher.group(1);
          this.instanceName = matcher.group(2);
          this.databaseName = matcher.group(3);
        } else {
          // should not happen, as database names are pre-validated in the setters.
          throw new IllegalArgumentException(
              "Invalid database name: " + this.fullyQualifiedDatabaseName);
        }
      }

      SpannerConnectionConfiguration configuration = new SpannerConnectionConfiguration(
            this.projectId, this.instanceName, this.databaseName, this.credentials);

      configuration.partialResultSetFetchSize = this.partialResultSetFetchSize;
      configuration.ddlOperationTimeout = this.ddlOperationTimeout;
      configuration.ddlOperationPollInterval = this.ddlOperationPollInterval;
      configuration.usePlainText = this.usePlainText;
      configuration.optimizerVersion = this.optimizerVersion;
      configuration.readonly = this.readonly;
      configuration.autocommit = this.autocommit;

      return configuration;
    }

  }

}
