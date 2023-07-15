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

import static com.google.auth.oauth2.TokenProviderBase.ACCESS_TOKEN;
import static com.google.auth.oauth2.TokenProviderBase.successfulResponse;

import com.google.cloud.spring.core.ReactiveTokenProvider;
import java.io.IOException;
import java.net.URISyntaxException;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

class UserCredentialsTokenProviderTest {

  private static String USER_CREDENTIALS_TOKEN =
      "{\"access_token\":\"" + ACCESS_TOKEN
          + "\",\"token_type\":\"Bearer\",\"expires_in\":3599,\"refresh_token\":\"test_refresh_token\",\"scope\":\"https://www.googleapis.com/auth/cloud-platform\"}";

  private static String USER_CREDENTIALS_TOKEN_BAD_RESPONSE =
      "{\"token\":\"" + ACCESS_TOKEN
          + "\",\"token_type\":\"Bearer\",\"in\":3599,\"refresh_token\":\"test_refresh_token\",\"scope\":\"https://www.googleapis.com/auth/cloud-platform\"}";

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
  void testRetrieve() throws Exception {
    mockWebServer.enqueue(successfulResponse(USER_CREDENTIALS_TOKEN));
    UserCredentials userCredentials = createUserCredentials();
    ReactiveTokenProvider tokenProvider = new UserCredentialsTokenProvider(webClient,
        userCredentials, tokenUrl);
    StepVerifier.create(tokenProvider.retrieve().map(AccessToken::getTokenValue))
        .expectNext(ACCESS_TOKEN)
        .verifyComplete();
  }

  @Test
  void testRetrieveErrorParsingResponse() {
    mockWebServer.enqueue(successfulResponse(USER_CREDENTIALS_TOKEN_BAD_RESPONSE));
    UserCredentials userCredentials = createUserCredentials();
    ReactiveTokenProvider tokenProvider = new UserCredentialsTokenProvider(webClient,
        userCredentials, tokenUrl);
    StepVerifier.create(tokenProvider.retrieve())
        .expectNext()
        .verifyErrorMatches(TokenProviderBase::tokenParseError);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  private static UserCredentials createUserCredentials() {
    return UserCredentials.newBuilder()
        .setClientId("test_client_id")
        .setClientSecret("test_client_secret")
        .setRefreshToken("refresh_token")
        .build();
  }

}