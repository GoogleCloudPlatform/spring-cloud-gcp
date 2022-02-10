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

import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Statement.Builder;
import com.google.cloud.spanner.ValueBinder;
import com.google.cloud.spanner.r2dbc.SpannerType;
import com.google.cloud.spanner.r2dbc.util.Assert;
import java.util.function.BiConsumer;

class SingleTypeBinder<T> implements ClientLibraryTypeBinder {

  private Class<T> type;

  private BiConsumer<ValueBinder<Statement.Builder>, T> bindingConsumer;

  public SingleTypeBinder(
      Class<T> type, BiConsumer<ValueBinder<Statement.Builder>, T> bindingConsumer) {
    this.type = type;
    this.bindingConsumer = bindingConsumer;
  }

  @Override
  public boolean canBind(Class<?> type, SpannerType unusedSpannerType) {
    Assert.requireNonNull(type, "type to encode must not be null");

    return this.type.isAssignableFrom(type);
  }

  @Override
  public void bind(Builder builder, String name, Object value, SpannerType unusedSpannerType) {
    this.bindingConsumer.accept(builder.bind(name), (T) value);
  }
}
