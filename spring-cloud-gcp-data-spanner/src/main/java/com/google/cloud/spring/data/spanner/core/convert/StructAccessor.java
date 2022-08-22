/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.data.spanner.core.convert;

import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.AbstractStructReader;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.Type;
import com.google.cloud.spanner.Type.Code;
import com.google.cloud.spring.core.util.MapBuilder;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerDataException;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * A convenience wrapper class around Struct to make reading columns easier without knowing their
 * type.
 *
 * @since 1.1
 */
public class StructAccessor {

  // @formatter:off
  static final Map<Class, BiFunction<Struct, String, List>> readIterableMapping =
      new MapBuilder<Class, BiFunction<Struct, String, List>>()
          .put(Boolean.class, AbstractStructReader::getBooleanList)
          .put(Long.class, AbstractStructReader::getLongList)
          .put(String.class, AbstractStructReader::getStringList)
          .put(Double.class, AbstractStructReader::getDoubleList)
          .put(Timestamp.class, AbstractStructReader::getTimestampList)
          .put(Date.class, AbstractStructReader::getDateList)
          .put(ByteArray.class, AbstractStructReader::getBytesList)
          .put(BigDecimal.class, AbstractStructReader::getBigDecimalList)
          .put(Struct.class, AbstractStructReader::getStructList)
          .build();

  static final Map<Class, BiFunction<Struct, String, ?>> singleItemReadMethodMapping =
      new MapBuilder<Class, BiFunction<Struct, String, ?>>()
          .put(Boolean.class, AbstractStructReader::getBoolean)
          .put(Long.class, AbstractStructReader::getLong)
          .put(long.class, AbstractStructReader::getLong)
          .put(String.class, AbstractStructReader::getString)
          .put(Double.class, AbstractStructReader::getDouble)
          .put(double.class, AbstractStructReader::getDouble)
          .put(Timestamp.class, AbstractStructReader::getTimestamp)
          .put(Date.class, AbstractStructReader::getDate)
          .put(BigDecimal.class, AbstractStructReader::getBigDecimal)
          .put(ByteArray.class, AbstractStructReader::getBytes)
          .put(double[].class, AbstractStructReader::getDoubleArray)
          .put(long[].class, AbstractStructReader::getLongArray)
          .put(boolean[].class, AbstractStructReader::getBooleanArray)
          // Note that Struct.class appears in this map. While we support
          // converting structs into POJO fields of POJOs, the value in this map is for
          // the case where the field within the POJO is Struct.
          .put(Struct.class, Struct::getStruct)
          .build();

  static final Map<Class, BiFunction<Struct, Integer, ?>> singleItemReadMethodMappingIntCol =
      new MapBuilder<Class, BiFunction<Struct, Integer, ?>>()
          .put(Boolean.class, AbstractStructReader::getBoolean)
          .put(Long.class, AbstractStructReader::getLong)
          .put(long.class, AbstractStructReader::getLong)
          .put(String.class, AbstractStructReader::getString)
          .put(Double.class, AbstractStructReader::getDouble)
          .put(double.class, AbstractStructReader::getDouble)
          .put(Timestamp.class, AbstractStructReader::getTimestamp)
          .put(Date.class, AbstractStructReader::getDate)
          .put(BigDecimal.class, AbstractStructReader::getBigDecimal)
          .put(ByteArray.class, AbstractStructReader::getBytes)
          .put(double[].class, AbstractStructReader::getDoubleArray)
          .put(long[].class, AbstractStructReader::getLongArray)
          .put(boolean[].class, AbstractStructReader::getBooleanArray)
          // Note that Struct.class appears in this map. While we support
          // converting structs into POJO fields of POJOs, the value in this map
          // is for
          // the case where the field within the POJO is Struct.
          .put(Struct.class, Struct::getStruct)
          .build();

  // @formatter:on
  private static final String EXCEPTION_COL_NOT_ARRAY = "Column is not an ARRAY type: ";

  private Struct struct;

  private Set<String> columnNamesIndex;

  private Gson gson;

  public StructAccessor(Struct struct) {
    this.struct = struct;
    this.columnNamesIndex = indexColumnNames();
  }

  public StructAccessor(Struct struct, Gson gson) {
    this.struct = struct;
    this.columnNamesIndex = indexColumnNames();
    this.gson = gson;
  }

