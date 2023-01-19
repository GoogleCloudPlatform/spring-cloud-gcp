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

package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests for the Spanner repository example. */
@EnabledIfSystemProperty(named = "it.spanner", matches = "true")
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpannerRepositoryIntegrationTests {
  @LocalServerPort
  private int port;

  @Autowired private TraderRepository traderRepository;

  @Autowired private TradeRepository tradeRepository;

  @Autowired private SpannerSchemaUtils spannerSchemaUtils;

  @Autowired private SpannerRepositoryExample spannerRepositoryExample;

  @BeforeEach
  @AfterEach
  void cleanupAndSetupTables() {
    this.spannerRepositoryExample.createTablesIfNotExists();
    this.tradeRepository.deleteAll();
    this.traderRepository.deleteAll();
  }

  @Test
  void testRestEndpoint() {
    this.spannerRepositoryExample.runExample();

    TestRestTemplate testRestTemplate = new TestRestTemplate();
    ResponseEntity<PagedModel<Trade>> tradesResponse =
        testRestTemplate.exchange(
            String.format("http://localhost:%s/trades", this.port),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {});
    assertThat(tradesResponse.getBody().getMetadata().getTotalElements()).isEqualTo(8);
  }

  @Test
  void testRestEndpointPut() {
    this.spannerRepositoryExample.runExample();

    TestRestTemplate testRestTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<Trader> tradesResponse =
        testRestTemplate.exchange(
            String.format("http://localhost:%s/traders/t123", this.port),
            HttpMethod.PUT,
            new HttpEntity<>(
                "{\"firstName\": \"John\", \"lastName\": \"Smith\", \"createdOn\": \"2000-01-02"
                    + "T03:04:05.000Z\", \"modifiedOn\": [\"2000-01-02T03:04:05.000Z\"]}",
                headers),
            new ParameterizedTypeReference<>() {});

    ZonedDateTime expectedUtcDate = ZonedDateTime.of(2000, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC);
    Timestamp expectedTimestamp = new Timestamp(expectedUtcDate.toEpochSecond() * 1000L);
    Trader expected =
        new Trader(
            "t123",
            "John",
            "Smith",
            expectedTimestamp,
            Collections.singletonList(expectedTimestamp));
    assertThat(tradesResponse.getBody()).isEqualTo(expected);
    assertThat(this.traderRepository.findAllById(Collections.singleton("t123")).iterator().next())
        .isEqualTo(expected);
  }

  @Test
  void testLoadsCorrectData() {
    assertThat(this.traderRepository.count()).isZero();
    assertThat(this.tradeRepository.count()).isZero();

    this.spannerRepositoryExample.runExample();
    List<String> traderIds = new ArrayList<>();
    this.traderRepository.findAll().forEach(t -> traderIds.add(t.getTraderId()));
    assertThat(traderIds)
        .containsExactlyInAnyOrder(
            "demo_trader1",
            "demo_trader2",
            "demo_trader3",
            "demo_trader_json1",
            "demo_trader_json2",
            "demo_trader_json3",
            "demo_trader_json4");

    assertThat(this.tradeRepository.findAll()).hasSize(8);

    assertThat(this.tradeRepository.findByActionAndSymbol(PageRequest.of(0, 1),
        "BUY",
        "STOCK1"
    )).hasSize(1);

    Set<String> tradeSpannerKeys = new HashSet<>();
    this.tradeRepository
        .findAll()
        .forEach(t -> tradeSpannerKeys.add(this.spannerSchemaUtils.getKey(t).toString()));

    assertThat(tradeSpannerKeys)
        .containsExactlyInAnyOrder(
            "[demo_trader1,1]",
            "[demo_trader1,2]",
            "[demo_trader1,3]",
            "[demo_trader2,1]",
            "[demo_trader2,2]",
            "[demo_trader2,3]",
            "[demo_trader3,1]",
            "[demo_trader3,2]");

    List<String> buyTradeIds = this.tradeRepository.getTradeIds("BUY");
    assertThat(buyTradeIds).hasSize(5);

    assertThat(this.traderRepository.findById("demo_trader1").get().getTrades()).hasSize(3);
  }

  @Test
  void testJsonAndArrayJsonFieldReadWrite() {

    Address address = new Address(5L, "address line", true);
    Trader trader = new Trader("demo_trader1", "John", "Doe",
        Arrays.asList(address, address, address));
    trader.setHomeAddress(address);
    this.traderRepository.save(trader);

    Trader traderFound = this.traderRepository.findById("demo_trader1").get();
    assertThat(traderFound.getTraderId()).isEqualTo(trader.getTraderId());
    assertThat(traderFound.getHomeAddress()).isEqualTo(address);
    assertThat(traderFound.getAddressList()).contains(address, address, address);
  }
}
