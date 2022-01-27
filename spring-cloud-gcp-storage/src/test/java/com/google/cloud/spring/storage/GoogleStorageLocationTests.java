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

package com.google.cloud.spring.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

class GoogleStorageLocationTests {

  @Test
  void testBadInputsToConstructor() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new GoogleStorageLocation(null));
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new GoogleStorageLocation(""));
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new GoogleStorageLocation(" "));
  }

  @Test
  void testCorrectLocationForBucket() {
    GoogleStorageLocation location = GoogleStorageLocation.forBucket("bucketName");
    assertThat(location.uriString()).isEqualTo("gs://bucketName/");
    assertThat(location.isBucket()).isTrue();
  }

  @Test
  void testCorrectLocationForFolder() {
    GoogleStorageLocation location = GoogleStorageLocation.forFolder("bucketName", "folderName");
    assertThat(location.uriString()).isEqualTo("gs://bucketName/folderName/");
    assertThat(location.isFolder()).isTrue();
  }

  @Test
  void testCorrectLocationForFile() {
    GoogleStorageLocation location = GoogleStorageLocation.forFile("bucketName", "fileName");
    assertThat(location.uriString()).isEqualTo("gs://bucketName/fileName");
    assertThat(location.isFile()).isTrue();
  }
}
