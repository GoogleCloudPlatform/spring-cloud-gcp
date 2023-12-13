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
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.google.cloud.datastore.ProjectionEntityQuery;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import com.google.cloud.spring.data.datastore.it.testdomains.EmbeddedEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.TestEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.TestEntity.Shape;
import com.google.cloud.spring.data.datastore.it.testdomains.TestEntityProjection;
import com.google.cloud.spring.data.datastore.it.testdomains.TestEntityRepository;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Projection tests separated from DatastoreIntegrationTests.java that needs mockito */
@DisabledInNativeImage
@DisabledInAotMode
@EnabledIfSystemProperty(named = "it.datastore", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DatastoreIntegrationTestConfiguration.class})
public class DatastoreProjectionIntegrationTests {
  @SpyBean private DatastoreTemplate datastoreTemplate;
  @Autowired private TestEntityRepository testEntityRepository;

  private final TestEntity testEntityA = new TestEntity(1L, "red", 1L, Shape.CIRCLE, null);

  private final TestEntity testEntityB = new TestEntity(2L, "blue", 2L, Shape.CIRCLE, null);

  private final TestEntity testEntityC =
      new TestEntity(3L, "red", 1L, Shape.CIRCLE, null, new EmbeddedEntity("c"));

  private final TestEntity testEntityD =
      new TestEntity(4L, "red", 1L, Shape.SQUARE, null, new EmbeddedEntity("d"));

  private final List<TestEntity> allTestEntities =
      Arrays.asList(this.testEntityA, this.testEntityB, this.testEntityC, this.testEntityD);

  @BeforeEach
  void saveEntities() {
    this.testEntityRepository.saveAll(this.allTestEntities);
    await()
        .atMost(20, TimeUnit.SECONDS)
        .untilAsserted(() -> assertThat(this.testEntityRepository.countBySize(1L)).isEqualTo(3));
  }

  @AfterEach
  void deleteAll() {
    this.testEntityRepository.deleteAll();
  }

  @Test
  void projectionTest() {
    reset(datastoreTemplate);
    assertThat(this.testEntityRepository.findBySize(2L).getColor()).isEqualTo("blue");

    ProjectionEntityQuery projectionQuery =
        com.google.cloud.datastore.Query.newProjectionEntityQueryBuilder()
            .addProjection("color")
            .setFilter(PropertyFilter.eq("size", 2L))
            .setKind("test_entities_ci")
            .setLimit(1)
            .build();

    verify(datastoreTemplate).queryKeysOrEntities(eq(projectionQuery), any());
  }

  @Test
  void testSlicedEntityProjections() {
    reset(datastoreTemplate);
    Slice<TestEntityProjection> testEntityProjectionSlice =
        this.testEntityRepository.findBySize(2L, PageRequest.of(0, 1));

    List<TestEntityProjection> testEntityProjections =
        testEntityProjectionSlice.get().collect(Collectors.toList());

    assertThat(testEntityProjections).hasSize(1);
    assertThat(testEntityProjections.get(0)).isInstanceOf(TestEntityProjection.class);
    assertThat(testEntityProjections.get(0)).isNotInstanceOf(TestEntity.class);

    // Verifies that the projection method call works.
    assertThat(testEntityProjections.get(0).getColor()).isEqualTo("blue");

    ProjectionEntityQuery projectionQuery =
        com.google.cloud.datastore.Query.newProjectionEntityQueryBuilder()
            .addProjection("color")
            .setFilter(PropertyFilter.eq("size", 2L))
            .setKind("test_entities_ci")
            .setLimit(1)
            .build();

    verify(datastoreTemplate).queryKeysOrEntities(eq(projectionQuery), any());
  }
}
