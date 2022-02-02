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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.Value;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.TestEntity;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/** Tests for Spanner mapping and converting. */
class ConverterAwareMappingSpannerEntityProcessorTests {

  private static final Offset<Double> DELTA = Offset.offset(0.00001);

  private static final Converter<SpannerType, JavaType> SPANNER_TO_JAVA =
      new Converter<SpannerType, JavaType>() {

        @Override
        public JavaType convert(SpannerType source) {
          return new JavaType() {};
        }
      };

  private static final Converter<JavaType, SpannerType> JAVA_TO_SPANNER =
      new Converter<JavaType, SpannerType>() {

        @Override
        public SpannerType convert(@NonNull JavaType source) {
          return new SpannerType() {};
        }
      };

  private SpannerEntityProcessor spannerEntityProcessor;

  private static final Converter<LocalDateTime, Timestamp> LOCAL_DATE_TIME_WRITE_CONVERTER =
      new Converter<LocalDateTime, Timestamp>() {

        @Override
        public Timestamp convert(LocalDateTime localDateTime) {
          return Timestamp.parseTimestamp("1999-01-01T01:01:01.01Z");
        }
      };

  private static final Converter<Timestamp, LocalDateTime> LOCAL_DATE_TIME_READ_CONVERTER =
      new Converter<Timestamp, LocalDateTime>() {

        @Override
        public LocalDateTime convert(Timestamp timestamp) {
          return Instant.ofEpochSecond(1000, 99).atZone(ZoneId.of("UTC")).toLocalDateTime();
        }
      };

  @BeforeEach
  void setUp() {
    this.spannerEntityProcessor =
        new ConverterAwareMappingSpannerEntityProcessor(new SpannerMappingContext());
  }

  @Test
  void customTimeConverter() {
    ConverterAwareMappingSpannerEntityProcessor processorWithCustomConverters =
        new ConverterAwareMappingSpannerEntityProcessor(
            new SpannerMappingContext(),
            Collections.singletonList(LOCAL_DATE_TIME_WRITE_CONVERTER),
            Collections.singletonList(LOCAL_DATE_TIME_READ_CONVERTER));

    Timestamp sourceValue = Timestamp.parseTimestamp("2019-10-12T07:20:50.52Z");
    LocalDateTime dateTime =
        processorWithCustomConverters.getReadConverter().convert(sourceValue, LocalDateTime.class);
    assertThat(dateTime).isEqualTo(LOCAL_DATE_TIME_READ_CONVERTER.convert(sourceValue));

    Timestamp timestamp =
        processorWithCustomConverters.getWriteConverter().convert(dateTime, Timestamp.class);
    assertThat(timestamp).isEqualTo(LOCAL_DATE_TIME_WRITE_CONVERTER.convert(dateTime));

    ConverterAwareMappingSpannerEntityProcessor processor =
        new ConverterAwareMappingSpannerEntityProcessor(new SpannerMappingContext());

    Timestamp sourceValue2 = Timestamp.parseTimestamp("2019-10-12T07:20:50.52Z");
    LocalDateTime dateTime2 =
        processor.getReadConverter().convert(sourceValue2, LocalDateTime.class);
    assertThat(dateTime2).isNotEqualTo(LOCAL_DATE_TIME_READ_CONVERTER.convert(sourceValue2));

    Timestamp timestamp2 = processor.getWriteConverter().convert(dateTime2, Timestamp.class);
    assertThat(timestamp2).isNotEqualTo(LOCAL_DATE_TIME_WRITE_CONVERTER.convert(dateTime2));
  }

  @Test
  void canConvertDefaultTypesNoCustomConverters() {
    ConverterAwareMappingSpannerEntityProcessor converter =
        new ConverterAwareMappingSpannerEntityProcessor(new SpannerMappingContext());

    verifyCanConvert(converter, java.util.Date.class, Timestamp.class);
    verifyCanConvert(converter, Instant.class, Timestamp.class);
    verifyCanConvert(converter, LocalDate.class, Date.class);
  }

