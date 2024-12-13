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

package com.google.cloud.spring.data.spanner.core.convert;

import com.google.cloud.spanner.Struct;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerDataException;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerPersistentEntity;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerPersistentProperty;
import java.util.Set;
import org.springframework.data.mapping.InstanceCreatorMetadata;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.model.EntityInstantiator;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.mapping.model.ParameterValueProvider;
import org.springframework.data.mapping.model.PersistentEntityParameterValueProvider;

/**
 * A reading converter for Spanner that uses custom converters.
 *
 * @since 1.1
 */
class ConverterAwareMappingSpannerEntityReader implements SpannerEntityReader {

  private final SpannerMappingContext spannerMappingContext;

  private final EntityInstantiators instantiators;

  private final SpannerReadConverter converter;

  ConverterAwareMappingSpannerEntityReader(
      SpannerMappingContext spannerMappingContext, SpannerReadConverter spannerReadConverter) {
    this.spannerMappingContext = spannerMappingContext;

    this.instantiators = new EntityInstantiators();

    this.converter = spannerReadConverter;
  }

  /**
   * Reads a single POJO from a Cloud Spanner row.
   *
   * @param type the type of POJO
   * @param source the Cloud Spanner row
   * @param includeColumns the columns to read. If null then all columns will be read.
   * @param allowMissingColumns if true, then properties with no corresponding column are not
   *     mapped. If false, then an exception is thrown.
   * @param <R> the type of the POJO.
   * @return the POJO
   */
  @SuppressWarnings("unchecked")
  public <R> R read(
      Class<R> type, Struct source, Set<String> includeColumns, boolean allowMissingColumns) {
    boolean readAllColumns = includeColumns == null;
    SpannerPersistentEntity<R> persistentEntity =
        (SpannerPersistentEntity<R>) this.spannerMappingContext.getPersistentEntityOrFail(type);

    StructAccessor structAccessor = new StructAccessor(source, this.spannerMappingContext.getGson());

    StructPropertyValueProvider propertyValueProvider =
        new StructPropertyValueProvider(structAccessor, this.converter, this, allowMissingColumns);

    InstanceCreatorMetadata<SpannerPersistentProperty> instanceCreatorMetadata =
        persistentEntity.getInstanceCreatorMetadata();

    // @formatter:off
    ParameterValueProvider<SpannerPersistentProperty> parameterValueProvider =
        new PersistentEntityParameterValueProvider<>(persistentEntity, propertyValueProvider, null);
    // @formatter:on

    EntityInstantiator instantiator = this.instantiators.getInstantiatorFor(persistentEntity);
    R instance = instantiator.createInstance(persistentEntity, parameterValueProvider);
    PersistentPropertyAccessor accessor = persistentEntity.getPropertyAccessor(instance);

    persistentEntity.doWithProperties(
        (PropertyHandler<SpannerPersistentProperty>)
            spannerPersistentProperty -> {
              if (spannerPersistentProperty.isEmbedded()) {
                accessor.setProperty(
                    spannerPersistentProperty,
                    read(
                        spannerPersistentProperty.getType(),
                        source,
                        includeColumns,
                        allowMissingColumns));
              } else {
                if (!shouldSkipProperty(
                    structAccessor,
                    spannerPersistentProperty,
                    includeColumns,
                    readAllColumns,
                    allowMissingColumns,
                    instanceCreatorMetadata)) {

                  Object value = propertyValueProvider.getPropertyValue(spannerPersistentProperty);
                  accessor.setProperty(spannerPersistentProperty, value);
                }
              }
            });

    return instance;
  }

  private boolean shouldSkipProperty(
      StructAccessor struct,
      SpannerPersistentProperty spannerPersistentProperty,
      Set<String> includeColumns,
      boolean readAllColumns,
      boolean allowMissingColumns,
      InstanceCreatorMetadata<SpannerPersistentProperty> persistenceConstructor) {
    String columnName = spannerPersistentProperty.getColumnName();
    boolean notRequiredByPartialRead = !readAllColumns && !includeColumns.contains(columnName);

    return spannerPersistentProperty.isLazyInterleaved()
        || notRequiredByPartialRead
        || isMissingColumn(struct, allowMissingColumns, columnName)
        || struct.isNull(columnName)
        || persistenceConstructor.isCreatorParameter(spannerPersistentProperty);
  }

  private boolean isMissingColumn(
      StructAccessor struct, boolean allowMissingColumns, String columnName) {
    boolean missingColumn = !struct.hasColumn(columnName);
    if (missingColumn && !allowMissingColumns) {
      throw new SpannerDataException(
          "Unable to read column from Cloud Spanner results: " + columnName);
    }
    return missingColumn;
  }
}
