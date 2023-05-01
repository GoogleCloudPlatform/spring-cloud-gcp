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

import static com.google.auth.oauth2.Constants.ERROR_PARSING_TOKEN_REFRESH_RESPONSE;
import okhttp3.mockwebserver.MockResponse;

public class TokenProviderBase {

    static final Long SECONDS = 3600L;
    static final String ACCESS_TOKEN = "ya29.a0AfH6SMAa-dKy_...";

    private TokenProviderBase() {
    }

    static MockResponse successfulResponse(String response) {
        return new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(response);
    }
    static long addExpiration(Long millis) {
        return millis + (SECONDS * 1000L);
    }

    static boolean expectedToken(Long expirationWindowStart, AccessToken at) {
        boolean isExpirationWithinWindow = isExpirationWithinWindow(expirationWindowStart, at);
        return isExpirationWithinWindow &&
               at.getTokenValue().equals(ACCESS_TOKEN);
    }

    static boolean tokenParseError(Throwable e) {
        return e instanceof IOException &&
               e.getMessage().contains(ERROR_PARSING_TOKEN_REFRESH_RESPONSE);
    }

    static boolean isExpirationWithinWindow(Long expirationWindowStart, AccessToken at) {
        Long expirationWindowEnd = addExpiration(System.currentTimeMillis());
        Long expiration = at.getExpirationTimeMillis();
        boolean expirationWithinLimits = expiration <= expirationWindowEnd && expiration >= expirationWindowStart;
        return expirationWithinLimits;
    }

}
