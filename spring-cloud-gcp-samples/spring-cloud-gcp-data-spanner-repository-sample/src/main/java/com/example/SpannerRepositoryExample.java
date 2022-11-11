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

import com.google.cloud.spanner.Key;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;

/** Example repository usage. */
@SpringBootApplication
public class SpannerRepositoryExample {
  private static final Log LOGGER = LogFactory.getLog(SpannerRepositoryExample.class);

  private final TraderRepository traderRepository;

  private final TradeRepository tradeRepository;

  private final SpannerSchemaUtils spannerSchemaUtils;

  private final SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

  private static final String DEMO_TRADER_1 = "demo_trader1";

  private static final String DEMO_TRADER_2 = "demo_trader2";

  private static final String DEMO_TRADER_3 = "demo_trader3";

  private static final String STOCK_1 = "STOCK1";

  private static final String STOCK_2 = "STOCK2";

  public SpannerRepositoryExample(TraderRepository traderRepository,
      TradeRepository tradeRepository,
      SpannerSchemaUtils spannerSchemaUtils,
      SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate) {
    this.traderRepository = traderRepository;
    this.tradeRepository = tradeRepository;
    this.spannerSchemaUtils = spannerSchemaUtils;
    this.spannerDatabaseAdminTemplate = spannerDatabaseAdminTemplate;
  }

