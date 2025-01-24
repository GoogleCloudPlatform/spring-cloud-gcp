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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests for the Spanner persistent property. */
@ExtendWith(SpringExtension.class)
class SpannerPersistentPropertyImplTests {

  @Test
  void testGetColumn() {
    assertThat(new SpannerMappingContext().getPersistentEntity(TestEntity.class).columns())
            .containsExactlyInAnyOrder("id", "custom_col", "other", "doubleList");
  }

  @Test
  void testNullColumnName() {

    SpannerMappingContext context = new SpannerMappingContext();
    FieldNamingStrategy namingStrat = mock(FieldNamingStrategy.class);
    when(namingStrat.getFieldName(any())).thenReturn(null);
    context.setFieldNamingStrategy(namingStrat);

    assertThatThrownBy(() -> context.getPersistentEntity(TestEntity.class))
            .hasMessageContaining("Invalid (null or empty) field name returned for "
                    + "property @com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey")
            .hasMessageContaining("keyOrder=1")
            .hasMessageContaining("value=1")
            .hasMessageContaining("java.lang.String com.google.cloud.spring.data.spanner.core.mapping."
                    + "SpannerPersistentPropertyImplTests$TestEntity.id by class "
                    + "org.springframework.data.mapping.model.FieldNamingStrategy$MockitoMock$");
  }


  @Test
  void testAssociations() {
    new SpannerMappingContext()
            .getPersistentEntity(TestEntity.class)
            .doWithProperties(
                    (PropertyHandler<SpannerPersistentProperty>)
                            prop -> {
                              assertThat(
                                      ((SpannerPersistentPropertyImpl) prop).createAssociation().getInverse())
                                      .isSameAs(prop);
                              assertThat(
                                      ((SpannerPersistentPropertyImpl) prop).createAssociation().getObverse())
                                      .isNull();
                            });
  }

  @Test
  void testColumnInnerType() {
    assertThat(
            new SpannerMappingContext()
                    .getPersistentEntity(TestEntity.class)
                    .getPersistentProperty("doubleList")
                    .getColumnInnerType())
            .isEqualTo(Double.class);
  }

  @Test
  void testNoPojoIdProperties() {
    new SpannerMappingContext()
            .getPersistentEntity(TestEntity.class)
            .doWithProperties(
                    (PropertyHandler<SpannerPersistentProperty>)
                            prop -> assertThat(prop.isIdProperty()).isFalse());
  }

  @Test
  void testIgnoredProperty() {
    new SpannerMappingContext()
            .getPersistentEntity(TestEntity.class)
            .doWithProperties(
                    (PropertyHandler<SpannerPersistentProperty>)
                            prop -> assertThat(prop.getColumnName()).isNotEqualTo("not_mapped"));
  }

  @Table(name = "custom_test_table")
  private static class TestEntity {
    @PrimaryKey(keyOrder = 1)
    String id;

    @Column(name = "custom_col")
    String something;

    @Column(name = "")
    String other;

    List<Double> doubleList;

    @NotMapped
    @Column(name = "not_mapped")
    String notMappedString;
  }
}
