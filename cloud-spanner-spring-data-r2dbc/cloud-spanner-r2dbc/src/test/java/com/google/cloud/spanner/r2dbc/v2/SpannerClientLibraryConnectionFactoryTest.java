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

import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.r2dbc.SpannerConnectionConfiguration;
import org.junit.jupiter.api.Test;

public class SpannerClientLibraryConnectionFactoryTest {

  SpannerConnectionConfiguration.Builder configBuilder =
      new SpannerConnectionConfiguration.Builder()
        .setProjectId("test-project")
        .setInstanceName("test-instance")
        .setDatabaseName("test-database")
        .setCredentials(NoCredentials.getInstance());

  @Test
  public void testProjectId() {

    SpannerConnectionConfiguration config = this.configBuilder
        .setProjectId("custom-project")
        .build();

    SpannerOptions options = config.buildSpannerOptions();
    assertThat(options.getProjectId()).isEqualTo("custom-project");
  }

  @Test
  public void testUserAgentString() {

    SpannerConnectionConfiguration config = this.configBuilder.build();

    SpannerOptions options = config.buildSpannerOptions();

    // The version suffix is not available until code is packaged as a JAR.
    assertThat(options.getUserAgent()).startsWith("cloud-spanner-r2dbc/");
  }
}
