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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.Type;
import com.google.cloud.spanner.Type.StructField;
import com.google.cloud.spanner.Value;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.FaultyTestEntity;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.OuterTestEntity;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.OuterTestEntityFlat;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.OuterTestEntityFlatFaulty;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.OuterTestHoldingStructEntity;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.OuterTestHoldingStructsEntity;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.TestEntity;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerDataException;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

/** Tests for converting and reading Spanner entities and objects. */
class ConverterAwareMappingSpannerEntityReaderTests {

  private SpannerEntityReader spannerEntityReader;

  private SpannerReadConverter spannerReadConverter;

  @BeforeEach
  void setup() {
    this.spannerReadConverter = new SpannerReadConverter();
    SpannerMappingContext mappingContext = new SpannerMappingContext(new Gson());
    this.spannerEntityReader =
        new ConverterAwareMappingSpannerEntityReader(
            mappingContext, this.spannerReadConverter);
  }

  @Test
  void readNestedStructTest() {
    Struct innerStruct = Struct.newBuilder().set("value").to(Value.string("inner-value")).build();
    Struct outerStruct =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("innerTestEntities")
            .toStructArray(
                Type.struct(StructField.of("value", Type.string())), List.of(innerStruct))
            .build();

    OuterTestEntity result =
        this.spannerEntityReader.read(OuterTestEntity.class, outerStruct, null, true);
    assertThat(result.id).isEqualTo("key1");
    assertThat(result.innerTestEntities).hasSize(1);
    assertThat(result.innerTestEntities.get(0).value).isEqualTo("inner-value");
    assertThat(result.innerTestEntities.get(0).missingColumnValue).isNull();
  }

  @Test
  void readNestedStructsAsStructsTest() {
    Struct innerStruct = Struct.newBuilder().set("value").to(Value.string("inner-value")).build();
    Struct outerStruct =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("innerStructs")
            .toStructArray(
                Type.struct(StructField.of("value", Type.string())), Arrays.asList(innerStruct))
            .build();

    OuterTestHoldingStructsEntity result =
        this.spannerEntityReader.read(OuterTestHoldingStructsEntity.class, outerStruct);
    assertThat(result.id).isEqualTo("key1");
    assertThat(result.innerStructs).hasSize(1);
    assertThat(result.innerStructs.get(0).getString("value")).isEqualTo("inner-value");
  }

  @Test
  void readNestedStructAsStructTest() {
    Struct innerStruct = Struct.newBuilder().set("value").to(Value.string("inner-value")).build();
    Struct outerStruct =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("innerStruct")
            .to(innerStruct)
            .build();

    OuterTestHoldingStructEntity result =
        this.spannerEntityReader.read(OuterTestHoldingStructEntity.class, outerStruct);
    assertThat(result.id).isEqualTo("key1");
    assertThat(result.innerStruct.getString("value")).isEqualTo("inner-value");
  }

  @Test
  void readArraySingularMismatchTest() {
    Struct rowStruct =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("innerTestEntities")
            .to(Value.int64(3))
            .build();

    assertThatThrownBy(() -> this.spannerEntityReader.read(OuterTestEntity.class, rowStruct))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("Column is not an ARRAY type: innerTestEntities");
  }

  @Test
  void readSingularArrayMismatchTest() {
    Struct colStruct = Struct.newBuilder().set("string_col").to(Value.string("value")).build();
    Struct rowStruct =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("innerLengths")
            .toStructArray(
                Type.struct(StructField.of("string_col", Type.string())), List.of(colStruct))
            .build();

    ConverterAwareMappingSpannerEntityReader testReader = new ConverterAwareMappingSpannerEntityReader(new SpannerMappingContext(), new SpannerReadConverter(
        List.of(
            new Converter<Struct, Integer>() {
              @Nullable
              @Override
              public Integer convert(Struct source) {
                return source.getString("string_col").length();
              }
            })));
    assertThatThrownBy(() -> testReader.read(OuterTestEntityFlatFaulty.class, rowStruct))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("The value in column with name innerLengths could not be converted to the corresponding"
                    + " property in the entity. The property's type is class java.lang.Integer.");
  }

