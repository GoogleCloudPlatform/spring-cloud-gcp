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

import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.threeten.bp.Duration;

@ConfigurationProperties("com.google.cloud.vision.v1.spring.auto.product-search")
public class ProductSearchSpringProperties implements CredentialsSupplier {
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials(
          "https://www.googleapis.com/auth/cloud-platform",
          "https://www.googleapis.com/auth/cloud-vision");

  private String quotaProjectId;
  private Integer executorThreadCount;
  private boolean useRest;
  private Duration createProductSetInitialRetryDelay;
  private Double createProductSetRetryDelayMultiplier;
  private Duration createProductSetMaxRetryDelay;
  private Duration createProductSetInitialRpcTimeout;
  private Double createProductSetRpcTimeoutMultiplier;
  private Duration createProductSetMaxRpcTimeout;
  private Duration createProductSetTotalTimeout;
  private Duration listProductSetsInitialRetryDelay;
  private Double listProductSetsRetryDelayMultiplier;
  private Duration listProductSetsMaxRetryDelay;
  private Duration listProductSetsInitialRpcTimeout;
  private Double listProductSetsRpcTimeoutMultiplier;
  private Duration listProductSetsMaxRpcTimeout;
  private Duration listProductSetsTotalTimeout;
  private Duration getProductSetInitialRetryDelay;
  private Double getProductSetRetryDelayMultiplier;
  private Duration getProductSetMaxRetryDelay;
  private Duration getProductSetInitialRpcTimeout;
  private Double getProductSetRpcTimeoutMultiplier;
  private Duration getProductSetMaxRpcTimeout;
  private Duration getProductSetTotalTimeout;
  private Duration updateProductSetInitialRetryDelay;
  private Double updateProductSetRetryDelayMultiplier;
  private Duration updateProductSetMaxRetryDelay;
  private Duration updateProductSetInitialRpcTimeout;
  private Double updateProductSetRpcTimeoutMultiplier;
  private Duration updateProductSetMaxRpcTimeout;
  private Duration updateProductSetTotalTimeout;
  private Duration deleteProductSetInitialRetryDelay;
  private Double deleteProductSetRetryDelayMultiplier;
  private Duration deleteProductSetMaxRetryDelay;
  private Duration deleteProductSetInitialRpcTimeout;
  private Double deleteProductSetRpcTimeoutMultiplier;
  private Duration deleteProductSetMaxRpcTimeout;
  private Duration deleteProductSetTotalTimeout;
  private Duration createProductInitialRetryDelay;
  private Double createProductRetryDelayMultiplier;
  private Duration createProductMaxRetryDelay;
  private Duration createProductInitialRpcTimeout;
  private Double createProductRpcTimeoutMultiplier;
  private Duration createProductMaxRpcTimeout;
  private Duration createProductTotalTimeout;
  private Duration listProductsInitialRetryDelay;
  private Double listProductsRetryDelayMultiplier;
  private Duration listProductsMaxRetryDelay;
  private Duration listProductsInitialRpcTimeout;
  private Double listProductsRpcTimeoutMultiplier;
  private Duration listProductsMaxRpcTimeout;
  private Duration listProductsTotalTimeout;
  private Duration getProductInitialRetryDelay;
  private Double getProductRetryDelayMultiplier;
  private Duration getProductMaxRetryDelay;
  private Duration getProductInitialRpcTimeout;
  private Double getProductRpcTimeoutMultiplier;
  private Duration getProductMaxRpcTimeout;
  private Duration getProductTotalTimeout;
  private Duration updateProductInitialRetryDelay;
  private Double updateProductRetryDelayMultiplier;
  private Duration updateProductMaxRetryDelay;
  private Duration updateProductInitialRpcTimeout;
  private Double updateProductRpcTimeoutMultiplier;
  private Duration updateProductMaxRpcTimeout;
  private Duration updateProductTotalTimeout;
  private Duration deleteProductInitialRetryDelay;
  private Double deleteProductRetryDelayMultiplier;
  private Duration deleteProductMaxRetryDelay;
  private Duration deleteProductInitialRpcTimeout;
  private Double deleteProductRpcTimeoutMultiplier;
  private Duration deleteProductMaxRpcTimeout;
  private Duration deleteProductTotalTimeout;
  private Duration createReferenceImageInitialRetryDelay;
  private Double createReferenceImageRetryDelayMultiplier;
  private Duration createReferenceImageMaxRetryDelay;
  private Duration createReferenceImageInitialRpcTimeout;
  private Double createReferenceImageRpcTimeoutMultiplier;
  private Duration createReferenceImageMaxRpcTimeout;
  private Duration createReferenceImageTotalTimeout;
  private Duration deleteReferenceImageInitialRetryDelay;
  private Double deleteReferenceImageRetryDelayMultiplier;
  private Duration deleteReferenceImageMaxRetryDelay;
  private Duration deleteReferenceImageInitialRpcTimeout;
  private Double deleteReferenceImageRpcTimeoutMultiplier;
  private Duration deleteReferenceImageMaxRpcTimeout;
  private Duration deleteReferenceImageTotalTimeout;
  private Duration listReferenceImagesInitialRetryDelay;
  private Double listReferenceImagesRetryDelayMultiplier;
  private Duration listReferenceImagesMaxRetryDelay;
  private Duration listReferenceImagesInitialRpcTimeout;
  private Double listReferenceImagesRpcTimeoutMultiplier;
  private Duration listReferenceImagesMaxRpcTimeout;
  private Duration listReferenceImagesTotalTimeout;
  private Duration getReferenceImageInitialRetryDelay;
  private Double getReferenceImageRetryDelayMultiplier;
  private Duration getReferenceImageMaxRetryDelay;
  private Duration getReferenceImageInitialRpcTimeout;
  private Double getReferenceImageRpcTimeoutMultiplier;
  private Duration getReferenceImageMaxRpcTimeout;
  private Duration getReferenceImageTotalTimeout;
  private Duration addProductToProductSetInitialRetryDelay;
  private Double addProductToProductSetRetryDelayMultiplier;
  private Duration addProductToProductSetMaxRetryDelay;
  private Duration addProductToProductSetInitialRpcTimeout;
  private Double addProductToProductSetRpcTimeoutMultiplier;
  private Duration addProductToProductSetMaxRpcTimeout;
  private Duration addProductToProductSetTotalTimeout;
  private Duration removeProductFromProductSetInitialRetryDelay;
  private Double removeProductFromProductSetRetryDelayMultiplier;
  private Duration removeProductFromProductSetMaxRetryDelay;
  private Duration removeProductFromProductSetInitialRpcTimeout;
  private Double removeProductFromProductSetRpcTimeoutMultiplier;
  private Duration removeProductFromProductSetMaxRpcTimeout;
  private Duration removeProductFromProductSetTotalTimeout;
  private Duration listProductsInProductSetInitialRetryDelay;
  private Double listProductsInProductSetRetryDelayMultiplier;
  private Duration listProductsInProductSetMaxRetryDelay;
  private Duration listProductsInProductSetInitialRpcTimeout;
  private Double listProductsInProductSetRpcTimeoutMultiplier;
  private Duration listProductsInProductSetMaxRpcTimeout;
  private Duration listProductsInProductSetTotalTimeout;
  private Duration importProductSetsInitialRetryDelay;
  private Double importProductSetsRetryDelayMultiplier;
  private Duration importProductSetsMaxRetryDelay;
  private Duration importProductSetsInitialRpcTimeout;
  private Double importProductSetsRpcTimeoutMultiplier;
  private Duration importProductSetsMaxRpcTimeout;
  private Duration importProductSetsTotalTimeout;
  private Duration purgeProductsInitialRetryDelay;
  private Double purgeProductsRetryDelayMultiplier;
  private Duration purgeProductsMaxRetryDelay;
  private Duration purgeProductsInitialRpcTimeout;
  private Double purgeProductsRpcTimeoutMultiplier;
  private Duration purgeProductsMaxRpcTimeout;
  private Duration purgeProductsTotalTimeout;

