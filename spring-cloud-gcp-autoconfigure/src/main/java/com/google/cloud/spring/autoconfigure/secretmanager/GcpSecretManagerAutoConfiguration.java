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

package com.google.cloud.spring.autoconfigure.secretmanager;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import java.io.IOException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 *  Autoconfiguration for GCP Secret Manager.
 *
 * @since 4.0.1
 */
@AutoConfiguration
@EnableConfigurationProperties(GcpSecretManagerProperties.class)
@ConditionalOnClass(SecretManagerTemplate.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.secretmanager.enabled", matchIfMissing = true)
public class GcpSecretManagerAutoConfiguration {

  private final GcpProjectIdProvider gcpProjectIdProvider;
  private final GcpSecretManagerProperties properties;
  private final CredentialsProvider credentialsProvider;

  public GcpSecretManagerAutoConfiguration(
      CredentialsProvider credentialsProvider,
      GcpSecretManagerProperties properties,
      GcpProjectIdProvider projectIdProvider) {

    this.credentialsProvider = credentialsProvider;
    this.properties = properties;
    this.gcpProjectIdProvider =
        properties.getProjectId() != null
            ? properties::getProjectId
            : projectIdProvider;
  }

  @Bean
  @ConditionalOnMissingBean
  public SecretManagerServiceClient secretManagerClient()
      throws IOException {
    SecretManagerServiceSettings settings =
        SecretManagerServiceSettings.newBuilder()
            .setCredentialsProvider(this.credentialsProvider)
            .setHeaderProvider(
                new UserAgentHeaderProvider(GcpSecretManagerAutoConfiguration.class))
            .build();

    return SecretManagerServiceClient.create(settings);
  }

  @Bean
  @ConditionalOnMissingBean
  public SecretManagerTemplate secretManagerTemplate(SecretManagerServiceClient client) {
    return new SecretManagerTemplate(client, this.gcpProjectIdProvider)
        .setAllowDefaultSecretValue(this.properties.isAllowDefaultSecret());
  }
}
