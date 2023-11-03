/*
 * Copyright 2019-2019 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.firestore;

import com.google.api.client.util.escape.PercentEscaper;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.internal.Headers;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.core.UserAgentHeaderProvider;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import com.google.cloud.spring.data.firestore.mapping.FirestoreClassMapper;
import com.google.cloud.spring.data.firestore.mapping.FirestoreDefaultClassMapper;
import com.google.cloud.spring.data.firestore.mapping.FirestoreMappingContext;
import com.google.firestore.v1.FirestoreGrpc;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.stub.MetadataUtils;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

/**
 * Provides classes to use with Cloud Firestore.
 *
 * @since 1.2
 */
@AutoConfiguration
@AutoConfigureAfter(GcpContextAutoConfiguration.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.firestore.enabled", matchIfMissing = true)
@ConditionalOnClass({Firestore.class})
@EnableConfigurationProperties(GcpFirestoreProperties.class)
public class GcpFirestoreAutoConfiguration {

  private static final UserAgentHeaderProvider USER_AGENT_HEADER_PROVIDER =
      new UserAgentHeaderProvider(GcpFirestoreAutoConfiguration.class);

  private static final PercentEscaper PERCENT_ESCAPER = new PercentEscaper("._-~");

  private final String projectId;

  private final String databaseId;

  private final CredentialsProvider credentialsProvider;

  private final String hostPort;

  private final String firestoreRootPath;

  GcpFirestoreAutoConfiguration(
      GcpFirestoreProperties gcpFirestoreProperties,
      GcpProjectIdProvider projectIdProvider,
      CredentialsProvider credentialsProvider)
      throws IOException {

    this.projectId = gcpFirestoreProperties.getResolvedProjectId(projectIdProvider);
    this.databaseId = gcpFirestoreProperties.getResolvedDatabaseId();

    if (gcpFirestoreProperties.getEmulator().isEnabled()) {
      // if the emulator is enabled, create CredentialsProvider for this particular case.
      this.credentialsProvider = NoCredentialsProvider.create();
    } else {
      this.credentialsProvider =
          (gcpFirestoreProperties.getCredentials().hasKey()
              ? new DefaultCredentialsProvider(gcpFirestoreProperties)
              : credentialsProvider);
    }

    this.hostPort = gcpFirestoreProperties.getHostPort();
    this.firestoreRootPath = gcpFirestoreProperties.getFirestoreRootPath(projectIdProvider);
  }

  @Bean
  @ConditionalOnMissingBean
  public FirestoreOptions firestoreOptions() {
    return FirestoreOptions.getDefaultInstance().toBuilder()
        .setCredentialsProvider(this.credentialsProvider)
        .setProjectId(this.projectId)
        .setDatabaseId(databaseId)
        .setHeaderProvider(USER_AGENT_HEADER_PROVIDER)
        .setChannelProvider(
            InstantiatingGrpcChannelProvider.newBuilder().setEndpoint(this.hostPort).build())
        .build();
  }

  @Bean
  @ConditionalOnMissingBean
  public Firestore firestore(FirestoreOptions firestoreOptions) {
    return firestoreOptions.getService();
  }

  CredentialsProvider getCredentialsProvider() {
    return credentialsProvider;
  }

  /** The Firestore reactive template and data repositories support auto-configuration. */
  @ConditionalOnClass({FirestoreGrpc.FirestoreStub.class, Flux.class})
  class FirestoreReactiveAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public FirestoreGrpc.FirestoreStub firestoreGrpcStub(
        @Qualifier("firestoreManagedChannel") ManagedChannel firestoreManagedChannel)
        throws IOException {
      return FirestoreGrpc.newStub(firestoreManagedChannel)
          .withCallCredentials(
              MoreCallCredentials.from(
                  GcpFirestoreAutoConfiguration.this.credentialsProvider.getCredentials()));
    }

    @Bean
    @ConditionalOnMissingBean
    public FirestoreMappingContext firestoreMappingContext() {
      return new FirestoreMappingContext();
    }

    @Bean
    @ConditionalOnMissingBean
    public FirestoreClassMapper getClassMapper(FirestoreMappingContext mappingContext) {
      return new FirestoreDefaultClassMapper(mappingContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public FirestoreTemplate firestoreTemplate(
        FirestoreGrpc.FirestoreStub firestoreStub,
        FirestoreClassMapper classMapper,
        FirestoreMappingContext firestoreMappingContext) {
      return new FirestoreTemplate(
          firestoreStub,
          GcpFirestoreAutoConfiguration.this.firestoreRootPath,
          classMapper,
          firestoreMappingContext);
    }

    @Bean
    @ConditionalOnMissingBean(name = "firestoreRoutingHeadersInterceptor")
    public ClientInterceptor firestoreRoutingHeadersInterceptor() {
      // add routing header for custom database id
      Metadata routingHeader = new Metadata();
      if (projectId != null && databaseId != null) {
        Metadata.Key<String> key =
            Metadata.Key.of(Headers.DYNAMIC_ROUTING_HEADER_KEY, Metadata.ASCII_STRING_MARSHALLER);
        routingHeader.put(key,
            "project_id=" + PERCENT_ESCAPER.escape(projectId)
                + "&database_id=" + PERCENT_ESCAPER.escape(databaseId));
      }
      return MetadataUtils.newAttachHeadersInterceptor(routingHeader);
    }

    @Bean
    @ConditionalOnMissingBean(name = "firestoreManagedChannel")
    public ManagedChannel firestoreManagedChannel(
        ClientInterceptor firestoreRoutingHeadersInterceptor) {
      return ManagedChannelBuilder.forTarget(
              "dns:///" + GcpFirestoreAutoConfiguration.this.hostPort)
          .userAgent(USER_AGENT_HEADER_PROVIDER.getUserAgent())
          .intercept(firestoreRoutingHeadersInterceptor)
          .build();
    }
  }
}
