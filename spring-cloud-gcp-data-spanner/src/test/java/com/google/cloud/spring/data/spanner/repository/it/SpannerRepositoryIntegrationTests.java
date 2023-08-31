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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerPersistentEntity;
import com.google.cloud.spring.data.spanner.repository.support.SimpleSpannerRepository;
import com.google.cloud.spring.data.spanner.test.AbstractSpannerIntegrationTest;
import com.google.cloud.spring.data.spanner.test.domain.Details;
import com.google.cloud.spring.data.spanner.test.domain.SubTrade;
import com.google.cloud.spring.data.spanner.test.domain.SubTradeComponent;
import com.google.cloud.spring.data.spanner.test.domain.SubTradeComponentRepository;
import com.google.cloud.spring.data.spanner.test.domain.SubTradeRepository;
import com.google.cloud.spring.data.spanner.test.domain.SymbolAction;
import com.google.cloud.spring.data.spanner.test.domain.Trade;
import com.google.cloud.spring.data.spanner.test.domain.TradeProjection;
import com.google.cloud.spring.data.spanner.test.domain.TradeRepository;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AopTestUtils;
import org.springframework.transaction.annotation.Transactional;

/** Integration tests for Spanner Repository that uses many features. */
@EnabledIfSystemProperty(named = "it.spanner", matches = "true")
@ExtendWith(SpringExtension.class)
public class SpannerRepositoryIntegrationTests extends AbstractSpannerIntegrationTest {

  @Autowired TradeRepository tradeRepository;

  @Autowired SubTradeRepository subTradeRepository;

  @Autowired SubTradeComponentRepository subTradeComponentRepository;

  @Autowired TradeRepositoryTransactionalService tradeRepositoryTransactionalService;

  @Autowired SpannerMappingContext spannerMappingContext;

  @BeforeEach
  @AfterEach
  void cleanUpData() {
    this.tradeRepository.deleteAll();
  }

  @Test
  void queryOptionalSingleValueTest() {
    Trade trade = Trade.makeTrade(null, 0);
    this.spannerOperations.insert(trade);

    Optional<String> nonEmpty = tradeRepository.fetchSymbolById(trade.getId());
    assertThat(nonEmpty).isPresent().contains("ABCD");

    Optional<String> empty = tradeRepository.fetchSymbolById(trade.getId() + "doesNotExist");
    assertThat(empty).isNotPresent();
  }

  @Test
  void queryMethodsTest_simple() {
    final int subTrades = 42;
    Trade trade = Trade.makeTrade(null, subTrades);
    this.spannerOperations.insert(trade);

    Optional<Trade> fetchedTrade = tradeRepository.fetchById(trade.getId());
    assertThat(fetchedTrade)
        .isPresent()
        .get()
        .hasFieldOrPropertyWithValue("bigDecimalField", trade.getBigDecimalField())
        .hasFieldOrPropertyWithValue("bigDecimals", trade.getBigDecimals());

    final String identifier = trade.getTradeDetail().getId();
    final String traderId = trade.getTraderId();

    assertThat(subTradeRepository.countBy(identifier, traderId)).isEqualTo(subTrades);

    List<SubTrade> list =
        subTradeRepository.getList(identifier, traderId, Sort.by(Order.desc("subTradeId")));
    assertThat(list)
        .hasSize(subTrades)
        .last()
        .satisfies(s -> assertThat(s.getSubTradeId()).isEqualTo("subTrade0"));

    List<SubTrade> page =
        subTradeRepository.getPage(
            identifier, traderId, PageRequest.of(0, 1024, Sort.by(Order.asc("subTradeId"))));
    assertThat(page)
        .hasSize(subTrades)
        .first()
        .satisfies(s -> assertThat(s.getSubTradeId()).isEqualTo("subTrade0"));
  }

  @Test
  void queryMethodsTest_updateActionTradeById() {
    List<Trade> trader1BuyTrades = insertTrades("trader1", "BUY", 3);

    Trade buyTrade1 = trader1BuyTrades.get(0);
    this.tradeRepository.updateActionTradeById(buyTrade1.getId(), "invalid action");
    assertThat(this.tradeRepository.findById(this.spannerSchemaUtils.getKey(buyTrade1)))
        .isPresent()
        .get()
        .extracting(Trade::getAction)
        .isEqualTo("invalid action");

    this.tradeRepository.updateActionTradeById(buyTrade1.getId(), "BUY");
    assertThat(this.tradeRepository.findById(this.spannerSchemaUtils.getKey(buyTrade1)))
        .isPresent()
        .get()
        .extracting(Trade::getAction)
        .isEqualTo("BUY");
  }

