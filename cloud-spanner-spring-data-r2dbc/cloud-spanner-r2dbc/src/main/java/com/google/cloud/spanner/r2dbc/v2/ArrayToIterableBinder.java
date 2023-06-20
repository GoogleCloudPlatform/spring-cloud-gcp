/*
 * Copyright 2022-2022 Google LLC
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

import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.ValueBinder;
import com.google.cloud.spanner.r2dbc.SpannerType;
import com.google.cloud.spanner.r2dbc.util.Assert;
import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * Binds object arrays to client library {@link ValueBinder} API that requires iterable collections.
 *
 * <p>Also useful for binding arrays of primitives that are too short to be natively supported in
 *     Spanner since these have to use the {@link Iterable}-accepting variants of binding methods.
 *     Only a single array type can be bound by an instance of this class.
 *
 * @param <T> Array element type
 */
class ArrayToIterableBinder<T> implements ClientLibraryTypeBinder {

  private Class<T[]> type;

  private BiConsumer<ValueBinder<Statement.Builder>, Iterable<T>> bindingConsumer;

  ArrayToIterableBinder(Class<T[]> type,
      BiConsumer<ValueBinder<Statement.Builder>, Iterable<T>> bindingConsumer) {
    this.type = type;
    this.bindingConsumer = bindingConsumer;
  }

  @Override
  public boolean canBind(Class<?> type, SpannerType unusedSpannerType) {
    Assert.requireNonNull(type, "type to encode must not be null");

    return this.type.equals(type);
  }

  @Override
  public void bind(Statement.Builder builder, String name, Object value, SpannerType unusedType) {
    if (value == null) {
      this.bindingConsumer.accept(builder.bind(name), null);
    } else {
      this.bindingConsumer.accept(builder.bind(name), Arrays.asList((T[]) value));
    }
  }
}
