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

import org.springframework.data.mapping.PersistentProperty;

/**
 * Persistent property for Google Cloud Datastore.
 *
 * @since 1.1
 */
public interface DatastorePersistentProperty
        extends PersistentProperty<DatastorePersistentProperty> {

  /**
   * Get the name of the field to store this property in Datastore.
   * @return the string name of the field.
   */
  String getFieldName();

  /**
   * Whether the property contains child entities via Ancestor relationship.
   * @return {@code true} if it contains child entities.
   */
  boolean isDescendants();

  /**
   * True if the property should be excluded from indexes.
   * @return true if unindexed.
   */
  boolean isUnindexed();

  /**
   * Get the {@link EmbeddedType} of the property.
   * @return the embedded type.
   */
  EmbeddedType getEmbeddedType();

  /**
   * True if the property is stored within Datastore entity.
   * @return true if column-backed.
   */
  boolean isColumnBacked();

  /**
   * Return whether this property is a lazily-fetched one.
   * @return {@code true} if lazily-fetched.
   */
  boolean isLazyLoaded();

  /**
   * Return whether to skip null value.
   * @return {@code true} if null is skipped.
   */
  default boolean isSkipNullValue() {
    return false;
  }

  // Auditing methods without @Override to avoid compilation issues in the interface
  boolean isCreatedByProperty();

  boolean isCreatedDateProperty();

  boolean isLastModifiedByProperty();

  boolean isLastModifiedDateProperty();
}