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

import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.DRIVER_NAME;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.PROJECT;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.cloud.spanner.r2dbc.client.Client;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit test for {@link SpannerConnectionFactoryProvider}.
 */
public class SpannerConnectionFactoryProviderTest {

  public static final ConnectionFactoryOptions SPANNER_OPTIONS =
      ConnectionFactoryOptions.builder()
          .option(DRIVER, DRIVER_NAME)
          .option(PROJECT, "project-id")
          .option(INSTANCE, "an-instance")
          .option(DATABASE, "db")
          .build();

  SpannerConnectionFactoryProvider spannerConnectionFactoryProvider;

  /**
   * Initializes unit under test with a mock {@link Client}.
   */
  @Before
  public void setUp() {
    this.spannerConnectionFactoryProvider = new SpannerConnectionFactoryProvider();
    Client mockClient = Mockito.mock(Client.class);
    spannerConnectionFactoryProvider.setClient(mockClient);
  }

  @Test
  public void testCreate() {
    ConnectionFactory spannerConnectionFactory =
        this.spannerConnectionFactoryProvider.create(SPANNER_OPTIONS);
    assertThat(spannerConnectionFactory).isNotNull();
    assertThat(spannerConnectionFactory).isInstanceOf(SpannerConnectionFactory.class);
  }

  @Test
  public void testSupportsThrowsExceptionOnNullOptions() {
    assertThatThrownBy(() -> {
      this.spannerConnectionFactoryProvider.supports(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("connectionFactoryOptions must not be null");
  }

  @Test
  public void testSupportsReturnsFalseWhenNoDriverInOptions() {
    assertFalse(this.spannerConnectionFactoryProvider.supports(
        ConnectionFactoryOptions.builder().build()));
  }

  @Test
  public void testSupportsReturnsFalseWhenWrongDriverInOptions() {
    assertFalse(this.spannerConnectionFactoryProvider.supports(buildOptions("not spanner")));
  }

  @Test
  public void testSupportsReturnsTrueWhenCorrectDriverInOptions() {
    assertTrue(this.spannerConnectionFactoryProvider.supports(buildOptions("spanner")));
  }

  private static ConnectionFactoryOptions buildOptions(String driverName) {
    return ConnectionFactoryOptions.builder()
        .option(ConnectionFactoryOptions.DRIVER, driverName)
        .build();
  }
}
