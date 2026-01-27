/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.secretmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.endpoint.SanitizableData;
import org.springframework.core.env.PropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link SecretManagerSanitizingFunction}.
 */
class SecretManagerSanitizingFunctionTests {

    private final SecretManagerSanitizingFunction sanitizingFunction = new SecretManagerSanitizingFunction();

    @Test
    void testApply_sanitizesSecretManagerProperty() {
        String key = "my.secret";
        String unresolvedValue = "${sm://my-password}";
        String resolvedValue = "super-secret-123";

        PropertySource<?> mockSource = mock(PropertySource.class);
        when(mockSource.getProperty(key)).thenReturn(unresolvedValue);

        SanitizableData data = new SanitizableData(mockSource, key, resolvedValue);
        SanitizableData result = sanitizingFunction.apply(data);

        // Vérifie que la valeur affichée est l'expression ${sm://...} et non le secret réel
        assertThat(result.getValue()).isEqualTo(unresolvedValue);
    }

    @Test
    void testApply_doesNotSanitizeRegularProperty() {
        String key = "normal.prop";
        String value = "normal-value";

        PropertySource<?> mockSource = mock(PropertySource.class);
        when(mockSource.getProperty(key)).thenReturn(value);

        SanitizableData data = new SanitizableData(mockSource, key, value);
        SanitizableData result = sanitizingFunction.apply(data);

        // Vérifie qu'on ne touche pas aux propriétés classiques
        assertThat(result.getValue()).isEqualTo(value);
    }
}