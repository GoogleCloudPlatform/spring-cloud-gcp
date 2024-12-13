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

package com.google.cloud.spring.data.datastore.core.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreDataException;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.core.mapping.DatastorePersistentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;

/** Tests for the object to key factory. */
class DatastoreServiceObjectToKeyFactoryTests {

  private final Datastore datastore = mock(Datastore.class);

  private final DatastoreMappingContext datastoreMappingContext = new DatastoreMappingContext();

  private final DatastoreServiceObjectToKeyFactory datastoreServiceObjectToKeyFactory =
      new DatastoreServiceObjectToKeyFactory(() -> this.datastore);

  @Test
  void getKeyFromIdKeyTest() {
    when(this.datastore.newKeyFactory()).thenReturn(new KeyFactory("p").setKind("k"));
    Key key = new KeyFactory("project").setKind("kind").newKey("key");
    assertThat(this.datastoreServiceObjectToKeyFactory.getKeyFromId(key, "kind")).isSameAs(key);
  }

  @Test
  void getKeyFromIdStringTest() {
    when(this.datastore.newKeyFactory()).thenReturn(new KeyFactory("p").setKind("k"));
    assertThat(this.datastoreServiceObjectToKeyFactory.getKeyFromId("key", "custom_test_kind"))
        .isEqualTo(new KeyFactory("p").setKind("custom_test_kind").newKey("key"));
  }

  @Test
  void getKeyFromIdLongTest() {
    when(this.datastore.newKeyFactory()).thenReturn(new KeyFactory("p").setKind("k"));
    assertThat(new KeyFactory("p").setKind("custom_test_kind").newKey(3L))
        .isEqualTo(this.datastoreServiceObjectToKeyFactory.getKeyFromId(3L, "custom_test_kind"));
  }

  @Test
  void getKeyFromIdExceptionTest() {
    when(this.datastore.newKeyFactory()).thenReturn(new KeyFactory("p").setKind("k"));
    assertThatThrownBy(() -> this.datastoreServiceObjectToKeyFactory.getKeyFromId(true, "custom_test_kind"))
            .isInstanceOf(DatastoreDataException.class)
            .hasMessage("Keys can only be created using String or long values.");
  }

  @Test
  void getKeyTest() {
    when(this.datastore.newKeyFactory()).thenReturn(new KeyFactory("p").setKind("k"));
    TestEntityWithId testEntity = new TestEntityWithId();
    testEntity.id = 1L;

    Key actual =
        this.datastoreServiceObjectToKeyFactory.getKeyFromObject(
            testEntity, this.datastoreMappingContext.getPersistentEntity(TestEntityWithId.class));
    Key expectedKey = new KeyFactory("p").setKind("custom_test_kind").newKey(1L);

    assertThat(actual).isEqualTo(expectedKey);
  }

  @Test
  void getKeyNoIdTest() {

    DatastorePersistentEntity dpeTest = this.datastoreMappingContext.getPersistentEntity(TestEntityNoId.class);
    Object entity = new TestEntityNoId();

    assertThatThrownBy(() -> this.datastoreServiceObjectToKeyFactory.getKeyFromObject(entity, dpeTest))
            .isInstanceOf(DatastoreDataException.class)
            .hasMessage("An ID property was required but does not exist for the type: "
                    + "class com.google.cloud.spring.data.datastore.core.convert."
                    + "DatastoreServiceObjectToKeyFactoryTests$TestEntityNoId");
  }

  @Test
  void nullIdTest() {
    assertThat(
            this.datastoreServiceObjectToKeyFactory.getKeyFromObject(
                new TestEntityWithId(),
                this.datastoreMappingContext.getPersistentEntity(TestEntityWithId.class)))
        .isNull();
  }

