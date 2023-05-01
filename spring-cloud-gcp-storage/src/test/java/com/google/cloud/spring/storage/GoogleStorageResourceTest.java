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

package com.google.cloud.spring.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class GoogleStorageResourceTest {

  private final Storage mockStorage = mock(Storage.class);
  private final Bucket mockBucket = mock(Bucket.class);
  private final Blob mockBlob = mock(Blob.class);

  @Test
  void testConstructorValidation() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new GoogleStorageResource(null, "gs://foo", false))
        .withMessageContaining("Storage object can not be null");

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new GoogleStorageResource(mockStorage, (String) null, false));
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new GoogleStorageResource(mockStorage, "", false));
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new GoogleStorageResource(mockStorage, "foo", false));

    assertThat(new GoogleStorageResource(mockStorage, "gs://foo", true)).isNotNull();
    assertThat(new GoogleStorageResource(mockStorage, "gs://foo/bar", true)).isNotNull();
  }

  @Test
  void getUrlBucket() throws IOException {
    when(mockStorage.get("my-bucket")).thenReturn(mockBucket);
    when(mockBucket.getSelfLink()).thenReturn("https://www.googleapis.com/storage/v1/b/my-bucket");

    GoogleStorageResource gsr = new GoogleStorageResource(mockStorage, "gs://my-bucket");
    assertThat(gsr.getURL()).isNotNull();
  }

  @Test
  void getUrlObject() throws IOException {
    when(mockStorage.get(BlobId.of("my-bucket", "my-object"))).thenReturn(mockBlob);
    when(mockBlob.getSelfLink())
        .thenReturn("https://www.googleapis.com/storage/v1/b/my-bucket/o/my-object");

    GoogleStorageResource gsr = new GoogleStorageResource(mockStorage, "gs://my-bucket/my-object");
    assertThat(gsr.getURL()).isNotNull();
  }

  @Test
  void getLastModifiedTest() throws IOException {
    OffsetDateTime now = OffsetDateTime.now();
    when(mockStorage.get(any(BlobId.class))).thenReturn(mockBlob);
    when(mockBlob.getUpdateTimeOffsetDateTime()).thenReturn(now);
    GoogleStorageResource gsr = new GoogleStorageResource(mockStorage, "gs://my-bucket/my-object");
    assertThat(gsr.lastModified()).isEqualTo(now.toInstant().toEpochMilli());
  }

  @Test
  void isReadableTest() {
    GoogleStorageLocation location = mock(GoogleStorageLocation.class);
    GoogleStorageResource gsr = new GoogleStorageResource(mockStorage, location, false);
    when(location.isBucket()).thenReturn(true);
    assertThat(gsr.isReadable()).isFalse();
    when(location.isBucket()).thenReturn(false);
    assertThat(gsr.isReadable()).isTrue();
  }

  @Test
  void createBlobTest() {
    GoogleStorageResource gsr = new GoogleStorageResource(mockStorage, "gs://my-bucket/my-object");
    gsr.createBlob();
    verify(mockStorage)
        .create(BlobInfo.newBuilder("my-bucket", "my-object").build());
  }

  @Test
  void getGoogleStorageLocationTest() {
    GoogleStorageResource gsr = new GoogleStorageResource(mockStorage, "gs://my-bucket/my-object");
    assertThat(gsr.getGoogleStorageLocation().uriString()).isEqualTo("gs://my-bucket/my-object");
  }
}
