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

package com.google.cloud.spring.data.datastore.core.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreDataException;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.core.mapping.DatastorePersistentEntity;
import com.google.cloud.spring.data.datastore.core.mapping.DatastorePersistentProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for the entity value provider. */
class EntityPropertyValueProviderTests {

  private static final LocalDatastoreHelper HELPER = LocalDatastoreHelper.create(1.0);

  private final DatastoreMappingContext datastoreMappingContext = new DatastoreMappingContext();

  private Datastore datastore;

  private TwoStepsConversions twoStepsConversion =
      new TwoStepsConversions(new DatastoreCustomConversions(), null, this.datastoreMappingContext);

  private DatastorePersistentEntity<TestDatastoreItem> persistentEntity =
      (DatastorePersistentEntity<TestDatastoreItem>)
          this.datastoreMappingContext.getPersistentEntity(TestDatastoreItem.class);

  @BeforeEach
  void setUp() {
    this.datastore =
        HELPER.getOptions().toBuilder().setNamespace("ghijklmnop").build().getService();
  }

  @Test
  void getPropertyValue() {
    byte[] bytes = {1, 2, 3};
    Entity entity =
        Entity.newBuilder(this.datastore.newKeyFactory().setKind("aKind").newKey("1"))
            .set("stringField", "string value")
            .set("boolField", true)
            .set("doubleField", 3.1415D)
            .set("longField", 123L)
            .set("latLngField", LatLng.of(10, 20))
            .set("timestampField", Timestamp.ofTimeSecondsAndNanos(30, 40))
            .set("blobField", Blob.copyFrom(bytes))
            .build();

    EntityPropertyValueProvider provider =
        new EntityPropertyValueProvider(entity, this.twoStepsConversion);

    assertThat(
            (String)
                provider.getPropertyValue(
                    this.persistentEntity.getPersistentProperty("stringField")))
        .as("validate string field")
        .isEqualTo("string value");
    assertThat(
            (Boolean)
                provider.getPropertyValue(this.persistentEntity.getPersistentProperty("boolField")))
        .as("validate boolean field")
        .isTrue();
    assertThat(
            (Double)
                provider.getPropertyValue(
                    this.persistentEntity.getPersistentProperty("doubleField")))
        .as("validate double field")
        .isEqualTo(3.1415D);
    assertThat(
            (Long)
                provider.getPropertyValue(this.persistentEntity.getPersistentProperty("longField")))
        .as("validate long field")
        .isEqualTo(123L);
    assertThat(
            (LatLng)
                provider.getPropertyValue(
                    this.persistentEntity.getPersistentProperty("latLngField")))
        .as("validate latLng field")
        .isEqualTo(LatLng.of(10, 20));
    assertThat(
            (Timestamp)
                provider.getPropertyValue(
                    this.persistentEntity.getPersistentProperty("timestampField")))
        .as("validate timestamp field")
        .isEqualTo(Timestamp.ofTimeSecondsAndNanos(30, 40));
    assertThat(
            (Blob)
                provider.getPropertyValue(this.persistentEntity.getPersistentProperty("blobField")))
        .as("validate blob field")
        .isEqualTo(Blob.copyFrom(bytes));
  }

  @Test
  void testException() {

    Entity entity =
        Entity.newBuilder(this.datastore.newKeyFactory().setKind("aKind").newKey("1"))
            .set("boolField", 123L)
            .build();

    EntityPropertyValueProvider provider =
        new EntityPropertyValueProvider(entity, this.twoStepsConversion);

    DatastorePersistentProperty testDpe = this.persistentEntity.getPersistentProperty("boolField");

    assertThatThrownBy(() -> provider.getPropertyValue(testDpe))
            .isInstanceOf(DatastoreDataException.class)
            .hasMessageContaining("Unable to read property boolField")
            .hasStackTraceContaining("Unable to convert class java.lang.Long to class java.lang.Boolean");
  }
}
