/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.spring.autoconfigure.parameter;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterManagerSettings;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import java.io.IOException;
import java.util.function.Function;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Bootstrap auto configuration for Google Cloud Parameter Starter.
 *
 * @since 1.1
 */
@AutoConfiguration
@ConditionalOnProperty(
    prefix = "spring.cloud.gcp.parameter",
    name = "enabled",
    havingValue = "true")
@EnableConfigurationProperties(GcpParameterProperties.class)
public class GcpParameterBootstrapConfiguration {

  private static final String DEFAULT_UNIVERSE_DOMAIN = "googleapis.com";
  private static final String UNIVERSE_DOMAIN_ENV_VAR = "GOOGLE_CLOUD_UNIVERSE_DOMAIN";

  /**
   * Resolves the universe domain from explicit configuration, falling back to {@code
   * GOOGLE_CLOUD_UNIVERSE_DOMAIN} and then {@code googleapis.com}.
   */
  static String resolveUniverseDomain(
      @Nullable String configuredUniverseDomain, Function<String, String> envProvider) {
    if (StringUtils.hasText(configuredUniverseDomain)) {
      return configuredUniverseDomain;
    }
    String envUniverseDomain = envProvider.apply(UNIVERSE_DOMAIN_ENV_VAR);
    if (StringUtils.hasText(envUniverseDomain)) {
      return envUniverseDomain;
    }
    return DEFAULT_UNIVERSE_DOMAIN;
  }

  /**
   * Builds {@link ParameterManagerSettings} configured with the resolved universe domain and
   * regional endpoint when applicable.
   */
  static ParameterManagerSettings buildParameterManagerSettings(
      GcpParameterProperties properties,
      CredentialsProvider credentialsProvider,
      Function<String, String> envProvider)
      throws IOException {
    String resolvedUniverseDomain =
        resolveUniverseDomain(properties.getUniverseDomain(), envProvider);
    ParameterManagerSettings.Builder settings =
        ParameterManagerSettings.newBuilder()
            .setCredentialsProvider(credentialsProvider)
            .setHeaderProvider(
                new UserAgentHeaderProvider(GcpParameterBootstrapConfiguration.class))
            .setUniverseDomain(resolvedUniverseDomain);

    if (StringUtils.hasText(properties.getLocation())
        && !properties.getLocation().equals("global")) {
      String apiEndpoint =
          String.format(
              "parametermanager.%s.rep.%s:443", properties.getLocation(), resolvedUniverseDomain);
      settings.setEndpoint(apiEndpoint);
    }

    return settings.build();
  }

  @Bean
  @ConditionalOnMissingBean
  public ParameterManagerClient parameterManagerClient(GcpParameterProperties properties)
      throws IOException {
    ParameterManagerSettings settings =
        buildParameterManagerSettings(
            properties, new DefaultCredentialsProvider(properties), System::getenv);
    return ParameterManagerClient.create(settings);
  }

  @Bean
  @ConditionalOnMissingBean
  public GoogleParameterPropertySourceLocator googleConfigPropertySourceLocator(
      GcpParameterProperties parameterProperties, ParameterManagerClient parameterManagerClient)
      throws IOException {
    return new GoogleParameterPropertySourceLocator(
        new DefaultGcpProjectIdProvider(),
        new DefaultCredentialsProvider(parameterProperties),
        parameterProperties,
        parameterManagerClient);
  }
}
