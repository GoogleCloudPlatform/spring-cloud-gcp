/*
 * Copyright 2017-2018 the original author or authors.
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

package com.google.cloud.spring.data.spanner.core.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.Key;
import com.google.cloud.spring.data.spanner.core.convert.ConverterAwareMappingSpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.convert.SpannerEntityProcessor;
import com.google.spanner.v1.TypeCode;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.SimplePropertyHandler;
import org.springframework.data.util.TypeInformation;

/** Tests for the Spanner persistent entity. */
class SpannerPersistentEntityImplTests {

  private final SpannerMappingContext spannerMappingContext;

  private final SpannerEntityProcessor spannerEntityProcessor;

  SpannerPersistentEntityImplTests() {
    this.spannerMappingContext = new SpannerMappingContext();
    this.spannerEntityProcessor = new ConverterAwareMappingSpannerEntityProcessor(
        this.spannerMappingContext);
  }

  @Test
  void testTableName() {
    SpannerPersistentEntityImpl<TestEntity> entity =
        new SpannerPersistentEntityImpl<>(TypeInformation.of(TestEntity.class),
            this.spannerMappingContext, this.spannerEntityProcessor);

    assertThat(entity.tableName()).isEqualTo("custom_test_table");
  }

  @Test
  void testRawTableName() {
    SpannerPersistentEntityImpl<EntityNoCustomName> entity =
        new SpannerPersistentEntityImpl<>(TypeInformation.of(EntityNoCustomName.class),
            this.spannerMappingContext, this.spannerEntityProcessor);

    assertThat(entity.tableName()).isEqualTo("entityNoCustomName");
  }

  @Test
  void testEmptyCustomTableName() {
    SpannerPersistentEntityImpl<EntityEmptyCustomName> entity =
        new SpannerPersistentEntityImpl<>(TypeInformation.of(EntityEmptyCustomName.class),
            this.spannerMappingContext, this.spannerEntityProcessor);

    assertThat(entity.tableName()).isEqualTo("entityEmptyCustomName");
  }

  @Test
  void testColumns() {
    assertThat(new SpannerMappingContext().getPersistentEntity(TestEntity.class).columns())
        .containsExactlyInAnyOrder("id", "custom_col");
  }

  @Test
  void testExpressionResolutionWithoutApplicationContext() {

    SpannerPersistentEntityImpl<EntityWithExpression> entity =
        new SpannerPersistentEntityImpl<>(TypeInformation.of(EntityWithExpression.class),
            this.spannerMappingContext, this.spannerEntityProcessor);
    assertThatThrownBy(entity::tableName)
        .isInstanceOf(SpannerDataException.class)
        .hasMessage("Error getting table name for EntityWithExpression")
        .hasStackTraceContaining("EL1007E: Property or field 'tablePostfix' cannot be found on null");
  }

  @Test
  void testExpressionResolutionFromApplicationContext() {
    SpannerPersistentEntityImpl<EntityWithExpression> entity =
        new SpannerPersistentEntityImpl<>(TypeInformation.of(EntityWithExpression.class),
            this.spannerMappingContext, this.spannerEntityProcessor);

    ApplicationContext applicationContext = mock(ApplicationContext.class);
    when(applicationContext.getBean("tablePostfix")).thenReturn("something");
    when(applicationContext.containsBean("tablePostfix")).thenReturn(true);

    entity.setApplicationContext(applicationContext);
    assertThat(entity.tableName()).isEqualTo("table_something");
  }

  @Test
  void testDuplicatePrimaryKeyOrder() {

    SpannerMappingContext spannerMappingContext = new SpannerMappingContext();
    assertThatThrownBy(() -> spannerMappingContext.getPersistentEntity(EntityWithDuplicatePrimaryKeyOrder.class))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("Two properties were annotated with the same primary key order: id2 and id in EntityWithDuplicatePrimaryKeyOrder.");
  }

  @Test
  void testInvalidPrimaryKeyOrder() {

    SpannerMappingContext spannerMappingContext = new SpannerMappingContext();

    assertThatThrownBy(() -> spannerMappingContext.getPersistentEntity(EntityWithWronglyOrderedKeys.class))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("The primary key columns were not given a consecutive order. There is no property annotated with order 2 in EntityWithWronglyOrderedKeys.");

  }

