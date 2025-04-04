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

import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterManagerSettings;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import java.io.IOException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Bootstrap auto configuration for Google Cloud Parameter Starter.
 *
 * @since 1.1
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "spring.cloud.gcp.parameter", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(GcpParameterProperties.class)
public class GcpParameterBootstrapConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public ParameterManagerClient parameterManagerClient(GcpParameterProperties properties)
      throws IOException {
    ParameterManagerSettings.Builder settings =
        ParameterManagerSettings.newBuilder()
            .setCredentialsProvider(new DefaultCredentialsProvider(properties))
            .setHeaderProvider(
                new UserAgentHeaderProvider(GcpParameterBootstrapConfiguration.class));

    if (!properties.getLocation().equals("global")) {
      String apiEndpoint = String.format("parametermanager.%s.rep.googleapis.com:443", properties.getLocation());
      settings.setEndpoint(apiEndpoint);
    }

    return ParameterManagerClient.create(settings.build());
  }

  @Bean
  @ConditionalOnMissingBean
  public GoogleParameterPropertySourceLocator googleConfigPropertySourceLocator(
      GcpParameterProperties parameterProperties,
      ParameterManagerClient parameterManagerClient) throws IOException {
    return new GoogleParameterPropertySourceLocator(
        new DefaultGcpProjectIdProvider(),
        new DefaultCredentialsProvider(parameterProperties),
        parameterProperties,
        parameterManagerClient);
  }
}
