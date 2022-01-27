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

package com.google.cloud.spring.data.spanner.repository.query;

import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * A Query Method for Spanner.
 *
 * @since 1.1
 */
public class SpannerQueryMethod extends QueryMethod {

  private final Method queryMethod;

  /**
   * Creates a new {@link QueryMethod} from the given parameters. Looks up the correct query to use
   * for following invocations of the method given.
   *
   * @param queryMethod must not be {@literal null}.
   * @param metadata must not be {@literal null}.
   * @param factory must not be {@literal null}.
   */
  public SpannerQueryMethod(
      Method queryMethod, RepositoryMetadata metadata, ProjectionFactory factory) {
    super(queryMethod, metadata, factory);
    this.queryMethod = queryMethod;
  }

  /**
   * Returns whether the method has an annotated query.
   *
   * @return true this query method has annotation that holds the query string.
   */
  public boolean hasAnnotatedQuery() {
    return findAnnotatedQuery().isPresent();
  }

  private Optional<String> findAnnotatedQuery() {

    return Optional.ofNullable(getQueryAnnotation())
        .map(AnnotationUtils::getValue)
        .map(String.class::cast)
        .filter(StringUtils::hasText);
  }

  /**
   * Get the method metadata.
   *
   * @return the method metadata.
   */
  Method getQueryMethod() {
    return this.queryMethod;
  }

  /**
   * Returns the {@link Query} annotation that is applied to the method or {@code null} if none
   * available.
   *
   * @return the query annotation that is applied.
   */
  @Nullable
  Query getQueryAnnotation() {
    return AnnotatedElementUtils.findMergedAnnotation(this.queryMethod, Query.class);
  }
}
