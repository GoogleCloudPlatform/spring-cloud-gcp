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

package com.google.cloud.spring.data.spanner.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Mutation.Op;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import com.google.cloud.spring.data.spanner.core.convert.ConversionUtils;
import com.google.cloud.spring.data.spanner.core.convert.SpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.Embedded;
import com.google.cloud.spring.data.spanner.core.mapping.Interleaved;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerDataException;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for the Spanner mutation factory implementation. */
class SpannerMutationFactoryImplTests {

  private SpannerMappingContext mappingContext;

  private SpannerEntityProcessor objectMapper;

  private SpannerSchemaUtils spannerSchemaUtils;

  private SpannerMutationFactoryImpl spannerMutationFactory;

  @BeforeEach
  void setUp() {
    this.mappingContext = new SpannerMappingContext();
    this.objectMapper = mock(SpannerEntityProcessor.class);
    this.spannerSchemaUtils = new SpannerSchemaUtils(this.mappingContext, this.objectMapper, true);
    this.spannerMutationFactory =
        new SpannerMutationFactoryImpl(
            this.objectMapper, this.mappingContext, this.spannerSchemaUtils);
  }

  private void executeWriteTest(Function<TestEntity, List<Mutation>> writeFunc, Op writeMethod) {
    TestEntity t = new TestEntity();
    List<Mutation> mutations = writeFunc.apply(t);
    t.id = "a";
    Mutation parentMutation = mutations.get(0);
    assertThat(mutations).hasSize(1);
    assertThat(parentMutation.getTable()).isEqualTo("custom_test_table");
    assertThat(parentMutation.getOperation()).isEqualTo(writeMethod);
    ChildEntity c1 = new ChildEntity();
    c1.keyComponents = new EmbeddedKeyComponents();
    c1.keyComponents.id = t.id;
    c1.keyComponents.id2 = "c2";
    ChildEntity c2 = new ChildEntity();
    c2.keyComponents = new EmbeddedKeyComponents();
    c2.keyComponents.id = t.id;
    c2.keyComponents.id2 = "c3";
    t.childEntities = Arrays.asList(c1, c2);
    mutations = writeFunc.apply(t);
    parentMutation = mutations.get(0);
    assertThat(mutations).hasSize(3);
    List<Mutation> childMutations = mutations.subList(1, mutations.size());
    assertThat(parentMutation.getTable()).isEqualTo("custom_test_table");
    assertThat(parentMutation.getOperation()).isEqualTo(writeMethod);
    for (Mutation childMutation : childMutations) {
      assertThat(childMutation.getTable()).isEqualTo("child_test_table");
      assertThat(childMutation.getOperation()).isEqualTo(writeMethod);
    }
  }

  @Test
  @SuppressWarnings("ReturnValueIgnored")
  void lazyWriteTest() {
    TestEntity t = new TestEntity();
    t.id = "a";
    ChildEntity c1 = new ChildEntity();
    c1.keyComponents = new EmbeddedKeyComponents();
    c1.keyComponents.id = t.id;
    c1.keyComponents.id2 = "c2";
    ChildEntity c2 = new ChildEntity();
    c2.keyComponents = new EmbeddedKeyComponents();
    c2.keyComponents.id = t.id;
    c2.keyComponents.id2 = "c3";

    // intentionally setting it as an untouched-proxy
    t.childEntities = ConversionUtils.wrapSimpleLazyProxy(() -> Arrays.asList(c1, c2), List.class);

    List<Mutation> mutations = this.spannerMutationFactory.upsert(t, null);
    Mutation parentMutation = mutations.get(0);

    // The size is 1 because the child is an un-evaluated proxy.
    assertThat(mutations).hasSize(1);
    assertThat(parentMutation.getTable()).isEqualTo("custom_test_table");
    assertThat(parentMutation.getOperation()).isEqualTo(Op.INSERT_OR_UPDATE);

    t.childEntities.size();

    // mutations should now be generated since the child proxy has been touched.
    mutations = this.spannerMutationFactory.upsert(t, null);
    parentMutation = mutations.get(0);
    assertThat(mutations).hasSize(3);
    List<Mutation> childMutations = mutations.subList(1, mutations.size());
    assertThat(parentMutation.getTable()).isEqualTo("custom_test_table");
    assertThat(parentMutation.getOperation()).isEqualTo(Op.INSERT_OR_UPDATE);
    for (Mutation childMutation : childMutations) {
      assertThat(childMutation.getTable()).isEqualTo("child_test_table");
      assertThat(childMutation.getOperation()).isEqualTo(Op.INSERT_OR_UPDATE);
    }
  }