  @Test
  void allocateIdForObjectTest() {
    TestEntityWithKeyId testEntityWithKeyId = new TestEntityWithKeyId();

    doAnswer(
            invocation -> {
              IncompleteKey incompleteKey = (IncompleteKey) invocation.getArguments()[0];
              long id = 123L;
              if (incompleteKey.getAncestors().size() > 0) {
                id = 456L;
              }
              return Key.newBuilder(incompleteKey, id).build();
            })
        .when(this.datastore)
        .allocateId((IncompleteKey) any());

    when(this.datastore.newKeyFactory()).thenReturn(new KeyFactory("project"));
    Key allocatedKey =
        this.datastoreServiceObjectToKeyFactory.allocateKeyForObject(
            testEntityWithKeyId,
            this.datastoreMappingContext.getPersistentEntity(testEntityWithKeyId.getClass()));
    Key key = new KeyFactory("project").setKind("custom_test_kind").newKey(123L);
    assertThat(allocatedKey).isEqualTo(key);
    assertThat(testEntityWithKeyId.id).isEqualTo(key);

    Key allocatedKeyWithAncestor =
        this.datastoreServiceObjectToKeyFactory.allocateKeyForObject(
            testEntityWithKeyId,
            this.datastoreMappingContext.getPersistentEntity(testEntityWithKeyId.getClass()),
            allocatedKey);
    Key keyWithAncestor =
        new KeyFactory("project")
            .setKind("custom_test_kind")
            .addAncestor(PathElement.of(key.getKind(), key.getId()))
            .newKey(456L);
    assertThat(allocatedKeyWithAncestor).isEqualTo(keyWithAncestor);
    assertThat(testEntityWithKeyId.id).isEqualTo(keyWithAncestor);
  }

  @Test
  void allocateIdForObjectNonKeyIdTest() {

    TestEntityWithId testEntityWithId = new TestEntityWithId();
    KeyFactory keyFactory = new KeyFactory("project").setKind("kind");
    Key testKey = keyFactory.newKey("ancestor");

    DatastorePersistentEntity dpeTest = this.datastoreMappingContext.getPersistentEntity(testEntityWithId.getClass());

    when(this.datastore.newKeyFactory()).thenReturn(keyFactory);

    assertThatThrownBy(() -> this.datastoreServiceObjectToKeyFactory.allocateKeyForObject(testEntityWithId, dpeTest, testKey))
            .isInstanceOf(DatastoreDataException.class)
            .hasMessage("Only Key types are allowed for descendants id");
  }

  @Test
  void allocateIdForObjectUnsupportedKeyTypeIdTest() {

    TestEntityWithStringId testEntityWithStringId = new TestEntityWithStringId();
    KeyFactory keyFactory = new KeyFactory("project").setKind("kind");
    Key testKey = keyFactory.newKey("key");
    when(this.datastore.newKeyFactory()).thenReturn(keyFactory);
    DatastorePersistentEntity dpeTest = this.datastoreMappingContext.getPersistentEntity(testEntityWithStringId.getClass());

    assertThatThrownBy(() -> this.datastoreServiceObjectToKeyFactory.allocateKeyForObject(testEntityWithStringId, dpeTest, testKey))
            .isInstanceOf(DatastoreDataException.class)
            .hasMessage("Cloud Datastore can only allocate IDs for Long and Key properties. "
                    + "Cannot allocate for type: class java.lang.String");
  }

  @com.google.cloud.spring.data.datastore.core.mapping.Entity(name = "custom_test_kind")
  private static class TestEntityWithId {
    @Id Long id;
  }

  @com.google.cloud.spring.data.datastore.core.mapping.Entity(name = "custom_test_kind")
  private static class TestEntityWithStringId {
    @Id String id;
  }

  @com.google.cloud.spring.data.datastore.core.mapping.Entity(name = "custom_test_kind")
  private static class TestEntityNoId {}

  @com.google.cloud.spring.data.datastore.core.mapping.Entity(name = "custom_test_kind")
  private static class TestEntityWithKeyId {
    @Id Key id;
  }
}
