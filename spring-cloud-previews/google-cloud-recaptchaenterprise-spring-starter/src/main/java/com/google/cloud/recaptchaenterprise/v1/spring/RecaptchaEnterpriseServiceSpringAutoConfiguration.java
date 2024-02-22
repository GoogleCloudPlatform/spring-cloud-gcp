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

package com.google.cloud.recaptchaenterprise.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceSettings;
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
 * Auto-configuration for {@link RecaptchaEnterpriseServiceClient}.
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
@ConditionalOnClass(RecaptchaEnterpriseServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.recaptchaenterprise.v1.recaptcha-enterprise-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(RecaptchaEnterpriseServiceSpringProperties.class)
public class RecaptchaEnterpriseServiceSpringAutoConfiguration {
  private final RecaptchaEnterpriseServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(RecaptchaEnterpriseServiceSpringAutoConfiguration.class);

  protected RecaptchaEnterpriseServiceSpringAutoConfiguration(
      RecaptchaEnterpriseServiceSpringProperties clientProperties,
      CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from RecaptchaEnterpriseService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultRecaptchaEnterpriseServiceTransportChannelProvider")
  public TransportChannelProvider defaultRecaptchaEnterpriseServiceTransportChannelProvider() {
    return RecaptchaEnterpriseServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a RecaptchaEnterpriseServiceSettings bean configured to use a
   * DefaultCredentialsProvider and the client library's default transport channel provider
   * (defaultRecaptchaEnterpriseServiceTransportChannelProvider()). It also configures the quota
   * project ID and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in RecaptchaEnterpriseServiceSpringProperties. Method-level properties will take precedence
   * over service-level properties if available, and client library defaults will be used if neither
   * are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link RecaptchaEnterpriseServiceSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public RecaptchaEnterpriseServiceSettings recaptchaEnterpriseServiceSettings(
      @Qualifier("defaultRecaptchaEnterpriseServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    RecaptchaEnterpriseServiceSettings.Builder clientSettingsBuilder =
        RecaptchaEnterpriseServiceSettings.newBuilder();
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
          RecaptchaEnterpriseServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings createAssessmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createAssessmentSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createAssessmentSettings()
          .setRetrySettings(createAssessmentRetrySettings);

      RetrySettings annotateAssessmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.annotateAssessmentSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .annotateAssessmentSettings()
          .setRetrySettings(annotateAssessmentRetrySettings);

      RetrySettings createKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createKeySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createKeySettings().setRetrySettings(createKeyRetrySettings);

      RetrySettings listKeysRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listKeysSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listKeysSettings().setRetrySettings(listKeysRetrySettings);

      RetrySettings retrieveLegacySecretKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.retrieveLegacySecretKeySettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .retrieveLegacySecretKeySettings()
          .setRetrySettings(retrieveLegacySecretKeyRetrySettings);

      RetrySettings getKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getKeySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getKeySettings().setRetrySettings(getKeyRetrySettings);

      RetrySettings updateKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateKeySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateKeySettings().setRetrySettings(updateKeyRetrySettings);

      RetrySettings deleteKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteKeySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteKeySettings().setRetrySettings(deleteKeyRetrySettings);

      RetrySettings migrateKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.migrateKeySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.migrateKeySettings().setRetrySettings(migrateKeyRetrySettings);

      RetrySettings getMetricsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getMetricsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getMetricsSettings().setRetrySettings(getMetricsRetrySettings);

      RetrySettings createFirewallPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createFirewallPolicySettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .createFirewallPolicySettings()
          .setRetrySettings(createFirewallPolicyRetrySettings);

      RetrySettings listFirewallPoliciesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listFirewallPoliciesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listFirewallPoliciesSettings()
          .setRetrySettings(listFirewallPoliciesRetrySettings);

      RetrySettings getFirewallPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getFirewallPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getFirewallPolicySettings()
          .setRetrySettings(getFirewallPolicyRetrySettings);

      RetrySettings updateFirewallPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateFirewallPolicySettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .updateFirewallPolicySettings()
          .setRetrySettings(updateFirewallPolicyRetrySettings);

      RetrySettings deleteFirewallPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteFirewallPolicySettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .deleteFirewallPolicySettings()
          .setRetrySettings(deleteFirewallPolicyRetrySettings);

      RetrySettings reorderFirewallPoliciesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.reorderFirewallPoliciesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .reorderFirewallPoliciesSettings()
          .setRetrySettings(reorderFirewallPoliciesRetrySettings);

      RetrySettings listRelatedAccountGroupsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRelatedAccountGroupsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listRelatedAccountGroupsSettings()
          .setRetrySettings(listRelatedAccountGroupsRetrySettings);

      RetrySettings listRelatedAccountGroupMembershipsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRelatedAccountGroupMembershipsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listRelatedAccountGroupMembershipsSettings()
          .setRetrySettings(listRelatedAccountGroupMembershipsRetrySettings);

      RetrySettings searchRelatedAccountGroupMembershipsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder
                  .searchRelatedAccountGroupMembershipsSettings()
                  .getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .searchRelatedAccountGroupMembershipsSettings()
          .setRetrySettings(searchRelatedAccountGroupMembershipsRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry createAssessmentRetry = clientProperties.getCreateAssessmentRetry();
    if (createAssessmentRetry != null) {
      RetrySettings createAssessmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createAssessmentSettings().getRetrySettings(),
              createAssessmentRetry);
      clientSettingsBuilder
          .createAssessmentSettings()
          .setRetrySettings(createAssessmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createAssessment from properties.");
      }
    }
    Retry annotateAssessmentRetry = clientProperties.getAnnotateAssessmentRetry();
    if (annotateAssessmentRetry != null) {
      RetrySettings annotateAssessmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.annotateAssessmentSettings().getRetrySettings(),
              annotateAssessmentRetry);
      clientSettingsBuilder
          .annotateAssessmentSettings()
          .setRetrySettings(annotateAssessmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for annotateAssessment from properties.");
      }
    }
    Retry createKeyRetry = clientProperties.getCreateKeyRetry();
    if (createKeyRetry != null) {
      RetrySettings createKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createKeySettings().getRetrySettings(), createKeyRetry);
      clientSettingsBuilder.createKeySettings().setRetrySettings(createKeyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createKey from properties.");
      }
    }
    Retry listKeysRetry = clientProperties.getListKeysRetry();
    if (listKeysRetry != null) {
      RetrySettings listKeysRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listKeysSettings().getRetrySettings(), listKeysRetry);
      clientSettingsBuilder.listKeysSettings().setRetrySettings(listKeysRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listKeys from properties.");
      }
    }
    Retry retrieveLegacySecretKeyRetry = clientProperties.getRetrieveLegacySecretKeyRetry();
    if (retrieveLegacySecretKeyRetry != null) {
      RetrySettings retrieveLegacySecretKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.retrieveLegacySecretKeySettings().getRetrySettings(),
              retrieveLegacySecretKeyRetry);
      clientSettingsBuilder
          .retrieveLegacySecretKeySettings()
          .setRetrySettings(retrieveLegacySecretKeyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for retrieveLegacySecretKey from properties.");
      }
    }
    Retry getKeyRetry = clientProperties.getGetKeyRetry();
    if (getKeyRetry != null) {
      RetrySettings getKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getKeySettings().getRetrySettings(), getKeyRetry);
      clientSettingsBuilder.getKeySettings().setRetrySettings(getKeyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getKey from properties.");
      }
    }
    Retry updateKeyRetry = clientProperties.getUpdateKeyRetry();
    if (updateKeyRetry != null) {
      RetrySettings updateKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateKeySettings().getRetrySettings(), updateKeyRetry);
      clientSettingsBuilder.updateKeySettings().setRetrySettings(updateKeyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateKey from properties.");
      }
    }
    Retry deleteKeyRetry = clientProperties.getDeleteKeyRetry();
    if (deleteKeyRetry != null) {
      RetrySettings deleteKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteKeySettings().getRetrySettings(), deleteKeyRetry);
      clientSettingsBuilder.deleteKeySettings().setRetrySettings(deleteKeyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteKey from properties.");
      }
    }
    Retry migrateKeyRetry = clientProperties.getMigrateKeyRetry();
    if (migrateKeyRetry != null) {
      RetrySettings migrateKeyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.migrateKeySettings().getRetrySettings(), migrateKeyRetry);
      clientSettingsBuilder.migrateKeySettings().setRetrySettings(migrateKeyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for migrateKey from properties.");
      }
    }
    Retry getMetricsRetry = clientProperties.getGetMetricsRetry();
    if (getMetricsRetry != null) {
      RetrySettings getMetricsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getMetricsSettings().getRetrySettings(), getMetricsRetry);
      clientSettingsBuilder.getMetricsSettings().setRetrySettings(getMetricsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getMetrics from properties.");
      }
    }
    Retry createFirewallPolicyRetry = clientProperties.getCreateFirewallPolicyRetry();
    if (createFirewallPolicyRetry != null) {
      RetrySettings createFirewallPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createFirewallPolicySettings().getRetrySettings(),
              createFirewallPolicyRetry);
      clientSettingsBuilder
          .createFirewallPolicySettings()
          .setRetrySettings(createFirewallPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createFirewallPolicy from properties.");
      }
    }
    Retry listFirewallPoliciesRetry = clientProperties.getListFirewallPoliciesRetry();
    if (listFirewallPoliciesRetry != null) {
      RetrySettings listFirewallPoliciesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listFirewallPoliciesSettings().getRetrySettings(),
              listFirewallPoliciesRetry);
      clientSettingsBuilder
          .listFirewallPoliciesSettings()
          .setRetrySettings(listFirewallPoliciesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listFirewallPolicies from properties.");
      }
    }
    Retry getFirewallPolicyRetry = clientProperties.getGetFirewallPolicyRetry();
    if (getFirewallPolicyRetry != null) {
      RetrySettings getFirewallPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getFirewallPolicySettings().getRetrySettings(),
              getFirewallPolicyRetry);
      clientSettingsBuilder
          .getFirewallPolicySettings()
          .setRetrySettings(getFirewallPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getFirewallPolicy from properties.");
      }
    }
    Retry updateFirewallPolicyRetry = clientProperties.getUpdateFirewallPolicyRetry();
    if (updateFirewallPolicyRetry != null) {
      RetrySettings updateFirewallPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateFirewallPolicySettings().getRetrySettings(),
              updateFirewallPolicyRetry);
      clientSettingsBuilder
          .updateFirewallPolicySettings()
          .setRetrySettings(updateFirewallPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateFirewallPolicy from properties.");
      }
    }
    Retry deleteFirewallPolicyRetry = clientProperties.getDeleteFirewallPolicyRetry();
    if (deleteFirewallPolicyRetry != null) {
      RetrySettings deleteFirewallPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteFirewallPolicySettings().getRetrySettings(),
              deleteFirewallPolicyRetry);
      clientSettingsBuilder
          .deleteFirewallPolicySettings()
          .setRetrySettings(deleteFirewallPolicyRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteFirewallPolicy from properties.");
      }
    }
    Retry reorderFirewallPoliciesRetry = clientProperties.getReorderFirewallPoliciesRetry();
    if (reorderFirewallPoliciesRetry != null) {
      RetrySettings reorderFirewallPoliciesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.reorderFirewallPoliciesSettings().getRetrySettings(),
              reorderFirewallPoliciesRetry);
      clientSettingsBuilder
          .reorderFirewallPoliciesSettings()
          .setRetrySettings(reorderFirewallPoliciesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for reorderFirewallPolicies from properties.");
      }
    }
    Retry listRelatedAccountGroupsRetry = clientProperties.getListRelatedAccountGroupsRetry();
    if (listRelatedAccountGroupsRetry != null) {
      RetrySettings listRelatedAccountGroupsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRelatedAccountGroupsSettings().getRetrySettings(),
              listRelatedAccountGroupsRetry);
      clientSettingsBuilder
          .listRelatedAccountGroupsSettings()
          .setRetrySettings(listRelatedAccountGroupsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listRelatedAccountGroups from properties.");
      }
    }
    Retry listRelatedAccountGroupMembershipsRetry =
        clientProperties.getListRelatedAccountGroupMembershipsRetry();
    if (listRelatedAccountGroupMembershipsRetry != null) {
      RetrySettings listRelatedAccountGroupMembershipsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listRelatedAccountGroupMembershipsSettings().getRetrySettings(),
              listRelatedAccountGroupMembershipsRetry);
      clientSettingsBuilder
          .listRelatedAccountGroupMembershipsSettings()
          .setRetrySettings(listRelatedAccountGroupMembershipsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listRelatedAccountGroupMemberships from properties.");
      }
    }
    Retry searchRelatedAccountGroupMembershipsRetry =
        clientProperties.getSearchRelatedAccountGroupMembershipsRetry();
    if (searchRelatedAccountGroupMembershipsRetry != null) {
      RetrySettings searchRelatedAccountGroupMembershipsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder
                  .searchRelatedAccountGroupMembershipsSettings()
                  .getRetrySettings(),
              searchRelatedAccountGroupMembershipsRetry);
      clientSettingsBuilder
          .searchRelatedAccountGroupMembershipsSettings()
          .setRetrySettings(searchRelatedAccountGroupMembershipsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for searchRelatedAccountGroupMemberships from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a RecaptchaEnterpriseServiceClient bean configured with
   * RecaptchaEnterpriseServiceSettings.
   *
   * @param recaptchaEnterpriseServiceSettings settings to configure an instance of client bean.
   * @return a {@link RecaptchaEnterpriseServiceClient} bean configured with {@link
   *     RecaptchaEnterpriseServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public RecaptchaEnterpriseServiceClient recaptchaEnterpriseServiceClient(
      RecaptchaEnterpriseServiceSettings recaptchaEnterpriseServiceSettings) throws IOException {
    return RecaptchaEnterpriseServiceClient.create(recaptchaEnterpriseServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-recaptcha-enterprise-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
