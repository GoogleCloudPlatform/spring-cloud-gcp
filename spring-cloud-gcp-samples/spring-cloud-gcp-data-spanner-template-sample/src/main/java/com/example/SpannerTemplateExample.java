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

import com.google.cloud.spanner.KeySet;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;

/** Example usage of the Spanner Template. */
@SpringBootApplication
@ImportRuntimeHints(SpannerTemplateRuntimeHints.class)
public class SpannerTemplateExample {
  private static final Log LOGGER = LogFactory.getLog(SpannerTemplateExample.class);

  private static final String TEMPLATE_TRADER_1 = "template_trader1";

  @Autowired private SpannerTemplate spannerTemplate;

  @Autowired private SpannerSchemaUtils spannerSchemaUtils;

  @Autowired private SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

  public void runExample() {
    createTablesIfNotExists();
    this.spannerTemplate.delete(Trader.class, KeySet.all());
    this.spannerTemplate.delete(Trade.class, KeySet.all());

    Trader trader = new Trader(TEMPLATE_TRADER_1, "John", "Doe");

    this.spannerTemplate.insert(trader);

    Trade t =
        new Trade(
            "1", "BUY", 100.0, 50.0, "STOCK1", TEMPLATE_TRADER_1, Arrays.asList(99.0, 101.00));

    this.spannerTemplate.insert(t);

    t.setTradeId("2");
    t.setTraderId(TEMPLATE_TRADER_1);
    t.setAction("SELL");
    this.spannerTemplate.insert(t);

    t.setTradeId("1");
    t.setTraderId("template_trader2");
    this.spannerTemplate.insert(t);

    List<Trade> tradesByAction = this.spannerTemplate.readAll(Trade.class);
    LOGGER.info("All trades created by the example:");
    for (Trade trade : tradesByAction) {
      LOGGER.info(trade);
    }
  }

  void createTablesIfNotExists() {
    if (!this.spannerDatabaseAdminTemplate.tableExists("trades_template")) {
      this.spannerDatabaseAdminTemplate.executeDdlStrings(
          Collections.singletonList(this.spannerSchemaUtils.getCreateTableDdlString(Trade.class)), true);
    }

    if (!this.spannerDatabaseAdminTemplate.tableExists("traders_template")) {
      this.spannerDatabaseAdminTemplate.executeDdlStrings(
          Collections.singletonList(this.spannerSchemaUtils.getCreateTableDdlString(Trader.class)), true);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(SpannerTemplateExample.class, args);
  }

  @Bean
  ApplicationRunner applicationRunner() {
    return args -> {
      LOGGER.info("Running the Spanner Template Example.");
      runExample();
    };
  }
}
