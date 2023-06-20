/*
 * Copyright 2017-2021 the original author or authors.
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

package com.google.cloud.spring.data.datastore.repository.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.spring.data.datastore.core.DatastoreOperations;
import com.google.cloud.spring.data.datastore.core.DatastoreQueryOptions;
import com.google.cloud.spring.data.datastore.core.DatastoreResultsIterable;
import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import com.google.cloud.spring.data.datastore.repository.query.DatastorePageable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

/** Tests for the default Datastore Repository implementation. */
class SimpleDatastoreRepositoryTests {

  private final DatastoreTemplate datastoreTemplate = mock(DatastoreTemplate.class);

  private final SimpleDatastoreRepository<Object, String> simpleDatastoreRepository =
      new SimpleDatastoreRepository<>(this.datastoreTemplate, Object.class);

  private final SimpleDatastoreRepository<Object, Object> spyRepo =
      spy(new SimpleDatastoreRepository<>(this.datastoreTemplate, Object.class));

  @Test
  void saveTest() {
    Object object = new Object();
    this.simpleDatastoreRepository.save(object);
    verify(this.datastoreTemplate).save(same(object));
  }

  @Test
  void saveAllTest() {
    Iterable entities = Arrays.asList();
    this.simpleDatastoreRepository.saveAll(entities);
    verify(this.datastoreTemplate).saveAll(same(entities));
  }

  @Test
  void findByIdTest() {
    String id = "key";
    this.simpleDatastoreRepository.findById(id);
    verify(this.datastoreTemplate).findById(id, Object.class);
  }

  @Test
  void existsByIdTest() {
    String id = "key";
    this.simpleDatastoreRepository.existsById(id);
    verify(this.datastoreTemplate).existsById(id, Object.class);
  }

  @Test
  void findAllTest() {
    this.simpleDatastoreRepository.findAll();
    verify(this.datastoreTemplate).findAll(Object.class);
  }

  @Test
  void findAllByIdTest() {
    List<String> keys = Arrays.asList("1", "2");
    this.simpleDatastoreRepository.findAllById(keys);
    verify(this.datastoreTemplate).findAllById(keys, Object.class);
  }

  @Test
  void countTest() {
    this.simpleDatastoreRepository.count();
    verify(this.datastoreTemplate).count(Object.class);
  }

  @Test
  void deleteByIdTest() {
    String id = "key";
    this.simpleDatastoreRepository.deleteById(id);
    verify(this.datastoreTemplate).deleteById(id, Object.class);
  }

  @Test
  void deleteTest() {
    Object object = new Object();
    this.simpleDatastoreRepository.delete(object);
    verify(this.datastoreTemplate).delete(same(object));
  }

  @Test
  void deleteAllTest() {
    Iterable entities = Arrays.asList();
    this.simpleDatastoreRepository.deleteAll(entities);
    verify(this.datastoreTemplate).deleteAll(same(entities));
  }

  @Test
  void deleteAllClassTest() {
    this.simpleDatastoreRepository.deleteAll();
    verify(this.datastoreTemplate).deleteAll(Object.class);
  }

  @Test
  void runTransactionCallableTest() {
    when(this.datastoreTemplate.performTransaction(any()))
        .thenAnswer(
            invocation -> {
              Function<DatastoreOperations, String> f = invocation.getArgument(0);
              return f.apply(this.datastoreTemplate);
            });

    String result =
        new SimpleDatastoreRepository<Object, String>(this.datastoreTemplate, Object.class)
            .performTransaction(repo -> "test");
    assertThat(result).isEqualTo("test");
  }

  @Test
  void findAllPageableAsc() {
    this.simpleDatastoreRepository.findAll(PageRequest.of(0, 5, Sort.Direction.ASC, "property1"));

    verify(this.datastoreTemplate)
        .findAll(
            Object.class,
            new DatastoreQueryOptions.Builder()
                .setLimit(5)
                .setOffset(0)
                .setSort(Sort.by("property1"))
                .build());
    verify(this.datastoreTemplate).count(any());
  }