  @Test
  void queryMethodsTest_BoundParameters() {
    insertTrades("trader1", "BUY", 3);
    insertTrades("trader1", "SELL", 2);
    insertTrades("trader2", "SELL", 3);

    assertThat(this.tradeRepository.count()).isEqualTo(8L);

    assertThat(this.tradeRepository.countByActionIn(Arrays.asList("BUY", "SELL"))).isEqualTo(8L);
    assertThat(this.tradeRepository.countByActionIn(Collections.singletonList("BUY")))
        .isEqualTo(3L);
    assertThat(this.tradeRepository.countByActionIn(Collections.singletonList("SELL")))
        .isEqualTo(5L);
    assertThat(this.tradeRepository.countWithInQuery(Arrays.asList("BUY", "SELL"))).isEqualTo(8L);
    assertThat(this.tradeRepository.countWithInQuery(Collections.singletonList("BUY")))
        .isEqualTo(3L);
    assertThat(this.tradeRepository.countWithInQuery(Collections.singletonList("SELL")))
        .isEqualTo(5L);
    assertThat(this.tradeRepository.findByActionIn(Sets.newHashSet(Arrays.asList("BUY", "SELL"))))
        .hasSize(8);
    assertThat(this.tradeRepository.findByActionIn(Collections.singleton("BUY"))).hasSize(3);
    assertThat(this.tradeRepository.findByActionIn(Collections.singleton("SELL"))).hasSize(5);
  }

  @Test
  void queryMethodsTest_deleteByAction() {
    insertTrades("trader1", "BUY", 3);
    insertTrades("trader1", "SELL", 2);
    insertTrades("trader2", "SELL", 3);

    assertThat(this.tradeRepository.deleteByAction("BUY")).isEqualTo(3);
    assertThat(this.tradeRepository.count()).isEqualTo(5L);
  }

  @Test
  void queryMethodsTest_deleteBySymbol() {
    insertTrades("trader1", "SELL", 2);
    insertTrades("trader2", "SELL", 3);

    assertThat(this.tradeRepository.deleteBySymbol("ABCD")).hasSize(5);
    assertThat(this.tradeRepository.count()).isZero();
  }

  @Test
  void queryMethodsTest_deleteBySymbolAndAction() {
    insertTrades("trader1", "SELL", 2);
    insertTrades("trader2", "SELL", 3);

    assertThat(this.tradeRepository.count()).isEqualTo(5L);
    this.tradeRepository.deleteBySymbolAndAction("ABCD", "SELL");
    assertThat(this.tradeRepository.count()).isZero();
  }

  @Test
  void queryMethodsTest_deleteAllById() {
    List<Trade> trades = insertTrades("trader1", "BUY", 5);

    Trade someTrade1 = trades.get(0);
    Trade someTrade2 = trades.get(1);
    Key key1 = this.spannerSchemaUtils.getKey(someTrade1);
    Key key2 = this.spannerSchemaUtils.getKey(someTrade2);

    assertThat(this.tradeRepository.count()).isEqualTo(5);

    SimpleSpannerRepository repo = AopTestUtils.getTargetObject(this.tradeRepository);
    repo.deleteAllById(Arrays.asList(key1, key2));

    assertThat(this.tradeRepository.findAll())
        .doesNotContainAnyElementsOf(Arrays.asList(someTrade1, someTrade2))
        .containsExactlyInAnyOrderElementsOf(trades.subList(2, 5));
  }

  @Test
  void queryMethodsTest_deleteAllById_doesNothingOnEmptyIds() {
    List<Trade> trades = insertTrades("trader1", "BUY", 5);
    assertThat(this.tradeRepository.count()).isEqualTo(5);

    this.tradeRepository.deleteAllById(new ArrayList<>());
    assertThat(this.tradeRepository.count()).isEqualTo(5);
  }

