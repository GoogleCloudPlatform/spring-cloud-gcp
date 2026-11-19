/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.data.datastore.repository.support;

import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.core.mapping.event.BeforeSaveEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mapping.PersistentEntity;

/**
 * Auditing event listener that listens for {@code BeforeSaveEvent}.
 *
 * @since 1.2
 */
public class DatastoreAuditingEventListener implements ApplicationListener<BeforeSaveEvent> {

  private final AuditingHandler handler;

  private final DatastoreMappingContext mappingContext;

  /**
   * Constructor.
   *
   * @param datastoreAuditingHandler the auditing handler to set auditing properties.
   * @param mappingContext the mapping context to check entity states.
   */
  public DatastoreAuditingEventListener(
      AuditingHandler datastoreAuditingHandler, DatastoreMappingContext mappingContext) {
    this.handler = datastoreAuditingHandler;
    this.mappingContext = mappingContext;
  }

  @Override
  public void onApplicationEvent(BeforeSaveEvent event) {
    Iterable<?> entities = event.getTargetEntities();

    if (entities != null) {
      entities.forEach(entity -> {
        PersistentEntity<?, ?> persistentEntity =
            this.mappingContext.getPersistentEntity(entity.getClass());

        if (persistentEntity != null && persistentEntity.isNew(entity)) {
          this.handler.markCreated(entity);
        } else {
          this.handler.markModified(entity);
        }
      });
    }
  }
}