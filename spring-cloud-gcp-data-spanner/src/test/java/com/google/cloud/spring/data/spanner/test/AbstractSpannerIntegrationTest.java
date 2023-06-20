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

package com.google.cloud.spring.data.spanner.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import com.google.cloud.spring.data.spanner.core.SpannerOperations;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.test.domain.CommitTimestamps;
import com.google.cloud.spring.data.spanner.test.domain.Trade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;

/**
 * This class provides the foundation for the integration test framework for Spanner. Its
 * responsibilities:
 *
 * <ul>
 *   <li>initializes the Spring application context
 *   <li>sets up the database schema
 * </ul>
 *
 * <p>Prerequisites for running integration tests:
 *
 * <p>For Cloud Spanner integration tests, you will need to have an instance predefined, everything
 * else is generated. The instance by default is "integration-instance", which can be overriden in
 * <code>src/test/resources/application-test.properties</code>, the property: <code>
 * test.integration.spanner.instance</code>
 *
 * <p>Within the <code>integration-instance</code> instance, the tests rely on a single database for
 * tests, <code>integration-db</code>. This is automatically created, if doesn't exist. The tables
 * are generated to have a unique suffix, which is updated on the entity annotations as well
 * dynamically to avoid collisions of multiple parallel tests running against the same instance.
 */
@EnabledIfSystemProperty(named = "it.spanner", matches = "true")
@ContextConfiguration(classes = {IntegrationTestConfiguration.class})
@TestExecutionListeners(
    listeners = SpannerTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class AbstractSpannerIntegrationTest {

  @Autowired protected SpannerOperations spannerOperations;

  @Autowired protected ApplicationContext applicationContext;

  @Autowired SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

  @Autowired protected SpannerSchemaUtils spannerSchemaUtils;

  @Autowired SpannerMappingContext spannerMappingContext;

  @Test
  public void tableCreatedTest() {
    assertThat(
            this.spannerDatabaseAdminTemplate.tableExists(
                this.spannerMappingContext.getPersistentEntityOrFail(Trade.class).tableName()))
        .isTrue();
    assertThat(
            this.spannerDatabaseAdminTemplate.tableExists(
                this.spannerMappingContext
                    .getPersistentEntityOrFail(CommitTimestamps.class)
                    .tableName()))
        .isTrue();
  }
}
