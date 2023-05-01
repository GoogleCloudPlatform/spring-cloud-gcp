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

import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import com.google.cloud.spring.data.spanner.repository.config.SpannerRepositoryConfigurationExtension;
import com.google.cloud.spring.data.spanner.repository.support.SpannerRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

/**
 * Enables autoconfiguration for {@link
 * com.google.cloud.spring.data.spanner.repository.config.EnableSpannerRepositories}.
 */
@AutoConfiguration
@ConditionalOnClass(SpannerRepository.class)
@ConditionalOnMissingBean({
  SpannerRepositoryFactoryBean.class,
  SpannerRepositoryConfigurationExtension.class
})
@ConditionalOnProperty(value = "spring.cloud.gcp.spanner.enabled", matchIfMissing = true)
@Import({SpannerRepositoriesAutoConfigureRegistrar.class})
@AutoConfigureBefore(GcpSpannerAutoConfiguration.class)
public class SpannerRepositoriesAutoConfiguration {}
