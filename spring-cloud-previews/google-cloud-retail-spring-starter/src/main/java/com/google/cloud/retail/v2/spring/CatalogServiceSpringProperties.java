/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.retail.v2.spring;

import com.google.api.core.BetaApi;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.CredentialsSupplier;
import com.google.cloud.spring.core.Retry;
import javax.annotation.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/** Provides default property values for CatalogService client bean */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@ConfigurationProperties("com.google.cloud.retail.v2.catalog-service")
public class CatalogServiceSpringProperties implements CredentialsSupplier {
  /** OAuth2 credentials to authenticate and authorize calls to Google Cloud Client Libraries. */
  @NestedConfigurationProperty
  private final Credentials credentials =
      new Credentials("https://www.googleapis.com/auth/cloud-platform");
  /** Quota project to use for billing. */
  private String quotaProjectId;
  /** Number of threads used for executors. */
  private Integer executorThreadCount;
  /** Allow override of default transport channel provider to use REST instead of gRPC. */
  private boolean useRest = false;
  /** Allow override of retry settings at service level, applying to all of its RPC methods. */
  @NestedConfigurationProperty private Retry retry;
  /**
   * Allow override of retry settings at method-level for listCatalogs. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry listCatalogsRetry;
  /**
   * Allow override of retry settings at method-level for updateCatalog. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateCatalogRetry;
  /**
   * Allow override of retry settings at method-level for setDefaultBranch. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry setDefaultBranchRetry;
  /**
   * Allow override of retry settings at method-level for getDefaultBranch. If defined, this takes
   * precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getDefaultBranchRetry;
  /**
   * Allow override of retry settings at method-level for getCompletionConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getCompletionConfigRetry;
  /**
   * Allow override of retry settings at method-level for updateCompletionConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateCompletionConfigRetry;
  /**
   * Allow override of retry settings at method-level for getAttributesConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry getAttributesConfigRetry;
  /**
   * Allow override of retry settings at method-level for updateAttributesConfig. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry updateAttributesConfigRetry;
  /**
   * Allow override of retry settings at method-level for addCatalogAttribute. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry addCatalogAttributeRetry;
  /**
   * Allow override of retry settings at method-level for removeCatalogAttribute. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry removeCatalogAttributeRetry;
  /**
   * Allow override of retry settings at method-level for replaceCatalogAttribute. If defined, this
   * takes precedence over service-level retry configurations for that RPC method.
   */
  @NestedConfigurationProperty private Retry replaceCatalogAttributeRetry;

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

  public void setUseRest(boolean useRest) {
    this.useRest = useRest;
  }

  public Integer getExecutorThreadCount() {
    return this.executorThreadCount;
  }

  public void setExecutorThreadCount(Integer executorThreadCount) {
    this.executorThreadCount = executorThreadCount;
  }

  public Retry getRetry() {
    return this.retry;
  }

  public void setRetry(Retry retry) {
    this.retry = retry;
  }

  public Retry getListCatalogsRetry() {
    return this.listCatalogsRetry;
  }

  public void setListCatalogsRetry(Retry listCatalogsRetry) {
    this.listCatalogsRetry = listCatalogsRetry;
  }

  public Retry getUpdateCatalogRetry() {
    return this.updateCatalogRetry;
  }

  public void setUpdateCatalogRetry(Retry updateCatalogRetry) {
    this.updateCatalogRetry = updateCatalogRetry;
  }

  public Retry getSetDefaultBranchRetry() {
    return this.setDefaultBranchRetry;
  }

  public void setSetDefaultBranchRetry(Retry setDefaultBranchRetry) {
    this.setDefaultBranchRetry = setDefaultBranchRetry;
  }

  public Retry getGetDefaultBranchRetry() {
    return this.getDefaultBranchRetry;
  }

  public void setGetDefaultBranchRetry(Retry getDefaultBranchRetry) {
    this.getDefaultBranchRetry = getDefaultBranchRetry;
  }

  public Retry getGetCompletionConfigRetry() {
    return this.getCompletionConfigRetry;
  }

  public void setGetCompletionConfigRetry(Retry getCompletionConfigRetry) {
    this.getCompletionConfigRetry = getCompletionConfigRetry;
  }

  public Retry getUpdateCompletionConfigRetry() {
    return this.updateCompletionConfigRetry;
  }

  public void setUpdateCompletionConfigRetry(Retry updateCompletionConfigRetry) {
    this.updateCompletionConfigRetry = updateCompletionConfigRetry;
  }

  public Retry getGetAttributesConfigRetry() {
    return this.getAttributesConfigRetry;
  }

  public void setGetAttributesConfigRetry(Retry getAttributesConfigRetry) {
    this.getAttributesConfigRetry = getAttributesConfigRetry;
  }

  public Retry getUpdateAttributesConfigRetry() {
    return this.updateAttributesConfigRetry;
  }

  public void setUpdateAttributesConfigRetry(Retry updateAttributesConfigRetry) {
    this.updateAttributesConfigRetry = updateAttributesConfigRetry;
  }

  public Retry getAddCatalogAttributeRetry() {
    return this.addCatalogAttributeRetry;
  }

  public void setAddCatalogAttributeRetry(Retry addCatalogAttributeRetry) {
    this.addCatalogAttributeRetry = addCatalogAttributeRetry;
  }

  public Retry getRemoveCatalogAttributeRetry() {
    return this.removeCatalogAttributeRetry;
  }

  public void setRemoveCatalogAttributeRetry(Retry removeCatalogAttributeRetry) {
    this.removeCatalogAttributeRetry = removeCatalogAttributeRetry;
  }

  public Retry getReplaceCatalogAttributeRetry() {
    return this.replaceCatalogAttributeRetry;
  }

  public void setReplaceCatalogAttributeRetry(Retry replaceCatalogAttributeRetry) {
    this.replaceCatalogAttributeRetry = replaceCatalogAttributeRetry;
  }
}
