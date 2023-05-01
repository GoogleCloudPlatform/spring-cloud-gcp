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

package com.google.cloud.spring.autoconfigure.spanner;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spring.data.spanner.core.SpannerTransactionManager;
import java.util.function.Supplier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for {@link SpannerTransactionManager}.
 *
 * @since 1.1
 */
@AutoConfiguration
@ConditionalOnClass(SpannerTransactionManager.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.spanner.enabled", matchIfMissing = true)
@AutoConfigureBefore(TransactionAutoConfiguration.class)
public class SpannerTransactionManagerAutoConfiguration {

  private SpannerTransactionManagerAutoConfiguration() {

  }

  /** Config settings. */
  @AutoConfiguration
  static class DatabaseClientTransactionManagerConfiguration {

    private final Supplier<DatabaseClient> databaseClientProvider;

    private final TransactionManagerCustomizers transactionManagerCustomizers;

    DatabaseClientTransactionManagerConfiguration(
        Supplier<DatabaseClient> databaseClientProvider,
        ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
      this.databaseClientProvider = databaseClientProvider;
      this.transactionManagerCustomizers = transactionManagerCustomizers.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpannerTransactionManager spannerTransactionManager() {
      SpannerTransactionManager transactionManager =
          new SpannerTransactionManager(this.databaseClientProvider);
      if (this.transactionManagerCustomizers != null) {
        this.transactionManagerCustomizers.customize(transactionManager);
      }
      return transactionManager;
    }
  }
}
