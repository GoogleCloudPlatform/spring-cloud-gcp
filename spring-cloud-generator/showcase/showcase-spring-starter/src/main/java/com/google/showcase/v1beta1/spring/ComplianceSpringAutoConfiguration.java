/*
 * Copyright 2023 Google LLC
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

package com.google.showcase.v1beta1.spring;

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
import com.google.showcase.v1beta1.ComplianceClient;
import com.google.showcase.v1beta1.ComplianceSettings;
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
 * Auto-configuration for {@link ComplianceClient}.
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
@ConditionalOnClass(ComplianceClient.class)
@ConditionalOnProperty(
    value = "com.google.showcase.v1beta1.compliance.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ComplianceSpringProperties.class)
public class ComplianceSpringAutoConfiguration {
  private final ComplianceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(ComplianceSpringAutoConfiguration.class);

  protected ComplianceSpringAutoConfiguration(
      ComplianceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Compliance-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultComplianceTransportChannelProvider")
  public TransportChannelProvider defaultComplianceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return ComplianceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return ComplianceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ComplianceSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultComplianceTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ComplianceSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ComplianceSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ComplianceSettings complianceSettings(
      @Qualifier("defaultComplianceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ComplianceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = ComplianceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = ComplianceSettings.newBuilder();
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
          ComplianceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings repeatDataBodyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataBodySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.repeatDataBodySettings().setRetrySettings(repeatDataBodyRetrySettings);

      RetrySettings repeatDataBodyInfoRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataBodyInfoSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .repeatDataBodyInfoSettings()
          .setRetrySettings(repeatDataBodyInfoRetrySettings);

      RetrySettings repeatDataQueryRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataQuerySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .repeatDataQuerySettings()
          .setRetrySettings(repeatDataQueryRetrySettings);

      RetrySettings repeatDataSimplePathRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataSimplePathSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .repeatDataSimplePathSettings()
          .setRetrySettings(repeatDataSimplePathRetrySettings);

      RetrySettings repeatDataPathResourceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataPathResourceSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .repeatDataPathResourceSettings()
          .setRetrySettings(repeatDataPathResourceRetrySettings);

      RetrySettings repeatDataPathTrailingResourceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataPathTrailingResourceSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .repeatDataPathTrailingResourceSettings()
          .setRetrySettings(repeatDataPathTrailingResourceRetrySettings);

      RetrySettings repeatDataBodyPutRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataBodyPutSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .repeatDataBodyPutSettings()
          .setRetrySettings(repeatDataBodyPutRetrySettings);

      RetrySettings repeatDataBodyPatchRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataBodyPatchSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .repeatDataBodyPatchSettings()
          .setRetrySettings(repeatDataBodyPatchRetrySettings);

      RetrySettings getEnumRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getEnumSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getEnumSettings().setRetrySettings(getEnumRetrySettings);

      RetrySettings verifyEnumRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.verifyEnumSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.verifyEnumSettings().setRetrySettings(verifyEnumRetrySettings);

      RetrySettings listLocationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listLocationsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listLocationsSettings().setRetrySettings(listLocationsRetrySettings);

      RetrySettings getLocationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getLocationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getLocationSettings().setRetrySettings(getLocationRetrySettings);

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
    Retry repeatDataBodyRetry = clientProperties.getRepeatDataBodyRetry();
    if (repeatDataBodyRetry != null) {
      RetrySettings repeatDataBodyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataBodySettings().getRetrySettings(),
              repeatDataBodyRetry);
      clientSettingsBuilder.repeatDataBodySettings().setRetrySettings(repeatDataBodyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for repeatDataBody from properties.");
      }
    }
    Retry repeatDataBodyInfoRetry = clientProperties.getRepeatDataBodyInfoRetry();
    if (repeatDataBodyInfoRetry != null) {
      RetrySettings repeatDataBodyInfoRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataBodyInfoSettings().getRetrySettings(),
              repeatDataBodyInfoRetry);
      clientSettingsBuilder
          .repeatDataBodyInfoSettings()
          .setRetrySettings(repeatDataBodyInfoRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for repeatDataBodyInfo from properties.");
      }
    }
    Retry repeatDataQueryRetry = clientProperties.getRepeatDataQueryRetry();
    if (repeatDataQueryRetry != null) {
      RetrySettings repeatDataQueryRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataQuerySettings().getRetrySettings(),
              repeatDataQueryRetry);
      clientSettingsBuilder
          .repeatDataQuerySettings()
          .setRetrySettings(repeatDataQueryRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for repeatDataQuery from properties.");
      }
    }
    Retry repeatDataSimplePathRetry = clientProperties.getRepeatDataSimplePathRetry();
    if (repeatDataSimplePathRetry != null) {
      RetrySettings repeatDataSimplePathRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataSimplePathSettings().getRetrySettings(),
              repeatDataSimplePathRetry);
      clientSettingsBuilder
          .repeatDataSimplePathSettings()
          .setRetrySettings(repeatDataSimplePathRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for repeatDataSimplePath from properties.");
      }
    }
    Retry repeatDataPathResourceRetry = clientProperties.getRepeatDataPathResourceRetry();
    if (repeatDataPathResourceRetry != null) {
      RetrySettings repeatDataPathResourceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataPathResourceSettings().getRetrySettings(),
              repeatDataPathResourceRetry);
      clientSettingsBuilder
          .repeatDataPathResourceSettings()
          .setRetrySettings(repeatDataPathResourceRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for repeatDataPathResource from properties.");
      }
    }
    Retry repeatDataPathTrailingResourceRetry =
        clientProperties.getRepeatDataPathTrailingResourceRetry();
    if (repeatDataPathTrailingResourceRetry != null) {
      RetrySettings repeatDataPathTrailingResourceRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataPathTrailingResourceSettings().getRetrySettings(),
              repeatDataPathTrailingResourceRetry);
      clientSettingsBuilder
          .repeatDataPathTrailingResourceSettings()
          .setRetrySettings(repeatDataPathTrailingResourceRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for repeatDataPathTrailingResource from properties.");
      }
    }
    Retry repeatDataBodyPutRetry = clientProperties.getRepeatDataBodyPutRetry();
    if (repeatDataBodyPutRetry != null) {
      RetrySettings repeatDataBodyPutRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataBodyPutSettings().getRetrySettings(),
              repeatDataBodyPutRetry);
      clientSettingsBuilder
          .repeatDataBodyPutSettings()
          .setRetrySettings(repeatDataBodyPutRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for repeatDataBodyPut from properties.");
      }
    }
    Retry repeatDataBodyPatchRetry = clientProperties.getRepeatDataBodyPatchRetry();
    if (repeatDataBodyPatchRetry != null) {
      RetrySettings repeatDataBodyPatchRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.repeatDataBodyPatchSettings().getRetrySettings(),
              repeatDataBodyPatchRetry);
      clientSettingsBuilder
          .repeatDataBodyPatchSettings()
          .setRetrySettings(repeatDataBodyPatchRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for repeatDataBodyPatch from properties.");
      }
    }
    Retry getEnumRetry = clientProperties.getGetEnumRetry();
    if (getEnumRetry != null) {
      RetrySettings getEnumRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getEnumSettings().getRetrySettings(), getEnumRetry);
      clientSettingsBuilder.getEnumSettings().setRetrySettings(getEnumRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getEnum from properties.");
      }
    }
    Retry verifyEnumRetry = clientProperties.getVerifyEnumRetry();
    if (verifyEnumRetry != null) {
      RetrySettings verifyEnumRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.verifyEnumSettings().getRetrySettings(), verifyEnumRetry);
      clientSettingsBuilder.verifyEnumSettings().setRetrySettings(verifyEnumRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for verifyEnum from properties.");
      }
    }
    Retry listLocationsRetry = clientProperties.getListLocationsRetry();
    if (listLocationsRetry != null) {
      RetrySettings listLocationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listLocationsSettings().getRetrySettings(), listLocationsRetry);
      clientSettingsBuilder.listLocationsSettings().setRetrySettings(listLocationsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listLocations from properties.");
      }
    }
    Retry getLocationRetry = clientProperties.getGetLocationRetry();
    if (getLocationRetry != null) {
      RetrySettings getLocationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getLocationSettings().getRetrySettings(), getLocationRetry);
      clientSettingsBuilder.getLocationSettings().setRetrySettings(getLocationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getLocation from properties.");
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
   * Provides a ComplianceClient bean configured with ComplianceSettings.
   *
   * @param complianceSettings settings to configure an instance of client bean.
   * @return a {@link ComplianceClient} bean configured with {@link ComplianceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ComplianceClient complianceClient(ComplianceSettings complianceSettings)
      throws IOException {
    return ComplianceClient.create(complianceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-compliance";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
