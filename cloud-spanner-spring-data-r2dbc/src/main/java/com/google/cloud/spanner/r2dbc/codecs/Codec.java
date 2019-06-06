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
import reactor.util.annotation.Nullable;

public interface Codec<T> {

  /**
   * Indicates if the codec can decode a value.
   *
   * @param dataType spanner data type
   * @param type input data type
   * @return true the codec can decode a value, false otherwise
   */
  boolean canDecode(Type dataType, Class<?> type);

  /**
   * Indicates if the codec can encode a value.
   *
   * @param type input data object type
   * @return true the codec can encode value, false otherwise
   */
  boolean canEncode(Class type);

  /**
   * Indicates if the codec can encode null.
   *
   * @return true if the codec can encode null, false otherwise
   */
  boolean canEncodeNull(Type type);

  /**
   * Decode data to a value.
   *
   * @param value the {@link Value} object containing the value to decode
   * @param spannerType the type to decode to
   * @return the decoded value
   */
  @Nullable
  T decode(Value value, Type spannerType);


  /**
   * Encode a value.
   *
   * @param value the value to encode
   * @return the encoded value
   */
  Value encode(Object value);

  /**
   * Encode a null value.
   *
   * @return the encoded null value
   */
  Value encodeNull();

  /**
   * The type that can be encoded to or decoded from {@link Value}.
   *
   * @return the encoded value
   */
  Class<?> type();

  TypeCode getTypeCode();
}
