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

package com.google.cloud.spring.data.spanner.repository.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spring.data.spanner.core.SpannerPageableQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.convert.SpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerDataException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Tests for the standard Spanner repository implementation. */
class SimpleSpannerRepositoryTests {

  private SpannerTemplate template;

  private SpannerEntityProcessor entityProcessor;

  private static final Key A_KEY = Key.of("key");

  @BeforeEach
  void setup() {
    this.template = mock(SpannerTemplate.class);
    this.entityProcessor = mock(SpannerEntityProcessor.class);
    when(this.template.getSpannerEntityProcessor()).thenReturn(this.entityProcessor);
  }

  @Test
  void constructorNullSpannerOperationsTest() {

    assertThatThrownBy(() -> new SimpleSpannerRepository<Object, Key>(null, Object.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A valid SpannerTemplate object is required.");
  }

  @Test
  void constructorNullEntityTypeTest() {

    assertThatThrownBy(() -> new SimpleSpannerRepository<Object, Key>(this.template, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A valid entity type is required.");
  }

  @Test
  void getSpannerOperationsTest() {
    assertThat(this.template)
        .isSameAs(
            new SimpleSpannerRepository<Object, Key>(this.template, Object.class)
                .getSpannerTemplate());
  }

  @Test
  void saveNullObjectTest() {
    SimpleSpannerRepository spannerRepository = new SimpleSpannerRepository<Object, Key>(this.template, Object.class);

    assertThatThrownBy(() -> spannerRepository.save(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A non-null entity is required for saving.");
  }

  @Test
  void findNullIdTest() {
    SimpleSpannerRepository spannerRepository = new SimpleSpannerRepository<Object, Key>(this.template, Object.class);

    assertThatThrownBy(() -> spannerRepository.findById(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A non-null ID is required.");


  }

  @Test
  void existsNullIdTest() {

    SimpleSpannerRepository spannerRepository = new SimpleSpannerRepository<Object, Key>(this.template, Object.class);

    assertThatThrownBy(() -> spannerRepository.existsById(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A non-null ID is required.");

  }

  @Test
  void deleteNullIdTest() {

    SimpleSpannerRepository spannerRepository = new SimpleSpannerRepository<Object, Key>(this.template, Object.class);

    assertThatThrownBy(() -> spannerRepository.deleteById(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A non-null ID is required.");
  }

  @Test
  void deleteNullEntityTest() {

    SimpleSpannerRepository spannerRepository = new SimpleSpannerRepository<Object, Key>(this.template, Object.class);

    assertThatThrownBy(() -> spannerRepository.delete(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A non-null entity is required.");

  }

  @Test
  void deleteAllNullEntityTest() {

    SimpleSpannerRepository spannerRepository = new SimpleSpannerRepository<Object, Key>(this.template, Object.class);

    assertThatThrownBy(() -> spannerRepository.deleteAll(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A non-null list of entities is required.");
  }

  @Test
  void saveAllNullEntityTest() {

    SimpleSpannerRepository spannerRepository = new SimpleSpannerRepository<Object, Key>(this.template, Object.class);

    assertThatThrownBy(() -> spannerRepository.saveAll(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("A non-null list of entities is required for saving.");
  }

  @Test
  void saveTest() {
    Object ob = new Object();
    assertThat(new SimpleSpannerRepository<Object, Key>(this.template, Object.class).save(ob))
        .isEqualTo(ob);
    verify(this.template, times(1)).upsert(ob);
  }

  @Test
  void saveAllTest() {
    Object ob = new Object();
    Object ob2 = new Object();
    Iterable<Object> ret =
        new SimpleSpannerRepository<Object, Key>(this.template, Object.class)
            .saveAll(Arrays.asList(ob, ob2));
    assertThat(ret).containsExactlyInAnyOrder(ob, ob2);
    verify(this.template, times(1)).upsertAll(Arrays.asList(ob, ob2));
  }

  @Test
  void findByIdTest() {
    Object ret = new Object();
    when(this.entityProcessor.convertToKey(A_KEY)).thenReturn(A_KEY);
    when(this.template.read(Object.class, A_KEY)).thenReturn(ret);
    assertThat(
            new SimpleSpannerRepository<Object, Key>(this.template, Object.class).findById(A_KEY))
        .contains(ret);
    verify(this.template, times(1)).read(Object.class, A_KEY);
  }

  @Test
  void findByIdKeyWritingThrowsAnException() {
    when(this.entityProcessor.convertToKey(any())).thenThrow(SpannerDataException.class);

    SimpleSpannerRepository spannerRepository = new SimpleSpannerRepository<Object, Object[]>(this.template, Object.class);

    assertThatThrownBy(() -> spannerRepository.findById(new Object[] {}))
            .isInstanceOf(SpannerDataException.class);
  }


  @Test
  void existsByIdTestFound() {
    when(this.entityProcessor.convertToKey(A_KEY)).thenReturn(A_KEY);
    when(this.template.existsById(Object.class, A_KEY)).thenReturn(true);
    assertThat(
            new SimpleSpannerRepository<Object, Key>(this.template, Object.class).existsById(A_KEY))
        .isTrue();
  }

  @Test
  void existsByIdTestNotFound() {
    when(this.entityProcessor.convertToKey(A_KEY)).thenReturn(A_KEY);
    when(this.template.existsById(Object.class, A_KEY)).thenReturn(false);
    assertThat(
            new SimpleSpannerRepository<Object, Key>(this.template, Object.class).existsById(A_KEY))
        .isFalse();
  }

  @Test
  void findAllTest() {
    new SimpleSpannerRepository<Object, Key>(this.template, Object.class).findAll();
    verify(this.template, times(1)).readAll(Object.class);
  }

  @Test
  void findAllSortTest() {
    Sort sort = mock(Sort.class);
    when(this.template.queryAll(eq(Object.class), any()))
        .thenAnswer(
            invocation -> {
              SpannerPageableQueryOptions spannerQueryOptions = invocation.getArgument(1);
              assertThat(spannerQueryOptions.getSort()).isSameAs(sort);
              return null;
            });
    new SimpleSpannerRepository<Object, Key>(this.template, Object.class).findAll(sort);
    verify(this.template, times(1)).queryAll(eq(Object.class), any());
  }

  @Test
  void findAllPageableTest() {
    Pageable pageable = mock(Pageable.class);
    Sort sort = mock(Sort.class);
    when(pageable.getSort()).thenReturn(sort);
    when(pageable.getOffset()).thenReturn(3L);
    when(pageable.getPageSize()).thenReturn(5);
    when(this.template.queryAll(eq(Object.class), any()))
        .thenAnswer(
            invocation -> {
              SpannerPageableQueryOptions spannerQueryOptions = invocation.getArgument(1);
              assertThat(spannerQueryOptions.getSort()).isSameAs(sort);
              assertThat(spannerQueryOptions.getOffset()).isEqualTo(3);
              assertThat(spannerQueryOptions.getLimit()).isEqualTo(5);
              return new ArrayList<>();
            });
    new SimpleSpannerRepository<Object, Key>(this.template, Object.class).findAll(pageable);
    verify(this.template, times(1)).queryAll(eq(Object.class), any());
  }

  @Test
  void findAllByIdTest() {
    List<Key> unconvertedKey = Arrays.asList(Key.of("key1"), Key.of("key2"));

    when(this.entityProcessor.convertToKey(Key.of("key1"))).thenReturn(Key.of("key1"));
    when(this.entityProcessor.convertToKey(Key.of("key2"))).thenReturn(Key.of("key2"));
    when(this.template.read(eq(Object.class), (KeySet) any()))
        .thenAnswer(
            invocation -> {
              KeySet keys = invocation.getArgument(1);
              assertThat(keys.getKeys()).containsExactlyInAnyOrder(Key.of("key2"), Key.of("key1"));
              return null;
            });

    new SimpleSpannerRepository<Object, Key>(this.template, Object.class)
        .findAllById(unconvertedKey);
  }

  @Test
  void findAllById_failsOnNull() {
    SimpleSpannerRepository<Object, Key> repo =
        new SimpleSpannerRepository<>(this.template, Object.class);
    assertThatThrownBy(() -> repo.findAllById(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("IDs must not be null");
    verifyNoInteractions(this.template);
  }

  @Test
  void findAllById_shortcutsToEmptyReturn() {
    SimpleSpannerRepository<Object, Key> repo =
        new SimpleSpannerRepository<>(this.template, Object.class);
    assertThat(repo.findAllById(new ArrayList<>()))
        .isEmpty();
    verifyNoInteractions(this.template);
  }

  @Test
  void countTest() {
    new SimpleSpannerRepository<Object, Key>(this.template, Object.class).count();
    verify(this.template, times(1)).count(Object.class);
  }

  @Test
  void deleteByIdTest() {
    when(this.entityProcessor.convertToKey(A_KEY)).thenReturn(A_KEY);
    new SimpleSpannerRepository<Object, Key>(this.template, Object.class).deleteById(A_KEY);
    verify(this.template, times(1)).delete(Object.class, A_KEY);
    verify(this.template, times(1)).delete(Object.class, A_KEY);
  }

  @Test
  void deleteAllByIdTest() {
    List<String> unconvertedKey = Arrays.asList("key1", "key2");

    when(this.entityProcessor.convertToKey("key1")).thenReturn(Key.of("key1"));
    when(this.entityProcessor.convertToKey("key2")).thenReturn(Key.of("key2"));

    new SimpleSpannerRepository<Object, String>(this.template, Object.class)
        .deleteAllById(unconvertedKey);
    ArgumentCaptor<KeySet> argumentCaptor = ArgumentCaptor.forClass(KeySet.class);
    verify(this.template).delete(eq(Object.class), argumentCaptor.capture());
    assertThat(argumentCaptor.getValue().getKeys())
        .containsExactlyInAnyOrder(Key.of("key2"), Key.of("key1"));
  }

  @Test
  void deleteAllById_failsOnNull() {
    SimpleSpannerRepository<Object, Key> repo =
        new SimpleSpannerRepository<>(this.template, Object.class);
    assertThatThrownBy(() -> repo.deleteAllById(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("IDs must not be null");
  }

  @Test
  void deleteTest() {
    Object ob = new Object();
    new SimpleSpannerRepository<Object, Key>(this.template, Object.class).delete(ob);
    verify(this.template, times(1)).delete(ob);
  }

  @Test
  void deleteAllTest() {
    new SimpleSpannerRepository<Object, Key>(this.template, Object.class).deleteAll();
    verify(this.template, times(1)).delete(Object.class, KeySet.all());
  }

  @Test
  void readOnlyTransactionTest() {
    when(this.template.performReadOnlyTransaction(any(), any()))
        .thenAnswer(
            invocation -> {
              Function<SpannerTemplate, String> f = invocation.getArgument(0);
              return f.apply(this.template);
            });

    Object object =
        new SimpleSpannerRepository<Object, Key>(this.template, Object.class)
            .performReadOnlyTransaction(repo -> "test");
    assertThat(object).isEqualTo("test");
  }

  @Test
  void readWriteTransactionTest() {
    when(this.template.performReadWriteTransaction(any()))
        .thenAnswer(
            invocation -> {
              Function<SpannerTemplate, String> f = invocation.getArgument(0);
              return f.apply(this.template);
            });

    Object object =
        new SimpleSpannerRepository<Object, Key>(this.template, Object.class)
            .performReadWriteTransaction(repo -> "test");
    assertThat(object).isEqualTo("test");
  }
}
