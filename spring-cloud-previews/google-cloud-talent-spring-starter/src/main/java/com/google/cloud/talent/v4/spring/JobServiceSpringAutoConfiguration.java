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
import com.google.cloud.talent.v4.JobServiceClient;
import com.google.cloud.talent.v4.JobServiceSettings;
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
 * Auto-configuration for {@link JobServiceClient}.
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
@ConditionalOnClass(JobServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.talent.v4.job-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(JobServiceSpringProperties.class)
public class JobServiceSpringAutoConfiguration {
  private final JobServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER = LogFactory.getLog(JobServiceSpringAutoConfiguration.class);

  protected JobServiceSpringAutoConfiguration(
      JobServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from JobService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultJobServiceTransportChannelProvider")
  public TransportChannelProvider defaultJobServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return JobServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return JobServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a JobServiceSettings bean configured to use a DefaultCredentialsProvider and the
   * client library's default transport channel provider
   * (defaultJobServiceTransportChannelProvider()). It also configures the quota project ID and
   * executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in JobServiceSpringProperties. Method-level properties will take precedence over service-level
   * properties if available, and client library defaults will be used if neither are specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link JobServiceSettings} bean configured with {@link TransportChannelProvider}
   *     bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public JobServiceSettings jobServiceSettings(
      @Qualifier("defaultJobServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    JobServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = JobServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = JobServiceSettings.newBuilder();
    }
    clientSettingsBuilder
        .setCredentialsProvider(this.credentialsProvider)
        .setTransportChannelProvider(defaultTransportChannelProvider)
        .setEndpoint(JobServiceSettings.getDefaultEndpoint())
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
          JobServiceSettings.defaultExecutorProviderBuilder()
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

      RetrySettings getJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getJobSettings().setRetrySettings(getJobRetrySettings);

      RetrySettings updateJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.updateJobSettings().setRetrySettings(updateJobRetrySettings);

      RetrySettings deleteJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteJobSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.deleteJobSettings().setRetrySettings(deleteJobRetrySettings);

      RetrySettings listJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listJobsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.listJobsSettings().setRetrySettings(listJobsRetrySettings);

      RetrySettings searchJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.searchJobsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.searchJobsSettings().setRetrySettings(searchJobsRetrySettings);

      RetrySettings searchJobsForAlertRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.searchJobsForAlertSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .searchJobsForAlertSettings()
          .setRetrySettings(searchJobsForAlertRetrySettings);

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
    Retry updateJobRetry = clientProperties.getUpdateJobRetry();
    if (updateJobRetry != null) {
      RetrySettings updateJobRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateJobSettings().getRetrySettings(), updateJobRetry);
      clientSettingsBuilder.updateJobSettings().setRetrySettings(updateJobRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for updateJob from properties.");
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
    Retry searchJobsRetry = clientProperties.getSearchJobsRetry();
    if (searchJobsRetry != null) {
      RetrySettings searchJobsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.searchJobsSettings().getRetrySettings(), searchJobsRetry);
      clientSettingsBuilder.searchJobsSettings().setRetrySettings(searchJobsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for searchJobs from properties.");
      }
    }
    Retry searchJobsForAlertRetry = clientProperties.getSearchJobsForAlertRetry();
    if (searchJobsForAlertRetry != null) {
      RetrySettings searchJobsForAlertRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.searchJobsForAlertSettings().getRetrySettings(),
              searchJobsForAlertRetry);
      clientSettingsBuilder
          .searchJobsForAlertSettings()
          .setRetrySettings(searchJobsForAlertRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for searchJobsForAlert from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a JobServiceClient bean configured with JobServiceSettings.
   *
   * @param jobServiceSettings settings to configure an instance of client bean.
   * @return a {@link JobServiceClient} bean configured with {@link JobServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public JobServiceClient jobServiceClient(JobServiceSettings jobServiceSettings)
      throws IOException {
    return JobServiceClient.create(jobServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-job-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