  @Test
  void readConvertedNestedStructTest() {

    Struct colStruct = Struct.newBuilder().set("string_col").to(Value.string("value")).build();
    Struct rowStruct =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("innerLengths")
            .toStructArray(
                Type.struct(StructField.of("string_col", Type.string())), Arrays.asList(colStruct))
            .build();

    OuterTestEntityFlat result =
        new ConverterAwareMappingSpannerEntityReader(
                new SpannerMappingContext(),
                new SpannerReadConverter(
                    List.of(
                        new Converter<Struct, Integer>() {
                          @Nullable
                          @Override
                          public Integer convert(Struct source) {
                            return source.getString("string_col").length();
                          }
                        })))
            .read(OuterTestEntityFlat.class, rowStruct);
    assertThat(result.id).isEqualTo("key1");
    assertThat(result.innerLengths).hasSize(1);
    assertThat(result.innerLengths.get(0)).isEqualTo(5);
  }

  @Test
  void readNotFoundColumnTest() {

    Struct struct =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("custom_col")
            .to(Value.string("string1"))
            .set("booleanField")
            .to(Value.bool(true))
            .set("longField")
            .to(Value.int64(3L))
            .set("doubleArray")
            .to(Value.float64Array(new double[] {3.33, 3.33, 3.33}))
            .set("dateField")
            .to(Value.date(Date.fromYearMonthDay(2018, 11, 22)))
            .set("timestampField")
            .to(Value.timestamp(Timestamp.ofTimeMicroseconds(333)))
            .set("bytes")
            .to(Value.bytes(ByteArray.copyFrom("string1")))
            .build();

    assertThatThrownBy(() -> this.spannerEntityReader.read(TestEntity.class, struct))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("Unable to read column from Cloud Spanner results: id4");
  }

  @Test
  void readUnconvertableValueTest() {

    Struct struct =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("id2")
            .to(Value.string("key2"))
            .set("id3")
            .to(Value.string("key3"))
            .set("id4")
            .to(Value.string("key4"))
            .set("intField2")
            .to(Value.int64(333L))
            .set("custom_col")
            .to(Value.string("WHITE"))
            .set("booleanField")
            .to(Value.bool(true))
            .set("longField")
            .to(Value.int64(3L))
            .set("doubleField")
            .to(Value.string("UNCONVERTABLE VALUE"))
            .set("doubleArray")
            .to(Value.float64Array(new double[] {3.33, 3.33, 3.33}))
            .set("dateField")
            .to(Value.date(Date.fromYearMonthDay(2018, 11, 22)))
            .set("timestampField")
            .to(Value.timestamp(Timestamp.ofTimeMicroseconds(333)))
            .set("bytes")
            .to(Value.bytes(ByteArray.copyFrom("string1")))
            .build();


    assertThatThrownBy(() -> this.spannerEntityReader.read(TestEntity.class, struct))
        .isInstanceOf(ConversionFailedException.class)
        .hasMessage("Failed to convert from type [java.lang.String] to type "
            + "[java.lang.Double] for value [UNCONVERTABLE VALUE]")
        .hasStackTraceContaining(
            "java.lang.NumberFormatException: For input string: \"UNCONVERTABLEVALUE\"");
  }

  @Test
  void readUnmatachableTypesTest() {
    Struct struct =
        Struct.newBuilder().set("fieldWithUnsupportedType").to(Value.string("key1")).build();

    assertThatThrownBy(() -> this.spannerEntityReader.read(FaultyTestEntity.class, struct))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("Unable to read column from Cloud Spanner results: id");
  }

  @Test
  void shouldReadEntityWithNoDefaultConstructor() {
    Struct row = Struct.newBuilder().set("id").to(Value.string("1234")).build();
    TestEntities.SimpleConstructorTester result =
        this.spannerEntityReader.read(TestEntities.SimpleConstructorTester.class, row);

    assertThat(result.id).isEqualTo("1234");
  }

  @Test
  void readNestedStructWithConstructor() {
    Struct innerStruct = Struct.newBuilder().set("value").to(Value.string("value")).build();
    Struct outerStruct =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("innerTestEntities")
            .toStructArray(
                Type.struct(StructField.of("value", Type.string())), List.of(innerStruct))
            .build();

    TestEntities.OuterTestEntityWithConstructor result =
        this.spannerEntityReader.read(
            TestEntities.OuterTestEntityWithConstructor.class, outerStruct, null, true);
    assertThat(result.id).isEqualTo("key1");
    assertThat(result.innerTestEntities).hasSize(1);
    assertThat(result.innerTestEntities.get(0).value).isEqualTo("value");
  }