  @Override
  public Credentials getCredentials() {
    return this.credentials;
  }

  public String getQuotaProjectId() {
    return this.quotaProjectId;
  }

  public void setQuotaProjectId(String quotaProjectId) {
    this.quotaProjectId = quotaProjectId;
  }

  public boolean getUseRest() {
    return this.useRest;
  }

  public Integer getExecutorThreadCount() {
    return this.executorThreadCount;
  }

  public void setExecutorThreadCount(Integer executorThreadCount) {
    this.executorThreadCount = executorThreadCount;
  }

  public Duration getCreateProductSetInitialRetryDelay() {
    return this.createProductSetInitialRetryDelay;
  }

  public void setCreateProductSetInitialRetryDelay(Duration createProductSetInitialRetryDelay) {
    this.createProductSetInitialRetryDelay = createProductSetInitialRetryDelay;
  }

  public Double getCreateProductSetRetryDelayMultiplier() {
    return this.createProductSetRetryDelayMultiplier;
  }

  public void setCreateProductSetRetryDelayMultiplier(Double createProductSetRetryDelayMultiplier) {
    this.createProductSetRetryDelayMultiplier = createProductSetRetryDelayMultiplier;
  }

  public Duration getCreateProductSetMaxRetryDelay() {
    return this.createProductSetMaxRetryDelay;
  }

  public void setCreateProductSetMaxRetryDelay(Duration createProductSetMaxRetryDelay) {
    this.createProductSetMaxRetryDelay = createProductSetMaxRetryDelay;
  }

  public Duration getCreateProductSetInitialRpcTimeout() {
    return this.createProductSetInitialRpcTimeout;
  }

  public void setCreateProductSetInitialRpcTimeout(Duration createProductSetInitialRpcTimeout) {
    this.createProductSetInitialRpcTimeout = createProductSetInitialRpcTimeout;
  }

  public Double getCreateProductSetRpcTimeoutMultiplier() {
    return this.createProductSetRpcTimeoutMultiplier;
  }

  public void setCreateProductSetRpcTimeoutMultiplier(Double createProductSetRpcTimeoutMultiplier) {
    this.createProductSetRpcTimeoutMultiplier = createProductSetRpcTimeoutMultiplier;
  }

  public Duration getCreateProductSetMaxRpcTimeout() {
    return this.createProductSetMaxRpcTimeout;
  }

  public void setCreateProductSetMaxRpcTimeout(Duration createProductSetMaxRpcTimeout) {
    this.createProductSetMaxRpcTimeout = createProductSetMaxRpcTimeout;
  }

  public Duration getCreateProductSetTotalTimeout() {
    return this.createProductSetTotalTimeout;
  }

  public void setCreateProductSetTotalTimeout(Duration createProductSetTotalTimeout) {
    this.createProductSetTotalTimeout = createProductSetTotalTimeout;
  }

  public Duration getListProductSetsInitialRetryDelay() {
    return this.listProductSetsInitialRetryDelay;
  }

  public void setListProductSetsInitialRetryDelay(Duration listProductSetsInitialRetryDelay) {
    this.listProductSetsInitialRetryDelay = listProductSetsInitialRetryDelay;
  }

  public Double getListProductSetsRetryDelayMultiplier() {
    return this.listProductSetsRetryDelayMultiplier;
  }

  public void setListProductSetsRetryDelayMultiplier(Double listProductSetsRetryDelayMultiplier) {
    this.listProductSetsRetryDelayMultiplier = listProductSetsRetryDelayMultiplier;
  }

  public Duration getListProductSetsMaxRetryDelay() {
    return this.listProductSetsMaxRetryDelay;
  }

  public void setListProductSetsMaxRetryDelay(Duration listProductSetsMaxRetryDelay) {
    this.listProductSetsMaxRetryDelay = listProductSetsMaxRetryDelay;
  }

  public Duration getListProductSetsInitialRpcTimeout() {
    return this.listProductSetsInitialRpcTimeout;
  }

  public void setListProductSetsInitialRpcTimeout(Duration listProductSetsInitialRpcTimeout) {
    this.listProductSetsInitialRpcTimeout = listProductSetsInitialRpcTimeout;
  }

  public Double getListProductSetsRpcTimeoutMultiplier() {
    return this.listProductSetsRpcTimeoutMultiplier;
  }

  public void setListProductSetsRpcTimeoutMultiplier(Double listProductSetsRpcTimeoutMultiplier) {
    this.listProductSetsRpcTimeoutMultiplier = listProductSetsRpcTimeoutMultiplier;
  }

  public Duration getListProductSetsMaxRpcTimeout() {
    return this.listProductSetsMaxRpcTimeout;
  }

  public void setListProductSetsMaxRpcTimeout(Duration listProductSetsMaxRpcTimeout) {
    this.listProductSetsMaxRpcTimeout = listProductSetsMaxRpcTimeout;
  }

  public Duration getListProductSetsTotalTimeout() {
    return this.listProductSetsTotalTimeout;
  }

  public void setListProductSetsTotalTimeout(Duration listProductSetsTotalTimeout) {
    this.listProductSetsTotalTimeout = listProductSetsTotalTimeout;
  }

  public Duration getGetProductSetInitialRetryDelay() {
    return this.getProductSetInitialRetryDelay;
  }

  public void setGetProductSetInitialRetryDelay(Duration getProductSetInitialRetryDelay) {
    this.getProductSetInitialRetryDelay = getProductSetInitialRetryDelay;
  }

