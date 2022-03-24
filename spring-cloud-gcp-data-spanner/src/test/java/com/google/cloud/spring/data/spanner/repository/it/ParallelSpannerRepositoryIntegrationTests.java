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

import com.google.cloud.spanner.KeySet;
import com.google.cloud.spring.data.spanner.test.AbstractSpannerIntegrationTest;
import com.google.cloud.spring.data.spanner.test.domain.Trade;
import com.google.cloud.spring.data.spanner.test.domain.TradeRepository;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests multiple threads using a single repository instance. */
@EnabledIfSystemProperty(named = "it.spanner", matches = "true")
@ExtendWith(SpringExtension.class)
class ParallelSpannerRepositoryIntegrationTests extends AbstractSpannerIntegrationTest {

  private static final int PARALLEL_OPERATIONS = 2;

  @Autowired TradeRepository tradeRepository;

  @BeforeEach
  @AfterEach
  void cleanUpData() {
    this.spannerOperations.delete(Trade.class, KeySet.all());
  }

  @Test
  void testParallelOperations() {

    this.tradeRepository.performReadWriteTransaction(
        repo -> {
          executeInParallel(
              unused -> {
                repo.save(Trade.makeTrade());

                // all of the threads are using the same transaction at the same time, so they all
                // still
                // see empty table
                assertThat(repo.count()).isZero();
              });
          assertThat(repo.count()).isZero();
          return 0;
        });

    executeInParallel(
        unused ->
            assertThat(this.tradeRepository.countByAction("BUY")).isEqualTo(PARALLEL_OPERATIONS));

    executeInParallel(
        index ->
            this.tradeRepository.updateActionTradeById(
                ((List<Trade>) this.tradeRepository.findAll()).get(index).getId(), "SELL"));

    executeInParallel(
        unused ->
            assertThat(this.tradeRepository.countByAction("SELL")).isEqualTo(PARALLEL_OPERATIONS));

    executeInParallel(
        unused ->
            assertThat(this.tradeRepository.countByActionQuery("SELL"))
                .isEqualTo(PARALLEL_OPERATIONS));
  }

  private void executeInParallel(IntConsumer function) {
    IntStream.range(0, PARALLEL_OPERATIONS).parallel().forEach(function);
  }
}
