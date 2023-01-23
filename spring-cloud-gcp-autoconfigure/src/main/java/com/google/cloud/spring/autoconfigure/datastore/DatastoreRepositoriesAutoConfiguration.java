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

package com.google.cloud.spring.autoconfigure.datastore;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import com.google.cloud.spring.data.datastore.repository.config.DatastoreRepositoryConfigurationExtension;
import com.google.cloud.spring.data.datastore.repository.support.DatastoreRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

/**
 * Enables autoconfiguration for {@link
 * com.google.cloud.spring.data.datastore.repository.config.EnableDatastoreRepositories}.
 *
 * @since 1.1
 */
@AutoConfiguration
@ConditionalOnClass(DatastoreRepository.class)
@ConditionalOnMissingBean({
  DatastoreRepositoryFactoryBean.class,
  DatastoreRepositoryConfigurationExtension.class
})
@ConditionalOnProperty(value = "spring.cloud.gcp.datastore.enabled", matchIfMissing = true)
@Import({DatastoreRepositoriesAutoConfigureRegistrar.class})
@AutoConfigureBefore(GcpDatastoreAutoConfiguration.class)
public class DatastoreRepositoriesAutoConfiguration {}