  @Test
  void testNoIdEntity() {
    assertThat(
            new SpannerMappingContext().getPersistentEntity(EntityWithNoId.class).getIdProperty())
        .isNotNull();
  }

  @Test
  void testGetIdProperty() {
    assertThat(new SpannerMappingContext().getPersistentEntity(TestEntity.class).getIdProperty())
        .isInstanceOf(SpannerCompositeKeyProperty.class);
  }

  @Test
  void testHasIdProperty() {
    assertThat(new SpannerMappingContext().getPersistentEntity(TestEntity.class).hasIdProperty())
        .isTrue();
  }

  @Test
  void testSetIdProperty() {
    SpannerPersistentEntity entity =
        new SpannerMappingContext().getPersistentEntity(MultiIdsEntity.class);

    PersistentProperty idProperty = entity.getIdProperty();

    MultiIdsEntity t = new MultiIdsEntity();
    entity.getPropertyAccessor(t).setProperty(idProperty, Key.of("blah", 123L, 123.45D));

    assertThat(t.id).isEqualTo("blah");
    assertThat(t.id2).isEqualTo(123L);
    assertThat(t.id3).isEqualTo(123.45D);
  }

  @Test
  void testSetIdPropertyLongerKey() {

    SpannerPersistentEntity entity =
        new SpannerMappingContext().getPersistentEntity(MultiIdsEntity.class);

    PersistentProperty idProperty = entity.getIdProperty();

    MultiIdsEntity t = new MultiIdsEntity();
    PersistentPropertyAccessor propertyAccessor = entity.getPropertyAccessor(t);

    Key testKey = Key.of("blah", 123L, 123.45D, "abc");

    assertThatThrownBy(() -> propertyAccessor.setProperty(idProperty, testKey))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("The number of key parts is not equal to the number of primary key properties");
  }

  @Test
  void testSetIdPropertyNullKey() {

    SpannerPersistentEntity entity =
        new SpannerMappingContext().getPersistentEntity(MultiIdsEntity.class);

    PersistentProperty idProperty = entity.getIdProperty();

    MultiIdsEntity t = new MultiIdsEntity();
    PersistentPropertyAccessor propertyAccessor = entity.getPropertyAccessor(t);

    assertThatThrownBy(() -> propertyAccessor.setProperty(idProperty, null))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("The number of key parts is not equal to the number of primary key properties");

  }

  @Test
  void testIgnoredProperty() {
    TestEntity t = new TestEntity();
    t.id = "a";
    t.something = "a";
    t.notMapped = "b";
    SpannerPersistentEntity p = new SpannerMappingContext().getPersistentEntity(TestEntity.class);
    PersistentPropertyAccessor accessor = p.getPropertyAccessor(t);
    p.doWithProperties(
        (SimplePropertyHandler)
            property -> assertThat(accessor.getProperty(property)).isNotEqualTo("b"));
  }

  @Test
  void testInvalidTableName() {

    SpannerPersistentEntityImpl<EntityBadName> entity =
        new SpannerPersistentEntityImpl<>(TypeInformation.of(EntityBadName.class),
            this.spannerMappingContext, this.spannerEntityProcessor);

    assertThatThrownBy(entity::tableName)
        .isInstanceOf(SpannerDataException.class)
        .hasMessage("Error getting table name for EntityBadName")
        .hasStackTraceContaining(
            "Only letters, numbers, and underscores are allowed in table names: ;DROP TABLE your_table;");
  }

  @Test
  void testSpelInvalidName() {

    SpannerPersistentEntityImpl<EntityWithExpression> entity =
        new SpannerPersistentEntityImpl<>(TypeInformation.of(EntityWithExpression.class),
            this.spannerMappingContext, this.spannerEntityProcessor);

    ApplicationContext applicationContext = mock(ApplicationContext.class);
    when(applicationContext.getBean("tablePostfix")).thenReturn("; DROP TABLE your_table;");
    when(applicationContext.containsBean("tablePostfix")).thenReturn(true);

    entity.setApplicationContext(applicationContext);

    assertThatThrownBy(entity::tableName)
        .isInstanceOf(SpannerDataException.class)
        .hasMessage("Error getting table name for EntityWithExpression")
        .hasStackTraceContaining(
            "Only letters, numbers, and underscores are allowed in table names: "
                + "table_; DROP TABLE your_table;");


  }

