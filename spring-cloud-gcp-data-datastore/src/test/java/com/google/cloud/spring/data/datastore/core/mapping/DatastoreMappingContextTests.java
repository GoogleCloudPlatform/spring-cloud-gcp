/*
 * Copyright 2017-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.cloud.Timestamp;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.util.TypeInformation;

/**
 * Tests for the {@link DatastoreMappingContext}.
 */
class DatastoreMappingContextTests {

  @Test
  void testCreatedDatePropertyRecognition() {
    DatastoreMappingContext context = new DatastoreMappingContext();

    // Analyze the entity and retrieve its metadata
    DatastorePersistentEntity<?> entity = context.getPersistentEntity(EntityWithAudit.class);

    // Identify the property annotated with @CreatedDate
    DatastorePersistentProperty property = entity.getPersistentProperty(CreatedDate.class);

    // Verify that the mapping context correctly identifies the auditing property
    // This is expected to fail until the feature is implemented
    assertThat(property).as("The @CreatedDate property should be recognized").isNotNull();
    assertThat(property.isCreatedDateProperty())
            .as("The property should be identified as a created date property")
            .isTrue();
  }

  @Test
  void testApplicationContextPassing() {
    DatastorePersistentEntityImpl<?> mockEntity = mock(DatastorePersistentEntityImpl.class);
    DatastoreMappingContext context = createDatastoreMappingContextWith(mockEntity);
    ApplicationContext applicationContext = mock(ApplicationContext.class);
    context.setApplicationContext(applicationContext);

    context.createPersistentEntity(TypeInformation.of(Object.class));

    verify(mockEntity, times(1)).setApplicationContext(applicationContext);
  }

  @Test
  void testApplicationContextIsNotSet() {
    DatastorePersistentEntityImpl<?> mockEntity = mock(DatastorePersistentEntityImpl.class);
    DatastoreMappingContext context = createDatastoreMappingContextWith(mockEntity);

    context.createPersistentEntity(TypeInformation.of(Object.class));

    verifyNoMoreInteractions(mockEntity);
  }

  @Test
  void testGetInvalidEntity() {
    DatastoreMappingContext context = new DatastoreMappingContext();

    assertThatThrownBy(() -> context.getDatastorePersistentEntity(Integer.class))
            .isInstanceOf(DatastoreDataException.class)
            .hasMessage("Unable to find a DatastorePersistentEntity for: class java.lang.Integer");
  }

  @Test
  void testTimestampNotAnEntity() {
    // Datastore native types like Timestamp should be considered simple type and not an entity
    DatastoreMappingContext context = new DatastoreMappingContext();
    assertThatThrownBy(() -> context.getDatastorePersistentEntity(Timestamp.class))
            .isInstanceOf(DatastoreDataException.class)
            .hasMessage(
                    "Unable to find a DatastorePersistentEntity for: class com.google.cloud.Timestamp");
  }

  private DatastoreMappingContext createDatastoreMappingContextWith(
          DatastorePersistentEntityImpl<?> mockEntity) {
    return new DatastoreMappingContext() {
      @Override
      @SuppressWarnings("unchecked")
      protected <T> DatastorePersistentEntityImpl<T> constructPersistentEntity(
              TypeInformation<T> typeInformation) {
        return (DatastorePersistentEntityImpl<T>) mockEntity;
      }
    };
  }

  /**
   * Mock entity used to verify auditing annotation support.
   */
  static class EntityWithAudit {
    @CreatedDate
    LocalDateTime createdAt;
  }
}