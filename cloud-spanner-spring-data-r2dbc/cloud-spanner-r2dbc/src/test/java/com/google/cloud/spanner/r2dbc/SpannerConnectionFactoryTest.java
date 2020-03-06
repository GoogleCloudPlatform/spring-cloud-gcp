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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.r2dbc.client.Client;
import com.google.spanner.v1.Session;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Test for {@link SpannerConnectionFactory}.
 */
public class SpannerConnectionFactoryTest {

  private SpannerConnectionConfiguration config;

  /**
   * Sets up {@link SpannerConnectionConfiguration} for test.
   */
  @BeforeEach
  public void setupConnectionConfiguration() throws IOException {
    this.config = new SpannerConnectionConfiguration.Builder()
        .setProjectId("a-project")
        .setInstanceName("an-instance")
        .setDatabaseName("db")
        .build();
  }

  @Test
  public void getMetadataReturnsSingleton() {
    Client mockClient = Mockito.mock(Client.class);
    SpannerConnectionFactory factory = new SpannerConnectionFactory(mockClient, this.config);

    assertThat(factory.getMetadata()).isSameAs(SpannerConnectionFactoryMetadata.INSTANCE);
  }

  @Test
  public void createReturnsNewSpannerConnection() {

    Client mockClient = Mockito.mock(Client.class);
    Session session = Session.newBuilder().setName("jam session").build();
    when(mockClient.createSession("projects/a-project/instances/an-instance/databases/db"))
        .thenReturn(Mono.just(session));

    SpannerConnectionFactory factory = new SpannerConnectionFactory(mockClient, this.config);
    Mono<SpannerConnection> connection = Mono.from(factory.create());

    StepVerifier.create(connection.map(con -> con.getSessionName()))
            .expectNext("jam session")
            .verifyComplete();
  }
}
