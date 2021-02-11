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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test for {@link SpannerConnectionMetadata}.
 */
class SpannerConnectionMetadataTest {

  @Test
  void productNameIsCorrect() {
    SpannerConnectionMetadata spannerConnectionMetadata = SpannerConnectionMetadata.INSTANCE;
    assertThat(spannerConnectionMetadata.getDatabaseProductName()).isEqualTo("Cloud Spanner");
  }

  @Test
  void productVersionIrrelevant() {
    assertThat(SpannerConnectionMetadata.INSTANCE.getDatabaseVersion()).isEqualTo("n/a");
  }
}
