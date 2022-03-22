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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests for Google Cloud Storage. */
@SpringBootTest(properties = "spring.main.banner-mode=off")
@ExtendWith(SpringExtension.class)
class GoogleStorageTests {

  @Value("gs://test-spring/images/spring.png")
  private Resource remoteResource;

  @Value("gs://test_spring/images/spring.png")
  private Resource remoteResourceWithUnderscore;

  @Value("gs://test-spring/")
  private Resource bucketResource;

  @Autowired private Storage mockStorage;

  @Test
  void testEmptyPath() {

    assertThatThrownBy(() -> new GoogleStorageResource(this.mockStorage, "gs://", false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid location: gs://");
  }

  @Test
  void testSlashPath() {

    assertThatThrownBy(() -> new GoogleStorageResource(this.mockStorage, "gs:///", false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("No bucket specified in the location: gs:///");
  }

  @Test
  void testValidObject() throws Exception {
    BlobId validBlobId = BlobId.of("test-spring", "images/spring.png");
    Blob mockedBlob = mock(Blob.class);
    when(mockedBlob.getSize()).thenReturn(4096L);
    when(this.mockStorage.get(validBlobId)).thenReturn(mockedBlob);

    Assert.assertTrue(this.remoteResource.exists());
    Assert.assertEquals(4096L, this.remoteResource.contentLength());
  }

  @Test
  void testValidObjectWithUnderscore() throws Exception {
    BlobId validBlobWithUnderscore = BlobId.of("test_spring", "images/spring.png");
    Blob mockedBlob = mock(Blob.class);
    when(mockStorage.get(validBlobWithUnderscore)).thenReturn(mockedBlob);

    Assert.assertTrue(this.remoteResourceWithUnderscore.exists());
  }

  @Test
  void testValidBucket() throws IOException {
    Assert.assertEquals("gs://test-spring/", this.bucketResource.getDescription());
    Assert.assertEquals("test-spring", this.bucketResource.getFilename());
    Assert.assertEquals("gs://test-spring/", this.bucketResource.getURI().toString());

    String relative = this.bucketResource.createRelative("aaa/bbb").getURI().toString();
    Assert.assertEquals("gs://test-spring/aaa/bbb", relative);
    Assert.assertEquals(
        relative, this.bucketResource.createRelative("/aaa/bbb").getURI().toString());

    Assert.assertNull(((GoogleStorageResource) this.bucketResource).getBlobName());
    Assert.assertTrue(((GoogleStorageResource) this.bucketResource).isBucket());

    Assert.assertTrue(this.bucketResource.exists());
    Assert.assertTrue(((GoogleStorageResource) this.bucketResource).bucketExists());
  }

  @Test
  void testBucketNotEndingInSlash() {
    Assert.assertTrue(new GoogleStorageResource(this.mockStorage, "gs://test-spring").isBucket());
  }

  @Test
  void testSpecifyBucketCorrect() {

    Bucket mockedBucket = mock(Bucket.class);
    when(this.mockStorage.get("test-spring")).thenReturn(mockedBucket);
    when(mockedBucket.exists()).thenReturn(true);
    when(mockedBucket.getName()).thenReturn("test-spring");

    GoogleStorageLocation location = GoogleStorageLocation.forBucket("test-spring");
    GoogleStorageResource googleStorageResource =
        new GoogleStorageResource(this.mockStorage, location, false);

    Assert.assertTrue(googleStorageResource.isBucket());
    Assert.assertEquals("test-spring", googleStorageResource.getBucketName());
    Assert.assertEquals("test-spring", googleStorageResource.getBucket().getName());
    Assert.assertTrue(googleStorageResource.exists());
  }

  @Test
  void testSpecifyPathCorrect() {
    BlobId validBlobId = BlobId.of("test-spring", "images/spring.png");
    Blob mockedBlob = mock(Blob.class);
    when(this.mockStorage.get(validBlobId)).thenReturn(mockedBlob);

    GoogleStorageResource googleStorageResource =
        new GoogleStorageResource(this.mockStorage, "gs://test-spring/images/spring.png", false);

    Assert.assertTrue(googleStorageResource.exists());
  }

  @Test
  void testBucketOutputStream() throws IOException {

    assertThatThrownBy(() ->  ((WritableResource) this.bucketResource).getOutputStream())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot open an output stream to a bucket: 'gs://test-spring/'");
  }

  @Test
  void testBucketNoBlobInputStream() throws IOException {

    assertThatThrownBy(() ->  this.bucketResource.getInputStream())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot open an input stream to a bucket: 'gs://test-spring/'");
  }

  @Test
  void testBucketNoBlobContentLength() throws IOException {

    assertThatThrownBy(() -> this.bucketResource.contentLength())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("No blob id specified in the location: 'gs://test-spring/', and the operation is not allowed on buckets.");
  }

  @Test
  void testBucketNoBlobFile() throws IOException {

    assertThatThrownBy(() -> this.bucketResource.getFile())
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessage("gs://test-spring/ cannot be resolved to absolute file path");
  }

  @Test
  void testBucketNoBlobLastModified() throws IOException {

    assertThatThrownBy(() ->  this.bucketResource.lastModified())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("No blob id specified in the location: 'gs://test-spring/', and the operation is not allowed on buckets.");
  }

  @Test
  void testBucketNoBlobResourceStatuses() throws IOException {
    Assert.assertFalse(this.bucketResource.isOpen());
    Assert.assertFalse(this.bucketResource.isReadable());
    Assert.assertFalse(((WritableResource) this.bucketResource).isWritable());
    Assert.assertTrue(this.bucketResource.exists());
  }

  @Test
  void testWritable() throws Exception {
    WriteChannel writeChannel = mock(WriteChannel.class);
    when(this.mockStorage.writer(any(BlobInfo.class))).thenReturn(writeChannel);

    Assert.assertTrue(this.remoteResource instanceof WritableResource);
    WritableResource writableResource = (WritableResource) this.remoteResource;
    Assert.assertTrue(writableResource.isWritable());
    writableResource.getOutputStream();
  }

  @Test
  void testWritableOutputStream() throws Exception {
    String location = "gs://test-spring/test";
    Blob blob = mock(Blob.class);
    WriteChannel writeChannel = mock(WriteChannel.class);
    when(blob.writer()).thenReturn(writeChannel);
    when(blob.exists()).thenReturn(true);
    when(this.mockStorage.get(BlobId.of("test-spring", "test"))).thenReturn(blob);

    GoogleStorageResource resource = new GoogleStorageResource(this.mockStorage, location);
    OutputStream os = resource.getOutputStream();
    Assert.assertNotNull(os);
  }

  @Test
  void testWritableOutputStreamNoAutoCreateOnNullBlob() throws Exception {

    String location = "gs://test-spring/test";
    when(this.mockStorage.get(BlobId.of("test-spring", "test"))).thenReturn(null);

    GoogleStorageResource resource = new GoogleStorageResource(this.mockStorage, location, false);

    assertThatThrownBy(() -> resource.getOutputStream())
            .isInstanceOf(FileNotFoundException.class)
            .hasMessage("The blob was not found: gs://test-spring/test");
  }

  @Test
  void testWritableOutputStreamWithAutoCreateOnNullBlob() throws Exception {
    String location = "gs://test-spring/test";
    BlobId blobId = BlobId.of("test-spring", "test");
    when(this.mockStorage.get(blobId)).thenReturn(null);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    WriteChannel writeChannel = mock(WriteChannel.class);
    Blob blob = mock(Blob.class);
    when(blob.writer()).thenReturn(writeChannel);
    when(this.mockStorage.create(blobInfo)).thenReturn(blob);

    GoogleStorageResource resource = new GoogleStorageResource(this.mockStorage, location);
    GoogleStorageResource spyResource = spy(resource);
    OutputStream os = spyResource.getOutputStream();
    Assert.assertNotNull(os);
  }

  @Test
  void testWritableOutputStreamWithAutoCreateOnNonExistantBlob() throws Exception {
    String location = "gs://test-spring/test";
    BlobId blobId = BlobId.of("test-spring", "test");
    Blob nonExistantBlob = mock(Blob.class);
    when(nonExistantBlob.exists()).thenReturn(false);
    when(this.mockStorage.get(blobId)).thenReturn(nonExistantBlob);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    WriteChannel writeChannel = mock(WriteChannel.class);
    Blob blob = mock(Blob.class);
    when(blob.writer()).thenReturn(writeChannel);
    when(this.mockStorage.create(blobInfo)).thenReturn(blob);

    GoogleStorageResource resource = new GoogleStorageResource(this.mockStorage, location);
    GoogleStorageResource spyResource = spy(resource);
    OutputStream os = spyResource.getOutputStream();
    Assert.assertNotNull(os);
  }

  @Test
  void testCreateBlobWithContents() {
    Storage mockStorage = mock(Storage.class);
    String location = "gs://test-bucket/filename";
    byte[] contentBytes = "test contents".getBytes();

    GoogleStorageResource resource = new GoogleStorageResource(mockStorage, location);
    resource.createBlob(contentBytes);

    verify(mockStorage)
        .create(BlobInfo.newBuilder("test-bucket", "filename").build(), contentBytes);
  }

  @Test
  void testGetInputStreamOnNullBlob() throws Exception {
    String location = "gs://test-spring/test";
    Storage storage = mock(Storage.class);
    when(storage.get(BlobId.of("test-spring", "test"))).thenReturn(null);

    GoogleStorageResource resource = new GoogleStorageResource(storage, location, false);

    assertThatThrownBy(() -> resource.getInputStream())
            .isInstanceOf(FileNotFoundException.class)
            .hasMessage("The blob was not found: gs://test-spring/test");

  }

  @Test
  void testGetFilenameOnNonExistingBlob() throws Exception {
    String location = "gs://test-spring/test";
    Storage storage = mock(Storage.class);
    when(storage.get(BlobId.of("test-spring", "test"))).thenReturn(null);

    GoogleStorageResource resource = new GoogleStorageResource(storage, location, false);
    Assert.assertEquals("test", resource.getFilename());
  }

  @Test
  void testisAutoCreateFilesGetterSetter() {
    String location = "gs://test-spring/test";
    Storage storage = mock(Storage.class);
    GoogleStorageResource resource = new GoogleStorageResource(storage, location);
    Assert.assertTrue(resource.isAutoCreateFiles());
  }

  @Test
  void testCreateRelative() throws IOException {
    String location = "gs://test-spring/test.png";
    Storage storage = mock(Storage.class);
    GoogleStorageResource resource = new GoogleStorageResource(storage, location);
    GoogleStorageResource relative =
        (GoogleStorageResource) resource.createRelative("relative.png");
    Assert.assertEquals("gs://test-spring/relative.png", relative.getURI().toString());
  }

  @Test
  void testCreateRelativeSubdirs() throws IOException {
    String location = "gs://test-spring/t1/test.png";
    Storage storage = mock(Storage.class);
    GoogleStorageResource resource = new GoogleStorageResource(storage, location);
    GoogleStorageResource relative =
        (GoogleStorageResource) resource.createRelative("r1/relative.png");
    Assert.assertEquals("gs://test-spring/t1/r1/relative.png", relative.getURI().toString());
  }

  @Test
  void nullSignedUrlForNullBlob() throws IOException {
    String location = "gs://test-spring/t1/test.png";
    Storage storage = mock(Storage.class);
    GoogleStorageResource resource = new GoogleStorageResource(storage, location, false);
    when(storage.get(any(BlobId.class))).thenReturn(null);
    Assert.assertNull(resource.createSignedUrl(TimeUnit.DAYS, 1));
  }

  @Test
  void signedUrlFunctionCalled() throws IOException {
    String location = "gs://test-spring/t1/test.png";
    Storage storage = mock(Storage.class);
    Blob blob = mock(Blob.class);
    when(blob.getBucket()).thenReturn("fakeBucket");
    when(blob.getName()).thenReturn("fakeObject");
    GoogleStorageResource resource = new GoogleStorageResource(storage, location);
    when(storage.get(any(BlobId.class))).thenReturn(blob);
    Storage.SignUrlOption option = Storage.SignUrlOption.httpMethod(HttpMethod.PUT);
    resource.createSignedUrl(TimeUnit.DAYS, 1L, option);
    verify(storage, times(1)).signUrl(any(), eq(1L), eq(TimeUnit.DAYS), eq(option));
  }

  @Test
  void testBucketDoesNotExist() {
    Bucket mockedBucket = mock(Bucket.class);
    when(this.mockStorage.create(BucketInfo.newBuilder("non-existing").build()))
        .thenReturn(mockedBucket);
    when(mockedBucket.getName()).thenReturn("test-spring");

    GoogleStorageResource bucket =
        new GoogleStorageResource(this.mockStorage, "gs://non-existing/");
    Assert.assertFalse(bucket.bucketExists());
    Assert.assertFalse(bucket.exists());

    Assert.assertNotNull(bucket.createBucket());
  }

  @Test
  void testBucketExistsButResourceDoesNot() {
    GoogleStorageResource resource =
        new GoogleStorageResource(this.mockStorage, "gs://test-spring/file1");

    Bucket mockedBucket = mock(Bucket.class);
    when(this.mockStorage.get("test-spring")).thenReturn(mockedBucket);
    when(mockedBucket.exists()).thenReturn(true);

    Assert.assertTrue(resource.bucketExists());
    Assert.assertFalse(resource.exists());
  }

  /** Configuration for the tests. */
  @Configuration
  @Import(GoogleStorageProtocolResolver.class)
  static class StorageApplication {

    @Bean
    public static Storage mockStorage() throws Exception {
      return mock(Storage.class);
    }

    @Bean
    public static GoogleStorageProtocolResolverSettings googleStorageProtocolResolverSettings() {
      return new GoogleStorageProtocolResolverSettings();
    }
  }
}
