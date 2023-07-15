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

import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.ComputeEngineTokenProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.oauth2.ServiceAccountTokenProvider;
import com.google.auth.oauth2.UserCredentials;
import com.google.auth.oauth2.UserCredentialsTokenProvider;
import com.google.cloud.spring.core.reactor.CacheableTokenProvider;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public interface ReactiveTokenProvider {

  Mono<AccessToken> retrieve();

  static ReactiveTokenProvider createCacheable(Credentials credentials) {
    ReactiveTokenProvider reactiveTokenProvider = create(credentials);
    return new CacheableTokenProvider(reactiveTokenProvider);
  }

  static ReactiveTokenProvider create(Credentials credentials) {
    WebClient webClient = WebClient.builder().build();
    return create(credentials, webClient);
  }

  static ReactiveTokenProvider create(Credentials credentials, WebClient webClient) {
    if (credentials instanceof UserCredentials) {
      return new UserCredentialsTokenProvider(webClient, (UserCredentials) credentials);
    } else if (credentials instanceof ServiceAccountCredentials) {
      return new ServiceAccountTokenProvider(webClient, (ServiceAccountCredentials) credentials);
    } else if (credentials instanceof ComputeEngineCredentials) {
      return new ComputeEngineTokenProvider(webClient, (ComputeEngineCredentials) credentials);
    } else {
      throw new UnsupportedOperationException(
          "Unsupported credentials type. UserCredentials,ServiceAccountCredentials,ComputeEngineCredentials are supported");
    }
  }

}