  public Double getGetProductSetRetryDelayMultiplier() {
    return this.getProductSetRetryDelayMultiplier;
  }

  public void setGetProductSetRetryDelayMultiplier(Double getProductSetRetryDelayMultiplier) {
    this.getProductSetRetryDelayMultiplier = getProductSetRetryDelayMultiplier;
  }

  public Duration getGetProductSetMaxRetryDelay() {
    return this.getProductSetMaxRetryDelay;
  }

  public void setGetProductSetMaxRetryDelay(Duration getProductSetMaxRetryDelay) {
    this.getProductSetMaxRetryDelay = getProductSetMaxRetryDelay;
  }

  public Duration getGetProductSetInitialRpcTimeout() {
    return this.getProductSetInitialRpcTimeout;
  }

  public void setGetProductSetInitialRpcTimeout(Duration getProductSetInitialRpcTimeout) {
    this.getProductSetInitialRpcTimeout = getProductSetInitialRpcTimeout;
  }

  public Double getGetProductSetRpcTimeoutMultiplier() {
    return this.getProductSetRpcTimeoutMultiplier;
  }

  public void setGetProductSetRpcTimeoutMultiplier(Double getProductSetRpcTimeoutMultiplier) {
    this.getProductSetRpcTimeoutMultiplier = getProductSetRpcTimeoutMultiplier;
  }

  public Duration getGetProductSetMaxRpcTimeout() {
    return this.getProductSetMaxRpcTimeout;
  }

  public void setGetProductSetMaxRpcTimeout(Duration getProductSetMaxRpcTimeout) {
    this.getProductSetMaxRpcTimeout = getProductSetMaxRpcTimeout;
  }

  public Duration getGetProductSetTotalTimeout() {
    return this.getProductSetTotalTimeout;
  }

  public void setGetProductSetTotalTimeout(Duration getProductSetTotalTimeout) {
    this.getProductSetTotalTimeout = getProductSetTotalTimeout;
  }

  public Duration getUpdateProductSetInitialRetryDelay() {
    return this.updateProductSetInitialRetryDelay;
  }

  public void setUpdateProductSetInitialRetryDelay(Duration updateProductSetInitialRetryDelay) {
    this.updateProductSetInitialRetryDelay = updateProductSetInitialRetryDelay;
  }

  public Double getUpdateProductSetRetryDelayMultiplier() {
    return this.updateProductSetRetryDelayMultiplier;
  }

  public void setUpdateProductSetRetryDelayMultiplier(Double updateProductSetRetryDelayMultiplier) {
    this.updateProductSetRetryDelayMultiplier = updateProductSetRetryDelayMultiplier;
  }

  public Duration getUpdateProductSetMaxRetryDelay() {
    return this.updateProductSetMaxRetryDelay;
  }

  public void setUpdateProductSetMaxRetryDelay(Duration updateProductSetMaxRetryDelay) {
    this.updateProductSetMaxRetryDelay = updateProductSetMaxRetryDelay;
  }

  public Duration getUpdateProductSetInitialRpcTimeout() {
    return this.updateProductSetInitialRpcTimeout;
  }

  public void setUpdateProductSetInitialRpcTimeout(Duration updateProductSetInitialRpcTimeout) {
    this.updateProductSetInitialRpcTimeout = updateProductSetInitialRpcTimeout;
  }

  public Double getUpdateProductSetRpcTimeoutMultiplier() {
    return this.updateProductSetRpcTimeoutMultiplier;
  }

  public void setUpdateProductSetRpcTimeoutMultiplier(Double updateProductSetRpcTimeoutMultiplier) {
    this.updateProductSetRpcTimeoutMultiplier = updateProductSetRpcTimeoutMultiplier;
  }

  public Duration getUpdateProductSetMaxRpcTimeout() {
    return this.updateProductSetMaxRpcTimeout;
  }

  public void setUpdateProductSetMaxRpcTimeout(Duration updateProductSetMaxRpcTimeout) {
    this.updateProductSetMaxRpcTimeout = updateProductSetMaxRpcTimeout;
  }

  public Duration getUpdateProductSetTotalTimeout() {
    return this.updateProductSetTotalTimeout;
  }

  public void setUpdateProductSetTotalTimeout(Duration updateProductSetTotalTimeout) {
    this.updateProductSetTotalTimeout = updateProductSetTotalTimeout;
  }

  public Duration getDeleteProductSetInitialRetryDelay() {
    return this.deleteProductSetInitialRetryDelay;
  }

  public void setDeleteProductSetInitialRetryDelay(Duration deleteProductSetInitialRetryDelay) {
    this.deleteProductSetInitialRetryDelay = deleteProductSetInitialRetryDelay;
  }

  public Double getDeleteProductSetRetryDelayMultiplier() {
    return this.deleteProductSetRetryDelayMultiplier;
  }

  public void setDeleteProductSetRetryDelayMultiplier(Double deleteProductSetRetryDelayMultiplier) {
    this.deleteProductSetRetryDelayMultiplier = deleteProductSetRetryDelayMultiplier;
  }

  public Duration getDeleteProductSetMaxRetryDelay() {
    return this.deleteProductSetMaxRetryDelay;
  }

  public void setDeleteProductSetMaxRetryDelay(Duration deleteProductSetMaxRetryDelay) {
    this.deleteProductSetMaxRetryDelay = deleteProductSetMaxRetryDelay;
  }

  public Duration getDeleteProductSetInitialRpcTimeout() {
    return this.deleteProductSetInitialRpcTimeout;
  }

  public void setDeleteProductSetInitialRpcTimeout(Duration deleteProductSetInitialRpcTimeout) {
    this.deleteProductSetInitialRpcTimeout = deleteProductSetInitialRpcTimeout;
  }

  public Double getDeleteProductSetRpcTimeoutMultiplier() {
    return this.deleteProductSetRpcTimeoutMultiplier;
  }

  public void setDeleteProductSetRpcTimeoutMultiplier(Double deleteProductSetRpcTimeoutMultiplier) {
    this.deleteProductSetRpcTimeoutMultiplier = deleteProductSetRpcTimeoutMultiplier;
  }

  public Duration getDeleteProductSetMaxRpcTimeout() {
    return this.deleteProductSetMaxRpcTimeout;
  }

  public void setDeleteProductSetMaxRpcTimeout(Duration deleteProductSetMaxRpcTimeout) {
    this.deleteProductSetMaxRpcTimeout = deleteProductSetMaxRpcTimeout;
  }

  public Duration getDeleteProductSetTotalTimeout() {
    return this.deleteProductSetTotalTimeout;
  }

  public void setDeleteProductSetTotalTimeout(Duration deleteProductSetTotalTimeout) {
    this.deleteProductSetTotalTimeout = deleteProductSetTotalTimeout;
  }

