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

import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.AUTOCOMMIT;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.CREDENTIALS;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.DRIVER_NAME;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.GOOGLE_CREDENTIALS;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.OPTIMIZER_VERSION;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.PROJECT;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.READONLY;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.URL;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.USE_PLAIN_TEXT;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spanner.r2dbc.v2.SpannerClientLibraryConnectionFactory;
import com.google.protobuf.Value;
import com.google.spanner.v1.PartialResultSet;
import com.google.spanner.v1.ResultSetMetadata;
import com.google.spanner.v1.StructType;
import com.google.spanner.v1.StructType.Field;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link SpannerConnectionFactoryProvider}.
 */
class SpannerConnectionFactoryProviderTest {

  public static final ConnectionFactoryOptions SPANNER_OPTIONS =
      ConnectionFactoryOptions.builder()
          .option(DRIVER, DRIVER_NAME)
          .option(PROJECT, "project-id")
          .option(INSTANCE, "an-instance")
          .option(DATABASE, "db")
          .option(GOOGLE_CREDENTIALS, mock(GoogleCredentials.class))
          .option(USE_PLAIN_TEXT, true) // prevent looking for default credentials
          .build();

  ConnectionFactoryOptions.Builder optionsBuilder;

  SpannerConnectionFactoryProvider spannerConnectionFactoryProvider;

  CredentialsHelper mockCredentialsHelper;

  GoogleCredentials mockCredentials;

  /**
   * Initializes unit under test.
   */
  @BeforeEach
  public void setUp() {
    this.spannerConnectionFactoryProvider = new SpannerConnectionFactoryProvider();

    this.mockCredentialsHelper = mock(CredentialsHelper.class);
    this.spannerConnectionFactoryProvider.setCredentialsHelper(this.mockCredentialsHelper);

    this.mockCredentials = mock(GoogleCredentials.class);

    this.optionsBuilder = ConnectionFactoryOptions.builder()
        .option(DRIVER, "cloudspanner")
        .option(DATABASE, "projects/p/instances/i/databases/d")
        .option(GOOGLE_CREDENTIALS, this.mockCredentials);
  }

  @Test
  void testCreate() {
    ConnectionFactory spannerConnectionFactory =
        this.spannerConnectionFactoryProvider.create(SPANNER_OPTIONS);
    assertThat(spannerConnectionFactory).isNotNull();
    assertThat(spannerConnectionFactory).isInstanceOf(SpannerClientLibraryConnectionFactory.class);
  }

  @Test
  void testCreateFactoryWithOldDriverNameWillReturnCorrectFactory() {
    ConnectionFactory spannerConnectionFactory =
        ConnectionFactories.get("r2dbc:spanner://spanner.googleapis.com:443/projects/"
            + "myproject/instances/myinstance/databases/mydatabase?usePlainText=true");
    assertThat(spannerConnectionFactory)
        .isNotNull()
        .isInstanceOf(SpannerClientLibraryConnectionFactory.class);
  }

  @Test
  void testCreateFactoryWithDriverNameWillReturnCorrectFactory() {
    ConnectionFactory spannerConnectionFactory =
        ConnectionFactories.get("r2dbc:cloudspanner://spanner.googleapis.com:443/projects/"
            + "myproject/instances/myinstance/databases/mydatabase?usePlainText=true");
    assertThat(spannerConnectionFactory)
        .isNotNull()
        .isInstanceOf(SpannerClientLibraryConnectionFactory.class);
  }

  @Test
  void testCreateFactoryWithUrl() {
    ConnectionFactoryOptions optionsWithUrl =
        ConnectionFactoryOptions.builder()
            .option(DRIVER, DRIVER_NAME)
            .option(URL, "r2dbc:cloudspanner://spanner.googleapis.com:443/projects/"
                + "myproject/instances/myinstance/databases/mydatabase")
            .option(GOOGLE_CREDENTIALS, this.mockCredentials)
            .build();

    ConnectionFactory spannerConnectionFactory =
        this.spannerConnectionFactoryProvider.create(optionsWithUrl);
    assertThat(spannerConnectionFactory)
        .isNotNull()
        .isInstanceOf(SpannerClientLibraryConnectionFactory.class);
  }