  @Test
  void findAllPageableDesc() {
    this.simpleDatastoreRepository.findAll(
        PageRequest.of(1, 5, Sort.Direction.DESC, "property1", "property2"));
    verify(this.datastoreTemplate)
        .findAll(
            Object.class,
            new DatastoreQueryOptions.Builder()
                .setLimit(5)
                .setOffset(5)
                .setSort(
                    Sort.by(
                        new Sort.Order(Sort.Direction.DESC, "property1"),
                        new Sort.Order(Sort.Direction.DESC, "property2")))
                .setCursor(null)
                .build());
    verify(this.datastoreTemplate).count(any());
  }

  @Test
  void findAllPageableCursor() {
    Cursor cursor = Cursor.copyFrom("abc".getBytes());
    Pageable pageable =
        DatastorePageable.from(
            PageRequest.of(1, 5, Sort.Direction.DESC, "property1", "property2"), cursor, 10L);
    this.simpleDatastoreRepository.findAll(pageable);
    verify(this.datastoreTemplate)
        .findAll(
            Object.class,
            new DatastoreQueryOptions.Builder()
                .setLimit(5)
                .setOffset(5)
                .setSort(
                    Sort.by(
                        new Sort.Order(Sort.Direction.DESC, "property1"),
                        new Sort.Order(Sort.Direction.DESC, "property2")))
                .setCursor(cursor)
                .build());
    verify(this.datastoreTemplate, times(0)).count(any());
  }

  @Test
  void findAllByExample() {
    Example<Object> example = Example.of(new Object());
    this.simpleDatastoreRepository.findAll(example);
    verify(this.datastoreTemplate).queryByExample(same(example), isNull());
  }

  @Test
  void findAllByExampleSort() {
    Example<Object> example = Example.of(new Object());
    Sort sort = Sort.by("id");
    this.simpleDatastoreRepository.findAll(example, sort);
    verify(this.datastoreTemplate)
        .queryByExample(
            same(example), eq(new DatastoreQueryOptions.Builder().setSort(sort).build()));
  }

  @Test
  void findAllByExamplePage() {
    Example<Object> example = Example.of(new Object());
    Sort sort = Sort.by("id");

    doAnswer(invocationOnMock -> new DatastoreResultsIterable(Arrays.asList(1, 2), null))
        .when(this.datastoreTemplate)
        .queryByExample(
            same(example),
            eq(new DatastoreQueryOptions.Builder().setLimit(2).setOffset(2).setSort(sort).build()));

    doAnswer(invocationOnMock -> new DatastoreResultsIterable(Arrays.asList(1, 2, 3, 4, 5), null))
        .when(this.datastoreTemplate)
        .keyQueryByExample(same(example), isNull());

    Page<Object> result =
        this.simpleDatastoreRepository.findAll(example, PageRequest.of(1, 2, sort));
    assertThat(result).containsExactly(1, 2);
    assertThat(result.getTotalElements()).isEqualTo(5);
    verify(this.datastoreTemplate)
        .queryByExample(
            same(example),
            eq(new DatastoreQueryOptions.Builder().setLimit(2).setOffset(2).setSort(sort).build()));
    verify(this.datastoreTemplate).keyQueryByExample(same(example), isNull());
  }

  @Test
  void findAllByExamplePageCursor() {
    Example<Object> example = Example.of(new Object());
    Sort sort = Sort.by("id");
    Cursor cursor = Cursor.copyFrom("abc".getBytes());

    doAnswer(invocationOnMock -> new DatastoreResultsIterable(Arrays.asList(1, 2), cursor))
        .when(this.datastoreTemplate)
        .queryByExample(
            same(example),
            eq(new DatastoreQueryOptions.Builder().setLimit(2).setOffset(0).setSort(sort).build()));

    doAnswer(invocationOnMock -> new DatastoreResultsIterable(Arrays.asList(3, 4), null))
        .when(this.datastoreTemplate)
        .queryByExample(
            same(example),
            eq(
                new DatastoreQueryOptions.Builder()
                    .setLimit(2)
                    .setOffset(2)
                    .setSort(sort)
                    .setCursor(cursor)
                    .build()));

    doAnswer(invocationOnMock -> new DatastoreResultsIterable(Arrays.asList(1, 2, 3, 4, 5), null))
        .when(this.datastoreTemplate)
        .keyQueryByExample(same(example), isNull());

    Page<Object> result =
        this.simpleDatastoreRepository.findAll(example, PageRequest.of(0, 2, sort));
    assertThat(result).containsExactly(1, 2);
    assertThat(result.getTotalElements()).isEqualTo(5);

    Page<Object> resultNext =
        this.simpleDatastoreRepository.findAll(example, result.getPageable().next());
    assertThat(resultNext).containsExactly(3, 4);
    assertThat(resultNext.getTotalElements()).isEqualTo(5);

    verify(this.datastoreTemplate)
        .queryByExample(
            same(example),
            eq(new DatastoreQueryOptions.Builder().setLimit(2).setOffset(0).setSort(sort).build()));
    verify(this.datastoreTemplate)
        .queryByExample(
            same(example),
            eq(
                new DatastoreQueryOptions.Builder()
                    .setLimit(2)
                    .setOffset(2)
                    .setSort(sort)
                    .setCursor(cursor)
                    .build()));
    verify(this.datastoreTemplate).keyQueryByExample(same(example), isNull());
  }

