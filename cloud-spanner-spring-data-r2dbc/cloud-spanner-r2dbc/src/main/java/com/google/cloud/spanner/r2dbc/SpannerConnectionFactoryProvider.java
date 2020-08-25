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

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;

import com.google.auth.oauth2.GoogleCredentials;
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

  /** Number of partial result sets to buffer during a read query operation. */
  public static final Option<Integer> PARTIAL_RESULT_SET_FETCH_SIZE =
      Option.valueOf("partial_result_set_fetch_size");

  /** Duration to wait for a DDL operation before timing out. */
  public static final Option<Duration> DDL_OPERATION_TIMEOUT =
      Option.valueOf("ddl_operation_timeout");

  /** Duration to wait between each poll checking for the completion of DDL operations. */
  public static final Option<Duration> DDL_OPERATION_POLL_INTERVAL =
      Option.valueOf("ddl_operation_poll_interval");

  /**
   * Option specifying the location of the GCP credentials file.
   */
  public static final Option<GoogleCredentials> GOOGLE_CREDENTIALS =
      Option.valueOf("google_credentials");

  private Client client;

  @Override
  public ConnectionFactory create(ConnectionFactoryOptions connectionFactoryOptions) {

    SpannerConnectionConfiguration config = createConfiguration(connectionFactoryOptions);
    if (this.client == null) {
      // GrpcClient should only be instantiated if/when a SpannerConnectionFactory is needed.
      this.client = new GrpcClient(config.getCredentials());
    }

    if (connectionFactoryOptions.hasOption(Option.valueOf("client-implementation"))
        && connectionFactoryOptions.getValue(Option.valueOf("client-implementation"))
        .equals("client-library")) {
      return new SpannerClientLibraryConnectionFactory(this.client, config);
    } else {
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

  private static SpannerConnectionConfiguration createConfiguration(
      ConnectionFactoryOptions options) {

    SpannerConnectionConfiguration.Builder config = new SpannerConnectionConfiguration.Builder();

    if (options.hasOption(URL)) {
      config.setUrl(options.getValue(URL));
    } else {
      config.setProjectId(options.getRequiredValue(PROJECT))
          .setInstanceName(options.getRequiredValue(INSTANCE))
          .setDatabaseName(options.getRequiredValue(DATABASE));
    }

    config.setCredentials(options.getValue(GOOGLE_CREDENTIALS));

    if (options.hasOption(PARTIAL_RESULT_SET_FETCH_SIZE)) {
      config.setPartialResultSetFetchSize(options.getValue(PARTIAL_RESULT_SET_FETCH_SIZE));
    }

    if (options.hasOption(DDL_OPERATION_TIMEOUT)) {
      config.setDdlOperationTimeout(options.getValue(DDL_OPERATION_TIMEOUT));
    }

    if (options.hasOption(DDL_OPERATION_POLL_INTERVAL)) {
      config.setDdlOperationPollInterval(options.getValue(DDL_OPERATION_POLL_INTERVAL));
    }

    return config.build();
  }

}