  public Duration getCreateProductInitialRetryDelay() {
    return this.createProductInitialRetryDelay;
  }

  public void setCreateProductInitialRetryDelay(Duration createProductInitialRetryDelay) {
    this.createProductInitialRetryDelay = createProductInitialRetryDelay;
  }

  public Double getCreateProductRetryDelayMultiplier() {
    return this.createProductRetryDelayMultiplier;
  }

  public void setCreateProductRetryDelayMultiplier(Double createProductRetryDelayMultiplier) {
    this.createProductRetryDelayMultiplier = createProductRetryDelayMultiplier;
  }

  public Duration getCreateProductMaxRetryDelay() {
    return this.createProductMaxRetryDelay;
  }

  public void setCreateProductMaxRetryDelay(Duration createProductMaxRetryDelay) {
    this.createProductMaxRetryDelay = createProductMaxRetryDelay;
  }

  public Duration getCreateProductInitialRpcTimeout() {
    return this.createProductInitialRpcTimeout;
  }

  public void setCreateProductInitialRpcTimeout(Duration createProductInitialRpcTimeout) {
    this.createProductInitialRpcTimeout = createProductInitialRpcTimeout;
  }

  public Double getCreateProductRpcTimeoutMultiplier() {
    return this.createProductRpcTimeoutMultiplier;
  }

  public void setCreateProductRpcTimeoutMultiplier(Double createProductRpcTimeoutMultiplier) {
    this.createProductRpcTimeoutMultiplier = createProductRpcTimeoutMultiplier;
  }

  public Duration getCreateProductMaxRpcTimeout() {
    return this.createProductMaxRpcTimeout;
  }

  public void setCreateProductMaxRpcTimeout(Duration createProductMaxRpcTimeout) {
    this.createProductMaxRpcTimeout = createProductMaxRpcTimeout;
  }

  public Duration getCreateProductTotalTimeout() {
    return this.createProductTotalTimeout;
  }

  public void setCreateProductTotalTimeout(Duration createProductTotalTimeout) {
    this.createProductTotalTimeout = createProductTotalTimeout;
  }

  public Duration getListProductsInitialRetryDelay() {
    return this.listProductsInitialRetryDelay;
  }

  public void setListProductsInitialRetryDelay(Duration listProductsInitialRetryDelay) {
    this.listProductsInitialRetryDelay = listProductsInitialRetryDelay;
  }

  public Double getListProductsRetryDelayMultiplier() {
    return this.listProductsRetryDelayMultiplier;
  }

  public void setListProductsRetryDelayMultiplier(Double listProductsRetryDelayMultiplier) {
    this.listProductsRetryDelayMultiplier = listProductsRetryDelayMultiplier;
  }

  public Duration getListProductsMaxRetryDelay() {
    return this.listProductsMaxRetryDelay;
  }

  public void setListProductsMaxRetryDelay(Duration listProductsMaxRetryDelay) {
    this.listProductsMaxRetryDelay = listProductsMaxRetryDelay;
  }

  public Duration getListProductsInitialRpcTimeout() {
    return this.listProductsInitialRpcTimeout;
  }

  public void setListProductsInitialRpcTimeout(Duration listProductsInitialRpcTimeout) {
    this.listProductsInitialRpcTimeout = listProductsInitialRpcTimeout;
  }

  public Double getListProductsRpcTimeoutMultiplier() {
    return this.listProductsRpcTimeoutMultiplier;
  }

  public void setListProductsRpcTimeoutMultiplier(Double listProductsRpcTimeoutMultiplier) {
    this.listProductsRpcTimeoutMultiplier = listProductsRpcTimeoutMultiplier;
  }

  public Duration getListProductsMaxRpcTimeout() {
    return this.listProductsMaxRpcTimeout;
  }

  public void setListProductsMaxRpcTimeout(Duration listProductsMaxRpcTimeout) {
    this.listProductsMaxRpcTimeout = listProductsMaxRpcTimeout;
  }

  public Duration getListProductsTotalTimeout() {
    return this.listProductsTotalTimeout;
  }

  public void setListProductsTotalTimeout(Duration listProductsTotalTimeout) {
    this.listProductsTotalTimeout = listProductsTotalTimeout;
  }

  public Duration getGetProductInitialRetryDelay() {
    return this.getProductInitialRetryDelay;
  }

  public void setGetProductInitialRetryDelay(Duration getProductInitialRetryDelay) {
    this.getProductInitialRetryDelay = getProductInitialRetryDelay;
  }

  public Double getGetProductRetryDelayMultiplier() {
    return this.getProductRetryDelayMultiplier;
  }

  public void setGetProductRetryDelayMultiplier(Double getProductRetryDelayMultiplier) {
    this.getProductRetryDelayMultiplier = getProductRetryDelayMultiplier;
  }

  public Duration getGetProductMaxRetryDelay() {
    return this.getProductMaxRetryDelay;
  }

  public void setGetProductMaxRetryDelay(Duration getProductMaxRetryDelay) {
    this.getProductMaxRetryDelay = getProductMaxRetryDelay;
  }

  public Duration getGetProductInitialRpcTimeout() {
    return this.getProductInitialRpcTimeout;
  }

  public void setGetProductInitialRpcTimeout(Duration getProductInitialRpcTimeout) {
    this.getProductInitialRpcTimeout = getProductInitialRpcTimeout;
  }

  public Double getGetProductRpcTimeoutMultiplier() {
    return this.getProductRpcTimeoutMultiplier;
  }

  public void setGetProductRpcTimeoutMultiplier(Double getProductRpcTimeoutMultiplier) {
    this.getProductRpcTimeoutMultiplier = getProductRpcTimeoutMultiplier;
  }

  public Duration getGetProductMaxRpcTimeout() {
    return this.getProductMaxRpcTimeout;
  }

  public void setGetProductMaxRpcTimeout(Duration getProductMaxRpcTimeout) {
    this.getProductMaxRpcTimeout = getProductMaxRpcTimeout;
  }

  public Duration getGetProductTotalTimeout() {
    return this.getProductTotalTimeout;
  }

  public void setGetProductTotalTimeout(Duration getProductTotalTimeout) {
    this.getProductTotalTimeout = getProductTotalTimeout;
  }

  public Duration getUpdateProductInitialRetryDelay() {
    return this.updateProductInitialRetryDelay;
  }

  public void setUpdateProductInitialRetryDelay(Duration updateProductInitialRetryDelay) {
    this.updateProductInitialRetryDelay = updateProductInitialRetryDelay;
  }

  public Double getUpdateProductRetryDelayMultiplier() {
    return this.updateProductRetryDelayMultiplier;
  }

