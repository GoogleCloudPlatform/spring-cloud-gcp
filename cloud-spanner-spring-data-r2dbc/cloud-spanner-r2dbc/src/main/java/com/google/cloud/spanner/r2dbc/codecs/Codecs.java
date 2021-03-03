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

package com.google.cloud.spanner.r2dbc.codecs;

import com.google.protobuf.Value;
import com.google.spanner.v1.Type;
import reactor.util.annotation.Nullable;

/**
 * Encodes and decodes objects.
 */
public interface Codecs {

  /**
   * Decode data to a value.
   *
   * @param value the {@link Value} object containing the value to decode
   * @param spannerType the type to decode to
   * @param type the type to decode to
   * @param <T> the type of item being returned
   * @return the decoded value
   * @throws NullPointerException if {@code format} or {@code type} is {@code null}
   */
  @Nullable
  <T> T decode(Value value, Type spannerType, Class<? extends T> type);

  /**
   * Encode a value.
   *
   * @param value the value to encode
   * @return the encoded value
   * @throws NullPointerException if {@code value} is {@code null}
   */
  Value encode(Object value);

  <T> Codec<T> getCodec(Class<T> type);

}