  @Test
  void testDuplicateEmbeddedColumnName() {


    assertThatThrownBy(() ->   this.spannerMappingContext.getPersistentEntity(EmbeddedParentDuplicateColumn.class))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("Two properties resolve to the same column name: other in EmbeddedParentDuplicateColumn");

  }

  @Test
  void testEmbeddedParentKeys() {
    GrandParentEmbedded grandParentEmbedded = new GrandParentEmbedded();
    grandParentEmbedded.id = "1";

    ParentEmbedded parentEmbedded = new ParentEmbedded();
    parentEmbedded.grandParentEmbedded = grandParentEmbedded;
    parentEmbedded.id2 = 2;
    parentEmbedded.id3 = 3L;

    ChildEmbedded childEmbedded = new ChildEmbedded();
    childEmbedded.parentEmbedded = parentEmbedded;
    childEmbedded.id4 = "4";

    // intentionally null, which is a supported key component type.
    childEmbedded.id5 = null;

    Key key =
        (Key)
            this.spannerMappingContext
                .getPersistentEntity(ChildEmbedded.class)
                .getIdentifierAccessor(childEmbedded)
                .getIdentifier();

    assertThat(key)
        .isEqualTo(
            Key.newBuilder()
                .append("1")
                .append("2")
                .append("3")
                .append("4")
                .appendObject(null)
                .build());
  }

  @Test
  void testEmbeddedCollection() {

    assertThatThrownBy(() -> this.spannerMappingContext.getPersistentEntity(ChildCollectionEmbedded.class))
            .isInstanceOf(SpannerDataException.class)
            .hasMessageContaining("Embedded properties cannot be collections: ");
  }

  @Test
  void testExcludeEmbeddedColumnNames() {
    assertThat(this.spannerMappingContext.getPersistentEntity(ChildEmbedded.class).columns())
        .containsExactlyInAnyOrder("id", "id2", "id3", "id4", "id5");
  }

  @Test
  void doWithChildrenCollectionsTest() {
    PropertyHandler<SpannerPersistentProperty> mockHandler = mock(PropertyHandler.class);
    SpannerPersistentEntity spannerPersistentEntity =
        this.spannerMappingContext.getPersistentEntity(ParentInRelationship.class);
    doAnswer(
            invocation -> {
              String colName = ((SpannerPersistentProperty) invocation.getArgument(0)).getName();
              assertThat(colName.equals("childrenA") || colName.equals("childrenB")).isTrue();
              return null;
            })
        .when(mockHandler)
        .doWithPersistentProperty(any());
    spannerPersistentEntity.doWithInterleavedProperties(mockHandler);
    verify(mockHandler, times(2)).doWithPersistentProperty(any());
  }

  @Test
  void testParentChildPkNamesMismatch() {

    assertThatThrownBy(() ->   this.spannerMappingContext.getPersistentEntity(ParentInRelationshipMismatchedKeyName.class))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("The child primary key column (ChildBinRelationship.id) at position 1 does not match that "
                    + "of its parent (ParentInRelationshipMismatchedKeyName.idNameDifferentThanChildren).");
  }

  @Test
  void testGetJsonPropertyName() {
    SpannerPersistentEntityImpl<EntityWithJsonField> entityWithJsonField =
        (SpannerPersistentEntityImpl<EntityWithJsonField>)
            this.spannerMappingContext.getPersistentEntity(EntityWithJsonField.class);

    assertThat(entityWithJsonField.isJsonProperty(JsonEntity.class)).isTrue();
    assertThat(entityWithJsonField.isJsonProperty(String.class)).isFalse();

    SpannerPersistentEntityImpl<TestEntity> entityWithNoJsonField =
        (SpannerPersistentEntityImpl<TestEntity>)
            this.spannerMappingContext.getPersistentEntity(TestEntity.class);

    assertThat(entityWithNoJsonField.isJsonProperty(String.class)).isFalse();
    assertThat(entityWithNoJsonField.isJsonProperty(long.class)).isFalse();

    SpannerPersistentEntityImpl<EntityWithArrayJsonField> entityWithArrayJsonField =
        (SpannerPersistentEntityImpl<EntityWithArrayJsonField>)
            this.spannerMappingContext.getPersistentEntity(EntityWithArrayJsonField.class);
    assertThat(entityWithArrayJsonField.isJsonProperty(JsonEntity.class)).isTrue();
    assertThat(entityWithArrayJsonField.isJsonProperty(String.class)).isFalse();
  }

