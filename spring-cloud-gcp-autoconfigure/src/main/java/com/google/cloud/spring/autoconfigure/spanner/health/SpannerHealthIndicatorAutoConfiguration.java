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

package com.google.cloud.spring.autoconfigure.spanner.health;

import com.google.cloud.spring.autoconfigure.spanner.GcpSpannerAutoConfiguration;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import java.util.Map;
import org.springframework.boot.actuate.autoconfigure.health.CompositeHealthContributorConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.autoconfigure.health.HealthContributorAutoConfiguration;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

/**
 * {@link HealthContributorAutoConfiguration Auto-configuration} for
 * {@link SpannerHealthIndicator}.
 *
 * @since 2.0.6
 */
@AutoConfiguration
@ConditionalOnClass({SpannerTemplate.class, HealthIndicator.class})
@ConditionalOnBean(SpannerTemplate.class)
@ConditionalOnEnabledHealthIndicator("spanner")
@AutoConfigureBefore(HealthContributorAutoConfiguration.class)
@AutoConfigureAfter(GcpSpannerAutoConfiguration.class)
@EnableConfigurationProperties(SpannerHealthIndicatorProperties.class)
public class SpannerHealthIndicatorAutoConfiguration
    extends CompositeHealthContributorConfiguration<SpannerHealthIndicator, SpannerTemplate> {

  public SpannerHealthIndicatorAutoConfiguration(
      SpannerHealthIndicatorProperties spannerHealthProperties) {
    super(spannerTemplate ->
        new SpannerHealthIndicator(
            spannerTemplate,
            spannerHealthProperties.getQuery()));
  }

  @Bean
  @ConditionalOnMissingBean(name = {"spannerHealthIndicator", "spannerHealthContributor"})
  public HealthContributor spannerHealthContributor(Map<String, SpannerTemplate> spannerTemplates) {
    Assert.notNull(spannerTemplates, "SpannerTemplates must be provided");
    return createContributor(spannerTemplates);
  }
}
