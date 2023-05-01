package com.google.auth.oauth2;

import java.io.IOException;
import java.util.Date;

import org.springframework.web.reactive.function.client.WebClient;

import com.google.api.client.util.GenericData;
import com.google.cloud.spring.core.ReactiveTokenProvider;

import reactor.core.publisher.Mono;

public class ComputeEngineTokenProvider implements ReactiveTokenProvider {

    private final WebClient webClient;
    private final ComputeEngineCredentials computeEngineCredentials;

    private final String tokenUrl;

    public ComputeEngineTokenProvider(WebClient webClient, ComputeEngineCredentials computeEngineCredentials) {
        this.webClient = webClient;
        this.computeEngineCredentials = computeEngineCredentials;
        tokenUrl = computeEngineCredentials.createTokenUrlWithScopes();
    }

    public ComputeEngineTokenProvider(WebClient webClient, ComputeEngineCredentials computeEngineCredentials, String tokenUrl) {
        this.webClient = webClient;
        this.computeEngineCredentials = computeEngineCredentials;
        this.tokenUrl = tokenUrl;
    }

    @Override
    public Mono<AccessToken> retrieve() {
        return webClient.get().uri(tokenUrl)
                        .header("Metadata-Flavor", "Google")
                        .retrieve()
                        .bodyToMono(GenericData.class)
                        .flatMap(gd -> {
                            try {
                                String tokenValue = OAuth2Utils.validateString(gd, "access_token", "Error parsing token refresh response. ");
                                int expiresInSeconds = OAuth2Utils.validateInt32(gd, "expires_in", "Error parsing token refresh response. ");
                                long expiresAtMilliseconds = computeEngineCredentials.clock.currentTimeMillis() + (long) (expiresInSeconds * 1000L);
                                AccessToken accessToken = new AccessToken(tokenValue, new Date(expiresAtMilliseconds));
                                return Mono.just(accessToken);
                            } catch (IOException e) {
                                return Mono.error(e);
                            }
                        });
    }

}
