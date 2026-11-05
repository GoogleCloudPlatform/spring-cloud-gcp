/*
 * Copyright 2017-2026 the original author or authors.
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

import com.google.cloud.spring.secretmanager.SecretManagerSyntaxUtils;
import org.springframework.boot.actuate.endpoint.SanitizableData;
import org.springframework.boot.actuate.endpoint.SanitizingFunction;
import org.springframework.core.env.PropertySource;

/**
 * A {@link SanitizingFunction} that prevents GCP Secret Manager secrets from being exposed
 * in plain text via Spring Boot Actuator endpoints (e.g. {@code /actuator/env}).
 *
 * <p>When a property's unresolved value contains a Secret Manager reference such as
 * {@code ${sm@my-secret}} or {@code ${sm://my-secret}}, this function replaces the resolved
 * secret value with the unresolved expression, so that the actual secret is never surfaced
 * regardless of the value of {@code management.endpoint.env.show-values}.
 *
 * @since 6.4.0
 */
public class SecretManagerSanitizingFunction implements SanitizingFunction {

  @Override
  public SanitizableData apply(SanitizableData data) {
    PropertySource<?> propertySource = data.getPropertySource();

    if (propertySource == null || data.getValue() == null) {
      return data;
    }

    Object unresolvedValue = propertySource.getProperty(data.getKey());

    if (unresolvedValue instanceof String stringValue) {
      for (String prefix : SecretManagerSyntaxUtils.PREFIXES) {
        if (stringValue.contains("${" + prefix)) {
          // Replace the resolved secret with the unresolved SM expression so the
          // real secret is never surfaced in actuator output.
          return data.withValue(stringValue);
        }
      }
    }

    return data;
  }
}
