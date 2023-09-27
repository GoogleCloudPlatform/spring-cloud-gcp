/*
 * Copyright 2021-2021 Google LLC
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

package com.google.cloud.spanner.r2dbc.springdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.r2dbc.SpannerConnectionConfiguration;
import com.google.cloud.spanner.r2dbc.v2.SpannerClientLibraryConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.binding.BindMarkersFactory;

class SpannerBindMarkerFactoryProviderTest {

  @Test
  void spannerBindMarkersFoundForV2ConnectionFactory() {
    SpannerBindMarkerFactoryProvider provider = new SpannerBindMarkerFactoryProvider();
    SpannerConnectionConfiguration mockConfig = mock(SpannerConnectionConfiguration.class);
    SpannerOptions mockSpannerOptions = mock(SpannerOptions.class);
    Spanner mockService = mock(Spanner.class);

    when(mockConfig.buildSpannerOptions()).thenReturn(mockSpannerOptions);
    when(mockSpannerOptions.getService()).thenReturn(mockService);

    SpannerClientLibraryConnectionFactory cf =
        new SpannerClientLibraryConnectionFactory(mockConfig);

    BindMarkersFactory factory = provider.getBindMarkers(cf);
    assertThat(factory).isSameAs(SpannerR2dbcDialect.NAMED);
  }

  @Test
  void spannerBindMarkersNotFoundForUnknownFactory() {
    SpannerBindMarkerFactoryProvider provider = new SpannerBindMarkerFactoryProvider();
    ConnectionFactory cf = mock(ConnectionFactory.class);
    ConnectionFactoryMetadata mockMetadata = mock(ConnectionFactoryMetadata.class);
    when(cf.getMetadata()).thenReturn(mockMetadata);
    when(mockMetadata.getName()).thenReturn("SOME_DATABASE");

    BindMarkersFactory factory = provider.getBindMarkers(cf);
    assertThat(factory).isNull();
  }

}
