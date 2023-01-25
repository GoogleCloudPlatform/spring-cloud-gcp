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

package com.google.appengine.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.appengine.v1.FirewallClient;
import com.google.appengine.v1.FirewallSettings;
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
 * Auto-configuration for {@link FirewallClient}.
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
@ConditionalOnClass(FirewallClient.class)
@ConditionalOnProperty(value = "com.google.appengine.v1.firewall.enabled", matchIfMissing = true)
@EnableConfigurationProperties(FirewallSpringProperties.class)
public class FirewallSpringAutoConfiguration {
  private final FirewallSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(FirewallSpringAutoConfiguration.class);

  protected FirewallSpringAutoConfiguration(
      FirewallSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from Firewall-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultFirewallTransportChannelProvider")
  public TransportChannelProvider defaultFirewallTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return FirewallSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return FirewallSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a FirewallSettings bean configured to use the default credentials provider (obtained
   * with firewallCredentials()) and its default transport channel provider
   * (defaultFirewallTransportChannelProvider()). It also configures the quota project ID if
   * provided. It will configure an executor provider in case there is more than one thread
   * configured in the client
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in FirewallSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link FirewallSettings} bean configured with {@link TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public FirewallSettings firewallSettings(
      @Qualifier("defaultFirewallTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    FirewallSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = FirewallSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = FirewallSettings.newBuilder();
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
          FirewallSettings.defaultExecutorProviderBuilder()
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
      RetrySettings listIngressRulesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listIngressRulesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listIngressRulesSettings()
          .setRetrySettings(listIngressRulesRetrySettings);

      RetrySettings batchUpdateIngressRulesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.batchUpdateIngressRulesSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .batchUpdateIngressRulesSettings()
          .setRetrySettings(batchUpdateIngressRulesRetrySettings);

      RetrySettings createIngressRuleRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createIngressRuleSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createIngressRuleSettings()
          .setRetrySettings(createIngressRuleRetrySettings);

      RetrySettings getIngressRuleRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIngressRuleSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getIngressRuleSettings().setRetrySettings(getIngressRuleRetrySettings);

      RetrySettings updateIngressRuleRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateIngressRuleSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateIngressRuleSettings()
          .setRetrySettings(updateIngressRuleRetrySettings);

      RetrySettings deleteIngressRuleRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteIngressRuleSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .deleteIngressRuleSettings()
          .setRetrySettings(deleteIngressRuleRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry listIngressRulesRetry = clientProperties.getListIngressRulesRetry();
    if (listIngressRulesRetry != null) {
      RetrySettings listIngressRulesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listIngressRulesSettings().getRetrySettings(),
              listIngressRulesRetry);
      clientSettingsBuilder
          .listIngressRulesSettings()
          .setRetrySettings(listIngressRulesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listIngressRules from properties.");
      }
    }
    Retry batchUpdateIngressRulesRetry = clientProperties.getBatchUpdateIngressRulesRetry();
    if (batchUpdateIngressRulesRetry != null) {
      RetrySettings batchUpdateIngressRulesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.batchUpdateIngressRulesSettings().getRetrySettings(),
              batchUpdateIngressRulesRetry);
      clientSettingsBuilder
          .batchUpdateIngressRulesSettings()
          .setRetrySettings(batchUpdateIngressRulesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for batchUpdateIngressRules from properties.");
      }
    }
    Retry createIngressRuleRetry = clientProperties.getCreateIngressRuleRetry();
    if (createIngressRuleRetry != null) {
      RetrySettings createIngressRuleRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createIngressRuleSettings().getRetrySettings(),
              createIngressRuleRetry);
      clientSettingsBuilder
          .createIngressRuleSettings()
          .setRetrySettings(createIngressRuleRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createIngressRule from properties.");
      }
    }
    Retry getIngressRuleRetry = clientProperties.getGetIngressRuleRetry();
    if (getIngressRuleRetry != null) {
      RetrySettings getIngressRuleRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIngressRuleSettings().getRetrySettings(),
              getIngressRuleRetry);
      clientSettingsBuilder.getIngressRuleSettings().setRetrySettings(getIngressRuleRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getIngressRule from properties.");
      }
    }
    Retry updateIngressRuleRetry = clientProperties.getUpdateIngressRuleRetry();
    if (updateIngressRuleRetry != null) {
      RetrySettings updateIngressRuleRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateIngressRuleSettings().getRetrySettings(),
              updateIngressRuleRetry);
      clientSettingsBuilder
          .updateIngressRuleSettings()
          .setRetrySettings(updateIngressRuleRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateIngressRule from properties.");
      }
    }
    Retry deleteIngressRuleRetry = clientProperties.getDeleteIngressRuleRetry();
    if (deleteIngressRuleRetry != null) {
      RetrySettings deleteIngressRuleRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteIngressRuleSettings().getRetrySettings(),
              deleteIngressRuleRetry);
      clientSettingsBuilder
          .deleteIngressRuleSettings()
          .setRetrySettings(deleteIngressRuleRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteIngressRule from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a FirewallClient bean configured with FirewallSettings.
   *
   * @param firewallSettings settings to configure an instance of client bean.
   * @return a {@link FirewallClient} bean configured with {@link FirewallSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public FirewallClient firewallClient(FirewallSettings firewallSettings) throws IOException {
    return FirewallClient.create(firewallSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-firewall";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
