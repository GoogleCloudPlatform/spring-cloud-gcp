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

package com.google.cloud.spring.data.datastore.core.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.SimplePropertyHandler;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.expression.spel.SpelEvaluationException;

/** Tests for the Datastore Persistent Entity. */
class DatastorePersistentEntityImplTests {

  private final DatastoreMappingContext datastoreMappingContext = new DatastoreMappingContext();

  @Test
  void testTableName() {
    DatastorePersistentEntityImpl<TestEntity> entity =
        new DatastorePersistentEntityImpl<>(TypeInformation.of(TestEntity.class), null);
    assertThat(entity.kindName()).isEqualTo("custom_test_kind");
  }

  @Test
  void testRawTableName() {
    DatastorePersistentEntityImpl<EntityNoCustomName> entity =
        new DatastorePersistentEntityImpl<>(
            TypeInformation.of(EntityNoCustomName.class), null);

    assertThat(entity.kindName()).isEqualTo("entityNoCustomName");
  }

  @Test
  void testEmptyCustomTableName() {
    DatastorePersistentEntityImpl<EntityEmptyCustomName> entity =
        new DatastorePersistentEntityImpl<>(
            TypeInformation.of(EntityEmptyCustomName.class), null);

    assertThat(entity.kindName()).isEqualTo("entityEmptyCustomName");
  }

  @Test
  void testExpressionResolutionWithoutApplicationContext() {
    DatastorePersistentEntityImpl<EntityWithExpression> entity =
        new DatastorePersistentEntityImpl<>(
            TypeInformation.of(EntityWithExpression.class), null);

    assertThatThrownBy(entity::kindName)
            .isInstanceOf(SpelEvaluationException.class)
            .hasMessageContaining("Property or field 'kindPostfix' cannot be found on null");
  }

  @Test
  void testExpressionResolutionFromApplicationContext() {
    DatastorePersistentEntityImpl<EntityWithExpression> entity =
        new DatastorePersistentEntityImpl<>(
            TypeInformation.of(EntityWithExpression.class), null);

    ApplicationContext applicationContext = mock(ApplicationContext.class);
    when(applicationContext.getBean("kindPostfix")).thenReturn("something");
    when(applicationContext.containsBean("kindPostfix")).thenReturn(true);

    entity.setApplicationContext(applicationContext);
    assertThat(entity.kindName()).isEqualTo("kind_something");
  }

  @Test
  void testHasIdProperty() {
    assertThat(new DatastoreMappingContext().getPersistentEntity(TestEntity.class).hasIdProperty())
        .isTrue();
  }

  @Test
  void testHasNoIdProperty() {
    assertThat(
            new DatastoreMappingContext().getPersistentEntity(EntityWithNoId.class).hasIdProperty())
        .isFalse();
  }

  @Test
  void testGetIdPropertyOrFail() {

    DatastorePersistentEntity testEntity = new DatastoreMappingContext().getPersistentEntity(EntityWithNoId.class);

    assertThatThrownBy(testEntity::getIdPropertyOrFail)
            .isInstanceOf(DatastoreDataException.class)
            .hasMessage("An ID property was required but does not exist for the type: "
                    + "class com.google.cloud.spring.data.datastore.core.mapping."
                    + "DatastorePersistentEntityImplTests$EntityWithNoId");


  }

  @Test
  void testIgnoredProperty() {
    TestEntity t = new TestEntity();
    t.id = "a";
    t.something = "a";
    t.notMapped = "b";
    DatastorePersistentEntity p =
        new DatastoreMappingContext().getPersistentEntity(TestEntity.class);
    PersistentPropertyAccessor accessor = p.getPropertyAccessor(t);

    p.doWithProperties(
        (SimplePropertyHandler)
            property -> assertThat(accessor.getProperty(property)).isNotEqualTo("b"));
  }

