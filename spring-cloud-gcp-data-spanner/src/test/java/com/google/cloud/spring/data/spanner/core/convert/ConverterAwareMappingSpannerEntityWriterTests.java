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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.util.ReflectionUtils.doWithFields;
import static org.springframework.util.ReflectionUtils.setField;

import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Mutation.WriteBuilder;
import com.google.cloud.spanner.Value;
import com.google.cloud.spanner.ValueBinder;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.ChildTestEntity;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.FaultyTestEntity;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.FaultyTestEntity2;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.TestEmbeddedColumns;
import com.google.cloud.spring.data.spanner.core.convert.TestEntities.TestEntity;
import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerDataException;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.test.domain.CommitTimestamps;
import com.google.gson.Gson;
import com.google.spanner.v1.TypeCode;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for the conversion and mapping of entities for write. */
class ConverterAwareMappingSpannerEntityWriterTests {

  private SpannerEntityWriter spannerEntityWriter;

  private SpannerWriteConverter writeConverter;

  @BeforeEach
  void setup() {
    this.writeConverter = new SpannerWriteConverter();
    SpannerMappingContext spannerMappingContext = new SpannerMappingContext(new Gson());
    this.spannerEntityWriter =
        new ConverterAwareMappingSpannerEntityWriter(
            spannerMappingContext, this.writeConverter);
  }

