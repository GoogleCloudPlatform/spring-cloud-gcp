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

package com.google.cloud.spring.data.spanner.core.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.ByteArray;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.Type;
import com.google.cloud.spring.data.spanner.core.convert.ConverterAwareMappingSpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.convert.SpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.Embedded;
import com.google.cloud.spring.data.spanner.core.mapping.Interleaved;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerPersistentProperty;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import com.google.spanner.v1.TypeCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.OptionalLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Tests for the Spanner schema utils. */
class SpannerSchemaUtilsTests {

  private SpannerSchemaUtils spannerSchemaUtils;

  private SpannerEntityProcessor spannerEntityProcessor;

  @BeforeEach
  void setUp() {
    SpannerMappingContext spannerMappingContext = new SpannerMappingContext();
    this.spannerSchemaUtils =
        new SpannerSchemaUtils(
            spannerMappingContext,
            new ConverterAwareMappingSpannerEntityProcessor(spannerMappingContext),
            true);
    this.spannerEntityProcessor =
        new ConverterAwareMappingSpannerEntityProcessor(spannerMappingContext);
  }

  @Test
  void getDropDdlTest() {
    assertThat(this.spannerSchemaUtils.getDropTableDdlString(TestEntity.class))
        .isEqualTo("DROP TABLE custom_test_table");
  }

  @Test
  void getCreateDdlTest() {
    String ddlResult =
        "CREATE TABLE custom_test_table ( id STRING(MAX) , id3 INT64 , id_2 STRING(MAX) , bytes2"
            + " BYTES(MAX) , custom_col FLOAT64 NOT NULL , other STRING(333) , primitiveDoubleField"
            + " FLOAT64 , bigDoubleField FLOAT64 , bigLongField INT64 , primitiveIntField INT64 ,"
            + " bigIntField INT64 , bytes BYTES(MAX) , bytesList ARRAY<BYTES(111)> , integerList"
            + " ARRAY<INT64> , doubles ARRAY<FLOAT64> , commitTimestamp TIMESTAMP OPTIONS"
            + " (allow_commit_timestamp=true) , bigDecimalField NUMERIC , bigDecimals"
            + " ARRAY<NUMERIC> , jsonCol JSON ) PRIMARY KEY ( id , id_2 , id3 )";

    assertThat(this.spannerSchemaUtils.getCreateTableDdlString(TestEntity.class))
        .isEqualTo(ddlResult);
  }

  @Test
  void createDdlString() {
    assertColumnDdl(String.class, null, "id", null, OptionalLong.empty(), "id STRING(MAX)");
  }

  @Test
  void createDdlStringCustomLength() {
    assertColumnDdl(String.class, null, "id", null, OptionalLong.of(333L), "id STRING(333)");
  }

  @Test
  void createDdlBytesMax() {
    assertColumnDdl(ByteArray.class, null, "bytes", null, OptionalLong.empty(), "bytes BYTES(MAX)");
  }

  @Test
  void createDdlBytesCustomLength() {
    assertColumnDdl(
        ByteArray.class, null, "bytes", null, OptionalLong.of(333L), "bytes BYTES(333)");
  }

  @Test
  void ddlForListOfByteArray() {
    assertColumnDdl(
        List.class,
        ByteArray.class,
        "bytesList",
        null,
        OptionalLong.of(111L),
        "bytesList ARRAY<BYTES(111)>");
  }

  @Test
  void ddlForDoubleArray() {
    assertColumnDdl(
        double[].class, null, "doubles", null, OptionalLong.of(111L), "doubles ARRAY<FLOAT64>");
  }

  @Test
  void ddlForNumericList() {
    assertColumnDdl(
        List.class,
        BigDecimal.class,
        "bigDecimals",
        null,
        OptionalLong.empty(),
        "bigDecimals ARRAY<NUMERIC>");
  }

  @Test
  void createDdlNumeric() {
    assertColumnDdl(
        BigDecimal.class, null, "bigDecimal", null, OptionalLong.empty(), "bigDecimal NUMERIC");
  }

  @Test
  void ddlForListOfListOfIntegers() {
    assertColumnDdl(
        List.class,
        Integer.class,
        "integerList",
        null,
        OptionalLong.empty(),
        "integerList ARRAY<INT64>");
  }

  @Test
  void ddlForListOfListOfDoubles() {
    assertColumnDdl(
        List.class,
        Double.class,
        "doubleList",
        null,
        OptionalLong.empty(),
        "doubleList ARRAY<FLOAT64>");
  }

  @Test
  void createDdlForJson() {
    assertColumnDdl(
        JsonColumn.class, null, "jsonCol", Type.Code.JSON, OptionalLong.empty(), "jsonCol JSON");
    assertColumnDdl(
        List.class, JsonColumn.class, "arrayJsonCol", Type.Code.JSON, OptionalLong.empty(),
        "arrayJsonCol ARRAY<JSON>");
  }

