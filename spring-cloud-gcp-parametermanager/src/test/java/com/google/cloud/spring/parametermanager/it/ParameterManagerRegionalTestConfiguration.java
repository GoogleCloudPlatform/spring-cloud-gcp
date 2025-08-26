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

package com.google.cloud.spring.parametermanager.it;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.parametermanager.v1.ParameterManagerClient;
import com.google.cloud.parametermanager.v1.ParameterManagerSettings;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpEnvironmentProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpEnvironmentProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.parametermanager.ParameterManagerClientFactory;
import com.google.cloud.spring.parametermanager.ParameterManagerTemplate;
import com.google.protobuf.ByteString;
import java.io.IOException;
import javax.annotation.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ObjectUtils;

@Configuration
public class ParameterManagerRegionalTestConfiguration {

  /** Default value for the latest version of the parameter. */
  public static final String GLOBAL_LOCATION = "global";

  private final GcpProjectIdProvider projectIdProvider;

  private final CredentialsProvider credentialsProvider;

  public ParameterManagerRegionalTestConfiguration(ConfigurableEnvironment configurableEnvironment)
      throws IOException {

    this.projectIdProvider = new DefaultGcpProjectIdProvider();
    this.credentialsProvider = new DefaultCredentialsProvider(Credentials::new);

    // Registers {@link ByteString} type converters to convert to String and byte[].
    configurableEnvironment
        .getConversionService()
        .addConverter(
            new Converter<ByteString, String>() {
              @Override
              public String convert(ByteString source) {
                return source.toStringUtf8();
              }
            });

    configurableEnvironment
        .getConversionService()
        .addConverter(
            new Converter<ByteString, byte[]>() {
              @Override
              public byte[] convert(ByteString source) {
                return source.toByteArray();
              }
            });
  }

  @Bean
  public GcpProjectIdProvider gcpProjectIdProvider() {
    return this.projectIdProvider;
  }

  @Bean
  public static GcpEnvironmentProvider gcpEnvironmentProvider() {
    return new DefaultGcpEnvironmentProvider();
  }

  @Bean
  public ParameterManagerClientFactory parameterManagerClientFactory() throws IOException {
    return location -> {
      if (ObjectUtils.isEmpty(location)) {
        location = GLOBAL_LOCATION;
      }
      try {
        ParameterManagerSettings.Builder settings =
            ParameterManagerSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider);
        if (!location.equals(GLOBAL_LOCATION)) {
          settings.setEndpoint(String.format("parametermanager.%s.rep.googleapis.com:443", location));
        }
        return ParameterManagerClient.create(settings.build());
      } catch (IOException e) {
        throw new RuntimeException(
            "Failed to create ParameterManagerClient for location: " + location, e);
      }
    };
  }

  @Bean
  public ParameterManagerTemplate parameterManagerTemplate(
      ParameterManagerClientFactory parameterManagerClientFactory) {
    return new ParameterManagerTemplate(parameterManagerClientFactory, this.projectIdProvider);
  }
}
