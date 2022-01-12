/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.datastore;

import com.google.cloud.datastore.Key;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreDataException;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Optional;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;

/**
 * A key converter that parses Key JSON from REST requests.
 *
 * @since 1.2
 */
public class DatastoreKeyIdConverter implements BackendIdConverter {

  private final DatastoreMappingContext datastoreMappingContext;

  /**
   * Constructor.
   *
   * @param datastoreMappingContext the mapping context.
   */
  public DatastoreKeyIdConverter(DatastoreMappingContext datastoreMappingContext) {
    this.datastoreMappingContext = datastoreMappingContext;
  }

  @Override
  public Serializable fromRequestId(String s, Class<?> clazz) {
    try {
      return Key.fromUrlSafe(URLDecoder.decode(s, Charset.defaultCharset().name()));
    } catch (UnsupportedEncodingException e) {
      throw new DatastoreDataException("Could not decode URL key param: " + s);
    }
  }

  @Override
  public String toRequestId(Serializable serializable, Class<?> clazz) {
    return ((Key) serializable).toUrlSafe();
  }

  @Override
  public boolean supports(Class<?> entityType) {
    // This ID converter only covers the Datastore key type. Returning false here causes the
    // default converter from Spring Data to be used.
    return Optional.ofNullable(this.datastoreMappingContext.getPersistentEntity(entityType))
        .map(PersistentEntity::getIdProperty)
        .map(PersistentProperty::getType)
        .map(clz -> clz.equals(Key.class))
        .orElse(false);
  }
}
