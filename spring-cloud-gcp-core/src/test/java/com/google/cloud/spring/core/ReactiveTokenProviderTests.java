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

package com.google.cloud.spring.core;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.auth.oauth2.ComputeEngineTokenProvider;
import com.google.auth.oauth2.ServiceAccountTokenProvider;
import com.google.auth.oauth2.UserCredentialsTokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ReactiveTokenProviderTests {

    @Test
    void testCreateCacheable() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("fake-credential-key.json");
        ServiceAccountCredentials serviceAccountCredentials = ServiceAccountCredentials.fromStream(classPathResource.getInputStream());
        ReactiveTokenProvider reactiveTokenProvider = ReactiveTokenProvider.createCacheable(serviceAccountCredentials);
        assertThat(reactiveTokenProvider).isInstanceOf(CacheableTokenProvider.class);
    }

    @Test
    void testCreateUserCredentialsTokenProvider() {
        UserCredentials userCredentials = mock(UserCredentials.class);
        ReactiveTokenProvider reactiveTokenProvider = ReactiveTokenProvider.create(userCredentials);
        assertThat(reactiveTokenProvider).isInstanceOf(UserCredentialsTokenProvider.class);
    }

    @Test
    void testCreateServiceAccountTokenProvider() {
        ServiceAccountCredentials serviceAccountCredentials = mock(ServiceAccountCredentials.class);
        ReactiveTokenProvider reactiveTokenProvider = ReactiveTokenProvider.create(serviceAccountCredentials);
        assertThat(reactiveTokenProvider).isInstanceOf(ServiceAccountTokenProvider.class);
    }

    @Test
    void testCreateComputeEngineTokenProvider() {
        ComputeEngineCredentials computeEngineCredentials = mock(ComputeEngineCredentials.class);
        ReactiveTokenProvider reactiveTokenProvider = ReactiveTokenProvider.create(computeEngineCredentials);
        assertThat(reactiveTokenProvider).isInstanceOf(ComputeEngineTokenProvider.class);
    }

}