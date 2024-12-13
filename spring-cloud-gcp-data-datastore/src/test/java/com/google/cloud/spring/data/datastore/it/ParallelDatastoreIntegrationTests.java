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

import com.google.cloud.spring.data.datastore.it.testdomains.TestEntity;
import com.google.cloud.spring.data.datastore.it.testdomains.TestEntityRepository;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests performing many operations at the same time using single instances of the repository. */
@EnabledIfSystemProperty(named = "it.datastore", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DatastoreIntegrationTestConfiguration.class})
@DisabledInAotMode
class ParallelDatastoreIntegrationTests extends AbstractDatastoreIntegrationTests {

  private static final int PARALLEL_OPERATIONS = 10;

  @Autowired
  TestEntityRepository testEntityRepository;

  @AfterEach
  void deleteAll() {
    this.testEntityRepository.deleteAll();
  }

  @Test
  void testParallelOperations() {
    performOperation(
            x ->
                    this.testEntityRepository.save(
                            new TestEntity((long) x, "color", (long) x, null, null)));

    waitUntilTrue(() -> this.testEntityRepository.count() == PARALLEL_OPERATIONS - 1);

    performOperation(x -> assertThat(this.testEntityRepository.getSizes(x)).hasSize(x));

    performOperation(x -> this.testEntityRepository.deleteBySize(x));

    waitUntilTrue(() -> this.testEntityRepository.count() == 0);
  }

  private void performOperation(IntConsumer function) {
    IntStream.range(1, PARALLEL_OPERATIONS).parallel().forEach(function);
  }
}
