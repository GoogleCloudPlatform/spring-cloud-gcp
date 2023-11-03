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
import static org.mockito.Mockito.mock;

import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/** Tests for the Datastore Repository factory bean. */
class DatastoreRepositoryFactoryBeanTests {

  private DatastoreRepositoryFactoryBean<DatastoreRepository<Object, String>, Object, String>
      datastoreRepositoryFactoryBean;

  private DatastoreMappingContext datastoreMappingContext = new DatastoreMappingContext();

  private DatastoreTemplate datastoreTemplate = mock(DatastoreTemplate.class);

  @BeforeEach
  void setUp() {
    this.datastoreRepositoryFactoryBean =
        new DatastoreRepositoryFactoryBean(DatastoreRepository.class);
    this.datastoreRepositoryFactoryBean.setDatastoreMappingContext(this.datastoreMappingContext);
    this.datastoreRepositoryFactoryBean.setDatastoreTemplate(this.datastoreTemplate);
  }

  @Test
  void createRepositoryFactoryTest() {
    RepositoryFactorySupport factory =
        this.datastoreRepositoryFactoryBean.createRepositoryFactory();
    assertThat(factory.getClass()).isEqualTo(DatastoreRepositoryFactory.class);
  }
}
