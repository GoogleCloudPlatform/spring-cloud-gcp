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

package com.google.cloud.spring.data.firestore.repository.query;

import static com.google.cloud.spring.data.firestore.FirestoreTemplate.NAME_FIELD;
import static org.springframework.data.repository.query.parser.Part.Type.CONTAINING;
import static org.springframework.data.repository.query.parser.Part.Type.GREATER_THAN;
import static org.springframework.data.repository.query.parser.Part.Type.GREATER_THAN_EQUAL;
import static org.springframework.data.repository.query.parser.Part.Type.IN;
import static org.springframework.data.repository.query.parser.Part.Type.LESS_THAN;
import static org.springframework.data.repository.query.parser.Part.Type.LESS_THAN_EQUAL;
import static org.springframework.data.repository.query.parser.Part.Type.NEGATING_SIMPLE_PROPERTY;
import static org.springframework.data.repository.query.parser.Part.Type.NOT_IN;
import static org.springframework.data.repository.query.parser.Part.Type.SIMPLE_PROPERTY;

import com.google.cloud.spring.core.util.MapBuilder;
import com.google.cloud.spring.data.firestore.FirestoreDataException;
import com.google.cloud.spring.data.firestore.FirestoreReactiveOperations;
import com.google.cloud.spring.data.firestore.mapping.FirestoreClassMapper;
import com.google.cloud.spring.data.firestore.mapping.FirestoreMappingContext;
import com.google.cloud.spring.data.firestore.mapping.FirestorePersistentEntity;
import com.google.cloud.spring.data.firestore.mapping.FirestorePersistentProperty;
import com.google.firestore.v1.StructuredQuery;
import com.google.firestore.v1.StructuredQuery.FieldReference;
import com.google.firestore.v1.Value;
import com.google.protobuf.Int32Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

/**
 * @since 1.2
 */
public class PartTreeFirestoreQuery implements RepositoryQuery {
  private final PartTree tree;

  private final FirestoreQueryMethod queryMethod;

  private final FirestoreReactiveOperations reactiveOperations;

  private final FirestorePersistentEntity<?> persistentEntity;

  private final FirestoreClassMapper classMapper;

  private final FirestoreMappingContext mappingContext;

  private static final Map<Part.Type, OperatorSelector> PART_TO_FILTER_OP =
      new MapBuilder<Part.Type, OperatorSelector>()
          .put(SIMPLE_PROPERTY, new OperatorSelector(StructuredQuery.FieldFilter.Operator.EQUAL))
          .put(
              GREATER_THAN_EQUAL,
              new OperatorSelector(StructuredQuery.FieldFilter.Operator.GREATER_THAN_OR_EQUAL))
          .put(
              GREATER_THAN, new OperatorSelector(StructuredQuery.FieldFilter.Operator.GREATER_THAN))
          .put(
              LESS_THAN_EQUAL,
              new OperatorSelector(StructuredQuery.FieldFilter.Operator.LESS_THAN_OR_EQUAL))
          .put(LESS_THAN, new OperatorSelector(StructuredQuery.FieldFilter.Operator.LESS_THAN))
          .put(IN, new OperatorSelector(StructuredQuery.FieldFilter.Operator.IN))
          .put(
              CONTAINING,
              new OperatorSelector(
                  StructuredQuery.FieldFilter.Operator.ARRAY_CONTAINS,
                  StructuredQuery.FieldFilter.Operator.ARRAY_CONTAINS_ANY))
          .put(
              NEGATING_SIMPLE_PROPERTY,
              new OperatorSelector(StructuredQuery.FieldFilter.Operator.NOT_EQUAL))
          .put(NOT_IN, new OperatorSelector(StructuredQuery.FieldFilter.Operator.NOT_IN))
          .build();

