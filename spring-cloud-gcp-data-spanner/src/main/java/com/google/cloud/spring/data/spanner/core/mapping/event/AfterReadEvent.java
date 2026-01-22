/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.data.spanner.core.mapping.event;

import com.google.cloud.spanner.KeySet;
import com.google.cloud.spring.data.spanner.core.SpannerReadOptions;
import java.util.Objects;

/**
 * This event read operation on Cloud Spanner.
 */
public final class AfterReadEvent extends LoadEvent {

  private final SpannerReadOptions spannerReadOptions;

  private final KeySet keySet;

  /**
   * Constructor.
   *
   * @param source The entities that were read from Cloud Spanner.
   * @param keys the keys that were read.
   * @param options the options that were used to conduct the read.
   */
  public AfterReadEvent(
      final Iterable source, final KeySet keys, final SpannerReadOptions options) {
    super(source);
    this.keySet = keys;
    this.spannerReadOptions = options;
  }

  /**
   * Get the options that were used to conduct the read.
   *
   * @return the read options.
   */
  public SpannerReadOptions getSpannerReadOptions() {
    return this.spannerReadOptions;
  }

  /**
   * Get the keys that were read.
   *
   * @return the key set.
   */
  public KeySet getKeySet() {
    return this.keySet;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AfterReadEvent that = (AfterReadEvent) o;
    return Objects.equals(getRetrievedEntities(), that.getRetrievedEntities())
        && Objects.equals(getKeySet(), that.getKeySet())
        && Objects.equals(getSpannerReadOptions(), that.getSpannerReadOptions());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRetrievedEntities(), getSpannerReadOptions(), getKeySet());
  }
}