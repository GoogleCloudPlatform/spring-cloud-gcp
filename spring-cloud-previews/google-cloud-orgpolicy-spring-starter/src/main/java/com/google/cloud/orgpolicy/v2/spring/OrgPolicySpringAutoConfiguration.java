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

package com.google.cloud.orgpolicy.v2.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.orgpolicy.v2.OrgPolicyClient;
import com.google.cloud.orgpolicy.v2.OrgPolicySettings;
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
 * Auto-configuration for {@link OrgPolicyClient}.
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
@ConditionalOnClass(OrgPolicyClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.orgpolicy.v2.org-policy.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(OrgPolicySpringProperties.class)
public class OrgPolicySpringAutoConfiguration {
  private final OrgPolicySpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(OrgPolicySpringAutoConfiguration.class);

  protected OrgPolicySpringAutoConfiguration(
      OrgPolicySpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from OrgPolicy-specific configuration");
      }
      this.credentialsProvider =
          ((CredentialsProvider) new DefaultCredentialsProvider(this.clientProperties));
    } else {
      this.credentialsProvider = credentialsProvider;
    }
  }

  /**
   * Provides a default transport channel provider bean. The default is gRPC and will default to it
   * unless the useRest option is provided to use HTTP transport instead
   *
   * @return a default transport channel provider.
   */
  @Bean
  @ConditionalOnMissingBean(name = "defaultOrgPolicyTransportChannelProvider")
  public TransportChannelProvider defaultOrgPolicyTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return OrgPolicySettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return OrgPolicySettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a OrgPolicySettings bean configured to use a DefaultCredentialsProvider and the client
   * library's default transport channel provider (defaultOrgPolicyTransportChannelProvider()). It
   * also configures the quota project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in OrgPolicySpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link OrgPolicySettings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public OrgPolicySettings orgPolicySettings(
      @Qualifier("defaultOrgPolicyTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    OrgPolicySettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = OrgPolicySettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = OrgPolicySettings.newBuilder();
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
          OrgPolicySettings.defaultExecutorProviderBuilder()
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
      RetrySettings listConstraintsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConstraintsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listConstraintsSettings()
          .setRetrySettings(listConstraintsRetrySettings);

      RetrySettings listPoliciesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPoliciesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listPoliciesSettings().setRetrySettings(listPoliciesRetrySettings);

      RetrySettings getPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getPolicySettings().setRetrySettings(getPolicyRetrySettings);

      RetrySettings getEffectivePolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getEffectivePolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getEffectivePolicySettings()
          .setRetrySettings(getEffectivePolicyRetrySettings);

      RetrySettings createPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createPolicySettings().setRetrySettings(createPolicyRetrySettings);

      RetrySettings updatePolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updatePolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updatePolicySettings().setRetrySettings(updatePolicyRetrySettings);

      RetrySettings deletePolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deletePolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deletePolicySettings().setRetrySettings(deletePolicyRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listConstraintsRetry = clientProperties.getListConstraintsRetry();
    if (listConstraintsRetry != null) {
      RetrySettings listConstraintsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConstraintsSettings().getRetrySettings(),
              listConstraintsRetry);
      clientSettingsBuilder
          .listConstraintsSettings()
          .setRetrySettings(listConstraintsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listConstraints from properties.");
      }
    }
    Retry listPoliciesRetry = clientProperties.getListPoliciesRetry();
    if (listPoliciesRetry != null) {
      RetrySettings listPoliciesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listPoliciesSettings().getRetrySettings(), listPoliciesRetry);
      clientSettingsBuilder.listPoliciesSettings().setRetrySettings(listPoliciesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listPolicies from properties.");
      }
    }
    Retry getPolicyRetry = clientProperties.getGetPolicyRetry();
    if (getPolicyRetry != null) {
      RetrySettings getPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getPolicySettings().getRetrySettings(), getPolicyRetry);
      clientSettingsBuilder.getPolicySettings().setRetrySettings(getPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getPolicy from properties.");
      }
    }
    Retry getEffectivePolicyRetry = clientProperties.getGetEffectivePolicyRetry();
    if (getEffectivePolicyRetry != null) {
      RetrySettings getEffectivePolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getEffectivePolicySettings().getRetrySettings(),
              getEffectivePolicyRetry);
      clientSettingsBuilder
          .getEffectivePolicySettings()
          .setRetrySettings(getEffectivePolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getEffectivePolicy from properties.");
      }
    }
    Retry createPolicyRetry = clientProperties.getCreatePolicyRetry();
    if (createPolicyRetry != null) {
      RetrySettings createPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createPolicySettings().getRetrySettings(), createPolicyRetry);
      clientSettingsBuilder.createPolicySettings().setRetrySettings(createPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createPolicy from properties.");
      }
    }
    Retry updatePolicyRetry = clientProperties.getUpdatePolicyRetry();
    if (updatePolicyRetry != null) {
      RetrySettings updatePolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updatePolicySettings().getRetrySettings(), updatePolicyRetry);
      clientSettingsBuilder.updatePolicySettings().setRetrySettings(updatePolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updatePolicy from properties.");
      }
    }
    Retry deletePolicyRetry = clientProperties.getDeletePolicyRetry();
    if (deletePolicyRetry != null) {
      RetrySettings deletePolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deletePolicySettings().getRetrySettings(), deletePolicyRetry);
      clientSettingsBuilder.deletePolicySettings().setRetrySettings(deletePolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deletePolicy from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a OrgPolicyClient bean configured with OrgPolicySettings.
   *
   * @param orgPolicySettings settings to configure an instance of client bean.
   * @return a {@link OrgPolicyClient} bean configured with {@link OrgPolicySettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public OrgPolicyClient orgPolicyClient(OrgPolicySettings orgPolicySettings) throws IOException {
    return OrgPolicyClient.create(orgPolicySettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-org-policy";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
