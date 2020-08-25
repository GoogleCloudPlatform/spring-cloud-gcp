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

import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.r2dbc.statement.TypedNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ClientLibraryBinder {
  private static final List<ClientLibraryTypeBinder> binders = buildBinders();

  private static List<ClientLibraryTypeBinder> buildBinders() {
    List<ClientLibraryTypeBinder> binders = new ArrayList<>();
    binders.add(
        new ClientLibraryTypeBinderImpl(Long.class, (binder, val) -> binder.to((Long) val)));
    binders.add(
        new ClientLibraryTypeBinderImpl(Double.class, (binder, val) -> binder.to((Double) val)));
    binders.add(
        new ClientLibraryTypeBinderImpl(Boolean.class, (binder, val) -> binder.to((Boolean) val)));
    binders.add(
        new ClientLibraryTypeBinderImpl(ByteArray.class,
            (binder, val) -> binder.to((ByteArray) val)));
    binders.add(
        new ClientLibraryTypeBinderImpl(Date.class, (binder, val) -> binder.to((Date) val)));
    binders.add(
        new ClientLibraryTypeBinderImpl(String.class, (binder, val) -> binder.to((String) val)));
    binders.add(
        new ClientLibraryTypeBinderImpl(Timestamp.class,
            (binder, val) -> binder.to((Timestamp) val)));

    // There is technically one more supported type -  binder.to(Type type, @Nullable Struct value),
    // but it is not clear how r2dbc could pass both the type and the value

    return binders;
  }

  static void bind(Statement.Builder builder, String name, Object value) {
    Class<?> valueClass = isTypedNull(value) ? ((TypedNull) value).getType() : value.getClass();

    Optional<ClientLibraryTypeBinder> optionalBinder =
        binders.stream().filter(e -> e.canBind(valueClass)).findFirst();
    if (!optionalBinder.isPresent()) {
      throw new BindingFailureException("Can't find a binder for type: " + valueClass);
    }
    optionalBinder.get().bind(builder, name, isTypedNull(value) ? null : value);
  }

  private static boolean isTypedNull(Object value) {
    return value.getClass().equals(TypedNull.class);
  }
}
