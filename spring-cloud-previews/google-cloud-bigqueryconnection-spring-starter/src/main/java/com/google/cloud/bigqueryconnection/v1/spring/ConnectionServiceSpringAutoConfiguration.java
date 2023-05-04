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

package com.google.cloud.bigqueryconnection.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.bigqueryconnection.v1.ConnectionServiceClient;
import com.google.cloud.bigqueryconnection.v1.ConnectionServiceSettings;
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
 * Auto-configuration for {@link ConnectionServiceClient}.
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
@ConditionalOnClass(ConnectionServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.bigqueryconnection.v1.connection-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ConnectionServiceSpringProperties.class)
public class ConnectionServiceSpringAutoConfiguration {
  private final ConnectionServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(ConnectionServiceSpringAutoConfiguration.class);

  protected ConnectionServiceSpringAutoConfiguration(
      ConnectionServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from ConnectionService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultConnectionServiceTransportChannelProvider")
  public TransportChannelProvider defaultConnectionServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return ConnectionServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return ConnectionServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ConnectionServiceSettings bean configured to use a DefaultCredentialsProvider and
   * the client library's default transport channel provider
   * (defaultConnectionServiceTransportChannelProvider()). It also configures the quota project ID
   * and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ConnectionServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ConnectionServiceSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ConnectionServiceSettings connectionServiceSettings(
      @Qualifier("defaultConnectionServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ConnectionServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = ConnectionServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = ConnectionServiceSettings.newBuilder();
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
          ConnectionServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings createConnectionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createConnectionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createConnectionSettings()
          .setRetrySettings(createConnectionRetrySettings);

      RetrySettings getConnectionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConnectionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getConnectionSettings().setRetrySettings(getConnectionRetrySettings);

      RetrySettings listConnectionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConnectionsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listConnectionsSettings()
          .setRetrySettings(listConnectionsRetrySettings);

      RetrySettings updateConnectionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateConnectionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateConnectionSettings()
          .setRetrySettings(updateConnectionRetrySettings);

      RetrySettings deleteConnectionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteConnectionSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .deleteConnectionSettings()
          .setRetrySettings(deleteConnectionRetrySettings);

      RetrySettings getIamPolicyRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getIamPolicySettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getIamPolicySettings().setRetrySettings(getIamPolicyRetrySettings);

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
    Retry createConnectionRetry = clientProperties.getCreateConnectionRetry();
    if (createConnectionRetry != null) {
      RetrySettings createConnectionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createConnectionSettings().getRetrySettings(),
              createConnectionRetry);
      clientSettingsBuilder
          .createConnectionSettings()
          .setRetrySettings(createConnectionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createConnection from properties.");
      }
    }
    Retry getConnectionRetry = clientProperties.getGetConnectionRetry();
    if (getConnectionRetry != null) {
      RetrySettings getConnectionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getConnectionSettings().getRetrySettings(), getConnectionRetry);
      clientSettingsBuilder.getConnectionSettings().setRetrySettings(getConnectionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getConnection from properties.");
      }
    }
    Retry listConnectionsRetry = clientProperties.getListConnectionsRetry();
    if (listConnectionsRetry != null) {
      RetrySettings listConnectionsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listConnectionsSettings().getRetrySettings(),
              listConnectionsRetry);
      clientSettingsBuilder
          .listConnectionsSettings()
          .setRetrySettings(listConnectionsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listConnections from properties.");
      }
    }
    Retry updateConnectionRetry = clientProperties.getUpdateConnectionRetry();
    if (updateConnectionRetry != null) {
      RetrySettings updateConnectionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateConnectionSettings().getRetrySettings(),
              updateConnectionRetry);
      clientSettingsBuilder
          .updateConnectionSettings()
          .setRetrySettings(updateConnectionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateConnection from properties.");
      }
    }
    Retry deleteConnectionRetry = clientProperties.getDeleteConnectionRetry();
    if (deleteConnectionRetry != null) {
      RetrySettings deleteConnectionRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteConnectionSettings().getRetrySettings(),
              deleteConnectionRetry);
      clientSettingsBuilder
          .deleteConnectionSettings()
          .setRetrySettings(deleteConnectionRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteConnection from properties.");
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
   * Provides a ConnectionServiceClient bean configured with ConnectionServiceSettings.
   *
   * @param connectionServiceSettings settings to configure an instance of client bean.
   * @return a {@link ConnectionServiceClient} bean configured with {@link
   *     ConnectionServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ConnectionServiceClient connectionServiceClient(
      ConnectionServiceSettings connectionServiceSettings) throws IOException {
    return ConnectionServiceClient.create(connectionServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-connection-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
