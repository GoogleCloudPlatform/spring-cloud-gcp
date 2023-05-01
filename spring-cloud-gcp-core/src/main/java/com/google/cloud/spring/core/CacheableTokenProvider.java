package com.google.cloud.spring.core;

import java.util.concurrent.atomic.AtomicReference;

import com.google.auth.oauth2.AccessToken;

import reactor.core.publisher.Mono;

public class CacheableTokenProvider implements ReactiveTokenProvider {

    private ReactiveTokenProvider reactiveTokenProvider;

    private AtomicReference<Mono<AccessToken>> atomicReference;

    public CacheableTokenProvider(ReactiveTokenProvider reactiveTokenProvider) {
        this.reactiveTokenProvider = reactiveTokenProvider;
        this.atomicReference = new AtomicReference<>(reactiveTokenProvider.retrieve());
    }

    public CacheableTokenProvider(ReactiveTokenProvider reactiveTokenProvider, AtomicReference<Mono<AccessToken>> atomicReference) {
        this.reactiveTokenProvider = reactiveTokenProvider;
        this.atomicReference = atomicReference;
    }

    @Override
    public Mono<AccessToken> retrieve() {
        Mono<AccessToken> currentInstance = atomicReference.get();
        return currentInstance.flatMap(t -> {
                                           boolean expiresSoon = true;
                                           if(expiresSoon) {
                                               return refreshOnExpiration(currentInstance);
                                           } else {
                                               return Mono.just(t);
                                           }
                                       }
        );
    }

    private Mono<AccessToken> refreshOnExpiration(Mono<AccessToken> expected) {
        Mono<AccessToken> toExecute = executeRequest();
        atomicReference.compareAndSet(expected, toExecute);
        return atomicReference.get();
    }

    Mono<AccessToken> executeRequest() {
        return reactiveTokenProvider.retrieve();
    }

}
