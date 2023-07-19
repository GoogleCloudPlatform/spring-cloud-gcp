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


import com.google.api.client.json.JsonParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.spring.core.ReactiveTokenProvider;
import com.google.cloud.storage.BlobId;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GoogleStorageTemplate {

  private final String baseUrl;

  private static final String DEFAULT_BASE_URL = "https://storage.googleapis.com/";
  private static final String STORAGE_PATH = "/storage/v1";

  private final WebClient webClient;
  private final ReactiveTokenProvider reactiveTokenProvider;

  public GoogleStorageTemplate(WebClient webClient, ReactiveTokenProvider reactiveTokenProvider) {
    this(DEFAULT_BASE_URL, webClient, reactiveTokenProvider);
  }

  public GoogleStorageTemplate(String baseUrl, WebClient webClient,
      ReactiveTokenProvider reactiveTokenProvider) {
    this.webClient = webClient;
    this.reactiveTokenProvider = reactiveTokenProvider;
    this.baseUrl = baseUrl;
  }

  public Mono<StorageObject> get(BlobId blobId) {
    String url = baseUrl + STORAGE_PATH + getObjectPath(blobId.getBucket(), blobId.getName());
    return reactiveTokenProvider.retrieve()
        .flatMap(at -> webClient.get()
            .uri(url)
            .header("Authorization", "Bearer " + at.getTokenValue())
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(GoogleStorageTemplate::getStorageObject)
        );
  }

  public Mono<String> download(BlobId blobId) {
    String url =
        baseUrl + STORAGE_PATH + getObjectPath(blobId.getBucket(), blobId.getName()) + "?alt=media";
    return reactiveTokenProvider.retrieve()
        .flatMap(at -> webClient.get()
            .uri(url)
            .header("Authorization", "Bearer " + at.getTokenValue())
            .retrieve()
            .bodyToMono(String.class)
        );
  }

  public Mono<StorageObject> create(BlobId blobId, Flux<ByteBuffer> bytes) {
    String urlStr = baseUrl + "/upload" + STORAGE_PATH + getBucketPath(blobId.getBucket()) + "/o";
    URI uri = UriComponentsBuilder.fromHttpUrl(urlStr)
        .queryParam("name", blobId.getName())
        .queryParam("uploadType", "media")
        .build().toUri();

    return reactiveTokenProvider.retrieve()
        .flatMap(at -> webClient.post()
            .uri(uri)
            .header("Authorization", "Bearer " + at.getTokenValue())
            .body(BodyInserters.fromPublisher(bytes, ByteBuffer.class))
            .retrieve().bodyToMono(String.class).flatMap(GoogleStorageTemplate::getStorageObject));


  }

  public Mono<Objects> list(String bucket, MultiValueMap<String, String> options) {
    String urlStr = baseUrl + STORAGE_PATH + getBucketPath(bucket) + "/o";

    URI uri = UriComponentsBuilder.fromHttpUrl(urlStr).queryParams(options).build().toUri();
    return reactiveTokenProvider.retrieve()
        .flatMap(at -> webClient.get()
            .uri(uri)
            .header("Authorization", "Bearer " + at.getTokenValue())
            .exchangeToMono(clientResponse -> mapResponseBody(clientResponse))
            .flatMap(GoogleStorageTemplate::getObjects)
        );
  }


  public Mono<Void> delete(BlobId blobId) {
    String url = baseUrl + STORAGE_PATH + getObjectPath(blobId.getBucket(), blobId.getName());
    return reactiveTokenProvider.retrieve()
        .flatMap(at -> webClient.delete()
            .uri(url)
            .header("Authorization", "Bearer " + at.getTokenValue())
            .exchangeToMono(GoogleStorageTemplate::mapResponseBody)
            .flatMap(a -> Mono.empty())
        );
  }

  private static Mono<String> mapResponseBody(ClientResponse clientResponse) {
    if (clientResponse.statusCode().isError()) {
      return mapError(clientResponse);
    } else {
      return clientResponse.bodyToMono(String.class);
    }
  }

  private static <T> Mono<T> mapError(ClientResponse clientResponse) {
    return clientResponse.bodyToMono(String.class).map(StorageException::new).flatMap(Mono::error);
  }


  private static Mono<StorageObject> getStorageObject(String s) {
    try {
      try (JsonParser jsonParser = GsonFactory.getDefaultInstance().createJsonParser(s)) {
        StorageObject storageObject = jsonParser.parse(StorageObject.class);
        return Mono.just(storageObject);
      }
    } catch (IOException e) {
      return Mono.error(e);
    }
  }

  private static Mono<Objects> getObjects(String s) {
    try (JsonParser jsonParser = GsonFactory.getDefaultInstance().createJsonParser(s)) {
      Objects storageObject = jsonParser.parse(Objects.class);
      return Mono.just(storageObject);
    } catch (IOException e) {
      return Mono.error(e);
    }
  }

  private String getBucketPath(String bucket) {
    return "/b/" + bucket;
  }

  private String getObjectPath(String bucket, String objectName) {
    return getBucketPath(bucket) + "/o/" + URLEncoder.encode(objectName, StandardCharsets.UTF_8);
  }


}
