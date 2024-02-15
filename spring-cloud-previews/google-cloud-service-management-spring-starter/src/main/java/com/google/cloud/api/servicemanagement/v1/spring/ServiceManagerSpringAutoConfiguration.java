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

package com.google.cloud.api.servicemanagement.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.api.servicemanagement.v1.ServiceManagerClient;
import com.google.cloud.api.servicemanagement.v1.ServiceManagerSettings;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.Retry;
import com.google.cloud.spring.core.util.RetryUtil;
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
 * Auto-configuration for {@link ServiceManagerClient}.
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
@ConditionalOnClass(ServiceManagerClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.api.servicemanagement.v1.service-manager.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ServiceManagerSpringProperties.class)
public class ServiceManagerSpringAutoConfiguration {
  private final ServiceManagerSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(ServiceManagerSpringAutoConfiguration.class);

  protected ServiceManagerSpringAutoConfiguration(
      ServiceManagerSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from ServiceManager-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultServiceManagerTransportChannelProvider")
  public TransportChannelProvider defaultServiceManagerTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return ServiceManagerSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return ServiceManagerSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ServiceManagerSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultServiceManagerTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ServiceManagerSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ServiceManagerSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ServiceManagerSettings serviceManagerSettings(
      @Qualifier("defaultServiceManagerTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ServiceManagerSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = ServiceManagerSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = ServiceManagerSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
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
          ServiceManagerSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listServicesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listServicesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listServicesSettings().setRetrySettings(listServicesRetrySettings);

      RetrySettings getServiceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getServiceSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getServiceSettings().setRetrySettings(getServiceRetrySettings);

      RetrySettings listServiceConfigsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listServiceConfigsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listServiceConfigsSettings()
          .setRetrySettings(listServiceConfigsRetrySettings);

      RetrySettings getServiceConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getServiceConfigSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getServiceConfigSettings()
          .setRetrySettings(getServiceConfigRetrySettings);

      RetrySettings createServiceConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createServiceConfigSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createServiceConfigSettings()
          .setRetrySettings(createServiceConfigRetrySettings);

      RetrySettings listServiceRolloutsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listServiceRolloutsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listServiceRolloutsSettings()
          .setRetrySettings(listServiceRolloutsRetrySettings);

      RetrySettings getServiceRolloutRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getServiceRolloutSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getServiceRolloutSettings()
          .setRetrySettings(getServiceRolloutRetrySettings);

      RetrySettings generateConfigReportRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateConfigReportSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .generateConfigReportSettings()
          .setRetrySettings(generateConfigReportRetrySettings);

      RetrySettings setIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setIamPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.setIamPolicySettings().setRetrySettings(setIamPolicyRetrySettings);

      RetrySettings getIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIamPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getIamPolicySettings().setRetrySettings(getIamPolicyRetrySettings);

      RetrySettings testIamPermissionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.testIamPermissionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .testIamPermissionsSettings()
          .setRetrySettings(testIamPermissionsRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listServicesRetry = clientProperties.getListServicesRetry();
    if (listServicesRetry != null) {
      RetrySettings listServicesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listServicesSettings().getRetrySettings(), listServicesRetry);
      clientSettingsBuilder.listServicesSettings().setRetrySettings(listServicesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listServices from properties.");
      }
    }
    Retry getServiceRetry = clientProperties.getGetServiceRetry();
    if (getServiceRetry != null) {
      RetrySettings getServiceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getServiceSettings().getRetrySettings(), getServiceRetry);
      clientSettingsBuilder.getServiceSettings().setRetrySettings(getServiceRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getService from properties.");
      }
    }
    Retry listServiceConfigsRetry = clientProperties.getListServiceConfigsRetry();
    if (listServiceConfigsRetry != null) {
      RetrySettings listServiceConfigsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listServiceConfigsSettings().getRetrySettings(),
              listServiceConfigsRetry);
      clientSettingsBuilder
          .listServiceConfigsSettings()
          .setRetrySettings(listServiceConfigsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listServiceConfigs from properties.");
      }
    }
    Retry getServiceConfigRetry = clientProperties.getGetServiceConfigRetry();
    if (getServiceConfigRetry != null) {
      RetrySettings getServiceConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getServiceConfigSettings().getRetrySettings(),
              getServiceConfigRetry);
      clientSettingsBuilder
          .getServiceConfigSettings()
          .setRetrySettings(getServiceConfigRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getServiceConfig from properties.");
      }
    }
    Retry createServiceConfigRetry = clientProperties.getCreateServiceConfigRetry();
    if (createServiceConfigRetry != null) {
      RetrySettings createServiceConfigRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createServiceConfigSettings().getRetrySettings(),
              createServiceConfigRetry);
      clientSettingsBuilder
          .createServiceConfigSettings()
          .setRetrySettings(createServiceConfigRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createServiceConfig from properties.");
      }
    }
    Retry listServiceRolloutsRetry = clientProperties.getListServiceRolloutsRetry();
    if (listServiceRolloutsRetry != null) {
      RetrySettings listServiceRolloutsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listServiceRolloutsSettings().getRetrySettings(),
              listServiceRolloutsRetry);
      clientSettingsBuilder
          .listServiceRolloutsSettings()
          .setRetrySettings(listServiceRolloutsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listServiceRollouts from properties.");
      }
    }
    Retry getServiceRolloutRetry = clientProperties.getGetServiceRolloutRetry();
    if (getServiceRolloutRetry != null) {
      RetrySettings getServiceRolloutRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getServiceRolloutSettings().getRetrySettings(),
              getServiceRolloutRetry);
      clientSettingsBuilder
          .getServiceRolloutSettings()
          .setRetrySettings(getServiceRolloutRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getServiceRollout from properties.");
      }
    }
    Retry generateConfigReportRetry = clientProperties.getGenerateConfigReportRetry();
    if (generateConfigReportRetry != null) {
      RetrySettings generateConfigReportRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.generateConfigReportSettings().getRetrySettings(),
              generateConfigReportRetry);
      clientSettingsBuilder
          .generateConfigReportSettings()
          .setRetrySettings(generateConfigReportRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for generateConfigReport from properties.");
      }
    }
    Retry setIamPolicyRetry = clientProperties.getSetIamPolicyRetry();
    if (setIamPolicyRetry != null) {
      RetrySettings setIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setIamPolicySettings().getRetrySettings(), setIamPolicyRetry);
      clientSettingsBuilder.setIamPolicySettings().setRetrySettings(setIamPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for setIamPolicy from properties.");
      }
    }
    Retry getIamPolicyRetry = clientProperties.getGetIamPolicyRetry();
    if (getIamPolicyRetry != null) {
      RetrySettings getIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIamPolicySettings().getRetrySettings(), getIamPolicyRetry);
      clientSettingsBuilder.getIamPolicySettings().setRetrySettings(getIamPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getIamPolicy from properties.");
      }
    }
    Retry testIamPermissionsRetry = clientProperties.getTestIamPermissionsRetry();
    if (testIamPermissionsRetry != null) {
      RetrySettings testIamPermissionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.testIamPermissionsSettings().getRetrySettings(),
              testIamPermissionsRetry);
      clientSettingsBuilder
          .testIamPermissionsSettings()
          .setRetrySettings(testIamPermissionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for testIamPermissions from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a ServiceManagerClient bean configured with ServiceManagerSettings.
   *
   * @param serviceManagerSettings settings to configure an instance of client bean.
   * @return a {@link ServiceManagerClient} bean configured with {@link ServiceManagerSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ServiceManagerClient serviceManagerClient(ServiceManagerSettings serviceManagerSettings)
      throws IOException {
    return ServiceManagerClient.create(serviceManagerSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-service-manager";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
