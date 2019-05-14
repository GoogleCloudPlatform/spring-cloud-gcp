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

import com.google.protobuf.Value;
import com.google.spanner.v1.Type;
import com.google.spanner.v1.TypeCode;

final class Float64Codec extends AbstractCodec<Double> {

  Float64Codec() {
    super(Double.class);
  }

  @Override
  boolean doCanDecode(Type dataType) {
    return dataType.getCode() == TypeCode.FLOAT64;
  }

  @Override
  Double doDecode(Value value, Type spannerType, Class<? extends Double> type) {
    return (Double) ValueUtils.decodeValue(spannerType, value);
  }

  @Override
  Value doEncode(Double value) {
    Value result;
    if (value == Double.NaN) {
      result = Value.newBuilder().setStringValue("NaN").build();
    } else if (value == Double.NEGATIVE_INFINITY) {
      result = Value.newBuilder().setStringValue("-Infinity").build();
    } else if (value == Double.POSITIVE_INFINITY) {
      result = Value.newBuilder().setStringValue("Infinity").build();
    } else {
      result = Value.newBuilder().setNumberValue(value).build();
    }
    return result;
  }
}
