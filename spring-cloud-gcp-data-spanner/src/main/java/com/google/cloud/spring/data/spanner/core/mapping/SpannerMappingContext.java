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

package com.google.cloud.spring.data.spanner.core.mapping;

import com.google.cloud.spring.data.spanner.core.convert.ConverterAwareMappingSpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.convert.SpannerEntityProcessor;
import com.google.gson.Gson;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.PropertyNameFieldNamingStrategy;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * A mapping context for Cloud Spanner that provides ways to create persistent entities and
 * properties.
 *
 * @since 1.1
 */
public class SpannerMappingContext
    extends AbstractMappingContext<SpannerPersistentEntity<?>, SpannerPersistentProperty>
    implements ApplicationContextAware {

  private static final FieldNamingStrategy DEFAULT_NAMING_STRATEGY =
      PropertyNameFieldNamingStrategy.INSTANCE;

  private FieldNamingStrategy fieldNamingStrategy = DEFAULT_NAMING_STRATEGY;

  private ApplicationContext applicationContext;

  private Gson gson;

  public SpannerMappingContext() {
  }

  public SpannerMappingContext(Gson gson) {
    Assert.notNull(gson, "A non-null gson is required.");
    this.gson = gson;
  }

  @NonNull
  public Gson getGson() {
    return gson;
  }

  /**
   * Set the field naming strategy used when creating persistent properties.
   *
   * @param fieldNamingStrategy the field naming strategy passed used by created persistent
   *     properties get column names.
   */
  public void setFieldNamingStrategy(FieldNamingStrategy fieldNamingStrategy) {
    this.fieldNamingStrategy =
        (fieldNamingStrategy != null) ? fieldNamingStrategy : DEFAULT_NAMING_STRATEGY;
  }

  /**
   * Gets the field naming strategy used by this mapping context.
   *
   * @return the field naming strategy.
   */
  public FieldNamingStrategy getFieldNamingStrategy() {
    return this.fieldNamingStrategy;
  }

  @Override
  protected <T> SpannerPersistentEntity<T> createPersistentEntity(
      TypeInformation<T> typeInformation) {
    SpannerPersistentEntityImpl<T> persistentEntity = constructPersistentEntity(typeInformation);
    if (this.applicationContext != null) {
      persistentEntity.setApplicationContext(this.applicationContext);
    }
    return persistentEntity;
  }

  protected <T> SpannerPersistentEntityImpl<T> constructPersistentEntity(
      TypeInformation<T> typeInformation) {
    SpannerEntityProcessor processor;
    if (this.applicationContext == null || !this.applicationContext.containsBean("spannerConverter")) {
      processor = new ConverterAwareMappingSpannerEntityProcessor(this);
    } else {
      processor = this.applicationContext.getBean(SpannerEntityProcessor.class);
    }
    return new SpannerPersistentEntityImpl<>(typeInformation, this, processor);
  }

  @Override
  protected SpannerPersistentProperty createPersistentProperty(
      Property property, SpannerPersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
    return new SpannerPersistentPropertyImpl(
        property, owner, simpleTypeHolder, this.fieldNamingStrategy);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public SpannerPersistentEntity<?> getPersistentEntityOrFail(Class<?> entityClass) {
    SpannerPersistentEntity<?> entity = super.getPersistentEntity(entityClass);
    if (entity == null) {
      throw new SpannerDataException(
          "The provided entity class cannot be converted to a Spanner Entity: " + entityClass);
    }
    return entity;
  }
}
