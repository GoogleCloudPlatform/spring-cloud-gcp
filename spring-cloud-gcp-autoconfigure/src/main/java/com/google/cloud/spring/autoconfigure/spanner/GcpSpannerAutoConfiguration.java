/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.spanner;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.SessionPoolOptions;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.SpannerOptions.Builder;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.data.spanner.core.SpannerMutationFactory;
import com.google.cloud.spring.data.spanner.core.SpannerMutationFactoryImpl;
import com.google.cloud.spring.data.spanner.core.SpannerOperations;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.google.cloud.spring.data.spanner.core.admin.CachingComposingSupplier;
import com.google.cloud.spring.data.spanner.core.admin.DatabaseIdProvider;
import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import com.google.cloud.spring.data.spanner.core.convert.ConverterAwareMappingSpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.convert.SpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.rest.webmvc.spi.BackendIdConverter;

/** Provides Spring Data classes to use with Cloud Spanner. */
@AutoConfiguration
@AutoConfigureAfter(GcpContextAutoConfiguration.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.spanner.enabled", matchIfMissing = true)
@ConditionalOnClass({
  SpannerMappingContext.class,
  SpannerOperations.class,
  SpannerMutationFactory.class,
  SpannerEntityProcessor.class
})
@EnableConfigurationProperties(GcpSpannerProperties.class)
public class GcpSpannerAutoConfiguration {

  /** Core settings. */
  static class CoreSpannerAutoConfiguration {

    private final String projectId;

    private final String instanceId;

    private final String databaseName;

    private final Credentials credentials;

    private final int numRpcChannels;

    private final int prefetchChunks;

    private final int minSessions;

    private final int maxSessions;

    private final int maxIdleSessions;

    private final float writeSessionsFraction;

    private final int keepAliveIntervalMinutes;

    private final boolean createInterleavedTableDdlOnDeleteCascade;

    private final boolean failIfPoolExhausted;

    CoreSpannerAutoConfiguration(
        GcpSpannerProperties gcpSpannerProperties,
        GcpProjectIdProvider projectIdProvider,
        CredentialsProvider credentialsProvider)
        throws IOException {
      this.credentials =
          (gcpSpannerProperties.getCredentials().hasKey()
                  ? new DefaultCredentialsProvider(gcpSpannerProperties)
                  : credentialsProvider)
              .getCredentials();
      this.projectId =
          (gcpSpannerProperties.getProjectId() != null)
              ? gcpSpannerProperties.getProjectId()
              : projectIdProvider.getProjectId();
      this.instanceId = gcpSpannerProperties.getInstanceId();
      this.databaseName = gcpSpannerProperties.getDatabase();
      this.numRpcChannels = gcpSpannerProperties.getNumRpcChannels();
      this.prefetchChunks = gcpSpannerProperties.getPrefetchChunks();
      this.minSessions = gcpSpannerProperties.getMinSessions();
      this.maxSessions = gcpSpannerProperties.getMaxSessions();
      this.maxIdleSessions = gcpSpannerProperties.getMaxIdleSessions();
      this.writeSessionsFraction = gcpSpannerProperties.getWriteSessionsFraction();
      this.keepAliveIntervalMinutes = gcpSpannerProperties.getKeepAliveIntervalMinutes();
      this.createInterleavedTableDdlOnDeleteCascade =
          gcpSpannerProperties.isCreateInterleavedTableDdlOnDeleteCascade();
      this.failIfPoolExhausted = gcpSpannerProperties.isFailIfPoolExhausted();
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public SpannerOptions.Builder spannerOptionsBuilder(
        SessionPoolOptions sessionPoolOptions, Optional<SpannerOptionsCustomizer> customizer) {
      Builder builder =
          SpannerOptions.newBuilder()
              .setProjectId(this.projectId)
              .setHeaderProvider(new UserAgentHeaderProvider(this.getClass()))
              .setCredentials(this.credentials);
      if (this.numRpcChannels >= 0) {
        builder.setNumChannels(this.numRpcChannels);
      }
      if (this.prefetchChunks >= 0) {
        builder.setPrefetchChunks(this.prefetchChunks);
      }
      builder.setSessionPoolOption(sessionPoolOptions);

      customizer.ifPresent(c -> c.apply(builder));

      return builder;
    }

    @Bean
    @ConditionalOnMissingBean
    public SpannerOptions spannerOptions(SpannerOptions.Builder builder) {
      return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionPoolOptions sessionPoolOptions() {
      SessionPoolOptions.Builder builder = SessionPoolOptions.newBuilder();
      if (this.minSessions >= 0) {
        builder.setMinSessions(this.minSessions);
      }

      if (this.maxSessions >= 0) {
        builder.setMaxSessions(this.maxSessions);
      }

      if (this.maxIdleSessions >= 0) {
        builder.setMaxIdleSessions(this.maxIdleSessions);
      }

      if (this.writeSessionsFraction >= 0) {
        builder.setWriteSessionsFraction(this.writeSessionsFraction);
      }

      if (this.keepAliveIntervalMinutes >= 0) {
        builder.setKeepAliveIntervalMinutes(this.keepAliveIntervalMinutes);
      }

      if (this.failIfPoolExhausted) {
        builder.setFailIfPoolExhausted();
      }

      return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public DatabaseIdProvider databaseId() {
      return () -> DatabaseId.of(this.projectId, this.instanceId, this.databaseName);
    }

    @Bean
    @ConditionalOnMissingBean
    public Spanner spanner(SpannerOptions spannerOptions) {
      return spannerOptions.getService();
    }

    @Bean
    @ConditionalOnMissingBean(value = DatabaseClient.class, parameterizedContainer = Supplier.class)
    public Supplier<DatabaseClient> databaseClientProvider(
        Spanner spanner, Supplier<DatabaseId> databaseIdProvider) {
      return new CachingComposingSupplier<>(databaseIdProvider, spanner::getDatabaseClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public DatabaseAdminClient spannerDatabaseAdminClient(Spanner spanner) {
      return spanner.getDatabaseAdminClient();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpannerMappingContext spannerMappingContext(Gson gson) {
      return new SpannerMappingContext(gson);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpannerTemplate spannerTemplate(
        Supplier<DatabaseClient> databaseClientProvider,
        SpannerMappingContext mappingContext,
        SpannerEntityProcessor spannerEntityProcessor,
        SpannerMutationFactory spannerMutationFactory,
        SpannerSchemaUtils spannerSchemaUtils) {
      return new SpannerTemplate(
          databaseClientProvider,
          mappingContext,
          spannerEntityProcessor,
          spannerMutationFactory,
          spannerSchemaUtils);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpannerEntityProcessor spannerConverter(SpannerMappingContext mappingContext) {
      return new ConverterAwareMappingSpannerEntityProcessor(mappingContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpannerMutationFactory spannerMutationFactory(
        SpannerEntityProcessor spannerEntityProcessor,
        SpannerMappingContext spannerMappingContext,
        SpannerSchemaUtils spannerSchemaUtils) {
      return new SpannerMutationFactoryImpl(
          spannerEntityProcessor, spannerMappingContext, spannerSchemaUtils);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpannerSchemaUtils spannerSchemaUtils(
        SpannerMappingContext spannerMappingContext,
        SpannerEntityProcessor spannerEntityProcessor) {
      return new SpannerSchemaUtils(
          spannerMappingContext,
          spannerEntityProcessor,
          this.createInterleavedTableDdlOnDeleteCascade);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate(
        Supplier<DatabaseClient> databaseClientProvider,
        DatabaseAdminClient adminClient,
        Supplier<DatabaseId> databaseIdProvider) {
      return new SpannerDatabaseAdminTemplate(
          adminClient, databaseClientProvider, databaseIdProvider);
    }
  }

  /** REST settings. */
  @ConditionalOnClass({BackendIdConverter.class, SpannerMappingContext.class})
  static class SpannerKeyRestSupportAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public BackendIdConverter spannerKeyIdConverter(SpannerMappingContext mappingContext) {
      return new SpannerKeyIdConverter(mappingContext);
    }
  }
}
