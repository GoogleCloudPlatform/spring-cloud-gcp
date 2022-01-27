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

package com.google.cloud.spring.data.datastore.core.convert;

import com.google.cloud.datastore.Value;
import com.google.cloud.spring.data.datastore.core.mapping.DatastorePersistentProperty;
import com.google.cloud.spring.data.datastore.core.mapping.EmbeddedType;
import java.util.Optional;
import org.springframework.data.util.TypeInformation;

/**
 * An interface for type conversions on read and on write.
 *
 * @since 1.1
 */
public interface ReadWriteConversions {

  /**
   * Converts a given object to an object of a target type.
   *
   * @param val the simple type or Iterable value to convert. Arrays are not supported.
   * @param targetCollectionType the type of the collection to be converted into. {@code null} if
   *     the property is a singular object.
   * @param targetComponentType the type of the property to convert. For collection-like properties
   *     this refers to the individual items' type.
   * @param <T> the type of the object that is produced by reading
   * @return an object of a target type.
   */
  <T> T convertOnRead(Object val, Class targetCollectionType, Class targetComponentType);

  /**
   * Converts a given object to an object of a target type that is possibly an embedded entity.
   *
   * @param val the simple type or Iterable value to convert. Arrays are not supported.
   * @param embeddedType contains the type of embedded entity conversion should produce.
   * @param targetTypeInformation type metadata information for the desired type.
   * @param <T> the type of the object that is produced by reading
   * @return an object of a target type.
   */
  <T> T convertOnRead(Object val, EmbeddedType embeddedType, TypeInformation targetTypeInformation);

  /**
   * Converts an object to a Cloud Datastore {@link Value}; supports collections.
   *
   * @param obj the objects to convert.
   * @param persistentProperty the source field information.
   * @return a Cloud Datastore value.
   */
  Value convertOnWrite(Object obj, DatastorePersistentProperty persistentProperty);

  /**
   * Converts an object to a Cloud Datastore {@link Value}, for non-collection objects.
   *
   * @param obj the object to convert.
   * @return a Cloud Datastore value.
   */
  Value convertOnWriteSingle(Object obj);

  /**
   * Get the Cloud Datastore-compatible native Java type that can be used to store the given type.
   *
   * @param inputType the given type to test.
   * @return the Cloud Datastore-compatible native Java type, if it exists.
   */
  Optional<Class<?>> getDatastoreCompatibleType(Class inputType);

  /**
   * Registers {@link DatastoreEntityConverter} to be used for embedded entities.
   *
   * @param datastoreEntityConverter the DatastoreEntityConverter.
   */
  void registerEntityConverter(DatastoreEntityConverter datastoreEntityConverter);
}
