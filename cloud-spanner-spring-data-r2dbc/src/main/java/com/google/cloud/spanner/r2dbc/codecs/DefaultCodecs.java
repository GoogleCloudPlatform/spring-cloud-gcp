/*
 * Copyright 2019 Google LLC
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

package com.google.cloud.spanner.r2dbc.codecs;

import com.google.cloud.spanner.r2dbc.util.Assert;
import com.google.protobuf.NullValue;
import com.google.protobuf.Value;
import com.google.spanner.v1.Type;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import reactor.util.annotation.Nullable;

/**
 * The default {@link Codecs} implementation. Delegates to type-specific codec implementations.
 */
public final class DefaultCodecs implements Codecs {

  static final com.google.protobuf.Value NULL_VALUE =
      com.google.protobuf.Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build();

  private final List<Codec<?>> codecs;

  DefaultCodecs() {
    this.codecs = Arrays.asList(
        new ArrayCodec(this, Boolean[].class),
        new ArrayCodec(this, byte[][].class),
        new ArrayCodec(this, LocalDate[].class),
        new ArrayCodec(this, Double[].class),
        new ArrayCodec(this, Long[].class),
        new ArrayCodec(this, String[].class),
        new ArrayCodec(this, Timestamp[].class),
        new BooleanCodec(),
        new BytesCodec(),
        new DateCodec(),
        new Float64Codec(),
        new Int64Codec(),
        new StringCodec(),
        new TimestampCodec());
  }

  @Override
  @Nullable
  @SuppressWarnings("unchecked")
  public <T> T decode(Value value, Type spannerType, Class<? extends T> type) {
    Assert.requireNonNull(value, "value must not be null");
    Assert.requireNonNull(spannerType, "spannerType must not be null");
    Assert.requireNonNull(type, "type must not be null");

    for (Codec<?> codec : this.codecs) {
      if (codec.canDecode(spannerType, type)) {
        return ((Codec<T>) codec).decode(value, spannerType, type);
      }
    }

    throw new IllegalArgumentException(
        String.format("Cannot decode value of type %s", type.getName()));
  }

  @Override
  public Value encode(Object value) {
    if (value == null) {
      return NULL_VALUE;
    }
    for (Codec<?> codec : this.codecs) {
      if (codec.canEncode(value)) {
        return codec.encode(value);
      }
    }

    throw new IllegalArgumentException(
        String.format("Cannot encode parameter of type %s", value.getClass().getName()));
  }
}