  @Test
  void canConvertDefaultTypesCustomConverters() {
    ConverterAwareMappingSpannerEntityProcessor converter =
        new ConverterAwareMappingSpannerEntityProcessor(
            new SpannerMappingContext(),
            Collections.singletonList(JAVA_TO_SPANNER),
            Collections.singletonList(SPANNER_TO_JAVA));

    verifyCanConvert(converter, java.util.Date.class, Timestamp.class);
    verifyCanConvert(converter, LocalDate.class, Date.class);
    verifyCanConvert(converter, Instant.class, Timestamp.class);
    verifyCanConvert(converter, JavaType.class, SpannerType.class);
  }

  @Test
  void timestampCorrespondingType() {
    ConverterAwareMappingSpannerEntityProcessor converter =
        new ConverterAwareMappingSpannerEntityProcessor(new SpannerMappingContext());

    Class<?> spannerJavaType =
        converter.getCorrespondingSpannerJavaType(java.sql.Timestamp.class, false);

    assertThat(spannerJavaType).isEqualTo(Timestamp.class);
  }

  @Test
  void timestampIterableCorrespondingType() {
    ConverterAwareMappingSpannerEntityProcessor converter =
        new ConverterAwareMappingSpannerEntityProcessor(new SpannerMappingContext());

    Class<?> spannerJavaType =
        converter.getCorrespondingSpannerJavaType(java.sql.Timestamp.class, true);

    assertThat(spannerJavaType).isEqualTo(Timestamp.class);
  }

  private void verifyCanConvert(
      ConverterAwareMappingSpannerEntityProcessor converter,
      Class<?> javaType,
      Class<?> spannerType) {
    SpannerWriteConverter writeConverter = converter.getWriteConverter();
    SpannerReadConverter readConverter = converter.getReadConverter();

    assertThat(writeConverter.canConvert(javaType, spannerType)).isTrue();
    assertThat(readConverter.canConvert(spannerType, javaType)).isTrue();
  }

