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

import io.r2dbc.spi.ConnectionFactory;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Unit test for {@link SpannerConnectionFactoryProvider}.
 */
public class SpannerConnectionFactoryProviderTest {

  @Test
  public void testCreate() {
    SpannerConnectionFactoryProvider spannerConnectionFactoryProvider =
        new SpannerConnectionFactoryProvider();
    ConnectionFactory spannerConnectionFactory = spannerConnectionFactoryProvider
        .create(null);

    Assertions.assertThat(spannerConnectionFactory).isNotNull();
  }
}
