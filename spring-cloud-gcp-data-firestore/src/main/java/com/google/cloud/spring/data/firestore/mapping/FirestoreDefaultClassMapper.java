/*
 * Copyright 2019-2019 the original author or authors.
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

package com.google.cloud.spring.data.firestore.mapping;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Internal;
import com.google.cloud.spring.core.util.MapBuilder;
import com.google.firestore.v1.Document;
import com.google.firestore.v1.Value;
import java.util.Map;
import java.util.Objects;

/**
 * Uses Firestore client library to provide object mapping functionality.
 *
 * @since 1.2.2
 */
public final class FirestoreDefaultClassMapper implements FirestoreClassMapper {

  private static final Internal INTERNAL =
      new Internal(FirestoreOptions.newBuilder().setProjectId("dummy-project-id").build(), null);

  private static final String VALUE_FIELD_NAME = "value";

  private static final String NOT_USED_PATH = "/not/used/path";

  private FirestoreMappingContext mappingContext;

  public FirestoreDefaultClassMapper(FirestoreMappingContext mappingContext) {
    this.mappingContext = mappingContext;
  }

  public <T> Value toFirestoreValue(T sourceValue) {
    DocumentSnapshot documentSnapshot =
        INTERNAL.snapshotFromMap(
            NOT_USED_PATH,
            new MapBuilder<String, Object>().put(VALUE_FIELD_NAME, sourceValue).build());
    return INTERNAL.protoFromSnapshot(documentSnapshot).get(VALUE_FIELD_NAME);
  }

  public <T> Document entityToDocument(T entity, String documentResourceName) {
    DocumentSnapshot documentSnapshot = INTERNAL.snapshotFromObject(NOT_USED_PATH, entity);
    return Document.newBuilder()
        .putAllFields(removeUpdateTimestamp(INTERNAL.protoFromSnapshot(documentSnapshot), entity))
        .setName(documentResourceName)
        .build();
  }

  public <T> T documentToEntity(Document document, Class<T> clazz) {
    DocumentSnapshot documentSnapshot = INTERNAL.snapshotFromProto(Timestamp.now(), document);
    T entity = documentSnapshot.toObject(clazz);
    return setUpdateTime(entity, documentSnapshot.getUpdateTime());
  }

  public <T> T setUpdateTime(T entity, Timestamp updateTime) {
    FirestorePersistentEntity<?> persistentEntity =
        this.mappingContext.getPersistentEntity(entity.getClass());
    FirestorePersistentProperty updateTimeProperty =
        Objects.requireNonNull(persistentEntity).getUpdateTimeProperty();

    if (updateTimeProperty != null) {
      persistentEntity.getPropertyAccessor(entity).setProperty(updateTimeProperty, updateTime);
    }

    return entity;
  }

  private Map<String, Value> removeUpdateTimestamp(Map<String, Value> valuesMap, Object entity) {
    FirestorePersistentEntity<?> persistentEntity =
        this.mappingContext.getPersistentEntity(entity.getClass());
    FirestorePersistentProperty updateTimeProperty =
        Objects.requireNonNull(persistentEntity).getUpdateTimeProperty();
    if (updateTimeProperty != null) {
      valuesMap.remove(updateTimeProperty.getFieldName());
    }
    return valuesMap;
  }
}