  @Test
  void testSupportsThrowsExceptionOnNullOptions() {
    assertThatThrownBy(() -> {
      this.spannerConnectionFactoryProvider.supports(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("connectionFactoryOptions must not be null");
  }

  @Test
  void testSupportsReturnsFalseWhenNoDriverInOptions() {
    assertFalse(this.spannerConnectionFactoryProvider.supports(
        ConnectionFactoryOptions.builder().build()));
  }

  @Test
  void testSupportsReturnsFalseWhenWrongDriverInOptions() {
    assertFalse(this.spannerConnectionFactoryProvider.supports(buildOptions("not spanner")));
  }

  @Test
  void testSupportsReturnsTrueWhenCorrectDriverInOptions() {
    assertTrue(this.spannerConnectionFactoryProvider.supports(buildOptions("spanner")));
  }

  @Test
  void getDriverReturnsSpanner() {
    assertThat(this.spannerConnectionFactoryProvider.getDriver()).isEqualTo(DRIVER_NAME);
  }

  @Test
  void createFactoryFromUrlWithOauthCredentials() {

    when(this.mockCredentialsHelper.getOauthCredentials(anyString()))
        .thenReturn(this.mockCredentials);

    ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(
        "r2dbc:cloudspanner://host:443/projects/p/instances/i/databases/d?oauthToken=ABC");
    SpannerConnectionConfiguration config =
        this.spannerConnectionFactoryProvider.createConfiguration(options);

    assertEquals(this.mockCredentials, config.getCredentials());
    verify(this.mockCredentialsHelper).getOauthCredentials("ABC");
  }

  @Test
  void createFactoryFromUrlWithGoogleCredentialsIncorrectType() {

    ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(
        "r2dbc:cloudspanner://host:443/projects/p/instances/i/databases/d?google_credentials=ABC");
    assertThatThrownBy(() -> this.spannerConnectionFactoryProvider.createConfiguration(options))
        .isInstanceOf(ClassCastException.class)
        .hasMessageContaining("com.google.auth.oauth2.OAuth2Credentials");
  }

  @Test
  void createFactoryWithGoogleCredentialsRetainsThem() {

    ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
        .option(DATABASE, "projects/p/instances/i/databases/d")
        .option(DRIVER, "cloudspanner")
        .option(GOOGLE_CREDENTIALS, this.mockCredentials)
        .build();

    SpannerConnectionConfiguration config =
        this.spannerConnectionFactoryProvider.createConfiguration(options);

    assertEquals(this.mockCredentials, config.getCredentials());
    verifyNoInteractions(this.mockCredentialsHelper);
  }

  @Test
  void createFactoryWithFileCredentials() {

    when(this.mockCredentialsHelper.getFileCredentials(anyString()))
        .thenReturn(this.mockCredentials);

    ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(
        "r2dbc:cloudspanner://host:443/projects/p/instances/i/databases/d?credentials=ABCD");

    SpannerConnectionConfiguration config =
        this.spannerConnectionFactoryProvider.createConfiguration(options);

    assertEquals(this.mockCredentials, config.getCredentials());
    verify(this.mockCredentialsHelper).getFileCredentials("ABCD");
  }

  @Test
  void multipleAuthenticationMethodsDisallowed() {
    String prefix = "r2dbc:cloudspanner://host:443/projects/p/instances/i/databases/d?";

    ConnectionFactoryOptions config =
        ConnectionFactoryOptions.parse(prefix + "credentials=A&oauthToken=B");

    assertThatThrownBy(() -> this.spannerConnectionFactoryProvider.createConfiguration(config))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Please provide at most one authentication option");
  }

  @Test
  void multipleAuthenticationMethodsDisallowedProgrammatic() {

    ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
        .option(DATABASE, "projects/p/instances/i/databases/d")
        .option(DRIVER, "cloudspanner")
        .option(GOOGLE_CREDENTIALS, this.mockCredentials)
        .option(CREDENTIALS, "ABC")
        .build();

    assertThatThrownBy(() -> this.spannerConnectionFactoryProvider.createConfiguration(options)
    ).isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Please provide at most one authentication option");
  }

  @Test
  void passOptimizerVersion() {
    ConnectionFactoryOptions options = this.optionsBuilder
        .option(OPTIMIZER_VERSION, "2")
        .build();

    SpannerConnectionConfiguration config =
        this.spannerConnectionFactoryProvider.createConfiguration(options);

    assertEquals("2", config.getOptimizerVersion());
  }

  @Test
  void readonlyFalseByDefault() {
    SpannerConnectionConfiguration config =
        this.spannerConnectionFactoryProvider.createConfiguration(this.optionsBuilder.build());

    assertEquals(false, config.isReadonly());
  }

  @Test
  void passReadonlyTrueOption() {
    ConnectionFactoryOptions options = this.optionsBuilder
        .option(READONLY, true)
        .build();

    SpannerConnectionConfiguration config =
        this.spannerConnectionFactoryProvider.createConfiguration(options);

    assertEquals(true, config.isReadonly());
  }

  @Test
  void autocommitTrueByDefault() {
    SpannerConnectionConfiguration config =
        this.spannerConnectionFactoryProvider.createConfiguration(this.optionsBuilder.build());

    assertEquals(true, config.isAutocommit());
  }

  @Test
  void passAutocommitFalseOption() {
    ConnectionFactoryOptions options = this.optionsBuilder
        .option(AUTOCOMMIT, false)
        .build();

    SpannerConnectionConfiguration config =
        this.spannerConnectionFactoryProvider.createConfiguration(options);

    assertEquals(false, config.isAutocommit());
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
