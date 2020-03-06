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

import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.DRIVER_NAME;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.GOOGLE_CREDENTIALS;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.PARTIAL_RESULT_SET_FETCH_SIZE;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.PROJECT;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.URL;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spanner.r2dbc.client.Client;
import com.google.protobuf.Value;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.Session;
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

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
          .option(GOOGLE_CREDENTIALS, mock(GoogleCredentials.class))
          .build();

  SpannerConnectionFactoryProvider spannerConnectionFactoryProvider;

  Client mockClient;

  /**
   * Initializes unit under test with a mock {@link Client}.
   */
  @BeforeEach
  public void setUp() {
    this.mockClient =  mock(Client.class);
    this.spannerConnectionFactoryProvider = new SpannerConnectionFactoryProvider();
    this.spannerConnectionFactoryProvider.setClient(this.mockClient);
  }

  @Test
  public void testCreate() {
    ConnectionFactory spannerConnectionFactory =
        this.spannerConnectionFactoryProvider.create(SPANNER_OPTIONS);
    assertThat(spannerConnectionFactory).isNotNull();
    assertThat(spannerConnectionFactory).isInstanceOf(SpannerConnectionFactory.class);
  }

  @Test
  public void testCreateFactoryWithUrl() {
    ConnectionFactoryOptions optionsWithUrl =
        ConnectionFactoryOptions.builder()
            .option(DRIVER, DRIVER_NAME)
            .option(URL, "r2dbc:spanner://spanner.googleapis.com:443/projects/"
                + "myproject/instances/myinstance/databases/mydatabase")
            .build();

    ConnectionFactory spannerConnectionFactory =
        this.spannerConnectionFactoryProvider.create(optionsWithUrl);
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

  @Test
  public void getDriverReturnsSpanner() {
    assertThat(this.spannerConnectionFactoryProvider.getDriver()).isEqualTo(DRIVER_NAME);
  }

  @Test
  public void partialResultSetFetchSizePropagatesAsDemand() {
    ConnectionFactoryOptions options =
        ConnectionFactoryOptions.builder()
            .option(PARTIAL_RESULT_SET_FETCH_SIZE, 4)
            .option(DRIVER, DRIVER_NAME)
            .option(PROJECT, "project-id")
            .option(INSTANCE, "an-instance")
            .option(DATABASE, "db")
            .option(GOOGLE_CREDENTIALS, mock(GoogleCredentials.class))
            .build();

    SpannerConnectionFactory connectionFactory
        = (SpannerConnectionFactory)this.spannerConnectionFactoryProvider.create(options);

    TestPublisher<PartialResultSet> partialResultSetPublisher = TestPublisher.create();
    when(this.mockClient.executeStreamingSql(
        any(StatementExecutionContext.class), any(), any(), any()))
        .thenReturn(partialResultSetPublisher.flux());
    Session session = Session.newBuilder().setName("session-name").build();
    when(this.mockClient.createSession(any()))
        .thenReturn(Mono.just(session));


    StepVerifier.create(connectionFactory.create()
        .flatMapMany(c -> c.createStatement("SELECT * from table").execute())
            .flatMap(result -> result.map((row,meta) -> row.get(0, String.class)))
        )
        .then(() -> {
          // switchOnFirst() requests one element, then requests the demand balance.
          partialResultSetPublisher.assertMinRequested(1);
          partialResultSetPublisher.next(makeBook("Odyssey"));
          partialResultSetPublisher.assertMinRequested(3);
        })
        .expectNext("Odyssey")
        .then(() -> {
          partialResultSetPublisher.next(makeBook("Illiad"), makeBook("Margites"));
          partialResultSetPublisher.assertMinRequested(4);
        })
        .expectNext("Illiad")
        .expectNext("Margites")
        .then(() -> {
          partialResultSetPublisher.complete();
        })
        .verifyComplete();
  }

  private PartialResultSet makeBook(String odyssey) {
    return PartialResultSet.newBuilder()
        .setMetadata(ResultSetMetadata.newBuilder().setRowType(StructType.newBuilder()
            .addFields(
                Field.newBuilder().setName("book")
                    .setType(Type.newBuilder().setCode(TypeCode.STRING)))))
        .addValues(Value.newBuilder().setStringValue(odyssey))
        .build();
  }

  private static ConnectionFactoryOptions buildOptions(String driverName) {
    return ConnectionFactoryOptions.builder()
        .option(ConnectionFactoryOptions.DRIVER, driverName)
        .build();
  }
}
