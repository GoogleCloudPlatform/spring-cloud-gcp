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

import static com.google.cloud.spring.storage.MockReplies.downloadObjectResponse;
import static com.google.cloud.spring.storage.MockReplies.getObjectResponse;
import static com.google.cloud.spring.storage.MockReplies.insertObjectRespose;

import com.google.cloud.spring.core.ReactiveTokenProvider;
import com.google.cloud.storage.BlobId;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.UUID;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class GoogleStorageTemplateTests {

  private MockWebServer mockWebServer;

  private WebClient webClient;

  private String baseUrl;

  private GoogleStorageTemplate googleStorageTemplate;

  @BeforeEach
  void setUp() throws IOException, URISyntaxException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    webClient = WebClient.builder().build();
    baseUrl = mockWebServer.url("/").toString();

    String token = UUID.randomUUID().toString();
    Date date = new Date();

    ReactiveTokenProvider tokenProvider = TokenProviderUtil.mock(token, date);
    this.googleStorageTemplate = new GoogleStorageTemplate(baseUrl, webClient, tokenProvider);
  }

  @Test
  void testGet() {
    mockWebServer.enqueue(getObjectResponse());

    BlobId blobId = BlobId.of("bucket", "object");

    StepVerifier.create(googleStorageTemplate.get(blobId))
        .expectNextMatches(so ->
            so.getBucket().equals("bucket")
                && so.getName().equals("object")
                && so.getGeneration().equals(1234567890L)
                && so.getStorageClass().equals("STANDARD")
        ).verifyComplete();
  }


  @Test
  void testDownload() {
    mockWebServer.enqueue(downloadObjectResponse());

    BlobId blobId = BlobId.of("bucket", "object");

    StepVerifier.create(googleStorageTemplate.download(blobId))
        .expectNext("{\n"
            + "  \"key\": \"value\",\n"
            + "  \"description\": \"This is mock data\"\n"
            + "}")
        .verifyComplete();
  }

  @Test
  void testInsert() {
    mockWebServer.enqueue(insertObjectRespose());
    BlobId blobId = BlobId.of("bucket", "object");

    ByteBuffer[] byteBuffers = new ByteBuffer[]{ByteBuffer.wrap("hello".getBytes())};
    Flux<ByteBuffer> bufferFlux = Flux.fromArray(byteBuffers);

    StepVerifier.create(googleStorageTemplate.create(blobId, bufferFlux))
        .expectNextMatches(
            so -> so.getBucket().equals("bucket")
                && so.getName().equals("object")
                && so.getGeneration().equals(1234567890L)
        ).verifyComplete();

  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

}