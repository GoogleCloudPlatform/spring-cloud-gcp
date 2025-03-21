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

package com.google.cloud.spring.autoconfigure.paramconfig;

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
 * Bootstrap auto configuration for Google Cloud ParamConfig Starter.
 *
 * @since 1.1
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "spring.cloud.gcp.paramconfig", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(GcpParamConfigProperties.class)
public class GcpParamConfigBootstrapConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public ParameterManagerClient parameterManagerClient(GcpParamConfigProperties properties)
      throws IOException {
    ParameterManagerSettings.Builder settings =
        ParameterManagerSettings.newBuilder()
            .setCredentialsProvider(new DefaultCredentialsProvider(properties))
            .setHeaderProvider(
                new UserAgentHeaderProvider(GcpParamConfigBootstrapConfiguration.class));

    if (!properties.getLocation().equals("global")) {
      String apiEndpoint = String.format("parametermanager.%s.rep.googleapis.com:443", properties.getLocation());
      settings.setEndpoint(apiEndpoint);
    }

    return ParameterManagerClient.create(settings.build());
  }

  @Bean
  @ConditionalOnMissingBean
  public GoogleParamConfigPropertySourceLocator googleConfigPropertySourceLocator(
      GcpParamConfigProperties paramConfigProperties,
      ParameterManagerClient parameterManagerClient) throws IOException {
    return new GoogleParamConfigPropertySourceLocator(
        new DefaultGcpProjectIdProvider(),
        new DefaultCredentialsProvider(paramConfigProperties),
        paramConfigProperties,
        parameterManagerClient);
  }
}
