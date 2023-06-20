/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.data.spanner.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.google.cloud.spanner.Options.ReadAndQueryOption;
import com.google.cloud.spanner.Options.ReadOption;
import com.google.cloud.spanner.Options.ReadQueryUpdateTransactionOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests for the Spanner read options. */
class SpannerReadOptionsTests {

  @Test
  void addNullReadOptionTest() {
    SpannerReadOptions testSpannerReadOptions = new SpannerReadOptions();
    assertThatThrownBy(() -> testSpannerReadOptions.addReadOption(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Valid read option is required!");
  }

  @Test
  void addReadOptionTest() {
    SpannerReadOptions spannerReadOptions = new SpannerReadOptions();
    ReadOption r1 = mock(ReadOption.class);
    ReadOption r2 = mock(ReadOption.class);
    spannerReadOptions.addReadOption(r1).addReadOption(r2);
    assertThat(Arrays.asList(spannerReadOptions.getOptions())).containsExactlyInAnyOrder(r1, r2);
  }

  @Test
  void convertReadToQueryOptionTest_withSupportedOptions() {
    SpannerReadOptions spannerReadOptions = new SpannerReadOptions();
    ReadAndQueryOption r1 = mock(ReadAndQueryOption.class);
    ReadQueryUpdateTransactionOption r2 = mock(ReadQueryUpdateTransactionOption.class);
    spannerReadOptions.addReadOption(r1).addReadOption(r2);
    SpannerQueryOptions spannerQueryOptions = spannerReadOptions.toQueryOptions();
    assertThat(spannerQueryOptions.getOptions()).hasSize(2);
  }

  @Test
  void convertReadToQueryOptionTest_throwIfNotSupported() {
    SpannerReadOptions spannerReadOptions = new SpannerReadOptions();
    ReadOption r1 = mock(ReadOption.class);
    spannerReadOptions.addReadOption(r1);
    assertThatThrownBy(spannerReadOptions::toQueryOptions)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Can't convert");
  }

  @Test
  void includePropertiesTest() {
    SpannerReadOptions spannerReadOptions = new SpannerReadOptions();
    Set<String> includeProperties = Collections.emptySet();
    assertThat(spannerReadOptions.getIncludeProperties()).isNull();
    spannerReadOptions.setIncludeProperties(includeProperties);
    assertThat(spannerReadOptions.getIncludeProperties()).isNotNull();
  }
}
