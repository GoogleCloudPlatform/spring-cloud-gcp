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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.endpoint.SanitizableData;
import org.springframework.core.env.PropertySource;

/**
 * Unit tests for {@link SecretManagerSanitizingFunction}.
 */
class SecretManagerSanitizingFunctionTest {

  private final SecretManagerSanitizingFunction sanitizingFunction =
      new SecretManagerSanitizingFunction();

  @Test
  void sanitizesSmAtPrefixProperty() {
    String key = "my.secret";
    String unresolvedValue = "${sm@my-password}";
    String resolvedValue = "super-secret-123";

    PropertySource<?> mockSource = mock(PropertySource.class);
    when(mockSource.getProperty(key)).thenReturn(unresolvedValue);

    SanitizableData data = new SanitizableData(mockSource, key, resolvedValue);
    SanitizableData result = sanitizingFunction.apply(data);

    assertThat(result.getValue()).isEqualTo(unresolvedValue);
  }

  @Test
  void sanitizesSmSlashPrefixProperty() {
    String key = "my.secret";
    String unresolvedValue = "${sm://my-password}";
    String resolvedValue = "super-secret-123";

    PropertySource<?> mockSource = mock(PropertySource.class);
    when(mockSource.getProperty(key)).thenReturn(unresolvedValue);

    SanitizableData data = new SanitizableData(mockSource, key, resolvedValue);
    SanitizableData result = sanitizingFunction.apply(data);

    assertThat(result.getValue()).isEqualTo(unresolvedValue);
  }

  @Test
  void sanitizesCompositeValueContainingSmReference() {
    String key = "my.database.url";
    String unresolvedValue = "https://user:${sm@my-pass}@host/db";
    String resolvedValue = "https://user:secret123@host/db";

    PropertySource<?> mockSource = mock(PropertySource.class);
    when(mockSource.getProperty(key)).thenReturn(unresolvedValue);

    SanitizableData data = new SanitizableData(mockSource, key, resolvedValue);
    SanitizableData result = sanitizingFunction.apply(data);

    // The resolved URL (with embedded secret) should be replaced by the unresolved expression.
    assertThat(result.getValue()).isEqualTo(unresolvedValue);
  }

  @Test
  void doesNotSanitizeRegularProperty() {
    String key = "normal.prop";
    String value = "normal-value";

    PropertySource<?> mockSource = mock(PropertySource.class);
    when(mockSource.getProperty(key)).thenReturn(value);

    SanitizableData data = new SanitizableData(mockSource, key, value);
    SanitizableData result = sanitizingFunction.apply(data);

    assertThat(result.getValue()).isEqualTo(value);
  }

  @Test
  void returnsDataUnchangedWhenPropertySourceIsNull() {
    SanitizableData data = new SanitizableData(null, "my.key", "some-value");
    SanitizableData result = sanitizingFunction.apply(data);

    assertThat(result.getValue()).isEqualTo("some-value");
  }

  @Test
  void returnsDataUnchangedWhenValueIsNull() {
    PropertySource<?> mockSource = mock(PropertySource.class);
    when(mockSource.getProperty("my.key")).thenReturn("${sm@some-secret}");

    SanitizableData data = new SanitizableData(mockSource, "my.key", null);
    SanitizableData result = sanitizingFunction.apply(data);

    assertThat(result.getValue()).isNull();
  }

  @Test
  void returnsDataUnchangedWhenUnresolvedValueIsNull() {
    PropertySource<?> mockSource = mock(PropertySource.class);
    when(mockSource.getProperty("my.key")).thenReturn(null);

    SanitizableData data = new SanitizableData(mockSource, "my.key", "resolved-value");
    SanitizableData result = sanitizingFunction.apply(data);

    // No SM prefix detected — pass through unchanged.
    assertThat(result.getValue()).isEqualTo("resolved-value");
  }
}
