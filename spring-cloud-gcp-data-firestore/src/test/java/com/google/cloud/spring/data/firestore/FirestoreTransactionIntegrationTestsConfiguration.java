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

package com.google.cloud.spring.data.firestore;

import com.google.api.client.util.escape.PercentEscaper;
import com.google.api.gax.rpc.internal.Headers;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.data.firestore.entities.UserRepository;
import com.google.cloud.spring.data.firestore.it.UserService;
import com.google.cloud.spring.data.firestore.mapping.FirestoreClassMapper;
import com.google.cloud.spring.data.firestore.mapping.FirestoreDefaultClassMapper;
import com.google.cloud.spring.data.firestore.mapping.FirestoreMappingContext;
import com.google.cloud.spring.data.firestore.repository.config.EnableReactiveFirestoreRepositories;
import com.google.cloud.spring.data.firestore.transaction.ReactiveFirestoreTransactionManager;
import com.google.firestore.v1.FirestoreGrpc;
import io.grpc.CallCredentials;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.stub.MetadataUtils;
import java.io.IOException;
import java.util.UUID;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Spring config for the integration tests. */
@Configuration
@PropertySource("application-test.properties")
@EnableReactiveFirestoreRepositories(basePackageClasses = UserRepository.class)
@EnableTransactionManagement
public class FirestoreTransactionIntegrationTestsConfiguration {
  String defaultParent;

  String uuid = UUID.randomUUID().toString();

  String projectId;

  String databaseId;

  @Autowired
  public FirestoreTransactionIntegrationTestsConfiguration(
      @Value("${test.integration.firestore.database-id:(default)}") String databaseId) {
    this.projectId = new DefaultGcpProjectIdProvider().getProjectId();
    this.databaseId = databaseId;
    this.defaultParent =
        String.format("projects/%s/databases/%s/documents", this.projectId, databaseId);
  }

  private static final PercentEscaper PERCENT_ESCAPER = new PercentEscaper("._-~");

  @Bean
  FirestoreGrpc.FirestoreStub firestoreStub(ClientInterceptor firestoreRoutingHeadersInterceptor)
      throws IOException {
    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
    CallCredentials callCredentials = MoreCallCredentials.from(credentials);

    // Create a channel
    ManagedChannel channel =
        ManagedChannelBuilder.forTarget("dns:///firestore.googleapis.com:443")
            .intercept(firestoreRoutingHeadersInterceptor)
            .build();
    return FirestoreGrpc.newStub(channel).withCallCredentials(callCredentials);
  }

  @Bean
  public FirestoreMappingContext firestoreMappingContext() {
    return new FirestoreMappingContext();
  }

  @Bean
  public FirestoreTemplate firestoreTemplate(
      FirestoreGrpc.FirestoreStub firestoreStub,
      FirestoreClassMapper classMapper,
      FirestoreMappingContext firestoreMappingContext) {
    return new FirestoreTemplate(
        firestoreStub, this.defaultParent, classMapper, firestoreMappingContext, uuid);
  }

  @Bean
  @ConditionalOnMissingBean(name = "firestoreRoutingHeadersInterceptor")
  public ClientInterceptor firestoreRoutingHeadersInterceptor() {
    // add routing header for custom database id
    Metadata routingHeader = new Metadata();
    if (projectId != null && databaseId != null) {
      Metadata.Key<String> key =
          Metadata.Key.of(Headers.DYNAMIC_ROUTING_HEADER_KEY, Metadata.ASCII_STRING_MARSHALLER);
      routingHeader.put(
          key,
          "project_id="
              + PERCENT_ESCAPER.escape(projectId)
              + "&database_id="
              + PERCENT_ESCAPER.escape(databaseId));
    }
    return MetadataUtils.newAttachHeadersInterceptor(routingHeader);
  }
  
  @Bean
  @ConditionalOnMissingBean
  public ReactiveFirestoreTransactionManager firestoreTransactionManager(
      FirestoreGrpc.FirestoreStub firestoreStub, FirestoreClassMapper classMapper) {
    return Mockito.spy(
        new ReactiveFirestoreTransactionManager(firestoreStub, this.defaultParent, classMapper));
  }

  // tag::user_service_bean[]
  @Bean
  public UserService userService() {
    return new UserService();
  }

  // end::user_service_bean[]

  @Bean
  @ConditionalOnMissingBean
  public FirestoreClassMapper getClassMapper(FirestoreMappingContext mappingContext) {
    return new FirestoreDefaultClassMapper(mappingContext);
  }
}