  public void setUpdateProductRetryDelayMultiplier(Double updateProductRetryDelayMultiplier) {
    this.updateProductRetryDelayMultiplier = updateProductRetryDelayMultiplier;
  }

  public Duration getUpdateProductMaxRetryDelay() {
    return this.updateProductMaxRetryDelay;
  }

  public void setUpdateProductMaxRetryDelay(Duration updateProductMaxRetryDelay) {
    this.updateProductMaxRetryDelay = updateProductMaxRetryDelay;
  }

  public Duration getUpdateProductInitialRpcTimeout() {
    return this.updateProductInitialRpcTimeout;
  }

  public void setUpdateProductInitialRpcTimeout(Duration updateProductInitialRpcTimeout) {
    this.updateProductInitialRpcTimeout = updateProductInitialRpcTimeout;
  }

  public Double getUpdateProductRpcTimeoutMultiplier() {
    return this.updateProductRpcTimeoutMultiplier;
  }

  public void setUpdateProductRpcTimeoutMultiplier(Double updateProductRpcTimeoutMultiplier) {
    this.updateProductRpcTimeoutMultiplier = updateProductRpcTimeoutMultiplier;
  }

  public Duration getUpdateProductMaxRpcTimeout() {
    return this.updateProductMaxRpcTimeout;
  }

  public void setUpdateProductMaxRpcTimeout(Duration updateProductMaxRpcTimeout) {
    this.updateProductMaxRpcTimeout = updateProductMaxRpcTimeout;
  }

  public Duration getUpdateProductTotalTimeout() {
    return this.updateProductTotalTimeout;
  }

  public void setUpdateProductTotalTimeout(Duration updateProductTotalTimeout) {
    this.updateProductTotalTimeout = updateProductTotalTimeout;
  }

  public Duration getDeleteProductInitialRetryDelay() {
    return this.deleteProductInitialRetryDelay;
  }

  public void setDeleteProductInitialRetryDelay(Duration deleteProductInitialRetryDelay) {
    this.deleteProductInitialRetryDelay = deleteProductInitialRetryDelay;
  }

  public Double getDeleteProductRetryDelayMultiplier() {
    return this.deleteProductRetryDelayMultiplier;
  }

  public void setDeleteProductRetryDelayMultiplier(Double deleteProductRetryDelayMultiplier) {
    this.deleteProductRetryDelayMultiplier = deleteProductRetryDelayMultiplier;
  }

  public Duration getDeleteProductMaxRetryDelay() {
    return this.deleteProductMaxRetryDelay;
  }

  public void setDeleteProductMaxRetryDelay(Duration deleteProductMaxRetryDelay) {
    this.deleteProductMaxRetryDelay = deleteProductMaxRetryDelay;
  }

  public Duration getDeleteProductInitialRpcTimeout() {
    return this.deleteProductInitialRpcTimeout;
  }

  public void setDeleteProductInitialRpcTimeout(Duration deleteProductInitialRpcTimeout) {
    this.deleteProductInitialRpcTimeout = deleteProductInitialRpcTimeout;
  }

  public Double getDeleteProductRpcTimeoutMultiplier() {
    return this.deleteProductRpcTimeoutMultiplier;
  }

  public void setDeleteProductRpcTimeoutMultiplier(Double deleteProductRpcTimeoutMultiplier) {
    this.deleteProductRpcTimeoutMultiplier = deleteProductRpcTimeoutMultiplier;
  }

  public Duration getDeleteProductMaxRpcTimeout() {
    return this.deleteProductMaxRpcTimeout;
  }

  public void setDeleteProductMaxRpcTimeout(Duration deleteProductMaxRpcTimeout) {
    this.deleteProductMaxRpcTimeout = deleteProductMaxRpcTimeout;
  }

  public Duration getDeleteProductTotalTimeout() {
    return this.deleteProductTotalTimeout;
  }

  public void setDeleteProductTotalTimeout(Duration deleteProductTotalTimeout) {
    this.deleteProductTotalTimeout = deleteProductTotalTimeout;
  }

  public Duration getCreateReferenceImageInitialRetryDelay() {
    return this.createReferenceImageInitialRetryDelay;
  }

  public void setCreateReferenceImageInitialRetryDelay(
      Duration createReferenceImageInitialRetryDelay) {
    this.createReferenceImageInitialRetryDelay = createReferenceImageInitialRetryDelay;
  }

  public Double getCreateReferenceImageRetryDelayMultiplier() {
    return this.createReferenceImageRetryDelayMultiplier;
  }

  public void setCreateReferenceImageRetryDelayMultiplier(
      Double createReferenceImageRetryDelayMultiplier) {
    this.createReferenceImageRetryDelayMultiplier = createReferenceImageRetryDelayMultiplier;
  }

  public Duration getCreateReferenceImageMaxRetryDelay() {
    return this.createReferenceImageMaxRetryDelay;
  }

  public void setCreateReferenceImageMaxRetryDelay(Duration createReferenceImageMaxRetryDelay) {
    this.createReferenceImageMaxRetryDelay = createReferenceImageMaxRetryDelay;
  }

  public Duration getCreateReferenceImageInitialRpcTimeout() {
    return this.createReferenceImageInitialRpcTimeout;
  }

  public void setCreateReferenceImageInitialRpcTimeout(
      Duration createReferenceImageInitialRpcTimeout) {
    this.createReferenceImageInitialRpcTimeout = createReferenceImageInitialRpcTimeout;
  }

  public Double getCreateReferenceImageRpcTimeoutMultiplier() {
    return this.createReferenceImageRpcTimeoutMultiplier;
  }

  public void setCreateReferenceImageRpcTimeoutMultiplier(
      Double createReferenceImageRpcTimeoutMultiplier) {
    this.createReferenceImageRpcTimeoutMultiplier = createReferenceImageRpcTimeoutMultiplier;
  }

  public Duration getCreateReferenceImageMaxRpcTimeout() {
    return this.createReferenceImageMaxRpcTimeout;
  }

  public void setCreateReferenceImageMaxRpcTimeout(Duration createReferenceImageMaxRpcTimeout) {
    this.createReferenceImageMaxRpcTimeout = createReferenceImageMaxRpcTimeout;
  }

  public Duration getCreateReferenceImageTotalTimeout() {
    return this.createReferenceImageTotalTimeout;
  }

  public void setCreateReferenceImageTotalTimeout(Duration createReferenceImageTotalTimeout) {
    this.createReferenceImageTotalTimeout = createReferenceImageTotalTimeout;
  }

  public Duration getDeleteReferenceImageInitialRetryDelay() {
    return this.deleteReferenceImageInitialRetryDelay;
  }

  public void setDeleteReferenceImageInitialRetryDelay(
      Duration deleteReferenceImageInitialRetryDelay) {
    this.deleteReferenceImageInitialRetryDelay = deleteReferenceImageInitialRetryDelay;
  }

