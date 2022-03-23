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

package com.google.cloud.spring.vision;

import static com.google.cloud.spring.vision.CloudVisionTemplate.EMPTY_RESPONSE_ERROR_MESSAGE;
import static com.google.cloud.spring.vision.CloudVisionTemplate.READ_BYTES_ERROR_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.vision.v1.AnnotateFileRequest;
import com.google.cloud.vision.v1.AnnotateFileResponse;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateFilesRequest;
import com.google.cloud.vision.v1.BatchAnnotateFilesResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageContext;
import com.google.cloud.vision.v1.InputConfig;
import com.google.protobuf.ByteString;
import com.google.rpc.Status;
import io.grpc.Status.Code;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * Unit tests for the {@link CloudVisionTemplate}.
 *
 * @since 1.1
 */
class CloudVisionTemplateTests {

  // Resource representing a fake image blob
  private static final Resource FAKE_IMAGE = new ByteArrayResource("fake_image".getBytes());
  private static final Resource FAKE_PDF = new ByteArrayResource("fake_pdf".getBytes());

  private static final BatchAnnotateImagesResponse DEFAULT_API_RESPONSE =
      BatchAnnotateImagesResponse.newBuilder()
          .addResponses(AnnotateImageResponse.getDefaultInstance())
          .build();

  private static final BatchAnnotateFilesResponse DEFAULT_FILES_API_RESPONSE =
      BatchAnnotateFilesResponse.newBuilder()
          .addResponses(
              AnnotateFileResponse.newBuilder()
                  .addResponses(AnnotateImageResponse.getDefaultInstance())
                  .build())
          .build();

  private ImageAnnotatorClient imageAnnotatorClient;

  private CloudVisionTemplate cloudVisionTemplate;

  @BeforeEach
  void setupVisionTemplateMock() {
    this.imageAnnotatorClient = Mockito.mock(ImageAnnotatorClient.class);
    this.cloudVisionTemplate = new CloudVisionTemplate(this.imageAnnotatorClient);
  }

  @Test
  void testAddImageContext_analyzeImage() throws IOException {
    when(this.imageAnnotatorClient.batchAnnotateImages(any(BatchAnnotateImagesRequest.class)))
        .thenReturn(DEFAULT_API_RESPONSE);

    ImageContext imageContext = Mockito.mock(ImageContext.class);

    this.cloudVisionTemplate.analyzeImage(FAKE_IMAGE, imageContext, Type.FACE_DETECTION);

    BatchAnnotateImagesRequest expectedRequest =
        BatchAnnotateImagesRequest.newBuilder()
            .addRequests(
                AnnotateImageRequest.newBuilder()
                    .addFeatures(Feature.newBuilder().setType(Type.FACE_DETECTION))
                    .setImageContext(imageContext)
                    .setImage(
                        Image.newBuilder()
                            .setContent(ByteString.readFrom(FAKE_IMAGE.getInputStream()))
                            .build()))
            .build();

    verify(this.imageAnnotatorClient, times(1)).batchAnnotateImages(expectedRequest);
  }

  @Test
  void testAddImageContext_extractText() throws IOException {
    when(this.imageAnnotatorClient.batchAnnotateImages(any(BatchAnnotateImagesRequest.class)))
        .thenReturn(DEFAULT_API_RESPONSE);

    ImageContext imageContext = Mockito.mock(ImageContext.class);

    this.cloudVisionTemplate.extractTextFromImage(FAKE_IMAGE, imageContext);

    BatchAnnotateImagesRequest expectedRequest =
        BatchAnnotateImagesRequest.newBuilder()
            .addRequests(
                AnnotateImageRequest.newBuilder()
                    .addFeatures(Feature.newBuilder().setType(Type.TEXT_DETECTION))
                    .setImageContext(imageContext)
                    .setImage(
                        Image.newBuilder()
                            .setContent(ByteString.readFrom(FAKE_IMAGE.getInputStream()))
                            .build()))
            .build();

    verify(this.imageAnnotatorClient, times(1)).batchAnnotateImages(expectedRequest);
  }

