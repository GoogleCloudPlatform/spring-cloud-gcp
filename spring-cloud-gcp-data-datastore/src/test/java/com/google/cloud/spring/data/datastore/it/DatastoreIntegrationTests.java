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

package com.google.cloud.spring.data.datastore.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.DatastoreReaderWriter;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.spring.data.datastore.aot.TestRuntimeHints;
import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreDataException;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.core.mapping.DatastorePersistentEntity;
import com.google.cloud.spring.data.datastore.entities.CustomMap;
import com.google.cloud.spring.data.datastore.it.testdomains.AncestorEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.Cat;
import com.google.cloud.spring.data.datastore.it.testdomains.CommunicationChannels;
import com.google.cloud.spring.data.datastore.it.testdomains.Company;
import com.google.cloud.spring.data.datastore.it.testdomains.CompanyWithBooleanPrimitive;
import com.google.cloud.spring.data.datastore.it.testdomains.Dog;
import com.google.cloud.spring.data.datastore.it.testdomains.DogRepository;
import com.google.cloud.spring.data.datastore.it.testdomains.EmbeddableTreeNode;
import com.google.cloud.spring.data.datastore.it.testdomains.EmbeddedEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.Employee;
import com.google.cloud.spring.data.datastore.it.testdomains.Event;
import com.google.cloud.spring.data.datastore.it.testdomains.LazyEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.ParentEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.Pet;
import com.google.cloud.spring.data.datastore.it.testdomains.PetOwner;
import com.google.cloud.spring.data.datastore.it.testdomains.PetRepository;
import com.google.cloud.spring.data.datastore.it.testdomains.Product;
import com.google.cloud.spring.data.datastore.it.testdomains.ProductRepository;
import com.google.cloud.spring.data.datastore.it.testdomains.Pug;
import com.google.cloud.spring.data.datastore.it.testdomains.ReferenceEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.ReferenceLazyEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.ServiceConfiguration;
import com.google.cloud.spring.data.datastore.it.testdomains.Store;
import com.google.cloud.spring.data.datastore.it.testdomains.SubEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.TestEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.TestEntity.Shape;
import com.google.cloud.spring.data.datastore.it.testdomains.TestEntityProjection;
import com.google.cloud.spring.data.datastore.it.testdomains.TestEntityRepository;
import com.google.cloud.spring.data.datastore.it.testdomains.TreeCollection;
import com.google.cloud.spring.data.datastore.repository.support.SimpleDatastoreRepository;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AopTestUtils;
import org.springframework.transaction.TransactionSystemException;

/** Integration tests for Datastore that use many features. */
@EnabledIfSystemProperty(named = "it.datastore", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DatastoreIntegrationTestConfiguration.class})
@ImportRuntimeHints({TestRuntimeHints.class})
@DisabledInAotMode
class DatastoreIntegrationTests extends AbstractDatastoreIntegrationTests {

  // This value is multiplied against recorded actual times needed to wait for eventual
  // consistency.
  private static final int WAIT_FOR_EVENTUAL_CONSISTENCY_SAFETY_MULTIPLE = 3;

  @Autowired private ProductRepository productRepository;

  @Autowired private TestEntityRepository testEntityRepository;

  @Autowired private PetRepository petRepository;

  @Autowired private DogRepository dogRepository;

  @Autowired private DatastoreTemplate datastoreTemplate;

  @Autowired private DatastoreMappingContext mappingContext;

  @Autowired private TransactionalTemplateService transactionalTemplateService;

  @Autowired private DatastoreReaderWriter datastore;

  private Key keyForMap;

  private final TestEntity testEntityA = new TestEntity(1L, "red", 1L, Shape.CIRCLE, null);

  private final TestEntity testEntityB = new TestEntity(2L, "blue", 2L, Shape.CIRCLE, null);

  private final TestEntity testEntityC =
      new TestEntity(3L, "red", 1L, Shape.CIRCLE, null, new EmbeddedEntity("c"));

  private final TestEntity testEntityD =
      new TestEntity(4L, "red", 1L, Shape.SQUARE, null, new EmbeddedEntity("d"));

  private final List<TestEntity> allTestEntities =
      Arrays.asList(this.testEntityA, this.testEntityB, this.testEntityC, this.testEntityD);

  @AfterEach
  void deleteAll() {
    this.datastoreTemplate.deleteAll(EmbeddableTreeNode.class);
    this.datastoreTemplate.deleteAll(AncestorEntity.class);
    this.datastoreTemplate.deleteAll(AncestorEntity.DescendantEntry.class);
    this.datastoreTemplate.deleteAll(TreeCollection.class);
    this.datastoreTemplate.deleteAll(ReferenceEntity.class);
    this.datastoreTemplate.deleteAll(ReferenceLazyEntity.class);
    this.datastoreTemplate.deleteAll(ParentEntity.class);
    this.datastoreTemplate.deleteAll(SubEntity.class);
    this.datastoreTemplate.deleteAll(Pet.class);
    this.datastoreTemplate.deleteAll(PetOwner.class);
    this.datastoreTemplate.deleteAll(Event.class);
    this.datastoreTemplate.deleteAll(LazyEntity.class);
    this.datastoreTemplate.deleteAll(Product.class);
    this.datastoreTemplate.deleteAll(Store.class);
    this.datastoreTemplate.deleteAll(Company.class);
    this.datastoreTemplate.deleteAll(Employee.class);
    this.datastoreTemplate.deleteAll(ServiceConfiguration.class);
    this.testEntityRepository.deleteAll();
    if (this.keyForMap != null) {
      this.datastore.delete(this.keyForMap);
    }
  }

  @BeforeEach
  void saveEntities() {
    this.testEntityRepository.saveAll(this.allTestEntities);
    await()
        .atMost(20, TimeUnit.SECONDS)
        .untilAsserted(() -> assertThat(this.testEntityRepository.countBySize(1L)).isEqualTo(3));
  }

