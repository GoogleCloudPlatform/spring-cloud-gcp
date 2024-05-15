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

package com.google.cloud.spring.data.spanner.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import com.google.cloud.spring.data.spanner.core.convert.SpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import com.google.cloud.spring.data.spanner.repository.config.EnableSpannerAuditing;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests the auditing features of the template. */
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@DisabledInAotMode
class SpannerTemplateAuditingTests {

  private static final List<Mutation> UPSERT_MUTATION =
      List.of(Mutation.newInsertOrUpdateBuilder("custom_test_table").build());

  private static final Instant LONG_AGO = Instant.parse("2000-01-01T00:00:00.00Z");

  @Autowired SpannerTemplate spannerTemplate;

  @Test
  void testModifiedNullProperties() {
    TestEntity testEntity = new TestEntity();
    testEntity.id = "a";
    // intentionally leaving the other two audit properties untouched.

    this.spannerTemplate.upsert(testEntity);
    assertThat(testEntity.lastTouched).isNotNull();
    assertThat(testEntity.lastUser).isNotNull();
  }

  @Test
  void testModifiedPrevProperties() {
    TestEntity testEntity = new TestEntity();
    testEntity.id = "a";
    testEntity.lastTouched = LONG_AGO;
    testEntity.lastUser = "person";

    this.spannerTemplate.upsert(testEntity);
    assertThat(testEntity.lastTouched).isNotEqualTo(LONG_AGO);
    assertThat(testEntity.lastUser).isNotEqualTo("person");
  }

  /** Spring config for the tests. */
  @Configuration
  @EnableSpannerAuditing
  static class Config {

    @Bean
    public SpannerMappingContext spannerMappingContext() {
      return new SpannerMappingContext();
    }

    @Bean
    public SpannerEntityProcessor spannerEntityProcessor() {
      return mock(SpannerEntityProcessor.class);
    }

    @Bean
    public SpannerTemplate spannerTemplate(SpannerMappingContext spannerMappingContext) {
      SpannerEntityProcessor objectMapper = mock(SpannerEntityProcessor.class);
      SpannerMutationFactory mutationFactory = mock(SpannerMutationFactory.class);

      when(mutationFactory.upsert(Mockito.any(TestEntity.class), Mockito.any()))
          .thenAnswer(
              invocation -> {
                TestEntity testEntity = invocation.getArgument(0);
                assertThat(testEntity.lastTouched).isNotNull();
                assertThat(testEntity.lastTouched).isAfter(LONG_AGO);
                assertThat(testEntity.lastUser).isEqualTo("test_user");
                return UPSERT_MUTATION;
              });

      SpannerSchemaUtils schemaUtils =
          new SpannerSchemaUtils(spannerMappingContext, objectMapper, true);

      return new SpannerTemplate(
          () -> mock(DatabaseClient.class),
          spannerMappingContext,
          objectMapper,
          mutationFactory,
          schemaUtils);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
      return () -> Optional.of("test_user");
    }
  }

  @Table(name = "custom_test_table")
  private static class TestEntity {
    @PrimaryKey String id;

    @LastModifiedBy String lastUser;

    @LastModifiedDate Instant lastTouched;
  }
}