  public Double getDeleteReferenceImageRetryDelayMultiplier() {
    return this.deleteReferenceImageRetryDelayMultiplier;
  }

  public void setDeleteReferenceImageRetryDelayMultiplier(
      Double deleteReferenceImageRetryDelayMultiplier) {
    this.deleteReferenceImageRetryDelayMultiplier = deleteReferenceImageRetryDelayMultiplier;
  }

  public Duration getDeleteReferenceImageMaxRetryDelay() {
    return this.deleteReferenceImageMaxRetryDelay;
  }

  public void setDeleteReferenceImageMaxRetryDelay(Duration deleteReferenceImageMaxRetryDelay) {
    this.deleteReferenceImageMaxRetryDelay = deleteReferenceImageMaxRetryDelay;
  }

  public Duration getDeleteReferenceImageInitialRpcTimeout() {
    return this.deleteReferenceImageInitialRpcTimeout;
  }

  public void setDeleteReferenceImageInitialRpcTimeout(
      Duration deleteReferenceImageInitialRpcTimeout) {
    this.deleteReferenceImageInitialRpcTimeout = deleteReferenceImageInitialRpcTimeout;
  }

  public Double getDeleteReferenceImageRpcTimeoutMultiplier() {
    return this.deleteReferenceImageRpcTimeoutMultiplier;
  }

  public void setDeleteReferenceImageRpcTimeoutMultiplier(
      Double deleteReferenceImageRpcTimeoutMultiplier) {
    this.deleteReferenceImageRpcTimeoutMultiplier = deleteReferenceImageRpcTimeoutMultiplier;
  }

  public Duration getDeleteReferenceImageMaxRpcTimeout() {
    return this.deleteReferenceImageMaxRpcTimeout;
  }

  public void setDeleteReferenceImageMaxRpcTimeout(Duration deleteReferenceImageMaxRpcTimeout) {
    this.deleteReferenceImageMaxRpcTimeout = deleteReferenceImageMaxRpcTimeout;
  }

  public Duration getDeleteReferenceImageTotalTimeout() {
    return this.deleteReferenceImageTotalTimeout;
  }

  public void setDeleteReferenceImageTotalTimeout(Duration deleteReferenceImageTotalTimeout) {
    this.deleteReferenceImageTotalTimeout = deleteReferenceImageTotalTimeout;
  }

  public Duration getListReferenceImagesInitialRetryDelay() {
    return this.listReferenceImagesInitialRetryDelay;
  }

  public void setListReferenceImagesInitialRetryDelay(
      Duration listReferenceImagesInitialRetryDelay) {
    this.listReferenceImagesInitialRetryDelay = listReferenceImagesInitialRetryDelay;
  }

  public Double getListReferenceImagesRetryDelayMultiplier() {
    return this.listReferenceImagesRetryDelayMultiplier;
  }

  public void setListReferenceImagesRetryDelayMultiplier(
      Double listReferenceImagesRetryDelayMultiplier) {
    this.listReferenceImagesRetryDelayMultiplier = listReferenceImagesRetryDelayMultiplier;
  }

  public Duration getListReferenceImagesMaxRetryDelay() {
    return this.listReferenceImagesMaxRetryDelay;
  }

  public void setListReferenceImagesMaxRetryDelay(Duration listReferenceImagesMaxRetryDelay) {
    this.listReferenceImagesMaxRetryDelay = listReferenceImagesMaxRetryDelay;
  }

  public Duration getListReferenceImagesInitialRpcTimeout() {
    return this.listReferenceImagesInitialRpcTimeout;
  }

  public void setListReferenceImagesInitialRpcTimeout(
      Duration listReferenceImagesInitialRpcTimeout) {
    this.listReferenceImagesInitialRpcTimeout = listReferenceImagesInitialRpcTimeout;
  }

  public Double getListReferenceImagesRpcTimeoutMultiplier() {
    return this.listReferenceImagesRpcTimeoutMultiplier;
  }

  public void setListReferenceImagesRpcTimeoutMultiplier(
      Double listReferenceImagesRpcTimeoutMultiplier) {
    this.listReferenceImagesRpcTimeoutMultiplier = listReferenceImagesRpcTimeoutMultiplier;
  }

  public Duration getListReferenceImagesMaxRpcTimeout() {
    return this.listReferenceImagesMaxRpcTimeout;
  }

  public void setListReferenceImagesMaxRpcTimeout(Duration listReferenceImagesMaxRpcTimeout) {
    this.listReferenceImagesMaxRpcTimeout = listReferenceImagesMaxRpcTimeout;
  }

  public Duration getListReferenceImagesTotalTimeout() {
    return this.listReferenceImagesTotalTimeout;
  }

  public void setListReferenceImagesTotalTimeout(Duration listReferenceImagesTotalTimeout) {
    this.listReferenceImagesTotalTimeout = listReferenceImagesTotalTimeout;
  }

  public Duration getGetReferenceImageInitialRetryDelay() {
    return this.getReferenceImageInitialRetryDelay;
  }

  public void setGetReferenceImageInitialRetryDelay(Duration getReferenceImageInitialRetryDelay) {
    this.getReferenceImageInitialRetryDelay = getReferenceImageInitialRetryDelay;
  }

  public Double getGetReferenceImageRetryDelayMultiplier() {
    return this.getReferenceImageRetryDelayMultiplier;
  }

  public void setGetReferenceImageRetryDelayMultiplier(
      Double getReferenceImageRetryDelayMultiplier) {
    this.getReferenceImageRetryDelayMultiplier = getReferenceImageRetryDelayMultiplier;
  }

  public Duration getGetReferenceImageMaxRetryDelay() {
    return this.getReferenceImageMaxRetryDelay;
  }

  public void setGetReferenceImageMaxRetryDelay(Duration getReferenceImageMaxRetryDelay) {
    this.getReferenceImageMaxRetryDelay = getReferenceImageMaxRetryDelay;
  }

  public Duration getGetReferenceImageInitialRpcTimeout() {
    return this.getReferenceImageInitialRpcTimeout;
  }

  public void setGetReferenceImageInitialRpcTimeout(Duration getReferenceImageInitialRpcTimeout) {
    this.getReferenceImageInitialRpcTimeout = getReferenceImageInitialRpcTimeout;
  }

  public Double getGetReferenceImageRpcTimeoutMultiplier() {
    return this.getReferenceImageRpcTimeoutMultiplier;
  }

  public void setGetReferenceImageRpcTimeoutMultiplier(
      Double getReferenceImageRpcTimeoutMultiplier) {
    this.getReferenceImageRpcTimeoutMultiplier = getReferenceImageRpcTimeoutMultiplier;
  }