  private void assertColumnDdl(
      Class clazz,
      Class innerClazz,
      String name,
      Type.Code code,
      OptionalLong length,
      String expectedDdl) {
    SpannerPersistentProperty spannerPersistentProperty = mock(SpannerPersistentProperty.class);

    // @formatter:off
    Mockito.<Class>when(spannerPersistentProperty.getType()).thenReturn(clazz);
    Mockito.<Class>when(spannerPersistentProperty.getColumnInnerType()).thenReturn(innerClazz);
    // @formatter:on

    when(spannerPersistentProperty.getColumnName()).thenReturn(name);
    when(spannerPersistentProperty.getMaxColumnLength()).thenReturn(length);

    when(spannerPersistentProperty.getAnnotatedColumnItemType()).thenReturn(code);
    assertThat(
            this.spannerSchemaUtils.getColumnDdlString(
                spannerPersistentProperty, this.spannerEntityProcessor))
        .isEqualTo(expectedDdl);
  }

  @Test
  void getIdTest() {
    TestEntity t = new TestEntity();
    t.id = "aaa";
    t.embeddedColumns = new EmbeddedColumns();
    t.embeddedColumns.id2 = "2";
    t.id3 = 3L;

    Key expectedKey =
        Key.newBuilder().append(t.id).appendObject(t.embeddedColumns.id2).append(t.id3).build();

    assertThat(this.spannerSchemaUtils.getKey(t)).isEqualTo(expectedKey);
  }

  @Test
  void getCreateDdlHierarchyTest() {
    List<String> createStrings =
        this.spannerSchemaUtils.getCreateTableDdlStringsForInterleavedHierarchy(ParentEntity.class);
    assertThat(createStrings)
        .containsExactly(
            "CREATE TABLE parent_test_table ( id STRING(MAX) "
                + ", id_2 STRING(MAX) , bytes2 BYTES(MAX) , "
                + "custom_col STRING(MAX) , other STRING(MAX) ) PRIMARY KEY ( id , id_2 )",
            "CREATE TABLE child_test_table ( id STRING(MAX) "
                + ", id_2 STRING(MAX) , bytes2 BYTES(MAX) , "
                + "id3 STRING(MAX) ) PRIMARY KEY ( id , id_2 , id3 ), INTERLEAVE IN PARENT "
                + "parent_test_table ON DELETE CASCADE",
            "CREATE TABLE grand_child_test_table ( id STRING(MAX) , id_2 STRING(MAX) , "
                + "id3 STRING(MAX) , id4 STRING(MAX) ) PRIMARY KEY ( id , id_2 , id3 , id4 ), "
                + "INTERLEAVE IN PARENT child_test_table ON DELETE CASCADE");
  }

  @Test
  void getDropDdlHierarchyTest() {
    List<String> dropStrings =
        this.spannerSchemaUtils.getDropTableDdlStringsForInterleavedHierarchy(ParentEntity.class);
    assertThat(dropStrings)
        .containsExactly(
            "DROP TABLE grand_child_test_table",
            "DROP TABLE child_test_table",
            "DROP TABLE parent_test_table");
  }

  @Table(name = "custom_test_table")
  private static class TestEntity {
    @PrimaryKey(keyOrder = 1)
    String id;

    @PrimaryKey(keyOrder = 3)
    long id3;

    @PrimaryKey(keyOrder = 2)
    @Embedded
    EmbeddedColumns embeddedColumns;

    // Intentionally incompatible column type for testing.
    @Column(
        name = "custom_col",
        spannerTypeMaxLength = 123,
        spannerType = TypeCode.FLOAT64,
        nullable = false)
    String something;

    @Column(name = "", spannerTypeMaxLength = 333)
    String other;

    double primitiveDoubleField;

    Double bigDoubleField;

    Long bigLongField;

    int primitiveIntField;

    Integer bigIntField;

    ByteArray bytes;

    @Column(spannerTypeMaxLength = 111)
    List<ByteArray> bytesList;

    List<Integer> integerList;

    double[] doubles;

    // this is intentionally a double to test that it is forced to be TIMESTAMP on Spanner
    // anyway
    @Column(spannerCommitTimestamp = true)
    double commitTimestamp;

    BigDecimal bigDecimalField;

    List<BigDecimal> bigDecimals;

    @Column(spannerType = TypeCode.JSON)
    JsonColumn jsonCol;
  }

  private static class JsonColumn {
    String param1;
    String param2;
  }

  private static class EmbeddedColumns {
    @PrimaryKey
    @Column(name = "id_2")
    String id2;

    ByteArray bytes2;
  }

  @Table(name = "parent_test_table")
  private static class ParentEntity {

    @PrimaryKey(keyOrder = 1)
    String id;

    @PrimaryKey(keyOrder = 2)
    @Embedded
    EmbeddedColumns embeddedColumns;

    @Column(name = "custom_col")
    String something;

    @Column(name = "")
    String other;

    @Interleaved List<ChildEntity> childEntities;

    @Interleaved List<ChildEntity> childEntities2;
  }

  @Table(name = "child_test_table")
  private static class ChildEntity {
    @PrimaryKey(keyOrder = 1)
    String id;

    @PrimaryKey(keyOrder = 2)
    @Embedded
    EmbeddedColumns embeddedColumns;

    @PrimaryKey(keyOrder = 3)
    String id3;

    @Interleaved List<GrandChildEntity> childEntities;

    @Interleaved List<GrandChildEntity> childEntities2;
  }

  @Table(name = "grand_child_test_table")
  private static class GrandChildEntity {
    @PrimaryKey(keyOrder = 1)
    String id;

    @PrimaryKey(keyOrder = 2)
    String id_2;

    @PrimaryKey(keyOrder = 3)
    String id3;

    @PrimaryKey(keyOrder = 4)
    String id4;
  }
}
