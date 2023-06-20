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

import com.google.cloud.spanner.AbstractStructReader;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.Type;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

class ClientLibraryDecoder {
  private static final Map<Type, BiFunction<Struct, Integer, Object>> decodersMap =
      createDecoders();

  private static final Map<Type, BiFunction<Struct, Integer, Object>> arrayDecodersMap =
      createArrayDecoders();

  // Struct type is the same for arrays and lists, so array getters have to live in a separate map.
  private static Map<Type, BiFunction<Struct, Integer, Object>> createArrayDecoders() {
    Map<Type, BiFunction<Struct, Integer, Object>> decoders = new HashMap<>();
    decoders.put(Type.array(Type.int64()), AbstractStructReader::getLongArray);
    decoders.put(Type.array(Type.float64()), AbstractStructReader::getDoubleArray);
    decoders.put(Type.array(Type.bool()), AbstractStructReader::getBooleanArray);

    // Only 3 primitive array types are supported by client library; the rest have to be converted.
    decoders.put(
        Type.array(Type.string()),
        (struct, index) -> struct.getStringList(index).toArray(new String[0]));
    return decoders;
  }

  private static Map<Type, BiFunction<Struct, Integer, Object>> createDecoders() {
    Map<Type, BiFunction<Struct, Integer, Object>> decoders = new HashMap<>();
    decoders.put(Type.int64(), AbstractStructReader::getLong);
    decoders.put(Type.array(Type.int64()), AbstractStructReader::getLongList);

    decoders.put(Type.float64(), AbstractStructReader::getDouble);
    decoders.put(Type.array(Type.float64()), AbstractStructReader::getDoubleList);

    decoders.put(Type.bool(), AbstractStructReader::getBoolean);
    decoders.put(Type.array(Type.bool()), AbstractStructReader::getBooleanList);

    decoders.put(Type.bytes(), AbstractStructReader::getBytes);
    decoders.put(Type.array(Type.bytes()), AbstractStructReader::getBytesList);

    decoders.put(Type.date(), AbstractStructReader::getDate);
    decoders.put(Type.array(Type.date()), AbstractStructReader::getDateList);

    decoders.put(Type.string(), AbstractStructReader::getString);
    decoders.put(Type.array(Type.string()), AbstractStructReader::getStringList);

    decoders.put(Type.timestamp(), AbstractStructReader::getTimestamp);
    decoders.put(Type.array(Type.timestamp()), AbstractStructReader::getTimestampList);

    decoders.put(Type.numeric(), AbstractStructReader::getBigDecimal);
    decoders.put(Type.array(Type.numeric()), AbstractStructReader::getBigDecimalList);

    decoders.put(Type.json(), AbstractStructReader::getJson);
    return decoders;
  }

  /**
   * Decodes result given index and type.
   *
   * @param struct the result struct
   * @param index the index of the result to decode
   * @param type the type of the result
   * @param <T> the type of the result
   *
   * @return decoded value
   */
  public static <T> T decode(Struct struct, int index, Class<T> type) {
    Map<Type, BiFunction<Struct, Integer, Object>> selectedCodecsMap =
        type.isArray() ? arrayDecodersMap : decodersMap;
    Object value =
        struct.isNull(index)
            ? null
            : readAndConvert(struct, index, selectedCodecsMap, type);

    return (T) value;
  }

  private static <T> T readAndConvert(Struct struct, int index,
      Map<Type, BiFunction<Struct, Integer, Object>> selectedCodecsMap, Class<T> type) {
    Object value = selectedCodecsMap.get(struct.getColumnType(index)).apply(struct, index);
    // need to convert to String to JsonWrapper when invoked from Spring Data with type =
    // Object.class
    if (struct.getColumnType(index) == Type.json()) {
      return (T) SpannerClientLibraryConverters.convert(value, JsonWrapper.class);
    }
    if (type.isAssignableFrom(value.getClass())) {
      return (T) value;
    }
    return SpannerClientLibraryConverters.convert(value, type);
  }

  static Class<?> getDefaultJavaType(Type spannerType) {
    switch (spannerType.getCode()) {
      case BOOL: return Boolean.class;
      case INT64: return Long.class;
      case FLOAT64: return Double.class;
      case STRING: return String.class;
      case BYTES: return ByteBuffer.class;
      case TIMESTAMP: return LocalDateTime.class;
      case DATE: return LocalDate.class;
      case NUMERIC: return BigDecimal.class;
      case ARRAY: return Array.newInstance(getDefaultJavaType(spannerType.getArrayElementType()), 0)
          .getClass();
      case JSON: return JsonWrapper.class;
      default:
        return Object.class;
    }
  }
}
