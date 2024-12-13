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

package com.google.cloud.spring.data.datastore.core.convert;

import static com.google.cloud.spring.data.datastore.core.util.ValueUtil.boxIfNeeded;

import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.FullEntity.Builder;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.Value;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreDataException;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.core.mapping.DatastorePersistentProperty;
import com.google.cloud.spring.data.datastore.core.mapping.EmbeddedType;
import com.google.cloud.spring.data.datastore.core.util.ValueUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * In order to support {@link CustomConversions}, this class applies 2-step conversions. The first
 * step produces one of {@link org.springframework.data.mapping.model.SimpleTypeHolder}'s simple
 * types. The second step converts simple types to Datastore-native types. The second step is
 * skipped if the first one produces a Datastore-native type.
 *
 * @since 1.1
 */
public class TwoStepsConversions implements ReadWriteConversions {
  private static final Converter<Blob, byte[]> BLOB_TO_BYTE_ARRAY_CONVERTER =
      new Converter<>() {
        @Override
        public byte[] convert(Blob source) {
          return source.toByteArray();
        }
      };

  private static final Converter<byte[], Blob> BYTE_ARRAY_TO_BLOB_CONVERTER =
      new Converter<>() {
        @Override
        public Blob convert(byte[] source) {
          return Blob.copyFrom(source);
        }
      };

  private final GenericConversionService conversionService;

  private final GenericConversionService internalConversionService;

  private final CustomConversions customConversions;

  private final ObjectToKeyFactory objectToKeyFactory;

  private final DatastoreMappingContext datastoreMappingContext;

  private DatastoreEntityConverter datastoreEntityConverter;

  private final Map<Class, Optional<Class<?>>> writeConverters = new ConcurrentHashMap<>();