  @Test
  void testFindByExampleReference() {
    Store store1 = new Store("store1");
    Product product1 = new Product(store1);

    productRepository.save(product1);

    Store store2 = new Store("store2");
    Product product2 = new Product(store2);

    productRepository.save(product2);

    Pageable pageable = PageRequest.of(0, 3);
    Product product = new Product(store1);
    Example<Product> example = Example.of(product);
    Page<Product> pagedProduct = this.productRepository.findAll(example, pageable);

    assertThat(pagedProduct).containsOnly(product1);

    product = new Product(null);
    example = Example.of(product);
    pagedProduct = this.productRepository.findAll(example, pageable);

    assertThat(pagedProduct).containsExactlyInAnyOrder(product1, product2);

    product = new Product(null);
    example =
        Example.of(
            product, ExampleMatcher.matching().withIgnorePaths("id").withIncludeNullValues());
    pagedProduct = this.productRepository.findAll(example, pageable);

    assertThat(pagedProduct).isEmpty();
  }

  @Test
  void testFindByExample() {
    assertThat(
            this.testEntityRepository.findAll(
                Example.of(new TestEntity(null, "red", null, Shape.CIRCLE, null))))
        .containsExactlyInAnyOrder(this.testEntityA, this.testEntityC);

    assertThat(
            this.testEntityRepository.findAll(
                Example.of(new TestEntity(2L, "blue", null, null, null))))
        .containsExactly(this.testEntityB);

    assertThat(
            this.testEntityRepository.findAll(
                Example.of(new TestEntity(2L, "red", null, null, null))))
        .isEmpty();

    Page<TestEntity> result1 =
        this.testEntityRepository.findAll(
            Example.of(new TestEntity(null, null, null, null, null)),
            PageRequest.of(0, 2, Sort.by("size")));
    assertThat(result1.getTotalElements()).isEqualTo(4);
    assertThat(result1.getNumber()).isZero();
    assertThat(result1.getNumberOfElements()).isEqualTo(2);
    assertThat(result1.getTotalPages()).isEqualTo(2);
    assertThat(result1.hasNext()).isTrue();
    assertThat(result1).containsExactly(this.testEntityA, this.testEntityC);

    Page<TestEntity> result2 =
        this.testEntityRepository.findAll(
            Example.of(new TestEntity(null, null, null, null, null)), result1.getPageable().next());
    assertThat(result2.getTotalElements()).isEqualTo(4);
    assertThat(result2.getNumber()).isEqualTo(1);
    assertThat(result2.getNumberOfElements()).isEqualTo(2);
    assertThat(result2.getTotalPages()).isEqualTo(2);
    assertThat(result2.hasNext()).isFalse();
    assertThat(result2).containsExactly(this.testEntityD, this.testEntityB);

    assertThat(
            this.testEntityRepository.findAll(
                Example.of(new TestEntity(null, null, null, null, null)),
                Sort.by(Sort.Direction.ASC, "id")))
        .containsExactly(this.testEntityA, this.testEntityB, this.testEntityC, this.testEntityD);

    assertThat(
            this.testEntityRepository.count(
                Example.of(
                    new TestEntity(null, "red", null, Shape.CIRCLE, null),
                    ExampleMatcher.matching().withIgnorePaths("size", "blobField"))))
        .isEqualTo(2);

    assertThat(
            this.testEntityRepository.exists(
                Example.of(
                    new TestEntity(null, "red", null, Shape.CIRCLE, null),
                    ExampleMatcher.matching().withIgnorePaths("size", "blobField"))))
        .isTrue();

    assertThat(
            this.testEntityRepository.exists(
                Example.of(
                    new TestEntity(null, "red", null, null, null),
                    ExampleMatcher.matching().withIgnorePaths("id").withIncludeNullValues())))
        .isFalse();

    assertThat(
            this.testEntityRepository.exists(
                Example.of(new TestEntity(null, "red", null, null, null))))
        .isTrue();
  }

  @Test
  void testSlice() {
    Slice<TestEntity> slice =
        this.testEntityRepository.findEntitiesWithCustomQuerySlice("red", PageRequest.of(0, 1));

    assertThat(slice.hasNext()).isTrue();
    assertThat(slice).hasSize(1);
    List<TestEntity> results = new ArrayList<>(slice.getContent());

    slice =
        this.testEntityRepository.findEntitiesWithCustomQuerySlice(
            "red", slice.getPageable().next());

    assertThat(slice.hasNext()).isTrue();
    assertThat(slice).hasSize(1);
    results.addAll(slice.getContent());

    slice =
        this.testEntityRepository.findEntitiesWithCustomQuerySlice(
            "red", slice.getPageable().next());

    assertThat(slice.hasNext()).isFalse();
    assertThat(slice).hasSize(1);
    results.addAll(slice.getContent());

    assertThat(results)
        .containsExactlyInAnyOrder(this.testEntityA, this.testEntityC, this.testEntityD);
  }

  @Test
  void testNextPageAwareQuery() {
    DatastorePersistentEntity<?> persistentEntity =
        this.mappingContext.getPersistentEntity(TestEntity.class);

    EntityQuery query =
        StructuredQuery.newEntityQueryBuilder()
            .setKind(persistentEntity.kindName())
            .setFilter(PropertyFilter.eq("color", "red"))
            .setLimit(2)
            .build();

    Slice<TestEntity> results =
        this.datastoreTemplate.queryEntitiesSlice(query, TestEntity.class, PageRequest.of(0, 2));

    List<TestEntity> testEntities = new ArrayList<>();

    testEntities.addAll(results.toList());
    assertThat(results.hasNext()).isTrue();

    results =
        this.datastoreTemplate.queryEntitiesSlice(query, TestEntity.class, results.nextPageable());

    testEntities.addAll(results.toList());

    assertThat(results.hasNext()).isFalse();
    assertThat(testEntities).contains(testEntityA, testEntityC, testEntityD);
  }

  @Test
  void testPage() {
    Page<TestEntity> page =
        this.testEntityRepository.findEntitiesWithCustomQueryPage("red", PageRequest.of(0, 2));

    assertThat(page.hasNext()).isTrue();
    assertThat(page).hasSize(2);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getTotalElements()).isEqualTo(3);
    List<TestEntity> results = new ArrayList<>(page.getContent());

