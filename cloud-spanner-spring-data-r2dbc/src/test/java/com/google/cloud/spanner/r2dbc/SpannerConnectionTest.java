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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spanner.r2dbc.client.Client;
import com.google.spanner.v1.Session;
import io.r2dbc.spi.Statement;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test for {@link SpannerConnection}.
 */
public class SpannerConnectionTest {

  @Test
  public void createStatementDummyImplementation() {
    Client mockClient = Mockito.mock(Client.class);
    SpannerConnectionConfiguration config
        = new SpannerConnectionConfiguration.Builder()
        .setProjectId("a-project")
        .setInstanceName("an-instance")
        .setDatabaseName("db")
        .build();
    Session session = Session.newBuilder().setName("project/session/1234").build();
    SpannerConnection connection = new SpannerConnection(mockClient, session);
    Statement statement = connection.createStatement("not actual sql");
    assertThat(statement).isInstanceOf(SpannerStatement.class);
  }
}
