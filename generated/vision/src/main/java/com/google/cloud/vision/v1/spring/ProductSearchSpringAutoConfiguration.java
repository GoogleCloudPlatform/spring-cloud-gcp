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
import com.google.cloud.vision.v1.ProductSearchClient;
import com.google.cloud.vision.v1.ProductSearchSettings;
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
@ConditionalOnClass(ProductSearchClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.vision.v1.spring.auto.product-search.enabled",
    matchIfMissing = false)
@EnableConfigurationProperties(ProductSearchSpringProperties.class)
public class ProductSearchSpringAutoConfiguration {
  private final ProductSearchSpringProperties clientProperties;
  private static final Log LOGGER = LogFactory.getLog(ProductSearchSpringAutoConfig.class);

  protected ProductSearchSpringAutoConfiguration(ProductSearchSpringProperties clientProperties) {
    this.clientProperties = clientProperties;
  }

  @Bean
  @ConditionalOnMissingBean
  public CredentialsProvider productSearchCredentials() throws IOException {
    return ((CredentialsProvider) new DefaultCredentialsProvider(this.clientProperties));
  }

  @Bean
  @ConditionalOnMissingBean
  public TransportChannelProvider defaultProductSearchTransportChannelProvider() {
    return ProductSearchSettings.defaultTransportChannelProvider();
  }

