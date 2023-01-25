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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.cloud.Timestamp;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.TypeInformation;

/** Tests for the `DatastoreMappingContext`. */
class DatastoreMappingContextTests {
  @Test
  void testApplicationContextPassing() {
    DatastorePersistentEntityImpl mockEntity = mock(DatastorePersistentEntityImpl.class);
    DatastoreMappingContext context = createDatastoreMappingContextWith(mockEntity);
    ApplicationContext applicationContext = mock(ApplicationContext.class);
    context.setApplicationContext(applicationContext);

    context.createPersistentEntity(TypeInformation.of(Object.class));

    verify(mockEntity, times(1)).setApplicationContext(applicationContext);
  }

  @Test
  void testApplicationContextIsNotSet() {
    DatastorePersistentEntityImpl mockEntity = mock(DatastorePersistentEntityImpl.class);
    DatastoreMappingContext context = createDatastoreMappingContextWith(mockEntity);

    context.createPersistentEntity(TypeInformation.of(Object.class));

    verifyNoMoreInteractions(mockEntity);
  }

  @Test
  void testGetInvalidEntity() {
    DatastorePersistentEntityImpl mockEntity = mock(DatastorePersistentEntityImpl.class);
    DatastoreMappingContext context = createDatastoreMappingContextWith(mockEntity);

    assertThatThrownBy(() -> context.getDatastorePersistentEntity(Integer.class))
        .isInstanceOf(DatastoreDataException.class)
        .hasMessage("Unable to find a DatastorePersistentEntity for: class java.lang.Integer");
  }

  @Test
  void testTimestampNotAnEntity() {
    // Datastore native types like Timestamp should be considered simple type and no an entity
    DatastoreMappingContext context = new DatastoreMappingContext();
    assertThatThrownBy(() -> context.getDatastorePersistentEntity(Timestamp.class))
        .isInstanceOf(DatastoreDataException.class)
        .hasMessage(
            "Unable to find a DatastorePersistentEntity for: class com.google.cloud.Timestamp");
  }

  private DatastoreMappingContext createDatastoreMappingContextWith(
      DatastorePersistentEntityImpl mockEntity) {
    return new DatastoreMappingContext() {
      @Override
      @SuppressWarnings("unchecked")
      protected DatastorePersistentEntityImpl constructPersistentEntity(
          TypeInformation typeInformation) {
        return mockEntity;
      }
    };
  }
}