  @Test
  @SuppressWarnings("unchecked")
  void writeTest() {
    TestEntity t = new TestEntity();
    t.id = "key1";
    t.enumField = TestEntity.Color.WHITE;

    // any positive time value will do.
    t.commitTimestamp = Timestamp.ofTimeMicroseconds(1000);

    t.booleanField = true;
    t.intField = 123;
    t.longField = 3L;
    t.doubleField = 3.33;
    t.doubleArray = new double[] {3.33, 3.33, 3.33};
    t.doubleList = new ArrayList<>();
    t.doubleList.add(3.33);
    t.stringList = new ArrayList<>();
    t.stringList.add("stringstringstring");
    t.dateField = Date.fromYearMonthDay(2018, 11, 22);
    t.timestampField = Timestamp.ofTimeMicroseconds(333);
    t.bytes = ByteArray.copyFrom("333");
    t.booleanList = new ArrayList<>();
    t.booleanList.add(t.booleanField);
    t.longList = new ArrayList<>();
    t.longList.add(t.longField);
    t.dateList = new ArrayList<>();
    t.dateList.add(t.dateField);
    t.timestampList = new ArrayList<>();
    t.timestampList.add(t.timestampField);
    t.bytesList = new ArrayList<>();
    t.bytesList.add(t.bytes);

    // this property will be ignored in write mapping because it is a child relationship. no
    // exception will result even though it is an unsupported type for writing.
    t.childTestEntities = new ArrayList<>();
    t.childTestEntities.add(new ChildTestEntity());

    t.testEmbeddedColumns = new TestEmbeddedColumns();
    t.testEmbeddedColumns.id2 = "key2";
    t.testEmbeddedColumns.id3 = "key3";
    t.testEmbeddedColumns.intField2 = 123;

    Instant i1 = Instant.ofEpochSecond(111);
    Instant i2 = Instant.ofEpochSecond(222);
    Instant i3 = Instant.ofEpochSecond(333);
    t.momentsInTime = new ArrayList<>();
    t.momentsInTime.add(i1);
    t.momentsInTime.add(i2);
    t.momentsInTime.add(i3);

    Timestamp t1 = Timestamp.ofTimeSecondsAndNanos(111, 0);
    Timestamp t2 = Timestamp.ofTimeSecondsAndNanos(222, 0);
    Timestamp t3 = Timestamp.ofTimeSecondsAndNanos(333, 0);
    List<Timestamp> timestamps = new ArrayList<>();
    timestamps.add(t1);
    timestamps.add(t2);
    timestamps.add(t3);

    t.bigDecimalField = new BigDecimal("1000000.00001");
    t.bigDecimals = Arrays.asList(new BigDecimal("999999999.0025"), BigDecimal.ZERO);

    WriteBuilder writeBuilder = mock(WriteBuilder.class);

    ValueBinder<WriteBuilder> idBinder = mock(ValueBinder.class);
    when(idBinder.to(anyString())).thenReturn(null);
    when(writeBuilder.set("id")).thenReturn(idBinder);

    ValueBinder<WriteBuilder> id2Binder = mock(ValueBinder.class);
    when(id2Binder.to(anyString())).thenReturn(null);
    when(writeBuilder.set("id2")).thenReturn(id2Binder);

    ValueBinder<WriteBuilder> id3Binder = mock(ValueBinder.class);
    when(id3Binder.to(anyString())).thenReturn(null);
    when(writeBuilder.set("id3")).thenReturn(id3Binder);

    ValueBinder<WriteBuilder> id4Binder = mock(ValueBinder.class);
    when(id4Binder.to(anyString())).thenReturn(null);
    when(writeBuilder.set("id4")).thenReturn(id4Binder);

    ValueBinder<WriteBuilder> stringFieldBinder = mock(ValueBinder.class);
    when(stringFieldBinder.to(anyString())).thenReturn(null);
    when(writeBuilder.set("custom_col")).thenReturn(stringFieldBinder);

    ValueBinder<WriteBuilder> booleanFieldBinder = mock(ValueBinder.class);
    when(booleanFieldBinder.to((Boolean) any())).thenReturn(null);
    when(writeBuilder.set("booleanField")).thenReturn(booleanFieldBinder);

    ValueBinder<WriteBuilder> intFieldBinder = mock(ValueBinder.class);
    when(intFieldBinder.to(anyLong())).thenReturn(null);
    when(writeBuilder.set("intField")).thenReturn(intFieldBinder);

    ValueBinder<WriteBuilder> intField2Binder = mock(ValueBinder.class);
    when(intField2Binder.to(anyLong())).thenReturn(null);
    when(writeBuilder.set("intField2")).thenReturn(intField2Binder);

    ValueBinder<WriteBuilder> longFieldBinder = mock(ValueBinder.class);
    when(longFieldBinder.to(anyString())).thenReturn(null);
    when(writeBuilder.set("longField")).thenReturn(longFieldBinder);

    ValueBinder<WriteBuilder> doubleFieldBinder = mock(ValueBinder.class);
    when(doubleFieldBinder.to(anyDouble())).thenReturn(null);
    when(writeBuilder.set("doubleField")).thenReturn(doubleFieldBinder);

    ValueBinder<WriteBuilder> doubleArrayFieldBinder = mock(ValueBinder.class);
    when(doubleArrayFieldBinder.toStringArray(any())).thenReturn(null);
    when(writeBuilder.set("doubleArray")).thenReturn(doubleArrayFieldBinder);

    ValueBinder<WriteBuilder> doubleListFieldBinder = mock(ValueBinder.class);
    when(doubleListFieldBinder.toFloat64Array((Iterable<Double>) any())).thenReturn(null);
    when(writeBuilder.set("doubleList")).thenReturn(doubleListFieldBinder);

    ValueBinder<WriteBuilder> stringListFieldBinder = mock(ValueBinder.class);
    when(stringListFieldBinder.toStringArray(any())).thenReturn(null);
    when(writeBuilder.set("stringList")).thenReturn(stringListFieldBinder);

    ValueBinder<WriteBuilder> booleanListFieldBinder = mock(ValueBinder.class);
    when(booleanListFieldBinder.toBoolArray((Iterable<Boolean>) any())).thenReturn(null);
    when(writeBuilder.set("booleanList")).thenReturn(booleanListFieldBinder);

    ValueBinder<WriteBuilder> longListFieldBinder = mock(ValueBinder.class);
    when(longListFieldBinder.toStringArray(any())).thenReturn(null);
    when(writeBuilder.set("longList")).thenReturn(longListFieldBinder);

    ValueBinder<WriteBuilder> timestampListFieldBinder = mock(ValueBinder.class);
    when(timestampListFieldBinder.toTimestampArray(any())).thenReturn(null);
    when(writeBuilder.set("timestampList")).thenReturn(timestampListFieldBinder);

    ValueBinder<WriteBuilder> dateListFieldBinder = mock(ValueBinder.class);
    when(dateListFieldBinder.toDateArray(any())).thenReturn(null);
    when(writeBuilder.set("dateList")).thenReturn(dateListFieldBinder);

    ValueBinder<WriteBuilder> instantListFieldBinder = mock(ValueBinder.class);
    when(instantListFieldBinder.toTimestampArray(any())).thenReturn(null);
    when(writeBuilder.set("momentsInTime")).thenReturn(instantListFieldBinder);

    ValueBinder<WriteBuilder> bytesListFieldBinder = mock(ValueBinder.class);
    when(bytesListFieldBinder.toDateArray(any())).thenReturn(null);
    when(writeBuilder.set("bytesList")).thenReturn(bytesListFieldBinder);

    ValueBinder<WriteBuilder> dateFieldBinder = mock(ValueBinder.class);
    when(dateFieldBinder.to((Date) any())).thenReturn(null);
    when(writeBuilder.set("dateField")).thenReturn(dateFieldBinder);

    ValueBinder<WriteBuilder> timestampFieldBinder = mock(ValueBinder.class);
    when(timestampFieldBinder.to((Timestamp) any())).thenReturn(null);
    when(writeBuilder.set("timestampField")).thenReturn(timestampFieldBinder);

    ValueBinder<WriteBuilder> bytesFieldBinder = mock(ValueBinder.class);
    when(bytesFieldBinder.to((ByteArray) any())).thenReturn(null);
    when(writeBuilder.set("bytes")).thenReturn(bytesFieldBinder);

    ValueBinder<WriteBuilder> commitTimestampBinder = mock(ValueBinder.class);
    when(commitTimestampBinder.to((Timestamp) any())).thenReturn(null);
    when(writeBuilder.set("commitTimestamp")).thenReturn(commitTimestampBinder);

    ValueBinder<WriteBuilder> bigDecimalFieldBinder = mock(ValueBinder.class);
    when(bigDecimalFieldBinder.to((BigDecimal) any())).thenReturn(null);
    when(writeBuilder.set("bigDecimalField")).thenReturn(bigDecimalFieldBinder);

    ValueBinder<WriteBuilder> bigDecimalsBinder = mock(ValueBinder.class);
    when(bigDecimalsBinder.toNumericArray(any())).thenReturn(null);
    when(writeBuilder.set("bigDecimals")).thenReturn(bigDecimalsBinder);

    this.spannerEntityWriter.write(t, writeBuilder::set);

    verify(idBinder, times(1)).to(t.id);
    verify(id2Binder, times(1)).to(t.testEmbeddedColumns.id2);
    verify(id3Binder, times(1)).to(t.testEmbeddedColumns.id3);
    verify(stringFieldBinder, times(1)).to(t.enumField.toString());
    verify(booleanFieldBinder, times(1)).to(Boolean.valueOf(t.booleanField));
    verify(intFieldBinder, times(1)).to(Long.valueOf(t.intField));
    verify(intField2Binder, times(1)).to(Long.valueOf(t.testEmbeddedColumns.intField2));
    verify(longFieldBinder, times(1)).to(String.valueOf(t.longField));
    verify(doubleFieldBinder, times(1)).to(Double.valueOf(t.doubleField));
    verify(doubleArrayFieldBinder, times(1)).to("3.33,3.33,3.33");
    verify(doubleListFieldBinder, times(1)).toFloat64Array(t.doubleList);
    verify(stringListFieldBinder, times(1)).toStringArray(t.stringList);
    verify(booleanListFieldBinder, times(1)).toBoolArray(t.booleanList);
    verify(longListFieldBinder, times(1)).toStringArray(any());
    verify(timestampListFieldBinder, times(1)).toTimestampArray(t.timestampList);
    verify(dateListFieldBinder, times(1)).toDateArray(t.dateList);
    verify(bytesListFieldBinder, times(1)).toBytesArray(t.bytesList);
    verify(dateFieldBinder, times(1)).to(t.dateField);
    verify(timestampFieldBinder, times(1)).to(t.timestampField);
    verify(bytesFieldBinder, times(1)).to(t.bytes);
    verify(instantListFieldBinder, times(1)).toTimestampArray(timestamps);

    // the positive value set earlier must not be passed to Spanner. it must be replaced by
    // the dummy value.
    verify(commitTimestampBinder, times(1)).to(Value.COMMIT_TIMESTAMP);

    verify(bigDecimalFieldBinder, times(1)).to(t.bigDecimalField);
    verify(bigDecimalsBinder, times(1)).toNumericArray(t.bigDecimals);
  }