  @Bean
  @ConditionalOnMissingBean
  public ProductSearchClient productSearchClient(
      @Qualifier("productSearchCredentials") CredentialsProvider credentialsProvider,
      @Qualifier("defaultProductSearchTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ProductSearchSettings.Builder clientSettingsBuilder =
        ProductSearchSettings.newBuilder()
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
          ProductSearchSettings.defaultExecutorProviderBuilder()
              .setExecutorThreadCount(this.clientProperties.getExecutorThreadCount())
              .build();
      clientSettingsBuilder.setBackgroundExecutorProvider(executorProvider);
      LOGGER.info(
          "Background executor thread count is " + this.clientProperties.getExecutorThreadCount());
    }
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder.setTransportChannelProvider(
          ProductSearchSettings.defaultHttpJsonTransportProviderBuilder().build());
      LOGGER.info("Using HTTP transport channel");
    }
    RetrySettings.Builder createProductSetRetrySettingBuilder =
        clientSettingsBuilder.createProductSetSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getCreateProductSetInitialRetryDelay() != null) {
      createProductSetRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getCreateProductSetInitialRetryDelay());
      LOGGER.info(
          "CreateProductSetInitialRetryDelay set to "
              + this.clientProperties.getCreateProductSetInitialRetryDelay());
    }
    if (this.clientProperties.getCreateProductSetRetryDelayMultiplier() != null) {
      createProductSetRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getCreateProductSetRetryDelayMultiplier());
      LOGGER.info(
          "CreateProductSetRetryDelayMultiplier set to "
              + this.clientProperties.getCreateProductSetRetryDelayMultiplier());
    }
    if (this.clientProperties.getCreateProductSetMaxRetryDelay() != null) {
      createProductSetRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getCreateProductSetMaxRetryDelay());
      LOGGER.info(
          "CreateProductSetMaxRetryDelay set to "
              + this.clientProperties.getCreateProductSetMaxRetryDelay());
    }
    if (this.clientProperties.getCreateProductSetInitialRpcTimeout() != null) {
      createProductSetRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getCreateProductSetInitialRpcTimeout());
      LOGGER.info(
          "CreateProductSetInitialRpcTimeout set to "
              + this.clientProperties.getCreateProductSetInitialRpcTimeout());
    }
    if (this.clientProperties.getCreateProductSetRpcTimeoutMultiplier() != null) {
      createProductSetRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getCreateProductSetRpcTimeoutMultiplier());
      LOGGER.info(
          "CreateProductSetRpcTimeoutMultiplier set to "
              + this.clientProperties.getCreateProductSetRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getCreateProductSetMaxRpcTimeout() != null) {
      createProductSetRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getCreateProductSetMaxRpcTimeout());
      LOGGER.info(
          "CreateProductSetMaxRpcTimeout set to "
              + this.clientProperties.getCreateProductSetMaxRpcTimeout());
    }
    if (this.clientProperties.getCreateProductSetTotalTimeout() != null) {
      createProductSetRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getCreateProductSetTotalTimeout());
      LOGGER.info(
          "CreateProductSetTotalTimeout set to "
              + this.clientProperties.getCreateProductSetTotalTimeout());
    }
    clientSettingsBuilder
        .createProductSetSettings()
        .setRetrySettings(createProductSetRetrySettingBuilder.build());
    RetrySettings.Builder listProductSetsRetrySettingBuilder =
        clientSettingsBuilder.listProductSetsSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getListProductSetsInitialRetryDelay() != null) {
      listProductSetsRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getListProductSetsInitialRetryDelay());
      LOGGER.info(
          "ListProductSetsInitialRetryDelay set to "
              + this.clientProperties.getListProductSetsInitialRetryDelay());
    }
    if (this.clientProperties.getListProductSetsRetryDelayMultiplier() != null) {
      listProductSetsRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getListProductSetsRetryDelayMultiplier());
      LOGGER.info(
          "ListProductSetsRetryDelayMultiplier set to "
              + this.clientProperties.getListProductSetsRetryDelayMultiplier());
    }
    if (this.clientProperties.getListProductSetsMaxRetryDelay() != null) {
      listProductSetsRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getListProductSetsMaxRetryDelay());
      LOGGER.info(
          "ListProductSetsMaxRetryDelay set to "
              + this.clientProperties.getListProductSetsMaxRetryDelay());
    }
    if (this.clientProperties.getListProductSetsInitialRpcTimeout() != null) {
      listProductSetsRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getListProductSetsInitialRpcTimeout());
      LOGGER.info(
          "ListProductSetsInitialRpcTimeout set to "
              + this.clientProperties.getListProductSetsInitialRpcTimeout());
    }
    if (this.clientProperties.getListProductSetsRpcTimeoutMultiplier() != null) {
      listProductSetsRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getListProductSetsRpcTimeoutMultiplier());
      LOGGER.info(
          "ListProductSetsRpcTimeoutMultiplier set to "
              + this.clientProperties.getListProductSetsRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getListProductSetsMaxRpcTimeout() != null) {
      listProductSetsRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getListProductSetsMaxRpcTimeout());
      LOGGER.info(
          "ListProductSetsMaxRpcTimeout set to "
              + this.clientProperties.getListProductSetsMaxRpcTimeout());
    }
    if (this.clientProperties.getListProductSetsTotalTimeout() != null) {
      listProductSetsRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getListProductSetsTotalTimeout());
      LOGGER.info(
          "ListProductSetsTotalTimeout set to "
              + this.clientProperties.getListProductSetsTotalTimeout());
    }
    clientSettingsBuilder
        .listProductSetsSettings()
        .setRetrySettings(listProductSetsRetrySettingBuilder.build());
    RetrySettings.Builder getProductSetRetrySettingBuilder =
        clientSettingsBuilder.getProductSetSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getGetProductSetInitialRetryDelay() != null) {
      getProductSetRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getGetProductSetInitialRetryDelay());
      LOGGER.info(
          "GetProductSetInitialRetryDelay set to "
              + this.clientProperties.getGetProductSetInitialRetryDelay());
    }
    if (this.clientProperties.getGetProductSetRetryDelayMultiplier() != null) {
      getProductSetRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getGetProductSetRetryDelayMultiplier());
      LOGGER.info(
          "GetProductSetRetryDelayMultiplier set to "
              + this.clientProperties.getGetProductSetRetryDelayMultiplier());
    }
    if (this.clientProperties.getGetProductSetMaxRetryDelay() != null) {
      getProductSetRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getGetProductSetMaxRetryDelay());
      LOGGER.info(
          "GetProductSetMaxRetryDelay set to "
              + this.clientProperties.getGetProductSetMaxRetryDelay());
    }
    if (this.clientProperties.getGetProductSetInitialRpcTimeout() != null) {
      getProductSetRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getGetProductSetInitialRpcTimeout());
      LOGGER.info(
          "GetProductSetInitialRpcTimeout set to "
              + this.clientProperties.getGetProductSetInitialRpcTimeout());
    }
    if (this.clientProperties.getGetProductSetRpcTimeoutMultiplier() != null) {
      getProductSetRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getGetProductSetRpcTimeoutMultiplier());
      LOGGER.info(
          "GetProductSetRpcTimeoutMultiplier set to "
              + this.clientProperties.getGetProductSetRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getGetProductSetMaxRpcTimeout() != null) {
      getProductSetRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getGetProductSetMaxRpcTimeout());
      LOGGER.info(
          "GetProductSetMaxRpcTimeout set to "
              + this.clientProperties.getGetProductSetMaxRpcTimeout());
    }
    if (this.clientProperties.getGetProductSetTotalTimeout() != null) {
      getProductSetRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getGetProductSetTotalTimeout());
      LOGGER.info(
          "GetProductSetTotalTimeout set to "
              + this.clientProperties.getGetProductSetTotalTimeout());
    }
    clientSettingsBuilder
        .getProductSetSettings()
        .setRetrySettings(getProductSetRetrySettingBuilder.build());
    RetrySettings.Builder updateProductSetRetrySettingBuilder =
        clientSettingsBuilder.updateProductSetSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getUpdateProductSetInitialRetryDelay() != null) {
      updateProductSetRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getUpdateProductSetInitialRetryDelay());
      LOGGER.info(
          "UpdateProductSetInitialRetryDelay set to "
              + this.clientProperties.getUpdateProductSetInitialRetryDelay());
    }
    if (this.clientProperties.getUpdateProductSetRetryDelayMultiplier() != null) {
      updateProductSetRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getUpdateProductSetRetryDelayMultiplier());
      LOGGER.info(
          "UpdateProductSetRetryDelayMultiplier set to "
              + this.clientProperties.getUpdateProductSetRetryDelayMultiplier());
    }
    if (this.clientProperties.getUpdateProductSetMaxRetryDelay() != null) {
      updateProductSetRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getUpdateProductSetMaxRetryDelay());
      LOGGER.info(
          "UpdateProductSetMaxRetryDelay set to "
              + this.clientProperties.getUpdateProductSetMaxRetryDelay());
    }
    if (this.clientProperties.getUpdateProductSetInitialRpcTimeout() != null) {
      updateProductSetRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getUpdateProductSetInitialRpcTimeout());
      LOGGER.info(
          "UpdateProductSetInitialRpcTimeout set to "
              + this.clientProperties.getUpdateProductSetInitialRpcTimeout());
    }
    if (this.clientProperties.getUpdateProductSetRpcTimeoutMultiplier() != null) {
      updateProductSetRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getUpdateProductSetRpcTimeoutMultiplier());
      LOGGER.info(
          "UpdateProductSetRpcTimeoutMultiplier set to "
              + this.clientProperties.getUpdateProductSetRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getUpdateProductSetMaxRpcTimeout() != null) {
      updateProductSetRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getUpdateProductSetMaxRpcTimeout());
      LOGGER.info(
          "UpdateProductSetMaxRpcTimeout set to "
              + this.clientProperties.getUpdateProductSetMaxRpcTimeout());
    }
    if (this.clientProperties.getUpdateProductSetTotalTimeout() != null) {
      updateProductSetRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getUpdateProductSetTotalTimeout());
      LOGGER.info(
          "UpdateProductSetTotalTimeout set to "
              + this.clientProperties.getUpdateProductSetTotalTimeout());
    }
    clientSettingsBuilder
        .updateProductSetSettings()
        .setRetrySettings(updateProductSetRetrySettingBuilder.build());
    RetrySettings.Builder deleteProductSetRetrySettingBuilder =
        clientSettingsBuilder.deleteProductSetSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getDeleteProductSetInitialRetryDelay() != null) {
      deleteProductSetRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getDeleteProductSetInitialRetryDelay());
      LOGGER.info(
          "DeleteProductSetInitialRetryDelay set to "
              + this.clientProperties.getDeleteProductSetInitialRetryDelay());
    }
    if (this.clientProperties.getDeleteProductSetRetryDelayMultiplier() != null) {
      deleteProductSetRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getDeleteProductSetRetryDelayMultiplier());
      LOGGER.info(
          "DeleteProductSetRetryDelayMultiplier set to "
              + this.clientProperties.getDeleteProductSetRetryDelayMultiplier());
    }
    if (this.clientProperties.getDeleteProductSetMaxRetryDelay() != null) {
      deleteProductSetRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getDeleteProductSetMaxRetryDelay());
      LOGGER.info(
          "DeleteProductSetMaxRetryDelay set to "
              + this.clientProperties.getDeleteProductSetMaxRetryDelay());
    }
    if (this.clientProperties.getDeleteProductSetInitialRpcTimeout() != null) {
      deleteProductSetRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getDeleteProductSetInitialRpcTimeout());
      LOGGER.info(
          "DeleteProductSetInitialRpcTimeout set to "
              + this.clientProperties.getDeleteProductSetInitialRpcTimeout());
    }
    if (this.clientProperties.getDeleteProductSetRpcTimeoutMultiplier() != null) {
      deleteProductSetRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getDeleteProductSetRpcTimeoutMultiplier());
      LOGGER.info(
          "DeleteProductSetRpcTimeoutMultiplier set to "
              + this.clientProperties.getDeleteProductSetRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getDeleteProductSetMaxRpcTimeout() != null) {
      deleteProductSetRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getDeleteProductSetMaxRpcTimeout());
      LOGGER.info(
          "DeleteProductSetMaxRpcTimeout set to "
              + this.clientProperties.getDeleteProductSetMaxRpcTimeout());
    }
    if (this.clientProperties.getDeleteProductSetTotalTimeout() != null) {
      deleteProductSetRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getDeleteProductSetTotalTimeout());
      LOGGER.info(
          "DeleteProductSetTotalTimeout set to "
              + this.clientProperties.getDeleteProductSetTotalTimeout());
    }
    clientSettingsBuilder
        .deleteProductSetSettings()
        .setRetrySettings(deleteProductSetRetrySettingBuilder.build());
    RetrySettings.Builder createProductRetrySettingBuilder =
        clientSettingsBuilder.createProductSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getCreateProductInitialRetryDelay() != null) {
      createProductRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getCreateProductInitialRetryDelay());
      LOGGER.info(
          "CreateProductInitialRetryDelay set to "
              + this.clientProperties.getCreateProductInitialRetryDelay());
    }
    if (this.clientProperties.getCreateProductRetryDelayMultiplier() != null) {
      createProductRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getCreateProductRetryDelayMultiplier());
      LOGGER.info(
          "CreateProductRetryDelayMultiplier set to "
              + this.clientProperties.getCreateProductRetryDelayMultiplier());
    }
    if (this.clientProperties.getCreateProductMaxRetryDelay() != null) {
      createProductRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getCreateProductMaxRetryDelay());
      LOGGER.info(
          "CreateProductMaxRetryDelay set to "
              + this.clientProperties.getCreateProductMaxRetryDelay());
    }
    if (this.clientProperties.getCreateProductInitialRpcTimeout() != null) {
      createProductRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getCreateProductInitialRpcTimeout());
      LOGGER.info(
          "CreateProductInitialRpcTimeout set to "
              + this.clientProperties.getCreateProductInitialRpcTimeout());
    }
    if (this.clientProperties.getCreateProductRpcTimeoutMultiplier() != null) {
      createProductRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getCreateProductRpcTimeoutMultiplier());
      LOGGER.info(
          "CreateProductRpcTimeoutMultiplier set to "
              + this.clientProperties.getCreateProductRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getCreateProductMaxRpcTimeout() != null) {
      createProductRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getCreateProductMaxRpcTimeout());
      LOGGER.info(
          "CreateProductMaxRpcTimeout set to "
              + this.clientProperties.getCreateProductMaxRpcTimeout());
    }
    if (this.clientProperties.getCreateProductTotalTimeout() != null) {
      createProductRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getCreateProductTotalTimeout());
      LOGGER.info(
          "CreateProductTotalTimeout set to "
              + this.clientProperties.getCreateProductTotalTimeout());
    }
    clientSettingsBuilder
        .createProductSettings()
        .setRetrySettings(createProductRetrySettingBuilder.build());
    RetrySettings.Builder listProductsRetrySettingBuilder =
        clientSettingsBuilder.listProductsSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getListProductsInitialRetryDelay() != null) {
      listProductsRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getListProductsInitialRetryDelay());
      LOGGER.info(
          "ListProductsInitialRetryDelay set to "
              + this.clientProperties.getListProductsInitialRetryDelay());
    }
    if (this.clientProperties.getListProductsRetryDelayMultiplier() != null) {
      listProductsRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getListProductsRetryDelayMultiplier());
      LOGGER.info(
          "ListProductsRetryDelayMultiplier set to "
              + this.clientProperties.getListProductsRetryDelayMultiplier());
    }
    if (this.clientProperties.getListProductsMaxRetryDelay() != null) {
      listProductsRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getListProductsMaxRetryDelay());
      LOGGER.info(
          "ListProductsMaxRetryDelay set to "
              + this.clientProperties.getListProductsMaxRetryDelay());
    }
    if (this.clientProperties.getListProductsInitialRpcTimeout() != null) {
      listProductsRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getListProductsInitialRpcTimeout());
      LOGGER.info(
          "ListProductsInitialRpcTimeout set to "
              + this.clientProperties.getListProductsInitialRpcTimeout());
    }
    if (this.clientProperties.getListProductsRpcTimeoutMultiplier() != null) {
      listProductsRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getListProductsRpcTimeoutMultiplier());
      LOGGER.info(
          "ListProductsRpcTimeoutMultiplier set to "
              + this.clientProperties.getListProductsRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getListProductsMaxRpcTimeout() != null) {
      listProductsRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getListProductsMaxRpcTimeout());
      LOGGER.info(
          "ListProductsMaxRpcTimeout set to "
              + this.clientProperties.getListProductsMaxRpcTimeout());
    }
    if (this.clientProperties.getListProductsTotalTimeout() != null) {
      listProductsRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getListProductsTotalTimeout());
      LOGGER.info(
          "ListProductsTotalTimeout set to " + this.clientProperties.getListProductsTotalTimeout());
    }
    clientSettingsBuilder
        .listProductsSettings()
        .setRetrySettings(listProductsRetrySettingBuilder.build());
    RetrySettings.Builder getProductRetrySettingBuilder =
        clientSettingsBuilder.getProductSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getGetProductInitialRetryDelay() != null) {
      getProductRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getGetProductInitialRetryDelay());
      LOGGER.info(
          "GetProductInitialRetryDelay set to "
              + this.clientProperties.getGetProductInitialRetryDelay());
    }
    if (this.clientProperties.getGetProductRetryDelayMultiplier() != null) {
      getProductRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getGetProductRetryDelayMultiplier());
      LOGGER.info(
          "GetProductRetryDelayMultiplier set to "
              + this.clientProperties.getGetProductRetryDelayMultiplier());
    }
    if (this.clientProperties.getGetProductMaxRetryDelay() != null) {
      getProductRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getGetProductMaxRetryDelay());
      LOGGER.info(
          "GetProductMaxRetryDelay set to " + this.clientProperties.getGetProductMaxRetryDelay());
    }
    if (this.clientProperties.getGetProductInitialRpcTimeout() != null) {
      getProductRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getGetProductInitialRpcTimeout());
      LOGGER.info(
          "GetProductInitialRpcTimeout set to "
              + this.clientProperties.getGetProductInitialRpcTimeout());
    }
    if (this.clientProperties.getGetProductRpcTimeoutMultiplier() != null) {
      getProductRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getGetProductRpcTimeoutMultiplier());
      LOGGER.info(
          "GetProductRpcTimeoutMultiplier set to "
              + this.clientProperties.getGetProductRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getGetProductMaxRpcTimeout() != null) {
      getProductRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getGetProductMaxRpcTimeout());
      LOGGER.info(
          "GetProductMaxRpcTimeout set to " + this.clientProperties.getGetProductMaxRpcTimeout());
    }
    if (this.clientProperties.getGetProductTotalTimeout() != null) {
      getProductRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getGetProductTotalTimeout());
      LOGGER.info(
          "GetProductTotalTimeout set to " + this.clientProperties.getGetProductTotalTimeout());
    }
    clientSettingsBuilder
        .getProductSettings()
        .setRetrySettings(getProductRetrySettingBuilder.build());
    RetrySettings.Builder updateProductRetrySettingBuilder =
        clientSettingsBuilder.updateProductSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getUpdateProductInitialRetryDelay() != null) {
      updateProductRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getUpdateProductInitialRetryDelay());
      LOGGER.info(
          "UpdateProductInitialRetryDelay set to "
              + this.clientProperties.getUpdateProductInitialRetryDelay());
    }
    if (this.clientProperties.getUpdateProductRetryDelayMultiplier() != null) {
      updateProductRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getUpdateProductRetryDelayMultiplier());
      LOGGER.info(
          "UpdateProductRetryDelayMultiplier set to "
              + this.clientProperties.getUpdateProductRetryDelayMultiplier());
    }
    if (this.clientProperties.getUpdateProductMaxRetryDelay() != null) {
      updateProductRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getUpdateProductMaxRetryDelay());
      LOGGER.info(
          "UpdateProductMaxRetryDelay set to "
              + this.clientProperties.getUpdateProductMaxRetryDelay());
    }
    if (this.clientProperties.getUpdateProductInitialRpcTimeout() != null) {
      updateProductRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getUpdateProductInitialRpcTimeout());
      LOGGER.info(
          "UpdateProductInitialRpcTimeout set to "
              + this.clientProperties.getUpdateProductInitialRpcTimeout());
    }
    if (this.clientProperties.getUpdateProductRpcTimeoutMultiplier() != null) {
      updateProductRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getUpdateProductRpcTimeoutMultiplier());
      LOGGER.info(
          "UpdateProductRpcTimeoutMultiplier set to "
              + this.clientProperties.getUpdateProductRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getUpdateProductMaxRpcTimeout() != null) {
      updateProductRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getUpdateProductMaxRpcTimeout());
      LOGGER.info(
          "UpdateProductMaxRpcTimeout set to "
              + this.clientProperties.getUpdateProductMaxRpcTimeout());
    }
    if (this.clientProperties.getUpdateProductTotalTimeout() != null) {
      updateProductRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getUpdateProductTotalTimeout());
      LOGGER.info(
          "UpdateProductTotalTimeout set to "
              + this.clientProperties.getUpdateProductTotalTimeout());
    }
    clientSettingsBuilder
        .updateProductSettings()
        .setRetrySettings(updateProductRetrySettingBuilder.build());
    RetrySettings.Builder deleteProductRetrySettingBuilder =
        clientSettingsBuilder.deleteProductSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getDeleteProductInitialRetryDelay() != null) {
      deleteProductRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getDeleteProductInitialRetryDelay());
      LOGGER.info(
          "DeleteProductInitialRetryDelay set to "
              + this.clientProperties.getDeleteProductInitialRetryDelay());
    }
    if (this.clientProperties.getDeleteProductRetryDelayMultiplier() != null) {
      deleteProductRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getDeleteProductRetryDelayMultiplier());
      LOGGER.info(
          "DeleteProductRetryDelayMultiplier set to "
              + this.clientProperties.getDeleteProductRetryDelayMultiplier());
    }
    if (this.clientProperties.getDeleteProductMaxRetryDelay() != null) {
      deleteProductRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getDeleteProductMaxRetryDelay());
      LOGGER.info(
          "DeleteProductMaxRetryDelay set to "
              + this.clientProperties.getDeleteProductMaxRetryDelay());
    }
    if (this.clientProperties.getDeleteProductInitialRpcTimeout() != null) {
      deleteProductRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getDeleteProductInitialRpcTimeout());
      LOGGER.info(
          "DeleteProductInitialRpcTimeout set to "
              + this.clientProperties.getDeleteProductInitialRpcTimeout());
    }
    if (this.clientProperties.getDeleteProductRpcTimeoutMultiplier() != null) {
      deleteProductRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getDeleteProductRpcTimeoutMultiplier());
      LOGGER.info(
          "DeleteProductRpcTimeoutMultiplier set to "
              + this.clientProperties.getDeleteProductRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getDeleteProductMaxRpcTimeout() != null) {
      deleteProductRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getDeleteProductMaxRpcTimeout());
      LOGGER.info(
          "DeleteProductMaxRpcTimeout set to "
              + this.clientProperties.getDeleteProductMaxRpcTimeout());
    }
    if (this.clientProperties.getDeleteProductTotalTimeout() != null) {
      deleteProductRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getDeleteProductTotalTimeout());
      LOGGER.info(
          "DeleteProductTotalTimeout set to "
              + this.clientProperties.getDeleteProductTotalTimeout());
    }
    clientSettingsBuilder
        .deleteProductSettings()
        .setRetrySettings(deleteProductRetrySettingBuilder.build());
    RetrySettings.Builder createReferenceImageRetrySettingBuilder =
        clientSettingsBuilder.createReferenceImageSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getCreateReferenceImageInitialRetryDelay() != null) {
      createReferenceImageRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getCreateReferenceImageInitialRetryDelay());
      LOGGER.info(
          "CreateReferenceImageInitialRetryDelay set to "
              + this.clientProperties.getCreateReferenceImageInitialRetryDelay());
    }
    if (this.clientProperties.getCreateReferenceImageRetryDelayMultiplier() != null) {
      createReferenceImageRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getCreateReferenceImageRetryDelayMultiplier());
      LOGGER.info(
          "CreateReferenceImageRetryDelayMultiplier set to "
              + this.clientProperties.getCreateReferenceImageRetryDelayMultiplier());
    }
    if (this.clientProperties.getCreateReferenceImageMaxRetryDelay() != null) {
      createReferenceImageRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getCreateReferenceImageMaxRetryDelay());
      LOGGER.info(
          "CreateReferenceImageMaxRetryDelay set to "
              + this.clientProperties.getCreateReferenceImageMaxRetryDelay());
    }
    if (this.clientProperties.getCreateReferenceImageInitialRpcTimeout() != null) {
      createReferenceImageRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getCreateReferenceImageInitialRpcTimeout());
      LOGGER.info(
          "CreateReferenceImageInitialRpcTimeout set to "
              + this.clientProperties.getCreateReferenceImageInitialRpcTimeout());
    }
    if (this.clientProperties.getCreateReferenceImageRpcTimeoutMultiplier() != null) {
      createReferenceImageRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getCreateReferenceImageRpcTimeoutMultiplier());
      LOGGER.info(
          "CreateReferenceImageRpcTimeoutMultiplier set to "
              + this.clientProperties.getCreateReferenceImageRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getCreateReferenceImageMaxRpcTimeout() != null) {
      createReferenceImageRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getCreateReferenceImageMaxRpcTimeout());
      LOGGER.info(
          "CreateReferenceImageMaxRpcTimeout set to "
              + this.clientProperties.getCreateReferenceImageMaxRpcTimeout());
    }
    if (this.clientProperties.getCreateReferenceImageTotalTimeout() != null) {
      createReferenceImageRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getCreateReferenceImageTotalTimeout());
      LOGGER.info(
          "CreateReferenceImageTotalTimeout set to "
              + this.clientProperties.getCreateReferenceImageTotalTimeout());
    }
    clientSettingsBuilder
        .createReferenceImageSettings()
        .setRetrySettings(createReferenceImageRetrySettingBuilder.build());
    RetrySettings.Builder deleteReferenceImageRetrySettingBuilder =
        clientSettingsBuilder.deleteReferenceImageSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getDeleteReferenceImageInitialRetryDelay() != null) {
      deleteReferenceImageRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getDeleteReferenceImageInitialRetryDelay());
      LOGGER.info(
          "DeleteReferenceImageInitialRetryDelay set to "
              + this.clientProperties.getDeleteReferenceImageInitialRetryDelay());
    }
    if (this.clientProperties.getDeleteReferenceImageRetryDelayMultiplier() != null) {
      deleteReferenceImageRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getDeleteReferenceImageRetryDelayMultiplier());
      LOGGER.info(
          "DeleteReferenceImageRetryDelayMultiplier set to "
              + this.clientProperties.getDeleteReferenceImageRetryDelayMultiplier());
    }
    if (this.clientProperties.getDeleteReferenceImageMaxRetryDelay() != null) {
      deleteReferenceImageRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getDeleteReferenceImageMaxRetryDelay());
      LOGGER.info(
          "DeleteReferenceImageMaxRetryDelay set to "
              + this.clientProperties.getDeleteReferenceImageMaxRetryDelay());
    }
    if (this.clientProperties.getDeleteReferenceImageInitialRpcTimeout() != null) {
      deleteReferenceImageRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getDeleteReferenceImageInitialRpcTimeout());
      LOGGER.info(
          "DeleteReferenceImageInitialRpcTimeout set to "
              + this.clientProperties.getDeleteReferenceImageInitialRpcTimeout());
    }
    if (this.clientProperties.getDeleteReferenceImageRpcTimeoutMultiplier() != null) {
      deleteReferenceImageRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getDeleteReferenceImageRpcTimeoutMultiplier());
      LOGGER.info(
          "DeleteReferenceImageRpcTimeoutMultiplier set to "
              + this.clientProperties.getDeleteReferenceImageRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getDeleteReferenceImageMaxRpcTimeout() != null) {
      deleteReferenceImageRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getDeleteReferenceImageMaxRpcTimeout());
      LOGGER.info(
          "DeleteReferenceImageMaxRpcTimeout set to "
              + this.clientProperties.getDeleteReferenceImageMaxRpcTimeout());
    }
    if (this.clientProperties.getDeleteReferenceImageTotalTimeout() != null) {
      deleteReferenceImageRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getDeleteReferenceImageTotalTimeout());
      LOGGER.info(
          "DeleteReferenceImageTotalTimeout set to "
              + this.clientProperties.getDeleteReferenceImageTotalTimeout());
    }
    clientSettingsBuilder
        .deleteReferenceImageSettings()
        .setRetrySettings(deleteReferenceImageRetrySettingBuilder.build());
    RetrySettings.Builder listReferenceImagesRetrySettingBuilder =
        clientSettingsBuilder.listReferenceImagesSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getListReferenceImagesInitialRetryDelay() != null) {
      listReferenceImagesRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getListReferenceImagesInitialRetryDelay());
      LOGGER.info(
          "ListReferenceImagesInitialRetryDelay set to "
              + this.clientProperties.getListReferenceImagesInitialRetryDelay());
    }
    if (this.clientProperties.getListReferenceImagesRetryDelayMultiplier() != null) {
      listReferenceImagesRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getListReferenceImagesRetryDelayMultiplier());
      LOGGER.info(
          "ListReferenceImagesRetryDelayMultiplier set to "
              + this.clientProperties.getListReferenceImagesRetryDelayMultiplier());
    }
    if (this.clientProperties.getListReferenceImagesMaxRetryDelay() != null) {
      listReferenceImagesRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getListReferenceImagesMaxRetryDelay());
      LOGGER.info(
          "ListReferenceImagesMaxRetryDelay set to "
              + this.clientProperties.getListReferenceImagesMaxRetryDelay());
    }
    if (this.clientProperties.getListReferenceImagesInitialRpcTimeout() != null) {
      listReferenceImagesRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getListReferenceImagesInitialRpcTimeout());
      LOGGER.info(
          "ListReferenceImagesInitialRpcTimeout set to "
              + this.clientProperties.getListReferenceImagesInitialRpcTimeout());
    }
    if (this.clientProperties.getListReferenceImagesRpcTimeoutMultiplier() != null) {
      listReferenceImagesRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getListReferenceImagesRpcTimeoutMultiplier());
      LOGGER.info(
          "ListReferenceImagesRpcTimeoutMultiplier set to "
              + this.clientProperties.getListReferenceImagesRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getListReferenceImagesMaxRpcTimeout() != null) {
      listReferenceImagesRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getListReferenceImagesMaxRpcTimeout());
      LOGGER.info(
          "ListReferenceImagesMaxRpcTimeout set to "
              + this.clientProperties.getListReferenceImagesMaxRpcTimeout());
    }
    if (this.clientProperties.getListReferenceImagesTotalTimeout() != null) {
      listReferenceImagesRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getListReferenceImagesTotalTimeout());
      LOGGER.info(
          "ListReferenceImagesTotalTimeout set to "
              + this.clientProperties.getListReferenceImagesTotalTimeout());
    }
    clientSettingsBuilder
        .listReferenceImagesSettings()
        .setRetrySettings(listReferenceImagesRetrySettingBuilder.build());
    RetrySettings.Builder getReferenceImageRetrySettingBuilder =
        clientSettingsBuilder.getReferenceImageSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getGetReferenceImageInitialRetryDelay() != null) {
      getReferenceImageRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getGetReferenceImageInitialRetryDelay());
      LOGGER.info(
          "GetReferenceImageInitialRetryDelay set to "
              + this.clientProperties.getGetReferenceImageInitialRetryDelay());
    }
    if (this.clientProperties.getGetReferenceImageRetryDelayMultiplier() != null) {
      getReferenceImageRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getGetReferenceImageRetryDelayMultiplier());
      LOGGER.info(
          "GetReferenceImageRetryDelayMultiplier set to "
              + this.clientProperties.getGetReferenceImageRetryDelayMultiplier());
    }
    if (this.clientProperties.getGetReferenceImageMaxRetryDelay() != null) {
      getReferenceImageRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getGetReferenceImageMaxRetryDelay());
      LOGGER.info(
          "GetReferenceImageMaxRetryDelay set to "
              + this.clientProperties.getGetReferenceImageMaxRetryDelay());
    }
    if (this.clientProperties.getGetReferenceImageInitialRpcTimeout() != null) {
      getReferenceImageRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getGetReferenceImageInitialRpcTimeout());
      LOGGER.info(
          "GetReferenceImageInitialRpcTimeout set to "
              + this.clientProperties.getGetReferenceImageInitialRpcTimeout());
    }
    if (this.clientProperties.getGetReferenceImageRpcTimeoutMultiplier() != null) {
      getReferenceImageRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getGetReferenceImageRpcTimeoutMultiplier());
      LOGGER.info(
          "GetReferenceImageRpcTimeoutMultiplier set to "
              + this.clientProperties.getGetReferenceImageRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getGetReferenceImageMaxRpcTimeout() != null) {
      getReferenceImageRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getGetReferenceImageMaxRpcTimeout());
      LOGGER.info(
          "GetReferenceImageMaxRpcTimeout set to "
              + this.clientProperties.getGetReferenceImageMaxRpcTimeout());
    }
    if (this.clientProperties.getGetReferenceImageTotalTimeout() != null) {
      getReferenceImageRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getGetReferenceImageTotalTimeout());
      LOGGER.info(
          "GetReferenceImageTotalTimeout set to "
              + this.clientProperties.getGetReferenceImageTotalTimeout());
    }
    clientSettingsBuilder
        .getReferenceImageSettings()
        .setRetrySettings(getReferenceImageRetrySettingBuilder.build());
    RetrySettings.Builder addProductToProductSetRetrySettingBuilder =
        clientSettingsBuilder.addProductToProductSetSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getAddProductToProductSetInitialRetryDelay() != null) {
      addProductToProductSetRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getAddProductToProductSetInitialRetryDelay());
      LOGGER.info(
          "AddProductToProductSetInitialRetryDelay set to "
              + this.clientProperties.getAddProductToProductSetInitialRetryDelay());
    }
    if (this.clientProperties.getAddProductToProductSetRetryDelayMultiplier() != null) {
      addProductToProductSetRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getAddProductToProductSetRetryDelayMultiplier());
      LOGGER.info(
          "AddProductToProductSetRetryDelayMultiplier set to "
              + this.clientProperties.getAddProductToProductSetRetryDelayMultiplier());
    }
    if (this.clientProperties.getAddProductToProductSetMaxRetryDelay() != null) {
      addProductToProductSetRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getAddProductToProductSetMaxRetryDelay());
      LOGGER.info(
          "AddProductToProductSetMaxRetryDelay set to "
              + this.clientProperties.getAddProductToProductSetMaxRetryDelay());
    }
    if (this.clientProperties.getAddProductToProductSetInitialRpcTimeout() != null) {
      addProductToProductSetRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getAddProductToProductSetInitialRpcTimeout());
      LOGGER.info(
          "AddProductToProductSetInitialRpcTimeout set to "
              + this.clientProperties.getAddProductToProductSetInitialRpcTimeout());
    }
    if (this.clientProperties.getAddProductToProductSetRpcTimeoutMultiplier() != null) {
      addProductToProductSetRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getAddProductToProductSetRpcTimeoutMultiplier());
      LOGGER.info(
          "AddProductToProductSetRpcTimeoutMultiplier set to "
              + this.clientProperties.getAddProductToProductSetRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getAddProductToProductSetMaxRpcTimeout() != null) {
      addProductToProductSetRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getAddProductToProductSetMaxRpcTimeout());
      LOGGER.info(
          "AddProductToProductSetMaxRpcTimeout set to "
              + this.clientProperties.getAddProductToProductSetMaxRpcTimeout());
    }
    if (this.clientProperties.getAddProductToProductSetTotalTimeout() != null) {
      addProductToProductSetRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getAddProductToProductSetTotalTimeout());
      LOGGER.info(
          "AddProductToProductSetTotalTimeout set to "
              + this.clientProperties.getAddProductToProductSetTotalTimeout());
    }
    clientSettingsBuilder
        .addProductToProductSetSettings()
        .setRetrySettings(addProductToProductSetRetrySettingBuilder.build());
    RetrySettings.Builder removeProductFromProductSetRetrySettingBuilder =
        clientSettingsBuilder.removeProductFromProductSetSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getRemoveProductFromProductSetInitialRetryDelay() != null) {
      removeProductFromProductSetRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getRemoveProductFromProductSetInitialRetryDelay());
      LOGGER.info(
          "RemoveProductFromProductSetInitialRetryDelay set to "
              + this.clientProperties.getRemoveProductFromProductSetInitialRetryDelay());
    }
    if (this.clientProperties.getRemoveProductFromProductSetRetryDelayMultiplier() != null) {
      removeProductFromProductSetRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getRemoveProductFromProductSetRetryDelayMultiplier());
      LOGGER.info(
          "RemoveProductFromProductSetRetryDelayMultiplier set to "
              + this.clientProperties.getRemoveProductFromProductSetRetryDelayMultiplier());
    }
    if (this.clientProperties.getRemoveProductFromProductSetMaxRetryDelay() != null) {
      removeProductFromProductSetRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getRemoveProductFromProductSetMaxRetryDelay());
      LOGGER.info(
          "RemoveProductFromProductSetMaxRetryDelay set to "
              + this.clientProperties.getRemoveProductFromProductSetMaxRetryDelay());
    }
    if (this.clientProperties.getRemoveProductFromProductSetInitialRpcTimeout() != null) {
      removeProductFromProductSetRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getRemoveProductFromProductSetInitialRpcTimeout());
      LOGGER.info(
          "RemoveProductFromProductSetInitialRpcTimeout set to "
              + this.clientProperties.getRemoveProductFromProductSetInitialRpcTimeout());
    }
    if (this.clientProperties.getRemoveProductFromProductSetRpcTimeoutMultiplier() != null) {
      removeProductFromProductSetRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getRemoveProductFromProductSetRpcTimeoutMultiplier());
      LOGGER.info(
          "RemoveProductFromProductSetRpcTimeoutMultiplier set to "
              + this.clientProperties.getRemoveProductFromProductSetRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getRemoveProductFromProductSetMaxRpcTimeout() != null) {
      removeProductFromProductSetRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getRemoveProductFromProductSetMaxRpcTimeout());
      LOGGER.info(
          "RemoveProductFromProductSetMaxRpcTimeout set to "
              + this.clientProperties.getRemoveProductFromProductSetMaxRpcTimeout());
    }
    if (this.clientProperties.getRemoveProductFromProductSetTotalTimeout() != null) {
      removeProductFromProductSetRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getRemoveProductFromProductSetTotalTimeout());
      LOGGER.info(
          "RemoveProductFromProductSetTotalTimeout set to "
              + this.clientProperties.getRemoveProductFromProductSetTotalTimeout());
    }
    clientSettingsBuilder
        .removeProductFromProductSetSettings()
        .setRetrySettings(removeProductFromProductSetRetrySettingBuilder.build());
    RetrySettings.Builder listProductsInProductSetRetrySettingBuilder =
        clientSettingsBuilder.listProductsInProductSetSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getListProductsInProductSetInitialRetryDelay() != null) {
      listProductsInProductSetRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getListProductsInProductSetInitialRetryDelay());
      LOGGER.info(
          "ListProductsInProductSetInitialRetryDelay set to "
              + this.clientProperties.getListProductsInProductSetInitialRetryDelay());
    }
    if (this.clientProperties.getListProductsInProductSetRetryDelayMultiplier() != null) {
      listProductsInProductSetRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getListProductsInProductSetRetryDelayMultiplier());
      LOGGER.info(
          "ListProductsInProductSetRetryDelayMultiplier set to "
              + this.clientProperties.getListProductsInProductSetRetryDelayMultiplier());
    }
    if (this.clientProperties.getListProductsInProductSetMaxRetryDelay() != null) {
      listProductsInProductSetRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getListProductsInProductSetMaxRetryDelay());
      LOGGER.info(
          "ListProductsInProductSetMaxRetryDelay set to "
              + this.clientProperties.getListProductsInProductSetMaxRetryDelay());
    }
    if (this.clientProperties.getListProductsInProductSetInitialRpcTimeout() != null) {
      listProductsInProductSetRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getListProductsInProductSetInitialRpcTimeout());
      LOGGER.info(
          "ListProductsInProductSetInitialRpcTimeout set to "
              + this.clientProperties.getListProductsInProductSetInitialRpcTimeout());
    }
    if (this.clientProperties.getListProductsInProductSetRpcTimeoutMultiplier() != null) {
      listProductsInProductSetRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getListProductsInProductSetRpcTimeoutMultiplier());
      LOGGER.info(
          "ListProductsInProductSetRpcTimeoutMultiplier set to "
              + this.clientProperties.getListProductsInProductSetRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getListProductsInProductSetMaxRpcTimeout() != null) {
      listProductsInProductSetRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getListProductsInProductSetMaxRpcTimeout());
      LOGGER.info(
          "ListProductsInProductSetMaxRpcTimeout set to "
              + this.clientProperties.getListProductsInProductSetMaxRpcTimeout());
    }
    if (this.clientProperties.getListProductsInProductSetTotalTimeout() != null) {
      listProductsInProductSetRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getListProductsInProductSetTotalTimeout());
      LOGGER.info(
          "ListProductsInProductSetTotalTimeout set to "
              + this.clientProperties.getListProductsInProductSetTotalTimeout());
    }
    clientSettingsBuilder
        .listProductsInProductSetSettings()
        .setRetrySettings(listProductsInProductSetRetrySettingBuilder.build());
    RetrySettings.Builder importProductSetsRetrySettingBuilder =
        clientSettingsBuilder.importProductSetsSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getImportProductSetsInitialRetryDelay() != null) {
      importProductSetsRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getImportProductSetsInitialRetryDelay());
      LOGGER.info(
          "ImportProductSetsInitialRetryDelay set to "
              + this.clientProperties.getImportProductSetsInitialRetryDelay());
    }
    if (this.clientProperties.getImportProductSetsRetryDelayMultiplier() != null) {
      importProductSetsRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getImportProductSetsRetryDelayMultiplier());
      LOGGER.info(
          "ImportProductSetsRetryDelayMultiplier set to "
              + this.clientProperties.getImportProductSetsRetryDelayMultiplier());
    }
    if (this.clientProperties.getImportProductSetsMaxRetryDelay() != null) {
      importProductSetsRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getImportProductSetsMaxRetryDelay());
      LOGGER.info(
          "ImportProductSetsMaxRetryDelay set to "
              + this.clientProperties.getImportProductSetsMaxRetryDelay());
    }
    if (this.clientProperties.getImportProductSetsInitialRpcTimeout() != null) {
      importProductSetsRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getImportProductSetsInitialRpcTimeout());
      LOGGER.info(
          "ImportProductSetsInitialRpcTimeout set to "
              + this.clientProperties.getImportProductSetsInitialRpcTimeout());
    }
    if (this.clientProperties.getImportProductSetsRpcTimeoutMultiplier() != null) {
      importProductSetsRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getImportProductSetsRpcTimeoutMultiplier());
      LOGGER.info(
          "ImportProductSetsRpcTimeoutMultiplier set to "
              + this.clientProperties.getImportProductSetsRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getImportProductSetsMaxRpcTimeout() != null) {
      importProductSetsRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getImportProductSetsMaxRpcTimeout());
      LOGGER.info(
          "ImportProductSetsMaxRpcTimeout set to "
              + this.clientProperties.getImportProductSetsMaxRpcTimeout());
    }
    if (this.clientProperties.getImportProductSetsTotalTimeout() != null) {
      importProductSetsRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getImportProductSetsTotalTimeout());
      LOGGER.info(
          "ImportProductSetsTotalTimeout set to "
              + this.clientProperties.getImportProductSetsTotalTimeout());
    }
    clientSettingsBuilder
        .importProductSetsSettings()
        .setRetrySettings(importProductSetsRetrySettingBuilder.build());
    RetrySettings.Builder purgeProductsRetrySettingBuilder =
        clientSettingsBuilder.purgeProductsSettings().getRetrySettings().toBuilder();
    if (this.clientProperties.getPurgeProductsInitialRetryDelay() != null) {
      purgeProductsRetrySettingBuilder.setInitialRetryDelay(
          this.clientProperties.getPurgeProductsInitialRetryDelay());
      LOGGER.info(
          "PurgeProductsInitialRetryDelay set to "
              + this.clientProperties.getPurgeProductsInitialRetryDelay());
    }
    if (this.clientProperties.getPurgeProductsRetryDelayMultiplier() != null) {
      purgeProductsRetrySettingBuilder.setRetryDelayMultiplier(
          this.clientProperties.getPurgeProductsRetryDelayMultiplier());
      LOGGER.info(
          "PurgeProductsRetryDelayMultiplier set to "
              + this.clientProperties.getPurgeProductsRetryDelayMultiplier());
    }
    if (this.clientProperties.getPurgeProductsMaxRetryDelay() != null) {
      purgeProductsRetrySettingBuilder.setMaxRetryDelay(
          this.clientProperties.getPurgeProductsMaxRetryDelay());
      LOGGER.info(
          "PurgeProductsMaxRetryDelay set to "
              + this.clientProperties.getPurgeProductsMaxRetryDelay());
    }
    if (this.clientProperties.getPurgeProductsInitialRpcTimeout() != null) {
      purgeProductsRetrySettingBuilder.setInitialRpcTimeout(
          this.clientProperties.getPurgeProductsInitialRpcTimeout());
      LOGGER.info(
          "PurgeProductsInitialRpcTimeout set to "
              + this.clientProperties.getPurgeProductsInitialRpcTimeout());
    }
    if (this.clientProperties.getPurgeProductsRpcTimeoutMultiplier() != null) {
      purgeProductsRetrySettingBuilder.setRpcTimeoutMultiplier(
          this.clientProperties.getPurgeProductsRpcTimeoutMultiplier());
      LOGGER.info(
          "PurgeProductsRpcTimeoutMultiplier set to "
              + this.clientProperties.getPurgeProductsRpcTimeoutMultiplier());
    }
    if (this.clientProperties.getPurgeProductsMaxRpcTimeout() != null) {
      purgeProductsRetrySettingBuilder.setMaxRpcTimeout(
          this.clientProperties.getPurgeProductsMaxRpcTimeout());
      LOGGER.info(
          "PurgeProductsMaxRpcTimeout set to "
              + this.clientProperties.getPurgeProductsMaxRpcTimeout());
    }
    if (this.clientProperties.getPurgeProductsTotalTimeout() != null) {
      purgeProductsRetrySettingBuilder.setTotalTimeout(
          this.clientProperties.getPurgeProductsTotalTimeout());
      LOGGER.info(
          "PurgeProductsTotalTimeout set to "
              + this.clientProperties.getPurgeProductsTotalTimeout());
    }
    clientSettingsBuilder
        .purgeProductsSettings()
        .setRetrySettings(purgeProductsRetrySettingBuilder.build());
    return ProductSearchClient.create(clientSettingsBuilder.build());
  }
}