  public Duration getGetReferenceImageMaxRpcTimeout() {
    return this.getReferenceImageMaxRpcTimeout;
  }

  public void setGetReferenceImageMaxRpcTimeout(Duration getReferenceImageMaxRpcTimeout) {
    this.getReferenceImageMaxRpcTimeout = getReferenceImageMaxRpcTimeout;
  }

  public Duration getGetReferenceImageTotalTimeout() {
    return this.getReferenceImageTotalTimeout;
  }

  public void setGetReferenceImageTotalTimeout(Duration getReferenceImageTotalTimeout) {
    this.getReferenceImageTotalTimeout = getReferenceImageTotalTimeout;
  }

  public Duration getAddProductToProductSetInitialRetryDelay() {
    return this.addProductToProductSetInitialRetryDelay;
  }

  public void setAddProductToProductSetInitialRetryDelay(
      Duration addProductToProductSetInitialRetryDelay) {
    this.addProductToProductSetInitialRetryDelay = addProductToProductSetInitialRetryDelay;
  }

  public Double getAddProductToProductSetRetryDelayMultiplier() {
    return this.addProductToProductSetRetryDelayMultiplier;
  }

  public void setAddProductToProductSetRetryDelayMultiplier(
      Double addProductToProductSetRetryDelayMultiplier) {
    this.addProductToProductSetRetryDelayMultiplier = addProductToProductSetRetryDelayMultiplier;
  }

  public Duration getAddProductToProductSetMaxRetryDelay() {
    return this.addProductToProductSetMaxRetryDelay;
  }

  public void setAddProductToProductSetMaxRetryDelay(Duration addProductToProductSetMaxRetryDelay) {
    this.addProductToProductSetMaxRetryDelay = addProductToProductSetMaxRetryDelay;
  }

  public Duration getAddProductToProductSetInitialRpcTimeout() {
    return this.addProductToProductSetInitialRpcTimeout;
  }

  public void setAddProductToProductSetInitialRpcTimeout(
      Duration addProductToProductSetInitialRpcTimeout) {
    this.addProductToProductSetInitialRpcTimeout = addProductToProductSetInitialRpcTimeout;
  }

  public Double getAddProductToProductSetRpcTimeoutMultiplier() {
    return this.addProductToProductSetRpcTimeoutMultiplier;
  }

  public void setAddProductToProductSetRpcTimeoutMultiplier(
      Double addProductToProductSetRpcTimeoutMultiplier) {
    this.addProductToProductSetRpcTimeoutMultiplier = addProductToProductSetRpcTimeoutMultiplier;
  }

  public Duration getAddProductToProductSetMaxRpcTimeout() {
    return this.addProductToProductSetMaxRpcTimeout;
  }

  public void setAddProductToProductSetMaxRpcTimeout(Duration addProductToProductSetMaxRpcTimeout) {
    this.addProductToProductSetMaxRpcTimeout = addProductToProductSetMaxRpcTimeout;
  }

  public Duration getAddProductToProductSetTotalTimeout() {
    return this.addProductToProductSetTotalTimeout;
  }

  public void setAddProductToProductSetTotalTimeout(Duration addProductToProductSetTotalTimeout) {
    this.addProductToProductSetTotalTimeout = addProductToProductSetTotalTimeout;
  }

  public Duration getRemoveProductFromProductSetInitialRetryDelay() {
    return this.removeProductFromProductSetInitialRetryDelay;
  }

  public void setRemoveProductFromProductSetInitialRetryDelay(
      Duration removeProductFromProductSetInitialRetryDelay) {
    this.removeProductFromProductSetInitialRetryDelay =
        removeProductFromProductSetInitialRetryDelay;
  }

  public Double getRemoveProductFromProductSetRetryDelayMultiplier() {
    return this.removeProductFromProductSetRetryDelayMultiplier;
  }

  public void setRemoveProductFromProductSetRetryDelayMultiplier(
      Double removeProductFromProductSetRetryDelayMultiplier) {
    this.removeProductFromProductSetRetryDelayMultiplier =
        removeProductFromProductSetRetryDelayMultiplier;
  }

  public Duration getRemoveProductFromProductSetMaxRetryDelay() {
    return this.removeProductFromProductSetMaxRetryDelay;
  }

  public void setRemoveProductFromProductSetMaxRetryDelay(
      Duration removeProductFromProductSetMaxRetryDelay) {
    this.removeProductFromProductSetMaxRetryDelay = removeProductFromProductSetMaxRetryDelay;
  }

  public Duration getRemoveProductFromProductSetInitialRpcTimeout() {
    return this.removeProductFromProductSetInitialRpcTimeout;
  }

  public void setRemoveProductFromProductSetInitialRpcTimeout(
      Duration removeProductFromProductSetInitialRpcTimeout) {
    this.removeProductFromProductSetInitialRpcTimeout =
        removeProductFromProductSetInitialRpcTimeout;
  }

  public Double getRemoveProductFromProductSetRpcTimeoutMultiplier() {
    return this.removeProductFromProductSetRpcTimeoutMultiplier;
  }

  public void setRemoveProductFromProductSetRpcTimeoutMultiplier(
      Double removeProductFromProductSetRpcTimeoutMultiplier) {
    this.removeProductFromProductSetRpcTimeoutMultiplier =
        removeProductFromProductSetRpcTimeoutMultiplier;
  }

  public Duration getRemoveProductFromProductSetMaxRpcTimeout() {
    return this.removeProductFromProductSetMaxRpcTimeout;
  }

  public void setRemoveProductFromProductSetMaxRpcTimeout(
      Duration removeProductFromProductSetMaxRpcTimeout) {
    this.removeProductFromProductSetMaxRpcTimeout = removeProductFromProductSetMaxRpcTimeout;
  }

  public Duration getRemoveProductFromProductSetTotalTimeout() {
    return this.removeProductFromProductSetTotalTimeout;
  }

  public void setRemoveProductFromProductSetTotalTimeout(
      Duration removeProductFromProductSetTotalTimeout) {
    this.removeProductFromProductSetTotalTimeout = removeProductFromProductSetTotalTimeout;
  }

  public Duration getListProductsInProductSetInitialRetryDelay() {
    return this.listProductsInProductSetInitialRetryDelay;
  }

  public void setListProductsInProductSetInitialRetryDelay(
      Duration listProductsInProductSetInitialRetryDelay) {
    this.listProductsInProductSetInitialRetryDelay = listProductsInProductSetInitialRetryDelay;
  }

  public Double getListProductsInProductSetRetryDelayMultiplier() {
    return this.listProductsInProductSetRetryDelayMultiplier;
  }

  public void setListProductsInProductSetRetryDelayMultiplier(
      Double listProductsInProductSetRetryDelayMultiplier) {
    this.listProductsInProductSetRetryDelayMultiplier =
        listProductsInProductSetRetryDelayMultiplier;
  }

