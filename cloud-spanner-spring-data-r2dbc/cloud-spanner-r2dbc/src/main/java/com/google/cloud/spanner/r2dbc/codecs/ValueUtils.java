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
import com.google.protobuf.Value;
import com.google.protobuf.Value.KindCase;
import com.google.spanner.v1.Type;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ValueUtils {

  static final DateTimeFormatter TIMESTAMP_FORMATTER = (new DateTimeFormatterBuilder())
      .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
      .optionalStart().appendOffsetId().optionalEnd().toFormatter().withZone(ZoneOffset.UTC);

  private static final Pattern FORMAT_REGEXP = Pattern.compile("(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)");

  static Object decodeValue(Type fieldType, com.google.protobuf.Value proto) {
    if (proto.getKindCase() == KindCase.NULL_VALUE) {
      return null;
    }
    switch (fieldType.getCode()) {
      case BOOL:
        checkType(fieldType, proto, KindCase.BOOL_VALUE);
        return parseBoolean(proto);
      case INT64:
        checkType(fieldType, proto, KindCase.STRING_VALUE);
        return parseLong(proto);
      case FLOAT64:
        return valueProtoToFloat64(proto);
      case STRING:
        checkType(fieldType, proto, KindCase.STRING_VALUE);
        return proto.getStringValue();
      case BYTES:
        checkType(fieldType, proto, KindCase.STRING_VALUE);
        return parseBytes(proto);
      case TIMESTAMP:
        checkType(fieldType, proto, KindCase.STRING_VALUE);
        return parseTimestamp(proto);
      case DATE:
        checkType(fieldType, proto, KindCase.STRING_VALUE);
        return parseDate(proto);
      case ARRAY:
        checkType(fieldType, proto, KindCase.LIST_VALUE);
        ListValue listValue = proto.getListValue();
        return decodeArrayValue(fieldType.getArrayElementType(), listValue);
      default:
        throw new AssertionError("Unhandled type code: " + fieldType.getCode());
    }
  }

  private static Object decodeArrayValue(Type elementType, ListValue listValue) {
    switch (elementType.getCode()) {
      case BOOL:
        return listValue.getValuesList().stream()
            .map(ValueUtils::parseBoolean)
            .toArray(Boolean[]::new);
      case INT64:
        return listValue.getValuesList().stream()
            .map(ValueUtils::parseLong)
            .toArray(Long[]::new);
      case FLOAT64:
        return listValue.getValuesList().stream()
            .map(ValueUtils::valueProtoToFloat64)
            .toArray(Double[]::new);
      case STRING:
        return listValue.getValuesList().stream()
            .map(ValueUtils::parseString)
            .toArray(String[]::new);
      case BYTES:
        return listValue.getValuesList().stream()
            .map(ValueUtils::parseBytes)
            .toArray(ByteBuffer[]::new);
      case TIMESTAMP:
        return listValue.getValuesList().stream()
            .map(ValueUtils::parseTimestamp)
            .toArray(LocalDateTime[]::new);
      case DATE:
        return listValue.getValuesList().stream()
            .map(ValueUtils::parseDate)
            .toArray(LocalDate[]::new);

      default:
        throw new AssertionError("Unhandled type code: " + elementType.getCode());
    }
  }

  private static Long parseLong(Value proto) {
    return proto.getKindCase() == KindCase.NULL_VALUE ? null
        : Long.parseLong(proto.getStringValue());
  }

  private static String parseString(Value input) {
    return input.getKindCase() == KindCase.NULL_VALUE ? null : input.getStringValue();
  }

  private static Boolean parseBoolean(Value input) {
    return input.getKindCase() == KindCase.NULL_VALUE ? null : input.getBoolValue();
  }

  private static ByteBuffer parseBytes(Value value) {
    if (value.getKindCase() == KindCase.NULL_VALUE || value.getStringValue() == null) {
      return null;
    }
    return ByteBuffer.wrap(value.getStringValueBytes().toByteArray());
  }

  private static LocalDateTime parseTimestamp(Value proto) {
    if (proto.getKindCase() == KindCase.NULL_VALUE || proto.getStringValue() == null) {
      return null;
    }
    TemporalAccessor temporalAccessor = TIMESTAMP_FORMATTER.parse(proto.getStringValue());
    return LocalDateTime.ofInstant(Instant.from(temporalAccessor), ZoneOffset.UTC);
  }

  private static LocalDate parseDate(Value proto) {
    String date = proto.getStringValue();
    if (proto.getKindCase() == KindCase.NULL_VALUE || date == null) {
      return null;
    }
    Matcher matcher = FORMAT_REGEXP.matcher(date);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid date: " + date);
    } else {
      int year = Integer.parseInt(matcher.group(1));
      int month = Integer.parseInt(matcher.group(2));
      int dayOfMonth = Integer.parseInt(matcher.group(3));
      return LocalDate.of(year, month, dayOfMonth);
    }
  }

  private static void checkType(
      Type fieldType, com.google.protobuf.Value proto, KindCase expected) {
    if (proto.getKindCase() != expected) {
      throw new IllegalArgumentException(
          "Invalid value for column type "
              + fieldType
              + " expected "
              + expected
              + " but was "
              + proto.getKindCase());
    }
  }

  private static Double valueProtoToFloat64(com.google.protobuf.Value proto) {
    if (proto.getKindCase() == KindCase.NULL_VALUE) {
      return null;
    }
    if (proto.getKindCase() == KindCase.STRING_VALUE) {
      switch (proto.getStringValue()) {
        case "-Infinity":
          return Double.NEGATIVE_INFINITY;
        case "Infinity":
          return Double.POSITIVE_INFINITY;
        case "NaN":
          return Double.NaN;
        default:
          // Fall-through to handling below to produce an error.
      }
    }
    if (proto.getKindCase() != KindCase.NUMBER_VALUE) {
      throw new IllegalArgumentException(
          "Invalid value for column type "
              + "Float64"
              + " expected NUMBER_VALUE or STRING_VALUE with value one of"
              + " \"Infinity\", \"-Infinity\", or \"NaN\" but was "
              + proto.getKindCase()
              + (proto.getKindCase() == KindCase.STRING_VALUE
              ? " with value \"" + proto.getStringValue() + "\""
              : ""));
    }
    return proto.getNumberValue();
  }
}
