package com.google.cloud.spring.storage;

import java.util.Date;

import com.google.auth.oauth2.AccessToken;
import com.google.cloud.spring.core.ReactiveTokenProvider;

import reactor.core.publisher.Mono;

class TokenProviderUtil {

    static ReactiveTokenProvider mock(String token, Date time) {
        return () -> Mono.just(new AccessToken(token, time));
    }

}
