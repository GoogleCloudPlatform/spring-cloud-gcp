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

package com.google.cloud.spring.secretmanager.it;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpEnvironmentProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpEnvironmentProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.secretmanager.SecretManagerServiceClientFactory;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import com.google.protobuf.ByteString;
import java.io.IOException;
import javax.annotation.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ObjectUtils;

@Configuration
public class SecretManagerRegionalTestConfiguration {

  /** Default value for the latest version of the secret. */
  public static final String GLOBAL_LOCATION = "global";

  private final GcpProjectIdProvider projectIdProvider;

  private final CredentialsProvider credentialsProvider;

  public SecretManagerRegionalTestConfiguration(ConfigurableEnvironment configurableEnvironment)
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
  public SecretManagerServiceClientFactory secretManagerServiceClientFactory() throws IOException {
    return new SecretManagerServiceClientFactory() {
      @Override
      public SecretManagerServiceClient getClient(@Nullable String location) {
        if (ObjectUtils.isEmpty(location)) {
          location = GLOBAL_LOCATION;
        }
        try {
          SecretManagerServiceSettings.Builder settings =
              SecretManagerServiceSettings.newBuilder()
                  .setCredentialsProvider(credentialsProvider);
          if (!location.equals(GLOBAL_LOCATION)) {
            settings.setEndpoint(String.format("secretmanager.%s.rep.googleapis.com:443", location));
          }
          return SecretManagerServiceClient.create(settings.build());
        } catch (IOException e) {
          throw new RuntimeException(
              "Failed to create SecretManagerServiceClient for location: " + location, e);
        }
      }
    };
  }

  @Bean
  public SecretManagerTemplate secretManagerTemplate(
      SecretManagerServiceClientFactory secretManagerServiceClientFactory) {
    return new SecretManagerTemplate(secretManagerServiceClientFactory, this.projectIdProvider);
  }
}
