package com.google.auth.oauth2;

import java.io.IOException;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.api.client.util.GenericData;
import com.google.cloud.spring.core.ReactiveTokenProvider;

import reactor.core.publisher.Mono;

public class UserCredentialsTokenProvider implements ReactiveTokenProvider {

    private final WebClient webClient;
    private final UserCredentials userCredentials;

    private final String tokenUrl;


    public UserCredentialsTokenProvider(WebClient webClient, UserCredentials userCredentials) {
        this.webClient = webClient;
        this.userCredentials = userCredentials;
        this.tokenUrl = OAuth2Utils.TOKEN_SERVER_URI.toString();
    }

    public UserCredentialsTokenProvider(WebClient webClient, UserCredentials userCredentials, String tokenUrl) {
        this.webClient = webClient;
        this.userCredentials = userCredentials;
        this.tokenUrl = tokenUrl;
    }

    @Override
    public Mono<AccessToken> retrieve() {
        if (userCredentials.getRefreshToken() == null) {
            throw new IllegalStateException("UserCredentials instance cannot refresh because there is no refresh token.");
        } else {
            GenericData tokenRequest = new GenericData();
            tokenRequest.set("client_id", this.userCredentials.getClientId());
            tokenRequest.set("client_secret", this.userCredentials.getClientSecret());
            tokenRequest.set("refresh_token", this.userCredentials.getRefreshToken());
            tokenRequest.set("grant_type", "refresh_token");

            return webClient.post().uri(tokenUrl)
                     .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                     .body(BodyInserters.fromFormData("client_id", this.userCredentials.getClientId())
                                        .with("client_secret", this.userCredentials.getClientSecret())
                                        .with("refresh_token", this.userCredentials.getRefreshToken())
                                        .with("grant_type", "refresh_token")
                     ).retrieve().bodyToMono(GenericData.class)
                     .flatMap(gd -> {
                         try {
                             AccessToken accessToken = toAccessToken(gd);
                             return Mono.just(accessToken);
                         } catch (IOException e) {
                             return Mono.error(e);
                         }
                     });
        }

    }

    private AccessToken toAccessToken(GenericData gd) throws IOException {
        String accessTokenValue = OAuth2Utils.validateString(gd, "access_token", "Error parsing token refresh response. ");
        int expiresInSeconds = OAuth2Utils.validateInt32(gd, "expires_in", "Error parsing token refresh response. ");
        long expiresAtMilliseconds = userCredentials.clock.currentTimeMillis() + (long)(expiresInSeconds * 1000);
        String scopes = OAuth2Utils.validateOptionalString(gd, "scope", "Error parsing token refresh response. ");
        AccessToken accessToken = AccessToken.newBuilder().setExpirationTime(new Date(expiresAtMilliseconds)).setTokenValue(accessTokenValue).setScopes(scopes).build();
        return accessToken;
    }

}
