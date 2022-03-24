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

package com.google.cloud.spring.data.spanner.repository.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.google.cloud.spanner.Key;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import com.google.cloud.spring.data.spanner.repository.query.SpannerQueryLookupStrategy;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;

/** Tests for the Spanner repository factory. */
class SpannerRepositoryFactoryTests {

  private SpannerRepositoryFactory spannerRepositoryFactory;

  private SpannerTemplate spannerTemplate;

  @BeforeEach
  void setUp() {
    SpannerMappingContext spannerMappingContext = new SpannerMappingContext();
    this.spannerTemplate = mock(SpannerTemplate.class);
    this.spannerRepositoryFactory =
        new SpannerRepositoryFactory(spannerMappingContext, this.spannerTemplate);
  }

  @Test
  void getEntityInformationTest() {
    EntityInformation<TestEntity, Key> entityInformation =
        this.spannerRepositoryFactory.getEntityInformation(TestEntity.class);
    assertThat(entityInformation.getJavaType()).isEqualTo(TestEntity.class);
    assertThat(entityInformation.getIdType()).isEqualTo(Key.class);

    TestEntity t = new TestEntity();
    t.id = "a";
    t.id2 = 3L;
    assertThat(entityInformation.getId(t))
        .isEqualTo(Key.newBuilder().append(t.id).append(t.id2).build());
  }

  @Test
  void getEntityInformationNotAvailableTest() {
    SpannerRepositoryFactory factory =
        new SpannerRepositoryFactory(mock(SpannerMappingContext.class), this.spannerTemplate);

    assertThatThrownBy(() -> factory.getEntityInformation(TestEntity.class))
            .isInstanceOf(MappingException.class)
            .hasMessage("Could not lookup mapping metadata for domain "
                    + "class com.google.cloud.spring.data.spanner.repository.support."
                    + "SpannerRepositoryFactoryTests$TestEntity!");
  }

  @Test
  void getTargetRepositoryTest() {
    RepositoryInformation repoInfo = mock(RepositoryInformation.class);
    // @formatter:off
    Mockito.<Class<?>>when(repoInfo.getRepositoryBaseClass())
        .thenReturn(SimpleSpannerRepository.class);
    Mockito.<Class<?>>when(repoInfo.getDomainType()).thenReturn(TestEntity.class);
    // @formatter:on
    Object repo = this.spannerRepositoryFactory.getTargetRepository(repoInfo);
    assertThat(repo).isInstanceOf(SimpleSpannerRepository.class);
  }

  @Test
  void getRepositoryBaseClassTest() {
    Class baseClass = this.spannerRepositoryFactory.getRepositoryBaseClass(null);
    assertThat(baseClass).isEqualTo(SimpleSpannerRepository.class);
  }

  @Test
  void getQueryLookupStrategyTest() {
    Optional<QueryLookupStrategy> qls =
        this.spannerRepositoryFactory.getQueryLookupStrategy(
            null, mock(QueryMethodEvaluationContextProvider.class));
    assertThat(qls.get()).isInstanceOf(SpannerQueryLookupStrategy.class);
  }

  @Table(name = "custom_test_table")
  private static class TestEntity {
    @PrimaryKey(keyOrder = 1)
    String id;

    @PrimaryKey(keyOrder = 2)
    long id2;

    @Column(name = "custom_col")
    String something;
  }
}
