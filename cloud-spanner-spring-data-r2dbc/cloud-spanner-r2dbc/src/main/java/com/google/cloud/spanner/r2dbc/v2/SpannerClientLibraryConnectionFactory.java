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

package com.google.cloud.spanner.r2dbc.v2;

import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.r2dbc.SpannerConnectionConfiguration;
import com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryMetadata;
import com.google.cloud.spanner.r2dbc.util.Assert;
import io.r2dbc.spi.Closeable;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * The factory is for a specific project/instance/database + credentials.
 *
 */
public class SpannerClientLibraryConnectionFactory implements ConnectionFactory, Closeable {

  private SpannerConnectionConfiguration config;

  private Spanner spannerClient;

  /** R2DBC ConnectionFactory based on the Cloud Spanner Client Library. */
  public SpannerClientLibraryConnectionFactory(SpannerConnectionConfiguration config) {
    this.config = Assert.requireNonNull(config, "Spanner configuration must not be null");

    this.spannerClient = config.buildSpannerOptions().getService();
  }

  @Override
  public Publisher<? extends Connection> create() {
    Mono<SpannerClientLibraryConnection> connection = Mono.just(
        new SpannerClientLibraryConnection(
            new DatabaseClientReactiveAdapter(this.spannerClient, this.config))
    );

    if (this.config.isReadonly()) {
      connection = connection.delayUntil(conn -> conn.beginReadonlyTransaction());
    }

    // Autocommit is on by default; turn off if needed.
    if (!this.config.isAutocommit()) {
      connection = connection.delayUntil(conn -> conn.setAutoCommit(false));
    }

    return connection;
  }

  @Override
  public ConnectionFactoryMetadata getMetadata() {
    return SpannerConnectionFactoryMetadata.INSTANCE;
  }

  /**
   * Cleans up the client library resources.
   *
   * <p>This method returns a publisher based on a blocking client library call, and is meant to be
   * called at the end of the application lifecycle.
   *
   * @return A Mono indicating that the blocking call completed
   */
  @Override
  public Mono<Void> close() {
    return Mono.fromRunnable(() -> this.spannerClient.close());
  }
}
