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

package com.google.cloud.bigquery.reservation.v1.spring;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.HeaderProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.bigquery.reservation.v1.ReservationServiceClient;
import com.google.cloud.bigquery.reservation.v1.ReservationServiceSettings;
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
 * Auto-configuration for {@link ReservationServiceClient}.
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
@ConditionalOnClass(ReservationServiceClient.class)
@ConditionalOnProperty(
    value = "com.google.cloud.bigquery.reservation.v1.reservation-service.enabled",
    matchIfMissing = true)
@EnableConfigurationProperties(ReservationServiceSpringProperties.class)
public class ReservationServiceSpringAutoConfiguration {
  private final ReservationServiceSpringProperties clientProperties;
  private final CredentialsProvider credentialsProvider;
  private static final Log LOGGER =
      LogFactory.getLog(ReservationServiceSpringAutoConfiguration.class);

  protected ReservationServiceSpringAutoConfiguration(
      ReservationServiceSpringProperties clientProperties, CredentialsProvider credentialsProvider)
      throws IOException {
    this.clientProperties = clientProperties;
    if (this.clientProperties.getCredentials().hasKey()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using credentials from ReservationService-specific configuration");
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
  @ConditionalOnMissingBean(name = "defaultReservationServiceTransportChannelProvider")
  public TransportChannelProvider defaultReservationServiceTransportChannelProvider() {
    if (this.clientProperties.getUseRest()) {
      return ReservationServiceSettings.defaultHttpJsonTransportProviderBuilder().build();
    }
    return ReservationServiceSettings.defaultTransportChannelProvider();
  }

  /**
   * Provides a ReservationServiceSettings bean configured to use a DefaultCredentialsProvider and
   * the client library's default transport channel provider
   * (defaultReservationServiceTransportChannelProvider()). It also configures the quota project ID
   * and executor thread count, if provided through properties.
   *
   * <p>Retry settings are also configured from service-level and method-level properties specified
   * in ReservationServiceSpringProperties. Method-level properties will take precedence over
   * service-level properties if available, and client library defaults will be used if neither are
   * specified.
   *
   * @param defaultTransportChannelProvider TransportChannelProvider to use in the settings.
   * @return a {@link ReservationServiceSettings} bean configured with {@link
   *     TransportChannelProvider} bean.
   */
  @Bean
  @ConditionalOnMissingBean
  public ReservationServiceSettings reservationServiceSettings(
      @Qualifier("defaultReservationServiceTransportChannelProvider")
          TransportChannelProvider defaultTransportChannelProvider)
      throws IOException {
    ReservationServiceSettings.Builder clientSettingsBuilder;
    if (this.clientProperties.getUseRest()) {
      clientSettingsBuilder = ReservationServiceSettings.newHttpJsonBuilder();
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Using REST (HTTP/JSON) transport.");
      }
    } else {
      clientSettingsBuilder = ReservationServiceSettings.newBuilder();
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
          ReservationServiceSettings.defaultExecutorProviderBuilder()
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
      RetrySettings createReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createReservationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createReservationSettings()
          .setRetrySettings(createReservationRetrySettings);

      RetrySettings listReservationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listReservationsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listReservationsSettings()
          .setRetrySettings(listReservationsRetrySettings);

      RetrySettings getReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getReservationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.getReservationSettings().setRetrySettings(getReservationRetrySettings);

      RetrySettings deleteReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteReservationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .deleteReservationSettings()
          .setRetrySettings(deleteReservationRetrySettings);

      RetrySettings updateReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateReservationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateReservationSettings()
          .setRetrySettings(updateReservationRetrySettings);

      RetrySettings createCapacityCommitmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createCapacityCommitmentSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .createCapacityCommitmentSettings()
          .setRetrySettings(createCapacityCommitmentRetrySettings);

      RetrySettings listCapacityCommitmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listCapacityCommitmentsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .listCapacityCommitmentsSettings()
          .setRetrySettings(listCapacityCommitmentsRetrySettings);

      RetrySettings getCapacityCommitmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getCapacityCommitmentSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .getCapacityCommitmentSettings()
          .setRetrySettings(getCapacityCommitmentRetrySettings);

      RetrySettings deleteCapacityCommitmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteCapacityCommitmentSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .deleteCapacityCommitmentSettings()
          .setRetrySettings(deleteCapacityCommitmentRetrySettings);

      RetrySettings updateCapacityCommitmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateCapacityCommitmentSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .updateCapacityCommitmentSettings()
          .setRetrySettings(updateCapacityCommitmentRetrySettings);

      RetrySettings splitCapacityCommitmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.splitCapacityCommitmentSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .splitCapacityCommitmentSettings()
          .setRetrySettings(splitCapacityCommitmentRetrySettings);

      RetrySettings mergeCapacityCommitmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.mergeCapacityCommitmentsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .mergeCapacityCommitmentsSettings()
          .setRetrySettings(mergeCapacityCommitmentsRetrySettings);

      RetrySettings createAssignmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createAssignmentSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .createAssignmentSettings()
          .setRetrySettings(createAssignmentRetrySettings);

      RetrySettings listAssignmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listAssignmentsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .listAssignmentsSettings()
          .setRetrySettings(listAssignmentsRetrySettings);

      RetrySettings deleteAssignmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteAssignmentSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .deleteAssignmentSettings()
          .setRetrySettings(deleteAssignmentRetrySettings);

      RetrySettings searchAssignmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.searchAssignmentsSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .searchAssignmentsSettings()
          .setRetrySettings(searchAssignmentsRetrySettings);

      RetrySettings searchAllAssignmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.searchAllAssignmentsSettings().getRetrySettings(),
              serviceRetry);
      clientSettingsBuilder
          .searchAllAssignmentsSettings()
          .setRetrySettings(searchAllAssignmentsRetrySettings);

      RetrySettings moveAssignmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.moveAssignmentSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder.moveAssignmentSettings().setRetrySettings(moveAssignmentRetrySettings);

      RetrySettings updateAssignmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateAssignmentSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateAssignmentSettings()
          .setRetrySettings(updateAssignmentRetrySettings);

      RetrySettings getBiReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBiReservationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .getBiReservationSettings()
          .setRetrySettings(getBiReservationRetrySettings);

      RetrySettings updateBiReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateBiReservationSettings().getRetrySettings(), serviceRetry);
      clientSettingsBuilder
          .updateBiReservationSettings()
          .setRetrySettings(updateBiReservationRetrySettings);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured service-level retry settings from properties.");
      }
    }
    Retry createReservationRetry = clientProperties.getCreateReservationRetry();
    if (createReservationRetry != null) {
      RetrySettings createReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createReservationSettings().getRetrySettings(),
              createReservationRetry);
      clientSettingsBuilder
          .createReservationSettings()
          .setRetrySettings(createReservationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createReservation from properties.");
      }
    }
    Retry listReservationsRetry = clientProperties.getListReservationsRetry();
    if (listReservationsRetry != null) {
      RetrySettings listReservationsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listReservationsSettings().getRetrySettings(),
              listReservationsRetry);
      clientSettingsBuilder
          .listReservationsSettings()
          .setRetrySettings(listReservationsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listReservations from properties.");
      }
    }
    Retry getReservationRetry = clientProperties.getGetReservationRetry();
    if (getReservationRetry != null) {
      RetrySettings getReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getReservationSettings().getRetrySettings(),
              getReservationRetry);
      clientSettingsBuilder.getReservationSettings().setRetrySettings(getReservationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for getReservation from properties.");
      }
    }
    Retry deleteReservationRetry = clientProperties.getDeleteReservationRetry();
    if (deleteReservationRetry != null) {
      RetrySettings deleteReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteReservationSettings().getRetrySettings(),
              deleteReservationRetry);
      clientSettingsBuilder
          .deleteReservationSettings()
          .setRetrySettings(deleteReservationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteReservation from properties.");
      }
    }
    Retry updateReservationRetry = clientProperties.getUpdateReservationRetry();
    if (updateReservationRetry != null) {
      RetrySettings updateReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateReservationSettings().getRetrySettings(),
              updateReservationRetry);
      clientSettingsBuilder
          .updateReservationSettings()
          .setRetrySettings(updateReservationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateReservation from properties.");
      }
    }
    Retry createCapacityCommitmentRetry = clientProperties.getCreateCapacityCommitmentRetry();
    if (createCapacityCommitmentRetry != null) {
      RetrySettings createCapacityCommitmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createCapacityCommitmentSettings().getRetrySettings(),
              createCapacityCommitmentRetry);
      clientSettingsBuilder
          .createCapacityCommitmentSettings()
          .setRetrySettings(createCapacityCommitmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createCapacityCommitment from properties.");
      }
    }
    Retry listCapacityCommitmentsRetry = clientProperties.getListCapacityCommitmentsRetry();
    if (listCapacityCommitmentsRetry != null) {
      RetrySettings listCapacityCommitmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listCapacityCommitmentsSettings().getRetrySettings(),
              listCapacityCommitmentsRetry);
      clientSettingsBuilder
          .listCapacityCommitmentsSettings()
          .setRetrySettings(listCapacityCommitmentsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for listCapacityCommitments from properties.");
      }
    }
    Retry getCapacityCommitmentRetry = clientProperties.getGetCapacityCommitmentRetry();
    if (getCapacityCommitmentRetry != null) {
      RetrySettings getCapacityCommitmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getCapacityCommitmentSettings().getRetrySettings(),
              getCapacityCommitmentRetry);
      clientSettingsBuilder
          .getCapacityCommitmentSettings()
          .setRetrySettings(getCapacityCommitmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getCapacityCommitment from properties.");
      }
    }
    Retry deleteCapacityCommitmentRetry = clientProperties.getDeleteCapacityCommitmentRetry();
    if (deleteCapacityCommitmentRetry != null) {
      RetrySettings deleteCapacityCommitmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteCapacityCommitmentSettings().getRetrySettings(),
              deleteCapacityCommitmentRetry);
      clientSettingsBuilder
          .deleteCapacityCommitmentSettings()
          .setRetrySettings(deleteCapacityCommitmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteCapacityCommitment from properties.");
      }
    }
    Retry updateCapacityCommitmentRetry = clientProperties.getUpdateCapacityCommitmentRetry();
    if (updateCapacityCommitmentRetry != null) {
      RetrySettings updateCapacityCommitmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateCapacityCommitmentSettings().getRetrySettings(),
              updateCapacityCommitmentRetry);
      clientSettingsBuilder
          .updateCapacityCommitmentSettings()
          .setRetrySettings(updateCapacityCommitmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateCapacityCommitment from properties.");
      }
    }
    Retry splitCapacityCommitmentRetry = clientProperties.getSplitCapacityCommitmentRetry();
    if (splitCapacityCommitmentRetry != null) {
      RetrySettings splitCapacityCommitmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.splitCapacityCommitmentSettings().getRetrySettings(),
              splitCapacityCommitmentRetry);
      clientSettingsBuilder
          .splitCapacityCommitmentSettings()
          .setRetrySettings(splitCapacityCommitmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for splitCapacityCommitment from properties.");
      }
    }
    Retry mergeCapacityCommitmentsRetry = clientProperties.getMergeCapacityCommitmentsRetry();
    if (mergeCapacityCommitmentsRetry != null) {
      RetrySettings mergeCapacityCommitmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.mergeCapacityCommitmentsSettings().getRetrySettings(),
              mergeCapacityCommitmentsRetry);
      clientSettingsBuilder
          .mergeCapacityCommitmentsSettings()
          .setRetrySettings(mergeCapacityCommitmentsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for mergeCapacityCommitments from properties.");
      }
    }
    Retry createAssignmentRetry = clientProperties.getCreateAssignmentRetry();
    if (createAssignmentRetry != null) {
      RetrySettings createAssignmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.createAssignmentSettings().getRetrySettings(),
              createAssignmentRetry);
      clientSettingsBuilder
          .createAssignmentSettings()
          .setRetrySettings(createAssignmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for createAssignment from properties.");
      }
    }
    Retry listAssignmentsRetry = clientProperties.getListAssignmentsRetry();
    if (listAssignmentsRetry != null) {
      RetrySettings listAssignmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.listAssignmentsSettings().getRetrySettings(),
              listAssignmentsRetry);
      clientSettingsBuilder
          .listAssignmentsSettings()
          .setRetrySettings(listAssignmentsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for listAssignments from properties.");
      }
    }
    Retry deleteAssignmentRetry = clientProperties.getDeleteAssignmentRetry();
    if (deleteAssignmentRetry != null) {
      RetrySettings deleteAssignmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.deleteAssignmentSettings().getRetrySettings(),
              deleteAssignmentRetry);
      clientSettingsBuilder
          .deleteAssignmentSettings()
          .setRetrySettings(deleteAssignmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for deleteAssignment from properties.");
      }
    }
    Retry searchAssignmentsRetry = clientProperties.getSearchAssignmentsRetry();
    if (searchAssignmentsRetry != null) {
      RetrySettings searchAssignmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.searchAssignmentsSettings().getRetrySettings(),
              searchAssignmentsRetry);
      clientSettingsBuilder
          .searchAssignmentsSettings()
          .setRetrySettings(searchAssignmentsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for searchAssignments from properties.");
      }
    }
    Retry searchAllAssignmentsRetry = clientProperties.getSearchAllAssignmentsRetry();
    if (searchAllAssignmentsRetry != null) {
      RetrySettings searchAllAssignmentsRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.searchAllAssignmentsSettings().getRetrySettings(),
              searchAllAssignmentsRetry);
      clientSettingsBuilder
          .searchAllAssignmentsSettings()
          .setRetrySettings(searchAllAssignmentsRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for searchAllAssignments from properties.");
      }
    }
    Retry moveAssignmentRetry = clientProperties.getMoveAssignmentRetry();
    if (moveAssignmentRetry != null) {
      RetrySettings moveAssignmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.moveAssignmentSettings().getRetrySettings(),
              moveAssignmentRetry);
      clientSettingsBuilder.moveAssignmentSettings().setRetrySettings(moveAssignmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Configured method-level retry settings for moveAssignment from properties.");
      }
    }
    Retry updateAssignmentRetry = clientProperties.getUpdateAssignmentRetry();
    if (updateAssignmentRetry != null) {
      RetrySettings updateAssignmentRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateAssignmentSettings().getRetrySettings(),
              updateAssignmentRetry);
      clientSettingsBuilder
          .updateAssignmentSettings()
          .setRetrySettings(updateAssignmentRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateAssignment from properties.");
      }
    }
    Retry getBiReservationRetry = clientProperties.getGetBiReservationRetry();
    if (getBiReservationRetry != null) {
      RetrySettings getBiReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.getBiReservationSettings().getRetrySettings(),
              getBiReservationRetry);
      clientSettingsBuilder
          .getBiReservationSettings()
          .setRetrySettings(getBiReservationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for getBiReservation from properties.");
      }
    }
    Retry updateBiReservationRetry = clientProperties.getUpdateBiReservationRetry();
    if (updateBiReservationRetry != null) {
      RetrySettings updateBiReservationRetrySettings =
          RetryUtil.updateRetrySettings(
              clientSettingsBuilder.updateBiReservationSettings().getRetrySettings(),
              updateBiReservationRetry);
      clientSettingsBuilder
          .updateBiReservationSettings()
          .setRetrySettings(updateBiReservationRetrySettings);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
            "Configured method-level retry settings for updateBiReservation from properties.");
      }
    }
    return clientSettingsBuilder.build();
  }

  /**
   * Provides a ReservationServiceClient bean configured with ReservationServiceSettings.
   *
   * @param reservationServiceSettings settings to configure an instance of client bean.
   * @return a {@link ReservationServiceClient} bean configured with {@link
   *     ReservationServiceSettings}
   */
  @Bean
  @ConditionalOnMissingBean
  public ReservationServiceClient reservationServiceClient(
      ReservationServiceSettings reservationServiceSettings) throws IOException {
    return ReservationServiceClient.create(reservationServiceSettings);
  }

  private HeaderProvider userAgentHeaderProvider() {
    String springLibrary = "spring-autogen-reservation-service";
    String version = this.getClass().getPackage().getImplementationVersion();
    return () -> Collections.singletonMap("user-agent", springLibrary + "/" + version);
  }
}
