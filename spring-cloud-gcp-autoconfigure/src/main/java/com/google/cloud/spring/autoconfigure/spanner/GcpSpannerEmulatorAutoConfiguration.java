/*
 * Copyright 2017-2020 the original author or authors.
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

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import java.io.IOException;
import java.util.Optional;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

/**
 * Provides auto-configuration to use the Spanner emulator if enabled.
 *
 * @since 1.2.3
 */
@AutoConfiguration
@AutoConfigureBefore({GcpSpannerAutoConfiguration.class, GcpContextAutoConfiguration.class})
@EnableConfigurationProperties(GcpSpannerProperties.class)
@ConditionalOnProperty(
    prefix = "spring.cloud.gcp.spanner.emulator",
    name = "enabled",
    havingValue = "true")
public class GcpSpannerEmulatorAutoConfiguration {

  private final GcpSpannerProperties properties;

  private final String projectId;

  private final CredentialsProvider credentialsProvider;

  public GcpSpannerEmulatorAutoConfiguration(
      GcpSpannerProperties properties, GcpProjectIdProvider projectIdProvider) {

    this.projectId =
        (properties.getProjectId() != null)
            ? properties.getProjectId()
            : projectIdProvider.getProjectId();

    this.properties = properties;

    this.credentialsProvider = NoCredentials::getInstance;
  }

  @Bean
  @ConditionalOnMissingBean
  public CredentialsProvider credentialsProvider() {
    return this.credentialsProvider;
  }

  @Bean
  @ConditionalOnMissingBean
  public SpannerOptions spannerOptions(Optional<SpannerOptionsCustomizer> customizer)
      throws IOException {
    Assert.notNull(
        this.properties.getEmulatorHost(), "`spring.cloud.gcp.spanner.emulator-host` must be set.");
    SpannerOptions.Builder builder = SpannerOptions.newBuilder();
    builder
        .setProjectId(this.projectId)
        .setCredentials(this.credentialsProvider.getCredentials())
        .setEmulatorHost(this.properties.getEmulatorHost());
    customizer.ifPresent(c -> c.apply(builder));
    return builder.build();
  }
}
