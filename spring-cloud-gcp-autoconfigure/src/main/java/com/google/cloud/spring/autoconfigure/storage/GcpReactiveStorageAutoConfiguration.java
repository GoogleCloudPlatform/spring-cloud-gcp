/*
 * Copyright 2017-2023 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.storage;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.spring.autoconfigure.core.GcpProperties;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.ReactiveTokenProvider;
import com.google.cloud.spring.storage.GoogleStorageProtocolResolver;
import com.google.cloud.spring.storage.GoogleStorageTemplate;
import java.io.IOException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@ConditionalOnClass(WebClient.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.storage.reactive.enabled", matchIfMissing = true)
@EnableConfigurationProperties({GcpProperties.class, GcpStorageProperties.class})
@Import(GoogleStorageProtocolResolver.class)
public class GcpReactiveStorageAutoConfiguration {

  private final GcpProjectIdProvider gcpProjectIdProvider;

  private final CredentialsProvider credentialsProvider;

  public GcpReactiveStorageAutoConfiguration(
      GcpProjectIdProvider coreProjectIdProvider,
      CredentialsProvider credentialsProvider,
      GcpStorageProperties gcpStorageProperties)
      throws IOException {

    this.gcpProjectIdProvider =
        gcpStorageProperties.getProjectId() != null
            ? gcpStorageProperties::getProjectId
            : coreProjectIdProvider;

    this.credentialsProvider =
        gcpStorageProperties.getCredentials().hasKey()
            ? new DefaultCredentialsProvider(gcpStorageProperties)
            : credentialsProvider;
  }

  @Bean
  @ConditionalOnMissingBean
  public WebClient webClient() throws IOException {
    return WebClient.builder().build();
  }

  @Bean
  @ConditionalOnMissingBean
  public ReactiveTokenProvider reactiveTokenProvider(WebClient webClient) throws IOException {
    return ReactiveTokenProvider.create(credentialsProvider.getCredentials(), webClient);
  }

  @Bean
  @ConditionalOnMissingBean
  public GoogleStorageTemplate storage(WebClient webClient,
      ReactiveTokenProvider reactiveTokenProvider) throws IOException {
    return new GoogleStorageTemplate(webClient, reactiveTokenProvider);
  }

}