  public Duration getListProductsInProductSetMaxRetryDelay() {
    return this.listProductsInProductSetMaxRetryDelay;
  }

  public void setListProductsInProductSetMaxRetryDelay(
      Duration listProductsInProductSetMaxRetryDelay) {
    this.listProductsInProductSetMaxRetryDelay = listProductsInProductSetMaxRetryDelay;
  }

  public Duration getListProductsInProductSetInitialRpcTimeout() {
    return this.listProductsInProductSetInitialRpcTimeout;
  }

  public void setListProductsInProductSetInitialRpcTimeout(
      Duration listProductsInProductSetInitialRpcTimeout) {
    this.listProductsInProductSetInitialRpcTimeout = listProductsInProductSetInitialRpcTimeout;
  }

  public Double getListProductsInProductSetRpcTimeoutMultiplier() {
    return this.listProductsInProductSetRpcTimeoutMultiplier;
  }

  public void setListProductsInProductSetRpcTimeoutMultiplier(
      Double listProductsInProductSetRpcTimeoutMultiplier) {
    this.listProductsInProductSetRpcTimeoutMultiplier =
        listProductsInProductSetRpcTimeoutMultiplier;
  }

  public Duration getListProductsInProductSetMaxRpcTimeout() {
    return this.listProductsInProductSetMaxRpcTimeout;
  }

  public void setListProductsInProductSetMaxRpcTimeout(
      Duration listProductsInProductSetMaxRpcTimeout) {
    this.listProductsInProductSetMaxRpcTimeout = listProductsInProductSetMaxRpcTimeout;
  }

  public Duration getListProductsInProductSetTotalTimeout() {
    return this.listProductsInProductSetTotalTimeout;
  }

  public void setListProductsInProductSetTotalTimeout(
      Duration listProductsInProductSetTotalTimeout) {
    this.listProductsInProductSetTotalTimeout = listProductsInProductSetTotalTimeout;
  }

  public Duration getImportProductSetsInitialRetryDelay() {
    return this.importProductSetsInitialRetryDelay;
  }

  public void setImportProductSetsInitialRetryDelay(Duration importProductSetsInitialRetryDelay) {
    this.importProductSetsInitialRetryDelay = importProductSetsInitialRetryDelay;
  }

  public Double getImportProductSetsRetryDelayMultiplier() {
    return this.importProductSetsRetryDelayMultiplier;
  }

  public void setImportProductSetsRetryDelayMultiplier(
      Double importProductSetsRetryDelayMultiplier) {
    this.importProductSetsRetryDelayMultiplier = importProductSetsRetryDelayMultiplier;
  }

  public Duration getImportProductSetsMaxRetryDelay() {
    return this.importProductSetsMaxRetryDelay;
  }

  public void setImportProductSetsMaxRetryDelay(Duration importProductSetsMaxRetryDelay) {
    this.importProductSetsMaxRetryDelay = importProductSetsMaxRetryDelay;
  }

  public Duration getImportProductSetsInitialRpcTimeout() {
    return this.importProductSetsInitialRpcTimeout;
  }

  public void setImportProductSetsInitialRpcTimeout(Duration importProductSetsInitialRpcTimeout) {
    this.importProductSetsInitialRpcTimeout = importProductSetsInitialRpcTimeout;
  }

  public Double getImportProductSetsRpcTimeoutMultiplier() {
    return this.importProductSetsRpcTimeoutMultiplier;
  }

  public void setImportProductSetsRpcTimeoutMultiplier(
      Double importProductSetsRpcTimeoutMultiplier) {
    this.importProductSetsRpcTimeoutMultiplier = importProductSetsRpcTimeoutMultiplier;
  }

  public Duration getImportProductSetsMaxRpcTimeout() {
    return this.importProductSetsMaxRpcTimeout;
  }

  public void setImportProductSetsMaxRpcTimeout(Duration importProductSetsMaxRpcTimeout) {
    this.importProductSetsMaxRpcTimeout = importProductSetsMaxRpcTimeout;
  }

  public Duration getImportProductSetsTotalTimeout() {
    return this.importProductSetsTotalTimeout;
  }

  public void setImportProductSetsTotalTimeout(Duration importProductSetsTotalTimeout) {
    this.importProductSetsTotalTimeout = importProductSetsTotalTimeout;
  }

  public Duration getPurgeProductsInitialRetryDelay() {
    return this.purgeProductsInitialRetryDelay;
  }

  public void setPurgeProductsInitialRetryDelay(Duration purgeProductsInitialRetryDelay) {
    this.purgeProductsInitialRetryDelay = purgeProductsInitialRetryDelay;
  }

  public Double getPurgeProductsRetryDelayMultiplier() {
    return this.purgeProductsRetryDelayMultiplier;
  }

  public void setPurgeProductsRetryDelayMultiplier(Double purgeProductsRetryDelayMultiplier) {
    this.purgeProductsRetryDelayMultiplier = purgeProductsRetryDelayMultiplier;
  }

  public Duration getPurgeProductsMaxRetryDelay() {
    return this.purgeProductsMaxRetryDelay;
  }

  public void setPurgeProductsMaxRetryDelay(Duration purgeProductsMaxRetryDelay) {
    this.purgeProductsMaxRetryDelay = purgeProductsMaxRetryDelay;
  }

  public Duration getPurgeProductsInitialRpcTimeout() {
    return this.purgeProductsInitialRpcTimeout;
  }

  public void setPurgeProductsInitialRpcTimeout(Duration purgeProductsInitialRpcTimeout) {
    this.purgeProductsInitialRpcTimeout = purgeProductsInitialRpcTimeout;
  }

  public Double getPurgeProductsRpcTimeoutMultiplier() {
    return this.purgeProductsRpcTimeoutMultiplier;
  }

  public void setPurgeProductsRpcTimeoutMultiplier(Double purgeProductsRpcTimeoutMultiplier) {
    this.purgeProductsRpcTimeoutMultiplier = purgeProductsRpcTimeoutMultiplier;
  }

  public Duration getPurgeProductsMaxRpcTimeout() {
    return this.purgeProductsMaxRpcTimeout;
  }

  public void setPurgeProductsMaxRpcTimeout(Duration purgeProductsMaxRpcTimeout) {
    this.purgeProductsMaxRpcTimeout = purgeProductsMaxRpcTimeout;
  }

  public Duration getPurgeProductsTotalTimeout() {
    return this.purgeProductsTotalTimeout;
  }

  public void setPurgeProductsTotalTimeout(Duration purgeProductsTotalTimeout) {
    this.purgeProductsTotalTimeout = purgeProductsTotalTimeout;
  }
}
