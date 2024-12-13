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

package com.google.cloud.spring.data.datastore.repository.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;

/** Tests for the Datastore Repository factory. */
class DatastoreRepositoryFactoryTests {

  private DatastoreRepositoryFactory datastoreRepositoryFactory;

  private DatastoreTemplate datastoreTemplate;

  @BeforeEach
  void setUp() {
    DatastoreMappingContext datastoreMappingContext = new DatastoreMappingContext();
    this.datastoreTemplate = mock(DatastoreTemplate.class);
    this.datastoreRepositoryFactory =
        new DatastoreRepositoryFactory(datastoreMappingContext, this.datastoreTemplate);
  }

  @Test
  void getEntityInformationTest() {
    EntityInformation<TestEntity, String> entityInformation =
        this.datastoreRepositoryFactory.getEntityInformation(TestEntity.class);
    assertThat(entityInformation.getJavaType()).isEqualTo(TestEntity.class);
    assertThat(entityInformation.getIdType()).isEqualTo(String.class);

    TestEntity t = new TestEntity();
    t.id = "a";
    assertThat(entityInformation.getId(t)).isEqualTo("a");
  }

  @Test
  void getEntityInformationNotAvailableTest() {

    DatastoreRepositoryFactory factory =
        new DatastoreRepositoryFactory(mock(DatastoreMappingContext.class), this.datastoreTemplate);

    assertThatThrownBy(() -> factory.getEntityInformation(TestEntity.class))
            .isInstanceOf(MappingException.class)
            .hasMessage("Could not lookup mapping metadata for domain class: com.google.cloud.spring.data.datastore.repository.support."
                    + "DatastoreRepositoryFactoryTests$TestEntity");
  }

  @Test
  void getTargetRepositoryTest() {
    RepositoryInformation repoInfo = mock(RepositoryInformation.class);
    Mockito.<Class<?>>when(repoInfo.getRepositoryBaseClass())
        .thenReturn(SimpleDatastoreRepository.class);
    Mockito.<Class<?>>when(repoInfo.getDomainType()).thenReturn(TestEntity.class);
    Object repo = this.datastoreRepositoryFactory.getTargetRepository(repoInfo);
    assertThat(repo.getClass()).isEqualTo(SimpleDatastoreRepository.class);
  }

  @Test
  void getRepositoryBaseClassTest() {
    Class baseClass = this.datastoreRepositoryFactory.getRepositoryBaseClass(null);
    assertThat(baseClass).isEqualTo(SimpleDatastoreRepository.class);
  }

  @Entity(name = "custom_test_kind")
  private static class TestEntity {
    @Id String id;

    @Field(name = "custom_col")
    String something;
  }
}