  @Test
  void testAddInputConfig_extractText() throws IOException {
    when(this.imageAnnotatorClient.batchAnnotateFiles(any(BatchAnnotateFilesRequest.class)))
        .thenReturn(DEFAULT_FILES_API_RESPONSE);

    this.cloudVisionTemplate.extractTextFromPdf(FAKE_PDF);

    BatchAnnotateFilesRequest expectedRequest =
        BatchAnnotateFilesRequest.newBuilder()
            .addRequests(
                AnnotateFileRequest.newBuilder()
                    .addFeatures(Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION))
                    .setInputConfig(
                        InputConfig.newBuilder()
                            .setMimeType("application/pdf")
                            .setContent(ByteString.readFrom(FAKE_PDF.getInputStream()))
                            .build())
                    .build())
            .build();

    verify(this.imageAnnotatorClient, times(1)).batchAnnotateFiles(expectedRequest);
  }

  @Test
  void testEmptyClientResponseError() {
    when(this.imageAnnotatorClient.batchAnnotateImages(any(BatchAnnotateImagesRequest.class)))
        .thenReturn(BatchAnnotateImagesResponse.getDefaultInstance());

    assertThatThrownBy(() -> this.cloudVisionTemplate.analyzeImage(FAKE_IMAGE, Type.TEXT_DETECTION))
            .isInstanceOf(CloudVisionException.class)
            .hasMessage(EMPTY_RESPONSE_ERROR_MESSAGE);
  }

  @Test
  void testEmptyClientAnnotateFileResponseError() {
    when(this.imageAnnotatorClient.batchAnnotateFiles(any(BatchAnnotateFilesRequest.class)))
        .thenReturn(BatchAnnotateFilesResponse.getDefaultInstance());

    assertThatThrownBy(() ->  this.cloudVisionTemplate.analyzeFile(FAKE_PDF, "application/pdf", Type.TEXT_DETECTION))
            .isInstanceOf(CloudVisionException.class)
            .hasMessage(EMPTY_RESPONSE_ERROR_MESSAGE);

  }

  @Test
  void testExtractTextError() {
    AnnotateImageResponse response =
        AnnotateImageResponse.newBuilder()
            .setError(
                Status.newBuilder()
                    .setCode(Code.INTERNAL.value())
                    .setMessage("Error Message from Vision API."))
            .build();

    BatchAnnotateImagesResponse responseBatch =
        BatchAnnotateImagesResponse.newBuilder().addResponses(response).build();

    when(this.imageAnnotatorClient.batchAnnotateImages(any(BatchAnnotateImagesRequest.class)))
        .thenReturn(responseBatch);


    assertThatThrownBy(() -> this.cloudVisionTemplate.extractTextFromImage(FAKE_IMAGE))
            .isInstanceOf(CloudVisionException.class)
            .hasMessage("Error Message from Vision API.");
  }

  @Test
  void testResourceReadingError() {

    Resource imageResource = new BadResource();
    assertThatThrownBy(() -> this.cloudVisionTemplate.analyzeImage(imageResource, Type.LABEL_DETECTION))
            .isInstanceOf(CloudVisionException.class)
            .hasMessageContaining(READ_BYTES_ERROR_MESSAGE);
  }

  @Test
  void testFileResourceReadingError() {

    Resource imageResource = new BadResource();
    assertThatThrownBy(() ->  this.cloudVisionTemplate.analyzeFile(imageResource, "application/pdf", Type.LABEL_DETECTION))
            .isInstanceOf(CloudVisionException.class)
            .hasMessageContaining(READ_BYTES_ERROR_MESSAGE);
  }

  private static final class BadResource extends AbstractResource {
    @Override
    public String getDescription() {
      return "bad resource";
    }

    @Override
    public InputStream getInputStream() throws IOException {
      throw new IOException("Failed to open resource.");
    }
  }
}
