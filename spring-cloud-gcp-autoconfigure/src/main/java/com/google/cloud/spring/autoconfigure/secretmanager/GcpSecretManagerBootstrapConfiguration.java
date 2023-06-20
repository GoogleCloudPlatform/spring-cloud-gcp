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

package com.google.cloud.spring.autoconfigure.secretmanager;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.secretmanager.SecretManagerPropertySourceLocator;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Bootstrap Autoconfiguration for GCP Secret Manager which enables loading secrets as properties
 * into the application {@link org.springframework.core.env.Environment}.
 *
 * @since 1.2.2
 * @deprecated since external resources should be using Spring Boot's Config Data API, more info in
 *     <a href="https://spring.io/blog/2020/08/14/config-file-processing-in-spring-boot-2-4">here</a>.
 */
@Deprecated
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GcpSecretManagerProperties.class)
@ConditionalOnClass({SecretManagerServiceClient.class, SecretManagerTemplate.class})
@ConditionalOnProperty(value = "spring.cloud.gcp.secretmanager.enabled", matchIfMissing = true)
public class GcpSecretManagerBootstrapConfiguration {

  private final GcpProjectIdProvider gcpProjectIdProvider;
  private final GcpSecretManagerProperties properties;

  public GcpSecretManagerBootstrapConfiguration(
      GcpSecretManagerProperties properties, ConfigurableEnvironment configurableEnvironment) {

    this.properties = properties;
    this.gcpProjectIdProvider =
        properties.getProjectId() != null
            ? properties::getProjectId
            : new DefaultGcpProjectIdProvider();
  }

  @Bean
  @ConditionalOnMissingBean
  public CredentialsProvider googleCredentials(GcpSecretManagerProperties secretManagerProperties)
      throws IOException {
    return new DefaultCredentialsProvider(secretManagerProperties);
  }

  @Bean
  @ConditionalOnMissingBean
  public SecretManagerServiceClient secretManagerClient(CredentialsProvider googleCredentials)
      throws IOException {
    SecretManagerServiceSettings settings =
        SecretManagerServiceSettings.newBuilder()
            .setCredentialsProvider(googleCredentials)
            .setHeaderProvider(
                new UserAgentHeaderProvider(GcpSecretManagerBootstrapConfiguration.class))
            .build();

    return SecretManagerServiceClient.create(settings);
  }

  @Bean
  @ConditionalOnMissingBean
  public SecretManagerTemplate secretManagerTemplate(SecretManagerServiceClient client) {
    return new SecretManagerTemplate(client, this.gcpProjectIdProvider)
        .setAllowDefaultSecretValue(this.properties.isAllowDefaultSecret());
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(value = "spring.cloud.gcp.secretmanager.legacy", matchIfMissing = true)
  public SecretManagerPropertySourceLocator secretManagerPropertySourceLocator(
      SecretManagerTemplate secretManagerTemplate) {
    return new SecretManagerPropertySourceLocator(secretManagerTemplate, this.gcpProjectIdProvider);
  }
}