  Object getSingleValue(String colName) {
    Type colType = this.struct.getColumnType(colName);
    Class sourceType = getSingleItemTypeCode(colType);
    BiFunction readFunction = singleItemReadMethodMapping.get(sourceType);
    if (readFunction == null) {
      // This case should only occur if the POJO field is non-Iterable, but the column type
      // is ARRAY of STRUCT, TIMESTAMP, DATE, BYTES, or STRING. This use-case is not supported.
      return null;
    }
    return readFunction.apply(this.struct, colName);
  }

  public Object getSingleValue(int colIndex) {
    if (this.struct.isNull(colIndex)) {
      return null;
    }
    Type colType = this.struct.getColumnType(colIndex);
    Class sourceType = getSingleItemTypeCode(colType);
    BiFunction readFunction = singleItemReadMethodMappingIntCol.get(sourceType);
    if (readFunction == null) {
      // This case should only occur if the POJO field is non-Iterable, but the
      // column type
      // is ARRAY of STRUCT, TIMESTAMP, DATE, BYTES, or STRING. This use-case is not
      // supported.
      return null;
    }
    return readFunction.apply(this.struct, colIndex);
  }

  List getListValue(String colName) {
    if (this.struct.getColumnType(colName).getCode() != Code.ARRAY) {
      throw new SpannerDataException(EXCEPTION_COL_NOT_ARRAY + colName);
    }
    Type.Code innerTypeCode = this.struct.getColumnType(colName).getArrayElementType().getCode();
    Class clazz = SpannerTypeMapper.getSimpleJavaClassFor(innerTypeCode);
    BiFunction<Struct, String, List> readMethod = readIterableMapping.get(clazz);
    return readMethod.apply(this.struct, colName);
  }

  <T> List<T> getListJsonValue(String colName, Class<T> colType) {
    if (this.struct.getColumnType(colName).getCode() != Code.ARRAY) {
      throw new SpannerDataException(EXCEPTION_COL_NOT_ARRAY + colName);
    }
    List<String> jsonStringList = this.struct.getJsonList(colName);
    List<T> result = new ArrayList<>();
    jsonStringList.forEach(item ->
        result.add(gson.fromJson(item, colType)));
    return result;
  }

  public  <T> Object getJsonValue(int colIndex, Class<T> colType) {
    if (this.struct.getColumnType(colIndex).getCode() != Code.ARRAY) {
      return getSingleJsonValue(colIndex, colType);
    }
    return getListJsonValue(colIndex, colType);
  }

  private  <T> List<T> getListJsonValue(int colIndex, Class<T> colType) {
    if (this.struct.getColumnType(colIndex).getCode() != Code.ARRAY) {
      throw new SpannerDataException(EXCEPTION_COL_NOT_ARRAY + colIndex);
    }
    List<String> jsonStringList = this.struct.getJsonList(colIndex);
    List<T> result = new ArrayList<>();
    jsonStringList.forEach(item ->
        result.add(gson.fromJson(item, colType)));
    return result;
  }

  boolean hasColumn(String columnName) {
    return this.columnNamesIndex.contains(columnName);
  }

  boolean isNull(String columnName) {
    return this.struct.isNull(columnName);
  }

  private Set<String> indexColumnNames() {
    Set<String> cols = new HashSet<>();
    for (Type.StructField f : this.struct.getType().getStructFields()) {
      cols.add(f.getName());
    }
    return cols;
  }

  private Class getSingleItemTypeCode(Type colType) {
    Code code = colType.getCode();
    return code.equals(Code.ARRAY)
        ? SpannerTypeMapper.getArrayJavaClassFor(colType.getArrayElementType().getCode())
        : SpannerTypeMapper.getSimpleJavaClassFor(code);
  }

  <T> T getSingleJsonValue(String colName, Class<T> colType) {
    if (this.struct.isNull(colName)) {
      return null;
    }
    String jsonString = this.struct.getJson(colName);
    return gson.fromJson(jsonString, colType);
  }

  //TODO: change this to private in next major release
  public  <T> T getSingleJsonValue(int colIndex, Class<T> colType) {
    if (this.struct.getColumnType(colIndex).getCode() != Code.JSON) {
      throw new SpannerDataException("Column of index " + colIndex + " not an JSON type.");
    }
    if (this.struct.isNull(colIndex)) {
      return null;
    }
    String jsonString = this.struct.getJson(colIndex);
    return gson.fromJson(jsonString, colType);
  }
}
