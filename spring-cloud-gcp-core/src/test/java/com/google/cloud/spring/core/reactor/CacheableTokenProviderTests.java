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

package com.google.cloud.spring.core.reactor;


import com.google.auth.oauth2.AccessToken;
import com.google.cloud.spring.core.ReactiveTokenProvider;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CacheableTokenProviderTests {

  @Test
  void testRetrieveCache() {
    ReactiveTokenProvider reactiveTokenProvider = () -> Mono.fromSupplier(() -> {
      String token = UUID.randomUUID().toString();
      Date date = new Date();
      return new AccessToken(token, date);
    });

    ReactiveTokenProvider cacheableTokenProvider = new CacheableTokenProvider(
        reactiveTokenProvider);

    StepVerifier.create(
            Mono.zip(cacheableTokenProvider.retrieve(), cacheableTokenProvider.retrieve())
                .map(tuple -> tuple.getT1().equals(tuple.getT2())))
        .expectNext(true)
        .verifyComplete();
  }

  @Test
  void testRetrieveExpired() throws InterruptedException {
    ReactiveTokenProvider reactiveTokenProvider = () -> Mono.fromSupplier(() -> {
      String token = UUID.randomUUID().toString();
      return new AccessToken(token, new Date(System.currentTimeMillis() + 4 * 1000));
    });

    ReactiveTokenProvider cacheableTokenProvider = new CacheableTokenProvider(
        reactiveTokenProvider);

    Mono<AccessToken> firstMono = cacheableTokenProvider.retrieve();

    AccessToken accessToken = firstMono.block();

    Thread.sleep(1000L);
    Mono<AccessToken> secondMono = cacheableTokenProvider.retrieve();

    StepVerifier.create(secondMono.map(at -> at.equals(accessToken)))
        .expectNext(false)
        .verifyComplete();
  }


}