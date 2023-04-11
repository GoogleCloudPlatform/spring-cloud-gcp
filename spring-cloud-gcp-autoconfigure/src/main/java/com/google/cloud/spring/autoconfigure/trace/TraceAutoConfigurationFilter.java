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

package com.google.cloud.spring.autoconfigure.trace;

import java.util.Arrays;
import java.util.Set;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * Exclude Spring Boot AutoConfiguration classes as they provide incompatible beans when using Cloud
 * Trace.
 *
 * @since 4.1.2
 */
public class TraceAutoConfigurationFilter
    implements AutoConfigurationImportFilter, EnvironmentAware {

  private static final String DISABLE_SPRING_BOOT_PROPERTY_NAME =
      "spring.cloud.gcp.trace.disable-spring-boot-autoconfig";

  private static final Set<String> SHOULD_SKIP =
      Set.of(
          "org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration");

  private boolean skipImportFilter = false;

  @Override
  public boolean[] match(
      String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
    boolean[] matches = new boolean[autoConfigurationClasses.length];

    if (skipImportFilter) {
      Arrays.fill(matches, true);
    } else {
      for (int i = 0; i < autoConfigurationClasses.length; i++) {
        matches[i] =
            autoConfigurationClasses[i] == null
                || !SHOULD_SKIP.contains(autoConfigurationClasses[i]);
      }
    }

    return matches;
  }

  @Override
  public void setEnvironment(Environment environment) {
    // The default behavior is to disable the spring boot tracing autoconfiguration
    boolean shouldDisableSpringBootAutoconfiguration =
        environment.getProperty(DISABLE_SPRING_BOOT_PROPERTY_NAME, Boolean.class, true);
    this.skipImportFilter = !shouldDisableSpringBootAutoconfiguration;
  }
}
