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
import java.util.concurrent.atomic.AtomicReference;
import reactor.core.publisher.Mono;

public class CacheableTokenProvider implements ReactiveTokenProvider {

  private ReactiveTokenProvider reactiveTokenProvider;

  private AtomicReference<Mono<AccessToken>> atomicReference;

  private final RefreshThreshold refreshThreshold;

  public CacheableTokenProvider(ReactiveTokenProvider reactiveTokenProvider) {
    this(reactiveTokenProvider, new RefreshThreshold());
  }

  public CacheableTokenProvider(ReactiveTokenProvider reactiveTokenProvider,
      RefreshThreshold refreshThreshold) {
    this.reactiveTokenProvider = reactiveTokenProvider;
    this.atomicReference = new AtomicReference<>(cachedRetrieval());
    this.refreshThreshold = refreshThreshold;
  }

  @Override
  public Mono<AccessToken> retrieve() {
    Mono<AccessToken> currentInstance = atomicReference.get();
    return currentInstance.flatMap(t -> createNewIfCloseToExpiration(currentInstance, t));
  }

  private Mono<AccessToken> createNewIfCloseToExpiration(Mono<AccessToken> currentInstance,
      AccessToken t) {
    boolean expiresSoon = refreshThreshold.over(t);
    if (expiresSoon) {
      return refreshOnExpiration(currentInstance);
    } else {
      return Mono.just(t);
    }
  }

  private Mono<AccessToken> refreshOnExpiration(Mono<AccessToken> expected) {
    Mono<AccessToken> toExecute = cachedRetrieval();
    atomicReference.compareAndSet(expected, toExecute);
    return atomicReference.get();
  }

  Mono<AccessToken> cachedRetrieval() {
    return reactiveTokenProvider.retrieve().cache();
  }

}
