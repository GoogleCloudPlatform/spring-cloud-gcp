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

import java.io.IOException;
import java.util.Date;

import org.springframework.web.reactive.function.client.WebClient;

import com.google.api.client.util.GenericData;
import com.google.cloud.spring.core.ReactiveTokenProvider;

import static com.google.auth.oauth2.Constants.ACCESS_TOKEN;
import static com.google.auth.oauth2.Constants.ERROR_PARSING_TOKEN_REFRESH_RESPONSE;
import static com.google.auth.oauth2.Constants.EXPIRES_IN;
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
                                String tokenValue = OAuth2Utils.validateString(gd, ACCESS_TOKEN, ERROR_PARSING_TOKEN_REFRESH_RESPONSE);
                                int expiresInSeconds = OAuth2Utils.validateInt32(gd, EXPIRES_IN, ERROR_PARSING_TOKEN_REFRESH_RESPONSE);
                                long expiresAtMilliseconds = computeEngineCredentials.clock.currentTimeMillis() + (expiresInSeconds * 1000L);
                                AccessToken accessToken = new AccessToken(tokenValue, new Date(expiresAtMilliseconds));
                                return Mono.just(accessToken);
                            } catch (IOException e) {
                                return Mono.error(e);
                            }
                        });
    }

}
