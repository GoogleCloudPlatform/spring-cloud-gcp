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
import com.google.api.gax.rpc.EndpointContext;
import com.google.auth.Credentials;
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

  @Bean
  @ConditionalOnMissingBean
  public ParameterManagerClient parameterManagerClient(GcpParameterProperties properties)
      throws IOException {
    return ParameterManagerClient.create(
        buildSettings(properties, new DefaultCredentialsProvider(properties), System::getenv)
            .build());
  }

  static ParameterManagerSettings.Builder buildSettings(
      GcpParameterProperties properties,
      CredentialsProvider credentialsProvider,
      Function<String, String> env) {
    ParameterManagerSettings.Builder settings =
        ParameterManagerSettings.newBuilder()
            .setCredentialsProvider(credentialsProvider)
            .setHeaderProvider(
                new UserAgentHeaderProvider(GcpParameterBootstrapConfiguration.class));

    // Only override universeDomain when explicitly set; otherwise let GAX resolve
    // GOOGLE_CLOUD_UNIVERSE_DOMAIN or default to googleapis.com.
    if (StringUtils.hasText(properties.getUniverseDomain())) {
      settings.setUniverseDomain(properties.getUniverseDomain());
    }

    // Calling setEndpoint() disables GAX endpoint templating, so interpolate the resolved
    // universe domain into regional endpoints instead of hardcoding googleapis.com.
    if (StringUtils.hasText(properties.getLocation())
        && !"global".equals(properties.getLocation())) {
      String universeDomain = resolveUniverseDomain(properties.getUniverseDomain(), env);
      settings.setUniverseDomain(universeDomain);
      settings.setEndpoint(
          String.format(
              "parametermanager.%s.rep.%s:443", properties.getLocation(), universeDomain));
    }
    return settings;
  }

  static String resolveUniverseDomain(String configured, Function<String, String> env) {
    if (StringUtils.hasText(configured)) {
      return configured;
    }
    String fromEnv = env.apply(EndpointContext.GOOGLE_CLOUD_UNIVERSE_DOMAIN);
    if (StringUtils.hasText(fromEnv)) {
      return fromEnv;
    }
    return Credentials.GOOGLE_DEFAULT_UNIVERSE;
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
