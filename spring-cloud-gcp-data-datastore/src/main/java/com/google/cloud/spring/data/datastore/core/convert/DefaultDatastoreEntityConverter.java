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

import static com.google.cloud.spring.data.datastore.core.mapping.EmbeddedType.NOT_EMBEDDED;

import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreDataException;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.core.mapping.DatastorePersistentEntity;
import com.google.cloud.spring.data.datastore.core.mapping.DatastorePersistentProperty;
import com.google.cloud.spring.data.datastore.core.mapping.EmbeddedType;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.model.EntityInstantiator;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.mapping.model.ParameterValueProvider;
import org.springframework.data.mapping.model.PersistentEntityParameterValueProvider;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * A class for object to entity and entity to object conversions.
 *
 * @since 1.1
 */
public class DefaultDatastoreEntityConverter implements DatastoreEntityConverter {
  private final DatastoreMappingContext mappingContext;

  private final EntityInstantiators instantiators = new EntityInstantiators();

  private final ReadWriteConversions conversions;

  public DefaultDatastoreEntityConverter(
      DatastoreMappingContext mappingContext, ObjectToKeyFactory objectToKeyFactory) {
    this(
        mappingContext,
        new TwoStepsConversions(
            new DatastoreCustomConversions(), objectToKeyFactory, mappingContext));
  }

  public DefaultDatastoreEntityConverter(
      DatastoreMappingContext mappingContext, ReadWriteConversions conversions) {
    this.mappingContext = mappingContext;
    this.conversions = conversions;

    conversions.registerEntityConverter(this);
  }

  @Override
  public ReadWriteConversions getConversions() {
    return this.conversions;
  }

  @Override
  public <T, R> Map<T, R> readAsMap(BaseEntity entity, TypeInformation mapTypeInformation) {
    Assert.notNull(mapTypeInformation, "mapTypeInformation can't be null");
    if (entity == null) {
      return null;
    }
    Map<T, R> result;
    if (!mapTypeInformation.getType().isInterface()) {
      try {
        result =
            (Map<T, R>)
                ((Constructor<?>) mapTypeInformation.getType().getConstructor()).newInstance();
      } catch (Exception e) {
        throw new DatastoreDataException(
            "Unable to create an instance of a custom map type: "
                + mapTypeInformation.getType()
                + " (make sure the class is public and has a public no-args constructor)",
            e);
      }
    } else {
      result = new HashMap<>();
    }

    EntityPropertyValueProvider propertyValueProvider =
        new EntityPropertyValueProvider(entity, this.conversions);
    Set<String> fieldNames = entity.getNames();
    for (String field : fieldNames) {
      result.put(
          this.conversions.convertOnRead(
              field, NOT_EMBEDDED, mapTypeInformation.getComponentType()),
          propertyValueProvider.getPropertyValue(
              field,
              EmbeddedType.of(mapTypeInformation.getMapValueType()),
              mapTypeInformation.getMapValueType()));
    }
    return result;
  }

  @Override
  public <T, R> Map<T, R> readAsMap(
      Class<T> keyType, TypeInformation<R> componentType, BaseEntity entity) {
    if (entity == null) {
      return null;
    }
    return readAsMap(entity, TypeInformation.of(HashMap.class));
  }