  public void runExample() {
    createTablesIfNotExists();
    this.traderRepository.deleteAll();
    this.tradeRepository.deleteAll();

    this.traderRepository.save(new Trader(DEMO_TRADER_1, "John", "Doe"));
    this.traderRepository.save(new Trader(DEMO_TRADER_2, "Mary", "Jane"));
    this.traderRepository.save(new Trader(DEMO_TRADER_3, "Scott", "Smith"));

    this.tradeRepository.save(
        new Trade("1", "BUY", 100.0, 50.0, STOCK_1, DEMO_TRADER_1, Arrays.asList(99.0, 101.00)));
    this.tradeRepository.save(
        new Trade("2", "BUY", 105.0, 60.0, STOCK_2, DEMO_TRADER_1, Arrays.asList(99.0, 101.00)));
    this.tradeRepository.save(
        new Trade("3", "BUY", 100.0, 50.0, STOCK_1, DEMO_TRADER_1, Arrays.asList(99.0, 101.00)));
    this.tradeRepository.save(
        new Trade("1", "BUY", 100.0, 70.0, STOCK_2, DEMO_TRADER_2, Arrays.asList(99.0, 101.00)));
    this.tradeRepository.save(
        new Trade("2", "BUY", 103.0, 50.0, STOCK_1, DEMO_TRADER_2, Arrays.asList(99.0, 101.00)));
    this.tradeRepository.save(
        new Trade("3", "SELL", 100.0, 52.0, STOCK_2, DEMO_TRADER_2, Arrays.asList(99.0, 101.00)));
    this.tradeRepository.save(
        new Trade("1", "SELL", 98.0, 50.0, STOCK_1, DEMO_TRADER_3, Arrays.asList(99.0, 101.00)));
    this.tradeRepository.save(
        new Trade("2", "SELL", 110.0, 50.0, STOCK_2, DEMO_TRADER_3, Arrays.asList(99.0, 101.00)));

    LOGGER.info(
        "The table for trades has been cleared and "
            + this.tradeRepository.count()
            + " new trades have been inserted:");

    Iterable<Trade> allTrades = this.tradeRepository.findAll();

    LOGGER.info("All trades:");
    for (Trade t : allTrades) {
      LOGGER.info(t);
    }

    LOGGER.info("Make a pageable query:");
    List<Trade> tradesPageOne = this.tradeRepository.findByActionAndSymbol(PageRequest.of(0, 1),
        "BUY",
        STOCK_1
        );
    for (Trade t : tradesPageOne) {
      LOGGER.info(t);
    }

    LOGGER.info("These are the Cloud Spanner primary keys for the trades:");
    for (Trade t : allTrades) {
      Key key = this.spannerSchemaUtils.getKey(t);
      LOGGER.info(key);
    }

    LOGGER.info("There are " + this.tradeRepository.countByAction("BUY") + " BUY trades:");
    for (Trade t : this.tradeRepository.findByAction("BUY")) {
      LOGGER.info(t);
    }

    LOGGER.info("A query method can retrieve a single entity:");
    LOGGER.info(this.tradeRepository.getAnyOneTrade());

    LOGGER.info("A query method can also select properties in entities:");
    this.tradeRepository.getTradeIds("BUY").forEach(LOGGER::info);

    LOGGER.info("Lazy-loading collection of trades for 'demo_trader1':");
    this.traderRepository.findById(DEMO_TRADER_1)
        .ifPresent(trader -> LOGGER.info(trader.getTrades()));

    LOGGER.info("Try http://localhost:8080/trades in the browser to see all trades.");

    LOGGER.info(
        "JSON or ARRAY<JSON> field should be annotated with \"@Column(spannerType = TypeCode.JSON)\" in data"
            + " class.");

    Trader trader1 =
        new Trader("demo_trader_json1", "John", "Doe", new Address(5L, "fake address 1", true));
    Trader trader2 =
        new Trader("demo_trader_json2", "Mary", "Jane", new Address(8L, "fake address 2", true));
    Trader trader3 =
        new Trader("demo_trader_json3", "Scott", "Smith", new Address(8L, "fake address 3", false));
    trader3.setHomeAddress(new Address(8L, "fake address 3 in unused detail", false));
    Trader trader4 =
        new Trader("demo_trader_json4", "John", "Doe",
            Arrays.asList(new Address(666L, "fake address 4", false),
                new Address(777L, "fake address 5", false)));

    this.traderRepository.save(trader1);
    this.traderRepository.save(trader2);
    this.traderRepository.save(trader3);
    this.traderRepository.save(trader4);

    this.traderRepository.findById("demo_trader_json1")
        .ifPresent(trader -> LOGGER.info(String.format("Find trader by Id and print out JSON field 'workAddress' as string: %s", trader.getWorkAddress())));

    this.traderRepository.findById("demo_trader_json3")
        .ifPresent(trader -> LOGGER.info(String.format("Find trader by Id and print out JSON field 'unusedDetails' as string:  %s", trader.getHomeAddress())));

    this.traderRepository.findById("demo_trader_json4")
        .ifPresent(trader -> LOGGER.info(String.format("Find trader by Id and print out ARRAY<JSON> field 'addressList' as string: %s", trader.getAddressList())));

    long count = this.traderRepository.getCountActive("true");
    LOGGER.info("A query method can query on the properties of JSON values");
    LOGGER.info("Count of records with workAddress.active = true is " + count + ". ");

    List<Address> details = this.traderRepository.getTraderWorkAddressByActive("true");
    LOGGER.info("A query method can return a list of the JSON field values in POJO.");
    LOGGER.info("Work addresses with active = true: ");
    details.forEach(x -> LOGGER.info(x.toString()));
  }

  void createTablesIfNotExists() {
    if (!this.spannerDatabaseAdminTemplate.tableExists("trades_repository")) {
      this.spannerDatabaseAdminTemplate.executeDdlStrings(
          List.of(this.spannerSchemaUtils.getCreateTableDdlString(Trade.class)), true);
    }

    if (!this.spannerDatabaseAdminTemplate.tableExists("traders_repository")) {
      this.spannerDatabaseAdminTemplate.executeDdlStrings(
          List.of(this.spannerSchemaUtils.getCreateTableDdlString(Trader.class)), true);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(SpannerRepositoryExample.class, args);
  }

  @Bean
  ApplicationRunner applicationRunner() {
    return args -> {
      LOGGER.info("Running the Spanner Repository Example.");
      runExample();
    };
  }
}
