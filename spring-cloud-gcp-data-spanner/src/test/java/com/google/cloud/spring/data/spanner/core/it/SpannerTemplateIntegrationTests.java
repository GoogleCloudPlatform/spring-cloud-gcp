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

package com.google.cloud.spring.data.spanner.core.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spring.data.spanner.core.SpannerPageableQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerReadOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerDataException;
import com.google.cloud.spring.data.spanner.test.AbstractSpannerIntegrationTest;
import com.google.cloud.spring.data.spanner.test.domain.Details;
import com.google.cloud.spring.data.spanner.test.domain.Trade;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/** Integration tests that use many features of the Spanner Template. */
@EnabledIfSystemProperty(named = "it.spanner", matches = "true")
@ExtendWith(SpringExtension.class)
public class SpannerTemplateIntegrationTests extends AbstractSpannerIntegrationTest {

  @Autowired TemplateTransactionalService transactionalService;

  @Test
  void testReadOnlyOperation() {
    // Integration tests are configured with 10 sessions max. This will hang and fail if there
    // is a leak.
    for (int i = 0; i < 20; i++) {
      Awaitility.await()
          .atMost(10, TimeUnit.SECONDS)
          .until(
              () -> {
                this.transactionalService.testReadOnlyOperation();
                return true;
              });
    }
  }

  @Test
  void insertAndDeleteSequence() {

    this.spannerOperations.delete(Trade.class, KeySet.all());

    assertThat(this.spannerOperations.count(Trade.class)).isZero();

    Trade trade = Trade.makeTrade(null, 1);
    this.spannerOperations.insert(trade);
    assertThat(this.spannerOperations.count(Trade.class)).isEqualTo(1L);

    List<Trade> trades =
        this.spannerOperations.queryAll(Trade.class, new SpannerPageableQueryOptions());

    assertThat(trades).containsExactly(trade);

    Trade retrievedTrade =
        this.spannerOperations.read(Trade.class, Key.of(trade.getId(), trade.getTraderId()));
    assertThat(retrievedTrade).isEqualTo(trade);

    trades = this.spannerOperations.readAll(Trade.class);

    assertThat(trades).containsExactly(trade);

    Trade trade2 = Trade.makeTrade(null, 1);
    this.spannerOperations.insert(trade2);

    trades =
        this.spannerOperations.read(
            Trade.class,
            KeySet.newBuilder()
                .addKey(Key.of(trade.getId(), trade.getTraderId()))
                .addKey(Key.of(trade2.getId(), trade2.getTraderId()))
                .build());

    assertThat(trades).containsExactlyInAnyOrder(trade, trade2);

    this.spannerOperations.deleteAll(Arrays.asList(trade, trade2));
    assertThat(this.spannerOperations.count(Trade.class)).isZero();
  }

  @Test
  void insertAndDeleteWithJsonField() {

    this.spannerOperations.delete(Trade.class, KeySet.all());
    assertThat(this.spannerOperations.count(Trade.class)).isZero();

    Trade trade1 = Trade.makeTrade();
    trade1.setOptionalDetails(new Details("abc", "def"));
    Trade trade2 = Trade.makeTrade();
    trade2.setOptionalDetails(new Details("some context", null));

    this.spannerOperations.insert(trade1);
    this.spannerOperations.insert(trade2);
    assertThat(this.spannerOperations.count(Trade.class)).isEqualTo(2L);

    List<Trade> trades =
        this.spannerOperations.queryAll(Trade.class, new SpannerPageableQueryOptions());

    assertThat(trades).containsExactlyInAnyOrder(trade1, trade2);

    Trade retrievedTrade =
        this.spannerOperations.read(Trade.class, Key.of(trade1.getId(), trade1.getTraderId()));
    assertThat(retrievedTrade).isEqualTo(trade1);
    assertThat(retrievedTrade.getOptionalDetails()).isInstanceOf(Details.class);
    assertThat(retrievedTrade.getOptionalDetails()).isEqualTo(new Details("abc", "def"));

    this.spannerOperations.deleteAll(Arrays.asList(trade1, trade2));
    assertThat(this.spannerOperations.count(Trade.class)).isZero();
  }

  @Test
  void insertAndDeleteWithArrayJsonField() {

    this.spannerOperations.delete(Trade.class, KeySet.all());
    assertThat(this.spannerOperations.count(Trade.class)).isZero();

    Details details1 = new Details("abc", "def");
    Details details2 = new Details("123", "234");
    Trade trade1 = Trade.makeTrade();
    trade1.setAdditionalDetails(Arrays.asList(details1, details2));
    Trade trade2 = Trade.makeTrade();
    trade2.setAdditionalDetails(null);

    this.spannerOperations.insert(trade1);
    this.spannerOperations.insert(trade2);
    assertThat(this.spannerOperations.count(Trade.class)).isEqualTo(2L);

    List<Trade> trades =
        this.spannerOperations.queryAll(Trade.class, new SpannerPageableQueryOptions());

    assertThat(trades).containsExactlyInAnyOrder(trade1, trade2);

    Trade retrievedTrade =
        this.spannerOperations.read(Trade.class, Key.of(trade1.getId(), trade1.getTraderId()));
    assertThat(retrievedTrade).isEqualTo(trade1);
    assertThat(retrievedTrade.getAdditionalDetails()).isInstanceOf(List.class);
    assertThat(retrievedTrade.getAdditionalDetails()).containsExactly(details1, details2);

    this.spannerOperations.deleteAll(Arrays.asList(trade1, trade2));
    assertThat(this.spannerOperations.count(Trade.class)).isZero();
  }

  @Test
  void readWriteTransactionTest() {
    Trade trade = Trade.makeTrade();
    this.spannerOperations.performReadWriteTransaction(
        transactionOperations -> {
          long beforeCount = transactionOperations.count(Trade.class);
          transactionOperations.insert(trade);

          // because the insert happens within the same transaction, this count is unchanged.
          assertThat(transactionOperations.count(Trade.class)).isEqualTo(beforeCount);
          return null;
        });

    assertThat(this.spannerOperations.count(Trade.class)).isEqualTo(1L);
    this.transactionalService.testTransactionalAnnotation();
    assertThat(this.spannerOperations.count(Trade.class)).isEqualTo(2L);
  }

  @Test
  void readOnlyTransactionTest() {

    Trade trade = Trade.makeTrade();

    Function<SpannerTemplate, Void> operation =  transactionOperations -> {
      // cannot do mutate in a read-only transaction
      transactionOperations.insert(trade);
      return null;
    };
    SpannerReadOptions readOptions = new SpannerReadOptions();

    assertThatThrownBy(() -> this.spannerOperations.performReadOnlyTransaction(operation, readOptions))
            .isInstanceOf(SpannerDataException.class)
            .hasMessage("A read-only transaction template cannot perform mutations.");

  }

  /** a transactional service for testing annotated transaction methods. */
  public static class TemplateTransactionalService {

    @Autowired SpannerTemplate spannerTemplate;

    @Transactional
    public void testTransactionalAnnotation() {
      long beforeCount = this.spannerTemplate.count(Trade.class);
      Trade trade = Trade.makeTrade();
      this.spannerTemplate.insert(trade);

      // because the insert happens within the same transaction, this count is unchanged.
      assertThat(this.spannerTemplate.count(Trade.class)).isEqualTo(beforeCount);
    }

    @Transactional(readOnly = true)
    public void testReadOnlyOperation() {
      this.spannerTemplate.count(Trade.class);
    }
  }
}
