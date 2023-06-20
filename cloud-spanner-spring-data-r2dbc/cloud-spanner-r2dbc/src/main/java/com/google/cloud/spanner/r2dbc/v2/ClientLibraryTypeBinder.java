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

import com.google.cloud.spanner.Statement.Builder;
import com.google.cloud.spanner.r2dbc.SpannerType;

interface ClientLibraryTypeBinder {

  /**
   * Indicates if the binder can bind a value of a given type.
   *
   * @param type input data object type
   * @param spannerType optional {@link SpannerType} hint.
   *
   * @return true if the codec can encode value, false otherwise
   */
  boolean canBind(Class<?> type, SpannerType spannerType);

  /**
   * Bind a value.
   *
   * @param builder a builder to buind the value to
   * @param name parameter name
   * @param value the value to bind
   */
  void bind(Builder builder, String name, Object value, SpannerType spannerType);
}
