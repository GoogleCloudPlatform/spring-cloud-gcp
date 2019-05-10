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

package com.google.cloud.spanner.r2dbc.it;

import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.ServiceOptions;
import com.google.cloud.spanner.r2dbc.SpannerConnection;
import com.google.cloud.spanner.r2dbc.SpannerConnectionFactory;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.junit.Test;
import reactor.core.publisher.Mono;

/**
 * Integration test for connecting to a real Spanner instance.
 */
public class SpannerIT {

  @Test
  public void testCreatingSession() {
    ConnectionFactory connectionFactory =
        ConnectionFactories.get(ConnectionFactoryOptions.builder()
            // TODO: consider whether to bring autodiscovery of project ID
            .option(Option.valueOf("project"), ServiceOptions.getDefaultProjectId())
            .option(DRIVER, "spanner")
            .option(INSTANCE, "reactivetest")
            .option(DATABASE, "testdb")
            .build());

    assertThat(connectionFactory).isInstanceOf(SpannerConnectionFactory.class);

    Mono<Connection> connection = (Mono<Connection>) connectionFactory.create();
    SpannerConnection spannerConnection = (SpannerConnection)connection.block();
    assertThat(spannerConnection.getSession().getName()).contains("/sessions/");
  }

}
