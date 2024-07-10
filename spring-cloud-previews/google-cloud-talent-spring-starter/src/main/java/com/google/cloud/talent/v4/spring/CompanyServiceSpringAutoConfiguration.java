/*
 * Copyright 2024 Google LLC
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

package com.google.cloud.talent.v4.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.Retry;
import com.google.cloud.spring.core.util.RetryUtil;
import com.google.cloud.talent.v4.CompanyServiceClient;
import com.google.cloud.talent.v4.CompanyServiceSettings;
import java.io.IOException;
import java.util.Collections;
import javax.annotation.Generated;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Auto-configuration for {@link CompanyServiceClient}.
 *
 * <p>Provides auto-configuration for Spring Boot
 *
 * <p>The default instance has everything set to sensible defaults:
 *
 * <ul>
 *   <li>The default transport provider is used.
 *   <li>Credentials are acquired automatically through Application Default Credentials.
 *   <li>Retries are configured for idempotent methods but not for non-idempotent methods.
 * </ul>
 */
@Generated("by google-cloud-spring-generator")
@BetaApi("Autogenerated Spring autoconfiguration is not yet stable")
@AutoConfiguration
@AutoConfigureAfter(GcpContextAutoConfiguration.class)
@ConditionalOnClass(CompanyServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.talent.v4.company-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(CompanyServiceSpringProperties.class)
public class CompanyServiceSpringAutoConfiguration {
  private final CompanyServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(CompanyServiceSpringAutoConfiguration.class);

  protected CompanyServiceSpringAutoConfiguration(
      CompanyServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from CompanyService-specific configuration");
      }
      this.credentialsProvider =
          ((CredentialsProvider) new DefaultCredentialsProvider(this.clientProperties));
    } else {
      this.credentialsProvider = credentialsProvider;
    }
  }

  /**
   * Provides a default transport channel provider bean, corresponding to the client library's
   * default transport channel provider. If the library supports both GRPC and REST transport, and
   * the useRest property is configured, the HTTP/JSON transport provider will be used instead of
   * GRPC.
   *
   * @return a default transport channel provider.
   */
  @Bean
  @ConditionalOnMissingBean(name = "defaultCompanyServiceTransportChannelProvider")
  public TransportChannelProvider defaultCompanyServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return CompanyServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return CompanyServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a CompanyServiceSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultCompanyServiceTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in CompanyServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link CompanyServiceSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public CompanyServiceSettings companyServiceSettings(
      @Qualifier("defaultCompanyServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    CompanyServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = CompanyServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = CompanyServiceSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(CompanyServiceSettings.getDefaultEndpoint())
        .setHeaderProvider(this.userAgentHeaderProvider());
    if (this.clientProperties.getQuotaProjectId() != null) {
      clientSettingsBuilder.setQuotaProjectId(this.clientProperties.getQuotaProjectId());
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Quota project id set to "
                + this.clientProperties.getQuotaProjectId()
                + ", this overrides project id from credentials.");
      }
    }
    if (this.clientProperties.getExecutorThreadCount() != null) {
      ExecutorProvider executorProvider =
          CompanyServiceSettings.defaultExecutorProviderBuilder()
              .setExecutorThreadCount(this.clientProperties.getExecutorThreadCount())
              .build();
      clientSettingsBuilder.setBackgroundExecutorProvider(executorProvider);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Background executor thread count is "
                + this.clientProperties.getExecutorThreadCount());
      }
    }
    Retry serviceRetry = clientProperties.getRetry();
    if (serviceRetry != null) {
      RetrySettings createCompanyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createCompanySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createCompanySettings().setRetrySettings(createCompanyRetrySettings);

      RetrySettings getCompanyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getCompanySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getCompanySettings().setRetrySettings(getCompanyRetrySettings);

      RetrySettings updateCompanyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateCompanySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateCompanySettings().setRetrySettings(updateCompanyRetrySettings);

      RetrySettings deleteCompanyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteCompanySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteCompanySettings().setRetrySettings(deleteCompanyRetrySettings);

      RetrySettings listCompaniesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listCompaniesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listCompaniesSettings().setRetrySettings(listCompaniesRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry createCompanyRetry = clientProperties.getCreateCompanyRetry();
    if (createCompanyRetry != null) {
      RetrySettings createCompanyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createCompanySettings().getRetrySettings(), createCompanyRetry);
      clientSettingsBuilder.createCompanySettings().setRetrySettings(createCompanyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createCompany from properties.");
      }
    }
    Retry getCompanyRetry = clientProperties.getGetCompanyRetry();
    if (getCompanyRetry != null) {
      RetrySettings getCompanyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getCompanySettings().getRetrySettings(), getCompanyRetry);
      clientSettingsBuilder.getCompanySettings().setRetrySettings(getCompanyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getCompany from properties.");
      }
    }
    Retry updateCompanyRetry = clientProperties.getUpdateCompanyRetry();
    if (updateCompanyRetry != null) {
      RetrySettings updateCompanyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateCompanySettings().getRetrySettings(), updateCompanyRetry);
      clientSettingsBuilder.updateCompanySettings().setRetrySettings(updateCompanyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateCompany from properties.");
      }
    }
    Retry deleteCompanyRetry = clientProperties.getDeleteCompanyRetry();
    if (deleteCompanyRetry != null) {
      RetrySettings deleteCompanyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteCompanySettings().getRetrySettings(), deleteCompanyRetry);
      clientSettingsBuilder.deleteCompanySettings().setRetrySettings(deleteCompanyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteCompany from properties.");
      }
    }
    Retry listCompaniesRetry = clientProperties.getListCompaniesRetry();
    if (listCompaniesRetry != null) {
      RetrySettings listCompaniesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listCompaniesSettings().getRetrySettings(), listCompaniesRetry);
      clientSettingsBuilder.listCompaniesSettings().setRetrySettings(listCompaniesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listCompanies from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a CompanyServiceClient bean configured with CompanyServiceSettings.
   *
   * @param companyServiceSettings settings to configure an instance of client bean.
   * @return a {@link CompanyServiceClient} bean configured with {@link CompanyServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public CompanyServiceClient companyServiceClient(CompanyServiceSettings companyServiceSettings)
      throws IOException {
    return CompanyServiceClient.create(companyServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-company-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