  @Test
  void mapToListTest() {
    List<Double> doubleList = Collections.singletonList(3.33);
    List<String> stringList = Collections.singletonList("string");
    List<Instant> instants =
        Arrays.asList(
            Instant.ofEpochSecond(111), Instant.ofEpochSecond(222), Instant.ofEpochSecond(333));
    List<Timestamp> timestamps =
        Arrays.asList(
            Timestamp.ofTimeSecondsAndNanos(111, 0),
            Timestamp.ofTimeSecondsAndNanos(222, 0),
            Timestamp.ofTimeSecondsAndNanos(333, 0));

    Struct struct1 =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("id2")
            .to(Value.string("key2"))
            .set("id3")
            .to(Value.string("key3"))
            .set("id4")
            .to(Value.string("key4"))
            .set("custom_col")
            .to(Value.string("WHITE"))
            .set("booleanField")
            .to(Value.bool(true))
            .set("intField")
            .to(Value.int64(123L))
            .set("intField2")
            .to(Value.int64(333L))
            .set("longField")
            .to(Value.int64(3L))
            .set("doubleField")
            .to(Value.float64(3.33))
            .set("doubleArray")
            .to(Value.float64Array(new double[] {3.33, 3.33, 3.33}))
            .set("doubleList")
            .to(Value.float64Array(doubleList))
            .set("stringList")
            .to(Value.stringArray(stringList))
            .set("booleanList")
            .to(Value.boolArray(new boolean[] {}))
            .set("longList")
            .to(Value.int64Array(new long[] {}))
            .set("timestampList")
            .to(Value.timestampArray(new ArrayList<>()))
            .set("dateList")
            .to(Value.dateArray(new ArrayList<>()))
            .set("bytesList")
            .to(Value.bytesArray(new ArrayList<>()))
            .set("dateField")
            .to(Value.date(Date.fromYearMonthDay(2018, 11, 22)))
            .set("timestampField")
            .to(Value.timestamp(Timestamp.ofTimeMicroseconds(333)))
            .set("bytes")
            .to(Value.bytes(ByteArray.copyFrom("string1")))
            .set("momentsInTime")
            .to(Value.timestampArray(timestamps))
            .set("commitTimestamp")
            .to(Value.timestamp(Timestamp.ofTimeMicroseconds(1)))
            .set("bigDecimalField")
            .to(Value.numeric(BigDecimal.TEN))
            .set("bigDecimals")
            .to(Value.numericArray(Arrays.asList(BigDecimal.ONE, BigDecimal.ZERO)))
            .build();

    Struct struct2 =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key12"))
            .set("id2")
            .to(Value.string("key22"))
            .set("id3")
            .to(Value.string("key32"))
            .set("id4")
            .to(Value.string("key42"))
            .set("custom_col")
            .to(Value.string("BLACK"))
            .set("booleanField")
            .to(Value.bool(true))
            .set("intField")
            .to(Value.int64(222L))
            .set("intField2")
            .to(Value.int64(555L))
            .set("longField")
            .to(Value.int64(5L))
            .set("doubleField")
            .to(Value.float64(5.55))
            .set("doubleArray")
            .to(Value.float64Array(new double[] {5.55, 5.55}))
            .set("doubleList")
            .to(Value.float64Array(doubleList))
            .set("stringList")
            .to(Value.stringArray(stringList))
            .set("booleanList")
            .to(Value.boolArray(new boolean[] {}))
            .set("longList")
            .to(Value.int64Array(new long[] {}))
            .set("timestampList")
            .to(Value.timestampArray(new ArrayList<>()))
            .set("dateList")
            .to(Value.dateArray(new ArrayList<>()))
            .set("bytesList")
            .to(Value.bytesArray(new ArrayList<>()))
            .set("dateField")
            .to(Value.date(Date.fromYearMonthDay(2019, 11, 22)))
            .set("timestampField")
            .to(Value.timestamp(Timestamp.ofTimeMicroseconds(555)))
            .set("momentsInTime")
            .to(Value.timestampArray(timestamps))
            .set("bytes")
            .to(Value.bytes(ByteArray.copyFrom("string2")))
            .set("commitTimestamp")
            .to(Value.timestamp(Timestamp.ofTimeMicroseconds(1)))
            .set("bigDecimalField")
            .to(Value.numeric(new BigDecimal("0.0001")))
            .set("bigDecimals")
            .to(
                Value.numericArray(
                    Arrays.asList(new BigDecimal("-0.999"), new BigDecimal("10.9001"))))
            .build();

    MockResults mockResults = new MockResults();
    mockResults.structs = Arrays.asList(struct1, struct2);

    ResultSet results = mock(ResultSet.class);
    when(results.next()).thenAnswer(invocation -> mockResults.next());
    when(results.getCurrentRowAsStruct()).thenAnswer(invocation -> mockResults.getCurrent());

    List<TestEntity> entities = this.spannerEntityProcessor.mapToList(results, TestEntity.class);

    verify(results, times(1)).close();

    assertThat(entities).hasSize(2);

    TestEntity t1 = entities.get(0);
    TestEntity t2 = entities.get(1);

    assertThat(t1)
        .hasFieldOrPropertyWithValue("id", "key1")
        .hasFieldOrPropertyWithValue("id4", "key4")
        .hasFieldOrPropertyWithValue("enumField", TestEntity.Color.WHITE)
        .hasFieldOrPropertyWithValue("booleanField", true)
        .hasFieldOrPropertyWithValue("intField", 123)
        .hasFieldOrPropertyWithValue("longField", 3L)
        .hasFieldOrPropertyWithValue("momentsInTime", instants)
        .hasFieldOrPropertyWithValue("bytes", ByteArray.copyFrom("string1"))
        .hasFieldOrPropertyWithValue("commitTimestamp", Timestamp.ofTimeMicroseconds(1))
        .hasFieldOrPropertyWithValue("bigDecimalField", BigDecimal.TEN);

    assertThat(t1.testEmbeddedColumns.id2).isEqualTo("key2");
    assertThat(t1.testEmbeddedColumns.id3).isEqualTo("key3");
    assertThat(t1.testEmbeddedColumns.intField2).isEqualTo(333);
    assertThat(t1.dateField.getYear()).isEqualTo(2018);
    assertThat(t1.doubleField).isEqualTo(3.33, DELTA);
    assertThat(t1.doubleArray).hasSize(3);
    assertThat(t1.bigDecimals).containsExactly(BigDecimal.ONE, BigDecimal.ZERO);

    assertThat(t2)
        .hasFieldOrPropertyWithValue("id", "key12")
        .hasFieldOrPropertyWithValue("id4", "key42")
        .hasFieldOrPropertyWithValue("enumField", TestEntity.Color.BLACK)
        .hasFieldOrPropertyWithValue("booleanField", true)
        .hasFieldOrPropertyWithValue("intField", 222)
        .hasFieldOrPropertyWithValue("longField", 5L)
        .hasFieldOrPropertyWithValue("momentsInTime", instants)
        .hasFieldOrPropertyWithValue("bytes", ByteArray.copyFrom("string2"))
        .hasFieldOrPropertyWithValue("commitTimestamp", Timestamp.ofTimeMicroseconds(1));

    assertThat(t2.testEmbeddedColumns.intField2).isEqualTo(555);
    assertThat(t2.testEmbeddedColumns.id2).isEqualTo("key22");
    assertThat(t2.testEmbeddedColumns.id3).isEqualTo("key32");
    assertThat(t2.dateField.getYear()).isEqualTo(2019);
    assertThat(t2.doubleField).isEqualTo(5.55, DELTA);
    assertThat(t2.doubleArray).hasSize(2);
    assertThat(t2.doubleList).hasSize(1);
    assertThat(t2.doubleList.get(0)).isEqualTo(3.33, DELTA);
    assertThat(t2.stringList).containsExactly("string");
    assertThat(t2.bigDecimalField).isEqualTo(new BigDecimal("0.0001"));
    assertThat(t2.bigDecimals).containsExactly(new BigDecimal("-0.999"), new BigDecimal("10.9001"));
  }

