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

package com.google.cloud.spring.data.datastore.repository.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyQuery;
import com.google.cloud.datastore.KeyValue;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.spring.data.datastore.core.DatastoreResultsIterable;
import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import com.google.cloud.spring.data.datastore.core.convert.DatastoreCustomConversions;
import com.google.cloud.spring.data.datastore.core.convert.DatastoreEntityConverter;
import com.google.cloud.spring.data.datastore.core.convert.ReadWriteConversions;
import com.google.cloud.spring.data.datastore.core.convert.TwoStepsConversions;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Field;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.ProjectionInformation;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.lang.Nullable;

/** Tests for Part-Tree Datastore Query Methods. */
class PartTreeDatastoreQueryTests {

  private static final Object[] EMPTY_PARAMETERS = new Object[0];

  private static final DatastoreResultsIterable<Object> EMPTY_RESPONSE =
          new DatastoreResultsIterable<>(Collections.emptyIterator(), null);
  static final CompositeFilter FILTER =
          CompositeFilter.and(
                  PropertyFilter.eq("action", "BUY"),
                  PropertyFilter.eq("ticker", "abcd"),
                  PropertyFilter.lt("price", 8.88),
                  PropertyFilter.ge("price", 3.33),
                  PropertyFilter.isNull("__key__"));

  private DatastoreTemplate datastoreTemplate;

  private DatastoreQueryMethod queryMethod;

  private DatastoreMappingContext datastoreMappingContext;

  private PartTreeDatastoreQuery partTreeDatastoreQuery;

  private DatastoreEntityConverter datastoreEntityConverter;

  private ReadWriteConversions readWriteConversions;

  @BeforeEach
  void initMocks() {
    this.queryMethod = mock(DatastoreQueryMethod.class);
    when(this.queryMethod.getReturnedObjectType()).thenReturn((Class) Trade.class);
    this.datastoreTemplate = mock(DatastoreTemplate.class);
    this.datastoreMappingContext = new DatastoreMappingContext();
    this.datastoreEntityConverter = mock(DatastoreEntityConverter.class);
    this.readWriteConversions =
            new TwoStepsConversions(
                    new DatastoreCustomConversions(), null, this.datastoreMappingContext);
    when(this.datastoreTemplate.getDatastoreEntityConverter())
            .thenReturn(this.datastoreEntityConverter);
    when(this.datastoreEntityConverter.getConversions()).thenReturn(this.readWriteConversions);
  }

  private PartTreeDatastoreQuery<Trade> createQuery(
          boolean isPageQuery, boolean isSliceQuery, ProjectionInformation projectionInformation) {
    ProjectionFactory projectionFactory = mock(ProjectionFactory.class);
    doReturn(projectionInformation != null ? projectionInformation : getProjectionInformationMock())
            .when(projectionFactory)
            .getProjectionInformation(any());

    PartTreeDatastoreQuery<Trade> tradePartTreeDatastoreQuery =
            new PartTreeDatastoreQuery<>(
                    this.queryMethod,
                    this.datastoreTemplate,
                    this.datastoreMappingContext,
                    Trade.class,
                    projectionFactory);
    PartTreeDatastoreQuery<Trade> spy = spy(tradePartTreeDatastoreQuery);
    doReturn(isPageQuery).when(spy).isPageQuery();
    doReturn(isSliceQuery).when(spy).isSliceQuery();
    doAnswer(invocation -> invocation.getArguments()[0])
            .when(spy)
            .processRawObjectForProjection(any());
    doAnswer(invocation -> invocation.getArguments()[0])
            .when(spy)
            .convertResultCollection(any(), isNotNull());

    return spy;
  }

  private ProjectionInformation getProjectionInformationMock() {
    ProjectionInformation mock = mock(ProjectionInformation.class);
    doReturn(Trade.class).when(mock).getType();
    return mock;
  }

  @Test
  void compoundNameConventionTest() throws NoSuchMethodException {
    queryWithMockResult(
            "findTop333ByActionAndSymbolAndPriceLessThan"
                    + "AndPriceGreaterThanEqual"
                    + "AndEmbeddedEntityStringFieldEquals"
                    + "AndIdIsNullOrderByIdDesc",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod",
                            String.class,
                            String.class,
                            double.class,
                            double.class,
                            String.class));

    Object[] params =
        new Object[] {
          "BUY",
          "abcd",
          // this int param requires custom conversion
          8,
          3.33,
          "abc"
        };

