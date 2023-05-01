package com.google.cloud.spring.core;

import org.springframework.web.reactive.function.client.WebClient;

import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.auth.oauth2.ComputeEngineTokenProvider;
import com.google.auth.oauth2.ServiceAccountTokenProvider;
import com.google.auth.oauth2.UserCredentialsTokenProvider;

import reactor.core.publisher.Mono;

public interface ReactiveTokenProvider {

    Mono<AccessToken> retrieve();

    static ReactiveTokenProvider createCacheable(Credentials credentials) {
        ReactiveTokenProvider reactiveTokenProvider = create(credentials);
        return new CacheableTokenProvider(reactiveTokenProvider);
    }

    static ReactiveTokenProvider create(Credentials credentials) {
        WebClient webClient = WebClient.builder().build();
        if (credentials instanceof UserCredentials) {
            return new UserCredentialsTokenProvider(webClient, (UserCredentials) credentials);
        } else if (credentials instanceof ServiceAccountCredentials) {
            return new ServiceAccountTokenProvider(webClient, (ServiceAccountCredentials) credentials);
        } else if (credentials instanceof ComputeEngineCredentials) {
            return new ComputeEngineTokenProvider(webClient, (ComputeEngineCredentials) credentials);
        } else {
            throw new UnsupportedOperationException("Unsupported credentials type. UserCredentials,ServiceAccountCredentials,ComputeEngineCredentials are supported");
        }
    }

}