  @Test
  void insertTest() {
    executeWriteTest(t -> this.spannerMutationFactory.insert(t), Op.INSERT);
  }

  @Test
  void insertChildrenMismatchIdTest() {

    TestEntity t = new TestEntity();
    t.id = "a";
    ChildEntity c1 = new ChildEntity();
    c1.keyComponents = new EmbeddedKeyComponents();
    c1.keyComponents.id = "b";
    c1.keyComponents.id2 = "c1";
    t.childEntities = Collections.singletonList(c1);
    // throws exception because child entity's id column does not match that of its
    // parent.
    assertThatThrownBy(() -> this.spannerMutationFactory.insert(t))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("A child entity's common primary key parts with its parent must have the same values."
                    + " Primary key component 1 does not match for entities: class"
                    + " com.google.cloud.spring.data.spanner.core.SpannerMutationFactoryImplTests$TestEntity"
                    + " class"
                    + " com.google.cloud.spring.data.spanner.core.SpannerMutationFactoryImplTests$ChildEntity");
  }

  @Test
  void updateTest() {
    executeWriteTest(t -> this.spannerMutationFactory.update(t, null), Op.UPDATE);
  }

  @Test
  void upsertTest() {
    executeWriteTest(t -> this.spannerMutationFactory.upsert(t, null), Op.INSERT_OR_UPDATE);
  }

  @Test
  void deleteEntitiesTest() {
    TestEntity t1 = new TestEntity();
    t1.id = "key1";
    TestEntity t2 = new TestEntity();
    t2.id = "key2";

    Mutation mutation = this.spannerMutationFactory.delete(TestEntity.class, Arrays.asList(t1, t2));
    assertThat(mutation.getTable()).isEqualTo("custom_test_table");
    assertThat(mutation.getOperation()).isEqualTo(Op.DELETE);
    List<String> keys = new ArrayList<>();
    mutation
        .getKeySet()
        .getKeys()
        .forEach(
            key -> {
              keys.add((String) (key.getParts().iterator().next()));
            });
    assertThat(keys).containsExactlyInAnyOrder(t1.id, t2.id);
  }

  @Test
  void deleteEntityTest() {
    TestEntity t1 = new TestEntity();
    t1.id = "key1";

    Mutation mutation = this.spannerMutationFactory.delete(t1);
    assertThat(mutation.getTable()).isEqualTo("custom_test_table");
    assertThat(mutation.getOperation()).isEqualTo(Op.DELETE);
    List<String> keys = new ArrayList<>();
    mutation
        .getKeySet()
        .getKeys()
        .forEach(
            key -> {
              keys.add((String) (key.getParts().iterator().next()));
            });
    assertThat(keys).containsExactlyInAnyOrder(t1.id);
  }

  @Test
  void deleteKeysTest() {
    KeySet keySet = KeySet.newBuilder().addKey(Key.of("key1")).addKey(Key.of("key2")).build();
    Mutation mutation = this.spannerMutationFactory.delete(TestEntity.class, keySet);
    assertThat(mutation.getTable()).isEqualTo("custom_test_table");
    assertThat(mutation.getOperation()).isEqualTo(Op.DELETE);
    List<String> keys = new ArrayList<>();
    mutation
        .getKeySet()
        .getKeys()
        .forEach(
            key -> {
              keys.add((String) (key.getParts().iterator().next()));
            });
    assertThat(keys).containsExactlyInAnyOrder("key1", "key2");
  }

  @Test
  void deleteKeyTest() {
    Key key = Key.of("key1");
    Mutation mutation = this.spannerMutationFactory.delete(TestEntity.class, key);
    assertThat(mutation.getTable()).isEqualTo("custom_test_table");
    assertThat(mutation.getOperation()).isEqualTo(Op.DELETE);
    List<String> keys = new ArrayList<>();
    mutation
        .getKeySet()
        .getKeys()
        .forEach(
            k -> {
              keys.add((String) (k.getParts().iterator().next()));
            });
    assertThat(keys).containsExactlyInAnyOrder("key1");
  }

  @Table(name = "custom_test_table")
  private static class TestEntity {
    @PrimaryKey(keyOrder = 1)
    String id;

    @Column(name = "custom_col")
    String something;

    @Column(name = "")
    String other;

    @Interleaved List<ChildEntity> childEntities;
  }

  @Table(name = "child_test_table")
  private static class ChildEntity {
    @Embedded
    @PrimaryKey(keyOrder = 1)
    EmbeddedKeyComponents keyComponents;
  }

  private static class EmbeddedKeyComponents {
    @PrimaryKey(keyOrder = 1)
    String id;

    @PrimaryKey(keyOrder = 2)
    String id2;
  }
}