    when(this.datastoreTemplate.queryKeysOrEntities(any(), any()))
            .thenAnswer(
                    invocation -> {
                      EntityQuery statement = invocation.getArgument(0);

                      EntityQuery expected =
                              StructuredQuery.newEntityQueryBuilder()
                                      .setFilter(
                                              CompositeFilter.and(
                                                      PropertyFilter.eq("action", "BUY"),
                                                      PropertyFilter.eq("ticker", "abcd"),
                                                      PropertyFilter.lt("price", 8L),
                                                      PropertyFilter.ge("price", 3.33),
                                                      PropertyFilter.eq("embeddedEntity.stringField", "abc"),
                                                      PropertyFilter.isNull("__key__")))
                                      .setKind("trades")
                                      .setOrderBy(OrderBy.desc("__key__"))
                                      .setLimit(333)
                                      .build();

                      assertThat(statement).isEqualTo(expected);

                      return EMPTY_RESPONSE;
                    });

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    this.partTreeDatastoreQuery.execute(params);
    verify(this.datastoreTemplate).queryKeysOrEntities(any(), any());
  }

  @Test
  void compoundNameConventionProjectionTest()
          throws NoSuchMethodException, IntrospectionException {
    ProjectionInformation projectionInformation = mock(ProjectionInformation.class);
    doReturn(TradeProjection.class).when(projectionInformation).getType();
    doReturn(true).when(projectionInformation).isClosed();
    doReturn(
            Arrays.asList(
                    new PropertyDescriptor("id", null, null),
                    new PropertyDescriptor("symbol", null, null)))
            .when(projectionInformation)
            .getInputProperties();

    queryWithMockResult(
            "findTop333ByActionAndSymbolAndPriceLessThan"
                    + "AndPriceGreaterThanEqual"
                    + "AndEmbeddedEntityStringFieldEquals"
                    + "AndIdIsNullOrderByIdDesc",
            null,
            getClass()
                    .getMethod(
                            "tradeMethodProjection",
                            String.class,
                            String.class,
                            double.class,
                            double.class,
                            String.class),
            projectionInformation);

    Object[] params =
            new Object[] {"BUY", "abcd",
              // this int param requires custom conversion
              8, 3.33, "abc"
            };

    when(this.datastoreTemplate.queryKeysOrEntities(any(), any()))
            .thenAnswer(
                    invocation -> {
                      StructuredQuery statement = invocation.getArgument(0);

                      StructuredQuery expected =
                              StructuredQuery.newProjectionEntityQueryBuilder()
                                      .addProjection("__key__", "ticker")
                                      .setFilter(
                                              CompositeFilter.and(
                                                      PropertyFilter.eq("action", "BUY"),
                                                      PropertyFilter.eq("ticker", "abcd"),
                                                      PropertyFilter.lt("price", 8L),
                                                      PropertyFilter.ge("price", 3.33),
                                                      PropertyFilter.eq("embeddedEntity.stringField", "abc"),
                                                      PropertyFilter.isNull("__key__")))
                                      .setKind("trades")
                                      .setOrderBy(OrderBy.desc("__key__"))
                                      .setLimit(333)
                                      .build();

                      assertThat(statement).isEqualTo(expected);

                      return EMPTY_RESPONSE;
                    });

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    this.partTreeDatastoreQuery.execute(params);
    verify(this.datastoreTemplate).queryKeysOrEntities(any(), any());
  }

  @Test
  void ambiguousSortPageableParam() throws NoSuchMethodException {
    queryWithMockResult(
            "findTop333ByActionAndSymbolAndPriceLessThanAndPriceGreater"
                    + "ThanEqualAndIdIsNullOrderByIdDesc",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod",
                            String.class,
                            String.class,
                            double.class,
                            double.class,
                            Pageable.class));

    Object[] params =
            new Object[] {"BUY", "abcd", 8.88, 3.33, PageRequest.of(1, 444, Sort.Direction.ASC, "price")
            };

    when(this.datastoreTemplate.queryKeysOrEntities(any(), any()))
            .thenAnswer(
                    invocation -> {
                      EntityQuery statement = invocation.getArgument(0);

                      EntityQuery expected =
                              StructuredQuery.newEntityQueryBuilder()
                                      .setFilter(FILTER)
                                      .setKind("trades")
                                      .setOffset(444)
                                      .setLimit(444)
                                      .setOrderBy(OrderBy.desc("__key__"), OrderBy.asc("price"))
                                      .build();

                      assertThat(statement).isEqualTo(expected);

                      return EMPTY_RESPONSE;
                    });

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    this.partTreeDatastoreQuery.execute(params);
    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(any(), any());
  }

  @Test
  void nullPageable() throws NoSuchMethodException {
    queryWithMockResult(
            "findTop333ByActionAndSymbolAndPriceLessThanAndPriceGreater"
                    + "ThanEqualAndIdIsNullOrderByIdDesc",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod",
                            String.class,
                            String.class,
                            double.class,
                            double.class,
                            Pageable.class));

    Object[] params = new Object[] {"BUY", "abcd", 8.88, 3.33, null};

    when(this.datastoreTemplate.queryKeysOrEntities(any(), any()))
            .thenAnswer(
                    invocation -> {
                      EntityQuery statement = invocation.getArgument(0);

                      EntityQuery expected =
                              StructuredQuery.newEntityQueryBuilder()
                                      .setFilter(FILTER)
                                      .setKind("trades")
                                      .setLimit(333)
                                      .setOrderBy(OrderBy.desc("__key__"))
                                      .build();

                      assertThat(statement).isEqualTo(expected);

                      return EMPTY_RESPONSE;
                    });

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    this.partTreeDatastoreQuery.execute(params);
    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(any(), any());
  }

  @Test
  void ambiguousSort() throws NoSuchMethodException {
    queryWithMockResult(
            "findByActionAndSymbolAndPriceLessThanAndPriceGreater"
                    + "ThanEqualAndIdIsNullOrderByIdDesc",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod", String.class, String.class, double.class, double.class, Sort.class));

    Object[] params =
            new Object[] {"BUY", "abcd", 8.88, 3.33, Sort.by(Sort.Direction.ASC, "price")};

    when(this.datastoreTemplate.queryKeysOrEntities(any(), any()))
            .thenAnswer(
                    invocation -> {
                      EntityQuery statement = invocation.getArgument(0);

                      EntityQuery expected =
                              StructuredQuery.newEntityQueryBuilder()
                                      .setFilter(FILTER)
                                      .setKind("trades")
                                      .setOrderBy(OrderBy.desc("__key__"), OrderBy.asc("price"))
                                      .build();

                      assertThat(statement).isEqualTo(expected);

                      return EMPTY_RESPONSE;
                    });

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    this.partTreeDatastoreQuery.execute(params);
    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(any(), any());
  }

  @Test
  void nullSort() throws NoSuchMethodException {
    queryWithMockResult(
            "findByActionAndSymbolAndPriceLessThanAndPriceGreater"
                    + "ThanEqualAndIdIsNullOrderByIdDesc",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod", String.class, String.class, double.class, double.class, Sort.class));

    Object[] params = new Object[] {"BUY", "abcd", 8.88, 3.33, null};

    when(this.datastoreTemplate.queryKeysOrEntities(any(), any()))
            .thenAnswer(
                    invocation -> {
                      EntityQuery statement = invocation.getArgument(0);

                      EntityQuery expected =
                              StructuredQuery.newEntityQueryBuilder()
                                      .setFilter(FILTER)
                                      .setKind("trades")
                                      .setOrderBy(OrderBy.desc("__key__"))
                                      .build();

                      assertThat(statement).isEqualTo(expected);

                      return EMPTY_RESPONSE;
                    });

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    this.partTreeDatastoreQuery.execute(params);
    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(any(), any());
  }

  @Test
  void caseInsensitiveSort() throws NoSuchMethodException {
    queryWithMockResult(
            "findByActionAndSymbolAndPriceLessThanAndPriceGreater"
                    + "ThanEqualAndIdIsNullOrderByIdDesc",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod", String.class, String.class, double.class, double.class, Sort.class));

    Object[] params =
            new Object[] {"BUY", "abcd", 8.88, 3.33, Sort.by(Sort.Order.by("price").ignoreCase())};

    assertThatThrownBy(() -> this.partTreeDatastoreQuery.execute(params))
            .hasMessage("Datastore doesn't support sorting ignoring case");
  }

  @Test
  void caseNullHandlingSort() throws NoSuchMethodException {

    queryWithMockResult(
            "findByActionAndSymbolAndPriceLessThanAndPriceGreater"
                    + "ThanEqualAndIdIsNullOrderByIdDesc",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod", String.class, String.class, double.class, double.class, Sort.class));

    Object[] params =
            new Object[] {"BUY", "abcd", 8.88, 3.33, Sort.by(Sort.Order.by("price").nullsFirst())};


    assertThatThrownBy(() -> this.partTreeDatastoreQuery.execute(params))
            .hasMessage("Datastore supports only NullHandling.NATIVE null handling");
  }

  @Test
  void pageableParam() throws NoSuchMethodException {
    queryWithMockResult(
            "findByActionAndSymbolAndPriceLessThanAndPriceGreater" + "ThanEqualAndIdIsNull",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod",
                            String.class,
                            String.class,
                            double.class,
                            double.class,
                            Pageable.class));

    Object[] params =
            new Object[] {"BUY", "abcd", 8.88, 3.33, PageRequest.of(1, 444, Sort.Direction.DESC, "id")};

    when(this.datastoreTemplate.queryKeysOrEntities(any(), any()))
            .thenAnswer(
                    invocation -> {
                      EntityQuery statement = invocation.getArgument(0);

                      EntityQuery expected =
                              StructuredQuery.newEntityQueryBuilder()
                                      .setFilter(FILTER)
                                      .setKind("trades")
                                      .setOffset(444)
                                      .setOrderBy(OrderBy.desc("__key__"))
                                      .setLimit(444)
                                      .build();

                      assertThat(statement).isEqualTo(expected);

                      return EMPTY_RESPONSE;
                    });

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    this.partTreeDatastoreQuery.execute(params);
    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(any(), any());
  }

  @Test
  void pageableQuery() throws NoSuchMethodException {
    queryWithMockResult(
            "findByActionAndSymbolAndPriceLessThanAndPriceGreater" + "ThanEqualAndIdIsNull",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod",
                            String.class,
                            String.class,
                            double.class,
                            double.class,
                            Pageable.class));

    this.partTreeDatastoreQuery = createQuery(true, false, null);

    Object[] params =
            new Object[] {"BUY", "abcd", 8.88, 3.33, PageRequest.of(1, 2, Sort.Direction.DESC, "id")};

    preparePageResults(2, 2, null, Arrays.asList(3, 4), Arrays.asList(1, 2, 3, 4));

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    Page result = (Page) this.partTreeDatastoreQuery.execute(params);
    assertThat(result.getTotalElements()).isEqualTo(4);
    assertThat(result.getTotalPages()).isEqualTo(2);
    assertThat(result.getNumberOfElements()).isEqualTo(2);

    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(isA(EntityQuery.class), any());

    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(isA(KeyQuery.class), any());
  }

  @Test
  void pageableQueryNextPage() throws NoSuchMethodException {
    queryWithMockResult(
            "findByActionAndSymbolAndPriceLessThanAndPriceGreater" + "ThanEqualAndIdIsNull",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod",
                            String.class,
                            String.class,
                            double.class,
                            double.class,
                            Pageable.class));

    this.partTreeDatastoreQuery = createQuery(true, false, null);

    PageRequest pageRequest = PageRequest.of(1, 2, Sort.Direction.DESC, "id");
    Cursor cursor = Cursor.copyFrom("abc".getBytes());
    Object[] params =
            new Object[] {"BUY", "abcd", 8.88, 3.33, DatastorePageable.from(pageRequest, cursor, 99L)};

    preparePageResults(2, 2, cursor, Arrays.asList(3, 4), Arrays.asList(1, 2, 3, 4));

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    Page result = (Page) this.partTreeDatastoreQuery.execute(params);
    assertThat(result.getTotalElements()).isEqualTo(99L);
    assertThat(result.getTotalPages()).isEqualTo(50);
    assertThat(result.getNumberOfElements()).isEqualTo(2);

    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(any(), any());
  }

  @Test
  void pageableQueryMissingPageableParamReturnsAllResults() throws NoSuchMethodException {
    queryWithMockResult(
            "findByActionAndSymbolAndPriceLessThanAndPriceGreater"
                    + "ThanEqualAndIdIsNullOrderByIdDesc",
            null,
            getClass()
                    .getMethod("tradeMethod", String.class, String.class, double.class, double.class));

    this.partTreeDatastoreQuery = createQuery(true, false, null);

    Object[] params = new Object[] {"BUY", "abcd", 8.88, 3.33};

    preparePageResults(0, null, null, Arrays.asList(1, 2, 3, 4), Arrays.asList(1, 2, 3, 4));

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    Page result = (Page) this.partTreeDatastoreQuery.execute(params);
    assertThat(result.getTotalElements()).isEqualTo(4);
    assertThat(result.getTotalPages()).isEqualTo(1);

    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(isA(EntityQuery.class), any());

    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(isA(KeyQuery.class), any());
  }

  @Test
  void sliceQueryLast() throws NoSuchMethodException {
    queryWithMockResult(
            "findByActionAndSymbolAndPriceLessThanAndPriceGreater" + "ThanEqualAndIdIsNull",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod",
                            String.class,
                            String.class,
                            double.class,
                            double.class,
                            Pageable.class));

    this.partTreeDatastoreQuery = createQuery(false, true, null);

    Object[] params =
            new Object[] {"BUY", "abcd", 8.88, 3.33, PageRequest.of(1, 2, Sort.Direction.DESC, "id")};

    prepareSliceResults(2, 2, true);

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    Slice result = (Slice) this.partTreeDatastoreQuery.execute(params);
    assertThat(result.hasNext()).isTrue();

    verify(this.datastoreTemplate, times(1)).queryEntitiesSlice(any(), any(), any());
  }

  @Test
  void sliceQueryNoPageableParam() throws NoSuchMethodException {
    queryWithMockResult(
            "findByActionAndSymbolAndPriceLessThanAndPriceGreater"
                    + "ThanEqualAndIdIsNullOrderByIdDesc",
            null,
            getClass()
                    .getMethod("tradeMethod", String.class, String.class, double.class, double.class));

    this.partTreeDatastoreQuery = createQuery(false, true, null);

    Object[] params = new Object[] {"BUY", "abcd", 8.88, 3.33};

    prepareSliceResults(0, null, false);

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    Slice result = (Slice) this.partTreeDatastoreQuery.execute(params);
    assertThat(result.hasNext()).isFalse();

    verify(this.datastoreTemplate, times(1))
            .queryEntitiesSlice(isA(EntityQuery.class), any(), any());
  }

  @Test
  void sliceQuery() throws NoSuchMethodException {
    queryWithMockResult(
            "findByActionAndSymbolAndPriceLessThanAndPriceGreater" + "ThanEqualAndIdIsNull",
            null,
            getClass()
                    .getMethod(
                            "tradeMethod",
                            String.class,
                            String.class,
                            double.class,
                            double.class,
                            Pageable.class));

    this.partTreeDatastoreQuery = createQuery(false, true, null);

    Object[] params =
            new Object[] {"BUY", "abcd", 8.88, 3.33, PageRequest.of(0, 2, Sort.Direction.DESC, "id")};

    prepareSliceResults(0, 2, false);

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    Slice result = (Slice) this.partTreeDatastoreQuery.execute(params);
    assertThat(result.hasNext()).isFalse();

    verify(this.datastoreTemplate, times(1))
            .queryEntitiesSlice(isA(EntityQuery.class), any(), any());
  }

  private void preparePageResults(
          int offset,
          Integer limit,
          Cursor cursor,
          List<Integer> pageResults,
          List<Integer> fullResults) {
    when(this.datastoreTemplate.queryKeysOrEntities(isA(EntityQuery.class), any()))
            .thenAnswer(
                    invocation -> {
                      EntityQuery statement = invocation.getArgument(0);
                      EntityQuery expected =
                              StructuredQuery.newEntityQueryBuilder()
                                      .setFilter(FILTER)
                                      .setKind("trades")
                                      .setStartCursor(cursor)
                                      .setOffset(cursor != null ? 0 : offset)
                                      .setOrderBy(OrderBy.desc("__key__"))
                                      .setLimit(limit)
                                      .build();

                      assertThat(statement).isEqualTo(expected);
                      return new DatastoreResultsIterable(
                              pageResults.iterator(), Cursor.copyFrom("abc".getBytes()));
                    });

    when(this.datastoreTemplate.queryKeysOrEntities(isA(KeyQuery.class), any()))
            .thenAnswer(
                    invocation -> {
                      KeyQuery statement = invocation.getArgument(0);
                      KeyQuery expected =
                              StructuredQuery.newKeyQueryBuilder()
                                      .setFilter(FILTER)
                                      .setKind("trades")
                                      .setOrderBy(OrderBy.desc("__key__"))
                                      .build();

                      assertThat(statement).isEqualTo(expected);
                      return new DatastoreResultsIterable(
                              fullResults.iterator(), Cursor.copyFrom("def".getBytes()));
                    });
  }

  private void prepareSliceResults(int offset, Integer queryLimit, Boolean hasNext) {
    Cursor cursor = Cursor.copyFrom("abc".getBytes());
    List<Integer> datastoreMatchingRecords = Arrays.asList(3, 4, 5);
    when(this.datastoreTemplate.queryEntitiesSlice(isA(EntityQuery.class), any(), any()))
            .thenAnswer(
                    invocation -> {
                      EntityQuery statement = invocation.getArgument(0);
                      EntityQuery expected =
                              StructuredQuery.newEntityQueryBuilder()
                                      .setFilter(FILTER)
                                      .setKind("trades")
                                      .setOffset(offset)
                                      .setOrderBy(OrderBy.desc("__key__"))
                                      .setLimit(queryLimit)
                                      .build();

                      assertThat(statement).isEqualTo(expected);
                      return new SliceImpl(
                              new DatastoreResultsIterable(datastoreMatchingRecords.iterator(), cursor)
                                      .toList(),
                              Pageable.unpaged(),
                              hasNext);
                    });
  }

  @Test
  void deleteTest() throws NoSuchMethodException {
    queryWithMockResult(
            "deleteByAction", null, getClass().getMethod("countByAction", String.class));

    this.partTreeDatastoreQuery = createQuery(false, false, null);

    Object[] params = new Object[] {"BUY"};

    prepareDeleteResults(false);

    when(this.queryMethod.getReturnedObjectType()).thenReturn((Class) int.class);

    this.partTreeDatastoreQuery.execute(params);

    verify(this.datastoreTemplate, times(0)).query(any(), (Function) any());

    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(any(), any());

    verify(this.datastoreTemplate, times(1)).deleteAllById(eq(Arrays.asList(3, 4, 5)), any());
  }

  @Test
  void deleteReturnCollectionTest() throws NoSuchMethodException {
    queryWithMockResult(
            "deleteByAction", null, getClass().getMethod("countByAction", String.class));

    this.partTreeDatastoreQuery = createQuery(false, false, null);

    Object[] params = new Object[] {"BUY"};

    prepareDeleteResults(true);

    when(this.queryMethod.getCollectionReturnType()).thenReturn(List.class);

    List result = (List) this.partTreeDatastoreQuery.execute(params);
    assertThat(result).containsExactly(3, 4, 5);

    verify(this.datastoreTemplate, times(0)).query(any(), (Function) any());

    verify(this.datastoreTemplate, times(1)).queryKeysOrEntities(any(), any());

    verify(this.datastoreTemplate, times(1)).deleteAll(Arrays.asList(3, 4, 5));
  }

  private void prepareDeleteResults(boolean isCollection) {
    Cursor cursor = Cursor.copyFrom("abc".getBytes());
    List<Integer> datastoreMatchingRecords = Arrays.asList(3, 4, 5);
    when(this.datastoreTemplate.queryKeysOrEntities(any(), any()))
            .thenAnswer(
                    invocation -> {
                      StructuredQuery<?> statement = invocation.getArgument(0);
                      StructuredQuery.Builder builder =
                              isCollection
                                      ? StructuredQuery.newEntityQueryBuilder()
                                      : StructuredQuery.newKeyQueryBuilder();
                      StructuredQuery<?> expected =
                              builder.setFilter(PropertyFilter.eq("action", "BUY")).setKind("trades").build();

                      assertThat(statement).isEqualTo(expected);
                      return new DatastoreResultsIterable(datastoreMatchingRecords.iterator(), cursor);
                    });
  }

  @Test
  void unspecifiedParametersTest() throws NoSuchMethodException {

    queryWithMockResult(
            "countByTraderIdBetween", null, getClass().getMethod("countByAction", String.class));

    when(this.queryMethod.getName())
            .thenReturn(
                    "findByActionAndSymbolAndPriceLessThanAndPriceGreater"
                            + "ThanEqualAndIdIsNullOrderByIdDesc");
    this.partTreeDatastoreQuery = createQuery(false, false, null);

    // There are too few params specified, so the exception will occur.
    Object[] params = new Object[] {"BUY"};

    assertThatThrownBy(() -> this.partTreeDatastoreQuery.execute(params))
            .hasMessage("Too few parameters are provided for query method: "
                    + "findByActionAndSymbolAndPriceLessThanAndPriceGreaterThanEqualAndIdIsNullOrderByIdDesc");
  }

  @Test
  void unsupportedParamTypeTest() throws NoSuchMethodException {

    queryWithMockResult("findByAction", null, getClass().getMethod("countByPrice", Integer.class));

    this.partTreeDatastoreQuery = createQuery(false, false, null);

    Object[] params = new Object[] {new Trade()};

    assertThatThrownBy(() ->  this.partTreeDatastoreQuery.execute(params))
            .hasMessage("Unable to convert class "
                    + "com.google.cloud.spring.data.datastore.repository.query."
                    + "PartTreeDatastoreQueryTests$Trade to Datastore supported type.");
  }

  @Test
  void unSupportedPredicateTest() throws NoSuchMethodException {

    queryWithMockResult("countByTraderIdBetween", null, getClass().getMethod("traderAndPrice"));
    this.partTreeDatastoreQuery = createQuery(false, false, null);

    assertThatThrownBy(() -> this.partTreeDatastoreQuery.execute(EMPTY_PARAMETERS))
            .hasMessageContaining("Unsupported predicate keyword: BETWEEN");


  }

  @Test
  void unSupportedOrTest() {
    //PartTreeDatastoreQuery constructor will fail as part of queryWithMockResult setup
    assertThatThrownBy(() -> queryWithMockResult("countByTraderIdOrPrice", null, getClass().getMethod("traderAndPrice")))
            .hasMessage("Cloud Datastore only supports multiple filters combined with AND.");

  }

  @Test
  void countTest() throws NoSuchMethodException {
    List<Trade> results = new ArrayList<>();
    results.add(new Trade());

    queryWithMockResult(
            "countByAction", results, getClass().getMethod("countByAction", String.class));

    PartTreeDatastoreQuery spyQuery = this.partTreeDatastoreQuery;

    Object[] params =
            new Object[] {"BUY", };
    assertThat(spyQuery.execute(params)).isEqualTo(1L);
  }

  @Test
  void existShouldBeTrueWhenResultSetIsNotEmpty() throws NoSuchMethodException {
    List<Trade> results = new ArrayList<>();
    results.add(new Trade());

    queryWithMockResult(
            "existsByAction", results, getClass().getMethod("countByAction", String.class));

    PartTreeDatastoreQuery spyQuery = this.partTreeDatastoreQuery;

    doAnswer(invocation -> invocation.getArgument(0))
            .when(spyQuery)
            .processRawObjectForProjection(any());

    Object[] params =
            new Object[] {"BUY", };
    assertThat((boolean) spyQuery.execute(params)).isTrue();
  }

  @Test
  void existShouldBeFalseWhenResultSetIsEmpty() throws NoSuchMethodException {
    queryWithMockResult(
            "existsByAction",
            Collections.emptyList(),
            getClass().getMethod("countByAction", String.class));

    PartTreeDatastoreQuery spyQuery = this.partTreeDatastoreQuery;

    doAnswer(invocation -> invocation.getArgument(0))
            .when(spyQuery)
            .processRawObjectForProjection(any());

    Object[] params =
            new Object[] {"BUY", };
    assertThat((boolean) spyQuery.execute(params)).isFalse();
  }

  @Test
  void nonCollectionReturnType() throws NoSuchMethodException {
    Trade trade = new Trade();
    queryWithMockResult(
            "findByAction", null, getClass().getMethod("findByAction", String.class), true, null);

    Object[] params =
            new Object[] {"BUY", };

    when(this.datastoreTemplate.queryKeysOrEntities(any(), any()))
            .thenAnswer(
                    invocation -> {
                      EntityQuery statement = invocation.getArgument(0);

                      EntityQuery expected =
                              StructuredQuery.newEntityQueryBuilder()
                                      .setFilter(PropertyFilter.eq("action", "BUY"))
                                      .setKind("trades")
                                      .setLimit(1)
                                      .build();

                      assertThat(statement).isEqualTo(expected);

                      List<Trade> results = Collections.singletonList(trade);
                      return new DatastoreResultsIterable(results.iterator(), null);
                    });

    assertThat(this.partTreeDatastoreQuery.execute(params)).isEqualTo(trade);
  }

  @Test
  void usingIdField() throws NoSuchMethodException {
    Trade trade = new Trade();
    queryWithMockResult(
            "findByActionAndId",
            null,
            getClass().getMethod("findByActionAndId", String.class, String.class),
            true,
            null);

    Object[] params = new Object[] {"BUY", "id1"};
    when(this.datastoreTemplate.createKey("trades", "id1"))
            .thenAnswer(
                    invocation ->
                            Key.newBuilder("project", invocation.getArgument(0), invocation.getArgument(1))
                                    .build());

    when(this.datastoreTemplate.queryKeysOrEntities(any(), any()))
            .thenAnswer(
                    invocation -> {
                      EntityQuery statement = invocation.getArgument(0);

                      EntityQuery expected =
                              StructuredQuery.newEntityQueryBuilder()
                                      .setFilter(
                                              CompositeFilter.and(
                                                      PropertyFilter.eq("action", "BUY"),
                                                      PropertyFilter.eq(
                                                              "__key__",
                                                              KeyValue.of(Key.newBuilder("project", "trades", "id1").build()))))
                                      .setKind("trades")
                                      .setLimit(1)
                                      .build();

                      assertThat(statement).isEqualTo(expected);

                      List<Trade> results = Collections.singletonList(trade);
                      return new DatastoreResultsIterable(results.iterator(), null);
                    });

    assertThat(this.partTreeDatastoreQuery.execute(params)).isEqualTo(trade);
  }

  @Test
  void nonCollectionReturnTypeNoResultsNullable() throws NoSuchMethodException {
    queryWithMockResult(
            "findByAction",
            Collections.emptyList(),
            getClass().getMethod("findByActionNullable", String.class),
            true,
            null);

    Object[] params =
            new Object[] {"BUY", };
    assertThat(this.partTreeDatastoreQuery.execute(params)).isNull();
  }

  @Test
  void nonCollectionReturnTypeNoResultsOptional() throws NoSuchMethodException {
    queryWithMockResult(
            "findByAction",
            Collections.emptyList(),
            getClass().getMethod("findByActionOptional", String.class),
            true,
            null);

    Object[] params =
            new Object[] {"BUY", };
    assertThat((Optional) this.partTreeDatastoreQuery.execute(params)).isNotPresent();
  }

  @Test
  void streamResultTest() throws NoSuchMethodException {
    Trade tradeA = new Trade();
    tradeA.id = "a";
    Trade tradeB = new Trade();
    tradeB.id = "b";
    queryWithMockResult(
            "findStreamByAction",
            Arrays.asList(tradeA, tradeB),
            getClass().getMethod("findStreamByAction", String.class));
    when(this.queryMethod.isStreamQuery()).thenReturn(true);
    Object[] params =
            new Object[] {"BUY", };
    Object result = this.partTreeDatastoreQuery.execute(params);
    assertThat(result).isInstanceOf(Stream.class);
    assertThat((Stream) result).hasSize(2).contains(tradeA, tradeB);
  }

  private void queryWithMockResult(
          String queryName, List results, Method m, ProjectionInformation projectionInformation) {
    queryWithMockResult(queryName, results, m, false, projectionInformation);
  }

  private void queryWithMockResult(String queryName, List results, Method m) {
    queryWithMockResult(queryName, results, m, false, null);
  }

  private void queryWithMockResult(
          String queryName,
          List results,
          Method m,
          boolean mockOptionalNullable,
          ProjectionInformation projectionInformation) {
    when(this.queryMethod.getName()).thenReturn(queryName);
    doReturn(new DefaultParameters(m)).when(this.queryMethod).getParameters();
    if (mockOptionalNullable) {
      DefaultRepositoryMetadata mockMetadata = mock(DefaultRepositoryMetadata.class);
      doReturn(m.getReturnType()).when(mockMetadata).getReturnedDomainClass(m);
      doReturn(ClassTypeInformation.fromReturnTypeOf(m)).when(mockMetadata).getReturnType(m);
      DatastoreQueryMethod datastoreQueryMethod =
              new DatastoreQueryMethod(m, mockMetadata, mock(SpelAwareProxyProjectionFactory.class));
      doReturn(datastoreQueryMethod.isOptionalReturnType())
              .when(this.queryMethod)
              .isOptionalReturnType();
      doReturn(datastoreQueryMethod.isNullable()).when(this.queryMethod).isNullable();
    }

    this.partTreeDatastoreQuery = createQuery(false, false, projectionInformation);
    when(this.datastoreTemplate.queryKeysOrEntities(any(), Mockito.<Class<Trade>>any()))
            .thenReturn(
                    new DatastoreResultsIterable<>(
                            results != null ? results.iterator() : Collections.emptyIterator(), null));
  }

  public Trade findByAction(String action) {
    return null;
  }

  public Stream<Trade> findStreamByAction(String action) {
    return null;
  }

  @Nullable
  public Trade findByActionNullable(String action) {
    return null;
  }

  @Nullable
  public Trade findByActionAndId(String action, String id) {
    return null;
  }

  public Optional<Trade> findByActionOptional(String action) {
    return null;
  }

  public List<Trade> tradeMethod(
          String action, String symbol, double pless, double pgreater, String embeddedProperty) {
    return null;
  }

  public List<TradeProjection> tradeMethodProjection(
          String action, String symbol, double pless, double pgreater, String embeddedProperty) {
    return null;
  }

  public List<Trade> tradeMethod(String action, String symbol, double pless, double pgreater) {
    return null;
  }

  public List<Trade> tradeMethod(
          String action, String symbol, double pless, double pgreater, Pageable pageable) {
    return null;
  }

  public List<Trade> tradeMethod(
          String action, String symbol, double pless, double pgreater, Sort sort) {
    return null;
  }

  public int traderAndPrice() {
    return 0;
  }

  public int countByAction(String action) {
    return 0;
  }

  public int countByPrice(Integer action) {
    return 0;
  }

  @Entity(name = "trades")
  private static class Trade {
    @Id String id;

    String action;

    Double price;

    Double shares;

    @Field(name = "ticker")
    String symbol;

    @Field(name = "trader_id")
    String traderId;

    EmbeddedEntity embeddedEntity;
  }

  public interface TradeProjection {
    String getId();

    String getSymbol();
  }

  @Entity
  public class EmbeddedEntity {
    private String stringField;
  }
}
