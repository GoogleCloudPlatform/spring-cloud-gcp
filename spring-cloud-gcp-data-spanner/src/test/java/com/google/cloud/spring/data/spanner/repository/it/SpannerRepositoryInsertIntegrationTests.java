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

package com.google.cloud.spring.data.spanner.repository.it;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import com.google.cloud.spring.data.spanner.test.IntegrationTestConfiguration;
import com.google.cloud.spring.data.spanner.test.domain.Singer;
import com.google.cloud.spring.data.spanner.test.domain.SingerRepository;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Integration tests for Spanner Repository. */

@EnabledIfSystemProperty(named = "it.spanner", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IntegrationTestConfiguration.class})
class SpannerRepositoryInsertIntegrationTests {

  @Autowired SingerRepository singerRepository;

  @Autowired SpannerTemplate spannerTemplate;

  @Autowired protected SpannerSchemaUtils spannerSchemaUtils;

  @Autowired SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

  @BeforeEach
  void setUp() {
    if (!this.spannerDatabaseAdminTemplate.tableExists("singers_list")) {
      this.spannerDatabaseAdminTemplate.executeDdlStrings(
          Collections.singleton(this.spannerSchemaUtils.getCreateTableDdlString(Singer.class)),
          true);
    }
    this.singerRepository.deleteAll();
  }

  @AfterEach
  void clearData() {
    this.singerRepository.deleteAll();
  }

  @Test
  void insertTest() {
    singerRepository.insert(1, "Cher", null);
    Iterable<Singer> singers = singerRepository.findAll();
    assertThat(singers).containsExactly(new Singer(1, "Cher", null));
  }
}
