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
import com.google.cloud.spanner.Interval;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.Embedded;
import com.google.cloud.spring.data.spanner.core.mapping.Interleaved;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import com.google.spanner.v1.TypeCode;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Test entities for Spanner tests that hit many features and situations. */
class TestEntities {

  /** Test domain type holding properties with many types and key components. */
  @Table(name = "custom_test_table")
  static class TestEntity {
    @PrimaryKey String id;

    @PrimaryKey(keyOrder = 3)
    String id4;

    @PrimaryKey(keyOrder = 2)
    @Embedded
    TestEmbeddedColumns testEmbeddedColumns;

    @Column(name = "custom_col")
    Color enumField;

    @Column(name = "")
    boolean booleanField;

    // This long is forced to be stored as a String for testing
    @Column(spannerType = TypeCode.STRING)
    long longField;

    double doubleField;
    float floatField;

    // This double array is forced to be stored as a String for testing
    @Column(spannerType = TypeCode.STRING)
    double[] doubleArray;

    // This double array is forced to be stored as a String for testing
    @Column(spannerType = TypeCode.STRING)
    float[] floatArray;

    // int is not a native Cloud Spanner type, so this will utilize custom conversions.
    int intField;

    List<Double> doubleList;
    List<Float> floatList;

    List<String> stringList;

    List<Boolean> booleanList;

    // This long list is forced to be stored as a String for testing
    @Column(spannerType = TypeCode.STRING)
    List<Long> longList;

    List<Timestamp> timestampList;

    List<Date> dateList;

    List<ByteArray> bytesList;

    List<Instant> momentsInTime;

    Date dateField;

    Timestamp timestampField;

    ByteArray bytes;

    @Interleaved(lazy = true)
    List<ChildTestEntity> childTestEntities;

    /** A enum used to test conversion and storage. */
    enum Color {
      WHITE,
      BLACK
    }

    @Column(spannerCommitTimestamp = true)
    Timestamp commitTimestamp;

    BigDecimal bigDecimalField;

    List<BigDecimal> bigDecimals;

    Interval intervalField;

    List<Interval> intervalList;

    UUID uuidField;

    List<UUID> uuidList;
  }

  /** A test entity that acts as a child of another entity. */
  static class ChildTestEntity {
    @PrimaryKey String id;

    @PrimaryKey(keyOrder = 3)
    String id4;

    @PrimaryKey(keyOrder = 2)
    @Embedded
    TestEmbeddedColumns testEmbeddedColumns;

    @PrimaryKey(keyOrder = 4)
    String id5;
  }

  /** A test class that holds key components while being embedded. */
  static class TestEmbeddedColumns {
    @PrimaryKey String id2;

    @PrimaryKey(keyOrder = 2)
    String id3;

    int intField2;
  }

  /** A fault class with a bad property type. */
  @Table(name = "faulty_test_table")
  static class FaultyTestEntity {
    @PrimaryKey String id;

    TestEntity fieldWithUnsupportedType;
  }

  /** A fault class with unsupported list property type. */
  @Table(name = "faulty_test_table_2")
  static class FaultyTestEntity2 {
    @PrimaryKey String id;

    List<TestEntity> listWithUnsupportedInnerType;
  }

  /** A test class that holds an inner collection of entities. */
  @Table(name = "outer_test_entity")
  static class OuterTestEntity {
    @PrimaryKey String id;

    List<InnerTestEntity> innerTestEntities;
  }

  /** A test class that holds Structs inside. */
  @Table(name = "outer_test_entity")
  static class OuterTestHoldingStructsEntity {
    @PrimaryKey String id;

    List<Struct> innerStructs;
  }

  /** A test class that holds a single inner Struct. */
  @Table(name = "outer_test_entity")
  static class OuterTestHoldingStructEntity {
    @PrimaryKey String id;

    Struct innerStruct;
  }

  /** A test class that holds a single inner simple type collection property. */
  @Table(name = "outer_test_entity_flat")
  static class OuterTestEntityFlat {
    @PrimaryKey String id;

    List<Integer> innerLengths;
  }

  /** A test class that holds a single simple property. */
  @Table(name = "outer_test_entity_flat_faulty")
  static class OuterTestEntityFlatFaulty {
    @PrimaryKey String id;

    Integer innerLengths;
  }

  /** A test entity that tests a single property without a value. */
  static class InnerTestEntity {
    @PrimaryKey String value;

    String missingColumnValue;
  }

  /** A test class to test Spring Data's constructor support. */
  static class SimpleConstructorTester {
    @PrimaryKey final String id;

    SimpleConstructorTester(String id) {
      this.id = id;
    }
  }

  /** A class with a list that doesn't have an explicit param type. */
  static class TestEntityWithListWithZeroTypeArgs {
    @PrimaryKey List zeroArgsListOfObjects;
  }

  /** A test classs that uses a complex constructor. */
  @Table(name = "outer_test_entity")
  static class OuterTestEntityWithConstructor {
    @PrimaryKey String id;

    List<InnerTestEntity> innerTestEntities;

    OuterTestEntityWithConstructor(String id, List<InnerTestEntity> innerTestEntities) {
      this.id = id;
      this.innerTestEntities = innerTestEntities;
    }
  }

  /** A test class with a partial constructor meant to test Spring Data's constructor support. */
  @Table(name = "custom_test_table")
  static class PartialConstructor {
    @PrimaryKey String id;

    @Column(name = "custom_col")
    String stringField;

    @Column(name = "")
    boolean booleanField;

    long longField;

    double doubleField;

    PartialConstructor(String id, String stringField, boolean booleanField) {
      this.id = id;
      this.stringField = stringField;
      this.booleanField = booleanField;
    }
  }

  /** A test class with Json field. */
  @Table(name = "json_test_table")
  static class TestEntityJson {
    @PrimaryKey String id;

    @Column(spannerType = TypeCode.JSON)
    Params params;

    TestEntityJson(String id, Params params) {
      this.id = id;
      this.params = params;
    }
  }

  /** A test class with Json Array field. */
  @Table(name = "jsonarray_test_table2")
  static class TestEntityJsonArray {
    @PrimaryKey String id;

    @Column(spannerType = TypeCode.JSON)
    List<Params> paramsList;

    TestEntityJsonArray(String id, List<Params> paramsList) {
      this.id = id;
      this.paramsList = paramsList;
    }
  }

  /** A test class with Json field. */
  @Table(name = "json_test_table")
  static class TestEntityInstantInJson {
    @PrimaryKey String id;

    @Column(spannerType = TypeCode.JSON)
    InstantParam params;

    TestEntityInstantInJson(String id, InstantParam params) {
      this.id = id;
      this.params = params;
    }
  }

  static class Params {
    String p1;

    String p2;

    Params(String p1, String p2) {
      this.p1 = p1;
      this.p2 = p2;
    }
  }

  static class InstantParam {
    Instant instant;

    InstantParam(Instant instant) {
      this.instant = instant;
    }
  }
}
