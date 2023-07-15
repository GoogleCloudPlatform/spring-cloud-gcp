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

import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.GenericData;
import com.google.cloud.spring.core.ReactiveTokenProvider;
import java.io.IOException;
import java.util.Date;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ServiceAccountTokenProvider implements ReactiveTokenProvider {

  private static final String GRANT_TYPE = "grant_type";
  private static final String ASSERTION = "assertion";
  private final WebClient webClient;
  private final ServiceAccountCredentials serviceAccountCredentials;

  private final String tokenUrl;

  public ServiceAccountTokenProvider(WebClient webClient,
      ServiceAccountCredentials serviceAccountCredentials) {
    this.webClient = webClient;
    this.serviceAccountCredentials = serviceAccountCredentials;
    tokenUrl = OAuth2Utils.TOKEN_SERVER_URI.toString();
  }

  public ServiceAccountTokenProvider(WebClient webClient,
      ServiceAccountCredentials serviceAccountCredentials, String tokenUrl) {
    this.webClient = webClient;
    this.serviceAccountCredentials = serviceAccountCredentials;
    this.tokenUrl = tokenUrl;
  }

  @Override
  public Mono<AccessToken> retrieve() {
    JsonFactory jsonFactory = OAuth2Utils.JSON_FACTORY;
    long currentTime = serviceAccountCredentials.clock.currentTimeMillis();

    try {
      String assertion = serviceAccountCredentials.createAssertion(jsonFactory, currentTime);

      return webClient.post()
          .uri(tokenUrl)
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .body(refreshForm(assertion))
          .retrieve()
          .bodyToMono(GenericData.class)
          .flatMap(gd -> {
            try {
              AccessToken accessToken = getAccessToken(gd);
              return Mono.just(accessToken);
            } catch (IOException e) {
              return Mono.error(e);
            }
          });

    } catch (IOException e) {
      return Mono.error(e);
    }
  }

  private AccessToken getAccessToken(GenericData gd) throws IOException {
    String tokenValue = OAuth2Utils.validateString(gd, "access_token",
        "Error parsing token refresh response. ");
    int expiresInSeconds = OAuth2Utils.validateInt32(gd, "expires_in",
        "Error parsing token refresh response. ");
    long expiresAtMilliseconds =
        serviceAccountCredentials.clock.currentTimeMillis() + (long) expiresInSeconds * 1000L;
    AccessToken accessToken = new AccessToken(tokenValue, new Date(expiresAtMilliseconds));
    return accessToken;
  }

  private static BodyInserters.FormInserter<String> refreshForm(String assertion) {
    return BodyInserters.fromFormData(GRANT_TYPE, OAuth2Utils.GRANT_TYPE_JWT_BEARER)
        .with(ASSERTION, assertion);
  }

}
