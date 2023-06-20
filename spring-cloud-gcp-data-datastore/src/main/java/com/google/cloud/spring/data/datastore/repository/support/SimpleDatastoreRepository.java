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

package com.google.cloud.spring.data.datastore.repository.support;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Key;
import com.google.cloud.spring.data.datastore.core.DatastoreOperations;
import com.google.cloud.spring.data.datastore.core.DatastoreQueryOptions;
import com.google.cloud.spring.data.datastore.core.DatastoreResultsCollection;
import com.google.cloud.spring.data.datastore.core.DatastoreResultsIterable;
import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import com.google.cloud.spring.data.datastore.repository.query.DatastorePageable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Implementation of {@link DatastoreRepository}.
 *
 * @param <T> the type of the entities
 * @param <I> the id type of the entities
 * @since 1.1
 */
public class SimpleDatastoreRepository<T, I> implements DatastoreRepository<T, I> {

  private final DatastoreOperations datastoreTemplate;

  private final Class<T> entityType;

  private static final Log LOGGER = LogFactory.getLog(SimpleDatastoreRepository.class);

  public SimpleDatastoreRepository(DatastoreOperations datastoreTemplate, Class<T> entityType) {
    Assert.notNull(datastoreTemplate, "A non-null DatastoreOperations is required.");
    Assert.notNull(entityType, "A non-null entity type is required.");
    this.datastoreTemplate = datastoreTemplate;
    this.entityType = entityType;
  }

  @Override
  public <A> A performTransaction(Function<DatastoreRepository<T, I>, A> operations) {
    return this.datastoreTemplate.performTransaction(
        template -> operations.apply(new SimpleDatastoreRepository<>(template, this.entityType)));
  }

  @Override
  public Iterable<T> findAll(Sort sort) {
    Assert.notNull(sort, "A non-null Sort is required.");
    return this.datastoreTemplate.findAll(
        this.entityType, new DatastoreQueryOptions.Builder().setSort(sort).build());
  }

  @Override
  public Page<T> findAll(Pageable pageable) {
    Assert.notNull(pageable, "A non-null Pageable is required.");
    Collection<T> entities =
        this.datastoreTemplate.findAll(
            this.entityType,
            new DatastoreQueryOptions.Builder()
                .setLimit(pageable.getPageSize())
                .setOffset((int) pageable.getOffset())
                .setSort(pageable.getSort())
                .setCursor(getCursor(pageable))
                .build());

    Long totalCount =
        getOrComputeTotalCount(pageable, () -> this.datastoreTemplate.count(this.entityType));
    Pageable cursorPageable =
        DatastorePageable.from(
            pageable,
            entities instanceof DatastoreResultsCollection
                ? ((DatastoreResultsCollection) entities).getCursor()
                : null,
            totalCount);

    return new PageImpl<>(
        entities != null ? new ArrayList<>(entities) : Collections.emptyList(),
        cursorPageable,
        totalCount);
  }

  @Override
  public <S extends T> S save(S entity) {
    return this.datastoreTemplate.save(entity);
  }

