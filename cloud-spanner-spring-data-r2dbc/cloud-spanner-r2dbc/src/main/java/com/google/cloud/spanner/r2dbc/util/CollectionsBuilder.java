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

package com.google.cloud.spanner.r2dbc.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides helpers for building immutable collections of objects conveniently.
 */
public class CollectionsBuilder {

  private CollectionsBuilder() {
    // Prevent instantiation.
  }

  /**
   * Returns an unmodifiable set of the items provided.
   *
   * @param <T> the type of object being built into a {@link Set}.
   * @param items the collection of items to build into a {@link Set}.
   *
   * @return the resulting set of items.
   */
  public static <T> Set<T> setOf(T... items) {
    HashSet<T> set = new HashSet<T>();
    for (T item : items) {
      set.add(item);
    }
    return Collections.unmodifiableSet(set);
  }
}
