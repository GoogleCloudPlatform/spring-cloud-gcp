/*
 * Copyright 2022-2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.data.datastore.core;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.spring.data.datastore.core.convert.DatastoreEntityConverter;
import com.google.cloud.spring.data.datastore.core.convert.ObjectToKeyFactory;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreDataException;
import com.google.cloud.spring.data.datastore.core.mapping.DatastoreMappingContext;
import com.google.cloud.spring.data.datastore.core.mapping.Field;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

public class DatastoreQueryByExampleTests {

  private final Datastore datastore = mock(Datastore.class);

  private DatastoreTemplate datastoreTemplate;

  private final DatastoreEntityConverter datastoreEntityConverter =
      mock(DatastoreEntityConverter.class);
  private final ObjectToKeyFactory objectToKeyFactory = mock(ObjectToKeyFactory.class);


  @BeforeEach
  void setup() {
    this.datastoreTemplate =
        new DatastoreTemplate(
            () -> this.datastore,
            this.datastoreEntityConverter,
            new DatastoreMappingContext(),
            this.objectToKeyFactory);
  }

  @Test
  void basicThrowableAssert() {

    assertThatThrownBy(() -> {
      throw new RuntimeException("seriously?"); }
    )
        .isInstanceOf(RuntimeException.class)
        .hasMessage("seriously?");
  }

  @Test
  void queryByExampleDeepPathTest() {

    Example testExample = Example.of(new SimpleTestEntity(), ExampleMatcher.matching().withIgnorePaths("intField.a"));
    assertThatThrownBy(() -> {
      this.datastoreTemplate.queryByExample(testExample, null);
    })
        .isInstanceOf(DatastoreDataException.class)
        .hasMessage("Ignored paths deeper than 1 are not supported");
  }


  @com.google.cloud.spring.data.datastore.core.mapping.Entity(name = "test_kind")
  private static class SimpleTestEntity {
    @Id
    String id;

    String color;

    @Field(name = "int_field")
    int intField;
  }
}
