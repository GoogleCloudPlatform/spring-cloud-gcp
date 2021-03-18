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

import static com.google.cloud.spanner.r2dbc.SpannerConnectionConfiguration.FQDN_PATTERN_PARSE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.r2dbc.client.Client;
import com.google.cloud.spanner.r2dbc.client.GrpcClient;
import com.google.cloud.spanner.r2dbc.util.Assert;
import com.google.cloud.spanner.r2dbc.v2.SpannerClientLibraryConnectionFactory;
import com.google.common.annotations.VisibleForTesting;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.ConnectionFactoryProvider;
import io.r2dbc.spi.Option;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of {@link ConnectionFactoryProvider} for creating {@link
 * SpannerConnectionFactory}s.
 *
 */
public class SpannerConnectionFactoryProvider implements ConnectionFactoryProvider {

  /** R2DBC driver name for Google Cloud Spanner. */
  public static final String DRIVER_NAME = "spanner";

  /** Option name for GCP Project. */
  public static final Option<String> PROJECT = Option.valueOf("project");

  /** Option name for GCP Spanner instance. */
  public static final Option<String> INSTANCE = Option.valueOf("instance");

  /**
   * Option name for specifying the Cloud Spanner R2DBC URL.
   */
  public static final Option<String> URL = Option.valueOf("url");

  public static final Option<Integer> THREAD_POOL_SIZE = Option.valueOf("thread_pool_size");

  /** Number of partial result sets to buffer during a read query operation. */
  public static final Option<Integer> PARTIAL_RESULT_SET_FETCH_SIZE =
      Option.valueOf("partial_result_set_fetch_size");

  /** Duration to wait for a DDL operation before timing out. */
  public static final Option<Duration> DDL_OPERATION_TIMEOUT =
      Option.valueOf("ddl_operation_timeout");

  /** Duration to wait between each poll checking for the completion of DDL operations. */
  public static final Option<Duration> DDL_OPERATION_POLL_INTERVAL =
      Option.valueOf("ddl_operation_poll_interval");

  // TODO: GH-292
  public static final Option<String> OPTIMIZER_VERSION =
      Option.valueOf("optimizerVersion");
  /**
   * Option specifying the already-instantiated credentials object.
   */
  public static final Option<GoogleCredentials> GOOGLE_CREDENTIALS =
      Option.valueOf("google_credentials");

  /**
   * Option specifying the location of the GCP credentials file. Same as GOOGLE_CREDENTIALS,
   * but consistent with the JDBC driver option.
   */
  public static final Option<String> CREDENTIALS = Option.valueOf("credentials");

  // TODO: GH-292
  /** Plain-text option used to connect to the emulator. */
  public static final Option<Boolean> USE_PLAIN_TEXT = Option.valueOf("usePlainText");

  /** OAuth token to use for authentication. */
  public static final Option<String> OAUTH_TOKEN = Option.valueOf("oauthToken");

  private static final Option[] SECURITY_OPTIONS =
      new Option[] { OAUTH_TOKEN, CREDENTIALS, GOOGLE_CREDENTIALS};

  private Client client;

  private CredentialsHelper credentialsHelper = new CredentialsHelper();

  @Override
  public ConnectionFactory create(ConnectionFactoryOptions connectionFactoryOptions) {

    SpannerConnectionConfiguration config = createConfiguration(connectionFactoryOptions);

    if (connectionFactoryOptions.hasOption(Option.valueOf("client-implementation"))
        && connectionFactoryOptions.getValue(Option.valueOf("client-implementation"))
        .equals("client-library")) {
      return new SpannerClientLibraryConnectionFactory(config);

    } else {
      if (this.client == null) {
        // GrpcClient should only be instantiated if/when a SpannerConnectionFactory is needed.
        this.client = new GrpcClient(config.getCredentials());
      }
      return new SpannerConnectionFactory(this.client, config);
    }
  }