    page =
        this.testEntityRepository.findEntitiesWithCustomQueryPage("red", page.getPageable().next());

    assertThat(page.hasNext()).isFalse();
    assertThat(page).hasSize(1);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getTotalElements()).isEqualTo(3);
    results.addAll(page.getContent());

    assertThat(results)
        .containsExactlyInAnyOrder(this.testEntityA, this.testEntityC, this.testEntityD);
  }

  @Test
  void testProjectionPage() {
    Page<String> page =
        this.testEntityRepository.getColorsPage(
            PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "color")));

    assertThat(page.hasNext()).isTrue();
    assertThat(page).hasSize(3);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getTotalElements()).isEqualTo(4);
    assertThat(page.getContent()).containsExactly("red", "red", "red");

    page = this.testEntityRepository.getColorsPage(page.getPageable().next());

    assertThat(page.hasNext()).isFalse();
    assertThat(page).hasSize(1);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getTotalElements()).isEqualTo(4);
    assertThat(page.getContent()).containsExactly("blue");
  }

  @Test
  void testSliceSort() {
    List<TestEntity> results =
        this.testEntityRepository.findEntitiesWithCustomQuerySort(Sort.by("color"));

    assertThat(results.get(0)).isEqualTo(this.testEntityB);
    assertThat(results)
        .containsExactlyInAnyOrder(
            this.testEntityA, this.testEntityB, this.testEntityC, this.testEntityD);
  }

  @Test
  void testSliceSortDesc() {
    List<TestEntity> results =
        this.testEntityRepository.findEntitiesWithCustomQuerySort(
            Sort.by(Sort.Direction.DESC, "color"));

    assertThat(results.get(results.size() - 1)).isEqualTo(this.testEntityB);
    assertThat(results)
        .containsExactlyInAnyOrder(
            this.testEntityA, this.testEntityB, this.testEntityC, this.testEntityD);
  }

  @Test
  void testFinds() {
    assertThat(this.testEntityRepository.findByEmbeddedEntityStringField("c"))
        .containsExactly(this.testEntityC);
    assertThat(this.testEntityRepository.findByEmbeddedEntityStringField("d"))
        .containsExactly(this.testEntityD);

    assertThat(this.testEntityRepository.findFirstByColor("blue")).contains(this.testEntityB);
    assertThat(this.testEntityRepository.findFirstByColor("green")).isNotPresent();

    assertThatThrownBy(() -> this.testEntityRepository.findByColor("green"))
        .isInstanceOf(EmptyResultDataAccessException.class)
        .hasMessageMatching("Result must not be null");

    assertThat(
            this.testEntityRepository.findByShape(Shape.SQUARE).stream()
                .map(TestEntity::getId)
                .collect(Collectors.toList()))
        .contains(4L);

    Slice<TestEntity> red1 = this.testEntityRepository.findByColor("red", PageRequest.of(0, 1));
    assertThat(red1.hasNext()).isTrue();
    assertThat(red1.getNumber()).isZero();
    Slice<TestEntity> red2 =
        this.testEntityRepository.findByColor("red", red1.getPageable().next());
    assertThat(red2.hasNext()).isTrue();
    assertThat(red2.getNumber()).isEqualTo(1);
    Slice<TestEntity> red3 =
        this.testEntityRepository.findByColor("red", red2.getPageable().next());
    assertThat(red3.hasNext()).isFalse();
    assertThat(red3.getNumber()).isEqualTo(2);

    assertThat(this.testEntityRepository.findByColor("red", PageRequest.of(1, 1)).hasNext())
        .isTrue();
    assertThat(this.testEntityRepository.findByColor("red", PageRequest.of(2, 1)).hasNext())
        .isFalse();

    Page<TestEntity> circles =
        this.testEntityRepository.findByShape(Shape.CIRCLE, PageRequest.of(0, 2));
    assertThat(circles.getTotalElements()).isEqualTo(3L);
    assertThat(circles.getTotalPages()).isEqualTo(2);
    assertThat(circles.get().count()).isEqualTo(2L);
    assertThat(circles.get().allMatch(e -> e.getShape().equals(Shape.CIRCLE))).isTrue();

    Page<TestEntity> circlesNext =
        this.testEntityRepository.findByShape(Shape.CIRCLE, circles.nextPageable());
    assertThat(circlesNext.getTotalElements()).isEqualTo(3L);
    assertThat(circlesNext.getTotalPages()).isEqualTo(2);
    assertThat(circlesNext.get().count()).isEqualTo(1L);
    assertThat(circlesNext.get().allMatch(e -> e.getShape().equals(Shape.CIRCLE))).isTrue();

    assertThat(
            this.testEntityRepository.findByEnumQueryParam(Shape.SQUARE).stream()
                .map(TestEntity::getId)
                .collect(Collectors.toList()))
        .contains(4L);
  }

  @Test
  void testGets() {
    assertThat(this.testEntityRepository.getByColor("green")).isNull();
    assertThat(this.testEntityRepository.getByColor("blue")).isEqualTo(this.testEntityB);
    assertThat(this.testEntityRepository.getByColorAndIdGreaterThanEqualOrderById("red", 3L))
        .containsExactly(this.testEntityC, this.testEntityD);

    assertThat(
            this.testEntityRepository.getKeys().stream()
                .map(Key::getId)
                .collect(Collectors.toList()))
        .containsExactlyInAnyOrder(1L, 2L, 3L, 4L);

    assertThat(this.testEntityRepository.getKey().getId()).isEqualTo((Long) 1L);
    assertThat(this.testEntityRepository.getSizes(1L)).hasSize(3);
    assertThat(this.testEntityRepository.getOneSize(2L)).isEqualTo(2);
    assertThat(this.testEntityRepository.getOneTestEntity(2L)).isNotNull();
  }

  @Test
  void testDeleteSomeButNotAll() {
    assertThat(this.testEntityRepository.deleteBySize(1L)).isEqualTo(3);
    assertThat(this.testEntityRepository.countBySize(1L)).isZero();
  }

  @Test
  void testDeleteAllBySize() {
    this.testEntityRepository.deleteBySizeEquals(1L);
    assertThat(this.testEntityRepository.countBySize(1L)).isZero();
  }

  @Test
  void testRemoveByColor() {
    List<Long> removedId =
        this.testEntityRepository.removeByColor("red").stream()
            .map(TestEntity::getId)
            .collect(Collectors.toList());
    assertThat(removedId).containsExactlyInAnyOrder(1L, 3L, 4L);
  }

  @Test
  void testUpdateBlobFields() {
    assertThat(this.testEntityRepository.findById(1L))
        .isPresent()
        .get()
        .hasFieldOrPropertyWithValue("blobField", null);

    this.testEntityA.setBlobField(Blob.copyFrom("testValueA".getBytes()));
    this.testEntityRepository.save(this.testEntityA);

    assertThat(this.testEntityRepository.findById(1L))
        .isPresent()
        .get()
        .hasFieldOrPropertyWithValue("blobField", Blob.copyFrom("testValueA".getBytes()));
  }

  @Test
  void testWithCustomQuery() {
    assertThat(
            this.testEntityRepository.findEntitiesWithCustomQueryWithId(
                1L, this.datastoreTemplate.createKey(TestEntity.class, 1L)))
        .containsOnly(this.testEntityA);

    List<TestEntity> foundByCustomQuery = this.testEntityRepository.findEntitiesWithCustomQuery(1L);
    assertThat(foundByCustomQuery).hasSize(3);
    assertThat(foundByCustomQuery)
        .extracting(te -> te.getId())
        .containsExactlyInAnyOrder(1L, 3L, 4L);

    TestEntity[] foundByCustomProjectionQuery =
        this.testEntityRepository.findEntitiesWithCustomProjectionQuery(1L);
    assertThat(foundByCustomProjectionQuery).hasSize(3);
    assertThat(foundByCustomProjectionQuery[0].getBlobField()).isNull();
    assertThat(foundByCustomProjectionQuery[0].getId()).isEqualTo((Long) 1L);

    assertThat(this.testEntityRepository.countEntitiesWithCustomQuery(1L)).isEqualTo(3);
    assertThat(this.testEntityRepository.existsByEntitiesWithCustomQuery(1L)).isTrue();
    assertThat(this.testEntityRepository.existsByEntitiesWithCustomQuery(100L)).isFalse();
  }

  @Test
  void testCounting() {
    assertThat(this.testEntityRepository.countBySizeAndColor(2, "blue")).isEqualTo(1);
    assertThat(this.testEntityRepository.getBySize(2L).getColor()).isEqualTo("blue");
    assertThat(this.testEntityRepository.countBySizeAndColor(1, "red")).isEqualTo(3);
    assertThat(
            this.testEntityRepository.findTop3BySizeAndColor(1, "red").stream()
                .map(TestEntity::getId)
                .collect(Collectors.toList()))
        .containsExactlyInAnyOrder(1L, 3L, 4L);
  }

  @Test
  void testClearBlobField() {
    this.testEntityA.setBlobField(null);
    this.testEntityRepository.save(this.testEntityA);
    assertThat(this.testEntityRepository.findById(1L))
        .isPresent()
        .get()
        .hasFieldOrPropertyWithValue("blobField", null);
  }

  @Test
  void testDelete() {
    assertThat(this.testEntityRepository.findAllById(Arrays.asList(1L, 2L))).hasSize(2);
    this.testEntityRepository.delete(this.testEntityA);
    assertThat(this.testEntityRepository.findById(1L)).isNotPresent();
  }

  @Test
  void deleteAllByIdTest() {
    assertThat(this.testEntityRepository.findAllById(Arrays.asList(1L, 2L))).hasSize(2);
    // cast to SimpleDatastoreRepository for method be reachable with Spring Boot 2.4
    SimpleDatastoreRepository simpleRepository =
        AopTestUtils.getTargetObject(this.testEntityRepository);
    simpleRepository.deleteAllById(Arrays.asList(1L, 2L));
    assertThat(this.testEntityRepository.findById(1L)).isNotPresent();
    assertThat(this.testEntityRepository.findById(2L)).isNotPresent();
  }

  @Test
  void testTransactions() {
    this.testEntityRepository.deleteAll();

    this.transactionalTemplateService.testSaveAndStateConstantInTransaction(
        this.allTestEntities, 1000);

    await()
        .atMost(20, TimeUnit.SECONDS)
        .untilAsserted(() -> assertThat(this.testEntityRepository.countBySize(1L)).isEqualTo(3));

    this.testEntityRepository.deleteAll();

    try {
      this.transactionalTemplateService.testSaveInTransactionFailed(this.allTestEntities);
    } catch (Exception ignored) {
      // do nothing
    }

    // we wait a period long enough that the previously attempted failed save would
    // show up if it is unexpectedly successful and committed.
    await()
        .pollDelay(Duration.ofSeconds(2))
        .timeout(Duration.ofMinutes(1))
        .untilAsserted(
            () -> {
              assertThat(this.testEntityRepository.count()).isZero();
              assertThat(
                      this.testEntityRepository
                          .findAllById(Arrays.asList(1L, 2L))
                          .iterator()
                          .hasNext())
                  .isFalse();
            });
  }

  @Test
  void embeddedEntitiesTest() {
    EmbeddableTreeNode treeNode10 = new EmbeddableTreeNode(10, null, null);
    EmbeddableTreeNode treeNode8 = new EmbeddableTreeNode(8, null, null);
    EmbeddableTreeNode treeNode9 = new EmbeddableTreeNode(9, treeNode8, treeNode10);
    EmbeddableTreeNode treeNode7 = new EmbeddableTreeNode(7, null, treeNode9);

    this.datastoreTemplate.save(treeNode7);

    EmbeddableTreeNode loaded = this.datastoreTemplate.findById(7L, EmbeddableTreeNode.class);

    assertThat(loaded).isEqualTo(treeNode7);
  }

  @Test
  void embeddedCollectionTest() {
    EmbeddableTreeNode treeNode10 = new EmbeddableTreeNode(10, null, null);
    EmbeddableTreeNode treeNode8 = new EmbeddableTreeNode(8, null, null);
    EmbeddableTreeNode treeNode9 = new EmbeddableTreeNode(9, treeNode8, treeNode10);
    EmbeddableTreeNode treeNode7 = new EmbeddableTreeNode(7, null, treeNode9);

    TreeCollection treeCollection =
        new TreeCollection(1L, Arrays.asList(treeNode7, treeNode8, treeNode9, treeNode10));

    this.datastoreTemplate.save(treeCollection);

    TreeCollection loaded = this.datastoreTemplate.findById(1L, TreeCollection.class);

    assertThat(loaded).isEqualTo(treeCollection);
  }

  @Test
  void ancestorsTest() {
    AncestorEntity.DescendantEntry descendantEntryA = new AncestorEntity.DescendantEntry("a");
    AncestorEntity.DescendantEntry descendantEntryB = new AncestorEntity.DescendantEntry("b");
    AncestorEntity.DescendantEntry descendantEntryC = new AncestorEntity.DescendantEntry("c");

    AncestorEntity ancestorEntity =
        new AncestorEntity(
            "abc", Arrays.asList(descendantEntryA, descendantEntryB, descendantEntryC));

    this.datastoreTemplate.save(ancestorEntity);
    waitUntilTrue(
        () -> {
          AncestorEntity byId =
              this.datastoreTemplate.findById(ancestorEntity.id, AncestorEntity.class);
          return byId != null && byId.descendants.size() == 3;
        });

    AncestorEntity loadedEntity =
        this.datastoreTemplate.findById(ancestorEntity.id, AncestorEntity.class);
    assertThat(loadedEntity).isEqualTo(ancestorEntity);

    ancestorEntity.descendants.forEach(
        descendantEntry -> descendantEntry.name = descendantEntry.name + " updated");
    this.datastoreTemplate.save(ancestorEntity);
    waitUntilTrue(
        () ->
            this.datastoreTemplate.findAll(AncestorEntity.DescendantEntry.class).stream()
                .allMatch(descendantEntry -> descendantEntry.name.contains("updated")));

    AncestorEntity loadedEntityAfterUpdate =
        this.datastoreTemplate.findById(ancestorEntity.id, AncestorEntity.class);
    assertThat(loadedEntityAfterUpdate).isEqualTo(ancestorEntity);
  }

  @Test
  void referenceTest() {
    ReferenceEntity parent = saveReferenceEntitiesGraph();

    ReferenceEntity loadedParent = this.datastoreTemplate.findById(parent.id, ReferenceEntity.class);
    assertThat(loadedParent).isEqualTo(parent);

    parent.name = "parent updated";
    parent.children.forEach(child -> child.name = child.name + " updated");
    parent.sibling.name = "sibling updated";

    this.datastoreTemplate.save(parent);

    waitUntilTrue(
        () ->
            this.datastoreTemplate.findAll(ReferenceEntity.class).stream()
                .allMatch(entry -> entry.name.contains("updated")));

    ReferenceEntity loadedParentAfterUpdate =
        this.datastoreTemplate.findById(parent.id, ReferenceEntity.class);
    assertThat(loadedParentAfterUpdate).isEqualTo(parent);
  }

  @Test
  @DisabledInNativeImage
  @DisabledInAotMode
  void lazyReferenceCollectionTest() {
    ReferenceLazyEntity parent = saveEntitiesGraph();

    ReferenceLazyEntity lazyParent = this.datastoreTemplate.findById(parent.id, ReferenceLazyEntity.class);

    // Saving an entity with not loaded lazy field
    this.datastoreTemplate.save(lazyParent);

    ReferenceLazyEntity loadedParent =
        this.datastoreTemplate.findById(lazyParent.id, ReferenceLazyEntity.class);
    assertThat(loadedParent.children)
        .containsExactlyInAnyOrder(parent.children.toArray(new ReferenceLazyEntity[0]));
  }

  @Test
  @DisabledInNativeImage
  @DisabledInAotMode
  void lazyReferenceTest() throws InterruptedException {
    LazyEntity lazyParentEntity = new LazyEntity(new LazyEntity(new LazyEntity()));
    this.datastoreTemplate.save(lazyParentEntity);

    LazyEntity loadedParent =
        this.datastoreTemplate.findById(lazyParentEntity.id, LazyEntity.class);

    // Saving an entity with not loaded lazy field
    this.datastoreTemplate.save(loadedParent);

    loadedParent = this.datastoreTemplate.findById(loadedParent.id, LazyEntity.class);
    assertThat(loadedParent).isEqualTo(lazyParentEntity);
  }

  @Test
  @DisabledInNativeImage
  @DisabledInAotMode
  void singularLazyPropertyTest() {
    LazyEntity lazyParentEntity = new LazyEntity(new LazyEntity(new LazyEntity()));
    this.datastoreTemplate.save(lazyParentEntity);

    LazyEntity loadedParent =
        this.datastoreTemplate.findById(lazyParentEntity.id, LazyEntity.class);
    assertThat(loadedParent).isEqualTo(lazyParentEntity);
  }

  @Test
  @DisabledInNativeImage
  @DisabledInAotMode
  void lazyReferenceTransactionTest() {
    ReferenceLazyEntity parent = saveEntitiesGraph();

    // Exception should be produced if a lazy loaded property accessed outside of the initial
    // transaction
    ReferenceLazyEntity finalLoadedParent = this.transactionalTemplateService.findByIdLazy(parent.id);
    assertThatThrownBy(() -> finalLoadedParent.children.size())
        .isInstanceOf(DatastoreDataException.class)
        .hasMessage("Lazy load should be invoked within the same transaction");

    // No exception should be produced if a lazy loaded property accessed within the initial
    // transaction
    ReferenceLazyEntity finalLoadedParentLazyLoaded =
        this.transactionalTemplateService.findByIdLazyAndLoad(parent.id);
    assertThat(finalLoadedParentLazyLoaded).isEqualTo(parent);
  }

  private ReferenceLazyEntity saveEntitiesGraph() {
    ReferenceLazyEntity child1 = new ReferenceLazyEntity("child1", null, null);
    ReferenceLazyEntity child2 = new ReferenceLazyEntity("child2", null, null);
    ReferenceLazyEntity sibling = new ReferenceLazyEntity("sibling", null, null);
    ReferenceLazyEntity parent =
        new ReferenceLazyEntity("parent", sibling, Arrays.asList(child1, child2));
    this.datastoreTemplate.save(parent);
    waitUntilTrue(() -> this.datastoreTemplate.findAll(ReferenceLazyEntity.class).size() == 4);
    return parent;
  }

  private ReferenceEntity saveReferenceEntitiesGraph() {
    ReferenceEntity child1 = new ReferenceEntity("child1", null, null);
    ReferenceEntity child2 = new ReferenceEntity("child2", null, null);
    ReferenceEntity sibling = new ReferenceEntity("sibling", null, null);
    ReferenceEntity parent = new ReferenceEntity("parent", sibling, Arrays.asList(child1, child2));
    this.datastoreTemplate.save(parent);
    waitUntilTrue(() -> this.datastoreTemplate.findAll(ReferenceEntity.class).size() == 4);
    return parent;
  }

  @Test
  void allocateIdTest() {
    // intentionally null ID value
    TestEntity testEntity = new TestEntity(null, "red", 1L, Shape.CIRCLE, null);
    assertThat(testEntity.getId()).isNull();
    this.testEntityRepository.save(testEntity);
    assertThat(testEntity.getId()).isNotNull();
    assertThat(this.testEntityRepository.findById(testEntity.getId())).isPresent();
  }

  @Test
  void mapTest() {
    Map<String, Long> map = new HashMap<>();
    map.put("field1", 1L);
    map.put("field2", 2L);
    map.put("field3", 3L);

    this.keyForMap = this.datastoreTemplate.createKey("map", "myMap");

    this.datastoreTemplate.writeMap(this.keyForMap, map);
    Map<String, Long> loadedMap = this.datastoreTemplate.findByIdAsMap(this.keyForMap, Long.class);

    assertThat(loadedMap).isEqualTo(map);
  }

  @Test
  void recursiveSave() {
    SubEntity subEntity1 = new SubEntity();
    SubEntity subEntity2 = new SubEntity();
    SubEntity subEntity3 = new SubEntity();
    SubEntity subEntity4 = new SubEntity();

    subEntity1.stringList = Arrays.asList("a", "b");

    ParentEntity parentEntity =
        new ParentEntity(
            Arrays.asList(subEntity1, subEntity2),
            Collections.singletonList(subEntity4),
            subEntity3);
    subEntity1.parent = parentEntity;
    subEntity2.parent = parentEntity;
    subEntity3.parent = parentEntity;
    subEntity4.parent = parentEntity;

    subEntity1.sibling = subEntity2;
    subEntity2.sibling = subEntity1;

    subEntity3.sibling = subEntity4;
    subEntity4.sibling = subEntity3;

    this.datastoreTemplate.save(parentEntity);

    ParentEntity readParentEntity =
        this.datastoreTemplate.findById(parentEntity.id, ParentEntity.class);

    SubEntity readSubEntity1 = readParentEntity.subEntities.get(0);
    assertThat(readSubEntity1.parent).isSameAs(readParentEntity);
    assertThat(readSubEntity1.parent.subEntities.get(0)).isSameAs(readSubEntity1);

    SubEntity readSubEntity3 = readParentEntity.singularSubEntity;
    assertThat(readSubEntity3.parent).isSameAs(readParentEntity);
    assertThat(readSubEntity3.parent.singularSubEntity).isSameAs(readSubEntity3);

    SubEntity readSubEntity4 = readParentEntity.descendants.get(0);
    assertThat(readSubEntity4.parent).isSameAs(readParentEntity);
    assertThat(readSubEntity4.sibling).isSameAs(readSubEntity3);
    assertThat(readSubEntity3.sibling).isSameAs(readSubEntity4);

    Collection<SubEntity> allById =
        this.datastoreTemplate.findAllById(
            Arrays.asList(subEntity1.key, subEntity2.key), SubEntity.class);
    Iterator<SubEntity> iterator = allById.iterator();
    readSubEntity1 = iterator.next();
    SubEntity readSubEntity2 = iterator.next();
    assertThat(readSubEntity1.sibling).isSameAs(readSubEntity2);
    assertThat(readSubEntity2.sibling).isSameAs(readSubEntity1);
  }

  @Test
  void nullPropertyTest() {
    SubEntity subEntity1 = new SubEntity();
    subEntity1.stringList = Arrays.asList("a", "b", null, "c");
    subEntity1.stringProperty = null;

    this.datastoreTemplate.save(subEntity1);

    SubEntity readEntity = this.datastoreTemplate.findById(subEntity1.key, SubEntity.class);

    assertThat(readEntity.stringProperty).isNull();
    assertThat(readEntity.stringList).containsExactlyInAnyOrder("a", "b", null, "c");
  }

  @Test
  void inheritanceTest() {
    PetOwner petOwner = new PetOwner();
    petOwner.pets = Arrays.asList(new Cat("Alice"), new Cat("Bob"), new Pug("Bob"), new Dog("Bob"));

    this.datastoreTemplate.save(petOwner);

    PetOwner readPetOwner = this.datastoreTemplate.findById(petOwner.id, PetOwner.class);

    assertThat(readPetOwner.pets).hasSize(4);

    assertThat(readPetOwner.pets.stream().filter(pet -> "meow".equals(pet.speak()))).hasSize(2);
    assertThat(readPetOwner.pets.stream().filter(pet -> "woof".equals(pet.speak()))).hasSize(1);
    assertThat(readPetOwner.pets.stream().filter(pet -> "woof woof".equals(pet.speak())))
        .hasSize(1);

    waitUntilTrue(() -> this.datastoreTemplate.count(Pet.class) == 4);
    List<Pet> bobPets = this.petRepository.findByName("Bob");
    assertThat(bobPets.stream().map(Pet::speak))
        .containsExactlyInAnyOrder("meow", "woof", "woof woof");

    List<Dog> bobDogs = this.dogRepository.findByName("Bob");
    assertThat(bobDogs.stream().map(Pet::speak)).containsExactlyInAnyOrder("woof", "woof woof");

    assertThatThrownBy(() -> this.dogRepository.findByCustomQuery())
        .isInstanceOf(DatastoreDataException.class)
        .hasMessage("Can't append discrimination condition");
  }

  @Test
  void inheritanceTestFindAll() {
    this.datastoreTemplate.saveAll(
        Arrays.asList(new Cat("Cat1"), new Dog("Dog1"), new Pug("Dog2")));

    waitUntilTrue(() -> this.datastoreTemplate.count(Pet.class) == 3);

    Collection<Dog> dogs = this.datastoreTemplate.findAll(Dog.class);

    assertThat(dogs).hasSize(2);

    Long dogCount = dogs.stream().filter(pet -> "woof".equals(pet.speak())).count();
    Long pugCount = dogs.stream().filter(pet -> "woof woof".equals(pet.speak())).count();

    assertThat(pugCount).isEqualTo(1);
    assertThat(dogCount).isEqualTo(1);
  }

  @Test
  void enumKeys() {
    Map<CommunicationChannels, String> phone = new HashMap<>();
    phone.put(CommunicationChannels.SMS, "123456");

    Map<CommunicationChannels, String> email = new HashMap<>();
    phone.put(CommunicationChannels.EMAIL, "a@b.c");

    Event event1 = new Event("event1", phone);
    Event event2 = new Event("event2", email);

    this.datastoreTemplate.saveAll(Arrays.asList(event1, event2));

    waitUntilTrue(() -> this.datastoreTemplate.count(Event.class) == 2);

    Collection<Event> events = this.datastoreTemplate.findAll(Event.class);

    assertThat(events).containsExactlyInAnyOrder(event1, event2);
  }

  @Test
  void mapSubclass() {
    CustomMap customMap1 = new CustomMap();
    customMap1.put("key1", "val1");
    ServiceConfiguration service1 = new ServiceConfiguration("service1", customMap1);
    CustomMap customMap2 = new CustomMap();
    customMap2.put("key2", "val2");
    ServiceConfiguration service2 = new ServiceConfiguration("service2", customMap2);

    this.datastoreTemplate.saveAll(Arrays.asList(service1, service2));

    waitUntilTrue(() -> this.datastoreTemplate.count(ServiceConfiguration.class) == 2);

    Collection<ServiceConfiguration> events =
        this.datastoreTemplate.findAll(ServiceConfiguration.class);

    assertThat(events).containsExactlyInAnyOrder(service1, service2);
  }

  @Test
  void readOnlySaveTest() {

    assertThatThrownBy(() -> this.transactionalTemplateService.writingInReadOnly())
        .isInstanceOf(TransactionSystemException.class)
        .hasMessageContaining("Cloud Datastore transaction failed to commit.");
  }

  @Test
  void readOnlyDeleteTest() {

    assertThatThrownBy(() -> this.transactionalTemplateService.deleteInReadOnly())
        .isInstanceOf(TransactionSystemException.class)
        .hasMessageContaining("Cloud Datastore transaction failed to commit.");
  }

  @Test
  void readOnlyCountTest() {
    assertThat(this.transactionalTemplateService.findByIdInReadOnly(1)).isEqualTo(this.testEntityA);
  }

  @Test
  void sameClassDescendantsTest() {
    Employee entity3 = new Employee(Collections.EMPTY_LIST);
    Employee entity2 = new Employee(Collections.EMPTY_LIST);
    Employee entity1 = new Employee(Arrays.asList(entity2, entity3));
    Company company = new Company(1L, Arrays.asList(entity1));
    this.datastoreTemplate.save(company);

    Company readCompany = this.datastoreTemplate.findById(company.id, Company.class);
    Employee child = readCompany.leaders.get(0);

    assertThat(child.id).isEqualTo(entity1.id);
    assertThat(child.subordinates).containsExactlyInAnyOrderElementsOf(entity1.subordinates);

    assertThat(readCompany.leaders).hasSize(1);
    assertThat(readCompany.leaders.get(0).id).isEqualTo(entity1.id);
  }



  @Test
  void testPageableGqlEntityProjectionsPage() {
    Page<TestEntityProjection> page =
        this.testEntityRepository.getBySizePage(2L, PageRequest.of(0, 3));

    List<TestEntityProjection> testEntityProjections = page.get().collect(Collectors.toList());

    assertThat(testEntityProjections).hasSize(1);
    assertThat(testEntityProjections.get(0)).isInstanceOf(TestEntityProjection.class);
    assertThat(testEntityProjections.get(0)).isNotInstanceOf(TestEntity.class);
    assertThat(testEntityProjections.get(0).getColor()).isEqualTo("blue");
  }

  @Test
  void testPageableGqlEntityProjectionsSlice() {
    Slice<TestEntityProjection> slice =
        this.testEntityRepository.getBySizeSlice(2L, PageRequest.of(0, 3));

    List<TestEntityProjection> testEntityProjections = slice.get().collect(Collectors.toList());

    assertThat(testEntityProjections).hasSize(1);
    assertThat(testEntityProjections.get(0)).isInstanceOf(TestEntityProjection.class);
    assertThat(testEntityProjections.get(0)).isNotInstanceOf(TestEntity.class);
    assertThat(testEntityProjections.get(0).getColor()).isEqualTo("blue");
  }

  @Timeout(10000L)
  @Test
  void testSliceString() {
    try {
      Slice<String> slice =
          this.testEntityRepository.getSliceStringBySize(2L, PageRequest.of(0, 3));

      List<String> testEntityProjections = slice.get().collect(Collectors.toList());

      assertThat(testEntityProjections).hasSize(1);
      assertThat(testEntityProjections.get(0)).isEqualTo("blue");
    } catch (DatastoreException e) {
      if (e.getMessage().contains("no matching index found")) {
        throw new RuntimeException(
            "The required index is not found. "
                + "The index could be created by running this command from 'resources' directory: "
                + "'gcloud datastore indexes create index.yaml'");
      }
      throw e;
    }
  }

  @Timeout(10000L)
  @Test
  void testUnindex() {
    SubEntity childSubEntity = new SubEntity();
    childSubEntity.stringList = Collections.singletonList(generateString(1600));
    childSubEntity.stringProperty = generateString(1600);
    SubEntity parentSubEntity = new SubEntity();
    parentSubEntity.embeddedSubEntities = Collections.singletonList(childSubEntity);
    assertThat(this.datastoreTemplate.save(parentSubEntity)).isNotNull();
  }

  private String generateString(int length) {
    return IntStream.range(0, length).mapToObj(String::valueOf).collect(Collectors.joining(","));
  }

  @Test
  void newFieldTest() {
    Company company = new Company(1L, Collections.emptyList());
    company.name = "name1";
    this.datastoreTemplate.save(company);

    CompanyWithBooleanPrimitive companyWithBooleanPrimitive =
        this.datastoreTemplate.findById(1L, CompanyWithBooleanPrimitive.class);
    assertThat(companyWithBooleanPrimitive.name).isEqualTo(company.name);
    assertThat(companyWithBooleanPrimitive.active).isFalse();
  }

  @Test
  void returnStreamPartTreeTest() {
    this.testEntityRepository.saveAll(this.allTestEntities);
    Stream<TestEntity> resultStream = this.testEntityRepository.findPartTreeStreamByColor("red");
    assertThat(resultStream).hasSize(3).contains(testEntityA, testEntityC, testEntityD);
  }

  @Test
  void returnStreamGqlTest() {
    this.testEntityRepository.saveAll(this.allTestEntities);
    Stream<TestEntity> resultStream = this.testEntityRepository.findGqlStreamByColor("red");
    assertThat(resultStream).hasSize(3).contains(testEntityA, testEntityC, testEntityD);
  }

  @Test
  void queryByTimestampTest() {
    Timestamp date1 = Timestamp.parseTimestamp("2020-08-04T00:00:00Z");
    Timestamp date2 = Timestamp.parseTimestamp("2021-08-04T00:00:00Z");
    TestEntity testEntity1 = new TestEntity(1L, "red", 1L, date1);
    TestEntity testEntity2 = new TestEntity(2L, "blue", 2L, date2);
    TestEntity testEntity3 = new TestEntity(3L, "red", 1L, date1);
    TestEntity testEntity4 = new TestEntity(4L, "red", 1L, null);

    this.testEntityRepository.saveAll(
        Arrays.asList(testEntity1, testEntity2, testEntity3, testEntity4));
    Timestamp startDate = Timestamp.parseTimestamp("2020-07-04T00:00:00Z");
    Timestamp endDate = Timestamp.parseTimestamp("2020-08-06T00:00:00Z");

    List<TestEntity> results = this.testEntityRepository.getAllBetweenDates(startDate, endDate);
    assertThat(results).containsExactly(testEntity1, testEntity3);

    List<TestEntity> results2 = this.testEntityRepository.findByDatetimeGreaterThan(endDate);
    assertThat(results2).containsExactly(testEntity2);
  }

  @Test
  void testFindByExampleFluent() {
    Example<TestEntity> exampleRedCircle =
        Example.of(new TestEntity(null, "red", null, Shape.CIRCLE, null));
    Example<TestEntity> exampleRed = Example.of(new TestEntity(null, "red", null, null, null));

    List<TestEntity> entityRedAll = this.testEntityRepository.findBy(exampleRed, q -> q.all());
    assertThat(entityRedAll)
        .containsExactlyInAnyOrder(this.testEntityA, this.testEntityC, this.testEntityD);

    List<TestEntity> entityRedAllReverseSortedById =
        this.testEntityRepository.findBy(
            exampleRed, q -> q.sortBy(Sort.by("id").descending()).all());
    assertThat(entityRedAllReverseSortedById)
        .containsExactly(this.testEntityD, this.testEntityC, this.testEntityA);

    long countRedCircle =
        this.testEntityRepository.findBy(exampleRedCircle, FetchableFluentQuery::count);
    assertThat(countRedCircle).isEqualTo(2);

    boolean existsRed = this.testEntityRepository.findBy(exampleRed, FetchableFluentQuery::exists);
    assertThat(existsRed).isTrue();

    TestEntity firstValueRed =
        this.testEntityRepository.findBy(exampleRed, FetchableFluentQuery::firstValue);
    assertThat(firstValueRed).isEqualTo(testEntityA);

    TestEntity oneValueRed = this.testEntityRepository.findBy(exampleRed, q -> q.oneValue());
    assertThat(oneValueRed.getColor()).isEqualTo("red");

    Optional<TestEntity> onePurple =
        this.testEntityRepository.findBy(
            Example.of(new TestEntity(null, "purple", null, null, null)),
            FetchableFluentQuery::one);
    assertThat(onePurple).isNotPresent();

    Pageable pageable = PageRequest.of(0, 2);
    Page<TestEntity> pagedResults =
        this.testEntityRepository.findBy(exampleRed, q -> q.page(pageable));
    assertThat(pagedResults).containsExactly(this.testEntityA, this.testEntityC);

    Optional<TestEntity> oneRed =
        this.testEntityRepository.findBy(exampleRed, q -> q.sortBy(Sort.by("id")).one());
    assertThat(oneRed).isPresent().get().isEqualTo(testEntityA);

    long firstValueReverseSortedById =
        this.testEntityRepository.findBy(
            exampleRed, q -> q.sortBy(Sort.by("id").descending()).firstValue().getId());
    assertThat(firstValueReverseSortedById).isEqualTo(4L);

    List<String> redIdListReverseSorted =
        this.testEntityRepository.findBy(
            exampleRed,
            q ->
                q.sortBy(Sort.by("id").descending()).stream()
                    .map(x -> x.getId().toString())
                    .collect(Collectors.toList()));
    assertThat(redIdListReverseSorted).containsExactly("4", "3", "1");
  }
}
