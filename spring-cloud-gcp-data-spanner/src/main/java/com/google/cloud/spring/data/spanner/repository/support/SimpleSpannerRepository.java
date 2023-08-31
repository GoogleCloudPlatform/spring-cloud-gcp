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

package com.google.cloud.spring.data.spanner.repository.support;

import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spring.data.spanner.core.SpannerOperations;
import com.google.cloud.spring.data.spanner.core.SpannerPageableQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

/**
 * The default implementation of a SpannerRepository.
 *
 * @param <T> the entity type of the repository
 * @param <I> the id type of the entity
 * @since 1.1
 */
public class SimpleSpannerRepository<T, I> implements SpannerRepository<T, I> {

  private static final String NON_NULL_ID_REQUIRED = "A non-null ID is required.";

  private final SpannerTemplate spannerTemplate;

  private final Class<T> entityType;

  public SimpleSpannerRepository(SpannerTemplate spannerTemplate, Class<T> entityType) {
    Assert.notNull(spannerTemplate, "A valid SpannerTemplate object is required.");
    Assert.notNull(entityType, "A valid entity type is required.");
    this.spannerTemplate = spannerTemplate;
    this.entityType = entityType;
  }

  @Override
  public SpannerOperations getSpannerTemplate() {
    return this.spannerTemplate;
  }

  @Override
  public <A> A performReadOnlyTransaction(Function<SpannerRepository<T, I>, A> operations) {
    return this.spannerTemplate.performReadOnlyTransaction(
        transactionSpannerOperations ->
            operations.apply(
                new SimpleSpannerRepository<>(transactionSpannerOperations, this.entityType)),
        null);
  }

  @Override
  public <A> A performReadWriteTransaction(Function<SpannerRepository<T, I>, A> operations) {
    return this.spannerTemplate.performReadWriteTransaction(
        transactionSpannerOperations ->
            operations.apply(
                new SimpleSpannerRepository<>(transactionSpannerOperations, this.entityType)));
  }

  @Override
  public <S extends T> S save(S entity) {
    Assert.notNull(entity, "A non-null entity is required for saving.");
    this.spannerTemplate.upsert(entity);
    return entity;
  }

  @Override
  public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
    Assert.notNull(entities, "A non-null list of entities is required for saving.");
    this.spannerTemplate.upsertAll(entities);
    return entities;
  }

  @Override
  public Optional<T> findById(I id) {
    Assert.notNull(id, NON_NULL_ID_REQUIRED);
    T result = this.spannerTemplate.read(this.entityType, toKey(id));
    return Optional.<T>ofNullable(result);
  }

  @Override
  public boolean existsById(I id) {
    Assert.notNull(id, NON_NULL_ID_REQUIRED);
    return this.spannerTemplate.existsById(this.entityType, toKey(id));
  }

  @Override
  public Iterable<T> findAll() {
    return this.spannerTemplate.readAll(this.entityType);
  }

  @Override
  public Iterable<T> findAllById(Iterable<I> ids) {
    Assert.notNull(ids, "IDs must not be null");

    KeySet.Builder builder = KeySet.newBuilder();
    int keyCount = 0;

    for (Object id : ids) {
      builder.addKey(toKey(id));
      keyCount++;
    }

    if (keyCount == 0) {
      return Collections.emptyList();
    }

    return this.spannerTemplate.read(this.entityType, builder.build());
  }

  @Override
  public long count() {
    return this.spannerTemplate.count(this.entityType);
  }

  @Override
  public void deleteById(Object id) {
    Assert.notNull(id, NON_NULL_ID_REQUIRED);
    this.spannerTemplate.delete(this.entityType, toKey(id));
  }

  @Override
  public void delete(Object entity) {
    Assert.notNull(entity, "A non-null entity is required.");
    this.spannerTemplate.delete(entity);
  }

  @Override
  public void deleteAll(Iterable<? extends T> entities) {
    Assert.notNull(entities, "A non-null list of entities is required.");
    this.spannerTemplate.deleteAll(entities);
  }

  @Override
  public void deleteAll() {
    this.spannerTemplate.delete(this.entityType, KeySet.all());
  }

  @Override
  public Iterable<T> findAll(Sort sort) {
    return this.spannerTemplate.queryAll(
        this.entityType, new SpannerPageableQueryOptions().setSort(sort));
  }

  @Override
  public Page<T> findAll(Pageable pageable) {
    return new PageImpl<>(
        this.spannerTemplate.queryAll(
            this.entityType,
            new SpannerPageableQueryOptions()
                .setLimit(pageable.getPageSize())
                .setOffset(pageable.getOffset())
                .setSort(pageable.getSort())),
        pageable,
        this.spannerTemplate.count(this.entityType));
  }

  @Override
  public void deleteAllById(Iterable<? extends I> ids) {
    Assert.notNull(ids, "IDs must not be null");
    KeySet.Builder builder = KeySet.newBuilder();
    for (Object id : ids) {
      builder.addKey(toKey(id));
    }
    this.spannerTemplate.delete(this.entityType, builder.build());
  }

  private Key toKey(Object id) {
    return this.spannerTemplate.getSpannerEntityProcessor().convertToKey(id);
  }
}
