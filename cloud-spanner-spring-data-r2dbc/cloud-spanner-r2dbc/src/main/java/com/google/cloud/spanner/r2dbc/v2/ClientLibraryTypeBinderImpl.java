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
import com.google.cloud.spanner.ValueBinder;
import com.google.cloud.spanner.r2dbc.util.Assert;
import java.util.function.BiConsumer;

public class ClientLibraryTypeBinderImpl<T> implements ClientLibraryTypeBinder<T> {

  private Class<T> type;

  private BiConsumer<ValueBinder, T> bindingConsumer;

  public ClientLibraryTypeBinderImpl(
      Class<T> type, BiConsumer<ValueBinder, T> bindingConsumer) {
    this.type = type;
    this.bindingConsumer = bindingConsumer;
  }

  @Override
  public boolean canBind(Class<T> type) {
    Assert.requireNonNull(type, "type to encode must not be null");

    return this.type.isAssignableFrom(type);
  }

  @Override
  public void bind(Builder builder, String name, T value) {
    this.bindingConsumer.accept(builder.bind(name), value);
  }
}
