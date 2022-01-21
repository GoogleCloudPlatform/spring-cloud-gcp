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

package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spanner.KeySet;
import com.google.cloud.spring.data.spanner.core.SpannerOperations;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests for the Spanner template usage. */
@EnabledIfSystemProperty(named = "it.spanner", matches = "true")
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest
class SpannerTemplateIntegrationTests {
  @Autowired private SpannerOperations spannerOperations;

  @Autowired private SpannerSchemaUtils spannerSchemaUtils;

  @Autowired private SpannerTemplateExample spannerTemplateExample;

  @BeforeEach
  @AfterEach
  void cleanupSpannerTables() {
    this.spannerTemplateExample.createTablesIfNotExists();
    this.spannerOperations.delete(Trader.class, KeySet.all());
    this.spannerOperations.delete(Trade.class, KeySet.all());
  }

  @Test
  void testSpannerTemplateLoadsData() {
    assertThat(this.spannerOperations.readAll(Trade.class)).isEmpty();

    this.spannerTemplateExample.runExample();

    Set<String> tradeSpannerKeys =
        this.spannerOperations.readAll(Trade.class).stream()
            .map(t -> this.spannerSchemaUtils.getKey(t).toString())
            .collect(Collectors.toSet());

    assertThat(tradeSpannerKeys)
        .containsExactlyInAnyOrder(
            "[template_trader1,1]", "[template_trader1,2]", "[template_trader2,1]");
  }
}
