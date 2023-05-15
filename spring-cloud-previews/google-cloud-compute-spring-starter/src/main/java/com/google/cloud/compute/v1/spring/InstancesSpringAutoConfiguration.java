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

package com.google.cloud.compute.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.InstancesSettings;
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
 * Auto-configuration for {@link InstancesClient}.
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
@ConditionalOnClass(InstancesClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.compute.v1.instances.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(InstancesSpringProperties.class)
public class InstancesSpringAutoConfiguration {
  private final InstancesSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(InstancesSpringAutoConfiguration.class);

  protected InstancesSpringAutoConfiguration(
      InstancesSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Instances-specific configuration");
      }
      this.credentialsProvider =
          ((CredentialsProvider) new DefaultCredentialsProvider(this.clientProperties));
    } else {
      this.credentialsProvider = credentialsProvider;
    }
  }

  /**
   * Provides a default transport channel provider bean. The default is gRPC and will default to it
   * unless the useRest option is supported and provided to use HTTP transport instead
   *
   * @return a default transport channel provider.
   */
  @Bean
  @ConditionalOnMissingBean(name = "defaultInstancesTransportChannelProvider")
  public TransportChannelProvider defaultInstancesTransportChannelProvider() {
    return InstancesSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a InstancesSettings bean configured to use a DefaultCredentialsProvider and the client
   * library's default transport channel provider (defaultInstancesTransportChannelProvider()). It
   * also configures the quota project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in InstancesSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link InstancesSettings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public InstancesSettings instancesSettings(
      @Qualifier("defaultInstancesTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    InstancesSettings.Builder clientSettingsBuilder = InstancesSettings.newBuilder();
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
          InstancesSettings.defaultExecutorProviderBuilder()
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
      RetrySettings aggregatedListRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.aggregatedListSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.aggregatedListSettings().setRetrySettings(aggregatedListRetrySettings);

      RetrySettings getRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getSettings().setRetrySettings(getRetrySettings);

      RetrySettings getEffectiveFirewallsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getEffectiveFirewallsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getEffectiveFirewallsSettings()
          .setRetrySettings(getEffectiveFirewallsRetrySettings);

      RetrySettings getGuestAttributesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getGuestAttributesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getGuestAttributesSettings()
          .setRetrySettings(getGuestAttributesRetrySettings);

      RetrySettings getIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIamPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getIamPolicySettings().setRetrySettings(getIamPolicyRetrySettings);

      RetrySettings getScreenshotRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getScreenshotSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getScreenshotSettings().setRetrySettings(getScreenshotRetrySettings);

      RetrySettings getSerialPortOutputRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSerialPortOutputSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getSerialPortOutputSettings()
          .setRetrySettings(getSerialPortOutputRetrySettings);

      RetrySettings getShieldedInstanceIdentityRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getShieldedInstanceIdentitySettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getShieldedInstanceIdentitySettings()
          .setRetrySettings(getShieldedInstanceIdentityRetrySettings);

      RetrySettings listRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listSettings().setRetrySettings(listRetrySettings);

      RetrySettings listReferrersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listReferrersSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listReferrersSettings().setRetrySettings(listReferrersRetrySettings);

      RetrySettings sendDiagnosticInterruptRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.sendDiagnosticInterruptSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .sendDiagnosticInterruptSettings()
          .setRetrySettings(sendDiagnosticInterruptRetrySettings);

      RetrySettings setIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.setIamPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.setIamPolicySettings().setRetrySettings(setIamPolicyRetrySettings);

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
    Retry aggregatedListRetry = clientProperties.getAggregatedListRetry();
    if (aggregatedListRetry != null) {
      RetrySettings aggregatedListRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.aggregatedListSettings().getRetrySettings(),
              aggregatedListRetry);
      clientSettingsBuilder.aggregatedListSettings().setRetrySettings(aggregatedListRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for aggregatedList from properties.");
      }
    }
    Retry getRetry = clientProperties.getGetRetry();
    if (getRetry != null) {
      RetrySettings getRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSettings().getRetrySettings(), getRetry);
      clientSettingsBuilder.getSettings().setRetrySettings(getRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for get from properties.");
      }
    }
    Retry getEffectiveFirewallsRetry = clientProperties.getGetEffectiveFirewallsRetry();
    if (getEffectiveFirewallsRetry != null) {
      RetrySettings getEffectiveFirewallsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getEffectiveFirewallsSettings().getRetrySettings(),
              getEffectiveFirewallsRetry);
      clientSettingsBuilder
          .getEffectiveFirewallsSettings()
          .setRetrySettings(getEffectiveFirewallsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getEffectiveFirewalls from properties.");
      }
    }
    Retry getGuestAttributesRetry = clientProperties.getGetGuestAttributesRetry();
    if (getGuestAttributesRetry != null) {
      RetrySettings getGuestAttributesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getGuestAttributesSettings().getRetrySettings(),
              getGuestAttributesRetry);
      clientSettingsBuilder
          .getGuestAttributesSettings()
          .setRetrySettings(getGuestAttributesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getGuestAttributes from properties.");
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
    Retry getScreenshotRetry = clientProperties.getGetScreenshotRetry();
    if (getScreenshotRetry != null) {
      RetrySettings getScreenshotRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getScreenshotSettings().getRetrySettings(), getScreenshotRetry);
      clientSettingsBuilder.getScreenshotSettings().setRetrySettings(getScreenshotRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getScreenshot from properties.");
      }
    }
    Retry getSerialPortOutputRetry = clientProperties.getGetSerialPortOutputRetry();
    if (getSerialPortOutputRetry != null) {
      RetrySettings getSerialPortOutputRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getSerialPortOutputSettings().getRetrySettings(),
              getSerialPortOutputRetry);
      clientSettingsBuilder
          .getSerialPortOutputSettings()
          .setRetrySettings(getSerialPortOutputRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getSerialPortOutput from properties.");
      }
    }
    Retry getShieldedInstanceIdentityRetry = clientProperties.getGetShieldedInstanceIdentityRetry();
    if (getShieldedInstanceIdentityRetry != null) {
      RetrySettings getShieldedInstanceIdentityRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getShieldedInstanceIdentitySettings().getRetrySettings(),
              getShieldedInstanceIdentityRetry);
      clientSettingsBuilder
          .getShieldedInstanceIdentitySettings()
          .setRetrySettings(getShieldedInstanceIdentityRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getShieldedInstanceIdentity from properties.");
      }
    }
    Retry listRetry = clientProperties.getListRetry();
    if (listRetry != null) {
      RetrySettings listRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listSettings().getRetrySettings(), listRetry);
      clientSettingsBuilder.listSettings().setRetrySettings(listRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for list from properties.");
      }
    }
    Retry listReferrersRetry = clientProperties.getListReferrersRetry();
    if (listReferrersRetry != null) {
      RetrySettings listReferrersRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listReferrersSettings().getRetrySettings(), listReferrersRetry);
      clientSettingsBuilder.listReferrersSettings().setRetrySettings(listReferrersRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listReferrers from properties.");
      }
    }
    Retry sendDiagnosticInterruptRetry = clientProperties.getSendDiagnosticInterruptRetry();
    if (sendDiagnosticInterruptRetry != null) {
      RetrySettings sendDiagnosticInterruptRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.sendDiagnosticInterruptSettings().getRetrySettings(),
              sendDiagnosticInterruptRetry);
      clientSettingsBuilder
          .sendDiagnosticInterruptSettings()
          .setRetrySettings(sendDiagnosticInterruptRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for sendDiagnosticInterrupt from properties.");
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
   * Provides a InstancesClient bean configured with InstancesSettings.
   *
   * @param instancesSettings settings to configure an instance of client bean.
   * @return a {@link InstancesClient} bean configured with {@link InstancesSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public InstancesClient instancesClient(InstancesSettings instancesSettings) throws IOException {
    return InstancesClient.create(instancesSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-instances";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