  public <T> DatastorePersistentEntity<T> getDiscriminationPersistentEntity(
      Class<T> entityClass, BaseEntity<?> entity) {
    DatastorePersistentEntity ostensiblePersistentEntity =
        this.mappingContext.getPersistentEntity(entityClass);

    if (ostensiblePersistentEntity == null) {
      throw new DatastoreDataException("Unable to convert Datastore Entity to " + entityClass);
    }

    EntityPropertyValueProvider propertyValueProvider =
        new EntityPropertyValueProvider(entity, this.conversions);

    return getDiscriminationPersistentEntity(ostensiblePersistentEntity, propertyValueProvider);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> R read(Class<R> clazz, BaseEntity entity) {
    if (entity == null) {
      return null;
    }
    DatastorePersistentEntity<R> ostensiblePersistentEntity =
        (DatastorePersistentEntity<R>) this.mappingContext.getPersistentEntity(clazz);

    if (ostensiblePersistentEntity == null) {
      throw new DatastoreDataException("Unable to convert Datastore Entity to " + clazz);
    }

    EntityPropertyValueProvider propertyValueProvider =
        new EntityPropertyValueProvider(entity, this.conversions);

    DatastorePersistentEntity<?> persistentEntity =
        getDiscriminationPersistentEntity(ostensiblePersistentEntity, propertyValueProvider);

    ParameterValueProvider<DatastorePersistentProperty> parameterValueProvider =
        new PersistentEntityParameterValueProvider<>(persistentEntity, propertyValueProvider, null);

    EntityInstantiator instantiator = this.instantiators.getInstantiatorFor(persistentEntity);
    Object instance;
    try {
      instance = instantiator.createInstance(persistentEntity, parameterValueProvider);
      PersistentPropertyAccessor accessor = persistentEntity.getPropertyAccessor(instance);

      persistentEntity.doWithColumnBackedProperties(
          datastorePersistentProperty -> {
            // if a property is a constructor argument, it was already computed on instantiation
            if (!persistentEntity.isCreatorArgument(datastorePersistentProperty)) {
              Object value = propertyValueProvider.getPropertyValue(datastorePersistentProperty);
              if (value != null) {
                accessor.setProperty(datastorePersistentProperty, value);
              }
            }
          });
    } catch (DatastoreDataException ex) {
      throw new DatastoreDataException(
          "Unable to read " + persistentEntity.getName() + " entity", ex);
    }

    return (R) instance;
  }

  private DatastorePersistentEntity getDiscriminationPersistentEntity(
      DatastorePersistentEntity ostensibleEntity,
      EntityPropertyValueProvider propertyValueProvider) {
    if (ostensibleEntity.getDiscriminationFieldName() == null) {
      return ostensibleEntity;
    }

    Set<Class> members =
        DatastoreMappingContext.getDiscriminationFamily(ostensibleEntity.getType());
    Optional<DatastorePersistentEntity> persistentEntity =
        members == null
            ? Optional.empty()
            : members.stream()
                .map(x -> (DatastorePersistentEntity) this.mappingContext.getPersistentEntity(x))
                .filter(x -> x != null && isDiscriminationFieldMatch(x, propertyValueProvider))
                .findFirst();

    return persistentEntity.orElse(ostensibleEntity);
  }

  private boolean isDiscriminationFieldMatch(
      DatastorePersistentEntity entity, EntityPropertyValueProvider propertyValueProvider) {
    return ((String[])
            propertyValueProvider.getPropertyValue(
                entity.getDiscriminationFieldName(),
                NOT_EMBEDDED,
                TypeInformation.of(String[].class)))
        [0].equals(entity.getDiscriminatorValue());
  }

  @Override
  @SuppressWarnings("unchecked")
  public void write(Object source, @NonNull BaseEntity.Builder sink) {
    DatastorePersistentEntity<?> persistentEntity =
        this.mappingContext.getDatastorePersistentEntity(source.getClass());

    String discriminationFieldName = persistentEntity.getDiscriminationFieldName();
    List<String> discriminationValues = persistentEntity.getCompatibleDiscriminationValues();
    if (!discriminationValues.isEmpty() || discriminationFieldName != null) {
      sink.set(
          discriminationFieldName,
          discriminationValues.stream().map(StringValue::of).toList());
    }
    PersistentPropertyAccessor accessor = persistentEntity.getPropertyAccessor(source);
    persistentEntity.doWithColumnBackedProperties(
        (DatastorePersistentProperty persistentProperty) -> {
          // Datastore doesn't store its Key as a regular field.
          if (persistentProperty.isIdProperty()) {
            return;
          }
          try {
            Object val = accessor.getProperty(persistentProperty);
            Value convertedVal = this.conversions.convertOnWrite(val, persistentProperty);

            if (persistentProperty.isUnindexed()) {
              convertedVal = setExcludeFromIndexes(convertedVal);
            }
            sink.set(persistentProperty.getFieldName(), convertedVal);
          } catch (DatastoreDataException ex) {
            throw new DatastoreDataException(
                "Unable to write "
                    + persistentEntity.kindName()
                    + "."
                    + persistentProperty.getFieldName(),
                ex);
          }
        });
  }

  private Value setExcludeFromIndexes(Value convertedVal) {
    // ListValues must have its contents individually excluded instead.
    // the entire list must NOT be excluded or there will be an exception.
    // Same for maps and embedded entities which are stored as EntityValue.
    if (convertedVal.getClass().equals(EntityValue.class)) {
      FullEntity.Builder<IncompleteKey> builder = FullEntity.newBuilder();
      ((EntityValue) convertedVal)
          .get()
          .getProperties()
          .forEach((key, value) -> builder.set(key, setExcludeFromIndexes(value)));
      return EntityValue.of(builder.build());
    } else if (convertedVal.getClass().equals(ListValue.class)) {
      return ListValue.of(
          (List)
              ((ListValue) convertedVal)
                  .get().stream().map(this::setExcludeFromIndexes).toList());
    } else {
      return convertedVal.toBuilder().setExcludeFromIndexes(true).build();
    }
  }
}