  @Test
  void testPartialConstructor() {
    Struct struct =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("custom_col")
            .to(Value.string("string1"))
            .set("booleanField")
            .to(Value.bool(true))
            .set("longField")
            .to(Value.int64(3L))
            .set("doubleField")
            .to(Value.float64(3.14))
            .build();

    TestEntities.PartialConstructor result =
        this.spannerEntityReader.read(TestEntities.PartialConstructor.class, struct);
    assertThat(result.longField).isEqualTo(3L);
    assertThat(result.doubleField).isEqualTo(3.14);
  }

  @Test
  void ensureConstructorArgsAreReadOnce() {
    Struct row = mock(Struct.class);
    when(row.getString("id")).thenReturn("1234");
    when(row.getType())
        .thenReturn(Type.struct(List.of(StructField.of("id", Type.string()))));
    when(row.getColumnType("id")).thenReturn(Type.string());

    TestEntities.SimpleConstructorTester result =
        this.spannerEntityReader.read(TestEntities.SimpleConstructorTester.class, row);

    assertThat(result.id).isEqualTo("1234");
    verify(row, times(1)).getString("id");
  }

  @Test
  void testPartialConstructorWithNotEnoughArgs() {
    Struct struct =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("booleanField")
            .to(Value.bool(true))
            .set("longField")
            .to(Value.int64(3L))
            .set("doubleField")
            .to(Value.float64(3.14))
            .build();

    assertThatThrownBy(() -> this.spannerEntityReader.read(TestEntities.PartialConstructor.class, struct))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("Column not found: custom_col");
  }

  @Test
  void zeroArgsListShouldNotThrowError() {

    Struct struct =
        Struct.newBuilder()
            .set("zeroArgsListOfObjects")
            .to(Value.stringArray(List.of("hello", "world")))
            .build();
    // Starting from Spring 3.0, Collection types without generics can be resolved to type with wildcard
    // generics (i.e., "?"). For example, "zeroArgsListOfObjects" will be resolved to List<?>, rather
    // than List.
    assertThatNoException()
        .isThrownBy(() -> this.spannerEntityReader.read(TestEntities.TestEntityWithListWithZeroTypeArgs.class, struct));
  }

  @Test
  void readJsonFieldTest() {
    Struct row = mock(Struct.class);
    when(row.getString("id")).thenReturn("1234");
    when(row.getType())
        .thenReturn(
            Type.struct(
                Arrays.asList(
                    Type.StructField.of("id", Type.string()),
                    Type.StructField.of("params", Type.json()))));
    when(row.getColumnType("id")).thenReturn(Type.string());

    when(row.getJson("params")).thenReturn("{\"p1\":\"address line\",\"p2\":\"5\"}");

    TestEntities.TestEntityJson result =
        this.spannerEntityReader.read(TestEntities.TestEntityJson.class, row);

    assertThat(result.id).isEqualTo("1234");

    assertThat(result.params.p1).isEqualTo("address line");
    assertThat(result.params.p2).isEqualTo("5");
  }

  @Test
  void readArrayJsonFieldTest() {
    Struct row = mock(Struct.class);
    when(row.getString("id")).thenReturn("1234");
    when(row.getType())
        .thenReturn(
            Type.struct(
                Arrays.asList(
                    Type.StructField.of("id", Type.string()),
                    Type.StructField.of("paramsList", Type.array(Type.json())))));
    when(row.getColumnType("id")).thenReturn(Type.string());

    when(row.getColumnType("paramsList")).thenReturn(Type.array(Type.json()));
    when(row.getJsonList("paramsList")).thenReturn(
        Arrays.asList("{\"p1\":\"address line\",\"p2\":\"5\"}",
            "{\"p1\":\"address line 2\",\"p2\":\"6\"}", null));

    TestEntities.TestEntityJsonArray result =
        this.spannerEntityReader.read(TestEntities.TestEntityJsonArray.class, row);

    assertThat(result.id).isEqualTo("1234");

    assertThat(result.paramsList.get(0).p1).isEqualTo("address line");
    assertThat(result.paramsList.get(0).p2).isEqualTo("5");

    assertThat(result.paramsList.get(1).p1).isEqualTo("address line 2");
    assertThat(result.paramsList.get(1).p2).isEqualTo("6");

    assertThat(result.paramsList.get(2)).isNull();
  }
}