  @Test
  void queryMethodsTest_readsAndCounts() {
    List<Trade> trader1BuyTrades = insertTrades("trader1", "BUY", 3);
    List<Trade> trader1SellTrades = insertTrades("trader1", "SELL", 2);
    List<Trade> trader2Trades = insertTrades("trader2", "SELL", 3);

    Iterable<Trade> allTrades =
        Iterables.concat(trader1BuyTrades, trader1SellTrades, trader2Trades);
    assertThat(this.spannerOperations.readAll(Trade.class))
        .containsExactlyInAnyOrderElementsOf(allTrades);

    assertThat(this.tradeRepository.countByAction("BUY")).isEqualTo(3);
    assertThat(this.tradeRepository.countByActionQuery("BUY")).isEqualTo(3);
    assertThat(this.tradeRepository.existsByActionQuery("BUY")).isTrue();

    assertThat(this.tradeRepository.getOneTrade("BUY")).isNotNull();

    assertThat(this.tradeRepository.getFirstString("BUY")).isEqualTo("BUY");
    assertThat(this.tradeRepository.getFirstStringList("BUY"))
        .containsExactlyInAnyOrder("BUY", "BUY", "BUY");
  }

  @Test
  void queryMethodsTest_Trader2() {
    List<Trade> trader2Trades = insertTrades("trader2", "SELL", 3);

    List<Trade> trader2TradesRetrieved = this.tradeRepository.findByTraderId("trader2");
    assertThat(trader2TradesRetrieved).containsExactlyInAnyOrderElementsOf(trader2Trades);

    assertThat(
            this.tradeRepository.findByTraderId(
                "trader2", PageRequest.of(0, 2, Sort.by("tradeTime"))))
        .containsExactlyInAnyOrder(trader2Trades.get(0), trader2Trades.get(1));

    assertThat(
            this.tradeRepository.findByTraderId(
                "trader2", PageRequest.of(1, 2, Sort.by("tradeTime"))))
        .containsExactlyInAnyOrder(trader2Trades.get(2));

    assertThat(
            this.tradeRepository.findByTraderId(
                "trader2", PageRequest.of(0, 2, Sort.by(Direction.DESC, "tradeTime"))))
        .containsExactlyInAnyOrder(trader2Trades.get(2), trader2Trades.get(1));

    assertThat(
            this.tradeRepository.findByTraderId(
                "trader2", PageRequest.of(1, 2, Sort.by(Direction.DESC, "tradeTime"))))
        .containsExactlyInAnyOrder(trader2Trades.get(0));

    assertThat(
            this.tradeRepository.findTop2ByTraderIdOrderByTradeTimeAsc(
                "trader2", Pageable.unpaged()))
        .containsExactlyInAnyOrder(trader2Trades.get(0), trader2Trades.get(1));

    assertThat(
            this.tradeRepository.findTop2ByTraderIdOrderByTradeTimeAsc(
                "trader2", PageRequest.of(0, 1)))
        .containsExactlyInAnyOrder(trader2Trades.get(0));

    assertThat(
            this.tradeRepository.findTop2ByTraderIdOrderByTradeTimeAsc(
                "trader2", PageRequest.of(0, 1, Sort.by(Direction.DESC, "tradeTime"))))
        .containsExactlyInAnyOrder(trader2Trades.get(2));
  }

  @Test
  void queryMethodsTest_caseSensitive() {
    insertTrades("trader1", "BUY", 3);

    List<TradeProjection> tradeProjectionsRetrieved =
        this.tradeRepository.findByActionIgnoreCase("bUy");
    assertThat(tradeProjectionsRetrieved).hasSize(3);
    for (TradeProjection tradeProjection : tradeProjectionsRetrieved) {
      assertThat(tradeProjection.getAction()).isEqualTo("BUY");
      assertThat(tradeProjection.getSymbolAndAction()).isEqualTo("ABCD BUY");
    }
  }

