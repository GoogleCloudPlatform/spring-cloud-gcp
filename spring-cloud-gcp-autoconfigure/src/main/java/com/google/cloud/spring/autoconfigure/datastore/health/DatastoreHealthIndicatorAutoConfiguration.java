/*
 * Copyright 2018-2019 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.datastore.health;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.spring.autoconfigure.datastore.DatastoreProvider;
import com.google.cloud.spring.autoconfigure.datastore.GcpDatastoreAutoConfiguration;
import java.util.function.Supplier;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.autoconfigure.health.HealthContributorAutoConfiguration;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * {@link HealthContributorAutoConfiguration Auto-configuration} for {@link
 * DatastoreHealthIndicator}.
 *
 * @since 1.2
 */
@AutoConfiguration
@ConditionalOnClass({Datastore.class, HealthIndicator.class})
@ConditionalOnBean(value = Datastore.class, parameterizedContainer = Supplier.class)
@ConditionalOnEnabledHealthIndicator("datastore")
@AutoConfigureBefore(HealthContributorAutoConfiguration.class)
@AutoConfigureAfter(GcpDatastoreAutoConfiguration.class)
public class DatastoreHealthIndicatorAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public HealthIndicator datastoreHealthIndicator(DatastoreProvider datastore) {
    return new DatastoreHealthIndicator(datastore);
  }
}
