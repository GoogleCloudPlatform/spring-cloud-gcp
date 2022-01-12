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

package com.google.cloud.spring.data.datastore.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** A transactional service used for integration tests. */
public class TransactionalTemplateService {

  @Autowired private DatastoreTemplate datastoreTemplate;

  @Transactional
  public void testSaveAndStateConstantInTransaction(
      List<TestEntity> testEntities, long waitMillisecondsForConfirmation) {

    for (TestEntity testEntity : testEntities) {
      assertThat(this.datastoreTemplate.findById(testEntity.getId(), TestEntity.class)).isNull();
    }

    this.datastoreTemplate.saveAll(testEntities);

    // Because these saved entities should NOT appear when we subsequently check, we
    // must wait a period of time that would see a non-transactional save go through.
    await()
        .pollDelay(waitMillisecondsForConfirmation, TimeUnit.MILLISECONDS)
        .atMost(Duration.ofMinutes(1))
        .untilAsserted(
            () -> {
              // Datastore transactions always see the state at the start of the transaction. Even
              // after waiting these entities should not be found.
              for (TestEntity testEntity : testEntities) {
                assertThat(this.datastoreTemplate.findById(testEntity.getId(), TestEntity.class))
                    .isNull();
              }
            });
  }

  @Transactional
  public void testSaveInTransactionFailed(List<TestEntity> testEntities) {
    this.datastoreTemplate.saveAll(testEntities);
    throw new RuntimeException("Intentional failure to cause rollback.");
  }

  @Transactional(readOnly = true)
  public void writingInReadOnly() {
    this.datastoreTemplate.save(new TestEntity(1L, "red", 1L, TestEntity.Shape.CIRCLE, null));
  }

  @Transactional(readOnly = true)
  public void deleteInReadOnly() {
    this.datastoreTemplate.delete(new TestEntity(1L, "red", 1L, TestEntity.Shape.CIRCLE, null));
  }

  @Transactional(readOnly = true)
  public TestEntity findByIdInReadOnly(long id) {
    return this.datastoreTemplate.findById(id, TestEntity.class);
  }

  @Transactional
  public ReferenceEntry findByIdLazy(long id) {
    return this.datastoreTemplate.findById(id, ReferenceEntry.class);
  }

  @Transactional
  @SuppressWarnings("ReturnValueIgnored")
  public ReferenceEntry findByIdLazyAndLoad(long id) {
    ReferenceEntry entry = this.datastoreTemplate.findById(id, ReferenceEntry.class);
    entry.children.size();
    return entry;
  }
}