  private static class ParentInRelationship {
    @PrimaryKey String id;

    @Interleaved List<ChildAinRelationship> childrenA;

    @Interleaved List<ChildBinRelationship> childrenB;
  }

  private static class ChildAinRelationship {
    @PrimaryKey String id;

    @PrimaryKey(keyOrder = 2)
    String id2;
  }

  private static class EmbeddedKeyComponents {
    @PrimaryKey String id;

    @PrimaryKey(keyOrder = 2)
    String id2;
  }

  private static class ChildBinRelationship {
    @Embedded @PrimaryKey EmbeddedKeyComponents embeddedKeyComponents;
  }

  private static class ParentInRelationshipMismatchedKeyName {
    @PrimaryKey String idNameDifferentThanChildren;

    @Interleaved List<ChildBinRelationship> childrenA;
  }

  private static class GrandParentEmbedded {
    @PrimaryKey String id;
  }

  private static class ParentEmbedded {
    @PrimaryKey @Embedded GrandParentEmbedded grandParentEmbedded;

    // This property requires conversion to be stored as a STRING column.
    @PrimaryKey(keyOrder = 2)
    @Column(name = "id2", spannerType = TypeCode.STRING)
    int id2;

    // This property will be stored as a STRING column even though Long is a natively supported
    // type.
    @PrimaryKey(keyOrder = 3)
    @Column(name = "id3", spannerType = TypeCode.STRING)
    Long id3;
  }

  private static class ChildEmbedded {
    @PrimaryKey @Embedded ParentEmbedded parentEmbedded;

    @PrimaryKey(keyOrder = 2)
    String id4;

    @PrimaryKey(keyOrder = 3)
    @Column(spannerType = TypeCode.STRING)
    Long id5;
  }

  private static class ChildCollectionEmbedded {
    @PrimaryKey @Embedded List<ParentEmbedded> parentEmbedded;

    @PrimaryKey(keyOrder = 2)
    String id4;
  }

  private static class EmbeddedParentDuplicateColumn {
    @PrimaryKey String id;

    String other;

    @Embedded EmbeddedChildDuplicateColumn embeddedChildDuplicateColumn;
  }

  private static class EmbeddedChildDuplicateColumn {
    @Column(name = "other")
    String stuff;
  }

  @Table(name = ";DROP TABLE your_table;")
  private static class EntityBadName {
    @PrimaryKey(keyOrder = 1)
    String id;

    String something;
  }

  @Table(name = "custom_test_table")
  private static class TestEntity {
    @PrimaryKey(keyOrder = 1)
    String id;

    @Column(name = "custom_col")
    String something;

    @NotMapped String notMapped;
  }

  private static class EntityNoCustomName {
    @PrimaryKey(keyOrder = 1)
    String id;

    String something;
  }

  @Table
  private static class EntityEmptyCustomName {
    @PrimaryKey(keyOrder = 1)
    String id;

    String something;
  }

  @Table(name = "#{'table_'.concat(tablePostfix)}")
  private static class EntityWithExpression {
    @PrimaryKey(keyOrder = 1)
    String id;

    String something;
  }

  private static class EntityWithDuplicatePrimaryKeyOrder {
    @PrimaryKey(keyOrder = 1)
    String id;

    @PrimaryKey(keyOrder = 1)
    String id2;
  }

  private static class EntityWithWronglyOrderedKeys {
    @PrimaryKey(keyOrder = 1)
    String id;

    @PrimaryKey(keyOrder = 3)
    String id2;
  }

  private static class EntityWithNoId {
    String id;
  }

  private static class MultiIdsEntity {
    @PrimaryKey(keyOrder = 1)
    String id;

    @PrimaryKey(keyOrder = 2)
    Long id2;

    @PrimaryKey(keyOrder = 3)
    Double id3;
  }

  private static class EntityWithJsonField {
    @PrimaryKey String id;

    @Column(spannerType = TypeCode.JSON)
    JsonEntity jsonField;
  }

  private static class EntityWithArrayJsonField {
    @PrimaryKey String id;

    @Column(spannerType = TypeCode.JSON)
    List<JsonEntity> jsonListField;
  }

  private static class JsonEntity {}
}
