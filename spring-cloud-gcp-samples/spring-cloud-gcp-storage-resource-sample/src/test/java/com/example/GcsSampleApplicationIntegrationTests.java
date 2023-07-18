/*
 * Copyright 2017-2018 the original author or authors.
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

package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This verifies the sample application for using GCP Storage with Spring Resource abstractions.
 *
 * <p>To run the test, set the gcs-resource-test-bucket property in application.properties to the
 * name of your bucket and run: mvn test -Dit.storage
 */
@EnabledIfSystemProperty(named = "it.storage", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {GcsApplication.class})
class GcsSampleApplicationIntegrationTests {

  private final String filename = String.format("file-%s.txt", UUID.randomUUID());
  @Autowired private Storage storage;
  @Autowired private TestRestTemplate testRestTemplate;
  @Value("${gcs-resource-test-bucket}")
  private String bucketName;
  @LocalServerPort private int port;
  private String appUrl;

  @BeforeEach
  void initializeAppUrl() {
    this.appUrl = "http://localhost:" + this.port;
  }

  @AfterClass
  void cleanupCloudStorage() {
    BlobId blobId = BlobId.of(this.bucketName, filename);
    Blob blob = storage.get(blobId);
    if (blob != null) {
      blob.delete();
    }
  }

  @Test
  void testGcsResourceIsLoaded() {
    BlobId blobId = BlobId.of(this.bucketName, filename);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
    this.storage.create(blobInfo, "Good Morning!".getBytes(StandardCharsets.UTF_8));

    // Verify the contents of the uploaded file.
    String getUrl =
        UriComponentsBuilder.fromHttpUrl(this.appUrl + "/")
            .queryParam("filename", filename)
            .toUriString();
    Awaitility.await()
        .atMost(15, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              String result = this.testRestTemplate.getForObject(getUrl, String.class);
              assertThat(result).isEqualTo("Good Morning!\n");
            });

    // Update the contents of the uploaded file and verify.
    String postUrl =
        UriComponentsBuilder.fromHttpUrl(this.appUrl + "/")
            .queryParam("filename", filename)
            .toUriString();
    this.testRestTemplate.postForObject(postUrl, "Good Night!", String.class);
    Awaitility.await()
        .atMost(15, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              String result = this.testRestTemplate.getForObject(getUrl, String.class);
              assertThat(result).isEqualTo("Good Night!\n");
            });
  }
}
