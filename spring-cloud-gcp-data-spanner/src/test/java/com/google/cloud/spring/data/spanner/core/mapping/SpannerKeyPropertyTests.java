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
import static org.mockito.Mockito.mock;

import com.google.cloud.spanner.Key;
import java.lang.annotation.Annotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.TypeInformation;

/** Tests for the Spanner custom key property. */
class SpannerKeyPropertyTests {

  private SpannerCompositeKeyProperty spannerKeyProperty;

  private SpannerPersistentEntity spannerPersistentEntity;

  @BeforeEach
  void setup() {
    this.spannerPersistentEntity = mock(SpannerPersistentEntity.class);
    this.spannerKeyProperty =
        new SpannerCompositeKeyProperty(
            this.spannerPersistentEntity, new SpannerPersistentProperty[] {});
  }

  @Test
  void nullSpannerPersistentEntityTest() {

    assertThatThrownBy(() -> new SpannerCompositeKeyProperty(null, new SpannerPersistentProperty[] {}))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A valid Cloud Spanner persistent entity is required.");
  }

  @Test
  void nullPropertiesTest() {

    assertThatThrownBy(() -> new SpannerCompositeKeyProperty(this.spannerPersistentEntity, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A valid array of primary key properties is required.");

  }

  @Test
  void getColumnNameTest() {
    assertThat(this.spannerKeyProperty.getColumnName()).isNull();
  }

  @Test
  void getColumnInnerTypeTest() {
    assertThat(this.spannerKeyProperty.getColumnInnerType()).isNull();
  }

  @Test
  void getPrimaryKeyOrderTest() {
    assertThat(this.spannerKeyProperty.getPrimaryKeyOrder()).isNull();
  }

  @Test
  void getOwnerTest() {
    assertThat(this.spannerKeyProperty.getOwner()).isSameAs(this.spannerPersistentEntity);
  }

  @Test
  void getNameTest() {
    assertThat(this.spannerKeyProperty.getName()).isNull();
  }

  @Test
  void getTypeTest() {
    assertThat(this.spannerKeyProperty.getType()).isEqualTo(Key.class);
  }

  @Test
  void getTypeInformationTest() {
    assertThat(this.spannerKeyProperty.getTypeInformation())
        .isEqualTo(TypeInformation.of(Key.class));
  }

  @Test
  void getPersistentEntityTypeInformationTest() {
    assertThat(this.spannerKeyProperty.getPersistentEntityTypeInformation().iterator().hasNext())
        .isFalse();
  }

  @Test
  void getGetterTest() {
    assertThat(this.spannerKeyProperty.getGetter()).isNull();
  }

  @Test
  void getSetterTest() {
    assertThat(this.spannerKeyProperty.getSetter()).isNull();
  }

  @Test
  void getFieldTest() {
    assertThat(this.spannerKeyProperty.getField()).isNull();
  }

  @Test
  void getSpelExpressionTest() {
    assertThat(this.spannerKeyProperty.getSpelExpression()).isNull();
  }

  @Test
  void getAssociationTest() {
    assertThat(this.spannerKeyProperty.getAssociation()).isNull();
  }

  @Test
  void isEntityTest() {
    assertThat(this.spannerKeyProperty.isEntity()).isFalse();
  }

  @Test
  void isIdPropertyTest() {
    assertThat(this.spannerKeyProperty.isIdProperty()).isTrue();
  }

  @Test
  void isVersionPropertyTest() {
    assertThat(this.spannerKeyProperty.isVersionProperty()).isFalse();
  }

  @Test
  void isCollectionLikeTest() {
    assertThat(this.spannerKeyProperty.isCollectionLike()).isFalse();
  }

  @Test
  void isMapTest() {
    assertThat(this.spannerKeyProperty.isMap()).isFalse();
  }

  @Test
  void isArrayTest() {
    assertThat(this.spannerKeyProperty.isArray()).isFalse();
  }

  @Test
  void isTransientTest() {
    assertThat(this.spannerKeyProperty.isTransient()).isFalse();
  }

  @Test
  void isWritableTest() {
    assertThat(this.spannerKeyProperty.isWritable()).isFalse();
  }

  @Test
  void isAssociationTest() {
    assertThat(this.spannerKeyProperty.isAssociation()).isFalse();
  }

  @Test
  void getComponentTypeTest() {
    assertThat(this.spannerKeyProperty.getComponentType()).isNull();
  }

  @Test
  void getRawTypeTest() {
    assertThat(this.spannerKeyProperty.getRawType()).isEqualTo(Key.class);
  }

  @Test
  void getMapValueTypeTest() {
    assertThat(this.spannerKeyProperty.getMapValueType()).isNull();
  }

  @Test
  void getActualTypeTest() {
    assertThat(this.spannerKeyProperty.getActualType()).isEqualTo(Key.class);
  }

  @Test
  void findAnnotationTest() {
    Annotation annotation = this.spannerKeyProperty.findAnnotation(null);
    assertThat(annotation).isNull();
  }

  @Test
  void findPropertyOrOwnerAnnotationTest() {
    Annotation annotation = this.spannerKeyProperty.findPropertyOrOwnerAnnotation(null);
    assertThat(annotation).isNull();
  }

  @Test
  void isAnnotationPresentTest() {
    assertThat(this.spannerKeyProperty.isAnnotationPresent(null)).isFalse();
  }

  @Test
  void usePropertyAccessTest() {
    assertThat(this.spannerKeyProperty.usePropertyAccess()).isFalse();
  }

  @Test
  void getAssociationTargetTypeInformationTest() {
    assertThat(this.spannerKeyProperty.getAssociationTargetTypeInformation()).isNull();
  }
}