  public PartTreeFirestoreQuery(
      FirestoreQueryMethod queryMethod,
      FirestoreReactiveOperations reactiveOperations,
      FirestoreMappingContext mappingContext,
      FirestoreClassMapper classMapper) {
    this.queryMethod = queryMethod;
    this.reactiveOperations = reactiveOperations;
    ReturnedType returnedType = queryMethod.getResultProcessor().getReturnedType();
    this.tree = new PartTree(queryMethod.getName(), returnedType.getDomainType());
    this.persistentEntity = mappingContext.getPersistentEntity(returnedType.getDomainType());
    this.mappingContext = mappingContext;
    this.classMapper = classMapper;
    validate();
  }

  private void validate() {
    List parts = this.tree.get().collect(Collectors.toList());
    if (parts.size() > 1 && parts.get(0) instanceof PartTree.OrPart) {
      throw new FirestoreDataException(
          "Cloud Firestore doesn't support 'OR' (method name: "
              + this.getQueryMethod().getName()
              + ")");
    }
    List<String> unsupportedParts =
        this.tree.getParts().stream()
            .filter(part -> !isSupportedPart(part.getType()))
            .map(part -> part.getType().toString())
            .collect(Collectors.toList());
    if (!unsupportedParts.isEmpty()) {
      throw new FirestoreDataException(
          "Unsupported predicate keywords: "
              + unsupportedParts
              + " in "
              + this.getQueryMethod().getName());
    }
  }

  private boolean isSupportedPart(Part.Type partType) {
    return PART_TO_FILTER_OP.containsKey(partType) || partType == Part.Type.IS_NULL;
  }

  @Override
  public Object execute(Object[] parameters) {
    StructuredQuery.Builder builder = createBuilderWithFilter(parameters);
    Sort sort = null;

    // Handle Pageable parameters.
    if (!getQueryMethod().getParameters().isEmpty()) {
      ParameterAccessor paramAccessor =
          new ParametersParameterAccessor(getQueryMethod().getParameters(), parameters);
      Pageable pageable = paramAccessor.getPageable();
      if (pageable.isPaged()) {
        builder.setOffset((int) Math.min(Integer.MAX_VALUE, pageable.getOffset()));
        builder.setLimit(Int32Value.newBuilder().setValue(pageable.getPageSize()));
      }
      sort = paramAccessor.getSort();
    }

    if (getQueryMethod().getName().startsWith("findFirst")
        || getQueryMethod().getName().startsWith("findTop")) {
      builder.setLimit(Int32Value.newBuilder().setValue(1));
    }

    if (sort == null || sort.isUnsorted()) {
      sort = this.tree.getSort();
    }
    if (sort.isSorted()) {
      builder.addAllOrderBy(createFirestoreSortOrders(sort));
    }

    if (this.tree.isCountProjection()) {
      return this.reactiveOperations.count(this.persistentEntity.getType(), builder);
    } else {
      return this.reactiveOperations.execute(builder, this.persistentEntity.getType());
    }
  }

  /**
   * This method converts {@link org.springframework.data.domain.Sort.Order} to {@link
   * StructuredQuery.Order} for Firestore.
   */
  private List<StructuredQuery.Order> createFirestoreSortOrders(Sort sort) {
    List<StructuredQuery.Order> sortOrders = new ArrayList<>();

    for (Order order : sort) {
      if (order.isIgnoreCase()) {
        throw new IllegalArgumentException("Datastore does not support ignore case sort orders.");
      }

      // Get the name of the field to sort on
      FirestorePersistentProperty persistentProperty =
          this.persistentEntity.getPersistentProperty(order.getProperty());
      if (persistentProperty == null) {
        throw new IllegalArgumentException(
            "Persistent property does not exist: " + order.getProperty());
      }
      String fieldName = persistentProperty.getFieldName();

      StructuredQuery.Direction dir =
          order.getDirection() == Direction.DESC
              ? StructuredQuery.Direction.DESCENDING
              : StructuredQuery.Direction.ASCENDING;

      FieldReference ref = FieldReference.newBuilder().setFieldPath(fieldName).build();
      com.google.firestore.v1.StructuredQuery.Order firestoreOrder =
          com.google.firestore.v1.StructuredQuery.Order.newBuilder()
              .setField(ref)
              .setDirection(dir)
              .build();

      sortOrders.add(firestoreOrder);
    }

    return sortOrders;
  }

