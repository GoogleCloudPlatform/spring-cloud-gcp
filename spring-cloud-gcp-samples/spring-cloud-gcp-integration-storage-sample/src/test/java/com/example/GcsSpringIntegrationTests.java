/*
 * Copyright 2017-2019 the original author or authors.
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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This test uploads a file to Google Cloud Storage and verifies that it was received in the local
 * directory specified in application-test.properties.
 *
 * <p>To run this test locally, first specify the buckets and local directory used by sample in
 * java/com/example/resources/application.properties. Then, run: mvn -Dit.storage=true test.
 */
@EnabledIfSystemProperty(named = "it.storage", matches = "true")
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application.properties")
@SpringBootTest(classes = {GcsSpringIntegrationApplication.class})
@Import(GcsSpringIntegrationTestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GcsSpringIntegrationTests {

  private static final String TEST_FILE = String.format("test_file_%s", UUID.randomUUID());
  private static final Log LOGGER = LogFactory.getLog(GcsSpringIntegrationTests.class);

  @Autowired private Storage storage;

  @Autowired
  @Qualifier("uniqueDirectory")
  private String uniqueDirectory;

  @Value("${gcs-read-bucket}")
  private String cloudInputBucket;

  @Value("${gcs-write-bucket}")
  private String cloudOutputBucket;

  @Value("${gcs-local-directory}")
  private String outputFolder;

  @AfterAll
  void teardownTestEnvironment() throws IOException {
    cleanupCloudStorage();
    cleanupLocalDirectories();
  }

  @Test
  void testFilePropagatedToLocalDirectory() {
    BlobId blobId = BlobId.of(this.cloudInputBucket, TEST_FILE);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
    this.storage.create(blobInfo, "Hello World!".getBytes(StandardCharsets.UTF_8));
    Awaitility.await()
        .atMost(30, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              Path outputFile = Paths.get(uniqueDirectory + "/" + TEST_FILE);
              assertThat(Files.exists(outputFile)).isTrue();
              assertThat(Files.isRegularFile(outputFile)).isTrue();

              String firstLine = Files.lines(outputFile).findFirst().get();
              assertThat(firstLine).isEqualTo("Hello World!");

              List<String> blobNamesInOutputBucket = new ArrayList<>();
              this.storage
                  .list(this.cloudOutputBucket)
                  .iterateAll()
                  .forEach(b -> blobNamesInOutputBucket.add(b.getName()));

              assertThat(blobNamesInOutputBucket).contains(TEST_FILE);
            });
  }

  @Test
  void testAutomaticGcsLocalDirectoryCreation() {
    assertThat(Files.exists(Paths.get(outputFolder))).isTrue();
  }

  void cleanupCloudStorage() {
    BlobId inputBucketBlobId = BlobId.of(cloudInputBucket, TEST_FILE);
    Blob inputBucketBlob = storage.get(inputBucketBlobId);
    if (inputBucketBlob != null) {
      inputBucketBlob.delete();
    }

    BlobId outputBucketBlobId = BlobId.of(cloudOutputBucket, TEST_FILE);
    Blob outputBucketBlob = storage.get(outputBucketBlobId);
    if (outputBucketBlob != null) {
      outputBucketBlob.delete();
    }
  }

  void cleanupLocalDirectories() throws IOException {
    cleanupLocalDirectory(Paths.get(uniqueDirectory));
    cleanupLocalDirectory(Paths.get(outputFolder));
  }

  void cleanupLocalDirectory(Path testDirectory) throws IOException {
    if (Files.exists(testDirectory)) {
      if (Files.isDirectory(testDirectory)) {
        try (Stream<Path> files = Files.list(testDirectory)) {
          files.forEach(
              path -> {
                try {
                  Files.delete(path);
                } catch (IOException ioe) {
                  LOGGER.info("Error deleting test file.", ioe);
                }
              });
        }
      }
      Files.delete(testDirectory);
    }
  }
}
