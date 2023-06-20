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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.r2dbc.SpannerConnectionConfiguration;
import com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryMetadata;
import io.r2dbc.spi.Connection;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class SpannerClientLibraryConnectionFactoryTest {

  SpannerConnectionConfiguration.Builder configBuilder =
      new SpannerConnectionConfiguration.Builder()
        .setProjectId("test-project")
        .setInstanceName("test-instance")
        .setDatabaseName("test-database")
        .setCredentials(NoCredentials.getInstance());

  @Test
  void testProjectId() {

    SpannerConnectionConfiguration config = this.configBuilder
        .setProjectId("custom-project")
        .build();

    SpannerOptions options = config.buildSpannerOptions();
    assertThat(options.getProjectId()).isEqualTo("custom-project");
  }

  @Test
  void testUserAgentString() {

    SpannerConnectionConfiguration config = this.configBuilder.build();

    SpannerOptions options = config.buildSpannerOptions();

    // The version suffix is not available until code is packaged as a JAR.
    assertThat(options.getUserAgent()).startsWith("cloud-spanner-r2dbc/");
  }

  @Test
  void testSessionCreation() {
    SpannerClientLibraryConnectionFactory cf =
        new SpannerClientLibraryConnectionFactory(this.configBuilder.build());
    Connection conn = Mono.from(cf.create()).block();

    assertThat(conn).isInstanceOf(SpannerClientLibraryConnection.class);
  }

  @Test
  void createConnectionDefaultsToAutocommitOn() {
    SpannerClientLibraryConnectionFactory cf = new SpannerClientLibraryConnectionFactory(
        this.configBuilder.build()
    );
    StepVerifier.create(Mono.from(cf.create()).map(conn -> conn.isAutoCommit()))
        .expectNext(true)
        .verifyComplete();
  }

  @Test
  void createConnectionWithAutocommitExplicitlyOn() {
    SpannerClientLibraryConnectionFactory cf = new SpannerClientLibraryConnectionFactory(
        this.configBuilder
            .setAutocommit(true)
            .build()
    );
    StepVerifier.create(Mono.from(cf.create()).map(conn -> conn.isAutoCommit()))
        .expectNext(true)
        .verifyComplete();
  }

  @Test
  void createConnectionWithAutocommitExplicitlyOff() {
    SpannerClientLibraryConnectionFactory cf = new SpannerClientLibraryConnectionFactory(
        this.configBuilder
            .setAutocommit(false)
            .build()
    );
    StepVerifier.create(Mono.from(cf.create()).map(conn -> conn.isAutoCommit()))
        .expectNext(false)
        .verifyComplete();
  }

  @Test
  void createConnectionDefaultsToReadonlyOff() {
    SpannerClientLibraryConnectionFactory cf = new SpannerClientLibraryConnectionFactory(
        this.configBuilder.build()
    );
    StepVerifier.create(
        Mono.from(cf.create())
            .map(conn -> ((SpannerClientLibraryConnection) conn).isInReadonlyTransaction())
    )
        .expectNext(false)
        .verifyComplete();
  }

  @Test
  void createConnectionWithReadonlyExplicitlyOn() {
    SpannerClientLibraryConnectionFactory cf = new SpannerClientLibraryConnectionFactory(
        this.configBuilder
            .setReadonly(true)
            .build()
    );
    StepVerifier.create(
        Mono.from(cf.create())
          .map(conn -> ((SpannerClientLibraryConnection) conn).isInReadonlyTransaction())
    )
        .expectNext(true)
        .verifyComplete();
  }

  @Test
  void createConnectionWithReadonlyExplicitlyOff() {
    SpannerClientLibraryConnectionFactory cf = new SpannerClientLibraryConnectionFactory(
        this.configBuilder
            .setReadonly(false)
            .build()
    );
    StepVerifier.create(
        Mono.from(cf.create())
            .map(conn -> ((SpannerClientLibraryConnection) conn).isInReadonlyTransaction())
    )
        .expectNext(false)
        .verifyComplete();
  }

  @Test
  void connectionFactoryClosingResultsInSpannerClientClosure() {
    SpannerConnectionConfiguration mockConfig = mock(SpannerConnectionConfiguration.class);
    SpannerOptions mockSpannerOptions = mock(SpannerOptions.class);
    Spanner mockSpanner = mock(Spanner.class);
    when(mockConfig.buildSpannerOptions()).thenReturn(mockSpannerOptions);
    when(mockSpannerOptions.getService()).thenReturn(mockSpanner);

    SpannerClientLibraryConnectionFactory cf =
        new SpannerClientLibraryConnectionFactory(mockConfig);
    StepVerifier.create(cf.close()).verifyComplete();

    verify(mockSpanner).close();
    verifyNoMoreInteractions(mockSpanner);

  }

  @Test
  void testGetMetadata() {
    SpannerClientLibraryConnectionFactory cf =
        new SpannerClientLibraryConnectionFactory(this.configBuilder.build());

    assertThat(cf.getMetadata()).isSameAs(SpannerConnectionFactoryMetadata.INSTANCE);

    cf.close().block();
  }
}
