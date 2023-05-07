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

class RefreshThreshold {

    static final int DEFAULT_MILLIS_BEFORE_EXPIRATION = 60 * 1000;
    private final int millisBeforeExpiration;

    public RefreshThreshold() {
        this(DEFAULT_MILLIS_BEFORE_EXPIRATION);
    }

    public RefreshThreshold(int millisBeforeExpiration) {
        this.millisBeforeExpiration = millisBeforeExpiration;
    }

    boolean over(AccessToken accessToken) {
        long currentMillis = System.currentTimeMillis();
        long expirationMillis = accessToken.getExpirationTime().getTime();
        return currentMillis > expirationMillis - millisBeforeExpiration;
    }

}
