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

package com.google.cloud.spring.autoconfigure.parametermanager;

import com.google.protobuf.ByteString;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.ConfigurableEnvironment;

/** Registers converters used by Spring Cloud GCP Parameter Manager. */
public class GcpParameterManagerEnvironmentPostProcessor implements EnvironmentPostProcessor {

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    boolean isParameterManagerEnabled =
        Boolean.parseBoolean(
            environment.getProperty("spring.cloud.gcp.parametermanager.enabled", "true"));

    if (isParameterManagerEnabled) {
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
