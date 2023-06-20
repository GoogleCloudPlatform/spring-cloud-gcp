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

import com.google.cloud.spanner.Options.QueryOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests for the Spanner sort and page query options. */
class SpannerSortPageQueryOptionsTests {

  @Test
  void addNullQueryOptionTest() {

    SpannerQueryOptions testSpannerQueryOptions = new SpannerQueryOptions();
    assertThatThrownBy(() -> testSpannerQueryOptions.addQueryOption(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Valid query option is required!");
  }

  @Test
  void includePropertiesTest() {
    SpannerPageableQueryOptions spannerQueryOptions = new SpannerPageableQueryOptions();
    Set<String> includeProperties = Collections.emptySet();
    assertThat(spannerQueryOptions.getIncludeProperties()).isNull();
    spannerQueryOptions.setIncludeProperties(includeProperties);
    assertThat(spannerQueryOptions.getIncludeProperties()).isNotNull();
  }

  @Test
  void addQueryOptionTest() {
    SpannerPageableQueryOptions spannerQueryOptions = new SpannerPageableQueryOptions();
    QueryOption r1 = mock(QueryOption.class);
    QueryOption r2 = mock(QueryOption.class);
    spannerQueryOptions.addQueryOption(r1).addQueryOption(r2);
    assertThat(Arrays.asList(spannerQueryOptions.getOptions())).containsExactlyInAnyOrder(r1, r2);
  }
}