  private StructuredQuery.Builder createBuilderWithFilter(Object[] parameters) {
    StructuredQuery.Builder builder = StructuredQuery.newBuilder();

    Iterator it = Arrays.asList(parameters).iterator();

    StructuredQuery.CompositeFilter.Builder compositeFilter =
        StructuredQuery.CompositeFilter.newBuilder();
    compositeFilter.setOp(StructuredQuery.CompositeFilter.Operator.AND);

    this.tree
        .getParts()
        .forEach(
            part -> {
              StructuredQuery.FieldReference fieldReference =
                  StructuredQuery.FieldReference.newBuilder().setFieldPath(buildName(part)).build();
              StructuredQuery.Filter.Builder filter = StructuredQuery.Filter.newBuilder();

              if (part.getType() == Part.Type.IS_NULL) {
                filter
                    .getUnaryFilterBuilder()
                    .setField(fieldReference)
                    .setOp(StructuredQuery.UnaryFilter.Operator.IS_NULL);
              } else {
                if (!it.hasNext()) {
                  throw new FirestoreDataException(
                      "Too few parameters are provided for query method: "
                          + getQueryMethod().getName());
                }
                Object value = it.next();
                StructuredQuery.FieldFilter.Builder fieldFilterBuilder =
                    filter
                        .getFieldFilterBuilder()
                        .setField(fieldReference)
                        .setOp(getOperator(part, value));
                if (fieldReference.getFieldPath().equals(NAME_FIELD)) {

                  // In Firestore, a field that is used as a document ID can only be of String type
                  fieldFilterBuilder.setValue(
                      Value.newBuilder()
                          .setReferenceValue(
                              this.reactiveOperations.buildResourceName(
                                  persistentEntity, (String) value)));
                } else {
                  fieldFilterBuilder.setValue(this.classMapper.toFirestoreValue(value));
                }
              }
              compositeFilter.addFilters(filter.build());
            });

    builder.setWhere(
        StructuredQuery.Filter.newBuilder().setCompositeFilter(compositeFilter.build()));
    return builder;
  }

  private String buildName(Part part) {
    Iterable<PropertyPath> iterable = () -> part.getProperty().iterator();

    return StreamSupport.stream(iterable.spliterator(), false)
        .map(
            propertyPath -> {
              FirestorePersistentEntity<?> fpe =
                  this.mappingContext.getPersistentEntity(propertyPath.getOwningType());
              return fpe.getPersistentProperty(propertyPath.getSegment()).getFieldName();
            })
        .collect(Collectors.joining("."));
  }

  @Override
  public QueryMethod getQueryMethod() {
    return this.queryMethod;
  }

  private StructuredQuery.FieldFilter.Operator getOperator(Part part, Object value) {
    OperatorSelector operatorSelector = PART_TO_FILTER_OP.get(part.getType());
    return operatorSelector.getOperator(value);
  }

  static class OperatorSelector {
    StructuredQuery.FieldFilter.Operator operatorForSingleType;

    StructuredQuery.FieldFilter.Operator operatorForIterableType;

    OperatorSelector(
        StructuredQuery.FieldFilter.Operator operatorForSingleType,
        StructuredQuery.FieldFilter.Operator operatorForIterableType) {
      this.operatorForSingleType = operatorForSingleType;
      this.operatorForIterableType = operatorForIterableType;
    }

    OperatorSelector(StructuredQuery.FieldFilter.Operator commonOperator) {
      this.operatorForSingleType = commonOperator;
      this.operatorForIterableType = commonOperator;
    }

    StructuredQuery.FieldFilter.Operator getOperator(Object val) {
      return val instanceof Iterable ? this.operatorForIterableType : this.operatorForSingleType;
    }
  }
}