  @Test
  void queryMethodsTest_sortingAndPaging() {
    List<Trade> trader1BuyTrades = insertTrades("trader1", "BUY", 3);
    insertTrades("trader1", "SELL", 2);
    insertTrades("trader2", "SELL", 3);

    List<Trade> tradesReceivedPage0 =
        this.tradeRepository.findAll(PageRequest.of(0, 3, Sort.by(Order.asc("id")))).getContent();
    assertThat(tradesReceivedPage0).hasSize(3);
    assertThat(tradesReceivedPage0.get(0).getId()).isLessThan(tradesReceivedPage0.get(1).getId());
    assertThat(tradesReceivedPage0.get(1).getId()).isLessThan(tradesReceivedPage0.get(2).getId());

    List<Trade> tradesReceivedPage1 =
        this.tradeRepository.findAll(PageRequest.of(1, 3, Sort.by(Order.asc("id")))).getContent();
    assertThat(tradesReceivedPage1).hasSize(3);
    assertThat(tradesReceivedPage0.get(2).getId()).isLessThan(tradesReceivedPage1.get(0).getId());
    assertThat(tradesReceivedPage1.get(0).getId()).isLessThan(tradesReceivedPage1.get(1).getId());
    assertThat(tradesReceivedPage1.get(1).getId()).isLessThan(tradesReceivedPage1.get(2).getId());

    List<Trade> tradesReceivedPage2 =
        this.tradeRepository.findAll(PageRequest.of(2, 3, Sort.by(Order.asc("id")))).getContent();
    assertThat(tradesReceivedPage2).hasSize(2);
    assertThat(tradesReceivedPage1.get(2).getId()).isLessThan(tradesReceivedPage2.get(0).getId());
    assertThat(tradesReceivedPage2.get(0).getId()).isLessThan(tradesReceivedPage2.get(1).getId());

    List<Trade> buyTradesRetrieved =
        this.tradeRepository.annotatedTradesByAction(
            "BUY", PageRequest.of(0, 100, Sort.by(Order.desc("id"))));
    assertThat(buyTradesRetrieved).containsExactlyInAnyOrderElementsOf(trader1BuyTrades);
    assertThat(buyTradesRetrieved.get(0).getId()).isGreaterThan(buyTradesRetrieved.get(1).getId());
    assertThat(buyTradesRetrieved.get(1).getId()).isGreaterThan(buyTradesRetrieved.get(2).getId());
  }

  @Test
  void queryMethodsTest_CustomSort() {
    insertTrades("trader1", "BUY", 3);
    insertTrades("trader1", "SELL", 2);
    insertTrades("trader2", "SELL", 3);

    List<Trade> customSortedTrades =
        this.tradeRepository.sortedTrades(
            PageRequest.of(2, 2, org.springframework.data.domain.Sort.by(Order.asc("id"))));

    assertThat(customSortedTrades).hasSize(2);
    assertThat(customSortedTrades.get(0).getId()).isLessThan(customSortedTrades.get(1).getId());
  }

  @Test
  void queryMethodsTest_Wildcards() {
    insertTrades("trader1", "BUY", 3);

    this.tradeRepository
        .findBySymbolLike("%BCD")
        .forEach(x -> assertThat(x.getSymbol()).isEqualTo("ABCD"));
    assertThat(this.tradeRepository.findBySymbolNotLike("%BCD")).isEmpty();

    this.tradeRepository
        .findBySymbolContains("BCD")
        .forEach(x -> assertThat(x.getSymbol()).isEqualTo("ABCD"));
    assertThat(this.tradeRepository.findBySymbolNotContains("BCD")).isEmpty();

    assertThat(this.tradeRepository.findBySymbolAndActionPojo(new SymbolAction("ABCD", "BUY")))
        .hasSize(3);
    assertThat(
            this.tradeRepository.findBySymbolAndActionStruct(
                Struct.newBuilder().set("symbol").to("ABCD").set("action").to("BUY").build()))
        .hasSize(3);
  }

  @Test
  void queryMethodsTest_NullColumns() {
    insertTrades("trader1", "BUY", 3);

    Trade someTrade = this.tradeRepository.findBySymbolContains("ABCD").get(0);
    assertThat(someTrade.getExecutionTimes()).isNotNull();
    assertThat(someTrade.getSymbol()).isNotNull();
    someTrade.setExecutionTimes(null);
    someTrade.setSymbol(null);
    this.tradeRepository.save(someTrade);
    someTrade =
        this.tradeRepository
            .findById(this.spannerSchemaUtils.getKey(someTrade))
            .orElseThrow(() -> new AssertionError("did not find expected trade"));
    assertThat(someTrade.getExecutionTimes()).isNull();
    assertThat(someTrade.getSymbol()).isNull();
  }

