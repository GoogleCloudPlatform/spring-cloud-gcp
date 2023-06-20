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
import com.google.cloud.spanner.Value;
import com.google.cloud.spanner.ValueBinder;
import com.google.cloud.spanner.r2dbc.BindingFailureException;
import com.google.cloud.spanner.r2dbc.SpannerType;
import com.google.cloud.spanner.r2dbc.statement.TypedNull;
import com.google.cloud.spanner.r2dbc.util.Assert;
import io.r2dbc.spi.Parameter;
import io.r2dbc.spi.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ClientLibraryBinder {
  private static final List<ClientLibraryTypeBinder> binders = buildBinders();

  private static List<ClientLibraryTypeBinder> buildBinders() {
    List<ClientLibraryTypeBinder> binders = new ArrayList<>();
    binders.add(
        new SingleTypeBinder<>(Integer.class,
            (binder, val) -> binder.to(longFromInteger(val))));
    binders.add(new SingleTypeBinder<>(Long.class, ValueBinder::to));
    binders.add(new SingleTypeBinder<>(Double.class, ValueBinder::to));
    binders.add(new SingleTypeBinder<>(Boolean.class, ValueBinder::to));
    binders.add(new SingleTypeBinder<>(ByteArray.class, ValueBinder::to));
    binders.add(new SingleTypeBinder<>(Date.class, ValueBinder::to));
    binders.add(new SingleTypeBinder<>(String.class, ValueBinder::to));
    binders.add(new SingleTypeBinder<>(Timestamp.class, ValueBinder::to));
    binders.add(new SingleTypeBinder<>(BigDecimal.class, ValueBinder::to));
    binders.add(
        new SingleTypeBinder<>(
            JsonWrapper.class,
            (binder, val) -> binder.to(val == null ? Value.json(null) : val.getJsonVal())));

    // Primitive arrays
    binders.add(new SingleTypeBinder<>(boolean[].class, ValueBinder::toBoolArray));
    binders.add(new SingleTypeBinder<>(long[].class, ValueBinder::toInt64Array));
    binders.add(new SingleTypeBinder<>(double[].class, ValueBinder::toFloat64Array));

    // Primitive arrays that have to expand element size to 64 bits to match Spanner types.
    binders.add(new SingleTypeBinder<>(
        int[].class, (binder, val) -> binder.toInt64Array(toLongArray(val))));
    binders.add(new SingleTypeBinder<>(
        float[].class, (binder, val) -> binder.toFloat64Array(toDoubleArray(val))));

    // Object arrays
    binders.add(new ArrayToIterableBinder<>(Boolean[].class, ValueBinder::toBoolArray));
    binders.add(new ArrayToIterableBinder<>(ByteArray[].class, ValueBinder::toBytesArray));
    binders.add(new ArrayToIterableBinder<>(Date[].class, ValueBinder::toDateArray));
    binders.add(new ArrayToIterableBinder<>(String[].class, ValueBinder::toStringArray));
    binders.add(new ArrayToIterableBinder<>(Timestamp[].class, ValueBinder::toTimestampArray));
    binders.add(new ArrayToIterableBinder<>(BigDecimal[].class, ValueBinder::toNumericArray));

    // STRUCT not supported

    // Binds collections to Spanner array, if an element type hint is available.
    binders.add(new IterableBinder());

    return binders;
  }

  private static long[] toLongArray(int[] input) {
    if (input == null) {
      return new long[0];
    }
    long[] output = new long[input.length];
    for (int i = 0; i < input.length; i++) {
      output[i] = input[i];
    }
    return output;
  }

  private static double[] toDoubleArray(float[] input) {
    if (input == null) {
      return new double[0];
    }
    double[] output = new double[input.length];
    for (int i = 0; i < input.length; i++) {
      output[i] = input[i];
    }
    return output;
  }

  static void bind(Statement.Builder builder, String name, Object value) {
    Assert.requireNonNull(name, "Column name must not be null");
    Assert.requireNonNull(value, "Value must not be null");

    Object finalValue;
    Class<?> valueClass;

    final SpannerType spannerType;

    if (value instanceof Parameter) {
      Parameter param = (Parameter) value;
      finalValue = param.getValue();
      Type type = param.getType();
      spannerType = type instanceof SpannerType ? (SpannerType) param.getType() : null;
      valueClass = type.getJavaType();
    } else if (isTypedNull(value)) {
      finalValue = null;
      valueClass = ((TypedNull) value).getType();
      spannerType = null;
    } else {
      finalValue = value;
      valueClass = value.getClass();
      spannerType = null;
    }

    Optional<ClientLibraryTypeBinder> optionalBinder = binders.stream()
        .filter(e -> e.canBind(valueClass, spannerType))
        .findFirst();
    if (!optionalBinder.isPresent()) {
      throw new BindingFailureException("Can't find a binder for type: " + valueClass);
    }

    optionalBinder.get().bind(builder, name, finalValue, spannerType);
  }

  private static boolean isTypedNull(Object value) {
    return value.getClass().equals(TypedNull.class);
  }

  private static Long longFromInteger(Integer intValue) {
    return intValue == null ? null : intValue.longValue();
  }
}