  @Test
  void testDiscriminationMetadata() {
    DatastorePersistentEntity base =
        this.datastoreMappingContext.getPersistentEntity(TestEntity.class);
    DatastorePersistentEntity a1 =
        this.datastoreMappingContext.getPersistentEntity(SubA1TestEntity.class);
    DatastorePersistentEntity a2 =
        this.datastoreMappingContext.getPersistentEntity(SubA2TestEntity.class);

    assertThat(base.kindName()).isEqualTo("custom_test_kind");
    assertThat(a1.kindName()).isEqualTo("custom_test_kind");
    assertThat(a2.kindName()).isEqualTo("custom_test_kind");

    assertThat(base.getDiscriminationFieldName()).isEqualTo("type_disc_col");
    assertThat(a1.getDiscriminationFieldName()).isEqualTo("type_disc_col");
    assertThat(a2.getDiscriminationFieldName()).isEqualTo("type_disc_col");

    assertThat(base.getDiscriminatorValue()).isNull();
    assertThat(a1.getDiscriminatorValue()).isEqualTo("A1");
    assertThat(a2.getDiscriminatorValue()).isEqualTo("A2");

    assertThat(this.datastoreMappingContext.getDiscriminationFamily(TestEntity.class))
        .containsExactlyInAnyOrder(SubA1TestEntity.class, SubA2TestEntity.class);
    assertThat(this.datastoreMappingContext.getDiscriminationFamily(SubA1TestEntity.class))
        .containsExactlyInAnyOrder(SubA2TestEntity.class);
    assertThat(this.datastoreMappingContext.getDiscriminationFamily(SubA2TestEntity.class))
        .isEmpty();

    assertThat(this.datastoreMappingContext.getDiscriminationFamily(SubA1TestEntity.class))
        .isNotEqualTo(this.datastoreMappingContext.getDiscriminationFamily(DiscrimEntityA.class));
  }

  @Test
  void testConflictingDiscriminationFieldNames() {

    assertThatThrownBy(() -> this.datastoreMappingContext.getPersistentEntity(DiscrimEntityB.class))
            .isInstanceOf(DatastoreDataException.class)
            .hasMessageContaining("This class and its super class both have "
                    + "discrimination fields but they are different fields: ");

  }

  @Test
  void testEntityMissingDiscriminationSuperclass() {

    DatastorePersistentEntity dpe = this.datastoreMappingContext.getPersistentEntity(TestEntityNoSuperclass.class);

    assertThatThrownBy(() -> dpe.kindName())
            .isInstanceOf(DatastoreDataException.class)
            .hasMessageContaining("This class expects a discrimination field but none are designated");
  }

  @Entity
  @DiscriminatorField(field = "colA")
  @DiscriminatorValue("a")
  private static class DiscrimEntityA {}

  @Entity
  @DiscriminatorField(field = "colA")
  @DiscriminatorValue("c")
  private static class DiscrimEntityC extends DiscrimEntityA {}

  @Entity
  @DiscriminatorField(field = "colB")
  @DiscriminatorValue("b")
  private static class DiscrimEntityB extends DiscrimEntityA {}

  @Entity(name = "custom_test_kind")
  @DiscriminatorField(field = "type_disc_col")
  private static class TestEntity {
    @Id String id;

    @Field(name = "custom_col")
    String something;

    @Transient String notMapped;
  }

  @Entity
  @DiscriminatorValue("A1")
  private static class SubA1TestEntity extends TestEntity {

    @Field(name = "type_disc_col")
    String discValue;
  }

  @Entity
  @DiscriminatorValue("A2")
  private static class SubA2TestEntity extends SubA1TestEntity {}

  @Entity
  @DiscriminatorValue("N/A")
  private static class TestEntityNoSuperclass {
    @Id String id;
  }

  private static class EntityNoCustomName {
    @Id String id;

    String something;
  }

  @Entity
  private static class EntityEmptyCustomName {
    @Id String id;

    String something;
  }

  @Entity(name = "#{'kind_'.concat(kindPostfix)}")
  private static class EntityWithExpression {
    @Id String id;

    String something;
  }

  private static class EntityWithNoId {
    String id;
  }
}