  @Test
  void mapToListPartialColumnsTest() {
    List<Double> doubleList = new ArrayList<>();
    doubleList.add(3.33);
    List<String> stringList = new ArrayList<>();
    stringList.add("string");

    Struct struct1 =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key1"))
            .set("custom_col")
            .to(Value.string("WHITE"))
            .set("doubleList")
            .to(Value.float64Array(doubleList))
            .set("stringList")
            .to(Value.stringArray(stringList))
            .build();

    Struct struct2 =
        Struct.newBuilder()
            .set("id")
            .to(Value.string("key2"))
            .set("custom_col")
            .to(Value.string("BLACK"))
            .set("doubleList")
            .to(Value.float64Array(doubleList))
            .set("stringList")
            .to(Value.stringArray(stringList))
            .build();

    MockResults mockResults = new MockResults();
    mockResults.structs = Arrays.asList(struct1, struct2);

    ResultSet results = mock(ResultSet.class);
    when(results.next()).thenAnswer(invocation -> mockResults.next());
    when(results.getCurrentRowAsStruct()).thenAnswer(invocation -> mockResults.getCurrent());

    List<TestEntity> entities =
        this.spannerEntityProcessor.mapToList(results, TestEntity.class, "id", "custom_col");

    verify(results, times(1)).close();

    assertThat(entities).hasSize(2);

    TestEntity t1 = entities.get(0);
    TestEntity t2 = entities.get(1);

    assertThat(t1.id).isEqualTo("key1");
    assertThat(t1.enumField).isEqualTo(TestEntity.Color.WHITE);

    // This should not have been set
    assertThat(t1.doubleList).isNull();

    assertThat(t2.id).isEqualTo("key2");
    assertThat(t2.enumField).isEqualTo(TestEntity.Color.BLACK);

    // This should not have been set
    assertThat(t2.stringList).isNull();
  }

  private interface SpannerType {}

  private interface JavaType {}

  /** A mock results class for mocked queries. */
  static class MockResults {
    List<Struct> structs;

    int counter = -1;

    boolean next() {
      if (this.counter < this.structs.size() - 1) {
        this.counter++;
        return true;
      }
      return false;
    }

    Struct getCurrent() {
      return this.structs.get(this.counter);
    }
  }
}
