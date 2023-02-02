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

import static com.google.cloud.spring.autoconfigure.secretmanager.SecretManagerConfigDataLocationResolver.PREFIX;

import com.google.protobuf.ByteString;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Registers converters used by Spring Cloud GCP Secret Manager.
 */
public class GcpSecretManagerEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

  static final String IMPORT_PROPERTY = "spring.config.import";

  @Override
  public int getOrder() {
    return ConfigDataEnvironmentPostProcessor.ORDER - 1;
  }

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    boolean isSecretManagerEnabled =
        Boolean.parseBoolean(
            environment.getProperty("spring.cloud.gcp.secretmanager.enabled", "true"));

    if (isSecretManagerEnabled) {
      // Add a `spring.config.import=sm://` property by default.
      String externalImport = environment.getProperty(IMPORT_PROPERTY, "");
      if (!externalImport.contains(PREFIX)) {
        // If the property key exists, append a separator to the value.
        if (!externalImport.isEmpty()) {
          externalImport = externalImport.concat(";");
        }
        environment
            .getPropertySources()
            .addFirst(
                new MapPropertySource("secret-manager",
                    Map.of(IMPORT_PROPERTY, externalImport.concat(PREFIX))));
      }

      // Registers {@link ByteString} type converters to convert to String and byte[].
      environment
          .getConversionService()
          .addConverter(
              new Converter<ByteString, String>() {
                @Override
                public String convert(ByteString source) {
                  return source.toStringUtf8();
                }
              });

      environment
          .getConversionService()
          .addConverter(
              new Converter<ByteString, byte[]>() {
                @Override
                public byte[] convert(ByteString source) {
                  return source.toByteArray();
                }
              });
    }
  }
}
