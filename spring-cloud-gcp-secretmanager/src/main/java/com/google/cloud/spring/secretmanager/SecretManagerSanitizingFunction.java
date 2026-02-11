/*
 * Copyright 2017-2026 the original author or authors.
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

import org.springframework.boot.actuate.endpoint.SanitizableData;
import org.springframework.boot.actuate.endpoint.SanitizingFunction;
import org.springframework.core.env.PropertySource;

/**
 * Sanitize data with secrets from GCP Secret Manager.
 *
 * @since 5.10.0
 */
public class SecretManagerSanitizingFunction implements SanitizingFunction {

    @Override
    public SanitizableData apply(SanitizableData data) {
        PropertySource<?> propertySource = data.getPropertySource();

        if (propertySource == null || data.getValue() == null) {
            return data;
        }

        // On récupère la valeur "non-résolue" (ex: ${sm://mon-secret})
        Object unresolvedValue = propertySource.getProperty(data.getKey());

        if (unresolvedValue instanceof String stringValue) {
            for (String secretManagerPrefix : SecretManagerSyntaxUtils.PREFIXES) {
                if (stringValue.contains("${" + secretManagerPrefix)) {
                    // Si la valeur contient un préfixe Secret Manager, on retourne l'expression
                    // au lieu du secret réel pour le masquer.
                    return data.withValue(stringValue);
                }
            }
        }

        return data;
    }
}