  @Test
  void writeNullColumnsTest() {
    TestEntity t = new TestEntity();

    t.dateField = null;
    t.doubleList = null;

    WriteBuilder writeBuilder = mock(WriteBuilder.class);

    ValueBinder<WriteBuilder> dateFieldBinder = mock(ValueBinder.class);
    when(dateFieldBinder.to((Date) any())).thenReturn(null);
    when(writeBuilder.set("dateField")).thenReturn(dateFieldBinder);

    ValueBinder<WriteBuilder> doubleListFieldBinder = mock(ValueBinder.class);
    when(doubleListFieldBinder.toFloat64Array((Iterable<Double>) any())).thenReturn(null);
    when(writeBuilder.set("doubleList")).thenReturn(doubleListFieldBinder);

    this.spannerEntityWriter.write(
        t,
        writeBuilder::set,
        Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("dateField", "doubleList"))));
    verify(dateFieldBinder, times(1)).to((Date) isNull());
    verify(doubleListFieldBinder, times(1)).toFloat64Array((Iterable<Double>) isNull());
  }

  @Test
  @SuppressWarnings("unchecked")
  void writeSomeColumnsTest() {
    TestEntity t = new TestEntity();
    t.id = "key1";
    t.enumField = TestEntity.Color.BLACK;

    WriteBuilder writeBuilder = mock(WriteBuilder.class);

    ValueBinder<WriteBuilder> idBinder = mock(ValueBinder.class);
    when(idBinder.to(anyString())).thenReturn(null);
    when(writeBuilder.set("id")).thenReturn(idBinder);

    ValueBinder<WriteBuilder> stringFieldBinder = mock(ValueBinder.class);
    when(stringFieldBinder.to(anyString())).thenReturn(null);
    when(writeBuilder.set("custom_col")).thenReturn(stringFieldBinder);

    ValueBinder<WriteBuilder> booleanFieldBinder = mock(ValueBinder.class);
    when(booleanFieldBinder.to((Boolean) any())).thenReturn(null);
    when(writeBuilder.set("booleanField")).thenReturn(booleanFieldBinder);

    this.spannerEntityWriter.write(
        t, writeBuilder::set, new HashSet<>(Arrays.asList("id", "custom_col")));

    verify(idBinder, times(1)).to(t.id);
    verify(stringFieldBinder, times(1)).to(t.enumField.toString());
    verifyNoInteractions(booleanFieldBinder);
  }

  @Test
  void writeJsonTest() {
    TestEntities.Params parameters = new TestEntities.Params("some value", "some other value");
    TestEntities.TestEntityJson testEntity = new TestEntities.TestEntityJson("id1", parameters);

    WriteBuilder writeBuilder = mock(WriteBuilder.class);
    ValueBinder<WriteBuilder> valueBinder = mock(ValueBinder.class);

    when(writeBuilder.set("id")).thenReturn(valueBinder);
    when(writeBuilder.set("params")).thenReturn(valueBinder);

    this.spannerEntityWriter.write(testEntity, writeBuilder::set);

    verify(valueBinder).to(testEntity.id);
    verify(valueBinder).to(Value.json("{\"p1\":\"some value\",\"p2\":\"some other value\"}"));
  }

  @Test
  void writeNullJsonTest() {
    TestEntities.TestEntityJson testEntity = new TestEntities.TestEntityJson("id1", null);

    WriteBuilder writeBuilder = mock(WriteBuilder.class);
    ValueBinder<WriteBuilder> valueBinder = mock(ValueBinder.class);

    when(writeBuilder.set("id")).thenReturn(valueBinder);
    when(writeBuilder.set("params")).thenReturn(valueBinder);

    this.spannerEntityWriter.write(testEntity, writeBuilder::set);

    verify(valueBinder).to(testEntity.id);
    verify(valueBinder).to(Value.json(null));
  }

  @Test
  void writeJsonArrayTest() {
    TestEntities.Params parameters = new TestEntities.Params("some value", "some other value");
    TestEntities.TestEntityJsonArray testEntity = new TestEntities.TestEntityJsonArray("id1", Arrays.asList(parameters, parameters));

    WriteBuilder writeBuilder = mock(WriteBuilder.class);
    ValueBinder<WriteBuilder> valueBinder = mock(ValueBinder.class);

    when(writeBuilder.set("id")).thenReturn(valueBinder);
    when(writeBuilder.set("paramsList")).thenReturn(valueBinder);

    this.spannerEntityWriter.write(testEntity, writeBuilder::set);

    List<String> stringList = new ArrayList<>();
    stringList.add("{\"p1\":\"some value\",\"p2\":\"some other value\"}");
    stringList.add("{\"p1\":\"some value\",\"p2\":\"some other value\"}");

    verify(valueBinder).to(testEntity.id);
    verify(valueBinder).toJsonArray(stringList);
  }

  @Test
  void writeNullEmptyJsonArrayTest() {
    TestEntities.TestEntityJsonArray testNull = new TestEntities.TestEntityJsonArray("id1", null);
    TestEntities.TestEntityJsonArray testEmpty = new TestEntities.TestEntityJsonArray("id2", new ArrayList<>());

    WriteBuilder writeBuilder = mock(WriteBuilder.class);
    ValueBinder<WriteBuilder> valueBinder = mock(ValueBinder.class);

    when(writeBuilder.set("id")).thenReturn(valueBinder);
    when(writeBuilder.set("paramsList")).thenReturn(valueBinder);

    this.spannerEntityWriter.write(testNull, writeBuilder::set);
    this.spannerEntityWriter.write(testEmpty, writeBuilder::set);

    verify(valueBinder).to(testNull.id);
    verify(valueBinder).toJsonArray(isNull());
    verify(valueBinder).to(testEmpty.id);
    verify(valueBinder).toJsonArray(new ArrayList<>());
  }

  @Test
  void writeUnsupportedTypeIterableTest() {

    FaultyTestEntity2 ft = new FaultyTestEntity2();
    ft.listWithUnsupportedInnerType = new ArrayList<>();
    WriteBuilder writeBuilder = Mutation.newInsertBuilder("faulty_test_table_2");

    assertThatThrownBy(() -> this.spannerEntityWriter.write(ft, writeBuilder::set))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("Unsupported mapping for type: interface java.util.List");
  }

  @Test
  void writeIncompatibleTypeTest() {

    FaultyTestEntity ft = new FaultyTestEntity();
    ft.fieldWithUnsupportedType = new TestEntity();
    WriteBuilder writeBuilder = Mutation.newInsertBuilder("faulty_test_table");

    assertThatThrownBy(() -> this.spannerEntityWriter.write(ft, writeBuilder::set))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("Unsupported mapping for type: "
                    + "class com.google.cloud.spring.data.spanner.core.convert.TestEntities$TestEntity");

  }

  @Test
  void writingNullToKeyShouldThrowException() {

    assertThatThrownBy(() -> this.spannerEntityWriter.convertToKey(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Key of an entity to be written cannot be null!");
  }

  @Test
  void writeValidColumnToKey() {
    Key key = this.spannerEntityWriter.convertToKey(true);
    assertThat(key).isEqualTo(Key.of(true));
  }

  @Test
  void testUserSetUnconvertableColumnType() {

    UserSetUnconvertableColumnType userSetUnconvertableColumnType =
        new UserSetUnconvertableColumnType();
    WriteBuilder writeBuilder = Mutation.newInsertBuilder("faulty_test_table");

    assertThatThrownBy(() -> this.spannerEntityWriter.write(userSetUnconvertableColumnType, writeBuilder::set))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("Unsupported mapping for type: boolean");
  }

  @Test
  void testCommitTimestampsType() {
    CommitTimestamps entity = new CommitTimestamps();

    doWithFields(
        CommitTimestamps.class,
        f -> setField(f, entity, CommitTimestamp.of(f.getType())),
        ff -> !ff.isSynthetic() && Objects.isNull(ff.getAnnotation(PrimaryKey.class)));

    WriteBuilder writeBuilder = Mutation.newInsertBuilder("commit_timestamps_table");
    this.spannerEntityWriter.write(entity, writeBuilder::set);
    Mutation mutation = writeBuilder.build();
    assertThat(
            mutation.asMap().entrySet().stream()
                .filter(e -> !"id".equals(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()))
        .isNotEmpty()
        .allMatch(Value::isCommitTimestamp);
  }

  /** A test type that cannot be converted. */
  static class UserSetUnconvertableColumnType {
    @PrimaryKey
    @Column(spannerType = TypeCode.DATE)
    boolean id;
  }
}
