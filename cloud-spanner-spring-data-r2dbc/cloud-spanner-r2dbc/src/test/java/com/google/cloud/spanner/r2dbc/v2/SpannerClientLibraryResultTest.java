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

package com.google.cloud.spanner.r2dbc.v2;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.r2dbc.spi.RowMetadata;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class SpannerClientLibraryResultTest {

  @Test
  void nullRowsNotAllowed() {
    assertThrows(IllegalArgumentException.class,
        () -> new SpannerClientLibraryResult(null, 42));
  }

  @Test
  void getRowsUpdatedReturnsCorrectNumber() {
    SpannerClientLibraryResult result = new SpannerClientLibraryResult(Flux.empty(), 42);
    StepVerifier.create(result.getRowsUpdated())
        .expectNext(42)
        .verifyComplete();
  }

  @Test
  void mapGeneratesMetadataOnlyOnFirstCall() {
    SpannerClientLibraryRow mockRow1 = mock(SpannerClientLibraryRow.class);
    when(mockRow1.get("col")).thenReturn("value1");
    RowMetadata mockMetadata = mock(RowMetadata.class);
    when(mockRow1.generateMetadata()).thenReturn(mockMetadata);

    SpannerClientLibraryRow mockRow2 = mock(SpannerClientLibraryRow.class);
    when(mockRow2.get("col")).thenReturn("value2");

    SpannerClientLibraryResult result =
        new SpannerClientLibraryResult(Flux.just(mockRow1, mockRow2), 0);

    StepVerifier.create(
        result.map((r, rm) -> r.get("col"))
    ).expectNext("value1", "value2")
    .verifyComplete();

    verify(mockRow1).generateMetadata();
    verify(mockRow2, times(0)).generateMetadata();
  }

  @Test
  void filterNotSupported() {
    SpannerClientLibraryResult result = new SpannerClientLibraryResult(Flux.empty(), 0);
    assertThatThrownBy(() -> result.filter(null))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void flatMapWithSegmentNotSupported() {
    SpannerClientLibraryResult result = new SpannerClientLibraryResult(Flux.empty(), 0);
    StepVerifier.create(
        result.flatMap(segment -> Mono.empty())
    ).verifyError(UnsupportedOperationException.class);
  }

}
