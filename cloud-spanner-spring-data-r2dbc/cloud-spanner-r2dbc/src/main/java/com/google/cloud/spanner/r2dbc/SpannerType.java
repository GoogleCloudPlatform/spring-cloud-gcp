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

package com.google.cloud.spanner.r2dbc;


import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.Type;
import com.google.cloud.spanner.Type.Code;
import com.google.cloud.spanner.ValueBinder;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

/**
 * An R2DBC {@link io.r2dbc.spi.Type}-compatible wrapper around Cloud Spanner's {@link Type}.
 */
public abstract class SpannerType implements io.r2dbc.spi.Type {

  private Type type;

  private static final Map<Code, Class<?>> SPANNER_TO_JAVA_TYPES = buildTypeMap();
  private static final Map<Code, IterableStatementBinder> ARRAY_BINDERS = buildArrayBinderMap();

  private SpannerType(Type type) {
    this.type = type;
  }

  public abstract boolean isArray();

  /**
   * Adds type-specific binding of provided value to statement.
   * Fails if the value or type are not compatiblie with Spanner arrays.
   *
   * @param binder a {@link ValueBinder} from a call to {@code Statement.Builder.bind(String)}
   * @param value {@link Iterable} value to bind
   */
  public void bindIterable(ValueBinder<Statement.Builder> binder, Iterable<?> value) {
    if (!this.isArray()) {
      throw new BindingFailureException("Iterable cannot be bound to a non-array Spanner type.");
    }

    IterableStatementBinder typedBinder =
        ARRAY_BINDERS.get(this.type.getArrayElementType().getCode());
    if (typedBinder == null) {
      throw new BindingFailureException("Array binder not found for type " + this.type);
    }
    typedBinder.bind(binder, value);

  }

  /**
   * Returns a `{@link SpannerType} corresponding to the provided Spanner client library type.
   *
   * @param type client library {@link Type}
   * @return `{@link SpannerType} wrapper for the provided client library type
   */
  public static SpannerType of(Type type) {
    return new SpannerType(type) {
      @Override
      public Class<?> getJavaType() {
        return SPANNER_TO_JAVA_TYPES.get(type.getCode());
      }

      @Override
      public String getName() {
        return type.toString();
      }

      @Override
      public boolean isArray() {
        return type.getCode() == Code.ARRAY;
      }
    };
  }

  private static Map<Code, Class<?>> buildTypeMap() {
    Map<Code, Class<?>> map = new EnumMap<>(Code.class);
    map.put(Code.BOOL, Boolean.class);
    map.put(Code.BYTES, ByteArray.class);
    map.put(Code.DATE, Date.class);
    map.put(Code.FLOAT64, Double.class);
    map.put(Code.NUMERIC, BigDecimal.class);
    map.put(Code.INT64, Long.class);
    map.put(Code.STRING, String.class);
    map.put(Code.STRUCT, Struct.class);
    map.put(Code.TIMESTAMP, Timestamp.class);
    map.put(Code.ARRAY, Iterable.class);

    return map;
  }

  private static Map<Code, IterableStatementBinder> buildArrayBinderMap() {
    Map<Code, IterableStatementBinder> map = new EnumMap<>(Code.class);
    map.put(Code.BOOL, (b, i) -> b.toBoolArray((Iterable<Boolean>) i));
    map.put(Code.INT64, (b, i) -> b.toInt64Array((Iterable<Long>) i));
    map.put(Code.NUMERIC, (b, i) -> b.toNumericArray((Iterable<BigDecimal>) i));
    map.put(Code.FLOAT64, (b, i) -> b.toFloat64Array((Iterable<Double>) i));
    map.put(Code.STRING, (b, i) -> b.toStringArray((Iterable<String>) i));
    map.put(Code.BYTES, (b, i) -> b.toBytesArray((Iterable<ByteArray>) i));
    map.put(Code.TIMESTAMP, (b, i) -> b.toTimestampArray((Iterable<Timestamp>) i));
    map.put(Code.DATE, (b, i) -> b.toDateArray((Iterable<Date>) i));
    // no JSON, ARRAY, STRUCT for now

    return map;
  }

  // More readable equivalent to BiConsumer<ValueBinder<Statement.Builder>, Iterable<?>>
  @FunctionalInterface
  private interface IterableStatementBinder {
    void bind(ValueBinder<Statement.Builder> binder, Iterable<?> value);
  }
}