  @Test
  void queryMethodsTest_ParentChildOperations() {
    insertTrades("trader1", "BUY", 3);

    Trade someTrade = this.tradeRepository.findBySymbolContains("ABCD").get(0);

    // testing parent-child relationships
    assertThat(someTrade.getSubTrades()).isEmpty();
    SubTrade subTrade1 =
        new SubTrade(someTrade.getTradeDetail().getId(), someTrade.getTraderId(), "subTrade1");
    SubTrade subTrade2 =
        new SubTrade(someTrade.getTradeDetail().getId(), someTrade.getTraderId(), "subTrade2");

    SubTradeComponent subTradeComponent11 =
        new SubTradeComponent(
            someTrade.getTradeDetail().getId(), someTrade.getTraderId(), "subTrade1", "11a", "11b");
    subTradeComponent11.setCommitTimestamp(Timestamp.ofTimeMicroseconds(11));

    SubTradeComponent subTradeComponent21 =
        new SubTradeComponent(
            someTrade.getTradeDetail().getId(), someTrade.getTraderId(), "subTrade2", "21a", "21b");
    subTradeComponent21.setCommitTimestamp(Timestamp.ofTimeMicroseconds(21));

    SubTradeComponent subTradeComponent22 =
        new SubTradeComponent(
            someTrade.getTradeDetail().getId(), someTrade.getTraderId(), "subTrade2", "22a", "22b");
    subTradeComponent22.setCommitTimestamp(Timestamp.ofTimeMicroseconds(22));

    subTrade1.setSubTradeComponentList(Collections.singletonList(subTradeComponent11));
    subTrade2.setSubTradeComponentList(Arrays.asList(subTradeComponent21, subTradeComponent22));
    someTrade.setSubTrades(Arrays.asList(subTrade1, subTrade2));

    this.tradeRepository.save(someTrade);

    assertThat(this.subTradeRepository.count()).isEqualTo(2);
    assertThat(this.subTradeComponentRepository.count()).isEqualTo(3);

    Iterable<SubTradeComponent> subTradeComponents = this.subTradeComponentRepository.findAll();
    Timestamp expectedTimestamp = subTradeComponents.iterator().next().getCommitTimestamp();

    assertThat(subTradeComponents)
        .hasSize(3)
        .extracting(SubTradeComponent::getCommitTimestamp)
        .allSatisfy(
            ts ->
                assertThat(ts)
                    .isEqualTo(expectedTimestamp)
                    .isGreaterThan(Timestamp.ofTimeMicroseconds(22)));

    this.subTradeRepository.deleteById(this.spannerSchemaUtils.getKey(subTrade1));
    assertThat(this.subTradeComponentRepository.count()).isEqualTo(2);

    someTrade =
        this.tradeRepository
            .findById(this.spannerSchemaUtils.getKey(someTrade))
            .orElseThrow(() -> new AssertionError("did not find expected trade"));
    assertThat(someTrade.getSubTrades())
        .hasSize(1)
        .first()
        .hasFieldOrPropertyWithValue("subTradeId", "subTrade2")
        .extracting(SubTrade::getSubTradeComponentList)
        .asList()
        .hasSize(2);

    this.tradeRepository.delete(someTrade);

    assertThat(this.subTradeComponentRepository.count()).isZero();
    assertThat(this.subTradeRepository.count()).isZero();
  }

  @Test
  void queryMethodsTest_EagerFetch() {
    final Trade aTrade = Trade.makeTrade("trader1", 0, 0);
    aTrade.setAction("BUY");
    aTrade.setSymbol("ABCD");
    this.tradeRepository.save(aTrade);

    assertThat(tradeRepository.fetchById(aTrade.getId()))
        .isNotEmpty()
        .hasValueSatisfying(t -> assertThat(t.getId()).isEqualTo(aTrade.getId()))
        .hasValueSatisfying(t -> assertThat(t.getTraderId()).isEqualTo(aTrade.getTraderId()))
        .hasValueSatisfying(t -> assertThat(t.getSymbol()).isEqualTo(aTrade.getSymbol()))
        .hasValueSatisfying(
            t -> assertThat(t.getSubTrades()).hasSize(aTrade.getSubTrades().size()));
  }

