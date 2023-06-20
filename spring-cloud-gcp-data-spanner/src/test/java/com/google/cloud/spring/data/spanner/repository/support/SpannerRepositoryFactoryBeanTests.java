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

package com.google.cloud.spring.data.spanner.repository.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.cloud.spanner.Key;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/** Tests Spanner repository factory bean. */
class SpannerRepositoryFactoryBeanTests {

  private SpannerRepositoryFactoryBean<SpannerRepository<Object, Key>, Object, Key>
      spannerRepositoryFactoryBean;

  private SpannerMappingContext spannerMappingContext;

  private SpannerTemplate spannerTemplate;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    this.spannerMappingContext = new SpannerMappingContext();
    this.spannerTemplate = mock(SpannerTemplate.class);
    this.spannerRepositoryFactoryBean = new SpannerRepositoryFactoryBean(SpannerRepository.class);
    this.spannerRepositoryFactoryBean.setSpannerMappingContext(this.spannerMappingContext);
    this.spannerRepositoryFactoryBean.setSpannerTemplate(this.spannerTemplate);
  }

  @Test
  void createRepositoryFactoryTest() {
    RepositoryFactorySupport factory = this.spannerRepositoryFactoryBean.createRepositoryFactory();
    assertThat(factory).isInstanceOf(SpannerRepositoryFactory.class);
  }
}
