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
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.cloud.spring.core.ReactiveTokenProvider;

import static com.google.auth.oauth2.Constants.ERROR_PARSING_TOKEN_REFRESH_RESPONSE;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.test.StepVerifier;

class ComputeEngineTokenProviderTests {

    private static final Long SECONDS = 3600L;
    private static final String ACCESS_TOKEN = "ya29.a0AfH6SMAa-dKy_...";

    private static String RESPONSE_BODY = "{\"access_token\":\"" + ACCESS_TOKEN + "\",\"expires_in\":" + SECONDS + ",\"token_type\":\"Bearer\"}";
    private static String BAD_RESPONSE_BODY = "{\"token\":\"" + ACCESS_TOKEN + "\",\"in\":" + SECONDS + ",\"token_type\":\"Bearer\"}";
    private MockWebServer mockWebServer;

    private WebClient webClient;

    private String tokenUri;

    @BeforeEach
    void setUp() throws IOException, URISyntaxException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.builder().build();
        tokenUri = mockWebServer.url("/").toString();
    }

    @Test
    void testRetrieve() {
        enqueResponse(RESPONSE_BODY);
        ComputeEngineCredentials computeEngineCredentials = ComputeEngineCredentials.create();
        ReactiveTokenProvider tokenProvider = new ComputeEngineTokenProvider(webClient, computeEngineCredentials, tokenUri);
        Long expirationWindowStart = addExpiration(System.currentTimeMillis());
        StepVerifier.create(tokenProvider.retrieve())
                    .expectNextMatches(
                            at -> {
                                Long expirationWindowEnd = addExpiration(System.currentTimeMillis());
                                Long expiration = at.getExpirationTimeMillis();
                                return expiration <= expirationWindowEnd && expiration >= expirationWindowStart &&
                                       at.getTokenValue().equals(ACCESS_TOKEN);
                            }
                    )
                    .expectNext()
                    .verifyComplete();
    }

    @Test
    void testRetrieveErrorParsingResponse() {
        enqueResponse(BAD_RESPONSE_BODY);
        ComputeEngineCredentials computeEngineCredentials = ComputeEngineCredentials.create();
        ReactiveTokenProvider tokenProvider = new ComputeEngineTokenProvider(webClient, computeEngineCredentials, tokenUri);
        StepVerifier.create(tokenProvider.retrieve())
                    .expectNext()
                    .verifyErrorMatches(
                            e ->
                                    e instanceof IOException &&
                                    e.getMessage().contains(ERROR_PARSING_TOKEN_REFRESH_RESPONSE)
                    );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private void enqueResponse(String response) {
        MockResponse mockResponse = new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(response);
        mockWebServer.enqueue(mockResponse);
    }

    private static long addExpiration(Long millis) {
        return millis + (SECONDS * 1000L);
    }

}