  @Test
  void queryMethodsTest_SoftDelete() {
    Trade someTrade = insertTrade("trader1", "BUY", 1);
    SubTrade subTrade1 =
        new SubTrade(someTrade.getTradeDetail().getId(), someTrade.getTraderId(), "subTrade1");
    SubTrade subTrade2 =
        new SubTrade(someTrade.getTradeDetail().getId(), someTrade.getTraderId(), "subTrade2");
    someTrade.setSubTrades(Arrays.asList(subTrade1, subTrade2));
    this.tradeRepository.save(someTrade);

    assertThat(this.tradeRepository.count()).isEqualTo(1L);

    assertThat(tradeRepository.findAll())
        .isNotNull()
        .hasSize(1)
        .first()
        .extracting(Trade::getSubTrades)
        .asList()
        .hasSize(2);

    subTrade1.setDisabled(true); // a soft-delete
    subTradeRepository.save(subTrade1);

    Trade gotTrade =
        this.tradeRepository
            .findById(this.spannerSchemaUtils.getKey(someTrade))
            .orElseThrow(() -> new AssertionError("did not find expected trade"));
    assertThat(gotTrade.getSubTrades())
        .doesNotContain(subTrade1) // "subTrade1" was soft-deleted
        .hasSize(1);
  }

  @Test
  void existsTest() {
    Trade trade = Trade.makeTrade();
    this.tradeRepository.save(trade);
    SpannerPersistentEntity<?> persistentEntity =
        this.spannerMappingContext.getPersistentEntity(Trade.class);
    PersistentPropertyAccessor accessor = persistentEntity.getPropertyAccessor(trade);
    PersistentProperty idProperty = persistentEntity.getIdProperty();
    Key key = (Key) accessor.getProperty(idProperty);
    assertThat(this.tradeRepository.existsById(key)).isTrue();
    this.tradeRepository.delete(trade);
    assertThat(this.tradeRepository.existsById(key)).isFalse();
  }

  @Test
  void testNonNull() {
    assertThatThrownBy(() -> this.tradeRepository.getByAction("non-existing-action"))
        .isInstanceOf(EmptyResultDataAccessException.class)
        .hasMessageMatching("Result must not be null");
  }

  @Test
  void testWithJsonField() {
    Trade trade1 = Trade.makeTrade();
    trade1.setOptionalDetails(new Details("abc", "def"));
    trade1.setBackupDetails(new Details("backup context", "backup context continued"));
    Trade trade2 = Trade.makeTrade();
    trade2.setOptionalDetails(new Details("some context", null));
    Trade trade3 = Trade.makeTrade();
    this.tradeRepository.save(trade1);
    this.tradeRepository.save(trade2);
    this.tradeRepository.save(trade3);

    assertThat(this.tradeRepository.findAll()).contains(trade1, trade2, trade3);
    assertThat(this.tradeRepository.getByDetailP1("abc")).hasSize(1).contains(trade1);

    String traderId = trade1.getTraderId();
    Optional<Details> optionalDetails = this.tradeRepository.getOptionalDetailsById(traderId);
    assertThat(optionalDetails).isEqualTo(Optional.of(new Details("abc", "def")));

    String traderId3 = trade3.getTraderId();
    Optional<Details> empty = this.tradeRepository.getOptionalDetailsById(traderId3);
    assertThat(empty).isNotPresent();
  }

  @Test
  void testWithArrayJsonField() {
    Details details1 = new Details("abc", "def");
    Details details2 = new Details("123", "234");
    Trade trade1 = Trade.makeTrade();
    trade1.setAdditionalDetails(Arrays.asList(details1, details2));
    Trade trade2 = Trade.makeTrade();
    trade2.setAdditionalDetails(null);
    Trade trade3 = Trade.makeTrade();
    trade3.setOptionalDetails(details1);

    this.tradeRepository.save(trade1);
    this.tradeRepository.save(trade2);
    this.tradeRepository.save(trade3);

    assertThat(this.tradeRepository.findAll()).contains(trade1, trade2, trade3);

    String traderId = trade1.getTraderId();
    List<List<Details>> detailsList = this.tradeRepository.getAdditionalDetailsById(traderId);
    assertThat(detailsList.get(0)).containsExactly(details1, details2);

    String traderId3 = trade3.getTraderId();
    Optional<Details> optionalDetails = this.tradeRepository.getOptionalDetailsById(traderId3);
    assertThat(optionalDetails).isEqualTo(Optional.of(details1));
  }

