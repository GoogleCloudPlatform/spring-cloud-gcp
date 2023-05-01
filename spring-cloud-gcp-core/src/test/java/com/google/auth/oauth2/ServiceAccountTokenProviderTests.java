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

package com.google.auth.oauth2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.cloud.spring.core.ReactiveTokenProvider;

import static com.google.auth.oauth2.TokenProviderBase.ACCESS_TOKEN;
import static com.google.auth.oauth2.TokenProviderBase.SECONDS;
import static com.google.auth.oauth2.TokenProviderBase.addExpiration;
import static com.google.auth.oauth2.TokenProviderBase.expectedToken;
import static com.google.auth.oauth2.TokenProviderBase.successfulResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.test.StepVerifier;

class ServiceAccountTokenProviderTests {

    private static final ClassPathResource SA_RESOURCE = new ClassPathResource("fake-credential-key.json");

    private static final String SERVICE_ACCOUNT_TOKEN = "{\n" +
                                                        "  \"access_token\": \"" + ACCESS_TOKEN + "\",\n" +
                                                        "  \"expires_in\": " + SECONDS + ",\n" +
                                                        "  \"scope\": \"https://www.googleapis.com/auth/drive.readonly\",\n" +
                                                        "  \"token_type\": \"Bearer\"\n" +
                                                        "}";

    private static final String SERVICE_ACCOUNT_TOKEN_BAD_RESPONSE = "{\n" +
                                                                     "  \"token\": \"" + ACCESS_TOKEN + "\",\n" +
                                                                     "  \"in\": " + SECONDS + ",\n" +
                                                                     "  \"scope\": \"https://www.googleapis.com/auth/drive.readonly\",\n" +
                                                                     "  \"token_type\": \"Bearer\"\n" +
                                                                     "}";

    private MockWebServer mockWebServer;

    private WebClient webClient;

    private String tokenUrl;

    @BeforeEach
    void setUp() throws IOException, URISyntaxException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.builder().build();
        tokenUrl = mockWebServer.url("/").toString();
    }

    @Test
    void testRetrieve() throws IOException {
        mockWebServer.enqueue(successfulResponse(SERVICE_ACCOUNT_TOKEN));
        ServiceAccountCredentials serviceAccountCredentials = ServiceAccountCredentials.fromStream(SA_RESOURCE.getInputStream());
        ReactiveTokenProvider tokenProvider = new ServiceAccountTokenProvider(webClient, serviceAccountCredentials, tokenUrl);
        Long expirationWindowStart = addExpiration(System.currentTimeMillis());
        StepVerifier.create(tokenProvider.retrieve())
                    .expectNextMatches(at -> expectedToken(expirationWindowStart, at))
                    .verifyComplete();
    }

    @Test
    void testRetrieveErrorParsingResponse() throws IOException {
        mockWebServer.enqueue(successfulResponse(SERVICE_ACCOUNT_TOKEN_BAD_RESPONSE));
        ServiceAccountCredentials serviceAccountCredentials = ServiceAccountCredentials.fromStream(SA_RESOURCE.getInputStream());
        ReactiveTokenProvider tokenProvider = new ServiceAccountTokenProvider(webClient, serviceAccountCredentials, tokenUrl);
        StepVerifier.create(tokenProvider.retrieve())
                    .expectNext()
                    .verifyErrorMatches(TokenProviderBase::tokenParseError);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

}