  @Override
  public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
    return this.datastoreTemplate.saveAll(entities);
  }

  @Override
  public Optional<T> findById(I id) {
    return Optional.ofNullable(this.datastoreTemplate.findById(id, this.entityType));
  }

  @Override
  public boolean existsById(I id) {
    return this.datastoreTemplate.existsById(id, this.entityType);
  }

  @Override
  public Iterable<T> findAll() {
    return this.datastoreTemplate.findAll(this.entityType);
  }

  @Override
  public Iterable<T> findAllById(Iterable<I> ids) {
    return this.datastoreTemplate.findAllById(ids, this.entityType);
  }

  @Override
  public long count() {
    return this.datastoreTemplate.count(this.entityType);
  }

  @Override
  public void deleteById(I id) {
    this.datastoreTemplate.deleteById(id, this.entityType);
  }

  @Override
  public void delete(T entity) {
    this.datastoreTemplate.delete(entity);
  }

  @Override
  public void deleteAll(Iterable<? extends T> entities) {
    this.datastoreTemplate.deleteAll(entities);
  }

  @Override
  public void deleteAll() {
    this.datastoreTemplate.deleteAll(this.entityType);
  }

  @Override
  public <S extends T> Optional<S> findOne(Example<S> example) {
    Iterable<S> entities =
        this.datastoreTemplate.queryByExample(
            example, new DatastoreQueryOptions.Builder().setLimit(1).build());
    Iterator<S> iterator = entities.iterator();
    return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
  }

  <S extends T> S findFirstSorted(Example<S> example, Sort sort) {
    Iterable<S> entities =
        this.datastoreTemplate.queryByExample(
            example, new DatastoreQueryOptions.Builder().setSort(sort).setLimit(1).build());
    Iterator<S> iterator = entities.iterator();
    return iterator.hasNext() ? iterator.next() : null;
  }

  @Override
  public <S extends T> Iterable<S> findAll(Example<S> example) {
    return this.datastoreTemplate.queryByExample(example, null);
  }

  @Override
  public <S extends T> Iterable<S> findAll(Example<S> example, Sort sort) {
    return this.datastoreTemplate.queryByExample(
        example, new DatastoreQueryOptions.Builder().setSort(sort).build());
  }

  @Override
  public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
    Assert.notNull(pageable, "A non-null pageable is required.");

    Iterable<S> entities =
        this.datastoreTemplate.queryByExample(
            example,
            new DatastoreQueryOptions.Builder()
                .setLimit(pageable.getPageSize())
                .setOffset((int) pageable.getOffset())
                .setSort(pageable.getSort())
                .setCursor(getCursor(pageable))
                .build());
    List<S> result =
        StreamSupport.stream(entities.spliterator(), false).collect(Collectors.toList());

    Long totalCount = getOrComputeTotalCount(pageable, () -> count(example));
    Pageable cursorPageable =
        DatastorePageable.from(
            pageable,
            entities instanceof DatastoreResultsIterable
                ? ((DatastoreResultsIterable) entities).getCursor()
                : null,
            totalCount);

    return new PageImpl<>(result, cursorPageable, totalCount);
  }

  @Override
  public <S extends T> long count(Example<S> example) {
    Iterable<Key> keys = this.datastoreTemplate.keyQueryByExample(example, null);

    return StreamSupport.stream(keys.spliterator(), false).count();
  }

  @Override
  public <S extends T> boolean exists(Example<S> example) {
    Iterable<Key> keys =
        this.datastoreTemplate.keyQueryByExample(
            example, new DatastoreQueryOptions.Builder().setLimit(1).build());
    return StreamSupport.stream(keys.spliterator(), false).findAny().isPresent();
  }

  private static Cursor getCursor(Pageable pageable) {
    return pageable instanceof DatastorePageable ? ((DatastorePageable) pageable).toCursor() : null;
  }

  private static Long getOrComputeTotalCount(Pageable pageable, LongSupplier countCall) {
    return pageable instanceof DatastorePageable
        ? ((DatastorePageable) pageable).getTotalCount()
        : countCall.getAsLong();
  }

  @Override
  public void deleteAllById(Iterable<? extends I> iterable) {
    this.datastoreTemplate.deleteAllById(iterable, entityType);
  }

  @Override
  public <S extends T, R> R findBy(
      Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
    Assert.notNull(example, "Example must not be null");
    Assert.notNull(queryFunction, "Query function must not be null");

    return queryFunction.apply(new DatastoreFluentQueryByExample<>(example));
  }

  class DatastoreFluentQueryByExample<S extends T> implements FluentQuery.FetchableFluentQuery<S> {
    private final Example<S> example;

    private final Sort sort;

    DatastoreFluentQueryByExample(Example<S> example) {
      this(example, Sort.unsorted());
    }

    DatastoreFluentQueryByExample(Example<S> example, Sort sort) {
      this.example = example;
      this.sort = sort;
    }

    @NonNull
    @Override
    public FetchableFluentQuery<S> sortBy(@NonNull Sort sort) {
      return new DatastoreFluentQueryByExample<>(this.example, sort);
    }

    @NonNull
    @Override
    public Optional<S> one() {
      return SimpleDatastoreRepository.this.findOne(this.example);
    }

    @Nullable
    @Override
    public S oneValue() {
      Optional<S> one = one();
      return one.orElse(null);
    }

    @Override
    public S firstValue() {
      if (this.sort.isUnsorted()) {
        LOGGER.warn(
            "firstValue() used without sorting. "
                + "Use oneValue() instead if order does not matter.");
      }
      return SimpleDatastoreRepository.this.findFirstSorted(this.example, this.sort);
    }

    @NonNull
    @Override
    public List<S> all() {
      return stream().collect(Collectors.toList());
    }

    @NonNull
    @Override
    public Page<S> page(@NonNull Pageable pageable) {
      return SimpleDatastoreRepository.this.findAll(this.example, pageable);
    }

    @NonNull
    @Override
    public Stream<S> stream() {
      return Streamable.of(SimpleDatastoreRepository.this.findAll(this.example, this.sort))
          .stream();
    }

    @Override
    public long count() {
      return SimpleDatastoreRepository.this.count(this.example);
    }

    @Override
    public boolean exists() {
      return SimpleDatastoreRepository.this.exists(this.example);
    }

    @Override
    public FetchableFluentQuery<S> project(Collection properties) {
      throw new UnsupportedOperationException();
    }

    @Override
    public <V> FetchableFluentQuery<V> as(Class<V> resultType) {
      throw new UnsupportedOperationException();
    }
  }
}