  @Test
  void findAllByExamplePageNull() {

    Example<Object> example =  Example.of(new Object());

    assertThatThrownBy(() ->  this.simpleDatastoreRepository.findAll(example, (Pageable) null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A non-null pageable is required.");

  }

  @Test
  void findOneByExample() {
    Example<Object> example = Example.of(new Object());

    doAnswer(invocationOnMock -> new DatastoreResultsIterable(Arrays.asList(1), null))
        .when(this.datastoreTemplate)
        .queryByExample(same(example), eq(new DatastoreQueryOptions.Builder().setLimit(1).build()));

    assertThat(this.simpleDatastoreRepository.findOne(example)).contains(1);

    verify(this.datastoreTemplate)
        .queryByExample(same(example), eq(new DatastoreQueryOptions.Builder().setLimit(1).build()));
  }

  @Test
  void existsByExampleTrue() {
    Example<Object> example2 = Example.of(new Object());

    doAnswer(invocationOnMock -> Arrays.asList(1))
        .when(this.datastoreTemplate)
        .keyQueryByExample(
            same(example2), eq(new DatastoreQueryOptions.Builder().setLimit(1).build()));

    assertThat(this.simpleDatastoreRepository.exists(example2)).isTrue();

    verify(this.datastoreTemplate)
        .keyQueryByExample(
            same(example2), eq(new DatastoreQueryOptions.Builder().setLimit(1).build()));
  }

  @Test
  void existsByExampleFalse() {
    Example<Object> example2 = Example.of(new Object());

    doAnswer(invocationOnMock -> Arrays.asList())
        .when(this.datastoreTemplate)
        .keyQueryByExample(
            same(example2), eq(new DatastoreQueryOptions.Builder().setLimit(1).build()));

    assertThat(this.simpleDatastoreRepository.exists(example2)).isFalse();

    verify(this.datastoreTemplate)
        .keyQueryByExample(
            same(example2), eq(new DatastoreQueryOptions.Builder().setLimit(1).build()));
  }

  @Test
  void countByExample() {
    Example<Object> example2 = Example.of(new Object());

    doAnswer(invocationOnMock -> Arrays.asList(1, 2, 3))
        .when(this.datastoreTemplate)
        .keyQueryByExample(same(example2), isNull());

    assertThat(this.simpleDatastoreRepository.count(example2)).isEqualTo(3);

    verify(this.datastoreTemplate).keyQueryByExample(same(example2), isNull());
  }

  @Test
  void countByExampleZero() {
    Example<Object> example1 = Example.of(new Object());

    doAnswer(invocationOnMock -> new ArrayList<>())
        .when(this.datastoreTemplate)
        .keyQueryByExample(same(example1), isNull());

    assertThat(this.simpleDatastoreRepository.count(example1)).isZero();

    verify(this.datastoreTemplate).keyQueryByExample(same(example1), isNull());
  }

  @Test
  void findAllSortAsc() {
    this.simpleDatastoreRepository.findAll(
        Sort.by(
            new Sort.Order(Sort.Direction.DESC, "property1"),
            new Sort.Order(Sort.Direction.ASC, "property2")));
    DatastoreQueryOptions opts =
        new DatastoreQueryOptions.Builder()
            .setSort(
                Sort.by(
                    new Sort.Order(Sort.Direction.DESC, "property1"),
                    new Sort.Order(Sort.Direction.ASC, "property2")))
            .build();
    verify(this.datastoreTemplate).findAll(Object.class, opts);
  }

  @Test
  void deleteAllById() {
    List<String> keys = Arrays.asList("1", "2");
    this.simpleDatastoreRepository.deleteAllById(keys);
    verify(this.datastoreTemplate).deleteAllById(keys, Object.class);
  }

  @Test
  void findByExampleFluentQueryAll() {
    Example<Object> example = Example.of(new Object());
    Sort sort = Sort.by("id");
    Iterable entities = Arrays.asList();
    doAnswer(invocationOnMock -> new DatastoreResultsIterable(entities, null))
        .when(this.datastoreTemplate)
        .queryByExample(same(example), any());
    this.spyRepo.findBy(example, FetchableFluentQuery::all);
    verify(this.spyRepo).findAll(same(example), eq(Sort.unsorted()));
    this.spyRepo.findBy(example, query -> query.sortBy(sort).all());
    verify(this.spyRepo).findAll(same(example), eq(sort));
  }

  @Test
  void findByExampleFluentQueryOneValue() {
    Example<Object> example = Example.of(new Object());
    Iterable entities = Arrays.asList();
    doAnswer(invocationOnMock -> new DatastoreResultsIterable(entities, null))
        .when(this.datastoreTemplate)
        .queryByExample(same(example), any());
    this.spyRepo.findBy(example, FetchableFluentQuery::oneValue);
    verify(this.spyRepo).findOne(same(example));
  }

  @Test
  void findByExampleFluentQuerySortAndFirstValue() {
    Example<Object> example = Example.of(new Object());
    Sort sort = Sort.by("id");
    Iterable entities = Arrays.asList(1);
    doAnswer(invocationOnMock -> new DatastoreResultsIterable(entities, null))
        .when(this.datastoreTemplate)
        .queryByExample(same(example), any());
    this.spyRepo.findBy(example, q -> q.sortBy(sort).firstValue());
    verify(this.spyRepo).findFirstSorted(same(example), same(sort));
    verify(this.datastoreTemplate)
        .queryByExample(
            same(example),
            eq(new DatastoreQueryOptions.Builder().setSort(sort).setLimit(1).build()));
  }

  @Test
  void findByExampleFluentQueryExists() {
    Example<Object> example = Example.of(new Object());
    doAnswer(invocationOnMock -> Arrays.asList())
        .when(this.datastoreTemplate)
        .keyQueryByExample(
            same(example), eq(new DatastoreQueryOptions.Builder().setLimit(1).build()));

    this.spyRepo.findBy(example, FetchableFluentQuery::exists);
    verify(this.spyRepo).exists(same(example));
  }

  @Test
  void findByExampleFluentQueryCount() {
    Example<Object> example = Example.of(new Object());
    doAnswer(invocationOnMock -> Arrays.asList(1, 2, 3))
        .when(this.datastoreTemplate)
        .keyQueryByExample(same(example), isNull());

    this.spyRepo.findBy(example, FetchableFluentQuery::count);
    verify(this.spyRepo).count(same(example));
  }

  @Test
  void findByExampleFluentQueryPage() {
    Example<Object> example = Example.of(new Object());
    Sort sort = Sort.by("id");

    doAnswer(invocationOnMock -> new DatastoreResultsIterable(Arrays.asList(1, 2), null))
        .when(this.datastoreTemplate)
        .queryByExample(
            same(example),
            eq(new DatastoreQueryOptions.Builder().setLimit(2).setOffset(2).setSort(sort).build()));

    doAnswer(invocationOnMock -> new DatastoreResultsIterable(Arrays.asList(1, 2, 3, 4, 5), null))
        .when(this.datastoreTemplate)
        .keyQueryByExample(same(example), isNull());

    PageRequest pageRequest = PageRequest.of(1, 2, sort);
    this.spyRepo.findBy(example, q -> q.page(pageRequest));
    verify(this.spyRepo).findAll(same(example), same(pageRequest));
  }

  @Test
  void findByExampleFluentQueryAsUnsupported() {

    Example<Object> example = Example.of(new Object());

    assertThatThrownBy(() -> this.simpleDatastoreRepository.findBy(example, q -> q.as(Object.class).all()))
            .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void findByExampleFluentQueryProjectUnsupported() {
    Example<Object> example = Example.of(new Object());

    assertThatThrownBy(() -> this.simpleDatastoreRepository.findBy(example, q -> q.project("firstProperty").all()))
            .isInstanceOf(UnsupportedOperationException.class);
  }
}
