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

import com.google.protobuf.ListValue;
import com.google.protobuf.ListValue.Builder;
import com.google.protobuf.Value;
import com.google.spanner.v1.TypeCode;

final class ArrayCodec<A> extends SpannerCodec<A[]> {

  private Codecs codecs;

  private TypeCode elementTypeCode;

  ArrayCodec(Codecs codecs, Class<A[]> klass, TypeCode elementTypeCode) {
    super(klass, TypeCode.ARRAY, null,
        (val, spannerType) -> (A[]) ValueUtils.decodeValue(spannerType, val));
    this.codecs = codecs;
    this.elementTypeCode = elementTypeCode;
  }

  @Override
  Value doEncode(A[] value) {
    Builder builder = ListValue.newBuilder();
    for (A val : value) {
      builder.addValues(this.codecs.encode(val));
    }
    return Value.newBuilder().setListValue(builder.build()).build();
  }

  @Override
  public TypeCode getArrayElementTypeCode() {
    return this.elementTypeCode;
  }
}
