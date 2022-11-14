/*
 * Copyright 2022 Google LLC
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

package com.google.cloud.vision.v1.spring;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.httpjson.InstantiatingHttpJsonChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import java.io.IOException;
import javax.annotation.Generated;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.threeten.bp.Duration;

@Generated("by gapic-generator-java")
@AutoConfiguration
@ConditionalOnClass(ImageAnnotatorClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.vision.v1.spring.auto.image-annotator.enabled",
    matchIfMissing = false)
@EnableConfigurationProperties(ImageAnnotatorSpringProperties.class)
public class ImageAnnotatorSpringAutoConfiguration {
  private final ImageAnnotatorSpringProperties clientProperties;
  private static final Log LOGGER = LogFactory.getLog(ImageAnnotatorSpringAutoConfig.class);

  protected ImageAnnotatorSpringAutoConfiguration(ImageAnnotatorSpringProperties clientProperties) {
    this.clientProperties = clientProperties;
  }

  @Bean
  @ConditionalOnMissingBean
  public CredentialsProvider imageAnnotatorCredentials() throws IOException {
    return ((CredentialsProvider) new DefaultCredentialsProvider(this.clientProperties));
  }

  @Bean
  @ConditionalOnMissingBean
  public TransportChannelProvider defaultImageAnnotatorTransportChannelProvider() {
    return ImageAnnotatorSettings.defaultTransportChannelProvider();
  }

  @Bean
  @ConditionalOnMissingBean
  public ImageAnnotatorClient imageAnnotatorClient(
      @Qualifier("imageAnnotatorCredentials") CredentialsProvider credentialsProvider,
      @Qualifier("defaultImageAnnotatorTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ImageAnnotatorSettings.Builder clientSettingsBuilder =
        ImageAnnotatorSettings.newBuilder()
            .setCredentialsProvider(credentialsProvider)
            .setTransportChannelProvider(defaultTransportChannelProvider)
            .setHeaderProvider();
    if (this.clientProperties.getQuotaProjectId() != null) {
      clientSettingsBuilder.setQuotaProjectId(this.clientProperties.getQuotaProjectId());
      LOGGER.info(
          "Quota project id set to "
              + this.clientProperties.getQuotaProjectId()
              + ", this overrides project id from credentials.");
    }
    if (this.clientProperties.getExecutorThreadCount() != null) {
      ExecutorProvider executorProvider =
          ImageAnnotatorSettings.defaultExecutorProviderBuilder()
              .setExecutorThreadCount(this.clientProperties.getExecutorThreadCount())
              .build();
      clientSettingsBuilder.setBackgroundExecutorProvider(executorProvider);
      LOGGER.info(
          "Background executor thread count is " + this.clientProperties.getExecutorThreadCount());
    }
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder.setTransportChannelProvider(
          ImageAnnotatorSettings.defaultHttpJsonTransportProviderBuilder().build());
      LOGGER.info("Using HTTP transport channel");
    }
    RetrySettings.Builder batchAnnotateImagesRetrySettingBuilder =
        clientSettingsBuilder.batchAnnotateImagesSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getBatchAnnotateImagesInitialRetryDelay() != null) {
      batchAnnotateImagesRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getBatchAnnotateImagesInitialRetryDelay());
      LOGGER.info(
          "BatchAnnotateImagesInitialRetryDelay set to "
              + this.clientProperties.getBatchAnnotateImagesInitialRetryDelay());
    }
    if (this.clientProperties.getBatchAnnotateImagesRetryDelayMultiplier() != null) {
      batchAnnotateImagesRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getBatchAnnotateImagesRetryDelayMultiplier());
      LOGGER.info(
          "BatchAnnotateImagesRetryDelayMultiplier set to "
              + this.clientProperties.getBatchAnnotateImagesRetryDelayMultiplier());
    }
    if (this.clientProperties.getBatchAnnotateImagesMaxRetryDelay() != null) {
      batchAnnotateImagesRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getBatchAnnotateImagesMaxRetryDelay());
      LOGGER.info(
          "BatchAnnotateImagesMaxRetryDelay set to "
              + this.clientProperties.getBatchAnnotateImagesMaxRetryDelay());
    }
    if (this.clientProperties.getBatchAnnotateImagesInitialRpcTimeout() != null) {
      batchAnnotateImagesRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getBatchAnnotateImagesInitialRpcTimeout());
      LOGGER.info(
          "BatchAnnotateImagesInitialRpcTimeout set to "
              + this.clientProperties.getBatchAnnotateImagesInitialRpcTimeout());
    }
    if (this.clientProperties.getBatchAnnotateImagesRpcTimeoutMultiplier() != null) {
      batchAnnotateImagesRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getBatchAnnotateImagesRpcTimeoutMultiplier());
      LOGGER.info(
          "BatchAnnotateImagesRpcTimeoutMultiplier set to "
              + this.clientProperties.getBatchAnnotateImagesRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getBatchAnnotateImagesMaxRpcTimeout() != null) {
      batchAnnotateImagesRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getBatchAnnotateImagesMaxRpcTimeout());
      LOGGER.info(
          "BatchAnnotateImagesMaxRpcTimeout set to "
              + this.clientProperties.getBatchAnnotateImagesMaxRpcTimeout());
    }
    if (this.clientProperties.getBatchAnnotateImagesTotalTimeout() != null) {
      batchAnnotateImagesRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getBatchAnnotateImagesTotalTimeout());
      LOGGER.info(
          "BatchAnnotateImagesTotalTimeout set to "
              + this.clientProperties.getBatchAnnotateImagesTotalTimeout());
    }
    clientSettingsBuilder
        .batchAnnotateImagesSettings()
        .setRetrySettings(batchAnnotateImagesRetrySettingBuilder.build());
    RetrySettings.Builder batchAnnotateFilesRetrySettingBuilder =
        clientSettingsBuilder.batchAnnotateFilesSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getBatchAnnotateFilesInitialRetryDelay() != null) {
      batchAnnotateFilesRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getBatchAnnotateFilesInitialRetryDelay());
      LOGGER.info(
          "BatchAnnotateFilesInitialRetryDelay set to "
              + this.clientProperties.getBatchAnnotateFilesInitialRetryDelay());
    }
    if (this.clientProperties.getBatchAnnotateFilesRetryDelayMultiplier() != null) {
      batchAnnotateFilesRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getBatchAnnotateFilesRetryDelayMultiplier());
      LOGGER.info(
          "BatchAnnotateFilesRetryDelayMultiplier set to "
              + this.clientProperties.getBatchAnnotateFilesRetryDelayMultiplier());
    }
    if (this.clientProperties.getBatchAnnotateFilesMaxRetryDelay() != null) {
      batchAnnotateFilesRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getBatchAnnotateFilesMaxRetryDelay());
      LOGGER.info(
          "BatchAnnotateFilesMaxRetryDelay set to "
              + this.clientProperties.getBatchAnnotateFilesMaxRetryDelay());
    }
    if (this.clientProperties.getBatchAnnotateFilesInitialRpcTimeout() != null) {
      batchAnnotateFilesRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getBatchAnnotateFilesInitialRpcTimeout());
      LOGGER.info(
          "BatchAnnotateFilesInitialRpcTimeout set to "
              + this.clientProperties.getBatchAnnotateFilesInitialRpcTimeout());
    }
    if (this.clientProperties.getBatchAnnotateFilesRpcTimeoutMultiplier() != null) {
      batchAnnotateFilesRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getBatchAnnotateFilesRpcTimeoutMultiplier());
      LOGGER.info(
          "BatchAnnotateFilesRpcTimeoutMultiplier set to "
              + this.clientProperties.getBatchAnnotateFilesRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getBatchAnnotateFilesMaxRpcTimeout() != null) {
      batchAnnotateFilesRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getBatchAnnotateFilesMaxRpcTimeout());
      LOGGER.info(
          "BatchAnnotateFilesMaxRpcTimeout set to "
              + this.clientProperties.getBatchAnnotateFilesMaxRpcTimeout());
    }
    if (this.clientProperties.getBatchAnnotateFilesTotalTimeout() != null) {
      batchAnnotateFilesRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getBatchAnnotateFilesTotalTimeout());
      LOGGER.info(
          "BatchAnnotateFilesTotalTimeout set to "
              + this.clientProperties.getBatchAnnotateFilesTotalTimeout());
    }
    clientSettingsBuilder
        .batchAnnotateFilesSettings()
        .setRetrySettings(batchAnnotateFilesRetrySettingBuilder.build());
    RetrySettings.Builder asyncBatchAnnotateImagesRetrySettingBuilder =
        clientSettingsBuilder.asyncBatchAnnotateImagesSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getAsyncBatchAnnotateImagesInitialRetryDelay() != null) {
      asyncBatchAnnotateImagesRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getAsyncBatchAnnotateImagesInitialRetryDelay());
      LOGGER.info(
          "AsyncBatchAnnotateImagesInitialRetryDelay set to "
              + this.clientProperties.getAsyncBatchAnnotateImagesInitialRetryDelay());
    }
    if (this.clientProperties.getAsyncBatchAnnotateImagesRetryDelayMultiplier() != null) {
      asyncBatchAnnotateImagesRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getAsyncBatchAnnotateImagesRetryDelayMultiplier());
      LOGGER.info(
          "AsyncBatchAnnotateImagesRetryDelayMultiplier set to "
              + this.clientProperties.getAsyncBatchAnnotateImagesRetryDelayMultiplier());
    }
    if (this.clientProperties.getAsyncBatchAnnotateImagesMaxRetryDelay() != null) {
      asyncBatchAnnotateImagesRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getAsyncBatchAnnotateImagesMaxRetryDelay());
      LOGGER.info(
          "AsyncBatchAnnotateImagesMaxRetryDelay set to "
              + this.clientProperties.getAsyncBatchAnnotateImagesMaxRetryDelay());
    }
    if (this.clientProperties.getAsyncBatchAnnotateImagesInitialRpcTimeout() != null) {
      asyncBatchAnnotateImagesRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getAsyncBatchAnnotateImagesInitialRpcTimeout());
      LOGGER.info(
          "AsyncBatchAnnotateImagesInitialRpcTimeout set to "
              + this.clientProperties.getAsyncBatchAnnotateImagesInitialRpcTimeout());
    }
    if (this.clientProperties.getAsyncBatchAnnotateImagesRpcTimeoutMultiplier() != null) {
      asyncBatchAnnotateImagesRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getAsyncBatchAnnotateImagesRpcTimeoutMultiplier());
      LOGGER.info(
          "AsyncBatchAnnotateImagesRpcTimeoutMultiplier set to "
              + this.clientProperties.getAsyncBatchAnnotateImagesRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getAsyncBatchAnnotateImagesMaxRpcTimeout() != null) {
      asyncBatchAnnotateImagesRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getAsyncBatchAnnotateImagesMaxRpcTimeout());
      LOGGER.info(
          "AsyncBatchAnnotateImagesMaxRpcTimeout set to "
              + this.clientProperties.getAsyncBatchAnnotateImagesMaxRpcTimeout());
    }
    if (this.clientProperties.getAsyncBatchAnnotateImagesTotalTimeout() != null) {
      asyncBatchAnnotateImagesRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getAsyncBatchAnnotateImagesTotalTimeout());
      LOGGER.info(
          "AsyncBatchAnnotateImagesTotalTimeout set to "
              + this.clientProperties.getAsyncBatchAnnotateImagesTotalTimeout());
    }
    clientSettingsBuilder
        .asyncBatchAnnotateImagesSettings()
        .setRetrySettings(asyncBatchAnnotateImagesRetrySettingBuilder.build());
    RetrySettings.Builder asyncBatchAnnotateFilesRetrySettingBuilder =
        clientSettingsBuilder.asyncBatchAnnotateFilesSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getAsyncBatchAnnotateFilesInitialRetryDelay() != null) {
      asyncBatchAnnotateFilesRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getAsyncBatchAnnotateFilesInitialRetryDelay());
      LOGGER.info(
          "AsyncBatchAnnotateFilesInitialRetryDelay set to "
              + this.clientProperties.getAsyncBatchAnnotateFilesInitialRetryDelay());
    }
    if (this.clientProperties.getAsyncBatchAnnotateFilesRetryDelayMultiplier() != null) {
      asyncBatchAnnotateFilesRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getAsyncBatchAnnotateFilesRetryDelayMultiplier());
      LOGGER.info(
          "AsyncBatchAnnotateFilesRetryDelayMultiplier set to "
              + this.clientProperties.getAsyncBatchAnnotateFilesRetryDelayMultiplier());
    }
    if (this.clientProperties.getAsyncBatchAnnotateFilesMaxRetryDelay() != null) {
      asyncBatchAnnotateFilesRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getAsyncBatchAnnotateFilesMaxRetryDelay());
      LOGGER.info(
          "AsyncBatchAnnotateFilesMaxRetryDelay set to "
              + this.clientProperties.getAsyncBatchAnnotateFilesMaxRetryDelay());
    }
    if (this.clientProperties.getAsyncBatchAnnotateFilesInitialRpcTimeout() != null) {
      asyncBatchAnnotateFilesRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getAsyncBatchAnnotateFilesInitialRpcTimeout());
      LOGGER.info(
          "AsyncBatchAnnotateFilesInitialRpcTimeout set to "
              + this.clientProperties.getAsyncBatchAnnotateFilesInitialRpcTimeout());
    }
    if (this.clientProperties.getAsyncBatchAnnotateFilesRpcTimeoutMultiplier() != null) {
      asyncBatchAnnotateFilesRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getAsyncBatchAnnotateFilesRpcTimeoutMultiplier());
      LOGGER.info(
          "AsyncBatchAnnotateFilesRpcTimeoutMultiplier set to "
              + this.clientProperties.getAsyncBatchAnnotateFilesRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getAsyncBatchAnnotateFilesMaxRpcTimeout() != null) {
      asyncBatchAnnotateFilesRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getAsyncBatchAnnotateFilesMaxRpcTimeout());
      LOGGER.info(
          "AsyncBatchAnnotateFilesMaxRpcTimeout set to "
              + this.clientProperties.getAsyncBatchAnnotateFilesMaxRpcTimeout());
    }
    if (this.clientProperties.getAsyncBatchAnnotateFilesTotalTimeout() != null) {
      asyncBatchAnnotateFilesRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getAsyncBatchAnnotateFilesTotalTimeout());
      LOGGER.info(
          "AsyncBatchAnnotateFilesTotalTimeout set to "
              + this.clientProperties.getAsyncBatchAnnotateFilesTotalTimeout());
    }
    clientSettingsBuilder
        .asyncBatchAnnotateFilesSettings()
        .setRetrySettings(asyncBatchAnnotateFilesRetrySettingBuilder.build());
    return ImageAnnotatorClient.create(clientSettingsBuilder.build());
  }
}
