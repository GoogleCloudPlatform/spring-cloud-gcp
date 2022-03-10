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
  void queryByExampleExactMatchTest() {

    Example testExample = Example.of(new SimpleTestEntity(), ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.REGEX));
    assertThatThrownBy(() -> this.datastoreTemplate.queryByExample(testExample, null))
        .isInstanceOf(DatastoreDataException.class)
        .hasMessage("Unsupported StringMatcher. Only EXACT and DEFAULT are supported");
  }

  @Test
  void queryByExampleIgnoreCaseTest() {

    Example testExample = Example.of(new SimpleTestEntity(), ExampleMatcher.matching().withIgnoreCase());
    assertThatThrownBy(() -> this.datastoreTemplate.queryByExample(testExample, null))
        .isInstanceOf(DatastoreDataException.class)
        .hasMessage("Ignore case matching is not supported");
  }

  @Test
  void queryByExampleAllMatchTest() {

    Example testExample = Example.of(new SimpleTestEntity(), ExampleMatcher.matchingAny());
    assertThatThrownBy(() -> this.datastoreTemplate.queryByExample(testExample, null))
        .isInstanceOf(DatastoreDataException.class)
        .hasMessage("Unsupported MatchMode. Only MatchMode.ALL is supported");
  }

  @Test
  void queryByExamplePropertyMatchersTest() {

    Example testExample = Example.of(
        new SimpleTestEntity(),
        ExampleMatcher.matching()
            .withMatcher(
                "id",
                ExampleMatcher.GenericPropertyMatcher.of(ExampleMatcher.StringMatcher.REGEX)));
    assertThatThrownBy(() ->   this.datastoreTemplate.queryByExample(testExample, null))
        .isInstanceOf(DatastoreDataException.class)
        .hasMessage("Property matchers are not supported");
  }

  @Test
  void queryByExampleCaseSensitiveTest() {

    Example testExample =  Example.of(
        new SimpleTestEntity(),
        ExampleMatcher.matching()
            .withMatcher("id", ExampleMatcher.GenericPropertyMatcher::caseSensitive));
    assertThatThrownBy(() -> this.datastoreTemplate.queryByExample(testExample, null))
        .isInstanceOf(DatastoreDataException.class)
        .hasMessage("Property matchers are not supported");
  }

  @Test
  void queryByExampleNullTest() {

    assertThatThrownBy(() -> this.datastoreTemplate.queryByExample(null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("A non-null example is expected");
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
