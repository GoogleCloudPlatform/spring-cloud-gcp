/*
 * Copyright 2021-2021 the original author or authors.
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

package com.google.cloud.spring.data.spanner.repository.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.util.TypeInformation;

class SpannerQueryMethodTests {

  RepositoryMetadata mockMetadata;
  ProjectionFactory mockProjectionFactory;

  @BeforeEach
  void setUp() throws Exception {
    this.mockMetadata = mock(RepositoryMetadata.class);
    this.mockProjectionFactory = mock(ProjectionFactory.class);
    doReturn(TypeInformation.fromReturnTypeOf(Example.class.getMethod("someAnnotatedMethod")))
        .when(mockMetadata).getReturnType(any());
    doAnswer(a -> String.class).when(mockMetadata).getReturnedDomainClass(any());
  }

  @Test
  void hasQueryAnnotationTrueIfNonEmptyQueryFound() throws NoSuchMethodException {
    SpannerQueryMethod queryMethod =
        new SpannerQueryMethod(
            Example.class.getMethod("someAnnotatedMethod"), mockMetadata, mockProjectionFactory);
    assertThat(queryMethod.hasAnnotatedQuery()).isTrue();
  }

  @Test
  void hasQueryAnnotationFalseIfNotAnnotated() throws NoSuchMethodException {
    SpannerQueryMethod queryMethod =
        new SpannerQueryMethod(
            Example.class.getMethod("plainMethod"), mockMetadata, mockProjectionFactory);
    assertThat(queryMethod.hasAnnotatedQuery()).isFalse();
  }

  @Test
  void getQueryMethodReturnsStoredConstructorArgument() throws NoSuchMethodException {
    Method method = Example.class.getMethod("plainMethod");
    SpannerQueryMethod queryMethod =
        new SpannerQueryMethod(method, mockMetadata, mockProjectionFactory);
    assertThat(queryMethod.getQueryMethod()).isSameAs(method);
  }

  @Test
  void getQueryAnnotationsReturnsCorrectOne() throws NoSuchMethodException {
    SpannerQueryMethod queryMethod =
        new SpannerQueryMethod(
            Example.class.getMethod("someAnnotatedMethod"), mockMetadata, mockProjectionFactory);
    Query query = queryMethod.getQueryAnnotation();
    assertThat(query.value()).isEqualTo("select something");
  }

  static class Example {
    @Query("select something")
    public String someAnnotatedMethod() {
      return "I'm annotated";
    }

    public String plainMethod() {
      return "I'm not annotated";
    }
  }
}
