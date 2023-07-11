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

package com.google.cloud.spring.storage;

import java.io.IOException;
import java.nio.charset.Charset;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;

import okhttp3.mockwebserver.MockResponse;

public class MockReplies {

    private MockReplies() {
    }

    static MockResponse getObjectResponse() {
        return successfulResponse(getObject());
    }

    static String getObject() {
        return getStringFromClassPath("storage-object.json");
    }

    static MockResponse downloadObjectResponse() {
        return successfulResponse(downloadObject());
    }

    static MockResponse insertObjectRespose() {
        return successfulResponse(insertObject());
    }

    private static String insertObject() {
        return getStringFromClassPath("insert-object.json");
    }

    static String downloadObject() {
        return getStringFromClassPath("download-object.json");
    }

    private static String getStringFromClassPath(String classPathFile) {
        Resource resource = new ClassPathResource(classPathFile);
        try {
            return StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    static MockResponse successfulResponse(String response) {
        return new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setResponseCode(200)
                .setBody(response);
    }
}