  public TwoStepsConversions(
      CustomConversions customConversions,
      ObjectToKeyFactory objectToKeyFactory,
      DatastoreMappingContext datastoreMappingContext) {
    this.objectToKeyFactory = objectToKeyFactory;
    this.datastoreMappingContext = datastoreMappingContext;
    this.conversionService = new DefaultConversionService();
    this.internalConversionService = new DefaultConversionService();
    this.customConversions = customConversions;
    this.customConversions.registerConvertersIn(this.conversionService);

    this.internalConversionService.addConverter(BYTE_ARRAY_TO_BLOB_CONVERTER);
    this.internalConversionService.addConverter(BLOB_TO_BYTE_ARRAY_CONVERTER);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convertOnRead(Object val, Class targetCollectionType, Class targetComponentType) {
    return convertOnRead(
        val,
        EmbeddedType.NOT_EMBEDDED,
        targetCollectionType,
        TypeInformation.of(targetComponentType));
  }

  @Override
  public <T> T convertOnRead(
      Object val, EmbeddedType embeddedType, TypeInformation targetTypeInformation) {
    TypeInformation componentTypeInformation;
    Class collectionType = null;
    if (ValueUtil.isCollectionLike(targetTypeInformation.getType())) {
      componentTypeInformation = targetTypeInformation.getComponentType();
      collectionType = targetTypeInformation.getType();
    } else {
      componentTypeInformation = targetTypeInformation;
    }
    return convertOnRead(val, embeddedType, collectionType, componentTypeInformation);
  }

  @SuppressWarnings("unchecked")
  private <T> T convertOnRead(
      Object val,
      EmbeddedType embeddedType,
      Class targetCollectionType,
      TypeInformation<?> targetComponentType) {
    if (val == null) {
      return null;
    }
    BiFunction<Object, TypeInformation<?>, ?> readConverter = switch (embeddedType) {
      case EMBEDDED_MAP -> (x, typeInformation) ->
          convertOnReadSingleEmbeddedMap(
              x,
              Objects.requireNonNull(typeInformation.getComponentType()).getType(),
              typeInformation.getMapValueType(),
              targetComponentType);
      case EMBEDDED_ENTITY -> this::convertOnReadSingleEmbedded;
      case NOT_EMBEDDED -> this::convertOnReadSingle;
    };

    if (ValueUtil.isCollectionLike(val.getClass())
        && targetCollectionType != null
        && targetComponentType != null) {
      // Convert collection.
      try {
        Assert.isInstanceOf(
            Iterable.class, val, "Value passed to convertOnRead expected to be Iterable");
        List<?> elements =
            StreamSupport.stream(((Iterable<?>) val).spliterator(), false)
                .map(
                    v -> {
                      Object o = (v instanceof Value) ? ((Value<?>) v).get() : v;
                      return readConverter.apply(o, targetComponentType);
                    })
                .toList();
        return convertCollection(elements, targetCollectionType);
      } catch (ConversionException | DatastoreDataException ex) {
        throw new DatastoreDataException("Unable process elements of a collection", ex);
      }
    }
    // Convert single value.
    return (T) readConverter.apply(val, targetComponentType);
  }

  private <T, R> Map<T, R> convertOnReadSingleEmbeddedMap(
      Object value,
      Class<T> keyType,
      TypeInformation<R> targetComponentType,
      TypeInformation<?> componentType) {
    Assert.notNull(value, "Cannot convert a null value.");
    if (value instanceof BaseEntity<?> baseEntity) {
      return this.datastoreEntityConverter.readAsMap(baseEntity, componentType);
    }
    throw new DatastoreDataException(
        "Embedded entity was expected, but " + value.getClass() + " found");
  }

  @SuppressWarnings("unchecked")
  private <T> T convertOnReadSingleEmbedded(
      Object value, TypeInformation<?> targetTypeInformation) {
    Assert.notNull(value, "Cannot convert a null value.");
    if (value instanceof BaseEntity<?> baseEntity) {
      return (T)
          this.datastoreEntityConverter.read(targetTypeInformation.getType(), baseEntity);
    }
    throw new DatastoreDataException(
        "Embedded entity was expected, but " + value.getClass() + " found");
  }

  @SuppressWarnings("unchecked")
  private <T> T convertOnReadSingle(Object val, TypeInformation<?> targetTypeInformation) {
    if (val == null) {
      return null;
    }
    Class<?> targetType = boxIfNeeded(targetTypeInformation.getType());
    Class<?> sourceType = val.getClass();
    Object result = null;
    TypeTargets typeTargets = computeTypeTargets(targetType);

    if (typeTargets.getFirstStepTarget() == null
        && typeTargets.getSecondStepTarget() == null
        && ClassUtils.isAssignable(targetType, val.getClass())) {
      // neither first or second steps were applied, no conversion is necessary
      result = val;
    } else if (typeTargets.getFirstStepTarget() == null
        && typeTargets.getSecondStepTarget() != null) {
      // only second step was applied on write
      result = this.internalConversionService.convert(val, targetType);
    } else if (typeTargets.getFirstStepTarget() != null
        && typeTargets.getSecondStepTarget() == null) {
      // only first step was applied on write
      result = this.conversionService.convert(val, targetType);
    } else if (typeTargets.getFirstStepTarget() != null
        && typeTargets.getSecondStepTarget() != null) {
      // both steps were applied
      Object secondStepVal =
          this.internalConversionService.convert(val, typeTargets.getFirstStepTarget());
      result = this.conversionService.convert(secondStepVal, targetType);
    } else if (DatastoreNativeTypes.isNativeType(sourceType)
        && this.conversionService.canConvert(sourceType, targetType)) {
      // the value can be directly converted
      result = this.conversionService.convert(val, targetType);
    } else if (DatastoreNativeTypes.isNativeType(sourceType)
        && this.internalConversionService.canConvert(sourceType, targetType)) {
      result = this.internalConversionService.convert(val, targetType);
    }

    if (result != null) {
      return (T) result;
    } else {
      throw new DatastoreDataException("Unable to convert " + val.getClass() + " to " + targetType);
    }
  }

  @Override
  public Value convertOnWrite(Object proppertyVal, DatastorePersistentProperty persistentProperty) {
    return convertOnWrite(
        proppertyVal,
        persistentProperty.getEmbeddedType(),
        persistentProperty.getFieldName(),
        persistentProperty.getTypeInformation());
  }

  private Value convertOnWrite(
      Object proppertyVal,
      EmbeddedType embeddedType,
      String fieldName,
      TypeInformation typeInformation) {
    Object val = proppertyVal;

    Function<Object, Value> writeConverter = this::convertOnWriteSingle;
    if (proppertyVal != null) {
      writeConverter = switch (embeddedType) {
        case EMBEDDED_MAP -> x -> convertOnWriteSingleEmbeddedMap(x, fieldName,
            typeInformation.getMapValueType());
        case EMBEDDED_ENTITY -> x -> convertOnWriteSingleEmbedded(x, fieldName);
        case NOT_EMBEDDED -> this::convertOnWriteSingle;
      };
    }

    val = ValueUtil.toListIfArray(val);

    if (val instanceof Iterable<?> iterable) {
      List<Value<?>> values = new ArrayList<>();
      for (Object propEltValue : iterable) {
        values.add(writeConverter.apply(propEltValue));
      }
      return ListValue.of(values);
    }
    return writeConverter.apply(val);
  }

  private EntityValue applyEntityValueBuilder(
      Object val, String kindName, Consumer<Builder> consumer, boolean createKey) {

    FullEntity.Builder<IncompleteKey> builder;
    if (!createKey) {
      builder = FullEntity.newBuilder();
    } else {
      /* The following does 3 sequential null checks. We only want an ID value if the object isn't null,
        has an ID property, and the ID property isn't null.
       */
      Optional idProp =
          Optional.ofNullable(val)
              .map(v -> this.datastoreMappingContext.getPersistentEntity(v.getClass()))
              .map(PersistentEntity::getIdProperty)
              .map(
                  id ->
                      Objects.requireNonNull(this.datastoreMappingContext
                              .getPersistentEntity(val.getClass()))
                          .getPropertyAccessor(val)
                          .getProperty(id));

      IncompleteKey key =
          idProp.isPresent()
              ? this.objectToKeyFactory.getKeyFromId(idProp.get(), kindName)
              : this.objectToKeyFactory.getIncompleteKey(kindName);
      builder = FullEntity.newBuilder(key);
    }
    consumer.accept(builder);
    return EntityValue.of(builder.build());
  }

  private EntityValue convertOnWriteSingleEmbeddedMap(
      Object val, String kindName, TypeInformation valueTypeInformation) {
    return applyEntityValueBuilder(
        null,
        kindName,
        builder -> {
          for (Map.Entry<?, ?> e : ((Map<?, ?>) val).entrySet()) {
            String field =
                convertOnReadSingle(
                    convertOnWriteSingle(e.getKey()).get(),
                    TypeInformation.of(String.class));
            builder.set(
                field,
                convertOnWrite(
                    e.getValue(),
                    EmbeddedType.of(valueTypeInformation),
                    field,
                    valueTypeInformation));
          }
        },
        false);
  }

  private EntityValue convertOnWriteSingleEmbedded(Object val, String kindName) {
    return applyEntityValueBuilder(
        val, kindName, builder -> this.datastoreEntityConverter.write(val, builder), true);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Value convertOnWriteSingle(Object propertyVal) {
    Object result = propertyVal;
    if (result != null) {
      TypeTargets typeTargets = computeTypeTargets(result.getClass());
      if (typeTargets.getFirstStepTarget() != null) {
        result = this.conversionService.convert(propertyVal, typeTargets.getFirstStepTarget());
      }

      if (typeTargets.getSecondStepTarget() != null) {
        result = this.internalConversionService.convert(result, typeTargets.getSecondStepTarget());
      }
    }
    return DatastoreNativeTypes.wrapValue(result);
  }

  private TypeTargets computeTypeTargets(Class<?> firstStepSource) {
    Class<?> firstStepTarget = null;
    Class<?> secondStepTarget = null;

    if (!DatastoreNativeTypes.isNativeType(firstStepSource)) {
      Optional<Class<?>> simpleType = this.customConversions.getCustomWriteTarget(firstStepSource);
      if (simpleType.isPresent()) {
        firstStepTarget = simpleType.get();
      }

      Class<?> effectiveFirstStepTarget =
          (firstStepTarget != null) ? firstStepTarget : firstStepSource;

      Optional<Class<?>> datastoreBasicType = getCustomWriteTarget(effectiveFirstStepTarget);

      if (datastoreBasicType.isPresent()) {
        secondStepTarget = datastoreBasicType.get();
      }
    }
    return new TypeTargets(firstStepTarget, secondStepTarget);
  }

  @SuppressWarnings("unchecked")
  public <T> T convertCollection(Object collection, Class<?> target) {
    if (collection == null || target == null || ClassUtils.isAssignableValue(target, collection)) {
      return (T) collection;
    }
    return (T) this.conversionService.convert(collection, target);
  }

  private Optional<Class<?>> getCustomWriteTarget(Class<?> sourceType) {
    if (DatastoreNativeTypes.isNativeType(sourceType)) {
      return Optional.empty();
    }
    return this.writeConverters.computeIfAbsent(sourceType, this::getDatastoreCompatibleType);
  }

  @Override
  public Optional<Class<?>> getDatastoreCompatibleType(Class inputType) {
    if (DatastoreNativeTypes.DATASTORE_NATIVE_TYPES.contains(inputType)) {
      return Optional.of(inputType);
    }
    return DatastoreNativeTypes.DATASTORE_NATIVE_TYPES.stream()
        .filter(
            simpleType ->
                this.internalConversionService.canConvert(inputType, simpleType)
                    && this.internalConversionService.canConvert(simpleType, inputType))
        .findAny();
  }

  @Override
  public void registerEntityConverter(DatastoreEntityConverter datastoreEntityConverter) {
    this.datastoreEntityConverter = datastoreEntityConverter;
  }

  private record TypeTargets(Class<?> firstStepTarget,
                             Class<?> secondStepTarget) {

    Class<?> getFirstStepTarget() {
      return this.firstStepTarget;
    }

    Class<?> getSecondStepTarget() {
      return this.secondStepTarget;
    }
  }
}
