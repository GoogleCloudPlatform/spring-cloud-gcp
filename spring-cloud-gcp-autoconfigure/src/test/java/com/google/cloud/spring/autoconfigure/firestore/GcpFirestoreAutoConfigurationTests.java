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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.firestore.v1.FirestoreGrpc;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionManager;

/**
 * Tests for Firestore auto-config.
 *
 * @since 1.2
 */
class GcpFirestoreAutoConfigurationTests {

  private ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GcpFirestoreAutoConfiguration.class,
                  FirestoreTransactionManagerAutoConfiguration.class,
                  GcpContextAutoConfiguration.class,
                  FirestoreRepositoriesAutoConfiguration.class))
          .withUserConfiguration(TestConfiguration.class)
          .withPropertyValues("spring.cloud.gcp.firestore.project-id=test-project");

  @Test
  void testFirestoreOptionsCorrectlySet() {
    this.contextRunner.run(
        context -> {
          FirestoreOptions datastoreOptions = context.getBean(Firestore.class).getOptions();
          assertThat(datastoreOptions.getProjectId()).isEqualTo("test-project");
          assertThat(datastoreOptions.getDatabaseId()).isEqualTo("(default)");
          assertThat(getRoutingHeader(context))
              .isEqualTo("project_id=test-project&database_id=%28default%29");
        });
  }

  @Test
  void testTestRepositoryCreated() {
    this.contextRunner.run(
        context -> assertThat(context.getBean(FirestoreTestRepository.class)).isNotNull());
  }

  @Test
  void testCorrectManagedChannel() {
    ManagedChannel wrongChannel = Mockito.mock(ManagedChannel.class);

    this.contextRunner
        .withBean("wrongManagedChannel", ManagedChannel.class, () -> wrongChannel)
        .run(
            ctx -> {
              FirestoreGrpc.FirestoreStub stub =
                  ctx.getBean("firestoreGrpcStub", FirestoreGrpc.FirestoreStub.class);
              ManagedChannel channel = (ManagedChannel) stub.getChannel();
              assertThat(channel.authority()).isEqualTo("firestore.googleapis.com:443");
              assertThat(getRoutingHeader(ctx)).isEqualTo("project_id=test-project&database_id=%28default%29");
            });
  }

  private static String getRoutingHeader(ApplicationContext ctx) {
    ClientInterceptor clientInterceptor =
        ctx.getBean("firestoreRoutingHeadersInterceptor", ClientInterceptor.class);
    Metadata extraHeaders = (Metadata) ReflectionTestUtils.getField(clientInterceptor,
        "extraHeaders");
    return extraHeaders.get(Key.of("x-goog-request-params", Metadata.ASCII_STRING_MARSHALLER));
  }

  @Test
  void testTransactionManagerExcludedWithoutAutoConfiguration() {
    contextRunner
        .withPropertyValues("spring.cloud.gcp.firestore.enabled=false")
        .run(
            ctx -> {
              assertThatThrownBy(() -> ctx.getBean(TransactionManager.class))
                  .isInstanceOf(NoSuchBeanDefinitionException.class);
            });
  }

  @Test
  void testDatabaseIdOverride() {
    contextRunner
        .withPropertyValues("spring.cloud.gcp.firestore.database-id=mydb^2",
            "spring.cloud.gcp.firestore.project-id=test")
        .run(
            context -> {
              FirestoreOptions datastoreOptions = context.getBean(Firestore.class).getOptions();
              assertThat(datastoreOptions.getDatabaseId()).isEqualTo("mydb^2");
              String rootPath = context.getBean(GcpFirestoreProperties.class)
                  .getFirestoreRootPath(() -> "xyz-ignored");
              assertThat(rootPath)
                  .isEqualTo("projects/test/databases/mydb^2/documents");
              assertThat(getRoutingHeader(context)).isEqualTo(
                  "project_id=test&database_id=mydb%5E2");
            });
  }

  /** Spring Boot config for tests. */
  @AutoConfigurationPackage
  static class TestConfiguration {

    @Bean
    public CredentialsProvider credentialsProvider() {
      return () -> mock(GoogleCredentials.class);
    }
  }
}