  @Override
  public boolean supports(ConnectionFactoryOptions connectionFactoryOptions) {
    Assert.requireNonNull(connectionFactoryOptions, "connectionFactoryOptions must not be null");
    String driver = connectionFactoryOptions.getValue(DRIVER);

    return DRIVER_NAME.equals(driver);
  }

  @Override
  public String getDriver() {
    return DRIVER_NAME;
  }

  @VisibleForTesting
  void setClient(Client client) {
    this.client = client;
  }

  @VisibleForTesting
  SpannerConnectionConfiguration createConfiguration(
      ConnectionFactoryOptions options) {

    SpannerConnectionConfiguration.Builder config = new SpannerConnectionConfiguration.Builder();

    // Directly passed URL is supported for backwards compatibility. R2DBC SPI does not provide
    // the original URL when creating connection through ConnectionFactories.get(String).
    if (options.hasOption(URL)) {
      config.setUrl(options.getValue(URL));
    } else if (options.hasOption(DATABASE)
        && FQDN_PATTERN_PARSE.matcher(options.getValue(DATABASE)).matches()) {
      // URL-based connection configuration
      config.setFullyQualifiedDatabaseName(options.getValue(DATABASE));
    } else {
      // Programmatic connection configuration.
      config.setProjectId(options.getRequiredValue(PROJECT))
          .setInstanceName(options.getRequiredValue(INSTANCE))
          .setDatabaseName(options.getRequiredValue(DATABASE));
    }

    config.setCredentials(extractCredentials(options));

    // V1 properties
    if (options.hasOption(PARTIAL_RESULT_SET_FETCH_SIZE)) {
      config.setPartialResultSetFetchSize(options.getValue(PARTIAL_RESULT_SET_FETCH_SIZE));
    }

    if (options.hasOption(DDL_OPERATION_TIMEOUT)) {
      config.setDdlOperationTimeout(options.getValue(DDL_OPERATION_TIMEOUT));
    }

    if (options.hasOption(DDL_OPERATION_POLL_INTERVAL)) {
      config.setDdlOperationPollInterval(options.getValue(DDL_OPERATION_POLL_INTERVAL));
    }

    // V2 properties
    if (options.hasOption(THREAD_POOL_SIZE)) {
      config.setThreadPoolSize(options.getValue(THREAD_POOL_SIZE));
    }

    if (options.hasOption(USE_PLAIN_TEXT)) {
      config.setUsePlainText(true);
    }

    if (options.hasOption(OPTIMIZER_VERSION)) {
      config.setOptimizerVersion(options.getValue(OPTIMIZER_VERSION));
    }

    return config.build();
  }

  /**
   * Extracts credentials from properties passed in either through URL or programmatically.
   * Fails if more than one known security option is specified.
   *
   * @param options user-supplied configuration options
   *
   * @return constructed credentials if successful
   */
  private OAuth2Credentials extractCredentials(ConnectionFactoryOptions options) {

    Set<Option<?>> foundSecurityOptions = new HashSet<>();
    for (Option<?> option : SECURITY_OPTIONS) {
      if (options.hasOption(option)) {
        foundSecurityOptions.add(option);
      }
    }

    if (foundSecurityOptions.size() > 1) {
      throw new IllegalArgumentException(
          "Please provide at most one authentication option. Found: " + foundSecurityOptions);
    }

    if (options.hasOption(OAUTH_TOKEN)) {
      return this.credentialsHelper.getOauthCredentials(options.getValue(OAUTH_TOKEN));
    } else if (options.hasOption(CREDENTIALS)) {
      return this.credentialsHelper.getFileCredentials(options.getValue(CREDENTIALS));
    } else if (options.hasOption(GOOGLE_CREDENTIALS)) {
      return options.getValue(GOOGLE_CREDENTIALS);
    } else if (options.hasOption(USE_PLAIN_TEXT)) {
      return NoCredentials.getInstance();
    }

    return this.credentialsHelper.getDefaultCredentials();
  }

  @VisibleForTesting
  public void setCredentialsHelper(CredentialsHelper credentialsHelper) {
    this.credentialsHelper = credentialsHelper;
  }
}
