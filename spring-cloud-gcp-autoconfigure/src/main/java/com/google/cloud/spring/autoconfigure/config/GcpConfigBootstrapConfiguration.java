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

package com.google.cloud.spring.autoconfigure.config;

import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import java.io.IOException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Bootstrap auto configuration for Google Cloud Runtime Configurator Starter.
 *
 * @since 1.1
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "spring.cloud.gcp.config", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(GcpConfigProperties.class)
public class GcpConfigBootstrapConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public GoogleConfigPropertySourceLocator googleConfigPropertySourceLocator(
      GcpConfigProperties configProperties) throws IOException {
    return new GoogleConfigPropertySourceLocator(
        new DefaultGcpProjectIdProvider(),
        new DefaultCredentialsProvider(configProperties),
        configProperties);
  }
}