  @Test
  void testTransaction() {
    this.tradeRepositoryTransactionalService.testTransactionalAnnotation(2);
    assertThat(this.tradeRepository.count()).isEqualTo(1L);
  }

  @Test
  void testTransactionRolledBack() {
    assertThat(this.tradeRepository.count()).isZero();
    try {
      this.tradeRepositoryTransactionalService.testTransactionRolledBack();
    } catch (RuntimeException re) {
      // expected exception that causes roll-back;
    }
    assertThat(this.tradeRepository.count()).isZero();
  }

  @Test
  void findAllByIdReturnsOnlyRequestedRows() {
    insertTrade("trader1", "BUY", 100);
    Trade trade2 = insertTrade("trader2", "BUY", 101);
    Trade trade3 = insertTrade("trader2", "SELL", 102);
    insertTrade("trader2", "SELL", 103);

    Iterable<Trade> foundTrades =
        this.tradeRepository.findAllById(
            Arrays.asList(
                Key.of(trade2.getTradeDetail().getId(), trade2.getTraderId()),
                Key.of(trade3.getTradeDetail().getId(), trade3.getTraderId())));

    assertThat(foundTrades).containsExactlyInAnyOrder(trade2, trade3);
  }

  @Test
  void findAllByIdReturnsNothingOnEmptyRequestIterable() {
    insertTrade("trader1", "BUY", 100);
    insertTrade("trader2", "BUY", 101);

    Iterable<Trade> foundTrades = this.tradeRepository.findAllById(new ArrayList<>());

    assertThat(foundTrades).isEmpty();
  }

  @Test
  void findAllByActionIsNotNull() {
    insertTrades("trader1", "SELL", 2);
    insertTrades("trader2", null, 3);

    assertThat(this.tradeRepository.count()).isEqualTo(5L);

    List<Trade> tradesWithActionNotNull = this.tradeRepository.findAllByActionIsNotNull("not used");
    assertThat(tradesWithActionNotNull).hasSize(2);
  }

  @Test
  void queryMethodsTest_sqlQueryReturnNull() {
    Trade sellTradeWithNullSymbol = Trade.makeTrade("trader1", 0, 4);
    sellTradeWithNullSymbol.setAction("SELL");
    sellTradeWithNullSymbol.setSymbol(null);
    this.spannerOperations.insert(sellTradeWithNullSymbol);

    Optional<String> symbol = this.tradeRepository.getSymbolById(sellTradeWithNullSymbol.getId());
    assertThat(symbol).isEmpty();
  }

  private List<Trade> insertTrades(String traderId, String action, int numTrades) {
    List<Trade> trades = new ArrayList<>();
    for (int i = 0; i < numTrades; i++) {
      trades.add(insertTrade(traderId, action, i));
    }
    return trades;
  }

  private Trade insertTrade(String traderId, String action, int tradeTime) {
    Trade t = Trade.makeTrade(traderId, 0, tradeTime);
    t.setAction(action);
    t.setSymbol("ABCD");
    this.spannerOperations.insert(t);
    return t;
  }

  /** A service that executes methods annotated as transactional. */
  public static class TradeRepositoryTransactionalService {

    @Autowired TradeRepository tradeRepository;

    @Transactional
    public void testTransactionalAnnotation(int numSubTrades) {
      Trade trade = Trade.makeTrade(null, numSubTrades);
      this.tradeRepository.save(trade);
      // because the insert happens within the same transaction, this count is still
      // 1
      assertThat(this.tradeRepository.count()).isZero();
    }

    @Transactional
    public void testTransactionRolledBack() {
      Trade trade = Trade.makeTrade();
      this.tradeRepository.save(trade);
      throw new RuntimeException("Intentional error to rollback save.");
    }
  }
}
