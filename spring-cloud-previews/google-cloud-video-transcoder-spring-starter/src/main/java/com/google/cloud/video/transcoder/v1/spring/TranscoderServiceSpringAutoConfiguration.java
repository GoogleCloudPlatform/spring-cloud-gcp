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

package com.google.cloud.video.transcoder.v1.spring;

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
import com.google.cloud.video.transcoder.v1.TranscoderServiceClient;
import com.google.cloud.video.transcoder.v1.TranscoderServiceSettings;
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
 * Auto-configuration for {@link TranscoderServiceClient}.
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
@ConditionalOnClass(TranscoderServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.video.transcoder.v1.transcoder-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(TranscoderServiceSpringProperties.class)
public class TranscoderServiceSpringAutoConfiguration {
  private final TranscoderServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(TranscoderServiceSpringAutoConfiguration.class);

  protected TranscoderServiceSpringAutoConfiguration(
      TranscoderServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from TranscoderService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultTranscoderServiceTransportChannelProvider")
  public TransportChannelProvider defaultTranscoderServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return TranscoderServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return TranscoderServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a TranscoderServiceSettings bean configured to use a DefaultCredentialsProvider and
   * the client library's default transport channel provider
   * (defaultTranscoderServiceTransportChannelProvider()). It also configures the quota project ID
   * and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in TranscoderServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link TranscoderServiceSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public TranscoderServiceSettings transcoderServiceSettings(
      @Qualifier("defaultTranscoderServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    TranscoderServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = TranscoderServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = TranscoderServiceSettings.newBuilder();
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
          TranscoderServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings createJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.createJobSettings().setRetrySettings(createJobRetrySettings);

      RetrySettings listJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listJobsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listJobsSettings().setRetrySettings(listJobsRetrySettings);

      RetrySettings getJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getJobSettings().setRetrySettings(getJobRetrySettings);

      RetrySettings deleteJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteJobSettings().setRetrySettings(deleteJobRetrySettings);

      RetrySettings createJobTemplateRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createJobTemplateSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createJobTemplateSettings()
          .setRetrySettings(createJobTemplateRetrySettings);

      RetrySettings listJobTemplatesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listJobTemplatesSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listJobTemplatesSettings()
          .setRetrySettings(listJobTemplatesRetrySettings);

      RetrySettings getJobTemplateRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getJobTemplateSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getJobTemplateSettings().setRetrySettings(getJobTemplateRetrySettings);

      RetrySettings deleteJobTemplateRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteJobTemplateSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .deleteJobTemplateSettings()
          .setRetrySettings(deleteJobTemplateRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry createJobRetry = clientProperties.getCreateJobRetry();
    if (createJobRetry != null) {
      RetrySettings createJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createJobSettings().getRetrySettings(), createJobRetry);
      clientSettingsBuilder.createJobSettings().setRetrySettings(createJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for createJob from properties.");
      }
    }
    Retry listJobsRetry = clientProperties.getListJobsRetry();
    if (listJobsRetry != null) {
      RetrySettings listJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listJobsSettings().getRetrySettings(), listJobsRetry);
      clientSettingsBuilder.listJobsSettings().setRetrySettings(listJobsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listJobs from properties.");
      }
    }
    Retry getJobRetry = clientProperties.getGetJobRetry();
    if (getJobRetry != null) {
      RetrySettings getJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getJobSettings().getRetrySettings(), getJobRetry);
      clientSettingsBuilder.getJobSettings().setRetrySettings(getJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getJob from properties.");
      }
    }
    Retry deleteJobRetry = clientProperties.getDeleteJobRetry();
    if (deleteJobRetry != null) {
      RetrySettings deleteJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteJobSettings().getRetrySettings(), deleteJobRetry);
      clientSettingsBuilder.deleteJobSettings().setRetrySettings(deleteJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for deleteJob from properties.");
      }
    }
    Retry createJobTemplateRetry = clientProperties.getCreateJobTemplateRetry();
    if (createJobTemplateRetry != null) {
      RetrySettings createJobTemplateRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createJobTemplateSettings().getRetrySettings(),
              createJobTemplateRetry);
      clientSettingsBuilder
          .createJobTemplateSettings()
          .setRetrySettings(createJobTemplateRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createJobTemplate from properties.");
      }
    }
    Retry listJobTemplatesRetry = clientProperties.getListJobTemplatesRetry();
    if (listJobTemplatesRetry != null) {
      RetrySettings listJobTemplatesRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listJobTemplatesSettings().getRetrySettings(),
              listJobTemplatesRetry);
      clientSettingsBuilder
          .listJobTemplatesSettings()
          .setRetrySettings(listJobTemplatesRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listJobTemplates from properties.");
      }
    }
    Retry getJobTemplateRetry = clientProperties.getGetJobTemplateRetry();
    if (getJobTemplateRetry != null) {
      RetrySettings getJobTemplateRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getJobTemplateSettings().getRetrySettings(),
              getJobTemplateRetry);
      clientSettingsBuilder.getJobTemplateSettings().setRetrySettings(getJobTemplateRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getJobTemplate from properties.");
      }
    }
    Retry deleteJobTemplateRetry = clientProperties.getDeleteJobTemplateRetry();
    if (deleteJobTemplateRetry != null) {
      RetrySettings deleteJobTemplateRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteJobTemplateSettings().getRetrySettings(),
              deleteJobTemplateRetry);
      clientSettingsBuilder
          .deleteJobTemplateSettings()
          .setRetrySettings(deleteJobTemplateRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteJobTemplate from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a TranscoderServiceClient bean configured with TranscoderServiceSettings.
   *
   * @param transcoderServiceSettings settings to configure an instance of client bean.
   * @return a {@link TranscoderServiceClient} bean configured with {@link
   *     TranscoderServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public TranscoderServiceClient transcoderServiceClient(
      TranscoderServiceSettings transcoderServiceSettings) throws IOException {
    return TranscoderServiceClient.create(transcoderServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-transcoder-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
