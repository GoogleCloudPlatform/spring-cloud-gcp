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

package com.google.cloud.spring.autoconfigure.spanner;

import com.google.cloud.spanner.Key;
import com.google.cloud.spring.data.spanner.core.convert.SpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerDataException;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerPersistentEntity;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerPersistentProperty;
import java.io.Serializable;
import java.util.StringJoiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;

/** Settings for key converter used in REST repositories. */
public class SpannerKeyIdConverter implements BackendIdConverter {

  private SpannerMappingContext mappingContext;

  @Autowired SpannerEntityProcessor spannerEntityProcessor;

  public SpannerKeyIdConverter(SpannerMappingContext mappingContext) {
    this.mappingContext = mappingContext;
  }

  @Override
  public Serializable fromRequestId(String id, Class<?> entityType) {
    if (id == null) {
      return null;
    }

    SpannerPersistentEntity<?> persistentEntity =
        this.mappingContext.getPersistentEntity(entityType);

    if (persistentEntity != null && persistentEntity.hasMultiFieldKey()) {
      return fromRequestIdSingleKey(id, persistentEntity.getIdProperty());
    }
    return fromRequestIdCompoundKey(id);
  }

  private Serializable fromRequestIdCompoundKey(String id) {
    Object[] parts = id.split(getUrlIdSeparator());
    return Key.of(parts);
  }

  private Serializable fromRequestIdSingleKey(String id, SpannerPersistentProperty idProperty) {
    Object convertedId =
        this.spannerEntityProcessor.getReadConverter().convert(id, idProperty.getType());
    if (convertedId instanceof Serializable) {
      return (Serializable) convertedId;
    }
    throw new SpannerDataException("Property type for ID field has to be Serializable");
  }

  protected String getUrlIdSeparator() {
    return ",";
  }

  @Override
  public String toRequestId(Serializable source, Class<?> entityType) {
    Key id = (Key) source;
    StringJoiner stringJoiner = new StringJoiner(getUrlIdSeparator());
    id.getParts().forEach(p -> stringJoiner.add(p.toString()));
    return stringJoiner.toString();
  }

  @Override
  public boolean supports(Class<?> type) {
    SpannerPersistentEntity<?> persistentEntity = this.mappingContext.getPersistentEntity(type);

    return persistentEntity != null && persistentEntity.getIdProperty() != null;
  }
}
